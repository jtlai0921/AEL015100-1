package ch15.gui.b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "ch15.gui.b4a.example", "ch15.gui.b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "ch15.gui.b4a.example", "ch15.gui.b4a.example.main");
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
public static String _urlbegin = "";
public static String _urlend = "";
public anywheresoftware.b4a.objects.EditTextWrapper _edtkeyword = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv4 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv5 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv6 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv7 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv8 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _igv9 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper[] _arrimgv = null;
public static int _imageindex = 0;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 40;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 41;BA.debugLine="Activity.Title = \"搜尋Flickr分享圖片\"";
mostCurrent._activity.setTitle((Object)("搜尋Flickr分享圖片"));
 //BA.debugLineNum = 43;BA.debugLine="arrImgV = Array As ImageView(igv1, igv2, igv3, igv4, _                           igv5, igv6, igv7, igv8, igv9)";
mostCurrent._arrimgv = new anywheresoftware.b4a.objects.ImageViewWrapper[]{mostCurrent._igv1,mostCurrent._igv2,mostCurrent._igv3,mostCurrent._igv4,mostCurrent._igv5,mostCurrent._igv6,mostCurrent._igv7,mostCurrent._igv8,mostCurrent._igv9};
 //BA.debugLineNum = 45;BA.debugLine="ResetImageView   ' 重設ImageView元件";
_resetimageview();
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
anywheresoftware.b4a.samples.httputils2.httpjob _job = null;
 //BA.debugLineNum = 67;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 68;BA.debugLine="Dim job As HttpJob";
_job = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 69;BA.debugLine="job.Initialize(\"JSONJob\", Me)";
_job._initialize(processBA,"JSONJob",main.getObject());
 //BA.debugLineNum = 70;BA.debugLine="job.Download(urlBegin &  edtKeyword.Text & urlEnd)";
_job._download(mostCurrent._urlbegin+mostCurrent._edtkeyword.getText()+mostCurrent._urlend);
 //BA.debugLineNum = 71;BA.debugLine="ProgressDialogShow2(\"搜尋Flickr分享圖片...\", False)";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow2(mostCurrent.activityBA,"搜尋Flickr分享圖片...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (processGlobalsRun == false) {
	    processGlobalsRun = true;
		try {
		        main._process_globals();
anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
		
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
 //BA.debugLineNum = 22;BA.debugLine="Dim urlBegin, urlEnd As String";
mostCurrent._urlbegin = "";
mostCurrent._urlend = "";
 //BA.debugLineNum = 23;BA.debugLine="urlBegin = \"http://api.flickr.com/services/feeds/photos_public.gne?tags=\"";
mostCurrent._urlbegin = "http://api.flickr.com/services/feeds/photos_public.gne?tags=";
 //BA.debugLineNum = 24;BA.debugLine="urlEnd = \"&tagmode=any&format=json&jsoncallback=?\"";
mostCurrent._urlend = "&tagmode=any&format=json&jsoncallback=?";
 //BA.debugLineNum = 25;BA.debugLine="Dim edtKeyword As EditText";
mostCurrent._edtkeyword = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim igv1 As ImageView";
mostCurrent._igv1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim igv2 As ImageView";
mostCurrent._igv2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim igv3 As ImageView";
mostCurrent._igv3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim igv4 As ImageView";
mostCurrent._igv4 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim igv5 As ImageView";
mostCurrent._igv5 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim igv6 As ImageView";
mostCurrent._igv6 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim igv7 As ImageView";
mostCurrent._igv7 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim igv8 As ImageView";
mostCurrent._igv8 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim igv9 As ImageView";
mostCurrent._igv9 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim arrImgV() As ImageView";
mostCurrent._arrimgv = new anywheresoftware.b4a.objects.ImageViewWrapper[(int)(0)];
{
int d0 = mostCurrent._arrimgv.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._arrimgv[i0] = new anywheresoftware.b4a.objects.ImageViewWrapper();
}
}
;
 //BA.debugLineNum = 36;BA.debugLine="Dim ImageIndex As Int = 0";
_imageindex = (int)(0);
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _imagejobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub ImageJobDone(Job As HttpJob)";
 //BA.debugLineNum = 111;BA.debugLine="If Job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 112;BA.debugLine="arrImgV(ImageIndex).Bitmap = Job.GetBitmap()";
mostCurrent._arrimgv[_imageindex].setBitmap((android.graphics.Bitmap)(_job._getbitmap().getObject()));
 };
 //BA.debugLineNum = 114;BA.debugLine="ImageIndex = ImageIndex + 1";
_imageindex = (int)(_imageindex+1);
 //BA.debugLineNum = 115;BA.debugLine="If ImageIndex = 9 Then ProgressDialogHide";
