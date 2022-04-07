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
        Dim currentdate As String = Day.systemDate
        Dim query As String = "SELECT SUM(amount) FROM `sale` WHERE `till_no`='" + tillno + "' AND `date`='" + currentdate + "' "
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            Try
                totalAmount = command.ExecuteScalar
            Catch ex As Exception
                totalAmount = 0
            End Try
            txtTotal.Text = LCurrency.displayValue(totalAmount.ToString)
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
            Exit Sub
        End Try

    End Sub

    Private Sub Button1_Click(sender As Object, e As EventArgs)
        ' PointOfSale.printpostest()
    End Sub

    Private Sub Panel1_Paint(sender As Object, e As PaintEventArgs) Handles Panel1.Paint

    End Sub
End Class