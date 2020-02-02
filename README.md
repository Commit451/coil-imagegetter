# CoilImageGetter
Loads images for Html rendering using Coil

[![Build Status](https://travis-ci.org/Commit451/CoilImageGetter.svg?branch=master)](https://travis-ci.org/Commit451/CoilImageGetter)
[![](https://jitpack.io/v/Commit451/CoilImageGetter.svg)](https://jitpack.io/#Commit451/CoilImageGetter)

## Dependency
Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Then, add the library to your project `build.gradle`
```gradle
dependencies {
    implementation("com.github.Commit451:CoilImageGetter:latest.version.here")
}
```

## Usage
When rendering HTML, you will need to provide an `Html.ImageGetter`, which
```kotlin
val html = ...
val getter = CoilImageGetter(textView)
val spanned = Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler)
textView.text = spanned
```

For advanced usage, you can also specify your own `ImageLoader` and source modifier.

License
--------

    Copyright 2020 Commit 451

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
