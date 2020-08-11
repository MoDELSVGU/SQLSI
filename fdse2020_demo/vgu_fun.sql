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

DROP FUNCTION IF EXISTS auth_READ_Lecturer_email;
/* FUNC: auth_READ_Lecturer_email */
DELIMITER //
CREATE FUNCTION auth_READ_Lecturer_email(kcaller varchar(250), krole varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Lecturer')
    THEN IF (auth_READ_Lecturer_email_Lecturer(kcaller, kself))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Lecturer_email_Lecturer;
/* FUNC: auth_READ_Lecturer_email_Lecturer */
DELIMITER //
CREATE FUNCTION auth_READ_Lecturer_email_Lecturer(kcaller varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(kcaller = kself) or (EXISTS (SELECT 1 FROM Enrollment e1 JOIN Enrollment e2 ON e1.students = e2.students WHERE e1.lecturers = kcaller AND e2.lecturers = kself))as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Student_email;
/* FUNC: auth_READ_Student_email */
DELIMITER //
CREATE FUNCTION auth_READ_Student_email(kcaller varchar(250), krole varchar(250), kself varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Lecturer')
    THEN IF (auth_READ_Student_email_Lecturer(kcaller, kself))
        THEN RETURN (1);
        ELSE RETURN (0);
    END IF;
ELSE RETURN 0;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_READ_Student_email_Lecturer;
/* FUNC: auth_READ_Student_email_Lecturer */
DELIMITER //
CREATE FUNCTION auth_READ_Student_email_Lecturer(kcaller varchar(250), kself varchar(250))
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
CREATE FUNCTION auth_READ_Enrollment(kcaller varchar(250), krole varchar(250), klecturers varchar(250), kstudents varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Lecturer')
    THEN IF (auth_READ_Enrollment_Lecturer(kcaller, klecturers, kstudents))
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
CREATE FUNCTION auth_READ_Enrollment_Lecturer(kcaller varchar(250), klecturers varchar(250), kstudents varchar(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM 
(SELECT 
(klecturers = kcaller) or (EXISTS (SELECT 1 FROM Enrollment WHERE lecturers = kcaller AND kstudents = students))as res
) AS TEMP;
RETURN (result);
END //
DELIMITER ;

