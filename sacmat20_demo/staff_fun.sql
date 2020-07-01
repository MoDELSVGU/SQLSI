DROP FUNCTION IF EXISTS auth_IDENTIFY_Employee;
DELIMITER //
CREATE FUNCTION auth_IDENTIFY_Employee(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF auth_IDENTIFY_Employee_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_IDENTIFY_Employee_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_IDENTIFY_Employee_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_name;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_name(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF auth_READ_Employee_name_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_name_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_name_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_age;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_age(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (role = 'Admin')
THEN IF auth_READ_Employee_age_Admin(caller, self)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
END IF;
IF auth_READ_Employee_age_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_age_Admin;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_age_Admin(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_age_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_age_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_LEFT
LEFT JOIN (SELECT 1 AS val, CASE TEMP_BODY.ref_self IS NULL WHEN 1 THEN 0 ELSE TEMP_BODY.res END AS res, TEMP_SOURCE.ref_self AS ref_self, TEMP_BODY.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_SOURCE
LEFT JOIN (SELECT 1 AS val, COUNT(*) > 0 AS res, TEMP_BODY.ref_caller AS ref_caller, TEMP_BODY.ref_self AS ref_self
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_s AS ref_s, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT TEMP_DMN.val AS val, TEMP_DMN.res AS res, TEMP_DMN.res AS ref_s, TEMP_DMN.ref_self AS ref_self
FROM (SELECT CASE Employee_supervisee_supervisor_Employee.supervisee IS NULL WHEN 1 THEN 0 ELSE 1 END AS val, Employee_supervisee_supervisor_Employee.supervisor AS res, TEMP_OBJ.ref_self AS ref_self
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_OBJ
LEFT JOIN Employee_supervisee_supervisor_Employee ON TEMP_OBJ.ref_self = Employee_supervisee_supervisor_Employee.supervisee AND TEMP_OBJ.val = 1) AS TEMP_DMN) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_BODY
WHERE TEMP_BODY.res = 1
GROUP BY TEMP_BODY.ref_caller, TEMP_BODY.ref_self) AS TEMP_BODY ON TEMP_SOURCE.ref_self = TEMP_BODY.ref_self) AS TEMP_RIGHT ON TEMP_LEFT.ref_self = TEMP_RIGHT.ref_self) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_salary;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_salary(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (role = 'Admin')
THEN IF auth_READ_Employee_salary_Admin(caller, self)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
END IF;
IF auth_READ_Employee_salary_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_salary_Admin;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_salary_Admin(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_salary_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_salary_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_LEFT
LEFT JOIN (SELECT 1 AS val, CASE TEMP_BODY.ref_self IS NULL WHEN 1 THEN 0 ELSE TEMP_BODY.res END AS res, TEMP_SOURCE.ref_self AS ref_self, TEMP_BODY.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_SOURCE
LEFT JOIN (SELECT 1 AS val, COUNT(*) > 0 AS res, TEMP_BODY.ref_caller AS ref_caller, TEMP_BODY.ref_self AS ref_self
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_s AS ref_s, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT TEMP_DMN.val AS val, TEMP_DMN.res AS res, TEMP_DMN.res AS ref_s, TEMP_DMN.ref_self AS ref_self
FROM (SELECT CASE Employee_supervisee_supervisor_Employee.supervisee IS NULL WHEN 1 THEN 0 ELSE 1 END AS val, Employee_supervisee_supervisor_Employee.supervisor AS res, TEMP_OBJ.ref_self AS ref_self
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_OBJ
LEFT JOIN Employee_supervisee_supervisor_Employee ON TEMP_OBJ.ref_self = Employee_supervisee_supervisor_Employee.supervisee AND TEMP_OBJ.val = 1) AS TEMP_DMN) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_BODY
WHERE TEMP_BODY.res = 1
GROUP BY TEMP_BODY.ref_caller, TEMP_BODY.ref_self) AS TEMP_BODY ON TEMP_SOURCE.ref_self = TEMP_BODY.ref_self) AS TEMP_RIGHT ON TEMP_LEFT.ref_self = TEMP_RIGHT.ref_self) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_IDENTIFY_Employee_supervisor;
DELIMITER //
CREATE FUNCTION auth_IDENTIFY_Employee_supervisor(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (role = 'Admin')
THEN IF auth_READ_Employee_supervisor_Admin(caller, self)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
END IF;
IF auth_READ_Employee_supervisor_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisor_Admin;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisor_Admin(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisor_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisor_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_LEFT
LEFT JOIN (SELECT 1 AS val, CASE TEMP_BODY.ref_self IS NULL WHEN 1 THEN 0 ELSE TEMP_BODY.res END AS res, TEMP_SOURCE.ref_self AS ref_self, TEMP_BODY.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_SOURCE
LEFT JOIN (SELECT 1 AS val, COUNT(*) > 0 AS res, TEMP_BODY.ref_caller AS ref_caller, TEMP_BODY.ref_self AS ref_self
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_s AS ref_s, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT TEMP_DMN.val AS val, TEMP_DMN.res AS res, TEMP_DMN.res AS ref_s, TEMP_DMN.ref_self AS ref_self
FROM (SELECT CASE Employee_supervisee_supervisor_Employee.supervisee IS NULL WHEN 1 THEN 0 ELSE 1 END AS val, Employee_supervisee_supervisor_Employee.supervisor AS res, TEMP_OBJ.ref_self AS ref_self
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_OBJ
LEFT JOIN Employee_supervisee_supervisor_Employee ON TEMP_OBJ.ref_self = Employee_supervisee_supervisor_Employee.supervisee AND TEMP_OBJ.val = 1) AS TEMP_DMN) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_BODY
WHERE TEMP_BODY.res = 1
GROUP BY TEMP_BODY.ref_caller, TEMP_BODY.ref_self) AS TEMP_BODY ON TEMP_SOURCE.ref_self = TEMP_BODY.ref_self) AS TEMP_RIGHT ON TEMP_LEFT.ref_self = TEMP_RIGHT.ref_self) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisor;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisor(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (role = 'Admin')
THEN IF auth_READ_Employee_supervisor_Admin(caller, self)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
END IF;
IF auth_READ_Employee_supervisor_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisor_Admin;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisor_Admin(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisor_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisor_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_LEFT
LEFT JOIN (SELECT 1 AS val, CASE TEMP_BODY.ref_self IS NULL WHEN 1 THEN 0 ELSE TEMP_BODY.res END AS res, TEMP_SOURCE.ref_self AS ref_self, TEMP_BODY.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_SOURCE
LEFT JOIN (SELECT 1 AS val, COUNT(*) > 0 AS res, TEMP_BODY.ref_caller AS ref_caller, TEMP_BODY.ref_self AS ref_self
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_s AS ref_s, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT TEMP_DMN.val AS val, TEMP_DMN.res AS res, TEMP_DMN.res AS ref_s, TEMP_DMN.ref_self AS ref_self
FROM (SELECT CASE Employee_supervisee_supervisor_Employee.supervisee IS NULL WHEN 1 THEN 0 ELSE 1 END AS val, Employee_supervisee_supervisor_Employee.supervisor AS res, TEMP_OBJ.ref_self AS ref_self
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_OBJ
LEFT JOIN Employee_supervisee_supervisor_Employee ON TEMP_OBJ.ref_self = Employee_supervisee_supervisor_Employee.supervisee AND TEMP_OBJ.val = 1) AS TEMP_DMN) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_BODY
WHERE TEMP_BODY.res = 1
GROUP BY TEMP_BODY.ref_caller, TEMP_BODY.ref_self) AS TEMP_BODY ON TEMP_SOURCE.ref_self = TEMP_BODY.ref_self) AS TEMP_RIGHT ON TEMP_LEFT.ref_self = TEMP_RIGHT.ref_self) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_IDENTIFY_Employee_supervisee;
DELIMITER //
CREATE FUNCTION auth_IDENTIFY_Employee_supervisee(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (role = 'Admin')
THEN IF auth_READ_Employee_supervisee_Admin(caller, self)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
END IF;
IF auth_READ_Employee_supervisee_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisee_Admin;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisee_Admin(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisee_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisee_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res OR TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_LEFT
LEFT JOIN (SELECT 1 AS val, CASE TEMP_BODY.ref_self IS NULL WHEN 1 THEN 0 ELSE TEMP_BODY.res END AS res, TEMP_SOURCE.ref_self AS ref_self, TEMP_BODY.ref_caller AS ref_caller
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_SOURCE
LEFT JOIN (SELECT 1 AS val, COUNT(*) > 0 AS res, TEMP_BODY.ref_caller AS ref_caller, TEMP_BODY.ref_self AS ref_self
FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val, TEMP_LEFT.ref_s AS ref_s, TEMP_LEFT.ref_self AS ref_self, TEMP_RIGHT.ref_caller AS ref_caller
FROM (SELECT TEMP_DMN.val AS val, TEMP_DMN.res AS res, TEMP_DMN.res AS ref_s, TEMP_DMN.ref_self AS ref_self
FROM (SELECT CASE Employee_supervisee_supervisor_Employee.supervisee IS NULL WHEN 1 THEN 0 ELSE 1 END AS val, Employee_supervisee_supervisor_Employee.supervisor AS res, TEMP_OBJ.ref_self AS ref_self
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_OBJ
LEFT JOIN Employee_supervisee_supervisor_Employee ON TEMP_OBJ.ref_self = Employee_supervisee_supervisor_Employee.supervisee AND TEMP_OBJ.val = 1) AS TEMP_DMN) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_BODY
WHERE TEMP_BODY.res = 1
GROUP BY TEMP_BODY.ref_caller, TEMP_BODY.ref_self) AS TEMP_BODY ON TEMP_SOURCE.ref_self = TEMP_BODY.ref_self) AS TEMP_RIGHT ON TEMP_LEFT.ref_self = TEMP_RIGHT.ref_self) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisee;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisee(caller VARCHAR(100), role VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (role = 'Admin')
THEN IF auth_READ_Employee_supervisee_Admin(caller, self)
THEN RETURN(1);
ELSE RETURN(0);
END IF;
END IF;
IF auth_READ_Employee_supervisee_D3f4u1t(caller, self)
THEN 
RETURN(1);
ELSE
RETURN (0);
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisee_Admin;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisee_Admin(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, true AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_supervisee_D3f4u1t;
DELIMITER //
CREATE FUNCTION auth_READ_Employee_supervisee_D3f4u1t(caller VARCHAR(100), self VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT TEMP_LEFT.res = TEMP_RIGHT.res AS res, CASE TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 WHEN 1 THEN 0 ELSE 1 END AS val
FROM (SELECT 1 AS val, self AS res, self AS ref_self) AS TEMP_LEFT
JOIN (SELECT 1 AS val, caller AS res, caller AS ref_caller) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

