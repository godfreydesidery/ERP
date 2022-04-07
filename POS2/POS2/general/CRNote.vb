Imports Devart.Data.MySql

Public Class CRNote
    Public Function getCreditNoteNo(cRNo As String) As Double
        Dim no As Double = 0
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            Dim query As String = ""
            query = "SELECT  `cr_no`, `cr_amount`, `cr_bill_no`, `cr_date`, `cr_status`, `cr_details` FROM `cr_note` WHERE `cr_no`='" + cRNo + "'"
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader()
            While reader.Read
                no = Val(reader.GetString("cr_amount"))
                Exit While
            End While
            conn.Close()
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return no
    End Function
End Class
