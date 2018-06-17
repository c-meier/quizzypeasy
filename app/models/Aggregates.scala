package models

case class FullQuizzQuestion(
                              category: Category,
                              quiz: Quiz,
                              question: Question,
                              userAnswer: Answer,
                              possibleAnswers: Seq[(PossibleAnswer, Boolean)],
                            ) {
  lazy val isCorrect: Boolean = {
    val correctAnswers = possibleAnswers.filter(_._2)
    question.questionType match {
      case QuestionType.MultipleChoice | QuestionType.TrueOrFalse =>
        correctAnswers.exists(_._1.value == userAnswer.userAnswer)
      case QuestionType.Match =>
        correctAnswers.exists(c => userAnswer.userAnswer.matches(c._1.value))
    }
  }
}