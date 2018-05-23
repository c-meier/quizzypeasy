package models

import java.util.Date

case class User(
                 id: Option[Long],
                 name: String,
                 hash: String,
                 salt: String,
                 dateInscription: Date,
                 isAdmin: Boolean)

case class Quiz(
               id: Option[Long],
               score: Int,
               categoryId: Long,
               userId: Option[Long]
               )

case class Category(
                   id: Option[Long],
                   name: String,
                   description: String
                   )

case class Question(
                   id: Option[Long],
                   name: String,
                   content: String,
                   questionType: QuestionType.type,
                   correctAnswerId: Long
                   )

case class PossibleAnswer(
                           id: Option[Long],
                           value: String,
                           questionId: Option[Long]
                         )

case class Answer(
                 userAnswer: String,
                 isFinal: Boolean,
                 questionId: Long,
                 quizId: Long
                 )

case class QuestionCategory(id: Option[Long],
                            questionId: Long,
                            categoryId: Long)


