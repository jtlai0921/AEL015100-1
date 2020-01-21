Type=Activity
Version=2.71
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim myPicture As Bitmap
End Sub

Sub Globals

End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.Title = "顯示照片"
End Sub

Sub Activity_Resume
    ' 是否有初始Bitmap物件
    If myPicture.IsInitialized() Then
        Dim bd As BitmapDrawable
        bd.Initialize(myPicture)
        bd.Gravity = Gravity.FILL
        ' 指定活動背景的Drawable物件
        Activity.Background = bd
    End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


