
package be.smalltownheroes.ketnet.karrewiet;

import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.IllegalArgumentException;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.comscore.Analytics;
import com.comscore.EventInfo;
import com.comscore.streaming.StreamingAnalytics;
import com.comscore.PublisherConfiguration;

public class RNComScoreModule extends ReactContextBaseJavaModule {

	private final ReactApplicationContext reactContext;

	private StreamingAnalytics streamingAnalytics;
	private String appName;

	public RNComScoreModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "RNComScore";
	}

	@ReactMethod
	public void init(ReadableMap options, ReadableMap metaData) {
		String comScoreAppName = options.getString("appName");
		String comScorePublisherId = options.getString("publisherId");
		String comScorePublisherSecret = options.getString("publisherSecret");
		String comScorePixelUrl = null;

		if (options.hasKey("pixelUrl")) {
			comScorePixelUrl = options.getString("pixelUrl");
		}

		this.streamingAnalytics = new StreamingAnalytics();

		HashMap<String, String> labels = extractLabelsFromMap(metaData);

		PublisherConfiguration.Builder builder = new PublisherConfiguration.Builder()
			.applicationName(comScoreAppName)
			.publisherId(comScorePublisherId)
			.publisherSecret(comScorePublisherSecret);

		if (comScorePixelUrl != null) {
			builder.liveEndpointUrl(comScorePixelUrl);
		}
		if (!labels.isEmpty()) {
			builder.persistentLabels(labels);
		}

		builder.secureTransmission(true);

		PublisherConfiguration publisher = builder
			.build();

		Analytics.getConfiguration().addClient(publisher);
		Analytics.start(this.reactContext);
		setAppName(comScoreAppName);
	}

	@ReactMethod
	public void trackView(String view) {
		String comScoreViewName = getAppName() + view;
		HashMap<String,String> labels = new HashMap<String,String>();
		labels.put("name", comScoreViewName.replace("/", "."));
		Analytics.notifyViewEvent(labels);
	}

	@ReactMethod
	public void trackEvent(String action, String category) {
		String comScoreEventName = category + "." + action;
		HashMap<String,String> labels = new HashMap<String,String>();
		labels.put("event", comScoreEventName);
		Analytics.notifyViewEvent(labels);
	}

	@ReactMethod
	public void trackVideoStreaming(ReadableMap videoInfo, String videoAction) {
		// Log.i("React-native-comscore", "####### videoAction ####### " + videoAction);
		// Log.i("React-native-comscore", "####### videoInfo ####### " + videoInfo);
		if (videoInfo != null) {
			HashMap<String, String> playbackLabels = extractLabelsFromMap(videoInfo);

			if (videoAction.equals("start")) {
				// Log.i("React-native-comscore", "####### notifyPlay ####### " + position);
				this.streamingAnalytics.createPlaybackSession();
				this.streamingAnalytics.getPlaybackSession().setLabels(playbackLabels);
				this.streamingAnalytics.notifyPlay();
			} else if (videoAction.equals("stop")) {
				// Log.i("React-native-comscore", "####### notifyEnd ####### " + position);
				this.streamingAnalytics.notifyEnd();
			} else if (videoAction.equals("pause")) {
				// Log.i("React-native-comscore", "####### notifyPause ####### " + position);
				this.streamingAnalytics.notifyPause();
			} else if (videoAction.equals("resume")) {
				// Log.i("React-native-comscore", "####### notifyPlay ####### " + position);
				this.streamingAnalytics.notifyPlay();
			}
		}
	}

	public HashMap<String, String> extractLabelsFromMap(ReadableMap metaData) {
		HashMap<String, String> labels = new HashMap<String, String>();

		ReadableMapKeySetIterator iterator = metaData.keySetIterator();
		while (iterator.hasNextKey()) {
			String key = iterator.nextKey();
			ReadableType type = metaData.getType(key);
			switch (type) {
				case String:
					labels.put(key, metaData.getString(key));
					break;
				default:
					throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
			}
		}

		return labels;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
