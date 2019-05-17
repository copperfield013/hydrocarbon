<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<div id="entity-selection">
	<nav>
		<form class="form-inline" action="admin/relationtreeview/openSelection">
			<input type="hidden" name="mappingName" value="${mappingName }">
			<input type="hidden" name="relationName" value="${relationName }">
			<input type="hidden" name="attrCount" value="${attrCount }">
			
			<c:forEach items="${attrMap }" var="map" begin="0" end="2">
				<div class="form-group">
					<label for="name">${map.key }</label>
					<input type="text" class="form-control" name="${map.key }" value="${map.value }" />
				</div>
			</c:forEach>
			
			<button type="submit" class="btn btn-default">查询</button>
		</form>
	</nav>
	<div class="row list-area">
		<input type="hidden" id="mappingName" value="${mappingName }">
		<input type="hidden" id="relationName" value="${relationName }">
		<table class="table">
			<thead>
				<tr>
					<th>#</th>
					<c:forEach items="${attrNameList }" var="attr"  begin="0" end="4">
						<th>${attr.title }</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reulstMap }" var="entityMap" varStatus="i">
					<tr data-id="${entityMap.key }">
						<td>${i.index + 1 }</td>
						<c:forEach items="${entityMap.value }" var="map"  begin="0" end="4">
                                <td>${map.value}</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="cpf-paginator" pageNo="${pageInfo.pageNo }" pageSize="${pageInfo.pageSize }" count="${pageInfo.count }"></div> 
	</div>
</div>
<div class="modal-footer">
	<div class="row">
		<div class="col-lg-3 col-lg-offset-4">
			<input id="btn-conf" class="btn btn-primary btn-block" type="button" value="确定" /> 
		</div>
	</div>
</div>

<style>
#entity-selection .list-area tbody>tr.selected {
    background-color: #87CEEB;
    color: #fff;
}
</style>

<script>
	seajs.use(['dialog','utils'], function(Dialog, Utils){
		var $page = $('#entity-selection');
		$(function () {
		    //除了表头（第一行）以外所有的行添加click事件.
		    $("tr").slice(1).click(function () {
	            $(this).addClass("selected");
	            $(this).siblings().removeClass("selected");
		    });
		});
		
		$("#btn-conf").on('click', function(){
			var $pag = $("#relation_tree_view_panel");
			var mappingName = $("#entity-selection .list-area").find("#mappingName").val();
			var relationName = $("#entity-selection .list-area").find("#relationName").val();
			
			if ($("#entity-selection .list-area tbody>tr.selected").length == 0) {
				Dialog.notice("请选择一个！", "warning");
				return;
			}
			var code = $("#entity-selection .list-area tbody>tr.selected").attr("data-id");
			$pag.data("selectEntity")(code+ " " + mappingName + " " +relationName);
		});
		
		
	});
</script>