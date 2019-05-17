define(function(require, exports, module){
	var EVENT_MAP = {
		'on-click' 	: 'click',
		'on-change'	: 'change',
		'on-input'	: 'input'
	};
	exports.bindScopeEvent = function($scope, events, doWithObj, retainAttr){
		for(var eventName in EVENT_MAP){
			$scope
				.filter('[' + eventName + ']')
				.add($scope.find('[' + eventName + ']'))
				.each(function(){
					var $this = $(this);
					var callbackName = $this.attr(eventName);
					var callbackParam = undefined;
					var args = [];
					if(events && callbackName){
						if(doWithObj){
							var matcher = /^do:(.+)\((.+)\)$/.exec(callbackName);
							if(matcher){
								callbackName = matcher[1];
								callbackParam = matcher[2];
								var exp = 'with(doWithObj){[' + callbackParam + ']}';
								try{
									args = eval(exp);
								}catch(e){
									console.error($this[0]);
									$.error(_this.getPath() + '模板中无法解析的表达式' + exp);
								}
							}
						}
						
						if(events[callbackName]){
							$this.on(EVENT_MAP[eventName], callbackParam? function(e){
								e.preventDefault();
								events[callbackName].apply(this, args.concat($.merge([], arguments).slice(1)));
							}: events[callbackName]);
						}
					}
					if(retainAttr == false){
						$this.removeAttr(eventName);
					}
				});
		}
	}
});