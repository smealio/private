using System;
using System.Collections.Generic;
using System.Text;

namespace CTCA.Telehealth.Microsoft.Security
{
    public interface IClientAccessTokenStrategy
    {
        public MicrosoftAccessToken AccessToken { get; set; }

        void SetAccessTokenAsync();

        void RefreshAccessTokenAsync();

        void ClearAccessToken();

        void GetAccessToken();
    }
}
