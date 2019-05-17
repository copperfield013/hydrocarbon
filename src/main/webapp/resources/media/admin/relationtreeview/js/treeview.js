
seajs.use(['dialog','utils', 'ajax', '$CPF'], function(Dialog, Utils, Ajax, $CPF){
	var $page = $("#relation_tree_view_panel");
	var rootNodeId = $(".entity-title", $page).attr("data-id");	
	var rootMappingName = $(".entity-title", $page).attr("data-mappingName");	
	var entityId = $(".entity-title", $page).attr("data-abcattrCode");	//根节点对应的实体code: abcattrCode
	var attrCount = $("#attr-count", $page).val();
	
    $(function(){
    	$CPF.showLoading();
    	getChildrenNode(rootNodeId, rootMappingName, attrCount);
	    $CPF.closeLoading();
    })
    
     $page.on('click', '#attr-count-determine-btn', function(e){
    	attrCount = $("#attr-count", $page).val();
    	var attrMaxSize = $("#attr-max-size", $page).val();
    	console.log(attrCount  + "-----" + attrMaxSize);
    	if(attrCount > attrMaxSize){
    		Dialog.notice("请输入合理的数字！", "warning");
    		return;
    	}
    	 getChildrenNode(rootNodeId, rootMappingName, attrCount);
    });
    
	function addUnfold(el) {	
		if($(el).hasClass("icon-add") && $(el).siblings(".icon-arrow").hasClass("active")) {
        	$(el).siblings(".icon-arrow").trigger("click");        	
        }else if($(el).hasClass("icon-add-sm") && $(el).siblings(".icon-arrow-sm").hasClass("active")){
        	$(el).siblings(".icon-arrow-sm").trigger("click");
        }else if($(el).hasClass("icon-add-abc") && $(el).siblings(".icon-arrow-sm").hasClass("active")){
        	$(el).siblings(".icon-arrow-sm").trigger("click");
        } 
	} 	
	function saveSuccess(el) {
		 $(el).closest(".label-bar").removeClass("edit");
		 $(el).closest(".entity-title").removeClass("edit");
	     $(el).closest(".entity-edit-wrap").removeClass("edit");
	     $(el).closest(".label-bar").find(".edit-input").attr("disabled", "true");
	     $(el).closest(".entity-title").find(".edit-input").attr("disabled", "true");
	     $(el).closest(".label-bar").find("select").attr("disabled", "true");
	     $(el).closest(".entity-title").find("select").attr("disabled", "true");
	     $(el).closest(".label-bar").addClass("al-save");
	}
	
	 /**
     * 加载子节点
     */
    function getChildrenNode(id, mappingName, attrCount){
    	$CPF.showLoading();
    	Ajax.ajax("admin/relationtreeview/getChildrenNode", {
    		code:id,
			mappingName:mappingName,
			nodeAttrCount:attrCount
		},function(data){
			initTreeNode(id, data);
			$CPF.closeLoading();
			
			/*if (data.code==200 && data.childList.length>0) {
				initTreeNode(data.childList);
				Dialog.notice("数据加载成功！", "success");
			} else if (data.code==200 && data.childList.length==0) {
				Dialog.notice("当前节点没有孩子！", "warning");
			} else {
				Dialog.notice("孩子数据加载错误！", "error");
			}
			$CPF.closeLoading();*/
		});
    }
    function initTreeNode(id, data){
    	$CPF.showLoading();
    	var parent = $(".collapse-header[data-id='" + id + "']", $page).next(".collapse-content")[0];	
    	var mappingNameMap = data["mappingNameMap"];
    	var labelSetMap = data["labelSetMap"];
    	var relationNameAttrMap = data["relationNameAttrMap"];
    	var relations = data["relations"];
    	var labelSetValue = data["labelSetValue"];
    	if (JSON.stringify(relations) === '{}') {
			Dialog.notice("当前节点没有孩子！", "warning");
			$CPF.closeLoading();
			return;
		}
    	
    	var dragWrapLen = $(".dragEdit-wrap", $page).length + 1 ;
    	var nodeHtml='';
    	for(var key in relations){
    		if(relations[key] instanceof Array){
    			var id = key;
				var array = relations[key];
				
				 nodeHtml = nodeHtml + 
				"<li class='attr-relative'>" + 
					"<div class='attr-relative-title attr-relative collapse-header' data-mappingName='"+ mappingNameMap[array[0]]+"'  data-relationName='"+array[0]+"' data-id='"+key+"' labelSetMap='"+labelSetMap[array[0]]+"' labelSetValue='"+labelSetValue[array[0]]+"'>" + 
						"<div class='icon-label attr-relative'>" + 
							"<i class='icon icon-attr-relative'></i><span title='"+array[0]+"' class='text'>" + array[0].substring(0, 5) + "</span>" +
						"</div>" + 
						"<div class='label-bar attr-relative al-save'>";
				 
				 			nodeHtml+= "<i title='具体关系' class='icon icon-add-tag-relative'></i>";
							for(var i=0; i<relationNameAttrMap[array[0]].length; i++){
								nodeHtml = nodeHtml + 
								"<input id='abc_node_attr_" + i + "' type='text' disabled class='edit-input text order' title='" + relationNameAttrMap[array[0]][i].abcattrName  + "' value='" + array[i + 1] + "'>";
							}
								nodeHtml = nodeHtml + 
							"<div class='btn-wrap'>" + 
								"<i class='icon icon-save'></i>" +
								"<i class='icon icon-add-abc abc'></i>" +
								"<a title='详情' class='tree-node-edit-btn' style='font-size: 14px;position:absolute;right:198px;' href='javascript:;'>详情</a>" +
								/*"<i class='icon icon-trash'></i>" + */
								"<i class='icon icon-arrow-sm active'></i>" +
							"</div>" +
						"</div>" +
					"</div>" +
					"<ul class='drag-wrap-repeat need-ajax dragEdit-wrap collapse-content collapse-content-inactive' id='dragEdit-"+dragWrapLen+"'>" +
					"</ul>" + 
		        "</li>";
    		}
				 dragWrapLen = dragWrapLen + 1;
    	}
    	$(parent).html(""); //清空子节点
    	var $html = $(nodeHtml).appendTo($(parent));
    	/*$html.find("select").css({"width":"12%","marginLeft":"16px"}).select2();*/
    	$CPF.closeLoading();
    }
    
    
    //关系下标签添加
    $page.on("click", ".icon-add-tag-relative", function (e) {
        e.stopPropagation();
        $(this).closest(".label-bar.tag").addClass("edit");
        removePop(); 
        
        //显示保存按钮
        $(this).closest(".label-bar").find(".edit-input").removeAttr("disabled");
    	$(this).closest(".label-bar").find("select").removeAttr("disabled");
        $(this).closest(".label-bar").addClass("edit");
        
       var labelSetMap =  $(this).closest(".collapse-header").attr("labelSetMap");
       var labelSetValue =  $(this).closest(".collapse-header").attr("labelSetValue");
        
       var labelSetMapArray = labelSetMap.split(",");
       var labelSetValueArray = labelSetValue.split(",");
       var html = "<ul class='tag-card'>";
		var has; //判断是否已经选中
       for(var i=0; i<labelSetMapArray.length; i++) {
			has = false; //每次都重置
			for(var j=0; j<labelSetValueArray.length; j++) {
				if(labelSetValueArray[j] == labelSetMapArray[i]) {
					has = true;
					break;
				}
			};
			if(has) {
				 html += "<li class='tag-card-list'>" +
		            "<span class='tag-checkbox tag-checkbox-checked'>" +
		            "<input data-id='"+labelSetMapArray[i]+"' type='checkbox' class='tag-checkbox-input' value='"+labelSetMapArray[i]+"'>" +
		            "<span class='tag-checkbox-inner'></span>" +
		            "</span>" +
		            "<span>"+labelSetMapArray[i]+"</span>" +
		            "</li>" 
			}else {
				 html += "<li class='tag-card-list'>" +
		            "<span class='tag-checkbox'>" +
		            "<input data-id='"+labelSetMapArray[i]+"' type='checkbox' class='tag-checkbox-input' value='"+labelSetMapArray[i]+"'>" +
		            "<span class='tag-checkbox-inner'></span>" +
		            "</span>" +
		            "<span>"+labelSetMapArray[i]+"</span>" +
		            "</li>" 
			}				
		};
		html += "</ul>";
			var wrap = $("#relation_tree_view_panel");
	   var offsetx = $(this).offset().left;
	   var offsety = $(this).offset().top;
	   var wrapOffsetx = wrap.offset().left;
	   var wrapOffsety = wrap.offset().top;
	   var top = offsety - wrapOffsety + 25;
	   var left = offsetx - wrapOffsetx - 90;
	   var popHtml = $(html).appendTo(wrap);
	   popHtml.css({
	       "top": top,
	       "left": left
	    });
        //popRelativeTag(this);
        $(this).addClass("active");
    });
    
    /**
     *弹出关系标签
     * @param el
     * @returns
     */
    function pop(el) {
    	var $content = $(el).closest(".collapse-header").siblings(".collapse-content");
        var dragWrapLen = $(".dragEdit-wrap").length + 1 ;
        
    	var mappingName = $(el).closest(".collapse-header").attr("data-mappingName");
    	  $CPF.showLoading();
    	  var e = el;
    	Ajax.ajax("admin/relationtreeview/getRelationAll", {
    		mappingName:mappingName
		}, function(data){
			if(data.code ==200){
				var html = "<ul mappingName='"+mappingName+"' class='card'>"; 
				var relationAll = data.relationAll;
				if (relationAll.length == 0) {
					$CPF.closeLoading();
					Dialog.notice("没有关系可选！", "warning");
					return;
				}
				for (var key in relationAll) {
					html += "<li class='card-list add-tag'>" +
		            "<i class='icon icon-card-tag'></i>" +
		            "<span class='text'>"+relationAll[key]+"</span>" +
		            "</li>";
				}
				html +="</ul>";
				
				var wrap = $("#relation_tree_view_panel");
				var offsetx = $(e).offset().left;
		        var offsety = $(e).offset().top;
		        var wrapOffsetx = wrap.offset().left;
		        var wrapOffsety = wrap.offset().top;
		        var top = offsety - wrapOffsety + 30;
		        var left = offsetx -wrapOffsetx - 90;
		        var popHtml = $(html).appendTo(wrap);
		        popHtml.css({
		            "top": top,
		            "left": left
		        });
		        $CPF.closeLoading();
			}else{
				Dialog.notice(data.msg, "error");
				$CPF.closeLoading();
			}
			
		});
    };
    
    //跟实体编辑事件绑定
    $page.on("click", ".tree-node-edit-btn", function (e) {
    	e.stopPropagation();
    	console.log("-----查看节点");
    	var rootId = $("#rootId", $page).val();
    	var entityId = $(this).closest(".collapse-header").attr("data-id");
    	var mappingName = $(this).closest(".collapse-header").attr("data-mappingName");
    	Dialog.openDialog("admin/relationtreeview/edit?mappingName=" + mappingName + "&id=" + entityId + "&rootId=" + rootId, "关系节点详情", "edit-tree-relation-node",{
			width	: $(window).width()/2 + 100,
			height	:  $(window).height()/2 + 100
		});
    	$(this).addClass("active");
    });
    
    //弹出页中的事件绑定添加标签
    $page.on("click", ".tag-checkbox-input", function (e) {   
        e.stopPropagation();
        el = $page.find(".icon-add-tag-relative.active")[0];
        var text = $(this).val();
        
        var $header = $(el).closest(".collapse-header");
        
       // var ul = $(el).closest(".label-bar.tag").find("ul");
        var $parent = $(this).parent(".tag-checkbox");
        if ($parent.hasClass("tag-checkbox-checked")) {
            $parent.removeClass("tag-checkbox-checked");
            //tagRemoveTag(el, text);    
            //删除去掉的标签
            var labelsetvalue =  $header.attr("labelsetvalue");
            var labelSetValueArr = labelsetvalue.split(",");
            var str ="";
            
            for(var i=0; i<labelSetValueArr.length; i++) {
            	if( text == labelSetValueArr[i]) {
            		continue;
				}
            	
            	str+=labelSetValueArr[i] + ",";
            }
           str =  str.substring(0, str.length-1);
           $header.attr("labelsetvalue", str);
        } else {
            $parent.addClass("tag-checkbox-checked");
           // tagAddTag(el, text, id);        
            
            //添加新的标签
           var labelsetvalue =  $header.attr("labelsetvalue");
           
           if (labelsetvalue.length == 0) {
        	   labelsetvalue+= text;
           } else {
        	   labelsetvalue+= "," + text;
           }
           
           $header.attr("labelsetvalue", labelsetvalue);
        };
        //judegArrow(ul)
    });
	
    /**
     * 弹出选择实体页面
     */
    $page.on("click", ".card>li.card-list", function(e) {
    	var attrCount = $("#attr-count", $page).val();
    	var relationName = $(this).children(".text").html();
    	var mappingName = $(this).parent().attr("mappingName");
    	Dialog.openDialog("admin/relationtreeview/openSelection?mappingName=" + mappingName + "&relationName=" + relationName + "&attrCount=" + attrCount,"选择实体", "add-tree-relation-node",{
			width	: $(window).width()/2 + 100,
			height	: $(window).height()/2 + 100
    	});
    	
    });
    
    
    $page.data("selectEntity", function(mapJson){
    	$CPF.showLoading();
    	console.log("添加节点...");
    	console.log(mapJson);
    	var str = mapJson;
    	var ss = str.split(" ");
    	var $contant = $("[data-mappingname='"+ss[1]+"']").siblings(".collapse-content");
    	var dragWrapLen = $(".dragEdit-wrap", $page).length + 1 ;
    	/*var el = $("[data-mappingname='"+ss[1]+"']").children(".label-bar").children(".btn-wrap").children(".icon-arrow-sm");*/
    	
    	
    	var $labelbar = $("[data-mappingname='"+ss[1]+"']").children(".label-bar");
    	var $btnwrap = $labelbar.children(".btn-wrap");
    	var $iconarr = $btnwrap.find(".icon-arrow-sm");
    	     	
    	Ajax.ajax("admin/relationtreeview/getEntityData", {
				code:ss[0],
				mappingName:ss[1],
				relationName:ss[2],
				nodeAttrCount:3
			}, function(data){
				if(data.code == 200){
					var nodeHtml ="";
					var resulmap = data.resulmap;
					var abcAttrNodeList = data.abcAttrNodeList;
					var labelSetMap = data.labelSetMap;
					var mappingNameMap = data.mappingNameMap;
					for(var key in resulmap){ 
						
						 nodeHtml += "<li class='attr-relative'>" + 
						"<div class='attr-relative-title attr-relative collapse-header' data-mappingName='" + mappingNameMap[ss[2]] + "'  data-relationName='"+ss[2]+"' data-id='" + key + "' labelSetMap='"+labelSetMap[ss[2]]+"' labelSetValue=''>" + 
							"<div class='icon-label attr-relative'>" + 
								"<i class='icon icon-attr-relative'></i><span title='"+ss[2]+"' class='text'>" + ss[2].substring(0, 5)+ "</span>" +
							"</div>" + 
							"<div class='label-bar attr-relative al-save edit'>";
						 
							nodeHtml+= "<i title='具体关系' class='icon icon-add-tag-relative'></i>";
								var i = 0;
								for (var p in abcAttrNodeList) {
									var title = abcAttrNodeList[p].title;
									nodeHtml = nodeHtml + 
									"<input id='abc_node_attr_" + i + "' type='text' disabled class='edit-input text order' title='" + title  + "' value='" + resulmap[key][title] + "'>";
									i++;
								}
								nodeHtml = nodeHtml + 
								"<div class='btn-wrap'>" + 
								"<i class='icon icon-save'></i>" +
								"<i class='icon icon-add-abc abc'></i>" +
								"<a title='详情' class='tree-node-edit-btn' style='font-size: 14px;position:absolute;right:198px;' href='javascript:;'>详情</a>" +
							/*	"<i class='icon icon-trash'></i>" + */
								"<i class='icon icon-arrow-sm active'></i>" +
							"</div>" +
						"</div>" +
					"</div>" +
					"<ul class='drag-wrap-repeat need-ajax dragEdit-wrap collapse-content collapse-content-inactive' id='dragEdit-"+dragWrapLen+"'>" +
					"</ul>" + 
		        "</li>";       		
					}
					
					var $html = $(nodeHtml).prependTo($contant);
					Dialog.closeDialog("add-tree-relation-node");	//关闭节点添加窗口
					if ($iconarr.hasClass("active")) {
						$iconarr.trigger("click");   
					}
					
					$CPF.closeLoading();
				}else{
					Dialog.notice(dao.msg, "error");
					Dialog.closeDialog("add-tree-relation-node");	//关闭节点添加窗口
					$CPF.closeLoading();
				}
			});
	});
    
	/**
     * 获取实体信息方法 示例     
     */
	function getEntity(entity) {		
		var cnName = $(entity).attr("data-cnname");		
		$("#relation_tree_view_panel .entity-title>.edit-input").val(cnName);
		$("#relation_tree_view_panel .entity-title>.entity-only-title").html(cnName);
		$("#relation_tree_view_panel .entity-edit-wrap").addClass("active");
	}
    
    /**
     * 删除属性标签页弹出方法
      */
    function popAttr(el) {
        var html = "<div class='delete-list'>" +
            "<p>" +
            "<i class='icon icon-mark'></i><span class='text'>确定要删除此条数据?</span>" +
            "</p>" +
            "<div class='delete-list-btn'>" +
            "<span class='opera cancel'>取消</span>" +
            "<span class='opera confirm'>确认</span>" +
            "</div>" +
            "</div>"

        var wrap = $("#relation_tree_view_panel");
        var offsetx = $(el).offset().left;
        var offsety = $(el).offset().top;
        var wrapOffsetx = wrap.offset().left;
        var wrapOffsety = wrap.offset().top;
        var top = offsety - wrapOffsety - 114;
        var left = offsetx - wrapOffsetx - 121;
        var popHtml = $(html).appendTo(wrap);
        popHtml.css({
            "top": top,
            "left": left
        });
    }

    /**
     * remove 添加页方法
      */
    function removePop() {
        $(".card").remove();
        $(".tag-card").remove();
        $(".delete-list").remove();
        $(".delete-list-c").remove();
        $(".icon-add").removeClass("active");
        $(".icon-add-tag").removeClass("active");
        $(".icon-add-tag-relative").removeClass("active");
        $(".icon-trash").removeClass("active");
        $(".icon-trash-sm").removeClass("active");

    };

    /**
     * 添加关系方法
      */

    //提醒有未保存的节点
    function judgeSave() {    	
        var editBar = $("#relation_tree_view_panel").find(".label-bar.edit");
        var editEntity = $("#relation_tree_view_panel").find(".entity-edit-wrap.edit");
        if(editBar.length > 0 || editEntity.length > 0) {
            Dialog.notice("请先保存正在编辑的节点！", "warning");
            return true;
        }
    }
    
    
    //保存实体与实体关系
    function relativeSave(el) {  
    	var $header = $(el).closest(".collapse-header");
    	var $parentheader = $header.closest(".collapse-content").siblings(".collapse-header");
    	
    	var parentMappingName = $parentheader.attr("data-mappingname");
    	var parentId = $parentheader.attr("data-id");
    	
    	var chileMappingName = $header.attr("data-mappingname");
    	var chileId = $header.attr("data-id");
    	
    	var relationName = $header.attr("data-relationName");
    	
    	var labelsetmap = $header.attr("labelsetmap");
    	var labelsetvalue = $header.attr("labelsetvalue");
    	
    	var msg = "确认保存节点？";
    	if (labelsetvalue.length == 0) {
    		msg="【节点没有具体关系】是否确认保存？";
		}
    	
    	Dialog.confirm(msg, function(yes){
        	if(yes){
            	$CPF.showLoading();
            	Ajax.ajax("admin/relationtreeview/addRelation", {
            		 parentMappingName:parentMappingName,
        			 parentId:parentId,
        			 chileMappingName:chileMappingName,
        			 chileId:chileId,
        			 relationName:relationName,
        			 labelsetmap:labelsetmap,
        			 labelsetvalue:labelsetvalue
        		}, function(data){
        			if (data.code == 200) {
        				
        				if (labelsetvalue.length == 0) {
        					$("#attr-count-determine-btn").trigger("click");   
        				}
        				saveSuccess(el)
        				Dialog.notice(data.msg, "success");
        			} else {
        				Dialog.notice(data.msg, "error");
        			}
        			
        			$CPF.closeLoading();
        		});
        	}
        });
    	
    };
    
    //tag删除
    $page.on("click", function () {  
        removePop();
    });
      

    //收缩事件绑定
    $("#relation_tree_view_panel").on("click", ".icon-arrow, .icon-arrow-sm", function (e) {
    	var attr_relative = $(this).closest(".collapse-header").hasClass("attr-relative");
    	e.stopPropagation();
    	var bar = $(this).closest(".label-bar")[0];
        var $content = $(this).closest(".collapse-header")
            .siblings(".collapse-content");
        var isRelative = $(this).closest(".label-bar").hasClass("attr-relative");        
        var needAjax = $content.hasClass("need-ajax");  //判断是否需要ajax获取数据   
        
        var entityId = $(this).closest(".collapse-header").attr("data-id");
    	var mappingName = $(this).closest(".collapse-header").attr("data-mappingName");
        
        $(this).toggleClass("active");
        if ($content.hasClass("collapse-content-active")) {
            $content
                .removeClass("collapse-content-active")
                .addClass("collapse-content-inactive");
        } else {
            $content
                .removeClass("collapse-content-inactive")
                .addClass("collapse-content-active");
        }        
       if(needAjax) {
    	   getChildrenNode(entityId, mappingName, attrCount);
    	   $content.removeClass("need-ajax");
        } else {
        	$content.addClass("need-ajax");
        }
    })

    //跟实体添加事件绑定
    $("#relation_tree_view_panel").on("click", ".icon-add, .icon-add-abc", function (e) {
        e.stopPropagation();
        var hasSave = judgeSave();
        if(hasSave){
            return;
        }
        removePop();
        pop(this);
        $(this).addClass("active")
    });

    //关系下标签添加

    //删除属性事件绑定
    $("#relation_tree_view_panel").on("click", ".icon-trash, .icon-trash-sm", function (e) {
        e.stopPropagation();
        removePop();
        var $header = $(this).closest(".label-bar").hasClass("attr-group");
        
        if ($header) { //delete-list-c
            popGroupAttr(this);
        } else { //delete-list
            popAttr(this);
        }
        $(this).addClass("active")
    })

    //添加页中的事件绑定
    $("#relation_tree_view_panel").on("click", ".card>li.card-list", function (e) {
        e.stopPropagation();
        if ($("#relation_tree_view_panel").find(".icon-add.active").length > 0) {
            var el = $("#relation_tree_view_panel").find(".icon-add.active")[0];
        } else if ($("#relation_tree_view_panel").find(".icon-add-sm.active").length > 0) {
            var el = $("#relation_tree_view_panel").find(".icon-add-sm.active")[0];
        } else if ($("#relation_tree_view_panel").find(".icon-add-abc.active").length > 0) {
            var el = $("#relation_tree_view_panel").find(".icon-add-abc.active")[0];
        }
        if ($(this).hasClass("add-relative")) {
            addRelative(el);
        }
        removePop();
        $(el).removeClass("active");
    });


    //双击编辑
    $("#relation_tree_view_panel").on("dblclick", ".label-bar", function(){
		$(this).find(".edit-input").removeAttr("disabled");
    	$(this).find("select").removeAttr("disabled");
        $(this).addClass("edit");
    })
    
    //双击编辑
    $("#relation_tree_view_panel").on("dblclick", ".entity-title", function(){   
    	$(this).find(".edit-input").removeAttr("disabled");
    	$(this).find("select").removeAttr("disabled");
        $(this).addClass("edit");
    })
    
    //保存
    $("#relation_tree_view_panel").on("click", ".icon-save", function() {        
        var entityTitle = $(this).closest(".entity-title");
        var labelBar = $(this).closest(".label-bar");
        if(entityTitle.length > 0) {
        	entitySave(this);
        	return;
        }
        if(labelBar.hasClass("tag")) {        	
        	tagSave(this);
        }else if(labelBar.hasClass("attr")) {
        	attrSave(this);
        }else if(labelBar.hasClass("more-attr")) {
        	moreAttrSave(this);
        }else if(labelBar.hasClass("attr-group")) {
        	attrGroupSave(this);
        }else if(labelBar.hasClass("attr-relative")) {
        	relativeSave(this);
        }else if(labelBar.hasClass("abc")) {
        	abcSave(this);
        }

    });
    
    //删除-全部
    $("#relation_tree_view_panel").on("click", ".opera.confirm", function(e) {  
    	e.stopPropagation();    
        var entityTitle = $(".icon-trash.active").closest(".entity-title");
        var labelBar = $(".icon-trash-sm.active").closest(".label-bar");
        if(entityTitle.length > 0) {
        	var el = $(".icon-trash.active")[0];
        	entityDelete(el);
        	return;
        } 
        var el = $(".icon-trash.active")[0];        
        relativeDelete(el);
    })
    
    
})