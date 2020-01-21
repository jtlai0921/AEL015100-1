Type=Class
Version=2.7
@EndOfDesignText@
' 類別模組Class module
Sub Class_Globals
    Private Name As String
    Private Birthday As Long
End Sub

Public Sub Initialize(aName As String, aBirthday As Long)
    Name = aName
    Birthday = aBirthday
End Sub

Public Sub GetName As String
    Return Name
End Sub

Public Sub GetAge As Int
    Return GetAgeAt(DateTime.Now)
End Sub

Public Sub GetAgeAt(now As Long) As Int
    Dim diff As Long
    Dim age As Int
    diff = now - Birthday
    age = Floor(diff / DateTime.TicksPerDay / 365)
    Return age
End Sub