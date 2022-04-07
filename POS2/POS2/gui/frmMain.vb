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
                calculateValues()
            End If

        End If
        Try
            If dtgrdViewItemList.CurrentCell.ColumnIndex = 2 Then
                'search() 'do further research
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
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
    Private Function searchByBarcode(barcode As String, q As Integer) As Boolean
        Return search(barcode, "", "", q)
    End Function
    Private Function searchByCode(code As String, q As Integer) As Boolean
        Return search("", code, "", q)
    End Function
    Private Function searchByDescription(description As String, q As Integer) As Boolean
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

            barcode = product.primaryBarcode
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

            If barcode = "" Then
                found = False
                loadCart(txtId.Text, Till.TILLNO)
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

                If dtgrdViewItemList.RowCount > 1 Then
                    If dtgrdViewItemList.Item(7, row - 1).Value > 1 Then
                        SaleSequence.multiple = True
                    Else
                        SaleSequence.multiple = False
                    End If
                End If
            Else
                loadCart(txtId.Text, Till.TILLNO)
            End If
        Catch ex As Exception
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

        loadCart(txtId.Text, Till.TILLNO)
        Return found
    End Function

    Private Function refreshList()
        If dtgrdViewItemList.RowCount > 0 Then
            Dim max As Integer = dtgrdViewItemList.RowCount - 2
            For i As Integer = max To 0 Step -1
                If dtgrdViewItemList.Item(1, i).Value = "" Or Val(dtgrdViewItemList.Item(7, i).Value) <= 0 Then
                    ' dtgrdViewItemList.Rows.RemoveAt(i)
                End If
            Next
        End If
        Try
            dtgrdViewItemList.EndEdit()
            If voidRow > -1 Then
                dtgrdViewItemList.Item(9, voidRow).Value = False
                If dtgrdViewItemList.Item(9, voidRow).Value = True Then
                    dtgrdViewItemList.Item(9, voidRow).Value = False
                Else
                    dtgrdViewItemList.Item(9, voidRow).Value = True
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
                Dim qty As Integer = Val(dtgrdViewItemList.Item(7, i).Value)

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

    Private Sub Button4_Click(sender As Object, e As EventArgs) Handles btnRefresh.Click
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
        End Try

        dtgrdViewItemList.EndEdit()

        If dtgrdViewItemList.CurrentCell.ColumnIndex = 9 Then
            Dim sn As String = dtgrdViewItemList.Item(11, dtgrdViewItemList.CurrentCell.RowIndex).Value
            loadCart(txtId.Text, Till.TILLNO)
            Dim void As Boolean = checkVoid(Till.TILLNO, sn)
            If allowVoid = False Then


                If User.authorize("VOID") Then
                    allowVoid = True
                Else
                    frmAllow.ShowDialog()
                    If frmAllow.allowed = True Then
                        allowVoid = True
                    End If
                End If

            End If

            If allowVoid = True Then
                If void = True Then
                    unvoid(Till.TILLNO, sn)
                Else
                    _void(Till.TILLNO, sn)
                End If
            End If
            loadCart(txtId.Text, Till.TILLNO)
        End If

        refreshList()
        calculateValues()
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
            loadCart(txtId.Text, Till.TILLNO)

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

                        updateDiscount(Till.TILLNO, sn, discPercentage)

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

            loadCart(txtId.Text, Till.TILLNO)
        End If

        refreshList()
        calculateValues()
    End Sub
    Private Sub updateDiscount(tillNo As String, sn As String, discRatio As Double)
        Dim detail As CartDetail = New CartDetail
        detail.id = sn
        detail.discountRatio = discRatio
        Web.put(detail, "carts/update_discount?detail_id=" + sn + "&discount_ratio=" + discRatio.ToString)
        '''''''''''
    End Sub

    Dim dialog As frmSearchItem

    Private Sub ToolStripButton6_Click(sender As Object, e As EventArgs) Handles tlstrDescription.Click

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

    Private Function printReceipt(tillNo As String, receiptNo As String, date_ As String, TIN As String, VRN As String, cash As String, balance As String)
        Dim printed As Boolean = False
        Dim size As Integer = -1
        For i As Integer = 0 To dtgrdViewItemList.RowCount - 2
            If dtgrdViewItemList.Item(9, i).Value = False Then
                size = size + 1
            End If
        Next
        Dim itemCode(size) As String
        Dim descr(size) As String
        Dim qty(size) As String
        Dim price(size) As String
        Dim tax(size) As String
        Dim amount(size) As String
        Dim subTotal As String = txtTotal.Text
        Dim totalVat As String = txtVAT.Text
        Dim total As String = txtGrandTotal.Text
        Dim discountRatio As String = txtDiscount.Text
        Dim count As Integer = 0

        For i As Integer = 0 To dtgrdViewItemList.RowCount - 2
            If dtgrdViewItemList.Item(9, i).Value = False Then
                itemCode(count) = dtgrdViewItemList.Item(1, i).Value
                'descr(count) = dtgrdViewItemList.Item(10, i).Value
                descr(count) = (New Item).getShortDescription(itemCode(count))
                qty(count) = dtgrdViewItemList.Item(7, i).Value
                price(count) = dtgrdViewItemList.Item(4, i).Value
                tax(count) = dtgrdViewItemList.Item(5, i).Value
                amount(count) = dtgrdViewItemList.Item(8, i).Value
                count = count + 1
            End If
        Next
        If PointOfSale.printReceipt(tillNo, receiptNo, date_, TIN, VRN, itemCode, descr, qty, price, tax, amount, subTotal, totalVat, total, cash, balance) = True Then
            printed = True
        End If
        Return printed
    End Function
    Dim saleId As String = ""
    Private Sub btnPay_Click(sender As Object, e As EventArgs) Handles btnPay.Click
        refreshList()
        calculateValues()
        If dtgrdViewItemList.RowCount > 0 And isAllVoid() = False Then
            frmPayPoint.txtTotal.Text = FormatNumber(txtGrandTotal.Text, 2, , , TriState.True)
            frmPayPoint.ShowDialog(Me)
            If frmPayPoint.DialogResult = Windows.Forms.DialogResult.Cancel Then
                calculateValues()
            Else

                Dim receipt As Receipt = New Receipt
                receipt.cash = frmPayPoint.cashReceived
                receipt.voucher = frmPayPoint.voucher
                receipt.deposit = frmPayPoint.deposit
                receipt.loyalty = frmPayPoint.loyalty
                receipt.crCard = frmPayPoint.CRCard
                receipt.cheque = frmPayPoint.cheque
                receipt.cap = frmPayPoint.CAP
                receipt.invoice = frmPayPoint.invoice
                receipt.crNote = frmPayPoint.CRNote
                receipt.mobile = frmPayPoint.mobile
                receipt.other = frmPayPoint.other

                Dim cashReceived As String = frmPayPoint.cashReceived
                Dim balance As String = frmPayPoint.balance

                receipt.no = "NA"

                receipt.till.no = Till.TILLNO
                receipt.issueDate = Day.systemDate

                receipt.cart.id = txtId.Text

                Dim response As Object = New Object
                Dim json As JObject = New JObject
                response = Web.post(receipt, "receipts/new")

                Dim receiptToPrint = JsonConvert.DeserializeObject(Of Receipt)(response.ToString)

                Dim tillNo As String = receiptToPrint.till.no
                Dim date_ As String = Day.systemDate
                Dim receiptNo As String = receiptToPrint.no
                Dim TIN As String = Company.TIN
                Dim VRN As String = Company.VRN

                If printReceipt(tillNo, receiptNo, date_, TIN, VRN, cashReceived, balance) = True Then
                    ''
                Else
                    MsgBox("Payment canceled", vbOKOnly + vbInformation, "Canceled")
                    Exit Sub
                End If
                loadCart(txtId.Text, Till.TILLNO)
                calculateValues()
                allowVoid = False
                If dtgrdViewItemList.RowCount = 1 Then
                    txtId.Text = ""
                End If
            End If
        End If
    End Sub

    Private Function updateQty(qty As Double, sn As String)
        Dim detail As CartDetail = New CartDetail
        detail.id = sn
        detail.qty = qty
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        response = Web.put(detail, "carts/update_qty?detail_id=" + sn + "&qty=" + qty.ToString)
        If response = True Then
            Return True
        Else
            Return False
        End If
    End Function
    Private Sub tpsLock_Click(sender As Object, e As EventArgs) Handles tpsLock.Click
        frmLock.ShowDialog()
    End Sub
    Private Sub frmMain_Shown(sender As Object, e As EventArgs) Handles Me.Shown
        RECEIPT_NO0 = (New Receipt).makeReceipt(Till.TILLNO, Day.systemDate)
        loadCart(txtId.Text, Till.TILLNO)
    End Sub
    Private Sub frmMain_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        tspStatus.Text = tspStatus.Text + "Logged in"
        tspUser.Text = tspUser.Text + User.FIRST_NAME + " " + User.LAST_NAME
        tspLogginTime.Text = tspLogginTime.Text + User.LOGIN_TIME
        tpsSystDate.Text = tpsSystDate.Text + Day.systemDate
        If User.authorize("SELLING") Then
            dtgrdViewItemList.Enabled = True
            tlsrpItemcode.Enabled = True
            tlstrDescription.Enabled = True
            tlstrpBarcode.Enabled = True
            btnPay.Enabled = True

        End If
        Dim item As New Item
        '  longList = item.getItemDescriptions()
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

    Private Sub Button1_Click(sender As Object, e As EventArgs) Handles btnQuantity.Click
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
                calculateValues()
            Else
                MsgBox("Invalid Quantity. Quantity should be a number", vbExclamation + vbOKOnly, "Error: Invalid Entry")
            End If
        End If
    End Sub

    Private Sub Button2_Click(sender As Object, e As EventArgs) Handles btnDiscount.Click
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
                    calculateValues()
                Else
                    Return
                End If
            Else
                MsgBox("Invalid Value. Discount ratio should be in percentage value between 0 and 100", vbExclamation + vbOKOnly, "Error: Invalid Entry")
            End If
        End If
    End Sub

    Private Sub Button3_Click(sender As Object, e As EventArgs) Handles btnPrice.Click
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
                    calculateValues()
                Else
                    Return
                End If
            Else
                MsgBox("Invalid Value. Price should be a number ie.200, 1000, 3500.00 etc", vbExclamation + vbOKOnly, "Error: Invalid Entry")
            End If
        End If
    End Sub

    Private Sub Button5_Click(sender As Object, e As EventArgs) Handles Button5.Click
        frmAllow.ShowDialog()
        If frmAllow.allowed = True Then
            Try
                dtgrdViewItemList.Rows.RemoveAt(dtgrdViewItemList.CurrentRow.Index)
            Catch ex As Exception
                MsgBox("No item selected", vbOKOnly + vbExclamation, "Error: No selection")
            End Try
            calculateValues()
        Else
            Return
        End If

    End Sub

    Private Sub Button6_Click(sender As Object, e As EventArgs) Handles Button6.Click
        frmAllow.ShowDialog()
        If frmAllow.allowed = True Then
            If dtgrdViewItemList.RowCount < 2 Then
                MsgBox("List empty!", vbOKOnly + vbExclamation, "Error: List empty")
                Exit Sub
            End If
            Try
                Dim res As Integer = MsgBox("Clear item list? This can not be undone. Proceed any way?", vbYesNo + vbQuestion, "Clear List")
                If res = 6 Then
                    dtgrdViewItemList.Rows.Clear()
                    calculateValues()
                End If
            Catch ex As Exception
                '
            End Try
        Else
            Return
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

    Private Sub dtgrdViewItemList_CellClick(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdViewItemList.CellClick
        Try
            If dtgrdViewItemList.CurrentCell.ColumnIndex = 2 Then
                Dim control As New TextBox

                control = DirectCast(dtgrdViewItemList.EditingControl, TextBox)
                Dim list As New List(Of String)
                Dim mySource As New AutoCompleteStringCollection
                Dim product_ As New Product
                list = product_.getDescriptions

                mySource.AddRange(list.ToArray)
                control.AutoCompleteCustomSource = mySource
                control.AutoCompleteMode = AutoCompleteMode.Suggest
                control.AutoCompleteSource = AutoCompleteSource.CustomSource

            End If
        Catch ex As Exception
            MsgBox(ex.StackTrace)
        End Try
    End Sub
    Dim longList As New List(Of String)
    Dim shortList As New List(Of String)


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
    Private Sub ToolStripButton8_Click(sender As Object, e As EventArgs) Handles ToolStripButton8.Click

    End Sub
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

    Private Sub AddToCart(sn As String, tillNo As String, barcode As String, code As String, description As String, sellingPriceVatIncl As Double, vat As Double, discountRatio As Double, qty As Double, amount As Double, shortDescr As String)
        Dim cart As New Cart
        cart.id = txtId.Text
        cart.till.no = tillNo
        Dim cartDetail As CartDetail = New CartDetail
        cartDetail.id = sn
        cartDetail.barcode = barcode
        cartDetail.code = code
        cartDetail.description = description
        cartDetail.sellingPriceVatIncl = sellingPriceVatIncl
        cartDetail.vat = vat
        cartDetail.discountRatio = discountRatio
        cartDetail.qty = qty
        cart.cartDetails.Add(cartDetail)

        Dim response As Object = New Object
        Dim json As JObject = New JObject
        If txtId.Text = "" Then
            response = Web.post(cart, "carts/new")
        Else
            response = Web.put(cart, "carts/update_by_id?id=" + txtId.Text)
        End If
        json = JObject.Parse(response)
        If txtId.Text = "" Then
            txtId.Text = json.SelectToken("id")
        End If
    End Sub
    Private Function checkVoid(tillNo As String, sn As String)
        Dim response As Object = New Object
        Dim json As JObject = New JObject
        response = Web.get_("carts/check_voided?detail_id=" + sn)
        If response = True Then
            Return True
        Else
            Return False
        End If
    End Function
    Private Sub _void(tillNo As String, id As String)
        Dim cartDetail As New CartDetail
        cartDetail.id = id
        cartDetail.cart.id = txtId.Text

        Dim response As Object = New Object
        Dim json As JObject = New JObject
        response = Web.put(cartDetail, "carts/void?detail_id=" + id)
        loadCart(txtId.Text, Till.TILLNO)

    End Sub
    Private Sub unvoid(tillNo As String, id As String)
        Dim cartDetail As New CartDetail
        cartDetail.id = id
        cartDetail.cart.id = txtId.Text

        Dim response As Object = New Object
        Dim json As JObject = New JObject
        response = Web.put(cartDetail, "carts/unvoid?detail_id=" + id)
        loadCart(txtId.Text, Till.TILLNO)
    End Sub
    Private Sub loadCart(id As String, tillNo As String)
        dtgrdViewItemList.Rows.Clear()

        Dim response As Object = New Object
        Dim json As JObject = New JObject
        Try
            response = Web.get_("carts/get_by_id_and_till_no?id=" + id + "&till_no=" + tillNo)
            json = JObject.Parse(response)
        Catch ex As Exception
            Exit Sub
        End Try

        Dim cart As Cart = JsonConvert.DeserializeObject(Of Cart)(json.ToString)

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
                dtgrdCell.Value = detail.discountRatio
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = detail.qty
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = amount
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewCheckBoxCell()
                If detail.voided = 1 Then
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
    End Sub

    Dim order As frmOrder
    Private Sub ToolStripButton1_Click(sender As Object, e As EventArgs) Handles ToolStripButton1.Click
        Try
            order = New frmOrder()
            order.ShowDialog(Me)
            If order.DialogResult = Windows.Forms.DialogResult.OK Then
                Dim size As Integer = frmOrder.length
                Dim bcodes(size) As String
                Dim icodes(size) As String
                Dim qtys(size) As Integer
                For i As Integer = 0 To size - 1
                    bcodes(i) = frmOrder.barcodes(i)
                    icodes(i) = frmOrder.itemcodes(i)
                    qtys(i) = frmOrder.quantitys(i)
                    If (bcodes(i)) <> "" Then
                        searchByBarcode(bcodes(i), qtys(i))
                    Else
                        searchByCode(icodes(i), qtys(i))
                    End If
                Next
                order.Dispose()
            Else
                order.Dispose()
            End If

        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
        If frmOrder.showAgain = True Then
            order = New frmOrder()
            order.ShowDialog(Me)
        End If
    End Sub
End Class
