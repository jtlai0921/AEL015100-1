package ch12.gui.b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class charts {
private static charts mostCurrent = new charts();
public static Object getObject() {
    throw new RuntimeException("Code module does not support this method.");
}
 public static class _pieitem{
public boolean IsInitialized;
public String Name;
public float Value;
public int Color;
public void Initialize() {
IsInitialized = true;
Name = "";
Value = 0f;
Color = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _piedata{
public boolean IsInitialized;
public anywheresoftware.b4a.objects.collections.List Items;
public anywheresoftware.b4a.objects.PanelWrapper Target;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper Canvas;
public int GapDegrees;
public float LegendTextSize;
public int LegendBackColor;
public void Initialize() {
IsInitialized = true;
Items = new anywheresoftware.b4a.objects.collections.List();
Target = new anywheresoftware.b4a.objects.PanelWrapper();
Canvas = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
GapDegrees = 0;
LegendTextSize = 0f;
LegendBackColor = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _graphinternal{
public boolean IsInitialized;
public int originX;
public int zeroY;
public int originY;
public int maxY;
public float intervalX;
public int gw;
public int gh;
public void Initialize() {
IsInitialized = true;
originX = 0;
zeroY = 0;
originY = 0;
maxY = 0;
intervalX = 0f;
gw = 0;
gh = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _graph{
public boolean IsInitialized;
public ch12.gui.b4a.example.charts._graphinternal GI;
public String Title;
public String YAxis;
public String XAxis;
public float YStart;
public float YEnd;
public float YInterval;
public int AxisColor;
public void Initialize() {
IsInitialized = true;
GI = new ch12.gui.b4a.example.charts._graphinternal();
Title = "";
YAxis = "";
XAxis = "";
YStart = 0f;
YEnd = 0f;
YInterval = 0f;
AxisColor = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _linepoint{
public boolean IsInitialized;
public String X;
public float Y;
public float[] YArray;
public boolean ShowTick;
public void Initialize() {
IsInitialized = true;
X = "";
Y = 0f;
YArray = new float[0];
;
ShowTick = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _linedata{
public boolean IsInitialized;
public anywheresoftware.b4a.objects.collections.List Points;
public anywheresoftware.b4a.objects.collections.List LinesColors;
public anywheresoftware.b4a.objects.PanelWrapper Target;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper Canvas;
public void Initialize() {
IsInitialized = true;
Points = new anywheresoftware.b4a.objects.collections.List();
LinesColors = new anywheresoftware.b4a.objects.collections.List();
Target = new anywheresoftware.b4a.objects.PanelWrapper();
Canvas = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _bardata{
public boolean IsInitialized;
public anywheresoftware.b4a.objects.collections.List Points;
public anywheresoftware.b4a.objects.collections.List BarsColors;
public anywheresoftware.b4a.objects.PanelWrapper Target;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper Canvas;
public boolean Stacked;
public int BarsWidth;
public void Initialize() {
IsInitialized = true;
Points = new anywheresoftware.b4a.objects.collections.List();
BarsColors = new anywheresoftware.b4a.objects.collections.List();
Target = new anywheresoftware.b4a.objects.PanelWrapper();
Canvas = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
Stacked = false;
BarsWidth = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public anywheresoftware.b4a.keywords.Common __c = null;
public ch12.gui.b4a.example.main _main = null;
public ch12.gui.b4a.example.linechart _linechart = null;
public static String  _addbarcolor(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._bardata _bd,int _color) throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub AddBarColor(BD As BarData, Color As Int)";
 //BA.debugLineNum = 36;BA.debugLine="If BD.BarsColors.IsInitialized = False Then BD.BarsColors.Initialize";
if (_bd.BarsColors.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
_bd.BarsColors.Initialize();};
 //BA.debugLineNum = 37;BA.debugLine="BD.BarsColors.Add(Color)";
_bd.BarsColors.Add((Object)(_color));
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _addbarpoint(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._bardata _bd,String _x,float[] _yarray) throws Exception{
ch12.gui.b4a.example.charts._linepoint _b = null;
 //BA.debugLineNum = 17;BA.debugLine="Sub AddBarPoint (BD As BarData, X As String, YArray() As Float)";
 //BA.debugLineNum = 18;BA.debugLine="If BD.Points.IsInitialized = False Then";
if (_bd.Points.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 19;BA.debugLine="BD.Points.Initialize";
_bd.Points.Initialize();
 //BA.debugLineNum = 21;BA.debugLine="Dim b As LinePoint";
_b = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 22;BA.debugLine="b.Initialize";
_b.Initialize();
 //BA.debugLineNum = 23;BA.debugLine="b.X = \"\"";
_b.X = "";
 //BA.debugLineNum = 24;BA.debugLine="b.ShowTick = False";
_b.ShowTick = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 25;BA.debugLine="BD.Points.Add(b)";
_bd.Points.Add((Object)(_b));
 };
 //BA.debugLineNum = 27;BA.debugLine="Dim b As LinePoint 'using the same structure of Line charts";
_b = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 28;BA.debugLine="b.Initialize";
_b.Initialize();
 //BA.debugLineNum = 29;BA.debugLine="b.X = X";
_b.X = _x;
 //BA.debugLineNum = 30;BA.debugLine="b.YArray = YArray";
_b.YArray = _yarray;
 //BA.debugLineNum = 31;BA.debugLine="b.ShowTick = True";
_b.ShowTick = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 32;BA.debugLine="BD.Points.Add(b)";
_bd.Points.Add((Object)(_b));
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _addlinecolor(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._linedata _ld,int _color) throws Exception{
 //BA.debugLineNum = 183;BA.debugLine="Sub AddLineColor(LD As LineData, Color As Int)";
 //BA.debugLineNum = 184;BA.debugLine="If LD.LinesColors.IsInitialized = False Then LD.LinesColors.Initialize";
if (_ld.LinesColors.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
_ld.LinesColors.Initialize();};
 //BA.debugLineNum = 185;BA.debugLine="LD.LinesColors.Add(Color)";
_ld.LinesColors.Add((Object)(_color));
 //BA.debugLineNum = 186;BA.debugLine="End Sub";
return "";
}
public static String  _addlinemultiplepoints(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._linedata _ld,String _x,float[] _yarray,boolean _showtick) throws Exception{
ch12.gui.b4a.example.charts._linepoint _p = null;
 //BA.debugLineNum = 173;BA.debugLine="Sub AddLineMultiplePoints(LD As LineData, X As String, YArray() As Float, ShowTick As Boolean)";
 //BA.debugLineNum = 174;BA.debugLine="If LD.Points.IsInitialized = False Then LD.Points.Initialize";
if (_ld.Points.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
_ld.Points.Initialize();};
 //BA.debugLineNum = 175;BA.debugLine="Dim p As LinePoint";
_p = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 176;BA.debugLine="p.Initialize";
_p.Initialize();
 //BA.debugLineNum = 177;BA.debugLine="p.X = X";
_p.X = _x;
 //BA.debugLineNum = 178;BA.debugLine="p.YArray = YArray";
_p.YArray = _yarray;
 //BA.debugLineNum = 179;BA.debugLine="p.ShowTick = ShowTick";
_p.ShowTick = _showtick;
 //BA.debugLineNum = 180;BA.debugLine="LD.Points.Add(p)";
_ld.Points.Add((Object)(_p));
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _addlinepoint(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._linedata _ld,String _x,float _y,boolean _showtick) throws Exception{
ch12.gui.b4a.example.charts._linepoint _p = null;
 //BA.debugLineNum = 163;BA.debugLine="Sub AddLinePoint (LD As LineData, X As String, Y As Float, ShowTick As Boolean)";
 //BA.debugLineNum = 164;BA.debugLine="If LD.Points.IsInitialized = False Then LD.Points.Initialize";
if (_ld.Points.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
_ld.Points.Initialize();};
 //BA.debugLineNum = 165;BA.debugLine="Dim p As LinePoint";
_p = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 166;BA.debugLine="p.Initialize";
_p.Initialize();
 //BA.debugLineNum = 167;BA.debugLine="p.X = X";
_p.X = _x;
 //BA.debugLineNum = 168;BA.debugLine="p.Y = Y";
_p.Y = _y;
 //BA.debugLineNum = 169;BA.debugLine="p.ShowTick = ShowTick";
_p.ShowTick = _showtick;
 //BA.debugLineNum = 170;BA.debugLine="LD.Points.Add(p)";
_ld.Points.Add((Object)(_p));
 //BA.debugLineNum = 171;BA.debugLine="End Sub";
return "";
}
public static String  _addpieitem(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._piedata _pd,String _name,float _value,int _color) throws Exception{
ch12.gui.b4a.example.charts._pieitem _i = null;
 //BA.debugLineNum = 238;BA.debugLine="Sub AddPieItem(PD As PieData, Name As String, Value As Float, Color As Int)";
 //BA.debugLineNum = 239;BA.debugLine="If PD.Items.IsInitialized = False Then PD.Items.Initialize";
if (_pd.Items.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
_pd.Items.Initialize();};
 //BA.debugLineNum = 240;BA.debugLine="If Color = 0 Then Color = Colors.RGB(Rnd(0, 255), Rnd(0, 255), Rnd(0, 255))";
if (_color==0) { 
_color = anywheresoftware.b4a.keywords.Common.Colors.RGB(anywheresoftware.b4a.keywords.Common.Rnd((int)(0),(int)(255)),anywheresoftware.b4a.keywords.Common.Rnd((int)(0),(int)(255)),anywheresoftware.b4a.keywords.Common.Rnd((int)(0),(int)(255)));};
 //BA.debugLineNum = 241;BA.debugLine="Dim i As PieItem";
_i = new ch12.gui.b4a.example.charts._pieitem();
 //BA.debugLineNum = 242;BA.debugLine="i.Initialize";
_i.Initialize();
 //BA.debugLineNum = 243;BA.debugLine="i.Name = Name";
_i.Name = _name;
 //BA.debugLineNum = 244;BA.debugLine="i.Value = Value";
_i.Value = _value;
 //BA.debugLineNum = 245;BA.debugLine="i.Color = Color";
_i.Color = _color;
 //BA.debugLineNum = 246;BA.debugLine="PD.Items.Add(i)";
_pd.Items.Add((Object)(_i));
 //BA.debugLineNum = 247;BA.debugLine="End Sub";
return "";
}
public static int  _calcpointtopixel(anywheresoftware.b4a.BA _ba,float _py,ch12.gui.b4a.example.charts._graph _g) throws Exception{
 //BA.debugLineNum = 228;BA.debugLine="Sub calcPointToPixel(py As Float, G As Graph) As Int";
 //BA.debugLineNum = 229;BA.debugLine="If G.YStart < 0 AND G.YEnd > 0 Then";
if (_g.YStart<0 && _g.YEnd>0) { 
 //BA.debugLineNum = 230;BA.debugLine="Return G.GI.zeroY - (G.GI.originY - G.GI.maxY) * py / (G.YEnd - G.YStart)";
if (true) return (int)(_g.GI.zeroY-(_g.GI.originY-_g.GI.maxY)*_py/(double)(_g.YEnd-_g.YStart));
 }else {
 //BA.debugLineNum = 232;BA.debugLine="Return G.GI.originY - (G.GI.originY - G.GI.maxY) * (py - G.YStart) / (G.YEnd - G.YStart)";
if (true) return (int)(_g.GI.originY-(_g.GI.originY-_g.GI.maxY)*(_py-_g.YStart)/(double)(_g.YEnd-_g.YStart));
 };
 //BA.debugLineNum = 234;BA.debugLine="End Sub";
return 0;
}
public static float  _calcslice(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.drawable.CanvasWrapper _canvas,int _radius,float _startingdegree,float _percent,int _color,int _gapdegrees) throws Exception{
float _b = 0f;
int _cx = 0;
int _cy = 0;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.PathWrapper _p = null;
float _gap = 0f;
int _i = 0;
 //BA.debugLineNum = 283;BA.debugLine="Sub calcSlice(Canvas As Canvas, Radius As Int, _  		StartingDegree As Float, Percent As Float, Color As Int, GapDegrees As Int) As Float";
 //BA.debugLineNum = 285;BA.debugLine="Dim b As Float";
_b = 0f;
 //BA.debugLineNum = 286;BA.debugLine="b = 360 * Percent";
_b = (float)(360*_percent);
 //BA.debugLineNum = 288;BA.debugLine="Dim cx, cy As Int";
_cx = 0;
_cy = 0;
 //BA.debugLineNum = 289;BA.debugLine="cx = Canvas.Bitmap.Width / 2";
_cx = (int)(_canvas.getBitmap().getWidth()/(double)2);
 //BA.debugLineNum = 290;BA.debugLine="cy = Canvas.Bitmap.Height / 2";
_cy = (int)(_canvas.getBitmap().getHeight()/(double)2);
 //BA.debugLineNum = 291;BA.debugLine="Dim p As Path";
_p = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.PathWrapper();
 //BA.debugLineNum = 292;BA.debugLine="p.Initialize(cx, cy)";
_p.Initialize((float)(_cx),(float)(_cy));
 //BA.debugLineNum = 293;BA.debugLine="Dim gap As Float";
_gap = 0f;
 //BA.debugLineNum = 294;BA.debugLine="gap = Percent * GapDegrees / 2";
_gap = (float)(_percent*_gapdegrees/(double)2);
 //BA.debugLineNum = 295;BA.debugLine="For i = StartingDegree + gap To StartingDegree + b - gap Step 10";
{
final double step252 = 10;
final double limit252 = (int)(_startingdegree+_b-_gap);
for (_i = (int)(_startingdegree+_gap); (step252 > 0 && _i <= limit252) || (step252 < 0 && _i >= limit252); _i += step252) {
 //BA.debugLineNum = 296;BA.debugLine="p.LineTo(cx + 2 * Radius * SinD(i), cy + 2 * Radius * CosD(i))";
_p.LineTo((float)(_cx+2*_radius*anywheresoftware.b4a.keywords.Common.SinD(_i)),(float)(_cy+2*_radius*anywheresoftware.b4a.keywords.Common.CosD(_i)));
 }
};
 //BA.debugLineNum = 298;BA.debugLine="p.LineTo(cx + 2 * Radius * SinD(StartingDegree + b - gap), cy + 2 * Radius * CosD(StartingDegree + b - gap))";
_p.LineTo((float)(_cx+2*_radius*anywheresoftware.b4a.keywords.Common.SinD(_startingdegree+_b-_gap)),(float)(_cy+2*_radius*anywheresoftware.b4a.keywords.Common.CosD(_startingdegree+_b-_gap)));
 //BA.debugLineNum = 299;BA.debugLine="p.LineTo(cx, cy)";
_p.LineTo((float)(_cx),(float)(_cy));
 //BA.debugLineNum = 300;BA.debugLine="Canvas.ClipPath(p) 'We are limiting the drawings to the required slice";
_canvas.ClipPath((android.graphics.Path)(_p.getObject()));
 //BA.debugLineNum = 301;BA.debugLine="Canvas.DrawCircle(cx, cy, Radius, Color, True, 0)";
_canvas.DrawCircle((float)(_cx),(float)(_cy),(float)(_radius),_color,anywheresoftware.b4a.keywords.Common.True,(float)(0));
 //BA.debugLineNum = 302;BA.debugLine="Canvas.RemoveClip";
_canvas.RemoveClip();
 //BA.debugLineNum = 303;BA.debugLine="Return b";
if (true) return _b;
 //BA.debugLineNum = 304;BA.debugLine="End Sub";
return 0f;
}
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper  _createlegend(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._piedata _pd) throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
float _textheight = 0f;
float _textwidth = 0f;
int _i = 0;
ch12.gui.b4a.example.charts._pieitem _it = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper _c = null;
 //BA.debugLineNum = 306;BA.debugLine="Sub createLegend(PD As PieData) As Bitmap";
 //BA.debugLineNum = 307;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 308;BA.debugLine="If PD.LegendTextSize = 0 Then PD.LegendTextSize = 15";
if (_pd.LegendTextSize==0) { 
_pd.LegendTextSize = (float)(15);};
 //BA.debugLineNum = 309;BA.debugLine="Dim textHeight, textWidth As Float";
_textheight = 0f;
_textwidth = 0f;
 //BA.debugLineNum = 310;BA.debugLine="textHeight = PD.Canvas.MeasureStringHeight(\"M\", Typeface.DEFAULT_BOLD, PD.LegendTextSize)";
_textheight = _pd.Canvas.MeasureStringHeight("M",anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD,_pd.LegendTextSize);
 //BA.debugLineNum = 311;BA.debugLine="For i = 0 To PD.Items.Size - 1";
{
final double step267 = 1;
final double limit267 = (int)(_pd.Items.getSize()-1);
for (_i = (int)(0); (step267 > 0 && _i <= limit267) || (step267 < 0 && _i >= limit267); _i += step267) {
 //BA.debugLineNum = 312;BA.debugLine="Dim it As PieItem";
_it = new ch12.gui.b4a.example.charts._pieitem();
 //BA.debugLineNum = 313;BA.debugLine="it = PD.Items.Get(i)";
_it = (ch12.gui.b4a.example.charts._pieitem)(_pd.Items.Get(_i));
 //BA.debugLineNum = 314;BA.debugLine="textWidth = Max(textWidth, PD.Canvas.MeasureStringWidth(it.Name, Typeface.DEFAULT_BOLD, PD.LegendTextSize))";
_textwidth = (float)(anywheresoftware.b4a.keywords.Common.Max(_textwidth,_pd.Canvas.MeasureStringWidth(_it.Name,anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD,_pd.LegendTextSize)));
 }
};
 //BA.debugLineNum = 316;BA.debugLine="bmp.InitializeMutable(textWidth + 20dip, 10dip +(textHeight + 10dip) * PD.Items.Size)";
_bmp.InitializeMutable((int)(_textwidth+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(20))),(int)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(10))+(_textheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(10)))*_pd.Items.getSize()));
 //BA.debugLineNum = 317;BA.debugLine="Dim c As Canvas";
_c = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 318;BA.debugLine="c.Initialize2(bmp)";
_c.Initialize2((android.graphics.Bitmap)(_bmp.getObject()));
 //BA.debugLineNum = 319;BA.debugLine="c.DrawColor(PD.LegendBackColor)";
_c.DrawColor(_pd.LegendBackColor);
 //BA.debugLineNum = 320;BA.debugLine="For i = 0 To PD.Items.Size - 1";
{
final double step276 = 1;
final double limit276 = (int)(_pd.Items.getSize()-1);
for (_i = (int)(0); (step276 > 0 && _i <= limit276) || (step276 < 0 && _i >= limit276); _i += step276) {
 //BA.debugLineNum = 321;BA.debugLine="Dim it As PieItem";
_it = new ch12.gui.b4a.example.charts._pieitem();
 //BA.debugLineNum = 322;BA.debugLine="it = PD.Items.Get(i)";
_it = (ch12.gui.b4a.example.charts._pieitem)(_pd.Items.Get(_i));
 //BA.debugLineNum = 323;BA.debugLine="c.DrawText(it.Name, 10dip, (i + 1) * (textHeight + 10dip), Typeface.DEFAULT_BOLD, PD.LegendTextSize, _ 			it.Color, \"LEFT\")";
_c.DrawText(_ba,_it.Name,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(10))),(float)((_i+1)*(_textheight+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(10)))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD,_pd.LegendTextSize,_it.Color,BA.getEnumFromString(android.graphics.Paint.Align.class,"LEFT"));
 }
};
 //BA.debugLineNum = 326;BA.debugLine="Return bmp";
if (true) return _bmp;
 //BA.debugLineNum = 327;BA.debugLine="End Sub";
return null;
}
public static String  _drawbarschart(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._graph _g,ch12.gui.b4a.example.charts._bardata _bd,int _backcolor) throws Exception{
ch12.gui.b4a.example.charts._linepoint _point = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect = null;
int _numberofbars = 0;
int _i = 0;
int _a = 0;
 //BA.debugLineNum = 40;BA.debugLine="Sub DrawBarsChart(G As Graph, BD As BarData, BackColor As Int)";
 //BA.debugLineNum = 41;BA.debugLine="If BD.Points.Size = 0 Then";
if (_bd.Points.getSize()==0) { 
 //BA.debugLineNum = 42;BA.debugLine="ToastMessageShow(\"Missing bars points.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Missing bars points.",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 43;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 45;BA.debugLine="BD.Canvas.Initialize(BD.Target)";
_bd.Canvas.Initialize((android.view.View)(_bd.Target.getObject()));
 //BA.debugLineNum = 46;BA.debugLine="BD.Canvas.DrawColor(BackColor)";
_bd.Canvas.DrawColor(_backcolor);
 //BA.debugLineNum = 47;BA.debugLine="Dim point As LinePoint";
_point = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 48;BA.debugLine="point = BD.Points.Get(1)";
_point = (ch12.gui.b4a.example.charts._linepoint)(_bd.Points.Get((int)(1)));
 //BA.debugLineNum = 49;BA.debugLine="If BD.Stacked = False Then";
if (_bd.Stacked==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 50;BA.debugLine="drawGraph(G, BD.Canvas, BD.Target, BD.Points, True, BD.BarsWidth)";
_drawgraph(_ba,_g,_bd.Canvas,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_bd.Target.getObject())),_bd.Points,anywheresoftware.b4a.keywords.Common.True,_bd.BarsWidth);
 }else {
 //BA.debugLineNum = 53;BA.debugLine="drawGraph(G, BD.Canvas, BD.Target, BD.Points, True, BD.BarsWidth / point.YArray.Length)";
_drawgraph(_ba,_g,_bd.Canvas,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_bd.Target.getObject())),_bd.Points,anywheresoftware.b4a.keywords.Common.True,(int)(_bd.BarsWidth/(double)_point.YArray.length));
 };
 //BA.debugLineNum = 57;BA.debugLine="Dim Rect As Rect";
_rect = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Rect.Initialize(0, 0, 0, G.GI.originY)";
_rect.Initialize((int)(0),(int)(0),(int)(0),_g.GI.originY);
 //BA.debugLineNum = 59;BA.debugLine="Dim numberOfBars As Int";
_numberofbars = 0;
 //BA.debugLineNum = 60;BA.debugLine="numberOfBars = point.YArray.Length";
_numberofbars = _point.YArray.length;
 //BA.debugLineNum = 62;BA.debugLine="For i = 1 To BD.Points.Size - 1";
{
final double step47 = 1;
final double limit47 = (int)(_bd.Points.getSize()-1);
for (_i = (int)(1); (step47 > 0 && _i <= limit47) || (step47 < 0 && _i >= limit47); _i += step47) {
 //BA.debugLineNum = 63;BA.debugLine="point = BD.Points.Get(i)";
_point = (ch12.gui.b4a.example.charts._linepoint)(_bd.Points.Get(_i));
 //BA.debugLineNum = 64;BA.debugLine="For a = 0 To numberOfBars - 1";
{
final double step49 = 1;
final double limit49 = (int)(_numberofbars-1);
for (_a = (int)(0); (step49 > 0 && _a <= limit49) || (step49 < 0 && _a >= limit49); _a += step49) {
 //BA.debugLineNum = 65;BA.debugLine="If BD.Stacked = False Then";
if (_bd.Stacked==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 66;BA.debugLine="Rect.Left = G.GI.originX + G.GI.intervalX * i + (a - numberOfBars / 2) * BD.BarsWidth";
_rect.setLeft((int)(_g.GI.originX+_g.GI.intervalX*_i+(_a-_numberofbars/(double)2)*_bd.BarsWidth));
 //BA.debugLineNum = 67;BA.debugLine="Rect.Right = Rect.Left + BD.BarsWidth";
_rect.setRight((int)(_rect.getLeft()+_bd.BarsWidth));
 //BA.debugLineNum = 68;BA.debugLine="If G.YStart < 0 AND G.YEnd > 0 Then";
if (_g.YStart<0 && _g.YEnd>0) { 
 //BA.debugLineNum = 69;BA.debugLine="If point.YArray(a) > 0 Then";
if (_point.YArray[_a]>0) { 
 //BA.debugLineNum = 70;BA.debugLine="Rect.Top = calcPointToPixel(point.YArray(a), G)";
_rect.setTop(_calcpointtopixel(_ba,_point.YArray[_a],_g));
 //BA.debugLineNum = 71;BA.debugLine="Rect.Bottom = G.GI.zeroY";
_rect.setBottom(_g.GI.zeroY);
 }else {
 //BA.debugLineNum = 73;BA.debugLine="Rect.Bottom = calcPointToPixel(point.YArray(a), G)";
_rect.setBottom(_calcpointtopixel(_ba,_point.YArray[_a],_g));
 //BA.debugLineNum = 74;BA.debugLine="Rect.Top = G.GI.zeroY";
_rect.setTop(_g.GI.zeroY);
 };
 }else {
 //BA.debugLineNum = 77;BA.debugLine="Rect.Top = calcPointToPixel(point.YArray(a), G)";
_rect.setTop(_calcpointtopixel(_ba,_point.YArray[_a],_g));
 //BA.debugLineNum = 78;BA.debugLine="Rect.Bottom = G.GI.originY";
_rect.setBottom(_g.GI.originY);
 };
 }else {
 //BA.debugLineNum = 81;BA.debugLine="Rect.Left = G.GI.originX + G.GI.intervalX * i - BD.BarsWidth / 2";
_rect.setLeft((int)(_g.GI.originX+_g.GI.intervalX*_i-_bd.BarsWidth/(double)2));
 //BA.debugLineNum = 82;BA.debugLine="Rect.Right = Rect.Left + BD.BarsWidth";
_rect.setRight((int)(_rect.getLeft()+_bd.BarsWidth));
 //BA.debugLineNum = 83;BA.debugLine="If a = 0 Then";
if (_a==0) { 
 //BA.debugLineNum = 84;BA.debugLine="Rect.Top = calcPointToPixel(0, G)";
_rect.setTop(_calcpointtopixel(_ba,(float)(0),_g));
 };
 //BA.debugLineNum = 86;BA.debugLine="Rect.Bottom = Rect.Top";
_rect.setBottom(_rect.getTop());
 //BA.debugLineNum = 87;BA.debugLine="Rect.Top = Rect.Bottom + calcPointToPixel(point.YArray(a), G) - G.GI.originY";
_rect.setTop((int)(_rect.getBottom()+_calcpointtopixel(_ba,_point.YArray[_a],_g)-_g.GI.originY));
 };
 //BA.debugLineNum = 89;BA.debugLine="BD.Canvas.DrawRect(Rect, BD.BarsColors.Get(a), True, 1dip)";
_bd.Canvas.DrawRect((android.graphics.Rect)(_rect.getObject()),(int)(BA.ObjectToNumber(_bd.BarsColors.Get(_a))),anywheresoftware.b4a.keywords.Common.True,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(1))));
 }
};
 }
};
 //BA.debugLineNum = 92;BA.debugLine="BD.Target.Invalidate";
_bd.Target.Invalidate();
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return "";
}
public static String  _drawgraph(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._graph _g,anywheresoftware.b4a.objects.drawable.CanvasWrapper _canvas,anywheresoftware.b4a.objects.ConcreteViewWrapper _target,anywheresoftware.b4a.objects.collections.List _points,boolean _bars,int _barswidth) throws Exception{
ch12.gui.b4a.example.charts._graphinternal _gi = null;
ch12.gui.b4a.example.charts._linepoint _p = null;
int _i = 0;
int _y = 0;
float _yvalue = 0f;
int _x = 0;
 //BA.debugLineNum = 97;BA.debugLine="Sub drawGraph (G As Graph, Canvas As Canvas, Target As View, Points As List, Bars As Boolean, BarsWidth As Int)";
 //BA.debugLineNum = 98;BA.debugLine="Dim GI As GraphInternal";
_gi = new ch12.gui.b4a.example.charts._graphinternal();
 //BA.debugLineNum = 99;BA.debugLine="G.GI = GI";
_g.GI = _gi;
 //BA.debugLineNum = 100;BA.debugLine="GI.Initialize";
_gi.Initialize();
 //BA.debugLineNum = 101;BA.debugLine="GI.maxY = 40dip											' top margin";
_gi.maxY = anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(40));
 //BA.debugLineNum = 102;BA.debugLine="GI.originX = 50dip									' left margin";
_gi.originX = anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(50));
 //BA.debugLineNum = 103;BA.debugLine="GI.originY = Target.Height - 60dip	' 60dip = right margin";
_gi.originY = (int)(_target.getHeight()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(60)));
 //BA.debugLineNum = 104;BA.debugLine="GI.gw = Target.Width - 70dip				' graph width 70dip = bottom margin";
_gi.gw = (int)(_target.getWidth()-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(70)));
 //BA.debugLineNum = 105;BA.debugLine="GI.gh = GI.originY - GI.maxY				' graph height";
_gi.gh = (int)(_gi.originY-_gi.maxY);
 //BA.debugLineNum = 106;BA.debugLine="If G.YStart < 0 AND G.YEnd > 0 Then";
if (_g.YStart<0 && _g.YEnd>0) { 
 //BA.debugLineNum = 107;BA.debugLine="GI.zeroY = GI.maxY + GI.gh * G.YEnd / (G.YEnd - G.YStart)";
_gi.zeroY = (int)(_gi.maxY+_gi.gh*_g.YEnd/(double)(_g.YEnd-_g.YStart));
 }else {
 //BA.debugLineNum = 109;BA.debugLine="GI.zeroY = GI.originY";
_gi.zeroY = _gi.originY;
 };
 //BA.debugLineNum = 111;BA.debugLine="If Bars Then";
if (_bars) { 
 //BA.debugLineNum = 112;BA.debugLine="Dim p As LinePoint";
_p = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 113;BA.debugLine="p = Points.Get(1)";
_p = (ch12.gui.b4a.example.charts._linepoint)(_points.Get((int)(1)));
 //BA.debugLineNum = 114;BA.debugLine="GI.intervalX = (GI.gw - p.YArray.Length / 2 * BarsWidth) / (Points.Size - 1)";
_gi.intervalX = (float)((_gi.gw-_p.YArray.length/(double)2*_barswidth)/(double)(_points.getSize()-1));
 }else {
 //BA.debugLineNum = 116;BA.debugLine="GI.intervalX = GI.gw / (Points.Size - 1)";
_gi.intervalX = (float)(_gi.gw/(double)(_points.getSize()-1));
 };
 //BA.debugLineNum = 119;BA.debugLine="Canvas.DrawLine(GI.originX, GI.originY + 2dip, GI.originX, GI.maxY - 2dip, G.AxisColor, 2dip)";
_canvas.DrawLine((float)(_gi.originX),(float)(_gi.originY+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))),(float)(_gi.originX),(float)(_gi.maxY-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 //BA.debugLineNum = 120;BA.debugLine="For i = 0 To (G.YEnd - G.YStart) / G.Yinterval + 1";
{
final double step101 = 1;
final double limit101 = (int)((_g.YEnd-_g.YStart)/(double)_g.YInterval+1);
for (_i = (int)(0); (step101 > 0 && _i <= limit101) || (step101 < 0 && _i >= limit101); _i += step101) {
 //BA.debugLineNum = 121;BA.debugLine="Dim y As Int";
_y = 0;
 //BA.debugLineNum = 122;BA.debugLine="Dim yValue As Float";
_yvalue = 0f;
 //BA.debugLineNum = 123;BA.debugLine="yValue = G.YStart + G.YInterval * i";
_yvalue = (float)(_g.YStart+_g.YInterval*_i);
 //BA.debugLineNum = 124;BA.debugLine="If yValue > G.YEnd Then Continue";
if (_yvalue>_g.YEnd) { 
if (true) continue;};
 //BA.debugLineNum = 125;BA.debugLine="y = calcPointToPixel(yValue, G)";
_y = _calcpointtopixel(_ba,_yvalue,_g);
 //BA.debugLineNum = 126;BA.debugLine="Canvas.DrawLine(GI.originX, y, GI.originX - 5dip, y, G.AxisColor, 2dip)";
_canvas.DrawLine((float)(_gi.originX),(float)(_y),(float)(_gi.originX-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5))),(float)(_y),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 //BA.debugLineNum = 127;BA.debugLine="If i < (G.YEnd - G.YStart) / G.Yinterval Then";
if (_i<(_g.YEnd-_g.YStart)/(double)_g.YInterval) { 
 //BA.debugLineNum = 128;BA.debugLine="Canvas.DrawLine(GI.originX, y, GI.originX + GI.gw, y, G.AxisColor, 1dip)";
_canvas.DrawLine((float)(_gi.originX),(float)(_y),(float)(_gi.originX+_gi.gw),(float)(_y),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(1))));
 }else {
 //BA.debugLineNum = 130;BA.debugLine="Canvas.DrawLine(GI.originX, y, GI.originX + GI.gw, y, G.AxisColor, 2dip)";
_canvas.DrawLine((float)(_gi.originX),(float)(_y),(float)(_gi.originX+_gi.gw),(float)(_y),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 };
 //BA.debugLineNum = 132;BA.debugLine="Canvas.DrawText(NumberFormat(yValue, 1, 2), GI.originX - 8dip, y + 5dip,Typeface.DEFAULT, 12, G.AxisColor, \"RIGHT\")";
_canvas.DrawText(_ba,anywheresoftware.b4a.keywords.Common.NumberFormat(_yvalue,(int)(1),(int)(2)),(float)(_gi.originX-anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(8))),(float)(_y+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT,(float)(12),_g.AxisColor,BA.getEnumFromString(android.graphics.Paint.Align.class,"RIGHT"));
 }
};
 //BA.debugLineNum = 135;BA.debugLine="Canvas.DrawText(G.Title, Target.Width / 2, 30dip, Typeface.DEFAULT_BOLD, 15, G.AxisColor, \"CENTER\")";
