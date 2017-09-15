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

package models

import controllers.helpers.BaseSpec
import models.repayments.SharesRepaymentDetailsModel

class SharesRepaymentDetailsModelSpec extends BaseSpec{

  val invalidSharesRepaymentDetails = SharesRepaymentDetailsModel()

  val validSharesRepaymentDetails = SharesRepaymentDetailsModel(Some(anySharesRepaymentModelYes), Some(whoRepaidSharesModel),
    Some(repaymentTypeShares), Some(dateSharesRepaidModel), Some(amountSharesRepaymentModel))

  val sharesRepaymentDetailsMissingAnySharesModel = SharesRepaymentDetailsModel(None, Some(whoRepaidSharesModel),
    Some(repaymentTypeShares), Some(dateSharesRepaidModel), Some(amountSharesRepaymentModel))

  val sharesRepaymentDetailsMissingRecipient = SharesRepaymentDetailsModel(Some(anySharesRepaymentModelYes), None,
    Some(repaymentTypeShares), Some(dateSharesRepaidModel), Some(amountSharesRepaymentModel))

  val sharesRepaymentDetailsMissingRepaymentType = SharesRepaymentDetailsModel(Some(anySharesRepaymentModelYes),
    Some(whoRepaidSharesModel), None, Some(dateSharesRepaidModel), Some(amountSharesRepaymentModel))

  val sharesRepaymentDetailsMissingRepaymentDate = SharesRepaymentDetailsModel(Some(anySharesRepaymentModelYes), Some(whoRepaidSharesModel),
    Some(repaymentTypeShares), None, Some(amountSharesRepaymentModel))

  val sharesRepaymentDetailsMissingRepaymentAmount = SharesRepaymentDetailsModel(Some(anySharesRepaymentModelYes), Some(whoRepaidSharesModel),
    Some(repaymentTypeShares), Some(dateSharesRepaidModel), None)

  "Calling validate" should {
    "return false" when {
      "the shares repayment details model is empty" in {
        invalidSharesRepaymentDetails.validate shouldBe false
      }
      "the shares repayment details is missing anySharesRepayment model" in {
        sharesRepaymentDetailsMissingAnySharesModel.validate shouldBe false
      }
      "the shares repayment details is missing recipient " in {
        sharesRepaymentDetailsMissingRecipient.validate shouldBe false
      }
      "the shares repayment details is missing repayment type" in {
        sharesRepaymentDetailsMissingRepaymentType.validate shouldBe false
      }
      "the shares repayment details is missing repayment date" in {
        sharesRepaymentDetailsMissingRepaymentDate.validate shouldBe false
      }
      "the shares repayment details is missing repayment amount " in {
        sharesRepaymentDetailsMissingRepaymentAmount.validate shouldBe false
      }
    }
    "return true" when {
      "the shares repayment details model is full and provide all models with valid data" in {
        validSharesRepaymentDetails.validate shouldBe true
      }
    }
  }
}
