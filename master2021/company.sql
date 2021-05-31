DROP DATABASE IF EXISTS CompanyDB;
CREATE DATABASE CompanyDB 
 DEFAULT CHARACTER SET utf8 
 DEFAULT COLLATE utf8_general_ci;
USE CompanyDB;
CREATE TABLE Employee (Employee_id VARCHAR (100) NOT NULL PRIMARY KEY, salary INT (11) , employmentLevel INT (11) , email VARCHAR (100) , name VARCHAR (100) );
CREATE TABLE Supervision (supervisees VARCHAR (100), supervisors VARCHAR (100));
ALTER TABLE Supervision ADD FOREIGN KEY (supervisees) REFERENCES Employee (Employee_id), ADD FOREIGN KEY (supervisors) REFERENCES Employee (Employee_id);
