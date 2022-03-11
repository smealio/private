using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.API
{
    public class Program
    {
        public static void Main(string[] args)
        {
            CreateWebHostBuilder(args).Build().Run();
        }

        public static IWebHostBuilder CreateWebHostBuilder(string[] args) =>
             WebHost.CreateDefaultBuilder(args)
                 .ConfigureLogging(config => config.ClearProviders())
                 //.UseSerilog()
                 .ConfigureAppConfiguration((context, config) =>
                 {
                     var builtConfig = config.Build();
                     config.AddAzureKeyVault($"{builtConfig["AzureKeyVaultUri"]}");
                     config.AddEnvironmentVariables();
                     config.AddJsonFile("appsettings.json", optional: true, reloadOnChange: true);
                     config.AddJsonFile($"appsettings.{context.HostingEnvironment.EnvironmentName}.json", optional: true);
                     //config.AddKeyVault(context.HostingEnvironment);
                 })
                 .UseIISIntegration()
                 .UseStartup<Startup>();
    }
}
