Imports Devart.Data.MySql

Public Class frmZReport
    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()
    End Sub

    Private Sub frmZReport_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        Dim cash As Double = 0
        Dim CRCards As Double = 0
        Dim giftVouchers As Double = 0
        Dim cheque As Double = 0
        Dim CRNotes As Double = 0
        Dim newFloat As Double = 0
        Dim tillno As String = Till.TILLNO
        Dim currentDate As String = Day.systemDate
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Dim reader As MySqlDataReader
        Dim query As String = ""
        Try
            conn.Open()
            query = "SELECT `sale`.`date`,`sale`.`id`,`sale`.`till_no`,`payment`.`sale_id`,`payment`.`cash`,`payment`.`voucher`,`payment`.`cr_card`,`payment`.`cr_note`,`payment`.`cheque` FROM `sale`,`payment` WHERE `sale`.`id`=`payment`.`sale_id` AND `sale`.`till_no`='" + Till.TILLNO + "' AND `sale`.`date`='" + currentDate + "'"
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.CommandText = query
            reader = command.ExecuteReader()
            While reader.Read
                cash = cash + Val(reader.GetString("cash"))
                giftVouchers = giftVouchers + Val(reader.GetString("voucher"))
                CRCards = CRCards + Val(reader.GetString("cr_card"))
                CRNotes = CRNotes + Val(reader.GetString("cr_note"))
                cheque = cheque + Val(reader.GetString("cheque"))
            End While
            query = "SELECT `amount` FROM `float_balance` WHERE `till_no`='" + Till.TILLNO + "'"
            command.CommandText = query
            reader = command.ExecuteReader()
            While reader.Read
                newFloat = Val(reader.GetString("amount"))
                Exit While
            End While
            conn.Close()

            txtCash.Text = LCurrency.displayValue(cash.ToString)
            txtCheques.Text = LCurrency.displayValue(cheque.ToString)
            txtGiftVouchers.Text = LCurrency.displayValue(giftVouchers.ToString)
            txtCRCards.Text = LCurrency.displayValue(CRCards.ToString)
            txtCRNotes.Text = LCurrency.displayValue(CRNotes.ToString)
            txtNewFloat.Text = LCurrency.displayValue(newFloat.ToString)
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
            Exit Sub
        End Try

    End Sub

    Private Sub btnPrint_Click(sender As Object, e As EventArgs) Handles btnPrint.Click

    End Sub
End Class