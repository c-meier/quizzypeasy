package models

import slick.memory.MemoryProfile.MappedColumnType

object QuestionType extends Enumeration {
  type QuestionType = Value
  val MultipleChoice, TrueOrFalse, Match = Value
}