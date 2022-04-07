Imports Devart.Data.MySql
Imports System.Threading
Public Class frmSearchItem

    Dim barCode As String = ""
    Dim itemCode As String = ""
    Dim description As String = ""
    Dim pck As String = ""
    Dim price As Double = 0
    Dim vat As Double = 0
    Dim discount As Double = 0
    Dim qty As Integer = 0
    Dim amount As Double = 0
    Dim short_description As String = ""
    Dim costPrice As String = ""
    Dim stock As String = ""

    Private Sub btnBack_Click(sender As Object, e As EventArgs) Handles btnBack.Click
        Me.Dispose()

    End Sub
    Private Sub txtsearchpreviewkeydown(sender As Object, e As PreviewKeyDownEventArgs) Handles txtSearch.PreviewKeyDown
        If e.KeyCode = Keys.Tab Then
            search()
        End If
    End Sub
    Private Sub btnSearch_Click(sender As Object, e As EventArgs) Handles btnSearch.Click
        search()
    End Sub
    Private Function searyItemCode(barcode As String)
        Dim found As Boolean = False
        Dim query As String = "SELECT `bar_code`, `item_code`, `description`, `short_description`,`pck` ,`cost_price`, `selling_price`, `discount`, `stock`, `vat` FROM `inventory` WHERE `bar_code`=@barcode"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@barcode", barcode)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            Dim no As Integer = 0
            If reader.HasRows = True Then
                While reader.Read
                    Dim bar_code As String = reader.GetString("bar_code")
                    Dim itemcode As String = reader.GetString("item_code")
                    Dim description As String = reader.GetString("description")
                    Dim short_description As String = reader.GetString("short_description")
                    Dim pck As String = reader.GetString("pck")
                    Dim costPrice As String = reader.GetString("cost_price")
                    Dim sellingPrice As String = reader.GetString("selling_price")
                    Dim discount As String = reader.GetString("discount")
                    Dim stock As String = reader.GetString("stock")
                    Dim vat As String = reader.GetString("vat")
                    Dim qty As Integer = 1
                    Dim amount As Double = (Val(qty) * sellingPrice) * (1 - Val(discount) / 100)
                    Exit While
                End While
                found = True
            Else
                MsgBox("Item not found", vbCritical + vbOKOnly, "Error: Not found")
            End If
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
            Return vbNull
            Exit Function
        End Try

        Return found
    End Function
    Private Function search()
        Dim found As Boolean = False
        'get barcode entered
        If lblSearchBy.Text = "Bar Code" Then
            If Not searchByBarCode(txtSearch.Text) = True Then
                Return vbNull
                Exit Function
            Else
                found = True
            End If
        ElseIf lblSearchBy.Text = "Item Code" Then
            If Not searchByItemCode(txtSearch.Text) = True Then
                Return vbNull
                Exit Function
            Else
                found = True
            End If
        Else
            If Not searchByDescription(txtSearch.Text) = True Then
                Return vbNull
                Exit Function
            Else
                found = True
            End If
        End If
        Return found
    End Function
    Private Function searchByBarCode(bar_code As String)
        Dim found As Boolean = False
        Dim query As String = "SELECT `items`.`item_code`, `bar_codes`.`item_scan_code`, `items`.`item_description`,`items`.`item_long_description`,`items`.`pck`, `items`.`retail_price`,`items`.`discount`,`items`.`vat`,`inventorys`.`item_code` FROM `items`,`inventorys`,`bar_codes` WHERE `items`.`item_code`=`inventorys`.`item_code` AND `items`.`item_scan_code` =@item_scan_code AND `bar_codes`.`item_code`=`items`.`item_code`"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@item_scan_code", bar_code)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            Dim no As Integer = 0
            If reader.HasRows = True Then
                While reader.Read
                    itemCode = reader.GetString("item_code")
                    barCode = reader.GetString("item_scan_code")
                    description = reader.GetString("item_long_description")
                    pck = reader.GetString("pck")
                    price = reader.GetString("retail_price")
                    discount = reader.GetString("discount")
                    vat = reader.GetString("vat")
                    short_description = reader.GetString("item_description")
                    qty = 1
                    amount = (Val(qty) * price) * (1 - Val(discount) / 100)
                    Exit While
                End While
                found = True
                displayValues()
            Else
                clearValues()
                MsgBox("Item not found", vbCritical + vbOKOnly)
            End If
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
            Return vbNull
            Exit Function
        End Try

        Return found
    End Function
    Private Function searchByItemCode(item_Code As String)
        Dim found As Boolean = False
        Dim query As String = "SELECT `items`.`item_code`, `items`.`item_description`,`items`.`item_long_description`,`items`.`pck`, `items`.`retail_price`,`items`.`discount`,`items`.`vat`,`inventorys`.`item_code` FROM `items`,`inventorys` WHERE `items`.`item_code`=`inventorys`.`item_code` AND `items`.`item_code` =@item_code"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@item_code", item_Code)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            Dim no As Integer = 0
            If reader.HasRows = True Then
                While reader.Read
                    itemCode = reader.GetString("item_code")
                    'barCode = reader.GetString("item_scan_code")
                    description = reader.GetString("item_long_description")
                    short_description = reader.GetString("item_description")
                    pck = reader.GetString("pck")
                    price = reader.GetString("retail_price")
                    discount = reader.GetString("discount")
                    vat = reader.GetString("vat")
                    qty = 1
                    amount = (Val(qty) * price) * (1 - Val(discount) / 100)
                    Exit While
                End While
                found = True
                displayValues()
            Else
                clearValues()
                MsgBox("Item not found", vbExclamation + vbOKOnly, "Error: Not found")
            End If
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
            Return vbNull
            Exit Function
        End Try

        Return found
    End Function

    Private Function searchByDescription(descr As String)
        Dim found As Boolean = False
        Dim query As String = "SELECT `items`.`item_code`,`items`.`item_description`, `items`.`item_long_description`,`items`.`pck`, `items`.`retail_price`,`items`.`discount`,`items`.`vat`,`inventorys`.`item_code` FROM `items`,`inventorys` WHERE `items`.`item_code`=`inventorys`.`item_code` AND `items`.`item_long_description` =@long_description"
        Dim command As New MySqlCommand()
        Dim conn As New MySqlConnection(Database.conString)
        Try
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@long_description", descr)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            Dim no As Integer = 0
            If reader.HasRows = True Then
                While reader.Read
                    itemCode = reader.GetString("item_code")
                    'barCode = reader.GetString("item_scan_code")
                    description = reader.GetString("item_long_description")
                    short_description = reader.GetString("item_description")
                    pck = reader.GetString("pck")
                    price = reader.GetString("retail_price")
                    discount = reader.GetString("discount")
                    vat = reader.GetString("vat")
                    qty = 1
                    amount = (Val(qty) * price) * (1 - Val(discount) / 100)
                    Exit While
                End While
                found = True
                displayValues()
            Else
                clearValues()
                MsgBox("Item not found", vbExclamation + vbOKOnly, "Error: Not found")
            End If
        Catch ex As Devart.Data.MySql.MySqlException
            LError.databaseConnection()
            Return vbNull
            Exit Function
        End Try

        Return vbNull
    End Function
    Private Function clearValues()
        'set values
        barCode = ""
        itemCode = ""
        description = ""
        pck = ""
        price = 0
        discount = 0
        vat = 0
        qty = 0
        amount = 0
        Return vbNull
    End Function
    Private Function displayValues()
        'display values
        txtBarCode.Text = barCode
        txtItemCode.Text = itemCode
        txtDescription.Text = description
        txtshortDescription.text = short_description
        txtPck.Text = pck
        txtPrice.Text = LCurrency.displayValue(price.ToString)
        txtDiscount.Text = LCurrency.displayValue(discount.ToString)
        txtVAT.Text = LCurrency.displayValue(vat.ToString)
        txtQty.Text = qty.ToString
        txtAmount.Text = LCurrency.displayValue(amount.ToString)
        Return vbNull
    End Function

    Private Sub btnInc_Click(sender As Object, e As EventArgs) Handles btnInc.Click
        Try
            txtQty.Text = (Val(txtQty.Text) + 1).ToString
            updateValues()
        Catch ex As Exception
            txtQty.Text = ""
        End Try
    End Sub

    Private Sub btnDec_Click(sender As Object, e As EventArgs) Handles btnDec.Click
        Try
            If Not Val(txtQty.Text) <= 0 Then
                txtQty.Text = (Val(txtQty.Text) - 1).ToString
            Else
                txtQty.Text = ""
            End If
            updateValues()
        Catch ex As Exception
            txtQty.Text = ""
        End Try
    End Sub

    Private Sub txtQty_TextChanged(sender As Object, e As EventArgs) Handles txtQty.TextChanged
        Try
            If Not IsNumeric(txtQty.Text) Or Val(txtQty.Text) Mod 1 > 0 Or txtItemCode.Text = "" Then
                txtQty.Text = ""
            End If
            updateValues()
        Catch ex As Exception
            txtQty.Text = ""
        End Try
        If Val(txtQty.Text) < 1 Then
            btnAdd.Enabled = False
        Else
            btnAdd.Enabled = True
        End If
    End Sub
    Private Function updateValues()
        qty = Val(txtQty.Text)
        amount = (price * qty) * (1 - (discount / 100))
        txtAmount.Text = LCurrency.displayValue(amount.ToString)
        Return vbNull
    End Function


    Dim hold = False


    Private Sub frmSearchItem_Load_1(sender As Object, e As EventArgs) Handles Me.Shown
        txtSearch.Focus()
    End Sub

    Private Sub btnAdd_Click(sender As Object, e As EventArgs) Handles btnAdd.Click

    End Sub



    Private Sub cmbSearch_SelectedIndexChanged(sender As Object, e As EventArgs)

    End Sub

    Private Sub txtSearch_GotFocus(sender As Object, e As EventArgs) Handles txtSearch.Enter

        Dim list As New List(Of String)
        Dim mySource As New AutoCompleteStringCollection
        If Me.Text = "Search Item by Description" Then
            'If txtSearch.Text.Length > 1 Then
            Try
                Dim query As String = "SELECT `items`.`item_code`,`items`.`item_long_description`, `inventorys`.`item_code`FROM `items`,`inventorys` WHERE `items`.`item_code`=`inventorys`.`item_code`" ' AND `items`.`item_long_description` LIKE '%" + txtSearch.Text + "%' LIMIT 1,10000"
                Dim command As New MySqlCommand()
                Dim conn As New MySqlConnection(Database.conString)
                Try
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
                        Exit Sub
                    End If
                    mySource.AddRange(list.ToArray)
                    txtSearch.AutoCompleteCustomSource = mySource
                    txtSearch.AutoCompleteMode = AutoCompleteMode.Suggest
                    txtSearch.AutoCompleteSource = AutoCompleteSource.CustomSource
                Catch ex As Devart.Data.MySql.MySqlException
                    LError.databaseConnection()
                    Exit Sub
                End Try

            Catch ex As Exception
                MsgBox(ex.Message.ToString)
            End Try
        End If
        ' End If
        If txtSearch.Text = "" Then
            btnSearch.Enabled = False
        Else
            btnSearch.Enabled = True
        End If


    End Sub

    Private Sub txtSearch_TextChanged(sender As Object, e As EventArgs) Handles txtSearch.TextChanged

    End Sub

    Private Sub txtSearch_Enter(sender As Object, e As EventArgs) Handles txtSearch.Enter

    End Sub

    Private Sub txtSearch_KeyPress(sender As Object, e As KeyPressEventArgs) Handles txtSearch.KeyPress

    End Sub

    Private Sub txtSearch_KeyDown(sender As Object, e As KeyEventArgs) Handles txtSearch.KeyDown
        If e.KeyCode = Keys.Enter Then
            search()
        End If
    End Sub

    'Private Sub txtSearch_previewkeydown(sender As Object, e As PreviewKeyDownEventArgs) Handles txtSearch.PreviewKeyDown
    '    If e.KeyCode = Keys.Tab Then
    '        search()
    '    End If
    'End Sub
End Class