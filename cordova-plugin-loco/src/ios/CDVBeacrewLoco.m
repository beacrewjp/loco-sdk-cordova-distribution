//
//  CDVBeacrewLoco.m
//
//  Created by Yukio Sekimoto on 2018/10/18.
//  Copyright (c) 2018 Beacrew Inc. All rights reserved.
//

#import "CDVBeacrewLoco.h"

@implementation CDVBeacrewLoco

- (void)initWithAPIKey:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);

    self.callbackId = command.callbackId;

    NSString *apiKey = [command.arguments objectAtIndex:0];
    BOOL autoScan = [[command.arguments objectAtIndex:1] boolValue];

    BCLManager *manager = [BCLManager sharedManager];
    manager.delegate = self;

    if (manager.state == BCLStateUninitialized || manager.state != BCLStateReady) {
        [manager initWithApiKey:apiKey autoScan:autoScan];
    }
}
	
- (void)scanStart:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    [[BCLManager sharedManager] scanStart];
}

- (void)scanStop:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    [[BCLManager sharedManager] scanStop];
}

- (void)getDeviceId:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    NSString *str = [[BCLManager sharedManager] getDeviceId];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:str];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getNearestBeaconId:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    NSString *str = [[BCLManager sharedManager] getNearestBeaconId];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:str];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getState:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    BCLState status = [[BCLManager sharedManager] state];
    NSString *str;
    switch (status) {
        case BCLStateUninitialized:
            str = @"Uninitialized";
            break;
        case BCLStateInitializing:
            str = @"Initializing";
            break;
        case BCLStateReady:
            str = @"Ready";
            break;
        case BCLStateScanning:
            str = @"Scanning";
            break;
        case BCLStateError:
            str = @"Error";
            break;
        default:
            str = @"Unknown";
            break;
    }
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:str];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getBeacons:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    NSMutableArray *marray = [NSMutableArray array];
    NSArray *beacons = [[BCLManager sharedManager] getBeacons];
    for (BCLBeacon *beacon in beacons) {
        [marray addObject:[CDVBeacrewLoco beaconToDictionary:beacon]];
    }
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"beacons"] = marray;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getClusters:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    NSMutableArray *marray = [NSMutableArray array];
    NSArray *clusters = [[BCLManager sharedManager] getClusters];
    for (BCLCluster *cluster in clusters) {
        [marray addObject:[CDVBeacrewLoco clusterToDictionary:cluster]];
    }
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"clusters"] = marray;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getRegions:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    NSMutableArray *marray = [NSMutableArray array];
    NSArray *regions = [[BCLManager sharedManager] getRegions];
    for (BCLRegion *region in regions) {
        [marray addObject:[CDVBeacrewLoco regionToDictionary:region]];
    }
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"regions"] = marray;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getActions:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    NSMutableArray *marray = [NSMutableArray array];
    NSArray *actions = [[BCLManager sharedManager] getActions];
    for (BCLAction *action in actions) {
        [marray addObject:[CDVBeacrewLoco actionToDictionary:action type:nil source:nil]];
    }
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"actions"] = marray;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)addEventLog:(CDVInvokedUrlCommand *)command {
    NSLog(@"%s", __FUNCTION__);
    [[BCLManager sharedManager] addEventLog:([command.arguments objectAtIndex:0]) value:([command.arguments objectAtIndex:1])];
}

+ (NSDictionary *)beaconToDictionary:(BCLBeacon *)beacon {
	NSMutableDictionary *beaconDictionary = [NSMutableDictionary dictionary];
    beaconDictionary[@"beaconId"] = beacon.beaconId;
    beaconDictionary[@"name"] = beacon.name;
    beaconDictionary[@"uuid"] = beacon.uuid;
    beaconDictionary[@"major"] = @(beacon.major);
    beaconDictionary[@"minor"] = @(beacon.minor);
    beaconDictionary[@"txPower"] = @(beacon.txPower);
    beaconDictionary[@"rssi"] = @(beacon.rssi);
    beaconDictionary[@"localName"] = beacon.localName;
    beaconDictionary[@"moduleId"] = beacon.moduleId;
    beaconDictionary[@"model"] = beacon.model;
    beaconDictionary[@"manufacturer"] = beacon.manufacturer;
    beaconDictionary[@"x"] = @(beacon.x);
    beaconDictionary[@"y"] = @(beacon.y);
    beaconDictionary[@"height"] = @(beacon.height);
	NSMutableArray *actionsArray = [NSMutableArray array];
    for (BCLAction *action in beacon.actions) {
	    [actionsArray addObject:[CDVBeacrewLoco actionToDictionary:action type:nil source:nil]];
    }
    beaconDictionary[@"actions"] = actionsArray;
    
    return beaconDictionary;
}

+ (NSDictionary *)actionToDictionary:(BCLAction *)action  type:(NSString *)type source:(id)source {
	NSMutableDictionary *actionDictionary = [NSMutableDictionary dictionary];
	actionDictionary[@"actionId"] = action.actionId;
	actionDictionary[@"name"] = action.name;
	actionDictionary[@"uri"] = (action.uri) ? action.uri : [NSNull null];
	actionDictionary[@"interval"] = @(action.interval);
	NSMutableArray *paramsArray = [NSMutableArray array];
    for (BCLParam *param in action.params) {
	    [paramsArray addObject:[CDVBeacrewLoco paramToDictionary:param]];
    }
    actionDictionary[@"params"] = paramsArray;
    if(type != nil) {
        actionDictionary[@"type"] = type;
        if ([source isKindOfClass:[BCLRegion class]]) {
            actionDictionary[@"source"] = [CDVBeacrewLoco regionToDictionary:source];
        } else if ([source isKindOfClass:[BCLBeacon class]]) {
            actionDictionary[@"source"] = [CDVBeacrewLoco beaconToDictionary:source];
        }
    }
    
    return actionDictionary;
}

