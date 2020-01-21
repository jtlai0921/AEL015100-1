package ch05.gui.b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class myclass extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "ch05.gui.b4a.example.myclass");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
        }
        _class_globals();
    }


 public anywheresoftware.b4a.keywords.Common __c = null;
public String _name = "";
public long _birthday = 0L;
public ch05.gui.b4a.example.main _main = null;
public ch05.gui.b4a.example.mytool _mytool = null;
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 2;BA.debugLine="Sub Class_Globals";
 //BA.debugLineNum = 3;BA.debugLine="Private Name As String";
_name = "";
 //BA.debugLineNum = 4;BA.debugLine="Private Birthday As Long";
_birthday = 0L;
 //BA.debugLineNum = 5;BA.debugLine="End Sub";
return "";
}
public int  _getage() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Public Sub GetAge As Int";
 //BA.debugLineNum = 17;BA.debugLine="Return GetAgeAt(DateTime.Now)";
if (true) return _getageat(__c.DateTime.getNow());
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return 0;
}
public int  _getageat(long _now) throws Exception{
long _diff = 0L;
int _age = 0;
 //BA.debugLineNum = 20;BA.debugLine="Public Sub GetAgeAt(now As Long) As Int";
 //BA.debugLineNum = 21;BA.debugLine="Dim diff As Long";
_diff = 0L;
 //BA.debugLineNum = 22;BA.debugLine="Dim age As Int";
_age = 0;
 //BA.debugLineNum = 23;BA.debugLine="diff = now - Birthday";
_diff = (long)(_now-_birthday);
 //BA.debugLineNum = 24;BA.debugLine="age = Floor(diff / DateTime.TicksPerDay / 365)";
_age = (int)(__c.Floor(_diff/(double)__c.DateTime.TicksPerDay/(double)365));
 //BA.debugLineNum = 25;BA.debugLine="Return age";
if (true) return _age;
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return 0;
}
public String  _getname() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Public Sub GetName As String";
 //BA.debugLineNum = 13;BA.debugLine="Return Name";
if (true) return _name;
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public String  _initialize(anywheresoftware.b4a.BA _ba,String _aname,long _abirthday) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 7;BA.debugLine="Public Sub Initialize(aName As String, aBirthday As Long)";
 //BA.debugLineNum = 8;BA.debugLine="Name = aName";
_name = _aname;
 //BA.debugLineNum = 9;BA.debugLine="Birthday = aBirthday";
_birthday = _abirthday;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
ba.sharedProcessBA.sender = sender;
return BA.SubDelegator.SubNotFound;
}
}
