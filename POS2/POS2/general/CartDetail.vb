Public Class CartDetail
    Public Property id As String
    Public Property code As String
    Public Property barcode As String
    Public Property description As String
    Public Property qty As Double
    Public Property vat As Double
    Public Property costPriceVatIncl As Double
    Public Property costPriceVatExcl As Double
    Public Property sellingPriceVatIncl As Double
    Public Property sellingPriceVatExcl As Double
    Public Property discountRatio As Double
    Public Property voided As Integer
    Public Property cart As Cart = New Cart
End Class
