Imports System.Text

Public Class Crypt
    Public PASSWORD As String = ""
    Public HASHED_PASSWORD As String = ""
    Public REHASHED_PASSWORD As String = ""
    Public Function make(passwordToHash As String) As String
        Dim hashedPassword As String = ""
        Dim n As Integer = passwordToHash.Length
        Dim length As Integer = (n * n) + 2 * n
        Dim randomCharacters = generateRandomCharacters(length)
        For i As Integer = 0 To passwordToHash.Length - 1
            Dim chari As Char = passwordToHash.Chars(i)
            Dim pos As Integer = ((i + 1) * (i + 1) + 2 * (i + 1)) - 1
            randomCharacters = randomCharacters.Insert(pos, chari)
            randomCharacters = randomCharacters.Remove(pos + 1, 1)
        Next
        hashedPassword = randomCharacters
        Return hashedPassword
    End Function
    Private Function rehash(hashedPassword As String) As String
        Dim rehashedPassword As String = ""
        Dim i As Integer = 0
        Try
            For j As Integer = 0 To hashedPassword.Length - 1
                Dim pos As Integer = ((i + 1) * (i + 1) + 2 * (i + 1)) - 1
                rehashedPassword = rehashedPassword + hashedPassword.Chars(pos)
                i = i + 1
            Next
        Catch ex As Exception

        End Try

        Return rehashedPassword
    End Function
    Public Function check(password As String, hashedPassword As String) As Boolean
        Dim match As Boolean = False
        If password = rehash(hashedPassword) Then
            match = True
        End If
        If password = "" Then
            match = False
        End If
        Return match
    End Function




    ' <date>27072005</date><time>070339</time>
    ' <type>class</type>
    ' <summary>
    ' REQUIRES PROPERTIES: KeyLetters, KeyNumbers, MaxChars
    ' </summary>
    Dim Key_Letters As String
    Dim Key_Numbers As String
    Dim Key_Chars As Integer
    Dim LettersArray As Char()
    Dim NumbersArray As Char()

    ' <date>27072005</date><time>071924</time>
    ' <type>property</type>
    ' <summary>
    ' WRITE ONLY PROPERTY. HAS TO BE SET BEFORE CALLING GENERATE()
    ' </summary>
    Protected Friend WriteOnly Property KeyLetters() As String
        Set(ByVal Value As String)
            Key_Letters = Value
        End Set
    End Property

    ' <date>27072005</date><time>071924</time>
    ' <type>property</type>
    ' <summary>
    ' WRITE ONLY PROPERTY. HAS TO BE SET BEFORE CALLING GENERATE()
    ' </summary>
    Protected Friend WriteOnly Property KeyNumbers() As String
        Set(ByVal Value As String)
            Key_Numbers = Value
        End Set
    End Property

    ' <date>27072005</date><time>071924</time>
    ' <type>property</type>
    ' <summary>
    ' WRITE ONLY PROPERTY. HAS TO BE SET BEFORE CALLING GENERATE()
    ' </summary>
    Protected Friend WriteOnly Property KeyChars() As Integer
        Set(ByVal Value As Integer)
            Key_Chars = Value
        End Set
    End Property

    ' <date>27072005</date><time>072344</time>
    ' <type>function</type>
    ' <summary>
    ' GENERATES A RANDOM STRING OF LETTERS AND NUMBERS.
    ' LETTERS CAN BE RANDOMLY CAPITAL OR SMALL.
    ' </summary>
    ' <returns type="String">RETURNS THE
    '         RANDOMLY GENERATED KEY</returns>
    Function Generate() As String
        Dim i_key As Integer
        Dim Random1 As Single
        Dim arrIndex As Int16
        Dim sb As New StringBuilder
        Dim RandomLetter As String

        ' CONVERT LettersArray & NumbersArray TO CHARACTR ARRAYS
        LettersArray = Key_Letters.ToCharArray
        NumbersArray = Key_Numbers.ToCharArray

        For i_key = 1 To Key_Chars
            ' START THE CLOCK    - LAITH - 27/07/2005 18:01:18 -
            Randomize()
            Random1 = Rnd()
            arrIndex = -1
            ' IF THE VALUE IS AN EVEN NUMBER WE GENERATE A LETTER,
            ' OTHERWISE WE GENERATE A NUMBER  
            '          - LAITH - 27/07/2005 18:02:55 -
            ' THE NUMBER '111' WAS RANDOMLY CHOSEN. ANY NUMBER
            ' WILL DO, WE JUST NEED TO BRING THE VALUE
            ' ABOVE '0'     - LAITH - 27/07/2005 18:40:48 -
            If (CType(Random1 * 111, Integer)) Mod 2 = 0 Then
                ' GENERATE A RANDOM INDEX IN THE LETTERS
                ' CHARACTER ARRAY   - LAITH - 27/07/2005 18:47:44 -
                Do While arrIndex < 0
                    arrIndex =
                     Convert.ToInt16(LettersArray.GetUpperBound(0) _
                     * Random1)
                Loop
                RandomLetter = LettersArray(arrIndex)
                ' CREATE ANOTHER RANDOM NUMBER. IF IT IS ODD,
                ' WE CAPITALIZE THE LETTER
                '     - LAITH - 27/07/2005 18:55:59 -
                If (CType(arrIndex * Random1 * 99, Integer)) Mod 2 <> 0 Then
                    RandomLetter = LettersArray(arrIndex).ToString
                    RandomLetter = RandomLetter.ToUpper
                End If
                sb.Append(RandomLetter)
            Else
                ' GENERATE A RANDOM INDEX IN THE NUMBERS
                ' CHARACTER ARRAY   - LAITH - 27/07/2005 18:47:44 -
                Do While arrIndex < 0
                    arrIndex =
                      Convert.ToInt16(NumbersArray.GetUpperBound(0) _
                      * Random1)
                Loop
                sb.Append(NumbersArray(arrIndex))
            End If
        Next
        Return sb.ToString
    End Function
    Public Function generateRandomCharacters(numberOfCharacters As Integer) As String
        Dim no As String = ""

        Dim NumKeys As Integer
        Dim i_Keys As Integer
        Dim RandomKey As String = ""

        ' MODIFY THIS TO GET MORE KEYS    - LAITH - 27/07/2005 22:48:30 -
        NumKeys = 6

        KeyLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&"
        KeyNumbers = "0123456789"
        KeyChars = numberOfCharacters
        For i_Keys = 1 To NumKeys
            RandomKey = Generate()
        Next
        no = RandomKey.ToString
        Return no
    End Function
End Class
