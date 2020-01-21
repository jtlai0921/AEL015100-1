package ch15.gui.b4a.example;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "ch15.gui.b4a.example", "ch15.gui.b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "ch15.gui.b4a.example", "ch15.gui.b4a.example.main");
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true)
				return true;
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.http.HttpClientWrapper _hc = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnstock = null;
public anywheresoftware.b4a.objects.collections.Map _stocks = null;
public static String _urlstring = "";
public static String _stockname = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblc = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcp = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblid = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbll = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbll_cur = null;
public anywheresoftware.b4a.objects.LabelWrapper _lble = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
int _i = 0;
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 34;BA.debugLine="Activity.Title = \"查詢ADR股票資訊\"";
mostCurrent._activity.setTitle((Object)("查詢ADR股票資訊"));
 //BA.debugLineNum = 35;BA.debugLine="hc.Initialize(\"Http\")";
_hc.Initialize("Http");
 //BA.debugLineNum = 36;BA.debugLine="Stocks.Initialize()  ' 初始Map物件";
mostCurrent._stocks.Initialize();
 //BA.debugLineNum = 37;BA.debugLine="urlString = \"http://finance.google.com/finance/info?client=ig&q=\"";
mostCurrent._urlstring = "http://finance.google.com/finance/info?client=ig&q=";
 //BA.debugLineNum = 38;BA.debugLine="stockName = \"NYSE:TSM\"";
mostCurrent._stockname = "NYSE:TSM";
 //BA.debugLineNum = 39;BA.debugLine="Stocks.Put(\"NYSE:TSM\",\"台積電ADR\")";
mostCurrent._stocks.Put((Object)("NYSE:TSM"),(Object)("台積電ADR"));
 //BA.debugLineNum = 40;BA.debugLine="Stocks.Put(\"NYSE:UMC\",\"聯電ADR\")";
mostCurrent._stocks.Put((Object)("NYSE:UMC"),(Object)("聯電ADR"));
 //BA.debugLineNum = 41;BA.debugLine="Stocks.Put(\"NYSE:ASX\",\"日月光ADR\")";
mostCurrent._stocks.Put((Object)("NYSE:ASX"),(Object)("日月光ADR"));
 //BA.debugLineNum = 42;BA.debugLine="Stocks.Put(\"NYSE:AUO\",\"友達ADR\")";
mostCurrent._stocks.Put((Object)("NYSE:AUO"),(Object)("友達ADR"));
 //BA.debugLineNum = 43;BA.debugLine="Stocks.Put(\"NYSE:CHT\",\"中華電信ADR\")";
mostCurrent._stocks.Put((Object)("NYSE:CHT"),(Object)("中華電信ADR"));
 //BA.debugLineNum = 44;BA.debugLine="Stocks.Put(\"NASD:SPIL\",\"矽品ADR\")";
mostCurrent._stocks.Put((Object)("NASD:SPIL"),(Object)("矽品ADR"));
 //BA.debugLineNum = 45;BA.debugLine="For i = 0 To Stocks.Size - 1";
{
final double step28 = 1;
final double limit28 = (int)(mostCurrent._stocks.getSize()-1);
for (_i = (int)(0); (step28 > 0 && _i <= limit28) || (step28 < 0 && _i >= limit28); _i += step28) {
 //BA.debugLineNum = 46;BA.debugLine="spnStock.Add(Stocks.GetValueAt(i))";
mostCurrent._spnstock.Add(String.valueOf(mostCurrent._stocks.GetValueAt(_i)));
 }
};
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper _req = null;
 //BA.debugLineNum = 58;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 59;BA.debugLine="Dim req As HttpRequest";
_req = new anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper();
 //BA.debugLineNum = 60;BA.debugLine="req.InitializeGet(urlString & stockName)";
_req.InitializeGet(mostCurrent._urlstring+mostCurrent._stockname);
 //BA.debugLineNum = 61;BA.debugLine="hc.Execute(req, 1)";
