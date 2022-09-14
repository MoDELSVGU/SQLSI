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

DROP FUNCTION IF EXISTS auth_READ_Lecturer_name;
/* FUNC: auth_READ_Lecturer_name */
DELIMITER //
CREATE FUNCTION auth_READ_Lecturer_name(_caller varchar(250), _role varchar(250), _self varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN 0;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Lecturer_age;
/* FUNC: auth_READ_Lecturer_age */
DELIMITER //
CREATE FUNCTION auth_READ_Lecturer_age(_caller varchar(250), _role varchar(250), _self varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN 0;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Student_name;
/* FUNC: auth_READ_Student_name */
DELIMITER //
CREATE FUNCTION auth_READ_Student_name(_caller varchar(250), _role varchar(250), _self varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN 0;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Student_age;
/* FUNC: auth_READ_Student_age */
DELIMITER //
CREATE FUNCTION auth_READ_Student_age(_caller varchar(250), _role varchar(250), _self varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (_role = 'Lecturer')
    THEN IF (auth_READ_Student_age_Lecturer(_caller, _self))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Student_age_Lecturer;
/* FUNC: auth_READ_Student_age_Lecturer */
DELIMITER //
CREATE FUNCTION auth_READ_Student_age_Lecturer(_caller varchar(250), _self varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(EXISTS (SELECT 1 FROM Enrollment WHERE lecturers = kcaller AND kself = students))as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Enrollment;
/* FUNC: auth_READ_Enrollment */
DELIMITER //
CREATE FUNCTION auth_READ_Enrollment(_students varchar(250), _caller varchar(250), _role varchar(250), _lecturers varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (_role = 'Lecturer')
    THEN IF (auth_READ_Enrollment_Lecturer(_students, _caller, _lecturers))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Enrollment_Lecturer;
/* FUNC: auth_READ_Enrollment_Lecturer */
DELIMITER //
CREATE FUNCTION auth_READ_Enrollment_Lecturer(_students varchar(250), _caller varchar(250), _lecturers varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(EXISTS (SELECT 1 FROM Enrollment WHERE lecturers = kcaller AND kstudents = students))as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

