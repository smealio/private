using System.Collections.Generic;

namespace CTCA.Telehealth.Cosmos.Clients
{
    public class CosmosClientSettings
    {
        public string DatabaseName { get; set; }
        public string ContainerName { get; set; }
        public string EndpointUrl { get; set; }
        public string AuthorizationKey { get; set; }
    }
}
