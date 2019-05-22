<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/base_empty.jsp"%>
<!DOCTYPE html>
<html>
	<head>
		<title><fmt:message key="theme.logo" bundle="${logo}"/></title>
		<link rel="icon" href="favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
		<jsp:include page="/WEB-INF/jsp/admin/common/admin-include.jsp"></jsp:include>
	</head>
	<body>
		<div class="navbar">
	        <div class="navbar-inner">
	             <div class="navbar-header pull-left">
	                 <a href="admin/logout" class="datacenter-logo">	                   
	                  <fmt:message key="theme.logo" bundle="${logo}"/>
	                 </a>
	             </div>
	             <div class="sidebar-collapse" id="sidebar-collapse">
	                    <i class="collapse-icon fa fa-bars"></i>
	             </div>
	             <%-- <c:if test="${fn:length(blocks) > 0 ||  sysConfig.showBlocksAnyway == 1}">
		             <div class="blocks-area">
		             	<ul>
		             		<li>
		             			<a>版块1</a>
		             			<ul>
		             				<li>
		             					<a>菜单1-1</a>
		             					<ul>
		             						<li><a>菜单1-1-1</a></li>
		             						<li><a>菜单1-1-2</a></li>
		             					</ul>
		             				</li>
		             				<li>
		             					<a>版块1-2</a>
		             				</li>
		             			</ul>
		             		</li>
		             		<li>
		             			<a>版块2</a>
		             			<ul>
		             				<li>
		             					<a>版块2-1</a>
		             				</li>
		             				<li>
		             					<a>版块2-2</a>
		             				</li>
		             			</ul>
		             		</li>
			             	<c:forEach items="${blocks }" var="block">
			             		
			             	</c:forEach>
		             	</ul>
		             </div>
	             </c:if> --%>
	             <div class="account-area">
	             	<div class="account-view">
	             		<div class="account-headicon">
		             		<i class="fa fa-user"></i>
		             	</div>
		             	<div class="account-userdesc">
		             		<p class="account-username">${user.nickname }</p>
		             		<p>管理员</p>
		             	</div>
		             	<div class="account-dropdown-icon">
		             		<i class="fa fa-caret-down"></i>
		             	</div>
	             	</div>
	             	<div class="account-dropdown-menu">
             			<div class="account-operate-list">
	             			<a class="tab" href="admin/config/user/detail" target="user_info">
             					<i class="fa fa-book"></i>
	             				用户详情
	             			</a>
	             			<a class="tab" href="admin/config/user/update" target="user_info">
             					<i class="fa fa-edit"></i>
	             				用户修改
	             			</a>
	             			<a class="jump" href="admin/logout">
             					<i class="glyphicon glyphicon-log-out"></i>
             					退出登录
             				</a>
             			</div>
             		</div>
	             	
	             </div>
	             
	             <%-- 
	             <div class="cmd-right">
	             	<span class="user-name">
		             	<a class="tab" href="admin/config/user/detail/" target="user_detail" title="用户信息">
		             		${user.nickname }
		             	</a>
	             	</span>
	                <a class="jump" href="admin/logout">退出登录</a>
	             </div> --%>

			</div>
		</div>
	    <div class="main-container container-fluid">
	        <div class="page-container">
	            <div class="page-sidebar" id="sidebar">
	                <ul class="nav sidebar-menu">
	                	<li class="open first">
	                		<a href="#">
	                			<i class="menu-icon glyphicon glyphicon-home"></i>
	                			<span class="menu-text">主页面</span>
	                		</a>
	                	</li>
	            		<jsp:include page="sidebar_menu.jsp" />
					</ul>
				</div>
				<div class="page-content">
					<div class="tabbable">	
						<div class="tab-warp">
							<a href="javascript:;" class="move left">◀</a>	
								<div class="nav-tabs-wrap">	
									<ul class="nav nav-tabs" id="main-tab-title-container">
										<li class="active main-tab-title">
										    <a data-toggle="tab" href="#cpf-home-tab">
												主页
										    </a>
										</li>
									</ul>
								</div>
							<a href="javascript:;" class="move right">▶</a>
						</div>				
						<div class="tab-content" id="main-tab-content-container">
							<div id="cpf-home-tab" class="tab-pane active main-tab-content">
								<jsp:include page="home.jsp"></jsp:include>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	    <script src="media/admin/plugins/beyond/js/bootstrap.js"></script>
	    <script src="media/admin/plugins/beyond/js/toastr/toastr.js"></script>
	    <script src="media/admin/plugins/beyond/js/beyond.min.js"></script>
	    
	    <script type="text/javascript">
	    	$(function(){
	    		seajs.config({
				base	: '${basePath}media/admin/',
				paths	: {
					  COMMON	: '${basePath}media/common/',
					  MAIN		: '${basePath}media/admin/main/js/'
				},
				alias	: {
					'jquery'	: 'COMMON/cpf/cpf-jquery.js?v=${cpfVersion}',
					'$CPF'		: 'COMMON/cpf/cpf-core.js?v=${cpfVersion}',
					'utils'		: 'COMMON/cpf/cpf-utils.js?v=${cpfVersion}',
					'page'		: 'COMMON/cpf/cpf-page.js?v=${cpfVersion}',
					'dialog'	: 'COMMON/cpf/cpf-dialog.js?v=${cpfVersion}',
					'paging'	: 'COMMON/cpf/cpf-paging.js?v=${cpfVersion}',
					'tree'		: 'COMMON/cpf/cpf-tree.js?v=${cpfVersion}',
					'form'		: 'COMMON/cpf/cpf-form.js?v=${cpfVersion}',
					'tab' 		: 'COMMON/cpf/cpf-tab.js?v=${cpfVersion}',
					'ajax'		: 'COMMON/cpf/cpf-ajax.js?v=${cpfVersion}',
					'poll'		: 'COMMON/cpf/cpf-poll.js?v=${cpfVersion}',
					'event'		: 'COMMON/cpf/cpf-event.js?v=${cpfVersion}',
					'css'		: 'COMMON/cpf/cpf-css.js?v=${cpfVersion}',
					'timer'		: 'COMMON/cpf/cpf-timer.js?v=${cpfVersion}',
					'console'	: 'COMMON/cpf/cpf-console.js?v=${cpfVersion}',
					'control'	: 'COMMON/cpf/cpf-control.js?v=${cpfVersion}',
					'checkbox'	: 'COMMON/cpf/cpf-checkbox.js?v=${cpfVersion}',
					'innerpage'	: 'COMMON/cpf/cpf-innerpage.js?v=$${cpfVersion}',
					'tmpl'		: 'COMMON/cpf/cpf-tmpl.js?v=${cpfVersion}',
					'select2'	: '${basePath}media/admin/plugins/select2/js/select2.full.js',
					'indexer'	: 'COMMON/cpf/cpf-indexer.js?v=${cpfVersion}'
				}
	    		});
	    		
	    		seajs.use('COMMON/cpf/cpf-main.js?v=${cpfVersion}');
	    	});
	    </script>
	</body>
</html>