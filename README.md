# SQLSI
`SQLSI` is a Java application that rewrites SQL queries into security-aware ones, i.e. "enforcing" some predefined Fine-Grained Access Control (FGAC) policy.

## Introduction
This open-source project is intended for readers of our papers:
- Model-based characterization of fine-grained access control authorization for SQL queries. [paper](http://www.jot.fm/contents/issue_2020_03/article15.html)
- A model-driven approach for enforcing fine-grained access control for SQL queries. [paper](https://link.springer.com/article/10.1007/s42979-021-00712-7)

## About

SQLSI takes three inputs: 
- a data model, 
- a security model,
- and a SQL-select statement.

SQLSI returns three outputs, namely, 
- the SQL database schema (corresponding to the given data model), 
- the SQL-authorization functions (corresponding to the given security model), 
- and the SQL secure stored procedure (corresponding to the given SQL-select statement).

More on the implementation design, please have a look at its [component diagram](https://github.com/SE-at-VGU/SQLSI/blob/SQLSI-fdse2020-v1/SQLSI.png?raw=true).

## Quick Guideline
Interested readers can clone our project (and related submodules) as follows:

```
git clone https://github.com/SE-at-VGU/SQLSI.git
cd SQLSI
git submodule update --init --recursive
```

The following snippet showcases the usage of `SQLSI`:

```java
SqlSI myExec = new SqlSI(); // Initialize SQLSI component object
myExec.setDataModel(<datamodel_url>); // set DataModel
myExec.setSecurityModel(<securitymodel_url>); // set SecurityModel
```
#### Generate SQL database schema file from a data model file.
```java
/* (1). To generate database schema: */
final String schemaURL = "<schema_destination_url>"; 
myExec.SqlSIGenDatabase(schemaURL); 
```
#### Generate SQL-authorization functions file from a data model file and a security model file.
```java
/* 2). To generate database access control policy: */
final String authFuncURL = "<policy_destination_url>";
myExec.SqlSIGenAuthFunc(authFuncURL);
```
#### Generate SQL secure stored procedure from a data model file, a security model file and an SQL-query.

```java
/* (3). To generate database secured stored-procedure */
final String queryProcURL = "<stored_procedure_url>";
final String statement = "<an_sql_select_statement>";
myExec.SqlSIGenSecQuery(queryProcURL, statement);
```
