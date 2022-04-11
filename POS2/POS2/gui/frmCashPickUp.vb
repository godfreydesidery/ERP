Imports Devart.Data.MySql
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class frmCashPickUp
    Dim currentAmount As Double = 0
    Dim pickUpAmount As Double = 0
    Dim newCashAmount As Double = 0
    Dim currentFloat As Double = 0

    Private Sub frmCashPickUp_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        Try
            Cursor.Current = Cursors.WaitCursor
            response = Web.get_("tills/get_cash?till_no=" + Till.TILLNO)
            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            MsgBox(ex.Message)
        End Try
        Cursor.Current = Cursors.Default
        txtAvailable.Text = LCurrency.displayValue(response.ToString)
    End Sub

    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()
    End Sub

    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        txtPickUp.Text = ""
    End Sub

    Private Sub txtPickUp_TextChanged(sender As Object, e As EventArgs) Handles txtPickUp.TextChanged
        Dim amount As String = txtPickUp.Text
        If IsNumeric(amount) And Val(amount) >= 0 Then 'And Val(amount) <= currentAmount Then
            newCashAmount = LCurrency.getValue(txtAvailable.Text) - Val(amount)
            txtRemaining.Text = LCurrency.displayValue(newCashAmount.ToString)
        Else
            txtPickUp.Text = ""
            txtRemaining.Text = ""
        End If
    End Sub

    Private Function getCurrentCash()
        Dim available As Double = 0

        Dim response As Object = New Object
        Dim json As JObject = New JObject

        Try
            Cursor.Current = Cursors.WaitCursor
            response = Web.get_("tills/get_cash?till_no=" + Till.TILLNO)
            available = response
            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            available = 0
            MsgBox(ex.ToString)
        End Try
        Cursor.Current = Cursors.Default
        Return available
    End Function

    Private Sub btnOK_Click(sender As Object, e As EventArgs) Handles btnOK.Click
        Dim amount As String = txtPickUp.Text
        'Dim detail As String = txtDetails.Text

        If getCurrentCash() < Val(amount) Then
            MsgBox("Could not complete operation. Insufficient cash amount available.", vbExclamation + vbOKOnly, "Error: Insufficient Funds")
            Exit Sub
        End If
        Dim res As Integer = MessageBox.Show("Pick up amount: " + LCurrency.displayValue(txtPickUp.Text) + " Confirm?", "Confirm Cash Pick up", MessageBoxButtons.YesNo)
        If res = DialogResult.Yes Then

            Dim cashPickUp As New CashPickUp
            cashPickUp.amount = amount
            cashPickUp.till.no = Till.TILLNO

            Dim response As Object = New Object
            Dim json As JObject = New JObject
            Try
                Cursor.Current = Cursors.WaitCursor
                response = Web.post(cashPickUp, "tills/cash_pick_up?till_no=" + Till.TILLNO)
                Cursor.Current = Cursors.Default
            Catch ex As Exception
                Cursor.Current = Cursors.Default
                MsgBox(ex.ToString)
                Exit Sub
            End Try
            Cursor.Current = Cursors.Default
            MsgBox("Cash Pick up registered successifully")
            Me.Dispose()
        End If
    End Sub

    Private Sub txtRemaining_TextChanged(sender As Object, e As EventArgs) Handles txtRemaining.TextChanged
        If txtRemaining.Text = "" Then
            btnOK.Enabled = False
        Else
            btnOK.Enabled = True
        End If
    End Sub
End Class