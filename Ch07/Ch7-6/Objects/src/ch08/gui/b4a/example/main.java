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
public static boolean _turn = false;
public static int[][] _board = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button8 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button9 = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Activity.Title = \"井字遊戲\"";
mostCurrent._activity.setTitle((Object)("井字遊戲"));
 //BA.debugLineNum = 34;BA.debugLine="Activity.AddMenuItem(\"新遊戲\", \"NewGame\")";
mostCurrent._activity.AddMenuItem("新遊戲","NewGame");
 //BA.debugLineNum = 35;BA.debugLine="NewGame";
_newgame();
 //BA.debugLineNum = 36;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 42;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 38;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 175;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 176;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 177;BA.debugLine="Button1.Text = \"○\" : board(0, 0) = 0";
mostCurrent._button1.setText((Object)("○"));
 //BA.debugLineNum = 177;BA.debugLine="Button1.Text = \"○\" : board(0, 0) = 0";
_board[(int)(0)][(int)(0)] = (int)(0);
 }else {
 //BA.debugLineNum = 179;BA.debugLine="Button1.Text = \"✕\" : board(0, 0) = 1";
mostCurrent._button1.setText((Object)("✕"));
 //BA.debugLineNum = 179;BA.debugLine="Button1.Text = \"✕\" : board(0, 0) = 1";
_board[(int)(0)][(int)(0)] = (int)(1);
 };
 //BA.debugLineNum = 181;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 182;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 165;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 166;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 167;BA.debugLine="Button2.Text = \"○\" : board(0, 1) = 0";
mostCurrent._button2.setText((Object)("○"));
 //BA.debugLineNum = 167;BA.debugLine="Button2.Text = \"○\" : board(0, 1) = 0";
_board[(int)(0)][(int)(1)] = (int)(0);
 }else {
 //BA.debugLineNum = 169;BA.debugLine="Button2.Text = \"✕\" : board(0, 1) = 1";
mostCurrent._button2.setText((Object)("✕"));
 //BA.debugLineNum = 169;BA.debugLine="Button2.Text = \"✕\" : board(0, 1) = 1";
_board[(int)(0)][(int)(1)] = (int)(1);
 };
 //BA.debugLineNum = 171;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 172;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
 //BA.debugLineNum = 155;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 156;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 157;BA.debugLine="Button3.Text = \"○\" : board(0, 2) = 0";
mostCurrent._button3.setText((Object)("○"));
 //BA.debugLineNum = 157;BA.debugLine="Button3.Text = \"○\" : board(0, 2) = 0";
_board[(int)(0)][(int)(2)] = (int)(0);
 }else {
 //BA.debugLineNum = 159;BA.debugLine="Button3.Text = \"✕\" : board(0, 2) = 1";
mostCurrent._button3.setText((Object)("✕"));
 //BA.debugLineNum = 159;BA.debugLine="Button3.Text = \"✕\" : board(0, 2) = 1";
_board[(int)(0)][(int)(2)] = (int)(1);
 };
 //BA.debugLineNum = 161;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 162;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 163;BA.debugLine="End Sub";
return "";
}
public static String  _button4_click() throws Exception{
 //BA.debugLineNum = 145;BA.debugLine="Sub Button4_Click";
 //BA.debugLineNum = 146;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 147;BA.debugLine="Button4.Text = \"○\" : board(1, 0) = 0";
mostCurrent._button4.setText((Object)("○"));
 //BA.debugLineNum = 147;BA.debugLine="Button4.Text = \"○\" : board(1, 0) = 0";
_board[(int)(1)][(int)(0)] = (int)(0);
 }else {
 //BA.debugLineNum = 149;BA.debugLine="Button4.Text = \"✕\" : board(1, 0) = 1";
mostCurrent._button4.setText((Object)("✕"));
 //BA.debugLineNum = 149;BA.debugLine="Button4.Text = \"✕\" : board(1, 0) = 1";
_board[(int)(1)][(int)(0)] = (int)(1);
 };
 //BA.debugLineNum = 151;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 152;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 153;BA.debugLine="End Sub";
