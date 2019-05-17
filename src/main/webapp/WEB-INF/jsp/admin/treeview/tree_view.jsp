<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<link href="media/admin/plugins/bootstrapt-treeview/dist/bootstrap-treeview.css" rel="stylesheet">

<div id="tree_view_${id }">
	<div class="page-header">
		<div class="header-title">
			<h1>树形插件</h1>
		</div>
	</div>
	<div class="page-body">
		<div class="row">
			<div class="col-sm-12">
				<div>
					<!-- <a id="add-root-node-btn" class="btn btn-primary" href="#" title="添加根节点">添加根节点</a> -->
					<a id="add-node-btn" class="btn btn-primary" href="#" title="添加节点">添加节点</a>
					<a id="edit-node-btn" class="btn btn-primary" href="#" title="编辑节点">编辑节点</a>
					<a id="remove-node-btn" class="btn btn-primary" href="#" title="删除节点">删除节点</a>
				</div>
				<label for="treeview"></label>
				<div id="treeview"></div>
				<div id="node-attr-add-div" style="display: none;">
					<c:forEach items="${nodeAttributeNameList }" var="nodeAttr">
						<div class="form-group">
							<label class="col-lg-2 control-label" for="${nodeAttr }">${nodeAttr }</label>
							<div class="col-lg-5">
								<input id="${nodeAttr }" type="text" class="form-control" name="${nodeAttr }" />
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
			<script type="text/javascript">
			seajs.use(['ajax', 'dialog'], function(Ajax, Dialog){
				var $page = $('#tree_view_${id }');
				console.log("------------------");
				console.log($page);
				console.log($("#add-node-btn", $page));
				//var tree_data = initTree("2adba5c2ab424be69cedd0f52a776631");
			
				function initTree(parentId, mappingName){
					Ajax.ajax("admin/treeview/data", {
						code	:	parentId,
						configName	:	mappingName
					},function(data){
						console.log("data is :");
						console.log(data);
						var tree_data = [
							{
								text :	data[1],
								lazyLoad:true,
								dataAttr	:	{
									mappingName : mappingName,
									id	:	data[0],
								},
							}
						];
						console.log(tree_data);
						var options = {
						          showTags: true,
						          levels: 1, //初始化是展开的层级
						          data: tree_data,
						          loadingIcon:"fa fa-hourglass",//懒加载过程中显示的沙漏字符图标
						          lazyLoad:loaddata//loaddata为点击懒加载节点目录时，运行的函数名称，把后端的数据添加到这个节点下面
						        };
						 console.log("初始化...");
					     $('#treeview', $page).treeview(options);
					     console.log("初始化完成...");
					});
				}
				
				$(function() {
					initTree("2adba5c2ab424be69cedd0f52a776631", "角色权限");
			    });
				/**
					节点懒加载
				*/
		        function loaddata(node){
					var id = node.dataAttr.id;
					console.log("id is : " + id);
					var mappingName = node.dataAttr.mappingName; 
					getData(node);
		        }
				
				function getData(node){
					if (node instanceof Array) {
						node = node[0];
					}
					var id = node.dataAttr.id;
					var mappingName = node.dataAttr.mappingName; 
					Ajax.ajax("admin/treeview/getChildrenNode", {
						code	:	id,
						mappingName	:	mappingName
					},function(data){
						console.log("data is :");
						console.log(data);
						var tree_data = new Array();
						setTreeNode(node, data, tree_data);
						$("#treeview", $page).treeview("addNode", [tree_data, node]);
					});
				}
				
				function setTreeNode(node, data, nodeArray){
					var mappingNameMap = data["mappingNameMap"];
					var relations = data["relations"];
					for(var key in relations){
						if(relations[key] instanceof Array){
							var id = key;
							var array = relations[key];
							
							var nodeObj = new Object();							
							nodeObj.text = array[1];
							nodeObj.lazyLoad = true;
							nodeObj.dataAttr = {
									mappingName : mappingNameMap[array[0]],
									id	:	id,
							};
							nodeObj.level = node.level + 1;
							nodeObj.tags = [array[0]];
							nodeArray.push(nodeObj);
						}
					}
					return nodeArray;
				}
				
		        /**
					添加节点
				*/
				$("#add-node-btn", $page).on('click', function(){
					var nodes = $("#treeview", $page).treeview("getSelected");
					console.log($('#treeview').treeview('getNodes', nodes[0]));
					if(nodes.length == 0){
						Dialog.notice("请选择一个节点！", 'warning');
						return;
					}
					console.log("-----添加节点");
					var mappingName = nodes[0].dataAttr.mappingName;
					var id = nodes[0].dataAttr.id;
					console.log(mappingName);
					console.log(id);
					Dialog.openDialog("admin/treeview/add?type=relation&mappingName=" + mappingName + "&id=" + id +"&rootId=${id}","添加关系节点", "add-tree-relation-node",{
						width	: 600,
						height	: 400
					});
				});
		        
		        function addRootNode(){
		        	console.log("创建根节点");
		        	var singleNode = {
		        		    text: "新增节点1",
		        	 }
		        	 $("#treeview", $page).treeview("addNode", [singleNode,[]]);
		        }
		        
				/**
					编辑节点
				*/
				$("#edit-node-btn", $page).on('click', function(){
					var nodes = $("#treeview", $page).treeview("getSelected");	
					if(nodes.length == 0){
						Dialog.notice("请选择一个节点！", 'warning');
						return;
					}
					var mappingName = nodes[0].dataAttr.mappingName;
					var id = nodes[0].dataAttr.id;
					
					Dialog.openDialog("admin/treeview/edit?mappingName=" + mappingName + "&id=" + id + "&rootId=${id}", "编辑关系节点", "edit-tree-relation-node",{
						width	: 600,
						height	: 400
					});
				});
				
				/*
					编辑节点时，将被编辑节点下的子节点复制到编辑后的节点上
				*/
				function setNode(sourceNode, newNode){
					
					newNode["text"] = sourceNode.text;
					newNode["dataAttr"] = sourceNode.dataAttr;
					newNode["tags"] = sourceNode.tags;
					newNode["lazyLoad"] = sourceNode.lazyLoad;
					
					if(typeof(sourceNode.nodes) == 'undefined' ){
						return newNode;
					}else{
						var nodes = new Array();
						for(var i=0; i<sourceNode.nodes.length; i++){
							var newSubNode = new Object();
							nodes[i] = setNode(sourceNode.nodes[i], newSubNode);
						}
						newNode["nodes"] = nodes;
						return newNode;
					}
				}
				/**
					删除节点
				*/
				$("#remove-node-btn", $page).on('click', function(){
					var nodes = $("#treeview", $page).treeview("getSelected");
					if(nodes.length == 0){
						Dialog.notice("请选择一个节点！", 'warning');
						return;
					}
					console.log("选中的节点是：" + nodes);
					Dialog.confirm('确认删除节点？', function(yes){
						if(yes){
							Ajax.ajax("admin/treeview/delete", {
								code	:	nodes[0].dataAttr.id
							}, function(data){
								if(data){
									$("#treeview", $page).treeview("removeNode", [ nodes, { silent: true } ]);
									Dialog.notice("删除成功！", "success");
								}else{
									Dialog.notice("删除失败！", "error");
								}
							});
						}
					});
				});
				
				$page.data("addSubmit", function(mapJson){
					var selectedNode = $("#treeview", $page).treeview("getSelected");
					var mappingName = selectedNode[0].dataAttr.mappingName;	
					Ajax.ajax("admin/treeview/saveNode", {
						mappingName	:	mappingName,
						paramJson	: JSON.stringify(mapJson)
					}, function(data){
						//TODO 关闭窗口，刷新添加后的树（调用点击父节点的方法刷新）
						console.log("success");
						refreshNode(selectedNode[0]);
						Dialog.closeDialog("add-tree-relation-node");	//关闭节点添加窗口
						Dialog.notice("添加成功！", "success");	//提示信息
					});
				});
				
				$page.data("editSubmit", function(mapJson){
					var selectedNode = $("#treeview", $page).treeview("getSelected");
					var mappingName = selectedNode[0].dataAttr.mappingName;	
					Ajax.ajax("admin/treeview/saveNode", {
						mappingName	:	mappingName,
						paramJson	: JSON.stringify(mapJson)
					}, function(data){
						//TODO 关闭窗口，刷新添加后的树（调用点击父节点的方法刷新）
						console.log("success");
						console.log(mapJson);
						
						var nodes = $("#treeview", $page).treeview("getSelected");	
						refreshNode($("#treeview", $page).treeview("getParents", nodes[0]));
						
						Dialog.closeDialog("edit-tree-relation-node");	//关闭节点添加窗口
						Dialog.notice("修改成功！", "success");
					});
				});
				function refreshNode(node){
					if (node instanceof Array) {
						node = node[0];
					}
					if(typeof(node.nodes) !='undefined' && node.nodes.length > 0){
						var length = node.nodes.length;
						for(var i=0; i<length; i++){
							console.log("------正在删除");
							console.log(node.nodes[i]);
							$('#treeview').treeview('removeNode', [node.nodes[i], { silent: true } ]);
							length--;
							i--;
						}
					}
					getData(node);
				}
			});	 
			</script>
		</div>
	</div>
</div>