Public Class Cart
    Public Property id As String
    Public Property cartDate As Date
    Public Property till As Till = New Till
    Public Property cartDetails As List(Of CartDetail) = New List(Of CartDetail)
    Public Property cartDetail As CartDetail
End Class
