Imports System.IO
Imports System.Xml

Public Class frmServSetup
    Private Sub btnClose_Click(sender As Object, e As EventArgs) Handles btnClose.Click
        Application.Exit()
    End Sub
    Private Function ping(address As String)
        Dim available As Boolean = False
        Try

            If My.Computer.Network.Ping(address, 100) Then
                available = True
            Else
                available = False
            End If
        Catch ex As Exception
            MsgBox(ex.Message)
        End Try

        Return available
    End Function
    Private Sub btnOK_Click(sender As Object, e As EventArgs) Handles btnOK.Click
        If True Then
            Dim localSettings As String = LApllication.localAppDataDir + "\localSettings.xml"
            ' Create the file if it does not exist
            Try
                File.Create(localSettings).Dispose()
                'Write To the xml file
                Dim settings As New XmlWriterSettings()
                settings.Indent = True
                Dim writer As XmlWriter = XmlWriter.Create(localSettings, settings)
                writer.WriteStartElement("Settings")
                writer.WriteStartElement("Server")
                writer.WriteStartElement("Address")
                writer.WriteString(txtAddress.Text)
                writer.WriteEndElement()
                writer.WriteEndElement()

                writer.WriteStartElement("Printer")

                writer.WriteStartElement("Fiscal_printer")
                writer.WriteStartElement("Enabled_fiscal")
                writer.WriteString("True")
                writer.WriteEndElement()
                writer.WriteEndElement()

                writer.WriteStartElement("POS_printer")
                writer.WriteStartElement("Enabled_POS")
                writer.WriteString("True")
                writer.WriteEndElement()
                writer.WriteEndElement()

                writer.WriteEndElement()
                writer.WriteEndElement()
                writer.Close()
                'writer.Dispose()
                Application.Exit()
            Catch ex As Exception
                MsgBox(ex.Message)
                Application.Exit()
            End Try
        End If
    End Sub

    Private Sub btnTest_Click(sender As Object, e As EventArgs) Handles btnTest.Click

        If ping(txtAddress.Text.ToString) = True Then
            MsgBox("Connected", vbOKOnly)
        Else
            MsgBox("Not Connected")
        End If
    End Sub
End Class