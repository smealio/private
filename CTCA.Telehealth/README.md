# Telehealth API

## Introduction

The Telehealth API creates, updates, and cancels Telehealth appointments between patients and providers. It receives data from Mirth which is used to create or modify an appointment or readiness check. Once an appointment or readiness check is created the API will add a new meeting to the Provider's or Concierge Team's calendar in Microsoft Teams. An email will be sent to the patient with a link to connect to the Provider or Concierge Team at the appropriate time.

## Application Details

### Frameworks and Libraries

- ASP.NET Core 3.1
- Microsoft.Extensions.Configuration.AzureKeyVault 3.1.14
- Serilog 2.10.0
- Serilog.Sinks.ApplicationInsights 3.1.0
- Swashbuckle.AspNetCore 6.1.1
- MediatR 9.0.0
- CorrelationId 3.0.0
- FluentValidation.AspNetCore 9.5.3

### Architecture and Patterns Used

The Mediator pattern is used with the MediatR library.

Key vaults are used for each app service in each environment.  This allows secrets to be stored in the key vaults and not in the code. Each app service has been given a system assigned managed identity to access resources in the resource group. Each key vault has an access policy given to its respective app service to only read and list keys in the key vault. Upon startup of the app service in Program.cs the CreateWebHostBuilder() method accesses the key vault and stores the secrets as a configuration file which is used throughout the application.

### External API Dependencies

- Azure Application Insights
- Azure Key Vault
- Azure CosmosDB
- Microsoft Graph API
- Microsoft Exchange Web Services (EWS)

### Subscription Key

The Ocp-Apim-Subscription-Key header is a an extra layer of security that allows clients to use a key to access a particular resource. This allows administrators to revoke API access to a particular client if needed without completely removing access to the API.

### Logging

Serilog with ApplicationInsights sink is used to log all requests and errors to Azure Application Insights.

## Endpoints

### **Meeting Controller**

**Get**

- Endpoint Name:      Get
- URL:                api/telehealthMeetings/{id}
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthMeetings/*id*>
- Method:             GET
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     id
- Body:               none
- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns TelehealthMeetingsViewModel.

- 404:    If the telehealth appointment is not found.

- 500:    Unhandled exception is returned.

### **Telehealth Appointment Controller**

**Post**

- Endpoint Name:      Post
- URL:                api/telehealthappointment
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthappointment>
- Method:             POST
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body:

```json
{
    
    "appointmentStartTime":"2021-06-01T09:00:00-04:00",
    "AppointmentTimeZone":"Eastern Standard Time",
    "appointmentDuration":30,
    "appointmentOrganizerEmail":"Test.Provider@ctca-hope.com",
    "appointmentProviderId": "90909",
    "appointmentProviderName": "Test Provider",
    "appointmentPatientName":"Test Patient",
    "appointmentPatientEmail":"your.name@ctca-hope.com",
    "appointmentServiceId":"Test0601210900",
    "pid":"1010101",
    "AppointmentServiceSource":"RMS"

}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns TelehealthAppointmentResponse.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

**Put**

- Endpoint Name:      Put
- URL:                api/telehealthappointment
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthappointment>
- Method:             PUT
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body: Appointment start time only needs to be updated.

```json
{
    
    "appointmentStartTime":"2021-06-02T09:00:00-04:00",
    "AppointmentTimeZone":"Eastern Standard Time",
    "appointmentDuration":30,
    "appointmentOrganizerEmail":"Test.Provider@ctca-hope.com",
    "appointmentProviderId": "90909",
    "appointmentProviderName": "Test Provider",
    "appointmentPatientName":"Test Patient",
    "appointmentPatientEmail":"your.name@ctca-hope.com",
    "appointmentServiceId":"Test0601210900",
    "pid":"1010101",
    "AppointmentServiceSource":"RMS"

}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns TelehealthAppointmentResponse.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

**Put (Cancel)**

- Endpoint Name:      Put
- URL:                api/telehealthAppointment/cancel
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthAppointment/cancel>
- Method:             POST
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body: Appointment start time only needs to be updated.

```json
{
    "appointmentServiceSource": "RMS",
    "appointmentServiceId": "Test0601210900",
    "pid": "1010101"
}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns empty response if successful.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

**Post (Search)**

- Endpoint Name:      Post
- URL:                api/telehealthAppointment/search
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthAppointment/search>
- Method:             POST
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body: Appointment start time only needs to be updated.

```json
{
    "pid": "1010101",
    "serviceSource": "RMS",
    "serviceSourceId": "Test0601210900",
    "beginDateTime": "2021-06-02T09:00:00-04:00",
    "endDateTime": "2021-06-02T09:30:00-04:00"
}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns empty response if successful.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

### **Readiness Check Controller**

**Create Readiness Check**

- Endpoint Name:      ReadinessCheck
- URL:                api/telehealthreadinesscheck
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthreadinesscheck>
- Method:             POST
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body:

```json
{
    
    "appointmentStartTime":"2021-06-01T09:00:00-04:00",
    "AppointmentTimeZone":"Eastern Standard Time",
    "appointmentDuration":30,
    "appointmentOrganizerEmail":"Test.Provider@ctca-hope.com",
    "appointmentProviderId": "90909",
    "appointmentProviderName": "Test Provider",
    "appointmentPatientName":"Test Patient",
    "appointmentPatientEmail":"your.name@ctca-hope.com",
    "appointmentServiceId":"Test0601210900",
    "pid":"1010101",
    "AppointmentServiceSource":"RMS"

}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns TelehealthAppointmentResponse.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

**Update Readiness Check**

- Endpoint Name:      UpdateReadinessCheck
- URL:                api/telehealthreadinesscheck
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthreadinesscheck>
- Method:             PUT
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body:

```json
{
    
    "appointmentStartTime":"2021-06-01T09:30:00-04:00",
    "AppointmentTimeZone":"Eastern Standard Time",
    "appointmentDuration":30,
    "appointmentOrganizerEmail":"Test.Provider@ctca-hope.com",
    "appointmentProviderId": "90909",
    "appointmentProviderName": "Test Provider",
    "appointmentPatientName":"Test Patient",
    "appointmentPatientEmail":"your.name@ctca-hope.com",
    "appointmentServiceId":"Test0601210900",
    "pid":"1010101",
    "AppointmentServiceSource":"RMS"

}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns TelehealthAppointmentResponse.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

**Cancel Readiness Check**

- Endpoint Name:      CancelReadinessCheck
- URL:                api/telehealthreadinesscheck/cancel
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthreadinesscheck/cancel>
- Method:             POST
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body: Appointment start time only needs to be updated.

```json
{
    "appointmentServiceSource": "RMS",
    "appointmentServiceId": "Test0601210900",
    "pid": "1010101"
}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns empty response if successful.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.

**Search Readiness Check**

- Endpoint Name:      SearchReadinessCheck
- URL:                api/telehealthreadinesscheck/search
- Example URL:        <https://apim.ctca-hope.com/test/telehealth/api/telehealthreadinesscheck/search>
- Method:             POST
- Required Headers:
  - Ocp-Apim-Subscription-Key
- URL Parameters:     none
- Body: Appointment start time only needs to be updated.

```json
{
    "pid": "1010101",
    "serviceSource": "RMS",
    "serviceSourceId": "Test0601210900",
    "beginDateTime": "2021-06-02T09:00:00-04:00",
    "endDateTime": "2021-06-02T09:30:00-04:00"
}
```

- Parameters:         none

Responses

Code: Description/Response Body

- 200:    Returns empty response if successful.

- 400:    If the request body is not valid.

- 500:    Unhandled exception is returned.