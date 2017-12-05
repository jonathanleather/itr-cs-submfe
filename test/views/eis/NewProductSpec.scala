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
import controllers.eis.NewProductController
import models.NewProductModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.i18n.Messages
import play.api.test.Helpers._
import views.helpers.ViewSpec
import play.api.i18n.Messages.Implicits._
import scala.concurrent.Future

class NewProductSpec extends ViewSpec {

  object TestController extends NewProductController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val submissionConnector = mockSubmissionConnector
  }

  def setupMocks(newProductModel: Option[NewProductModel] = None): Unit =
    when(mockS4lConnector.fetchAndGetFormData[NewProductModel](Matchers.eq(KeystoreKeys.newProduct))
      (Matchers.any(), Matchers.any(),Matchers.any())).thenReturn(Future.successful(newProductModel))

  "Verify that the NewProduct page contains the correct elements " +
    "when a valid NewProductModel is passed as returned from keystore" in new Setup {
    val document : Document = {
      setupMocks(Some(newProductMarketModelYes))
      val result = TestController.show.apply(authorisedFakeRequest)
      Jsoup.parse(contentAsString(result))
    }
    document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.NewGeographicalMarketController.show().url
    document.title() shouldBe Messages("page.investment.NewProduct.title")
    document.getElementById("main-heading").text() shouldBe Messages("page.investment.NewProduct.heading")
    document.select("#isNewProduct-yes").size() shouldBe 1
    document.select("#isNewProduct-no").size() shouldBe 1
    document.select("label[for=isNewProduct-yes]").text() shouldBe Messages("common.radioYesLabel")
    document.select("label[for=isNewProduct-no]").text() shouldBe Messages("common.radioNoLabel")
    document.select("legend").text() shouldBe Messages("page.investment.NewProduct.heading")
    document.select("legend").hasClass("visuallyhidden") shouldBe true
    document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe false

    document.body.getElementById("external-hint").text shouldBe
      s"${Messages("page.investment.NewProduct.hint")} ${Messages("page.investment.NewProduct.hint.link")} opens in a new window"
    document.getElementById("next").text() shouldBe Messages("common.button.snc")
  }

  "Verify that NewProduct page contains the correct elements when an empty model " +
    "is passed because nothing was returned from keystore" in new Setup {
    val document : Document = {
      setupMocks()
      val result = TestController.show.apply(authorisedFakeRequest)
      Jsoup.parse(contentAsString(result))
    }
    document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.NewGeographicalMarketController.show().url
    document.title() shouldBe Messages("page.investment.NewProduct.title")
    document.getElementById("main-heading").text() shouldBe Messages("page.investment.NewProduct.heading")
    document.select("#isNewProduct-yes").size() shouldBe 1
    document.select("#isNewProduct-no").size() shouldBe 1
    document.select("label[for=isNewProduct-yes]").text() shouldBe Messages("common.radioYesLabel")
    document.select("label[for=isNewProduct-no]").text() shouldBe Messages("common.radioNoLabel")
    document.select("legend").text() shouldBe Messages("page.investment.NewProduct.heading")
    document.select("legend").hasClass("visuallyhidden") shouldBe true
    document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe false

    document.body.getElementById("external-hint").text shouldBe
      s"${Messages("page.investment.NewProduct.hint")} ${Messages("page.investment.NewProduct.hint.link")} opens in a new window"
    document.getElementById("next").text() shouldBe Messages("common.button.snc")
  }

  "Verify that NewProduct page contains error summary when invalid model is submitted" in new Setup {
    val document : Document = {
      setupMocks()
      // submit the model with no radio selected as a post action
      val result = TestController.submit.apply(authorisedFakeRequest)
      Jsoup.parse(contentAsString(result))
    }
    // Make sure we have the expected error summary displayed
    document.getElementById("error-summary-display").hasClass("error-summary--show") shouldBe true
    document.getElementById("error-summary-heading").text shouldBe Messages("common.error.summary.heading")
    document.getElementById("isNewProduct-error-summary").text shouldBe Messages("validation.common.error.fieldRequired")
    document.getElementsByClass("error-notification").text shouldBe Messages("validation.common.error.fieldRequired")
    document.title() shouldBe Messages("page.investment.NewProduct.title")
  }
}
