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
    Dim pnlLines As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.Title = "顯示折線圖"
    ' 建立Panel元件
    pnlLines.Initialize("")
    Activity.AddView(pnlLines, 10%x, 10%y, 80%x, 80%y)
    ' 初始LineData物件
    Dim LD As LineData	
    LD.Initialize()
    LD.Target = pnlLines
    ' 指定線的色彩, 
    Charts.AddLineColor(LD, Colors.Blue) 
    Charts.AddLineColor(LD, Colors.Green) 
    ' 新增折線的多個點座標 
    For i = 0 To 3
        Charts.AddLineMultiplePoints(LD, "Q" & (i+1), Array As Float(Main.S12(i), Main.S13(i)), True)
    Next 
    ' 建立Graph物件的折線圖參數
    Dim G As Graph
    G.Initialize()
    G.Title = "業績折線圖"
    G.XAxis = "季"
    G.YAxis = "萬元"
    G.YStart = 0   ' Y軸最小值
    G.YEnd = 800   ' Y軸最大值
    G.YInterval = 50  ' 增量
    G.AxisColor = Colors.Black
    Charts.DrawLineChart(G, LD, Colors.LightGray)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


