

# Pet

Represents a pet in the store

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **Long** | Unique pet identifier |  |
|**name** | **String** | Pet&#39;s name |  |
|**species** | **String** | Animal species |  [optional] |
|**breed** | **String** |  |  [optional] |
|**status** | [**StatusEnum**](#StatusEnum) | Pet availability status |  |
|**dateOfBirth** | **LocalDate** | Date of birth (YYYY-MM-DD) |  [optional] |
|**ownerId** | **Long** | ID of the owner (if adopted) |  [optional] |
|**tags** | **List&lt;String&gt;** |  |  [optional] |



## Enum: StatusEnum

| Name | Value |
|---- | -----|
| AVAILABLE | &quot;available&quot; |
| PENDING | &quot;pending&quot; |
| SOLD | &quot;sold&quot; |



