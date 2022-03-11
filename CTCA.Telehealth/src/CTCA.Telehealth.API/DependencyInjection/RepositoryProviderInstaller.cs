using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Microsoft.Clients;
using System.Threading.Tasks;
using Microsoft.Azure.Cosmos.Fluent;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.Options;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class RepositoryProviderInstaller
    {
        public static void InstallRepositoryProvider(this IServiceCollection services, IConfiguration config)
        {
            var appSettings = new RepositoryProviderClientSettings();
            config.Bind("repoSettings", appSettings);

            
            services.AddSingleton<IRepositoryProvider>(InitializeCosmosClientInstanceAsync(appSettings).GetAwaiter().GetResult());

        }

        private static async Task<AppointmentRepository> InitializeCosmosClientInstanceAsync(RepositoryProviderClientSettings clientSettings)
        {

            CosmosClientBuilder clientBuilder = new CosmosClientBuilder(clientSettings.EndpointUrl, clientSettings.AuthorizationKey);
            CosmosClient client = clientBuilder
                                .WithConnectionModeDirect()
                                .Build();
            AppointmentRepository cosmosDbService = new AppointmentRepository(client, clientSettings);
            DatabaseResponse database = await client.CreateDatabaseIfNotExistsAsync(clientSettings.DatabaseName);
            await database.Database.CreateContainerIfNotExistsAsync(clientSettings.ContainerName, "/providerId");

            return cosmosDbService;
        }
    }
}
