package com.springauto.model;

public class ResponseInfo {
    private String statusCode;
    private String description;
    private String ref;
    private SchemaInfo schema;

    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public SchemaInfo getSchema() { return schema; }
    public void setSchema(SchemaInfo schema) { this.schema = schema; }

    public boolean is2xx() {
        return statusCode != null && statusCode.startsWith("2");
    }
}
