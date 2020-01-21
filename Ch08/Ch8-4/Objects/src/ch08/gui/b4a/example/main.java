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
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.drawable.ColorDrawable _cd = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpa = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpb = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpc = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpd = null;
 //BA.debugLineNum = 25;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 26;BA.debugLine="Activity.Title = \"客製化ListView元件\"";
mostCurrent._activity.setTitle((Object)("客製化ListView元件"));
 //BA.debugLineNum = 28;BA.debugLine="ListView1.Initialize(\"ListView1\")";
mostCurrent._listview1.Initialize(mostCurrent.activityBA,"ListView1");
 //BA.debugLineNum = 29;BA.debugLine="Activity.AddView(ListView1, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._listview1.getObject()),(int)(0),(int)(0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float)(100),mostCurrent.activityBA));
 //BA.debugLineNum = 30;BA.debugLine="Dim CD As ColorDrawable ' 背景色彩";
_cd = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 31;BA.debugLine="CD.Initialize(Colors.LightGray, 5)";
_cd.Initialize(anywheresoftware.b4a.keywords.Common.Colors.LightGray,(int)(5));
 //BA.debugLineNum = 33;BA.debugLine="ListView1.SingleLineLayout.ItemHeight = 60dip";
mostCurrent._listview1.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(60)));
 //BA.debugLineNum = 34;BA.debugLine="ListView1.SingleLineLayout.Background = CD";
mostCurrent._listview1.getSingleLineLayout().Background = (android.graphics.drawable.Drawable)(_cd.getObject());
 //BA.debugLineNum = 35;BA.debugLine="ListView1.SingleLineLayout.Label.TextSize = 20";
mostCurrent._listview1.getSingleLineLayout().Label.setTextSize((float)(20));
 //BA.debugLineNum = 36;BA.debugLine="ListView1.SingleLineLayout.Label.TextColor = Colors.Blue";
mostCurrent._listview1.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 37;BA.debugLine="ListView1.SingleLineLayout.Label.Gravity = Gravity.CENTER";
mostCurrent._listview1.getSingleLineLayout().Label.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 39;BA.debugLine="ListView1.TwoLinesLayout.SecondLabel.TextSize = 10";
mostCurrent._listview1.getTwoLinesLayout().SecondLabel.setTextSize((float)(10));
 //BA.debugLineNum = 41;BA.debugLine="ListView1.TwoLinesAndBitmap.ItemHeight = 70dip";
mostCurrent._listview1.getTwoLinesAndBitmap().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(70)));
 //BA.debugLineNum = 42;BA.debugLine="ListView1.TwoLinesAndBitmap.Label.Height = 35dip";
mostCurrent._listview1.getTwoLinesAndBitmap().Label.setHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(35)));
 //BA.debugLineNum = 43;BA.debugLine="ListView1.TwoLinesAndBitmap.Label.TextSize = 30";
mostCurrent._listview1.getTwoLinesAndBitmap().Label.setTextSize((float)(30));
 //BA.debugLineNum = 45;BA.debugLine="Dim bmpA, bmpB, bmpC, bmpD As Bitmap  ' 圖示";
_bmpa = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmpb = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmpc = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmpd = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 46;BA.debugLine="bmpA.Initialize(File.DirAssets, \"speakerA.jpg\")";
_bmpa.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"speakerA.jpg");
 //BA.debugLineNum = 47;BA.debugLine="bmpB.Initialize(File.DirAssets, \"speakerB.jpg\")";
_bmpb.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"speakerB.jpg");
 //BA.debugLineNum = 48;BA.debugLine="bmpC.Initialize(File.DirAssets, \"speakerC.jpg\")";
_bmpc.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"speakerC.jpg");
 //BA.debugLineNum = 49;BA.debugLine="bmpD.Initialize(File.DirAssets, \"speakerD.jpg\")";
_bmpd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"speakerD.jpg");
 //BA.debugLineNum = 50;BA.debugLine="ListView1.AddSingleLine(\"陳會安\")";
mostCurrent._listview1.AddSingleLine("陳會安");
 //BA.debugLineNum = 51;BA.debugLine="ListView1.AddSingleLine(\"江小魚\")";
mostCurrent._listview1.AddSingleLine("江小魚");
 //BA.debugLineNum = 52;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"講師 A\",\"講師B的經歷與專長\", bmpA)";
mostCurrent._listview1.AddTwoLinesAndBitmap("講師 A","講師B的經歷與專長",(android.graphics.Bitmap)(_bmpa.getObject()));
 //BA.debugLineNum = 53;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"講師 B\",\"講師B的經歷與專長\", bmpB)";
mostCurrent._listview1.AddTwoLinesAndBitmap("講師 B","講師B的經歷與專長",(android.graphics.Bitmap)(_bmpb.getObject()));
 //BA.debugLineNum = 54;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"講師 C\",\"講師C的經歷與專長\", bmpC)";
mostCurrent._listview1.AddTwoLinesAndBitmap("講師 C","講師C的經歷與專長",(android.graphics.Bitmap)(_bmpc.getObject()));
 //BA.debugLineNum = 55;BA.debugLine="ListView1.AddTwoLines(\"講師 D\",\"講師D的經歷與專長\")";
mostCurrent._listview1.AddTwoLines("講師 D","講師D的經歷與專長");
 //BA.debugLineNum = 56;BA.debugLine="ListView1.AddTwoLines(\"講師 E\",\"講師E的經歷與專長\")";
mostCurrent._listview1.AddTwoLines("講師 E","講師E的經歷與專長");
 //BA.debugLineNum = 57;BA.debugLine="ListView1.AddTwoLines(\"講師 F\",\"講師F的經歷與專長\")";
mostCurrent._listview1.AddTwoLines("講師 F","講師F的經歷與專長");
 //BA.debugLineNum = 58;BA.debugLine="ListView1.AddTwoLines(\"講師 G\",\"講師G的經歷與專長\")";
mostCurrent._listview1.AddTwoLines("講師 G","講師G的經歷與專長");
 //BA.debugLineNum = 59;BA.debugLine="ListView1.AddTwoLines(\"講師 H\",\"講師H的經歷與專長\")";
mostCurrent._listview1.AddTwoLines("講師 H","講師H的經歷與專長");
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
 //BA.debugLineNum = 22;BA.debugLine="Dim ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
return "";
}
public static String  _listview1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 70;BA.debugLine="Sub ListView1_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 71;BA.debugLine="Activity.Title = Value";
mostCurrent._activity.setTitle(_value);
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
