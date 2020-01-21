package ch11.gui.b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "ch11.gui.b4a.example", "ch11.gui.b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "ch11.gui.b4a.example", "ch11.gui.b4a.example.main");
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
public anywheresoftware.b4a.objects.PanelWrapper _pnlgraph = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cvsmain = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cvsgraph = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 27;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 28;BA.debugLine="Activity.Title = \"基本繪圖功能\"";
mostCurrent._activity.setTitle((Object)("基本繪圖功能"));
 //BA.debugLineNum = 30;BA.debugLine="cvsMain.Initialize(Activity)";
mostCurrent._cvsmain.Initialize((android.view.View)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 31;BA.debugLine="cvsGraph.Initialize(pnlGraph)";
mostCurrent._cvsgraph.Initialize((android.view.View)(mostCurrent._pnlgraph.getObject()));
 //BA.debugLineNum = 32;BA.debugLine="cvsGraph.DrawColor(Colors.White) ' 填滿背景色彩";
mostCurrent._cvsgraph.DrawColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect1 = null;
float _centerx = 0f;
float _centery = 0f;
float _radius = 0f;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect2 = null;
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 37;BA.debugLine="cvsMain.DrawLine(20dip, 20dip, 160dip, 20dip, Colors.Red, 3dip)";
mostCurrent._cvsmain.DrawLine((float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(160))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),anywheresoftware.b4a.keywords.Common.Colors.Red,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 38;BA.debugLine="cvsGraph.DrawLine(20dip, 20dip, 160dip, 20dip, Colors.Blue, 3dip)";
mostCurrent._cvsgraph.DrawLine((float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(160))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),anywheresoftware.b4a.keywords.Common.Colors.Blue,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 40;BA.debugLine="Dim rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 41;BA.debugLine="rect1.Initialize(20dip, 40dip, 150dip, 100dip)";
_rect1.Initialize(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(150)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(100)));
 //BA.debugLineNum = 42;BA.debugLine="cvsMain.DrawRect(rect1, Colors.Red, False, 3dip)";
mostCurrent._cvsmain.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Red,anywheresoftware.b4a.keywords.Common.False,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 43;BA.debugLine="cvsGraph.DrawRect(rect1, Colors.Blue, True, 3dip)";
mostCurrent._cvsgraph.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Blue,anywheresoftware.b4a.keywords.Common.True,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 45;BA.debugLine="cvsMain.DrawCircle(220dip, 70dip, 30dip, Colors.Red, False, 3dip)";
mostCurrent._cvsmain.DrawCircle((float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(220))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(70))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(30))),anywheresoftware.b4a.keywords.Common.Colors.Red,anywheresoftware.b4a.keywords.Common.False,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 46;BA.debugLine="Dim centerX, centerY, radius As Float";
_centerx = 0f;
_centery = 0f;
_radius = 0f;
 //BA.debugLineNum = 47;BA.debugLine="centerX = 220dip";
_centerx = (float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(220)));
 //BA.debugLineNum = 48;BA.debugLine="centerY = 70dip";
_centery = (float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(70)));
 //BA.debugLineNum = 49;BA.debugLine="radius = 30dip";
_radius = (float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(30)));
 //BA.debugLineNum = 50;BA.debugLine="cvsGraph.DrawCircle(centerX, centerY, radius, Colors.Blue, True, 3dip)";
mostCurrent._cvsgraph.DrawCircle(_centerx,_centery,_radius,anywheresoftware.b4a.keywords.Common.Colors.Blue,anywheresoftware.b4a.keywords.Common.True,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 51;BA.debugLine="cvsGraph.DrawCircle(centerX, centerY, radius, Colors.Red, False, 3dip)";
mostCurrent._cvsgraph.DrawCircle(_centerx,_centery,_radius,anywheresoftware.b4a.keywords.Common.Colors.Red,anywheresoftware.b4a.keywords.Common.False,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(3))));
 //BA.debugLineNum = 53;BA.debugLine="Dim rect2 As Rect";
_rect2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 54;BA.debugLine="rect2.Initialize(20dip, 120dip, 150dip, 190dip)";
_rect2.Initialize(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(120)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(150)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190)));
 //BA.debugLineNum = 55;BA.debugLine="cvsMain.DrawOval(rect2, Colors.Red, False, 5dip)";
mostCurrent._cvsmain.DrawOval((android.graphics.Rect)(_rect2.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Red,anywheresoftware.b4a.keywords.Common.False,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5))));
 //BA.debugLineNum = 56;BA.debugLine="cvsGraph.DrawOval(rect2, Colors.Blue, True, 5dip)";
mostCurrent._cvsgraph.DrawOval((android.graphics.Rect)(_rect2.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Blue,anywheresoftware.b4a.keywords.Common.True,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5))));
 //BA.debugLineNum = 58;BA.debugLine="cvsMain.DrawText(\"B4A繪圖\", 190dip, 150dip, Typeface.DEFAULT, 20, Colors.Red, \"LEFT\")";
mostCurrent._cvsmain.DrawText(mostCurrent.activityBA,"B4A繪圖",(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(150))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT,(float)(20),anywheresoftware.b4a.keywords.Common.Colors.Red,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 59;BA.debugLine="cvsGraph.DrawText(\"B4A繪圖\", 190dip, 150dip, Typeface.DEFAULT, 20, Colors.Blue, \"LEFT\")";
mostCurrent._cvsgraph.DrawText(mostCurrent.activityBA,"B4A繪圖",(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(150))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT,(float)(20),anywheresoftware.b4a.keywords.Common.Colors.Blue,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 //BA.debugLineNum = 60;BA.debugLine="Activity.Invalidate() ' 更新介面";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 61;BA.debugLine="pnlGraph.Invalidate()";
mostCurrent._pnlgraph.Invalidate();
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 22;BA.debugLine="Dim pnlGraph As Panel";
mostCurrent._pnlgraph = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim cvsMain, cvsGraph As Canvas";
mostCurrent._cvsmain = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
mostCurrent._cvsgraph = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
