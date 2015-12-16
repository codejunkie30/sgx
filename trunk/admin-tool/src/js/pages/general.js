"use strict";

var ko = require('knockout');
ko.validation = require('knockout.validation');
var API = require('../api');


function GeneralPage() {
    var self = this;
    this.init = function(){
        API.verifyUser(
            this.getTrialDuration.bind(this),
            function() {
                API.goToPage('/', 1500);
            }
        );
    }

    this.page = 'general';
    this.trialDuration = ko.observable().extend({number:true});
    this.durationInEdit = ko.observable(false);

    this.getTrialDuration = function() {
        API.showLoading();
        API.get( API.paths.getTrial, successFN.bind(this));
        function successFN(response) {
            API.hideLoading();
            this.trialDuration(response.trialDays);
        }
    }

    this.changeTrialDuration = function() {
        if(this.trialDurationError().length > 0) return;
        var newDuration = this.trialDuration();
        var halfDuration = parseInt(newDuration, 10)/2 + parseInt(newDuration%2, 10);
        var params = {trialDays: newDuration, halfwayDays: halfDuration};
        API.showLoading();
        API.post( API.paths.setTrial, successFN.bind(this), params );

        function successFN(response) {
            console.log(response);
            API.hideLoading();
            this.durationInEdit(false);
        }

    };

    this.initiateDurationEdit = function() {
        this.savedDuration = this.trialDuration();
        this.durationInEdit(true);
    };

    this.cancelDurationEdit = function() {
        this.trialDuration(this.savedDuration);
        this.durationInEdit(false);
    };


    this.exportReport = function() {
        window.open('/sgx/admin/excel');
    }

    this.trialDurationDisplay = ko.computed({
        read: function(){
            var days = this.trialDuration() || 0;
            return days+ ' days';
        }
    }, this);


    this.logout = function() {
        API.logout();
    },

    this.goToPage = API.goToPage,

    this.trialDurationError = ko.validation.group(this.trialDuration);

}


module.exports = new GeneralPage();