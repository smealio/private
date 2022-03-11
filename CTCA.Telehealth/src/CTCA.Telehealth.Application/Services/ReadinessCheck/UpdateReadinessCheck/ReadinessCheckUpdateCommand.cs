using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using MediatR;
using System;
using System.Collections.Generic;
using System.Text;

namespace CTCA.Telehealth.Application.Services.ReadinessCheck.UpdateReadinessCheck
{
    public class ReadinessCheckUpdateCommand : IRequest<ObjectResponse<TelehealthAppointment>>
    {
        public TelehealthAppointment ReadinessCheckRequest { get; }
        public ReadinessCheckUpdateCommand(TelehealthAppointment readinessCheckRequest)
        {
            ReadinessCheckRequest = readinessCheckRequest;
        }
    }
}
