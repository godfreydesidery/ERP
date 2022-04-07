Public Class TillPosition
    Public Property id As String

    Public Property cash As Double
    Public Property voucher As Double
    Public Property deposit As Double
    Public Property loyalty As Double
    Public Property crCard As Double
    Public Property cheque As Double
    Public Property cap As Double
    Public Property invoice As Double
    Public Property crNote As Double
    Public Property mobile As Double
    Public Property other As Double

    Public Property floatBalance As Double

    Public Property till As Till = New Till

End Class
