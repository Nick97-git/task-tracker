# Task Tracker
# Table of Contents
* [Project purpose](#purpose)
* [Technologies stack](#stack)
* [Launch project](#launch)
* [Launch project(using Docker)](#docker-launch)
* [Test project](#test)
* [Author](#author)

# <a name="purpose"></a>Project purpose
Managing tasks through API
<hr>
Without being authenticated you can register and login. Here is the list of what you can do being authenticated:

1. Update user's data
2. Delete user
3. Get user data
4. Get list of all users
5. Create task
6. Update task's data
7. Update task's status
8. Delete task
9. Get list of tasks(filtered by status and sorted by new/old users)
10. Update task's user
<hr>

# <a name="stack"></a>Technologies stack
* Spring Boot
* Spring Security(JWT Authentication)
* Spring Data JPA
* MySQL as main database
* H2 as test database
* Swagger
* Docker
* JUnit5
* Mockito 
* Lombok
* Sl4j
<hr>

# <a name="launch"></a>Launch project

1. Open the project in your IDE.

2. Add Java SDK 11 or above in Project Structure.

3. Install MySQL if you don't have it and start MySQLWorkbench.

4. At src\main\resources\application.properties use your username (spring.datasource.username) 
and password (spring.datasource.password) for MySQL Server to create a connection.

5. Change a path to log file at src\main\resources\application.properties if you want - logging.file.name.

6. Run the project.

# <a name = "docker-launch"></a>Launch project(using Docker)

1. Install Docker Desktop and register on DockerHub

2. Complete paragraphs of "Launch project" section from 1 to 5.

3. At docker-compose.yml use your username (MYSQL_USER) 
password (MYSQL_PASSWORD) and root password(MYSQL_ROOT_PASSWORD) for MySQL Server to create a connection.

4. Run next commands in terminal from root directory: 
* mvn clean package
* docker-compose up --build

# <a name = "test"></a>Test project
For testing this API you can download Postman or another such an analogue. 

There are test data that you can use.
There are one user already registered with both ADMIN and USER roles (email = "email@ukr.net", password = "1234") and 20 users with USER role (email = "email(number from 0 to 19)@ukr.net", password = "1234"). Also, there are prepared tasks with title as title + (number from 0 to 19) and user with email email@ukr.net as their creator. All new users automatically get USER role. You can change these test data in InjectDataController if you want.

All available endpoints you can see on http://localhost:8080/swagger-ui.html. 

For authorization, you must add a new header, where Authorization is key and Bearer token is value, where token - value that you will get after login query to the system in the response.

# <a name="author"></a>Author

Mykyta Arkhanhelskyi: https://github.com/Nick97-git
