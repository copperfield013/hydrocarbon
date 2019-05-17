<%@page import="cn.sowell.copframe.spring.properties.PropertyPlaceholder"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<sec:authorize access="hasAuthority('${configAuth }')">
	<li>
		<a href="#" class="menu-dropdown">
			<i class="menu-icon fa fa-desktop"></i>
			<span class="menu-text">系统配置</span>
		</a>
		<ul class="submenu">
			<li>
				<a class="tab" href="admin/config/sidemenu" target="sidemenu" title="功能列表管理">
		   			<span class="menu-text">功能列表管理</span>
		   		</a>
			</li>
		</ul>
	</li>
</sec:authorize>

<c:forEach var="menu" items="${menus }">
	<c:if test="${l1disables[menu.id] != true }">
		<li>
			<a href="#" class="menu-dropdown">
			    <i class="menu-icon fa fa-bookmark"></i>
			    <span class="menu-text">${menu.title }</span>
			</a>
			<ul class="submenu">
				<c:forEach var="level2" items="${menu.level2s }">
					<c:if test="${l2disables[level2.id] != true}">
						<li>
							<c:choose>
								<c:when test="${level2.templateGroupId != null }">
									<a class="tab" href="admin/modules/curd/list/${level2.id }" 
							   			target="entity_list_${level2.id }" title="${level2.title }">
							   			<span class="menu-text">${level2.title }</span>
							   		</a>
								</c:when>
								<c:when test="${level2.statViewId != null}">
									<a class="tab" href="admin/stat/view/index/${level2.id }" 
							   			target="stat_view_${level2.statViewId }" title="${level2.title }">
							   			<span class="menu-text">${level2.title }</span>
							   		</a>
								</c:when>
							</c:choose>
					   	</li>
					</c:if>
				</c:forEach>
			</ul>
		</li>
	</c:if>
</c:forEach>
<li>
	<a href="#" class="menu-dropdown">
		<i class="menu-icon fa fa-bookmark"></i>
		<span class="menu-text">demo</span>
		<i></i>
	</a>
	<ul class="submenu">
	   	<li>
	   		<a class="tab" href="admin/treeview/treeView?id=2adba5c2ab424be69cedd0f52a776631&mappingName=角色权限" target="treeview" title="树形控件">
	   			<span class="menu-text">树形控件</span>
	   		</a>
	   	</li>
	   	<li>
	   		<a class="tab" href="admin/treeview/tree_view_new?id=f0e47e1f25a747a492e3ea0e75f3603b&mappingName=角色权限" target="treeview" title="树形控件">
	   			<span class="menu-text">树形控件</span>
	   		</a>
	   	</li>
	   		<li>
	   		<a class="tab" href="admin/relationtreeview/tree_view_new?id=3ad48d5ac71e46e4b99f66ae7cab7158&mappingName=人口信息" target="relationtreeview" title="树形控件">
	   			<span class="menu-text">关系人口-tree</span>
	   		</a>
	   	</li>
	</ul>
</li>
