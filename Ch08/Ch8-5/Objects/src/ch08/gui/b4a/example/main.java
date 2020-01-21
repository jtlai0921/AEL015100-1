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
public anywheresoftware.b4a.objects.LabelWrapper _lblmoney = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbloriginal = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblslot1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblslot2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblslot3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public static int _bet = 0;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 34;BA.debugLine="Activity.Title = \"拉霸遊戲\"";
mostCurrent._activity.setTitle((Object)("拉霸遊戲"));
 //BA.debugLineNum = 35;BA.debugLine="Button1.Enabled = False";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 36;BA.debugLine="Activity.AddMenuItem(\"新遊戲\", \"NewGame\")";
mostCurrent._activity.AddMenuItem("新遊戲","NewGame");
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 98;BA.debugLine="If lblMoney.Text <= 0 Then  ' 是否仍有睹金";
if ((double)(Double.parseDouble(mostCurrent._lblmoney.getText()))<=0) { 
 //BA.debugLineNum = 99;BA.debugLine="Msgbox(\"遊戲結束!\", \"\") ' 沒有";
anywheresoftware.b4a.keywords.Common.Msgbox("遊戲結束!","",mostCurrent.activityBA);
 //BA.debugLineNum = 100;BA.debugLine="Button1.Enabled = False";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 102;BA.debugLine="lblResult.Text = \"!!!! 轉動中 !!!!\"";
mostCurrent._lblresult.setText((Object)("!!!! 轉動中 !!!!"));
 //BA.debugLineNum = 103;BA.debugLine="lblMoney.Text = lblMoney.Text - bet";
mostCurrent._lblmoney.setText((Object)((double)(Double.parseDouble(mostCurrent._lblmoney.getText()))-_bet));
 //BA.debugLineNum = 104;BA.debugLine="lblOriginal.Text = lblMoney.Text";
mostCurrent._lbloriginal.setText((Object)(mostCurrent._lblmoney.getText()));
 //BA.debugLineNum = 105;BA.debugLine="StartRoll  ' 轉輪";
_startroll();
 };
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _getaward(int _slot1,int _slot2,int _slot3) throws Exception{
int _award = 0;
 //BA.debugLineNum = 78;BA.debugLine="Sub GetAward(slot1 As Int, slot2 As Int, slot3 As Int)";
 //BA.debugLineNum = 79;BA.debugLine="Dim award As Int ' 獎金";
_award = 0;
 //BA.debugLineNum = 80;BA.debugLine="If slot1 = 7 AND slot2 = 7 AND slot3 = 7 Then ' 3個7";
if (_slot1==7 && _slot2==7 && _slot3==7) { 
 //BA.debugLineNum = 81;BA.debugLine="award = bet * 20";
_award = (int)(_bet*20);
 //BA.debugLineNum = 82;BA.debugLine="lblMoney.Text = award + lblOriginal.Text";
mostCurrent._lblmoney.setText((Object)(_award+(double)(Double.parseDouble(mostCurrent._lbloriginal.getText()))));
 //BA.debugLineNum = 83;BA.debugLine="lblResult.Text = \"轉中777 = 押注 X 20倍\"";
mostCurrent._lblresult.setText((Object)("轉中777 = 押注 X 20倍"));
 }else if(_slot1==_slot2 && _slot2==_slot3 && _slot1==_slot3) { 
 //BA.debugLineNum = 85;BA.debugLine="award = bet * 10";
_award = (int)(_bet*10);
 //BA.debugLineNum = 86;BA.debugLine="lblMoney.Text = award + lblOriginal.Text";
mostCurrent._lblmoney.setText((Object)(_award+(double)(Double.parseDouble(mostCurrent._lbloriginal.getText()))));
 //BA.debugLineNum = 87;BA.debugLine="lblResult.Text = \"轉中3個 = 押注 X 10倍\"";
mostCurrent._lblresult.setText((Object)("轉中3個 = 押注 X 10倍"));
 }else if(_slot1==_slot2 || _slot2==_slot3 || _slot1==_slot3) { 
 //BA.debugLineNum = 89;BA.debugLine="award = bet * 5 ' 2個相同";
_award = (int)(_bet*5);
 //BA.debugLineNum = 90;BA.debugLine="lblMoney.Text = award + lblOriginal.Text";
mostCurrent._lblmoney.setText((Object)(_award+(double)(Double.parseDouble(mostCurrent._lbloriginal.getText()))));
 //BA.debugLineNum = 91;BA.debugLine="lblResult.Text = \"轉中2個 = 押注 X 5倍\"";
mostCurrent._lblresult.setText((Object)("轉中2個 = 押注 X 5倍"));
 }else {
 //BA.debugLineNum = 93;BA.debugLine="lblResult.Text = \"!!失敗!! 請再轉一次!!\"";
mostCurrent._lblresult.setText((Object)("!!失敗!! 請再轉一次!!"));
 };
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 22;BA.debugLine="Dim lblMoney As Label";
mostCurrent._lblmoney = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblOriginal As Label";
mostCurrent._lbloriginal = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblSlot1 As Label";
mostCurrent._lblslot1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim lblSlot2 As Label";
mostCurrent._lblslot2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim lblSlot3 As Label";
mostCurrent._lblslot3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim bet As Int = 100";
_bet = (int)(100);
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _newgame_click() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub NewGame_Click";
 //BA.debugLineNum = 48;BA.debugLine="lblSlot1.Text = 0";
mostCurrent._lblslot1.setText((Object)(0));
 //BA.debugLineNum = 49;BA.debugLine="lblSlot2.Text = 0";
mostCurrent._lblslot2.setText((Object)(0));
 //BA.debugLineNum = 50;BA.debugLine="lblSlot3.Text = 0";
mostCurrent._lblslot3.setText((Object)(0));
 //BA.debugLineNum = 51;BA.debugLine="lblOriginal.Text = \"1000.0\"";
mostCurrent._lbloriginal.setText((Object)("1000.0"));
 //BA.debugLineNum = 52;BA.debugLine="lblMoney.Text = \"1000.0\"";
mostCurrent._lblmoney.setText((Object)("1000.0"));
 //BA.debugLineNum = 53;BA.debugLine="lblResult.Text = \"== 拉霸遊戲 ==\"";
mostCurrent._lblresult.setText((Object)("== 拉霸遊戲 =="));
 //BA.debugLineNum = 54;BA.debugLine="Button1.Enabled = True";
mostCurrent._button1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _sleep(long _ms) throws Exception{
long _now = 0L;
 //BA.debugLineNum = 69;BA.debugLine="Sub Sleep(ms As Long)   ' 暫停一段時間";
 //BA.debugLineNum = 70;BA.debugLine="Dim now As Long";
_now = 0L;
 //BA.debugLineNum = 71;BA.debugLine="If ms > 1000 Then ms = 1000";
if (_ms>1000) { 
_ms = (long)(1000);};
 //BA.debugLineNum = 72;BA.debugLine="now = DateTime.now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 73;BA.debugLine="Do Until (DateTime.now > now + ms)";
while (!((anywheresoftware.b4a.keywords.Common.DateTime.getNow()>_now+_ms))) {
 //BA.debugLineNum = 74;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 }
;
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _startroll() throws Exception{
int _i = 0;
 //BA.debugLineNum = 57;BA.debugLine="Sub StartRoll";
 //BA.debugLineNum = 58;BA.debugLine="For i = 1 To 10";
{
final double step32 = 1;
final double limit32 = (int)(10);
for (_i = (int)(1); (step32 > 0 && _i <= limit32) || (step32 < 0 && _i >= limit32); _i += step32) {
 //BA.debugLineNum = 60;BA.debugLine="lblSlot1.Text = Rnd(0, 9)";
mostCurrent._lblslot1.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int)(0),(int)(9))));
 //BA.debugLineNum = 61;BA.debugLine="lblSlot2.Text = Rnd(0, 9)";
mostCurrent._lblslot2.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int)(0),(int)(9))));
 //BA.debugLineNum = 62;BA.debugLine="lblSlot3.Text = Rnd(0, 9)";
mostCurrent._lblslot3.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int)(0),(int)(9))));
 //BA.debugLineNum = 63;BA.debugLine="Sleep(100)  ' 暫停100ms";
_sleep((long)(100));
 }
};
 //BA.debugLineNum = 66;BA.debugLine="GetAward(lblSlot1.Text, lblSlot2.Text, lblSlot3.Text)";
_getaward((int)(Double.parseDouble(mostCurrent._lblslot1.getText())),(int)(Double.parseDouble(mostCurrent._lblslot2.getText())),(int)(Double.parseDouble(mostCurrent._lblslot3.getText())));
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
}
