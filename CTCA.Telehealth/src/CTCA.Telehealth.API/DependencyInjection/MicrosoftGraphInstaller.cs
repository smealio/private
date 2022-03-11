using System;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Microsoft;
using CTCA.Telehealth.Microsoft.Clients;
using CTCA.Telehealth.Microsoft.Security;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class MicrosoftGraphInstaller
    {
        public static void InstallMicrosoftGraph(this IServiceCollection services, IConfiguration config)
        {
            services.Configure<MicrosoftGraphApiClientSettings>(options =>
                config.GetSection("MicrosoftGraphApiRbocSettings").Bind(options));

            services.AddHttpClient<RbocAccessTokenStrategy>();
            services.AddTransient<IAccessTokenStrategy, RbocAccessTokenStrategy>();
            services.AddHttpClient<IMeetingProvider, MicrosoftGraphRbocClient>();
        }
    }
}
