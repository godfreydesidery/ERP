Imports Devart.Data.MySql

Public Class Bill
    Public Function createBill(billNo As String, billAmount As String, billType As String, billStatus As String, billDate As String, billDescr As String) As String
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            Dim query As String = ""
            query = "INSERT INTO `bill`( `bill_no`, `amount`, `bill_date`, `bill_type`,`bill_status`, `description`) VALUES (@bill_no,@bill_amount,@bill_date,@bill_type,@bill_status,@description)"
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@bill_no", billNo)
            command.Parameters.AddWithValue("@bill_amount", billAmount)
            command.Parameters.AddWithValue("@bill_type", billType)
            command.Parameters.AddWithValue("@bill_status", billStatus)
            command.Parameters.AddWithValue("@bill_date", billDate)
            command.Parameters.AddWithValue("@description", billDescr)
            command.ExecuteNonQuery()
            conn.Close()
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return vbNull
    End Function
End Class
