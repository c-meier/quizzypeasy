package models

import java.time.LocalDateTime

import models.QuestionType.QuestionType

case class User(
                 id: Option[Long],
                 name: String,
                 password: String,
                 dateInscription: LocalDateTime,
                 isAdmin: Boolean)

case class Category(
                   id: Option[Long],
                   name: String,
                   description: String
                   )

case class Question(
                   id: Option[Long],
                   name: String,
                   content: String,
                   questionType: QuestionType
                   )

case class PossibleAnswer(
                           id: Option[Long],
                           value: String
                         )

case class Answer(
                 id: Option[Long],
                 userAnswer: String,
                 isFinal: Boolean,
                 questionId: Long,
                 quizId: Long
                 )

case class QuestionCategory(id: Option[Long],
                            questionId: Long,
                            categoryId: Long
                           )

case class AnswersQuestion(id: Option[Long],
                           answerId: Long,
                           questionId: Long,
                           correctAnswer: Boolean
                          )

case class Quiz(
                 id: Option[Long],
                 score: Int,
                 categoryId: Long,
                 userId: Long
               )


