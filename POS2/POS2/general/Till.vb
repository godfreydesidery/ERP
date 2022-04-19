Imports Devart.Data.MySql

Public Class Till
    Public Shared TILLNO As String = ""

    'Till basic information
    Public Property id As String
    Public Property no As String
    Public Property name As String
    Public Property computerName As String
    Public Property active As Boolean

    'Till postiotion
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

    'Till float balance
    Public Property floatBalance As Double
    'Printers information
    Public Property operatorName As String
    Public Property operatorPassword As String
    Public Property port As String
    Public Property fiscalPrinterEnabled As Boolean
    Public Property posPrinterLogicName As String
    Public Property posPrinterEnabled As Boolean
    Public Property negativeSalesEnabled As Boolean

    Public Shared Function tillTotalRegister(tillNo As String, cash As Double, voucher As Double, cheque As Double, deposit As Double, loyalty As Double, CRCard As Double, CAP As Double, invoice As Double, CRNote As Double, mobile As Double)
        Dim commited As Boolean = False

        Return commited
        Return vbNull
    End Function

End Class
