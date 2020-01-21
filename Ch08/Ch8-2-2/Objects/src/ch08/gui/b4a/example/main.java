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
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edttemp = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbloutput = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtamount = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblamount = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spntype = null;
public static double _rate = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _btnconvert = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtlength = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblunit = null;
public static boolean _lengthtype = false;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 38;BA.debugLine="Activity.Title = \"單位換算工具-1\"";
mostCurrent._activity.setTitle((Object)("單位換算工具-1"));
 //BA.debugLineNum = 40;BA.debugLine="TabHost1.AddTab(\"溫度\", \"Page1\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"溫度","Page1");
 //BA.debugLineNum = 41;BA.debugLine="TabHost1.AddTab(\"匯率\", \"Page2\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"匯率","Page2");
 //BA.debugLineNum = 42;BA.debugLine="TabHost1.AddTab(\"度量衡\", \"Page3\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"度量衡","Page3");
 //BA.debugLineNum = 44;BA.debugLine="edtTemp.Text = \"100.0\"";
mostCurrent._edttemp.setText((Object)("100.0"));
 //BA.debugLineNum = 45;BA.debugLine="edtAmount.Text = \"1000\"";
mostCurrent._edtamount.setText((Object)("1000"));
 //BA.debugLineNum = 46;BA.debugLine="edtLength.Text = \"100.0\"";
mostCurrent._edtlength.setText((Object)("100.0"));
 //BA.debugLineNum = 48;BA.debugLine="spnType.Add(\"美金\")";
mostCurrent._spntype.Add("美金");
 //BA.debugLineNum = 49;BA.debugLine="spnType.Add(\"日元\")";
mostCurrent._spntype.Add("日元");
 //BA.debugLineNum = 50;BA.debugLine="spnType.Add(\"人民幣\")";
