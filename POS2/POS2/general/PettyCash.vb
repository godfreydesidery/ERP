Public Class PettyCash
    Public Property id As String
    Public Property amount As Double
    Public Property details As String
    Public Property pickedAt As Date
    Public Property till As Till = New Till
    Public Property pickedBy As User = New User

End Class
