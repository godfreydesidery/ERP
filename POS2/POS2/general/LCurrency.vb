Public Class LCurrency
    Public Shared Function displayValue(value As String)
        If value = "" Then
            value = "0"
        End If
        Return FormatNumber(value.ToString, 2, , , TriState.True)
    End Function
    Public Shared Function getValue(value As String)
        Return value.Replace(",", "")
    End Function
End Class
