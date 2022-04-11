Imports Devart.Data.MySql
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class frmPettyCash

    Private Sub txtAmount_TextChanged(sender As Object, e As EventArgs) Handles txtAmount.TextChanged
        Dim amount As String = txtAmount.Text
        If IsNumeric(amount) And Val(amount) >= 0 Then
            btnOK.Enabled = True
        Else
            txtAmount.Text = ""
            btnOK.Enabled = False
        End If
    End Sub

    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()
    End Sub

    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        txtAmount.Text = ""
        txtDetails.Text = ""
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
    Private Function getCurrentFloat()
        Dim available As Double = 0

        Dim response As Object = New Object
        Dim json As JObject = New JObject

        Try
            Cursor.Current = Cursors.WaitCursor
            response = Web.get_("tills/get_float?till_no=" + Till.TILLNO)
            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            available = 0
            MsgBox(ex.Message)
        End Try
        Cursor.Current = Cursors.Default
        available = response
        Return available
    End Function
    Private Sub btnOK_Click(sender As Object, e As EventArgs) Handles btnOK.Click
        Dim amount As String = txtAmount.Text
        Dim detail As String = txtDetails.Text

        If getCurrentCash() < Val(amount) Then
            MsgBox("Could not complete operation. Insufficient cash amount available.", vbExclamation + vbOKOnly, "Error: Insufficient Funds")
            Exit Sub
        End If
        Dim res As Integer = MessageBox.Show("Petty Cash amount: " + LCurrency.displayValue(txtAmount.Text) + " Confirm?", "Confirm Petty Cash", MessageBoxButtons.YesNo)
        If res = DialogResult.Yes Then
            'record petty cash

            Dim pettyCash As New PettyCash
            pettyCash.amount = amount
            pettyCash.details = txtDetails.Text
            pettyCash.till.no = Till.TILLNO


            Dim response As Object = New Object
            Dim json As JObject = New JObject
            Try
                Cursor.Current = Cursors.WaitCursor
                response = Web.post(pettyCash, "tills/petty_cash?till_no=" + Till.TILLNO)
                Cursor.Current = Cursors.Default
            Catch ex As Exception
                Cursor.Current = Cursors.Default
                MsgBox(ex.ToString)
                Exit Sub
            End Try
            Cursor.Current = Cursors.Default
            MsgBox("Petty cash registered successifully")
            Me.Dispose()
        End If
    End Sub
End Class