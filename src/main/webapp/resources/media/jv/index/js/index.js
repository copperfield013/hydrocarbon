define(function(require, exports, module){
	exports.init = function(_param){
		var Ajax = require('ajax');
		var $CPF = require('$CPF');
		var defParam = {
			$page	: null
		};
		var param = $.extend({}, defParam, _param);
		var $page = param.$page;
		//调用api获得用户信息
		$CPF.showLoading();
		var anotherOver = false;
		function closeLoading(){
			if(anotherOver){
				$CPF.initPage($page);
				$CPF.closeLoading();
			}else{
				anotherOver = true;
			}
		}
		Ajax.ajax('api2/meta/user/current_user', function(data){
			if(data.user){
				console.log('用户信息');
				console.log(data);
				var $accountArea = $('#account-area', $page);
				$('.account-username', $accountArea).text(data.user.nickname);
				closeLoading();
			}
		});
		
		//调用api获得菜单信息
		
		Ajax.ajax('api2/meta/menu/get_menu', function(data){
			//获得菜单项模板显示
			if(data.menus){
				require('tmpl').load('media/jv/index/tmpl/menu_item.tmpl').done(function($menuItemTmpl){
					var $menu = $menuItemTmpl.tmpl(data);
					$('#sidebar>ul').append($menu);
					closeLoading();
				})
			}
		});
		
		$('#logout').click(function(){
			require('dialog').confirm('确认退出登录？').done(function(){
				localStorage.removeItem('datacenter-jv-token');
				location.href = 'jv/main/login';
			});
		});
		
	}
});