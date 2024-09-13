Imports System.IO
Imports System.Net.NetworkInformation

Public Class frmLogin

    ' TODO: Insert code to perform custom authentication using the provided username and password 
    ' (See http://go.microsoft.com/fwlink/?LinkId=35339).  
    ' The custom principal can then be attached to the current thread's principal as follows: 
    '     My.User.CurrentPrincipal = CustomPrincipal
    ' where CustomPrincipal is the IPrincipal implementation used to perform authentication. 
    ' Subsequently, My.User will return identity information encapsulated in the CustomPrincipal object
    ' such as the username, display name, etc.

    Private Sub OK_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles OK.Click

        If User.authenticate(txtUsername.Text, txtPassword.Text) = 0 Then
            frmMain.Show()
            Me.Close()
        Else
            MsgBox("Invalid Username and Password", vbExclamation + vbOKOnly, "Error: Not found")
            txtUsername.Focus()
        End If
    End Sub

    Private Sub Cancel_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Cancel.Click
        Me.Close()
    End Sub

    Private Sub txtUsername_KeyDown(sender As Object, e As KeyEventArgs) Handles txtUsername.KeyDown
        If e.KeyCode = Keys.Down Then
            txtPassword.Focus()
        End If
    End Sub

    Private Sub txtUsername_TextChanged(sender As Object, e As EventArgs) Handles txtUsername.TextChanged

    End Sub

    Private Sub txtPassword_GotFocus(sender As Object, e As EventArgs) Handles txtPassword.GotFocus
        txtPassword.Text = ""
    End Sub

    Private Sub txtPassword_KeyDown(sender As Object, e As KeyEventArgs) Handles txtPassword.KeyDown
        If e.KeyCode = Keys.Up Then
            txtUsername.Focus()
        End If
    End Sub

    Private Sub frmLogin_Load(sender As Object, e As EventArgs) Handles Me.Shown



        Dim path As String = LApllication.localAppDataDir
        'create a directory

        If (Not My.Computer.FileSystem.DirectoryExists(path)) Then

            My.Computer.FileSystem.CreateDirectory(path)
            serverSettings()
            Me.Close()
            Exit Sub
        End If
        Try
            Dim app As New LApllication

            app.loadSettings()
        Catch ex As Exception
            MsgBox("Failed to load application", vbCritical + vbOKOnly, "Error: Application")
            Application.Exit()
            End
        End Try

    End Sub
    Private Function serverSettings()
        frmServSetup.Show()
        Return vbNull
    End Function
    Declare Function Wow64DisableWow64FsRedirection Lib "kernel32" (ByRef oldvalue As Long) As Boolean
    Declare Function Wow64EnableWow64FsRedirection Lib "kernel32" (ByRef oldvalue As Long) As Boolean
    Private osk As String = "C:\Windows\System32\osk.exe"
    Private Sub startOSK()
        Dim old As Long
        If Environment.Is64BitOperatingSystem Then
            If Wow64DisableWow64FsRedirection(old) Then
                Process.Start(osk)
                Wow64EnableWow64FsRedirection(old)
            End If
        Else
            Process.Start(osk)
        End If
    End Sub
    Private Sub frmLogin_Load_1(sender As Object, e As EventArgs) Handles MyBase.Load

    End Sub

    Private Sub txtUsername_MouseDoubleClick(sender As Object, e As MouseEventArgs) Handles txtUsername.MouseDoubleClick
        startOSK()

    End Sub

    Private Sub txtPassword_TextChanged(sender As Object, e As EventArgs) Handles txtPassword.TextChanged

    End Sub

    Private Sub txtPassword_MouseDoubleClick(sender As Object, e As MouseEventArgs) Handles txtPassword.MouseDoubleClick
        startOSK()

    End Sub
End Class
