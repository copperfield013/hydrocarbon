<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<link rel="stylesheet" href="media/admin/relationtreeview/css/treeview.css">
<script src="media/admin/plugins/beyond/js/select2/select2.js"></script>
<script src="media/admin/plugins/sortable/Sortable.js"></script>
<script src="media/admin/relationtreeview/js/treeview.js"></script>

<div id="relation_tree_view_panel">
		<input type="hidden" id="rootId" value="${id }">
		<input type="hidden" id="rootMappingName" value="${mappingName }">
		<div class="page-header">
			<div class="header-title">
				<h1>树形插件</h1>
			</div>
		</div>
		<nav>
			<form class="form-inline" action="#">
				<div class="form-group">
					<label for="name">显示属性的个数：</label>
					<input id="attr-count" type="text" class="form-control" name="attrCount" value="${abcNodeAttrCount }"/>
					(*数值最大为${abcNodeAttrSize })
					<input id="attr-max-size" type="hidden" value="${abcNodeAttrSize }"/>
					<button id="attr-count-determine-btn" type="button" class="btn btn-default">确定</button>
				</div>
			</form>
		</nav>
        <div class="entity-edit-wrap active">
            <!-- 实体标题:begin -->
            <div class="entity-title collapse-header al-save need-ajax" data-mappingName="${mappingName }"  data-abcattrCode="${resultList[0] }" data-order="${btNode.order}" data-id="${resultList[0] }">
	            <div class="icon-label-master">
	                <i class="icon-root icon"></i>
	                <span class="text">ABC</span>
	            </div>
               <c:forEach items="${resultList }" begin="1" var="attrNodeName">
            		<input disabled type="text" class="edit-input" value="${attrNodeName }" title="">
            	</c:forEach>
            	 <span class="entity-only-title">${btNode.basicItem.cnName}</span>
                <div class="btn-wrap">
                	<!-- <i class="icon icon-save"></i> -->
                    <i class="icon icon-add"></i> 
                    <!--  <a title="编辑" class="tree-node-edit-btn" style="font-size: 14px;position:absolute;right:130px;" href="javascript:;">详情</a>
                   <i class="icon icon-trash"></i> -->
                    <i class="icon icon-arrow"></i>
                </div>
            </div>
            <!-- 实体标题:end -->
            <!-- 标签 不能拖拽 始终在第一个-->
           
            <!-- 拖拽排序wrap -->
            <ul class="dragEdit-wrap dragEdit-wrap-1 collapse-content  collapse-content-active" id="dragEdit-1">
                <!-- 属性-->
               
                <!-- 属性组 -->
               
                <!-- 多值属性 -->
               
                <!-- 关系 -->

            </ul>

        </div>
    </div>
