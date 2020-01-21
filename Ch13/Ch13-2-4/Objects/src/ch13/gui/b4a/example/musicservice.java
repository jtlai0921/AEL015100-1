package ch13.gui.b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class musicservice extends android.app.Service {
	public static class musicservice_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, musicservice.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static musicservice mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return musicservice.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "ch13.gui.b4a.example", "ch13.gui.b4a.example.musicservice");
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Service (musicservice) Create **");
        processBA.raiseEvent(null, "service_create");
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		handleStart(intent);
    }
    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
    	handleStart(intent);
		return android.app.Service.START_NOT_STICKY;
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (musicservice) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = new anywheresoftware.b4a.objects.IntentWrapper();
    			if (intent != null) {
    				if (intent.hasExtra("b4a_internal_intent"))
    					iw.setObject((android.content.Intent) intent.getParcelableExtra("b4a_internal_intent"));
    				else
    					iw.setObject(intent);
    			}
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
        BA.LogInfo("** Service (musicservice) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.MediaPlayerWrapper _mp = null;
public static long _duration = 0L;
public static long _position = 0L;
public static anywheresoftware.b4a.objects.Timer _tmrmedia = null;
public ch13.gui.b4a.example.main _main = null;
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 6;BA.debugLine="Dim MP As MediaPlayer";
_mp = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 7;BA.debugLine="Dim Duration, Position As Long";
_duration = 0L;
_position = 0L;
 //BA.debugLineNum = 8;BA.debugLine="Dim tmrMedia As Timer";
_tmrmedia = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 9;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
anywheresoftware.b4a.objects.NotificationWrapper _n = null;
 //BA.debugLineNum = 11;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 12;BA.debugLine="MP.Initialize()";
_mp.Initialize();
 //BA.debugLineNum = 13;BA.debugLine="MP.Load(File.DirAssets, \"IsawHerStandingThere.mid\")";
_mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"IsawHerStandingThere.mid");
 //BA.debugLineNum = 14;BA.debugLine="MP.Looping = True";
_mp.setLooping(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 15;BA.debugLine="tmrMedia.Initialize(\"tmrMedia\", 1000)";
_tmrmedia.Initialize(processBA,"tmrMedia",(long)(1000));
 //BA.debugLineNum = 16;BA.debugLine="Dim n As Notification";
_n = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 17;BA.debugLine="n.Initialize()";
_n.Initialize();
 //BA.debugLineNum = 18;BA.debugLine="n.Icon = \"icon\"";
_n.setIcon("icon");
 //BA.debugLineNum = 19;BA.debugLine="n.AutoCancel = True";
_n.setAutoCancel(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 20;BA.debugLine="n.SetInfo(\"音樂服務\", \"停止音樂服務...\", Main)";
_n.SetInfo(processBA,"音樂服務","停止音樂服務...",(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 21;BA.debugLine="n.Notify(1)";
_n.Notify((int)(1));
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 30;BA.debugLine="MP.Stop()";
_mp.Stop();
 //BA.debugLineNum = 31;BA.debugLine="tmrMedia.Enabled = False";
_tmrmedia.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Service_Start(StartingIntent As Intent)";
 //BA.debugLineNum = 25;BA.debugLine="MP.Play()      ' 播放";
_mp.Play();
 //BA.debugLineNum = 26;BA.debugLine="tmrMedia.Enabled = True  ' 啟動計時器";
_tmrmedia.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _tmrmedia_tick() throws Exception{
 //BA.debugLineNum = 34;BA.debugLine="Sub tmrMedia_Tick";
 //BA.debugLineNum = 35;BA.debugLine="If MP.IsPlaying() Then  ' 更新位置資訊";
if (_mp.IsPlaying()) { 
 //BA.debugLineNum = 36;BA.debugLine="Position = MP.Position";
_position = (long)(_mp.getPosition());
 //BA.debugLineNum = 37;BA.debugLine="Duration = MP.Duration";
_duration = (long)(_mp.getDuration());
 //BA.debugLineNum = 38;BA.debugLine="CallSub(Main, \"RefreshData\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._main.getObject()),"RefreshData");
 };
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
return "";
}
}
