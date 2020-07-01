DROP FUNCTION IF EXISTS checkAuth;
DELIMITER //
CREATE FUNCTION checkAuth(origWhere INT, authWhere INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (origWhere = 1 and authWhere = 1)
THEN RETURN(1);
ELSE IF (origWhere = 1 and authWhere = 0)
THEN
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
DROP FUNCTION IF EXISTS auth_read_Reg_User_family_name_Default;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_family_name_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_family_name_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_family_name_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT 1 AS val, TRUE AS res) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Reg_User_family_name;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_family_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Reg_User_family_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Reg_User_family_name_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Reg_User_family_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_middle_name_Default;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_middle_name_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_middle_name_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_middle_name_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT 1 AS val, TRUE AS res) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Reg_User_middle_name;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_middle_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Reg_User_middle_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Reg_User_middle_name_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Reg_User_middle_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_given_name_Default;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_given_name_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_given_name_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_given_name_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT 1 AS val, TRUE AS res) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Reg_User_given_name;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_given_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Reg_User_given_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Reg_User_given_name_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Reg_User_given_name_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_email_Default;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_email_Default (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_read_Reg_User_email_Admin;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_email_Admin (kself INT, kcaller INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res OR TEMP_RIGHT.res AS res FROM (SELECT CASE WHEN TEMP_LEFT.val = 0 OR TEMP_RIGHT.val = 0 THEN 0 ELSE 1 END AS val, TEMP_LEFT.res = TEMP_RIGHT.res AS res FROM (SELECT 1 AS val, kself AS res, kself AS ref_kself) AS TEMP_LEFT JOIN (SELECT 1 AS val, kcaller AS res, kcaller AS ref_kcaller) AS TEMP_RIGHT) AS TEMP_LEFT JOIN (SELECT 1 AS val, TRUE AS res) AS TEMP_RIGHT) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Reg_User_email;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_email(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_read_Reg_User_email_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_read_Reg_User_email_Admin(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_read_Reg_User_email_Default(kself, kcaller)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Reg_User_role;
DELIMITER //
CREATE FUNCTION auth_read_Reg_User_role(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Enrollment_courses;
DELIMITER //
CREATE FUNCTION auth_read_Enrollment_courses(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Teaching_courses;
DELIMITER //
CREATE FUNCTION auth_read_Teaching_courses(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Course_name;
DELIMITER //
CREATE FUNCTION auth_read_Course_name(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Enrollment_students;
DELIMITER //
CREATE FUNCTION auth_read_Enrollment_students(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_read_Teaching_lecturers;
DELIMITER //
CREATE FUNCTION auth_read_Teaching_lecturers(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
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

DROP FUNCTION IF EXISTS auth_delete_Reg_User;
DELIMITER //
CREATE FUNCTION auth_delete_Reg_User(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_delete_Student;
DELIMITER //
CREATE FUNCTION auth_delete_Student(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_delete_Lecturer;
DELIMITER //
CREATE FUNCTION auth_delete_Lecturer(kself INT, kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_delete_Course;
DELIMITER //
CREATE FUNCTION auth_delete_Course(kself INT, kcaller INT, krole VARCHAR(100))
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

DROP FUNCTION IF EXISTS auth_create_Reg_User;
DELIMITER //
CREATE FUNCTION auth_create_Reg_User(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Student;
DELIMITER //
CREATE FUNCTION auth_create_Student(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Enrollment;
DELIMITER //
CREATE FUNCTION auth_create_Enrollment(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Lecturer;
DELIMITER //
CREATE FUNCTION auth_create_Lecturer(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Teaching;
DELIMITER //
CREATE FUNCTION auth_create_Teaching(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Course;
DELIMITER //
CREATE FUNCTION auth_create_Course(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Enrollment;
DELIMITER //
CREATE FUNCTION auth_create_Enrollment(kcaller INT, krole VARCHAR(100))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_create_Teaching;
DELIMITER //
CREATE FUNCTION auth_create_Teaching(kcaller INT, krole VARCHAR(100))
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
DROP FUNCTION IF EXISTS auth_update_Reg_User_family_name_Default;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_family_name_Default (kself INT, kcaller INT, pfamily_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_family_name_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_family_name_Admin (kself INT, kcaller INT, pfamily_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Reg_User_family_name;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_family_name(kself INT, kcaller INT, krole VARCHAR(100), pfamily_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Reg_User_family_name_Default(kself, kcaller, pfamily_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Reg_User_family_name_Admin(kself, kcaller, pfamily_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Reg_User_family_name_Default(kself, kcaller, pfamily_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_middle_name_Default;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_middle_name_Default (kself INT, kcaller INT, pmiddle_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_middle_name_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_middle_name_Admin (kself INT, kcaller INT, pmiddle_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Reg_User_middle_name;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_middle_name(kself INT, kcaller INT, krole VARCHAR(100), pmiddle_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Reg_User_middle_name_Default(kself, kcaller, pmiddle_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Reg_User_middle_name_Admin(kself, kcaller, pmiddle_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Reg_User_middle_name_Default(kself, kcaller, pmiddle_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_given_name_Default;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_given_name_Default (kself INT, kcaller INT, pgiven_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_given_name_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_given_name_Admin (kself INT, kcaller INT, pgiven_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Reg_User_given_name;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_given_name(kself INT, kcaller INT, krole VARCHAR(100), pgiven_name VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Reg_User_given_name_Default(kself, kcaller, pgiven_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Reg_User_given_name_Admin(kself, kcaller, pgiven_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Reg_User_given_name_Default(kself, kcaller, pgiven_name)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_email_Default;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_email_Default (kself INT, kcaller INT, pemail VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;
DROP FUNCTION IF EXISTS auth_update_Reg_User_email_Admin;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_email_Admin (kself INT, kcaller INT, pemail VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
SELECT res INTO result FROM (SELECT 1 AS val, FALSE AS res) AS TEMP_result;
RETURN (result);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Reg_User_email;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_email(kself INT, kcaller INT, krole VARCHAR(100), pemail VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
IF (krole = 'Default')
THEN IF auth_update_Reg_User_email_Default(kself, kcaller, pemail)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF (krole = 'Admin')
THEN IF auth_update_Reg_User_email_Admin(kself, kcaller, pemail)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
ELSE
IF auth_update_Reg_User_email_Default(kself, kcaller, pemail)
THEN 
RETURN (1);
ELSE
RETURN (0);
END IF;
END IF;
END IF;
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Reg_User_role;
DELIMITER //
CREATE FUNCTION auth_update_Reg_User_role(kself INT, kcaller INT, krole VARCHAR(100), prole INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Enrollment_courses;
DELIMITER //
CREATE FUNCTION auth_update_Enrollment_courses(kself INT, kcaller INT, krole VARCHAR(100), pcourses INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Teaching_courses;
DELIMITER //
CREATE FUNCTION auth_update_Teaching_courses(kself INT, kcaller INT, krole VARCHAR(100), pcourses INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Course_name;
DELIMITER //
CREATE FUNCTION auth_update_Course_name(kself INT, kcaller INT, krole VARCHAR(100), pname VARCHAR(250))
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Enrollment_students;
DELIMITER //
CREATE FUNCTION auth_update_Enrollment_students(kself INT, kcaller INT, krole VARCHAR(100), pstudents INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;

DROP FUNCTION IF EXISTS auth_update_Teaching_lecturers;
DELIMITER //
CREATE FUNCTION auth_update_Teaching_lecturers(kself INT, kcaller INT, krole VARCHAR(100), plecturers INT)
RETURNS INT DETERMINISTIC
BEGIN
DECLARE result INT DEFAULT 0;
RETURN (0);
END //
DELIMITER ;
