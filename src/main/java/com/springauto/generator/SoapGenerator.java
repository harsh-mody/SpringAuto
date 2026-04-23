package com.springauto.generator;

import com.springauto.model.*;

import java.util.*;

public class SoapGenerator {

    public Map<String, String> generate(OpenApiSpec spec, String basePackage) {
        Map<String, String> files = new LinkedHashMap<>();
        String pkgPath  = basePackage.replace('.', '/');
        String ns       = "http://" + basePackage + "/ws";

        // XSD schema (single, covering all operations)
        files.put("src/main/resources/wsdl/schema.xsd", generateXsd(spec, ns));

        for (Map.Entry<String, List<OperationInfo>> entry : spec.getOperationsByTag().entrySet()) {
            String tag  = entry.getKey();
            String ns2  = ns + "/" + tag;

            // Endpoint
            files.put("src/main/java/" + pkgPath + "/soap/" + CodeUtils.toClassName(tag) + "SoapEndpoint.java",
                    generateEndpoint(spec, basePackage, tag, entry.getValue(), ns2));

            // Request/Response wrappers per operation
            for (OperationInfo op : entry.getValue()) {
                String reqClass = CodeUtils.toClassName(op.getOperationId()) + "SoapRequest";
                String resClass = CodeUtils.toClassName(op.getOperationId()) + "SoapResponse";

                String bodyType   = op.getRequestBodyRef() != null
                        ? CodeUtils.toClassName(op.getRequestBodyRef()) : null;
                String resultType = soapResultType(op);

                files.put("src/main/java/" + pkgPath + "/soap/" + reqClass + ".java",
                        generateSoapRequest(basePackage, reqClass, bodyType, op, ns2));
                files.put("src/main/java/" + pkgPath + "/soap/" + resClass + ".java",
                        generateSoapResponse(basePackage, resClass, resultType, ns2));
            }

            // WSDL per tag
            files.put("src/main/resources/wsdl/" + tag + "-service.wsdl",
                    generateWsdl(spec, tag, entry.getValue(), ns2));
        }

        return files;
    }

