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
import jp.beacrew.loco.BCLRegion;
import jp.beacrew.loco.BCLState;

public class LocoSDK extends CordovaPlugin implements BCLManagerEventListener{

    private BCLManager mBCLManager;
    private CallbackContext mCallbackContext;
    private static final int REQUEST_CODE_ENABLE_PERMISSION = 88008800;
    private String ApiKey;
    private boolean autoScan;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        
        if (action.equals("initWithAPIKey")) {
            mCallbackContext = callbackContext;
            ApiKey = data.getString(0);
            autoScan = data.getBoolean(1);

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
        if (mBCLManager != null) {
            mBCLManager.scanStart();
        }
    }

    private void scanStop() {
        if (mBCLManager != null) {
            mBCLManager.scanStop();
        }
    }
    private void getDeviceId(CallbackContext callbackContext) {
        String ret = mBCLManager.getDeviceId();
        PluginResult result = new PluginResult(PluginResult.Status.OK, ret);
       
        callbackContext.sendPluginResult(result);
    }

    private void getNearestBeaconId(CallbackContext callbackContext) {
        String ret = mBCLManager.getNearestBeaconId();
        PluginResult result = new PluginResult(PluginResult.Status.OK, ret);

        callbackContext.sendPluginResult(result);
    }

    private void getState(CallbackContext callbackContext) {
        String ret = mBCLManager.getState().toString();
        PluginResult result = new PluginResult(PluginResult.Status.OK, ret);

        callbackContext.sendPluginResult(result);    
    }

    private void addEventLog(String key, String value) {
        mBCLManager.addEventLog(key, value);
    }

