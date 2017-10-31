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

package views.eis

import auth.{MockConfig, MockAuthConnector}
import common.{Constants, KeystoreKeys}
import config.FrontendAppConfig
import controllers.eis.NewGeographicalMarketController
import models._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import views.helpers.ViewSpec

import scala.concurrent.Future

class NewGeographicalMarketSpec extends ViewSpec {

  object TestController extends NewGeographicalMarketController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  def setupMocks(backLink: Option[String] = None, newGeographicalMarketModel: Option[NewGeographicalMarketModel] = None): Unit = {
    when(mockS4lConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backLinkNewGeoMarket))(Matchers.any(), Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(backLink))
    when(mockS4lConnector.fetchAndGetFormData[NewGeographicalMarketModel](Matchers.eq(KeystoreKeys.newGeographicalMarket))
      (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(newGeographicalMarketModel))
  }

  "The NewGeographicalMarket Page" should {
    "Verify that the correct elements are loaded when navigating from TotalAmountRaised page" in new Setup {
      val document: Document = {
        setupMocks(Some(controllers.eis.routes.TotalAmountRaisedController.show().url), Some(newGeographicalMarketModelYes))
        val result = TestController.show.apply(authorisedFakeRequestToPOST("isNewGeographicalMarket" -> Constants.StandardRadioButtonYesValue))
        Jsoup.parse(contentAsString(result))
      }
      document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.TotalAmountRaisedController.show().url
      document.title() shouldBe Messages("page.investment.NewGeographicalMarket.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.investment.NewGeographicalMarket.heading")
      document.select("#isNewGeographicalMarket-yes").size() shouldBe 1
      document.select("#isNewGeographicalMarket-no").size() shouldBe 1
      document.getElementById("isNewGeographicalMarket-yesLabel").text() shouldBe Messages("common.radioYesLabel")
      document.getElementById("isNewGeographicalMarket-noLabel").text() shouldBe Messages("common.radioNoLabel")
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.details.three")
      document.body.getElementById("external-hint").text shouldBe
        s"${Messages("page.investment.NewGeographicalMarket.hint")} ${Messages("page.investment.NewGeographicalMarket.hint.link")} opens in a new window"
      document.getElementById("next").text() shouldBe Messages("common.button.snc")
      document.getElementById("isNewGeographicalMarket-legend").hasClass("visuallyhidden") shouldBe true
      document.getElementById("isNewGeographicalMarket-legend").text shouldBe Messages("page.investment.NewGeographicalMarket.legend")
    }

    "Verify that the correct elements are loaded when navigating from UsedInvestmentReasonBefore page" in new Setup {
      val document: Document = {
        setupMocks(Some(controllers.eis.routes.UsedInvestmentReasonBeforeController.show().url), Some(newGeographicalMarketModelYes))
        val result = TestController.show.apply(authorisedFakeRequest)
        Jsoup.parse(contentAsString(result))
      }
      document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.UsedInvestmentReasonBeforeController.show().url
      document.title() shouldBe Messages("page.investment.NewGeographicalMarket.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.investment.NewGeographicalMarket.heading")
      document.select("#isNewGeographicalMarket-yes").size() shouldBe 1
      document.select("#isNewGeographicalMarket-no").size() shouldBe 1
      document.getElementById("isNewGeographicalMarket-yesLabel").text() shouldBe Messages("common.radioYesLabel")
      document.getElementById("isNewGeographicalMarket-noLabel").text() shouldBe Messages("common.radioNoLabel")
      document.body.getElementById("external-hint").text shouldBe
        s"${Messages("page.investment.NewGeographicalMarket.hint")} ${Messages("page.investment.NewGeographicalMarket.hint.link")} opens in a new window"
      document.getElementById("next").text() shouldBe Messages("common.button.snc")
      document.getElementById("isNewGeographicalMarket-legend").hasClass("visuallyhidden") shouldBe true
      document.getElementById("isNewGeographicalMarket-legend").text shouldBe Messages("page.investment.NewGeographicalMarket.legend")
      document.select(".error-summary").isEmpty shouldBe true
    }

    "Verify that the correct elements are loaded when navigating from PreviousBeforeDOFCS page" in new Setup {
      val document: Document = {
        setupMocks(Some(controllers.eis.routes.PreviousBeforeDOFCSController.show().url), Some(newGeographicalMarketModelYes))
        val result = TestController.show.apply(authorisedFakeRequest)
        Jsoup.parse(contentAsString(result))
      }
      document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.PreviousBeforeDOFCSController.show().url
      document.title() shouldBe Messages("page.investment.NewGeographicalMarket.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.investment.NewGeographicalMarket.heading")
      document.select("#isNewGeographicalMarket-yes").size() shouldBe 1
      document.select("#isNewGeographicalMarket-no").size() shouldBe 1
      document.getElementById("isNewGeographicalMarket-yesLabel").text() shouldBe Messages("common.radioYesLabel")
      document.getElementById("isNewGeographicalMarket-noLabel").text() shouldBe Messages("common.radioNoLabel")
      document.body.getElementById("external-hint").text shouldBe
        s"${Messages("page.investment.NewGeographicalMarket.hint")} ${Messages("page.investment.NewGeographicalMarket.hint.link")} opens in a new window"
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.details.three")
      document.getElementById("next").text() shouldBe Messages("common.button.snc")
      document.getElementById("isNewGeographicalMarket-legend").hasClass("visuallyhidden") shouldBe true
      document.getElementById("isNewGeographicalMarket-legend").text shouldBe Messages("page.investment.NewGeographicalMarket.legend")
      document.select(".error-summary").isEmpty shouldBe true
    }

    "Verify that NewGeographicalMarket page contains error summary when no model is submitted" in new Setup {
      val document: Document = {
        setupMocks(Some(controllers.eis.routes.UsedInvestmentReasonBeforeController.show().url))
        val result = TestController.submit.apply(authorisedFakeRequest)
        Jsoup.parse(contentAsString(result))
      }
      // Make sure we have the expected error summary displayed
      document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe true
      document.getElementById("error-summary-heading").text shouldBe Messages("common.error.summary.heading")
      document.getElementById("isNewGeographicalMarket-error-summary").text shouldBe Messages("validation.common.error.fieldRequired")
      document.getElementsByClass("error-notification").text shouldBe Messages("validation.common.error.fieldRequired")
      document.title() shouldBe Messages("page.investment.NewGeographicalMarket.title")
    }
  }
}
