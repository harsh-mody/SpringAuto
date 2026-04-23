package com.springauto.model;

public class ParameterInfo {
    private String in;
    private String name;
    private boolean required;
    private SchemaInfo schema;

    public String getIn() { return in; }
    public void setIn(String in) { this.in = in; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public SchemaInfo getSchema() { return schema; }
    public void setSchema(SchemaInfo schema) { this.schema = schema; }

    public boolean isPath() { return "path".equals(in); }
    public boolean isQuery() { return "query".equals(in); }
    public boolean isHeader() { return "header".equals(in); }
}
