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


DROP FUNCTION IF EXISTS auth_read_Role_name;
DELIMITER //
CREATE FUNCTION auth_read_Role_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Employee_name_Default;
DELIMITER //
CREATE FUNCTION auth_read_Employee_name_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Unknown' AS type) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Employee_name_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Employee_name_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Unknown' AS type) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res < TEMP_RIGHT.res AS res FROM (SELECT TEMP_obj.val AS val, 'Integer' AS type, age AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_obj LEFT JOIN Employee ON Employee.Employee_id = TEMP_obj.ref_kself AND TEMP_obj.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 30 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res < TEMP_RIGHT.res AS res FROM (SELECT TEMP_obj.val AS val, 'Integer' AS type, salary AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_obj LEFT JOIN Employee ON Employee.Employee_id = TEMP_obj.ref_kself AND TEMP_obj.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 60000 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_name;
DELIMITER //
CREATE FUNCTION auth_read_Employee_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Employee_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Employee_name_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Employee_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Employee_age_Default;
DELIMITER //
CREATE FUNCTION auth_read_Employee_age_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Unknown' AS type) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Employee_age_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Employee_age_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Unknown' AS type) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res < TEMP_RIGHT.res AS res FROM (SELECT TEMP_obj.val AS val, 'Integer' AS type, age AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_obj LEFT JOIN Employee ON Employee.Employee_id = TEMP_obj.ref_kself AND TEMP_obj.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 30 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_age;
DELIMITER //
CREATE FUNCTION auth_read_Employee_age(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Employee_age_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Employee_age_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Employee_age_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Employee_salary_Default;
DELIMITER //
CREATE FUNCTION auth_read_Employee_salary_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Unknown' AS type) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Employee_salary_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Employee_salary_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller, 'Unknown' AS type) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, 'Boolean' AS type, TEMP_LEFT.res < TEMP_RIGHT.res AS res FROM (SELECT TEMP_obj.val AS val, 'Integer' AS type, salary AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself, 'Unknown' AS type) AS TEMP_obj LEFT JOIN Employee ON Employee.Employee_id = TEMP_obj.ref_kself AND TEMP_obj.val = 1) AS TEMP_LEFT JOIN (SELECT 1 AS val, 60000 AS res, 'Integer' AS type) AS TEMP_RIGHT) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Employee_salary;
DELIMITER //
CREATE FUNCTION auth_read_Employee_salary(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Employee_salary_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Employee_salary_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Employee_salary_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_delete_Role;
DELIMITER //
CREATE FUNCTION auth_delete_Role(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_delete_Employee;
DELIMITER //
CREATE FUNCTION auth_delete_Employee(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Role;
DELIMITER //
CREATE FUNCTION auth_create_Role(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Employee;
DELIMITER //
CREATE FUNCTION auth_create_Employee(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Role_name;
DELIMITER //
CREATE FUNCTION auth_update_Role_name(kself INT, kcaller INT, krole VARCHAR(100), pname VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Employee_name_Default;
DELIMITER //
CREATE FUNCTION auth_update_Employee_name_Default (kself INT, kcaller INT, pname VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res, 'Boolean' AS type) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Employee_name_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Employee_name_Admin (kself INT, kcaller INT, pname VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res, 'Boolean' AS type) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Employee_name;
DELIMITER //
CREATE FUNCTION auth_update_Employee_name(kself INT, kcaller INT, krole VARCHAR(100), pname VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Employee_name_Default(kself, kcaller, pname)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Employee_name_Admin(kself, kcaller, pname)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Employee_name_Default(kself, kcaller, pname)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Employee_age_Default;
DELIMITER //
CREATE FUNCTION auth_update_Employee_age_Default (kself INT, kcaller INT, page INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res, 'Boolean' AS type) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Employee_age_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Employee_age_Admin (kself INT, kcaller INT, page INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res, 'Boolean' AS type) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Employee_age;
DELIMITER //
CREATE FUNCTION auth_update_Employee_age(kself INT, kcaller INT, krole VARCHAR(100), page INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Employee_age_Default(kself, kcaller, page)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Employee_age_Admin(kself, kcaller, page)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Employee_age_Default(kself, kcaller, page)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Employee_salary_Default;
DELIMITER //
CREATE FUNCTION auth_update_Employee_salary_Default (kself INT, kcaller INT, psalary INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res, 'Boolean' AS type) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Employee_salary_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Employee_salary_Admin (kself INT, kcaller INT, psalary INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res, 'Boolean' AS type) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Employee_salary;
DELIMITER //
CREATE FUNCTION auth_update_Employee_salary(kself INT, kcaller INT, krole VARCHAR(100), psalary INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Employee_salary_Default(kself, kcaller, psalary)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Employee_salary_Admin(kself, kcaller, psalary)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Employee_salary_Default(kself, kcaller, psalary)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
