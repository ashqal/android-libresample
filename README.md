# android-libresample
android libresample, based on https://github.com/intervigilium/libresample

## Gradle
```java
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

```java
dependencies {
  implementation 'com.github.ashqal:android-libresample:0.3.0'
}
```

## Usage
* create
```java
Resample resample = new Resample();
resample.create(48000, 16000, 2048, 1);
```

* destroy
```java
Resample resample = new Resample();
resample.destroy();
```

* resample
```java
// input is ByteBuffer allocateDirect
// output is ByteBuffer allocateDirect

int output_len = resample.resample(input, output, input.remaining());

// usage of output
cached.put(output.array(), output.arrayOffset(), output_len);
```

* resampleEx
```java
// input is ByteBuffer allocateDirect
// output is ByteBuffer allocateDirect

int output_len = resample.resampleEx(input, output, input.remaining());

// usage of output
cached.put(output.array(), output.arrayOffset(), output_len);
```

## License
```
MIT License

Copyright (c) 2018 Asha

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