return "";
}
public static String  _button5_click() throws Exception{
 //BA.debugLineNum = 135;BA.debugLine="Sub Button5_Click";
 //BA.debugLineNum = 136;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 137;BA.debugLine="Button5.Text = \"○\" : board(1, 1) = 0";
mostCurrent._button5.setText((Object)("○"));
 //BA.debugLineNum = 137;BA.debugLine="Button5.Text = \"○\" : board(1, 1) = 0";
_board[(int)(1)][(int)(1)] = (int)(0);
 }else {
 //BA.debugLineNum = 139;BA.debugLine="Button5.Text = \"✕\" : board(1, 1) = 1";
mostCurrent._button5.setText((Object)("✕"));
 //BA.debugLineNum = 139;BA.debugLine="Button5.Text = \"✕\" : board(1, 1) = 1";
_board[(int)(1)][(int)(1)] = (int)(1);
 };
 //BA.debugLineNum = 141;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 142;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _button6_click() throws Exception{
 //BA.debugLineNum = 125;BA.debugLine="Sub Button6_Click";
 //BA.debugLineNum = 126;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 127;BA.debugLine="Button6.Text = \"○\" : board(1, 2) = 0";
mostCurrent._button6.setText((Object)("○"));
 //BA.debugLineNum = 127;BA.debugLine="Button6.Text = \"○\" : board(1, 2) = 0";
_board[(int)(1)][(int)(2)] = (int)(0);
 }else {
 //BA.debugLineNum = 129;BA.debugLine="Button6.Text = \"✕\" : board(1, 2) = 1";
mostCurrent._button6.setText((Object)("✕"));
 //BA.debugLineNum = 129;BA.debugLine="Button6.Text = \"✕\" : board(1, 2) = 1";
_board[(int)(1)][(int)(2)] = (int)(1);
 };
 //BA.debugLineNum = 131;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 132;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 133;BA.debugLine="End Sub";
return "";
}
public static String  _button7_click() throws Exception{
 //BA.debugLineNum = 115;BA.debugLine="Sub Button7_Click";
 //BA.debugLineNum = 116;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 117;BA.debugLine="Button7.Text = \"○\" : board(2, 0) = 0";
mostCurrent._button7.setText((Object)("○"));
 //BA.debugLineNum = 117;BA.debugLine="Button7.Text = \"○\" : board(2, 0) = 0";
_board[(int)(2)][(int)(0)] = (int)(0);
 }else {
 //BA.debugLineNum = 119;BA.debugLine="Button7.Text = \"✕\" : board(2, 0) = 1";
mostCurrent._button7.setText((Object)("✕"));
 //BA.debugLineNum = 119;BA.debugLine="Button7.Text = \"✕\" : board(2, 0) = 1";
_board[(int)(2)][(int)(0)] = (int)(1);
 };
 //BA.debugLineNum = 121;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 122;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _button8_click() throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub Button8_Click";
 //BA.debugLineNum = 106;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 107;BA.debugLine="Button8.Text = \"○\" : board(2, 1) = 0";
mostCurrent._button8.setText((Object)("○"));
 //BA.debugLineNum = 107;BA.debugLine="Button8.Text = \"○\" : board(2, 1) = 0";
_board[(int)(2)][(int)(1)] = (int)(0);
 }else {
 //BA.debugLineNum = 109;BA.debugLine="Button8.Text = \"✕\" : board(2, 1) = 1";
mostCurrent._button8.setText((Object)("✕"));
 //BA.debugLineNum = 109;BA.debugLine="Button8.Text = \"✕\" : board(2, 1) = 1";
_board[(int)(2)][(int)(1)] = (int)(1);
 };
 //BA.debugLineNum = 111;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 112;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _button9_click() throws Exception{
 //BA.debugLineNum = 95;BA.debugLine="Sub Button9_Click";
 //BA.debugLineNum = 96;BA.debugLine="If turn Then";
