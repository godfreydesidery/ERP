Imports Devart.Data.MySql
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class frmFloat

    Dim currentFloat As Double = 0
    Dim addFloat As Double = 0
    Dim newFloat As Double = 0

    Private Sub frmFloat_Load(sender As Object, e As EventArgs) Handles MyBase.Load

        Dim response As Object = New Object
        Dim json As JObject = New JObject

        Try
            response = Web.get_("tills/get_float_by_no?no=" + Till.TILLNO)
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
        json = JObject.Parse(response)
        Dim till_ As Till = JsonConvert.DeserializeObject(Of Till)(json.ToString)
        currentFloat = till_.floatBalance
        txtCurrentFloat.Text = LCurrency.displayValue(currentFloat.ToString)

    End Sub

    Private Sub txtAddFloat_GotFocus(sender As Object, e As EventArgs) Handles txtAddFloat.GotFocus
        txtDeductFloat.Text = ""
    End Sub

    Private Sub txtAddFloat_TextChanged(sender As Object, e As EventArgs) Handles txtAddFloat.TextChanged
        Dim amount As String = txtAddFloat.Text
        If IsNumeric(amount) And Val(amount) >= 0 Then
            newFloat = currentFloat + Val(amount)
            txtNewFloat.Text = LCurrency.displayValue(newFloat.ToString)
        Else
            txtAddFloat.Text = ""
            txtNewFloat.Text = ""
        End If
    End Sub

    Private Sub txtNewFloat_TextChanged(sender As Object, e As EventArgs) Handles txtNewFloat.TextChanged
        If txtNewFloat.Text = "" Then
            btnOK.Enabled = False
        Else
            btnOK.Enabled = True
        End If
    End Sub

    Private Sub btnOK_Click(sender As Object, e As EventArgs) Handles btnOK.Click
        Dim res As Integer = 0
        Dim op As String = ""
        If Val(txtAddFloat.Text) > 0 Then
            txtDeductFloat.Text = ""
            res = MessageBox.Show("Increase amount: " + LCurrency.displayValue(txtAddFloat.Text) + " New amount: " + LCurrency.displayValue(txtNewFloat.Text) + " Confirm?", "Confirm float increase", MessageBoxButtons.YesNo)
        ElseIf Val(txtDeductFloat.Text) > 0 Then
            txtAddFloat.Text = ""
            res = MessageBox.Show("Deduct amount: " + LCurrency.displayValue(txtDeductFloat.Text) + " New amount: " + LCurrency.displayValue(txtNewFloat.Text) + " Confirm?", "Confirm float deduction", MessageBoxButtons.YesNo)
        End If
        If res = DialogResult.Yes Then
            currentFloat = Val(LCurrency.getValue(txtNewFloat.Text))
            txtCurrentFloat.Text = LCurrency.displayValue(currentFloat.ToString)

            Dim till As New Till
            till.no = Till.TILLNO
            till.computerName = "NA"
            till.name = "NA"
            till.floatBalance = currentFloat

            Dim response As Object = New Object
            Dim json As JObject = New JObject
            Try
                response = Web.put(till, "tills/update_float_by_no?no=" + Till.TILLNO)
            Catch ex As Exception
                MsgBox(ex.ToString)
                Exit Sub
            End Try
            txtAddFloat.Text = ""
            txtDeductFloat.Text = ""
            MsgBox("Float updated successifully")
        End If
    End Sub

    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        txtAddFloat.Text = ""
    End Sub

    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()
    End Sub

    Private Sub txtDeductFloat_GotFocus(sender As Object, e As EventArgs) Handles txtDeductFloat.GotFocus
        txtAddFloat.Text = ""
    End Sub

    Private Sub txtDeductFloat_TextChanged(sender As Object, e As EventArgs) Handles txtDeductFloat.TextChanged
        Dim amount As String = txtDeductFloat.Text
        If IsNumeric(amount) And Val(amount) >= 0 And Val(amount) <= Val(currentFloat) Then
            newFloat = currentFloat - Val(amount)
            txtNewFloat.Text = LCurrency.displayValue(newFloat.ToString)
        Else
            txtDeductFloat.Text = ""
            txtNewFloat.Text = ""
        End If
    End Sub
End Class