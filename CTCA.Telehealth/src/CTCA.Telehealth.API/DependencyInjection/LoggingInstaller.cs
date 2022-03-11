using System;
using Microsoft.ApplicationInsights.Extensibility;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Serilog;
using ILogger = Microsoft.Extensions.Logging.ILogger;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class LoggingInstaller
    {
        /// <summary>
        /// Installs various aspects of logging.
        /// </summary>
        /// <param name="services"></param>
        /// <param name="config"></param>
        public static void AddCustomLogging(this IServiceCollection services, IConfiguration config)
        {
            services.AddLogging(c =>
            {
                c.AddConsole();
                c.AddDebug();
            });
            services.AddSingleton<ILogger>(provider =>
            {
                var factory = provider.GetRequiredService<ILoggerFactory>();
                return factory.CreateLogger("GenericLogger");
            });

            services.AddApplicationInsightsTelemetry();
            AddSerilog(config);
        }

        private static void AddSerilog(IConfiguration config)
        {
            var configuration = new LoggerConfiguration().ReadFrom.KeyValuePairs(config.AsEnumerable())
                .WriteTo.ApplicationInsights(config.GetSection("ApplicationInsights").GetValue<string>("InstrumentationKey"), TelemetryConverter.Traces);
            //.WriteTo.ApplicationInsights("703f32fc-7bfc-498e-854e-32e61a2830a8", TelemetryConverter.Traces);

            Serilog.Log.Logger = configuration.CreateLogger();
        }
    }
}
