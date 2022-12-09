Imports POS.Devices
Imports OposPOSPrinter_CCO
Imports System.IO
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq
'Imports OposFiscalPrinter_CCO
Public Class PointOfSale
    ''' <summary>
    ''' pos printer settings
    ''' </summary>
    Public Shared posPrinterLogicName As String = ""
    Public Shared posCashDrawerLogicName As String = ""
    Public Shared posLineDisplayLogicName As String = ""
    Public Shared posPrinterEnabled As String = ""

    ''' <summary>
    ''' 'fiscal printer settings
    ''' </summary>
    Public Shared strLogicalName As String = InstalledPPOSDevices.posLogicName  'get the available fiscal printer logical name
    Public Shared fiscalPrinterDeviceName As String = ""
    Public Shared operatorName As String = ""
    Public Shared operatorPassword As String = ""
    Public Shared port As String = ""
    Public Shared drawer As String = ""
    Public Shared fiscalPrinterEnabled As String = ""
    Public Shared negativeSalesEnabled As Boolean = True
    ''' <summary>
    ''' function to print receipt
    ''' </summary>
    ''' 
    ''' <param name="tillNo"></param>
    ''' <param name="receiptNo"></param>
    ''' <param name="date_"></param>
    ''' <param name="TIN"></param>
    ''' <param name="VRN"></param>
    ''' <param name="itemCode"></param>
    ''' <param name="descr"></param>
    ''' <param name="qty"></param>
    ''' <param name="price"></param>
    ''' <param name="tax"></param>
    ''' <param name="amount"></param>
    ''' <param name="subTotal"></param>
    ''' <param name="VAT"></param>
    ''' <param name="grandTotal"></param>
    ''' <returns></returns>
    ''' 
    Private Shared prn As New RawPrinterHelper

    Private Shared PrinterName As String = "EPSON TM-T20 Receipt"

    Private Shared eClear As String = Chr(27) + "@"
    Private Shared eCentre As String = Chr(27) + Chr(97) + "1"
    Private Shared eLeft As String = Chr(27) + Chr(97) + "0"
    Private Shared eRight As String = Chr(27) + Chr(97) + "2"
    Private Shared eDrawer As String = eClear + Chr(27) + "p" + Chr(0) + ".}"
    Private Shared eCut As String = Chr(27) + "i" + vbCrLf
    Private Shared eSmlText As String = Chr(27) + "!" + Chr(1)
    Private Shared eNmlText As String = Chr(27) + "!" + Chr(0)
    Private Shared eInit As String = eNmlText + Chr(13) + Chr(27) +
        "c6" + Chr(1) + Chr(27) + "R3" + vbCrLf
    Private Shared eBigCharOn As String = Chr(27) + "!" + Chr(56)
    Private Shared eBigCharOff As String = Chr(27) + "!" + Chr(0)


    Public Shared Function printFiscalReceipt1(tillNo As String, receiptNo As String, date_ As String, TIN As String, VRN As String, itemCode() As String, descr() As String, qty() As String, price() As String, tax() As String, amount() As String, subTotal As String, VAT As String, grandTotal As String, cash As String, balance As String)

        Dim fileName As String = "Receipt"
        Dim strFile As String = My.Computer.FileSystem.SpecialDirectories.MyDocuments & fileName & ".txt"
        Dim sw As StreamWriter

        Try
            If (Not File.Exists(strFile)) Then
                sw = File.CreateText(strFile)
            Else
                File.WriteAllText(strFile, "")
                sw = File.AppendText(strFile)
            End If
            Dim str As String = ""



            'sw.WriteLine("R_TXT ""Receipt :" + receiptNo + """")
            str = str + "R_TXT ""Receipt :" + receiptNo + """" + vbCrLf
            'sw.WriteLine("R_TXT ""Code  Qty Price@ """)
            str = str + "R_TXT ""Code  Qty Price@ """ + vbCrLf
            'sw.WriteLine("R_TXT ""Description """)
            str = str + "R_TXT ""Description """ + vbCrLf
            'sw.WriteLine("R_TXT ""===================================== """)
            str = str + "R_TXT ""===================================== """ + vbCrLf
            For i As Integer = 0 To descr.Length - 1
                'sw.WriteLine("R_TRP """ + itemCode(i) + """" + """""" + qty(i).Replace(",", "") + "* " + price(i) + "V4")
                str = str + "R_TRP """ + itemCode(i) + """" + """""" + qty(i).Replace(",", "") + "* " + price(i) + "V4" + vbCrLf
                'sw.WriteLine("R_TXT """ + descr(i) + """")
                str = str + "R_TXT """ + descr(i) + """" + vbCrLf
                'sw.WriteLine("R_TXT ""------------------------------------- """)
                str = str + "R_TXT ""------------------------------------- """ + vbCrLf
            Next
            'sw.WriteLine("R_TXT ""===================================== """)
            str = str + "R_TXT ""===================================== """ + vbCrLf
            sw.WriteLine("R_STT P")
            str = str + "R_STT P"

            sw.Write(str)

            sw.Close()
        Catch e As Exception
            MsgBox(e.StackTrace)


        End Try

        'Dim Proc As New Process
        'Proc.StartInfo.FileName = strFile
        ' Proc.StartInfo.Verb = "Print"
        'Proc.Start()
        'Proc.Close()
        Return vbNull



    End Function

    Public Shared Function printFiscalReceipt(tillNo As String, receiptNo As String, date_ As String, TIN As String, VRN As String, itemCode() As String, descr() As String, qty() As String, price() As String, tax() As String, amount() As String, subTotal As String, VAT As String, grandTotal As String, cash As String, balance As String, vatGroup() As String) As Boolean
        ''''''''''''Dim fiscalDir As String = My.Computer.FileSystem.SpecialDirectories.MyDocuments & "\in"
        Dim fiscalDir As String = "C:\OrbitFR\in"

        If Not Directory.Exists(fiscalDir) Then
            Directory.CreateDirectory(fiscalDir)
        End If

        Dim path As String = "\EFD_RECEIPT_" + DateTime.Now.ToString("yyyy-MM-dd HH-mm-ss") & ".txt"

        Dim strFile As String = fiscalDir + path

        'Dim vatGroup As String = "V2"

        Dim receiptText As String = ""
        receiptText = receiptText + "R_NAM" + """""" + Environment.NewLine
        receiptText = receiptText + "R_VRN" + """""" + Environment.NewLine
        receiptText = receiptText + "R_TIN" + """""" + Environment.NewLine

        For i As Integer = 0 To itemCode.Length - 2
            Dim aTest As Integer 'Not necessary, to buy time
            receiptText = receiptText + "R_TRP" + """" + itemCode(i) + " " + descr(i) + """" + qty(i) + "pcs.*" + Replace(price(i), ",", "") + vatGroup(i) + Environment.NewLine
            aTest = 2
        Next

        File.AppendAllText(strFile, receiptText)

        Return True
    End Function



    Public Shared Function printReceipt(tillNo As String, receiptNo As String, date_ As String, TIN As String, VRN As String, itemCode() As String, descr() As String, qty() As String, price() As String, tax() As String, amount() As String, subTotal As String, VAT As String, grandTotal As String, cash As String, balance As String)

        'PointOfSale.printFiscalReceipt(tillNo, receiptNo, date_, TIN, VRN, itemCode, descr, qty, price, tax, amount, subTotal, VAT, grandTotal, cash, balance)

        Dim continue_ As Boolean = True
        prn.OpenPrint(posPrinterLogicName)
        Try

            Try
                prn.OpenPrint(posPrinterLogicName)
            Catch ex As Exception

            End Try

            If prn.PrinterIsOpen = False And PointOfSale.posPrinterEnabled = "ENABLED" Then
                Dim res As Integer = MsgBox("Could Not connect to POS printer. Continue operation without printing POS receipt?", vbYesNo + vbQuestion, "Error: POS Printer not available")
                If res = DialogResult.Yes Then
                    continue_ = True
                Else
                    continue_ = False
                    Return continue_
                    Exit Function
                End If
            End If
            If PointOfSale.fiscalPrinterEnabled = "ENABLED" And continue_ = True Then
                'insert options for fiscal printer in the future
                Dim res As Integer = MsgBox("Could not connect to Fiscal printer. Continue operation without printing Fiscal receipt?", vbYesNo + vbQuestion, "Error: Fiscal Printer not available")
                If res = DialogResult.Yes Then
                    continue_ = True
                Else
                    continue_ = False
                    Return continue_
                    Exit Function
                End If
            End If



            If continue_ = False Then
                Return continue_
                Exit Function
            End If




            Dim space As String = ""
            For i As Integer = 1 To (40 - Company.NAME.ToString.Length) / 2
                space = space + " "
            Next
            Dim companyName As String = space + Company.NAME
            space = ""
            For i As Integer = 1 To (40 - Company.POST_CODE.ToString.Length) / 2
                space = space + " "
            Next
            Dim postCode As String = space + Company.POST_CODE
            space = ""
            For i As Integer = 1 To (40 - Company.PHYSICAL_ADDRESS.ToString.Length) / 2
                space = space + " "
            Next
            Dim physicalAddress As String = space + Company.PHYSICAL_ADDRESS
            space = ""
            For i As Integer = 1 To (40 - Company.TELEPHONE.ToString.Length) / 2
                space = space + " "
            Next
            Dim telephone As String = space + Company.TELEPHONE
            space = ""
            For i As Integer = 1 To (40 - Company.EMAIL.ToString.Length) / 2
                space = space + " "
            Next
            Dim email As String = space + Company.EMAIL

            Dim fDateTime As String
            Dim strOutputData As String = ""
            Dim CRLF
            Dim ESC

            fDateTime = Date.Now.ToString("yyyy/MM/dd HH:mm:ss") 'System date and time

            CRLF = Chr(13) + Chr(10)
            ESC = Chr(&H1B)



            strOutputData = strOutputData + companyName + CRLF
            strOutputData = strOutputData + postCode + CRLF

            strOutputData = strOutputData + physicalAddress + CRLF
            strOutputData = strOutputData + telephone + CRLF
            strOutputData = strOutputData + email + CRLF + CRLF
            strOutputData = strOutputData + "       ***  Sales Receipt  ***" + CRLF

            strOutputData = strOutputData + "TIN:        " + TIN + CRLF
            strOutputData = strOutputData + "VRN:        " + VRN + CRLF
            strOutputData = strOutputData + "Till No:    " + tillNo + CRLF
            strOutputData = strOutputData + "Receipt No: " + receiptNo + CRLF

            strOutputData = strOutputData + CRLF

            strOutputData = strOutputData + "CODE        QTY   PRICE@     AMOUNT" + CRLF
            strOutputData = strOutputData + "DESCRIPTION" + CRLF
            strOutputData = strOutputData + "====================================" + CRLF

            For i As Integer = 0 To descr.Length - 1
                strOutputData = strOutputData + itemCode(i) + " x " + qty(i) + "  " + price(i) + "  " + amount(i) + CRLF
                strOutputData = strOutputData + descr(i) + CRLF
            Next
            strOutputData = strOutputData + "------------------------------------" + CRLF

            strOutputData = strOutputData + "Sub Total                   " + subTotal + CRLF
            strOutputData = strOutputData + "Tax                         " + VAT + CRLF
            strOutputData = strOutputData + "Total Amount                " + grandTotal + CRLF
            strOutputData = strOutputData + "====================================" + CRLF
            strOutputData = strOutputData + "Cash              " + cash + CRLF
            strOutputData = strOutputData + "Balance           " + balance + CRLF
            strOutputData = strOutputData + "====================================" + CRLF
            strOutputData = strOutputData + "        You are Welcomed !" + CRLF
            strOutputData = strOutputData + "Sale Date&Time : " + fDateTime + CRLF + CRLF
            strOutputData = strOutputData + CRLF
            strOutputData = strOutputData + "  Served by: " + User.AALIAS + CRLF
            strOutputData = strOutputData + (Chr(&H1D) & "V" & Chr(66) & Chr(0))
            Try
                prn.OpenPrint(posPrinterLogicName)

                Print(strOutputData)

                prn.ClosePrint()

            Catch ex As Exception

            End Try
        Catch ex As Exception
            MsgBox("Operation canceled. Could not print POS receipt")
            Return continue_
            Exit Function
        End Try

        Return continue_
    End Function

    Public Shared Function printOrder(copy As String, orderNo As String, waiter As String, itemCode() As String, descr() As String, qty() As String, price() As String, amount() As String, grandTotal As String)
        Dim continue_ As Boolean = True
        prn.OpenPrint(posPrinterLogicName)
        Try
            Try
                prn.OpenPrint(posPrinterLogicName)
            Catch ex As Exception
                MsgBox(ex.StackTrace)
            End Try

            If prn.PrinterIsOpen = False And PointOfSale.posPrinterEnabled = "ENABLED" Then
                continue_ = False
                MsgBox("Could not print order. Printer error")
                Return continue_
                Exit Function
            End If

            Dim space As String = ""
            For i As Integer = 1 To (40 - Company.NAME.ToString.Length) / 2
                space = space + " "
            Next
            Dim companyName As String = space + Company.NAME
            Dim fDateTime As String
            Dim strOutputData As String = ""
            Dim CRLF
            Dim ESC
            fDateTime = Date.Now.ToString("yyyy/MM/dd HH:mm:ss") 'System date and time
            CRLF = Chr(13) + Chr(10)
            ESC = Chr(&H1B)
            strOutputData = strOutputData + companyName + CRLF
            strOutputData = strOutputData + "       ***  Order Slip  ***" + CRLF
            strOutputData = strOutputData + "Order No:    " + orderNo + CRLF
            strOutputData = strOutputData + "Slip:        " + copy + CRLF
            strOutputData = strOutputData + "Waiter Name: " + waiter + CRLF
            strOutputData = strOutputData + CRLF
            strOutputData = strOutputData + "CODE        QTY   PRICE@     AMOUNT" + CRLF
            strOutputData = strOutputData + "DESCRIPTION" + CRLF
            strOutputData = strOutputData + "====================================" + CRLF
            For i As Integer = 0 To descr.Length - 1
                strOutputData = strOutputData + itemCode(i) + " x " + qty(i) + "  " + price(i) + "  " + amount(i) + CRLF
                strOutputData = strOutputData + descr(i) + CRLF
            Next
            strOutputData = strOutputData + "------------------------------------" + CRLF
            strOutputData = strOutputData + "Total Amount                " + grandTotal + CRLF
            strOutputData = strOutputData + "====================================" + CRLF
            strOutputData = strOutputData + "Printed Date&Time : " + fDateTime + CRLF
            strOutputData = strOutputData + (Chr(&H1D) & "V" & Chr(66) & Chr(0))
            Try
                prn.OpenPrint(posPrinterLogicName)
                Print(strOutputData)
                prn.ClosePrint()
            Catch ex As Exception
                MsgBox(ex.Message)
            End Try
        Catch ex As Exception
            MsgBox("Operation canceled. Could not print Order Slip")
            Return continue_
            Exit Function
        End Try
        Return continue_
    End Function



    Private Shared Sub Print(Line As String)
        prn.SendStringToPrinter(PrinterName, Line + vbLf)
    End Sub

    Private Shared Sub PrintDashes()
        Print(eLeft + eNmlText + "-".PadRight(42, "-"))
    End Sub

    Public Sub EndPrint()
        prn.ClosePrint()
    End Sub

End Class
