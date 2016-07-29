// Knockout Validator JavaScript library Beta 1
// (c) George Mavritsakis - g.mavritsakis@gmail.com
// License: MIT (http://www.opensource.org/licenses/mit-license.php)

(function (ko, undefined) {
	ko.bindingHandlers['vHandle'] = {
		init: function (element, valueAccessor, allBindingsAccessor, defaultHanlderNames) {
			var observable = valueAccessor(),
				handlersNames = defaultHanlderNames || ko.validator.extensions.defaultAppliers,
				handlerName, handlerFunction,
				handlers = [],
				i, l;

			if (!ko.validator.isValidating(observable)) {
				return;
			}

			var allBindings = allBindingsAccessor();


			if (allBindings.handlers) {
				handlersNames = allBindings.handlers.split(",");

				for (i = 0, l = handlersNames.length; i < l; i++) {
					//trim
					handlersNames[i] = handlersNames[i].replace(/^\s\s*/, '').replace(/\s\s*$/, '');
				}
			}

			if (handlersNames.lenght === 0) {
				return;
			}

			for (i = 0, l = handlersNames.length; i < l; i++) {
				handlerName = handlersNames[i];
				if ((handlerFunction = ko.validator.extensions.validationAppliers[handlerName])) {
					//instantiate handlers
					handlers.push(new handlerFunction(element, valueAccessor, allBindingsAccessor, observable.isValid, observable.errorMessage));
				}
			}

			//if handlers found
			if (handlers.lenght === 0) {
				return;
			}

			function apply(val) {
				for (i = 0, l = handlers.length; i < l; i++) {
					handlers[i].apply(val);
				}
			}

			observable.isValid.subscribe(function (newVal) {
				apply(newVal);
			});

			apply(observable.isValid());
		},
		update: function (element, valueAccessor, allBindingsAccessor) {
			//do nothing, but i figured out that if this is not here, init wont do!
		}
	};

	ko.bindingHandlers['vValue'] = {
		'init': function (element, valueAccessor, allBindingsAccessor) {
			ko.bindingHandlers['vHandle'].init(element, valueAccessor, allBindingsAccessor, ['classApply', 'attrErrorApply']);
			ko.bindingHandlers['value'].init(element, valueAccessor, allBindingsAccessor);
		},
		'update': function (element, valueAccessor) {
			ko.bindingHandlers['value'].update(element, valueAccessor);
		}
	};
	ko.bindingHandlers['vErrorInput'] = {
		'init': function (element, valueAccessor, allBindingsAccessor) {
			ko.bindingHandlers['vHandle'].init(element, valueAccessor, allBindingsAccessor, ['classApply', 'attrErrorApply']);
		},
		'update': function (element, valueAccessor) {
		}
	};
	ko.bindingHandlers['vErrorResult'] = {
		'init': function (element, valueAccessor, allBindingsAccessor) {
			ko.bindingHandlers['vHandle'].init(element, valueAccessor, allBindingsAccessor, ['visibilityApply', 'textErrorApply']);
		},
		'update': function (element, valueAccessor) {
		}
	};

	ko.bindingHandlers['vErrorVisibility'] = {
		'init': function (element, valueAccessor, allBindingsAccessor) {
			ko.bindingHandlers['vHandle'].init(element, valueAccessor, allBindingsAccessor, ['visibilityApply']);
		},
		'update': function (element, valueAccessor) {
		}
	};

	ko.validator.extensions = {
		validationAppliers: {},
		defaultAppliers: []
	};

	ko.validator.extensions.validationAppliers['classApply'] = function (element, valueAccessor, allBindingsAccessor, isValidObservable, errMessageObservable) {
		var allBindings = allBindingsAccessor(),
				errCls = allBindings.vErrorClass || ko.validator.extensions.validationAppliers['classApply'].defaults.errorClass,
				successCls = allBindings.vSucccesssClass || ko.validator.extensions.validationAppliers['classApply'].defaults.succcesssClass;

		this.apply = function (isValid) {
			if (isValid) {
				if (errCls) { $(element).removeClass(errCls); }
				if (successCls) { $(element).addClass(successCls); }
			} else {
				if (errCls) { $(element).addClass(errCls); }
				if (successCls) { $(element).removeClass(successCls); }
			}
		};
	};
	ko.validator.extensions.validationAppliers['classApply'].defaults = {
		errorClass: 'vd-err',
		succcesssClass: null
	};

	ko.validator.extensions.validationAppliers['visibilityApply'] = function (element, valueAccessor, allBindingsAccessor, isValidObservable, errMessageObservable) {
		this.apply = function (isValid) {
			if (isValid) {
				$(element).hide();
			} else {
				$(element).show();
			}
		};
	};

	ko.validator.extensions.validationAppliers['attrErrorApply'] = function (element, valueAccessor, allBindingsAccessor, isValidObservable, errMessageObservable) {
		this.apply = function (isValid) {
			$(element).attr('err-msg', errMessageObservable());
		};
	};

	ko.validator.extensions.validationAppliers['textErrorApply'] = function (element, valueAccessor, allBindingsAccessor, isValidObservable, errMessageObservable) {
		this.apply = function (isValid) {
			$(element).text(errMessageObservable());
		};
	};

})(ko);


(function ($,ko, undefined) {
	var xOffset = -10, // x distance from mouse
		yOffset = 10; // y distance from mouse

	$(".vd-err").live('mouseenter', function (e) {
		var msg = $(this).attr('err-msg'),
			topPoint = e.pageY + yOffset,
			leftPoint =e.pageX + xOffset;
		
		if ($('p#vtip').none()) {
			$('body').append('<p id="vtip" class="err"></p>');
		}

		$('p#vtip').stop();

		$('#vtip').html('<img id="vtipArrow"/>' + msg);

		$('p#vtip #vtipArrow').attr("src", '/images/vtip/vtip_arrow_white.png');
		$('p#vtip').css({ top: topPoint + 40, left: leftPoint, opacity: 0, display: 'block' });
		$('p#vtip').animate({ top: topPoint, opacity: 1 });
	});

	$(".vd-err").live('mouseleave', function (e) {
		$("p#vtip").stop().fadeOut();
	});

	$(".vd-err").live('mousemove', function (e) {
		if ($("p#vtip").css('opacity') != 1) {
			return;
		}
		var topPoint = e.pageY + yOffset,
			leftPoint =e.pageX + xOffset;

		$("p#vtip").css("top", topPoint + "px").css("left", leftPoint + "px");
	});

})(jQuery,ko);