DROP PROCEDURE IF EXISTS read_lecture_email_by_name;
/* SELECT email FROM Lecturer WHERE name = 'lname1' */
DELIMITER //
CREATE PROCEDURE read_lecture_email_by_name(in kcaller varchar(250), in krole varchar(250))
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
SELECT * FROM Lecturer WHERE CASE auth_READ_Lecturer_name(kcaller, krole, Lecturer_id) WHEN TRUE THEN name ELSE throw_error() END = 'lname1'
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

