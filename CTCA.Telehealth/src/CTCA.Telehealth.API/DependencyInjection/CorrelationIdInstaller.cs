using System;
using CorrelationId.DependencyInjection;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class CorrelationIdInstaller
    {
        public static void InstallCorrelationId(this IServiceCollection services)
        {
            services.AddDefaultCorrelationId(options =>
            {
                options.CorrelationIdGenerator = () => "ctca-telehealth-" + Guid.NewGuid();
                options.AddToLoggingScope = true;
                options.EnforceHeader = true;
                options.IgnoreRequestHeader = false;
                options.IncludeInResponse = true;
                options.RequestHeader = "x-ctca-correlation-id";
                options.UpdateTraceIdentifier = false;
            });
        }
    }
}
