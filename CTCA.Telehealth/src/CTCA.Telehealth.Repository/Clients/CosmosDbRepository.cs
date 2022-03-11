using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Application.Common.Exceptions;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using Microsoft.Azure.Cosmos;
using Microsoft.Azure.Cosmos.Linq;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using ApplicationException = CTCA.Telehealth.Application.Common.Exceptions.ApplicationException;

namespace CTCA.Telehealth.Cosmos.Clients
{
    public abstract class CosmosDbRepository<T> : IRepository<T> where T : Entity
    {
        public Container _container;

        protected CosmosDbRepository(IOptions<CosmosClientSettings> clientSettings)
        {
            var dbClient = new CosmosClient(clientSettings.Value.EndpointUrl, clientSettings.Value.AuthorizationKey);
            _container = dbClient.GetContainer(clientSettings.Value.DatabaseName, clientSettings.Value.ContainerName);
        }

        public virtual async Task<T> GetByIdAsync(string id)
        {
            try
            {
               return await _container.ReadItemAsync<T>(id, ResolvePartitionKey(id));
            }
            catch (CosmosException e)
            {
                if (e.StatusCode == HttpStatusCode.NotFound)
                {
                    return null;
                }

                throw;
            }
        }

        public async Task<T> AddAsync(T entity)
        {
            try
            {
                entity.id = GenerateId(entity);
                var document = await _container.CreateItemAsync(entity);
                return document;
            }
            catch (CosmosException e)
            {
                if (e.StatusCode == HttpStatusCode.Conflict)
                {
                    throw new EntityAlreadyExistsException(entity.id);
                }

                throw;
            }
        }

        public async Task UpdateAsync(T entity)
        {
            try
            {
                await _container.ReplaceItemAsync(entity, entity.id);
            }
            catch (CosmosException e)
            {
                if (e.StatusCode == HttpStatusCode.NotFound)
                {
                    throw new ApplicationException(HttpStatusCode.NotFound, e.Message);
                }

                throw;
            }
        }

        public async Task DeleteAsync(T entity)
        {
            try
            {
                await _container.DeleteItemAsync<T>(entity.id, ResolvePartitionKey(entity.id));
            }
            catch (CosmosException e)
            {
                if (e.StatusCode == HttpStatusCode.NotFound)
                {
                    throw new ApplicationException(HttpStatusCode.NotFound);
                }

                throw;
            }
        }

        public async Task<List<T>> SearchAsync(Expression<Func<T, bool>> predicate)
        {
            try
            {
                var queryContainter = _container.GetItemLinqQueryable<T>();
                var iterator = queryContainter.Where(predicate).ToFeedIterator();

                var results = await iterator.ReadNextAsync();

                return results.ToList();
            }
            catch (CosmosException e)
            {
                if (e.StatusCode == HttpStatusCode.NotFound)
                {
                    throw new ApplicationException(HttpStatusCode.NotFound);
                }

                throw;
            }
        }

        public virtual string GenerateId(T entity) => Guid.NewGuid().ToString();
        public virtual PartitionKey ResolvePartitionKey(string entityId) 
        {
            var splitId = entityId.Split(':');
            // This logic was put in place after new containers were created with the providers email as the partition key.
            // This allows all previous appointments to work with no partition key and new appointments (after 06/2021) to work the new partition key.
            if (splitId.Length > 2)
            {
                var base64EncodedProviderEmail = entityId.Split(':')[2];
                var providerEmail = Encoding.UTF8.GetString(Convert.FromBase64String(base64EncodedProviderEmail));
                return new PartitionKey(providerEmail);
            }
            else
            {
                return PartitionKey.None;
            }
        } 
    }
}