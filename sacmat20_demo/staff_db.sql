DROP DATABASE IF EXISTS jot2020;
CREATE DATABASE jot2020 
 DEFAULT CHARACTER SET utf8 
 DEFAULT COLLATE utf8_general_ci;
USE jot2020;
CREATE TABLE Employee (Employee_id VARCHAR (100) NOT NULL PRIMARY KEY, name VARCHAR (100) , age INT (11) , salary INT (11) , role VARCHAR (100));
CREATE TABLE Employee_supervisee_supervisor_Employee (supervisee VARCHAR (100), supervisor VARCHAR (100));
ALTER TABLE Employee_supervisee_supervisor_Employee ADD FOREIGN KEY (supervisee) REFERENCES Employee (Employee_id), ADD FOREIGN KEY (supervisor) REFERENCES Employee (Employee_id);
