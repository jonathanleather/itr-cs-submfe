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

package controllers.eis

import auth.{MockAuthConnector, MockConfig}
import common.{Constants, KeystoreKeys}
import config.FrontendAuthConnector
import connectors.{EnrolmentConnector, S4LConnector}
import controllers.helpers.BaseSpec
import models.NewGeographicalMarketModel
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.Future

class NewGeographicalMarketControllerSpec extends BaseSpec {

  object TestController extends NewGeographicalMarketController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  "NewGeographicalMarketController" should {
    "use the correct keystore connector" in {
      NewGeographicalMarketController.s4lConnector shouldBe S4LConnector
    }
    "use the correct auth connector" in {
      NewGeographicalMarketController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      NewGeographicalMarketController.enrolmentConnector shouldBe EnrolmentConnector
    }
  }

  def setupMocks(newGeographicalMarketModel: Option[NewGeographicalMarketModel] = None, backLink: Option[String] = None): Unit = {
    when(mockS4lConnector.fetchAndGetFormData[NewGeographicalMarketModel](Matchers.any())(Matchers.any(), Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(newGeographicalMarketModel))
    when(mockS4lConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.backLinkNewGeoMarket))(Matchers.any(), Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(backLink))
  }

  "Sending a GET request to NewGeographicalMarketController when authenticated and enrolled" should {
    "return a OK when something is fetched from keystore" in {
      setupMocks(Some(newGeographicalMarketModelYes), Some(routes.TotalAmountRaisedController.show().url))
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a OK when nothing is fetched using keystore when authenticated and enrolled"  in {
      setupMocks(backLink = Some(routes.TotalAmountRaisedController.show().url))
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }

    "REDIRECT when no back link is fetched using keystore when authenticated and enrolled" in {
      setupMocks()
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.eis.routes.TotalAmountRaisedController.show().url)
        }
      )
    }
  }

  "Sending a valid 'Yes' form submit to the NewGeographicalMarketController when authenticated and enrolled" should {
    "redirect to the NewProductMarket page" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isNewGeographicalMarket" -> Constants.StandardRadioButtonYesValue
      submitWithSessionAndAuth(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.eis.routes.NewProductController.show().url)
        }
      )
    }
  }

  "Sending a valid 'No' form submit to the NewGeographicalMarketController when authenticated and enrolled" should {
    "redirect the NewProductMarket" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isNewGeographicalMarket" -> Constants.StandardRadioButtonNoValue
      submitWithSessionAndAuth(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.eis.routes.NewProductController.show().url)
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the NewGeographicalMarketController " +
    "with no backlink and when authenticated and enrolled" should {
    "redirect to TotalAmountRaised page" in {
      setupMocks()
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isNewGeographicalMarket" -> ""
      submitWithSessionAndAuth(TestController.submit, formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.eis.routes.TotalAmountRaisedController.show().url)
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the NewGeographicalMarketController when authenticated and enrolled" should {
    "respond with as a bad request with form errors" in {
      setupMocks(backLink = Some(routes.TotalAmountRaisedController.show().url))
      mockEnrolledRequest(eisSchemeTypesModel)
      val formInput = "isNewGeographicalMarket" -> ""
      submitWithSessionAndAuth(TestController.submit, formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }
}
