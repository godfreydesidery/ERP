Public Class Cart
    Public Shared Property NO_ As String = ""
    Public Property id As String
    Public Property no As String
    Public Property till As Till = New Till
    Public Property cartDetails As List(Of CartDetail) = New List(Of CartDetail)
    Public Property cartDetail As CartDetail
End Class
