package ch16.gui.b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class shake {
private static shake mostCurrent = new shake();
public static Object getObject() {
    throw new RuntimeException("Code module does not support this method.");
}
 public static class _axisdata{
public boolean IsInitialized;
public int flipCount;
public float lastPeak;
public float lastValue;
public long timeStamp;
public void Initialize() {
IsInitialized = true;
flipCount = 0;
lastPeak = 0f;
lastValue = 0f;
timeStamp = 0L;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _tw = null;
public static boolean _recording = false;
public static ch16.gui.b4a.example.shake._axisdata _axisx = null;
public static float _magnitudethreshold = 0f;
public static int _timethreshold = 0;
public static String _callbackactivity = "";
public ch16.gui.b4a.example.main _main = null;
public static String  _calcaxis(anywheresoftware.b4a.BA _ba,float _v,ch16.gui.b4a.example.shake._axisdata _axis) throws Exception{
float _difference = 0f;
 //BA.debugLineNum = 29;BA.debugLine="Sub CalcAxis(v As Float, axis As AxisData)";
 //BA.debugLineNum = 30;BA.debugLine="Dim difference As Float";
_difference = 0f;
 //BA.debugLineNum = 31;BA.debugLine="difference = v - axis.lastValue";
_difference = (float)(_v-_axis.lastValue);
 //BA.debugLineNum = 32;BA.debugLine="axis.lastValue = v";
_axis.lastValue = _v;
 //BA.debugLineNum = 33;BA.debugLine="If Abs(difference) > MagnitudeThreshold Then";
if (anywheresoftware.b4a.keywords.Common.Abs(_difference)>_magnitudethreshold) { 
 //BA.debugLineNum = 34;BA.debugLine="If DateTime.Now - axis.timeStamp > TimeThreshold Then";
if (anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_axis.timeStamp>_timethreshold) { 
 //BA.debugLineNum = 35;BA.debugLine="axis.Initialize 'reset the data";
_axis.Initialize();
 };
 //BA.debugLineNum = 37;BA.debugLine="If axis.flipCount < 0 Then";
if (_axis.flipCount<0) { 
 //BA.debugLineNum = 38;BA.debugLine="Log(\"Shake.CalcAxis - still waiting\")";
anywheresoftware.b4a.keywords.Common.Log("Shake.CalcAxis - still waiting");
 //BA.debugLineNum = 39;BA.debugLine="Return 'this will happen immediately after a \"shake\" event.";
if (true) return "";
 };
 //BA.debugLineNum = 41;BA.debugLine="If axis.lastPeak = 0 OR (axis.lastPeak < 0 AND difference > 0) OR (axis.lastPeak > 0 AND difference < 0) Then";
if (_axis.lastPeak==0 || (_axis.lastPeak<0 && _difference>0) || (_axis.lastPeak>0 && _difference<0)) { 
 //BA.debugLineNum = 42;BA.debugLine="axis.flipCount = axis.flipCount + 1";
_axis.flipCount = (int)(_axis.flipCount+1);
 //BA.debugLineNum = 43;BA.debugLine="axis.lastPeak = difference";
_axis.lastPeak = _difference;
 //BA.debugLineNum = 44;BA.debugLine="axis.timeStamp = DateTime.Now";
_axis.timeStamp = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 45;BA.debugLine="If axis.flipCount = 2 Then";
if (_axis.flipCount==2) { 
 //BA.debugLineNum = 46;BA.debugLine="CallSub(CallBackActivity, \"ShakeEvent\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(_ba,(Object)(_callbackactivity),"ShakeEvent");
 //BA.debugLineNum = 48;BA.debugLine="axis.flipCount = -10";
_axis.flipCount = (int)(-10);
 };
 }else {
 //BA.debugLineNum = 51;BA.debugLine="axis.timeStamp = DateTime.Now";
_axis.timeStamp = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 };
 };
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _endrecording(anywheresoftware.b4a.BA _ba) throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub EndRecording";
 //BA.debugLineNum = 19;BA.debugLine="tw.Close";
_tw.Close();
 //BA.debugLineNum = 20;BA.debugLine="recording = False";
_recording = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static String  _handlesensorevent(anywheresoftware.b4a.BA _ba,float[] _values) throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub HandleSensorEvent(values() As Float)";
 //BA.debugLineNum = 23;BA.debugLine="If recording Then";
if (_recording) { 
 //BA.debugLineNum = 24;BA.debugLine="tw.Write(values(0) & \",\" & values(1) & \",\" & values(2) & Chr(13) & Chr(10)) 'we are not using CRLF as we want the Windows end of line characters";
_tw.Write(BA.NumberToString(_values[(int)(0)])+","+BA.NumberToString(_values[(int)(1)])+","+BA.NumberToString(_values[(int)(2)])+String.valueOf(anywheresoftware.b4a.keywords.Common.Chr((int)(13)))+String.valueOf(anywheresoftware.b4a.keywords.Common.Chr((int)(10))));
 }else {
 //BA.debugLineNum = 26;BA.debugLine="CalcAxis(values(0), AxisX)";
_calcaxis(_ba,_values[(int)(0)],_axisx);
 };
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 3;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 4;BA.debugLine="Dim tw As TextWriter";
_tw = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 5;BA.debugLine="Dim recording As Boolean";
_recording = false;
 //BA.debugLineNum = 6;BA.debugLine="Type AxisData (flipCount As Int, lastPeak As Float,lastValue As Float, timeStamp As Long)";
;
 //BA.debugLineNum = 7;BA.debugLine="Dim AxisX As AxisData";
_axisx = new ch16.gui.b4a.example.shake._axisdata();
 //BA.debugLineNum = 8;BA.debugLine="Dim MagnitudeThreshold As Float";
_magnitudethreshold = 0f;
 //BA.debugLineNum = 9;BA.debugLine="Dim TimeThreshold As Int";
_timethreshold = 0;
 //BA.debugLineNum = 10;BA.debugLine="MagnitudeThreshold = 11";
_magnitudethreshold = (float)(11);
 //BA.debugLineNum = 11;BA.debugLine="TimeThreshold = 1500 'milliseconds";
_timethreshold = (int)(1500);
 //BA.debugLineNum = 12;BA.debugLine="Dim CallBackActivity As String";
_callbackactivity = "";
 //BA.debugLineNum = 13;BA.debugLine="End Sub";
return "";
}
public static String  _startrecording(anywheresoftware.b4a.BA _ba,String _dir,String _filename) throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub StartRecording(Dir As String, FileName As String)";
 //BA.debugLineNum = 15;BA.debugLine="tw.Initialize(File.OpenOutput(Dir, FileName, False))";
_tw.Initialize((java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput(_dir,_filename,anywheresoftware.b4a.keywords.Common.False).getObject()));
 //BA.debugLineNum = 16;BA.debugLine="recording = True";
_recording = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
}
