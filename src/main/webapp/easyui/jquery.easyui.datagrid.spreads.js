/**
 * jquery以及jquery.easyui拓展,避免重复工作
 */
;(function($) {
	$.fn.extend({
		grid:function(options){//options:datagrid参数,自用参数
			var selector = this.selector;
			var loadcallback = options.onLoadSuccess;
			var onSelectCallBack = options.onSelect;
			var onCheckCallBack = options.onCheck;
			var onCheckAll = options.onCheckAll;
			delete options.onLoadSuccess,options.onSelect,options.onCheck,options.onCheckAll;
			//默认设置
			var defaults={
				onLoadSuccess:function(data){//数据加载成功
					if($(this).datagrid("options")["singleSelect"]){
						$(selector).parent().find(".datagrid-header-check").empty()
					}
					$(selector).parent().find(".datagrid-body").css({"overflow-x":"auto"});
					//获取列表中的columns对象
					var cols = $(this).datagrid("options")["columns"][0];
					var firstVisiableCol="";//第一个可见列
					var visableCol = []
					for(var key in cols){//循环列
						var col=cols[key];
						//判断当前列是否可见
						if((!col["hidden"] || col["hidden"]!=true) && !col["checkbox"]){
							//列可见,则存储列名
							visableCol[visableCol.length] = col["field"];
						}
					}
					firstVisiableCol = visableCol.length>0?visableCol[0]:"";
					//判断查询返回的是否有结果,或者结果集长度为0
					if(!data.rows || data.rows.length == 0){
						//不存在结果集,或结果集长度为0
						/*var tips = "<font color=red size='2'>未查询到数据...</font>";
						var noneRow=new Object();
						noneRow[firstVisiableCol]=tips;
						noneRow["unvalid"]=1;//当前为提示行，数据无效
						//以第一个可见列的名称添加一行数据以提示没有数据
						$(this).datagrid("insertRow",{index:0,row:noneRow});
						//将第一行merge成一个单元格
						if($(this).datagrid("options")["mergeTip"]){
							$(this).datagrid("mergeCells",{index:0,field:firstVisiableCol,colspan:visableCol.length})
						}
						//锁定第一列的且无效数据的对齐方式为left
						$(selector).parent().find(".datagrid-view2 [datagrid-row-index='0']").find("div").css("text-align","left");
						//防止checkbox列也左对齐
						$(selector).parent().find(".datagrid-cell-check").css("text-align","center");*/

					}
					if(loadcallback){
						loadcallback(data);
					}
				},
				onSelect:function(index,rowData){
					if(rowData && rowData["unvalid"] && rowData["unvalid"]=="1"){
						$(this).datagrid("unselectRow",index);
						return false;
					}
					if(onSelectCallBack){
						onSelectCallBack(index,rowData);
					}
				},
				onCheck:function(index,rowData){
					if(rowData && rowData["unvalid"] && rowData["unvalid"]=="1"){
						$(this).datagrid("unselectRow",index);
						return false;
					}
					if(onCheckCallBack){
						onCheckCallBack(index,rowData);
					}
				},
				onCheckAll:function(rows){
					if(rows && rows.length==1 && rows[0]["unvalid"]=="1"){
						$(this).datagrid("uncheckAll");
						return false;
					}
					if(onCheckAll){
						onCheckAll(rows);
					}
				},
				striped:true,//隔行变色
				pageList:[5,10,20,35,50,100],//每页可显示数量列表
				pageSize:20,//每页
				pagination : true,//显示分页工具
				rownumbers : true,//行列号
       			singleSelect : true,//单选模式
				mergeTip:false//是否合并提示信息
			}
			
			var columns=options["columns"][0];//取得原有的列定义
			//向列定义中添加一个字段,用来存储当前是否为有效行
			columns[columns.length]={field:"unvalid",title:"unvalid",hidden:true};
			options["columns"][0]=columns;//重置原有列定义
			//处理字典表
			var dicFlag = false;
			for(var key in columns){
				var col = columns[key];//获取列定义
				if(col["dicKey"]){//包含字典定义
					columns[key]["formatter"]=$.dicFormat;
					columns[key]["_dicKey"]=col["dicKey"];
					delete columns[key]["dicKey"];
					dicFlag = true;
				}
			}
			if(dicFlag){
				$.ajax({
					type: "POST",
					url: _dic_all_url,
					async: false,
					cache:false,
					dataType: "json",
					success: function(data){
						$(document).data("_dic_json",data);
					}
				})
			}
			
			
			
			$.extend(defaults,options);
			if(!defaults["pagination"]){
				defaults["pageSize"] = 100;
			}

			$(selector).datagrid(defaults);
		},
		dateFormat:function(date,format){//将给定的时间给话为对应的格式字符串
			//参数:
			//		date:要格式化的时间对象
			//		format:格式化字符串 支持的字符M月 d日 y年 h小时 m分钟 s分钟 q时区 S毫秒
			var o = { 
				"M+" : date.getMonth()+1,
				"d+" : date.getDate(),
				"h+" : date.getHours(), 
				"m+" : date.getMinutes(), 
				"s+" : date.getSeconds(), 
				"q+" : Math.floor((date.getMonth()+3)/3), 
				"S" : date.getMilliseconds() 
			} 
			if(/(y+)/.test(format)) { 
				format = format.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length)); 
			} 
			for(var k in o) { 
				if(new RegExp("("+ k +")").test(format)) { 
					format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
				} 
			} 
			return format; 
		},
		setView:function(){//将指定的元素下的子元素设置为只读模式
			var selector=this.selector;
			$(selector).find(":input").attr("readonly",true).readonly();
			$(selector).find(".easyui-datetimespinner").datetimespinner("readonly",true);
			$(selector).find(".easyui-combobox").combobox("readonly",true);
			//checkbox,radio做特殊处理 disabled
			$(selector).find(":checkbox,:radio").attr("disabled",true);
		}
	});
	$.extend({
		dicFormat:function(value,row,index,dicKey){
			if(row["unvalid"] && row["unvalid"]==1){
				return "";
			}
			if(value==undefined){return value}
			var valueAry = value.toString().split(",");
			var returnAry = [];
			for(var key in valueAry){
				returnAry.push("@"+valueAry[key]+"@");
			}
			var returnValue = returnAry.join(",");
			var dicDate = $(document).data("_dic_json")
			if(dicKey && dicDate){
				var formatDatas = dicDate[dicKey];
				for(var key in formatDatas){
					returnValue = returnValue.replace("@"+formatDatas[key]["key"]+"@",formatDatas[key]["value"]);
				}
			}
			return returnValue.replace(new RegExp("@","gm"),"");
		}
	})
})(jQuery);
