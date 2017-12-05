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
import common.KeystoreKeys
import config.FrontendAppConfig
import controllers.eis.IsKnowledgeIntensiveController
import models.IsKnowledgeIntensiveModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.i18n.Messages
import play.api.test.Helpers._
import views.helpers.ViewSpec
import play.api.i18n.Messages.Implicits._

import scala.concurrent.Future

class IsKnowledgeIntensiveSpec extends ViewSpec {

  object TestController extends IsKnowledgeIntensiveController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  def setupMocks(isKnowledgeIntensiveModel: Option[IsKnowledgeIntensiveModel] = None): Unit =
    when(mockS4lConnector.fetchAndGetFormData[IsKnowledgeIntensiveModel](Matchers.eq(KeystoreKeys.isKnowledgeIntensive))
      (Matchers.any(), Matchers.any(),Matchers.any())).thenReturn(Future.successful(isKnowledgeIntensiveModel))

  "Verify that the isKnowledgeIntensive page contains the correct elements " +
    "when a valid IsKnowledgeIntensiveModel is passed as returned from storage" in new Setup {
    val document : Document = {
      setupMocks(Some(isKnowledgeIntensiveModelYes))
      val result = TestController.show.apply(authorisedFakeRequest)
      Jsoup.parse(contentAsString(result))
    }
    document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.IsCompanyKnowledgeIntensiveController.show().url
    document.title() shouldBe Messages("page.companyDetails.ApplyForKI.title")
    document.getElementById("main-heading").text() shouldBe Messages("page.companyDetails.ApplyForKI.heading")
    document.select("#isKnowledgeIntensive-yes").size() shouldBe 1
    document.select("#isKnowledgeIntensive-yes").size() shouldBe 1

    document.select("label[for=isKnowledgeIntensive-yes]").text() shouldBe Messages("common.radioYesLabel")
    document.select("label[for=isKnowledgeIntensive-no]").text() shouldBe Messages("common.radioNoLabel")
    document.select("legend").text() shouldBe Messages("page.companyDetails.ApplyForKI.heading")
    document.select("legend").hasClass("visuallyhidden") shouldBe true
    document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe false


    document.getElementById("next").text() shouldBe Messages("common.button.snc")
    //document.select(".error-summary").isEmpty shouldBe true
  }

  "Verify that isKnowledgeIntensive page contains the correct elements when an empty model " +
    "is passed because nothing was returned from storage" in new Setup {
    val document : Document = {
      setupMocks()
      val result = TestController.show.apply(authorisedFakeRequest)
      Jsoup.parse(contentAsString(result))
    }
    document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.IsCompanyKnowledgeIntensiveController.show().url
    document.title() shouldBe Messages("page.companyDetails.ApplyForKI.title")
    document.getElementById("main-heading").text() shouldBe Messages("page.companyDetails.ApplyForKI.heading")
    document.select("#isKnowledgeIntensive-yes").size() shouldBe 1
    document.select("#isKnowledgeIntensive-yes").size() shouldBe 1
    document.select("label[for=isKnowledgeIntensive-yes]").text() shouldBe Messages("common.radioYesLabel")
    document.select("label[for=isKnowledgeIntensive-no]").text() shouldBe Messages("common.radioNoLabel")
    document.select("legend").text() shouldBe Messages("page.companyDetails.ApplyForKI.heading")
    document.select("legend").hasClass("visuallyhidden") shouldBe true
    document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe false
    document.getElementById("next").text() shouldBe Messages("common.button.snc")
    //document.select(".error-summary").isEmpty shouldBe true
  }

  "Verify that IsKnowledgeIntensive page contains show the error summary when an invalid model (no radio button selection) is submitted" in new Setup {
    val document : Document = {
      // submit the model with no radio selected as a post action
      val result = TestController.submit.apply(authorisedFakeRequest)
      Jsoup.parse(contentAsString(result))
    }
    // Make sure we have the expected error summary displayed
    document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe true
    document.title() shouldBe Messages("page.companyDetails.ApplyForKI.title")
  }
}
