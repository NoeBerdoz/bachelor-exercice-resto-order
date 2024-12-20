# Data Mapper to Hibernate exercise

The first part of the exercise is about implementing a Data Mapper pattern in Java.
It can be found in the branch [data-mapper-implementation](https://github.com/NoeBerdoz/bachelor-exercice-resto-order/tree/data-mapper-implementation)

The second part of the exercise is present on the main branch and is about implementing Hibernate instead of using the Data Mapper pattern. 

## Setup

To run this project, you need to rename and modify the `persistence.xml.exemple` file to `persistence.xml` and fill in your database connection details.

Make sure to replace the placeholders with your actual database details.

Make sure to install the dependencies present in `pom.xml`.

By default, the logs are disabled for a smooth usage of the app, if logs are needed, make sure to modify this line in the Main class [Logger.getLogger("org.hibernate").setLevel(Level.OFF);](https://github.com/NoeBerdoz/bachelor-exercice-resto-order/blob/acaec933d0e9d803929b02b83a74207c58665745/src/main/java/ch/hearc/ig/orderresto/application/Main.java#L17).

Fake data with Restaurants, Products and Customers will be inserted in the database when the application starts.

This project was made using Java 21.

## Conclusion

Both parts of the exercise highlighted the complexity of managing Java objects with relational database data and demonstrated how a well-designed ORM framework like Hibernate can significantly simplify such tasks in a project.

The provided exercise was initially in poor condition, with incorrect SQL queries and names following the school's DSL. 
The code did not follow Java coding conventions and contained several inconsistencies.

But at least, this gives a glimpse of what development is in the real world, where you have to deal with code that was made by a developer in a hurry. 

Finally, It was a pleasure to learn by doing this exercise. 
