Imports Devart.Data.MySql

Public Class Receipt

    Public Property id As String
    Public Property no As String
    Public Property issueDate As Date
    Public Property status As String
    Public Property notes As String
    Public Property printed As Date
    Public Property rePrinted As Date
    Public Property cash As Double
    Public Property voucher As Double
    Public Property deposit As Double
    Public Property loyalty As Double
    Public Property crCard As Double
    Public Property cheque As Double
    Public Property cap As Double
    Public Property invoice As Double
    Public Property crNote As Double
    Public Property mobile As Double
    Public Property other As Double
    Public Property cart As Cart = New Cart
    Public Property till As Till = New Till
    Public Property createdUser As User = New User
    Public Property reprintedUser As User = New User
    Public Property receiptDetails As List(Of ReceiptDetail) = New List(Of ReceiptDetail)

    Public Shared Property CURRENT_RECEIPT = vbNull


    Public Function makeReceipt(tillNo As String, date_ As String) As Integer
        Dim number As Integer = 0

        'get the maximum receipt number for that particular date
        'and increment it by 1
        'to provide a starting point

        Return number
    End Function
End Class
