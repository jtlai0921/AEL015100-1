package ch13.gui.b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class timeservice extends android.app.Service {
	public static class timeservice_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, timeservice.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static timeservice mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return timeservice.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "ch13.gui.b4a.example", "ch13.gui.b4a.example.timeservice");
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
        BA.LogInfo("** Service (timeservice) Create **");
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
    	BA.LogInfo("** Service (timeservice) Start **");
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
        BA.LogInfo("** Service (timeservice) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static int _counter = 0;
public static boolean _isrun = false;
public ch13.gui.b4a.example.main _main = null;
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 6;BA.debugLine="Dim Counter As Int = 0";
_counter = (int)(0);
 //BA.debugLineNum = 7;BA.debugLine="Dim isRun As Boolean";
_isrun = false;
 //BA.debugLineNum = 8;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
anywheresoftware.b4a.objects.NotificationWrapper _n = null;
 //BA.debugLineNum = 10;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 11;BA.debugLine="Dim n As Notification";
_n = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 12;BA.debugLine="n.Initialize()";
_n.Initialize();
 //BA.debugLineNum = 13;BA.debugLine="n.Icon = \"icon\"";
_n.setIcon("icon");
 //BA.debugLineNum = 14;BA.debugLine="n.AutoCancel = True";
_n.setAutoCancel(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 15;BA.debugLine="n.SetInfo(\"計數器服務\", \"停止計數器服務...\", Main)";
_n.SetInfo(processBA,"計數器服務","停止計數器服務...",(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 16;BA.debugLine="n.Notify(1)";
_n.Notify((int)(1));
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Service_Start(StartingIntent As Intent)";
 //BA.debugLineNum = 20;BA.debugLine="Counter = Counter + 1";
_counter = (int)(_counter+1);
 //BA.debugLineNum = 21;BA.debugLine="If isRun Then";
if (_isrun) { 
 //BA.debugLineNum = 22;BA.debugLine="CallSub(Main, \"RefreshData\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._main.getObject()),"RefreshData");
 //BA.debugLineNum = 23;BA.debugLine="StartServiceAt(\"\", DateTime.Now + DateTime.TicksPerSecond, True)";
anywheresoftware.b4a.keywords.Common.StartServiceAt(processBA,(Object)(""),(long)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()+anywheresoftware.b4a.keywords.Common.DateTime.TicksPerSecond),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
}
