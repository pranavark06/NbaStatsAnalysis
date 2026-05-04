<p align="center">
<img src="https://github.com/thespcrewroy/4370/blob/main/assets/logo.jpeg" alt="Logo" width="400" height="400" />
<p align="center">
<a href="https://github.com/tylernlyons/4370/graphs/framework"><img alt="MySQL" src="https://img.shields.io/badge/MySQL-v8.0-red.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/4370/graphs/framework"><img alt="Java" src="https://img.shields.io/badge/Java-v21.0.8-green.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/4370/graphs/framework"><img alt="Java" src="https://img.shields.io/badge/Docker-v28.3.2-blue.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/4370/graphs/contributors"><img alt="MySQL" src="https://img.shields.io/badge/contributers-5-pink.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/4370/graphs/framework"><img alt="Pulls" src="https://img.shields.io/badge/pull%20requests-1-yellow.svg" height="20"/></a>
<a href="https://github.com/tylernlyons/4370/graphs/framework"><img alt="Issues" src="https://img.shields.io/badge/issues-0-orange.svg" height="20"/></a>
<a href="http://creativecommons.org/licenses/by-nc-nd/4.0/"><img alt="License: CC BY-NC-ND 4.0" src="https://img.shields.io/badge/License-All%20Rights%20Reserved-lightgrey.svg" height="20"/></a>
</p>


<p align="center">
  <b>A sports analytics platform for exploring NBA games, team rankings, draft history, and player performance data.</b></br>
  <sub>Use this platform to search basketball data, review game results, compare team standings, and explore draft records through database-driven pages. Built for a clean and practical sports analysis experience, this project highlights full-stack development with real-world features like authentication, searchable records, detailed game views, rankings, SQL joins, aggregation queries, and MySQL-backed data..<sub>
</p>

<br />

<div align="center">
  [PLACEHOLDER DEMO]
  <p><b>CSCI x370 Microblogging Platform Demo</b></p>
</div>

- **Simple:** A clean interface for browsing games, rankings, draft records, and player statistics.
- **Powerful:** Feature-rich functionality: login, signup, game details, team rankings, draft exploration, etc.
- **Practical:** SaaS project that demonstrates real-world Spring Boot, JDBC, MySQL, and web application design.

