package com.springauto.generator;

import com.springauto.model.OpenApiSpec;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationClassGenerator {

    public Map<String, String> generate(OpenApiSpec spec, String basePackage) {
        Map<String, String> files = new LinkedHashMap<>();
        String appClass = CodeUtils.toClassName(spec.safeTitle()) + "Application";
        String pkgPath  = basePackage.replace('.', '/');

        files.put("src/main/java/" + pkgPath + "/" + appClass + ".java",
                generateMain(basePackage, appClass));
        files.put("src/main/java/" + pkgPath + "/config/OpenApiConfig.java",
                generateOpenApiConfig(basePackage, spec));
        files.put("src/main/java/" + pkgPath + "/config/WebServiceConfig.java",
                generateWebServiceConfig(basePackage));
        files.put("src/main/resources/application.properties",
                generateProperties(spec));
        return files;
    }

    private String generateMain(String pkg, String appClass) {
        return """
                package %s;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;

                @SpringBootApplication
                public class %s {
                    public static void main(String[] args) {
                        SpringApplication.run(%s.class, args);
                    }
                }
                """.formatted(pkg, appClass, appClass);
    }

    private String generateOpenApiConfig(String pkg, OpenApiSpec spec) {
        String title   = spec.getTitle() != null ? spec.getTitle() : "Generated API";
        String version = spec.getVersion() != null ? spec.getVersion() : "1.0.0";
        return """
                package %s.config;

                import io.swagger.v3.oas.models.OpenAPI;
                import io.swagger.v3.oas.models.info.Info;
                import org.springframework.context.annotation.Bean;
                import org.springframework.context.annotation.Configuration;

                @Configuration
                public class OpenApiConfig {

                    @Bean
                    public OpenAPI openAPI() {
                        return new OpenAPI()
                                .info(new Info()
                                        .title("%s")
                                        .version("%s"));
                    }
                }
                """.formatted(pkg, title, version);
    }

    private String generateWebServiceConfig(String pkg) {
        return """
                package %s.config;

                import org.springframework.boot.web.servlet.ServletRegistrationBean;
                import org.springframework.context.ApplicationContext;
                import org.springframework.context.annotation.Bean;
                import org.springframework.context.annotation.Configuration;
                import org.springframework.core.io.ClassPathResource;
                import org.springframework.ws.config.annotation.EnableWs;
                import org.springframework.ws.config.annotation.WsConfigurerAdapter;
                import org.springframework.ws.transport.http.MessageDispatcherServlet;
                import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
                import org.springframework.xml.xsd.SimpleXsdSchema;
                import org.springframework.xml.xsd.XsdSchema;

                @EnableWs
                @Configuration
                public class WebServiceConfig extends WsConfigurerAdapter {

                    @Bean
                    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
                            ApplicationContext context) {
                        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
                        servlet.setApplicationContext(context);
                        servlet.setTransformWsdlLocations(true);
                        return new ServletRegistrationBean<>(servlet, "/ws/*");
                    }

                    @Bean(name = "serviceWsdl")
                    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema schema) {
                        DefaultWsdl11Definition def = new DefaultWsdl11Definition();
                        def.setPortTypeName("ServicePort");
                        def.setLocationUri("/ws");
                        def.setTargetNamespace("http://generated.api/ws");
                        def.setSchema(schema);
                        return def;
                    }

                    @Bean
                    public XsdSchema schema() {
                        return new SimpleXsdSchema(new ClassPathResource("wsdl/schema.xsd"));
                    }
                }
                """.formatted(pkg);
    }

    private String generateProperties(OpenApiSpec spec) {
        String base = spec.getBaseUrl() != null ? spec.getBaseUrl() : "";
        return "server.port=8080\n" +
               (base.isBlank() ? "" : "server.servlet.context-path=" + base + "\n") +
               "spring.mvc.throw-exception-if-no-handler-found=true\n" +
               "spring.web.resources.add-mappings=false\n" +
               "spring.jackson.mapper.accept-case-insensitive-enums=true\n";
    }
}
