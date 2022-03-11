using System;
using System.Collections.Generic;

namespace CTCA.Telehealth.Microsoft.Common
{
    public class ErrorResponse
    {
        /// <summary>
        /// Gets or sets the code.
        /// </summary>
        /// <value>
        /// The code.
        /// </value>
        public string Code { get; set; }

        /// <summary>
        /// Gets or sets the message.
        /// </summary>
        /// <value>
        /// The message.
        /// </value>
        public string Message { get; set; }

        /// <summary>
        /// Gets or sets the trace.
        /// </summary>
        /// <value>
        /// The trace.
        /// </value>
        public List<ErrorResponse> Trace { get; set; } = new List<ErrorResponse>();
    }
}
