DROP TRIGGER IF EXISTS `trigger_locales_insert_onefkey`;
DROP TRIGGER IF EXISTS `trigger_locales_update_onefkey`;
DROP TRIGGER IF EXISTS `trigger_wikidataids_insert_onefkey`;
DROP TRIGGER IF EXISTS `trigger_wikidataids_update_onefkey`;
DROP PROCEDURE IF EXISTS `procedure_one_fkey_constraint`;

DROP TABLE IF EXISTS `wikidataids`;
DROP TABLE IF EXISTS `locales`;
DROP TABLE IF EXISTS `quotes`;
DROP TABLE IF EXISTS `works`;
DROP TABLE IF EXISTS `authors`;

CREATE TABLE authors (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`slug` VARCHAR(255) UNIQUE NOT NULL,
	`image` LONGBLOB,
	`image_attribution` VARCHAR(255),
	PRIMARY KEY (`id`)
);

CREATE TABLE works (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`slug` VARCHAR(255) UNIQUE NOT NULL,
	`author_id` INTEGER NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `fkey_work_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
);

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
);

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
);

CREATE TABLE wikidataids (
	`id` INTEGER AUTO_INCREMENT NOT NULL,
	`wikidata_id` INTEGER UNIQUE NOT NULL,
	`author_id` INTEGER UNIQUE DEFAULT NULL,
	`work_id` INTEGER UNIQUE DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `fkey_wikidataid_author` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fkey_wikidataid_work` FOREIGN KEY (`work_id`) REFERENCES `works` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
);
