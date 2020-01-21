Type=Service
Version=2.7
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
    Dim Counter As Int = 0
    Dim isRun As Boolean
End Sub

Sub Service_Create
    Dim n As Notification
    n.Initialize()
    n.Icon = "icon"
    n.AutoCancel = True
    n.SetInfo("計數器服務", "停止計數器服務...", Main)
    n.Notify(1)
End Sub

Sub Service_Start(StartingIntent As Intent)
    Counter = Counter + 1
    If isRun Then
       CallSub(Main, "RefreshData")
        StartServiceAt("", DateTime.Now + DateTime.TicksPerSecond, True)
    End If
End Sub

Sub Service_Destroy

End Sub
