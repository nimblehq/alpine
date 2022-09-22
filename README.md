# **A**ndroid **L**ibrary for **P**assport **I**nformation via **N**FC **E**xtraction


ALPINE is a library to simplify the extraction of passport information by reading data off of the NFC chip

## Download

Clone the repository

`git clone git@github.com:nimblehq/alpine.git`

## Usage

### Scanning Passport MRZ

MRZ reading is performed by the `MrzProcessor` by passing in a filepath and a callback to receive the result.

To create an instance of `MrzProcessor`, call `newInstance()`

```kotlin
val mrzProcessor = MrzProcessor.newInstance()
```

To extract the MRZ from the passport, call `processImageFile()`

```kotlin
val imageFilePath: String = imageFile.absolutePath
val mrzProcessorResultListener = object : MrzProcessorResultListener {
    override fun onSuccess(mrzInfo: MrzInfo) {}
    override fun onError(e: MrzProcessorException) {}
}
mrzProcessor.processImageFile(imageFilePath, mrzProcessResultL)
```

### Reading NFC

NFC reading is performed by `NfcReader` by passing in a `Tag`, `MrzInfo` object and an optional `timeout`

To create an instance of `NfcReader`, call `newInstance()`

```kotlin
val nfcReader = NfcReader.newInstance(context)
```

To read the encrypted NFC data, call `readNfc()`, making sure to call this method *off* the main thread

```kotlin
val passportInfo = nfcReader.readNfc(tag, mrzInfo)
```

## Processing live camera input

Hooking up the `MrzProcessor` to process each frame of camera input will depend on the camera library used

### CameraX
To process each camera frame, make use of `ImageAnalysis`
```kotlin
val imageAnalysis = ImageAnalysis.Builder()
    .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
    .setTargetResolution(Size(1280, 720))
    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
    .build()
```
After creating the `ImageAnalysis` object, set an `Analyzer` on it
```kotlin
imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->
    mrzProcessor.processImage(imageProxy.toCameraImage(), object : MrzProcessorResultListener {
        override fun onSuccess(mrzInfo: MrzInfo) {
            imageProxy.close()
            NfcScanningActivity.start(this@CameraCaptureActivity, mrzInfo)
        }

        override fun onError(e: MrzProcessorException) = imageProxy.close()
    })
}
```
The `Analyzer` converts the `ImageProxy` provided by CameraX into a `CameraImage` that is then passed to `MrzProcessor` via the `processImage()` method
```kotlin
private fun ImageProxy.toCameraImage() = CameraImage(
    width = width,
    height = height,
    cropRect = cropRect,
    format = format,
    rotationDegrees = imageInfo.rotationDegrees,
    planes = planes.map {
        CameraImage.Plane(
            rowStride = it.rowStride,
            pixelStride = it.pixelStride,
            buffer = it.buffer
        )
    }
)
```

### Camera2
Camera2 makes use of `ImageReader` to process camera frame
```kotlin
val imageReader = ImageReader.newInstance()
imageReader.setOnImageAvailableListener({ reader ->
    val image: Image = reader.acquireNextImage()
    mrzProcessor.processImage(image.toCameraImage(), listener)
}, imageReaderHandler)
```
The `ImageReader` takes an `Image` from the `reader`, converts it into a `CameraImage`, and passes it to the `MrzProcessor`
```kotlin
private fun Image.toCameraImage() = CameraImage(
    width = width,
    height = height,
    cropRect = cropRect,
    format = format,
    rotationDegrees = getRotationDegrees(),
    planes = planes.map {
        CameraImage.Plane(
            rowStride = it.rowStride,
            pixelStride = it.pixelStride,
            buffer = it.buffer
        )
    }
)
```

## License

This project is Copyright (c) 2014 and onwards Nimble. It is free software and may be redistributed under the terms specified in the [LICENSE] file.

[LICENSE]: /LICENSE

## About
<a href="https://nimblehq.co/">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://assets.nimblehq.co/logo/dark/logo-dark-text-160.png">
    <img alt="Nimble logo" src="https://assets.nimblehq.co/logo/light/logo-light-text-160.png">
  </picture>    
</a>

This project is maintained and funded by Nimble.

We love open source and do our part in sharing our work with the community!
See [our other projects][community] or [hire our team][hire] to help build your product.

[community]: https://github.com/nimblehq
[hire]: https://nimblehq.co/
