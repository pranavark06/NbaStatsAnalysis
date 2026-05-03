<p align="center">
<p align="center">
<a href="https://github.com/tylernlyons/MicroBlogger/graphs/framework"><img alt="MySQL" src="https://img.shields.io/badge/MySQL-v8.0-red.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/MicroBlogger/graphs/framework"><img alt="Java" src="https://img.shields.io/badge/Java-v21.0.8-green.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/MicroBlogger/graphs/framework"><img alt="Java" src="https://img.shields.io/badge/Docker-v28.3.2-blue.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/MicroBlogger/graphs/contributors"><img alt="MySQL" src="https://img.shields.io/badge/contributers-5-pink.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/MicroBlogger/graphs/framework"><img alt="Pulls" src="https://img.shields.io/badge/pull%20requests-1-yellow.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/MicroBlogger/graphs/framework"><img alt="Issues" src="https://img.shields.io/badge/issues-0-orange.svg" height="20"/></a>
<a href="http://creativecommons.org/licenses/by-nc-nd/4.0/"><img alt="License: CC BY-NC-ND 4.0" src="https://img.shields.io/badge/License-All%20Rights%20Reserved-lightgrey.svg" height="20"/></a>

</p>

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#contributors)

## Thanks to all our Contributors!

|     Contribution         |    Member Name        |                                    Description                                      |
| ------------------------ | --------------------- | ----------------------------------------------------------------------------------- |
| Login/Signup             | `Tyler Lyons`         | `Users can sign up and log into the web app with using hashed passwords`            |
| Data Digest (ddl.sql)    | `Tyler Lyons`         | `Filtered raw kaggle data and seeded the database with SQL DDL statements`          |
| F1: Draft Explore        | `Tyler Lyons`         | `Built a draft search page where users can look up draft picks`                     |
| F2: View Games           | `Pranav Arkatala`     | `Built a browsing game results page where users can see all the individual player and team game stats for each game` |


Database name: sports
Database username: root
Database password: mysqlpass

Test users:
user: jdoe password: johnsSSPW
user: jsmith password: janesSSPW
user: mjones password: mikesSSPW


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/solar.png)](#running)

## Installation

### Requirements
```
Java (jdk 21.0.8 2025-07-15 LTS) programming language
MySQL 8 database management system
Docker version 28.3.2, build 578ccf6
Apache Maven 3.9.11
```
### Installing Docker
Check out the [Docker installation guide](https://github.com/thespcrewroy/CSCI4370-DatabaseManagement/blob/main/Notes/00-Setup/01-docker-mysql.md)

### Docker Setup
**1. Start MySQL Docker**
```console
docker start mysql-server-x370
```

If you have not created it yet:
```console
docker run --name mysql-server-x370 \
  -v ~/mysql-data-x370:/var/lib/mysql \
  -p 33306:3306 \
  -e MYSQL_ROOT_PASSWORD=mysqlpass \
  -d mysql:8.1.0
```

**2. Check Networking Settings** (should be a mapping from 3306 to 33306 on the tcp port)
```console
docker container inspect <container_id> --format '{{.NetworkSettings.Ports}}'
```

**3. Clone the Git Repo**
```console
git clone https://github.com/tylernlyons/P2_micro_blogging
```

**4. Head to the Project Filder**
```console
cd  /local/path/to/P2_micro_blogging/P2_micro_blogging
```

**5. Make Sure the MySQL Container is Running with the Correct Port Mapping**
```console
docker ps
```
**6. Copy the SQL Files Into the Container**
```console
docker cp \local\path\to\database_setup.sql <container_id>:/tmp/
```

**7. Open MySQL Inside Docker**
```console
docker exec -it <container_id> bash
mysql -u root -p
```
Password:
```console
mysqlpass
```

**8. Create/Import the Database**
```console
mysql -u root -p csx370_mb_platform < /tmp/database_setup.sql
```

**9. Exit**
```console
exit
```

### Running on OSX/Unix
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'
```

### Running on Windows CmdLine
```sh
mvn spring-boot:run -D"spring-boot.run.arguments=--server.port=8081"
```

### Running on Windows PowerShell
```shell
mvn spring-boot:run --% -Dspring-boot.run.arguments="--server.port=8081"
```

### Using the Application

Open the browser and navigate to the following URL: ```http://localhost:8081/```<br>

Create an account and login!


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/solar.png)](#resources)

## External Resources & Leranings

* https://www.w3resource.com/mysql/date-and-time-functions/mysql-date_sub-function.php#google_vignette - Used for showing the trending posts in the given time interval
* https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html - Code would not function on Windows without having to throw exceptions for risky behavior
