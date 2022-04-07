Public Class Hash

    Public Shared Function make(password As String)
        Dim hashedPassword As String = ""
        Dim crypt As New Crypt
        hashedPassword = crypt.make(password)
        Return hashedPassword
    End Function
    Public Shared Function check(userPassword As String, hashedPassword As String)
        Dim match As Boolean = False
        Dim crypt As New Crypt
        If crypt.check(userPassword, hashedPassword) = True Then
            match = True
        End If
        'If userPassword = hashedPassword Then 'this should be used until the hashing has fully implemented
        '    'match = True
        'End If
        Return match
    End Function

    'Public Shared Function make(password As String)
    '    Dim hashedPassword As String = ""

    '    Return hashedPassword
    'End Function
    'Public Shared Function check(userPassword As String, hashedPassword As String)
    '    Dim match As Boolean = False
    '    If userPassword = hashedPassword Then
    '        match = True
    '    End If
    '    Return match
    'End Function
End Class
