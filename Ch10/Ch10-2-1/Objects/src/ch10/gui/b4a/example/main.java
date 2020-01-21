package ch10.gui.b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "ch10.gui.b4a.example", "ch10.gui.b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "ch10.gui.b4a.example", "ch10.gui.b4a.example.main");
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
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtnote1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtnote2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtnote3 = null;
public static String _slot = "";
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 28;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 29;BA.debugLine="Activity.Title = \"行動Memo(II)-記事1\"";
mostCurrent._activity.setTitle((Object)("行動Memo(II)-記事1"));
 //BA.debugLineNum = 31;BA.debugLine="Activity.AddMenuItem(\"清除\", \"ClearNote\")";
mostCurrent._activity.AddMenuItem("清除","ClearNote");
 //BA.debugLineNum = 32;BA.debugLine="Activity.AddMenuItem(\"儲存\", \"SaveNote\")";
mostCurrent._activity.AddMenuItem("儲存","SaveNote");
 //BA.debugLineNum = 33;BA.debugLine="Activity.AddMenuItem(\"結束\", \"ExitApp\")";
mostCurrent._activity.AddMenuItem("結束","ExitApp");
 //BA.debugLineNum = 35;BA.debugLine="TabHost1.AddTab(\"記事1\", \"Page1\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"記事1","Page1");
 //BA.debugLineNum = 36;BA.debugLine="TabHost1.AddTab(\"記事2\", \"Page2\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"記事2","Page2");
 //BA.debugLineNum = 37;BA.debugLine="TabHost1.AddTab(\"記事3\", \"Page3\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"記事3","Page3");
 //BA.debugLineNum = 39;BA.debugLine="edtNote1.Width = Activity.Width";
mostCurrent._edtnote1.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 40;BA.debugLine="edtNote1.Height = Activity.Height - 50";
mostCurrent._edtnote1.setHeight((int)(mostCurrent._activity.getHeight()-50));
 //BA.debugLineNum = 41;BA.debugLine="edtNote2.Width = Activity.Width";
mostCurrent._edtnote2.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 42;BA.debugLine="edtNote2.Height = Activity.Height - 50";
mostCurrent._edtnote2.setHeight((int)(mostCurrent._activity.getHeight()-50));
 //BA.debugLineNum = 43;BA.debugLine="edtNote3.Width = Activity.Width";
mostCurrent._edtnote3.setWidth(mostCurrent._activity.getWidth());
 //BA.debugLineNum = 44;BA.debugLine="edtNote3.Height = Activity.Height - 50";
mostCurrent._edtnote3.setHeight((int)(mostCurrent._activity.getHeight()-50));
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 48;BA.debugLine="ReadFile(slot)";
_readfile(mostCurrent._slot);
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _clearnote_click() throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub ClearNote_Click";
 //BA.debugLineNum = 56;BA.debugLine="Select slot";
