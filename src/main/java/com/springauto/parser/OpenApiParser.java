package com.springauto.parser;

import com.springauto.model.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class OpenApiParser {

    public OpenApiSpec parse(File file) throws IOException {
        Map<String, Object> root;
        try (InputStream in = new FileInputStream(file)) {
            root = new Yaml().load(in);
        }

        OpenApiSpec spec = new OpenApiSpec();

        // info
        Map<String, Object> info = (Map<String, Object>) root.get("info");
        if (info != null) {
            spec.setTitle(str(info, "title"));
            spec.setVersion(str(info, "version"));
        }

        // servers
        List<Map<String, Object>> servers = (List<Map<String, Object>>) root.get("servers");
        if (servers != null && !servers.isEmpty()) {
            spec.setBaseUrl(str(servers.get(0), "url"));
        }

        // components/schemas
        Map<String, Object> components = (Map<String, Object>) root.get("components");
        Map<String, SchemaInfo> schemas = new LinkedHashMap<>();
        if (components != null) {
            Map<String, Object> schemaMap = (Map<String, Object>) components.get("schemas");
            if (schemaMap != null) {
                for (Map.Entry<String, Object> e : schemaMap.entrySet()) {
                    SchemaInfo si = parseSchema(e.getKey(), (Map<String, Object>) e.getValue());
                    schemas.put(e.getKey(), si);
                }
            }
        }
        // second pass: resolve required flags on properties
        for (SchemaInfo si : schemas.values()) {
            for (Map.Entry<String, PropertyInfo> pe : si.getProperties().entrySet()) {
                pe.getValue().setRequired(si.getRequired().contains(pe.getKey()));
            }
        }
        spec.setSchemas(schemas);

        // paths
        Map<String, Object> paths = (Map<String, Object>) root.get("paths");
        Map<String, List<OperationInfo>> byTag = new LinkedHashMap<>();
        if (paths != null) {
            for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
                String path = pathEntry.getKey();
                Map<String, Object> pathItem = (Map<String, Object>) pathEntry.getValue();
                for (String method : List.of("get", "post", "put", "delete", "patch", "head", "options")) {
                    if (!pathItem.containsKey(method)) continue;
                    Map<String, Object> opMap = (Map<String, Object>) pathItem.get(method);
                    OperationInfo op = parseOperation(method, path, opMap, schemas);
                    byTag.computeIfAbsent(op.getTag(), k -> new ArrayList<>()).add(op);
                }
            }
        }
        spec.setOperationsByTag(byTag);
        return spec;
    }

    private OperationInfo parseOperation(String method, String path,
                                         Map<String, Object> opMap,
                                         Map<String, SchemaInfo> schemas) {
        OperationInfo op = new OperationInfo();
        op.setMethod(method);
        op.setPath(path);
        op.setOperationId(str(opMap, "operationId"));
        op.setSummary(str(opMap, "summary"));
        op.setDescription(str(opMap, "description"));

        List<String> tags = (List<String>) opMap.get("tags");
        op.setTag(tags != null && !tags.isEmpty() ? tags.get(0) : "default");

        // parameters
        List<Map<String, Object>> params = (List<Map<String, Object>>) opMap.get("parameters");
        if (params != null) {
            for (Map<String, Object> p : params) {
                ParameterInfo pi = new ParameterInfo();
                pi.setIn(str(p, "in"));
                pi.setName(str(p, "name"));
                pi.setRequired(Boolean.TRUE.equals(p.get("required")));
                Map<String, Object> schema = (Map<String, Object>) p.get("schema");
                if (schema != null) {
                    pi.setSchema(parseSchemaInline(schema));
                }
                op.getParameters().add(pi);
            }
        }

        // requestBody
        Map<String, Object> rb = (Map<String, Object>) opMap.get("requestBody");
        if (rb != null) {
            Map<String, Object> content = (Map<String, Object>) rb.get("content");
            if (content != null) {
                Map<String, Object> json = (Map<String, Object>) content.get("application/json");
                if (json != null) {
                    Map<String, Object> schema = (Map<String, Object>) json.get("schema");
                    if (schema != null) {
                        String ref = refName(schema);
                        if (ref != null) {
                            op.setRequestBodyRef(ref);
                            op.setRequestBodySchema(schemas.get(ref));
                        } else {
                            op.setRequestBodySchema(parseSchemaInline(schema));
                        }
                    }
                }
            }
        }

        // responses
        Map<String, Object> responses = (Map<String, Object>) opMap.get("responses");
        if (responses != null) {
            for (Map.Entry<String, Object> re : responses.entrySet()) {
                ResponseInfo ri = new ResponseInfo();
                ri.setStatusCode(re.getKey());
                Map<String, Object> rv = (Map<String, Object>) re.getValue();
                ri.setDescription(str(rv, "description"));
                Map<String, Object> content = (Map<String, Object>) rv.get("content");
                if (content != null) {
                    Map<String, Object> json = (Map<String, Object>) content.get("application/json");
                    if (json != null) {
                        Map<String, Object> schema = (Map<String, Object>) json.get("schema");
                        if (schema != null) {
                            String ref = refName(schema);
                            if (ref != null) {
                                ri.setRef(ref);
                                ri.setSchema(schemas.get(ref));
                            } else {
                                ri.setSchema(parseSchemaInline(schema));
                            }
                        }
                    }
                }
                op.getResponses().add(ri);
            }
        }

        return op;
    }

    private SchemaInfo parseSchema(String name, Map<String, Object> map) {
        SchemaInfo si = parseSchemaInline(map);
        si.setName(name);
        return si;
    }

    private SchemaInfo parseSchemaInline(Map<String, Object> map) {
        SchemaInfo si = new SchemaInfo();
        if (map == null) return si;

        // handle $ref
        String ref = refName(map);
        if (ref != null) { si.setRef(ref); return si; }

        // handle allOf by merging
        List<Map<String, Object>> allOf = (List<Map<String, Object>>) map.get("allOf");
        if (allOf != null) {
            si.setType("object");
            for (Map<String, Object> part : allOf) {
                SchemaInfo merged = parseSchemaInline(part);
                si.getProperties().putAll(merged.getProperties());
                si.getRequired().addAll(merged.getRequired());
            }
            return si;
        }

        si.setType(str(map, "type"));
        si.setFormat(str(map, "format"));
        si.setDescription(str(map, "description"));

        // enum
        List<Object> enumVals = (List<Object>) map.get("enum");
        if (enumVals != null) {
            si.setEnum(true);
            enumVals.forEach(v -> si.getEnumValues().add(v.toString()));
        }

        // required
        List<String> req = (List<String>) map.get("required");
        if (req != null) si.setRequired(req);

        // constraints
        if (map.containsKey("minLength")) si.setMinLength(toInt(map.get("minLength")));
        if (map.containsKey("maxLength")) si.setMaxLength(toInt(map.get("maxLength")));
        if (map.containsKey("pattern"))   si.setPattern(str(map, "pattern"));
        if (map.containsKey("minimum"))   si.setMinimum(toDouble(map.get("minimum")));
        if (map.containsKey("maximum"))   si.setMaximum(toDouble(map.get("maximum")));
        if (map.containsKey("minItems"))  si.setMinItems(toInt(map.get("minItems")));
        if (map.containsKey("maxItems"))  si.setMaxItems(toInt(map.get("maxItems")));

        // properties
        Map<String, Object> props = (Map<String, Object>) map.get("properties");
        if (props != null) {
            for (Map.Entry<String, Object> pe : props.entrySet()) {
                PropertyInfo pi = parseProperty(pe.getKey(), (Map<String, Object>) pe.getValue());
                si.getProperties().put(pe.getKey(), pi);
            }
        }

        // array items
        if ("array".equals(si.getType())) {
            Map<String, Object> items = (Map<String, Object>) map.get("items");
            if (items != null) si.setItems(parseSchemaInline(items));
        }

        return si;
    }

    private PropertyInfo parseProperty(String name, Map<String, Object> map) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(name);
        if (map == null) return pi;

        String ref = refName(map);
        if (ref != null) { pi.setRef(ref); return pi; }

        pi.setType(str(map, "type"));
        pi.setFormat(str(map, "format"));
        pi.setDescription(str(map, "description"));

        List<Object> enumVals = (List<Object>) map.get("enum");
        if (enumVals != null) enumVals.forEach(v -> pi.getEnumValues().add(v.toString()));

        if (map.containsKey("minLength")) pi.setMinLength(toInt(map.get("minLength")));
        if (map.containsKey("maxLength")) pi.setMaxLength(toInt(map.get("maxLength")));
        if (map.containsKey("pattern"))   pi.setPattern(str(map, "pattern"));
        if (map.containsKey("minimum"))   pi.setMinimum(toDouble(map.get("minimum")));
        if (map.containsKey("maximum"))   pi.setMaximum(toDouble(map.get("maximum")));
        if (map.containsKey("minItems"))  pi.setMinItems(toInt(map.get("minItems")));
        if (map.containsKey("maxItems"))  pi.setMaxItems(toInt(map.get("maxItems")));

        if ("array".equals(pi.getType())) {
            Map<String, Object> items = (Map<String, Object>) map.get("items");
            if (items != null) pi.setItems(parseSchemaInline(items));
        }

        return pi;
    }

    private String refName(Map<String, Object> map) {
        Object r = map.get("$ref");
        if (r == null) return null;
        String s = r.toString();
        int idx = s.lastIndexOf('/');
        return idx >= 0 ? s.substring(idx + 1) : s;
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? null : v.toString();
    }

    private int toInt(Object v) {
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(v.toString());
    }

    private double toDouble(Object v) {
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(v.toString());
    }
}
