DROP FUNCTION IF EXISTS checkAuth;
DELIMITER //
CREATE FUNCTION checkAuth(origWhere INT, authWhere INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (origWhere = 1 and authWhere = 1)
THEN RETURN(1);
ELSE IF (authWhere = 0)
THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Unauthorized access';
RETURN (0);
ELSE RETURN (0);
END IF;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_name_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Employee_name_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kcaller AS ref_kcaller, TEMP_LEFT.ref_kself AS ref_kself FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself, TEMP_RIGHT.ref_kcaller AS ref_kcaller FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Employee' AS type) AS TEMP_RIGHT) AS TEMP_LEFT LEFT JOIN (SELECT TEMP_LEFT.res < TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself FROM (SELECT TEMP_OBJ.val AS val, 'Integer' AS type, Employee.salary AS res, TEMP_OBJ.ref_kself AS ref_kself FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_OBJ LEFT JOIN Employee ON TEMP_OBJ.ref_kself = Employee.Employee_id AND TEMP_OBJ.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 60000 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT ON TEMP_LEFT.ref_kself = TEMP_RIGHT.ref_kself) AS TEMP_LEFT LEFT JOIN (SELECT TEMP_LEFT.res < TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself FROM (SELECT TEMP_OBJ.val AS val, 'Integer' AS type, Employee.age AS res, TEMP_OBJ.ref_kself AS ref_kself FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_OBJ LEFT JOIN Employee ON TEMP_OBJ.ref_kself = Employee.Employee_id AND TEMP_OBJ.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 30 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT ON TEMP_LEFT.ref_kself = TEMP_RIGHT.ref_kself) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_name_Default;
DELIMITER //
CREATE FUNCTION auth_read_Employee_name_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Employee' AS type) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_name;
DELIMITER //
CREATE FUNCTION auth_read_Employee_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Admin')
THEN IF auth_read_Employee_name_Admin(kself, kcaller)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
ELSE
IF auth_read_Employee_name_Default(kself, kcaller)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_age_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Employee_age_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself, TEMP_RIGHT.ref_kcaller AS ref_kcaller FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Employee' AS type) AS TEMP_RIGHT) AS TEMP_LEFT LEFT JOIN (SELECT TEMP_LEFT.res < TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself FROM (SELECT TEMP_OBJ.val AS val, 'Integer' AS type, Employee.age AS res, TEMP_OBJ.ref_kself AS ref_kself FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_OBJ LEFT JOIN Employee ON TEMP_OBJ.ref_kself = Employee.Employee_id AND TEMP_OBJ.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 30 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT ON TEMP_LEFT.ref_kself = TEMP_RIGHT.ref_kself) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_age_Default;
DELIMITER //
CREATE FUNCTION auth_read_Employee_age_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Employee' AS type) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_age;
DELIMITER //
CREATE FUNCTION auth_read_Employee_age(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Admin')
THEN IF auth_read_Employee_age_Admin(kself, kcaller)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
ELSE
IF auth_read_Employee_age_Default(kself, kcaller)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_salary_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Employee_salary_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself, TEMP_RIGHT.ref_kcaller AS ref_kcaller FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Employee' AS type) AS TEMP_RIGHT) AS TEMP_LEFT LEFT JOIN (SELECT TEMP_LEFT.res < TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_kself AS ref_kself FROM (SELECT TEMP_OBJ.val AS val, 'Integer' AS type, Employee.salary AS res, TEMP_OBJ.ref_kself AS ref_kself FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_OBJ LEFT JOIN Employee ON TEMP_OBJ.ref_kself = Employee.Employee_id AND TEMP_OBJ.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 60000 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT ON TEMP_LEFT.ref_kself = TEMP_RIGHT.ref_kself) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_salary_Default;
DELIMITER //
CREATE FUNCTION auth_read_Employee_salary_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, 'Boolean' AS type, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Employee' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Employee' AS type) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_salary;
DELIMITER //
CREATE FUNCTION auth_read_Employee_salary(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Admin')
THEN IF auth_read_Employee_salary_Admin(kself, kcaller)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
ELSE
IF auth_read_Employee_salary_Default(kself, kcaller)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END IF;
END //
DELIMITER ;

