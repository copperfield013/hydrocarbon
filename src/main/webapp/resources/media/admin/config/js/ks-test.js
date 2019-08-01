define(function(require, exports, module){
	"user strict"
	var Utils = require('utils');
	var Ajax = require('ajax');
	var Dialog = require('dialog');
	
	var globalUserTokenCached = null;
	/**
	 * 
	 */
	exports.init = function(_param){
		var defaultParam = {
			$page 	: null,
			ksId	: null
		}
		
		var param = $.extend({}, defaultParam, _param);
		
		var $page = param.$page;
		
		var context = Utils.createContext({params:{}, pathVar:{}});
		
		require('event').prepareToContext($page, context);
		require('event').bindScopeEvent($page, getCallbacks());
		
		context.bind('token', function(val){
			//绑定token设置的事件
			if(val.after){
				context.getDom('btn-sign-in').attr('disabled', 'disabled');
			}else{
				context.getDom('btn-sign-in').removeAttr('disabled');
			}
		}).bind(['ks', 'pathVar'], function(val){
			//绑定修改路径参数事件
			var ks = context.getStatus('ks');
			var pathVarMap = val.after;
			var path = ks.path;
			for(var name in pathVarMap){
				path = path.replace(new RegExp('\\{\\s*' + name + '\\s*\\}'), pathVarMap[name]);
			}
			context.setStatus('path', path);
		}).bind(['path', 'token'], function(){
			var path = context.getStatus('path');
			if(path && context.getStatus('token')){
				context.getDom('path').val(path);
				context.getDom('btn-submit').removeAttr('disabled');
			}else{
				context.getDom('btn-submit').attr('disabled', 'disabled');
			}
		}).bind(['tmplMap', 'ks'], function(){
			var ks = context.getStatus('ks');
			var tmplMap  = context.getStatus('tmplMap');
			if(ks && tmplMap){
				tmplMap['ks-criterias'].replaceIn($page, {ks}, {
					triggerCriteriaChange	: function(criteria){
						var value = $(this).val();
						if(criteria.source === 'path-var'){
							context.getStatus('pathVar')[criteria.name] = value;
							context.setStatus('pathVar');
						}
					},
				})
			}
		});
		
		initInput();
		loadKs();
		loadTmpl();
		testUserTokenCached(globalUserTokenCached);
		
		function loadKs(){
			Ajax.ajax('admin/config/ks/load_ks/' + param.ksId).done(function(data){
				if(data.ks){
					context.setStatus('ks', data.ks);
				}
			});
		}
		
		function loadTmpl(){
			require('tmpl').load('media/admin/config/tmpl/ks-test.tmpl').done(function(tmplMap){
				context.setStatus('tmplMap', tmplMap);
			});
		}
		
		function initInput(){
			var resEditor = ace.edit(context.getDom('result')[0], {
	            theme: "ace/theme/monokai",
	            mode: "ace/mode/json",
	            wrap: true,
	            autoScrollEditorIntoView: true,
	            enableBasicAutocompletion: true,
	            enableSnippets: true,
	            enableLiveAutocompletion: true,
	        });
			resEditor.setReadOnly(true);
			context.setStatus('resEditor', resEditor);
		}
		
		function testUserTokenCached(userTokenCached){
			if(userTokenCached){
				Ajax.ajax('api2/auth/test', undefined, {headersHandler:tokenHeaderSetter(userTokenCached.token)})
					.done(function(){
						context.getDom('username').val(userTokenCached.username);
						context.getDom('password').val('*******');
						context.setStatus('token', userTokenCached.token);
					}).error(function(){
						globalUserTokenCached = null;
					});
			}
		}
		
		
		function tokenHeaderSetter(token){
			return function(headers){
				headers['datacenter-token'] = token;
			}
		}
		
		function getCallbacks(){
			return {
				//登录
				signIn	: function(){
					var username = context.getDom('username').val();
					var password = context.getDom('password').val();
					Ajax.ajax('api2/auth/token', {username, password}).done(function(data){
						if(data.status === 'suc' && data.token){
							Dialog.notice('登录成功', 'success');
							context.setStatus('token', data.token);
							globalUserTokenCached = {username:username, token:data.token};
						}else{
							Dialog.notice('登录失败', 'error');
						}
					});
				},
				userChanged	: function(){
					if(context.getDom('btn-sign-in').prop('disabled') && context.getStatus('token')){
						context.getDom('btn-sign-in').removeAttr('disabled');
					}
				},
				
				submit		: function(){
					var path = context.getStatus('path');
					var params = context.getStatus('params');
					var API_PREFIX = 'api2/ks/c/';
					if(path){
						Ajax.ajax(API_PREFIX + path, params, undefined, {headersHandler:tokenHeaderSetter(context.getStatus('token'))}).done(function(data){
							var resEditor = context.getStatus('resEditor');
							if(data.result){
								Dialog.notice('请求成功', 'success');
								resEditor.setValue(JSON.stringify(data.result, null, '\t'));
							}else{
								Dialog.notice('没有返回数据', 'warning');
								resEditor.setValue('');
							}
						});
					}
				}
			};
		}
	}
});