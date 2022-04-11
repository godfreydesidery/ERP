Imports System.Xml
Imports System.Net
Imports Devart.Data.MySql
Imports Newtonsoft.Json.Linq
Imports Newtonsoft.Json

Public Class LApllication
    Public Shared BASE_URL As String = ""

    Private Shared Function getRemoteXMLFile(path As String)
        Dim document As New System.Xml.XmlDocument()
        ' Create a WebRequest to the remote site
        Dim request As System.Net.HttpWebRequest = System.Net.HttpWebRequest.Create(path)

        ' NB! Use the following line ONLY if the website is protected
        'request.Credentials = New System.Net.NetworkCredential("username", "password")

        ' Call the remote site, and parse the data in a response object
        Dim response As System.Net.HttpWebResponse = request.GetResponse()

        ' Check if the response is OK (status code 200)
        If response.StatusCode = System.Net.HttpStatusCode.OK Then

            ' Parse the contents from the response to a stream object
            Dim stream As System.IO.Stream = response.GetResponseStream()
            ' Create a reader for the stream object
            Dim reader As New System.IO.StreamReader(stream)
            ' Read from the stream object using the reader, put the contents in a string
            Dim contents As String = reader.ReadToEnd()
            ' Create a new, empty XML document

            ' Load the contents into the XML document
            document.LoadXml(contents)

            ' Now you have a XmlDocument object that contains the XML from the remote site, you can
            ' use the objects and methods in the System.Xml namespace to read the document

        Else
            ' If the call to the remote site fails, you'll have to handle this. There can be many reasons, ie. the 
            ' remote site does not respond (code 404) or your username and password were incorrect (code 401)
            '
            ' See the codes in the System.Net.HttpStatusCode enumerator 

            Throw New Exception("Could not retrieve document from the URL, response code: " & response.StatusCode)

        End If

        Return document
    End Function
    Public Shared localAppDataDir As String = My.Computer.FileSystem.SpecialDirectories.MyDocuments & My.Application.Info.Title + "." + My.Application.Info.Version.Major.ToString + "." + My.Application.Info.Version.Minor.ToString

    Public Function loadSettings()
        Dim computerName As String = ""
        Try
            computerName = My.Computer.Name.ToString
        Catch ex As Exception

        End Try
        Dim loaded As Boolean = False
        Dim address As String = ""
        Dim path As String = localAppDataDir + "\localSettings.xml"
        Dim document As XmlReader
        Try
            If (IO.File.Exists(path)) Then
                document = New XmlTextReader(path)
                While (document.Read())
                    Dim type = document.NodeType
                    If (type = XmlNodeType.Element) Then
                        If (document.Name = "Address") Then
                            address = document.ReadInnerXml.ToString()
                            LApllication.BASE_URL = "http://" + address + "/api"
                        End If
                    End If
                End While
                document.Dispose()
            End If
        Catch ex As Exception
            Dim res As Integer = MsgBox("Could not load settings. Settings configurations not found. Configure System?", vbCritical + vbYesNo, "Missing Configurations")
            If res = DialogResult.Yes Then
                frmServSetup.ShowDialog()
            End If

            MsgBox("Could not load System. Application will close", vbCritical + vbOKOnly, "Error: Application")
            Application.Exit()
            End
        End Try

        Try
            Dim response As Object = New Object
            response = Web.get_("ping")
        Catch ex As Exception
            Dim res As Integer = MsgBox("Settings configurations not found. Configure System?", vbCritical + vbYesNo, "Missing Configurations")
            If res = DialogResult.Yes Then
                frmServSetup.ShowDialog()
            Else
                MsgBox("Could not load settings. Application will close.", vbExclamation + vbOKOnly, ex.Message.ToString)
            End If
            Application.Exit()
        End Try
        'Load Till information
        Try
            Dim response As Object = New Object
            response = Web.get_("tills/get_by_computer_name?computer_name=" + My.Computer.Name.ToString)
            Dim till As Till = JsonConvert.DeserializeObject(Of Till)(response.ToString)

            Till.TILLNO = till.no

            PointOfSale.operatorName = till.operatorName
            PointOfSale.operatorPassword = till.operatorPassword
            PointOfSale.port = till.port
            PointOfSale.fiscalPrinterEnabled = till.fiscalPrinterEnabled

            PointOfSale.posPrinterLogicName = till.posPrinterLogicName
            PointOfSale.posPrinterEnabled = till.posPrinterEnabled

        Catch ex As Exception
            MsgBox("Could not find till information. Application will close.", vbOKOnly + vbCritical, "Error: Till")
            Application.Exit()
        End Try

        Try
            Day.bussinessDate = Day.getCurrentDate()  'settings.SelectSingleNode("Settings/Day/Date").InnerText
        Catch ex As Exception
            MsgBox("Could not load Day Information. Day not set.", vbExclamation + vbOKOnly, "Error: Day error")
            Application.Exit()
            loaded = False
        End Try
        loaded = True
        If Company.loadCompanyDetails = True Then

        Else
            MsgBox("Could not load company information", vbOKOnly + vbCritical, "Error: Loading company information")
            loaded = False
        End If
        Return loaded
    End Function
End Class
