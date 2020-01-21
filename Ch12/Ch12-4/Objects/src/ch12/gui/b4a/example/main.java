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
public anywheresoftware.b4a.objects.ScrollViewWrapper _scrollview1 = null;
public anywheresoftware.b4a.objects.collections.List _pictures = null;
public ch12.gui.b4a.example.showimg _showimg = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
int _i = 0;
anywheresoftware.b4a.objects.ImageViewWrapper _igv = null;
 //BA.debugLineNum = 26;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 27;BA.debugLine="If FirstTime Then  ' 只載入一次圖片";
if (_firsttime) { 
 //BA.debugLineNum = 28;BA.debugLine="ProgressDialogShow(\"正在載入圖片....\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"正在載入圖片....");
 //BA.debugLineNum = 29;BA.debugLine="LoadImagesFromSD   ' 從SD卡載入圖片";
_loadimagesfromsd();
 //BA.debugLineNum = 30;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 };
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Activity.Title = \"行動圖庫II\"";
mostCurrent._activity.setTitle((Object)("行動圖庫II"));
 //BA.debugLineNum = 35;BA.debugLine="ScrollView1.Panel.Height = 200dip * Pictures.Size";
mostCurrent._scrollview1.getPanel().setHeight((int)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(200))*mostCurrent._pictures.getSize()));
 //BA.debugLineNum = 36;BA.debugLine="For i = 0 To Pictures.Size - 1";
{
final double step15 = 1;
final double limit15 = (int)(mostCurrent._pictures.getSize()-1);
for (_i = (int)(0); (step15 > 0 && _i <= limit15) || (step15 < 0 && _i >= limit15); _i += step15) {
 //BA.debugLineNum = 37;BA.debugLine="Dim igv As ImageView ' 建立ImageView";
_igv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 38;BA.debugLine="igv.Initialize(\"img\")";
_igv.Initialize(mostCurrent.activityBA,"img");
 //BA.debugLineNum = 39;BA.debugLine="igv.Bitmap = Pictures.Get(i)  ' 指定圖片";
_igv.setBitmap((android.graphics.Bitmap)(mostCurrent._pictures.Get(_i)));
 //BA.debugLineNum = 41;BA.debugLine="ScrollView1.Panel.AddView(igv, 5dip, 5dip + i * 200dip, ScrollView1.Width - 10dip, 190dip)";
mostCurrent._scrollview1.getPanel().AddView((android.view.View)(_igv.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5)),(int)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5))+_i*anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(200))),(int)(mostCurrent._scrollview1.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(10))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(190)));
 }
};
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
return "";
}
public static String  _btndown_click() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub btnDown_Click";
 //BA.debugLineNum = 93;BA.debugLine="ScrollView1.ScrollPosition = ScrollView1.ScrollPosition + 200dip";
mostCurrent._scrollview1.setScrollPosition((int)(mostCurrent._scrollview1.getScrollPosition()+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(200))));
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _btnup_click() throws Exception{
 //BA.debugLineNum = 88;BA.debugLine="Sub btnUp_Click";
 //BA.debugLineNum = 89;BA.debugLine="ScrollView1.ScrollPosition = ScrollView1.ScrollPosition - 200dip";
mostCurrent._scrollview1.setScrollPosition((int)(mostCurrent._scrollview1.getScrollPosition()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(200))));
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (processGlobalsRun == false) {
	    processGlobalsRun = true;
		try {
		        main._process_globals();
showimg._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (showimg.mostCurrent != null);
return vis;}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim ScrollView1 As ScrollView";
mostCurrent._scrollview1 = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim Pictures As List";
mostCurrent._pictures = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _img_click() throws Exception{
anywheresoftware.b4a.objects.ImageViewWrapper _igv = null;
 //BA.debugLineNum = 80;BA.debugLine="Sub img_Click";
 //BA.debugLineNum = 81;BA.debugLine="Dim igv As ImageView";
_igv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 82;BA.debugLine="igv = Sender   ' 取得ImageView元件";
_igv.setObject((android.widget.ImageView)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 84;BA.debugLine="ShowImg.myPicture = igv.Bitmap";
mostCurrent._showimg._mypicture.setObject((android.graphics.Bitmap)(_igv.getBitmap()));
 //BA.debugLineNum = 85;BA.debugLine="StartActivity(ShowImg)  ' 開啟ShowImg活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._showimg.getObject()));
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _loadimagesfromsd() throws Exception{
anywheresoftware.b4a.objects.collections.List _myfiles = null;
String _imagesfolder = "";
int _i = 0;
String _s = "";
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _b = null;
 //BA.debugLineNum = 53;BA.debugLine="Sub LoadImagesFromSD";
 //BA.debugLineNum = 54;BA.debugLine="Pictures.Initialize()";
mostCurrent._pictures.Initialize();
 //BA.debugLineNum = 55;BA.debugLine="Dim myFiles As List";
_myfiles = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 56;BA.debugLine="Dim imagesFolder As String";
_imagesfolder = "";
 //BA.debugLineNum = 57;BA.debugLine="imagesFolder = File.DirRootExternal & \"/Images\"";
_imagesfolder = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Images";
 //BA.debugLineNum = 59;BA.debugLine="If File.Exists(imagesFolder, \"\") = False Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(_imagesfolder,"")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 60;BA.debugLine="ToastMessageShow(\"圖片資料夾找不到: \" & CRLF & imagesFolder, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("圖片資料夾找不到: "+anywheresoftware.b4a.keywords.Common.CRLF+_imagesfolder,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 61;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 64;BA.debugLine="myFiles = File.ListFiles(imagesFolder)";
_myfiles = anywheresoftware.b4a.keywords.Common.File.ListFiles(_imagesfolder);
 //BA.debugLineNum = 65;BA.debugLine="For i = 0 To myFiles.Size - 1";
{
final double step36 = 1;
final double limit36 = (int)(_myfiles.getSize()-1);
for (_i = (int)(0); (step36 > 0 && _i <= limit36) || (step36 < 0 && _i >= limit36); _i += step36) {
 //BA.debugLineNum = 66;BA.debugLine="DoEvents    ' 為了ProgressDialog動畫";
anywheresoftware.b4a.keywords.Common.DoEvents();
 //BA.debugLineNum = 67;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 68;BA.debugLine="s = myFiles.Get(i)";
_s = String.valueOf(_myfiles.Get(_i));
 //BA.debugLineNum = 69;BA.debugLine="If s.ToLowerCase.EndsWith(\".jpg\") Then ' 是否是JPG檔";
if (_s.toLowerCase().endsWith(".jpg")) { 
 //BA.debugLineNum = 70;BA.debugLine="Dim b As Bitmap";
_b = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 72;BA.debugLine="b.InitializeSample(imagesFolder, s, 200dip, 200dip)";
_b.InitializeSample(_imagesfolder,_s,anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(200)));
 //BA.debugLineNum = 73;BA.debugLine="Pictures.Add(b) ' 新增至List物件";
mostCurrent._pictures.Add((Object)(_b.getObject()));
 //BA.debugLineNum = 74;BA.debugLine="If Pictures.Size > 20 Then Exit ' 最多20張圖片";
if (mostCurrent._pictures.getSize()>20) { 
if (true) break;};
 };
 }
};
 //BA.debugLineNum = 77;BA.debugLine="ToastMessageShow(\"找到 \" & Pictures.Size & \" 張圖片\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("找到 "+BA.NumberToString(mostCurrent._pictures.getSize())+" 張圖片",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
