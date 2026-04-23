# SpringAuto Compiler

> **OpenAPI 3.0 → Spring Boot REST + SOAP — zero AI, pure deterministic code generation.**

SpringAuto reads a single `openapi-spec.yaml` file and writes a complete, compilable Spring Boot 3 project. Every field constraint, HTTP method, path parameter, request body, and response schema in the spec becomes real, working Java code.

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [CLI Reference](#cli-reference)
- [Generation Modes](#generation-modes)
  - [Default — Stub Mode](#default--stub-mode)
  - [Scaffold Mode](#scaffold-mode)
  - [Scaffold Derivation Rules](#scaffold-derivation-rules)
- [What Gets Generated](#what-gets-generated)
  - [Project Layout](#project-layout)
  - [Model Classes](#model-classes)
  - [REST Controllers](#rest-controllers)
  - [Service Layer](#service-layer)
  - [SOAP Endpoints](#soap-endpoints)
  - [Standardised API Response](#standardised-api-response)
  - [Global Error Handling](#global-error-handling)
- [OpenAPI Feature Coverage](#openapi-feature-coverage)
- [Generated Project: Build & Run](#generated-project-build--run)
- [Example](#example)
- [Compiler Architecture](#compiler-architecture)
- [Extending the Compiler](#extending-the-compiler)

---

## Features

| Capability | Detail |
|---|---|
| REST API | One `@RestController` per OpenAPI tag — `GET`, `POST`, `PUT`, `DELETE`, `PATCH` |
| SOAP API | One Spring-WS `@Endpoint` per tag, JAXB payload wrappers, WSDL + XSD |
| Validation | `@NotNull`, `@NotBlank`, `@Size`, `@Pattern`, `@Email`, `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax` auto-applied from spec constraints |
| Standard response | `ApiResponse<T>` envelope on every endpoint: `statusCode`, `status`, `message`, `data`, `errors[]`, `timestamp` |
| Error handling | `GlobalExceptionHandler` covers 400, 404, 405, 415, 422, 429, 500, 503 and custom `ApiException` |
| Scaffold mode | `--scaffold` generates a fully runnable in-memory service — boot and test immediately, no extra code needed |
| Swagger UI | springdoc auto-wired at `/swagger-ui.html` |
| No AI | Fully deterministic — same spec always produces identical output |

---

## Requirements

**To build the compiler:**
- Java 17+
- Maven 3.8+

**To build the generated project:**
- Java 17+
- Maven 3.8+

---

## Quick Start

```bash
# 1. Clone / enter the compiler directory
cd SpringAuto

# 2. Build the fat JAR
mvn clean package -q

# 3a. Generate stubs (fill in business logic yourself)
java -jar target/spring-auto-compiler-1.0.0.jar \
  --spec   path/to/openapi.yaml \
  --output ./my-api \
  --package com.example.myapi

# 3b. Generate a fully runnable in-memory scaffold (boot immediately)
java -jar target/spring-auto-compiler-1.0.0.jar \
  --spec     path/to/openapi.yaml \
  --output   ./my-api \
  --package  com.example.myapi \
  --scaffold

# 4. Build and run the generated project
cd my-api
mvn clean package -q
java -jar target/*.jar
```

Or use the convenience wrapper:

```bash
chmod +x compile.sh
./compile.sh --spec openapi.yaml --output ./out --package com.example.api --scaffold
```

---

## CLI Reference

```
java -jar spring-auto-compiler-1.0.0.jar [options]

Options:
  --spec     <file>     Path to OpenAPI 3.0 YAML specification file      (required)
  --output   <dir>      Output directory for the generated project        (default: ./generated)
  --package  <package>  Base Java package name                            (default: com.generated.api)
  --scaffold            Generate runnable in-memory service implementations
  --help                Print this help message
```

---

## Generation Modes

### Default — Stub Mode

Without `--scaffold`, every `*ServiceImpl` contains `TODO` stubs. The project compiles but operations throw `UnsupportedOperationException` until you add your own business logic (database calls, external APIs, etc.).

```java
@Service
public class PetsServiceImpl implements PetsService {

    @Override
    public Pet createPet(CreatePetRequest request) {
        // TODO: implement Create a new pet
        throw new UnsupportedOperationException("Not implemented yet: createPet");
    }
}
```

Use this mode when you are wiring the generated code into an existing system and want full control over the service layer.

---

### Scaffold Mode

With `--scaffold`, every `*ServiceImpl` is a fully working in-memory implementation derived entirely from the OpenAPI spec. The generated project boots and handles all CRUD operations immediately — no additional code required.

```bash
java -jar spring-auto-compiler-1.0.0.jar --spec openapi.yaml --scaffold
cd generated && mvn clean package -q && java -jar target/*.jar
# API is live and functional at http://localhost:8080
```

Generated scaffold for the petstore example:

```java
@Service
public class PetsServiceImpl implements PetsService {

    private final Map<Long, Pet> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    @Override
    public List<Pet> listPets(String status, Integer page, Integer size) {
        List<Pet> result = new ArrayList<>(store.values());
        if (status != null && !status.isBlank()) {
            result = result.stream()
                    .filter(e -> e.getStatus() != null
                              && e.getStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        int pageNum  = (page != null && page > 0) ? page : 0;
        int pageSize = (size != null) ? size : 20;
        int from = pageNum * pageSize;
        int to   = Math.min(from + pageSize, result.size());
        return from >= result.size() ? List.of() : result.subList(from, to);
    }

    @Override
    public Pet createPet(CreatePetRequest request) {
        Pet entity = new Pet();
        entity.setId(idSeq.getAndIncrement());
        entity.setName(request.getName());
        entity.setSpecies(request.getSpecies());
        entity.setBreed(request.getBreed());
        if (request.getStatus() != null)
            entity.setStatus(Pet.StatusEnum.valueOf(request.getStatus().name()));
        entity.setDateOfBirth(request.getDateOfBirth());
        entity.setOwnerId(request.getOwnerId());
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Pet getPetById(Long petId) {
        Pet entity = store.get(petId);
        if (entity == null)
            throw ApiException.notFound("Pet not found with id: " + petId);
        return entity;
    }

    @Override
    public Pet updatePet(Long petId, UpdatePetRequest request) {
        Pet entity = store.get(petId);
        if (entity == null)
            throw ApiException.notFound("Pet not found with id: " + petId);
        if (request.getName()    != null) entity.setName(request.getName());
        if (request.getSpecies() != null) entity.setSpecies(request.getSpecies());
        if (request.getBreed()   != null) entity.setBreed(request.getBreed());
        if (request.getStatus()  != null)
            entity.setStatus(Pet.StatusEnum.valueOf(request.getStatus().name()));
        if (request.getOwnerId() != null) entity.setOwnerId(request.getOwnerId());
        return entity;
    }

    @Override
    public void deletePet(Long petId) {
        if (!store.containsKey(petId))
            throw ApiException.notFound("Pet not found with id: " + petId);
        store.remove(petId);
    }
}
```

> **Note:** The in-memory store is per-process and non-persistent. It is intended for rapid prototyping, integration testing, and API contract validation. Replace the service implementations with your real persistence layer when moving to production.

---

### Scaffold Derivation Rules

Everything in the scaffold impl is derived purely from the OpenAPI spec — no heuristics, no guessing:

| OpenAPI spec source | Derived scaffold behaviour |
|---|---|
| POST success response `$ref` (or GET-by-ID response `$ref`) | Entity type → `Map<IdType, Entity> store` |
| Path parameter type (`integer`+`int64` → `Long`, `integer` → `Integer`, `string` → UUID) | Map key type + `AtomicLong idSeq` (or UUID) |
| HTTP method + whether path params are present | Operation pattern: LIST / CREATE / GET_BY_ID / UPDATE / DELETE |
| Query param named `page`, `offset`, `pageNumber` | Pagination page index |
| Query param named `size`, `limit`, `pageSize`, `perPage` | Pagination page size |
| Other `string` query params | Case-insensitive `.equalsIgnoreCase()` stream filter |
| Request body `$ref` → schema properties overlapping with entity schema | `entity.setX(request.getX())` field mappings in CREATE |
| Same properties on UPDATE request schema | Null-safe `if (request.getX() != null) entity.setX(...)` patching |
| Properties with `enum` values on both request and entity schemas | `Entity.XEnum.valueOf(request.getX().name())` enum conversion |
| Entity `id` field (or first `Long`/`Integer` field) | `entity.setId(idSeq.getAndIncrement())` in CREATE; store key |
| Spec `required: [...]` → `@NotNull` / `@NotBlank` on model fields | 400 validation error returned automatically by `GlobalExceptionHandler` |

---

## What Gets Generated

### Project Layout

```
<output-dir>/
├── pom.xml                                   ← Spring Boot 3.2, Spring-WS, springdoc, JAXB
└── src/main/
    ├── java/<base-package>/
    │   ├── <Title>Application.java           ← @SpringBootApplication entry point
    │   ├── config/
    │   │   ├── OpenApiConfig.java            ← Swagger / springdoc bean
    │   │   └── WebServiceConfig.java         ← Spring-WS servlet + WSDL bean
    │   ├── controller/
    │   │   └── <Tag>Controller.java          ← one per OpenAPI tag
    │   ├── service/
    │   │   ├── <Tag>Service.java             ← interface, one per tag
    │   │   └── impl/
    │   │       └── <Tag>ServiceImpl.java     ← stub (default) or scaffold (--scaffold)
    │   ├── model/
    │   │   └── <Schema>.java                 ← one per components/schemas entry
    │   ├── dto/
    │   │   ├── ApiResponse.java              ← standard response envelope
    │   │   ├── ErrorDetail.java              ← per-error detail object
    │   │   └── ApiException.java             ← runtime exception for service errors
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java   ← @RestControllerAdvice
    │   └── soap/
    │       ├── <Tag>SoapEndpoint.java        ← @Endpoint, one per tag
    │       ├── <Op>SoapRequest.java          ← @XmlRootElement JAXB wrapper
    │       └── <Op>SoapResponse.java         ← @XmlRootElement JAXB wrapper
    └── resources/
        ├── application.properties
        └── wsdl/
            ├── schema.xsd                    ← XSD for all operations
            └── <tag>-service.wsdl            ← WSDL, one per tag
```

---

### Model Classes

Every entry in `components/schemas` becomes a Java class. OpenAPI validation keywords map directly to Jakarta Bean Validation annotations:

```yaml
# openapi.yaml
Owner:
  type: object
  required: [id, firstName, lastName, email]
  properties:
    id:
      type: integer
      format: int64
    firstName:
      type: string
      minLength: 1
      maxLength: 50
    email:
      type: string
      format: email
      pattern: "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    address:
      $ref: '#/components/schemas/Address'
```

Generated:

```java
public class Owner implements Serializable {

    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")
    @Size(max = 255)
    private String email;

    @Valid          // nested object validation cascades automatically
    private Address address;

    // getters + setters ...
}
```

**Type mappings:**

| OpenAPI type / format | Java type |
|---|---|
| `integer` | `Integer` |
| `integer` + `int64` | `Long` |
| `number` | `Double` |
| `number` + `float` | `Float` |
| `boolean` | `Boolean` |
| `string` | `String` |
| `string` + `date` | `java.time.LocalDate` |
| `string` + `date-time` | `java.time.OffsetDateTime` |
| `string` + `uuid` | `java.util.UUID` |
| `string` + `binary` / `byte` | `byte[]` |
| `array` | `List<T>` |
| `$ref` | Referenced class name |
| `enum` on string property | Inner `enum` class |

---

### REST Controllers

One `@RestController` per tag. Every path + method in the spec maps to an annotated method:

```java
@RestController
@RequestMapping
@Validated
@Tag(name = "pets")
public class PetsController {

    private final PetsService petsService;

    public PetsController(PetsService petsService) {
        this.petsService = petsService;
    }

    @Operation(summary = "List all pets")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful response"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<List<Pet>>> listPets(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page",   required = false) Integer page,
            @RequestParam(name = "size",   required = false) Integer size
    ) {
        List<Pet> result = petsService.listPets(status, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(result, 200, "Successful response"));
    }

    @PostMapping("/pets")
    public ResponseEntity<ApiResponse<Pet>> createPet(
            @Valid @RequestBody CreatePetRequest request
    ) {
        Pet result = petsService.createPet(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, 201, "Pet created"));
    }
}
```

**HTTP method mapping:**

| OpenAPI method | Spring annotation |
|---|---|
| `get` | `@GetMapping` |
| `post` | `@PostMapping` |
| `put` | `@PutMapping` |
| `delete` | `@DeleteMapping` |
| `patch` | `@PatchMapping` |
| `head` / `options` | `@RequestMapping(method = ...)` |

**Parameter mapping:**

| OpenAPI `in` | Spring annotation |
|---|---|
| `path` | `@PathVariable` |
| `query` | `@RequestParam` |
| `header` | `@RequestHeader` |
| `requestBody` | `@RequestBody` + `@Valid` |

---

### Service Layer

A Java interface and a `@Service` implementation are generated per tag.

**Default mode** — stubs that throw `UnsupportedOperationException`. Replace with your business logic:

```java
public interface PetsService {
    List<Pet> listPets(String status, Integer page, Integer size);
    Pet createPet(CreatePetRequest request);
    Pet getPetById(Long petId);
    Pet updatePet(Long petId, UpdatePetRequest request);
    void deletePet(Long petId);
}
```

**Scaffold mode** (`--scaffold`) — fully implemented in-memory service, ready to run. See [Scaffold Mode](#scaffold-mode) for the full generated output.

The SOAP endpoint reuses the same service interface, so both REST and SOAP share the same business logic automatically.

---

### SOAP Endpoints

The same service interface is reused by the SOAP layer. Each operation gets a `@PayloadRoot` handler, JAXB-annotated request/response wrappers, and a WSDL file.

```java
@Endpoint
public class PetsSoapEndpoint {

    private static final String NAMESPACE_URI = "http://com.example.petstore/ws";
    private final PetsService petsService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreatePetRequest")
    @ResponsePayload
    public CreatePetSoapResponse createPet(@RequestPayload CreatePetSoapRequest request) {
        CreatePetSoapResponse response = new CreatePetSoapResponse();
        Pet result = petsService.createPet(request.getBody());
        response.setResult(result);
        response.setSuccess(true);
        return response;
    }
}
```

SOAP is mounted at `/ws/*`. The WSDL is auto-published by Spring-WS:
```
http://localhost:8080/ws/serviceWsdl.wsdl
```

---

### Standardised API Response

Every REST endpoint returns the same envelope:

```json
{
  "statusCode": 201,
  "status": "success",
  "message": "Pet created",
  "data": {
    "id": 1,
    "name": "Buddy",
    "status": "AVAILABLE",
    "dateOfBirth": "2021-06-15"
  },
  "errors": [],
  "timestamp": "2026-04-23T10:15:30Z"
}
```

Error response:

```json
{
  "statusCode": 400,
  "status": "error",
  "message": "Validation failed",
  "data": null,
  "errors": [
    {
      "field": "name",
      "code": "NotBlank",
      "message": "must not be blank",
      "rejectedValue": ""
    },
    {
      "field": "status",
      "code": "NotNull",
      "message": "must not be null",
      "rejectedValue": null
    }
  ],
  "timestamp": "2026-04-23T10:15:30Z"
}
```

Use `ApiException` from the service layer to produce structured errors:

```java
throw ApiException.notFound("Pet with id " + petId + " does not exist");
throw ApiException.tooManyRequests("Rate limit exceeded — try again in 60 seconds");
throw ApiException.conflict("Pet name already registered for this owner");
```

---

### Global Error Handling

`GlobalExceptionHandler` maps every common Spring / Jakarta exception to the correct HTTP status code automatically:

| Exception | HTTP Status |
|---|---|
| `MethodArgumentNotValidException` | 400 — field + global validation errors with rejected values |
| `ConstraintViolationException` | 400 — path/query param constraint violations |
| `BindException` | 400 — form binding errors |
| `HttpMessageNotReadableException` | 400 — malformed JSON body |
| `MissingServletRequestParameterException` | 400 — missing required `@RequestParam` |
| `MethodArgumentTypeMismatchException` | 400 — wrong type for path/query param |
| `NoHandlerFoundException` | 404 — no matching endpoint |
| `HttpRequestMethodNotSupportedException` | 405 — wrong HTTP verb |
| `HttpMediaTypeNotSupportedException` | 415 — unsupported Content-Type |
| `ResponseStatusException` | mirrors the embedded status code |
| `ApiException` | maps to the code set when thrown |
| `Exception` (catch-all) | 500 |

---

## OpenAPI Feature Coverage

| Feature | Supported |
|---|---|
| `components/schemas` object types | Yes |
| `components/schemas` enum types | Yes — top-level enum class |
| Enum values on object properties | Yes — inner enum class |
| `$ref` to schemas | Yes — resolved by name |
| `allOf` (schema merging) | Yes — properties merged |
| `required` fields | Yes — `@NotNull` / `@NotBlank` |
| `minLength` / `maxLength` | Yes — `@Size` |
| `pattern` | Yes — `@Pattern` |
| `minimum` / `maximum` (integer) | Yes — `@Min` / `@Max` |
| `minimum` / `maximum` (number) | Yes — `@DecimalMin` / `@DecimalMax` |
| `format: email` | Yes — `@Email` |
| `format: date` | Yes — `LocalDate` |
| `format: date-time` | Yes — `OffsetDateTime` |
| `format: uuid` | Yes — `UUID` |
| `type: array` + `items` | Yes — `List<T>` |
| `minItems` / `maxItems` | Yes — `@Size` on list field |
| Path parameters (`in: path`) | Yes — `@PathVariable` |
| Query parameters (`in: query`) | Yes — `@RequestParam` |
| Header parameters (`in: header`) | Yes — `@RequestHeader` |
| `requestBody` | Yes — `@RequestBody` + `@Valid` |
| `responses` with 2xx success code | Yes — return type + `ResponseEntity` status |
| Multiple tags / controllers | Yes — one controller + service + SOAP endpoint per tag |
| `servers[0].url` as base path | Yes — used as request path prefix context |
| `operationId` | Yes — used as Java method name |
| `summary` / `description` | Yes — `@Operation` + Javadoc |

---

## Generated Project: Build & Run

```bash
cd <output-dir>

# Compile and package
mvn clean package

# Run
java -jar target/*.jar

# Swagger UI (REST)
open http://localhost:8080/swagger-ui.html

# Actuator health
curl http://localhost:8080/actuator/health

# SOAP WSDL
curl http://localhost:8080/ws/serviceWsdl.wsdl
```

---

## Example

The `example/` directory contains a complete Pet Store spec with two tags (`pets`, `owners`), nested schema refs, enum fields, regex patterns, and email validation.

```bash
# Generate with scaffold — boots and works immediately
java -jar target/spring-auto-compiler-1.0.0.jar \
  --spec     example/petstore.yaml \
  --output   example/generated-petstore \
  --package  com.example.petstore \
  --scaffold

cd example/generated-petstore
mvn clean package -q
java -jar target/*.jar
```

```bash
# Add a pet
curl -s -X POST http://localhost:8080/pets \
  -H 'Content-Type: application/json' \
  -d '{"name":"Buddy","species":"Dog","status":"AVAILABLE"}'

# List pets
curl -s http://localhost:8080/pets

# Filter by status
curl -s 'http://localhost:8080/pets?status=AVAILABLE'

# Get by ID
curl -s http://localhost:8080/pets/1

# Update
curl -s -X PUT http://localhost:8080/pets/1 \
  -H 'Content-Type: application/json' \
  -d '{"status":"SOLD"}'

# Delete
curl -s -X DELETE http://localhost:8080/pets/1
```

**Live terminal output:**

```
$ curl -s http://localhost:8080/pets
{"statusCode":200,"status":"success","message":"List all pets","data":[],"errors":[],"timestamp":"2026-04-23T23:40:08.942621+05:30"}

$ curl -s -X POST http://localhost:8080/pets \
  -H 'Content-Type: application/json' \
  -d '{"name":"Buddy","species":"Dog","breed":"Golden Retriever","status":"AVAILABLE","dateOfBirth":"2021-06-15"}'
{"statusCode":201,"status":"success","message":"Create a new pet","data":{"id":1,"name":"Buddy","species":"Dog","breed":"Golden Retriever","status":"AVAILABLE","dateOfBirth":"2021-06-15","ownerId":null,"tags":null},"errors":[],"timestamp":"2026-04-23T23:40:12.847255+05:30"}

$ curl -s http://localhost:8080/pets
{"statusCode":200,"status":"success","message":"List all pets","data":[{"id":1,"name":"Buddy","species":"Dog","breed":"Golden Retriever","status":"AVAILABLE","dateOfBirth":"2021-06-15","ownerId":null,"tags":null}],"errors":[],"timestamp":"2026-04-23T23:40:15.522969+05:30"}
```

---

Generated files from `example/petstore.yaml`:

```
44 files written including:
  6 model classes      (Pet, Owner, Address, CreatePetRequest, UpdatePetRequest, CreateOwnerRequest)
  2 REST controllers   (PetsController, OwnersController)
  4 service files      (PetsService, PetsServiceImpl, OwnersService, OwnersServiceImpl)
 16 SOAP files         (2 endpoints + 14 request/response wrappers)
  3 DTO files          (ApiResponse, ErrorDetail, ApiException)
  1 exception handler  (GlobalExceptionHandler)
  2 config classes     (OpenApiConfig, WebServiceConfig)
  2 WSDL files         (pets-service.wsdl, owners-service.wsdl)
  1 XSD schema         (schema.xsd)
  1 pom.xml
  1 application.properties
```

---

## Compiler Architecture

```
src/main/java/com/springauto/
├── SpringAutoCompiler.java            ← CLI entry point — parses --spec/--output/--package/--scaffold
├── parser/
│   └── OpenApiParser.java             ← SnakeYAML-based OpenAPI 3.0 parser
│                                         resolves $ref, merges allOf, parses all constraints
├── model/                             ← Internal representation (no Spring dependency)
│   ├── OpenApiSpec.java               ← Top-level: title, version, schemas, operations-by-tag
│   ├── OperationInfo.java             ← HTTP method, path, parameters, request body, responses
│   ├── SchemaInfo.java                ← Type, format, properties, validation constraints
│   ├── PropertyInfo.java              ← Single field: type, ref, required, constraints
│   ├── ParameterInfo.java             ← in, name, required, schema
│   └── ResponseInfo.java              ← Status code, description, response schema
└── generator/
    ├── CodeUtils.java                 ← Naming conventions, Java type mapping, annotation builder
    ├── ProjectGenerator.java          ← Orchestrates all generators, writes files to disk
    ├── PomXmlGenerator.java           ← Maven POM with all Spring Boot 3 dependencies
    ├── ApplicationClassGenerator.java ← Main class, application.properties, OpenApiConfig, XSD
    ├── ModelGenerator.java            ← Serializable POJO + validation annotations + inner enums
    ├── ControllerGenerator.java       ← @RestController with Spring MVC + Swagger annotations
    ├── ServiceGenerator.java          ← Interface + stub impl (default) or scaffold impl (--scaffold)
    ├── SoapGenerator.java             ← @Endpoint + JAXB wrappers + WebServiceConfig + WSDL
    ├── ExceptionHandlerGenerator.java ← @RestControllerAdvice covering all HTTP error codes
    └── ResponseDtoGenerator.java      ← ApiResponse<T>, ErrorDetail, ApiException
```

**Data flow:**

```
openapi.yaml
    │
    ▼
OpenApiParser           →  OpenApiSpec (internal model)
    │
    ▼
ProjectGenerator  (scaffold=true/false)
    ├── PomXmlGenerator
    ├── ApplicationClassGenerator
    ├── ModelGenerator                  one class per schema
    ├── ControllerGenerator             one class per tag
    ├── ServiceGenerator                interface + stub impl  ←  or scaffold impl when --scaffold
    ├── SoapGenerator                   endpoint + wrappers + WSDL per tag
    ├── ExceptionHandlerGenerator
    └── ResponseDtoGenerator
    │
    ▼
Generated Spring Boot project (written to --output directory)
```

---

## Extending the Compiler

**Add a new generator:**
1. Create a class in `com.springauto.generator`
2. Accept `OpenApiSpec` and return `String` (file content) or `Map<String, String>` (filename → content)
3. Call it from `ProjectGenerator.generate()` and write the output with `write(path, content)`

**Add a new OpenAPI constraint:**
1. Parse it in `OpenApiParser.parseSchemaMap()` / `parseProperty()`
2. Store it in `SchemaInfo` / `PropertyInfo`
3. Emit the annotation in `CodeUtils.validationAnnotations()`

**Add a new scaffold operation pattern:**
1. Add a new `case` to `classifyOperation()` in `ServiceGenerator`
2. Add the corresponding body generator in `scaffoldBody()`

**Support a new HTTP parameter location:**
1. Add detection in `ParameterInfo.java` (`isXxx()` method)
2. Emit the Spring annotation in `ControllerGenerator.buildControllerParams()`
3. Add to service call args in `ControllerGenerator.buildServiceCallArgs()`
