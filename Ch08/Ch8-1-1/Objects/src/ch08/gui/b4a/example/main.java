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
public anywheresoftware.b4a.objects.EditTextWrapper _edtnumber = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblmessage = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public static int _target = 0;
public static int _time = 0;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 30;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 31;BA.debugLine="Activity.Title = \"猜數字遊戲\"";
mostCurrent._activity.setTitle((Object)("猜數字遊戲"));
 //BA.debugLineNum = 32;BA.debugLine="Button1.Enabled = False";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 33;BA.debugLine="Activity.AddMenuItem(\"開始新遊戲\", \"BeginGame\")";
mostCurrent._activity.AddMenuItem("開始新遊戲","BeginGame");
 //BA.debugLineNum = 34;BA.debugLine="Activity.AddMenuItem(\"結束遊戲\", \"StopGame\")";
mostCurrent._activity.AddMenuItem("結束遊戲","StopGame");
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _begingame_click() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub BeginGame_Click";
 //BA.debugLineNum = 55;BA.debugLine="NewGame";
_newgame();
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
int _num = 0;
int _result = 0;
 //BA.debugLineNum = 64;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 65;BA.debugLine="Dim num As Int = edtNumber.Text";
_num = (int)(Double.parseDouble(mostCurrent._edtnumber.getText()));
 //BA.debugLineNum = 66;BA.debugLine="time = time + 1";
_time = (int)(_time+1);
 //BA.debugLineNum = 67;BA.debugLine="If num = target Then";
if (_num==_target) { 
 //BA.debugLineNum = 68;BA.debugLine="lblMessage.Text = \"猜中數字: \" & target";
mostCurrent._lblmessage.setText((Object)("猜中數字: "+BA.NumberToString(_target)));
 //BA.debugLineNum = 69;BA.debugLine="Dim result As Int";
_result = 0;
 //BA.debugLineNum = 70;BA.debugLine="result = Msgbox2(\"共猜: \" & time & \"次, 按[確定]鈕再玩一次.\", \"猜數字遊戲\",  \"確定\", \"\", \"結束\", Null)";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2("共猜: "+BA.NumberToString(_time)+"次, 按[確定]鈕再玩一次.","猜數字遊戲","確定","","結束",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 71;BA.debugLine="If result = DialogResponse.POSITIVE Then";
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 72;BA.debugLine="NewGame";
_newgame();
 }else {
 //BA.debugLineNum = 74;BA.debugLine="Button1.Enabled = False";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 75;BA.debugLine="lblMessage.Text = \"遊戲結束...\"";
mostCurrent._lblmessage.setText((Object)("遊戲結束..."));
 };
 }else {
 //BA.debugLineNum = 78;BA.debugLine="If num > target Then";
if (_num>_target) { 
 //BA.debugLineNum = 79;BA.debugLine="ToastMessageShow(num & \" 數字太大...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.NumberToString(_num)+" 數字太大...",anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 81;BA.debugLine="ToastMessageShow(num & \" 數字太小...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.NumberToString(_num)+" 數字太小...",anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 84;BA.debugLine="edtNumber.Text = \"\"";
mostCurrent._edtnumber.setText((Object)(""));
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 22;BA.debugLine="Dim edtNumber As EditText";
mostCurrent._edtnumber = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblMessage As Label";
mostCurrent._lblmessage = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim target As Int";
_target = 0;
 //BA.debugLineNum = 26;BA.debugLine="Dim time As Int";
_time = 0;
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _newgame() throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub NewGame";
 //BA.debugLineNum = 46;BA.debugLine="target = Rnd(1, 100)";
_target = anywheresoftware.b4a.keywords.Common.Rnd((int)(1),(int)(100));
 //BA.debugLineNum = 47;BA.debugLine="time = 0";
_time = (int)(0);
 //BA.debugLineNum = 48;BA.debugLine="Button1.Enabled = True";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 49;BA.debugLine="edtNumber.Text = \"\"";
mostCurrent._edtnumber.setText((Object)(""));
 //BA.debugLineNum = 50;BA.debugLine="lblMessage.Text = \"請輸入數字...\"";
mostCurrent._lblmessage.setText((Object)("請輸入數字..."));
 //BA.debugLineNum = 51;BA.debugLine="Msgbox(\"請輸入數字開始遊戲\", \"猜數字遊戲\")";
anywheresoftware.b4a.keywords.Common.Msgbox("請輸入數字開始遊戲","猜數字遊戲",mostCurrent.activityBA);
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _stopgame_click() throws Exception{
 //BA.debugLineNum = 58;BA.debugLine="Sub StopGame_Click";
 //BA.debugLineNum = 59;BA.debugLine="Button1.Enabled = False";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 60;BA.debugLine="edtNumber.Text = \"\"";
mostCurrent._edtnumber.setText((Object)(""));
 //BA.debugLineNum = 61;BA.debugLine="lblMessage.Text = \"遊戲結束...\"";
mostCurrent._lblmessage.setText((Object)("遊戲結束..."));
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
}
