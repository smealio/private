using System;
using Azure.Communication.Identity;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Microsoft.Clients.ACS;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class CommunicationServiceInstaller
    {
        public static void InstallComunicationServiceProvider(this IServiceCollection services, IConfiguration config)
        {
            var options = new CommunicationServiceClientSettings();
                config.GetSection("AzureCommunicationServices").Bind(options);

            var acsClient = new MicrosoftAcsClient(new CommunicationIdentityClient(options.CommunicationServiceUri));

            services.AddSingleton<ICommunicationProvider>(acsClient);
            
        }
    }
}
