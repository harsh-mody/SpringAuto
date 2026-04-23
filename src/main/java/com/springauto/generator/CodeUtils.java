package com.springauto.generator;

import com.springauto.model.PropertyInfo;
import com.springauto.model.SchemaInfo;

import java.util.*;

public class CodeUtils {

    public static String toClassName(String name) {
        if (name == null || name.isBlank()) return "Unknown";
        String[] parts = name.split("[_\\-\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    public static String toFieldName(String name) {
        String cn = toClassName(name);
        if (cn.isEmpty()) return "field";
        return Character.toLowerCase(cn.charAt(0)) + cn.substring(1);
    }

    public static String toMethodName(String prefix, String name) {
        return prefix + toClassName(name);
    }

    public static String toEnumValue(String raw) {
        return raw.toUpperCase().replaceAll("[^A-Z0-9]", "_");
    }

    /** Java type for a PropertyInfo field */
    public static String javaType(PropertyInfo p, String basePackage) {
        if (p.getRef() != null) return toClassName(p.getRef());
        if ("array".equals(p.getType())) {
            String inner = itemType(p.getItems(), basePackage);
            return "List<" + inner + ">";
        }
        return primitiveType(p.getType(), p.getFormat());
    }

    /** Java type for a SchemaInfo (used for return types) */
    public static String javaType(SchemaInfo s, String basePackage) {
        if (s == null) return "Void";
        if (s.getRef() != null) return toClassName(s.getRef());
        if ("array".equals(s.getType())) {
            String inner = itemType(s.getItems(), basePackage);
            return "List<" + inner + ">";
        }
        return primitiveType(s.getType(), s.getFormat());
    }

    private static String itemType(SchemaInfo items, String basePackage) {
        if (items == null) return "Object";
        if (items.getRef() != null) return toClassName(items.getRef());
        return primitiveType(items.getType(), items.getFormat());
    }

    public static String primitiveType(String type, String format) {
        if (type == null) return "Object";
        return switch (type) {
            case "integer" -> "int64".equals(format) ? "Long" : "Integer";
            case "number"  -> "float".equals(format) ? "Float" : "Double";
            case "boolean" -> "Boolean";
            case "string"  -> switch (format == null ? "" : format) {
                case "date"      -> "java.time.LocalDate";
                case "date-time" -> "java.time.OffsetDateTime";
                case "uuid"      -> "java.util.UUID";
                case "binary", "byte" -> "byte[]";
                default          -> "String";
            };
            default -> "Object";
        };
    }

    /** Validation annotations for a PropertyInfo */
    public static String validationAnnotations(PropertyInfo p, String indent) {
        StringBuilder sb = new StringBuilder();
        boolean isString = "string".equals(p.getType()) || p.getRef() == null && p.getType() == null;
        boolean isNum    = "integer".equals(p.getType()) || "number".equals(p.getType());

        if (p.isRequired()) {
            if ("string".equals(p.getType()) && p.getRef() == null && p.getEnumValues().isEmpty()) {
                sb.append(indent).append("@NotBlank\n");
            } else {
                sb.append(indent).append("@NotNull\n");
            }
        }

        if (p.getRef() != null) {
            sb.append(indent).append("@Valid\n");
        }

        if ("email".equals(p.getFormat())) {
            sb.append(indent).append("@Email\n");
        }

        if (p.getPattern() != null) {
            String escaped = p.getPattern().replace("\\", "\\\\").replace("\"", "\\\"");
            sb.append(indent).append("@Pattern(regexp = \"").append(escaped).append("\")\n");
        }

        boolean hasSizeMin = p.getMinLength() != null || p.getMinItems() != null;
        boolean hasSizeMax = p.getMaxLength() != null || p.getMaxItems() != null;
        if (hasSizeMin || hasSizeMax) {
            sb.append(indent).append("@Size(");
            List<String> parts = new ArrayList<>();
            if (hasSizeMin) parts.add("min = " + (p.getMinLength() != null ? p.getMinLength() : p.getMinItems()));
            if (hasSizeMax) parts.add("max = " + (p.getMaxLength() != null ? p.getMaxLength() : p.getMaxItems()));
            sb.append(String.join(", ", parts)).append(")\n");
        }

        if (isNum) {
            if (p.getMinimum() != null) {
                if ("integer".equals(p.getType())) {
                    sb.append(indent).append("@Min(").append(p.getMinimum().longValue()).append("L)\n");
                } else {
                    sb.append(indent).append("@DecimalMin(\"").append(p.getMinimum()).append("\")\n");
                }
            }
            if (p.getMaximum() != null) {
                if ("integer".equals(p.getType())) {
                    sb.append(indent).append("@Max(").append(p.getMaximum().longValue()).append("L)\n");
                } else {
                    sb.append(indent).append("@DecimalMax(\"").append(p.getMaximum()).append("\")\n");
                }
            }
        }

        return sb.toString();
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static boolean needsListImport(PropertyInfo p) {
        return "array".equals(p.getType());
    }

    /** Spring MVC method annotation for HTTP method */
    public static String springMappingAnnotation(String method, String path) {
        return switch (method.toLowerCase()) {
            case "get"    -> "@GetMapping(\"" + path + "\")";
            case "post"   -> "@PostMapping(\"" + path + "\")";
            case "put"    -> "@PutMapping(\"" + path + "\")";
            case "delete" -> "@DeleteMapping(\"" + path + "\")";
            case "patch"  -> "@PatchMapping(\"" + path + "\")";
            default       -> "@RequestMapping(method = RequestMethod." + method.toUpperCase() + ", path = \"" + path + "\")";
        };
    }

    public static int successStatus(String method) {
        return "post".equalsIgnoreCase(method) ? 201 : 200;
    }
}
