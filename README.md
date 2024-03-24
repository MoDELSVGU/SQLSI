# SQLSI
`SQLSI` is a Java application that rewrites SQL queries into security-aware ones, i.e. "enforcing" some predefined Fine-Grained Access Control (FGAC) policy.

## Introduction
test
This open-source project is intended for readers of our papers:
- Model-based characterization of fine-grained access control authorization for SQL queries. [paper](http://www.jot.fm/contents/issue_2020_03/article15.html)
- A model-driven approach for enforcing fine-grained access control for SQL queries. [paper](https://link.springer.com/article/10.1007/s42979-021-00712-7)

## About

SQLSI takes three inputs: 
- a data model, 
- a security model,
- and a SQL-select statement.

SQLSI returns three outputs, namely, 
- the generated SQL database schema (correspond to the given data model), 
- the generated SQL-authorization functions (correspond to the given security model) and,
- the generated SQL secure stored procedure (correspond to the given SQL-select statement).

## Quick Guideline
Interested readers can clone our project here 
```
git clone https://github.com/MoDELSVGU/SQLSI.git
git checkout main
```
or import it directly using Maven:
```
<dependency>
  <groupId>io.github.modelsvgu</groupId>
  <artifactId>sqlsi</artifactId>
  <version>[1.0.0,)</version>
</dependency>
```

The following snippet showcases the usage of `SQLSI`:

```java
SqlSI myExec = new SqlSI(); // Initialize SQLSI component
myExec.setDataModel(<datamodel_url>); // Setting (parsing) DataModel context
myExec.setSecurityModel(<securitymodel_url>); // Setting (parsing) SecurityModel context
```
#### Generate SQL database schema file from a data model file.
```java
/* (1). To generate MySQL database schema: */
final String schemaURL = "<schema_destination_url>"; 
myExec.SqlSIGenDatabase(schemaURL); 
```
#### Generate SQL-authorization functions file from a data model file and a security model file.
```java
/* 2). To generate MySQL authorization checks: */
final String authFuncURL = "<policy_destination_url>";
myExec.SqlSIGenAuthFunc(authFuncURL);
```
#### Generate SQL secure stored procedure from a data model file, a security model file and an SQL-query.

```java
/* (3). To generate MySQL secured stored-procedure */
final String queryProcURL = "<stored_procedure_url>";
final String statement = "<an_sql_select_statement>";
myExec.SqlSIGenSecQuery(queryProcURL, statement);
```
