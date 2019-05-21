/**
 * 为easyui.treeGrid的拓展方法
 * Created by qiuyu on 2015/6/29.
 */
;(function($) {
    $.fn.extend({
        // 对jquery函数进行拓展,调用方式$().functionName([pararms])
        // 调用dom元素需要将其dom对象传递到函数内的算法写在这里,函数内部this指向调用的dom对象
        tg:function(options){
            var selector = this.selector;
            var _loadSuccess = options.onLoadSuccess;
            var _setting ={
                checkbox:false,
                //默认的参数可以加在这里
                onLoadSuccess:function(){
                    var data = $(this).treegrid("getData");
                    var opts = $(this).treegrid("options");
                    if(opts.checkbox){
                        if(data){
                            for(var key in data){
                                //循环节点,第一级循环
                                var rowData = data[key];
                                var _id = rowData[opts.idField];
                                //第一级直接创建寻址_id和数据id是一个
                                $.createCheckBox(_id,_id);
                                if(rowData.children){ //如果存在子节点
                                    $.circleChildren(opts,rowData.children,_id);
                                }
                            }
                        }
                        $.bindCheckEvent();
                    }
                    if(_loadSuccess){
                        _loadSuccess(data);
                    }
                }
            };
            var opts = $.extend(_setting,options);
            $(selector).treegrid(opts);
        },
        getChecked:function(returnType){
            var returnType = returnType==undefined ? returnType : "object";
            var selector = this.selector;
            var check_id = [];
            $(selector).parent().find("._custom_check:checked").each(function(i,e){
                check_id.push($(e).attr("real_id"));
            })
            return returnType=="string"?check_id.join(","):check_id;
        },
        setChecked:function(data){
            var selector = this.selector;
            var checkIds = [];
            if(typeof data == "string"){
                checkIds = data.split(",");
            }else{
                checkIds = data;
            }
            for(var key in checkIds){
                var ck_id = checkIds[key];
                $(selector).parent().find("[real_id='"+ck_id+"']").prop("checked",true);
            }
        }
    });
    $.extend({
        createCheckBox:function(id,_id){
            $("[node-id='"+id+"']").find(".tree-icon").before("<input type='checkbox' class='_custom_check' real_id='"+id+"' m_id='"+_id+"'/>");
        },
        circleChildren:function(opts,childrens,pId){
            for(var key in childrens){
                var rowData = childrens[key];
                var id = rowData[opts.idField];
                var _id = pId+"_"+rowData[opts.idField];
                this.createCheckBox(id,_id);
                if(rowData.children){
                    this.circleChildren(opts,rowData.children,_id);
                }
            }
        },
        bindCheckEvent:function(){
            $("._custom_check").click(function(){
                var id = $(this).attr("m_id");//1_2_3_4
                //选中其所有的子节点
                if($(this).is(":checked")){
                    $("[m_id^='"+id+"_']").prop("checked",true);
                }else{
                    $("[m_id^='"+id+"_']").prop("checked",false);
                }
                var _pIds = id.split("_");
                //判断是否能勾选上级
                for(var i=_pIds.length-1;i>0;i--){
                    var check_range = _pIds.slice(0,i);
                    var flag = true;
                    $("[m_id^='"+check_range.join("_")+"_']").each(function(index,ele){
                        if(!$(ele).is(":checked")){
                            flag = false;
                        }
                    });
                    if(flag){
                        $("[m_id='"+check_range.join("_")+"']").prop("checked",true);
                    }else{
                        $("[m_id='"+check_range.join("_")+"']").prop("checked",false);
                    }
                }
            });
        }
    });
})(jQuery);