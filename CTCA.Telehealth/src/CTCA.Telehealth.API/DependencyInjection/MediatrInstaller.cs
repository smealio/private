using System;
using System.Collections.Generic;
using System.Reflection;
using CTCA.Telehealth.API.Properties;
using CTCA.Telehealth.Application.Properties;
using MediatR;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class MediatrInstaller
    {
        public static void InstallMediatr(this IServiceCollection services)
        {
            // do this to keep the scan small
            var includeAssemblies = new List<Assembly>
            {
                typeof(ApiLayerAssemblyAnchor).Assembly,
                typeof(ApplicationLayerAssemblyAnchor).Assembly,

            };

            services.AddMediatR(includeAssemblies, config =>
            {
                config.AsTransient();
            });

            //Add request and appex later
            //services.AddScoped(typeof(IPipelineBehavior<,>), typeof(RequestLoggingBehavior<,>));
            //services.AddScoped(typeof(IPipelineBehavior<,>), typeof(ApplicationExceptionBehavior<,>));
        }
    }
}
