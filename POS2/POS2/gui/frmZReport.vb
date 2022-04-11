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
        Dim currentDate As String = Day.bussinessDate
        Dim command As New MySqlCommand()


        txtCash.Text = LCurrency.displayValue(cash.ToString)
        txtCheques.Text = LCurrency.displayValue(cheque.ToString)
        txtGiftVouchers.Text = LCurrency.displayValue(giftVouchers.ToString)
        txtCRCards.Text = LCurrency.displayValue(CRCards.ToString)
        txtCRNotes.Text = LCurrency.displayValue(CRNotes.ToString)
        txtNewFloat.Text = LCurrency.displayValue(newFloat.ToString)

    End Sub

    Private Sub btnPrint_Click(sender As Object, e As EventArgs) Handles btnPrint.Click

    End Sub
End Class