<details>
<summary>📖 Table of Contents</summary>
<br />

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Thanks to all our Contributors!](#thanks-to-all-our-contributors)
- [Installation](#installation)
  - [Requirements](#requirements)
  - [Installing Docker](#installing-docker)
  - [Docker Setup](#docker-setup)
  - [Running the Application](#running-the-application)
  - [Using the Application](#using-the-application)
- [NonTrivial New Feature - Trending Page](#nontrivial-new-feature---trending-page)
  - [What the page shows](#what-the-page-shows)
  - [Request flow and behavior](#request-flow-and-behavior)
  - [Implementation locations](#implementation-locations)
  - [SQL used by the feature](#sql-used-by-the-feature)
  - [Why this is non-trivial](#why-this-is-non-trivial)
- [ER Diagram](#er-diagram)
- [Have Questions?](#have-questions)
</details>

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#team)

## Team Members (Group 16)
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/neil.png" width="160"><br>
      Neil Correia
    </td>
    <td align="center">
      <img src="assets/pranav.jpeg" width="160"><br>
      Pranav Arkatala
    </td>
    <td align="center">
      <img src="assets/sriram.png" width="160"><br>
      Sriram Surisetty
    </td>
    <td align="center">
      <img src="assets/swapnil2.png" width="160"><br>
      Swapnil Roy
    </td>
    <td align="center">
      <img src="assets/tyler.png" width="160"><br>
      Tyler Lyons
    </td>
  </tr>
</table>

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#contributors)

## Contributions

|     Contribution         |    Member Name     |                                    Description                                                                       |
| ------------------------ | ------------------ | -------------------------------------------------------------------------------------------------------------------- |
| prelim.pdf               | `TBD`              | `TBD`                                                                                                                |
| prelim.pdf               | `Swap R.`          | `Preliminary ER Diagram`                                                                                             |
| db_design.pdf            | `Swap R.`          | `ER Diagram`                                                                                                         |
| db_design.pdf            | `TBD`              | `TBD`                                                                                                                |
| ddl.sql                  | `Tyler L.`         | `Digested data by filtering raw kaggle data and seeded the database with SQL DDL`                                    |
| queries.sql              | `TBD`              | `TBD`                                                                                                                |
| data.sql                 | `TBD`              | `TBD`                                                                                                                |
| datasource.txt           | `TBD`              | `TBD`                                                                                                                |
| perf.txt                 | `Neil C.`          | `TBD`                                                                                                                |
| security.txt             | `Tyler L.`         | `Implemented user login using hashed passwords.`                                                                     |
| security.txt             | `Tyler L.`         | `Documented user login best OWASP security practices to prevent against sql injection.`                              |
| Readme.txt               | `Tyler L.`         | `Team name and contributions, database name sections`                                                                |
| Readme.txt               | `Swap R.`          | `Technology, 3rd Party, and setup instructions`                                                                      |
| F1: Draft Explore        | `Tyler L.`         | `Built a draft search page where users can look up draft picks`                                                      |
| F2: View Games           | `Pranav A.`        | `Built a browsing game results page where users can see all the individual player and team game stats for each game` |
| F3: ML/DS Feature        | `Swap R.`          | `TBD`                                                                                                                |
| F4: TBD                  | `Neil C.`          | `TBD`                                                                                                                |
| F5: TBD                  | `SriRam S.`        | `TBD`                                                                                                                |

<p align="center">
  <img src="https://github.com/thespcrewroy/MicroBloggr/blob/main/groupcontributionform.png" alt="Group Contribution Form" width="800" />
</p>

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#dependencies)

## Project Dependencies

### Package Requirements
```
Java (jdk 21.0.8 2025-07-15 LTS) programming language
MySQL 8 database management system
Docker version 28.3.2, build 578ccf6
Apache Maven 3.9.11
```

## Technologies Used
```
Spring Boot
Spring Boot Web
Spring Boot Mustache
Spring Boot JDBC
Spring Security Crypto
MySQL Connector/J
HTML/CSS
```

> [!IMPORTANT]\
> All of the above libraries were provided in Project 2. Thus, no written consent is required.

### Third-Party Libraries Used
```
N/A
```

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#project-structure)

## Project Structure

### Database
- Name: `sports`
- JDBC Connection Username: `root`
- JDBC Connection Password: `mysqlpass`

### Test Users
- `user: jdoe / password: johnsSSPW`
- `user: jsmith password: janesSSPW`
- `user: mjones password: mikesSSPW`

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#running)

## Installation

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

**2. Clone the Git Repo**
```console
git clone https://github.com/tylernlyons/4370
```

**3. Head to the Source Code Folder**
```console
cd  /local/path/to/4370/4370
```

**4. Copy the SQL Files Into the Container**
```console
docker cp ddl.sql mysql-server-x370:/tmp/ddl.sql
docker cp data.sql mysql-server-x370:/tmp/data.sql
```

**5. Open MySQL Inside Docker**
```console
docker exec -it mysql-server-x370 bash
mysql -u root -p
```
Password:
```console
mysqlpass
```

**6. Create/Import the Database** <br>

inside the `mysql>` prompt:
```sql
DROP DATABASE IF EXISTS sports;
CREATE DATABASE sports;
USE sports;

SOURCE /tmp/ddl.sql;
SOURCE /tmp/data.sql;
```

**7. Quick Sanity Check** <br>
```sql
USE sports;
SHOW TABLES;
```

<p align="center">
  <img src="https://github.com/thespcrewroy/4370/blob/main/assets/tables.png" alt="Imported Tables" width="300" 
/>

Added Rows:
* Pranav Arkatala: `player_game_stats`

**8. Exit**
```console
exit
```

### Running the Application

**OSX/Unix**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'
```

**Windows CmdLinex**
```sh
mvn spring-boot:run -D"spring-boot.run.arguments=--server.port=8081"
```

**Windows Powershell**
```shell
mvn spring-boot:run --% -Dspring-boot.run.arguments="--server.port=8081"
```

### Using the Application

Open the browser and navigate to the following URL: ```http://localhost:8081/```<br>

Create an account and login!

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#why-us)
## Why *CourtVision*? - Our Pitch

The project is in the sports analytics domain, specifically focused on NBA basketball data. Basketball analysts and fans often need to search across large amounts of game, team, player, and draft information. However, raw datasets are difficult to explore directly through SQL files or spreadsheets.

The problem this application addresses is making NBA data easier to access, search, compare, and understand through a web interface. Instead of manually querying a database, users can log in and explore meaningful basketball information such as game results, player game statistics, team rankings, and draft history through organized pages backed by a MySQL database.

[SOULUTION + INTERFACE DISCUSSION PLACEHOLDER]

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#er-diagram)
## ER Diagram

[PLACEHOLDER ER DIGRAM]

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#feedback)

## Have Questions?

> [!NOTE]\
> **Email one of the contributors or email Professor Sami Menik. That's it for now. Have a great day!**

* [Sami Menik](mailto:sami.menik@uga.edu)
* [Tyler Lyons](mailto:tnl56757@uga.edu)
* [SriRam SuriSetty](mailto:ss18317@uga.edu)
* [Neil Correia](mailto:nac55642@uga.edu)
* [Swapnil Roy](mailto:sr42946@uga.edu)
* [Pranav Arkatala](mailto:pa38192@uga.edu)

> If you stumble upon an issue or have a feature request you are very welcome to open a Github issue or pull request.


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#license)

[![License: All Rights Reserved](https://img.shields.io/badge/License-All%20Rights%20Reserved-lightgrey.svg)](http://creativecommons.org/licenses/by-nc-nd/4.0/)

<small>
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.

<p align="right"><a href="#top">🔼 Back to top</a></p>
</small>

