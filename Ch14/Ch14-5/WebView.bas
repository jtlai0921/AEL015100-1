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
    Dim WebView1 As WebView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("WebView")
    Activity.Title = "顯示記錄資料"
End Sub

Sub Activity_Resume
    Dim strSQL As String
    strSQL = "SELECT id, title, price FROM Books"
    WebView1.LoadHtml(DBUtils.ExecuteHtml(Main.SQL1, strSQL , Null, 0, True))
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub WebView1_OverrideUrl(Url As String) As Boolean
    ToastMessageShow(Url, False)
    Return True ' 不移至此URL
End Sub
