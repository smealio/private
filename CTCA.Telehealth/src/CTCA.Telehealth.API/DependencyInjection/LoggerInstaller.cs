using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.OpenApi.Models;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class LoggerInstaller
    {
        public static void InstallLogger(this IServiceCollection services, IWebHostEnvironment environment, IConfiguration config)
        {
            services.AddSingleton<ILoggerFactory, LoggerFactory>();
            services.AddSingleton(typeof(ILogger<>), typeof(Logger<>));
        }
        public static void UseLoggerConfig(this IApplicationBuilder app)
        {

            // SET ANY CONFIG INFO FOR LOGGER

            // FOR EXAMPLE
            //app.UseSwagger()
            //    .UseSwaggerUI(c =>
            //    {
            //        c.SwaggerEndpoint("/swagger/v1/swagger.json", "Telehealth API");
            //        c.DefaultModelRendering(Swashbuckle.AspNetCore.SwaggerUI.ModelRendering.Model);
            //        c.DisplayRequestDuration();
            //        c.DocExpansion(Swashbuckle.AspNetCore.SwaggerUI.DocExpansion.None);
            //        c.EnableDeepLinking();
            //        c.EnableFilter();
            //        c.ShowExtensions();
            //    }
            //);

        }
    }
}

