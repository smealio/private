using System.Threading;
using System;

using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.Azure.Cosmos;

using Moq;
using Xunit;

using CTCA.Telehealth.Application.Models;
/*
namespace CTCA.Telehealth.UnitTest
{
    public class RepoTests
    {
        private CosmosClient cosmosClient;
        private Database database;
        private Container container;

        [Fact]
        public async void PutAppointmentPass1()
        {
            // Arrange
            var logger = Mock.Of<ILogger<TelehealthAppointment>>();

            var appset = new RepositoryProviderClientSettings()
            {
                DatabaseName = "TeleHealthDB",
                ContainerName = "AppointmentContainer",

                // cloud Cosmos settings
                //EndpointUrl = "https://scheduling-api-db-dev.documents.azure.com:443/",
                //AuthorizationKey = "FFFNi9jXj5GIKOTgdCZHykKXITUDrRpGrxNyFo4TGFXBOlRM8gAHrlLfkC0mRxFMpBxxbxIZ4xqyCR9EiP7ouQ=="

                // local Cosmos settings
                EndpointUrl = "https://localhost:8081",
                AuthorizationKey = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw=="

            };

            this.cosmosClient = new CosmosClient(appset.EndpointUrl, appset.AuthorizationKey);
            this.database = await this.cosmosClient.CreateDatabaseIfNotExistsAsync(appset.DatabaseName);
            this.container = await this.database.CreateContainerIfNotExistsAsync(appset.ContainerName, "/Service/SourceProviderId");

            IOptions<RepositoryProviderClientSettings> reposettings = Options.Create(appset);

            AudioInformation ai = new AudioInformation();

            Service si = new Service()
            {
                SourceProviderId = "12345",
                SourceProviderEmail = "DrJohnDoe@noreply.com"
            };

            TelehealthAppointment ci = new TelehealthAppointment()
            {
                id = Guid.NewGuid().ToString(),
                audioInfo = ai,
                Service = si,
                StartDateTime = DateTime.UtcNow.AddHours(2),
                isReminderSent = false

                //id = Guid.NewGuid(),
                //MeetingId = "1",
                //JoinUrl = "http://JoinUrl.com",
                //JoinWebUrl = "http://JoinWebUrl",
                //StartDateTime = DateTime.UtcNow.Date,
                //EndDateTime = DateTime.UtcNow.Date,
                //ProviderID = "1",
                //PatientName = "Max",
                //PatientEmail = "",
                //ProviderEmail = "",
                //JoinContent = "",
                //public AudioInformation audioInfo =
                //MeetingID = "",
                //CalendarID =
                //isComplete =
                //isCancelled =
                //CreatedDT =
                //ModifiedDT =
                //modifiedBy =
                //ClientSource =
            };

            // Act
            AppointmentRepository appCosmos = new AppointmentRepository(this.cosmosClient, appset);
            CancellationToken ct = new CancellationToken();

            var myResponse = await appCosmos.PutAppointment(ci, ct);
            Assert.True(myResponse, "Rows added");
        }
        [Fact]
        public void PutAppointmentFail1()
        {

        }
        [Fact]
        public void GetAppointmentByServiceIDPass()
        {

        }
        [Fact]
        public void GetAppointmentByServiceIDFail()
        {

        }
        [Fact]
        public void GetAppointmentByServiceStartPass()
        {

        }
        [Fact]
        public void GetAppointmentByServiceStartFail()
        {

        }
        [Fact]
        public async void UpdateIsReminderSentPass()
        {
            // Arrange
            var logger = Mock.Of<ILogger<TelehealthAppointment>>();

            var appset = new RepositoryProviderClientSettings()
            {
                DatabaseName = "TeleHealthDB",
                ContainerName = "AppointmentContainer",

                // cloud Cosmos settings
                //EndpointUrl = "https://scheduling-api-db-dev.documents.azure.com:443/",
                //AuthorizationKey = "FFFNi9jXj5GIKOTgdCZHykKXITUDrRpGrxNyFo4TGFXBOlRM8gAHrlLfkC0mRxFMpBxxbxIZ4xqyCR9EiP7ouQ=="

                // local Cosmos settings
                EndpointUrl = "https://localhost:8081",
                AuthorizationKey = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw=="
            };

            this.cosmosClient = new CosmosClient(appset.EndpointUrl, appset.AuthorizationKey);
            this.database = await this.cosmosClient.CreateDatabaseIfNotExistsAsync(appset.DatabaseName);
            this.container = await this.database.CreateContainerIfNotExistsAsync(appset.ContainerName, "/Service/SourceProviderId");

            AudioInformation ai = new AudioInformation();

            Service si = new Service()
            {
                SourceProviderId = "12345"
            };

            TelehealthAppointment item = new TelehealthAppointment()
            {
                id = "9c97941e-eec2-4788-9fb0-58923b861fe2",
                audioInfo = ai,
                Service = si
            };

            // Act
            AppointmentRepository appCosmos = new AppointmentRepository(this.cosmosClient, appset);
            CancellationToken ct = new CancellationToken();

            item = await appCosmos.GetAppointmentByID(item.id, ct);
            var myResponse = await appCosmos.UpdateIsReminderSentAsync(item, ct);
            Assert.True(myResponse.StatusCode == System.Net.HttpStatusCode.OK, "Rows updated");

        }
        [Fact]
        public async void UpdateAppointment()
        {
            // Arrange
            var logger = Mock.Of<ILogger<TelehealthAppointment>>();

            var appset = new RepositoryProviderClientSettings()
            {
                DatabaseName = "TeleHealthDB",
                ContainerName = "AppointmentContainer",

                // cloud Cosmos settings
                //EndpointUrl = "https://scheduling-api-db-dev.documents.azure.com:443/",
                //AuthorizationKey = "FFFNi9jXj5GIKOTgdCZHykKXITUDrRpGrxNyFo4TGFXBOlRM8gAHrlLfkC0mRxFMpBxxbxIZ4xqyCR9EiP7ouQ=="

                // local Cosmos settings
                EndpointUrl = "https://localhost:8081",
                AuthorizationKey = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw=="
            };

            this.cosmosClient = new CosmosClient(appset.EndpointUrl, appset.AuthorizationKey);
            this.database = await this.cosmosClient.CreateDatabaseIfNotExistsAsync(appset.DatabaseName);
            this.container = await this.database.CreateContainerIfNotExistsAsync(appset.ContainerName, "/Service/SourceProviderId");

            AudioInformation ai = new AudioInformation();

            Service si = new Service()
            {
                SourceProviderId = "12345"
            };

            TelehealthAppointment item = new TelehealthAppointment()
            {
                id = "9c97941e-eec2-4788-9fb0-58923b861fe2",
                audioInfo = ai,
                Service = si
            };

            // Act
            AppointmentRepository appCosmos = new AppointmentRepository(this.cosmosClient, appset);
            CancellationToken ct = new CancellationToken();

            item = await appCosmos.GetAppointmentByID(item.id, ct);

            item.Service.SourceProviderEmail = "JaneDoe@noreply.com";
            item.PatientEmail = "PatientZero@noreply.com";
            item.PatientName = "Patient Zero";
            item.isCancelled = true;

            var myResponse = await appCosmos.UpdateItemAsync(item, ct);
            Assert.True(myResponse.StatusCode == System.Net.HttpStatusCode.OK, "Rows updated");

        }
        [Fact]
        public async void GetAllAppointmentsPass()
        {
            // Arrange
            var logger = Mock.Of<ILogger<TelehealthAppointment>>();

            var appset = new RepositoryProviderClientSettings()
            {
                DatabaseName = "TeleHealthDB",
                ContainerName = "AppointmentContainer",

                // cloud Cosmos settings
                EndpointUrl = "https://scheduling-api-db-dev.documents.azure.com:443/",
                AuthorizationKey = "FFFNi9jXj5GIKOTgdCZHykKXITUDrRpGrxNyFo4TGFXBOlRM8gAHrlLfkC0mRxFMpBxxbxIZ4xqyCR9EiP7ouQ=="

                // local Cosmos settings
                //EndpointUrl = "https://localhost:8081",
                //AuthorizationKey = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw=="

            };

            this.cosmosClient = new CosmosClient(appset.EndpointUrl, appset.AuthorizationKey);
            this.database = await this.cosmosClient.CreateDatabaseIfNotExistsAsync(appset.DatabaseName);
            this.container = await this.database.CreateContainerIfNotExistsAsync(appset.ContainerName, "/Service/SourceProviderId");

            //IOptions<RepositoryProviderClientSettings> reposettings = Options.Create(appset);

            var startFrom = new DateTimeOffset(DateTime.UtcNow).AddHours(3);

            // Act
            AppointmentRepository appCosmos = new AppointmentRepository(this.cosmosClient, appset);
            CancellationToken ct = new CancellationToken();

            var myResponse = await appCosmos.GetAllAppointmentsByDT(startFrom, ct);
            Assert.True(myResponse.Count > 0, "Rows returned");
        }
        [Fact]
        public async void GetAllAppointmentsFail()
        {
            // Arrange
            var logger = Mock.Of<ILogger<TelehealthAppointment>>();

            var appset = new RepositoryProviderClientSettings()
            {
                DatabaseName = "TeleHealthDB",
                ContainerName = "AppointmentContainer",

                // Cloud Cosmos settings
                EndpointUrl = "https://scheduling-api-db-dev.documents.azure.com:443/",
                AuthorizationKey = "FFFNi9jXj5GIKOTgdCZHykKXITUDrRpGrxNyFo4TGFXBOlRM8gAHrlLfkC0mRxFMpBxxbxIZ4xqyCR9EiP7ouQ=="
            };

            this.cosmosClient = new CosmosClient(appset.EndpointUrl, appset.AuthorizationKey);
            this.database = await this.cosmosClient.CreateDatabaseIfNotExistsAsync(appset.DatabaseName);
            this.container = await this.database.CreateContainerIfNotExistsAsync(appset.ContainerName, "/Service/SourceProviderId");

            var startFrom = new DateTimeOffset(DateTime.UtcNow).AddDays(-5);

            // Act
            AppointmentRepository appCosmos = new AppointmentRepository(this.cosmosClient, appset);
            CancellationToken ct = new CancellationToken();

            var myResponse = await appCosmos.GetAllAppointmentsByDT(startFrom, ct);
            Assert.True(myResponse == null, "No Rows returned");
        }
    }
}*/
