using System;
namespace CTCA.Telehealth.Application.Common.Exceptions
{
    public enum ApplicationFailureType
    {
        /// <summary>
        /// Invalid. The value has not been set in code.
        /// </summary>
        Unspecified,

        /// <summary>
        /// An entity was referenced in the operation that cannot be found.
        /// </summary>
        EntityNotFound,

        /// <summary>
        /// An object validation has failed.
        /// </summary>
        ValidationFailure,

        /// <summary>
        /// A unexpected failure has been returned from a downstream service. 
        /// </summary>
        ExternalServiceFailure,

        /// <summary>
        /// An unhandled exception has occurred.
        /// </summary>
        UnexpectedFailure,

        /// <summary>
        /// A situation has been encountered that is not permitted.
        /// </summary>
        BusinessRuleViolation,

        /// <summary>
        /// Either code or a configuration is incorrect (developer error).
        /// </summary>
        BadConfiguration,

        /// <summary>
        /// The operation has not been implemented.
        /// </summary>
        NotImplemented
    }
}
