package ch08.gui.b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "ch08.gui.b4a.example", "ch08.gui.b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "ch08.gui.b4a.example", "ch08.gui.b4a.example.main");
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
public anywheresoftware.b4a.objects.LabelWrapper _lblselected1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblselected2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblselected3 = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 28;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 29;BA.debugLine="Activity.Title = \"團購點餐券\"";
mostCurrent._activity.setTitle((Object)("團購點餐券"));
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
String _out = "";
anywheresoftware.b4a.objects.collections.Map _items = null;
int _i = 0;
 //BA.debugLineNum = 40;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 41;BA.debugLine="Dim out As String = \"\"";
_out = "";
 //BA.debugLineNum = 42;BA.debugLine="Dim items As Map";
_items = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 43;BA.debugLine="items.Initialize()";
_items.Initialize();
 //BA.debugLineNum = 44;BA.debugLine="items.Put(\"原味披薩\", True)";
_items.Put((Object)("原味披薩"),(Object)(anywheresoftware.b4a.keywords.Common.True));
 //BA.debugLineNum = 45;BA.debugLine="items.Put(\"牛肉披薩\", False)";
_items.Put((Object)("牛肉披薩"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 46;BA.debugLine="items.Put(\"海鮮披薩\", True)";
_items.Put((Object)("海鮮披薩"),(Object)(anywheresoftware.b4a.keywords.Common.True));
 //BA.debugLineNum = 47;BA.debugLine="items.Put(\"素食披薩\", False)";
_items.Put((Object)("素食披薩"),(Object)(anywheresoftware.b4a.keywords.Common.False));
 //BA.debugLineNum = 48;BA.debugLine="InputMap(items, \"主餐四選二\")";
anywheresoftware.b4a.keywords.Common.InputMap(_items,"主餐四選二",mostCurrent.activityBA);
 //BA.debugLineNum = 49;BA.debugLine="For i = 0 To items.Size - 1";
{
final double step24 = 1;
final double limit24 = (int)(_items.getSize()-1);
for (_i = (int)(0); (step24 > 0 && _i <= limit24) || (step24 < 0 && _i >= limit24); _i += step24) {
 //BA.debugLineNum = 50;BA.debugLine="If items.GetValueAt(i) Then";
if (BA.ObjectToBoolean(_items.GetValueAt(_i))) { 
 //BA.debugLineNum = 51;BA.debugLine="out = out & items.GetKeyAt(i) & \" \"";
_out = _out+String.valueOf(_items.GetKeyAt(_i))+" ";
 };
 }
};
 //BA.debugLineNum = 54;BA.debugLine="lblSelected1.Text = out";
mostCurrent._lblselected1.setText((Object)(_out));
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
String _out = "";
anywheresoftware.b4a.objects.collections.List _items = null;
anywheresoftware.b4a.objects.collections.List _result = null;
int _i = 0;
 //BA.debugLineNum = 57;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 58;BA.debugLine="Dim out As String = \"\"";
_out = "";
 //BA.debugLineNum = 59;BA.debugLine="Dim items, result As List";
_items = new anywheresoftware.b4a.objects.collections.List();
_result = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 60;BA.debugLine="items.Initialize()";
_items.Initialize();
 //BA.debugLineNum = 61;BA.debugLine="items.Add(\"薯條\")";
_items.Add((Object)("薯條"));
 //BA.debugLineNum = 62;BA.debugLine="items.Add(\"地瓜球\")";
_items.Add((Object)("地瓜球"));
 //BA.debugLineNum = 63;BA.debugLine="items.Add(\"蘋果派\")";
_items.Add((Object)("蘋果派"));
 //BA.debugLineNum = 64;BA.debugLine="items.Add(\"四塊雞塊\")";
_items.Add((Object)("四塊雞塊"));
 //BA.debugLineNum = 65;BA.debugLine="result = InputMultiList(items, \"副餐四選二\")";
_result = anywheresoftware.b4a.keywords.Common.InputMultiList(_items,"副餐四選二",mostCurrent.activityBA);
 //BA.debugLineNum = 66;BA.debugLine="If result.Size <= 0 Then";
if (_result.getSize()<=0) { 
 //BA.debugLineNum = 67;BA.debugLine="out = \"使用者沒有選擇...\"";
_out = "使用者沒有選擇...";
 }else {
 //BA.debugLineNum = 69;BA.debugLine="For i = 0 To result.Size - 1";
{
final double step43 = 1;
final double limit43 = (int)(_result.getSize()-1);
for (_i = (int)(0); (step43 > 0 && _i <= limit43) || (step43 < 0 && _i >= limit43); _i += step43) {
 //BA.debugLineNum = 70;BA.debugLine="out = out & result.Get(i) & \" \"";
_out = _out+String.valueOf(_result.Get(_i))+" ";
 }
};
 };
 //BA.debugLineNum = 73;BA.debugLine="lblSelected2.Text = out";
mostCurrent._lblselected2.setText((Object)(_out));
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
String _out = "";
int _index = 0;
anywheresoftware.b4a.objects.collections.List _items = null;
 //BA.debugLineNum = 76;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 77;BA.debugLine="Dim out As String = \"\"";
_out = "";
 //BA.debugLineNum = 78;BA.debugLine="Dim index As Int";
_index = 0;
 //BA.debugLineNum = 79;BA.debugLine="Dim items As List";
_items = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 80;BA.debugLine="items.Initialize()";
_items.Initialize();
 //BA.debugLineNum = 81;BA.debugLine="items.Add(\"可樂\")";
_items.Add((Object)("可樂"));
 //BA.debugLineNum = 82;BA.debugLine="items.Add(\"紅茶\")";
_items.Add((Object)("紅茶"));
 //BA.debugLineNum = 83;BA.debugLine="items.Add(\"綠茶\")";
_items.Add((Object)("綠茶"));
 //BA.debugLineNum = 84;BA.debugLine="items.Add(\"咖啡\")";
_items.Add((Object)("咖啡"));
 //BA.debugLineNum = 85;BA.debugLine="index = InputList(items, \"飲料四選一\", 1)";
_index = anywheresoftware.b4a.keywords.Common.InputList(_items,"飲料四選一",(int)(1),mostCurrent.activityBA);
 //BA.debugLineNum = 86;BA.debugLine="If index = DialogResponse.CANCEL Then";
if (_index==anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
 //BA.debugLineNum = 87;BA.debugLine="out = \"使用者沒有選擇...\"";
_out = "使用者沒有選擇...";
 }else {
 //BA.debugLineNum = 89;BA.debugLine="out = index";
_out = BA.NumberToString(_index);
 };
 //BA.debugLineNum = 91;BA.debugLine="lblSelected3.Text = out";
mostCurrent._lblselected3.setText((Object)(_out));
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim lblSelected1 As Label";
mostCurrent._lblselected1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblSelected2 As Label";
mostCurrent._lblselected2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblSelected3 As Label";
mostCurrent._lblselected3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
