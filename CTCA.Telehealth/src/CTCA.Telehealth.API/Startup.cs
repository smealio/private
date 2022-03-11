using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CorrelationId;
using CorrelationId.DependencyInjection;
using CTCA.Telehealth.API.DependencyInjection;
using CTCA.Telehealth.Application.Models;
using Microsoft.AspNet.OData.Builder;
using Microsoft.AspNet.OData.Extensions;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.OData.Edm;

namespace CTCA.Telehealth.API
{
    public class Startup
    {
        public IConfiguration Configuration { get; }
        public IWebHostEnvironment Environment { get; }

        public Startup(IConfiguration configuration, IWebHostEnvironment environment)
        {
            Configuration = configuration;
            Environment = environment;



        }

        // This method gets called by the runtime. Use this method to add services to the container.
        // For more information on how to configure your application, visit https://go.microsoft.com/fwlink/?LinkID=398940
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddHttpClient();
            //services.AddInfrastructure(Configuration);
            //services.InstallDomainServices();
            services.InstallMicrosoftGraph(Configuration);
            services.AddCorrelationId();
            services.InstallSwagger(Environment, Configuration);
            services.InstallMediatr();
            services.InstallMvc();
            services.InstallAutoMapper();
            services.InstallCalendarProvider(Configuration);
            services.InstallEmailProvider(Configuration);
            services.AddCustomLogging(Configuration);
            //services.InstallRepositoryProvider(Configuration);
            services.InstallCosmos(Configuration);
            //services.AddCustomLogging(Configuration);
            services.InstallComunicationServiceProvider(Configuration);

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


        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Usage", "MVC1005:Cannot use UseMvc with Endpoint Routing.", Justification = "check installer")]
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseHsts();
            }

            //app.UseCorrelationId();

            app.UseSwaggerConfig();

            app.UseMvc();
        }

    }
}
