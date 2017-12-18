#import <Foundation/Foundation.h>
#import "RNComscore.h"
#if __has_include("RCTConvert.h")
#import "RCTConvert.h"
#else
#import <React/RCTConvert.h>
#endif

#import <ComScore/ComScore.h>

@implementation RNComScore {

}

NSString *comScoreAppName;
NSString *comScorePublisherSecret;
NSString *comScorePublisherId;
NSString *comScorePixelUrl;
SCORStreamingAnalytics *streamingAnalytics;
SCORStreamingPlaybackSession *playbackSession;

- (dispatch_queue_t)methodQueue
{
	return dispatch_queue_create("com.facebook.React.AsyncLocalStorageQueue", DISPATCH_QUEUE_SERIAL);
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(init:(NSDictionary *) options metaData:(NSDictionary *) metaData)
{
	comScoreAppName = [RCTConvert NSString:options[@"appName"]];
	comScorePublisherId = [RCTConvert NSString:options[@"publisherId"]];
	comScorePublisherSecret = [RCTConvert NSString:options[@"publisherSecret"]];

	streamingAnalytics = [[SCORStreamingAnalytics alloc] init];

	SCORPublisherConfiguration *myPublisherConfig = [SCORPublisherConfiguration
	publisherConfigurationWithBuilderBlock:^(SCORPublisherConfigurationBuilder *builder) {
	  builder.publisherId = comScorePublisherId;
	  builder.publisherSecret = comScorePublisherSecret;
		builder.persistentLabels = metaData;
	}];
	[[SCORAnalytics configuration] addClientWithConfiguration:myPublisherConfig];
	[SCORAnalytics start];
}

RCT_EXPORT_METHOD(trackView:(NSString *) view)
{
	// slashes become dots
	NSString *dottedView = [view stringByReplacingOccurrencesOfString:@"/" withString:@"."];
	// all lowercase
	//NSString *comscoreViewName = [[CSAppName stringByAppendingString:dottedView ] lowercaseString];
	NSString *comscoreViewName = [comScoreAppName stringByAppendingString:dottedView ];
	SCOREventInfo *eventInfo = [[SCOREventInfo alloc] init];
	[eventInfo setLabels:@{@"name":comscoreViewName }];
	[SCORAnalytics notifyViewEventWithEventInfo:eventInfo];
}

RCT_EXPORT_METHOD(trackEvent:(NSString *)action category:(NSString *)category)
{
	NSString *eventName = [NSString stringWithFormat:@"%@.%@",category,action];
	SCOREventInfo *eventInfo = [[SCOREventInfo alloc] init];
	[eventInfo setLabels:@{@"name":eventName }];
	[SCORAnalytics notifyHiddenEventWithEventInfo:eventInfo];
}

RCT_EXPORT_METHOD(trackVideoStreaming:(NSDictionary*)videoInfo videoAction:(NSString *)videoAction)
{
	NSLog( @"Video Action: '%@'", videoAction );
	// long position = videoInfo[@"position"] ? [videoInfo[@"position"] longValue] : 0L;

	if ([videoAction isEqualToString:@"start"]) {
		// NSLog( @"notifyPlay: '%@'", videoAction );
		// [streamingAnalytics createPlaybackSession];
		// [[streamingAnalytics playbackSession] setAssetWithLabels: videoInfo];
		[streamingAnalytics createPlaybackSessionWithLabels videoInfo];
		// [streamingAnalytics notifyPlayWithLabels videoInfo];
		[streamingAnalytics notifyPlay];
		// [streamingAnalytics notifyPlayWithPosition:position labels:videoInfo];
	} else if ([videoAction isEqualToString:@"resume"]) {
		// NSLog( @"notifyPlay: '%@'", videoAction );
		[streamingAnalytics notifyPlay];
	} else if ([videoAction isEqualToString:@"stop"]) {
		// NSLog( @"notifyStop: '%@'", videoAction );
		[streamingAnalytics notifyEnd];
	} else if ([videoAction isEqualToString:@"pause"]) {
		// NSLog( @"notifyPause: '%@'", videoAction );
		[streamingAnalytics notifyPause];
	}
}

@end
