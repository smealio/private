using CTCA.Telehealth.Domain.Models;
using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Domain.Interfaces
{
    public interface IRepository<T> where T : Entity
    {
        Task<T> GetByIdAsync(string id);
        //Task<List<T>> GetAsync(IList<string> ids);
        //Task<IList<T>> GetAllAsync();
        Task<T> AddAsync(T entity);
        Task UpdateAsync(T entity);
        Task DeleteAsync(T entity);
        //Task UpsertAsync(T entity);
        Task<List<T>> SearchAsync(Expression<Func<T, bool>> predicate);
    }
}
