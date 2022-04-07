Imports Devart.Data.MySql

Public Class Collection
    Public Function collect(tillNo As String, date_ As String, cash As Double, voucher As Double, deposit As Double, loyalty As Double, CRCard As Double, cheque As Double, CAP As Double, invoice As Double, CRNote As Double, mobile As Double)
        Dim success As Boolean = False
        Dim conn As New MySqlConnection(Database.conString)
        Try
            Dim command As New MySqlCommand()
            conn.Open()
            command.Connection = conn
            command.CommandText = "INSERT INTO `collections`( `till_no`, `date`, `cash`, `voucher`, `deposit`, `loyalty`, `cr_card`, `cheque`, `cap`, `invoice`, `cr_note`, `mobile`) VALUES (@till_no,@date,@cash,@voucher,@deposit,@loyalty,@cr_card,@cheque,@cap,@invoice,@cr_note,@mobile)"
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@till_no", tillNo)
            command.Parameters.AddWithValue("@date", date_)
            command.Parameters.AddWithValue("@cash", cash.ToString)
            command.Parameters.AddWithValue("@voucher", voucher.ToString)
            command.Parameters.AddWithValue("@deposit", deposit.ToString)
            command.Parameters.AddWithValue("@loyalty", loyalty.ToString)
            command.Parameters.AddWithValue("@cr_card", CRCard.ToString)
            command.Parameters.AddWithValue("@cheque", cheque.ToString)
            command.Parameters.AddWithValue("@cap", CAP.ToString)
            command.Parameters.AddWithValue("@invoice", invoice.ToString)
            command.Parameters.AddWithValue("@cr_note", CRNote.ToString)
            command.Parameters.AddWithValue("@mobile", mobile.ToString)
            command.ExecuteNonQuery()
            conn.Close()
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
        End Try
        Return success
    End Function
End Class
