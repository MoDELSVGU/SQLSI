DROP PROCEDURE IF EXISTS ReadAllRegUserInfo;
DELIMITER //
CREATE PROCEDURE ReadAllRegUserInfo(IN kcaller INT)
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
SELECT Role.name INTO krole FROM Reg_User RIGHT JOIN Role ON Reg_User.role = Role.role_id WHERE kcaller = Reg_User.Reg_User_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT given_name, middle_name, family_name, email FROM Reg_User WHERE checkAuth(1, (auth_read_Reg_User_given_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_middle_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_family_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_email(Reg_User_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
DROP PROCEDURE IF EXISTS ReadThisRegUserInfo;
DELIMITER //
CREATE PROCEDURE ReadThisRegUserInfo(IN kcaller INT, IN preg_user_id INT)
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
SELECT Role.name INTO krole FROM Reg_User RIGHT JOIN Role ON Reg_User.role = Role.role_id WHERE kcaller = Reg_User.Reg_User_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT given_name, middle_name, family_name, email FROM Reg_User WHERE checkAuth(Reg_User_id = preg_user_id, (auth_read_Reg_User_given_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_middle_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_family_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_email(Reg_User_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
DROP PROCEDURE IF EXISTS ReadRegUserInfoThisEmail;
DELIMITER //
CREATE PROCEDURE ReadRegUserInfoThisEmail(IN kcaller INT, IN pemail VARCHAR(250))
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
SELECT Role.name INTO krole FROM Reg_User RIGHT JOIN Role ON Reg_User.role = Role.role_id WHERE kcaller = Reg_User.Reg_User_id;
END IF;
START TRANSACTION;
DROP TEMPORARY TABLE IF EXISTS Result;
CREATE TEMPORARY TABLE IF NOT EXISTS Result AS  (SELECT given_name, middle_name, family_name FROM Reg_User WHERE checkAuth(email = pemail, (auth_read_Reg_User_given_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_middle_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_family_name(Reg_User_id, kcaller, krole) = 1 AND auth_read_Reg_User_email(Reg_User_id, kcaller, krole) = 1)));
IF _rollback = 0
THEN SELECT * from Result;
END IF;
END //
DELIMITER ;
