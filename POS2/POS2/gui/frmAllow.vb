﻿Imports Devart.Data.MySql

Public Class frmAllow
    Public Shared allowed As Boolean = False
    Private Sub frmAllow_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        allowed = False
        txtPassword.Focus()
    End Sub

    Private Sub btnOK_Click(sender As Object, e As EventArgs) Handles btnOK.Click
        ' If User.authorize(txtUsername.Text, txtPassword.Text, User.authorize("SPECIAL")) = True Then
        ' allowed = True
        ' Me.Dispose()
        '  Else
        '  MsgBox("Operation denied. Action require special priveledges", vbCritical + vbOKOnly, "Error")
        '  End If

    End Sub
    Private Function checkAllowed(uname As String, pword As String)
        Dim allwd As Boolean = False
        'verify managers password
        Dim query As String = "SELECT `username`, `password`, `role`, `alias`, `status` FROM `users` WHERE `username`='" + uname + "' AND (`role`='MANAGER' OR `role`='CHIEF CASHIER')"

        Return allwd
    End Function

    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        allowed = False
        Me.Dispose()
    End Sub

    Private Sub txtUsername_KeyDown(sender As Object, e As KeyEventArgs) Handles txtPassword.KeyDown
        If e.KeyCode = Keys.Down Or e.KeyCode = Keys.Up Then
            txtUsername.Focus()
        End If
    End Sub

    Private Sub txtUsername_TextChanged(sender As Object, e As EventArgs) Handles txtPassword.TextChanged

    End Sub

    Private Sub txtPassword_KeyDown(sender As Object, e As KeyEventArgs) Handles txtUsername.KeyDown
        If e.KeyCode = Keys.Down Or e.KeyCode = Keys.Up Then
            txtPassword.Focus()
        End If
    End Sub

    Private Sub txtPassword_TextChanged(sender As Object, e As EventArgs) Handles txtUsername.TextChanged

    End Sub
End Class