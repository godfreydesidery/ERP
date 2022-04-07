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
    Public Shared _ALIAS As String = ""
    Public Shared LOGIN_TIME As String = ""
    Public Shared ROLE_ As String = ""

    Public Property id As String
    Public Property username As String
    Public Property password As String
    Public Property rollNo As String
    Public Property firstName As String
    Public Property secondName As String
    Public Property lastName As String
    Public Property accessToken As String
    Public Property status As String
    '   Public Property status As String
    Public Property active As Integer
    Public Property role As Role = New Role

    Public Shared Function authorize1(username As String, password As String, special As Boolean) As Boolean
        Dim auth As Boolean = False


        Dim query As String = "SELECT `id`, `first_name`, `second_name`, `last_name`, `pay_roll_no`, `username`, `password`, `role`, `alias`, `status` FROM `users` WHERE `username`=@username"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@username", username.ToString)
            Dim reader As MySqlDataReader = command.ExecuteReader()

            Dim no As Integer = 0
            While reader.Read
                If username = reader.GetString("username") And special = True Then
                    If Hash.check(password, reader.GetString("password")) Then
                        auth = True
                    End If
                End If
                Exit While
            End While
        Catch ex As Devart.Data.MySql.MySqlException
            MsgBox(ex.Message)
            Return vbNull
            Exit Function
        End Try
        Return auth
    End Function

    Public Shared Function authenticate(username As String, password As String) As Integer

        Dim user_ As User = New User
        user_.username = username
        user_.password = password
        user_.rollNo = "NA"
        user_.firstName = "NA"
        user_.secondName = "NA"
        user_.lastName = "NA"
        user_.firstName = "NA"

        '0- login succeeded
        '1- login failed due to invalid credentials
        '2- login failed due to other problems
        Dim auth As Integer = 0

        Dim response As Object = New Object
        Dim json As JObject = New JObject
        Try
            response = Web.post(user_, "login")
            json = JObject.Parse(response)

        Catch ex As Exception
            Return vbNull
            Exit Function
        End Try
        Dim user As User = JsonConvert.DeserializeObject(Of User)(json.ToString)
        If IsNothing(user.id) Then
            auth = 1
        ElseIf user.id.ToString = "" Then
            auth = 1
        Else
            User.USER_ID = user.id
            User.USERNAME_ = user.username
            User.PASSWORD_ = ""
            If user.active = 1 Then
                user.status = "ACTIVE"
            Else
                user.status = "DEACTIVATED"
            End If

            If IsNothing(user.role) Then
                User.ROLE_ = ""
            Else
                User.ROLE_ = user.role.name
            End If
            User.FIRST_NAME = user.firstName
            User.SECOND_NAME = user.secondName
            User.LAST_NAME = user.lastName
            User._ALIAS = User.FIRST_NAME + " " + User.LAST_NAME
            If user.status <> "DEACTIVATED" Then
                auth = 0 'user valid and login
            Else
                auth = 2 'user blocked
            End If
        End If
        Return auth
    End Function

    Public Function getAlias(id As String)
        Dim _alias As String = ""
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        response = Web.get_("users/get_by_id?id=" + id)
        json = JObject.Parse(response)

        _alias = json.SelectToken("firstName").ToString

        Return _alias
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