+ (NSDictionary *)regionToDictionary:(BCLRegion *)region {
	NSMutableDictionary *regionDictionary = [NSMutableDictionary dictionary];
    regionDictionary[@"regionId"] = region.regionId;
    regionDictionary[@"name"] = region.name;
    regionDictionary[@"type"] = region.type;
    regionDictionary[@"uuid"] = (region.uuid) ? region.uuid : [NSNull null];
    regionDictionary[@"major"] = (region.major) ? region.major : [NSNull null];
    regionDictionary[@"minor"] = (region.minor) ? region.minor : [NSNull null];
    regionDictionary[@"latitude"] = (region.latitude) ? region.latitude : [NSNull null];
    regionDictionary[@"longitude"] = (region.longitude) ? region.longitude : [NSNull null];
    regionDictionary[@"radius"] = (region.radius) ? region.radius : [NSNull null];
	NSMutableArray *inActionArray = [NSMutableArray array];
    for (BCLAction *action in region.inAction) {
	    [inActionArray addObject:[CDVBeacrewLoco actionToDictionary:action type:nil source:nil]];
    }
    regionDictionary[@"inAction"] = inActionArray;
	NSMutableArray *outActionArray = [NSMutableArray array];
    for (BCLAction *action in region.outAction) {
	    [outActionArray addObject:[CDVBeacrewLoco actionToDictionary:action type:nil source:nil]];
    }
    regionDictionary[@"outAction"] = outActionArray;
    
    return regionDictionary;
}

+ (NSDictionary *)clusterToDictionary:(BCLCluster *)cluster {
	NSMutableDictionary *clusterDictionary = [NSMutableDictionary dictionary];
    clusterDictionary[@"clusterId"] = cluster.clusterId;
    clusterDictionary[@"parentId"] = (cluster.parentId) ? cluster.parentId : [NSNull null];
    clusterDictionary[@"name"] = cluster.name;
    clusterDictionary[@"tag"] = cluster.tag;
    clusterDictionary[@"image"] = (cluster.image) ? cluster.image : [NSNull null];
	NSMutableArray *beaconsArray = [NSMutableArray array];
    for (BCLBeacon *beacon in cluster.beacons) {
	    [beaconsArray addObject:[CDVBeacrewLoco beaconToDictionary:beacon]];
    }
    clusterDictionary[@"beacons"] = beaconsArray;
    
    return clusterDictionary;
}

+ (NSDictionary *)paramToDictionary:(BCLParam *)param {
	NSMutableDictionary *paramDictionary = [NSMutableDictionary dictionary];
    paramDictionary[@"key"] = param.key;
    paramDictionary[@"value"] = param.value;
    
    return paramDictionary;
}

+ (NSDictionary *)errorToDictionary:(BCLError *)error {
	NSMutableDictionary *errorDictionary = [NSMutableDictionary dictionary];
    errorDictionary[@"code"] = @(error.code);
    errorDictionary[@"message"] = error.message;
    errorDictionary[@"detail"] = (error.detail) ? [error.detail localizedDescription] : [NSNull null];
    
    return errorDictionary;
}

#pragma mark BCLManagerDelegate

- (void)didRangeBeacons:(NSArray<BCLBeacon *> *)beacons {
    NSLog(@"%s", __FUNCTION__);
    
    NSMutableArray *marray = [NSMutableArray array];
    for (BCLBeacon *beacon in beacons) {
        [marray addObject:[CDVBeacrewLoco beaconToDictionary:beacon]];
    }
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"eventType"] = @"onBeaconDetected";
    mdic[@"message"] = @{@"beacons":marray};
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    pluginResult.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)didEnterRegion:(BCLRegion *)region {
    NSLog(@"%s", __FUNCTION__);
    
    NSDictionary *dic = [CDVBeacrewLoco regionToDictionary:region];
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"eventType"] = @"onRegionIn";
    mdic[@"message"] = dic;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    pluginResult.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)didExitRegion:(BCLRegion *)region {
    NSLog(@"%s", __FUNCTION__);
    
    NSDictionary *dic = [CDVBeacrewLoco regionToDictionary:region];
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"eventType"] = @"onRegionOut";
    mdic[@"message"] = dic;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    pluginResult.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)didActionCalled:(BCLAction *)action type:(NSString *)type source:(id)source {
    NSLog(@"%s", __FUNCTION__);
    
    NSDictionary *dic = [CDVBeacrewLoco actionToDictionary:action type:type source:source];
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"eventType"] = @"onActionDetected";
    mdic[@"message"] = dic;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    pluginResult.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)didFailWithError:(BCLError *)error {
    NSLog(@"%s", __FUNCTION__);
    
    NSDictionary *dic = [CDVBeacrewLoco errorToDictionary:error];
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"eventType"] = @"onError";
    mdic[@"message"] = dic;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    pluginResult.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

- (void)didChangeStatus:(BCLState)status {
    NSLog(@"%s", __FUNCTION__);
    
    NSString *initState = @"";
    switch (status) {
        case BCLStateUninitialized:
            initState = @"Uninitialized";
            break;
        case BCLStateInitializing:
            initState = @"Initializing";
            break;
        case BCLStateReady:
            initState = @"Ready";
            break;
        case BCLStateScanning:
            initState = @"Scanning";
            break;
        case BCLStateError:
            initState = @"Error";
            break;
        default:
            initState = @"Unknown";
            break;
    }
    NSMutableDictionary *mdic = [NSMutableDictionary dictionary];
    mdic[@"eventType"] = @"onStateChange";
    mdic[@"message"] = @{@"status":initState};
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:mdic];
    pluginResult.keepCallback = @YES;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

@end
