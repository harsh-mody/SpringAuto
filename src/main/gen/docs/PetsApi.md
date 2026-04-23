# PetsApi

All URIs are relative to */api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createPet**](PetsApi.md#createPet) | **POST** /pets | Create a new pet |
| [**deletePet**](PetsApi.md#deletePet) | **DELETE** /pets/{petId} | Delete a pet |
| [**getPetById**](PetsApi.md#getPetById) | **GET** /pets/{petId} | Get a pet by ID |
| [**listPets**](PetsApi.md#listPets) | **GET** /pets | List all pets |
| [**updatePet**](PetsApi.md#updatePet) | **PUT** /pets/{petId} | Update a pet |


<a id="createPet"></a>
# **createPet**
> Pet createPet(createPetRequest)

Create a new pet

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PetsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    PetsApi apiInstance = new PetsApi(defaultClient);
    CreatePetRequest createPetRequest = new CreatePetRequest(); // CreatePetRequest | Pet to create
    try {
      Pet result = apiInstance.createPet(createPetRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PetsApi#createPet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **createPetRequest** | [**CreatePetRequest**](CreatePetRequest.md)| Pet to create | |

### Return type

[**Pet**](Pet.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Pet created |  -  |
| **400** | Validation error |  -  |
| **409** | Pet already exists |  -  |

<a id="deletePet"></a>
# **deletePet**
> deletePet(petId)

Delete a pet

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PetsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    PetsApi apiInstance = new PetsApi(defaultClient);
    Long petId = 56L; // Long | 
    try {
      apiInstance.deletePet(petId);
    } catch (ApiException e) {
      System.err.println("Exception when calling PetsApi#deletePet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **petId** | **Long**|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Pet deleted |  -  |
| **404** | Pet not found |  -  |

<a id="getPetById"></a>
# **getPetById**
> Pet getPetById(petId)

Get a pet by ID

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PetsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    PetsApi apiInstance = new PetsApi(defaultClient);
    Long petId = 56L; // Long | 
    try {
      Pet result = apiInstance.getPetById(petId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PetsApi#getPetById");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **petId** | **Long**|  | |

### Return type

[**Pet**](Pet.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pet found |  -  |
| **404** | Pet not found |  -  |

<a id="listPets"></a>
# **listPets**
> List&lt;Pet&gt; listPets(status, page, size)

List all pets

Returns a paginated list of pets, optionally filtered by status.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PetsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    PetsApi apiInstance = new PetsApi(defaultClient);
    String status = "available"; // String | 
    Integer page = 0; // Integer | 
    Integer size = 20; // Integer | 
    try {
      List<Pet> result = apiInstance.listPets(status, page, size);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PetsApi#listPets");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **status** | **String**|  | [optional] [enum: available, pending, sold] |
| **page** | **Integer**|  | [optional] [default to 0] |
| **size** | **Integer**|  | [optional] [default to 20] |

### Return type

[**List&lt;Pet&gt;**](Pet.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |
| **400** | Invalid filter parameters |  -  |
| **429** | Too many requests |  -  |

<a id="updatePet"></a>
# **updatePet**
> Pet updatePet(petId, updatePetRequest)

Update a pet

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PetsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    PetsApi apiInstance = new PetsApi(defaultClient);
    Long petId = 56L; // Long | 
    UpdatePetRequest updatePetRequest = new UpdatePetRequest(); // UpdatePetRequest | 
    try {
      Pet result = apiInstance.updatePet(petId, updatePetRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PetsApi#updatePet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **petId** | **Long**|  | |
| **updatePetRequest** | [**UpdatePetRequest**](UpdatePetRequest.md)|  | |

### Return type

[**Pet**](Pet.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Pet updated |  -  |
| **400** | Validation error |  -  |
| **404** | Pet not found |  -  |

