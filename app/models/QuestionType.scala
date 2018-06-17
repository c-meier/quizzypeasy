package models

object QuestionType extends Enumeration {
  type QuestionType = Value
  val MultipleChoice, TrueOrFalse, Match /*, MultipleResponse */ = Value
}