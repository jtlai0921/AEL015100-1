Type=Activity
Version=2.71
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim ProductName As String = ""
    Dim ProductPrice As Int = 0
End Sub

Sub Globals
    Dim ListView1 As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.Title = "商品目錄"
    ' 建立與新增ListView元件
    ListView1.Initialize("ListView1")
    Activity.AddView(ListView1, 0, 0, 100%x, 100%y)
    ' 建立項目
    Dim bmpA, bmpB, bmpC, bmpD As Bitmap  ' 圖示
    bmpA.Initialize(File.DirAssets, "productA.png")
    bmpB.Initialize(File.DirAssets, "productB.png")
    bmpC.Initialize(File.DirAssets, "productC.png")
    bmpD.Initialize(File.DirAssets, "productD.png")
    ListView1.AddTwoLinesAndBitmap("外接式光碟機", "$1200", bmpA)
    ListView1.AddTwoLinesAndBitmap("光學USB滑鼠", "$200", bmpB)
    ListView1.AddTwoLinesAndBitmap("電腦標準鍵盤", "$350", bmpC)
    ListView1.AddTwoLinesAndBitmap("辦公型電話", "$430", bmpD)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub ListView1_ItemClick(Position As Int, Value As Object)
    ProductName = Value  ' 取得商品名稱
    Select Position      ' 取得商品價格
       Case 0: ProductPrice = 1200
       Case 1: ProductPrice = 200	   
       Case 2: ProductPrice = 350	   
       Case 3: ProductPrice = 430   
    End Select	   
    StartActivity(Main)  ' 返回Main主活動
End Sub