_hc.Execute(processBA,_req,(int)(1));
 //BA.debugLineNum = 62;BA.debugLine="ProgressDialogShow(\"取得資訊中...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"取得資訊中...");
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (processGlobalsRun == false) {
	    processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim spnStock As Spinner";
mostCurrent._spnstock = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim Stocks As Map";
mostCurrent._stocks = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 22;BA.debugLine="Dim urlString As String";
mostCurrent._urlstring = "";
 //BA.debugLineNum = 23;BA.debugLine="Dim stockName As String";
mostCurrent._stockname = "";
 //BA.debugLineNum = 24;BA.debugLine="Dim lblC As Label";
mostCurrent._lblc = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblCp As Label";
mostCurrent._lblcp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim lblId As Label";
mostCurrent._lblid = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim lblL As Label";
mostCurrent._lbll = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim lblL_cur As Label";
mostCurrent._lbll_cur = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim lblE As Label";
mostCurrent._lble = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _http_responseerror(String _reason,int _statuscode,int _taskid) throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub Http_ResponseError(Reason As String, StatusCode As Int, TaskId As Int)";
 //BA.debugLineNum = 90;BA.debugLine="ToastMessageShow(\"錯誤: 編號: \" & TaskId & \", 原因: \" & Reason & \", 狀態碼: \" & StatusCode, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("錯誤: 編號: "+BA.NumberToString(_taskid)+", 原因: "+_reason+", 狀態碼: "+BA.NumberToString(_statuscode),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 91;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _http_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _taskid) throws Exception{
String _rstr = "";
anywheresoftware.b4a.objects.collections.JSONParser _json = null;
anywheresoftware.b4a.objects.collections.Map _stockmap = null;
 //BA.debugLineNum = 72;BA.debugLine="Sub Http_ResponseSuccess(Response As HttpResponse, TaskId As Int)";
 //BA.debugLineNum = 73;BA.debugLine="Dim rStr As String";
_rstr = "";
 //BA.debugLineNum = 74;BA.debugLine="rStr = Response.GetString(\"UTF8\")  ' 取得傳回字串";
_rstr = _response.GetString("UTF8");
 //BA.debugLineNum = 75;BA.debugLine="Dim JSON As JSONParser";
_json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 76;BA.debugLine="JSON.Initialize(rStr)";
_json.Initialize(_rstr);
 //BA.debugLineNum = 77;BA.debugLine="Dim StockMap As Map";
_stockmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 79;BA.debugLine="StockMap = JSON.NextObject()";
_stockmap = _json.NextObject();
 //BA.debugLineNum = 80;BA.debugLine="lblE.Text = StockMap.Get(\"e\")";
mostCurrent._lble.setText(_stockmap.Get((Object)("e")));
 //BA.debugLineNum = 81;BA.debugLine="lblId.Text = StockMap.Get(\"id\")";
mostCurrent._lblid.setText(_stockmap.Get((Object)("id")));
 //BA.debugLineNum = 82;BA.debugLine="lblL.Text = StockMap.Get(\"l\")";
mostCurrent._lbll.setText(_stockmap.Get((Object)("l")));
 //BA.debugLineNum = 83;BA.debugLine="lblL_cur.Text = StockMap.Get(\"l_cur\")";
mostCurrent._lbll_cur.setText(_stockmap.Get((Object)("l_cur")));
 //BA.debugLineNum = 84;BA.debugLine="lblC.Text = StockMap.Get(\"c\")";
mostCurrent._lblc.setText(_stockmap.Get((Object)("c")));
 //BA.debugLineNum = 85;BA.debugLine="lblCp.Text = StockMap.Get(\"cp\")";
mostCurrent._lblcp.setText(_stockmap.Get((Object)("cp")));
 //BA.debugLineNum = 86;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim hc As HttpClient";
_hc = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _spnstock_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 65;BA.debugLine="Sub spnStock_ItemClick(Position As Int, Value As Object)";
 //BA.debugLineNum = 66;BA.debugLine="stockName = Stocks.GetKeyAt(Position)";
mostCurrent._stockname = String.valueOf(mostCurrent._stocks.GetKeyAt(_position));
 //BA.debugLineNum = 67;BA.debugLine="lblE.Text = \"\"   :   lblId.Text = \"\"";
mostCurrent._lble.setText((Object)(""));
 //BA.debugLineNum = 67;BA.debugLine="lblE.Text = \"\"   :   lblId.Text = \"\"";
mostCurrent._lblid.setText((Object)(""));
 //BA.debugLineNum = 68;BA.debugLine="lblL.Text = \"\"   :   lblL_cur.Text = \"\"";
mostCurrent._lbll.setText((Object)(""));
 //BA.debugLineNum = 68;BA.debugLine="lblL.Text = \"\"   :   lblL_cur.Text = \"\"";
mostCurrent._lbll_cur.setText((Object)(""));
 //BA.debugLineNum = 69;BA.debugLine="lblC.Text = \"\"   :   lblCp.Text = \"\"";
mostCurrent._lblc.setText((Object)(""));
 //BA.debugLineNum = 69;BA.debugLine="lblC.Text = \"\"   :   lblCp.Text = \"\"";
mostCurrent._lblcp.setText((Object)(""));
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
}
