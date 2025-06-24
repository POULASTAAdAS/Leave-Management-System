# <h1 align='center'>Leave Management Native Android Application</h1>

> LMS is a online native android application. It has 3 level of user.Permenent or SACT , Department Head , Principal or Headclark. Permenet or SACT teacher can apply for leave and view leave status and department head can also apply for leave and view his or her leave status also can approve or reject premenent or sact teacehr leave request also can download previous leave history or that department. As for Principal he or she can approve or reject all facaltys leave and update any facalty mem leave balance and add or remove facalty members. Also download a detailed leave report.

## <h2  align='center'>Showcase</h2>

<a  href="https://github.com/POULASTAAdAS/Leave-Management-System/blob/main/ss/showcase.svg ">

<img  src="https://github.com/POULASTAAdAS/Leave-Management-System/blob/main/ss/showcase.svg "  alt="ShowCase">

</a>

## Features

- Lavel based email (easy and fast) Authentication
- Fast as easy apply of leave
- Easily accessable leave status
- View Leave balance with one click
- Downloadable Leave Report as pdf with one click
- One click Updatetion of leave balance also auto update on every year
- Easily Add or remove employee
- Assign New Department Head or change existing with one click
- Detailed Profile View
- Update personal details
- Mail notification on every step

## Technology

> Client

- [Kotlin](https://kotlinlang.org/) Programing Language
- [Jetpack Compose](https://developer.android.com/jetpack?gad_source=1&gclid=Cj0KCQiA88a5BhDPARIsAFj595jSVle89CMGPqnq6A0C-V8KNDyNR8K_vGQZzUDgCO00VtoKs555fUsaAtXQEALw_wcB&gclsrc=aw.ds) - Ui Library
- [OkHttp](https://square.github.io/okhttp/) - Http Client to make api Request
- [Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) - A Jetpack compose library to load data as needed
- [Download Manager](https://developer.android.com/reference/kotlin/android/app/DownloadManager) - Efficiantly Download Report as pdf format
- [Room](https://developer.android.com/training/data-storage/room) - SqlLight Database for android
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Dependency Management Tool for Android
  > Server
- [Ktor](https://ktor.io/) - evented I/O for the Server
- [Exposed](https://ktor.io/docs/server-integrate-database.html) - Server side Sql Database
- [koin](https://insert-koin.io/) - Dependency management tool for server
- [gradle.kt](https://docs.gradle.org/current/userguide/kotlin_dsl.html) - the streaming build system

## Installation

1. Clone the repo:

```sh
   git clone https://github.com/POULASTAAdAS/Leave-Management-System.git
```

2. Setup enveroinment variables for the server project:

   > BASE_URL=
   > email=
   > password=
   > sessionEncryptionKey=
   > sessionSecretKey=

3. Run the docker-compose.yml file on [path](https://github.com/POULASTAAdAS/Leave-Management-System/blob/main/LMSServer/docker-compose.yml) this will create databsae lms and nessery tables for more deatils view [the sql file](https://github.com/POULASTAAdAS/Leave-Management-System/blob/main/LMSServer/mysql/init.sql)
   3.1 Change the emails on [the sql file](https://github.com/POULASTAAdAS/Leave-Management-System/blob/main/LMSServer/mysql/init.sql) before runing docker compose
   3.2 See [application.conf](https://github.com/POULASTAAdAS/Leave-Management-System/blob/main/LMSServer/src/main/resources/application.conf) file for setting up the urls and port

```sh
    docker compose up -d
```

> By default the server port is exposed on 8080 and database port on 3311

### This is all for server side setup as for client side

1. Setup the same base url on local.properties file

```sh
    BASE_URL=
```

> And you are good to go

## Preview

# License

```xml
Designed and developed by 2023 Poulastaa Das

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
