var dhxWins = new dhtmlXWindows();

var winName = "win";
var editMode;
dhtmlx.message.position="bottom";

function openWindow(title, urlink, w, h) {
	var w = dhxWins.createWindow(winName, 0, 0, w, h);
	w.setText(title);
	w.keepInViewport(true);
	w.setModal(true);
	w.centerOnScreen();
	w.button("minmax1").hide();
	w.button("minmax2").hide();
	w.button("park").hide();
	w.attachURL(urlink);
	w.denyResize();
    w.denyMove();
	return w;
}
function openWindowForAdd(title, formStructure, w, h, func) {
	var w = dhxWins.createWindow(winName, 0, 0, w, h);
	w.setText(title);
	w.keepInViewport(true);
	w.setModal(true);
	w.centerOnScreen();
	w.button("minmax1").hide();
	w.button("minmax2").hide();
	w.button("park").hide();
	w.denyResize();
    w.denyMove();
	var form = w.attachForm();
	form.loadStruct(formStructure, "json");
	form.enableLiveValidation(true);
	if (func != undefined)
		eval(func(form));
	return form;
}
function openWindowForEdit(title, formStructure, w, h, func) {
	var w = dhxWins.createWindow(winName, 0, 0, w, h);
	w.setText(title);
	w.keepInViewport(true);
	w.setModal(true);
	w.centerOnScreen();
	w.button("minmax1").hide();
	w.button("minmax2").hide();
	w.button("park").hide();
	w.denyResize();
    w.denyMove();
	var form = w.attachForm();
	form.loadStruct(formStructure, "json");
	form.enableLiveValidation(true);
	if (func != undefined)
		eval(func(form));
	return form;
}
function openWindowForTreeview(loadUrl,x,y,w,h,func) {
	var w=dhxWins.createWindow(winName,x,y,w,h);
	w.hideHeader();
    w.denyResize();
    w.denyMove();
	var myTreeView=w.attachTreeView({json:loadUrl});
	return myTreeView;
}

function openWindowForTreeviewWithName(name,loadUrl,x,y,w,h,func) {
    var w=dhxWins.createWindow(name,x,y,w,h);
    w.hideHeader();
    w.setModal(true);
    w.denyResize();
    w.denyMove();
    var myTreeView=w.attachTreeView({json:loadUrl});
    return myTreeView;
}
function addEvent(type,callback){
	dhxWins.window(winName).attachEvent(type,callback);
}

function closedialog(ret) {
	dhxWins.window(winName).close();
	editMode = "";
}
function closewithName(name,func){
	dhxWins.window(name).close();
	if(func!=null && func!=undefined){
		eval(func);
	}
}
function openMsgDialog(title, msg, func) {
	dhtmlx.message({
		title : title,
		type : "alert-warning",
		text : msg,
		callback : function() {
			if (func != null)
				eval(func)
		}
	});
}
function goFristPage() {
	var pageCount = myForm.getItemValue("query.pageCount");
	if (pageCount == 0) {
		return;
	}
	myForm.setItemValue("query.pageNumber", "1");
	gosearch();
}
function goPreviousPage() {
	var i = 0;
	var rc = myForm.getItemValue("query.recordCount");
	if (rc == 0)
		return;
	var pn = myForm.getItemValue("query.pageNumber");
	i = pn;
	i--;
	myForm.setItemValue("query.pageNumber", i)
	gosearch();
}
function goLastPage() {
	var i = 0;
	var rc = myForm.getItemValue("query.recordCount");
	if (rc == 0)
		return;
	var lp = myForm.getItemValue("query.pageCount");
	var pn = myForm.getItemValue("query.pageNumber");
	myForm.setItemValue("query.pageNumber", lp)
	gosearch();
}
function goNextPage() {
	var i = 0;
	var rc = myForm.getItemValue("query.recordCount");
	if (rc == 0)
		return;
	var pn = myForm.getItemValue("query.pageNumber");
	i = pn;
	i++;
	myForm.setItemValue("query.pageNumber", i)
	gosearch();
}
function goPage() {
	var i = 0;
	myForm.setItemValue("query.pageNumber", $("#jumpNum").val())
	gosearch();
}
function setpagesize() {
	var evt = window.event || arguments.callee.caller.arguments[0];
	var ikeyCode = evt.keyCode || evt.which;
	if (ikeyCode == 13) {
		var pageSize = $("#pageSize").val();
		myForm.setItemValue("query.pageSize", pageSize);
		myForm.setItemValue("query.pageNumber", "1");
		gosearch();
	}
}
function gosearch() {
	reloadGrid();
}
function reload() {
    reloadGrid();
}
function reloadGrid() {
	myForm.send(queryUrl, function(loader, response) {
		var tobj = eval('(' + response + ')');
		myGrid.clearAll();
		myGrid.parse(tobj, "json");
		statusbar.setText(constructPaging(tobj.query));
	});

}
function initCombo(combo,url){
	if(combo!=undefined){
		$.post(url,function(retval){
			combo.load(retval);
		 });
	}
}
function openWindow(title,urlink,w,h){
    var w = dhxWins.createWindow(winName, 0, 0, w, h);
    w.setText(title);
    w.keepInViewport(true);
    w.setModal(true);
    w.centerOnScreen();
    w.button("minmax1").hide();
    w.button("minmax2").hide();
    w.button("park").hide();
    w.attachURL(urlink);
    w.denyResize();
    w.denyMove();
    return w;
}
function openWindowInPage(title,w,h,objname){
    var w = dhxWins.createWindow(winName, 0, 0, w, h);
    w.setText(title);
    w.keepInViewport(true);
    w.setModal(true);
    w.centerOnScreen();
    w.button("minmax1").hide();
    w.button("minmax2").hide();
    w.button("park").hide();
    w.attachObject(objname);
    w.button("close").disable();
    w.denyResize();
    w.denyMove();
    return w;
}
function openSingleWindow(title,divhtml,width,height){
    if(height==null || height=='')
        height=100;
    if(width==null || width=='')
        width=300;
    var win2 = dhxWins.createWindow(winName, 0, 0, width, height);
    win2.setText(title);
    win2.keepInViewport(true);
    win2.setModal(true);
    win2.centerOnScreen();
    win2.button("minmax1").hide();
    win2.button("minmax2").hide();
    win2.button("park").hide();
    win2.attachHTMLString(divhtml);
    win2.denyResize();
    win2.denyMove();
    return win2;
}
function openMsg(title,message){
    dhtmlx.alert({
        title:title,
        ok:"OK",
        text:message
    });
}
function openAlert(title,message){
    dhtmlx.alert({
        title:title,
        type:"alert-warning",
        ok:"OK",
        text:message
    });
}
function openConfrim(title,message,fun) {
    dhtmlx.confirm({
        title:title,
        type:"confirm",
        text: message,
        callback: function(result){
            if(result==true){
                var fun1=eval(fun);
                fun1.apply();
                return true;
            } else
                return false;
        }
    });
}

