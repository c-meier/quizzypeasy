package models

case class FullQuizzQuestion(
                              category: Category,
                              quiz: Quiz,
                              question: Question,
                              userAnswer: Answer,
                              possibleAnswers: Seq[(PossibleAnswer, Boolean)],
                            )