using System;
using System.Collections.Generic;
using CTCA.Telehealth.Shared;
using Newtonsoft.Json;

namespace CTCA.Telehealth.API.Common
{
    
    public class ViewModelResult
    {

        [JsonProperty("metadata")]
        public Dictionary<string, object> Metadata { get; set; } = new Dictionary<string, object>();


        [JsonProperty("errors")]
        public List<ServiceError> Errors { get; set; } = new List<ServiceError>();

       
        [JsonProperty("isPartialSuccess")]
        public bool IsPartialSuccess { get; set; }
    }

   
    public class ViewModelResult<T> : ViewModelResult
    {
       
        [JsonProperty("result")]
        public T Result { get; set; }
        
       
        public ViewModelResult(T result) => Result = result;
    }
}
