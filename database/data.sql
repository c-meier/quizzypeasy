USE quizzypeasy;
INSERT INTO users (user_name, password, date_inscription, is_admin) VALUES
  ('admin',	'$2a$10$xYTE4i3hs0LoFJiA/3zoheS/7C1tQdmBLTTu4MpqDXryOWHOy6aeW', '2018-06-17 12:12:20', TRUE);

INSERT INTO categories (id_category, category_name, description) VALUES
  (1,'heig-vd','There is always better...'),
  (2,'scala','If I were to pick another language to use today other than Java, it would be SCALA - James Gosling');

/**
  questionType :
    0 -> multiple choice
    1 -> True/False
  */
INSERT INTO questions (id_question,name,content,question_type) VALUES
  (1,'Scala variables','How to declare a scala variable ?',0),
  (2,'What is the type of the variable gg ?','var gg = 8.88',2),
  (3, 'Simple arithmetics', 'Does 3 / 2 = 1.5 ?', 1),
  (4,'SCALA','What SCALA means ?',0),
  (5,'SCALA','Is scala a Statically-Typed language or a Dynamically-Type language ?',0),
  (6,'Pure or not pure','Is Scala a Pure OOP Language?',1),
  (7,'Functionnal or not Functionnal','Does Scala support all Functional Programming concepts?',1),
  (8,'Motto','What is the main motto of Scala Language?',2),
  (9,'Accessor','What is default access modifier in Scala?',0),
  (10,'Dude...really, Rentsch question like','What is one of the similarity between Scala’s Int and Java’s java.lang.Integer?',0),
  (11,'var and val','In SCALA, var stands for variable and val for value',1),
  (12, 'Architectural knowledge', 'Which story is skipped at the Cheseau site ?', 2),
  (13,'Maps', 'Immutable maps are the default maps if no explicit import ?',1);

INSERT INTO question_categories (id_question_category, category_id, question_id) VALUES
  (1,2,1),
  (2,2,2),
  (3,2,3),
  (4,1,4),
  (5,2,4),
  (6,2,5),
  (7,2,6),
  (8,2,7),
  (9,2,8),
  (10,2,9),
  (11,2,10),
  (12,2,11),
  (13,2,13);

INSERT INTO possible_answers (id_possible_answer, value) VALUES
  (1,'True'),
  (2,'False'),
  (3,'var'),
  (4,'valeur'),
  (5,'ver'),
  (6,'let'),
  (7, 'float'),
  (8, 'i|I'),
  (9,'Scalable Language'),
  (10, 'Sarcatstic Language'),
  (11, 'Scared Leeroy'),
  (12, 'Dynamically-Typed'),
  (13, 'Statically-Typed'),
  (14, 'Do More With Less( Code)?'),
  (15,'public'),
  (16,'private'),
  (17,'protected'),
  (18,'Both are 32-bit signed integers.'),
  (19,'Both are 64-bit signed integers.'),
  (20,'Like I care');

INSERT INTO answers_question (id_answers, id_question, correct_answer) VALUES
  (3,1,TRUE),
  (4,1,FALSE),
  (5,1,FALSE),
  (6,1,FALSE),
  (7,2,TRUE),
  (1,3,FALSE),
  (2,3,TRUE),
  (8,12,TRUE),
  (9,4,TRUE),
  (10,4,FALSE),
  (11,4,FALSE),
  (12,5,FALSE),
  (13,5,TRUE),
  (1,6,TRUE),
  (2,6,FALSE),
  (1,7,TRUE),
  (2,7,FALSE),
  (14,8,TRUE),
  (15,9,TRUE),
  (16,9,FALSE),
  (17,9,FALSE),
  (18,10,TRUE),
  (19,10,FALSE),
  (20,10,TRUE),
  (1,11,TRUE),
  (2,11,FALSE),
  (1,13,TRUE),
  (2,13,FALSE);




