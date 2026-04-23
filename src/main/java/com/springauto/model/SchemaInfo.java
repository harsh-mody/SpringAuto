package com.springauto.model;

import java.util.*;

public class SchemaInfo {
    private String name;
    private String type;
    private String format;
    private String ref;
    private String description;
    private boolean isEnum;
    private List<String> enumValues = new ArrayList<>();
    private List<String> required = new ArrayList<>();
    private Map<String, PropertyInfo> properties = new LinkedHashMap<>();
    private SchemaInfo items;

    private Integer minLength;
    private Integer maxLength;
    private String pattern;
    private Double minimum;
    private Double maximum;
    private Integer minItems;
    private Integer maxItems;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isEnum() { return isEnum; }
    public void setEnum(boolean anEnum) { isEnum = anEnum; }
    public List<String> getEnumValues() { return enumValues; }
    public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
    public List<String> getRequired() { return required; }
    public void setRequired(List<String> required) { this.required = required; }
    public Map<String, PropertyInfo> getProperties() { return properties; }
    public void setProperties(Map<String, PropertyInfo> properties) { this.properties = properties; }
    public SchemaInfo getItems() { return items; }
    public void setItems(SchemaInfo items) { this.items = items; }
    public Integer getMinLength() { return minLength; }
    public void setMinLength(Integer minLength) { this.minLength = minLength; }
    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public Double getMinimum() { return minimum; }
    public void setMinimum(Double minimum) { this.minimum = minimum; }
    public Double getMaximum() { return maximum; }
    public void setMaximum(Double maximum) { this.maximum = maximum; }
    public Integer getMinItems() { return minItems; }
    public void setMinItems(Integer minItems) { this.minItems = minItems; }
    public Integer getMaxItems() { return maxItems; }
    public void setMaxItems(Integer maxItems) { this.maxItems = maxItems; }
}
