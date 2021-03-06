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

package controllers.seis

import auth._
import common.{Constants, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import controllers.Helpers.ControllerHelpers
import forms.SeventyPercentSpentForm._
import models.SeventyPercentSpentModel
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import views.html.seis.companyDetails.SeventyPercentSpent

import scala.concurrent.Future

object SeventyPercentSpentController extends SeventyPercentSpentController{
  override lazy val s4lConnector = S4LConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait SeventyPercentSpentController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  override val acceptedFlows = Seq(Seq(SEIS))

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>

    def routeRequest(backUrl: Option[String]) = {
      if (backUrl.isDefined) {
        s4lConnector.fetchAndGetFormData[SeventyPercentSpentModel](KeystoreKeys.seventyPercentSpent).map {
          case Some(data) => Ok(SeventyPercentSpent(seventyPercentSpentForm.fill(data), backUrl.getOrElse("")))
          case None => Ok(SeventyPercentSpent(seventyPercentSpentForm, backUrl.getOrElse("")))
        }
      }
      else Future.successful(Redirect(routes.QualifyBusinessActivityController.show()))
    }
    for {
      link <- ControllerHelpers.getSavedBackLink(KeystoreKeys.backLinkSeventyPercentSpent, s4lConnector)
      route <- routeRequest(link)
    } yield route
  }


  val submit = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    seventyPercentSpentForm.bindFromRequest().fold(
      formWithErrors => {
        ControllerHelpers.getSavedBackLink(KeystoreKeys.backLinkSeventyPercentSpent, s4lConnector).flatMap {
          case Some(data) => Future.successful(BadRequest(SeventyPercentSpent(formWithErrors, data)))
          case None => Future.successful(Redirect(routes.QualifyBusinessActivityController.show()))
        }
      },
      validFormData => {
        s4lConnector.saveFormData(KeystoreKeys.seventyPercentSpent, validFormData)
        validFormData.isSeventyPercentSpent match {
          case Constants.StandardRadioButtonYesValue => {
            s4lConnector.saveFormData(KeystoreKeys.backLinkShareIssueDate,
              routes.SeventyPercentSpentController.show().url)

            Future.successful(Redirect(routes.ShareIssueDateController.show()))
          }
          case Constants.StandardRadioButtonNoValue =>
            Future.successful(Redirect(routes.SeventyPercentSpentErrorController.show()))
        }
      }
    )
  }
}