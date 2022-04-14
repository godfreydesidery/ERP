Imports Devart.Data.MySql
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class User
    Public Shared USER_ID As String = ""
    Public Shared USERNAME_ As String = ""
    Public Shared FIRST_NAME As String = ""
    Public Shared SECOND_NAME As String = ""
    Public Shared LAST_NAME As String = ""
    Public Shared PASSWORD_ As String = ""
    Public Shared AALIAS As String = ""
    Public Shared LOGIN_TIME As String = ""
    Public Shared ROLE_ As String = ""
    Public Shared TOKEN As String = ""

    Public Shared Function authenticate(username As String, password As String) As Integer
        '0-Login success
        '1-Login failed due to invalid credentials
        '2-Login failed due to other problems
        Dim user As New User
        User.TOKEN = ""
        Dim auth As Integer = 0
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        Dim tok As String = ""

        Try
            Cursor.Current = Cursors.WaitCursor
            response = Web.login(username, password)
            json = JObject.Parse(response.ToString)
            tok = json.SelectToken("access_token").ToString
            Cursor.Current = Cursors.Default
        Catch ex As NullReferenceException
            Cursor.Current = Cursors.Default
            'MsgBox("Could not log in, invalid username and password", "Error: Invalid Username and Password", vbOKOnly)
            Return 1
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            Return 2
        End Try

        'user = JsonConvert.DeserializeObject(Of User)(json.ToString)

        If String.Compare(tok, "") = -1 Then
            User.TOKEN = ""
            'MsgBox("Could not log in, invalid username and password", "Error: Invalid Username and Password", vbOKOnly)
            Return 1
        Else
            'Create a user session
            auth = 0
            User.TOKEN = tok
            Dim res As Object = New Object
            Dim jres As JObject = New JObject

            Try
                Cursor.Current = Cursors.WaitCursor
                res = Web.get_("users/load_user?username=" + username)
                jres = JObject.Parse(res.ToString)
                user = JsonConvert.DeserializeObject(Of User)(jres.ToString)
                User.AALIAS = jres.SelectToken("alias")
                Cursor.Current = Cursors.Default
            Catch ex As Exception
                Cursor.Current = Cursors.Default
                User.TOKEN = ""
                'MsgBox("Could not log in")
                Return 2
            End Try
        End If
        Cursor.Current = Cursors.Default
        Return auth
    End Function

    Public Shared Function authorize(priveledge As String) As Boolean
        Dim response As Boolean = False
        Try
            response = Web.get_("authorize?user_id=" + User.USER_ID + "&priveledge=" + priveledge)
            '  response = Web.get_("users/authorize/user_id=" + User.CURRENT_USER_ID + "&priveledge=" + priveledge)
            If response = True Then
                Return True
            Else
                Return False
            End If
        Catch ex As Exception
            response = False
        End Try
        Return False
    End Function
End Class


