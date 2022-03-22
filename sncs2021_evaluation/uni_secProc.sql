DROP PROCEDURE IF EXISTS Query1;
/* SELECT email FROM Lecturer WHERE Lecturer_id = 'Huong' */
DELIMITER //
CREATE PROCEDURE Query1(in kcaller varchar(250), in krole varchar(250))
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
  SET _rollback = 1;
  GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
  SELECT @p1, @p2;
  ROLLBACK;
END;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS TEMP1;
CREATE TEMPORARY TABLE TEMP1 AS (
SELECT * FROM Lecturer WHERE Lecturer_id = 'Huong'
);
DROP TEMPORARY TABLE IF EXISTS TEMP2;
CREATE TEMPORARY TABLE TEMP2 AS (
SELECT CASE auth_READ_Lecturer_email(kcaller, krole, Lecturer_id) WHEN TRUE THEN email ELSE throw_error() END AS email FROM TEMP1
);
IF _rollback = 0
THEN SELECT * from TEMP2;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Query2;
/* SELECT DISTINCT email FROM Lecturer JOIN (SELECT * FROM Enrollment WHERE students = 'Thanh' AND lecturers = 'Huong' ) AS TEMP ON TEMP.lecturers = Lecturer_id */
DELIMITER //
CREATE PROCEDURE Query2(in kcaller varchar(250), in krole varchar(250))
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
  SET _rollback = 1;
  GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
  SELECT @p1, @p2;
  ROLLBACK;
END;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS TEMP1;
CREATE TEMPORARY TABLE TEMP1 AS (
SELECT Lecturer_id AS lecturers, Student_id AS students FROM Lecturer, Student WHERE Student_id = 'Thanh' AND Lecturer_id = 'Huong'
);
DROP TEMPORARY TABLE IF EXISTS TEMP2;
CREATE TEMPORARY TABLE TEMP2 AS (
SELECT CASE auth_READ_Enrollment(kcaller, krole, lecturers, students) WHEN TRUE THEN lecturers ELSE throw_error() END AS lecturers FROM TEMP1
);
DROP TEMPORARY TABLE IF EXISTS TEMP3;
CREATE TEMPORARY TABLE TEMP3 AS (
SELECT * FROM Enrollment WHERE students = 'Thanh' AND lecturers = 'Huong'
);
DROP TEMPORARY TABLE IF EXISTS TEMP4;
CREATE TEMPORARY TABLE TEMP4 AS (
SELECT * FROM Lecturer JOIN TEMP3 AS TEMP ON TEMP.lecturers = Lecturer_id
);
DROP TEMPORARY TABLE IF EXISTS TEMP5;
CREATE TEMPORARY TABLE TEMP5 AS (
SELECT * FROM TEMP4
);
DROP TEMPORARY TABLE IF EXISTS TEMP6;
CREATE TEMPORARY TABLE TEMP6 AS (
SELECT CASE auth_READ_Lecturer_email(kcaller, krole, Lecturer_id) WHEN TRUE THEN email ELSE throw_error() END AS email FROM TEMP5
);
IF _rollback = 0
THEN SELECT * from TEMP6;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Query3;
/* SELECT DISTINCT email FROM Lecturer JOIN (SELECT e1.lecturers as lecturers FROM (SELECT * FROM Enrollment WHERE lecturers = 'Huong' ) AS e1 JOIN (SELECT * FROM Enrollment WHERE lecturers = 'Manuel' ) AS e2 ON e1.students = e2.students ) AS TEMP ON TEMP.lecturers = Lecturer_id */
DELIMITER //
CREATE PROCEDURE Query3(in kcaller varchar(250), in krole varchar(250))
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
  SET _rollback = 1;
  GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
  SELECT @p1, @p2;
  ROLLBACK;
