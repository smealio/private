using System;
using CTCA.Telehealth.API.Models.Response;
using CTCA.Telehealth.Domain.Models;

namespace CTCA.Telehealth.API.Models.Profiles
{
    public class MeetingsProfile : AutoMapper.Profile
    {
        public MeetingsProfile()
        {


            CreateMap<TelehealthAppointment, TelehealthMeetingsViewModel>()
                .ForMember(t => t.AppointmentDateTime, a => a.MapFrom(r => r.StartDateTime))
                .ForMember(t => t.MeetingId, a => a.MapFrom(r => r.MeetingId))
                .ForMember(t => t.MeetingJoinUrl, a => a.MapFrom(r => r.JoinUrl))
                .ForMember(t => t.MeetingJoinWebUrl, a => a.MapFrom(r => r.JoinWebUrl))
                .IncludeAllDerived();
        }
    }
}
