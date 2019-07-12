define(function(require, exports, module){
	
	var Utils = require('utils');
	var $CPF = require('$CPF');
	var Ajax = require("ajax");
	var Dialog = require('dialog');
	exports.init = function(_param){
		var param = $.extend({
			$page	: null
		}, _param);
		$CPF.showLoading();
		
		var $page = param.$page;
		var pageEvents = getPageEvents();
		
		var context = Utils.createContext({
			ksList	: [],
			KS_TYPE_MAP	: require('config/js/ks-edit').KS_TYPE_MAP
		});
		require('event').bindScopeEvent($page, pageEvents);
		require('event').prepareToContext($page, context);
		
		context.bind('ksList', renderList)
			;
		
		require('tmpl').load('media/admin/config/tmpl/ks-list.tmpl').done(function(tmplMap){
			context.setStatus('tmplMap', tmplMap);
		});
		
		loadList();
		
		
		function renderList(){
			var tmplMap = context.getStatus('tmplMap');
			if(tmplMap){
				tmplMap['ks-list'].replaceIn($page, context.properties, {
					
				});
				$CPF.closeLoading();
			}
		}
		
		function loadList(){
			var formData = new FormData(context.getDom('form')[0]);
			Ajax.ajax('admin/config/ks/load_all_ks', formData).done(function(data){
				if(data.status === 'suc' && data.ksList){
					context.setStatus('ksList', data.ksList); 
				}
			});
		}
		
		function confirmToMultiOperate(confirmMsg, url){
			var def = $.Deferred();
			var ksIds = [], ksTitles = [];
			var checkedRowGetter = context.getDom('table').data('checkedRowGetter');
			if(checkedRowGetter){
				var $rows = checkedRowGetter();
				$rows.each(function(){
					ksIds.push(this.getAttribute('data-id'));
					ksTitles.push(this.getAttribute('data-title'));
				});
			}
			if(ksIds.length > 0){
				Dialog.confirm(confirmMsg + ' 操作的轻服务包括[' + ksTitles.join() + ']').done(function(){
					Ajax.ajax(url, {ksIds: ksIds.join()}).done(function(data){
						if(data.status === 'suc'){
							Dialog.notice('操作成功', 'success');
							def.resolve($rows);
						}else{
							Dialog.notice('操作失败', 'error');
						}
					});
				});
			}
			return def.promise();
		}
		
		function getPageEvents(){
			var pageEvents = {
				bindTable	: function(){
					$(this).on('row-selected-change', function(e, $checkedRows){
						var $btnEnable = context.getDom('btn-enable'),
							$btnDisable =  context.getDom('btn-disable'),
							$btnRemove =  context.getDom('btn-remove');
							
						if($checkedRows.length > 0){
							$btnRemove.removeAttr('disabled');
							var disabledCheckCount =  disabledCheckedCount = $checkedRows.filter('.ks-disabled').length
								enabledCheckedCount = $checkedRows.length - disabledCheckedCount;
							if(disabledCheckedCount > 0) $btnEnable.removeAttr('disabled');
							else $btnEnable.attr('disabled', 'disabled');
							if(enabledCheckedCount > 0) $btnDisable.removeAttr('disabled');
							else $btnDisable.attr('disabled', 'disabled');
						}else{
							$btnEnable
								.add($btnDisable)
								.add($btnRemove)
								.attr('disabled', 'disabled');
						}
						
					});
				},
				filterKsList	: function(){
					var keyword = $(this).val();
					console.log(keyword);
				},
				remove		: function(){
					confirmToMultiOperate('确认删除轻服务？', 'admin/config/ks/remove').done(function($rows){
						$rows.remove();
					});
				},
				toggleEnable: function(){
					confirmToMultiOperate('确认启用轻服务？', 'admin/config/ks/enable').done(function($rows){
						$rows.removeClass('ks-disabled');
					});
				},
				toggleDisable: function(){
					confirmToMultiOperate('确认禁用轻服务？', 'admin/config/ks/disable').done(function($rows){
						$rows.addClass('ks-disabled');
					});
				}
			};
			return pageEvents;
		}
		
	};
});