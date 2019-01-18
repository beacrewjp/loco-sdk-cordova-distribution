    
    var Delegate;

    var mSuccess = function(message) {
        var json = JSON.parse(message);

        if (json.eventType == "onStateChange") {
            Delegate.stateChange(JSON.stringify(json.message));
        } else if (json.eventType == "onBeaconDetected") {
            Delegate.beaconDetected(JSON.stringify(json.message));
        } else if (json.eventType == "onRegionIn") {
            Delegate.enterRegion(JSON.stringify(json.message));
        } else if (json.eventType == "onRegionOut") {
            Delegate.exitRegion(JSON.stringify(json.message));
        } else if (json.eventType == "onActionDetected") {
            Delegate.actionDetected(JSON.stringify(json.message));
        } else if (json.eventType == "onError") {
            Delegate.error(JSON.stringify(json.message));
        }

    }

    var mFailure = function() {
        alert("Plugin error...");
    }

    var loco = {};

    loco.initialize = function(apiKey, autoScan) {
        // 第1引数: 成功時に呼び出す関数
        // 第2引数: エラー時に呼び出す関数
        // 第3引数: プラグインの名前（plugin.xmlのfeatureのnameに設定したもの）
        // 第4引数: HelloWorld.javaの第1引数に渡る名前
        // 第5引数: HelloWorld.javaの第2引数に渡る値
        cordova.exec(mSuccess, mFailure, "beacrewloco", "initWithAPIKey", [apiKey, autoScan]);
    };

    loco.scanstart = function() {
        cordova.exec(mSuccess, mFailure, "beacrewloco", "scanStart", []);
    };

    loco.scanstop = function() {
        cordova.exec(mSuccess, mFailure, "beacrewloco", "scanStop", []);
    };

    loco.setDelegate = function(newDelegate) {
        if (!(newDelegate instanceof loco.delegate)) {
    		console.error('newDelegate parameter has to be an instance of loco.delegate.');
    		return;
    	}
        Delegate = newDelegate;
    };

    loco.addEventLog = function(key, value) {
        cordova.exec(mSuccess, mFailure, "beacrewloco", "addEventLog", [key,value]);
    }

    loco.getDeviceId = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getDeviceId", []);
    };

    loco.getNearestBeaconId = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getNearestBeaconId", []);
    };

    loco.getClusters = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getClusters", []);
    };

    loco.getBeacons = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getBeacons", []);
    };

    loco.getRegions = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getRegions", []);
    };

    loco.getActions = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getActions", []);
    };

    
    loco.getState = function(success, failure) {
        cordova.exec(success, failure, "beacrewloco", "getState", []);
    };
    
    loco.delegate = function() {}

    loco.delegate.prototype.stateChange = function(payload) {
    }

    loco.delegate.prototype.beaconDetected = function(payload) {
    }

    loco.delegate.prototype.enterRegion = function(payload) {
    }

    loco.delegate.prototype.exitRegion = function(payload) {
    }

    loco.delegate.prototype.actionDetected = function(payload) {
    }

    loco.delegate.prototype.error = function(payload) {
    }

    loco.delegate.prototype.test = function(payload) {
    }

    module.exports = loco;