function openMsgPopUp(title,msg,width,height,func){
    dhtmlx.message({
        title: title,
        type: "alert-warning",
        text: msg,
        callback: function() {if(func!=null)eval(func)}
    });
}
function constructPaging(query) {
	var pageSize=query.pageSize;
	var pageNo=query.pageNumber;
	var totalCount=query.recordCount;
	var totalPage=query.pageCount;
    var prevPage = pageNo > 1 ? pageNo - 1 : 1;
    var nextPage = pageNo + 1 >= totalPage ? totalPage : pageNo + 1;
    var str="";
    str = str + "<div class='dhx_toolbar_material dhxtoolbar_icons_18 dhx_toolbar_shadow'>";
    str = str + "<div style='overflow: hidden;'><div style='float: left;display:inline'>共&nbsp;" + totalCount + "&nbsp;条,第&nbsp;" + pageNo + "页/共&nbsp;" + totalPage + " 页&nbsp;<input type=\"textbox\" size=3 align=\"right\" class=\"pTextStyle\" name=\"pageSize\" id=\"pageSize\" value=\"" + query.pageSize + "\" onKeyPress=\"javascript:setpagesize();\">" + "条/页&nbsp;</div>";
    str = str + " <div style=\"text-align: right;padding-left:300px;float:right;overflow: hidden;display:inline\">";
    if (pageNo <= 1) {
        str = str + "<span class='greyleftPageMore'>首页</span><span class='greyleftPage'>上一页</span>";
    } else {
        str = str + "<span><a class='leftPageMore' href='javascript:goFirstPage()'>首页</a></span>";
        str = str + "<span><a class='leftPage' href='javascript:goPreviousPage()'>上一页</a></span>";
    }
    if ((totalPage == pageNo) || (totalPage == 0)) {
        str = str + "<span class='greyrightPage'>下一页</span><span class='greyrightPageMore'>尾页</span>";
    } else {
        str = str + "<span><a class='rightPage' href='javascript:goNextPage()'>下一页</a></span>";
        str = str + "<span><a class='rightPageMore' href='javascript:goLasePage()'>尾页</a></span>";
    }
    str = str + "跳转到<input type='text' name='jumpNum' id='jumpNum' value='" + pageNo + "' size='2' maxlength='9'>" + "页";
    str = str + "<input type='button' name='jumpPage' value='GO' onclick='goPage()' class='dhxform_btn_txt' >";

    str = str + "</div>";
    str = str + "</div>";
    return str;

}
function decodeHTMLEntities(text) {
    var entities = [
        ['amp', '&'],
        ['apos', '\''],
        ['#x27', '\''],
        ['#x2F', '/'],
        ['#39', '\''],
        ['#47', '/'],
        ['lt', '<'],
        ['gt', '>'],
        ['nbsp', ' '],
        ['quot', '"']
    ];
    for (var i = 0, max = entities.length; i < max; ++i)
        text = text.replace(new RegExp('&'+entities[i][0]+';', 'g'), entities[i][1]);
    return text;
}