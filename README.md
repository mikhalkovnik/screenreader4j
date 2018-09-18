screenreader4j
==============

ScreenReader4j provides you take screenshots of windows that may be not focused.

Supported platforms
===================

1. Windows (XP and above)
2. ~~Linux~~ (Coming soon)
3. ~~Mac OS~~ (Coming soon)

How to use
==========
1. Find `NativeWindow` with `NativeWindow.find`
```java
List<NativeWindow> windows = NativeWindow.find(window -> window.title.contains("Notepad++"));
NativeWindow window = window.get(0);
```
2. Create new `ScreaderReader` instance
```java
ScreenReader reader = new ScreenReader();
```
3. Target `NativeWindow` to 'ScreenReader`
```java
reader.target(window);
```
4. Take buffer of pixel data
```java
int[] pixelData = reader.getBuffer();
```
5. Capture and process pixels
```java
while (running) {
  reader.capture();
  process(pixelData);
}
```
6. Release resources after using
```java
reader.release();
```