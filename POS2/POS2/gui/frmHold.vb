Public Class frmHold
    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        Me.Dispose()
    End Sub

    Private Sub frmHold_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        txtName.Focus()
    End Sub
End Class