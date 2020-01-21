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
    Dim Button1 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.Title = "第二個活動"
    Button1.Initialize("Button1")
    Button1.Text = "回到主活動"
    Activity.AddView(Button1, 50dip, 100dip, 60%x, 100dip)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    StartActivity("Main")
End Sub
