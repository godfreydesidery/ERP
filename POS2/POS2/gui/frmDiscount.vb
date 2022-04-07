Public Class frmDiscount
    Private Sub txtDiscount_TextChanged(sender As Object, e As EventArgs) Handles txtDiscount.TextChanged
        If Val(txtDiscount.Text) <= 0 Then
            txtDiscount.Text = 0
        End If
    End Sub

    Private Sub frmDiscount_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        txtDiscount.Text = 0
    End Sub
End Class