using System;
using System.Collections.Generic;

namespace CTCA.Telehealth.Application.Common
{
    public class PagedObjectSetResponse<T> : ObjectSetResponse<T>
    {
      
        public int Page { get; }
              
        public int PageSize { get; }
             
        public int Total { get; }

        public PagedObjectSetResponse(IEnumerable<ResponseObject<T>> results, int page, int pageSize, int total)
            : base(results)
        {
            Page = page;
            PageSize = pageSize;
            Total = total;
        }
    }
}
