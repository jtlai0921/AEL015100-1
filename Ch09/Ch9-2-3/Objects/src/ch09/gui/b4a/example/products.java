package ch09.gui.b4a.example;

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

public class products extends Activity implements B4AActivity{
	public static products mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "ch09.gui.b4a.example", "ch09.gui.b4a.example.products");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (products).");
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
		activityBA = new BA(this, layout, processBA, "ch09.gui.b4a.example", "ch09.gui.b4a.example.products");
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (products) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (products) Resume **");
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
		return products.class;
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
        BA.LogInfo("** Activity (products) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (products) Resume **");
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
public static String _productname = "";
public static int _productprice = 0;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public ch09.gui.b4a.example.main _main = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpa = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpb = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpc = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmpd = null;
 //BA.debugLineNum = 15;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 16;BA.debugLine="Activity.Title = \"商品目錄\"";
mostCurrent._activity.setTitle((Object)("商品目錄"));
 //BA.debugLineNum = 18;BA.debugLine="ListView1.Initialize(\"ListView1\")";
mostCurrent._listview1.Initialize(mostCurrent.activityBA,"ListView1");
 //BA.debugLineNum = 19;BA.debugLine="Activity.AddView(ListView1, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._listview1.getObject()),(int)(0),(int)(0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float)(100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float)(100),mostCurrent.activityBA));
 //BA.debugLineNum = 21;BA.debugLine="Dim bmpA, bmpB, bmpC, bmpD As Bitmap  ' 圖示";
_bmpa = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmpb = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmpc = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmpd = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 22;BA.debugLine="bmpA.Initialize(File.DirAssets, \"productA.png\")";
_bmpa.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"productA.png");
 //BA.debugLineNum = 23;BA.debugLine="bmpB.Initialize(File.DirAssets, \"productB.png\")";
_bmpb.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"productB.png");
 //BA.debugLineNum = 24;BA.debugLine="bmpC.Initialize(File.DirAssets, \"productC.png\")";
_bmpc.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"productC.png");
 //BA.debugLineNum = 25;BA.debugLine="bmpD.Initialize(File.DirAssets, \"productD.png\")";
_bmpd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"productD.png");
 //BA.debugLineNum = 26;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"外接式光碟機\", \"$1200\", bmpA)";
mostCurrent._listview1.AddTwoLinesAndBitmap("外接式光碟機","$1200",(android.graphics.Bitmap)(_bmpa.getObject()));
 //BA.debugLineNum = 27;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"光學USB滑鼠\", \"$200\", bmpB)";
mostCurrent._listview1.AddTwoLinesAndBitmap("光學USB滑鼠","$200",(android.graphics.Bitmap)(_bmpb.getObject()));
 //BA.debugLineNum = 28;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"電腦標準鍵盤\", \"$350\", bmpC)";
mostCurrent._listview1.AddTwoLinesAndBitmap("電腦標準鍵盤","$350",(android.graphics.Bitmap)(_bmpc.getObject()));
 //BA.debugLineNum = 29;BA.debugLine="ListView1.AddTwoLinesAndBitmap(\"辦公型電話\", \"$430\", bmpD)";
mostCurrent._listview1.AddTwoLinesAndBitmap("辦公型電話","$430",(android.graphics.Bitmap)(_bmpd.getObject()));
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 11;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 12;BA.debugLine="Dim ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 13;BA.debugLine="End Sub";
return "";
}
public static String  _listview1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub ListView1_ItemClick(Position As Int, Value As Object)";
 //BA.debugLineNum = 41;BA.debugLine="ProductName = Value  ' 取得商品名稱";
_productname = String.valueOf(_value);
 //BA.debugLineNum = 42;BA.debugLine="Select Position      ' 取得商品價格";
switch (_position) {
case 0:
 //BA.debugLineNum = 43;BA.debugLine="Case 0: ProductPrice = 1200";
_productprice = (int)(1200);
 break;
case 1:
 //BA.debugLineNum = 44;BA.debugLine="Case 1: ProductPrice = 200";
_productprice = (int)(200);
 break;
case 2:
 //BA.debugLineNum = 45;BA.debugLine="Case 2: ProductPrice = 350";
_productprice = (int)(350);
 break;
case 3:
 //BA.debugLineNum = 46;BA.debugLine="Case 3: ProductPrice = 430";
_productprice = (int)(430);
 break;
}
;
 //BA.debugLineNum = 48;BA.debugLine="StartActivity(Main)  ' 返回Main主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim ProductName As String = \"\"";
_productname = "";
 //BA.debugLineNum = 8;BA.debugLine="Dim ProductPrice As Int = 0";
_productprice = (int)(0);
 //BA.debugLineNum = 9;BA.debugLine="End Sub";
return "";
}
}
