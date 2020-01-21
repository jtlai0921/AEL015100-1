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
    Dim lblOutput As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("Error")
    Activity.Title = "顯示錯誤訊息"
End Sub

Sub Activity_Resume
    Dim i As Intent
    i = Activity.GetStartingIntent()
    If i.HasExtra("Notification_Tag") Then
        lblOutput.Text = i.GetExtra("Notification_Tag")
    End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    StartActivity(Main)	
End Sub