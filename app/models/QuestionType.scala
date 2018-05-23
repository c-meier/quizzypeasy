package models

import slick.memory.MemoryProfile.MappedColumnType

object QuestionType extends Enumeration {
  type QuestionType = Value
  val MultipleChoice = Value("MultipleChoice")
  val TrueOrFalse = Value("TrueOrFalse")
  val Match = Value("Match")

  implicit val questionTypeMapper = MappedColumnType.base[QuestionType, String](
    e => e.toString,
    s => QuestionType.withName(s)
  )
}

