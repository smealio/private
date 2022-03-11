using System;
using System.Collections.Generic;
using System.Text;

namespace CTCA.Telehealth.Shared
{
    public static class TimeZoneMapper {
        public static string TimeZone(string timezone, DateTimeOffset time)
        {
            TimeZoneInfo ti = TimeZoneInfo.FindSystemTimeZoneById(timezone);

            switch (timezone) {
                case "Central Standard Time":
                    if (ti.IsDaylightSavingTime(time))
                        return "Central Daylight Time";
                    return timezone;
                case "Eastern Standard Time":
                    if (ti.IsDaylightSavingTime(time))
                        return "Eastern Daylight Time";
                    return timezone;
                default:
                    return timezone;
            }
        }
    }
}
