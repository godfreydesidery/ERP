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
            response = Web.get_("tills/get_till_position_by_no?no=" + Till.TILLNO)
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
        json = JObject.Parse(response)
        Dim till_ As Till = JsonConvert.DeserializeObject(Of Till)(json.ToString)
        available = till_.cash

        Return available
    End Function
    Private Function getCurrentFloat()
        Dim available As Double = 0

        Dim response As Object = New Object
        Dim json As JObject = New JObject

        Try
            response = Web.get_("tills/get_till_position_by_no?no=" + Till.TILLNO)
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
        json = JObject.Parse(response)
        Dim till_ As Till = JsonConvert.DeserializeObject(Of Till)(json.ToString)
        available = till_.floatBalance

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
                response = Web.post(pettyCash, "petty_cashs/collect_by_till_no?no=" + Till.TILLNO)
            Catch ex As Exception
                MsgBox(ex.ToString)
                Exit Sub
            End Try
            MsgBox("Petty cash registered successifully")
            Me.Dispose()
        End If
    End Sub
End Class