/**
 * 
 */
seajs.use(['dialog','utils', 'ajax', '$CPF'], function(Dialog, Utils, Ajax, $CPF){
	var $page = $("#tree_view_panel");
	var rootNodeId = $(".entity-title", $page).attr("data-id");	
	var rootMappingName = $(".entity-title", $page).attr("data-mappingName");	
	var entityId = $(".entity-title", $page).attr("data-abcattrCode");	//根节点对应的实体code: abcattrCode
	var attrCount = $("#attr-count", $page).val();
	console.log(attrCount);
	
    $(function(){
	    $CPF.showLoading();
	    getChildrenNode(rootNodeId, rootMappingName, attrCount);
	    //getNodeOpsType();
	   //getDataType();
	    //drag($(".dragEdit-wrap", $page).length);       
	    //getChild(rootNodeId, false, null, entityId);  //直接执行
	    //$(".label-bar", $page).addClass("al-save");
	    //addEntityOPT();
	    $CPF.closeLoading();
    });
    /**
     * 加载子节点
     */
    function getChildrenNode(id, mappingName, attrCount){
    	$CPF.showLoading();
    	Ajax.ajax("admin/treeview/getChildrenNode", {
			code	:	id,
			mappingName	:	mappingName,
			nodeAttrCount : attrCount
		},function(data){
			console.log("children data is :");
			console.log(data);
			initTreeNode(id, data);
			$CPF.closeLoading();
		});
    }
    
    function getNodeSelf(id, mappingName, attrCount){
    	$CPF.showLoading();
    	Ajax.ajax("admin/treeview/getNodeSelf", {
			id	:	id,
			mappingName	:	mappingName,
			nodeAttrCount	:	attrCount
		},function(data){
			console.log("node self is :");
			console.log(data);
			updateTreeNode(id, data);
			$CPF.closeLoading();
		});
    }
    
    function initTreeNode(id, data){
    	$CPF.showLoading();
    	var parent = $(".collapse-header[data-id='" + id + "']", $page).next(".collapse-content")[0];	
    	var mappingNameMap = data["mappingNameMap"];
    	var labelSetMap = data["labelSetMap"];
    	var relationNameAttrMap = data["relationNameAttrMap"];
    	var relations = data["relations"];
    	var nodeHtml = "";
    	var dragWrapLen = $(".dragEdit-wrap", $page).length + 1 ;
    	for(var key in relations){
    		console.log("dragWrap length is " + dragWrapLen);
    		if(relations[key] instanceof Array){
    			var id = key;
				var array = relations[key];
				nodeHtml = nodeHtml + 
				"<li class='attr-relative'>" + 
					"<div class='attr-relative-title attr-relative collapse-header' data-mappingName='" + mappingNameMap[array[0]] + "' data-abcattrCode=''  data-order='' data-id='" + key + "'>" + 
						"<div class='icon-label attr-relative'>" + 
							"<i class='icon icon-attr-relative'></i><span class='text'>" + array[0] + "</span>" +
						"</div>" + 
						"<div class='label-bar attr-relative al-save'>" +
							"<input id='label_set' type='text' style='width:70px;' disabled class='edit-input text' value='" + labelSetMap[array[0]] + "' title='关系类型'>" ;
							for(var i=0; i<relationNameAttrMap[array[0]].length; i++){
								nodeHtml = nodeHtml + 
								"<input id='abc_node_attr_" + i + "' type='text' disabled class='edit-input text' title='" + relationNameAttrMap[array[0]][i].abcattrName  + "' value='" + array[i + 1] + "'>";
							}
							nodeHtml = nodeHtml + 
							"<div class='btn-wrap'>" + 
								"<i class='icon icon-save'></i>" +
								"<i class='icon icon-add-abc abc'></i>" +
								"<a title='编辑' class='tree-node-edit-btn' style='font-size: 14px;position:absolute;right:198px;' href='javascript:;'>编辑</a>" + 
								"<i class='icon icon-trash'></i>" + 
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
    	$CPF.closeLoading();
 	    //$html.find("select").css({"width":"15%","marginLeft":"60px"}).select2();
    }
    
    function updateTreeNode(id, data){
    	var parent = $(".collapse-header[data-id='" + id + "']", $page);
    	console.log("------------------------");
    	for(var i=0; i<data.length-1; i++){
    		$("#abc_node_attr_" + i, parent).attr("value", data[i+1]);
    	}
    	//$("#node_name", parent).attr("value", data[1]);
    }
    
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
    
    /**
     * 展开收缩事件绑定
     */
    $page.on('click', ".icon-arrow, .icon-arrow-sm", function(e){
    	console.log("伸缩...");
    	
    	var collapse_header = $(this).closest(".collapse-header");
    	console.log(collapse_header);
    	var entityId = $(this).closest(".collapse-header").attr("data-id");
    	var mappingName = $(this).closest(".collapse-header").attr("data-mappingName");
    	$(this).toggleClass("active");
    	var $content = collapse_header.siblings(".collapse-content");
    	var needAjax = $content.hasClass("need-ajax");
    	console.log("if need ajax : " + needAjax);
    	if ($content.hasClass("collapse-content-active")) {
            $content
                .removeClass("collapse-content-active")
                .addClass("collapse-content-inactive");
        } else {
            $content
                .removeClass("collapse-content-inactive")
                .addClass("collapse-content-active");
        }
    	if(needAjax){
    		getChildrenNode(entityId, mappingName, attrCount);
    	}
    });
    
    //跟实体添加事件绑定
    $page.on("click", ".icon-add, .icon-add-abc", function (e) {
        e.stopPropagation();
        console.log("-----添加节点");
        var rootId = $("#rootId", $page).val();
        var entityId = $(this).closest(".collapse-header").attr("data-id");
    	var mappingName = $(this).closest(".collapse-header").attr("data-mappingName");
    	Dialog.openDialog("admin/treeview/add?type=relation&mappingName=" + mappingName + "&id=" + entityId +"&rootId=" + rootId,"添加关系节点", "add-tree-relation-node",{
    		width	: $(window).width()/2 + 100,
    		height	: $(window).height()/2 + 100
    	});
        $(this).addClass("active");
    });
    //跟实体编辑事件绑定
    $page.on("click", ".tree-node-edit-btn", function (e) {
    	e.stopPropagation();
    	console.log("-----编辑节点");
    	var rootId = $("#rootId", $page).val();
    	var entityId = $(this).closest(".collapse-header").attr("data-id");
    	var mappingName = $(this).closest(".collapse-header").attr("data-mappingName");
    	Dialog.openDialog("admin/treeview/edit?mappingName=" + mappingName + "&id=" + entityId + "&rootId=" + rootId, "编辑关系节点", "edit-tree-relation-node",{
			width	: $(window).width()/2 + 100,
			height	:  $(window).height()/2 + 100
		});
    	$(this).addClass("active");
    });
    
    //删除属性事件绑定
    $page.on("click", ".icon-trash, .icon-trash-sm", function (e) {
        e.stopPropagation();
        console.log("-----删除节点");
        $this = $(this).closest(".collapse-header");
        var entityId =  $this.attr("data-id");
        Dialog.confirm('确认删除节点？', function(yes){
        	if(yes){
        		$CPF.showLoading();
        		 Ajax.ajax("admin/treeview/delete", {
        				code	:	entityId
        			}, function(data){
        				if(data){
        					//$("#treeview", $page).treeview("removeNode", [ nodes, { silent: true } ]);
        					console.log($this.parent("li"));
        					$this.parent("li").remove();
        					Dialog.notice("删除成功！", "success");
        				}else{
        					Dialog.notice("删除失败！", "error");
        				}
        				$CPF.closeLoading();
        			});
        	}
        });
        $(this).addClass("active")
    })
    
    $page.data("addSubmit", function(mapJson){
    	$CPF.showLoading();
    	console.log("添加节点...");
    	console.log(mapJson);
		Ajax.ajax("admin/treeview/saveNode", {
			paramJson	: JSON.stringify(mapJson)
		}, function(data){
			console.log("success");
			getChildrenNode(mapJson["parentNodeId"], mapJson["parentMappingName"], attrCount);
			Dialog.closeDialog("add-tree-relation-node");	//关闭节点添加窗口
			$CPF.closeLoading();
			Dialog.notice("添加成功！", "success");	//提示信息
		});
	});
    
    $page.data("editSubmit", function(mapJson){
    	$CPF.showLoading();
		Ajax.ajax("admin/treeview/saveNode", {
			paramJson	: JSON.stringify(mapJson)
		}, function(data){
			console.log("success");
			console.log(mapJson);
			//getChildrenNode(mapJson["parentNodeId"], mapJson["parentMappingName"]);
			//TODO 刷新被编辑的节点
			getNodeSelf(mapJson["parentNodeId"], mapJson["parentMappingName"], attrCount);
			Dialog.closeDialog("edit-tree-relation-node");	//关闭节点添加窗口
			Dialog.notice("修改成功！", "success");
			$CPF.closeLoading();
		});
	});

});