Public Class Key
    Public Shared Function place(_key As String)

        Return vbNull
    End Function
    Public Shared Function delete()

        Return vbNull
    End Function
    Public Shared Function enter()

        Return vbNull
    End Function
    Public Shared Function sendUp()
        Try
            SendKeys.Send("{up}")
        Catch ex As Exception

        End Try
        Return vbNull
    End Function
    Public Shared Function sendDown()
        Try
            SendKeys.Send(Keys.Down)
        Catch ex As Exception

        End Try
        Return vbNull
    End Function
    Public Shared Function sendLeft()
        Try
            SendKeys.Send("{left}")
        Catch ex As Exception

        End Try
        Return vbNull
    End Function
    Public Shared Function sendRight()
        Try
            SendKeys.Send("{right}")
        Catch ex As Exception

        End Try
        Return vbNull
    End Function
End Class
