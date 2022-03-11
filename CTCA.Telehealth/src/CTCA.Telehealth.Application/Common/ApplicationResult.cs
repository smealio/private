using System;
namespace CTCA.Telehealth.Application.Common
{
    public enum ApplicationResult
    {
        /// <summary>
        /// The application forgot to set the result type.
        /// </summary>
        Unspecified,

        /// <summary>
        /// The resource was found.
        /// </summary>
        Found,

        /// <summary>
        /// The resource could not be found.
        /// </summary>
        NotFound,

        /// <summary>
        /// The resource was created.
        /// </summary>
        Created,

        /// <summary>
        /// The operation was completed.
        /// </summary>
        Ok,

        /// <summary>
        /// The application did nothing.
        /// </summary>
        NoAction,

        /// <summary>
        /// The operation was deferred to the bus.
        /// </summary>
        Queued,

        /// <summary>
        /// The request could not be understood by the server due to malformed syntax.
        /// </summary>
        BadRequest,

        /// <summary>
        /// The server encountered an unexpected condition which prevented it from fulfilling the request.
        /// </summary>
        InternalServerError,

        /// <summary>
        /// The server does not support the functionality required to fulfill the request
        /// </summary>
        NotImplemented
    }
}