_canvas.DrawText(_ba,_g.Title,(float)(_target.getWidth()/(double)2),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(30))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD,(float)(15),_g.AxisColor,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 136;BA.debugLine="Canvas.DrawText(G.XAxis, Target.Width / 2, GI.originY + 45dip, Typeface.DEFAULT, 14, G.AxisColor, \"CENTER\")";
_canvas.DrawText(_ba,_g.XAxis,(float)(_target.getWidth()/(double)2),(float)(_gi.originY+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(45))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT,(float)(14),_g.AxisColor,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"));
 //BA.debugLineNum = 137;BA.debugLine="Canvas.DrawTextRotated(G.YAxis, 15dip, Target.Height / 2, Typeface.DEFAULT, 14, G.AxisColor, \"CENTER\", -90)";
_canvas.DrawTextRotated(_ba,_g.YAxis,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(15))),(float)(_target.getHeight()/(double)2),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT,(float)(14),_g.AxisColor,BA.getEnumFromString(android.graphics.Paint.Align.class,"CENTER"),(float)(-90));
 //BA.debugLineNum = 139;BA.debugLine="Canvas.DrawLine(GI.originX, GI.originY, GI.originX + GI.gw, GI.originY, G.AxisColor, 2dip)";
_canvas.DrawLine((float)(_gi.originX),(float)(_gi.originY),(float)(_gi.originX+_gi.gw),(float)(_gi.originY),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 //BA.debugLineNum = 140;BA.debugLine="For i = 0 To Points.Size - 1";
{
final double step119 = 1;
final double limit119 = (int)(_points.getSize()-1);
for (_i = (int)(0); (step119 > 0 && _i <= limit119) || (step119 < 0 && _i >= limit119); _i += step119) {
 //BA.debugLineNum = 141;BA.debugLine="Dim p As LinePoint";
_p = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 142;BA.debugLine="p = Points.Get(i)";
_p = (ch12.gui.b4a.example.charts._linepoint)(_points.Get(_i));
 //BA.debugLineNum = 143;BA.debugLine="If p.ShowTick Then";
if (_p.ShowTick) { 
 //BA.debugLineNum = 144;BA.debugLine="Dim x As Int";
_x = 0;
 //BA.debugLineNum = 145;BA.debugLine="x = GI.originX + i * GI.intervalX";
_x = (int)(_gi.originX+_i*_gi.intervalX);
 //BA.debugLineNum = 146;BA.debugLine="Canvas.DrawLine(x, GI.originY, x, GI.originY + 5dip, G.AxisColor, 2dip)";
_canvas.DrawLine((float)(_x),(float)(_gi.originY),(float)(_x),(float)(_gi.originY+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(5))),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 //BA.debugLineNum = 147;BA.debugLine="If Bars = False Then";
if (_bars==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 148;BA.debugLine="If i < Points.Size - 1 Then";
if (_i<_points.getSize()-1) { 
 //BA.debugLineNum = 149;BA.debugLine="Canvas.DrawLine(x, GI.originY, x, GI.maxY, G.AxisColor, 1dip) 'vertical lines";
_canvas.DrawLine((float)(_x),(float)(_gi.originY),(float)(_x),(float)(_gi.maxY),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(1))));
 }else {
 //BA.debugLineNum = 151;BA.debugLine="Canvas.DrawLine(x, GI.originY, x, GI.maxY, G.AxisColor, 2dip) 'last vertical line";
_canvas.DrawLine((float)(_x),(float)(_gi.originY),(float)(_x),(float)(_gi.maxY),_g.AxisColor,(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 };
 };
 //BA.debugLineNum = 154;BA.debugLine="If p.x.Length > 0 Then";
if (_p.X.length()>0) { 
 //BA.debugLineNum = 155;BA.debugLine="Canvas.DrawTextRotated(p.x, x, GI.originY + 12dip, Typeface.DEFAULT, 12, G.AxisColor, \"RIGHT\", -45)";
_canvas.DrawTextRotated(_ba,_p.X,(float)(_x),(float)(_gi.originY+anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(12))),anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT,(float)(12),_g.AxisColor,BA.getEnumFromString(android.graphics.Paint.Align.class,"RIGHT"),(float)(-45));
 };
 };
 }
};
 //BA.debugLineNum = 159;BA.debugLine="End Sub";
