using System;
namespace CTCA.Telehealth.Domain.Models
{
    public abstract class Entity
    {
       public string id { get; set; }
        public DateTime CreatedDT { get; set; }
        public string CreatedBy { get; set; }
        public DateTime ModifiedDT { get; set; }
        public string ModifiedBy { get; set; }
    }
}
