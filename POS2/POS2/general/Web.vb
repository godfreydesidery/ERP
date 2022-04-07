Imports System.IO
Imports System.Net
Imports System.Text
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class Web
    Public Shared Function post(data As Object, url As String) As Object
        Try
            Dim baseUrl As String = "http://127.0.0.1:8080"
            Dim encoding As New UTF8Encoding
            Dim byteData() As Byte = encoding.GetBytes(JsonConvert.SerializeObject(data, Formatting.Indented))
            Dim postReq As HttpWebRequest = CType(WebRequest.Create(baseUrl + "/" + url), HttpWebRequest)
            postReq.Method = "POST"
            postReq.KeepAlive = True
            postReq.ContentType = "application/json"
            postReq.Referer = baseUrl
            postReq.Headers("user_id") = User.USER_ID.ToString
            '  postReq .UserAgent =""
            postReq.ContentLength = byteData.Length
            Dim postReqstream As Stream = postReq.GetRequestStream
            postReqstream.Write(byteData, 0, byteData.Length)
            postReqstream.Close()
            Dim postResponse As HttpWebResponse
            postResponse = CType(postReq.GetResponse(), HttpWebResponse)
            Dim postRequestReader As New StreamReader(postResponse.GetResponseStream(), New UTF8Encoding)
            Dim responseFromServer As Object = postRequestReader.ReadToEnd
            'Dim json As Object = responseFromServer
            'Dim ser As JObject = JObject.Parse(json)
            Return responseFromServer
        Catch ex As System.Net.WebException
            MsgBox(ex.ToString)
            Return vbNull
        Catch ex As Exception
            MsgBox(ex.ToString)
        End Try
        Return vbNull
    End Function

    Public Shared Function put(data As Object, url As String) As Boolean
        Try
            Dim baseUrl As String = "http://127.0.0.1:8080"
            Dim encoding As New UTF8Encoding
            Dim byteData() As Byte = encoding.GetBytes(JsonConvert.SerializeObject(data, Formatting.Indented))
            Dim postReq As HttpWebRequest = CType(WebRequest.Create(baseUrl + "/" + url), HttpWebRequest)
            postReq.Method = "PUT"
            postReq.KeepAlive = True
            postReq.ContentType = "application/json"
            postReq.Referer = baseUrl
            postReq.Headers("user_id") = User.USER_ID.ToString
            '  postReq .UserAgent =""
            postReq.ContentLength = byteData.Length
            Dim postReqstream As Stream = postReq.GetRequestStream
            postReqstream.Write(byteData, 0, byteData.Length)
            postReqstream.Close()
            Dim postResponse As HttpWebResponse
            postResponse = CType(postReq.GetResponse(), HttpWebResponse)
            Dim postRequestReader As New StreamReader(postResponse.GetResponseStream(), New UTF8Encoding)
            Dim responseFromServer As Object = postRequestReader.ReadToEnd
            '  Dim json As Object = responseFromServer
            '   Dim ser As JObject = JObject.Parse(json)

            Return True
        Catch ex As System.Net.WebException
            MsgBox(ex.ToString)
            Return False
        Catch ex As Exception
            MsgBox(ex.Message)
            Return False
        End Try
        Return False
    End Function
    Public Shared Function patch(data As Object, url As String) As Boolean
        Try
            Dim baseUrl As String = "http://127.0.0.1:8080"
            Dim encoding As New UTF8Encoding
            Dim byteData() As Byte = encoding.GetBytes(JsonConvert.SerializeObject(data, Formatting.Indented))
            Dim postReq As HttpWebRequest = CType(WebRequest.Create(baseUrl + "/" + url), HttpWebRequest)
            postReq.Method = "PATCH"
            postReq.KeepAlive = True
            postReq.ContentType = "application/json"
            postReq.Referer = baseUrl
            postReq.Headers("user_id") = User.USER_ID.ToString
            '  postReq .UserAgent =""
            postReq.ContentLength = byteData.Length
            Dim postReqstream As Stream = postReq.GetRequestStream
            postReqstream.Write(byteData, 0, byteData.Length)
            postReqstream.Close()
            Dim postResponse As HttpWebResponse
            postResponse = CType(postReq.GetResponse(), HttpWebResponse)
            Dim postRequestReader As New StreamReader(postResponse.GetResponseStream(), New UTF8Encoding)
            Dim responseFromServer As Object = postRequestReader.ReadToEnd
            Dim json As Object = responseFromServer
            Dim ser As JObject = JObject.Parse(json)
            Return True
        Catch ex As System.Net.WebException
            MsgBox(ex.Message)
            Return vbNull
        Catch ex As Exception
            MsgBox(ex.ToString)
            Return False
        End Try
        Return False
    End Function
    Public Shared Function delete(url As String) As String
        Try
            Dim baseUrl As String = "http://127.0.0.1:8080"
            'Create initial request
            Dim request As HttpWebRequest = HttpWebRequest.Create(baseUrl + "/" + url)
            request.Proxy = Nothing
            '  request.UserAgent = "Test"
            request.Method = "DELETE"
            request.Headers("user_id") = User.USER_ID.ToString

            'Create the response and reader
            Dim response As HttpWebResponse = request.GetResponse
            Dim responseStream As System.IO.Stream = response.GetResponseStream

            'Create a new stream reader
            Dim streamReader As New System.IO.StreamReader(responseStream)
            Dim responseFromServer As String = streamReader.ReadToEnd
            streamReader.Close()
            Return responseFromServer.ToString
        Catch ex As System.Net.WebException
            Dim statusCode = DirectCast(ex.Response, HttpWebResponse).StatusCode
            Return "Operation failed " + ex.Message.ToString
            Exit Function
        Catch ex As Exception
            Return "Operation failed " + ex.Message.ToString
            Exit Function
        End Try
    End Function
    Public Shared Function get_(url As String) As Object
        '    url = System.Web.HttpUtility.UrlEncode(url)
        Try
            Dim baseUrl As String = "http://127.0.0.1:8080"
            'Create initial request
            Dim request As HttpWebRequest = HttpWebRequest.Create(baseUrl + "/" + url)
            '    MsgBox(baseUrl + "/" + System.Web.HttpUtility.UrlEncode(url))
            request.Proxy = Nothing
            request.UserAgent = "Test"
            request.Headers("user_id") = User.USER_ID.ToString

            'Create the response and reader
            Dim response As HttpWebResponse = request.GetResponse
            Dim responseStream As System.IO.Stream = response.GetResponseStream

            'Create a new stream reader
            Dim streamReader As New System.IO.StreamReader(responseStream)
            Dim responseFromServer As Object = streamReader.ReadToEnd
            'Dim json As Object = responseFromServer
            'Dim ser As JObject = JObject.Parse(json)
            streamReader.Close()
            Return responseFromServer
        Catch ex As System.Net.WebException
            '    MsgBox(ex.ToString)
            Return vbNull
            '  Dim statusCode = DirectCast(ex.Response, HttpWebResponse).StatusCode
            '   If statusCode = 404 Then
            '  MsgBox("No matching record")
            '   Else
            '   MsgBox(ex.ToString)
            '   End If
            '  Return vbNull
        Catch ex As Exception
            MsgBox(ex.ToString)
            '  Return vbNull
            '  MsgBox(ex.ToString)
            Return vbNull
        End Try
        Return vbNull
    End Function

End Class
