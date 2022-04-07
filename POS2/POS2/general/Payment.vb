Imports Devart.Data.MySql

Public Class Payment
    Public Shared cash As Double, voucher As Double, deposit As Double, loyalty As Double, CRCard As Double, CAP As Double, invoice As Double, CRNote As Double, mobile As Double
    Public Shared Function setPayment(_cash As Double, _voucher As Double, _deposit As Double, _loyalty As Double, _CRCard As Double, _CAP As Double, _invoice As Double, _CRNote As Double, _mobile As Double)

        cash = 0
        voucher = 0
        deposit = 0
        loyalty = 0
        CRCard = 0
        CAP = 0
        invoice = 0
        CRNote = 0
        mobile = 0


        cash = _cash
        voucher = _voucher
        deposit = _deposit
        loyalty = _loyalty
        CRCard = _CRCard
        CAP = _CAP
        invoice = _invoice
        CRNote = _CRNote
        mobile = _mobile


        Return vbNull
    End Function
    Public Shared Function commitPayment(paymentId As String)
        Dim commited As Boolean = False
        Try

            Dim conn As New MySqlConnection(Database.conString)
            conn.Open()
            Dim command As New MySqlCommand()
            command.Connection = conn
            command.CommandText = "INSERT INTO `payment`(`sale_id`, `cash`, `voucher`, `deposit`, `loyalty`, `cr_card`, `cap`, `invoice`, `cr_note`, `mobile`) VALUES (@sale_id,@cash,@voucher,@deposit,@loyalty,@cr_card,@cap,@invoice,@cr_note,@mobile)"
            command.Prepare()
            command.Parameters.AddWithValue("@sale_id", paymentId)
            command.Parameters.AddWithValue("@cash", cash)
            command.Parameters.AddWithValue("@voucher", voucher)
            command.Parameters.AddWithValue("@deposit", deposit)
            command.Parameters.AddWithValue("@loyalty", loyalty)
            command.Parameters.AddWithValue("@cr_card", CRCard)
            command.Parameters.AddWithValue("@cap", CAP)
            command.Parameters.AddWithValue("@invoice", invoice)
            command.Parameters.AddWithValue("@cr_note", CRNote)
            command.Parameters.AddWithValue("@mobile", mobile)


            command.ExecuteNonQuery()
            conn.Close()

            commited = True
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return commited
    End Function

End Class