switch (BA.switchObjectToInt(mostCurrent._slot,"1","2","3")) {
case 0:
 //BA.debugLineNum = 57;BA.debugLine="Case \"1\" : edtNote1.Text = \"\"";
mostCurrent._edtnote1.setText((Object)(""));
 break;
case 1:
 //BA.debugLineNum = 58;BA.debugLine="Case \"2\" : edtNote2.Text = \"\"";
mostCurrent._edtnote2.setText((Object)(""));
 break;
case 2:
 //BA.debugLineNum = 59;BA.debugLine="Case \"3\" : edtNote3.Text = \"\"";
mostCurrent._edtnote3.setText((Object)(""));
 break;
}
;
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _exitapp_click() throws Exception{
int _result = 0;
 //BA.debugLineNum = 76;BA.debugLine="Sub ExitApp_Click";
 //BA.debugLineNum = 77;BA.debugLine="Dim result As Int";
_result = 0;
 //BA.debugLineNum = 78;BA.debugLine="result = Msgbox2(\"確認結束行動Memo?\", \"行動Memo\", \"確認\", \"\", \"取消\", Null)";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2("確認結束行動Memo?","行動Memo","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 79;BA.debugLine="If result = DialogResponse.POSITIVE Then";
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 80;BA.debugLine="Activity.Finish()";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 81;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 22;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim edtNote1, edtNote2, edtNote3 As EditText";
mostCurrent._edtnote1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edtnote2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edtnote3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim slot As String = \"1\"  ' 目前的記事槽編號";
mostCurrent._slot = "1";
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _readfile(String _cslot) throws Exception{
String _content = "";
 //BA.debugLineNum = 91;BA.debugLine="Sub ReadFile(cslot As String)";
 //BA.debugLineNum = 92;BA.debugLine="Dim content As String = \"\"";
_content = "";
 //BA.debugLineNum = 94;BA.debugLine="If File.Exists(File.DirInternal,\"note\" & cslot & \".txt\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"note"+_cslot+".txt")) { 
 //BA.debugLineNum = 96;BA.debugLine="content = File.ReadString(File.DirInternal, \"note\" & cslot & \".txt\")";
_content = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"note"+_cslot+".txt");
 //BA.debugLineNum = 97;BA.debugLine="ToastMessageShow(\"讀取資料至:記事\" & cslot, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("讀取資料至:記事"+_cslot,anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 99;BA.debugLine="ToastMessageShow(\"記事\" & cslot & \" 目前並沒有資料...\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("記事"+_cslot+" 目前並沒有資料...",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 102;BA.debugLine="Select cslot";
switch (BA.switchObjectToInt(_cslot,"1","2","3")) {
case 0:
 //BA.debugLineNum = 103;BA.debugLine="Case \"1\" : edtNote1.Text = content";
mostCurrent._edtnote1.setText((Object)(_content));
 break;
case 1:
 //BA.debugLineNum = 104;BA.debugLine="Case \"2\" : edtNote2.Text = content";
mostCurrent._edtnote2.setText((Object)(_content));
 break;
case 2:
 //BA.debugLineNum = 105;BA.debugLine="Case \"3\" : edtNote3.Text = content";
mostCurrent._edtnote3.setText((Object)(_content));
 break;
}
;
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _savenote_click() throws Exception{
String _content = "";
 //BA.debugLineNum = 63;BA.debugLine="Sub SaveNote_Click";
 //BA.debugLineNum = 64;BA.debugLine="Dim content As String";
_content = "";
 //BA.debugLineNum = 66;BA.debugLine="Select slot";
switch (BA.switchObjectToInt(mostCurrent._slot,"1","2","3")) {
case 0:
 //BA.debugLineNum = 67;BA.debugLine="Case \"1\" : content = edtNote1.Text";
_content = mostCurrent._edtnote1.getText();
 break;
case 1:
 //BA.debugLineNum = 68;BA.debugLine="Case \"2\" : content = edtNote2.Text";
_content = mostCurrent._edtnote2.getText();
 break;
case 2:
 //BA.debugLineNum = 69;BA.debugLine="Case \"3\" : content = edtNote3.Text";
_content = mostCurrent._edtnote3.getText();
 break;
}
;
 //BA.debugLineNum = 72;BA.debugLine="File.WriteString(File.DirInternal, \"note\" & slot & \".txt\", content)";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"note"+mostCurrent._slot+".txt",_content);
 //BA.debugLineNum = 73;BA.debugLine="ToastMessageShow(\"資料儲存至:記事\" & slot, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("資料儲存至:記事"+mostCurrent._slot,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _tabhost1_tabchanged() throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Sub TabHost1_TabChanged";
 //BA.debugLineNum = 86;BA.debugLine="slot = TabHost1.CurrentTab + 1  ' 更新記事槽編號";
mostCurrent._slot = BA.NumberToString(mostCurrent._tabhost1.getCurrentTab()+1);
 //BA.debugLineNum = 87;BA.debugLine="Activity.Title = \"行動Memo(II)-記事\" & slot";
mostCurrent._activity.setTitle((Object)("行動Memo(II)-記事"+mostCurrent._slot));
 //BA.debugLineNum = 88;BA.debugLine="ReadFile(slot)";
_readfile(mostCurrent._slot);
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
}
