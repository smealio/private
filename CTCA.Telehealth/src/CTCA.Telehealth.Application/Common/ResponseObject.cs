using System;
using System.Collections.Generic;
using CTCA.Telehealth.Shared;

namespace CTCA.Telehealth.Application.Common
{
    public class ResponseObject<T>
    {

        public IDictionary<string, object> Metadata { get; } = new Dictionary<string, object>();

        public T Value { get; }

        public ResponseObject(T value)
        {
            Value = Ensure.IsNotNull(value, nameof(value));
        }

        public void AddMetadata(string key, object value)
        {
            Metadata.Add(key, value);
        }
    }
}
