using System;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Cosmos.Clients;
using CTCA.Telehealth.Domain.Models;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.Options;
using System.Threading.Tasks;
using System.Text;

namespace CTCA.Telehealth.Cosmos.Repository
{
    public class TelehealthAppointmentRepository : CosmosDbRepository<TelehealthAppointment>, ITelehealthAppointmentRepository
    {
        public TelehealthAppointmentRepository(IOptions<CosmosClientSettings> clientSettings) : base(clientSettings) { }

        public override string GenerateId(TelehealthAppointment entity) => $"{entity.id}:{Guid.NewGuid()}:{Convert.ToBase64String(Encoding.UTF8.GetBytes(entity.ProviderEmail))}";

    }
}
