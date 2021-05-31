DROP FUNCTION IF EXISTS throw_error;
/* FUNC: throw_error */
DELIMITER //
CREATE FUNCTION throw_error()
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Unauthorized access';
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_salary;
/* FUNC: auth_READ_Employee_salary */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_salary(kcaller varchar(250), krole varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Employee')
    THEN IF (auth_READ_Employee_salary_Employee(kcaller, kself))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_salary_Employee;
/* FUNC: auth_READ_Employee_salary_Employee */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_salary_Employee(kcaller varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(kcaller = kself) or (EXISTS (SELECT 1 FROM Supervision WHERE supervisors = kcaller AND kself = supervisees))as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_employmentLevel;
/* FUNC: auth_READ_Employee_employmentLevel */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_employmentLevel(kcaller varchar(250), krole varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Employee')
    THEN IF (auth_READ_Employee_employmentLevel_Employee(kcaller, kself))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_employmentLevel_Employee;
/* FUNC: auth_READ_Employee_employmentLevel_Employee */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_employmentLevel_Employee(kcaller varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(kcaller = kself)as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_email;
/* FUNC: auth_READ_Employee_email */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_email(kcaller varchar(250), krole varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Employee')
    THEN IF (auth_READ_Employee_email_Employee(kcaller, kself))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_email_Employee;
/* FUNC: auth_READ_Employee_email_Employee */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_email_Employee(kcaller varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(kcaller = kself) or ((SELECT employmentLevel FROM Employee WHERE Employee_id = kself) >= (SELECT employmentLevel FROM Employee WHERE Employee_id = kcaller))as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_name;
/* FUNC: auth_READ_Employee_name */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_name(kcaller varchar(250), krole varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Employee')
    THEN IF (auth_READ_Employee_name_Employee(kcaller, kself))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Employee_name_Employee;
/* FUNC: auth_READ_Employee_name_Employee */
DELIMITER //
CREATE FUNCTION auth_READ_Employee_name_Employee(kcaller varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(TRUE) or (kcaller = kself)as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Supervision;
/* FUNC: auth_READ_Supervision */
DELIMITER //
CREATE FUNCTION auth_READ_Supervision(kcaller varchar(250), ksupervisors varchar(250), ksupervisees varchar(250), krole varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Employee')
    THEN IF (auth_READ_Supervision_Employee(kcaller, ksupervisors, ksupervisees))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Supervision_Employee;
/* FUNC: auth_READ_Supervision_Employee */
DELIMITER //
CREATE FUNCTION auth_READ_Supervision_Employee(kcaller varchar(250), ksupervisors varchar(250), ksupervisees varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(kcaller = kself)as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

