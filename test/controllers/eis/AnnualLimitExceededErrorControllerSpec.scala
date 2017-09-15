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
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import controllers.helpers.BaseSpec
import play.api.test.Helpers._

class AnnualLimitExceededErrorControllerSpec extends BaseSpec {

  object TestController extends AnnualLimitExceededErrorController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
    override lazy val s4lConnector = mockS4lConnector
  }

 "AnnualLimitExceededErrorController" should {
    "use the correct auth connector" in {
      AnnualLimitExceededErrorController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      AnnualLimitExceededErrorController.enrolmentConnector shouldBe EnrolmentConnector
    }
	"use the correct keystore connector" in {
      AnnualLimitExceededErrorController.s4lConnector shouldBe S4LConnector
    }
	"use the correct application config" in {
      AnnualLimitExceededErrorController.applicationConfig shouldBe FrontendAppConfig
    }
  }

  "Sending a GET request to AnnualLimitExceededErrorController when authenticated and enrolled" should {
    "return a 200 OK" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending submission to AnnualLimitExceededErrorController when authenticated and enrolled" should {
    "redirect to correct page" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit)(
        result => {
		  status(result) shouldBe SEE_OTHER
          //TODO: change to match target options with mocking page page when available
          redirectLocation(result) shouldBe Some(routes.InvestmentGrowController.show().url)
        }
      )
    }
  }

}