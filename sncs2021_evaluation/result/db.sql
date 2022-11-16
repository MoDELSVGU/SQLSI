DROP DATABASE IF EXISTS mydb;
CREATE DATABASE mydb 
 DEFAULT CHARACTER SET utf8 
 DEFAULT COLLATE utf8_general_ci;
USE mydb;
CREATE TABLE Lecturer (Lecturer_id VARCHAR (100) NOT NULL PRIMARY KEY, age INT (11) , name VARCHAR (100) );
CREATE TABLE Student (Student_id VARCHAR (100) NOT NULL PRIMARY KEY, age INT (11) , name VARCHAR (100) );
CREATE TABLE Module (Module_id VARCHAR (100) NOT NULL PRIMARY KEY, code INT (11) );
ALTER TABLE Lecturer ADD COLUMN students INT (11), ADD FOREIGN KEY (students) REFERENCES Student (Student_id);
CREATE TABLE Enrollment_Module (Enrollment_Module_id VARCHAR (100) NOT NULL PRIMARY KEY, module VARCHAR (100), enrollment VARCHAR (100));
ALTER TABLE Enrollment_Module ADD FOREIGN KEY (module) REFERENCES Module (Module_id), ADD FOREIGN KEY (enrollment) REFERENCES Enrollment (Enrollment_id);
CREATE TABLE Enrollment (Enrollment_id VARCHAR (100) NOT NULL PRIMARY KEY, students VARCHAR (100), lecturers VARCHAR (100), name VARCHAR (100) );
ALTER TABLE Enrollment ADD FOREIGN KEY (students) REFERENCES Student (Student_id), ADD FOREIGN KEY (lecturers) REFERENCES Lecturer (Lecturer_id);
