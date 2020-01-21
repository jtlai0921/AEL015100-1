Type=Service
Version=2.7
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
    Dim rv As RemoteViews
End Sub

Sub Service_Create
    ' 指定更新周期為12小時, 即720分
    rv = ConfigureHomeWidget("DateWidget", "rv", 720, "日期小工具")
End Sub

Sub Service_Start (StartingIntent As Intent)
    If rv.HandleWidgetEvents(StartingIntent) Then Return
End Sub

Sub rv_RequestUpdate
    SetToday
    rv.UpdateWidget
End Sub

Sub rv_Disabled
    StopService("")
End Sub

Sub Service_Destroy

End Sub

Sub SetToday
    rv.SetText("lblMonth", DateTime.GetMonth(DateTime.Now) & "月")
    rv.SetText("lblDate", DateTime.GetDayOfMonth(DateTime.Now))
End Sub

