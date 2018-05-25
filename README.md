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
  implementation 'com.github.User:Repo:Tag'
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
resample.destroy(48000, 16000, 2048, 1);
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
