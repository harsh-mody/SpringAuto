package com.springauto.model;

import java.util.*;

public class OpenApiSpec {
    private String title;
    private String version;
    private String baseUrl;
    private Map<String, SchemaInfo> schemas = new LinkedHashMap<>();
    private Map<String, List<OperationInfo>> operationsByTag = new LinkedHashMap<>();

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public Map<String, SchemaInfo> getSchemas() { return schemas; }
    public void setSchemas(Map<String, SchemaInfo> schemas) { this.schemas = schemas; }
    public Map<String, List<OperationInfo>> getOperationsByTag() { return operationsByTag; }
    public void setOperationsByTag(Map<String, List<OperationInfo>> operationsByTag) { this.operationsByTag = operationsByTag; }

    public String safeTitle() {
        if (title == null || title.isBlank()) return "GeneratedApi";
        return title.replaceAll("[^a-zA-Z0-9]", "");
    }
}
