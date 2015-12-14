"use strict";

var ko = require('knockout');
ko.validation = require('knockout.validation');
var API = require('../api');


function GeneralPage() {

    this.init = function(){
        API.showLoading();
        API.get( API.paths.getTrial, successFN.bind(this));

        function successFN(response) {
            console.log(response);
            API.hideLoading();
            this.trialDuration(response.trialDays);
        }
    }

    this.page = 'general';
    this.trialDuration = ko.observable();
    this.durationInEdit = ko.observable(false);

    this.changeTrialDuration = function() {
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
        var params = {'runtime':'javascript'}
        API.post('/api/testpost', successFN, params );

        function successFN(data) {
            console.log(data);
        }
    }

    this.trialDurationDisplay = ko.computed({
        read: function(){
            var days = this.trialDuration() || 0;
            return days+ ' days';
        }
    }, this);


    this.logout = function() {
        API.logout();
    }

}


module.exports = new GeneralPage();