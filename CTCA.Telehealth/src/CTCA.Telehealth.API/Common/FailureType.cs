using System;
namespace CTCA.Telehealth.API.Common
{
    public enum FailureType
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
