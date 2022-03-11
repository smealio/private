using CTCA.Telehealth.Application.Models;
using MediatR;
using System;
using System.Collections.Generic;
using System.Text;

namespace CTCA.Telehealth.Application.Services.ReadinessCheck.CancelReadinessCheck
{
    public class ReadinessCheckCancelCommand : IRequest<(int, string)>
    {
        public AppointmentCancelRequest ReadinessCheckCancelRequest { get; }
        public ReadinessCheckCancelCommand(AppointmentCancelRequest request)
        {
            ReadinessCheckCancelRequest = request;
        }
    }
}
