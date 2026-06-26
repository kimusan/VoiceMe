# QuietType Third-Party Notices

This file tracks third-party software and model artifacts used by QuietType. It must be reviewed before every public binary release.

## Android and Kotlin build/runtime dependencies

| Component | Use | License / notice status |
| --- | --- | --- |
| Android Gradle Plugin | Android build tooling | Android Open Source Project / Apache-2.0 ecosystem. Verify exact notices for the release toolchain. |
| Kotlin | App language and Compose compiler plugin | Apache-2.0. |
| AndroidX Activity Compose | Compose Activity integration | Apache-2.0. |
| AndroidX Core KTX | Android compatibility helpers | Apache-2.0. |
| AndroidX Lifecycle Runtime Compose | Lifecycle-aware Compose state | Apache-2.0. |
| Jetpack Compose UI / Material 3 | App UI and Material You components | Apache-2.0. |
| Apache Commons Compress | Tar/BZip2 model archive inspection/extraction | Apache-2.0. |
| JUnit | Local unit tests | Eclipse Public License 1.0. Test-only dependency. |
| AndroidX Test / Espresso | Instrumentation test dependencies | Apache-2.0. Test-only dependencies. |

## Native ASR runtime

| Component | Use | Source | License / notice status |
| --- | --- | --- | --- |
| sherpa-onnx 1.13.3 Android AAR | Local ASR runtime loaded from `app/libs/sherpa-onnx-1.13.3.aar` | https://github.com/k2-fsa/sherpa-onnx | Apache-2.0 according to upstream project license. Before public release, verify the exact AAR provenance, SHA-256, included native libraries, and any bundled third-party notices from the upstream release artifact. |

## Downloadable ASR model artifacts

QuietType downloads model archives only after explicit user action. Each visible model must have URL, SHA-256, size, license/source, and runtime-required files documented before public release.

| Model ID | User-facing name | Source URL | SHA-256 | License / notice status |
| --- | --- | --- | --- | --- |
| `sherpa-onnx-nemo-parakeet-tdt-0.6b-v3-int8` | Parakeet TDT v3 multilingual int8 | https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-nemo-parakeet-tdt-0.6b-v3-int8.tar.bz2 | `5793d0fd397c5778d2cf2126994d58e9d56b1be7c04d13c7a15bb1b4eafb16bf` | Catalog states CC-BY-4.0 model and Apache-2.0 sherpa-onnx runtime. Verify the upstream Hugging Face/model card license and attribution text before public release. |
| `sherpa-onnx-nemo-parakeet-tdt-0.6b-v2-int8` | Parakeet TDT v2 English int8 | https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-nemo-parakeet-tdt-0.6b-v2-int8.tar.bz2 | `157c157bc51155e03e37d2466522a3a737dd9c72bb25f36eb18912964161e1ad` | Catalog states CC-BY-4.0 model and Apache-2.0 sherpa-onnx runtime. Verify the upstream Hugging Face/model card license and attribution text before public release. |
| `sherpa-onnx-nemo-fast-conformer-ctc-multilingual-int8` | Compact multilingual fallback | https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-nemo-fast-conformer-ctc-be-de-en-es-fr-hr-it-pl-ru-uk-20k-int8.tar.bz2 | `2116eebbfc923ee3332a244e8c933ccc1b7e6783070f7bf842d0b5fc64f6ae33` | Catalog currently requires release NOTICE review. Do not publish until upstream model license and attribution are verified. |
| `sherpa-onnx-streaming-zipformer-en-int8` | Compact streaming English | https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-streaming-zipformer-en-20M-2023-02-17.tar.bz2 | `9c559283e8498d3fe95913c79ca1cb454bb26281ac2b102b41306c7d752765d9` | Catalog currently requires release NOTICE review. Do not publish until upstream model license and attribution are verified. |

## Release gate

Before publishing a binary to GitHub Releases, F-Droid, or Google Play:

1. Verify every license above against the exact dependency/model artifact version being shipped.
2. Add any required copyright/attribution text from model cards and native runtime artifacts.
3. Confirm no proprietary or non-redistributable model/runtime artifact is bundled or required by the release channel.
4. Publish source, license, checksum, and attribution information alongside the release.