mostCurrent._spntype.Add("人民幣");
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _btnconvert_click() throws Exception{
double _length = 0;
double _result = 0;
 //BA.debugLineNum = 109;BA.debugLine="Sub btnConvert_Click";
 //BA.debugLineNum = 110;BA.debugLine="Dim length As Double = edtLength.Text";
_length = (double)(Double.parseDouble(mostCurrent._edtlength.getText()));
 //BA.debugLineNum = 111;BA.debugLine="Dim result As Double = 0.0";
_result = 0.0;
 //BA.debugLineNum = 112;BA.debugLine="If lengthType Then  ' 英吋轉公分";
if (_lengthtype) { 
 //BA.debugLineNum = 113;BA.debugLine="result = length * 2.54";
_result = _length*2.54;
 //BA.debugLineNum = 114;BA.debugLine="lblResult.Text = \"換成公分: \" & result";
mostCurrent._lblresult.setText((Object)("換成公分: "+BA.NumberToString(_result)));
 }else {
 //BA.debugLineNum = 116;BA.debugLine="result = length / 2.54";
_result = _length/(double)2.54;
 //BA.debugLineNum = 117;BA.debugLine="lblResult.Text = \"換成英吋: \" & result";
mostCurrent._lblresult.setText((Object)("換成英吋: "+BA.NumberToString(_result)));
 };
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _btnconvertc_click() throws Exception{
double _f = 0;
double _c = 0;
 //BA.debugLineNum = 73;BA.debugLine="Sub btnConvertC_Click";
 //BA.debugLineNum = 74;BA.debugLine="Dim F As Double = edtTemp.Text";
_f = (double)(Double.parseDouble(mostCurrent._edttemp.getText()));
 //BA.debugLineNum = 75;BA.debugLine="Dim C As Double";
_c = 0;
 //BA.debugLineNum = 77;BA.debugLine="C = (5.0 / 9.0 ) * (F - 32.0)";
_c = (5.0/(double)9.0)*(_f-32.0);
 //BA.debugLineNum = 78;BA.debugLine="lblOutput.Text=\"華氏: \" & F & \" 度=>攝氏: \" & C & \" 度\"";
mostCurrent._lbloutput.setText((Object)("華氏: "+BA.NumberToString(_f)+" 度=>攝氏: "+BA.NumberToString(_c)+" 度"));
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _btnconvertf_click() throws Exception{
double _f = 0;
double _c = 0;
 //BA.debugLineNum = 65;BA.debugLine="Sub btnConvertF_Click";
 //BA.debugLineNum = 66;BA.debugLine="Dim F As Double";
_f = 0;
 //BA.debugLineNum = 67;BA.debugLine="Dim C As Double = edtTemp.Text";
_c = (double)(Double.parseDouble(mostCurrent._edttemp.getText()));
 //BA.debugLineNum = 69;BA.debugLine="F = (9.0 * C) / 5.0 + 32.0";
_f = (9.0*_c)/(double)5.0+32.0;
 //BA.debugLineNum = 70;BA.debugLine="lblOutput.Text=\"攝氏: \" & C & \" 度=>華氏: \" & F & \" 度\"";
mostCurrent._lbloutput.setText((Object)("攝氏: "+BA.NumberToString(_c)+" 度=>華氏: "+BA.NumberToString(_f)+" 度"));
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _btnmoney_click() throws Exception{
double _amount = 0;
 //BA.debugLineNum = 81;BA.debugLine="Sub btnMoney_Click";
 //BA.debugLineNum = 83;BA.debugLine="Dim amount As Double = edtAmount.Text";
_amount = (double)(Double.parseDouble(mostCurrent._edtamount.getText()));
 //BA.debugLineNum = 85;BA.debugLine="lblAmount.Text = \"兌換成新台幣的金額: \" & (amount * rate)";
mostCurrent._lblamount.setText((Object)("兌換成新台幣的金額: "+BA.NumberToString((_amount*_rate))));
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 23;BA.debugLine="Dim edtTemp As EditText";
mostCurrent._edttemp = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblOutput As Label";
mostCurrent._lbloutput = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim edtAmount As EditText";
mostCurrent._edtamount = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim lblAmount As Label";
mostCurrent._lblamount = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim spnType As Spinner";
mostCurrent._spntype = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim rate As Double : rate = 29.9";
_rate = 0;
 //BA.debugLineNum = 28;BA.debugLine="Dim rate As Double : rate = 29.9";
_rate = 29.9;
 //BA.debugLineNum = 29;BA.debugLine="Dim btnConvert As Button";
mostCurrent._btnconvert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim edtLength As EditText";
mostCurrent._edtlength = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim lblUnit As Label";
mostCurrent._lblunit = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim lengthType As Boolean : lengthType = False";
_lengthtype = false;
 //BA.debugLineNum = 33;BA.debugLine="Dim lengthType As Boolean : lengthType = False";
_lengthtype = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _spntype_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 88;BA.debugLine="Sub spnType_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 90;BA.debugLine="Select Position";
switch (_position) {
case 0:
 //BA.debugLineNum = 91;BA.debugLine="Case 0 : rate = 29.9    ' 美金";
_rate = 29.9;
 break;
case 1:
 //BA.debugLineNum = 92;BA.debugLine="Case 1 : rate = 0.2949  ' 日元";
_rate = 0.2949;
 break;
case 2:
 //BA.debugLineNum = 93;BA.debugLine="Case 2 : rate = 4.861   ' 人民幣";
_rate = 4.861;
 break;
}
;
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _tabhost1_tabchanged() throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub TabHost1_TabChanged";
 //BA.debugLineNum = 62;BA.debugLine="Activity.Title = \"單位換算工具-\" & (TabHost1.CurrentTab + 1)";
mostCurrent._activity.setTitle((Object)("單位換算工具-"+BA.NumberToString((mostCurrent._tabhost1.getCurrentTab()+1))));
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _togglebutton1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Sub ToggleButton1_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 98;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 99;BA.debugLine="lengthType = True  ' 英製";
_lengthtype = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 100;BA.debugLine="lblUnit.Text = \"英吋\"";
mostCurrent._lblunit.setText((Object)("英吋"));
 //BA.debugLineNum = 101;BA.debugLine="btnConvert.Text = \"轉換成公制\"";
mostCurrent._btnconvert.setText((Object)("轉換成公制"));
 }else {
 //BA.debugLineNum = 103;BA.debugLine="lengthType = False ' 公制";
_lengthtype = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 104;BA.debugLine="lblUnit.Text = \"公分\"";
mostCurrent._lblunit.setText((Object)("公分"));
 //BA.debugLineNum = 105;BA.debugLine="btnConvert.Text = \"轉換成英制\"";
mostCurrent._btnconvert.setText((Object)("轉換成英制"));
 };
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
}
