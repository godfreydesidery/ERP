Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class frmShowHeld

    Dim carts As List(Of Cart)
    Private Sub btnClose_Click(sender As Object, e As EventArgs) Handles btnClose.Click
        Me.Dispose()
    End Sub

    Private Sub frmShowHeld_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        displayCartsHeld(Till.TILLNO)
    End Sub

    Private Function displayCartsHeld(tillNo As String)
        dtgrdHeld.Rows.Clear()

        carts = New List(Of Cart)
        Dim response As New Object()
        Dim json As New JObject()
        Cursor.Current = Cursors.WaitCursor
        Try

            response = Web.get_("carts/show_held?till_no=" + tillNo)

            For Each item As JObject In JArray.Parse(response)
                Dim cart As Cart = New Cart
                cart.id = item.SelectToken("id")
                cart.no = item.SelectToken("no")

                Dim dtgrdRow As New DataGridViewRow
                Dim dtgrdCell As DataGridViewCell

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = cart.id
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = cart.no
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdCell = New DataGridViewTextBoxCell()
                dtgrdCell.Value = LCurrency.displayValue(item.SelectToken("amount"))
                dtgrdRow.Cells.Add(dtgrdCell)

                dtgrdHeld.Rows.Add(dtgrdRow)

            Next

            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            MsgBox(ex.Message)
            Return Nothing
        End Try
        Cursor.Current = Cursors.Default

        Return vbNull
    End Function

    Private Sub dtgrdHeld_CellDoubleClick(sender As Object, e As DataGridViewCellEventArgs) Handles dtgrdHeld.CellDoubleClick
        unholdCart()
        Me.Dispose()
    End Sub

    Private Function unholdCart()
        Dim response As New Object
        Dim json As New JObject
        Try
            Cursor.Current = Cursors.WaitCursor
            response = Web.get_("carts/unhold?till_no=" + Till.TILLNO + "&id=" + dtgrdHeld.Item(0, dtgrdHeld.CurrentRow.Index).Value)
            Cursor.Current = Cursors.Default
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            MsgBox("Could not unhold, please empty the current cart")
            Return New Cart
        End Try
        Cursor.Current = Cursors.Default
        Return vbNull
    End Function
End Class