CREATE USER IF NOT EXISTS 'sqlUser'@'localhost' IDENTIFIED BY 'MadridView1234';
GRANT SELECT, UPDATE, DELETE, INSERT ON MadridViewDB.* TO 'sqlUser'@'localhost';

CREATE DATABASE MadridViewDB;

USE MadridViewDB;

CREATE TABLE IF NOT EXISTS Users(
	id int PRIMARY KEY AUTO_INCREMENT,
	userName varchar(32) UNIQUE NOT NULL,
	mail varchar(64) UNIQUE NOT NULL,
	password varchar(256) NOT NULL,
	verified int NOT NULL DEFAULT 0,
	activated int NOT NULL DEFAULT 1,
	dateIn timestamp DEFAULT CURRENT_TIMESTAMP
);