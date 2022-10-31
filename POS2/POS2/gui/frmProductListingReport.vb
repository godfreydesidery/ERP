Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class frmProductListingReport

    Dim report As List(Of ProductListingReport)
    Private Sub frmProductListingReport_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        lblHead.Text = "Product Listing Report for Till No " + Till.TILLNO
    End Sub

    Private Function runReport(startDate As Date, endDate As Date, tillNo As String) As List(Of ProductListingReport)
        report = New List(Of ProductListingReport)
        Dim response As New Object()
        Dim json As New JObject()
        Cursor.Current = Cursors.WaitCursor
        Try
            Dim args As ProductListingReportArgs = New ProductListingReportArgs
            args.from = startDate
            args.to = endDate
            args.tillNo = Till.TILLNO
            response = Web.post(args, "reports/product_listing_report")

            For Each item As JObject In JArray.Parse(response)
                Dim detail As ProductListingReport = New ProductListingReport
                detail.date = Date.Parse(item.SelectToken("date")(0).ToString() + "-" + item.SelectToken("date")(1).ToString() + "-" + item.SelectToken("date")(2).ToString())
                detail.barcode = item.SelectToken("barcode")
                detail.code = item.SelectToken("code")
                detail.description = item.SelectToken("description")
                detail.qty = item.SelectToken("qty")
                detail.amount = item.SelectToken("amount")
                detail.cashier = item.SelectToken("cashier")
                detail.receiptNo = item.SelectToken("receiptNo")

                report.Add(detail)
            Next

            Cursor.Current = Cursors.Default
            Return report
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            MsgBox(ex.Message)
            Return Nothing
        End Try
        Cursor.Current = Cursors.Default
    End Function


    Private Function displayReport(report As List(Of ProductListingReport))

        For Each detail As ProductListingReport In report
            Dim dtgrdRow As New DataGridViewRow
            Dim dtgrdCell As DataGridViewCell

            dtgrdCell = New DataGridViewTextBoxCell()
            dtgrdCell.Value = detail.date.ToString("yyyy-MM-dd")
            dtgrdRow.Cells.Add(dtgrdCell)

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
            dtgrdCell.Value = detail.qty
            dtgrdRow.Cells.Add(dtgrdCell)

            dtgrdCell = New DataGridViewTextBoxCell()
            dtgrdCell.Value = LCurrency.displayValue(detail.amount)
            dtgrdRow.Cells.Add(dtgrdCell)

            dtgrdCell = New DataGridViewTextBoxCell()
            dtgrdCell.Value = detail.cashier
            dtgrdRow.Cells.Add(dtgrdCell)

            dtgrdCell = New DataGridViewTextBoxCell()
            dtgrdCell.Value = detail.receiptNo
            dtgrdRow.Cells.Add(dtgrdCell)

            dtgrdReport.Rows.Add(dtgrdRow)
        Next
        Return vbNull
    End Function

    Private Sub Button1_Click(sender As Object, e As EventArgs) Handles Button1.Click
        dtgrdReport.Rows.Clear()
        displayReport(runReport(dateStart.Text, dateEnd.Text, Till.TILLNO))
    End Sub

    Private Sub Button2_Click(sender As Object, e As EventArgs) Handles Button2.Click
        Me.Dispose()
    End Sub

    Private Sub Button3_Click(sender As Object, e As EventArgs) Handles Button3.Click
        dtgrdReport.Rows.Clear()
    End Sub
End Class
Class ProductListingReportArgs
    Public Property from As Date
    Public Property [to] As Date
    Public Property tillNo As String
End Class