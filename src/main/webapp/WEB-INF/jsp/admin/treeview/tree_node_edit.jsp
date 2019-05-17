<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<div id="tree-node-edit">
	<div class="page-body">
		<div class="row">
			<div class="col-lg-12">
				<form id="edit-node-form" class="bv-form form-horizontal validate-form" action="#">
					<input type="hidden" id="parentNodeId" name="parentNodeId" value="${parentNodeId }"/>
					<input type="hidden" id="parentMappingName" name="parentMappingName" value="${mappingName }">
					<input type="hidden" id="parentMappingName" name="mappingName" value="${mappingName }">
					<c:forEach items="${abcAttrNodeList }" var="abcAttrNode">
						<div class="form-group">
							<label class="col-lg-2 control-label" for="${abcAttrNode.abcattrName }">${abcAttrNode.abcattrName }</label>
							<div class="col-lg-5">
								<c:if test="${abcAttrNode.controltype == 'text'}">
									<input type="text" id="${abcAttrNode.abcattrName }" class="form-control" name="${abcAttrNode.abcattrName }" value="${node[abcAttrNode.abcattrName] }"/>
								</c:if>
							</div>
						</div>
					</c:forEach>
					<div class="form-group">
			        	<div class="col-lg-offset-3 col-lg-3">
			        		<input id="close-and-submit-btn" class="btn btn-block btn-darkorange" type="button" value="提交" />
				        </div>
					</div>
				</form>
				<script type="text/javascript">
					seajs.use(['ajax'], function(Ajax){
						$("#close-and-submit-btn").on('click', function(){
							//var $page = $("#tree_view_${rootId}");
							var $page = $("#tree_view_panel");
							console.log($("#edit-node-form").serializeJson());
							$page.data("editSubmit")($("#edit-node-form").serializeJson());
						});
					});
				</script>
			</div>
		</div>
	</div>
</div>