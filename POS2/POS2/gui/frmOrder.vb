Imports Devart.Data.MySql
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class frmOrder
    Private Sub frmOrder_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        dtgrdOrders.Rows.Clear()
        'loadOrders()
        txtWaiterID.Text = ""
        cmbWaiters.Items.Clear()
        cmbWaiters.Items.Add("")

        Dim response As Object = New Object
        Dim json As JObject = New JObject

        Try
            response = Web.get_("users")
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
        Try
            ' json = JObject.Parse(response)
            Dim users_ As List(Of User) = JsonConvert.DeserializeObject(Of List(Of User))(response.ToString)

            For Each user In users_
                'cmbWaiters.Items.Add(user.rollNo)
            Next
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try

    End Sub
    Private Sub clearFields()
        txtOrderNo.Text = ""
        txtWaiterID.Text = ""
        cmbWaiters.Text = ""
        txtStatus.Text = ""
    End Sub
    Private Sub loadOrders()
        'loads orders to orders table

        dtgrdOrders.Rows.Clear()

    End Sub
    Private Function getOrderStatus(orderNo As String)
        Dim status As String = ""

        Return status
    End Function
    Private Sub getOrder(orderNo As String)

    End Sub
    Private Sub loadOrderDetails(orderNo As String)
        dtgrdItemList.Rows.Clear()


    End Sub

    Private Sub dtgrdOrders_CellContentClick(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdOrders.CellClick
        Dim r As Integer = dtgrdOrders.CurrentRow.Index
        Dim orderNo As String = dtgrdOrders.Item(0, r).Value.ToString
        getOrder(orderNo)
        txtOrderNo.ReadOnly = True
        loadOrderDetails(orderNo)
        refreshList()
        calculateValues()
    End Sub

    Private Sub dtgrdOrders_RowHeaderMouseClick(sender As Object, e As DataGridViewCellMouseEventArgs) Handles dtgrdOrders.RowHeaderMouseClick
        Try
            Dim r As Integer = dtgrdOrders.CurrentRow.Index
            Dim orderNo As String = dtgrdOrders.Item(0, r).Value.ToString
            loadOrderDetails(orderNo)
            txtOrderNo.ReadOnly = True
            getOrder(orderNo)
            loadOrderDetails(orderNo)
            refreshList()
            calculateValues()
        Catch ex As Exception

        End Try

    End Sub
    Private Function search(orderNo As String)
        Dim found As Boolean = False


        Return found
    End Function

    Private Sub btnSearch_Click(sender As Object, e As EventArgs) Handles btnSearch.Click
        If search(txtOrderNo.Text) = True Then
            txtOrderNo.ReadOnly = True
        Else
            dtgrdItemList.Rows.Clear()
            dtgrdItemList.ReadOnly = True
            MsgBox("No matching order", vbExclamation + vbOKOnly, "Error: Not found")
        End If
    End Sub
    Dim currentOrder As String = ""

    Private Sub btnNew_Click(sender As Object, e As EventArgs) Handles btnNew.Click
        txtOrderNo.ReadOnly = True
        currentOrder = ""
        dtgrdItemList.Rows.Clear()
        clearFields()
        txtWaiterID.Text = ""
        txtStatus.Text = "NEW"
        btnSearch.Enabled = False
        btnDelete.Enabled = False
    End Sub
    Private Function createNewOrder(waiterID As String)
        Dim orderNo As Long = 0

        Return orderNo
    End Function
    Private Function save()
        Dim saved As Boolean = False
        If txtStatus.Text = "NEW" Then
            If dtgrdItemList.RowCount <= 1 Then
                MsgBox("Could not create order. Order blank", vbOKOnly + vbExclamation, "Error: Blank order")
                Return saved
                Exit Function
            End If
            If txtWaiterID.Text <> "" And cmbWaiters.Text <> "" Then
                currentOrder = createNewOrder(txtWaiterID.Text)
            Else
                saved = False
                MsgBox("Could not create order. No waiter assigned", vbOKOnly + vbExclamation, "Error: Missing information")
                Return saved
                Exit Function
            End If
        ElseIf txtStatus.Text <> "" Then
            currentOrder = txtOrderNo.Text
        End If
        txtStatus.Text = "PENDING"
        For i As Integer = 0 To dtgrdItemList.RowCount - 2
            Dim sn As String = dtgrdItemList.Item(11, i).Value
            Dim barCode As String = dtgrdItemList.Item(0, i).Value
            Dim itemCode As String = dtgrdItemList.Item(1, i).Value
            Dim description As String = dtgrdItemList.Item(2, i).Value
            Dim qty As String = Val(dtgrdItemList.Item(7, i).Value)
            Dim price As String = dtgrdItemList.Item(4, i).Value


        Next
        saved = True
        loadOrderDetails(txtOrderNo.Text)
        dtgrdOrders.Rows.Clear()
        loadOrders()
        refreshList()
        calculateValues()
        Return saved
    End Function

    Private Sub btnSavePrint_Click(sender As Object, e As EventArgs) Handles btnSavePrint.Click
        If save() = True Then
            'print order
            Dim s As Integer = dtgrdItemList.RowCount - 1
            length = s
            Dim bcodes(s) As String
            Dim icodes(s) As String
            Dim descr(s) As String
            Dim qtys(s) As String
            Dim prices(s) As String
            Dim amounts(s) As String
            For i As Integer = 0 To s - 1
                bcodes(i) = dtgrdItemList.Item(0, i).Value.ToString
                icodes(i) = dtgrdItemList.Item(1, i).Value.ToString
                descr(i) = dtgrdItemList.Item(2, i).Value.ToString
                qtys(i) = dtgrdItemList.Item(7, i).Value.ToString
                prices(i) = dtgrdItemList.Item(4, i).Value.ToString
                amounts(i) = dtgrdItemList.Item(8, i).Value.ToString
            Next
            PointOfSale.printOrder("1", txtOrderNo.Text, cmbWaiters.Text, icodes, descr, qtys, prices, amounts, txtGrandTotal.Text)
            PointOfSale.printOrder("2", txtOrderNo.Text, cmbWaiters.Text, icodes, descr, qtys, prices, amounts, txtGrandTotal.Text)
        End If
    End Sub

    Private Sub cmbWaiters_SelectedIndexChanged(sender As Object, e As EventArgs) Handles cmbWaiters.SelectedIndexChanged

    End Sub

    Dim barCode As String = ""
    Dim itemCode As String = ""
    Dim description As String = ""
    Dim longDescription As String = ""
    Dim pck As String = ""
    Dim price As String = ""
    Dim vat As String = ""
    Dim discount As String = ""
    Dim qty As String = ""
    Dim amount As String = ""
    Dim void As Boolean = False
    Dim allowVoid As Boolean = False
    Dim seq As Integer = 0

    Private Function add(barCode As String, itemCode As String, descr As String, pck As String, price As String, vat As String, disc As String, qty As String, amount As String, shortDescr As String)

        Dim dtgrdRow As New DataGridViewRow
        Dim dtgrdCell As DataGridViewCell

        dtgrdItemList.EndEdit()

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = barCode
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = itemCode.ToString
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = descr.ToString
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = pck.ToString
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = price
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = vat
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = disc
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = qty
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdCell = New DataGridViewTextBoxCell()
        dtgrdCell.Value = amount
        dtgrdRow.Cells.Add(dtgrdCell)

        dtgrdItemList.Rows.Add(dtgrdRow)

        refreshList()
        calculateValues()

        Return vbNull
    End Function

    Private Sub dtgrdItemList_CellEndEdit(sender As Object, e As DataGridViewCellEventArgs)
        Dim qty As Integer = 0
        Dim sn As String = ""
        Try
            qty = Val(dtgrdItemList.Item(7, e.RowIndex).Value)
            sn = dtgrdItemList.Item(11, e.RowIndex).Value.ToString
        Catch ex As Exception

        End Try

        If Val(dtgrdItemList.Item(7, e.RowIndex).Value) >= 0 And Val(dtgrdItemList.Item(7, e.RowIndex).Value) <= 1000 Then
            If qty > 0 Then
                updateQty(qty, sn)
            Else
                updateQty("0", sn)
            End If
            calculateValues()
        Else
            MsgBox("Invalid quantity value. Quantity value should be between 1 and 1000", vbOKOnly + vbCritical, "Error: Invalid entry")
            dtgrdItemList.Item(7, e.RowIndex).Value = 1
            calculateValues()
        End If
        Try
            If dtgrdItemList.CurrentCell.ColumnIndex = 2 Then
                search() 'do further research
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try

    End Sub

    Protected Overrides Function ProcessCmdKey(ByRef msg As System.Windows.Forms.Message, ByVal keyData As System.Windows.Forms.Keys) As Boolean
        If msg.WParam.ToInt32() = Keys.Enter Then
            dtgrdItemList.EndEdit()
            search()
            Return True
            Exit Function
        End If

        ' Return MyBase.ProcessCmdKey(msg, keyData)
        Return False
    End Function
    Private Sub dtgrdItemList_CellClick(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdItemList.CellClick
        Try
            If dtgrdItemList.CurrentCell.ColumnIndex = 2 Then
                Dim control As New TextBox
                control = DirectCast(dtgrdItemList.EditingControl, TextBox)
                Dim list As New List(Of String)
                Dim mySource As New AutoCompleteStringCollection
                Dim item As New Item
                list = item.getItems(control.Text)
                mySource.AddRange(list.ToArray)
                control.AutoCompleteCustomSource = mySource
                control.AutoCompleteMode = AutoCompleteMode.Suggest
                control.AutoCompleteSource = AutoCompleteSource.CustomSource
            End If
        Catch ex As Exception
            'MsgBox(ex.StackTrace)
        End Try

    End Sub

    Private Sub search()
        If txtStatus.Text = "" Then
            Exit Sub
        End If
        Try
            Dim row As Integer = dtgrdItemList.CurrentCell.RowIndex
            Dim col As Integer = dtgrdItemList.CurrentCell.ColumnIndex
            If col = 0 And row = dtgrdItemList.RowCount - 2 Then
                'search item
                'add item to list
                Dim value As String = ""
                Dim search As Boolean = True
                Try
                    value = dtgrdItemList.Rows(row).Cells(col).Value.ToString
                    If value = "" Or value = "0" Then
                        search = False
                        Exit Sub
                    End If
                Catch ex As Exception
                    search = False
                End Try
                If search = True Then
                    Dim found As Boolean = searchByBarcode(value)
                    If found = True Then
                        'item found

                    ElseIf found = False Then
                        MsgBox("Item not found", vbOKOnly + vbExclamation, "Error: Not found")
                    End If

                End If
            ElseIf col = 7 And row = dtgrdItemList.RowCount - 2 Then

            End If
            If col = 1 And row = dtgrdItemList.RowCount - 2 Then
                'search item
                'add item to list
                Dim value As String = ""
                Dim search As Boolean = True
                Try
                    value = dtgrdItemList.Rows(row).Cells(col).Value.ToString
                    If value = "" Or value = "0" Then
                        search = False
                        Exit Sub
                    End If
                Catch ex As Exception
                    search = False
                End Try
                If search = True Then
                    Dim found As Boolean = searchByItemCode(value)
                    If found = True Then
                        'item found

                    ElseIf found = False Then
                        MsgBox("Item not found", vbOKOnly + vbExclamation, "Error: Not found")
                    End If

                End If
            ElseIf col = 7 And row = dtgrdItemList.RowCount - 2 Then

            End If
            If col = 2 And row = dtgrdItemList.RowCount - 2 Then
                'search item
                'add item to list
                Dim value As String = ""
                Dim search As Boolean = True
                Try
                    value = dtgrdItemList.Rows(row).Cells(col).Value.ToString
                    If value = "" Then
                        search = False
                        Exit Sub
                    End If
                Catch ex As Exception
                    MsgBox(ex.StackTrace)
                    search = False
                End Try
                If search = True Then
                    Dim found As Boolean = searchByDescription(value)
                    If found = True Then
                        'item found

                    ElseIf found = False Then
                        MsgBox("Item not found", vbOKOnly + vbExclamation, "Error: Not found")
                    End If

                End If
            ElseIf col = 7 And row = dtgrdItemList.RowCount - 2 Then

            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try

    End Sub
    Private Sub loadOrder(orderNo As String)
        dtgrdItemList.Rows.Clear()
        Dim query As String = "SELECT `sn`, `order_no`, `barcode`, `item_code`, `description`, `qty`, `price` FROM `cust_order_details` WHERE `order_no`='" + orderNo + "'"
        Dim command As New MySqlCommand()

        Dim reader As MySqlDataReader = command.ExecuteReader()
        Try
            If reader.HasRows = True Then
                Dim i As Integer = 0
                While reader.Read
                    Dim barcode As String = reader.GetString("barcode")
                    Dim itemcode As String = reader.GetString("item_code")
                    Dim description As String = reader.GetString("description")
                    Dim price As String = reader.GetString("price")
                    Dim vat As String = ""
                    Dim discount As String = ""
                    Dim qty As String = reader.GetString("qty")
                    Dim amount As String = ""
                    Dim sn As String = reader.GetString("sn")
                    Dim void As String = ""
                    Dim shortDescr As String = ""

                    Dim dtgrdRow As New DataGridViewRow
                    Dim dtgrdCell As DataGridViewCell

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = barcode
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = itemcode
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = description
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = ""
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = LCurrency.displayValue(price.ToString)
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = vat
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = discount
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = qty
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = amount
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewCheckBoxCell()
                    If void = "YES" Then
                        dtgrdCell.Value = True
                    Else
                        dtgrdCell.Value = False
                    End If
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = shortDescr
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdCell = New DataGridViewTextBoxCell()
                    dtgrdCell.Value = sn
                    dtgrdRow.Cells.Add(dtgrdCell)

                    dtgrdItemList.Rows.Add(dtgrdRow)

                    dtgrdItemList.Item(0, i).ReadOnly = True
                    dtgrdItemList.Item(1, i).ReadOnly = True
                    dtgrdItemList.Item(2, i).ReadOnly = True

                    i = i + 1


                End While
            Else

            End If
            refreshList()
            calculateValues()
            dtgrdItemList.CurrentCell = dtgrdItemList(0, dtgrdItemList.RowCount - 1)
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try

    End Sub

    Private Function searchByBarcode(barCode As String)

        Dim found As Boolean = False
        Dim query As String = "SELECT `items`.`item_code`, `bar_codes`.`item_scan_code`, `items`.`item_description`,`items`.`item_long_description`,`items`.`pck`, `items`.`retail_price`,`items`.`discount`,`items`.`vat`,`inventorys`.`item_code` FROM `items`,`inventorys`,`bar_codes` WHERE `items`.`item_code`=`inventorys`.`item_code` AND `bar_codes`.`item_scan_code` =@item_scan_code AND `bar_codes`.`item_code`=`items`.`item_code`"
        Dim command As New MySqlCommand()

        Dim reader As MySqlDataReader = command.ExecuteReader()
        Dim no As Integer = 0
        Dim row As Integer = dtgrdItemList.CurrentCell.RowIndex
        Try
            If reader.HasRows = True Then
                While reader.Read
                    barCode = reader.GetString("item_scan_code")
                    itemCode = reader.GetString("item_code")
                    description = reader.GetString("item_description")
                    longDescription = reader.GetString("item_long_description")
                    pck = reader.GetString("pck")
                    price = reader.GetString("retail_price")
                    discount = reader.GetString("discount")
                    vat = reader.GetString("vat")
                    qty = 1
                    price = (Val(price)) * (1 - Val(discount) / 100)
                    amount = (Val(qty) * price) * (1 - Val(discount) / 100)

                    found = True

                    If found = True Then

                        dtgrdItemList.Item(0, row).Value = barCode
                        dtgrdItemList.Item(1, row).Value = itemCode
                        dtgrdItemList.Item(2, row).Value = longDescription
                        dtgrdItemList.Item(4, row).Value = LCurrency.displayValue(price.ToString)
                        dtgrdItemList.Item(5, row).Value = LCurrency.displayValue(vat.ToString)
                        dtgrdItemList.Item(6, row).Value = LCurrency.displayValue(discount.ToString)
                        dtgrdItemList.Item(7, row).Value = qty
                        dtgrdItemList.Item(8, row).Value = LCurrency.displayValue(amount.ToString)
                        dtgrdItemList.Item(10, row).Value = description

                        dtgrdItemList.Item(0, row).ReadOnly = True
                        dtgrdItemList.Item(1, row).ReadOnly = True
                        dtgrdItemList.Item(2, row).ReadOnly = True

                    End If
                    Exit While
                End While
            Else
                dtgrdItemList.Item(0, row).Value = ""
                dtgrdItemList.Item(1, row).Value = ""
                dtgrdItemList.Item(2, row).Value = ""
                dtgrdItemList.Item(4, row).Value = ""
                dtgrdItemList.Item(5, row).Value = ""
                dtgrdItemList.Item(6, row).Value = ""
                dtgrdItemList.Item(7, row).Value = ""
                dtgrdItemList.Item(8, row).Value = ""

                dtgrdItemList.EndEdit()
                refreshList()
                calculateValues()
            End If
        Catch ex As Exception

        End Try

        Return found
    End Function
    Private Sub AddToOrder(sn As String, orderNo As String, barcode As String, itemCode As String, description As String, price As String, vat As String, discount As String, qty As String, amount As String, shortDescr As String)

    End Sub
    Private Function searchByDescription(longDescr As String)

        Dim found As Boolean = False
        Dim query As String = "SELECT `sn`, `item_code`, `item_scan_code`, `item_long_description`, `item_description`, `pck`, `department_id`, `class_id`, `sub_class_id`, `supplier_id`, `unit_cost_price`, `retail_price`, `discount`, `vat`, `margin`, `standard_uom`, `active` FROM `items` WHERE `item_long_description`='" + longDescr + "'"
        Dim command As New MySqlCommand()

        Dim reader As MySqlDataReader = command.ExecuteReader()
        Dim no As Integer = 0
        Dim row As Integer = dtgrdItemList.CurrentCell.RowIndex
        Try
            If reader.HasRows = True Then
                While reader.Read
                    barCode = ""
                    itemCode = reader.GetString("item_code")
                    description = reader.GetString("item_description")
                    longDescription = reader.GetString("item_long_description")
                    pck = reader.GetString("pck")
                    price = reader.GetString("retail_price")
                    discount = reader.GetString("discount")
                    vat = reader.GetString("vat")
                    qty = 1
                    price = (Val(price)) * (1 - Val(discount) / 100)
                    amount = (Val(qty) * price) * (1 - Val(discount) / 100)

                    found = True

                    If found = True Then

                        dtgrdItemList.Item(0, row).Value = barCode
                        dtgrdItemList.Item(1, row).Value = itemCode
                        dtgrdItemList.Item(2, row).Value = longDescription
                        dtgrdItemList.Item(4, row).Value = LCurrency.displayValue(price.ToString)
                        dtgrdItemList.Item(5, row).Value = LCurrency.displayValue(vat.ToString)
                        dtgrdItemList.Item(6, row).Value = LCurrency.displayValue(discount.ToString)
                        dtgrdItemList.Item(7, row).Value = qty
                        dtgrdItemList.Item(8, row).Value = LCurrency.displayValue(amount.ToString)
                        dtgrdItemList.Item(10, row).Value = description

                        dtgrdItemList.Item(0, row).ReadOnly = True
                        dtgrdItemList.Item(1, row).ReadOnly = True
                        dtgrdItemList.Item(2, row).ReadOnly = True

                    End If
                    Exit While
                End While
            Else
                dtgrdItemList.Item(0, row).Value = ""
                dtgrdItemList.Item(1, row).Value = ""
                dtgrdItemList.Item(2, row).Value = ""
                dtgrdItemList.Item(4, row).Value = ""
                dtgrdItemList.Item(5, row).Value = ""
                dtgrdItemList.Item(6, row).Value = ""
                dtgrdItemList.Item(7, row).Value = ""
                dtgrdItemList.Item(8, row).Value = ""


            End If
        Catch ex As Exception
            ' MsgBox(ex.Message)

        End Try
        dtgrdItemList.EndEdit()
        Return found
    End Function
    Private Function searchByItemCode(itemCode As String)

        Dim found As Boolean = False
        Dim query As String = "SELECT `items`.`item_code`, `items`.`item_description`,`items`.`item_long_description`,`items`.`pck`, `items`.`retail_price`,`items`.`discount`,`items`.`vat`,`inventorys`.`item_code` FROM `items`,`inventorys` WHERE `items`.`item_code`=`inventorys`.`item_code` AND `items`.`item_code` =@item_code"
        Dim command As New MySqlCommand()

        Dim reader As MySqlDataReader = command.ExecuteReader()
        Dim no As Integer = 0
        Dim row As Integer = dtgrdItemList.CurrentCell.RowIndex
        Try
            If reader.HasRows = True Then
                While reader.Read
                    barCode = ""
                    itemCode = reader.GetString("item_code")
                    description = reader.GetString("item_description")
                    longDescription = reader.GetString("item_long_description")
                    pck = reader.GetString("pck")
                    price = reader.GetString("retail_price")
                    discount = reader.GetString("discount")
                    vat = reader.GetString("vat")
                    qty = 1
                    price = (Val(price)) * (1 - Val(discount) / 100)
                    amount = (Val(qty) * price) * (1 - Val(discount) / 100)

                    found = True

                    If found = True Then

                        dtgrdItemList.Item(0, row).Value = barCode
                        dtgrdItemList.Item(1, row).Value = itemCode
                        dtgrdItemList.Item(2, row).Value = longDescription
                        dtgrdItemList.Item(4, row).Value = LCurrency.displayValue(price.ToString)
                        dtgrdItemList.Item(5, row).Value = LCurrency.displayValue(vat.ToString)
                        dtgrdItemList.Item(6, row).Value = LCurrency.displayValue(discount.ToString)
                        dtgrdItemList.Item(7, row).Value = qty
                        dtgrdItemList.Item(8, row).Value = LCurrency.displayValue(amount.ToString)
                        dtgrdItemList.Item(10, row).Value = description

                        dtgrdItemList.Item(0, row).ReadOnly = True
                        dtgrdItemList.Item(1, row).ReadOnly = True
                        dtgrdItemList.Item(2, row).ReadOnly = True

                    End If
                    Exit While
                End While
            Else
                dtgrdItemList.Item(0, row).Value = ""
                dtgrdItemList.Item(1, row).Value = ""
                dtgrdItemList.Item(2, row).Value = ""
                dtgrdItemList.Item(4, row).Value = ""
                dtgrdItemList.Item(5, row).Value = ""
                dtgrdItemList.Item(6, row).Value = ""
                dtgrdItemList.Item(7, row).Value = ""
                dtgrdItemList.Item(8, row).Value = ""

                dtgrdItemList.EndEdit()

            End If
        Catch ex As Exception
            ' MsgBox(ex.StackTrace)
        End Try
        dtgrdItemList.EndEdit()
        Return found
    End Function
    Private Function refreshList()
        If dtgrdItemList.RowCount > 0 Then
            Dim max As Integer = dtgrdItemList.RowCount - 2
            For i As Integer = max To 0 Step -1
                If dtgrdItemList.Item(1, i).Value = "" Or Val(dtgrdItemList.Item(7, i).Value) <= 0 Then
                    dtgrdItemList.Rows.RemoveAt(i)
                End If
            Next
        End If
        Try
            dtgrdItemList.EndEdit()
        Catch ex As Exception
            'MsgBox(ex.Message)
        End Try
        Return vbNull
    End Function
    Dim allow As Boolean = False
    Private Function calculateValues()
        Try
            dtgrdItemList.EndEdit()
            Dim _total As Double = 0
            Dim _vat As Double = 0
            Dim _discount As Double = 0
            Dim _grandTotal As Double = 0
            Dim rows As Integer = dtgrdItemList.RowCount
            For i As Integer = 0 To rows - 2

                Dim price As Double = Val(LCurrency.getValue(dtgrdItemList.Item(4, i).Value))
                Dim vat As Double = Val(LCurrency.getValue(dtgrdItemList.Item(5, i).Value))
                Dim discount As Double = Val(LCurrency.getValue(dtgrdItemList.Item(6, i).Value))
                Dim qty As Integer = Val(dtgrdItemList.Item(7, i).Value)

                Dim amount As Double = price * qty * (1 - discount / 100)
                dtgrdItemList.Item(8, i).Value = LCurrency.displayValue(amount.ToString)

                _total = _total + Val(LCurrency.getValue(dtgrdItemList.Item(8, i).Value.ToString))
                _vat = _vat + ((Val(LCurrency.getValue(dtgrdItemList.Item(5, i).Value.ToString)))) * Val(LCurrency.getValue(dtgrdItemList.Item(8, i).Value.ToString) / (100 + Val(LCurrency.getValue(dtgrdItemList.Item(5, i).Value.ToString))))
                _discount = _discount + (Val(LCurrency.getValue(dtgrdItemList.Item(6, i).Value.ToString)) / 100) * Val(LCurrency.getValue(dtgrdItemList.Item(8, i).Value.ToString))
            Next
            _grandTotal = _total
            txtGrandTotal.Text = LCurrency.displayValue(_grandTotal)
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try

        Return vbNull
    End Function

    Private Sub Button4_Click(sender As Object, e As EventArgs) Handles btnRefresh.Click
        refreshList()
        calculateValues()
    End Sub

    Dim dialog As frmSearchItem


    Private TILL_NO As String = ""
    Private RECEIPT_NO As Integer = 0

    Private Function isAllVoid()
        Dim allVoid As Boolean = True
        For i As Integer = 0 To dtgrdItemList.RowCount - 2
            If dtgrdItemList.Item(9, i).Value = False Then
                allVoid = False
            End If
        Next
        Return allVoid
    End Function


    Dim saleId As String = ""

    Private Function addInList(icode As String, barcode As String)
        Dim updated As Boolean = False
        Dim sn As String = ""
        Try
            For i As Integer = 0 To dtgrdItemList.RowCount - 3
                If dtgrdItemList.Item(1, i).Value.ToString = icode And dtgrdItemList.Item(9, i).Value = False Then
                    sn = dtgrdItemList.Item(11, i).Value.ToString
                    Exit For
                End If
            Next
        Catch ex As Exception

        End Try

        If barcode <> "" Then

        End If
        Return updated
    End Function
    Private Function updateQty(qty As Integer, sn As String)
        Dim updated As Boolean = False

        Return updated
    End Function

    Protected Overridable Function place(key As String)
        Try
            Dim row As Integer = dtgrdItemList.CurrentCell.RowIndex
            Dim col As Integer = dtgrdItemList.CurrentCell.ColumnIndex
            If dtgrdItemList.CurrentCell.ColumnIndex = 0 Or dtgrdItemList.CurrentCell.ColumnIndex = 1 Or dtgrdItemList.CurrentCell.ColumnIndex = 2 Or dtgrdItemList.CurrentCell.ColumnIndex = 7 Then

                dtgrdItemList.Select()
                dtgrdItemList.CurrentCell = dtgrdItemList.Item(col, row)
                Dim control As TextBox = DirectCast(dtgrdItemList.EditingControl, TextBox)
                control.SelectionStart = dtgrdItemList.CurrentCell.Value.ToString.Length
                control.SelectionLength = 0
                SendKeys.Send(key)
            Else
                dtgrdItemList.Select()
                dtgrdItemList.CurrentCell = dtgrdItemList.Item(col, row)
                SendKeys.Send(key)
            End If
        Catch ex As Exception
            'MsgBox(ex.StackTrace)
            Try
                SendKeys.Send(key)
            Catch ex2 As Exception
                MsgBox(ex.StackTrace)
            End Try

        End Try
        Return vbNull
    End Function

    Private Sub dtgrdViewItemList_CellClick(sender As Object, e As DataGridViewCellEventArgs)
        Try
            If dtgrdItemList.CurrentCell.ColumnIndex = 3 Then
                Dim control As New TextBox
                control = DirectCast(dtgrdItemList.EditingControl, TextBox)
                Dim list As New List(Of String)
                Dim mySource As New AutoCompleteStringCollection
                Dim item As New Item
                list = item.getItems(control.Text)
                mySource.AddRange(list.ToArray)
                control.AutoCompleteCustomSource = mySource
                control.AutoCompleteMode = AutoCompleteMode.Suggest
                control.AutoCompleteSource = AutoCompleteSource.CustomSource
            End If
        Catch ex As Exception
            'MsgBox(ex.StackTrace)
        End Try
    End Sub

    Declare Function Wow64DisableWow64FsRedirection Lib "kernel32" (ByRef oldvalue As Long) As Boolean
    Declare Function Wow64EnableWow64FsRedirection Lib "kernel32" (ByRef oldvalue As Long) As Boolean
    Private osk As String = "C:\Windows\System32\osk.exe"

    Private Sub startOSK()
        Dim old As Long
        If Environment.Is64BitOperatingSystem Then
            If Wow64DisableWow64FsRedirection(old) Then
                Process.Start(osk)
                Wow64EnableWow64FsRedirection(old)
            End If
        Else
            Process.Start(osk)
        End If
    End Sub
    Private Sub btnKeyboard_Click(sender As Object, e As EventArgs) Handles btnKeyBoard.Click
        startOSK()
    End Sub

    Private Sub AddToOrderDetails(sn As String, orderNo As String, barcode As String, itemCode As String, description As String, price As String, vat As String, discount As String, qty As String, amount As String, shortDescr As String)

    End Sub

    Private Sub btnEdit_Click(sender As Object, e As EventArgs) Handles btnEdit.Click
        clearFields()
        txtWaiterID.Text = ""
        dtgrdItemList.Rows.Clear()
        txtOrderNo.ReadOnly = False
        btnSearch.Enabled = True
        btnDelete.Enabled = False
    End Sub

    Private Sub dtgrdItemList_CellContentClick(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdItemList.CellContentClick

    End Sub

    Private Sub dtgrdItemList_KeyDown(sender As Object, e As KeyEventArgs) Handles dtgrdItemList.KeyDown

    End Sub

    Private Sub dtgrdOrders_CellContentClick_1(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdOrders.CellContentClick

    End Sub

    Private Sub btnClose_Click(sender As Object, e As EventArgs) Handles btnClose.Click
        Me.Dispose()
    End Sub

    Private Sub btnDelete_Click(sender As Object, e As EventArgs) Handles btnDelete.Click
        Dim res As Integer = MsgBox("Are you sure you want to remove the selected order? This can not be undone.", vbQuestion + vbYesNo, "Delete order?")
        If res = DialogResult.Yes Then

        End If
        clearFields()
        dtgrdOrders.Rows.Clear()
        loadOrders()
        txtWaiterID.Text = ""
        cmbWaiters.Text = ""
        dtgrdItemList.Rows.Clear()
    End Sub

    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        clearFields()
        btnDelete.Enabled = False
        btnSearch.Enabled = False
        txtOrderNo.ReadOnly = True
    End Sub
    Public Shared barcodes() As String
    Public Shared itemcodes() As String
    Public Shared quantitys() As Integer
    Public Shared length As Integer
    Public Shared showAgain As Boolean = False

    Private Sub btnCheckout_Click(sender As Object, e As EventArgs) Handles btnCheckout.Click
        showAgain = False
        Dim res As Integer = MsgBox("Are you sure you want to complete this order?", vbQuestion + vbYesNo, "Complete order")
        If res = DialogResult.Yes Then
            Dim s As Integer = dtgrdItemList.RowCount - 1
            length = s
            Dim bcodes(s) As String
            Dim icodes(s) As String
            Dim qtys(s) As Integer
            For i As Integer = 0 To s - 1
                bcodes(i) = dtgrdItemList.Item(0, i).Value.ToString
                icodes(i) = dtgrdItemList.Item(1, i).Value.ToString
                qtys(i) = Val(dtgrdItemList.Item(7, i).Value)
            Next
            barcodes = bcodes
            itemcodes = icodes
            quantitys = qtys
        Else
            Me.DialogResult = System.Windows.Forms.DialogResult.None
            Exit Sub
        End If

    End Sub
End Class