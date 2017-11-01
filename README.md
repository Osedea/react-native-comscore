
# react-native-comscore

## Getting started

`$ npm install react-native-comscore --save`

### Mostly automatic installation

`$ react-native link react-native-comscore`

#### iOS required extra step

Add to your Podfile :

```
pod 'ComScore'
```

Add the `AdSupport.framework` to your project

#### Android required extra steps

Add to your `android/app/build.gradle`:

```diff
repositories {
  // your other dependencies
+  maven { url 'http://comscore.bintray.com/Analytics' }
}
```

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-comscore` and add `RNComscore.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNComscore.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import be.smalltownheroes.ketnet.karrewiet.RNComscorePackage;` to the imports at the top of the file
  - Add `new RNComscorePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-comscore'
  	project(':react-native-comscore').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-comscore/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-comscore')
  	```

## Usage

```js
import RNComscore from 'react-native-comscore';

const comScoreTracker = new RNComscore(
    {
        appName: string,
        publisherId: string,
        publisherSecret: string,
        pixelUrl: ?string,
    },
    {} // optional labels
);

comscoreTracker.trackVideoStreaming(
    {
        ns_st_ci: string, // ex: '123456'
        ns_st_cl: string, // ex: '1200'
        ns_st_ge: string, // ex: 'movie'
        ...
    },
    'start' | 'resume' | 'stop' | 'pause'
);
```

## ComScore trackVideoStreaming Attributes of interest

|     label    |     Item             |
| ------------ | -------------------- |
| `ns_st_ci`   | Unique Content ID    |
| `ns_st_cl`   | Asset Length         |
| `ns_st_ge`   | Content Genre        |
| `ns_st_ct`   | Classification Type  |

(for more, see ComScore documentation)

## SDK Resources

* [iOS](https://github.com/comScore/ComScore-iOS-SDK)
* [Android](https://github.com/comScore/ComScore-Android)
