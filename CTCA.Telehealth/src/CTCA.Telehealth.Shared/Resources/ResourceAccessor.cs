using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

namespace CTCA.Telehealth.Shared.Resources
{
    public static class ResourceAccessor
    {
        public static string GetPatientEmailTemplate() { return Properties.Resources.TelehealthEmailv2; }
        public static string GetReadinessCheckTemplate() { return Properties.Resources.ReadinessCheckEmailv2; }
        public static string GetEmailAttachment(string name) { return System.Convert.ToBase64String((byte[])Properties.Resources.ResourceManager.GetObject(name)); }

    }
}
