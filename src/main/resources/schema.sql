DROP TRIGGER IF EXISTS `trigger_locales_insert_onefkey` $
DROP TRIGGER IF EXISTS `trigger_locales_update_onefkey` $
DROP TRIGGER IF EXISTS `trigger_wikidataids_insert_onefkey` $
DROP TRIGGER IF EXISTS `trigger_wikidataids_update_onefkey` $
DROP PROCEDURE IF EXISTS `procedure_one_fkey_constraint` $

DROP TABLE IF EXISTS `wikidataids` $
DROP TABLE IF EXISTS `locales` $
DROP TABLE IF EXISTS `quotes` $
DROP TABLE IF EXISTS `works` $
DROP TABLE IF EXISTS `authors` $

CREATE TABLE authors (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`slug` VARCHAR(255) UNIQUE NOT NULL,
	`image` LONGBLOB,
	`image_attribution` VARCHAR(255),
	PRIMARY KEY (`id`)
) $

CREATE TABLE works (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`slug` VARCHAR(255) UNIQUE NOT NULL,
	`author_id` INTEGER NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `fkey_work_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) $

CREATE TABLE quotes (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`schedule` VARCHAR(8) NOT NULL,
	`year` VARCHAR(8),
	`time` VARCHAR(8),
	`language` VARCHAR(8) NOT NULL,
	`corpus` TEXT NOT NULL,
	`href` VARCHAR(255),
	`meta` VARCHAR(255),
	`curator` VARCHAR(255),
	`locked` BOOLEAN NOT NULL DEFAULT 0,
	`work_id` INTEGER NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `fkey_quote_work` FOREIGN KEY (`work_id`) REFERENCES `works` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) $

CREATE TABLE locales (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`language` VARCHAR(8) NOT NULL,
	`name` VARCHAR(255) NOT NULL,
	`href` VARCHAR(255),
	`author_id` INTEGER DEFAULT NULL,
	`work_id` INTEGER DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `unique_language_author` (`language`, `author_id`),
	UNIQUE KEY `unique_language_work` (`language`, `work_id`),
	CONSTRAINT `fkey_locale_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fkey_locale_work` FOREIGN KEY (`work_id`) REFERENCES `works` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) $

CREATE TABLE wikidataids (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`wikidata_id` INTEGER UNIQUE NOT NULL,
	`author_id` INTEGER UNIQUE DEFAULT NULL,
	`work_id` INTEGER UNIQUE DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `fkey_wikidataid_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fkey_wikidataid_work` FOREIGN KEY (`work_id`) REFERENCES `works` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) $

-- CREATE PROCEDURE `procedure_one_fkey_constraint` (IN table_name VARCHAR(64))
-- BEGIN
-- 	DECLARE i, j INTEGER DEFAULT 1;
-- 	DECLARE result BOOLEAN DEFAULT 0;
-- 	DECLARE constraint_names_size, column_names_size INTEGER;
-- 	DECLARE constraint_name, column_name VARCHAR(64);
--
-- 	constraint_names_block: BEGIN
-- 		DECLARE constraint_names CURSOR FOR
-- 			SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
-- 			WHERE INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_TYPE = 'FOREIGN KEY'
-- 			AND INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_NAME = table_name;
-- 		SELECT FOUND_ROWS() INTO constraint_names_size;
--
-- 		constraint_names_loop: LOOP
-- 			IF (i > constraint_names_size) THEN
-- 				CLOSE constraint_names;
-- 				LEAVE constraint_names_loop;
-- 			ELSE
-- 				FETCH constraint_names INTO constraint_name;
-- 			END IF;
--
-- 			column_names_block: BEGIN
-- 				DECLARE column_names CURSOR FOR
-- 					SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
-- 					WHERE INFORMATION_SCHEMA.KEY_COLUMN_USAGE.CONSTRAINT_NAME = constraint_name;
-- 				SELECT FOUND_ROWS() INTO column_names_size;
--
-- 				column_names_loop: LOOP
-- 					IF (i > column_names_size) THEN
-- 						CLOSE column_names;
-- 						LEAVE column_names_loop;
-- 					ELSE
-- 						FETCH column_names INTO column_name;
-- 					END IF;
--
-- 					IF (CONCAT(NEW, '.', column_name) IS NOT NULL) THEN
-- 						IF (result < 1) THEN
-- 							SET result = 1;
-- 						ELSE
-- 							SIGNAL SQLSTATE '23000' SET MESSAGE_TEXT = 'One foreign key constraint fails: Multiple foreign keys';
-- 						END IF;
-- 					END IF;
--
-- 					SET j = j + 1;
-- 				END LOOP column_names_loop;
-- 			END column_names_block;
--
-- 			SET i = i + 1;
-- 		END LOOP constraint_names_loop;
-- 	END constraint_names_block;
--
-- 	IF (result < 1) THEN
-- 		SIGNAL SQLSTATE '23000' SET MESSAGE_TEXT = 'One foreign key constraint fails: No foreign key';
-- 	END IF;
-- END $
--
-- CREATE TRIGGER `trigger_locales_insert_onefkey` AFTER INSERT ON `locales`
-- FOR EACH ROW BEGIN CALL procedure_one_fkey_constraint('locales'); END $
--
-- CREATE TRIGGER `trigger_locales_update_onefkey` AFTER UPDATE ON `locales`
-- FOR EACH ROW BEGIN CALL procedure_one_fkey_constraint('locales'); END $
--
-- CREATE TRIGGER `trigger_wikidataids_insert_onefkey` AFTER INSERT ON `wikidataids`
-- FOR EACH ROW BEGIN CALL procedure_one_fkey_constraint('wikidataids'); END $
--
-- CREATE TRIGGER `trigger_wikidataids_update_onefkey` AFTER UPDATE ON `wikidataids`
-- FOR EACH ROW BEGIN CALL procedure_one_fkey_constraint('wikidataids'); END $
