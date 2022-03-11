using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using FluentValidation;

namespace CTCA.Telehealth.Shared
{
    /// <summary>
    /// Standard guards.
    /// </summary>
    public static class Ensure
    {

        /// <summary>
        /// Determines whether [is not null or empty] [the specified value].
        /// </summary>
        /// <param name="value">The value.</param>
        /// <param name="parameterName">Name of the parameter.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException"></exception>
        public static string IsNotNullOrEmpty(string value, string parameterName)
        {
            if (string.IsNullOrEmpty(value))
                throw new ArgumentException($"{parameterName} cannot be null!", parameterName);

            return value;
        }

        /// <summary>
        /// Determines whether [is not null or empty] [the specified value].
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="value">The value.</param>
        /// <param name="parameterName">Name of the parameter.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException"></exception>
        public static IEnumerable<T> IsNotNullOrEmpty<T>(IEnumerable<T> value, string parameterName)
        {
            if (value == null || !value.Any())
                throw new ArgumentException($"{parameterName} cannot be null or empty collection!");

            return value;
        }


        /// <summary>
        /// Determines whether the specified value has value.
        /// </summary>
        /// <param name="value">The value.</param>
        /// <param name="parameterName">Name of the parameter.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException"></exception>
        public static string HasValue(string value, string parameterName)
        {
            if (string.IsNullOrEmpty(value) || string.IsNullOrWhiteSpace(value))
                throw new ArgumentException($"{parameterName} must have a non-blank value!", parameterName);

            return value;
        }


        /// <summary>
        /// Determines whether [is not null] [the specified value].
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="value">The value.</param>
        /// <param name="parameterName">Name of the parameter.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException"></exception>
        public static T IsNotNull<T>(T value, string parameterName)
        {
            if (value == null)
                throw new ArgumentNullException(parameterName);

            return value;
        }


        /// <summary>
        /// Determines whether the specified collection has elements.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="collection">The collection.</param>
        /// <param name="parameterName">Name of the parameter.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException">Collection must have at least 1 element</exception>
        public static T HasElements<T>(T collection, string parameterName)
            where T : IEnumerable
        {
            var count = collection.Cast<object>().Count();

            return count <= 0
                ? throw new ArgumentException("Collection must have at least 1 element", parameterName)
                : collection;
        }


        /// <summary>
        /// Ensures the specified condition.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="predicate">The predicate.</param>
        /// <param name="value">The value.</param>
        /// <param name="message">The message.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException"></exception>
        public static T That<T>(Func<T, bool> predicate, T value, string message)
        {
            if (!predicate(value))
                throw new ArgumentException(message);

            return value;
        }

        /// <summary>
        /// Returns {T} if {T} is valid, otherwise throws an argument exception.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="input">The input.</param>
        /// <param name="parameterName">Name of the parameter.</param>
        /// <param name="validator">The validator.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException"></exception>
        public static T IsValid<T>(T input, string parameterName, IValidator<T> validator, Exception exceptionOverride = null)
        {
            var validationResult = validator.Validate(input);
            if (validationResult.IsValid)
                return input;

            var message = string.Join("; ", validationResult.Errors.Select(err => err.ErrorMessage));
            throw exceptionOverride ?? new ArgumentException(message, parameterName);
        }
    }
}
