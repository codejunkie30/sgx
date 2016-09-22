// Knockout Validator JavaScript library Beta 1
// (c) George Mavritsakis - g.mavritsakis@gmail.com
// License: MIT (http://www.opensource.org/licenses/mit-license.php)

(function (ko, undefined) {
	ko.subscribable.fn.validate = function (options) {
		//private properties
		var self = this, //subscribable
			_childValidators = null,
			_childSubscriptions = null,
			innerValid = ko.observable(),
			childrenValid = ko.observable(),
			lastErrror = '';


		//public properties
		self.isValid = ko.computed(function () {
			return innerValid() && childrenValid();
		});
		self.isInvalid = ko.computed(function () { return !self.isValid(); });
		self.errorMessage = ko.computed(function () { return self.isValid() ? '' : lastErrror; });

		if (options.dirtyTracing) {
			//set inital to true so that it wont interfere with invalid and it wont be traced
			self.isDirty = ko.observable(true);
			var _reseted = false;

			var result = ko.computed(function () {
				if (!self.isDirty()) {
					ko.toJS(self); //just for subscriptions
				}
				return self.isDirty();
			});

			result.subscribe(function () {
				//if previous not dirty, as long as i am here something changed
				if (!self.isDirty() && !_reseted) {
					self.isDirty(true);
				}

				_reseted = false;
			});

			self.resetDirty = function () {
				_reseted = true;
				self.isDirty(false);
			};

			self.hasValidChanges = ko.computed(function () {
				return self.isValid() && self.isDirty();
			});
		}

		function elementChanged(newValue) {
			if (options.children !== undefined) {
				processChildValidators();
			}

			validate(newValue);
			validateChildren();
		}

		function processChildValidators() {
			var index, length,
				underObject = ko.utils.unwrapObservable(self),
				newValidators = [],
				newSubscriptions = [],
				existingValidators = _childValidators,
				existingSubscriptions = _childSubscriptions,
				valIndex,
				subscription,
				item;

			function subscribeItemObservables(obj) {
				for (var prop in obj) {
					var objProp = obj[prop];
					if (objProp !== undefined && objProp !== null && obj.hasOwnProperty(prop) && ko.isObservable(objProp) && ko.validator.isValidating(objProp)) {
						if (existingValidators && (valIndex = existingValidators.replace(objProp, null)) !== -1) {
							subscription = existingSubscriptions[valIndex];
						}
						else {
							subscription = objProp.isValid.subscribe(validateChildren);
						}
						newValidators.push(objProp);
						newSubscriptions.push(subscription);
					}
				}
			}

			if (underObject) {
				//if it is an array, subscribe to all of its items observables
				if (ko.validator.isArray(underObject)) {
					for (index = 0, length = underObject.length; index < length; index++) {
						item = underObject[index];
						if (item) {
							subscribeItemObservables(item);
						}
					}
				}
				else {
					subscribeItemObservables(underObject);
				}

				//if there where any previous subscriptions, find all of them that are deleted and dispose subscriptions for them
				if (existingValidators) {
					JSLINQ(existingValidators).Where(null, undefined, true).ForEach(function (val, index) {
						existingSubscriptions[index].dispose();
					});
				}

				//store new validators, overwritting previous
				_childValidators = newValidators;
				_childSubscriptions = newSubscriptions;
			}
			else if (_childValidators) {
				//if object removed but i habe previous validations, unsubscribe from eveything
				for (index = 0, length = _childSubscriptions.length; index < length; index++) {
					_childSubscriptions[index].dispose();
				}
				_childValidators = null;
				_childSubscriptions = null;
			}
		}

		function validateChildren() {
			var bValid = true,
				index,
				length;

			//look for children
			if (_childValidators) {
				for (index = 0, length = _childValidators.length; (index < length && bValid); index++) {
					if (!_childValidators[index].isValid()) {
						bValid = false;
					}
				}
			}

			if (!bValid) {
				lastErrror = options.msg || "children error";
			}

			childrenValid(bValid);
		}

		function validate(newValue) {
			var bValid = true,
				checkVal;

			if (bValid && options.required !== undefined) {
				if (newValue === null || newValue === undefined || ('' + newValue) === '') {
					lastErrror = ko.validator.errorMessages.required;
					bValid = false;
				}
			}

			if (bValid && options.greaterThan !== undefined) {
				checkVal = +options.greaterThan;

				if (ko.validator.isArray(newValue)) {
					bValid = newValue.length > checkVal;
				} else {
					bValid = (+newValue) > checkVal;
					lastErrror = ko.validator.format(ko.validator.errorMessages.greaterThan, checkVal);
				}
			}

			if (bValid && options.lessThan !== undefined) {
				checkVal = +options.lessThan;

				if (ko.validator.isArray(newValue)) {
					bValid = newValue.length < checkVal;
				} else {
					bValid = (+newValue) < checkVal;
					lastErrror = ko.validator.format(ko.validator.errorMessages.lessThan, checkVal);
				}
			}

			if (bValid && options.customValidation !== undefined) {
				var res = options.customValidation(newValue);
				bValid = res.isValid;
				if (!res.isValid) {
					lastErrror = res.errorMsg;
				}
			}

			innerValid(bValid);
		}

		elementChanged(self());

		self.subscribe(elementChanged);

		return self;
	};

	//#endregion

	//#region utilities

	ko.validator = function () { };
	ko.validator.isArray = function (obj) {
		return Object.prototype.toString.call(obj) === '[object Array]';
	};
	ko.validator.isFunction = function (obj) {
		return Object.prototype.toString.call(obj) === '[object Function]';
	};
	ko.validator.isValidating = function (obj) {
		return ko.validator.isFunction(obj.isValid);
	};

	ko.validator.format = function (str) {

		var pattern = /\{\d+\}/g;
		var args = arguments;
		return str.replace(pattern, function (capture) {
			return args[+(capture.match(/\d+/)) + 1];
		});
	};

	ko.validator.dirtyFlag = function (root, isInitiallyDirty) {
		var result = function () { };
		var _initialState = ko.observable(ko.toJSON(root));
		var _isInitiallyDirty = ko.observable(isInitiallyDirty);

		result.isDirty = ko.dependentObservable(function () {
			return _isInitiallyDirty() || _initialState() !== ko.toJSON(root);
		});

		result.reset = function () {
			_initialState(ko.toJSON(root));
			_isInitiallyDirty(false);
		};

		return result;
	};

	//#endregion

	//#region options and default messages

	ko.validator.errorMessages = {
		required: 'Compulsory field',
		lessThan: 'Value must be less than {0}',
		greaterThan: 'Value must be greater than {0}'
	};

	//#endregion
})(ko);