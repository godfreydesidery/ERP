Imports Devart.Data.MySql

Public Class Inventory
    Inherits Item
    Public GL_MIN_INVENTORY As Double = 0
    Public GL_MAX_INVENTORY As Double = 0
    Public GL_REORDER_LEVEL As Double = 0
    Public GL_DEFAULT_REORDER_QTY As Double = 0
    Public Function getInventory(itemCode As String) As String
        Dim value As String = ""

        Return value
    End Function
    Public Function adjustInventory(itemCode As String, value As Double) As Boolean
        Dim success As Boolean = False
        Dim actValue As String = value.ToString

        Return success
    End Function
    Public Function addInventory(itemCode As String, minInventory As Double, maxInventory As Double, reorderLevel As Double, defReorderQty As Double) As Boolean
        Dim added As Boolean = False

        Return added
    End Function
    Public Function editInventory(itemCode As String, minInventory As Double, maxInventory As Double, reorderLevel As Double, defReorderQty As Double) As Boolean
        Dim edited As Boolean = False

        Return edited
    End Function
End Class
