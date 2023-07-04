# SQLSI
`SQLSI` is a Java application that rewrites SQL queries into security-aware ones, i.e. "enforcing" predefined Fine-Grained Access Control (FGAC) policy.

***(Jul, 2023) This branch (`models23`) is preserved for submitting working artifacts for the ACM/IEEE 26th International Conference on Model-Driven Engineering Languages and Systems (MODELS) 2023.***

## I. Introduction
This open-source project is intended for readers of our papers:
- An extended model-based characterization of fine-grained access control for SQL queries. [paper](tbd)
- For architecture design, see [here](https://github.com/MoDELSVGU/SQLSI/wiki/Architecture-design).

## II. About

SQLSI takes three inputs: 
- a data model, 
- a security model,
- and a SQL-select statement.

SQLSI returns three outputs, namely, 
- the generated SQL database schema (corresponding to the given data model), 
- the generated SQL-authorization functions (corresponding to the given security model) and,
- the generated SQL secure stored procedure (corresponding to the given SQL-select statement).

## III. Quick Guideline
Interested readers can clone our project here 
```bash
git clone https://github.com/MoDELSVGU/SQLSI.git
cd SQLSI
git checkout models23
```

Users can either call it as a standalone Java application or extend the implementation.

### III.A. Continuous development
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

### III.B. Standalone application

#### Requirements
- (required) Maven 3 and Java 1.8 (or higher).

#### How to bulid the standalone application
```bash
cd SQLSI
mvn clean install
```
When it is done, the executable jar (i.e., `sqlsi-1.0.3-ASC.jar`) is stored in `target` subdirectory along with the libraries on which it depends.

Copy the datamodel and securitymodel into this `target` directory.

To execute it, simply invoke the following command:
```bash
java -jar sqlsi-1.0.3-ASC.jar <datamodel_url> <securitymodel_url> <SQLquery>
```
in which:
- <datamodel_url> refers to the url of the datamodel, e.g., `voting_dm`
- <securitymodel_url> refers to the url of the securitymodel, e.g., `voting_sm`
- <SQLquery> refers to the SQL query, e.g., `SELECT value FROM Vote`

## IV. Case study: Voting system

The Voting Management System is a basic application designed for managing sensitive information related to elections.
It handles data regarding voters (such as name and social security number), 
elections (including descriptions) and votes (consisting of value, timestamp) cast by voters in elections.

### IV.A. Data model

For detailed information about the data model, please refer to the manuscript.

The datamodel primarily consists of three classes: two classes (`Election` and `Voter`) and one association class (`Vote`).
  - `Voter` class contains fields for `name` and `ssn` while `Election` has a field for `description`.
  - `Vote` class represents the association between `Voter` and `Election`, and includes an additional field `value` for storing the vote result.

Example of the aforementioned datamodel can be found [here](https://github.com/MoDELSVGU/SQLSI/blob/models23/resources/scenario1/voting_dm.json).

### IV.B. Security model

For detailed information about the security model, please refer to the manuscript.

The security model defines the following rules:
  - There is a single role in this application, namely, User.
  - A User can read (i) the description of an election, (ii) the name of the voter, and (iii) the value of the vote.
  - A User can only read their own social security number.
  - For each election, every User can know the voters who participated in the election.
  - For each election, every User can know the votes that were cast in the election.
  - For each vote, only the voter who cast the vote can know who cast it.

Example of the aforementioned securitymodel can be found [here](https://github.com/MoDELSVGU/SQLSI/blob/models23/resources/scenario1/voting_sm.json).

### IV.C. Queries

Consider the following queries:

#### Query#0: Query all votes' value.
```sql
SELECT value FROM Vote
```

#### Query#1: Query all votes' value of a user with id as `Alice`.
```sql
SELECT value FROM Vote WHERE voters = 'Alice'
```

#### Query#2: Query the elections in which `Bob` participated.
```sql
SELECT elections FROM Vote WHERE voters = 'Bob'
```

#### Query#3: Query all votes' value of the election id `Election_2023`.
```sql
SELECT value FROM Vote WHERE elections = 'Spain2023'
```

#### Query#4: Query the votes' value of the elections in which `Bob` participated.
```sql
SELECT value FROM Vote JOIN (SELECT Voter_id FROM Voter WHERE name = 'Bob') AS TEMP ON voters = TEMP.Voter_id
```

### IV.D. Scenario:
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

To replicate this scenario, please follow these instructions:
- Clone the project (see Section III).
- Build JAR file (see Section III.B.).
- Copy all files in `resources\scenario1` into the `target` folder.
- Stay in `target` and execute JAR file
```
java -jar sqlsi-1.0.3-ASC.jar "voting_dm" "voting_sm" <query>
```
in which `<query>` can be chosen from the queries above (see IV.C.).
- Source the SQL artifacts into the MySQL databases in the following order:
  - `mydb.sql`
  - `myfunc.sql`
  - `myquery.sql`
  - `scenario1.sql`
- Run the stored-procedure in MySQL server and observe the result:
```sql
call secquery(<user>, <role>);
```
in which:
  - `<user>` can be `'Alice'`, `'Bob'`, or `'Charlie'`
  - `<role>` can be `'Voter'`.

