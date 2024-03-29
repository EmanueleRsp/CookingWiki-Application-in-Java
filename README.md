# CookingWiki: share and find kitchen recipes - Application backend/frontend development

[![GitHub Release](https://img.shields.io/github/release/EmanueleRsp/CookingWiki-Application-in-Java?style=flat)](https://github.com/EmanueleRsp/CookingWiki-Application-in-Java/releases) [![GitHub license](https://img.shields.io/github/license/EmanueleRsp/CookingWiki-Application-in-Java?style=flat)](https://github.com/EmanueleRsp/CookingWiki-Application-in-Java/blob/main/LICENSE) 
[![Stars](https://img.shields.io/github/stars/EmanueleRsp/CookingWiki-Application-in-Java?style=social&label=Stars)](https://github.com/EmanueleRsp/CookingWiki-Application-in-Java/stargazers) 

The project consists in the creation of a **distributed Java application** that is composed of a **graphical interface** built with JavaFX and by a **service** through which the application data is managed. Application data is stored in a mySQL DBMS database.

- [CookingWiki: share and find kitchen recipes - Application backend/frontend development](#cookingwiki-share-and-find-kitchen-recipes---application-backendfrontend-development)
  - [Documentation](#documentation)
  - [Project structure](#project-structure)
  - [How to run the application](#how-to-run-the-application)
  - [Final evaluation and comments](#final-evaluation-and-comments)

For any issue or question, please don't hesitate to contact me!

## Documentation

> _This project was developed during "Advanced Programming" course for the Bachelor's degree in Computer Engineering at the University of Pisa, so inner workings and implementation details are described in **italian**._

The main documentation of the project is available [here](/docs/Presentazione.pdf). Unfortunately, due to lack of time, the documentation is **not so complete** and detailed as it should be. However, it provides a presentation of the application from the **point of view of the user**, to be able to appreciate its main features.

If you want to check the **assignment** for the project, you can find them in the pdf file [here](/docs/Specifiche.pdf).


## Project structure

The project is structured in the following way:
- **`CookingWikiClient/`** client application folder;
- **`CookingWikiServer/`** server application folder; 
- **`docs/`** folder containing the documentation of the project;
- **`zip/`** folder containing the two projects in zip format;  
- **`README.md`** the file you are reading right now :).


## How to run the application

You can download the latest zip file released from [here](https://github.com/EmanueleRsp/CookingWiki-Application-in-Java/releases).

The release includes both client and server _zip files_: **import** them in your favourite IDE (such as _Apache NetBeans IDE_).

The application uses a DBMS MySQL database to store data, so you need to **install** it on your machine if you don't have it yet. You can find the installation guide [here](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/). The project **database connection** is configured as follows:
```java

// Database connection parameters
String dbUrl = "jdbc:mysql://localhost:3306/cookingWiki";

// Database credentials
String username = "root";
String password = "root";

```
The application will use a database called `cookingWiki` and will try to connect to it using the default port `3306` with the user `root` and the password `root`. If you want to change these parameters, you can do it in the code.

Finally, you can **run** the two projects: **the server first, then the client**. 

## Final evaluation and comments

The project was evaluated with a **score of 30/30 with honours**.

The professor specially appreciated the **quality of the code** and the **large number of features** implemented in the application. He also appreciated the **creativity** and the **effort** put into the project.

The only negative notes were the **lack of documentation** and the **file organization**, because the source files are contained in the same directory and so it's not easy to manage them.