    private void getClusters(CallbackContext callbackContext) {
        ArrayList<BCLCluster> clusters;
        clusters = mBCLManager.getClusters();
        ArrayList<HashMap> clusterList = new ArrayList<>();

        for (int idx = 0; idx < clusters.size(); idx++) {
            ArrayList<HashMap> beaconsList = new ArrayList<>();
            HashMap clusterHash = new HashMap<>();
            BCLCluster cluster = clusters.get(idx);
            clusterHash.put("clusterId", cluster.getClusterId());
            clusterHash.put("parentId", cluster.getParentId());
            clusterHash.put("name", cluster.getName());
            clusterHash.put("tag", cluster.getType());


            //Beaconの要素
            for (BCLBeacon bclBeacon : cluster.getBeacons()) {
                ArrayList<HashMap> actionsList = new ArrayList<>();
                HashMap beaconsHash = new HashMap();

                beaconsHash.put("beaconId", bclBeacon.getBeaconId());
                beaconsHash.put("uuid", bclBeacon.getUuid());
                beaconsHash.put("major", bclBeacon.getMajor());
                beaconsHash.put("minor", bclBeacon.getMinor());

                //Actionの要素
                for (BCLAction bclAction : bclBeacon.getActions()) {
                    ArrayList<HashMap> paramList = new ArrayList<>();
                    HashMap actionHash = new HashMap();

                    actionHash.put("actionId", bclAction.getActionId());
                    actionHash.put("name", bclAction.getName());
                    actionHash.put("uri", bclAction.getUri());
                    actionHash.put("interval", bclAction.getInterval());

                    for (int idx2 = 0; idx2 < bclAction.getParams().size(); idx2++) {
                        HashMap<String, String> paramHash = new HashMap<>();

                        paramHash.put("key", bclAction.getParams().get(idx2).getKey());
                        paramHash.put("value", bclAction.getParams().get(idx2).getValue());

                        paramList.add(paramHash);
                    }

                    actionHash.put("params", paramList);
                    actionsList.add(actionHash);

                }

                beaconsHash.put("actions", actionsList);
                beaconsHash.put("name", bclBeacon.getName());
                beaconsHash.put("localName", bclBeacon.getLocalName());
                beaconsHash.put("model", bclBeacon.getBeaconModel());
                beaconsHash.put("manufacturer", bclBeacon.getBeaconManufacturer());

                beaconsHash.put("moduleId", bclBeacon.getModuleId());
                beaconsHash.put("x", bclBeacon.getBeaconX());
                beaconsHash.put("y", bclBeacon.getBeaconY());
                beaconsHash.put("height", bclBeacon.getBeaconH());
                beaconsHash.put("rssi", bclBeacon.getRssi());
                beaconsHash.put("txPower", bclBeacon.getTxPower());

                beaconsList.add(beaconsHash);
            }

            clusterHash.put("beacons", beaconsList);
            clusterHash.put("image", cluster.getImage());

            clusterList.add(clusterHash);
        }
        //Json
        HashMap clusterMap = new HashMap();
        clusterMap.put("clusters", clusterList);

        try {
            PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(clusterMap).toString(4));
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBeacons (CallbackContext callbackContext) {
        ArrayList<BCLBeacon> beacons;
        beacons = mBCLManager.getBeacons();

        ArrayList<HashMap> beaconList = new ArrayList<>();

        for (int idx = 0; idx < beacons.size(); idx++) {
            ArrayList<HashMap> actionsList = new ArrayList<>();
            HashMap beaconHash = new HashMap<>();
            BCLBeacon beacon = beacons.get(idx);
            beaconHash.put("beaconId", beacon.getBeaconId());
            beaconHash.put("uuid", beacon.getUuid());
            beaconHash.put("major", beacon.getMajor());
            beaconHash.put("minor", beacon.getMinor());


            //Actionの要素
            for (BCLAction bclAction : beacon.getActions()) {
                ArrayList<HashMap> paramList = new ArrayList<>();
                HashMap actionHash = new HashMap();

                actionHash.put("actionId", bclAction.getActionId());
                actionHash.put("name", bclAction.getName());
                actionHash.put("uri", bclAction.getUri());
                actionHash.put("interval", bclAction.getInterval());

                for (int idx2 = 0; idx2 < bclAction.getParams().size(); idx2++) {
                    HashMap<String, String> paramHash = new HashMap<>();

                    paramHash.put("key", bclAction.getParams().get(idx2).getKey());
                    paramHash.put("value", bclAction.getParams().get(idx2).getValue());

                    paramList.add(paramHash);
                }

                actionHash.put("params", paramList);
                actionsList.add(actionHash);

            }

            beaconHash.put("actions", actionsList);
            beaconHash.put("name", beacon.getName());
            beaconHash.put("localName", beacon.getLocalName());
            beaconHash.put("model", beacon.getBeaconModel());
            beaconHash.put("manufacturer", beacon.getBeaconManufacturer());
            beaconHash.put("moduleId", beacon.getModuleId());
            beaconHash.put("x", beacon.getBeaconX());
            beaconHash.put("y", beacon.getBeaconY());
            beaconHash.put("height", beacon.getBeaconH());

            beaconList.add(beaconHash);
        }
        //Json
        HashMap beaconMap = new HashMap();
        beaconMap.put("beacons", beaconList);

        try {
            PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(beaconMap).toString(4));
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRegions(CallbackContext callbackContext) {
        ArrayList<BCLRegion> regions;
        regions = mBCLManager.getRegions();
        ArrayList<HashMap> regionList = new ArrayList<>();

        for (int idx = 0; idx < regions.size(); idx++) {
            ArrayList<HashMap> inActionsList = new ArrayList<>();
            ArrayList<HashMap> outActionsList = new ArrayList<>();
            HashMap regionHash = new HashMap<>();
            BCLRegion region = regions.get(idx);
            regionHash.put("regionId", region.getRegionId());
            regionHash.put("type", region.getType());
            regionHash.put("name", region.getName());
            regionHash.put("uuid", region.getUuid());
            regionHash.put("major", region.getMajor());
            regionHash.put("minor", region.getMinor());
            regionHash.put("latitude", region.getLatitude());
            regionHash.put("longitude", region.getLongitude());
            regionHash.put("radius", region.getRadius());

            for (BCLAction inAction : region.getInAction()) {
                ArrayList<HashMap> paramList = new ArrayList<>();
                HashMap actionHash = new HashMap();

                actionHash.put("actionId", inAction.getActionId());
                actionHash.put("name", inAction.getName());
                actionHash.put("uri", inAction.getUri());
                actionHash.put("interval", inAction.getInterval());

                for (int idx2 = 0; idx2 < inAction.getParams().size(); idx2++) {
                    HashMap<String, String> paramHash = new HashMap<>();
                    paramHash.put("key", inAction.getParams().get(idx2).getKey());
                    paramHash.put("value", inAction.getParams().get(idx2).getValue());
                    paramList.add(paramHash);
                }
                actionHash.put("params", paramList);
                inActionsList.add(actionHash);
            }
            regionHash.put("inAction", inActionsList);

            for (BCLAction outAction : region.getOutAction()) {
                ArrayList<HashMap> paramList = new ArrayList<>();
                HashMap actionHash = new HashMap();

                actionHash.put("actionId", outAction.getActionId());
                actionHash.put("name", outAction.getName());
                actionHash.put("uri", outAction.getUri());
                actionHash.put("interval", outAction.getInterval());

                for (int idx2 = 0; idx2 < outAction.getParams().size(); idx2++) {
                    HashMap<String, String> paramHash = new HashMap<>();
                    paramHash.put("key", outAction.getParams().get(idx2).getKey());
                    paramHash.put("value", outAction.getParams().get(idx2).getValue());
                    paramList.add(paramHash);
                }
                actionHash.put("params", paramList);
                outActionsList.add(actionHash);
            }
            regionHash.put("outAction", outActionsList);
            regionList.add(regionHash);

        }
        //Json
        HashMap regionMap = new HashMap();
        regionMap.put("regions", regionList);
        try {
            PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(regionMap).toString(4));
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getActions(CallbackContext callbackContext) {
        ArrayList<BCLAction> actions;
        actions = mBCLManager.getActions();

        ArrayList<HashMap> actionList = new ArrayList<>();

        for (int idx = 0; idx < actions.size(); idx++) {
            HashMap actionHash = new HashMap<>();
            ArrayList<HashMap> paramList = new ArrayList<>();


            BCLAction action = actions.get(idx);
            actionHash.put("actionId", action.getActionId());
            actionHash.put("name", action.getName());
            actionHash.put("uri", action.getUri());
            actionHash.put("interval", action.getInterval());

            for (int idx2 = 0; idx2 < action.getParams().size(); idx2++) {
                HashMap<String, String> paramHash = new HashMap<>();

                paramHash.put("key", action.getParams().get(idx2).getKey());
                paramHash.put("value", action.getParams().get(idx2).getValue());

                paramList.add(paramHash);
            }

            actionHash.put("params", paramList);
            actionList.add(actionHash);
        }
        //Json
        HashMap actionMap = new HashMap();
        actionMap.put("actions", actionList);

        try {
            PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(actionMap).toString(4));
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateChange(BCLState bclState) {
        Log.d("LocoCordova", "onStateChange:" + bclState.toString());
        //mCallbackContext.success(bclState.toString());
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("eventType", "onStateChange");
            resultJson.put("message", createSendStateJson(bclState));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, resultJson.toString());
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);

    }

    @Override
    public void onBeaconDetected(ArrayList<BCLBeacon> arrayList) {
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("eventType", "onBeaconDetected");
            resultJson.put("message", createSendBeaconsJson(arrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, resultJson.toString());
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);


    }

    @Override
    public void onActionDetected(BCLAction bclAction, String s, Object o) {
        Log.d("LocoCordova", "onActionDetected:" + bclAction.getName());
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("eventType", "onActionDetected");
            resultJson.put("message", createSendActionsJson(bclAction, s, o));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, resultJson.toString());
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onRegionIn(BCLRegion bclRegion) {
        Log.d("LocoCordova", "onRegionIn:" + bclRegion.getName());
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("eventType", "onRegionIn");
            resultJson.put("message", createSendRegionJson(bclRegion));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, resultJson.toString());
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onRegionOut(BCLRegion bclRegion) {
        Log.d("LocoCordova", "onRegionOut:" + bclRegion.getName());
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("eventType", "onRegionOut");
            resultJson.put("message", createSendRegionJson(bclRegion));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, resultJson.toString());
        result.setKeepCallback(true);
        mCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onError(BCLError bclError) {
        Log.d("LocoCordova", "onError:" + bclError.getMessage());
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("eventType", "onError");
            resultJson.put("message", createSendErrorJson(bclError));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, resultJson.toString());
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
        mBCLManager = BCLManager.getInstance(cordova.getContext().getApplicationContext());
        mBCLManager.setListener(this);
        mBCLManager.initWithApiKey(ApiKey, autoScan);
    }

    @Override
    public void onDestroy(){
        Log.d("LocoCordova", "onDestroy:");
        if (mBCLManager != null) {
            mBCLManager.terminateService();
        }
    }

    private JSONObject createSendBeaconsJson(ArrayList<BCLBeacon> beacons) {

        ArrayList<HashMap> beaconList = new ArrayList<>();

        for (int idx = 0; idx < beacons.size(); idx++) {
            ArrayList<HashMap> actionsList = new ArrayList<>();
            HashMap beaconHash = new HashMap<>();
            BCLBeacon beacon = beacons.get(idx);
            beaconHash.put("beaconId", beacon.getBeaconId());
            beaconHash.put("uuid", beacon.getUuid());
            beaconHash.put("major", beacon.getMajor());
            beaconHash.put("minor", beacon.getMinor());


            //Actionの要素
            for (BCLAction bclAction : beacon.getActions()) {
                ArrayList<HashMap> paramList = new ArrayList<>();
                HashMap actionHash = new HashMap();

                actionHash.put("actionId", bclAction.getActionId());
                actionHash.put("name", bclAction.getName());
                actionHash.put("uri", bclAction.getUri());
                actionHash.put("interval", bclAction.getInterval());

                for (int idx2 = 0; idx2 < bclAction.getParams().size(); idx2++) {
                    HashMap<String, String> paramHash = new HashMap<>();

                    paramHash.put("key", bclAction.getParams().get(idx2).getKey());
                    paramHash.put("value", bclAction.getParams().get(idx2).getValue());

                    paramList.add(paramHash);
                }

                actionHash.put("params", paramList);
                actionsList.add(actionHash);

            }

            beaconHash.put("actions", actionsList);
            beaconHash.put("name", beacon.getName());
            beaconHash.put("localName", beacon.getLocalName());
            beaconHash.put("model", beacon.getBeaconModel());
            beaconHash.put("manufacturer", beacon.getBeaconManufacturer());
            beaconHash.put("moduleId", beacon.getModuleId());
            beaconHash.put("x", beacon.getBeaconX());
            beaconHash.put("y", beacon.getBeaconY());
            beaconHash.put("height", beacon.getBeaconH());
            beaconHash.put("rssi", beacon.getRssi());
            beaconHash.put("txPower", beacon.getTxPower());

            beaconList.add(beaconHash);
        }
        //Json
        HashMap beaconMap = new HashMap();
        beaconMap.put("beacons", beaconList);
        return new JSONObject(beaconMap);
    }

    private JSONObject createSendActionsJson(BCLAction bclAction, String type, Object source) {
        ArrayList<HashMap> paramList = new ArrayList<>();
        HashMap actionHash = new HashMap();

        actionHash.put("actionId", bclAction.getActionId());
        actionHash.put("name", bclAction.getName());
        actionHash.put("uri", bclAction.getUri());
        actionHash.put("interval", bclAction.getInterval());

        for (int idx2 = 0; idx2 < bclAction.getParams().size(); idx2++) {
            HashMap<String, String> paramHash = new HashMap<>();
            paramHash.put("key", bclAction.getParams().get(idx2).getKey());
            paramHash.put("value", bclAction.getParams().get(idx2).getValue());
            paramList.add(paramHash);
        }

        actionHash.put("params", paramList);
        actionHash.put("type", type);
        if (source instanceof BCLRegion) {
            actionHash.put("source", createSendRegionJson((BCLRegion)source));
        } else if(source instanceof BCLBeacon) {
            ArrayList<BCLBeacon> beaconArrayList = new ArrayList<>();
            beaconArrayList.add((BCLBeacon)source);
            actionHash.put("source", createSendBeaconsJson(beaconArrayList));
        }
        return new JSONObject(actionHash);


    }

    private JSONObject createSendRegionJson(BCLRegion bclRegion) {
        ArrayList<HashMap> inActionsList = new ArrayList<>();
        ArrayList<HashMap> outActionsList = new ArrayList<>();
        HashMap regionHash = new HashMap();

        regionHash.put("regionId", bclRegion.getRegionId());
        regionHash.put("type", bclRegion.getType());
        regionHash.put("name", bclRegion.getName());
        regionHash.put("uuid", bclRegion.getUuid());
        regionHash.put("major", bclRegion.getMajor());
        regionHash.put("minor", bclRegion.getMinor());
        regionHash.put("latitude", bclRegion.getLatitude());
        regionHash.put("longitude", bclRegion.getLongitude());
        regionHash.put("radius", bclRegion.getRadius());

        //inActionの要素
        for (BCLAction bclAction : bclRegion.getInAction()) {
            ArrayList<HashMap> paramList = new ArrayList<>();
            HashMap actionHash = new HashMap();

            actionHash.put("actionId", bclAction.getActionId());
            actionHash.put("name", bclAction.getName());
            actionHash.put("uri", bclAction.getUri());
            actionHash.put("interval", bclAction.getInterval());

            for (int idx2 = 0; idx2 < bclAction.getParams().size(); idx2++) {
                HashMap<String, String> paramHash = new HashMap<>();
                paramHash.put("key", bclAction.getParams().get(idx2).getKey());
                paramHash.put("value", bclAction.getParams().get(idx2).getValue());
                paramList.add(paramHash);
            }

            actionHash.put("params", paramList);
            inActionsList.add(actionHash);

        }
        regionHash.put("inAction", inActionsList);

        //outActionの要素
        for (BCLAction bclAction : bclRegion.getOutAction()) {
            ArrayList<HashMap> paramList = new ArrayList<>();
            HashMap actionHash = new HashMap();
            actionHash.put("actionId", bclAction.getActionId());
            actionHash.put("name", bclAction.getName());
            actionHash.put("uri", bclAction.getUri());
            actionHash.put("interval", bclAction.getInterval());

            for (int idx2 = 0; idx2 < bclAction.getParams().size(); idx2++) {
                HashMap<String, String> paramHash = new HashMap<>();
                paramHash.put("key", bclAction.getParams().get(idx2).getKey());
                paramHash.put("value", bclAction.getParams().get(idx2).getValue());
                paramList.add(paramHash);
            }
            actionHash.put("params", paramList);
            outActionsList.add(actionHash);
        }
        regionHash.put("outAction", outActionsList);

        return new JSONObject(regionHash);
    }

    private JSONObject createSendErrorJson(BCLError bclError) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", bclError.getCode());
            jsonObject.put("message", bclError.getMessage());
            if (bclError.getDetail() != null) {
                jsonObject.put("detail", bclError.getDetail().getMessage());
            } else {
                jsonObject.put("detail", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject createSendStateJson(BCLState bclState) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", bclState.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
