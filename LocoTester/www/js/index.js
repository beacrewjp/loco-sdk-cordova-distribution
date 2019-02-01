var scrollY = 0;
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {

        //Delegateの設定
        var delegate = new locoManager.Delegate();

        delegate.stateChange = function(payload) {
            var jsonString = JSON.stringify(payload, undefined, 4);
            console.log(jsonString);
            var text = document.getElementById('results');
            text.value += "stateChange:" + jsonString + "\n";
            autoScroll();
        };

        delegate.beaconDetected = function(payload){
            var jsonString = JSON.stringify(payload, undefined, 4);
            console.log(jsonString);
            var text = document.getElementById('results');
            text.value += "beaconDetected:" + jsonString + "\n";
            autoScroll();
        };

        delegate.enterRegion = function(payload) {
            var jsonString = JSON.stringify(payload, undefined, 4);
            console.log(jsonString);
            var text = document.getElementById('results');
            text.value += "enterRegion:" + jsonString + "\n";
            autoScroll();
        };

        delegate.exitRegion = function(payload) {
            var jsonString = JSON.stringify(payload, undefined, 4);
            console.log(jsonString);
            var text = document.getElementById('results');
            text.value += "exitRegion:" + jsonString + "\n";
            autoScroll();
        };

        delegate.actionDetected = function(payload) {
            var jsonString = JSON.stringify(payload, undefined, 4);
            console.log(jsonString);
            var text = document.getElementById('results');
            text.value += "actionDetected:" + jsonString + "\n";
            autoScroll();
        }

        delegate.error = function(payload) {
            var jsonString = JSON.stringify(payload, undefined, 4);
            console.log(jsonString);
            var text = document.getElementById('results');
            text.value += "error:" + jsonString + "\n";
            autoScroll();
        }

        //Delegateをセット
        locoManager.setDelegate(delegate);

        //ボタンのイベントリスナー設定
        var scanStartButton = document.getElementById("scanstart");
        scanStartButton.addEventListener("click",scanStart);

        var scanStopButton = document.getElementById("scanstop");
        scanStopButton.addEventListener("click", scanStop);

        var getDeviceIdButton = document.getElementById("getDeviceId");
        getDeviceIdButton.addEventListener("click", getDeviceId);

        var getClustersButton = document.getElementById("getClusters");
        getClustersButton.addEventListener("click", getClusters);

        var getNearestBeaconIdButton = document.getElementById("getNearestBeaconId");
        getNearestBeaconIdButton.addEventListener("click", getNearestBeaconId);

        var getStateButton = document.getElementById("getState");
        getStateButton.addEventListener("click", getState);

        var getBeaconsButton = document.getElementById("getBeacons");
        getBeaconsButton.addEventListener("click", getBeacons);

        var getRegionsButton = document.getElementById("getRegions");
        getRegionsButton.addEventListener("click", getRegions);

        var getActionsButton = document.getElementById("getActions");
        getActionsButton.addEventListener("click", getActions);

        //LocoのInitialize
        initialize();
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();

function initialize() {
    locoManager.initialize("YOUR_SDK_SECRET", false);
}

function scanStart() {
    locoManager.scanStart();
}

function scanStop() {
    locoManager.scanStop();
}

function getDeviceId() {
    var success = function(payload) {
        console.log(payload);
        var text = document.getElementById('results');
        text.value += "getDeviceId:" + payload + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getDeviceId(success, failure);
}

function getNearestBeaconId() {
    var success = function(payload) {
        console.log(payload);
        var text = document.getElementById('results');
        text.value += "getNearestBeaconId:" + payload + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getNearestBeaconId(success, failure);
}

function getState() {
    var success = function(payload) {
        console.log(payload);
        var text = document.getElementById('results');
        text.value += "getState:" + payload + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getState(success, failure);
}

function getClusters() {
    var success = function(payload) {
        var jsonString = JSON.stringify(payload, undefined, 4);
        console.log(jsonString);
        var text = document.getElementById('results');
        text.value += "getClusters:" + jsonString + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getClusters(success, failure);

}

function getBeacons() {
    var success = function(payload) {
        var jsonString = JSON.stringify(payload, undefined, 4);
        console.log(jsonString);
        var text = document.getElementById('results');
        text.value += "getBeacons:" + jsonString + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getBeacons(success, failure);
}

function getRegions() {
    var success = function(payload) {
        var jsonString = JSON.stringify(payload, undefined, 4);
        console.log(jsonString);
        var text = document.getElementById('results');
        text.value += "getRegions:" + jsonString + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getRegions(success, failure);
}

function getActions() {
    var success = function(payload) {
        var jsonString = JSON.stringify(payload, undefined, 4);
        console.log(jsonString);
        var text = document.getElementById('results');
        text.value += "getActions:" + jsonString + "\n";
        autoScroll();
    }

    var failure = function(payload) {
        alert("Plugin error...");
    }
    locoManager.getActions(success, failure);
}

function autoScroll() {
    var result = document.getElementById( "results" );
    scrollY = scrollY + 20;
    result.scrollTop = scrollY;
    if( scrollY < result.scrollHeight - result.clientHeight ){
        setTimeout( "autoScroll()", 20 );
    }
}
