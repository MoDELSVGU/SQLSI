DROP DATABASE IF EXISTS mydb;
CREATE DATABASE mydb 
 DEFAULT CHARACTER SET utf8 
 DEFAULT COLLATE utf8_general_ci;
USE mydb;
CREATE TABLE Lecturer (Lecturer_id VARCHAR (100) NOT NULL PRIMARY KEY, name VARCHAR (100) , age INT (11) );
CREATE TABLE Student (Student_id VARCHAR (100) NOT NULL PRIMARY KEY, name VARCHAR (100) , age INT (11) );
CREATE TABLE Enrollment (lecturers VARCHAR (100), students VARCHAR (100));
ALTER TABLE Enrollment ADD FOREIGN KEY (lecturers) REFERENCES Lecturer (Lecturer_id), ADD FOREIGN KEY (students) REFERENCES Student (Student_id);
