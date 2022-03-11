using System;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Application.Common.Exceptions;
using MediatR;
using Microsoft.Extensions.Logging;
using ApplicationException = CTCA.Telehealth.Application.Common.Exceptions.ApplicationException;

namespace CTCA.Telehealth.Application.Common
{

    public abstract class RequestHandler
    {
        protected ILogger Logger { get; }

        protected RequestHandler(ILogger logger)
        {
            Logger = logger;
        }

       
        protected async Task<TResponseType> RunAsync<TRequestType, TResponseType>(Func<Task<TResponseType>> action)
        {
            try
            {
                var requestType = $"{typeof(TRequestType).ToString() ?? "null"}";
                Logger.LogTrace("Processing request '{requestType}'", requestType);
                var result = await action();
                return result;
            }
            catch (EmailNotFoundException ex)
            {
                throw new ApplicationException(System.Net.HttpStatusCode.NotFound, ex.Message);
            }
            //Can repeat above if necessary for different types of exceptions
           
        }
    }

    /// <summary>
    /// Middleware for processing MediatR commands.
    /// </summary>
    /// <typeparam name="TRequest">The type of the request.</typeparam>
    /// <typeparam name="TResponse">The type of the response.</typeparam>
    /// <seealso cref="MediatR.IRequestHandler{TRequest, TResponse}" />
    public abstract class RequestHandler<TRequest, TResponse> : RequestHandler, IRequestHandler<TRequest, TResponse>
        where TRequest : IRequest<TResponse>
    {
        protected RequestHandler(ILogger logger) : base(logger) { }

        public async Task<TResponse> Handle(TRequest request, CancellationToken cancellationToken)
        {
            if (request == null)
                throw new ApplicationException(System.Net.HttpStatusCode.BadRequest,
                    $"{typeof(TRequest)} was null.",
                    new ArgumentNullException(typeof(TRequest).ToString()));

            var result = await RunAsync<TRequest, TResponse>(async () => await HandleRequest(request, cancellationToken));
            return result;
        }

        /// <summary>
        /// Handles the request.
        /// </summary>
        /// <param name="request">The request.</param>
        /// <param name="cancellationToken">The cancellation token.</param>
        /// <returns></returns>
        protected abstract Task<TResponse> HandleRequest(TRequest request, CancellationToken cancellationToken);
    }
}