if (_turn) { 
 //BA.debugLineNum = 97;BA.debugLine="Button9.Text = \"○\" : board(2, 2) = 0";
mostCurrent._button9.setText((Object)("○"));
 //BA.debugLineNum = 97;BA.debugLine="Button9.Text = \"○\" : board(2, 2) = 0";
_board[(int)(2)][(int)(2)] = (int)(0);
 }else {
 //BA.debugLineNum = 99;BA.debugLine="Button9.Text = \"✕\" : board(2, 2) = 1";
mostCurrent._button9.setText((Object)("✕"));
 //BA.debugLineNum = 99;BA.debugLine="Button9.Text = \"✕\" : board(2, 2) = 1";
_board[(int)(2)][(int)(2)] = (int)(1);
 };
 //BA.debugLineNum = 101;BA.debugLine="turn = Not(turn)";
_turn = anywheresoftware.b4a.keywords.Common.Not(_turn);
 //BA.debugLineNum = 102;BA.debugLine="CheckWin  ' 檢查是否有贏家";
_checkwin();
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _checkwin() throws Exception{
int _total = 0;
int _total1 = 0;
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 60;BA.debugLine="Sub CheckWin";
 //BA.debugLineNum = 61;BA.debugLine="Dim total, total1 As Int";
_total = 0;
_total1 = 0;
 //BA.debugLineNum = 63;BA.debugLine="For i = 0 To 2";
{
final double step43 = 1;
final double limit43 = (int)(2);
for (_i = (int)(0); (step43 > 0 && _i <= limit43) || (step43 < 0 && _i >= limit43); _i += step43) {
 //BA.debugLineNum = 64;BA.debugLine="total = 0 : total1 = 0";
_total = (int)(0);
 //BA.debugLineNum = 64;BA.debugLine="total = 0 : total1 = 0";
_total1 = (int)(0);
 //BA.debugLineNum = 65;BA.debugLine="For j = 0 To 2";
{
final double step46 = 1;
final double limit46 = (int)(2);
for (_j = (int)(0); (step46 > 0 && _j <= limit46) || (step46 < 0 && _j >= limit46); _j += step46) {
 //BA.debugLineNum = 66;BA.debugLine="total = total + board(i, j) ' 每一列";
_total = (int)(_total+_board[_i][_j]);
 //BA.debugLineNum = 67;BA.debugLine="total1 = total1 + board(j, i) '每一欄";
_total1 = (int)(_total1+_board[_j][_i]);
 }
};
 //BA.debugLineNum = 69;BA.debugLine="If total = 0 OR total1 = 0 Then";
if (_total==0 || _total1==0) { 
 //BA.debugLineNum = 70;BA.debugLine="Msgbox(\"遊戲結束！○贏\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox("遊戲結束！○贏","",mostCurrent.activityBA);
 //BA.debugLineNum = 71;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 73;BA.debugLine="If total = 3 OR total1 = 3 Then";
if (_total==3 || _total1==3) { 
 //BA.debugLineNum = 74;BA.debugLine="Msgbox(\"遊戲結束！✕贏\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox("遊戲結束！✕贏","",mostCurrent.activityBA);
 //BA.debugLineNum = 75;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 //BA.debugLineNum = 79;BA.debugLine="total = board(0, 0) + board(1, 1) + board(2, 2)";
_total = (int)(_board[(int)(0)][(int)(0)]+_board[(int)(1)][(int)(1)]+_board[(int)(2)][(int)(2)]);
 //BA.debugLineNum = 80;BA.debugLine="total1 = board(2, 0) + board(1, 1) + board(0, 2)";
_total1 = (int)(_board[(int)(2)][(int)(0)]+_board[(int)(1)][(int)(1)]+_board[(int)(0)][(int)(2)]);
 //BA.debugLineNum = 81;BA.debugLine="If total = 0 OR total1 = 0 Then";
if (_total==0 || _total1==0) { 
 //BA.debugLineNum = 82;BA.debugLine="Msgbox(\"遊戲結束！○贏\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox("遊戲結束！○贏","",mostCurrent.activityBA);
 //BA.debugLineNum = 83;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 85;BA.debugLine="If total = 3 OR total1 = 3 Then";
if (_total==3 || _total1==3) { 
 //BA.debugLineNum = 86;BA.debugLine="Msgbox(\"遊戲結束！✕贏\", \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox("遊戲結束！✕贏","",mostCurrent.activityBA);
 //BA.debugLineNum = 87;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 22;BA.debugLine="Dim turn As Boolean = True";
_turn = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 23;BA.debugLine="Dim board(3, 3) As Int";
_board = new int[(int)(3)][];
{
int d0 = _board.length;
int d1 = (int)(3);
for (int i0 = 0;i0 < d0;i0++) {
_board[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 24;BA.debugLine="Dim Button1 As Button : Dim Button2 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Button1 As Button : Dim Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim Button3 As Button : Dim Button4 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim Button3 As Button : Dim Button4 As Button";
mostCurrent._button4 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Button5 As Button : Dim Button6 As Button";
mostCurrent._button5 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Button5 As Button : Dim Button6 As Button";
mostCurrent._button6 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Button7 As Button : Dim Button8 As Button";
mostCurrent._button7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Button7 As Button : Dim Button8 As Button";
mostCurrent._button8 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim Button9 As Button";
mostCurrent._button9 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _newgame() throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 46;BA.debugLine="Sub NewGame";
 //BA.debugLineNum = 48;BA.debugLine="For i = 0 To 2";
{
final double step26 = 1;
final double limit26 = (int)(2);
for (_i = (int)(0); (step26 > 0 && _i <= limit26) || (step26 < 0 && _i >= limit26); _i += step26) {
 //BA.debugLineNum = 49;BA.debugLine="For j = 0 To 2";
{
final double step27 = 1;
final double limit27 = (int)(2);
for (_j = (int)(0); (step27 > 0 && _j <= limit27) || (step27 < 0 && _j >= limit27); _j += step27) {
 //BA.debugLineNum = 50;BA.debugLine="board(i, j) = -3";
_board[_i][_j] = (int)(-3);
 }
};
 }
};
 //BA.debugLineNum = 53;BA.debugLine="Button1.Text = \"\" :	Button2.Text = \"\"";
mostCurrent._button1.setText((Object)(""));
 //BA.debugLineNum = 53;BA.debugLine="Button1.Text = \"\" :	Button2.Text = \"\"";
mostCurrent._button2.setText((Object)(""));
 //BA.debugLineNum = 54;BA.debugLine="Button3.Text = \"\" :	Button4.Text = \"\"";
mostCurrent._button3.setText((Object)(""));
 //BA.debugLineNum = 54;BA.debugLine="Button3.Text = \"\" :	Button4.Text = \"\"";
mostCurrent._button4.setText((Object)(""));
 //BA.debugLineNum = 55;BA.debugLine="Button5.Text = \"\" :	Button6.Text = \"\"";
mostCurrent._button5.setText((Object)(""));
 //BA.debugLineNum = 55;BA.debugLine="Button5.Text = \"\" :	Button6.Text = \"\"";
mostCurrent._button6.setText((Object)(""));
 //BA.debugLineNum = 56;BA.debugLine="Button7.Text = \"\" :	Button8.Text = \"\"";
mostCurrent._button7.setText((Object)(""));
 //BA.debugLineNum = 56;BA.debugLine="Button7.Text = \"\" :	Button8.Text = \"\"";
mostCurrent._button8.setText((Object)(""));
 //BA.debugLineNum = 57;BA.debugLine="Button9.Text = \"\"";
mostCurrent._button9.setText((Object)(""));
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _newgame_click() throws Exception{
 //BA.debugLineNum = 91;BA.debugLine="Sub NewGame_Click";
 //BA.debugLineNum = 92;BA.debugLine="NewGame";
_newgame();
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
