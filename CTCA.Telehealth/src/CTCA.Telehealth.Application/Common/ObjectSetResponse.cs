using System;
using System.Collections.Generic;
using System.Linq;

namespace CTCA.Telehealth.Application.Common
{
   
    public class ObjectSetResponse<T> : ObjectResponse
    {
       
        public int Count => Results.Count;
              
        public IReadOnlyCollection<ResponseObject<T>> Results { get; set; }

       
        public ObjectSetResponse(IEnumerable<ResponseObject<T>> results)
        {
            Results = results?.ToList() ?? new List<ResponseObject<T>>();
        }
    }
}
