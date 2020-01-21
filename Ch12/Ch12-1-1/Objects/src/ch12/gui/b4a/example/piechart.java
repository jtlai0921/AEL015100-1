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

public class piechart extends Activity implements B4AActivity{
	public static piechart mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "ch12.gui.b4a.example", "ch12.gui.b4a.example.piechart");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (piechart).");
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
		activityBA = new BA(this, layout, processBA, "ch12.gui.b4a.example", "ch12.gui.b4a.example.piechart");
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (piechart) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (piechart) Resume **");
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
		return piechart.class;
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
        BA.LogInfo("** Activity (piechart) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (piechart) Resume **");
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
public static int _q1 = 0;
public static int _q2 = 0;
public static int _q3 = 0;
public static int _q4 = 0;
public anywheresoftware.b4a.objects.PanelWrapper _pnlpie = null;
public ch12.gui.b4a.example.main _main = null;
public ch12.gui.b4a.example.charts _charts = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
ch12.gui.b4a.example.charts._piedata _pd = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _legend = null;
anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
 //BA.debugLineNum = 14;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 15;BA.debugLine="Activity.Title = \"顯示業績統計圖表\"";
mostCurrent._activity.setTitle((Object)("顯示業績統計圖表"));
 //BA.debugLineNum = 17;BA.debugLine="pnlPie.Initialize(\"\")";
mostCurrent._pnlpie.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 18;BA.debugLine="Activity.AddView(pnlPie, 5%x, 5%y, 90%x, 90%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._pnlpie.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float)(5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float)(90),mostCurrent.activityBA));
 //BA.debugLineNum = 20;BA.debugLine="Dim PD As PieData";
_pd = new ch12.gui.b4a.example.charts._piedata();
 //BA.debugLineNum = 21;BA.debugLine="PD.Initialize()";
_pd.Initialize();
 //BA.debugLineNum = 22;BA.debugLine="PD.Target = pnlPie  ' 指定顯示的元件";
_pd.Target = mostCurrent._pnlpie;
 //BA.debugLineNum = 24;BA.debugLine="Charts.AddPieItem(PD, \"第一季: \" & Q1 , Q1, 0)";
mostCurrent._charts._addpieitem(mostCurrent.activityBA,_pd,"第一季: "+BA.NumberToString(_q1),(float)(_q1),(int)(0));
 //BA.debugLineNum = 25;BA.debugLine="Charts.AddPieItem(PD, \"第二季: \" & Q2 , Q2, 0)";
mostCurrent._charts._addpieitem(mostCurrent.activityBA,_pd,"第二季: "+BA.NumberToString(_q2),(float)(_q2),(int)(0));
 //BA.debugLineNum = 26;BA.debugLine="Charts.AddPieItem(PD, \"第三季: \" & Q3 , Q3, 0)";
mostCurrent._charts._addpieitem(mostCurrent.activityBA,_pd,"第三季: "+BA.NumberToString(_q3),(float)(_q3),(int)(0));
 //BA.debugLineNum = 27;BA.debugLine="Charts.AddPieItem(PD, \"第四季: \" & Q4 , Q4, 0)";
mostCurrent._charts._addpieitem(mostCurrent.activityBA,_pd,"第四季: "+BA.NumberToString(_q4),(float)(_q4),(int)(0));
 //BA.debugLineNum = 29;BA.debugLine="PD.GapDegrees = 10";
_pd.GapDegrees = (int)(10);
 //BA.debugLineNum = 31;BA.debugLine="PD.LegendBackColor = Colors.ARGB(150, 100, 120, 120)";
_pd.LegendBackColor = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int)(150),(int)(100),(int)(120),(int)(120));
 //BA.debugLineNum = 33;BA.debugLine="Dim legend As Bitmap";
_legend = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 34;BA.debugLine="legend = Charts.DrawPie(PD, Colors.Gray, True)";
_legend = mostCurrent._charts._drawpie(mostCurrent.activityBA,_pd,anywheresoftware.b4a.keywords.Common.Colors.Gray,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 35;BA.debugLine="If legend.IsInitialized() Then  ' 有圖例";
if (_legend.IsInitialized()) { 
 //BA.debugLineNum = 37;BA.debugLine="Dim ImageView1 As ImageView";
_imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 38;BA.debugLine="ImageView1.Initialize(\"\")";
_imageview1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 39;BA.debugLine="ImageView1.SetBackgroundImage(legend)";
_imageview1.SetBackgroundImage((android.graphics.Bitmap)(_legend.getObject()));
 //BA.debugLineNum = 41;BA.debugLine="pnlPie.AddView(ImageView1, 2dip,0dip, legend.Width, legend.Height)";
mostCurrent._pnlpie.AddView((android.view.View)(_imageview1.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(0)),_legend.getWidth(),_legend.getHeight());
 };
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 10;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 11;BA.debugLine="Dim pnlPie As Panel";
mostCurrent._pnlpie = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim Q1, Q2, Q3, Q4 As Int";
_q1 = 0;
_q2 = 0;
_q3 = 0;
_q4 = 0;
 //BA.debugLineNum = 8;BA.debugLine="End Sub";
return "";
}
}
