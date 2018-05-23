package models

import java.util.Date

import models.QuestionType.QuestionType

case class User(
                 id: Option[Long],
                 name: String,
                 password: String,
                 dateInscription: Date,
                 isAdmin: Boolean)

case class Quiz(
               id: Option[Long],
               score: Option[Int],
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
                   questionType: QuestionType,
                   correctAnswerId: Long
                   )

case class PossibleAnswer(
                           id: Option[Long],
                           value: String,
                           questionId: Option[Long]
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
                            categoryId: Long)


