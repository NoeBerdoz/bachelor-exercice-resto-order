# Data Mapper Exercise

This bachelor’s exercise demonstrates the use of the Data Mapper pattern in Java by connecting a small CLI application to an Oracle database, making the data persistent.

The goal of the exercise was to persist the application domain model in the provided data structure by applying [Martin Fowler's Data Mapper design pattern](https://martinfowler.com/eaaCatalog/dataMapper.html).
The classes present in the presentation and business layers have been given at the start of the project.

## Exercise questions

**How is the JDBC connection managed?**

My implementation uses a lazy-loaded singleton for the connection class, meaning the connection is only created when needed and closed when the application ends. 

Commits and rollbacks are managed by each Data Mapper class. A transaction is rolled back if an exception is thrown and is committed when the transaction is successful.

**How are primary keys generated and persisted in the Java objects?**

JDBC is implemented to use Oracle's `RETURNING INTO` clause in SQL, we can use it to retrieve the generated primary keys, which are then set in the corresponding Java objects after their creation.
This is defined in the `PreparedStatement.prepareStatement()` second parameter and can be used as follows with `getGeneratedKeys()` to retrieve the generated key:
```java
    PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});
    ResultSet generatedKeys = statement.getGeneratedKeys();
    if (generatedKeys.next()) {
        String generatedId = generatedKeys.getString(1);
    }
```

**How are relations managed?**  
The relations, when concerning a OneToMany relationship, selectWhere methods are at disposal to retrieve the related objects with a given foreign key.

But when it comes to MayToMany relationship, in my case the relation is managed by a specific DataMapper, `ProductOrderMapper` that has the responsibility to manage the relation between `Product` and `Order` entities (Java and SQL).  

I have to say that the implementation of bidirectional relationship as Object was a bit of a struggle as it comes with many challenges.
For example, depending on the implementation, loading an `Order` may load its `Products`, and each `Product` might load the `Order` again, causing infinite loops, this issue is called **circular reference**.

My implementation is not perfect, but it doesn't do circular reference. 

**When searching for a restaurant in the Data Mapper, what should be fetched? Should it be only the restaurant, its orders, or something else? Where do we stop?**

I used a lazy loading pattern. When searching for a restaurant, only the restaurant data is retrieved initially. 
Additional related data, such as orders, is only loaded when explicitly requested, minimizing unnecessary database queries and improving performance.

**How are relations between the different Data Mappers managed?**

I tried to follow SOLID principles and keep the Data Mappers independent of each other. 
However, in some cases, relations between different Data Mappers are implemented to maintain the cache.

And in my implementation, the ProductOrderMapper breaks this principle
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

Make sure to install the dependencies present in `pom.xml`.

## Possible Improvements

- **Make the DataMappers independant of each others**: In my current implementation, the ProductOrderMapper breaks this principle, but it could be fixed by doing 2 changes: 
  1. Extracting the object Mapping logic (`mapToObject()`) to another class responsibility
  2. Extracting the cache logic, so it is not implemented in each DataMappers and would avoid them to have the need to communicate together to update the cache. 

- **Multi-threading**: My implementation is not multi-threading friendly, each singleton is lazy-loaded instead of eagerly fetched, which is not multi-thread safe. (However, this approach is more optimized for a single-threaded environment). And many other things would need to be change in their implementation like the connection pooling possibility, the cache management etc...

- **Cache Management**: The cache implementation is far from perfect. And if other processes (besides this application) update the data in the database, the cache will not automatically update. Improvements could be made to synchronize the cache with the database more effectively.

## Conclusion

This exercise made me realize the complexity of managing Java objects with relational database data.

The provided exercise was initially in poor condition, with incorrect SQL queries and names following the school's DSL. 
The code did not follow Java coding conventions and contained several inconsistencies.

But at least, this gives a glimpse of what development is in the real world, where you have to deal with code that was made by a developer in a hurry. 

Finally, It was a pleasure to learn by doing this exercise. 
