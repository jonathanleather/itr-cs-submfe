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

import forms.ConfirmContactDetailsForm._
import models.{ConfirmContactDetailsModel, ContactDetailsModel}
import org.jsoup.Jsoup
import org.scalatestplus.play.OneAppPerSuite
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import views.html.eis.contactInformation._


class ConfirmContactDetailsSpec extends UnitSpec with OneAppPerSuite{

  val contactDetailsNoMobile =  ContactDetailsModel("Firstname","Lastname",Some("07000 111222"), None, "Firstname@Lastname-ltd.com")
  val contactDetailsMobile =  ContactDetailsModel("Firstname","Lastname",Some("07000 111222"), Some("07000 222111"), "Firstname@Lastname-ltd.com")



  lazy val view = ConfirmContactDetails(confirmContactDetailsForm.fill(ConfirmContactDetailsModel("",contactDetailsNoMobile)))(FakeRequest(), applicationMessages)
  lazy val viewWithMobile = ConfirmContactDetails(confirmContactDetailsForm.fill(ConfirmContactDetailsModel("",contactDetailsMobile)))(FakeRequest(), applicationMessages)
  lazy val document = Jsoup.parse(view.body)
  lazy val documentWithMobile = Jsoup.parse(viewWithMobile.body)

  "The Confirm Contact Details page" should {

    s"have the title '${Messages("page.contactInformation.ConfirmContactDetails.title")}" in {
      document.title() shouldBe Messages("page.contactInformation.ConfirmContactDetails.title")
    }

    s"have the heading '${Messages("page.contactInformation.ConfirmContactDetails.heading")}" in {
      document.getElementById("main-heading").text() shouldBe Messages("page.contactInformation.ConfirmContactDetails.heading")
    }

    "have a back-link" which {
      s"has the text '${Messages("common.section.progress.details.five")}" in {
        document.body.getElementById("back-link").text shouldEqual Messages("common.button.back")
      }

      s"has a link to '${controllers.eis.routes.ContactDetailsController.show().url}" in {
        document.body.getElementById("back-link").attr("href") shouldEqual controllers.eis.routes.InvestmentGrowController.show().url
      }

      s"has a Section heading next to it of '${Messages("common.section.progress.details.four")}" in {
        document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.details.five")
      }
    }

    "have a continue button" in {
      document.getElementById("next").text() shouldBe Messages("common.button.snc")
    }

    "have a form" which {
      lazy val form = document.select("form")

      "has a POST method" in {
        form.attr("method") shouldBe "POST"
      }

      "has an action to the correct post method" in {
        form.attr("action") shouldBe controllers.eis.routes.ConfirmContactDetailsController.submit().url
      }
    }

    "have a legend" which {
      lazy val legend = document.select("legend")
      "has the correct content" in {
        legend.text() shouldBe Messages("page.contactInformation.ConfirmContactDetails.heading")
      }

      "is visually hidden" in {
        legend.hasClass("visuallyhidden") shouldBe true
      }
    }

    "have radio options" which {

      "include a 'Yes' option" in {
        document.body.getElementById("contactDetailsUse-yes").attr("value") shouldBe Messages("common.radioYesLabel")
        document.select("label[for=contactDetailsUse-yes]").text() shouldBe Messages("common.radioYesLabel")
      }

      "includes a 'No' option" in {
        document.body.getElementById("contactDetailsUse-no").attr("value") shouldBe Messages("common.radioNoLabel")
        document.select("label[for=contactDetailsUse-no]").text() shouldBe Messages("common.radioNoLabel")
      }

    }

    "for a user with NO mobile should have a section for the contact details" which {

      s"includes the name '${contactDetailsNoMobile.fullName}'" in {
        document.body.getElementById("name").text shouldBe contactDetailsNoMobile.fullName
      }

      s"includes the Landline number '${contactDetailsNoMobile.telephoneNumber}'" in {
        document.body.getElementById("telephoneNumber").text shouldBe contactDetailsNoMobile.telephoneNumber.get
      }

      s"does NOT include the mobile number" in {
        document.body.getElementById("storedContactDetailsDiv").children().html() shouldNot contain ("mobileNumber")
      }

      s"includes the email address '${contactDetailsNoMobile.email}'" in {
        document.body.getElementById("email").text shouldBe contactDetailsNoMobile.email
      }

      "has a set of hidden fields" which {

        "includes a forename field" in {
          document.select("input[id=contactDetails.forename]").attr("value") shouldBe contactDetailsNoMobile.forename
        }

        "includes a surname field" in {
          document.select("input[id=contactDetails.surname]").attr("value") shouldBe contactDetailsNoMobile.surname
        }

        "includes a phone number field" in {
          document.select("input[id=contactDetails.telephoneNumber]").attr("value") shouldBe contactDetailsNoMobile.telephoneNumber.get
        }

        "includes a mobile number field" in {
          document.select("input[id=contactDetails.mobileNumber]").attr("value") shouldBe ""
        }

        "includes an email field" in {
          document.select("input[id=contactDetails.email]").attr("value") shouldBe contactDetailsNoMobile.email
        }
      }
    }

    "for a user with mobile contact details should have a section for the contact details" which {

      s"includes the name '${contactDetailsNoMobile.fullName}'" in {
        documentWithMobile.body.getElementById("name").text shouldBe contactDetailsNoMobile.fullName
      }

      s"includes the Landline number '${contactDetailsNoMobile.telephoneNumber}'" in {
        documentWithMobile.body.getElementById("telephoneNumber").text shouldBe contactDetailsNoMobile.telephoneNumber.get
      }

      s"includes the mobile number '${contactDetailsMobile.mobileNumber.get}'" in {
        documentWithMobile.body.getElementById("mobileNumber").text shouldBe contactDetailsMobile.mobileNumber.get
      }

      s"includes the email address '${contactDetailsNoMobile.email}'" in {
        documentWithMobile.body.getElementById("email").text shouldBe contactDetailsNoMobile.email
      }

      "has a set of hidden fields" which {

        "includes a forename field" in {
          documentWithMobile.select("input[id=contactDetails.forename]").attr("value") shouldBe contactDetailsMobile.forename
        }

        "includes a surname field" in {
          documentWithMobile.select("input[id=contactDetails.surname]").attr("value") shouldBe contactDetailsMobile.surname
        }

        "includes a phone number field" in {
          documentWithMobile.select("input[id=contactDetails.telephoneNumber]").attr("value") shouldBe contactDetailsMobile.telephoneNumber.get
        }

        "includes a mobile number field" in {
          documentWithMobile.select("input[id=contactDetails.mobileNumber]").attr("value") shouldBe contactDetailsMobile.mobileNumber.get
        }

        "includes an email field" in {
          documentWithMobile.select("input[id=contactDetails.email]").attr("value") shouldBe contactDetailsMobile.email
        }
      }
    }
  }
}
