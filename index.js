
import { NativeModules } from 'react-native';

const { RNComScore } = NativeModules;

class ComScoreTracker {

	constructor(options, metaData = {}) {
		RNComScore.init(options, metaData);
	}

	trackView(view) {
		RNComScore.trackView(view);
	}

	trackEvent(action, category) {
		RNComScore.trackEvent(action, category);
	}

	trackVideoStreaming(info, action) {
		RNComScore.trackVideoStreaming(info, action)
	}

}

export default ComScoreTracker;
