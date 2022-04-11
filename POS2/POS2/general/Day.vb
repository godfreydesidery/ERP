Imports Devart.Data.MySql
Imports Newtonsoft.Json
Imports Newtonsoft.Json.Linq

Public Class Day
    Public Shared zNo As Integer = 0
    Public Shared bussinessDate As String = ""



    Public Shared Function getCurrentDay1() As Date
        Dim date_ As Date = #0001-01-01#



        Return date_
    End Function

    Public Shared Function getCurrentDate() As String
        Dim response As New Object
        Dim json As New JObject
        Dim dayData As New DayData
        Cursor.Current = Cursors.WaitCursor
        Try
            response = Web.get_("days/get_bussiness_date")
            json = JObject.Parse(response.ToString)
            dayData = JsonConvert.DeserializeObject(Of DayData)(json.ToString)
            Cursor.Current = Cursors.Default
            Return dayData.bussinessDate
        Catch ex As Exception
            Cursor.Current = Cursors.Default
            Return "0001-01-01"
        End Try
        Cursor.Current = Cursors.Default
    End Function
End Class

Public Class DayData
    Public Property bussinessDate As String = "0001-01-01"
End Class
