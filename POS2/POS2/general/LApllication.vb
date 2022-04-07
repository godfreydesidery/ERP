Imports System.Xml
Imports System.Net
Imports Devart.Data.MySql
Imports Newtonsoft.Json.Linq

Public Class LApllication



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
    'Public Shared localAppDataDir As String = "C:\" + My.Application.Info.Title + "." + My.Application.Info.Version.Major.ToString + "." + My.Application.Info.Version.Minor.ToString
    Dim databaseAddress As String = ""
    Dim databasePassword As String = ""
    Dim databaseUserID As String = ""
    Dim databaseName As String = ""
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

        Dim settings As New System.Xml.XmlDocument()
        Try
            settings = getRemoteXMLFile("http://" + address + "/rms/settings/setting_info.xml")
        Catch ex As System.Net.WebException

            Dim res As Integer = MsgBox("Settings configuretions not found. Configure System?", vbCritical + vbYesNo, "Missing Configurations")
            If res = DialogResult.Yes Then
                frmServSetup.ShowDialog()
            Else
                MsgBox("Could not load settings. Application will close.", vbExclamation + vbOKOnly, ex.Message.ToString)
            End If
            Application.Exit()
            End
        Catch ex As Exception
            Dim res As Integer = MsgBox("Settings configurations not found. Configure System?", vbCritical + vbYesNo, "Missing Configurations")
            If res = DialogResult.Yes Then
                frmServSetup.ShowDialog()
            Else
                MsgBox("Could not load settings. Application will close.", vbExclamation + vbOKOnly, ex.Message.ToString)
            End If
            Application.Exit()
            End

        End Try

        'database infomation

        Try
            databaseName = settings.SelectSingleNode("Settings/Database/Name").InnerText.ToString
            databaseAddress = settings.SelectSingleNode("Settings/Database/Address").InnerText.ToString
            databaseUserID = settings.SelectSingleNode("Settings/Database/UserID").InnerText.ToString
            databasePassword = settings.SelectSingleNode("Settings/Database/Password").InnerText.ToString

        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try

        'load database

        Dim connectionString As String = ""
        connectionString = "server=" + databaseAddress + ";user id=" + databaseUserID + ";password=" + databasePassword + ";Database=" + databaseName + ";pooling=false"
        Database.conString = connectionString
        Dim con As New MySqlConnection(Database.conString)
        Dim isLoaded As Boolean = False
        Try
            isLoaded = True
            con.Open()
            con.Close()

        Catch ex As Exception
            isLoaded = False
            MsgBox("Could not connect to database: " + ex.Message.ToString)

        End Try



        Try
            Dim response As Object = New Object
            Dim json As JObject = New JObject
            response = Web.get_("tills/get_by_computer_name?computer_name=" + My.Computer.Name.ToString)
            json = JObject.Parse(response)
            Till.TILLNO = json.SelectToken("no")
        Catch ex As Exception
            MsgBox("Could not find till information. Application will close.", vbOKOnly + vbCritical, "Error: Till")
            Application.Exit()
        End Try







        'load till information
        Try
            Dim compName As String = My.Computer.Name.ToString
            Dim query As String = "SELECT till.till_no,till.computer_name FROM `till` WHERE till.computer_name=@computerName"
            Dim command As New MySqlCommand()
            Dim conn As New MySqlConnection(Database.conString)
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@computerName", compName.ToString)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            If reader.HasRows Then
                While reader.Read
                    '          Till.TILLNO = reader.GetString("till_no")
                    '       Exit While
                End While
            Else

            End If

        Catch ex As Exception
            '      MsgBox(ex.Message)
        End Try
        'load printer informations
        Try
            Dim compName As String = My.Computer.Name.ToString
            Dim query As String = "SELECT till.till_no,till.computer_name,fiscal_printer.operator_code,fiscal_printer.operator_password,fiscal_printer.port,fiscal_printer.status FROM `till`,`fiscal_printer` WHERE till.till_no=fiscal_printer.till_no AND till.computer_name=@computerName"
            Dim command As New MySqlCommand()
            Dim conn As New MySqlConnection(Database.conString)
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@computerName", compName.ToString)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            If reader.HasRows Then
                While reader.Read
                    PointOfSale.operatorName = reader.GetString("operator_code")
                    PointOfSale.operatorPassword = reader.GetString("operator_password")
                    PointOfSale.port = reader.GetString("port")
                    PointOfSale.fiscalPrinterEnabled = reader.GetString("status")
                    Exit While
                End While
            Else
                MsgBox("No fiscal printer settings", vbExclamation + vbOKOnly, "Error: Fiscal Printer")
            End If

        Catch ex As Exception
            '     LError.databaseConnection()
        End Try
        Try
            Dim compName As String = My.Computer.Name.ToString
            Dim query As String = "SELECT till.till_no,till.computer_name,pos_printer.logical_name,pos_printer.status FROM `till`,`pos_printer` WHERE till.till_no=pos_printer.till_no AND till.computer_name=@computerName"
            Dim command As New MySqlCommand()
            Dim conn As New MySqlConnection(Database.conString)
            conn.Open()
            command.CommandText = query
            command.Connection = conn
            command.CommandType = CommandType.Text
            command.Parameters.AddWithValue("@computerName", compName.ToString)
            Dim reader As MySqlDataReader = command.ExecuteReader()
            If reader.HasRows Then
                While reader.Read
                    PointOfSale.posPrinterLogicName = reader.GetString("logical_name")
                    PointOfSale.posPrinterEnabled = reader.GetString("status")
                    Exit While
                End While
            Else
                MsgBox("No POS printer settings", vbExclamation + vbOKOnly, "Error: POS Printer")
            End If

        Catch ex As Exception
            '    LError.databaseConnection()
        End Try

        'load day information
        Try
            Day.systemDate = Day.getCurrentDay.ToString("yyyy-MM-dd") 'settings.SelectSingleNode("Settings/Day/Date").InnerText
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
