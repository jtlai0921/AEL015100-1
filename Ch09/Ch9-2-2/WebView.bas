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
    Dim WebView1 As WebView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("WebView")
    Activity.Title = "WebView元件"
    WebView1.Width = 100%x
    WebView1.Height = 100%y
End Sub

Sub Activity_Resume
    Dim URL As String
    URL = Main.URL
    WebView1.LoadUrl(URL)
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


