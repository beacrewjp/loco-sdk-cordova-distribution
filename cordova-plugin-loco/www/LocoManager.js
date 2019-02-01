
function LocoManagerDelegate (){};

LocoManagerDelegate.prototype.stateChange = function(pluginResult) {
};

LocoManagerDelegate.prototype.beaconDetected = function(pluginResult) {
};

LocoManagerDelegate.prototype.enterRegion = function(pluginResult) {
};

LocoManagerDelegate.prototype.exitRegion = function(pluginResult) {
};

LocoManagerDelegate.prototype.actionDetected = function(pluginResult) {
};

LocoManagerDelegate.prototype.error = function(pluginResult) {
};

var locoManagerDelegate = LocoManagerDelegate();

var delegateSuccess = function(pluginResult) {   
    var eventType = pluginResult["eventType"];
    if (eventType == "onStateChange") {
        locoManagerDelegate.stateChange(pluginResult["message"]);
    } else if (eventType == "onBeaconDetected") {
        locoManagerDelegate.beaconDetected(pluginResult["message"]);
    } else if (eventType == "onRegionIn") {
        locoManagerDelegate.enterRegion(pluginResult["message"]);
    } else if (eventType == "onRegionOut") {
        locoManagerDelegate.exitRegion(pluginResult["message"]);
    } else if (eventType == "onActionDetected") {
        locoManagerDelegate.actionDetected(pluginResult["message"]);
    } else if (eventType == "onError") {
        locoManagerDelegate.error(pluginResult["message"]);
    }
}

var delegateFailure = function(pluginResult) {
    console.error("Loco Manager Error : delegate callback failure.");
}

function LocoManager (){};

LocoManager.prototype.initialize = function(apiKey, autoScan) {
    cordova.exec(delegateSuccess, delegateFailure, "beacrewloco", "initWithAPIKey", [apiKey, autoScan]);
};

LocoManager.prototype.scanStart = function() {
    cordova.exec(undefined, undefined, "beacrewloco", "scanStart", []);
};

LocoManager.prototype.scanStop = function() {
    cordova.exec(undefined, undefined, "beacrewloco", "scanStop", []);
};

LocoManager.prototype.setDelegate = function(newDelegate) {
    if (!(newDelegate instanceof LocoManagerDelegate)) {
		console.error('newDelegate parameter has to be an instance of locoManager.Delegate.');
		return;
	}
   locoManagerDelegate = newDelegate;
};

LocoManager.prototype.addEventLog = function(key, value) {
    cordova.exec(undefined, undefined, "beacrewloco", "addEventLog", [key,value]);
}

LocoManager.prototype.getDeviceId = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getDeviceId", []);
};

LocoManager.prototype.getNearestBeaconId = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getNearestBeaconId", []);
};

LocoManager.prototype.getClusters = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getClusters", []);
};

LocoManager.prototype.getBeacons = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getBeacons", []);
};

LocoManager.prototype.getRegions = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getRegions", []);
};

LocoManager.prototype.getActions = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getActions", []);
};

LocoManager.prototype.getState = function(success, failure) {
    cordova.exec(success, failure, "beacrewloco", "getState", []);
};

var locoManager = new LocoManager();
locoManager.Delegate = LocoManagerDelegate;

module.exports = locoManager;
