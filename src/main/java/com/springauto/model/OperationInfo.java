package com.springauto.model;

import java.util.*;

public class OperationInfo {
    private String method;
    private String path;
    private String operationId;
    private String summary;
    private String description;
    private String tag;
    private List<ParameterInfo> parameters = new ArrayList<>();
    private SchemaInfo requestBodySchema;
    private String requestBodyRef;
    private List<ResponseInfo> responses = new ArrayList<>();

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public List<ParameterInfo> getParameters() { return parameters; }
    public void setParameters(List<ParameterInfo> parameters) { this.parameters = parameters; }
    public SchemaInfo getRequestBodySchema() { return requestBodySchema; }
    public void setRequestBodySchema(SchemaInfo requestBodySchema) { this.requestBodySchema = requestBodySchema; }
    public String getRequestBodyRef() { return requestBodyRef; }
    public void setRequestBodyRef(String requestBodyRef) { this.requestBodyRef = requestBodyRef; }
    public List<ResponseInfo> getResponses() { return responses; }
    public void setResponses(List<ResponseInfo> responses) { this.responses = responses; }

    public ResponseInfo getPrimarySuccessResponse() {
        return responses.stream().filter(ResponseInfo::is2xx).findFirst().orElse(null);
    }

    public List<ParameterInfo> getPathParameters() {
        return parameters.stream().filter(ParameterInfo::isPath).toList();
    }

    public List<ParameterInfo> getQueryParameters() {
        return parameters.stream().filter(ParameterInfo::isQuery).toList();
    }

    public List<ParameterInfo> getHeaderParameters() {
        return parameters.stream().filter(ParameterInfo::isHeader).toList();
    }
}
