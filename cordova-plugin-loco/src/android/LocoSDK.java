package plugin.locosdk;

import android.Manifest;
import android.os.Build;
import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.beacrew.loco.BCLAction;
import jp.beacrew.loco.BCLBeacon;
import jp.beacrew.loco.BCLCluster;
import jp.beacrew.loco.BCLError;
import jp.beacrew.loco.BCLManager;
import jp.beacrew.loco.BCLManagerEventListener;
import jp.beacrew.loco.BCLParam;
import jp.beacrew.loco.BCLRegion;
import jp.beacrew.loco.BCLState;

public class LocoSDK extends CordovaPlugin implements BCLManagerEventListener{

    private BCLManager mManager;
    private CallbackContext mCallbackContext;
    private static final int REQUEST_CODE_ENABLE_PERMISSION = 88008800;
    private String mApiKey;
    private boolean mAutoScan;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals("initWithAPIKey")) {
            mCallbackContext = callbackContext;
            mApiKey = data.getString(0);
            mAutoScan = data.getBoolean(1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissions = {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                };
                if (hasAllPermissions(permissions)) {
                    initWithApiKey();
                } else {
                    cordova.requestPermissions(this, REQUEST_CODE_ENABLE_PERMISSION, permissions);
                }
            } else {
                initWithApiKey();
            }
        } else if (action.equals("scanStart")) {
            scanStart();
        } else if (action.equals("scanStop")) {
            scanStop();
        } else if (action.equals("getDeviceId")) {
            getDeviceId(callbackContext);
        } else if(action.equals("getNearestBeaconId")) {
            getNearestBeaconId(callbackContext);
        } else if (action.equals("getClusters")) {
            getClusters(callbackContext);
        } else if (action.equals("getBeacons")) {
            getBeacons(callbackContext);
        } else if (action.equals("getRegions")) {
            getRegions(callbackContext);
        } else if (action.equals("getActions")) {
            getActions(callbackContext);
        } else if (action.equals("addEventLog")) {
            addEventLog(data.getString(0), data.getString(1));
        } else if (action.equals("getState")) {
            getState(callbackContext);
        } else {
            return false;
        }

        return true;
    }

    private void scanStart() {
        if (mManager != null) {
            mManager.scanStart();
        }
    }

    private void scanStop() {
        if (mManager != null) {
            mManager.scanStop();
        }
    }
    
    private void getDeviceId(CallbackContext callbackContext) {
        String ret = mManager.getDeviceId();
        PluginResult result = new PluginResult(PluginResult.Status.OK, ret);
       
        callbackContext.sendPluginResult(result);
    }

    private void getNearestBeaconId(CallbackContext callbackContext) {
        String ret = mManager.getNearestBeaconId();
        PluginResult result = new PluginResult(PluginResult.Status.OK, ret);

        callbackContext.sendPluginResult(result);
    }

    private void getState(CallbackContext callbackContext) {
        String ret = mManager.getState().toString();
        PluginResult result = new PluginResult(PluginResult.Status.OK, ret);

        callbackContext.sendPluginResult(result);    
    }

    private void addEventLog(String key, String value) {
        mManager.addEventLog(key, value);
    }

    private void getClusters(CallbackContext callbackContext) {
        ArrayList<BCLCluster> clusters = mManager.getClusters();
        JSONArray array = new JSONArray();
        try {
            for (BCLCluster cluster : clusters) {
                array.put(clusterToJSONObject(cluster));
            }
            JSONObject json = new JSONObject();
            json.put("clusters", array);
            PluginResult result = new PluginResult(PluginResult.Status.OK, json);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBeacons (CallbackContext callbackContext) {
        ArrayList<BCLBeacon> beacons = mManager.getBeacons();
        JSONArray array = new JSONArray();
        try {
            for (BCLBeacon beacon : beacons) {
                array.put(beaconToJSONObject(beacon));
            }
            JSONObject json = new JSONObject();
            json.put("beacons", array);
            PluginResult result = new PluginResult(PluginResult.Status.OK, json);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRegions(CallbackContext callbackContext) {
        ArrayList<BCLRegion> regions = mManager.getRegions();
        JSONArray array = new JSONArray();
        try {
            for (BCLRegion region : regions) {
                array.put(regionToJSONObject(region));
            }
            JSONObject json = new JSONObject();
            json.put("regions", array);
            PluginResult result = new PluginResult(PluginResult.Status.OK, json);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getActions(CallbackContext callbackContext) {
        ArrayList<BCLAction> actions = mManager.getActions();
        JSONArray array = new JSONArray();
        try {
            for (BCLAction action : actions) {
                array.put(actionToJSONObject(action));
            }
            JSONObject json = new JSONObject();
            json.put("actions", array);
            PluginResult result = new PluginResult(PluginResult.Status.OK, json);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateChange(BCLState state) {
        Log.d("LocoCordova", "onStateChange:" + state.toString());
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", "onStateChange");
            json.put("message", stateToJSONObject(state));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onBeaconDetected(ArrayList<BCLBeacon> beacons) {
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", "onBeaconDetected");

            JSONArray array = new JSONArray();
            for (BCLBeacon beacon : beacons) {
                array.put(beaconToJSONObject(beacon));
            }
            JSONObject message = new JSONObject();
            message.put("beacons", array);
            
            json.put("message", message);
                    
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onActionDetected(BCLAction action, String type, Object source) {
        Log.d("LocoCordova", "onActionDetected:" + action.getName());
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", "onActionDetected");
            json.put("message", actionToJSONObject(action, type, source));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onRegionIn(BCLRegion region) {
        Log.d("LocoCordova", "onRegionIn:" + region.getName());
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", "onRegionIn");
            json.put("message", regionToJSONObject(region));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onRegionOut(BCLRegion region) {
        Log.d("LocoCordova", "onRegionOut:" + region.getName());
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", "onRegionOut");
            json.put("message", regionToJSONObject(region));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onError(BCLError error) {
        Log.d("LocoCordova", "onError:" + error.getMessage());
        JSONObject json = new JSONObject();
        try {
            json.put("eventType", "onError");
            json.put("message", errorToJSONObject(error));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    private boolean hasAllPermissions(String[] permissions) throws JSONException {
        for (int i = 0; i < permissions.length; i++){
            String permission = permissions[i];
            if(!cordova.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (permissions != null && permissions.length > 0) {
            //Call hasPermission again to verify
            boolean hasAllPermissions = hasAllPermissions(permissions);
            if (hasAllPermissions){
                initWithApiKey();
            }
        }
    }

    private void initWithApiKey() {
        mManager = BCLManager.getInstance(cordova.getContext().getApplicationContext());
        mManager.setListener(this);
        mManager.initWithApiKey(mApiKey, mAutoScan);
    }

    @Override
    public void onDestroy(){
        Log.d("LocoCordova", "onDestroy:");
        if (mManager != null) {
            mManager.terminateService();
        }
    }

    private JSONObject beaconToJSONObject(BCLBeacon beacon) throws JSONException {
        
        JSONObject json = new JSONObject();
        json.put("beaconId", beacon.getBeaconId());
        json.put("name", beacon.getName());
        json.put("uuid", beacon.getUuid());
        json.put("major", beacon.getMajor());
        json.put("minor", beacon.getMinor());
        json.put("txPower", beacon.getTxPower());
        json.put("rssi", beacon.getRssi());
        json.put("localName", beacon.getLocalName());
        json.put("moduleId", beacon.getModuleId());
        json.put("model", beacon.getBeaconModel());
        json.put("manufacturer", beacon.getBeaconManufacturer());
        json.put("x", beacon.getBeaconX());
        json.put("y", beacon.getBeaconY());
        json.put("height", beacon.getBeaconH());

        JSONArray actions = new JSONArray();
        for (BCLAction action : beacon.getActions()) {
            actions.put(actionToJSONObject(action));
        }
        json.put("actions", actions);
        
        return json;
    }

    private JSONObject actionToJSONObject(BCLAction action) throws JSONException {
        return actionToJSONObject(action, null, null);
    }

    private JSONObject actionToJSONObject(BCLAction action, String type, Object source) throws JSONException {
        
        JSONObject json = new JSONObject();
        json.put("actionId", action.getActionId());
        json.put("name", action.getName());
        json.put("uri", action.getUri());
        json.put("interval", action.getInterval());

        JSONArray params = new JSONArray();
        for (BCLParam param : action.getParams()) {
            params.put(paramToJSONObject(param));
        }
        json.put("params", params);

        if (type != null) {
            json.put("type", type);
            if (source instanceof BCLRegion) {
                json.put("source", regionToJSONObject((BCLRegion)source));
            } else if (source instanceof BCLBeacon) {
                json.put("source", beaconToJSONObject((BCLBeacon)source));
            }
        }
        
        return json;
    }

    private JSONObject regionToJSONObject(BCLRegion region) throws JSONException {
        
        JSONObject json = new JSONObject();
        json.put("regionId", region.getRegionId());
        json.put("name", region.getName());
        json.put("type", region.getType());
        json.put("uuid", region.getUuid());
        json.put("major", region.getMajor());
        json.put("minor", region.getMinor());
        json.put("latitude", region.getLatitude());
        json.put("longitude", region.getLongitude());
        json.put("radius", region.getRadius());

        JSONArray inAction = new JSONArray();
        for (BCLAction action : region.getInAction()) {
            inAction.put(actionToJSONObject(action));
        }
        json.put("inAction", inAction);

        JSONArray outAction = new JSONArray();
        for (BCLAction action : region.getOutAction()) {
            outAction.put(actionToJSONObject(action));
        }
        json.put("outAction", outAction);

        return json;
    }

    private JSONObject clusterToJSONObject(BCLCluster cluster) throws JSONException {
        
        JSONObject json = new JSONObject();        
        json.put("clusterId", cluster.getClusterId());
        json.put("parentId", cluster.getParentId());
        json.put("name", cluster.getName());
        json.put("tag", cluster.getType());
        json.put("image", cluster.getImage());
        
        JSONArray beacons = new JSONArray();
        for (BCLBeacon beacon : cluster.getBeacons()) {
            beacons.put(beaconToJSONObject(beacon));
        }
        json.put("beacons", beacons);

        return json;
    }

    private JSONObject paramToJSONObject(BCLParam param) throws JSONException {
        
        JSONObject json = new JSONObject();
        json.put("key", param.getKey());
        json.put("value", param.getValue());
        
        return json;
    }

    private JSONObject errorToJSONObject(BCLError error) throws JSONException {
        
        JSONObject json = new JSONObject();
        json.put("code", error.getCode());
        json.put("message", error.getMessage());
        if (error.getDetail() != null) {
            json.put("detail", error.getDetail().getMessage());
        } else {
            json.put("detail", null);
        }
        
        return json;
    }

    private JSONObject stateToJSONObject(BCLState state) throws JSONException {
        
        JSONObject json = new JSONObject();
        json.put("status", state.toString());
        
        return json;
    }
    
}
