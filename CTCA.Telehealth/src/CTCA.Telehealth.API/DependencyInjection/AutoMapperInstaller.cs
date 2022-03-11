using System;
using System.Collections.Generic;
using System.Reflection;
using AutoMapper;
using CTCA.Telehealth.API.Properties;
using CTCA.Telehealth.Application.Properties;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    /// <summary>
    /// Installs Automapper
    /// </summary>
    public static class AutoMapperInstaller
    {
        /// <summary>
        /// Scans for mapper profiles and adds them.
        /// </summary>
        /// <param name="services"></param>
        public static void InstallAutoMapper(this IServiceCollection services)
        {
            // do this to keep the scan small
            var includeAssemblies = new List<Assembly>
            {
                typeof(ApiLayerAssemblyAnchor).Assembly,
                typeof(ApplicationLayerAssemblyAnchor).Assembly,

            };

            services.AddAutoMapper(includeAssemblies);
        }
    }
}
