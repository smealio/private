﻿using System;
namespace CTCA.Telehealth.Microsoft
{
    public class MicrosoftGraphApiClientSettings
    {
        public string BaseUrl { get; set; }
        public string AuthUrl { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }
        public string GrantType { get; set; }
        public string Scope { get; set; }
        public string ClientId { get; set; }
        public string ClientSecret { get; set; }

    }
}
