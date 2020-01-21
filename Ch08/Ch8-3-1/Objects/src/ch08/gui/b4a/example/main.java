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
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edta = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtb = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtc = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbloutput = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="Activity.Title = \"PPI計算機\"";
mostCurrent._activity.setTitle((Object)("PPI計算機"));
 //BA.debugLineNum = 30;BA.debugLine="Label1.Initialize(\"\")";
mostCurrent._label1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 31;BA.debugLine="Label1.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL";
mostCurrent._label1.setGravity((int)(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 32;BA.debugLine="Label1.Text = \"長邊:\"";
mostCurrent._label1.setText((Object)("長邊:"));
 //BA.debugLineNum = 33;BA.debugLine="Activity.AddView(Label1, 20dip, 60dip, 80dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._label1.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(60)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(80)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 34;BA.debugLine="edtA.Initialize(\"\")";
mostCurrent._edta.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 35;BA.debugLine="edtA.Text = \"1280\"";
mostCurrent._edta.setText((Object)("1280"));
 //BA.debugLineNum = 36;BA.debugLine="Activity.AddView(edtA, 110dip, 60dip, 190dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._edta.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(110)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(60)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 37;BA.debugLine="Label2.Initialize(\"\")";
mostCurrent._label2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 38;BA.debugLine="Label2.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL";
mostCurrent._label2.setGravity((int)(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 39;BA.debugLine="Label2.Text = \"短邊:\"";
mostCurrent._label2.setText((Object)("短邊:"));
 //BA.debugLineNum = 40;BA.debugLine="Activity.AddView(Label2, 20dip, 120dip, 80dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._label2.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(120)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(80)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 41;BA.debugLine="edtB.Initialize(\"\")";
mostCurrent._edtb.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 42;BA.debugLine="edtB.Text = \"720\"";
mostCurrent._edtb.setText((Object)("720"));
 //BA.debugLineNum = 43;BA.debugLine="Activity.AddView(edtB, 110dip, 120dip, 190dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._edtb.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(110)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(120)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 44;BA.debugLine="Label3.Initialize(\"\")";
mostCurrent._label3.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 45;BA.debugLine="Label3.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL";
mostCurrent._label3.setGravity((int)(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 46;BA.debugLine="Label3.Text = \"尺寸:\"";
mostCurrent._label3.setText((Object)("尺寸:"));
 //BA.debugLineNum = 47;BA.debugLine="Activity.AddView(Label3, 20dip, 180dip, 80dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._label3.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(180)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(80)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 48;BA.debugLine="edtC.Initialize(\"\")";
mostCurrent._edtc.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 49;BA.debugLine="edtC.Text = \"4.3\"";
mostCurrent._edtc.setText((Object)("4.3"));
 //BA.debugLineNum = 50;BA.debugLine="Activity.AddView(edtC, 110dip, 180dip, 190dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._edtc.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(110)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(180)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 51;BA.debugLine="Button1.Initialize(\"Button1\")";
mostCurrent._button1.Initialize(mostCurrent.activityBA,"Button1");
 //BA.debugLineNum = 52;BA.debugLine="Button1.Text = \"計算\"";
mostCurrent._button1.setText((Object)("計算"));
 //BA.debugLineNum = 53;BA.debugLine="Activity.AddView(Button1, 20dip, 240dip, 190dip, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._button1.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(240)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 54;BA.debugLine="lblOutput.Initialize(\"\")";
mostCurrent._lbloutput.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 55;BA.debugLine="lblOutput.Color = Colors.White";
mostCurrent._lbloutput.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 56;BA.debugLine="lblOutput.TextSize = 20";
mostCurrent._lbloutput.setTextSize((float)(20));
 //BA.debugLineNum = 57;BA.debugLine="lblOutput.TextColor = Colors.Blue";
mostCurrent._lbloutput.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 58;BA.debugLine="lblOutput.Gravity = Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL";
mostCurrent._lbloutput.setGravity((int)(anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL+anywheresoftware.b4a.keywords.Common.Gravity.CENTER_VERTICAL));
 //BA.debugLineNum = 59;BA.debugLine="Activity.AddView(lblOutput, 10%x, 300dip, 80%x, 40dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lbloutput.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(300)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40)));
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
double _tmp = 0;
double _result = 0;
 //BA.debugLineNum = 70;BA.debugLine="Sub Button1_Click()";
 //BA.debugLineNum = 71;BA.debugLine="Dim tmp, result As Double";
_tmp = 0;
_result = 0;
 //BA.debugLineNum = 72;BA.debugLine="tmp = edtA.Text * edtA.Text + edtB.Text * edtB.Text";
_tmp = (double)(Double.parseDouble(mostCurrent._edta.getText()))*(double)(Double.parseDouble(mostCurrent._edta.getText()))+(double)(Double.parseDouble(mostCurrent._edtb.getText()))*(double)(Double.parseDouble(mostCurrent._edtb.getText()));
 //BA.debugLineNum = 73;BA.debugLine="result = Sqrt(tmp) / edtC.Text";
_result = anywheresoftware.b4a.keywords.Common.Sqrt(_tmp)/(double)(double)(Double.parseDouble(mostCurrent._edtc.getText()));
 //BA.debugLineNum = 74;BA.debugLine="lblOutput.Text = \"PPI值: \" & Round(result)";
mostCurrent._lbloutput.setText((Object)("PPI值: "+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Round(_result))));
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 22;BA.debugLine="Dim Label1, Label2, Label3 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim edtA, edtB, edtC As EditText";
mostCurrent._edta = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edtb = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edtc = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblOutput As Label";
mostCurrent._lbloutput = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
