Imports Devart.Data.MySql

Public Class Day
    Public Shared zNo As Integer = 0
    Public Shared systemDate As String = ""


    Public Shared Function getCurrentDay() As Date
        Dim date_ As Date = #0001-01-01#
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            Dim codeQuery As String = "SELECT `day_no`, `date`, `start_at`, `end_at`, `open_closed` FROM `day_log` ORDER BY `day_no` DESC LIMIT 1"
            conn.Open()
            command.CommandText = codeQuery
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader()
            While reader.Read
                date_ = reader.GetDateTime("date")
                Exit While
            End While
            conn.Close()
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return date_
    End Function


End Class
