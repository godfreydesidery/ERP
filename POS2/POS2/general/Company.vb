Imports Devart.Data.MySql

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
    Public Shared ADDRESS = ""
    Public Shared POST_CODE = ""
    Public Shared PHYSICAL_ADDRESS = ""
    Public Shared TELEPHONE = ""
    Public Shared MOBILE = ""
    Public Shared EMAIL = ""
    Public Shared FAX = ""

    Public Shared Function loadCompanyDetails() As Boolean
        Dim loaded As Boolean = False
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            'create bar code
            Dim codeQuery As String = "SELECT `company_name`, `contact_name`, `tin`, `vrn`, `bank_acc_name`, `bank_acc_address`, `bank_post_code`, `bank_name`, `bank_acc_no`, `address`, `post_code`, `physical_address`, `telephone`, `mobile`, `email`, `fax` FROM `company`"
            conn.Open()
            command.CommandText = codeQuery
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader()
            While reader.Read
                NAME = reader.GetString("company_name")
                CONTACT_NAME = reader.GetString("contact_name")
                TIN = reader.GetString("tin")
                VRN = reader.GetString("vrn")
                BANK_ACC_NAME = reader.GetString("bank_acc_name")
                BANK_ACC_ADDRESS = reader.GetString("bank_acc_address")
                BANK_POST_CODE = reader.GetString("bank_post_code")
                BANK_NAME = reader.GetString("bank_name")
                BANK_ACC_NO = reader.GetString("bank_acc_no")
                ADDRESS = reader.GetString("address")
                POST_CODE = reader.GetString("post_code")
                PHYSICAL_ADDRESS = reader.GetString("physical_address")
                TELEPHONE = reader.GetString("telephone")
                MOBILE = reader.GetString("mobile")
                EMAIL = reader.GetString("email")
                FAX = reader.GetString("fax")
                loaded = True
                Exit While
            End While
            conn.Close()
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return loaded
    End Function
End Class

