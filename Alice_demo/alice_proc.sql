DROP PROCEDURE IF EXISTS Q1;
DELIMITER //
CREATE PROCEDURE Q1(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT 1 FROM Employee WHERE checkAuth(1, 1));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q2;
DELIMITER //
CREATE PROCEDURE Q2(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT name FROM Employee WHERE checkAuth(1, (auth_read_Employee_name(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q3;
DELIMITER //
CREATE PROCEDURE Q3(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT age FROM Employee WHERE checkAuth(1, (auth_read_Employee_age(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q4;
DELIMITER //
CREATE PROCEDURE Q4(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT salary FROM Employee WHERE checkAuth(1, (auth_read_Employee_salary(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q5;
DELIMITER //
CREATE PROCEDURE Q5(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT TEMP.name FROM (SELECT name, salary FROM Employee) AS TEMP WHERE checkAuth(1, 1));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q6;
DELIMITER //
CREATE PROCEDURE Q6(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT name, age, salary FROM Employee WHERE checkAuth(1, (auth_read_Employee_name(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_age(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_salary(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q7;
DELIMITER //
CREATE PROCEDURE Q7(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT salary FROM Employee WHERE checkAuth(age >= 25, (auth_read_Employee_salary(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_age(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Q8;
DELIMITER //
CREATE PROCEDURE Q8(IN kcaller INT)
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE krole varchar(100) DEFAULT 'Default';
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
SET _rollback = 1;
GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
SELECT @p1, @p2;
ROLLBACK;
END;
IF kcaller > 0 THEN 
SELECT Role.name INTO krole FROM Employee RIGHT JOIN Role ON Employee.role = Role.role_id WHERE kcaller = Employee.Employee_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS (SELECT salary FROM Employee AS TEMP LEFT JOIN (SELECT Employee_id FROM Employee WHERE checkAuth(age < 25, (auth_read_Employee_age(Employee_id, kcaller, krole) = 1))) AS COMP ON TEMP.Employee_id = COMP.Employee_id WHERE checkAuth(COMP.Employee_id IS NULL, (auth_read_Employee_salary(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;

