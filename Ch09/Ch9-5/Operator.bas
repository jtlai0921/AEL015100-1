Type=Activity
Version=2.7
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals

End Sub

Sub Globals
    Dim ListView1 As ListView
    Dim opd1, opd2 As Int
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("Operator")
    Activity.Title = "選擇運算子"
    ' 新增項目
    ListView1.AddSingleLine("加法運算")
    ListView1.AddSingleLine("減法運算")
    ListView1.AddSingleLine("乘法運算")
    ListView1.AddSingleLine("除法運算")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub SetOperand(v1 As Int, v2 As Int)
    opd1 = v1
    opd2 = v2
End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Dim result As Double = 0.0
    ' 執行運算
    Select Position
       Case 0         ' 加
           result = opd1 + opd2
       Case 1         ' 減
           result = opd1 - opd2
       Case 2         ' 乘
           result = opd1 * opd2
       Case 3         ' 除
           result = opd1 / opd2
    End Select
    CallSubDelayed2(Main, "GetResult", result)
End Sub