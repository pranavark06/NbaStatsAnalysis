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

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#team)

## Group 16 Team Members
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/neil.png" width="160"><br>
      Neil
    </td>
    <td align="center">
      <img src="assets/pranav.jpeg" width="160"><br>
      Pranav
    </td>
    <td align="center">
      <img src="assets/sriram.png" width="160"><br>
      Sriram
    </td>
    <td align="center">
      <img src="assets/swapnil.png" width="160"><br>
      Swapnil
    </td>
    <td align="center">
      <img src="assets/tyler.png" width="160"><br>
      Tyler
    </td>
  </tr>
</table>



Database `sports`
Database username: root
Database password: mysqlpass

Test users:
user: jdoe password: johnsSSPW
user: jsmith password: janesSSPW
user: mjones password: mikesSSPW

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#contributors)

## Contributions

|     Contribution         |    Member Name     |                                    Description                                                                       |
| ------------------------ | ------------------ | -------------------------------------------------------------------------------------------------------------------- |
| Login/Signup             | `Tyler L.`         | `Users can sign up and log into the web app with using hashed passwords`                                             |
| Data Digest (ddl.sql)    | `Tyler L.`         | `Filtered raw kaggle data and seeded the database with SQL DDL statements`                                           |
| F1: Draft Explore        | `Tyler L.`         | `Built a draft search page where users can look up draft picks`                                                      |
( Readme.txt               |
| Readme.md                | 'Swapnil R.`       | `Readme.md styling and issue tracking, ensuring deliverables were met`                                               |
| F2: View Games           | `Pranav A.`        | `Built a browsing game results page where users can see all the individual player and team game stats for each game` |

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/aqua.png)](#running)

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

**2. Clone the Git Repo**
```console
git clone https://github.com/tylernlyons/P2_micro_blogging
```

**3. Head to the Project Filder**
```console
cd  /local/path/to/P2_micro_blogging/P2_micro_blogging
```

**4. Copy the SQL Files Into the Container**
```console
docker cp ddl.sql mysql-server-x370:/tmp/ddl.sql
docker cp data.sql mysql-server-x370:/tmp/data.sql
```

**5. Open MySQL Inside Docker**
```console
docker exec -it <container_id> bash
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
* Pranav Artakla: `player_game_stats`

**8. Exit**
```console
exit
```

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

