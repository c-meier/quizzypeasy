USE `QuizzyPeasy`;

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id_user INT(11) AUTO_INCREMENT NOT NULL,
  user_name VARCHAR(45) NOT NULL,
  password VARCHAR(255) NOT NULL,
  date_inscription TIMESTAMP NOT NULL,
  is_admin BOOLEAN NOT NULL,
  PRIMARY KEY (id_user)
);

DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
  id_category INT(11) AUTO_INCREMENT NOT NULL,
  category_name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_category)
);

DROP TABLE IF EXISTS quizzes;
CREATE TABLE quizzes (
  id_quiz INT(11) AUTO_INCREMENT NOT NULL,
  score INT,
  category_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  PRIMARY KEY (id_quiz),
  FOREIGN KEY (category_id) REFERENCES categories(id_category),
  FOREIGN KEY (user_id) REFERENCES users(id_user)
);

DROP TABLE IF EXISTS questions;
CREATE TABLE questions (
  id_question INT(11) AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  content VARCHAR(255) NOT NULL,
  questionType INT NOT NULL,
  PRIMARY KEY (id_question)
);

DROP TABLE IF EXISTS answers;
CREATE TABLE answers (
  id_answer INT(11) AUTO_INCREMENT NOT NULL,
  user_answer VARCHAR(255) NOT NULL,
  is_final BOOLEAN NOT NULL,
  question_id INT(11) NOT NULL,
  quizz_id INT(11) NOT NULL,
  PRIMARY KEY (id_answer),
  FOREIGN KEY (question_id) REFERENCES questions(id_question),
  FOREIGN KEY (quizz_id) REFERENCES quizzes(id_quiz)
);

DROP TABLE IF EXISTS question_categories;
CREATE TABLE question_categories (
  id_question_category INT(11) AUTO_INCREMENT NOT NULL,
  question_id INT(11) NOT NULL,
  category_id INT(11) NOT NULL,
  PRIMARY KEY (id_question_category),
  FOREIGN KEY (question_id) REFERENCES questions(id_question),
  FOREIGN KEY (category_id) REFERENCES categories(id_category)
);

DROP TABLE IF EXISTS possible_answers;
CREATE TABLE possible_answers (
  id_possible_answer INT(11) AUTO_INCREMENT NOT NULL,
  value VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_possible_answer)
);

DROP TABLE IF EXISTS answers_question;
CREATE TABLE answers_question (
  id_answers_question INT(11) AUTO_INCREMENT NOT NULL,
  id_answers INT(11) NOT NULL,
  id_question INT(11) NOT NULL,
  correct_answer bool NOT NULL,
  PRIMARY KEY (id_answers_question),
  FOREIGN KEY (id_answers) REFERENCES possible_answers(id_possible_answer),
  FOREIGN KEY (id_question) REFERENCES questions(id_question)
);
SET FOREIGN_KEY_CHECKS=1;
