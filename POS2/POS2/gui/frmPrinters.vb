Imports System.Xml

Public Class frmPrinters

    Private Sub Button3_Click(sender As Object, e As EventArgs) Handles Button3.Click
        Me.Dispose()
    End Sub

    Private Sub frmPrinters_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        Try
            If My.Computer.FileSystem.FileExists(path) Then
                ' Load the XmlDocument.
                Dim settingsXML As New XmlDocument()
                settingsXML.Load(path)

                Dim fiscalNode As XmlNode = settingsXML.SelectSingleNode("/Settings/Printer/Fiscal_printer/Enabled_fiscal")
                If fiscalNode IsNot Nothing Then
                    If fiscalNode.InnerText = "True" Then
                        fiscalEnable.Checked = True
                    End If
                End If

                Dim posNode As XmlNode = settingsXML.SelectSingleNode("/Settings/Printer/POS_printer/Enabled_POS")
                If posNode IsNot Nothing Then
                    If posNode.InnerText = "True" Then
                        posEnable.Checked = True
                    End If
                End If
            End If
        Catch ex As Exception

        End Try

        If User.ROLE_ = "admin" Then
            fiscalCode.Enabled = True
            fiscalPassword.Enabled = True
            fiscalDrawer.Enabled = True
        End If
        If User.ROLE_ = "cashier" Then

        End If
        If User.ROLE_ = "manager" Then
            'fiscalCode.Enabled = True
            'fiscalPassword.Enabled = True
            'fiscalDrawer.Enabled = True
        End If
    End Sub
    Dim localAppDataDir As String = "C:\" + My.Application.Info.Title + "." + My.Application.Info.Version.Major.ToString + "." + My.Application.Info.Version.Minor.ToString
    Dim path As String = localAppDataDir + "\localSettings.xml"
    Private Function saveFiscal()
        Try
            Dim enabled As String = "False"
            If fiscalEnable.Checked = True Then
                enabled = "True"
            End If
            If My.Computer.FileSystem.FileExists(path) Then
                ' Load the XmlDocument.
                Dim settingsXML As New XmlDocument()
                settingsXML.Load(path)

                Dim node As XmlNode = settingsXML.SelectSingleNode("/Settings/Printer/Fiscal_printer/Enabled_fiscal")
                If node IsNot Nothing Then
                    node.ChildNodes(0).InnerText = enabled
                    settingsXML.Save(path)
                    If enabled = "True" Then
                        PointOfSale.fiscalPrinterEnabled = True
                    Else
                        PointOfSale.fiscalPrinterEnabled = False
                    End If
                    MsgBox("Operation successiful", vbInformation + vbOKOnly, "Success: Fiscal Printer")
                Else
                    MsgBox("Operation failed", vbExclamation + vbOKOnly, "Error: Fiscal Printer")
                End If
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return vbNull
    End Function

    Private Function savePOS()
        Try
            Dim enabled As String = "False"
            If posEnable.Checked = True Then
                enabled = "True"
            End If
            If My.Computer.FileSystem.FileExists(path) Then
                ' Load the XmlDocument.
                Dim settingsXML As New XmlDocument()
                settingsXML.Load(path)

                Dim node As XmlNode = settingsXML.SelectSingleNode("/Settings/Printer/POS_printer/Enabled_POS")
                If node IsNot Nothing Then
                    node.ChildNodes(0).InnerText = enabled
                    settingsXML.Save(path)
                    If enabled = "True" Then
                        PointOfSale.posPrinterEnabled = True
                    Else
                        PointOfSale.posPrinterEnabled = False
                    End If
                    MsgBox("Operation successiful", vbInformation + vbOKOnly, "Success: POS Printer")
                Else
                    MsgBox("Operation failed", vbExclamation + vbOKOnly, "Error: POS Printer")
                End If
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try
        Return vbNull
    End Function
    Private Sub btnFiscalSave_Click(sender As Object, e As EventArgs) Handles btnFiscalSave.Click
        saveFiscal()
    End Sub

    Private Sub btnPOSSave_Click(sender As Object, e As EventArgs) Handles btnPOSSave.Click
        savePOS()
    End Sub

    Private Sub saveAll_Click(sender As Object, e As EventArgs) Handles saveAll.Click
        saveFiscal()
        savePOS()
    End Sub


End Class