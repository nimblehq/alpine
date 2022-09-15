# **A**ndroid **L**ibrary for **P**assport **I**nformation via **N**FC **E**xtraction


ALPINE is a library to simplify the extraction of passport information by reading data off of the NFC chip

## Download

Clone the repository

`git clone git@github.com:nimblehq/alpine.git`

## Usage

### Scanning Passport MRZ

MRZ reading is performed by the `MrzProcessor` by passing in a filepath and a callback to receive the result.

To create an instance of `MrzProcessor`, we can call `newInstance()`

```kotlin
val mrzProcessor = MrzProcessor.newInstance()
```

To extract the MRZ from the passport, we can call `processImageFile()`

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

To create an instance of `NfcReader`, we can call `newInstance()`

```kotlin
val nfcReader = NfcReader.newInstance(context)
```

To read the encrypted NFC data, we can call `readNfc()`

```kotlin
val passportInfo = nfcReader.readNfc(tag, mrzInfo)
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
