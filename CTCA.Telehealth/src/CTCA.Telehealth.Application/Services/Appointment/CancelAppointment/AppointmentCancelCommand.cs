using CTCA.Telehealth.Application.Models;
using MediatR;
using System;
using System.Collections.Generic;
using System.Text;

namespace CTCA.Telehealth.Application.Services.Appointment
{
    public class AppointmentCancelCommand : IRequest<(int, string)>
    {
        public AppointmentCancelRequest AppointmentCancelRequest { get; }
        public AppointmentCancelCommand(AppointmentCancelRequest request)
        {
            AppointmentCancelRequest = request;
        }
    
    }
}
