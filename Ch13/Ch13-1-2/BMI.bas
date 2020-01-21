Type=Activity
Version=2.71
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
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("BMI")
    Activity.Title = "BMI相關資訊"	
End Sub

Sub Activity_Resume
    Dim n As Notification
    n.Initialize()
    n.Cancel(1)   ' 取消通知
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    StartActivity(Main)	
End Sub