<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<div id="tree-add">
	<div class="page-body">
		<div class="row">
			<div class="col-lg-12">
				<form id="tree-relation-add-form" class="bv-form form-horizontal validate-form">
					<input type="hidden" id="parentNodeId" name="parentNodeId" value="${parentNodeId }"/>
					<input type="hidden" id="parentMappingName" name="parentMappingName" value="${mappingName }">
					<div id="select-entity-div" class="form-group">
						<label class="col-lg-2 control-label" for="select-entity">关系名称</label>
						<div class="col-lg-5">
							<input type="hidden" id="mappingName" name="mappingName">
							<select id="select-entity">
								<option value="">--请选择--</option>
								<c:forEach items="${nameList }" var="entityName">
									<option value="${entityName }" >${entityName }</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div id="relation-selected-div"></div>
					<div id="attr-area-div"></div>
					<div class="form-group">
			        	<div class="col-lg-offset-3 col-lg-3">
			        		<input id="close-and-submit-btn" class="btn btn-block btn-darkorange" type="button" value="提交" />
				        </div>
					</div>
				</form>
				<script type="text/javascript">
					seajs.use(['ajax'], function(Ajax){
						console.log("******添加节点******");
						$("#select-entity").on("change", function(){
							var checkedVal = $("#select-entity").val();
							var labelSetMap =  $.parseJSON('${labelSetMap }');
							var mappingNameMap =  $.parseJSON('${mappingNameMap }');
							var html = '<div class="form-group">' + 
										'<label class="col-lg-2 control-label" for="relation">关系类型</label> ' + 
										'<div class="col-lg-5"> ' + 
											'<select id="relation" name="relation"> ' +
												'<option value="">--请选择--</option> ' +
												'<c:forEach items="' + labelSetMap[checkedVal] + '" var="relationName"> ' +
													'<option value="${relationName }">${relationName }</option> ' +
												'</c:forEach> ' +
											'</select> ' +
										'</div> ' +
									'</div>';
							//var mappingName = $("#parentMappingName").val() + "." + checkedVal;
							var mappingName = mappingNameMap[checkedVal];
							$("#mappingName").val(mappingName);
							console.log(mappingNameMap);
							console.log("mapping name is : " + mappingName);
							Ajax.ajax('admin/treeview/getNodeAttr', {
								mappingName	:	mappingName
							}, function(data){
								var objArray = data.AttributesName; 
								var attrHtml = '';
								for (var i=0; i<objArray.length; i++){
									attrHtml = attrHtml + '<div class="form-group">' +
												'<label class="col-lg-2 control-label" for="' + objArray[i].abcattrName + '">' + objArray[i].abcattrName + '</label> ' + 
												'<div class="col-lg-5">';
									if(objArray[i].controltype == 'text'){
										attrHtml = attrHtml + '<input type="text" id="' + objArray[i].abcattrName + '" class="form-control" name="' + objArray[i].abcattrName + '" />'
									}
									attrHtml = attrHtml + '</div>' + 
												'</div>';
								}
								console.log("data json is : ");
								console.log(data);
								console.log(attrHtml);
								$("#attr-area-div").html(attrHtml);
							});
							$("#relation-selected-div").html(html);
							//$("#select-entity-div").append(html);
							//$("#select-entity-div").after(html);
						});
						
						$("#close-and-submit-btn").on('click', function(){
							$(this).attr("disabled","true"); //设置变灰按钮,防止多次提交
							//var $page = $("#tree_view_${rootId}");
							var $page = $("#tree_view_panel");
							console.log($page);
							var mappingName = $("#select-entity").val();
							var relation = $("#relation").val();
							var map = new Map();
							console.log($("#tree-relation-add-form").serializeJson());
							//$page.data("test")(JSON.stringify(map));
							$page.data("addSubmit")($("#tree-relation-add-form").serializeJson());
						});
						
						/*
							将序列化的表单转化为json对象
						*/
						/* $.fn.serializeJson=function(){  
				            var serializeObj={};  
				            var array=this.serializeArray();
				            $(array).each(function(){  
				                if(serializeObj[this.name]){  
				                    if($.isArray(serializeObj[this.name])){  
				                        serializeObj[this.name].push(this.value);  
				                    }else{  
				                        serializeObj[this.name]=[serializeObj[this.name],this.value];  
				                    }  
				                }else{  
				                    serializeObj[this.name]=this.value;   
				                }  
				            });  
				            return serializeObj;  
						};*/
					}); 
				
				</script>
			</div>
		</div>
	</div>
</div>