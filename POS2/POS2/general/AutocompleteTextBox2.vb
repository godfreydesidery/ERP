Public Class AutocompleteTextBox2
    Inherits Windows.Forms.DataGridTextBox


    Dim _listBox As ListBox
    Dim _isAdded As Boolean
    Dim _values As String()
    Dim _formerValue As String = String.Empty

    Public Sub New()
        InitializeComponent()
        ResetListBox()
    End Sub

    Private Sub InitializeComponent()
        _listBox = New ListBox()
        'Me.OnKeyDown += this_KeyDown()
        ' Me.OnKeyUp += this_KeyUp
    End Sub

    Private Sub ShowListBox()
        If _isAdded = False Then ''check here
            Parent.Controls.Add(_listBox)
            _listBox.Left = Left
            _listBox.Top = Top + Height
            _isAdded = True
        End If
    End Sub

    Private Sub ResetListBox()
        _listBox.Visible = False
    End Sub

    Private Sub this_KeyUp(sender As Object, e As KeyEventArgs)
        UpdateListBox()
    End Sub

    Private Sub this_KeyDown(sender As Object, e As KeyEventArgs)
        Select Case e.KeyCode
            Case Keys.Enter
            Case Keys.Tab
                If _listBox.Visible Then
                    Text = _listBox.SelectedItem.ToString
                    ResetListBox()
                    _formerValue = Text
                    Me.Select(Me.Text.Length, 0)
                    e.Handled = True
                End If
                Exit Select
            Case Keys.Down
                If ((_listBox.Visible) And (_listBox.SelectedIndex < _listBox.Items.Count - 1)) Then

                    _listBox.SelectedIndex = _listBox.SelectedIndex + 1
                    e.Handled = True
                    Exit Select
                End If
            Case Keys.Up
                If ((_listBox.Visible) And (_listBox.SelectedIndex > 0)) Then
                    _listBox.SelectedIndex = _listBox.SelectedIndex - 1
                    e.Handled = True
                    Exit Select
                End If
        End Select
    End Sub
    Protected Overrides Function IsInputKey(keyData As Keys) As Boolean
        Select Case keyData
            Case Keys.Tab
                If _listBox.Visible Then
                    Return True
                Else
                    Return False
                End If
            Case Else
                Return MyBase.IsInputKey(keyData) '''check here
        End Select
    End Function
    Function ContainsText(s As String, text As String) As Boolean
        Return s.Contains(text)
    End Function
    Private Sub UpdateListBox()
        If (Text = _formerValue) Then
            Return
            _formerValue = Me.Text

            Dim word As String = Me.Text
            If (word.Length > 0) Then ''''''''''''''
                ' Dim matches As String() = Array.FindAll(Of _values, Function(x) x.ToLower().Contains(word.ToLower()))
                Dim matches As List(Of String) '= Array.FindAll(Of _values,
                For j As Integer = 0 To _values.Count - 1
                    If _values(j).ToLower.Contains(word.ToLower()) Then
                        matches.Add(_values(j))
                    End If
                Next
                If matches.Count > 0 Then
                    ShowListBox()
                    _listBox.BeginUpdate()
                    _listBox.Items.Clear()
                    'Array.ForEach(matches, Function(x) _listBox.Items.Add(x))
                    For Each match As String In matches
                        _listBox.Items.Add(match)
                    Next
                    _listBox.SelectedIndex = 0
                    _listBox.Height = 0
                    _listBox.Width = 0
                    Focus()

                    Dim graphics As Graphics = _listBox.CreateGraphics()
                    Using (graphics)
                        For i As Integer = 0 To _listBox.Items.Count - 1
                            If i < 20 Then
                                _listBox.Height = _listBox.GetItemHeight(i) + 1
                                'it Item width Is larger than the current one
                                'set it to the New max item width
                                'GetItemRectangle does Not work for me
                                'we add a little extra space by using '_'
                                Dim itemWidth As Integer = CInt(graphics.MeasureString(((_listBox.Items(i) + "_")), _listBox.Font).Width) '''''''''''''
                                _listBox.Width = itemWidth ' (_listBox.Width < itemWidth) ? itemWidth : this.Width
                            End If
                        Next

                    End Using


                End If
                _listBox.EndUpdate()
            Else
                ResetListBox()
            End If
        Else
            ResetListBox()
        End If
    End Sub

    Public Property Values As String()
        Get
            Return _values
        End Get
        Set(ByVal value As String())
            _values = value
        End Set
    End Property

    Public Property SelectedValues As List(Of String)
        Get
            Dim result As String() = Text.Split(New String() {""}, StringSplitOptions.RemoveEmptyEntries)
            Return New List(Of String) '''''''''''''''
        End Get
        Set(value As List(Of String))

        End Set
    End Property

End Class
