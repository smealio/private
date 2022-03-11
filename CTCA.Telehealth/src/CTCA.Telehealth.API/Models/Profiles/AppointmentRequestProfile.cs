using System;
using AutoMapper;
using CTCA.Telehealth.API.Models.Request;
using CTCA.Telehealth.API.Models.Response;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Domain.Models;

namespace CTCA.Telehealth.API.Models.Profiles
{
    public class AppointmentRequestProfile : AutoMapper.Profile
    {
        public AppointmentRequestProfile()
        {
            CreateMap<TelehealthAppointmentRequest, Service>()
                .ForMember(t => t.Source, a => a.MapFrom(r => r.AppointmentServiceSource))
                .ForMember(t => t.SourceProviderEmail, a => a.MapFrom(r => r.AppointmentOrganizerEmail))
                .ForMember(t => t.SourceProviderId, a => a.MapFrom(r => r.AppointmentProviderId))
                .ForMember(t => t.Source, a => a.MapFrom(r => r.AppointmentServiceSource))
                .ForMember(t => t.SourceServiceId, a => a.MapFrom(r => r.AppointmentServiceId));



            CreateMap<TelehealthAppointmentRequest, TelehealthAppointment>()
                .ForMember(t => t.id, a => a.MapFrom(r => r.AppointmentServiceId))
                .ForMember(t => t.source, a => a.MapFrom(r => r.AppointmentServiceSource))
                .ForMember(t => t.PatientId, a => a.MapFrom(r => r.PID))
                .ForMember(t => t.PatientName, a => a.MapFrom(r => r.AppointmentPatientName))
                .ForMember(t => t.StartDateTime, a => a.MapFrom(r => r.AppointmentStartTime))
                .ForMember(t => t.ProviderEmail, a => a.MapFrom(r => r.AppointmentOrganizerEmail))
                .ForMember(t => t.ProviderName, a=> a.MapFrom(r=>r.AppointmentProviderName))
                .ForMember(t => t.EndDateTime, a => a.MapFrom(r => r.AppointmentStartTime.AddMinutes(r.AppointmentDuration)))
                .ForMember(t => t.AppointmentDuration, a=>a.MapFrom(r=>r.AppointmentDuration))
                .ForMember(t => t.Service, a => a.MapFrom(r => r.AppointmentOrganizerEmail))
                .ForMember(t => t.AppointmentTimeZone, a => a.MapFrom(r => r.AppointmentTimeZone))
                .ForMember(t => t.PatientEmail, a => a.MapFrom(r => r.AppointmentPatientEmail))
                .ForMember(t => t.Service, a => a.MapFrom(src => src))
                .IncludeAllDerived();

            CreateMap<TelehealthAppointment, TelehealthAppointmentResponse>()
                .ForMember(t => t.AppointmentPatientId, a => a.MapFrom(r => r.PatientId))
                    .ForMember(t => t.AppointmentProviderId, a => a.MapFrom(r => r.Service.SourceProviderId))
                    .ForMember(t => t.AppointmentServiceSource, a => a.MapFrom(r => r.source))
                    .ForMember(t => t.TelehealthAppointmentId, a => a.MapFrom(r => r.id))
                    .ForMember(t => t.AppointmentServiceId, a => a.MapFrom(r => r.Service.SourceServiceId));
        }
    }
}


