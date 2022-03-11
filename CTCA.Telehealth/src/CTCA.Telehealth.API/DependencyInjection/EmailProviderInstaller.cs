using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Microsoft;
using CTCA.Telehealth.Microsoft.Clients;
using CTCA.Telehealth.Microsoft.Models.Email;
using CTCA.Telehealth.Microsoft.Security;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class EmailProviderInstaller
    {
        public static void InstallEmailProvider(this IServiceCollection services, IConfiguration config)
        {
            services.Configure<MicrosoftGraphApiClientSettings>(options =>
                config.GetSection("MicrosoftGraphApiRbocSettings").Bind(options));

            services.Configure<EmailSettings>(options =>
                config.GetSection("EmailSettings").Bind(options));

            services.AddHttpClient<ClientCredentialsAccessTokenStrategy>();
            services.AddTransient<IClientAccessTokenStrategy, ClientCredentialsAccessTokenStrategy>();

            services.AddHttpClient<IMailProvider, MicrosoftGraphEmailClient>();
        }
    }
}
