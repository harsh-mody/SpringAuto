package com.springauto;

import com.springauto.generator.ProjectGenerator;
import com.springauto.model.OpenApiSpec;
import com.springauto.parser.OpenApiParser;

import java.io.File;

public class SpringAutoCompiler {

    public static void main(String[] args) {
        String spec = null;
        String output = "./generated";
        String pkg = "com.generated.api";
        boolean scaffold = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--spec"     -> spec     = args[++i];
                case "--output"   -> output   = args[++i];
                case "--package"  -> pkg      = args[++i];
                case "--scaffold" -> scaffold = true;
                case "--help"     -> { printHelp(); return; }
                default           -> System.err.println("Unknown option: " + args[i]);
            }
        }

        if (spec == null) {
            System.err.println("Error: --spec is required");
            printHelp();
            System.exit(1);
        }

        File specFile = new File(spec);
        if (!specFile.exists()) {
            System.err.println("Error: spec file not found: " + spec);
            System.exit(1);
        }

        try {
            OpenApiSpec apiSpec = new OpenApiParser().parse(specFile);
            new ProjectGenerator(apiSpec, new File(output), pkg, scaffold).generate();
            System.out.println("Done. Generated project at: " + new File(output).getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("""
                java -jar spring-auto-compiler-1.0.0.jar [options]

                Options:
                  --spec     <file>     Path to OpenAPI 3.0 YAML specification file      (required)
                  --output   <dir>      Output directory for the generated project        (default: ./generated)
                  --package  <package>  Base Java package name                            (default: com.generated.api)
                  --scaffold            Generate runnable in-memory service implementations
                  --help                Print this help message
                """);
    }
}
