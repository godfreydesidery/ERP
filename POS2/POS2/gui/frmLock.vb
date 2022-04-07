Public Class frmLock

    Private Sub btnUnlock_Click(sender As Object, e As EventArgs) Handles btnUnlock.Click
        unlock()
    End Sub
    Private Function unlock()
        Dim username As String = User.USERNAME_
        Dim password As String = User.PASSWORD_
        If txtUsername.Text = username And Hash.check(txtPassword.Text, password) = True Then
            Me.Dispose()
        Else
            MessageBox.Show("Unlock failed. Wrong username and password", "Error: Unlock fail", MessageBoxButtons.OK)
            txtUsername.Text = ""
            txtPassword.Text = ""
            txtUsername.Focus()
        End If
        Return vbNull
    End Function

    Private Sub btnExit_Click(sender As Object, e As EventArgs) Handles btnExit.Click
        Dim res As Integer = MessageBox.Show("Are you sure you want to close the application?", "Close Application", MessageBoxButtons.YesNo)
        If res = DialogResult.Yes Then
            Application.Exit()
        End If
    End Sub

    Private Sub txtUsername_GotFocus(sender As Object, e As EventArgs) Handles txtUsername.GotFocus
        txtPassword.Text = ""
    End Sub

    Private Sub txtUsername_KeyDown(sender As Object, e As KeyEventArgs) Handles txtUsername.KeyDown
        If e.KeyCode = Keys.Enter Then
            txtPassword.Text = ""
            txtPassword.Focus()
        End If
        If e.KeyCode = Keys.Down Or e.KeyCode = Keys.Up Then
            txtPassword.Text = ""
            txtPassword.Focus()
        End If
    End Sub


    Private Sub txtPassword_KeyDown(sender As Object, e As KeyEventArgs) Handles txtPassword.KeyDown
        If e.KeyCode = Keys.Enter Then
            unlock()
        End If
        If e.KeyCode = Keys.Down Or e.KeyCode = Keys.Up Then
            txtPassword.Text = ""
            txtUsername.Focus()
        End If
    End Sub

    Private Sub txtPassword_TextChanged(sender As Object, e As EventArgs) Handles txtPassword.TextChanged

    End Sub

    Private Sub frmLock_Load(sender As Object, e As EventArgs) Handles MyBase.Load

    End Sub

    Private Sub txtUsername_KeyPress(sender As Object, e As KeyPressEventArgs) Handles txtUsername.KeyPress

    End Sub

    Private Sub txtUsername_TextChanged(sender As Object, e As EventArgs) Handles txtUsername.TextChanged

    End Sub
End Class