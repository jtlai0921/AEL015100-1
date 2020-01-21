Type=Service
Version=2.7
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
    Dim MP As MediaPlayer
    Dim Duration, Position As Long
    Dim tmrMedia As Timer
End Sub

Sub Service_Create
    MP.Initialize()
    MP.Load(File.DirAssets, "IsawHerStandingThere.mid")
    MP.Looping = True
	tmrMedia.Initialize("tmrMedia", 1000)
    Dim n As Notification
    n.Initialize()
    n.Icon = "icon"
    n.AutoCancel = True
    n.SetInfo("音樂服務", "停止音樂服務...", Main)
    n.Notify(1)
End Sub

Sub Service_Start(StartingIntent As Intent)
    MP.Play()      ' 播放
    tmrMedia.Enabled = True  ' 啟動計時器
End Sub

Sub Service_Destroy
    MP.Stop()
    tmrMedia.Enabled = False
End Sub

Sub tmrMedia_Tick
    If MP.IsPlaying() Then  ' 更新位置資訊
       Position = MP.Position
	   Duration = MP.Duration
       CallSub(Main, "RefreshData")
    End If
End Sub

