USE quizzypeasy;
INSERT INTO users (user_name, password, date_inscription, is_admin) VALUES
  ('test','test',now(),FALSE),
  ('daniel','daniel',now(),TRUE);

INSERT INTO categories (id_category, category_name, description) VALUES
  (1,'heig-vd','Question about the HEIG-VD'),
  (2,'scala','Question about SCALA language');

/**
  questionType :
    1 -> multiple choice
    2 -> True/False
  */
INSERT INTO questions (id_question,name,content,questionType) VALUES
  (1,'Scala variables','What are the Scala variables ?',1),
  (2,'What is the type of the variable gg ?','var gg = 8.88',1);

INSERT INTO possible_answers (id_possible_answer, value) VALUES
  (1,'var'),
  (2,'val'),
  (3,'ver'),
  (4,'let');

INSERT INTO answers_question (id_answers, id_question, correct_answer) VALUES
  (1,1,TRUE),
  (2,1,TRUE),
  (3,1,FALSE),
  (4,1,FALSE);