    private String generateEndpoint(OpenApiSpec spec, String pkg, String tag,
                                    List<OperationInfo> ops, String ns) {
        StringBuilder sb = new StringBuilder();
        String className = CodeUtils.toClassName(tag) + "SoapEndpoint";
        String svcName   = CodeUtils.toClassName(tag) + "Service";
        String svcField  = CodeUtils.toFieldName(tag) + "Service";

        sb.append("package ").append(pkg).append(".soap;\n\n");
        sb.append("import ").append(pkg).append(".service.").append(svcName).append(";\n");
        sb.append("import org.springframework.ws.server.endpoint.annotation.Endpoint;\n");
        sb.append("import org.springframework.ws.server.endpoint.annotation.PayloadRoot;\n");
        sb.append("import org.springframework.ws.server.endpoint.annotation.RequestPayload;\n");
        sb.append("import org.springframework.ws.server.endpoint.annotation.ResponsePayload;\n\n");

        sb.append("@Endpoint\n");
        sb.append("public class ").append(className).append(" {\n\n");
        sb.append("    private static final String NAMESPACE_URI = \"").append(ns).append("\";\n");
        sb.append("    private final ").append(svcName).append(" ").append(svcField).append(";\n\n");
        sb.append("    public ").append(className).append("(").append(svcName)
          .append(" ").append(svcField).append(") {\n");
        sb.append("        this.").append(svcField).append(" = ").append(svcField).append(";\n");
        sb.append("    }\n\n");

        for (OperationInfo op : ops) {
            String reqClass = CodeUtils.toClassName(op.getOperationId()) + "SoapRequest";
            String resClass = CodeUtils.toClassName(op.getOperationId()) + "SoapResponse";
            String localPart = CodeUtils.toClassName(op.getOperationId()) + "Request";
            String resultType = soapResultType(op);

            sb.append("    @PayloadRoot(namespace = NAMESPACE_URI, localPart = \"").append(localPart).append("\")\n");
            sb.append("    @ResponsePayload\n");
            sb.append("    public ").append(resClass).append(" ").append(op.getOperationId())
              .append("(@RequestPayload ").append(reqClass).append(" request) {\n");
            sb.append("        ").append(resClass).append(" response = new ").append(resClass).append("();\n");

            List<String> args = new ArrayList<>();
            for (ParameterInfo p : op.getPathParameters())
                args.add("request.get" + CodeUtils.toClassName(CodeUtils.toFieldName(p.getName())) + "()");
            for (ParameterInfo p : op.getQueryParameters())
                args.add("request.get" + CodeUtils.toClassName(CodeUtils.toFieldName(p.getName())) + "()");
            if (op.getRequestBodyRef() != null) args.add("request.getBody()");

            String svcCall = svcField + "." + op.getOperationId() + "(" + String.join(", ", args) + ")";

            if ("void".equals(resultType)) {
                sb.append("        ").append(svcCall).append(";\n");
            } else {
                sb.append("        response.setResult(").append(svcCall).append(");\n");
            }
            sb.append("        response.setSuccess(true);\n");
            sb.append("        return response;\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String generateSoapRequest(String pkg, String className, String bodyType,
                                        OperationInfo op, String ns) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(".soap;\n\n");
        sb.append("import jakarta.xml.bind.annotation.XmlAccessType;\n");
        sb.append("import jakarta.xml.bind.annotation.XmlAccessorType;\n");
        sb.append("import jakarta.xml.bind.annotation.XmlRootElement;\n");
        if (bodyType != null) {
            sb.append("import ").append(pkg).append(".model.").append(bodyType).append(";\n");
        }
        sb.append("\n");
        sb.append("@XmlRootElement(namespace = \"").append(ns).append("\")\n");
        sb.append("@XmlAccessorType(XmlAccessType.FIELD)\n");
        sb.append("public class ").append(className).append(" {\n\n");

        // path + query parameters as fields
        for (ParameterInfo p : op.getPathParameters()) {
            String jt = CodeUtils.primitiveType(
                p.getSchema() != null ? p.getSchema().getType() : "string",
                p.getSchema() != null ? p.getSchema().getFormat() : null);
            String fn = CodeUtils.toFieldName(p.getName());
            sb.append("    private ").append(jt).append(" ").append(fn).append(";\n");
        }
        for (ParameterInfo p : op.getQueryParameters()) {
            String jt = CodeUtils.primitiveType(
                p.getSchema() != null ? p.getSchema().getType() : "string",
                p.getSchema() != null ? p.getSchema().getFormat() : null);
            String fn = CodeUtils.toFieldName(p.getName());
            sb.append("    private ").append(jt).append(" ").append(fn).append(";\n");
        }
        if (bodyType != null) {
            sb.append("    private ").append(bodyType).append(" body;\n");
        }
        sb.append("\n");

        // getters + setters
        for (ParameterInfo p : op.getPathParameters()) {
            String jt  = CodeUtils.primitiveType(
                p.getSchema() != null ? p.getSchema().getType() : "string",
                p.getSchema() != null ? p.getSchema().getFormat() : null);
            String fn  = CodeUtils.toFieldName(p.getName());
            String cap = CodeUtils.capitalize(fn);
            sb.append("    public ").append(jt).append(" get").append(cap).append("() { return ").append(fn).append("; }\n");
            sb.append("    public void set").append(cap).append("(").append(jt).append(" ").append(fn).append(") { this.").append(fn).append(" = ").append(fn).append("; }\n");
        }
        for (ParameterInfo p : op.getQueryParameters()) {
            String jt  = CodeUtils.primitiveType(
                p.getSchema() != null ? p.getSchema().getType() : "string",
                p.getSchema() != null ? p.getSchema().getFormat() : null);
            String fn  = CodeUtils.toFieldName(p.getName());
            String cap = CodeUtils.capitalize(fn);
            sb.append("    public ").append(jt).append(" get").append(cap).append("() { return ").append(fn).append("; }\n");
            sb.append("    public void set").append(cap).append("(").append(jt).append(" ").append(fn).append(") { this.").append(fn).append(" = ").append(fn).append("; }\n");
        }
        if (bodyType != null) {
            sb.append("    public ").append(bodyType).append(" getBody() { return body; }\n");
            sb.append("    public void setBody(").append(bodyType).append(" body) { this.body = body; }\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String generateSoapResponse(String pkg, String className, String resultType, String ns) {
        String importLine = "";
        if (!resultType.equals("void") && !resultType.equals("Object") && !resultType.startsWith("List")) {
            importLine = "import " + pkg + ".model." + resultType + ";\n";
        }
        String resultField = "void".equals(resultType) ? "" :
                "    private " + resultType + " result;\n" +
                "    public " + resultType + " getResult() { return result; }\n" +
                "    public void setResult(" + resultType + " result) { this.result = result; }\n";

        return """
                package %s.soap;

                import jakarta.xml.bind.annotation.XmlAccessType;
                import jakarta.xml.bind.annotation.XmlAccessorType;
                import jakarta.xml.bind.annotation.XmlRootElement;
                %s
                @XmlRootElement(namespace = "%s")
                @XmlAccessorType(XmlAccessType.FIELD)
                public class %s {

                    private boolean success;
                %s
                    public boolean isSuccess() { return success; }
                    public void setSuccess(boolean success) { this.success = success; }
                }
                """.formatted(pkg, importLine, ns, className, resultField);
    }

    private String generateXsd(OpenApiSpec spec, String ns) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n");
        sb.append("           targetNamespace=\"").append(ns).append("\"\n");
        sb.append("           xmlns:tns=\"").append(ns).append("\"\n");
        sb.append("           elementFormDefault=\"qualified\">\n\n");

        for (Map.Entry<String, List<OperationInfo>> tagEntry : spec.getOperationsByTag().entrySet()) {
            for (OperationInfo op : tagEntry.getValue()) {
                String reqLocal  = CodeUtils.toClassName(op.getOperationId()) + "Request";
                String resLocal  = CodeUtils.toClassName(op.getOperationId()) + "Response";

                sb.append("    <xs:element name=\"").append(reqLocal).append("\">\n");
                sb.append("        <xs:complexType><xs:sequence>\n");
                sb.append("            <xs:element name=\"param1\" type=\"xs:string\" minOccurs=\"0\"/>\n");
                sb.append("        </xs:sequence></xs:complexType>\n");
                sb.append("    </xs:element>\n\n");

                sb.append("    <xs:element name=\"").append(resLocal).append("\">\n");
                sb.append("        <xs:complexType><xs:sequence>\n");
                sb.append("            <xs:element name=\"success\" type=\"xs:boolean\"/>\n");
                sb.append("        </xs:sequence></xs:complexType>\n");
                sb.append("    </xs:element>\n\n");
            }
        }

        sb.append("</xs:schema>\n");
        return sb.toString();
    }

    private String generateWsdl(OpenApiSpec spec, String tag, List<OperationInfo> ops, String ns) {
        String svcName = CodeUtils.toClassName(tag) + "Service";
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n");
        sb.append("                  xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n");
        sb.append("                  xmlns:tns=\"").append(ns).append("\"\n");
        sb.append("                  targetNamespace=\"").append(ns).append("\"\n");
        sb.append("                  name=\"").append(svcName).append("\">\n\n");

        sb.append("    <wsdl:types><xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
        sb.append("        <!-- types defined in schema.xsd -->\n");
        sb.append("    </xs:schema></wsdl:types>\n\n");

        for (OperationInfo op : ops) {
            String opName = CodeUtils.toClassName(op.getOperationId());
            sb.append("    <wsdl:message name=\"").append(opName).append("Request\">\n");
            sb.append("        <wsdl:part name=\"parameters\" element=\"tns:").append(opName).append("Request\"/>\n");
            sb.append("    </wsdl:message>\n");
            sb.append("    <wsdl:message name=\"").append(opName).append("Response\">\n");
            sb.append("        <wsdl:part name=\"parameters\" element=\"tns:").append(opName).append("Response\"/>\n");
            sb.append("    </wsdl:message>\n\n");
        }

        sb.append("    <wsdl:portType name=\"").append(svcName).append("Port\">\n");
        for (OperationInfo op : ops) {
            String opName = CodeUtils.toClassName(op.getOperationId());
            sb.append("        <wsdl:operation name=\"").append(opName).append("\">\n");
            sb.append("            <wsdl:input message=\"tns:").append(opName).append("Request\"/>\n");
            sb.append("            <wsdl:output message=\"tns:").append(opName).append("Response\"/>\n");
            sb.append("        </wsdl:operation>\n");
        }
        sb.append("    </wsdl:portType>\n\n");

        sb.append("    <wsdl:binding name=\"").append(svcName).append("Binding\" type=\"tns:").append(svcName).append("Port\">\n");
        sb.append("        <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n");
        for (OperationInfo op : ops) {
            String opName = CodeUtils.toClassName(op.getOperationId());
            sb.append("        <wsdl:operation name=\"").append(opName).append("\">\n");
            sb.append("            <soap:operation soapAction=\"").append(ns).append("/").append(opName).append("\"/>\n");
            sb.append("            <wsdl:input><soap:body use=\"literal\"/></wsdl:input>\n");
            sb.append("            <wsdl:output><soap:body use=\"literal\"/></wsdl:output>\n");
            sb.append("        </wsdl:operation>\n");
        }
        sb.append("    </wsdl:binding>\n\n");

        sb.append("    <wsdl:service name=\"").append(svcName).append("\">\n");
        sb.append("        <wsdl:port name=\"").append(svcName).append("Port\" binding=\"tns:").append(svcName).append("Binding\">\n");
        sb.append("            <soap:address location=\"http://localhost:8080/ws\"/>\n");
        sb.append("        </wsdl:port>\n");
        sb.append("    </wsdl:service>\n\n");

        sb.append("</wsdl:definitions>\n");
        return sb.toString();
    }

    private String soapResultType(OperationInfo op) {
        ResponseInfo r = op.getPrimarySuccessResponse();
        if (r == null || "204".equals(r.getStatusCode())) return "void";
        if (r.getRef() != null) return CodeUtils.toClassName(r.getRef());
        SchemaInfo s = r.getSchema();
        if (s == null) return "void";
        if (s.getRef() != null) return CodeUtils.toClassName(s.getRef());
        return "Object";
    }
}
