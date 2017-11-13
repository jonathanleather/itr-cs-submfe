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

package forms

import models.NumberOfSharesModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import forms.NumberOfSharesForm._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

class NumberOfSharesFormSpec extends UnitSpec with OneAppPerSuite {

  val minimumValue:Int = 1
  val maxLengthValue = "123565666.222222222222222"
  val overMaxLengthValue:String = maxLengthValue + "1"

  "The NumberOfSharesForm" when {

    "provided with a model" should {
      val model = NumberOfSharesModel(12.3)
      lazy val form = numberOfSharesForm.fill(model)

      "return a valid map" in {
        form.data shouldBe Map("numberOfShares" -> "12.3")
      }
    }

    "provided with a valid map with the maximum number of decimal places" should {
      val map = Map("numberOfShares" -> "1.1111111111111")
      lazy val form = numberOfSharesForm.bind(map)

      "contain no errors" in {
        form.errors.isEmpty shouldBe true
      }

      "contain the correct model limited to 5 decimal places" in {
        form.value shouldBe Some(NumberOfSharesModel(1.11111))
      }
    }

    "provided with a valid map with the minimum amount" should {
      val map = Map("numberOfShares" -> "1.00000000000")
      lazy val form = numberOfSharesForm.bind(map)

      "contain no errors" in {
        form.errors.isEmpty shouldBe true
      }

      "contain the correct model limited to 5 decimal places" in {
        form.value shouldBe Some(NumberOfSharesModel(1.00000))
      }
    }

    "provided with a valid map with the maximum size" should {
      val map = Map("numberOfShares" -> "9999999999999")
      lazy val form = numberOfSharesForm.bind(map)

      "contain no errors" in {
        form.errors.isEmpty shouldBe true
      }

      "contain the correct model" in {
        form.value shouldBe Some(NumberOfSharesModel(9999999999999.0))
      }
    }


    "provided with an invalid map which is at max length" should {
      val map = Map("numberOfShares" -> s"$maxLengthValue")
      lazy val form = numberOfSharesForm.bind(map)

      "contain one error" in {
        form.errors.size shouldBe 0
      }
    }

    "provided with an invalid map which is above max length" should {
      val map = Map("numberOfShares" -> s"{$overMaxLengthValue")
      lazy val form = numberOfSharesForm.bind(map)

      "contain one error" in {
        form.errors.size shouldBe 2
      }

      "contain the too large error message" in {
        form.errors.head.message shouldBe "error.maxLength"
      }
    }


    "provided with an invalid map which is too large" should {
      val map = Map("numberOfShares" -> "9999999999999.1")
      lazy val form = numberOfSharesForm.bind(map)

      "contain one error" in {
        form.errors.size shouldBe 1
      }

      "contain the too large error message" in {
        form.errors.head.message shouldBe Messages("validation.error.numberOfShares.size", minimumValue)
      }
    }

    "provided with an invalid map with a non-numeric value" should {
      val map = Map("numberOfShares" -> "a")
      lazy val form = numberOfSharesForm.bind(map)

      "contain one error" in {
        form.errors.size shouldBe 1
      }

      "contain the not a number error message" in {
        form.errors.head.message shouldBe Messages("validation.error.numberOfShares.notANumber")
      }
    }

    "provided with an invalid map with an empty value" should {
      val map = Map("numberOfShares" -> " ")
      lazy val form = numberOfSharesForm.bind(map)

      "contain one error" in {
        form.errors.size shouldBe 1
      }

      "contain the not a number error message" in {
        form.errors.head.message shouldBe "error.required"
      }
    }
  }
}