return "";
}
public static String  _drawlinechart(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._graph _g,ch12.gui.b4a.example.charts._linedata _ld,int _backcolor) throws Exception{
ch12.gui.b4a.example.charts._linepoint _point = null;
float[] _py2 = null;
int _i = 0;
int _a = 0;
float _py = 0f;
 //BA.debugLineNum = 188;BA.debugLine="Sub DrawLineChart(G As Graph, LD As LineData, BackColor As Int)";
 //BA.debugLineNum = 189;BA.debugLine="If LD.Points.Size = 0 Then";
if (_ld.Points.getSize()==0) { 
 //BA.debugLineNum = 190;BA.debugLine="ToastMessageShow(\"Missing line points.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Missing line points.",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 191;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 193;BA.debugLine="LD.Canvas.Initialize(LD.Target)";
_ld.Canvas.Initialize((android.view.View)(_ld.Target.getObject()));
 //BA.debugLineNum = 194;BA.debugLine="LD.Canvas.DrawColor(BackColor)";
_ld.Canvas.DrawColor(_backcolor);
 //BA.debugLineNum = 195;BA.debugLine="drawGraph(G, LD.Canvas, LD.Target, LD.Points, False, 0)";
_drawgraph(_ba,_g,_ld.Canvas,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(_ld.Target.getObject())),_ld.Points,anywheresoftware.b4a.keywords.Common.False,(int)(0));
 //BA.debugLineNum = 197;BA.debugLine="Dim point As LinePoint";
_point = new ch12.gui.b4a.example.charts._linepoint();
 //BA.debugLineNum = 198;BA.debugLine="point = LD.Points.Get(0)";
_point = (ch12.gui.b4a.example.charts._linepoint)(_ld.Points.Get((int)(0)));
 //BA.debugLineNum = 199;BA.debugLine="If point.YArray.Length > 0 Then";
if (_point.YArray.length>0) { 
 //BA.debugLineNum = 201;BA.debugLine="Dim py2(point.YArray.Length) As Float";
_py2 = new float[_point.YArray.length];
;
 //BA.debugLineNum = 203;BA.debugLine="For i = 0 To py2.Length - 1";
{
final double step173 = 1;
final double limit173 = (int)(_py2.length-1);
for (_i = (int)(0); (step173 > 0 && _i <= limit173) || (step173 < 0 && _i >= limit173); _i += step173) {
 //BA.debugLineNum = 204;BA.debugLine="py2(i) = point.YArray(i)";
_py2[_i] = _point.YArray[_i];
 }
};
 //BA.debugLineNum = 207;BA.debugLine="For i = 1 To LD.Points.Size - 1";
{
final double step176 = 1;
final double limit176 = (int)(_ld.Points.getSize()-1);
for (_i = (int)(1); (step176 > 0 && _i <= limit176) || (step176 < 0 && _i >= limit176); _i += step176) {
 //BA.debugLineNum = 208;BA.debugLine="point = LD.Points.Get(i)";
_point = (ch12.gui.b4a.example.charts._linepoint)(_ld.Points.Get(_i));
 //BA.debugLineNum = 209;BA.debugLine="For a = 0 To py2.Length - 1";
{
final double step178 = 1;
final double limit178 = (int)(_py2.length-1);
for (_a = (int)(0); (step178 > 0 && _a <= limit178) || (step178 < 0 && _a >= limit178); _a += step178) {
 //BA.debugLineNum = 210;BA.debugLine="LD.Canvas.DrawLine(G.GI.originX + G.GI.intervalX * (i - 1), calcPointToPixel(py2(a), G), G.GI.originX + G.GI.intervalX * i, calcPointToPixel(point.YArray(a), G), LD.LinesColors.Get(a), 2dip)";
_ld.Canvas.DrawLine((float)(_g.GI.originX+_g.GI.intervalX*(_i-1)),(float)(_calcpointtopixel(_ba,_py2[_a],_g)),(float)(_g.GI.originX+_g.GI.intervalX*_i),(float)(_calcpointtopixel(_ba,_point.YArray[_a],_g)),(int)(BA.ObjectToNumber(_ld.LinesColors.Get(_a))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 //BA.debugLineNum = 211;BA.debugLine="py2(a) = point.YArray(a)";
_py2[_a] = _point.YArray[_a];
 }
};
 }
};
 }else {
 //BA.debugLineNum = 216;BA.debugLine="Dim py As Float";
_py = 0f;
 //BA.debugLineNum = 217;BA.debugLine="py = point.Y";
_py = _point.Y;
 //BA.debugLineNum = 218;BA.debugLine="For i = 1 To LD.Points.Size - 1";
{
final double step186 = 1;
final double limit186 = (int)(_ld.Points.getSize()-1);
for (_i = (int)(1); (step186 > 0 && _i <= limit186) || (step186 < 0 && _i >= limit186); _i += step186) {
 //BA.debugLineNum = 219;BA.debugLine="point = LD.Points.Get(i)";
_point = (ch12.gui.b4a.example.charts._linepoint)(_ld.Points.Get(_i));
 //BA.debugLineNum = 220;BA.debugLine="LD.Canvas.DrawLine(G.GI.originX + G.GI.intervalX * (i - 1), calcPointToPixel(py, G) _ 				, G.GI.originX + G.GI.intervalX * i, calcPointToPixel(point.Y, G), LD.LinesColors.Get(0), 2dip)";
_ld.Canvas.DrawLine((float)(_g.GI.originX+_g.GI.intervalX*(_i-1)),(float)(_calcpointtopixel(_ba,_py,_g)),(float)(_g.GI.originX+_g.GI.intervalX*_i),(float)(_calcpointtopixel(_ba,_point.Y,_g)),(int)(BA.ObjectToNumber(_ld.LinesColors.Get((int)(0)))),(float)(anywheresoftware.b4a.keywords.Common.DipToCurrent((int)(2))));
 //BA.debugLineNum = 222;BA.debugLine="py = point.Y";
_py = _point.Y;
 }
};
 };
 //BA.debugLineNum = 225;BA.debugLine="LD.Target.Invalidate";
_ld.Target.Invalidate();
 //BA.debugLineNum = 226;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper  _drawpie(anywheresoftware.b4a.BA _ba,ch12.gui.b4a.example.charts._piedata _pd,int _backcolor,boolean _createlegendbitmap) throws Exception{
int _radius = 0;
int _total = 0;
int _i = 0;
ch12.gui.b4a.example.charts._pieitem _it = null;
float _startingangle = 0f;
int _gapdegrees = 0;
 //BA.debugLineNum = 249;BA.debugLine="Sub DrawPie (PD As PieData, BackColor As Int, CreateLegendBitmap As Boolean) As Bitmap";
 //BA.debugLineNum = 250;BA.debugLine="If PD.Items.Size = 0 Then";
if (_pd.Items.getSize()==0) { 
 //BA.debugLineNum = 251;BA.debugLine="ToastMessageShow(\"Missing pie values.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Missing pie values.",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 252;BA.debugLine="Return";
if (true) return null;
 };
 //BA.debugLineNum = 254;BA.debugLine="PD.Canvas.Initialize(PD.Target)";
_pd.Canvas.Initialize((android.view.View)(_pd.Target.getObject()));
 //BA.debugLineNum = 255;BA.debugLine="PD.Canvas.DrawColor(BackColor)";
_pd.Canvas.DrawColor(_backcolor);
 //BA.debugLineNum = 256;BA.debugLine="Dim Radius As Int";
_radius = 0;
 //BA.debugLineNum = 257;BA.debugLine="Radius = Min(PD.Canvas.Bitmap.Width, PD.Canvas.Bitmap.Height) * 0.8 / 2";
_radius = (int)(anywheresoftware.b4a.keywords.Common.Min(_pd.Canvas.getBitmap().getWidth(),_pd.Canvas.getBitmap().getHeight())*0.8/(double)2);
 //BA.debugLineNum = 258;BA.debugLine="Dim total As Int";
_total = 0;
 //BA.debugLineNum = 259;BA.debugLine="For i = 0 To PD.Items.Size - 1";
{
final double step221 = 1;
final double limit221 = (int)(_pd.Items.getSize()-1);
for (_i = (int)(0); (step221 > 0 && _i <= limit221) || (step221 < 0 && _i >= limit221); _i += step221) {
 //BA.debugLineNum = 260;BA.debugLine="Dim it As PieItem";
_it = new ch12.gui.b4a.example.charts._pieitem();
 //BA.debugLineNum = 261;BA.debugLine="it = PD.Items.Get(i)";
_it = (ch12.gui.b4a.example.charts._pieitem)(_pd.Items.Get(_i));
 //BA.debugLineNum = 262;BA.debugLine="total = total + it.Value";
_total = (int)(_total+_it.Value);
 }
};
 //BA.debugLineNum = 264;BA.debugLine="Dim startingAngle As Float";
_startingangle = 0f;
 //BA.debugLineNum = 265;BA.debugLine="startingAngle = 0";
_startingangle = (float)(0);
 //BA.debugLineNum = 266;BA.debugLine="Dim GapDegrees As Int";
_gapdegrees = 0;
 //BA.debugLineNum = 267;BA.debugLine="If PD.Items.Size = 1 Then GapDegrees = 0 Else GapDegrees = PD.GapDegrees";
if (_pd.Items.getSize()==1) { 
_gapdegrees = (int)(0);}
else {
_gapdegrees = _pd.GapDegrees;};
 //BA.debugLineNum = 268;BA.debugLine="For i = 0 To PD.Items.Size - 1";
{
final double step230 = 1;
final double limit230 = (int)(_pd.Items.getSize()-1);
for (_i = (int)(0); (step230 > 0 && _i <= limit230) || (step230 < 0 && _i >= limit230); _i += step230) {
 //BA.debugLineNum = 269;BA.debugLine="Dim it As PieItem";
_it = new ch12.gui.b4a.example.charts._pieitem();
 //BA.debugLineNum = 270;BA.debugLine="it = PD.Items.Get(i)";
_it = (ch12.gui.b4a.example.charts._pieitem)(_pd.Items.Get(_i));
 //BA.debugLineNum = 271;BA.debugLine="startingAngle = startingAngle + _  			calcSlice(PD.Canvas, Radius, startingAngle, it.Value / total, it.Color, GapDegrees)";
_startingangle = (float)(_startingangle+_calcslice(_ba,_pd.Canvas,_radius,_startingangle,(float)(_it.Value/(double)_total),_it.Color,_gapdegrees));
 }
};
 //BA.debugLineNum = 274;BA.debugLine="PD.Target.Invalidate";
_pd.Target.Invalidate();
 //BA.debugLineNum = 275;BA.debugLine="If CreateLegendBitmap Then";
if (_createlegendbitmap) { 
 //BA.debugLineNum = 276;BA.debugLine="Return createLegend(PD)";
if (true) return _createlegend(_ba,_pd);
 }else {
 //BA.debugLineNum = 278;BA.debugLine="Return Null";
if (true) return (anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 };
 //BA.debugLineNum = 280;BA.debugLine="End Sub";
return null;
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 2;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 4;BA.debugLine="Type PieItem (Name As String, Value As Float, Color As Int)";
;
 //BA.debugLineNum = 5;BA.debugLine="Type PieData (Items As List, Target As Panel, Canvas As Canvas, GapDegrees As Int, _ 		LegendTextSize As Float, LegendBackColor As Int)";
;
 //BA.debugLineNum = 8;BA.debugLine="Type GraphInternal (originX As Int, zeroY As Int, originY As Int, maxY As Int, intervalX As Float, gw As Int, gh As Int)";
;
 //BA.debugLineNum = 9;BA.debugLine="Type Graph (GI As GraphInternal, Title As String, YAxis As String, XAxis As String, YStart As Float, _  		YEnd As Float, YInterval As Float, AxisColor As Int)";
;
 //BA.debugLineNum = 11;BA.debugLine="Type LinePoint (X As String, Y As Float, YArray() As Float, ShowTick As Boolean)";
;
 //BA.debugLineNum = 12;BA.debugLine="Type LineData (Points As List, LinesColors As List, Target As Panel, Canvas As Canvas)";
;
 //BA.debugLineNum = 13;BA.debugLine="Type BarData (Points As List, BarsColors As List, Target As Panel, Canvas As Canvas, Stacked As Boolean, BarsWidth As Int)";
;
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
}
