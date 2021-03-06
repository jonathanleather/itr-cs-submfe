/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.helpers

object MockDataGenerator {

  val random = new scala.util.Random

  def randomString(alphabet: String)(n: Int): String =
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

  def randomNumberString(n: Int): String =
    randomString("1234567890")(n)

  def randomWordString(n: Int): String =
    randomString("abcdefg  hijklmn $%^&*() opqrstuvwxyzABCD EFGHI$  JKLM  NOPQRS TUVW  XYZ&")(n)


  def randomAlphanumericString(n: Int): String =
    randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)
}
