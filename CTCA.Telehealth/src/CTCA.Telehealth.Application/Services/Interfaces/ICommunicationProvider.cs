using System;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Application.Services.Interfaces
{
    public interface ICommunicationProvider
    {
        Task<object> ProvideAccessToken(CancellationToken cancellationToken);
    }
}
