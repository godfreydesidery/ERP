Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class Product
    Public Property id As String
    Public Property primaryBarcode As String
    Public Property code As String
    Public Property description As String
    Public Property shortDescription As String
    Public Property commonDescription As String
    Public Property standardUom As String
    Public Property packSize As Double
    Public Property ingredients As String
    Public Property costPriceVatIncl As Double
    Public Property costPriceVatExcl As Double
    Public Property sellingPriceVatIncl As Double
    Public Property sellingPriceVatExcl As Double
    Public Property profitMargin As Double
    Public Property vat As Double
    Public Property discountRatio As Double
    Public Property stock As Double
    Public Property minimumStock As Double
    Public Property maximumStock As Double
    Public Property defaultReorderLevel As Double
    Public Property defaultReorderQty As Double

    Public Property status As String
    Public Property sellable As Integer
    Public Property returnable As Integer


    Public Function getDescriptions() As List(Of String)
        'Returns a list of descriptions of all the products
        Dim list As New List(Of String)
        Try
            Dim response As Object = New Object
            Dim json As JObject = New JObject
            response = Web.get_("products/descriptions")
            list = JsonConvert.DeserializeObject(Of List(Of String))(response.ToString)
            Return list
        Catch ex As Exception
            Return list
        End Try
        Return list
    End Function


End Class
