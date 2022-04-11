Imports Devart.Data.MySql
Imports OposPOSPrinter_CCO

Public Class frmXReport

    Dim totalAmount As Double = 0

    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()
    End Sub

    Private Sub btnPrint_Click(sender As Object, e As EventArgs) Handles btnPrint.Click
        Me.Dispose()
    End Sub

    Private Sub frmXReport_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        Dim tillno As String = Till.TILLNO
        Dim currentdate As String = Day.bussinessDate


    End Sub

    Private Sub Button1_Click(sender As Object, e As EventArgs)
        ' PointOfSale.printpostest()
    End Sub

    Private Sub Panel1_Paint(sender As Object, e As PaintEventArgs) Handles Panel1.Paint

    End Sub
End Class