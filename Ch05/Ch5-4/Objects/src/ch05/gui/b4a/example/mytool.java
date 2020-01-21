package ch05.gui.b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class mytool {
private static mytool mostCurrent = new mytool();
public static Object getObject() {
    throw new RuntimeException("Code module does not support this method.");
}
 public anywheresoftware.b4a.keywords.Common __c = null;
public static String _title = "";
public ch05.gui.b4a.example.main _main = null;
public static int  _intdivide(anywheresoftware.b4a.BA _ba,int _op1,int _op2) throws Exception{
int _result = 0;
 //BA.debugLineNum = 7;BA.debugLine="Sub IntDivide(Op1 As Int, Op2 As Int) As Int";
 //BA.debugLineNum = 8;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 9;BA.debugLine="Result = Op1 / Op2";
_result = (int)(_op1/(double)_op2);
 //BA.debugLineNum = 10;BA.debugLine="Return Result";
if (true) return _result;
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return 0;
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 3;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 4;BA.debugLine="Dim Title As String : Title = \"我的函數庫\"";
_title = "";
 //BA.debugLineNum = 4;BA.debugLine="Dim Title As String : Title = \"我的函數庫\"";
_title = "我的函數庫";
 //BA.debugLineNum = 5;BA.debugLine="End Sub";
return "";
}
public static String  _sleep(anywheresoftware.b4a.BA _ba,long _ms) throws Exception{
long _now = 0L;
 //BA.debugLineNum = 13;BA.debugLine="Sub Sleep(ms As Long)";
 //BA.debugLineNum = 14;BA.debugLine="Dim now As Long";
_now = 0L;
 //BA.debugLineNum = 15;BA.debugLine="If ms > 1000 Then ms = 1000";
if (_ms>1000) { 
_ms = (long)(1000);};
 //BA.debugLineNum = 16;BA.debugLine="now = DateTime.now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 17;BA.debugLine="Do Until (DateTime.now > now + ms)";
while (!((anywheresoftware.b4a.keywords.Common.DateTime.getNow()>_now+_ms))) {
 //BA.debugLineNum = 18;BA.debugLine="DoEvents";
anywheresoftware.b4a.keywords.Common.DoEvents();
 }
;
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
}
