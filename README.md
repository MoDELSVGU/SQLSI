# SQLSI
***(Jul, 2023) This branch (`models23`) is preserved for submitting working artifacts for the ACM/IEEE 26th International Conference on Model-Driven Engineering Languages and Systems (MODELS) 2023.***

`SQLSI` is a Java application that rewrites SQL queries into security-aware ones, i.e. "enforcing" predefined Fine-Grained Access Control (FGAC) policy.

## Introduction
This open-source project is intended for readers of our papers:
- An extended model-based characterization of fine-grained access control for SQL queries. [paper](tbd)
- For architecture design, see [here]().

## About

SQLSI takes three inputs: 
- a data model, 
- a security model,
- and a SQL-select statement.

SQLSI returns three outputs, namely, 
- the generated SQL database schema (corresponding to the given data model), 
- the generated SQL-authorization functions (corresponding to the given security model) and,
- the generated SQL secure stored procedure (corresponding to the given SQL-select statement).

## Quick Guideline
Interested readers can clone our project here 
```bash
git clone https://github.com/MoDELSVGU/SQLSI.git
git checkout models23
```

The following snippet demonstrates the usage of `SQLSI`:
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

## Case study: Voting system

The Voting Management System is a basic application designed for managing sensitive information related to elections.
It handles data regarding voters (such as name and social security number), 
elections (including descriptions) and votes (consisting of value, timestamp) cast by voters in elections.

### Data model

For detailed information about the data model, please refer to the following [link]().

The datamodel primarily consists of three classes: two classes (`Election` and `Voter`) and one association class (`Vote`).
  - `Voter` class contains fields for `name` and `ssn` while `Election` has a field for `description`.
  - `Vote` class represents the association between `Voter` and `Election`, and includes an additional field `value` for storing the vote result.

### Security model

For detailed information about the security model, please refer to the following [link]().

The security model defines the following rules:
  - There is a single role in this application, namely, User.
  - A User can read (i) the description of an election, (ii) the name of the voter, and (iii) the value of the vote.
  - A User can only read their own social security number.
  - For each election, every User can know the voters who participated in the election.
  - For each election, every User can know the votes that were cast in the election.
  - For each vote, only the voter who cast the vote can know who cast it.

### Query model

Consider the following queries:

#### Query#0: Query all votes' value.
```sql
SELECT value FROM Vote;
```

#### Query#1: Query all votes' value of a user with id as `Alice`.
```sql
SELECT value FROM Vote WHERE voters = 'Alice';
```

#### Query#2: Query the elections in which `Bob` participated.
```sql
SELECT elections FROM Vote WHERE voters = 'Bob';
```

#### Query#3: Query all votes' value of the election id `Election_2023`.
```sql
SELECT value FROM Vote WHERE elections = 'Election_2023';
```

#### Query#4: Query the votes' value of the elections in which `Bob` participated.
```sql
SELECT value FROM Vote
JOIN (SELECT Voter_id FROM Voter WHERE name = 'Bob') AS TEMP
ON voters = TEMP.Voter_id
```

### Scenario 1:
```
Voter
+----------+---------+------------+
| Voter_id | name    | ssn        |
+----------+---------+------------+
| Alice    | Alice   | Alicessn   |
| Bob      | Bob     | Bobssn     |
| Charlie  | Charlie | Charliessn |
+----------+---------+------------+

Election
+-------------+----------------------+
| Election_id | description          |
+-------------+----------------------+
| Spain2023   | Spain2023            |
| Swiss2023-1 | Swiss vote quarter 1 |
+-------------+----------------------+

Vote
+---------+-------------+--------+-------+
| Vote_id | elections   | voters | value |
+---------+-------------+--------+-------+
| 1       | Spain2023   | Alice  |     3 |
| 2       | Swiss2023-1 | Alice  |     4 |
| 3       | Swiss2023-1 | Bob    |     1 |
+---------+-------------+--------+-------+
```

### Scenario 2:

