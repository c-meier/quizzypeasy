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
  (2,'What is the type of the variable gg ?','var gg = 8.88',0);

INSERT INTO question_categories (id_question_category, category_id, question_id) VALUES
  (1,2,1),
  (2,2,2);

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




