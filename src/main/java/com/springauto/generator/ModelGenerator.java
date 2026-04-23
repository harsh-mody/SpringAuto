package com.springauto.generator;

import com.springauto.model.OpenApiSpec;
import com.springauto.model.PropertyInfo;
import com.springauto.model.SchemaInfo;

import java.util.*;

public class ModelGenerator {

    public Map<String, String> generate(OpenApiSpec spec, String basePackage) {
        Map<String, String> files = new LinkedHashMap<>();
        String pkgPath = basePackage.replace('.', '/');

        for (Map.Entry<String, SchemaInfo> entry : spec.getSchemas().entrySet()) {
            String className = CodeUtils.toClassName(entry.getKey());
            String content = generateClass(spec, basePackage, className, entry.getValue());
            files.put("src/main/java/" + pkgPath + "/model/" + className + ".java", content);
        }
        return files;
    }

    private String generateClass(OpenApiSpec spec, String pkg, String className, SchemaInfo schema) {
        StringBuilder sb = new StringBuilder();

        // collect needed imports
        Set<String> imports = new TreeSet<>();
        imports.add("java.io.Serializable");
        boolean needsList = false;
        boolean needsValid = false;
        boolean needsNotNull = false;
        boolean needsNotBlank = false;
        boolean needsSize = false;
        boolean needsPattern = false;
        boolean needsEmail = false;
        boolean needsMin = false;
        boolean needsMax = false;
        boolean needsDecimalMin = false;
        boolean needsDecimalMax = false;

        for (PropertyInfo p : schema.getProperties().values()) {
            String jt = CodeUtils.javaType(p, pkg);
            if (jt.startsWith("List<")) needsList = true;
            if (jt.equals("java.time.LocalDate")) imports.add("java.time.LocalDate");
            if (jt.equals("java.time.OffsetDateTime")) imports.add("java.time.OffsetDateTime");
            if (jt.equals("java.util.UUID")) imports.add("java.util.UUID");
            if (p.getRef() != null) needsValid = true;
            if (p.isRequired()) {
                if ("string".equals(p.getType()) && p.getEnumValues().isEmpty()) needsNotBlank = true;
                else needsNotNull = true;
            }
            if (p.getMinLength() != null || p.getMaxLength() != null ||
                p.getMinItems() != null || p.getMaxItems() != null) needsSize = true;
            if (p.getPattern() != null) needsPattern = true;
            if ("email".equals(p.getFormat())) needsEmail = true;
            if (p.getMinimum() != null) {
                if ("integer".equals(p.getType())) needsMin = true;
                else needsDecimalMin = true;
            }
            if (p.getMaximum() != null) {
                if ("integer".equals(p.getType())) needsMax = true;
                else needsDecimalMax = true;
            }
        }

        if (needsList)       imports.add("java.util.List");
        if (needsValid)      imports.add("jakarta.validation.Valid");
        if (needsNotNull)    imports.add("jakarta.validation.constraints.NotNull");
        if (needsNotBlank)   imports.add("jakarta.validation.constraints.NotBlank");
        if (needsSize)       imports.add("jakarta.validation.constraints.Size");
        if (needsPattern)    imports.add("jakarta.validation.constraints.Pattern");
        if (needsEmail)      imports.add("jakarta.validation.constraints.Email");
        if (needsMin)        imports.add("jakarta.validation.constraints.Min");
        if (needsMax)        imports.add("jakarta.validation.constraints.Max");
        if (needsDecimalMin) imports.add("jakarta.validation.constraints.DecimalMin");
        if (needsDecimalMax) imports.add("jakarta.validation.constraints.DecimalMax");

        sb.append("package ").append(pkg).append(".model;\n\n");
        for (String imp : imports) sb.append("import ").append(imp).append(";\n");
        sb.append("\n");

        if (schema.getDescription() != null) {
            sb.append("/** ").append(schema.getDescription()).append(" */\n");
        }
        sb.append("public class ").append(className).append(" implements Serializable {\n\n");

        // inner enums for enum properties
        for (PropertyInfo p : schema.getProperties().values()) {
            if (!p.getEnumValues().isEmpty()) {
                String enumName = CodeUtils.toClassName(p.getName()) + "Enum";
                sb.append("    public enum ").append(enumName).append(" {\n");
                for (String v : p.getEnumValues()) {
                    sb.append("        ").append(CodeUtils.toEnumValue(v)).append(",\n");
                }
                sb.append("    }\n\n");
            }
        }

        // fields
        for (PropertyInfo p : schema.getProperties().values()) {
            String jt = resolvedType(p);
            String annotations = CodeUtils.validationAnnotations(p, "    ");
            sb.append(annotations);
            sb.append("    private ").append(jt).append(" ").append(CodeUtils.toFieldName(p.getName())).append(";\n");
        }
        sb.append("\n");

        // getters + setters
        for (PropertyInfo p : schema.getProperties().values()) {
            String jt = resolvedType(p);
            String field = CodeUtils.toFieldName(p.getName());
            String cap   = CodeUtils.capitalize(field);
            sb.append("    public ").append(jt).append(" get").append(cap).append("() { return ").append(field).append("; }\n");
            sb.append("    public void set").append(cap).append("(").append(jt).append(" ").append(field).append(") { this.").append(field).append(" = ").append(field).append("; }\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String resolvedType(PropertyInfo p) {
        if (p.getRef() != null) return CodeUtils.toClassName(p.getRef());
        if (!p.getEnumValues().isEmpty()) return CodeUtils.toClassName(p.getName()) + "Enum";
        if ("array".equals(p.getType())) {
            SchemaInfo items = p.getItems();
            if (items == null) return "List<Object>";
            if (items.getRef() != null) return "List<" + CodeUtils.toClassName(items.getRef()) + ">";
            return "List<" + CodeUtils.primitiveType(items.getType(), items.getFormat()) + ">";
        }
        return CodeUtils.primitiveType(p.getType(), p.getFormat());
    }
}
