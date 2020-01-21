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
    Dim edtTitle As EditText
    Dim edtURL As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("AddURL")
    Activity.Title = "新增URL"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    Dim items As Map
    items.Initialize()
    If File.Exists(File.DirRootExternal,"mapItems.txt") Then 
        ' 從檔案讀取項目
        items = File.ReadMap(File.DirRootExternal, "mapItems.txt")
    End If
    ' 新增項目
    items.Put(edtTitle.Text, "http://" & edtURL.Text)
    ' 寫入項目至檔案
    File.WriteMap(File.DirRootExternal, "mapItems.txt", items)
    ToastMessageShow("新增項目: " & edtTitle.Text, True)
    edtTitle.Text = "" : edtURL.Text = ""
End Sub

Sub Button2_Click
    StartActivity(Main)	
End Sub