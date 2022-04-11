Imports Devart.Data.MySql

Public Class Item
    Public Function getShortDescription(itemCode As String) As String
        Dim descr As String = ""
        Dim query As String = "SELECT`item_description` FROM `items` WHERE `item_code`='" + itemCode + "'"
        Dim command As New MySqlCommand()

        Return descr
    End Function
    Public Shared Function getCostPrice(itemCode As String) As String
        Dim price As Double = 0
        Dim query As String = "SELECT`unit_cost_price` FROM `items` WHERE `item_code`='" + itemCode + "'"
        Dim command As New MySqlCommand()

        Return price
    End Function
    Public Function getItems(descr As String) As List(Of String)
        Dim list As New List(Of String)

        Return list
    End Function
    Public Function getItemDescriptions() As List(Of String)
        Dim list As New List(Of String)

        Return list
    End Function
End Class
