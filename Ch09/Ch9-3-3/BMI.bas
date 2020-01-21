Type=Activity
Version=2.7
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
    Dim lblOutput As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("BMI")
    Activity.Title = "BMI值"
End Sub

Sub Activity_Resume
    Dim h, w As Double
    Dim i As Intent
    ' 取得傳入的Intent物件
    i = Activity.GetStartingIntent()
    h = i.GetExtra("H")   ' 取出資料
    w = i.GetExtra("W")
    calBMI(h, w)
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub calBMI(h As Double, w As Double)
    lblOutput.Text = "BMI: " & (w / (h * h))
End Sub

Sub Button1_Click
    Dim i As Intent
    i.Initialize("", "")
    i.SetComponent("ch09.gui.b4a.example/.main")
    StartActivity(i)	
End Sub