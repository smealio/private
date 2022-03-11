using System;
using System.Configuration;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.API.ClientSettings;
using CTCA.Telehealth.API.Common;
using CTCA.Telehealth.Application.Services.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.API.Controllers
{

    public class AccessTokenController : ApiControllerBase
    {
        private readonly ILogger<AccessTokenController> _logger;
        private readonly ICommunicationProvider _client;

        public AccessTokenController(IServiceProvider serviceProvider, ILogger<AccessTokenController> logger, ICommunicationProvider client)
        : base(serviceProvider)
        {
            _logger = logger;
            _client = client;
        }

        [HttpPost("api/[controller]")]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> Get(CancellationToken cancellationToken)
        {
            var tokenResponse = await _client.ProvideAccessToken(cancellationToken);
            return Ok(tokenResponse);       
        }
    }
}
