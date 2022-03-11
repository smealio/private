using System;
namespace CTCA.Telehealth.Microsoft.Security
{

        public interface IAccessTokenStrategy
        {
            public MicrosoftAccessToken AccessToken { get; set; }

            void SetAccessTokenAsync();

            void RefreshAccessTokenAsync();

            void ClearAccessToken();

            void GetAccessToken();
        }
    
}
