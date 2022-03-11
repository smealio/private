using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;

namespace CTCA.Telehealth.API.Common.Exceptions
{
    /// <inheritdoc />
    /// <summary>
    /// Indicates that an exception occured while performing an operation within the application layer.
    /// </summary>
    /// <seealso cref="T:System.Exception" />
    [ExcludeFromCodeCoverage]
    public class ApplicationLayerException : Exception
    {

        /// <summary>
        /// Gets or sets the type of the failure.
        /// </summary>
        /// <value>
        /// The type of the failure.
        /// </value>
        public ApplicationFailureType FailureType { get; set; }

        /// <summary>
        /// Gets or sets the error messages.
        /// </summary>
        /// <value>
        /// The error messages.
        /// </value>
        public IEnumerable<string> ErrorMessages { get; set; } = new List<string>();

        /// <inheritdoc />
        /// <summary>
        /// Initializes a new instance of the <see cref="T:PetSmart.Prism.Invoice.Application.Common.Exceptions.InvoiceApplicationException" /> class.
        /// </summary>
        /// <param name="failureType">Type of the failure.</param>
        /// <param name="message">The message.</param>
        public ApplicationLayerException(ApplicationFailureType failureType, string message)
            : this(failureType, message, null)
        {
        }

        /// <inheritdoc />
        /// <summary>
        /// Initializes a new instance of the <see cref="T:PetSmart.Prism.Invoice.Application.Common.Exceptions.InvoiceApplicationException" /> class.
        /// </summary>
        /// <param name="failureType">Type of the failure.</param>
        /// <param name="message">The message.</param>
        /// <param name="innerException">The inner exception.</param>
        public ApplicationLayerException(ApplicationFailureType failureType, string message, Exception innerException)
            : base(message, innerException)
        {
            FailureType = failureType;
        }
    }
}
