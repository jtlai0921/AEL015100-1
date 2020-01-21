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
public static anywheresoftware.b4a.objects.Timer _timer1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlboard = null;
public static String _movetype = "";
public anywheresoftware.b4a.objects.ImageViewWrapper _igvtarget = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igvcave = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 27;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 28;BA.debugLine="Activity.Title = \"引蛇入洞\"";
mostCurrent._activity.setTitle((Object)("引蛇入洞"));
 //BA.debugLineNum = 29;BA.debugLine="If FirstTime = True Then";
if (_firsttime==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 30;BA.debugLine="igvTarget.Bitmap = LoadBitmap(File.DirAssets, \"SmallSnake.jpg\")";
mostCurrent._igvtarget.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"SmallSnake.jpg").getObject()));
 //BA.debugLineNum = 31;BA.debugLine="Timer1.Initialize(\"Timer1\", 100)";
_timer1.Initialize(processBA,"Timer1",(long)(100));
 };
 //BA.debugLineNum = 33;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _btn_down() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _b = null;
 //BA.debugLineNum = 44;BA.debugLine="Sub btn_Down";
 //BA.debugLineNum = 45;BA.debugLine="Dim b As Button";
_b = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 46;BA.debugLine="b = Sender";
_b.setObject((android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 47;BA.debugLine="moveType = b.Tag  ' 移動種類";
mostCurrent._movetype = String.valueOf(_b.getTag());
 //BA.debugLineNum = 48;BA.debugLine="Timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _btn_up() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub btn_Up";
 //BA.debugLineNum = 52;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 20;BA.debugLine="Dim pnlBoard As Panel";
mostCurrent._pnlboard = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim moveType As String";
mostCurrent._movetype = "";
 //BA.debugLineNum = 22;BA.debugLine="Dim igvTarget As ImageView";
mostCurrent._igvtarget = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim igvCave As ImageView";
mostCurrent._igvcave = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim Timer1 As Timer";
_timer1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
int _inc = 0;
int _w = 0;
int _h = 0;
 //BA.debugLineNum = 55;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 56;BA.debugLine="Dim inc As Int = 2";
_inc = (int)(2);
 //BA.debugLineNum = 57;BA.debugLine="Dim w, h As Int";
_w = 0;
_h = 0;
 //BA.debugLineNum = 58;BA.debugLine="w = igvTarget.Width";
_w = mostCurrent._igvtarget.getWidth();
 //BA.debugLineNum = 59;BA.debugLine="h = igvTarget.Height";
_h = mostCurrent._igvtarget.getHeight();
 //BA.debugLineNum = 60;BA.debugLine="Select moveType";
switch (BA.switchObjectToInt(mostCurrent._movetype,"Up","Down","Left","Right")) {
case 0:
 //BA.debugLineNum = 62;BA.debugLine="igvTarget.Top = igvTarget.Top - inc";
mostCurrent._igvtarget.setTop((int)(mostCurrent._igvtarget.getTop()-_inc));
 //BA.debugLineNum = 63;BA.debugLine="If igvTarget.Top <= 0 Then igvTarget.Top = pnlBoard.Height - h";
if (mostCurrent._igvtarget.getTop()<=0) { 
mostCurrent._igvtarget.setTop((int)(mostCurrent._pnlboard.getHeight()-_h));};
 break;
case 1:
 //BA.debugLineNum = 65;BA.debugLine="igvTarget.Top = igvTarget.Top + inc";
mostCurrent._igvtarget.setTop((int)(mostCurrent._igvtarget.getTop()+_inc));
 //BA.debugLineNum = 66;BA.debugLine="If igvTarget.Top >= pnlBoard.Height - h Then igvTarget.Top = 0";
if (mostCurrent._igvtarget.getTop()>=mostCurrent._pnlboard.getHeight()-_h) { 
mostCurrent._igvtarget.setTop((int)(0));};
 break;
case 2:
 //BA.debugLineNum = 68;BA.debugLine="igvTarget.Left = igvTarget.Left - inc";
mostCurrent._igvtarget.setLeft((int)(mostCurrent._igvtarget.getLeft()-_inc));
 //BA.debugLineNum = 69;BA.debugLine="If igvTarget.Left <= 0 Then igvTarget.Left = pnlBoard.Width - w";
if (mostCurrent._igvtarget.getLeft()<=0) { 
mostCurrent._igvtarget.setLeft((int)(mostCurrent._pnlboard.getWidth()-_w));};
 break;
case 3:
 //BA.debugLineNum = 71;BA.debugLine="igvTarget.Left = igvTarget.Left + inc";
mostCurrent._igvtarget.setLeft((int)(mostCurrent._igvtarget.getLeft()+_inc));
 //BA.debugLineNum = 72;BA.debugLine="If igvTarget.Left >= pnlBoard.Width - w Then igvTarget.Left = 0";
if (mostCurrent._igvtarget.getLeft()>=mostCurrent._pnlboard.getWidth()-_w) { 
mostCurrent._igvtarget.setLeft((int)(0));};
 break;
}
;
 //BA.debugLineNum = 75;BA.debugLine="If igvTarget.Top >= igvCave.Top AND igvTarget.Left >= igvCave.Left AND _        (igvTarget.Top + h) <= (igvCave.Top + igvCave.Height) AND _        (igvTarget.Left + w) <= (igvCave.Left + igvCave.Width) Then";
if (mostCurrent._igvtarget.getTop()>=mostCurrent._igvcave.getTop() && mostCurrent._igvtarget.getLeft()>=mostCurrent._igvcave.getLeft() && (mostCurrent._igvtarget.getTop()+_h)<=(mostCurrent._igvcave.getTop()+mostCurrent._igvcave.getHeight()) && (mostCurrent._igvtarget.getLeft()+_w)<=(mostCurrent._igvcave.getLeft()+mostCurrent._igvcave.getWidth())) { 
 //BA.debugLineNum = 78;BA.debugLine="Timer1.Enabled = False";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 79;BA.debugLine="Msgbox(\"已經成功引蛇入洞...\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox("已經成功引蛇入洞...","",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
}
