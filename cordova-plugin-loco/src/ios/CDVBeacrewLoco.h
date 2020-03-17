//
//  CDVBeacrewLoco.h
//
//  Created by Yukio Sekimoto on 2018/10/18.
//  Copyright (c) 2018 Beacrew Inc. All rights reserved.
//

#import <Cordova/CDV.h>
#import <BeacrewLoco/BeacrewLoco.h>

@interface CDVBeacrewLoco : CDVPlugin <BCLManagerDelegate>

@property (nonatomic, strong) NSString* callbackId;

- (void)initWithAPIKey:(CDVInvokedUrlCommand *)command;
- (void)scanStart:(CDVInvokedUrlCommand *)command;
- (void)scanStop:(CDVInvokedUrlCommand *)command;
- (void)getDeviceId:(CDVInvokedUrlCommand *)command;
- (void)getNearestBeaconId:(CDVInvokedUrlCommand *)command;
- (void)getState:(CDVInvokedUrlCommand *)command;
- (void)getBeacons:(CDVInvokedUrlCommand *)command;
- (void)getClusters:(CDVInvokedUrlCommand *)command;
- (void)getRegions:(CDVInvokedUrlCommand *)command;
- (void)getActions:(CDVInvokedUrlCommand *)command;
- (void)addEventLog:(CDVInvokedUrlCommand *)command;
- (void)addDeviceLog:(CDVInvokedUrlCommand *)command;
@end
