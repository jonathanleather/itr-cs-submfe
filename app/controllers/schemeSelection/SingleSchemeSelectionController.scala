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

package controllers.schemeSelection

import auth.AuthorisedAndEnrolledForTAVC
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{AdvancedAssuranceConnector, EnrolmentConnector, S4LConnector}
import controllers.predicates.FeatureSwitch
import play.api.mvc.{Action, AnyContent, Request, Result}
import forms.schemeSelection.SingleSchemeSelectionForm._
import models.submission.{SchemeTypesModel, SingleSchemeTypesModel}
import play.api.Logger
import views.html.schemeSelection.SingleSchemeSelection
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

object SingleSchemeSelectionController extends SingleSchemeSelectionController {
  override lazy val enrolmentConnector = EnrolmentConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val s4lConnector = S4LConnector
  val advancedAssuranceConnector = AdvancedAssuranceConnector
}

trait SingleSchemeSelectionController extends FrontendController with AuthorisedAndEnrolledForTAVC with FeatureSwitch {

  val advancedAssuranceConnector: AdvancedAssuranceConnector
  override val acceptedFlows = Seq()

  def show(): Action[AnyContent] = AuthorisedAndEnrolled.async { implicit user => implicit request =>

    def continue = {
      s4lConnector.fetchAndGetFormData[SchemeTypesModel](KeystoreKeys.selectedSchemes).map {
        case Some(scheme) => Ok(SingleSchemeSelection(singleSchemeSelectionForm.fill(SingleSchemeTypesModel.convertToSingleScheme(scheme))))
        case _ => Ok(SingleSchemeSelection(singleSchemeSelectionForm))
      }
    }

    for{
      aaInProgress <- advancedAssuranceConnector.getAdvancedAssuranceApplication()
      result <-continue
    } yield if(aaInProgress) Redirect(controllers.routes.HomeController.redirectToHub()) else result
  }

  def submit(): Action[AnyContent] = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    def continue = singleSchemeSelectionForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(SingleSchemeSelection(formWithErrors)))

      },
      validFormData => {
        val convertedData = SingleSchemeTypesModel.convertFromSingleScheme(validFormData)
        (for {
          saveSchemes <- s4lConnector.saveFormData(KeystoreKeys.selectedSchemes, convertedData)
          saveApplication <- s4lConnector.saveFormData(KeystoreKeys.applicationInProgress, true)
        } yield (saveSchemes, saveApplication)).map {
          result => routeToScheme(convertedData, validFormData)
        }.recover {
          case e: Exception => Logger.warn(s"[SingleSchemeSelectionController][submit] Error when calling saveFormData: ${e.getMessage}")
            routeToScheme(convertedData, validFormData)
        }
      }
    )

    for{
      aaInProgress <- advancedAssuranceConnector.getAdvancedAssuranceApplication()
      result <-continue
    } yield if(aaInProgress) Redirect(controllers.routes.HomeController.redirectToHub()) else result
  }

  private def routeToScheme(schemeTypesModel: SchemeTypesModel, singleSchemeTypesModel: SingleSchemeTypesModel)
                           (implicit request: Request[AnyContent]): Result = {
    schemeTypesModel match {
      //EIS Flow
      case SchemeTypesModel(true,false,false,false) => Redirect(controllers.eis.routes.InitialDeclarationController.show().url)
      //SEIS Flow
      case SchemeTypesModel(false,true,false,false) => Redirect(controllers.seis.routes.InitialDeclarationController.show().url)
      //Invalid Flow
      case _ => BadRequest(SingleSchemeSelection(singleSchemeSelectionForm.fill(singleSchemeTypesModel)))
    }
  }
}



