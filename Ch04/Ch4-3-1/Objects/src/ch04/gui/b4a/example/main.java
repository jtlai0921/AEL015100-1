package ch04.gui.b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "ch04.gui.b4a.example", "ch04.gui.b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "ch04.gui.b4a.example", "ch04.gui.b4a.example.main");
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
public anywheresoftware.b4a.objects.LabelWrapper _lbloutput = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 30;BA.debugLine="Activity.Title = \"B4A內建物件\"";
mostCurrent._activity.setTitle((Object)("B4A內建物件"));
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
String _out = "";
anywheresoftware.b4a.objects.collections.List _grades1 = null;
anywheresoftware.b4a.objects.collections.List _grades2 = null;
int _i = 0;
 //BA.debugLineNum = 41;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 42;BA.debugLine="Dim out = \"\" As String";
_out = "";
 //BA.debugLineNum = 43;BA.debugLine="Dim Grades1, Grades2 As List";
_grades1 = new anywheresoftware.b4a.objects.collections.List();
_grades2 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 44;BA.debugLine="Grades1.Initialize()  ' 初始List物件";
_grades1.Initialize();
 //BA.debugLineNum = 45;BA.debugLine="Grades2.Initialize2(Array As Int(56, 78))";
_grades2.Initialize2(anywheresoftware.b4a.keywords.Common.ArrayToList(new int[]{(int)(56),(int)(78)}));
 //BA.debugLineNum = 47;BA.debugLine="If Grades1.IsInitialized() Then ' 已初始";
if (_grades1.IsInitialized()) { 
 //BA.debugLineNum = 48;BA.debugLine="Grades1.Add(87)";
_grades1.Add((Object)(87));
 //BA.debugLineNum = 49;BA.debugLine="Grades1.AddAll(Grades2)";
_grades1.AddAll(_grades2);
 //BA.debugLineNum = 50;BA.debugLine="Grades1.AddAllAt(1, Array As Int(89, 71))";
_grades1.AddAllAt((int)(1),anywheresoftware.b4a.keywords.Common.ArrayToList(new int[]{(int)(89),(int)(71)}));
 };
 //BA.debugLineNum = 52;BA.debugLine="For i = 0 To Grades1.Size - 1";
{
final double step23 = 1;
final double limit23 = (int)(_grades1.getSize()-1);
for (_i = (int)(0); (step23 > 0 && _i <= limit23) || (step23 < 0 && _i >= limit23); _i += step23) {
 //BA.debugLineNum = 53;BA.debugLine="out = out & Grades1.Get(i) & \" \"";
_out = _out+String.valueOf(_grades1.Get(_i))+" ";
 }
};
 //BA.debugLineNum = 55;BA.debugLine="out = out & CRLF";
_out = _out+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 56;BA.debugLine="Grades1.InsertAt(2, 66) ' 插入元素";
_grades1.InsertAt((int)(2),(Object)(66));
 //BA.debugLineNum = 57;BA.debugLine="Grades1.RemoveAt(4)     ' 刪除元素";
_grades1.RemoveAt((int)(4));
 //BA.debugLineNum = 58;BA.debugLine="Grades1.Set(3, 99)      ' 取代元素";
_grades1.Set((int)(3),(Object)(99));
 //BA.debugLineNum = 59;BA.debugLine="For i = 0 To Grades1.Size - 1";
{
final double step30 = 1;
final double limit30 = (int)(_grades1.getSize()-1);
for (_i = (int)(0); (step30 > 0 && _i <= limit30) || (step30 < 0 && _i >= limit30); _i += step30) {
 //BA.debugLineNum = 60;BA.debugLine="out = out & Grades1.Get(i) & \" \"";
_out = _out+String.valueOf(_grades1.Get(_i))+" ";
 }
};
 //BA.debugLineNum = 62;BA.debugLine="out = out & CRLF";
_out = _out+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 63;BA.debugLine="If Grades1.IndexOf(66) <> -1 Then ' 搜尋";
if (_grades1.IndexOf((Object)(66))!=-1) { 
 //BA.debugLineNum = 64;BA.debugLine="out = out & \"找到66\" & CRLF";
_out = _out+"找到66"+anywheresoftware.b4a.keywords.Common.CRLF;
 };
 //BA.debugLineNum = 66;BA.debugLine="Grades1.Sort(True)  	' 排序";
_grades1.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 67;BA.debugLine="For i = 0 To Grades1.Size - 1";
{
final double step38 = 1;
final double limit38 = (int)(_grades1.getSize()-1);
for (_i = (int)(0); (step38 > 0 && _i <= limit38) || (step38 < 0 && _i >= limit38); _i += step38) {
 //BA.debugLineNum = 68;BA.debugLine="out = out & Grades1.Get(i) & \" \"";
_out = _out+String.valueOf(_grades1.Get(_i))+" ";
 }
};
 //BA.debugLineNum = 70;BA.debugLine="lblOutput.Text = out";
mostCurrent._lbloutput.setText((Object)(_out));
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 24;BA.debugLine="Dim lblOutput As Label";
mostCurrent._lbloutput = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
