package com.springauto.generator;

import com.springauto.model.*;

import java.util.*;

public class ServiceGenerator {

    private final boolean scaffold;

    public ServiceGenerator(boolean scaffold) {
        this.scaffold = scaffold;
    }

    public Map<String, String> generate(OpenApiSpec spec, String basePackage) {
        Map<String, String> files = new LinkedHashMap<>();
        String pkgPath = basePackage.replace('.', '/');

        for (Map.Entry<String, List<OperationInfo>> entry : spec.getOperationsByTag().entrySet()) {
            String tag   = entry.getKey();
            String iface = CodeUtils.toClassName(tag) + "Service";
            String impl  = iface + "Impl";

            files.put("src/main/java/" + pkgPath + "/service/" + iface + ".java",
                    generateInterface(spec, basePackage, tag, entry.getValue()));
            files.put("src/main/java/" + pkgPath + "/service/impl/" + impl + ".java",
                    generateImpl(spec, basePackage, tag, entry.getValue()));
        }
        return files;
    }

    private String generateInterface(OpenApiSpec spec, String pkg, String tag, List<OperationInfo> ops) {
        StringBuilder sb = new StringBuilder();
        String iface = CodeUtils.toClassName(tag) + "Service";

        Set<String> modelImports = new TreeSet<>();
        for (OperationInfo op : ops) collectImports(op, modelImports);

        sb.append("package ").append(pkg).append(".service;\n\n");
        for (String imp : modelImports) sb.append("import ").append(pkg).append(".model.").append(imp).append(";\n");
        sb.append("import java.util.List;\n\n");

        sb.append("public interface ").append(iface).append(" {\n\n");
        for (OperationInfo op : ops) {
            sb.append("    ").append(returnType(op)).append(" ").append(op.getOperationId())
              .append("(").append(paramSignature(op)).append(");\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String generateImpl(OpenApiSpec spec, String pkg, String tag, List<OperationInfo> ops) {
        StringBuilder sb = new StringBuilder();
        String iface = CodeUtils.toClassName(tag) + "Service";
        String impl  = iface + "Impl";

        Set<String> modelImports2 = new TreeSet<>();
        for (OperationInfo op : ops) collectImports(op, modelImports2);

        sb.append("package ").append(pkg).append(".service.impl;\n\n");
        sb.append("import ").append(pkg).append(".dto.ApiException;\n");
        sb.append("import ").append(pkg).append(".service.").append(iface).append(";\n");
        for (String imp : modelImports2) sb.append("import ").append(pkg).append(".model.").append(imp).append(";\n");

        if (scaffold) {
            sb.append("import java.util.*;\n");
            sb.append("import java.util.concurrent.*;\n");
            sb.append("import java.util.concurrent.atomic.AtomicLong;\n");
            sb.append("import java.util.stream.Collectors;\n");
        } else {
            sb.append("import java.util.List;\n");
        }
        sb.append("import org.springframework.stereotype.Service;\n\n");

        sb.append("@Service\n");
        sb.append("public class ").append(impl).append(" implements ").append(iface).append(" {\n\n");

        if (scaffold) {
            String entityType = detectEntityType(ops);
            if (entityType != null) {
                sb.append("    private final Map<Long, ").append(entityType)
                  .append("> store = new ConcurrentHashMap<>();\n");
                sb.append("    private final AtomicLong idSeq = new AtomicLong(1);\n\n");
            }
        }

        for (OperationInfo op : ops) {
            sb.append("    @Override\n");
            sb.append("    public ").append(returnType(op)).append(" ").append(op.getOperationId())
              .append("(").append(paramSignature(op)).append(") {\n");

            if (scaffold) {
                sb.append(scaffoldBody(op, spec));
            } else {
                sb.append("        // TODO: implement ").append(op.getSummary() != null ? op.getSummary() : op.getOperationId()).append("\n");
                sb.append("        throw new UnsupportedOperationException(\"Not implemented yet: ")
                  .append(op.getOperationId()).append("\");\n");
            }

            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String scaffoldBody(OperationInfo op, OpenApiSpec spec) {
        OpPattern pattern = classifyOperation(op);
        String entityType = detectEntityType(op);
        if (entityType == null) entityType = "Object";

        return switch (pattern) {
            case LIST -> scaffoldList(op, entityType);
            case CREATE -> scaffoldCreate(op, entityType, spec);
            case GET_BY_ID -> scaffoldGetById(op, entityType);
            case UPDATE -> scaffoldUpdate(op, entityType, spec);
            case DELETE -> scaffoldDelete(op);
            default -> "        throw new UnsupportedOperationException(\"Not implemented yet: " + op.getOperationId() + "\");\n";
        };
    }

    private String scaffoldList(OperationInfo op, String entityType) {
        StringBuilder sb = new StringBuilder();
        sb.append("        List<").append(entityType).append("> result = new ArrayList<>(store.values());\n");

        // string query filters
        for (ParameterInfo p : op.getQueryParameters()) {
            String jt = p.getSchema() != null ? p.getSchema().getType() : "string";
            if ("string".equals(jt)) {
                String fname = CodeUtils.toFieldName(p.getName());
                String getter = "get" + CodeUtils.toClassName(p.getName()) + "()";
                sb.append("        if (").append(fname).append(" != null && !").append(fname).append(".isBlank()) {\n");
                sb.append("            result = result.stream()\n");
                sb.append("                    .filter(e -> e.").append(getter)
                  .append(" != null && e.").append(getter).append(".toString().equalsIgnoreCase(").append(fname).append("))\n");
                sb.append("                    .collect(Collectors.toList());\n");
                sb.append("        }\n");
            }
        }

        // pagination
        String pageParam = findParam(op, "page", "offset", "pageNumber");
        String sizeParam = findParam(op, "size", "limit", "pageSize", "perPage");
        if (pageParam != null || sizeParam != null) {
            String pp = pageParam != null ? CodeUtils.toFieldName(pageParam) : "null";
            String sp = sizeParam != null ? CodeUtils.toFieldName(sizeParam) : "null";
            sb.append("        int pageNum  = (").append(pp).append(" != null && ").append(pp).append(" > 0) ? ").append(pp).append(" : 0;\n");
            sb.append("        int pageSize = (").append(sp).append(" != null) ? ").append(sp).append(" : 20;\n");
            sb.append("        int from = pageNum * pageSize;\n");
            sb.append("        int to   = Math.min(from + pageSize, result.size());\n");
            sb.append("        return from >= result.size() ? List.of() : result.subList(from, to);\n");
        } else {
            sb.append("        return result;\n");
        }
        return sb.toString();
    }

    private String scaffoldCreate(OperationInfo op, String entityType, OpenApiSpec spec) {
        StringBuilder sb = new StringBuilder();
        sb.append("        ").append(entityType).append(" entity = new ").append(entityType).append("();\n");
        sb.append("        entity.setId(idSeq.getAndIncrement());\n");

        SchemaInfo reqSchema = op.getRequestBodySchema();
        SchemaInfo entSchema = spec.getSchemas().get(entityType);

        if (reqSchema != null && entSchema != null) {
            for (String propName : reqSchema.getProperties().keySet()) {
                if ("id".equals(propName)) continue;
                PropertyInfo reqProp = reqSchema.getProperties().get(propName);
                PropertyInfo entProp = entSchema.getProperties().get(propName);
                if (entProp == null) continue;

                String getter = "get" + CodeUtils.toClassName(propName) + "()";
                String setter = "set" + CodeUtils.toClassName(propName);

                if (!reqProp.getEnumValues().isEmpty() && !entProp.getEnumValues().isEmpty()) {
                    String entEnum = entityType + "." + CodeUtils.toClassName(propName) + "Enum";
                    sb.append("        if (request.").append(getter).append(" != null)\n");
                    sb.append("            entity.").append(setter).append("(")
                      .append(entEnum).append(".valueOf(request.").append(getter).append(".name()));\n");
                } else {
                    sb.append("        entity.").append(setter).append("(request.").append(getter).append(");\n");
                }
            }
        }

        sb.append("        store.put(entity.getId(), entity);\n");
        sb.append("        return entity;\n");
        return sb.toString();
    }

    private String scaffoldGetById(OperationInfo op, String entityType) {
        String idParam = op.getPathParameters().isEmpty() ? "id" : op.getPathParameters().get(0).getName();
        String idVar   = CodeUtils.toFieldName(idParam);
        StringBuilder sb = new StringBuilder();
        sb.append("        ").append(entityType).append(" entity = store.get(").append(idVar).append(");\n");
        sb.append("        if (entity == null)\n");
        sb.append("            throw ApiException.notFound(\"").append(entityType).append(" not found with id: \" + ").append(idVar).append(");\n");
        sb.append("        return entity;\n");
        return sb.toString();
    }

    private String scaffoldUpdate(OperationInfo op, String entityType, OpenApiSpec spec) {
        String idParam = op.getPathParameters().isEmpty() ? "id" : op.getPathParameters().get(0).getName();
        String idVar   = CodeUtils.toFieldName(idParam);
        StringBuilder sb = new StringBuilder();
        sb.append("        ").append(entityType).append(" entity = store.get(").append(idVar).append(");\n");
        sb.append("        if (entity == null)\n");
        sb.append("            throw ApiException.notFound(\"").append(entityType).append(" not found with id: \" + ").append(idVar).append(");\n");

        SchemaInfo reqSchema = op.getRequestBodySchema();
        SchemaInfo entSchema = spec.getSchemas().get(entityType);

        if (reqSchema != null && entSchema != null) {
            for (String propName : reqSchema.getProperties().keySet()) {
                if ("id".equals(propName)) continue;
                PropertyInfo reqProp = reqSchema.getProperties().get(propName);
                PropertyInfo entProp = entSchema.getProperties().get(propName);
                if (entProp == null) continue;

                String getter = "get" + CodeUtils.toClassName(propName) + "()";
                String setter = "set" + CodeUtils.toClassName(propName);

                if (!reqProp.getEnumValues().isEmpty() && !entProp.getEnumValues().isEmpty()) {
                    String entEnum = entityType + "." + CodeUtils.toClassName(propName) + "Enum";
                    sb.append("        if (request.").append(getter).append(" != null)\n");
                    sb.append("            entity.").append(setter).append("(")
                      .append(entEnum).append(".valueOf(request.").append(getter).append(".name()));\n");
                } else {
                    sb.append("        if (request.").append(getter).append(" != null)\n");
                    sb.append("            entity.").append(setter).append("(request.").append(getter).append(");\n");
                }
            }
        }

        sb.append("        return entity;\n");
        return sb.toString();
    }

    private String scaffoldDelete(OperationInfo op) {
        String idParam = op.getPathParameters().isEmpty() ? "id" : op.getPathParameters().get(0).getName();
        String idVar   = CodeUtils.toFieldName(idParam);
        return "        if (!store.containsKey(" + idVar + "))\n" +
               "            throw ApiException.notFound(\"Entity not found with id: \" + " + idVar + ");\n" +
               "        store.remove(" + idVar + ");\n";
    }

    private String findParam(OperationInfo op, String... names) {
        Set<String> nameSet = new HashSet<>(Arrays.asList(names));
        for (ParameterInfo p : op.getQueryParameters()) {
            if (nameSet.contains(p.getName().toLowerCase())) return p.getName();
        }
        return null;
    }

    private String returnType(OperationInfo op) {
        ResponseInfo success = op.getPrimarySuccessResponse();
        if (success == null || "204".equals(success.getStatusCode())) return "void";
        if (success.getRef() != null) return CodeUtils.toClassName(success.getRef());
        SchemaInfo s = success.getSchema();
        if (s == null) return "void";
        if (s.getRef() != null) return CodeUtils.toClassName(s.getRef());
        if ("array".equals(s.getType())) {
            SchemaInfo items = s.getItems();
            if (items == null) return "List<Object>";
            if (items.getRef() != null) return "List<" + CodeUtils.toClassName(items.getRef()) + ">";
            return "List<" + CodeUtils.primitiveType(items.getType(), items.getFormat()) + ">";
        }
        return CodeUtils.primitiveType(s.getType(), s.getFormat());
    }

    private String paramSignature(OperationInfo op) {
        List<String> parts = new ArrayList<>();
        for (ParameterInfo p : op.getPathParameters()) {
            parts.add(paramJavaType(p) + " " + CodeUtils.toFieldName(p.getName()));
        }
        for (ParameterInfo p : op.getQueryParameters()) {
            parts.add(paramJavaType(p) + " " + CodeUtils.toFieldName(p.getName()));
        }
        for (ParameterInfo p : op.getHeaderParameters()) {
            parts.add("String " + CodeUtils.toFieldName(p.getName()));
        }
        if (op.getRequestBodyRef() != null) {
            parts.add(CodeUtils.toClassName(op.getRequestBodyRef()) + " request");
        } else if (op.getRequestBodySchema() != null) {
            parts.add("Object request");
        }
        return String.join(", ", parts);
    }

    private String paramJavaType(ParameterInfo p) {
        if (p.getSchema() == null) return "String";
        return CodeUtils.primitiveType(p.getSchema().getType(), p.getSchema().getFormat());
    }

    private void collectImports(OperationInfo op, Set<String> imports) {
        ResponseInfo success = op.getPrimarySuccessResponse();
        if (success != null) {
            if (success.getRef() != null) imports.add(CodeUtils.toClassName(success.getRef()));
            SchemaInfo s = success.getSchema();
            if (s != null && s.getRef() != null) imports.add(CodeUtils.toClassName(s.getRef()));
            if (s != null && "array".equals(s.getType()) && s.getItems() != null && s.getItems().getRef() != null) {
                imports.add(CodeUtils.toClassName(s.getItems().getRef()));
            }
        }
        if (op.getRequestBodyRef() != null) imports.add(CodeUtils.toClassName(op.getRequestBodyRef()));
    }

    private String detectEntityType(List<OperationInfo> ops) {
        for (OperationInfo op : ops) {
            if ("post".equalsIgnoreCase(op.getMethod())) {
                ResponseInfo r = op.getPrimarySuccessResponse();
                if (r != null && r.getRef() != null) return CodeUtils.toClassName(r.getRef());
                if (r != null && r.getSchema() != null && r.getSchema().getRef() != null)
                    return CodeUtils.toClassName(r.getSchema().getRef());
            }
        }
        for (OperationInfo op : ops) {
            ResponseInfo r = op.getPrimarySuccessResponse();
            if (r != null && r.getRef() != null) return CodeUtils.toClassName(r.getRef());
            if (r != null && r.getSchema() != null && r.getSchema().getRef() != null)
                return CodeUtils.toClassName(r.getSchema().getRef());
        }
        return null;
    }

    private String detectEntityType(OperationInfo op) {
        ResponseInfo r = op.getPrimarySuccessResponse();
        if (r == null) return null;
        if (r.getRef() != null) return CodeUtils.toClassName(r.getRef());
        SchemaInfo s = r.getSchema();
        if (s == null) return null;
        if (s.getRef() != null) return CodeUtils.toClassName(s.getRef());
        if ("array".equals(s.getType()) && s.getItems() != null && s.getItems().getRef() != null)
            return CodeUtils.toClassName(s.getItems().getRef());
        return null;
    }

    private enum OpPattern { LIST, CREATE, GET_BY_ID, UPDATE, DELETE, UNKNOWN }

    private OpPattern classifyOperation(OperationInfo op) {
        String method = op.getMethod().toLowerCase();
        boolean hasPathParam = !op.getPathParameters().isEmpty();
        boolean hasBody = op.getRequestBodyRef() != null || op.getRequestBodySchema() != null;
        return switch (method) {
            case "get"    -> hasPathParam ? OpPattern.GET_BY_ID : OpPattern.LIST;
            case "post"   -> OpPattern.CREATE;
            case "put", "patch" -> hasPathParam ? OpPattern.UPDATE : OpPattern.UNKNOWN;
            case "delete" -> hasPathParam ? OpPattern.DELETE : OpPattern.UNKNOWN;
            default       -> OpPattern.UNKNOWN;
        };
    }
}
