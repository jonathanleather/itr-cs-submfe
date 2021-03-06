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

package auth

import connectors.{EnrolmentConnector, S4LConnector}
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

object AuthEnrolledTestController extends AuthEnrolledTestController with MockitoSugar {
  override lazy val applicationConfig = mockConfig
  override lazy val authConnector = mockAuthConnector
  override lazy val enrolmentConnector = mock[EnrolmentConnector]
  override lazy val s4lConnector = mock[S4LConnector]
  override lazy val acceptedFlows = Seq()
}

trait AuthEnrolledTestController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  val authorisedAsyncAction = AuthorisedAndEnrolled.async {
    implicit user =>  implicit request => Future.successful(Ok)
  }

  val authorisedAction = AuthorisedAndEnrolled {
    implicit user =>  implicit request => Ok
  }

}
