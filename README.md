# SQLSI
SQLSI() is a model transformation function that rewrites SQL queries into
"authorized" SQL queries, i.e. into queries that "enforce" the given FGAC-policy at database level.

## Introduction
This open-source project is intended for readers of our manuscripts:
- Model-based characterization of fine-grained access control authorization for SQL queries.
- A model-driven approach for enforcing fine-grained access control for SQL queries.

## About
The following UML Component Diagram captures the design of SQLSI.
![alt text](https://github.com/SE-at-VGU/SQLSI/blob/SQLSI-fdse2020-v1/SQLSI.png?raw=true)

SQLSI takes three inputs: 
- a data model, 
- a security model,
- and a SQL-select statement.

SQLSI returns three outputs, namely, 
- the SQL database schema (correspond to the given data model), 
- the SQL-authorization functions (correspond to the given security model), 
- and the SQL secure stored procedure (correspond to the given SQL-select statement).

### Data Model
The data model is input in SQLSI in JSON-format. For example,
```
[
	{
		"class": "Lecturer",
		"attributes": [
			{
				"name": "email",
				"type": "String"
			}
		],
		"ends": [
			{
				"association": "Enrollment",
				"name": "students",
				"target": "Student",
				"opp": "lecturers",
				"mult": "*"
			}
		]
	},
	{
		"class": "Student",
		"attributes": [
			{
				"name": "email",
				"type": "String"
			}
		],
		"ends": [
			{
				"association": "Enrollment",
				"name": "lecturers",
				"target": "Lecturer",
				"opp": "students",
				"mult": "*"
			}
		]
	}
]
```
The mapping from the data model to the SQL database schema is implemented in our Java component DMTranslator.

### Security Model
The security model is input in SQLSI in JSON-format. For example,
```
[
	{
		"roles": [
			"Lecturer"
		],
		"actions": [
			"read"
		],
		"resources": [
			{
				"association": "Enrollment"
			}
		],
		"auth": [
			{
				"ocl": "klecturers = kcaller",
				"sql": "klecturers = kcaller"
			},
			{
				"ocl": "kcaller.students->exists(s|s=kstudents)",
				"sql": "EXISTS (SELECT 1 FROM Enrollment WHERE lecturers = kcaller AND kstudents = students)"
			}
		]
	},
	{
		"roles": [
			"Lecturer"
		],
		"actions": [
			"read"
		],
		"resources": [
			{
				"entity": "Student",
				"attribute": "email"
			}
		],
		"auth": [
			{
				"ocl": "kcaller.students->exists(s|s = kself)",
				"sql": "EXISTS (SELECT 1 FROM Enrollment WHERE lecturers = kcaller AND kself = students)"
			}
		]
	},
	{
		"roles": [
			"Lecturer"
		],
		"actions": [
			"read"
		],
		"resources": [
			{
				"entity": "Lecturer",
				"attribute": "email"
			}
		],
		"auth": [
			{
				"ocl": "kcaller = kself",
				"sql": "kcaller = kself"
			},
			{
				"ocl": "kcaller.students->exists(s|s.lecturers->exists(l|l=kself))",
				"sql": "EXISTS (SELECT 1 FROM Enrollment e1 JOIN Enrollment e2 ON e1.students = e2.students WHERE e1.lecturers = kcaller AND e2.lecturers = kself)"
			}
		]
	}
] 
```
The mapping from authorization policies in the security model to SQL-authorization functions is implemented in our Java component SMTranslator (which is included in this project).

### SQL Security Injector
Finally, the mapping from a SQL-query to the SQL secure stored procedure is implemented in our Java component QryTranslator (which is included in this project).

## Quick Guideline
Interested readers can clone our project (and related submodules) using ```git clone```:
```
git clone https://github.com/SE-at-VGU/SQLSI.git
git submodule update --init --recursive
```
SQLSI provides the following features:
```java
SqlSI sqlSI = new SqlSI();
```
### Generate SQL database schema file from a data model file.
```java
sqlSI.setUpDataModelFromURL( <dataModelInputURI> );
sqlSI.generateDBSchema( <sqlSchemaOutputURI> , <schemaName> );
```
### Generate SQL-authorization functions file from a data model file and a security model file.
```java
sqlSI.setUpDataModelFromURL( <dataModelInputURI> );
sqlSI.setUpSecurityModelFromURL( <policyModelInputURI> );
sqlSI.generateSQLAuthFunctions( <sqlAuthFunctionOutputURI> );
```
### Generate SQL secure stored procedure from a data model file, a security model file and an SQL-query.
Return SQL stored procedure in a file:
```java
sqlSI.setUpDataModelFromURL( <dataModelInputURI> );
sqlSI.setUpSecurityModelFromURL( <policyModelInputURI> );
sqlSI.generateSQLSecureQuery( <SQLQuery> , <sqlStoredProcedureOutputURI> );
```
or as a string:
```java
sqlSI.setUpDataModelFromURL( <dataModelInputURI> );
sqlSI.setUpSecurityModelFromURL( <policyModelInputURI> );
sqlSI.getSecQuery( <SQLQuery> );
```
