package util

import views.html

object CustomHelpers {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(html.customFieldConstructorTemplate.f)
}
