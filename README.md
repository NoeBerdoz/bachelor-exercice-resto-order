# Data Mapper Exercise

This bachelor’s exercise demonstrates the use of the Data Mapper pattern in Java by connecting a small CLI application to an Oracle database, making the data persistent.

The goal of the exercise was to persist the application domain model in the provided data structure by applying [Martin Fowler's Data Mapper design pattern](https://martinfowler.com/eaaCatalog/dataMapper.html), utilizing the available classes to manage database interactions.

## Exercise questions

**How is the JDBC connection managed?**

My implementation uses a lazy-loaded singleton for the connection class, meaning the connection is only created when needed and closed when the application ends. 

Commits and rollbacks are managed by each Data Mapper class. A transaction is rolled back if an exception is thrown and is committed when the transaction is successful.

**How are primary keys generated and persisted in the Java objects?**

JDBC is implemented to use Oracle's `RETURNING` clause in SQL to retrieve the generated primary keys, which are then set in the corresponding Java objects after their creation.
This is defined in the `PreparedStatement` and can be used as follows with `getGeneratedKeys()`:
```java
    PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});
    ResultSet generatedKeys = statement.getGeneratedKeys();
    if (generatedKeys.next()) {
        String generatedId = generatedKeys.getString(1);
    }
```

**How are relations managed?**


**When searching for a restaurant in the Data Mapper, what should be fetched? Should it be only the restaurant, its orders, or something else? Where do we stop?**

For my implementation, I used a lazy loading pattern. When searching for a restaurant, only the restaurant data is retrieved initially. Additional related data, such as orders, is only loaded when explicitly requested, minimizing unnecessary database queries and improving performance.

**How are relations between the different Data Mappers managed?**

The different Data Mappers are loosely coupled. They interact primarily through their caches and use each other’s data to maintain consistency. This ensures that objects like `Order` and `Product` are properly associated with the `Restaurant`, and their respective data is synchronized via the cache.

**How many interactions with the database are made by my code?**

The number of interactions with the database is minimized. I have implemented a cache and used the Identity Map pattern to ensure that data is fetched from the database only when it is not already present in the cache. This reduces unnecessary database queries and improves performance by reusing objects already loaded into memory.

## Setup

To run this project, you need to create a `database.properties` file at the root of your project, with the following content:

```properties
db.url=jdbc:oracle:thin:@your.db.url:port:sid
db.user=YOUR_USER
db.password=YOUR_PASSWORD
```

Make sure to replace the placeholders with your actual database details.

## Possible Improvements

- **Singleton Lazy Loading**: The singleton is lazy-loaded instead of eagerly fetched, which is not multi-thread safe. However, this approach is more optimized for a single-threaded environment, and multi-thread safety could be addressed by using appropriate synchronization techniques or an alternative design pattern.  
  

- **Cache Management**: The cache implementation is far from perfect. Currently, it is a bit convoluted, and if other processes (besides this application) update the data in the database, the cache will not automatically update. Improvements could be made to synchronize the cache with the database more effectively.

## Conclusion

This exercise made me realize the complexity of managing Java objects and relational database data.

The provided exercise was initially in poor condition, with incorrect SQL queries and names following the school's DSL. The code did not follow Java coding conventions and contained several inconsistencies. Through this exercise, I had the opportunity to refactor the code and improve its structure, bringing it closer to industry standards.


