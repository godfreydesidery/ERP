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


    Public Function makeReceipt(tillNo As String, date_ As String) As Integer
        Dim number As Integer = 0

        'get the maximum receipt number for that particular date
        'and increment it by 1
        'to provide a starting point
        Try
            Dim conn As New MySqlConnection(Database.conString)
            Dim command As New MySqlCommand()
            Dim query As String = ""
            query = "SELECT  MAX(`receipt_no`)AS `receipt_no` FROM `receipt` WHERE `till_no`='" + tillNo + "' AND `date`='" + date_ + "'"
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            Dim reader As MySqlDataReader = command.ExecuteReader()
            While reader.Read
                number = Val(reader.GetString("receipt_no")) + 1
                Exit While
            End While
            conn.Close()
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return number
    End Function
End Class
