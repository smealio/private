using System;
namespace CTCA.Telehealth.API.Common.Exceptions
{
    public enum ApplicationFailureType
    {
        Unspecified,
        EntityNotFound,
        ValidationFailure,
        ExternalServiceFailure,
        UnexpectedFailure,
        BusinessRuleViolation,
        BadConfiguration,
        NotImplemented
    }
}
