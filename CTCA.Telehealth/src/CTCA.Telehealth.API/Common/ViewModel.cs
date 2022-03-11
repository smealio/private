using System;
using Newtonsoft.Json;

namespace CTCA.Telehealth.API.Common
{
    public class ViewModel
    {
        [JsonProperty("metadata")]
        public Metadata Metadata { get; set; }
    }
}
