Imports Devart.Data.MySql

Public Class Item
    Public Function getShortDescription(itemCode As String) As String
        Dim descr As String = ""
        Dim query As String = "SELECT`item_description` FROM `items` WHERE `item_code`='" + itemCode + "'"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader
            While reader.Read
                descr = reader.GetString("item_description")
                Exit While
            End While
        Catch ex As Exception
            LError.databaseConnection()
        End Try
        Return descr
    End Function
    Public Shared Function getCostPrice(itemCode As String) As String
        Dim price As Double = 0
        Dim query As String = "SELECT`unit_cost_price` FROM `items` WHERE `item_code`='" + itemCode + "'"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader
            While reader.Read
                price = reader.GetString("unit_cost_price")
                Exit While
            End While
        Catch ex As Exception
            MsgBox(ex.Message)

            LError.databaseConnection()
        End Try
        Return price
    End Function
    Public Function getItems(descr As String) As List(Of String)
        Dim list As New List(Of String)
        Try
            ' Dim query As String = "SELECT `items`.`item_code`,`items`.`item_long_description`, `inventorys`.`item_code`FROM `items`,`inventorys` WHERE  `items`.`item_long_description` LIKE CONCAT('%','" + descr + "','%') LIMIT 1,500"
            Dim query As String = "SELECT `item_long_description` FROM `items`"

            Dim command As New MySqlCommand()
            Dim conn As New MySqlConnection(Database.conString)
            Try
                list.Clear()
                conn.Open()
                command.CommandText = query
                command.Connection = conn
                command.CommandType = CommandType.Text
                Dim itemreader As MySqlDataReader = command.ExecuteReader()
                If itemreader.HasRows = True Then
                    While itemreader.Read
                        list.Add(itemreader("item_long_description").ToString)
                    End While
                Else
                    Return list
                    Exit Function
                End If
            Catch ex As Devart.Data.MySql.MySqlException
                MsgBox(ex.Message)
                Return list
                Exit Function
            End Try

        Catch ex As Exception
            MsgBox(ex.StackTrace.ToString)
        End Try
        Return list
    End Function
    Public Function getItemDescriptions() As List(Of String)
        Dim list As New List(Of String)
        Try
            ' Dim query As String = "SELECT `items`.`item_code`,`items`.`item_long_description`, `inventorys`.`item_code`FROM `items`,`inventorys` WHERE  `items`.`item_long_description` LIKE CONCAT('%','" + descr + "','%') LIMIT 1,500"
            Dim query As String = "SELECT `item_long_description` FROM `items`"

            Dim command As New MySqlCommand()
            Dim conn As New MySqlConnection(Database.conString)
            Try
                list.Clear()
                conn.Open()
                command.CommandText = query
                command.Connection = conn
                command.CommandType = CommandType.Text
                Dim itemreader As MySqlDataReader = command.ExecuteReader()
                If itemreader.HasRows = True Then
                    While itemreader.Read
                        list.Add(itemreader("item_long_description").ToString)
                    End While
                Else
                    Return list
                    Exit Function
                End If
            Catch ex As Devart.Data.MySql.MySqlException
                MsgBox(ex.Message)
                Return list
                Exit Function
            End Try

        Catch ex As Exception
            MsgBox(ex.StackTrace.ToString)
        End Try
        Return list
    End Function
End Class
