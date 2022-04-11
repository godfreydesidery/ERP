Imports System.Windows.Forms
Imports Devart.Data.MySql
Imports Microsoft.PointOfService.PosPrinter
Imports POS.Devices
Imports Microsoft.PointOfService
Imports Newtonsoft.Json.Linq
Imports Newtonsoft.Json

Public Class frmMain
    Private Sub ExitToolsStripMenuItem_Click(ByVal sender As Object, ByVal e As EventArgs) Handles ExitToolStripMenuItem.Click
        Me.Close()
    End Sub

    Dim cart As Cart = New Cart

    Dim barCode As String = ""
    Dim code As String = ""
    Dim ShortDescription As String = ""
    Dim description As String = ""
    Dim packSize As Double = 1
    Dim price As Double = 0
    Dim vat As Double = 0
    Dim discountRatio As Double = 0
    Dim qty As Double = 0
    Dim amount As Double = 0
    Dim void As Boolean = False
    Dim allowVoid As Boolean = False
    Dim seq As Integer = 0

    Private Function openCashDrawer()
        Dim isOpen As Boolean = False
        Try

            Dim deviceInfo As DeviceInfo
            Dim posExplorer As New PosExplorer
            Dim strLogicalName As String = ""
            Dim ppdi As Microsoft.PointOfService.DeviceInfo = posExplorer.GetDevice(DeviceType.CashDrawer, strLogicalName)

            Dim P As Microsoft.PointOfService.CashDrawer = posExplorer.CreateInstance(ppdi)
            deviceInfo = posExplorer.GetDevice(DeviceType.CashDrawer, strLogicalName)
            P = posExplorer.CreateInstance(deviceInfo)
            P.OpenDrawer()

        Catch ex As Exception
            MsgBox(ex.Message, vbExclamation + vbOKOnly, "Error: Cash Drawer")
        End Try
        Return isOpen
    End Function

    Private Sub dtgrdViewItemList_CellEndEdit(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdViewItemList.CellEndEdit
        Dim qty As Double = 0
        Dim sn As String = ""
        Try
            qty = Val(dtgrdViewItemList.Item(7, e.RowIndex).Value)
            sn = dtgrdViewItemList.Item(11, e.RowIndex).Value.ToString
        Catch ex As Exception
            'MsgBox(ex.StackTrace)
        End Try

        If Val(dtgrdViewItemList.Item(7, e.RowIndex).Value) >= 0 And Val(dtgrdViewItemList.Item(7, e.RowIndex).Value) <= 1000 And dtgrdViewItemList.Item(11, e.RowIndex).Value <> "" Then
            If qty > 0 Then
                updateQty(qty, sn)
            Else
                updateQty(0, sn)
            End If
            calculateValues()
        Else
            If Val(dtgrdViewItemList.Item(7, e.RowIndex).Value) <= 0 And dtgrdViewItemList.Item(1, e.RowIndex).Value <> "" Then
                MsgBox("Invalid quantity value. Quantity value should be between 1 and 1000", vbOKOnly + vbCritical, "Error: Invalid entry")
                dtgrdViewItemList.Item(7, e.RowIndex).Value = 1
                refreshList()
                calculateValues()
            End If
        End If
    End Sub

    Protected Overrides Function ProcessCmdKey(ByRef msg As System.Windows.Forms.Message, ByVal keyData As System.Windows.Forms.Keys) As Boolean
        If msg.WParam.ToInt32() = Keys.Enter Then
            dtgrdViewItemList.EndEdit()
            search()
            Return True
            Exit Function
        End If
        ' Return MyBase.ProcessCmdKey(msg, keyData)
        Return False
    End Function

    Private Sub search()
        Try
            Dim row As Integer = dtgrdViewItemList.CurrentCell.RowIndex
            Dim col As Integer = dtgrdViewItemList.CurrentCell.ColumnIndex
            If col = 0 And row = dtgrdViewItemList.RowCount - 2 Then
                'search item
                'add item to list
                Dim value As String = ""
                Dim search As Boolean = True
                Try
                    value = dtgrdViewItemList.Rows(row).Cells(col).Value.ToString
                    If value = "" Or value = "0" Then
                        search = False
                        Exit Sub
                    End If
                Catch ex As Exception
                    search = False
                End Try
                If search = True Then
                    Dim found As Boolean = searchByBarcode(value, 1)
                    If found = True Then
                        'item found
                        If SaleSequence.multiple = True Then
                            For i As Integer = 0 To 7
                                'SendKeys.Send("{right}")
                            Next
                        Else
                            'SendKeys.Send("{down}")
                        End If
                    ElseIf found = False Then
                        MsgBox("Product not found", vbOKOnly + vbExclamation, "Error: Not found")
                    End If

                End If
            ElseIf col = 7 And row = dtgrdViewItemList.RowCount - 2 Then
                For i As Integer = 0 To 6
                    ' SendKeys.Send("{left}")
                Next
                ' SendKeys.Send("{down}")
            End If
            If col = 1 And row = dtgrdViewItemList.RowCount - 2 Then
                'search item
                'add item to list
                Dim value As String = ""
                Dim search As Boolean = True
                Try
                    value = dtgrdViewItemList.Rows(row).Cells(col).Value.ToString
                    If value = "" Or value = "0" Then
                        search = False
                        Exit Sub
                    End If
                Catch ex As Exception
                    search = False
                End Try
                If search = True Then
                    Dim found As Boolean = searchByCode(value, 1)
                    If found = True Then
                        'item found
                        If SaleSequence.multiple = True Then
                            For i As Integer = 0 To 6
                                ' SendKeys.Send("{right}")
                            Next
                        Else
                            'SendKeys.Send("{down}")
                            'SendKeys.Send("{left}")
                        End If
                    ElseIf found = False Then
                        MsgBox("Product not found", vbOKOnly + vbExclamation, "Error: Not found")
                    End If

                End If
            ElseIf col = 7 And row = dtgrdViewItemList.RowCount - 2 Then
                For i As Integer = 0 To 6
                    'SendKeys.Send("{left}")
                Next
                ' SendKeys.Send("{down}")
            End If
            If col = 2 And row = dtgrdViewItemList.RowCount - 2 Then
                'search item
                'add item to list
                Dim value As String = ""
                Dim search As Boolean = True
                Try
                    value = dtgrdViewItemList.Rows(row).Cells(col).Value.ToString
                    If value = "" Then
                        search = False
                        Exit Sub
                    End If
                Catch ex As Exception
                    MsgBox(ex.StackTrace)
                    search = False
                End Try
                If search = True Then
                    Dim found As Boolean = searchByDescription(value, 1)
                    If found = True Then
                        'item found
                        If SaleSequence.multiple = True Then
                            For i As Integer = 0 To 4
                                ' SendKeys.Send("{right}")
                            Next
                        Else
                            'SendKeys.Send("{down}")
                            'SendKeys.Send("{left}")
                            'SendKeys.Send("{left}")
                        End If
                    ElseIf found = False Then
                        MsgBox("Product not found", vbOKOnly + vbExclamation, "Error: Not found")
                    End If

                End If
            ElseIf col = 7 And row = dtgrdViewItemList.RowCount - 2 Then
                For i As Integer = 0 To 6
                    'SendKeys.Send("{left}")
                Next
                ' SendKeys.Send("{down}")
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        ' refreshList()
        ' dtgrdViewItemList.Select()
    End Sub
    Private Function searchByBarcode(barcode As String, q As Double) As Boolean
        Return search(barcode, "", "", q)
    End Function
    Private Function searchByCode(code As String, q As Double) As Boolean
        Return search("", code, "", q)
    End Function
    Private Function searchByDescription(description As String, q As Double) As Boolean
        Return search("", "", description, q)
    End Function

    Private Function search(barcode As String, code As String, description As String, q As Double)
        Dim found As Boolean = False
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        Dim no As Integer = 0
        Dim row As Integer = 0
        Try
            If barcode <> "" Then
                response = Web.get_("products/get_by_barcode?barcode=" + barcode)
            ElseIf code <> "" Then
                response = Web.get_("products/get_by_code?code=" + code)
            Else
                response = Web.get_("products/get_by_description?description=" + description)
            End If
            json = JObject.Parse(response)
            Dim product As Product = JsonConvert.DeserializeObject(Of Product)(json.ToString)

            barcode = product.barcode
            code = product.code
            description = product.description
            ShortDescription = product.shortDescription
            packSize = product.packSize
            discountRatio = product.discountRatio
            vat = product.vat
            qty = q
            price = product.sellingPriceVatIncl
            amount = (Val(qty) * price) * (1 - Val(discountRatio) / 100)
            found = True

            If code = "" Then
                found = False
                cart = loadCart(Till.TILLNO)
                displayCart(cart)
            End If

            Try
                row = dtgrdViewItemList.CurrentCell.RowIndex
            Catch ex As Exception
                dtgrdViewItemList.Rows.Add()
                row = dtgrdViewItemList.CurrentCell.RowIndex
            End Try

            If found = True Then
                dtgrdViewItemList.Item(0, row).Value = barcode
                dtgrdViewItemList.Item(1, row).Value = code
                dtgrdViewItemList.Item(2, row).Value = description
                dtgrdViewItemList.Item(4, row).Value = LCurrency.displayValue(price.ToString)
                dtgrdViewItemList.Item(5, row).Value = LCurrency.displayValue(vat.ToString)
                dtgrdViewItemList.Item(6, row).Value = LCurrency.displayValue(discountRatio.ToString)
                dtgrdViewItemList.Item(7, row).Value = qty
                dtgrdViewItemList.Item(8, row).Value = LCurrency.displayValue(amount.ToString)
                dtgrdViewItemList.Item(10, row).Value = description

                dtgrdViewItemList.Item(0, row).ReadOnly = True
                dtgrdViewItemList.Item(1, row).ReadOnly = True
                dtgrdViewItemList.Item(2, row).ReadOnly = True

                seq = seq + 1
                AddToCart("", Till.TILLNO, dtgrdViewItemList.Item(0, row).Value, dtgrdViewItemList.Item(1, row).Value, dtgrdViewItemList.Item(2, row).Value, dtgrdViewItemList.Item(4, row).Value, dtgrdViewItemList.Item(5, row).Value, dtgrdViewItemList.Item(6, row).Value, dtgrdViewItemList.Item(7, row).Value, dtgrdViewItemList.Item(8, row).Value, dtgrdViewItemList.Item(10, row).Value)

                If dtgrdViewItemList.RowCount > 2 Then
                    If dtgrdViewItemList.Item(7, row - 1).Value > 1 Then
                        SaleSequence.multiple = True
                    Else
                        SaleSequence.multiple = False
                    End If
                End If
            Else
                cart = loadCart(Till.TILLNO)
                displayCart(cart)
            End If
        Catch ex As Exception
            MsgBox(ex.ToString)
            dtgrdViewItemList.Item(0, row).Value = ""
            dtgrdViewItemList.Item(1, row).Value = ""
            dtgrdViewItemList.Item(2, row).Value = ""
            dtgrdViewItemList.Item(4, row).Value = ""
            dtgrdViewItemList.Item(5, row).Value = ""
            dtgrdViewItemList.Item(6, row).Value = ""
            dtgrdViewItemList.Item(7, row).Value = ""
            dtgrdViewItemList.Item(8, row).Value = ""

            dtgrdViewItemList.EndEdit()
            refreshList()
            calculateValues()
        End Try

        dtgrdViewItemList.EndEdit()
        refreshList()
        calculateValues()

        cart = loadCart(Till.TILLNO)
        displayCart(cart)
        Return found
    End Function

    Private Function refreshList()

        Try
            dtgrdViewItemList.EndEdit()
            If voidRow > -1 Then
                'dtgrdViewItemList.Item(9, voidRow).Value = False
                If dtgrdViewItemList.Item(9, voidRow).Value = True Then
                    'dtgrdViewItemList.Item(9, voidRow).Value = False
                Else
                    'dtgrdViewItemList.Item(9, voidRow).Value = True
                End If
            End If
            voidRow = -1
        Catch ex As Exception
            'MsgBox(ex.Message)
        End Try
        Return vbNull
    End Function
    Dim allow As Boolean = False
    Private Function calculateValues()

        Try
            dtgrdViewItemList.EndEdit()
            Dim _total As Double = 0
            Dim _vat As Double = 0
            Dim _discount As Double = 0
            Dim _grandTotal As Double = 0
            Dim rows As Integer = dtgrdViewItemList.RowCount
            For i As Integer = 0 To rows - 2

                Dim price As Double = Val(LCurrency.getValue(dtgrdViewItemList.Item(4, i).Value))
                Dim vat As Double = Val(LCurrency.getValue(dtgrdViewItemList.Item(5, i).Value))
                Dim discountRatio As Double = Val(LCurrency.getValue(dtgrdViewItemList.Item(6, i).Value))
                Dim qty As Double = Val(dtgrdViewItemList.Item(7, i).Value)

                Dim amount As Double = price * qty * (1 - discountRatio / 100)
                dtgrdViewItemList.Item(8, i).Value = LCurrency.displayValue(amount.ToString)


                If dtgrdViewItemList.Item(9, i).Value = False Then
                    _total = _total + Val(LCurrency.getValue(dtgrdViewItemList.Item(8, i).Value.ToString))
                    _vat = _vat + ((Val(LCurrency.getValue(dtgrdViewItemList.Item(5, i).Value.ToString)))) * Val(LCurrency.getValue(dtgrdViewItemList.Item(8, i).Value.ToString) / (100 + Val(LCurrency.getValue(dtgrdViewItemList.Item(5, i).Value.ToString))))


                    Dim discountedPrice As Double = Val(LCurrency.getValue(price)) * (1 - Val(discountRatio) / 100)

                    _discount = _discount + ((price - discountedPrice) * qty)

                    '_discount = _discount + (Val(LCurrency.getValue(dtgrdViewItemList.Item(6, i).Value.ToString)) / 100) * Val(LCurrency.getValue(dtgrdViewItemList.Item(8, i).Value.ToString))
                End If
            Next
            _grandTotal = _total
            txtTotal.Text = LCurrency.displayValue(_total)
            txtDiscount.Text = LCurrency.displayValue(_discount)
            txtVAT.Text = LCurrency.displayValue(_vat)
            txtGrandTotal.Text = LCurrency.displayValue(_grandTotal)
        Catch ex As Exception
            'MsgBox(ex.StackTrace)
        End Try

        Return vbNull
    End Function

    Private Sub Button4_Click(sender As Object, e As EventArgs)
        refreshList()
        calculateValues()
    End Sub
    Dim voidRow As Integer = -1
    Private Sub dtgrdViewItemList_CellContentClick(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdViewItemList.CellContentClick
        Dim row As Integer = -1
        Dim col As Integer = -1
        Try
            row = dtgrdViewItemList.CurrentRow.Index
            col = dtgrdViewItemList.CurrentCell.ColumnIndex
        Catch ex As Exception
            row = -1
            Exit Sub
        End Try
        If dtgrdViewItemList.CurrentCell.ColumnIndex = 9 Then
            Dim sn As String = dtgrdViewItemList.Item(11, row).Value
            If dtgrdViewItemList.Item(9, row).Value = False Then
                _void(sn)
            Else
                unvoid(sn)
            End If
            cart = loadCart(Till.TILLNO)
            displayCart(cart)
        End If
    End Sub
    Dim discountDialog As frmDiscount
    Private Sub dtgrdViewItemList_CellClick1(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdViewItemList.CellClick
        Dim row As Integer = -1
        Dim col As Integer = -1
        Dim amount = 0
        Dim item As String = ""
        Dim unitPrice As String = ""
        Try
            row = dtgrdViewItemList.CurrentRow.Index
            col = dtgrdViewItemList.CurrentCell.ColumnIndex
            amount = Val(LCurrency.getValue(dtgrdViewItemList.Item(4, row).Value.ToString))
            item = "(" + dtgrdViewItemList.Item(1, row).Value.ToString + ")  " + dtgrdViewItemList.Item(2, row).Value.ToString
            unitPrice = dtgrdViewItemList.Item(4, row).Value.ToString

        Catch ex As Exception
            row = -1
        End Try

        dtgrdViewItemList.EndEdit()

        If dtgrdViewItemList.CurrentCell.ColumnIndex = 6 Then
            Dim sn As String = dtgrdViewItemList.Item(11, dtgrdViewItemList.CurrentCell.RowIndex).Value
            cart = loadCart(Till.TILLNO)
            displayCart(cart)

            'process discount

            If User.authorize("DISCOUNT") = True Then

                discountDialog = New frmDiscount()
                discountDialog.lblItem.Text = item
                discountDialog.lblUnitPrice.Text = "Unit Price " + unitPrice
                discountDialog.ShowDialog()
                If discountDialog.DialogResult = Windows.Forms.DialogResult.OK Then
                    Dim discount As String = discountDialog.txtDiscount.Text
                    If Val(discount) <= amount Then

                        Dim discPercentage = (Val(discount) / amount) * 100

                        updateDiscount(sn, discPercentage)

                    Else
                        MsgBox("Invalid Discount Amount. Discount should be less than Unit Price", vbOKOnly + vbCritical, "Invalid Amount")

                    End If
                    discountDialog.Dispose()

                Else
                    discountDialog.Dispose()
                End If
            Else
                MsgBox("Operation denied!", vbOKOnly + vbExclamation)
            End If

            cart = loadCart(Till.TILLNO)
            displayCart(cart)
        End If

        refreshList()
        calculateValues()
    End Sub
    Private Sub updateDiscount(sn As String, discount As Double)
        Cursor.Current = Cursors.WaitCursor
        Dim detail As New CartDetail
        detail.id = sn
        detail.discount = discount
        Web.post(detail, "carts/update_discount")
        Cursor.Current = Cursors.Default
    End Sub

    Dim dialog As frmSearchItem

    Private Sub ToolStripButton6_Click(sender As Object, e As EventArgs)

        Dim control As TextBox = DirectCast(dtgrdViewItemList.EditingControl, TextBox)

        Dim list As New List(Of String)
        Dim mySource As New AutoCompleteStringCollection
        Dim item As New Item
        list = item.getItems(control.Text)
        mySource.AddRange(list.ToArray)
        control.AutoCompleteCustomSource = mySource
        control.AutoCompleteMode = AutoCompleteMode.Suggest
        control.AutoCompleteSource = AutoCompleteSource.CustomSource

    End Sub

    Private RECEIPT_NO0 As Integer = 0
    Private totalTaxReturns As Double = 0

    Private Function isAllVoid()
        Dim allVoid As Boolean = True
        For i As Integer = 0 To dtgrdViewItemList.RowCount - 2
            If dtgrdViewItemList.Item(9, i).Value = False Then
                allVoid = False
            End If
        Next
        Return allVoid
    End Function

    Private Function printReceipt(receipt As Receipt, tender As Double, balance As Double)
        Dim size As Integer = -1
        For i As Integer = 0 To receipt.receiptDetails.Count - 1
            size = size + 1
        Next
        Dim code(size + 1) As String
        Dim descr(size + 1) As String
        Dim qty(size + 1) As String
        Dim price(size + 1) As String
        Dim tax(size + 1) As String
        Dim amount(size + 1) As String
        Dim subTotal As String = txtTotal.Text
        Dim totalVat As String = txtVAT.Text
        Dim total As String = txtGrandTotal.Text
        Dim discount As String = txtDiscount.Text
        Dim count As Integer = 0
        For i As Integer = 0 To receipt.receiptDetails.Count - 1
            code(count) = receipt.receiptDetails(i).code
            descr(count) = receipt.receiptDetails(i).description
            qty(count) = receipt.receiptDetails(i).qty.ToString()
            price(count) = LCurrency.displayValue(receipt.receiptDetails(i).sellingPriceVatIncl.ToString())
            tax(count) = LCurrency.displayValue(receipt.receiptDetails(i).vat.ToString())
            amount(count) = LCurrency.displayValue(receipt.receiptDetails(i).amount.ToString())
            count = count + 1
        Next
        PointOfSale.printReceipt(Till.TILLNO, receipt.no, Day.bussinessDate, Company.TIN.ToString(), Company.VRN.ToString(), code, descr, qty, price, tax, amount, subTotal, totalVat, total, tender.ToString(), balance.ToString())
        Return vbNull
    End Function

    Private Sub btnPay_Click(sender As Object, e As EventArgs) Handles btnPay.Click
        Try
            refreshList()
            calculateValues()
            If dtgrdViewItemList.RowCount > 0 And isAllVoid() = False Then
                frmPayPoint.txtTotal.Text = FormatNumber(txtGrandTotal.Text, 2, , , TriState.True)
                frmPayPoint.ShowDialog(Me)
                If frmPayPoint.DialogResult = Windows.Forms.DialogResult.Cancel Then
                    calculateValues()
                Else
                    If frmPayPoint.paid = True Then
                        Dim receipt As Receipt = Receipt.CURRENT_RECEIPT
                        printReceipt(receipt, frmPayPoint.cash, Convert.ToDouble(LCurrency.getValue(frmPayPoint.balance)))
                        allowVoid = False
                        cart = loadCart(Till.TILLNO)
                        If IsNothing(cart) Then
                            cart = createCart(Till.TILLNO)
                        End If
                        displayCart(cart)
                    End If
                End If
            End If
        Catch ex As Exception

        End Try
        txtGrandTotal.Text = ""
        txtTotal.Text = ""
        txtVAT.Text = ""
        txtDiscount.Text = ""
        refreshList()
        calculateValues()
    End Sub

    Private Function updateQty(qty As Double, sn As String)
        Dim detail As New CartDetail
        detail.id = sn
        detail.qty = qty
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        Cursor.Current = Cursors.WaitCursor
        Try
            response = Web.post(detail, "carts/update_qty")
            Return True
        Catch ex As Exception
            Return False
        End Try
        Cursor.Current = Cursors.Default
    End Function
    Private Sub tpsLock_Click(sender As Object, e As EventArgs) Handles tpsLock.Click
        frmLock.ShowDialog()
    End Sub
    Private Sub frmMain_Shown(sender As Object, e As EventArgs) Handles Me.Shown
        RECEIPT_NO0 = (New Receipt).makeReceipt(Till.TILLNO, Day.bussinessDate)
        ''cart = loadCart(Till.TILLNO)
        ''displayCart(cart)
    End Sub
    Private Sub frmMain_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        tspStatus.Text = tspStatus.Text + "Logged in"
        tspUser.Text = tspUser.Text + User.AALIAS
        tspLogginTime.Text = tspLogginTime.Text + User.LOGIN_TIME
        tpsSystDate.Text = tpsSystDate.Text + Day.bussinessDate

        dtgrdViewItemList.Enabled = True
        btnPay.Enabled = True


        ' If User.authorize("SELLING") Then
        'dtgrdViewItemList.Enabled = True
        'tlsrpItemcode.Enabled = True
        'tlstrDescription.Enabled = True
        'tlstrpBarcode.Enabled = True
        'btnPay.Enabled = True

        ' End If
        Dim product As New Product
        longProductList = product.getDescriptions()

        cart = loadCart(Till.TILLNO)

        If IsNothing(cart) Then
            MsgBox("Check")
            cart = createCart(Till.TILLNO)
        End If
        displayCart(cart)
    End Sub
    Private Function loadUser(role As String)

        Return vbNull
    End Function
    Private Sub FloatToolStripMenuItem_Click(sender As Object, e As EventArgs) Handles FloatToolStripMenuItem.Click

        If User.authorize("FLOAT MANAGEMENT") Then
            frmFloat.ShowDialog()
        Else
            MsgBox("Access denied")
        End If

    End Sub

    Private Sub PickUpToolStripMenuItem_Click(sender As Object, e As EventArgs) Handles PickUpToolStripMenuItem.Click

        If User.authorize("CASH PICK UP") Then
            frmCashPickUp.ShowDialog()
        Else
            MsgBox("Access denied")
        End If

    End Sub

    Private Sub PettyCashToolStripMenuItem_Click(sender As Object, e As EventArgs) Handles PettyCashToolStripMenuItem.Click

        If User.authorize("PETTY CASH MANAGEMENT") Then
            frmPettyCash.ShowDialog()
        Else
            MsgBox("Access denied")
        End If

    End Sub

    Private Sub Button1_Click(sender As Object, e As EventArgs)
        frmNumInput.Text = "Enter Quantity"
        Dim qty As String = ""
        Dim row As Integer = -1
        Dim col As Integer = -1
        Try
            col = dtgrdViewItemList.CurrentCell.ColumnIndex
            row = dtgrdViewItemList.CurrentRow.Index
        Catch ex As Exception
            row = -1
        End Try
        If row < 0 Then
            MsgBox("Select item to change Quantity", vbExclamation + vbOKOnly, "Error: No Selection")
            Return
        End If
        If col <> 7 Then
            MsgBox("Point to the Quantity value to be changed", vbExclamation + vbOKOnly, "Error: No Selection")
            Return
        End If
        refreshList()
        If dtgrdViewItemList.RowCount > 1 Then
            frmNumInput.ShowDialog(Me)
        Else
            Exit Sub
        End If

        If frmNumInput.DialogResult = Windows.Forms.DialogResult.OK Then
            qty = frmNumInput.txtValue.Text
            If IsNumeric(qty.ToString) And Val(qty.ToString) Mod 1 >= 0.0999 And Val(qty) > 0 And row >= 0 Then
                dtgrdViewItemList.Item(7, row).Value = qty.ToString
                refreshList()
                calculateValues()
            Else
                MsgBox("Invalid Quantity. Quantity should be a number", vbExclamation + vbOKOnly, "Error: Invalid Entry")
            End If
        End If
    End Sub

    Private Sub Button2_Click(sender As Object, e As EventArgs)
        frmNumInput.Text = "Enter Discount ratio 0%-100%"
        Dim disc As String = ""
        Dim row As Integer = -1
        Dim col As Integer = -1
        Try
            col = dtgrdViewItemList.CurrentCell.ColumnIndex
            row = dtgrdViewItemList.CurrentRow.Index
        Catch ex As Exception
            row = -1
        End Try
        If row < 0 Then
            MsgBox("Select product to change Discount", vbExclamation + vbOKOnly, "Error: No Selection")
            Return
        End If
        If col <> 6 Then
            MsgBox("Point to the Discount value to be changed", vbExclamation + vbOKOnly, "Error: No Selection")
            Return
        End If
        refreshList()
        If dtgrdViewItemList.RowCount > 1 Then
            frmNumInput.ShowDialog(Me)
        Else
            Exit Sub
        End If

        If frmNumInput.DialogResult = Windows.Forms.DialogResult.OK Then
            disc = frmNumInput.txtValue.Text
            If IsNumeric(disc.ToString) And Val(disc.ToString) >= 0 And Val(disc.ToString) <= 100 And row >= 0 Then
                frmAllow.ShowDialog()
                If frmAllow.allowed = True Then
                    dtgrdViewItemList.Item(6, row).Value = LCurrency.displayValue(disc.ToString)
                    refreshList()
                    calculateValues()
                Else
                    Return
                End If
            Else
                MsgBox("Invalid Value. Discount ratio should be in percentage value between 0 and 100", vbExclamation + vbOKOnly, "Error: Invalid Entry")
            End If
        End If
    End Sub

    Private Sub Button3_Click(sender As Object, e As EventArgs)
        frmNumInput.Text = "Enter Price"
        Dim price As String = ""
        Dim row As Integer = -1
        Dim col As Integer = -1
        Try
            row = dtgrdViewItemList.CurrentRow.Index
            col = dtgrdViewItemList.CurrentCell.ColumnIndex
        Catch ex As Exception
            row = -1
        End Try
        If row < 0 Then
            MsgBox("Select item to change Price", vbExclamation + vbOKOnly, "Error: No Selection")
            Return
        End If
        If col <> 4 Then
            MsgBox("Point to the Price value to be changed", vbExclamation + vbOKOnly, "Error: No Selection")
            Return
        End If
        refreshList()
        If dtgrdViewItemList.RowCount > 1 Then
            frmNumInput.ShowDialog(Me)
        Else
            Exit Sub
        End If
        If frmNumInput.DialogResult = Windows.Forms.DialogResult.OK Then
            price = frmNumInput.txtValue.Text
            If IsNumeric(price.ToString) And row >= 0 Then
                frmAllow.ShowDialog()
                If frmAllow.allowed = True Then
                    dtgrdViewItemList.Item(4, row).Value = LCurrency.displayValue(price.ToString)
                    refreshList()
                    calculateValues()
                Else
                    Return
                End If
            Else
                MsgBox("Invalid Value. Price should be a number ie.200, 1000, 3500.00 etc", vbExclamation + vbOKOnly, "Error: Invalid Entry")
            End If
        End If
    End Sub

    Private Sub XReportToolStripMenuItem_Click(sender As Object, e As EventArgs) Handles XReportToolStripMenuItem.Click
        frmXReport.ShowDialog()
    End Sub

    Private Sub ZReportToolStripMenuItem_Click(sender As Object, e As EventArgs) Handles ZReportToolStripMenuItem.Click
        frmZReport.ShowDialog()
    End Sub

    Private Sub ToolStripButton7_Click(sender As Object, e As EventArgs) Handles ToolStripButton7.Click
        openCashDrawer()
    End Sub

    Private Sub SetupToolStripMenuItem_Click(sender As Object, e As EventArgs)
        frmFiscalPrinter.ShowDialog()
    End Sub

    Private Sub PrinterToolStripMenuItem_Click(sender As Object, e As EventArgs) Handles PrinterToolStripMenuItem.Click
        frmPrinters.ShowDialog()
    End Sub

    Protected Overridable Function place(key As String)
        Try
            Dim row As Integer = dtgrdViewItemList.CurrentCell.RowIndex
            Dim col As Integer = dtgrdViewItemList.CurrentCell.ColumnIndex
            If dtgrdViewItemList.CurrentCell.ColumnIndex = 0 Or dtgrdViewItemList.CurrentCell.ColumnIndex = 1 Or dtgrdViewItemList.CurrentCell.ColumnIndex = 2 Or dtgrdViewItemList.CurrentCell.ColumnIndex = 7 Then

                dtgrdViewItemList.Select()
                dtgrdViewItemList.CurrentCell = dtgrdViewItemList.Item(col, row)
                'Dim control As TextBox = DirectCast(dtgrdViewItemList.EditingControl, TextBox)
                Dim control As TextBox = DirectCast(dtgrdViewItemList.EditingControl, TextBox)
                control.SelectionStart = dtgrdViewItemList.CurrentCell.Value.ToString.Length
                control.SelectionLength = 0
                SendKeys.Send(key)
            Else
                dtgrdViewItemList.Select()
                dtgrdViewItemList.CurrentCell = dtgrdViewItemList.Item(col, row)
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



    Private Sub btn1_Click(sender As Object, e As EventArgs) Handles btn1.Click
        place("1")
    End Sub

    Private Sub btn2_Click(sender As Object, e As EventArgs) Handles btn2.Click
        place("2")
    End Sub

    Private Sub btn3_Click(sender As Object, e As EventArgs) Handles btn3.Click
        place("3")
    End Sub

    Private Sub btn4_Click(sender As Object, e As EventArgs) Handles btn4.Click
        place("4")
    End Sub

    Private Sub btn5_Click(sender As Object, e As EventArgs) Handles btn5.Click
        place("5")
    End Sub

    Private Sub btn6_Click(sender As Object, e As EventArgs) Handles btn6.Click
        place("6")
    End Sub

    Private Sub btn7_Click(sender As Object, e As EventArgs) Handles btn7.Click
        place("7")
    End Sub

    Private Sub btn8_Click(sender As Object, e As EventArgs) Handles btn8.Click
        place("8")
    End Sub

    Private Sub btn9_Click(sender As Object, e As EventArgs) Handles btn9.Click
        place("9")
    End Sub

    Private Sub btn0_Click(sender As Object, e As EventArgs) Handles btn0.Click
        place("0")
    End Sub

    Private Sub btnDot_Click(sender As Object, e As EventArgs) Handles btnDot.Click
        place(".")
    End Sub

    Private Sub btnClear_Click(sender As Object, e As EventArgs) Handles btnClear.Click
        place("{BACKSPACE}")
    End Sub

    Private Sub btnEnter_Click(sender As Object, e As EventArgs) Handles btnEnter.Click
        place("~") 'the enter key  ENTER for numeric keypad
    End Sub

    Private Sub btnUp_Click(sender As Object, e As EventArgs) Handles btnUp.Click
        place("{UP}")
    End Sub

    Private Sub btnLeft_Click(sender As Object, e As EventArgs) Handles btnLeft.Click
        place("{LEFT}")
    End Sub

    Private Sub btnRight_Click(sender As Object, e As EventArgs) Handles btnRight.Click
        place("{RIGHT}")
    End Sub

    Private Sub btnDown_Click(sender As Object, e As EventArgs) Handles btnDown.Click
        place("{DOWN}")
    End Sub


    Dim _listBox As ListBox
    Dim _isAdded As Boolean
    Dim _values As String()
    Dim _formerValue As String = String.Empty

    Private Sub Button43_Click(sender As Object, e As EventArgs) Handles Button43.Click
        pnlKeyBoard.Visible = False
    End Sub

    Private Sub btnCaps_Click(sender As Object, e As EventArgs) Handles btnCaps.Click
        If btnCaps.Text = "CAPS-ON" Then
            btnCaps.Text = "CAPS-OFF"
        ElseIf btnCaps.Text = "CAPS-OFF" Then
            btnCaps.Text = "CAPS-ON"
        Else
            btnCaps.Text = "CAPS-OFF"
        End If
    End Sub
    Private Sub placeAlpha(key As String)
        If btnCaps.Text = "CAPS-ON" Then
            place(key.ToUpper)
        Else
            place(key.ToLower)
        End If
    End Sub
    Private Sub btnQ_Click(sender As Object, e As EventArgs) Handles btnQ.Click
        placeAlpha("Q")
    End Sub

    Private Sub btnW_Click(sender As Object, e As EventArgs) Handles btnW.Click
        placeAlpha("W")
    End Sub

    Private Sub btnE_Click(sender As Object, e As EventArgs) Handles btnE.Click
        placeAlpha("E")
    End Sub

    Private Sub btnR_Click(sender As Object, e As EventArgs) Handles btnR.Click
        placeAlpha("R")
    End Sub

    Private Sub btnT_Click(sender As Object, e As EventArgs) Handles btnT.Click
        placeAlpha("T")
    End Sub

    Private Sub btnY_Click(sender As Object, e As EventArgs) Handles btnY.Click
        placeAlpha("Y")
    End Sub

    Private Sub btnU_Click(sender As Object, e As EventArgs) Handles btnU.Click
        placeAlpha("U")
    End Sub

    Private Sub btnI_Click(sender As Object, e As EventArgs) Handles btnI.Click
        placeAlpha("I")
    End Sub

    Private Sub btnO_Click(sender As Object, e As EventArgs) Handles btnO.Click
        placeAlpha("O")
    End Sub

    Private Sub btnP_Click(sender As Object, e As EventArgs) Handles btnP.Click
        placeAlpha("P")
    End Sub

    Private Sub btnA_Click(sender As Object, e As EventArgs) Handles btnA.Click
        placeAlpha("A")
    End Sub

    Private Sub btnS_Click(sender As Object, e As EventArgs) Handles btnS.Click
        placeAlpha("S")
    End Sub

    Private Sub btnD_Click(sender As Object, e As EventArgs) Handles btnD.Click
        placeAlpha("D")
    End Sub

    Private Sub btnF_Click(sender As Object, e As EventArgs) Handles btnF.Click
        placeAlpha("F")
    End Sub

    Private Sub btnG_Click(sender As Object, e As EventArgs) Handles btnG.Click
        placeAlpha("G")
    End Sub

    Private Sub btnH_Click(sender As Object, e As EventArgs) Handles btnH.Click
        placeAlpha("H")
    End Sub

    Private Sub btnJ_Click(sender As Object, e As EventArgs) Handles btnJ.Click
        placeAlpha("J")
    End Sub

    Private Sub btnK_Click(sender As Object, e As EventArgs) Handles btnK.Click
        placeAlpha("K")
    End Sub

    Private Sub btnL_Click(sender As Object, e As EventArgs) Handles btnL.Click
        placeAlpha("L")
    End Sub

    Private Sub btnZ_Click(sender As Object, e As EventArgs) Handles btnZ.Click
        placeAlpha("Z")
    End Sub

    Private Sub btnX_Click(sender As Object, e As EventArgs) Handles btnX.Click
        placeAlpha("X")
    End Sub

    Private Sub btnC_Click(sender As Object, e As EventArgs) Handles btnC.Click
        placeAlpha("C")
    End Sub

    Private Sub btnV_Click(sender As Object, e As EventArgs) Handles btnV.Click
        placeAlpha("V")
    End Sub

    Private Sub btnB_Click(sender As Object, e As EventArgs) Handles btnB.Click
        placeAlpha("B")
    End Sub

    Private Sub btnN_Click(sender As Object, e As EventArgs) Handles btnN.Click
        placeAlpha("N")
    End Sub

    Private Sub btnM_Click(sender As Object, e As EventArgs) Handles btnM.Click
        placeAlpha("M")
    End Sub

    Private Sub btnPoint_Click(sender As Object, e As EventArgs) Handles btnPoint.Click
        placeAlpha(".")
    End Sub

    Private Sub btnSpace_Click(sender As Object, e As EventArgs) Handles btnSpace.Click
        placeAlpha(" ")
    End Sub

    Private Sub btnReturn_Click(sender As Object, e As EventArgs) Handles btnReturn.Click
        place("~")
    End Sub

    Private Sub btnCancel_Click(sender As Object, e As EventArgs) Handles btnCancel.Click
        place("{BACKSPACE}")
    End Sub

    Private Sub Button1_Click_1(sender As Object, e As EventArgs) Handles Button1.Click
        place("{UP}")
    End Sub

    Private Sub Button2_Click_1(sender As Object, e As EventArgs) Handles Button2.Click
        place("{LEFT}")
    End Sub

    Private Sub Button3_Click_1(sender As Object, e As EventArgs) Handles Button3.Click
        place("{RIGHT}")
    End Sub

    Private Sub Button4_Click_1(sender As Object, e As EventArgs) Handles Button4.Click
        place("{DOWN}")
    End Sub

    Private Sub btnOne_Click(sender As Object, e As EventArgs) Handles btnOne.Click
        place("1")
    End Sub

    Private Sub btnTwo_Click(sender As Object, e As EventArgs) Handles btnTwo.Click
        place("2")
    End Sub

    Private Sub btnThree_Click(sender As Object, e As EventArgs) Handles btnThree.Click
        place("3")
    End Sub

    Private Sub btnFour_Click(sender As Object, e As EventArgs) Handles btnFour.Click
        place("4")
    End Sub

    Private Sub btnFive_Click(sender As Object, e As EventArgs) Handles btnFive.Click
        place("5")
    End Sub

    Private Sub btnSix_Click(sender As Object, e As EventArgs) Handles btnSix.Click
        place("6")
    End Sub

    Private Sub btnSeven_Click(sender As Object, e As EventArgs) Handles btnSeven.Click
        place("7")
    End Sub

    Private Sub btnEight_Click(sender As Object, e As EventArgs) Handles btnEight.Click
        place("8")
    End Sub

    Private Sub btnNine_Click(sender As Object, e As EventArgs) Handles btnNine.Click
        place("9")
    End Sub

    Private Sub btnZero_Click(sender As Object, e As EventArgs) Handles btnZero.Click
        place("0")
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
    Private Sub btnKeyboard_Click(sender As Object, e As EventArgs) Handles btnKeyboard.Click
        startOSK()
    End Sub

    Private Sub AddToCart(sn As String, tillNo As String, barcode As String, code As String, description As String, sellingPriceVatIncl As Double, vat As Double, discount As Double, qty As Double, amount As Double, shortDescr As String)

        Dim cart As New Cart
        cart.no = Cart.NO_
        cart.till.no = Till.TILLNO
        Dim cartDetail = New CartDetail
        cartDetail.cart = cart
        cartDetail.barcode = barcode
        cartDetail.code = code
        cartDetail.description = description
        cartDetail.sellingPriceVatIncl = sellingPriceVatIncl
        cartDetail.discount = discount
        cartDetail.vat = vat
        cartDetail.qty = qty
        cartDetail.amount = amount

        Dim response As New Object
        Dim json As New JObject
        Try
            Cursor.Current = Cursors.WaitCursor
            Web.post(cartDetail, "carts/add_detail")
            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            MsgBox("Could not add product", "Error: Process failed", vbOKOnly)
        End Try
        cart = loadCart(Till.TILLNO)
        displayCart(cart)
        Cursor.Current = Cursors.Default
    End Sub

    Private Sub _void(id As String)
        Dim cartDetail As CartDetail = New CartDetail()
        cartDetail.id = id
        Dim response As New Object()
        Dim json As New JObject()
        Cursor.Current = Cursors.WaitCursor
        response = Web.post(cartDetail, "carts/void")
        Cursor.Current = Cursors.Default
    End Sub

    Private Sub unvoid(id As String)
        Dim cartDetail As CartDetail = New CartDetail()
        cartDetail.id = id
        Dim response As New Object()
        Dim json As New JObject()
        Cursor.Current = Cursors.WaitCursor
        response = Web.post(cartDetail, "carts/unvoid")
        Cursor.Current = Cursors.Default
    End Sub

    Private Function loadCart(tillNo As String) As Cart
        dtgrdViewItemList.Rows.Clear()
        Dim response As New Object
        Dim json As New JObject
        Cursor.Current = Cursors.WaitCursor
        Try
            response = Web.get_("carts/load?till_no=" + tillNo)
            json = JObject.Parse(response.ToString)
            Cart.NO_ = json.SelectToken("no").ToString()
            cart = JsonConvert.DeserializeObject(Of Cart)(json.ToString)
            Cursor.Current = Cursors.Default
            Return cart
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            Return Nothing
        End Try
        Cursor.Current = Cursors.Default
    End Function

    Private Function createCart(tillNo As String) As Cart
        Dim response As New Object
        Dim json As New JObject
        Try
            Cursor.Current = Cursors.WaitCursor
            response = Web.get_("carts/create?till_no=" + tillNo)
            json = JObject.Parse(response.ToString())
            Cart.NO_ = json.SelectToken("no").ToString()
            cart = JsonConvert.DeserializeObject(Of Cart)(json.ToString())
            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            MsgBox("Could not create workspace, Application will close")
            Application.Exit()
            Return New Cart
        End Try
        Cursor.Current = Cursors.Default
        Return cart
    End Function

    Private Function displayCart(cart As Cart)
        dtgrdViewItemList.Rows.Clear()

        If Not IsNothing(cart.cartDetails) Then
            Dim i As Integer = 0
            For Each detail As CartDetail In cart.cartDetails
                Dim dtgrdRow As New DataGridViewRow
                Dim dtgrdCell As DataGridViewCell

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.barcode
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.code
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.description
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = ""
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = LCurrency.displayValue(detail.sellingPriceVatIncl)
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.vat
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.discount
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.qty
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = amount
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewCheckBoxCell()
                If detail.voided = True Then
                    dtgrdCell.Value = True
                Else
                    dtgrdCell.Value = False
                End If
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = ""
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.id
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdViewItemList.Rows.Add(dtgrdRow)

                dtgrdViewItemList.Item(0, i).ReadOnly = True
                dtgrdViewItemList.Item(1, i).ReadOnly = True
                dtgrdViewItemList.Item(2, i).ReadOnly = True

                i = i + 1
            Next
            refreshList()
            calculateValues()
            dtgrdViewItemList.CurrentCell = dtgrdViewItemList(0, dtgrdViewItemList.RowCount - 1)
        End If

        Return vbNull
    End Function

    Dim order As frmOrder

    Dim longProductList As List(Of String)
    Dim shortProductList As List(Of String) = New List(Of String)
    Dim control As TextBox
    Dim c As Integer = -1
    Dim r As Integer = -1
    Private Sub dtgrdViewItemList_CellValueChanged(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdViewItemList.CellEnter, dtgrdViewItemList.CellClick
        Dim rowHeight As Integer = dtgrdViewItemList.RowTemplate.Height
        Dim x As Integer = 340
        Dim y As Integer = 90 + (dtgrdViewItemList.RowCount - 1) * rowHeight
        If y > dtgrdViewItemList.Size().Height + 90 Then
            y = dtgrdViewItemList.Size.Height + 25
        End If
        cmbProducts.SetBounds(x, y, 300, rowHeight)
        If dtgrdViewItemList.CurrentCell.ColumnIndex = 2 Then
            r = dtgrdViewItemList.CurrentCell.RowIndex
            c = 2
            shortProductList.Clear()
            If dtgrdViewItemList.Item(c, r).Value = "" And r = dtgrdViewItemList.RowCount - 1 Then
                cmbProducts.Visible = True
                cmbProducts.Focus()
            Else
                cmbProducts.Visible = False
                cmbProducts.Items.Clear()
                c = -1
                r = -1
            End If
        Else
            cmbProducts.Visible = False
            cmbProducts.Items.Clear()
            c = -1
            r = -1
        End If
    End Sub
    Private Sub cmbDescription_KeyUp(sender As Object, e As EventArgs) Handles cmbProducts.KeyUp
        If Not c = 2 Then
            Exit Sub
        End If
        Dim currentText As String = cmbProducts.Text
        shortProductList.Clear()
        cmbProducts.Items.Clear()
        cmbProducts.Items.Add(currentText)
        cmbProducts.DroppedDown = True
        For Each text As String In longProductList
            Dim formattedText As String = text.ToUpper()
            If formattedText.Contains(cmbProducts.Text.ToUpper()) Then
                shortProductList.Add(text)
            End If
        Next
        cmbProducts.Items.AddRange(shortProductList.ToArray())
        cmbProducts.SelectionStart = cmbProducts.Text.Length
        Cursor.Current = Cursors.Default
    End Sub
    Private Sub cmbProducts_SelectedIndexChanged(sender As Object, e As EventArgs) Handles cmbProducts.SelectedValueChanged
        Try
            Dim value As String = cmbProducts.Text
            dtgrdViewItemList.Item(c, r).Value = value
            cmbProducts.Visible = False
            searchByDescription(value, 1)
        Catch ex As Exception

        End Try
    End Sub
End Class
