using System;
using System.Collections.Generic;
using System.Reflection;
using CTCA.Telehealth.API.Properties;
using FluentValidation.AspNetCore;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    /// <summary>
    /// Add and configure MVC pipeline.
    /// </summary>
    public static class MvcInstaller
    {
        /// <summary>
        /// Installs the MVC.
        /// </summary>
        /// <param name="services">The services.</param>
        public static void InstallMvc(this IServiceCollection services)
        {
            // do this to keep the scan small
            var includeAssemblies = new List<Assembly>
            {
                typeof(ApiLayerAssemblyAnchor).Assembly,
            };

            services.AddMvc(options =>
            {
                //Add ApEx Later
                //options.Filters.Add(typeof("AppExFilter""));

                options.EnableEndpointRouting = false;
            })
                .SetCompatibilityVersion(CompatibilityVersion.Version_3_0)
                .AddFluentValidation(config => config.RegisterValidatorsFromAssemblies(includeAssemblies));
        }
    }
}
