Imports Devart.Data.MySql

Public Class Inventory
    Inherits Item
    Public GL_MIN_INVENTORY As Double = 0
    Public GL_MAX_INVENTORY As Double = 0
    Public GL_REORDER_LEVEL As Double = 0
    Public GL_DEFAULT_REORDER_QTY As Double = 0
    Public Function getInventory(itemCode As String) As String
        Dim value As String = ""
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            'create bar code
            Dim codeQuery As String = "SELECT  `qty` FROM `inventorys` WHERE `item_code`='" + itemCode + "'"
            conn.Open()
            command.CommandText = codeQuery
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader()
            While reader.Read
                value = reader.GetString("qty")
                Exit While
            End While
            conn.Close()
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return value
    End Function
    Public Function adjustInventory(itemCode As String, value As Double) As Boolean
        Dim success As Boolean = False
        Dim actValue As String = value.ToString
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            'create bar code
            Dim query As String = "UPDATE `inventorys` SET `qty`=`qty`+" + actValue + " WHERE `item_code`='" + itemCode + "'"
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.ExecuteNonQuery()
            conn.Close()
            success = True
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
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
