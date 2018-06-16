USE quizzypeasy;
INSERT INTO users (user_name, password, date_inscription, is_admin) VALUES
  ('test','test',now(),FALSE),
  ('daniel','daniel',now(),TRUE);

INSERT INTO categories (id_category, category_name, description) VALUES
  (1,'heig-vd','There is always better...'),
  (2,'scala','If I were to pick another language to use today other than Java, it would be SCALA - James Gosling');

/**
  questionType :
    1 -> multiple choice
    2 -> True/False
  */
INSERT INTO questions (id_question,name,content,question_type) VALUES
  (1,'Scala variables','What are the Scala variables ?',0),
  (2,'What is the type of the variable gg ?','var gg = 8.88',2),
  (3, 'Simple arithmetics', 'Does 3 / 2 = 1.5 ?', 1);

INSERT INTO question_categories (id_question_category, category_id, question_id) VALUES
  (1,2,1),
  (2,2,2),
  (3,2,3);

INSERT INTO possible_answers (id_possible_answer, value) VALUES
  (1,'True'),
  (2,'False'),
  (3,'var'),
  (4,'valeur'),
  (5,'ver'),
  (6,'let'),
  (7, 'float');

INSERT INTO answers_question (id_answers, id_question, correct_answer) VALUES
  (3,1,TRUE),
  (4,1,FALSE),
  (5,1,FALSE),
  (6,1,FALSE),
  (7,2,TRUE),
  (1,3,FALSE),
  (2,3,TRUE);




