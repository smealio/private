using System;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Microsoft.Clients;
using Microsoft.Extensions.DependencyInjection;

namespace CTCA.Telehealth.API.DependencyInjection
{
    public static class DomainSeviceInstaller
    {
        public static void InstallDomainServices(this IServiceCollection services)
        {
            services.AddScoped<IMeetingProvider, MicrosoftGraphRbocClient>();
            services.AddScoped<ICalendarProvider, MicrosoftGraphO365Client>();
        }
    }
}
