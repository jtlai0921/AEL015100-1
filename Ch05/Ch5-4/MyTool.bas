Type=StaticCode
Version=2.7
@EndOfDesignText@
'Code module
'Subs in this code module will be accessible from all modules.
Sub Process_Globals
    Dim Title As String : Title = "我的函數庫"
End Sub

Sub IntDivide(Op1 As Int, Op2 As Int) As Int
    Dim Result As Int
    Result = Op1 / Op2
    Return Result
End Sub

Sub Sleep(ms As Long)
    Dim now As Long
    If ms > 1000 Then ms = 1000 
    now = DateTime.now
    Do Until (DateTime.now > now + ms)
        DoEvents
    Loop
End Sub