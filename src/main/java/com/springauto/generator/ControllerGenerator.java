package com.springauto.generator;

import com.springauto.model.*;

import java.util.*;

public class ControllerGenerator {

    public Map<String, String> generate(OpenApiSpec spec, String basePackage) {
        Map<String, String> files = new LinkedHashMap<>();
        String pkgPath = basePackage.replace('.', '/');

        for (Map.Entry<String, List<OperationInfo>> entry : spec.getOperationsByTag().entrySet()) {
            String tag        = entry.getKey();
            String className  = CodeUtils.toClassName(tag) + "Controller";
            String content    = generateController(spec, basePackage, tag, entry.getValue());
            files.put("src/main/java/" + pkgPath + "/controller/" + className + ".java", content);
        }
        return files;
    }

    private String generateController(OpenApiSpec spec, String pkg, String tag, List<OperationInfo> ops) {
        StringBuilder sb = new StringBuilder();
        String className = CodeUtils.toClassName(tag) + "Controller";
        String svcName   = CodeUtils.toClassName(tag) + "Service";
        String svcField  = CodeUtils.toFieldName(tag) + "Service";

        Set<String> modelImports = new TreeSet<>();
        for (OperationInfo op : ops) {
            collectModelImports(op, modelImports);
        }

        sb.append("package ").append(pkg).append(".controller;\n\n");
        sb.append("import ").append(pkg).append(".dto.ApiResponse;\n");
        sb.append("import ").append(pkg).append(".service.").append(svcName).append(";\n");
        for (String m : modelImports) sb.append("import ").append(pkg).append(".model.").append(m).append(";\n");
        sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
        sb.append("import io.swagger.v3.oas.annotations.responses.ApiResponses;\n");
        sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        sb.append("import jakarta.validation.Valid;\n");
        sb.append("import org.springframework.http.HttpStatus;\n");
        sb.append("import org.springframework.http.ResponseEntity;\n");
        sb.append("import org.springframework.validation.annotation.Validated;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import java.util.List;\n\n");

        sb.append("@RestController\n");
        sb.append("@Validated\n");
        sb.append("@Tag(name = \"").append(tag).append("\")\n");
        sb.append("public class ").append(className).append(" {\n\n");

        sb.append("    private final ").append(svcName).append(" ").append(svcField).append(";\n\n");
        sb.append("    public ").append(className).append("(").append(svcName).append(" ").append(svcField).append(") {\n");
        sb.append("        this.").append(svcField).append(" = ").append(svcField).append(";\n");
        sb.append("    }\n\n");

        for (OperationInfo op : ops) {
            sb.append(generateMethod(pkg, op, svcField));
            sb.append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String generateMethod(String pkg, OperationInfo op, String svcField) {
        StringBuilder sb = new StringBuilder();

        String summary = op.getSummary() != null ? op.getSummary() : op.getOperationId();
        if (summary != null) {
            sb.append("    @Operation(summary = \"").append(summary).append("\")\n");
        }

        // swagger response annotations
        sb.append("    @ApiResponses(value = {\n");
        for (ResponseInfo r : op.getResponses()) {
            sb.append("        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = \"")
              .append(r.getStatusCode()).append("\", description = \"")
              .append(r.getDescription() != null ? r.getDescription() : "").append("\"),\n");
        }
        sb.append("    })\n");

        // mapping
        sb.append("    ").append(CodeUtils.springMappingAnnotation(op.getMethod(), op.getPath())).append("\n");

        // return type
        ResponseInfo success = op.getPrimarySuccessResponse();
        String returnType = resolveReturnType(success);
        String httpStatus  = resolveHttpStatus(op.getMethod(), success);

        sb.append("    public ResponseEntity<ApiResponse<").append(returnType).append(">> ")
          .append(op.getOperationId()).append("(\n");

        // params
        List<String> params = new ArrayList<>();
        for (ParameterInfo p : op.getPathParameters()) {
            String jt = CodeUtils.primitiveType(
                p.getSchema() != null ? p.getSchema().getType() : "string",
                p.getSchema() != null ? p.getSchema().getFormat() : null);
            params.add("            @PathVariable(name = \"" + p.getName() + "\") " + jt + " " + CodeUtils.toFieldName(p.getName()));
        }
        for (ParameterInfo p : op.getQueryParameters()) {
            String jt = CodeUtils.primitiveType(
                p.getSchema() != null ? p.getSchema().getType() : "string",
                p.getSchema() != null ? p.getSchema().getFormat() : null);
            params.add("            @RequestParam(name = \"" + p.getName() + "\", required = " + p.isRequired() + ") " + jt + " " + CodeUtils.toFieldName(p.getName()));
        }
        for (ParameterInfo p : op.getHeaderParameters()) {
            params.add("            @RequestHeader(name = \"" + p.getName() + "\", required = " + p.isRequired() + ") String " + CodeUtils.toFieldName(p.getName()));
        }
        if (op.getRequestBodyRef() != null) {
            params.add("            @Valid @RequestBody " + CodeUtils.toClassName(op.getRequestBodyRef()) + " request");
        } else if (op.getRequestBodySchema() != null) {
            params.add("            @Valid @RequestBody Object request");
        }

        sb.append(String.join(",\n", params));
        sb.append("\n    ) {\n");

        // body
        String serviceCall = buildServiceCall(op, svcField);
        if ("void".equals(returnType) || "Void".equals(returnType) || "204".equals(
            success != null ? success.getStatusCode() : "")) {
            sb.append("        ").append(serviceCall).append(";\n");
            sb.append("        return ResponseEntity.status(HttpStatus.").append(httpStatus).append(")\n");
            sb.append("                .body(ApiResponse.success(null, ")
              .append(success != null ? success.getStatusCode() : "200")
              .append(", \"").append(summary != null ? summary : "Success").append("\"));\n");
        } else {
            sb.append("        ").append(returnType).append(" result = ").append(serviceCall).append(";\n");
            sb.append("        return ResponseEntity.status(HttpStatus.").append(httpStatus).append(")\n");
            sb.append("                .body(ApiResponse.success(result, ")
              .append(success != null ? success.getStatusCode() : "200")
              .append(", \"").append(summary != null ? summary : "Success").append("\"));\n");
        }

        sb.append("    }\n");
        return sb.toString();
    }

    private String resolveReturnType(ResponseInfo success) {
        if (success == null) return "Void";
        if ("204".equals(success.getStatusCode())) return "Void";
        if (success.getRef() != null) return CodeUtils.toClassName(success.getRef());
        SchemaInfo s = success.getSchema();
        if (s == null) return "Void";
        if (s.getRef() != null) return CodeUtils.toClassName(s.getRef());
        if ("array".equals(s.getType())) {
            SchemaInfo items = s.getItems();
            if (items == null) return "List<Object>";
            if (items.getRef() != null) return "List<" + CodeUtils.toClassName(items.getRef()) + ">";
            return "List<" + CodeUtils.primitiveType(items.getType(), items.getFormat()) + ">";
        }
        return CodeUtils.primitiveType(s.getType(), s.getFormat());
    }

    private String resolveHttpStatus(String method, ResponseInfo success) {
        if (success == null) return "OK";
        return switch (success.getStatusCode()) {
            case "201" -> "CREATED";
            case "202" -> "ACCEPTED";
            case "204" -> "NO_CONTENT";
            default    -> "OK";
        };
    }

    private String buildServiceCall(OperationInfo op, String svcField) {
        List<String> args = new ArrayList<>();
        for (ParameterInfo p : op.getPathParameters())  args.add(CodeUtils.toFieldName(p.getName()));
        for (ParameterInfo p : op.getQueryParameters()) args.add(CodeUtils.toFieldName(p.getName()));
        for (ParameterInfo p : op.getHeaderParameters()) args.add(CodeUtils.toFieldName(p.getName()));
        if (op.getRequestBodyRef() != null || op.getRequestBodySchema() != null) args.add("request");
        return svcField + "." + op.getOperationId() + "(" + String.join(", ", args) + ")";
    }

    private void collectModelImports(OperationInfo op, Set<String> imports) {
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
}
