DROP PROCEDURE IF EXISTS ReadNameAllEmployees;
DELIMITER //
CREATE PROCEDURE ReadNameAllEmployees(IN kcaller INT)
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
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT name FROM Employee WHERE checkAuth(1, (auth_read_Employee_name(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
DROP PROCEDURE IF EXISTS ReadAllEmployeeInfo;
DELIMITER //
CREATE PROCEDURE ReadAllEmployeeInfo(IN kcaller INT)
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
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT name, age, salary FROM Employee WHERE checkAuth(1, (auth_read_Employee_name(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_age(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_salary(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
DROP PROCEDURE IF EXISTS ReadNameEmployeesAboveEq25;
DELIMITER //
CREATE PROCEDURE ReadNameEmployeesAboveEq25(IN kcaller INT)
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
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT name FROM Employee WHERE checkAuth(age >= 25, (auth_read_Employee_name(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_age(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
DROP PROCEDURE IF EXISTS ReadNameEmployeesBelow25;
DELIMITER //
CREATE PROCEDURE ReadNameEmployeesBelow25(IN kcaller INT)
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
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT name FROM Employee WHERE checkAuth(age < 25, (auth_read_Employee_name(Employee_id, kcaller, krole) = 1 AND auth_read_Employee_age(Employee_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
