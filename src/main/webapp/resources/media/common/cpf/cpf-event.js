define(function(require, exports, module){
	var EVENT_MAP = {
		'on-click' 		: 'click',
		'on-change'		: 'change',
		'on-input'		: 'input',
		'on-dblclick'	: 'dblclick',
		'on-render'		: null,
		'after-render'	: null
	};
	exports.bindScopeEvent = function($scope, events, doWithObj, retainAttr){
		var afterRenderCallbacks = $.Callbacks();
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
									$.error('模板中无法解析的表达式' + exp);
								}
							}
						}
						if(events[callbackName]){
							if(eventName === 'on-render'){
								callbackParam? 
									events[callbackName].apply(this, args.concat($.merge([], arguments).slice(1)))
									: events[callbackName].apply(this)
							}else if(eventName === 'after-render'){
								var that = this;
								var thisArgs = arguments;
								afterRenderCallbacks.add(function(){
									callbackParam? 
											events[callbackName].apply(that, args.concat($.merge([], thisArgs).slice(1)))
											: events[callbackName].apply(that)
								});
							}else{
								$this.on(EVENT_MAP[eventName], callbackParam? function(e){
									e.preventDefault();
									return events[callbackName].apply(this, args.concat($.merge([], arguments).slice(1)));
								}: events[callbackName]);
							}
						}
					}
					if(retainAttr == false){
						$this.removeAttr(eventName);
					}
				});
		}
		return {
			afterRenderCallbacks
		};
	}
});