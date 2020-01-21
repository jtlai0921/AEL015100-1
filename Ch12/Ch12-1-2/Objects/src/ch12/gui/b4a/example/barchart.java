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

public class barchart extends Activity implements B4AActivity{
	public static barchart mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "ch12.gui.b4a.example", "ch12.gui.b4a.example.barchart");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (barchart).");
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
		activityBA = new BA(this, layout, processBA, "ch12.gui.b4a.example", "ch12.gui.b4a.example.barchart");
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (barchart) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (barchart) Resume **");
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
		return barchart.class;
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
        BA.LogInfo("** Activity (barchart) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (barchart) Resume **");
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
public anywheresoftware.b4a.objects.PanelWrapper _pnlbars = null;
public ch12.gui.b4a.example.main _main = null;
public ch12.gui.b4a.example.charts _charts = null;
public ch12.gui.b4a.example.stackedbarchart _stackedbarchart = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
ch12.gui.b4a.example.charts._bardata _bd = null;
ch12.gui.b4a.example.charts._graph _g = null;
 //BA.debugLineNum = 16;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 17;BA.debugLine="Activity.Title = \"顯示長條圖\"";
mostCurrent._activity.setTitle((Object)("顯示長條圖"));
 //BA.debugLineNum = 19;BA.debugLine="pnlBars.Initialize(\"\")";
mostCurrent._pnlbars.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 20;BA.debugLine="Activity.AddView(pnlBars, 10%x, 10%y, 80%x, 80%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnlbars.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float)(10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float)(80),mostCurrent.activityBA));
 //BA.debugLineNum = 22;BA.debugLine="Dim BD As BarData";
_bd = new ch12.gui.b4a.example.charts._bardata();
 //BA.debugLineNum = 23;BA.debugLine="BD.Initialize()";
_bd.Initialize();
 //BA.debugLineNum = 24;BA.debugLine="BD.Target = pnlBars";
_bd.Target = mostCurrent._pnlbars;
 //BA.debugLineNum = 25;BA.debugLine="BD.BarsWidth = 15dip";
_bd.BarsWidth = anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(15));
 //BA.debugLineNum = 26;BA.debugLine="BD.Stacked = False  ' 不是堆疊長條圖";
_bd.Stacked = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 28;BA.debugLine="Charts.AddBarColor(BD, Colors.Red)";
mostCurrent._charts._addbarcolor(mostCurrent.activityBA,_bd,anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 29;BA.debugLine="Charts.AddBarColor(BD, Colors.Blue)";
mostCurrent._charts._addbarcolor(mostCurrent.activityBA,_bd,anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 30;BA.debugLine="Charts.AddBarColor(BD, Colors.Green)";
mostCurrent._charts._addbarcolor(mostCurrent.activityBA,_bd,anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 31;BA.debugLine="Charts.AddBarColor(BD, Colors.Yellow)";
mostCurrent._charts._addbarcolor(mostCurrent.activityBA,_bd,anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 33;BA.debugLine="Charts.AddBarPoint(BD, 2012, Main.S12)";
mostCurrent._charts._addbarpoint(mostCurrent.activityBA,_bd,BA.NumberToString(2012),mostCurrent._main._s12);
 //BA.debugLineNum = 34;BA.debugLine="Charts.AddBarPoint(BD, 2013, Main.S13)";
mostCurrent._charts._addbarpoint(mostCurrent.activityBA,_bd,BA.NumberToString(2013),mostCurrent._main._s13);
 //BA.debugLineNum = 36;BA.debugLine="Dim G As Graph";
_g = new ch12.gui.b4a.example.charts._graph();
 //BA.debugLineNum = 37;BA.debugLine="G.Initialize()";
_g.Initialize();
 //BA.debugLineNum = 38;BA.debugLine="G.Title = \"業績長條圖\"";
_g.Title = "業績長條圖";
 //BA.debugLineNum = 39;BA.debugLine="G.XAxis = \"年\"";
_g.XAxis = "年";
 //BA.debugLineNum = 40;BA.debugLine="G.YAxis = \"萬元\"";
_g.YAxis = "萬元";
 //BA.debugLineNum = 41;BA.debugLine="G.YStart = 0   ' Y軸最小值";
_g.YStart = (float)(0);
 //BA.debugLineNum = 42;BA.debugLine="G.YEnd = 800   ' Y軸最大值";
_g.YEnd = (float)(800);
 //BA.debugLineNum = 43;BA.debugLine="G.YInterval = 100  ' 增量";
_g.YInterval = (float)(100);
 //BA.debugLineNum = 44;BA.debugLine="G.AxisColor = Colors.Black";
_g.AxisColor = anywheresoftware.b4a.keywords.Common.Colors.Black;
 //BA.debugLineNum = 45;BA.debugLine="Charts.DrawBarsChart(G, BD, Colors.LightGray)";
mostCurrent._charts._drawbarschart(mostCurrent.activityBA,_g,_bd,anywheresoftware.b4a.keywords.Common.Colors.LightGray);
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim pnlBars As Panel";
mostCurrent._pnlbars = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
}
