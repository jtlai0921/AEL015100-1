package ch12.gui.b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "ch12.gui.b4a.example", "ch12.gui.b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "ch12.gui.b4a.example", "ch12.gui.b4a.example.main");
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
public static float[] _s12 = null;
public static float[] _s13 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt12q1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt12q2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt12q3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt12q4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt13q1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt13q2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt13q3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt13q4 = null;
public ch12.gui.b4a.example.charts _charts = null;
public ch12.gui.b4a.example.barchart _barchart = null;
public ch12.gui.b4a.example.stackedbarchart _stackedbarchart = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Activity.Title = \"長條圖\"";
mostCurrent._activity.setTitle((Object)("長條圖"));
 //BA.debugLineNum = 34;BA.debugLine="edt12Q1.Text = \"234\"";
mostCurrent._edt12q1.setText((Object)("234"));
 //BA.debugLineNum = 35;BA.debugLine="edt12Q2.Text = \"256\"";
mostCurrent._edt12q2.setText((Object)("256"));
 //BA.debugLineNum = 36;BA.debugLine="edt12Q3.Text = \"453\"";
mostCurrent._edt12q3.setText((Object)("453"));
 //BA.debugLineNum = 37;BA.debugLine="edt12Q4.Text = \"512\"";
mostCurrent._edt12q4.setText((Object)("512"));
 //BA.debugLineNum = 38;BA.debugLine="edt13Q1.Text = \"150\"";
mostCurrent._edt13q1.setText((Object)("150"));
 //BA.debugLineNum = 39;BA.debugLine="edt13Q2.Text = \"211\"";
mostCurrent._edt13q2.setText((Object)("211"));
 //BA.debugLineNum = 40;BA.debugLine="edt13Q3.Text = \"325\"";
mostCurrent._edt13q3.setText((Object)("325"));
 //BA.debugLineNum = 41;BA.debugLine="edt13Q4.Text = \"413\"";
mostCurrent._edt13q4.setText((Object)("413"));
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 64;BA.debugLine="GetSalesData";
_getsalesdata();
 //BA.debugLineNum = 65;BA.debugLine="StartActivity(BarChart)  ' 啟動BarChart活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._barchart.getObject()));
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 68;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 69;BA.debugLine="GetSalesData";
_getsalesdata();
 //BA.debugLineNum = 70;BA.debugLine="StartActivity(StackedBarChart)  ' 啟動StackedBarChart活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._stackedbarchart.getObject()));
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _getsalesdata() throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub GetSalesData";
 //BA.debugLineNum = 53;BA.debugLine="S12(0) = edt12Q1.Text";
_s12[(int)(0)] = (float)(Double.parseDouble(mostCurrent._edt12q1.getText()));
 //BA.debugLineNum = 54;BA.debugLine="S12(1) = edt12Q2.Text";
_s12[(int)(1)] = (float)(Double.parseDouble(mostCurrent._edt12q2.getText()));
 //BA.debugLineNum = 55;BA.debugLine="S12(2) = edt12Q3.Text";
_s12[(int)(2)] = (float)(Double.parseDouble(mostCurrent._edt12q3.getText()));
 //BA.debugLineNum = 56;BA.debugLine="S12(3) = edt12Q4.Text";
_s12[(int)(3)] = (float)(Double.parseDouble(mostCurrent._edt12q4.getText()));
 //BA.debugLineNum = 57;BA.debugLine="S13(0) = edt13Q1.Text";
_s13[(int)(0)] = (float)(Double.parseDouble(mostCurrent._edt13q1.getText()));
 //BA.debugLineNum = 58;BA.debugLine="S13(1) = edt13Q2.Text";
_s13[(int)(1)] = (float)(Double.parseDouble(mostCurrent._edt13q2.getText()));
 //BA.debugLineNum = 59;BA.debugLine="S13(2) = edt13Q3.Text";
_s13[(int)(2)] = (float)(Double.parseDouble(mostCurrent._edt13q3.getText()));
 //BA.debugLineNum = 60;BA.debugLine="S13(3) = edt13Q4.Text";
_s13[(int)(3)] = (float)(Double.parseDouble(mostCurrent._edt13q4.getText()));
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (processGlobalsRun == false) {
	    processGlobalsRun = true;
		try {
		        main._process_globals();
charts._process_globals();
barchart._process_globals();
stackedbarchart._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (barchart.mostCurrent != null);
vis = vis | (stackedbarchart.mostCurrent != null);
return vis;}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim edt12Q1 As EditText";
mostCurrent._edt12q1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim edt12Q2 As EditText";
mostCurrent._edt12q2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim edt12Q3 As EditText";
mostCurrent._edt12q3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim edt12Q4 As EditText";
mostCurrent._edt12q4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim edt13Q1 As EditText";
mostCurrent._edt13q1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim edt13Q2 As EditText";
mostCurrent._edt13q2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim edt13Q3 As EditText";
mostCurrent._edt13q3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim edt13Q4 As EditText";
mostCurrent._edt13q4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim S12(4) As Float";
_s12 = new float[(int)(4)];
;
 //BA.debugLineNum = 17;BA.debugLine="Dim S13(4) As Float";
_s13 = new float[(int)(4)];
;
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
