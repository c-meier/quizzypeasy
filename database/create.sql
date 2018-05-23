DROP SCHEMA IF EXISTS `QuizzyPeasy`;

CREATE SCHEMA IF NOT EXISTS `QuizzyPeasy`;

USE `QuizzyPeasy`;

CREATE USER IF NOT EXISTS 'QuizzyPeasyUser'@'localhost' IDENTIFIED BY 'QuizzyPeasyPass';
GRANT ALL ON QuizzyPeasy.* TO 'QuizzyPeasyUser'@'localhost';