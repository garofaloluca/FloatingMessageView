# FloatingMessageView
![Logo](https://i.ibb.co/kHvjjmM/Floating-Message-View-banner.png)

FloatingMessageView is a library that makes you to show messages on the screen over the current layout (with the highest value on the Z-Axis) attaching it to a target view. 

## Screenshot
![Screenshot](https://i.ibb.co/0MjQQ4h/screen.jpg)<br>
More detailed image: https://i.ibb.co/nzSmV3c/screen.jpg

## Download

### Gradle

Put below code into your root ```build.gradle``` project file:
```gradle
allprojects 
{
    repositories 
    {
        maven { url "https://jitpack.io"}
    }
}
```

Add this dependency into your module's ```build.gradle``` file:
```gradle
dependencies 
{
  implementation 'com.github.garofaloluca:FloatingMessageView:1.0'
}
```
If you have problems using this library, it may be caused by the compatibility of the versions.<br>
Try to add these lines into your module's ```build.gradle``` file if it happens:
```gradle
android 
{
    compileOptions 
    {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## Usage

First, initialize the builder passing as parameter the target view:
```Java
FloatingMessageView.Builder builder = new FloatingMessageView.Builder(targetView);
```

You can customize message as you prefer:
```Java
FloatingMessageView fmv = builder.setBackgroundColor(Color.GREEN)
                                 .setTextColor(Color.WHITE)
                                 .setMessage("Tip about the view.")
                                 .setDuration(FloatingMessageView.DURATION_LONG)
                                 .build();
fmv.show();
```
You can also assign an indefinite duration for a different management of the message (for example to hide it after an event):
```Java
builder.setDuration(FloatingMessageView.DURATION_INDEFINITE);
```
For particular needs you can also set a single verse of the message through:
```Java
builder.setEverUp();
```
or
```Java
builder.setEverDown();
```
To adapt it to the style of your application, in addition to the colors, you can also customize the view shape and the arrow image:
```Java
//  Change the shape of the view
builder.setContentViewCornerRadius(0f);
//  Change arrow image
builder.setArrowDrawable(newArrowDrawable);
```
As with AlertDialogs, you can insert any view inside:
```Java
builder.setView(myCustomView);
```

## License
```
Copyright 2020 Luca Garofalo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
