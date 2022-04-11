Public Class frmLock

    Private Sub btnUnlock_Click(sender As Object, e As EventArgs) Handles btnUnlock.Click
        unlock()
    End Sub
    Private Function unlock()
        MessageBox.Show("Please Login afresh", "Unlock System", MessageBoxButtons.OK)
        Return vbNull
    End Function

    Private Sub btnExit_Click(sender As Object, e As EventArgs) Handles btnExit.Click
        Dim res As Integer = MessageBox.Show("Are you sure you want to close the application?", "Close Application", MessageBoxButtons.YesNo)
        If res = DialogResult.Yes Then
            Application.Exit()
        End If
    End Sub

    Private Sub txtPassword_KeyDown(sender As Object, e As KeyEventArgs)
        If e.KeyCode = Keys.Enter Then
            unlock()
        End If
    End Sub

    Private Sub txtPassword_TextChanged(sender As Object, e As EventArgs)

    End Sub

    Private Sub frmLock_Load(sender As Object, e As EventArgs) Handles MyBase.Load

    End Sub

    Private Sub txtUsername_KeyPress(sender As Object, e As KeyPressEventArgs)

    End Sub

    Private Sub txtUsername_TextChanged(sender As Object, e As EventArgs)

    End Sub
End Class