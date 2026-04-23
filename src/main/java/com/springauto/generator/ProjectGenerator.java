package com.springauto.generator;

import com.springauto.model.OpenApiSpec;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ProjectGenerator {

    private final OpenApiSpec spec;
    private final File outputDir;
    private final String basePackage;
    private final boolean scaffold;

    public ProjectGenerator(OpenApiSpec spec, File outputDir, String basePackage, boolean scaffold) {
        this.spec        = spec;
        this.outputDir   = outputDir;
        this.basePackage = basePackage;
        this.scaffold    = scaffold;
    }

    public void generate() throws IOException {
        int count = 0;

        // pom.xml
        count += write("pom.xml", new PomXmlGenerator().generate(spec, basePackage));

        // application class, config, properties
        for (Map.Entry<String, String> e : new ApplicationClassGenerator().generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        // models
        for (Map.Entry<String, String> e : new ModelGenerator().generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        // controllers
        for (Map.Entry<String, String> e : new ControllerGenerator().generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        // services
        for (Map.Entry<String, String> e : new ServiceGenerator(scaffold).generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        // DTOs
        for (Map.Entry<String, String> e : new ResponseDtoGenerator().generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        // exception handler
        for (Map.Entry<String, String> e : new ExceptionHandlerGenerator().generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        // SOAP
        for (Map.Entry<String, String> e : new SoapGenerator().generate(spec, basePackage).entrySet()) {
            count += write(e.getKey(), e.getValue());
        }

        System.out.println(count + " files written to " + outputDir.getAbsolutePath());
    }

    private int write(String relativePath, String content) throws IOException {
        Path target = outputDir.toPath().resolve(relativePath);
        Files.createDirectories(target.getParent());
        Files.writeString(target, content, StandardCharsets.UTF_8);
        System.out.println("  wrote: " + relativePath);
        return 1;
    }
}
