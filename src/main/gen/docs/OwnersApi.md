# OwnersApi

All URIs are relative to */api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createOwner**](OwnersApi.md#createOwner) | **POST** /owners | Register a new owner |
| [**getOwnerById**](OwnersApi.md#getOwnerById) | **GET** /owners/{ownerId} | Get an owner by ID |
| [**listOwners**](OwnersApi.md#listOwners) | **GET** /owners | List all owners |


<a id="createOwner"></a>
# **createOwner**
> Owner createOwner(createOwnerRequest)

Register a new owner

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OwnersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    OwnersApi apiInstance = new OwnersApi(defaultClient);
    CreateOwnerRequest createOwnerRequest = new CreateOwnerRequest(); // CreateOwnerRequest | 
    try {
      Owner result = apiInstance.createOwner(createOwnerRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OwnersApi#createOwner");
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
| **createOwnerRequest** | [**CreateOwnerRequest**](CreateOwnerRequest.md)|  | |

### Return type

[**Owner**](Owner.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Owner created |  -  |
| **400** | Validation failed |  -  |

<a id="getOwnerById"></a>
# **getOwnerById**
> Owner getOwnerById(ownerId)

Get an owner by ID

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OwnersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    OwnersApi apiInstance = new OwnersApi(defaultClient);
    Long ownerId = 56L; // Long | 
    try {
      Owner result = apiInstance.getOwnerById(ownerId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OwnersApi#getOwnerById");
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
| **ownerId** | **Long**|  | |

### Return type

[**Owner**](Owner.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Owner found |  -  |
| **404** | Owner not found |  -  |

<a id="listOwners"></a>
# **listOwners**
> List&lt;Owner&gt; listOwners(lastName)

List all owners

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OwnersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("/api/v1");

    OwnersApi apiInstance = new OwnersApi(defaultClient);
    String lastName = "lastName_example"; // String | 
    try {
      List<Owner> result = apiInstance.listOwners(lastName);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OwnersApi#listOwners");
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
| **lastName** | **String**|  | [optional] |

### Return type

[**List&lt;Owner&gt;**](Owner.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of owners |  -  |

