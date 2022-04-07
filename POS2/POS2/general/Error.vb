Public Class LError
    Public Shared Function databaseConnection()
        MsgBox("Could not connect to Database", vbExclamation + vbOKOnly, "Error: Database connection")
        Return vbNull
    End Function
End Class
