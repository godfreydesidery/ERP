Imports Newtonsoft.Json.Linq

Public Class Company

    Public Shared NAME = ""
    Public Shared CONTACT_NAME = ""
    Public Shared TIN = ""
    Public Shared VRN = ""
    Public Shared BANK_ACC_NAME = ""
    Public Shared BANK_ACC_ADDRESS = ""
    Public Shared BANK_POST_CODE = ""
    Public Shared BANK_NAME = ""
    Public Shared BANK_ACC_NO = ""
    Public Shared POST_ADDRESS = ""
    Public Shared POST_CODE = ""
    Public Shared PHYSICAL_ADDRESS = ""
    Public Shared TELEPHONE = ""
    Public Shared MOBILE = ""
    Public Shared EMAIL = ""
    Public Shared FAX = ""
    Public Shared WEBSITE = ""

    Public Shared Function loadCompanyDetails() As Boolean
        Dim loaded As Boolean = False
        Try
            Dim response As Object = New Object
            Dim json As JObject = New JObject
            response = Web.get_("company_profile/get")
            json = JObject.Parse(response)

            NAME = json.SelectToken("companyName").ToString()
            CONTACT_NAME = json.SelectToken("contactName").ToString()
            TIN = json.SelectToken("tin").ToString()
            VRN = json.SelectToken("vrn").ToString()
            BANK_ACC_NAME = json.SelectToken("bankAccountName").ToString()
            BANK_ACC_ADDRESS = json.SelectToken("bankPhysicalAddress").ToString()
            BANK_POST_CODE = json.SelectToken("bankPostCode").ToString()
            BANK_NAME = json.SelectToken("bankName").ToString()
            BANK_ACC_NO = json.SelectToken("bankAccountNo").ToString()
            POST_ADDRESS = json.SelectToken("postAddress").ToString()
            POST_CODE = json.SelectToken("postCode").ToString()
            PHYSICAL_ADDRESS = json.SelectToken("physicalAddress").ToString()
            TELEPHONE = json.SelectToken("telephone").ToString()
            MOBILE = json.SelectToken("mobile").ToString()
            EMAIL = json.SelectToken("email").ToString()
            WEBSITE = json.SelectToken("website").ToString()
            FAX = json.SelectToken("fax").ToString()
            loaded = True

        Catch ex As Exception
            MsgBox("Could not load company profile. Application will close.", vbOKOnly + vbCritical, "Error: Till")
            Application.Exit()
        End Try
        Return loaded
    End Function

End Class

