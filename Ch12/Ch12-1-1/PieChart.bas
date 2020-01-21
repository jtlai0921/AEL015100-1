Type=Activity
Version=2.7
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim Q1, Q2, Q3, Q4 As Int
End Sub

Sub Globals
    Dim pnlPie As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.Title = "顯示業績統計圖表"
    ' 新增Panel元件
    pnlPie.Initialize("")
    Activity.AddView(pnlPie, 5%x, 5%y, 90%x, 90%y)
    ' 初始PieData物件
    Dim PD As PieData
    PD.Initialize()
    PD.Target = pnlPie  ' 指定顯示的元件
    ' 新增項目
    Charts.AddPieItem(PD, "第一季: " & Q1 , Q1, 0)
    Charts.AddPieItem(PD, "第二季: " & Q2 , Q2, 0)
    Charts.AddPieItem(PD, "第三季: " & Q3 , Q3, 0)
    Charts.AddPieItem(PD, "第四季: " & Q4 , Q4, 0)
    ' 每一片的間隙, 值0為沒有間隙
    PD.GapDegrees = 10	
    ' 圖例的背景色彩
    PD.LegendBackColor = Colors.ARGB(150, 100, 120, 120)
    ' 繪出派圖, 傳回圖例的Bitmap物件
    Dim legend As Bitmap
    legend = Charts.DrawPie(PD, Colors.Gray, True)
    If legend.IsInitialized() Then  ' 有圖例
        ' 建立ImageView物件
        Dim ImageView1 As ImageView
        ImageView1.Initialize("")
        ImageView1.SetBackgroundImage(legend)	
        ' 在派圖新增圖例
        pnlPie.AddView(ImageView1, 2dip,0dip, legend.Width, legend.Height)
    End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


