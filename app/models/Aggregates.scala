package models

case class FullQuizzQuestion(
                            category: Category,
                            quiz: Quiz,
                            question: Question,
                            userAnswer: Answer,
                            possibleAnswer: Seq[(PossibleAnswer, Boolean)],
                            )