END;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS TEMP1;
CREATE TEMPORARY TABLE TEMP1 AS (
SELECT Lecturer_id AS lecturers, Student_id AS students FROM Lecturer, Student WHERE Lecturer_id = 'Huong'
);
DROP TEMPORARY TABLE IF EXISTS TEMP2;
CREATE TEMPORARY TABLE TEMP2 AS (
SELECT CASE auth_READ_Enrollment(kcaller, krole, lecturers, students) WHEN TRUE THEN lecturers ELSE throw_error() END AS lecturers FROM TEMP1
);
DROP TEMPORARY TABLE IF EXISTS TEMP3;
CREATE TEMPORARY TABLE TEMP3 AS (
SELECT * FROM Enrollment WHERE lecturers = 'Huong'
);
DROP TEMPORARY TABLE IF EXISTS TEMP4;
CREATE TEMPORARY TABLE TEMP4 AS (
SELECT Lecturer_id AS lecturers, Student_id AS students FROM Lecturer, Student WHERE Lecturer_id = 'Manuel'
);
DROP TEMPORARY TABLE IF EXISTS TEMP5;
CREATE TEMPORARY TABLE TEMP5 AS (
SELECT CASE auth_READ_Enrollment(kcaller, krole, lecturers, students) WHEN TRUE THEN lecturers ELSE throw_error() END AS lecturers FROM TEMP4
);
DROP TEMPORARY TABLE IF EXISTS TEMP6;
CREATE TEMPORARY TABLE TEMP6 AS (
SELECT * FROM Enrollment WHERE lecturers = 'Manuel'
);
DROP TEMPORARY TABLE IF EXISTS TEMP7;
CREATE TEMPORARY TABLE TEMP7 AS (
SELECT e1.lecturers AS lecturers FROM (SELECT * FROM Enrollment WHERE lecturers = 'Huong') AS e1 JOIN (SELECT * FROM Enrollment WHERE lecturers = 'Manuel') AS e2 ON e1.students = e2.students
);
DROP TEMPORARY TABLE IF EXISTS TEMP8;
CREATE TEMPORARY TABLE TEMP8 AS (
SELECT * FROM Lecturer JOIN TEMP7 AS TEMP ON TEMP.lecturers = Lecturer_id
);
DROP TEMPORARY TABLE IF EXISTS TEMP9;
CREATE TEMPORARY TABLE TEMP9 AS (
SELECT * FROM TEMP8
);
DROP TEMPORARY TABLE IF EXISTS TEMP10;
CREATE TEMPORARY TABLE TEMP10 AS (
SELECT CASE auth_READ_Lecturer_email(kcaller, krole, Lecturer_id) WHEN TRUE THEN email ELSE throw_error() END AS email FROM TEMP9
);
IF _rollback = 0
THEN SELECT * from TEMP10;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Query4;
/* SELECT Student_id FROM Student WHERE age > 18 */
DELIMITER //
CREATE PROCEDURE Query4(in kcaller varchar(250), in krole varchar(250))
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
  SET _rollback = 1;
  GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
  SELECT @p1, @p2;
  ROLLBACK;
END;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS TEMP1;
CREATE TEMPORARY TABLE TEMP1 AS (
SELECT * FROM Student WHERE CASE auth_READ_Student_age(kcaller, krole, Student_id) WHEN TRUE THEN age ELSE throw_error() END > 18
);
DROP TEMPORARY TABLE IF EXISTS TEMP2;
CREATE TEMPORARY TABLE TEMP2 AS (
SELECT Student_id AS Student_id FROM TEMP1
);
IF _rollback = 0
THEN SELECT * from TEMP2;
END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Query5;
/* SELECT students FROM Enrollment */
DELIMITER //
CREATE PROCEDURE Query5(in kcaller varchar(250), in krole varchar(250))
BEGIN
DECLARE _rollback int DEFAULT 0;
DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
  SET _rollback = 1;
  GET STACKED DIAGNOSTICS CONDITION 1 @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
  SELECT @p1, @p2;
  ROLLBACK;
END;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS TEMP1;
CREATE TEMPORARY TABLE TEMP1 AS (
SELECT Lecturer_id AS lecturers, Student_id AS students FROM Lecturer, Student
);
DROP TEMPORARY TABLE IF EXISTS TEMP2;
CREATE TEMPORARY TABLE TEMP2 AS (
SELECT CASE auth_READ_Enrollment(kcaller, krole, lecturers, students) WHEN TRUE THEN lecturers ELSE throw_error() END AS lecturers FROM TEMP1
);
DROP TEMPORARY TABLE IF EXISTS TEMP3;
CREATE TEMPORARY TABLE TEMP3 AS (
SELECT students FROM Enrollment
);
IF _rollback = 0
THEN SELECT * from TEMP3;
END IF;
END //
DELIMITER ;
