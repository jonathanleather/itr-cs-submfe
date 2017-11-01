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

package views.seis

import auth.{MockConfig, MockAuthConnector}
import common.KeystoreKeys
import controllers.seis.{DateOfIncorporationController, routes}
import models.DateOfIncorporationModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import views.helpers.ViewSpec

import scala.concurrent.Future

class DateOfIncorporationSpec extends ViewSpec {
  
  object TestController extends DateOfIncorporationController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }
  
  def setupMocks(dateOfIncorporationModel: Option[DateOfIncorporationModel] = None): Unit =
    when(mockS4lConnector.fetchAndGetFormData[DateOfIncorporationModel](Matchers.eq(KeystoreKeys.dateOfIncorporation))
      (Matchers.any(), Matchers.any(),Matchers.any())).thenReturn(Future.successful(dateOfIncorporationModel))

  "The Date Of Incorporation page" should {

    "Verify that date of incorporation page contains the correct elements when a valid DateOfIncorporationModel is passed" in new SEISSetup {
      val document: Document = {
        setupMocks(Some(dateOfIncorporationModel))
        val result = TestController.show.apply(authorisedFakeRequest)
        Jsoup.parse(contentAsString(result))
      }
      document.title() shouldBe Messages("page.companyDetails.DateOfIncorporation.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.companyDetails.DateOfIncorporation.heading")
      document.body.getElementsByClass("form-hint").text should include(Messages("common.date.hint.example"))
      document.body.getElementById("incorporationDay").parent.text shouldBe Messages("common.date.fields.day")
      document.body.getElementById("incorporationMonth").parent.text shouldBe Messages("common.date.fields.month")
      document.body.getElementById("incorporationYear").parent.text shouldBe Messages("common.date.fields.year")
      document.body.getElementById("question-date-text-legend-id").hasClass("visuallyhidden") shouldBe true
      document.body.getElementById("question-date-text-legend-id").text shouldBe Messages("page.companyDetails.DateOfIncorporation.heading")
      document.body.getElementById("help").hasClass("summary") shouldBe true
      document.body.getElementById("help").text shouldBe Messages("common.help.whereToFind")
      document.body.getElementById("date-of-incorporation-where-to-find").parent.text should include
        Messages("page.companyDetails.DateOfIncorporation.location")
      document.body.getElementById("company-house-db").text() shouldEqual getExternalLinkText(Messages("page.companyDetails.DateOfIncorporation.companiesHouse"))
      document.body.getElementById("company-house-db").attr("href") shouldEqual "https://www.gov.uk/get-information-about-a-company"
      document.body.getElementById("company-house-db").hasClass("external-link") shouldBe true
      document.getElementById("next").text() shouldBe Messages("common.button.snc")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.NatureOfBusinessController.show().url
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.details.one")
    }

    "Verify that the date of incorporation page contains the correct elements when an invalid DateOfIncorporationModel is passed" in new SEISSetup {
      val document: Document = {
        setupMocks()
        val result = TestController.submit.apply(authorisedFakeRequest)
        Jsoup.parse(contentAsString(result))
      }
      document.title() shouldBe Messages("page.companyDetails.DateOfIncorporation.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.companyDetails.DateOfIncorporation.heading")
      document.body.getElementsByClass("form-hint").text should include(Messages("common.date.hint.example"))
      document.body.getElementById("incorporationDay").parent.text shouldBe Messages("common.date.fields.day")
      document.body.getElementById("incorporationMonth").parent.text shouldBe Messages("common.date.fields.month")
      document.body.getElementById("incorporationYear").parent.text shouldBe Messages("common.date.fields.year")
      document.body.getElementById("question-date-text-legend-id").hasClass("visuallyhidden") shouldBe true
      document.body.getElementById("question-date-text-legend-id").text shouldBe Messages("page.companyDetails.DateOfIncorporation.heading")
      document.body.getElementById("help").hasClass("summary") shouldBe true
      document.body.getElementById("help").text shouldBe Messages("common.help.whereToFind")
      document.body.getElementById("date-of-incorporation-where-to-find").parent.text should include
        Messages("page.companyDetails.DateOfIncorporation.location")
      document.body.getElementById("company-house-db").text() shouldEqual getExternalLinkText(Messages("page.companyDetails.DateOfIncorporation.companiesHouse"))
      document.body.getElementById("company-house-db").attr("href") shouldEqual "https://www.gov.uk/get-information-about-a-company"
      document.body.getElementById("company-house-db").hasClass("external-link") shouldBe true
      document.getElementById("next").text() shouldBe Messages("common.button.snc")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.NatureOfBusinessController.show().url
      document.body.getElementById("progress-section").text shouldBe  Messages("common.section.progress.details.one")
      document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe true
      document.getElementById("error-summary-heading").text shouldBe Messages("common.error.summary.heading")
      document.getElementById("incorporationDay-error-summary").text shouldBe Messages("validation.error.DateNotEntered")
      document.getElementsByClass("error-notification").text shouldBe Messages("validation.error.DateNotEntered")

    }

  }

}