if (_imageindex==9) { 
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();};
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 75;BA.debugLine="Select Job.JobName";
switch (BA.switchObjectToInt(_job._jobname,"JSONJob","ImageJob")) {
case 0:
 //BA.debugLineNum = 77;BA.debugLine="JSONJobDone(Job)";
_jsonjobdone(_job);
 break;
case 1:
 //BA.debugLineNum = 79;BA.debugLine="ImageJobDone(Job)";
_imagejobdone(_job);
 break;
}
;
 //BA.debugLineNum = 81;BA.debugLine="Job.Release()";
_job._release();
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _jsonjobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
String _rstr = "";
anywheresoftware.b4a.objects.collections.JSONParser _json = null;
anywheresoftware.b4a.objects.collections.Map _itemmap = null;
anywheresoftware.b4a.objects.collections.List _itemlist = null;
int _i = 0;
anywheresoftware.b4a.objects.collections.Map _item = null;
anywheresoftware.b4a.objects.collections.Map _media = null;
anywheresoftware.b4a.samples.httputils2.httpjob _imagejob = null;
 //BA.debugLineNum = 84;BA.debugLine="Sub JSONJobDone(Job As HttpJob)";
 //BA.debugLineNum = 85;BA.debugLine="Dim rStr As String";
_rstr = "";
 //BA.debugLineNum = 86;BA.debugLine="If Job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 87;BA.debugLine="rStr = Job.GetString()  ' 取得JSON字串";
_rstr = _job._getstring();
 //BA.debugLineNum = 89;BA.debugLine="rStr = rStr.SubString2(1, rStr.Length)";
_rstr = _rstr.substring((int)(1),_rstr.length());
 //BA.debugLineNum = 91;BA.debugLine="Dim JSON As JSONParser";
_json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 92;BA.debugLine="JSON.Initialize(rStr)";
_json.Initialize(_rstr);
 //BA.debugLineNum = 93;BA.debugLine="Dim itemMap As Map";
_itemmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 94;BA.debugLine="itemMap = JSON.NextObject()";
_itemmap = _json.NextObject();
 //BA.debugLineNum = 95;BA.debugLine="Dim itemList As List";
_itemlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 96;BA.debugLine="itemList = itemMap.Get(\"items\")";
_itemlist.setObject((java.util.List)(_itemmap.Get((Object)("items"))));
 //BA.debugLineNum = 97;BA.debugLine="For i = 0 To 8  ' 只取9張圖片";
{
final double step62 = 1;
final double limit62 = (int)(8);
for (_i = (int)(0); (step62 > 0 && _i <= limit62) || (step62 < 0 && _i >= limit62); _i += step62) {
 //BA.debugLineNum = 98;BA.debugLine="Dim item, media As Map";
_item = new anywheresoftware.b4a.objects.collections.Map();
_media = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 99;BA.debugLine="item = itemList.Get(i)  ' 取得每一項目";
_item.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_itemlist.Get(_i)));
 //BA.debugLineNum = 100;BA.debugLine="media = item.Get(\"media\") ' 取出圖片URL";
_media.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_item.Get((Object)("media"))));
 //BA.debugLineNum = 101;BA.debugLine="Dim ImageJob As HttpJob   ' 建立下載圖片工作";
_imagejob = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 102;BA.debugLine="ImageJob.Initialize(\"ImageJob\", Me)";
_imagejob._initialize(processBA,"ImageJob",main.getObject());
 //BA.debugLineNum = 103;BA.debugLine="ImageJob.Download(media.Get(\"m\"))";
_imagejob._download(String.valueOf(_media.Get((Object)("m"))));
 }
};
 }else {
 //BA.debugLineNum = 106;BA.debugLine="ToastMessageShow(\"錯誤: \" & Job.ErrorMessage, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("錯誤: "+_job._errormessage,anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _resetimageview() throws Exception{
int _i = 0;
 //BA.debugLineNum = 56;BA.debugLine="Sub ResetImageView";
 //BA.debugLineNum = 58;BA.debugLine="For I = 0 To Activity.NumberOfViews - 1";
{
final double step30 = 1;
final double limit30 = (int)(mostCurrent._activity.getNumberOfViews()-1);
for (_i = (int)(0); (step30 > 0 && _i <= limit30) || (step30 < 0 && _i >= limit30); _i += step30) {
 //BA.debugLineNum = 60;BA.debugLine="If Activity.GetView(I) Is ImageView Then";
if (mostCurrent._activity.GetView(_i).getObjectOrNull() instanceof android.widget.ImageView) { 
 //BA.debugLineNum = 62;BA.debugLine="Activity.GetView(I).Color = Colors.White";
mostCurrent._activity.GetView(_i).setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 }
};
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
}
