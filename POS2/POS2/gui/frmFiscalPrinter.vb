Public Class frmFiscalPrinter

    Private Sub btnSave_Click(sender As Object, e As EventArgs) Handles btnSave.Click
        If save(txtName.Text, txtOperatorCode.Text, txtOperatorPassword.Text) = True Then

        Else
            MsgBox("Operation Failed", vbExclamation + vbOKOnly, "Error")
        End If
    End Sub
    Private Function save(name As String, operatorCode As String, operatorPassword As String)
        Dim saved As Boolean = False
        'save printer information

        Return saved
    End Function
    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()
    End Sub
End Class