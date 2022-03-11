using System;
using CTCA.Telehealth.Cosmos.Clients;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Cosmos.Repository;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Azure.Documents.Client;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using System.Linq;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class CosmosInstaller
    {
        public static void InstallCosmos(this IServiceCollection services, IConfiguration config)
        {
            services.Configure<CosmosClientSettings>(options =>
                config.GetSection("repoSettings").Bind(options));

            services.AddSingleton<ITelehealthAppointmentRepository, TelehealthAppointmentRepository>();

        }
    }
}
