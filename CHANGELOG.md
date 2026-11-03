## [2.1.1](https://github.com/gciatto/kt-math/compare/2.1.0...2.1.1) (2026-11-03)

### Bug Fixes

* **release:** no line break before the || true of the GitHub Packages publish, which broke the publish script ([879a63d](https://github.com/gciatto/kt-math/commit/879a63d1138162913623c35d5bd4185aa7fa24b1))

## [2.1.0](https://github.com/gciatto/kt-math/compare/2.0.0...2.1.0) (2026-11-03)

### Features

* GitHub releases attach the JVM jars, the npm tarball and the C shared libraries; npm package on GitHub Packages ([048fd2a](https://github.com/gciatto/kt-math/commit/048fd2a710a2cc0c2039657b76f5750b5809a18f))

### Documentation

* getting started in C with the prebuilt libraries, Kotlin/Native and GitHub Packages dependencies ([ec19157](https://github.com/gciatto/kt-math/commit/ec19157947447fd884e853a66602685ae48dc9cf))

### Build and continuous integration

* a CI summary, as in 2p-kt, filled in by every job as it finishes ([82d5be0](https://github.com/gciatto/kt-math/commit/82d5be0f9349905f3208a0dd156604e49d187d04))

## [2.0.0](https://github.com/gciatto/kt-math/compare/1.0.0...2.0.0) (2026-11-03)

### ⚠ BREAKING CHANGES

* no unnecessary nulls in the API: MathContext arguments default to UNLIMITED, plus and times take non-null operands
* pow, absoluteValue, unaryMinus and unaryPlus with a MathContext are non-null too
* BigDecimal.div with a MathContext and round are non-null
* **js:** an ES module exporting every declaration by name, with static companion members
* lock, DEBUG and JavaBigDecimalAdapter are internal
* **big:** BigDecimalMath functions are top-level and extension functions, exported to JS

### Features

* **big:** BigComplex (with BigComplexMath as members) and BigDecimalStream (as sequences) ([589ae14](https://github.com/gciatto/kt-math/commit/589ae14c4fe7d5aa18cb2115096a5517af70c72b))
* BigInteger.getIntBits and getLongBits ([fa68a0a](https://github.com/gciatto/kt-math/commit/fa68a0af19ae207b7eda760ba1551ba700d901a6))
* BigInteger.of(bytes) and BigInteger.of(signum, magnitude) factories ([7d43bf4](https://github.com/gciatto/kt-math/commit/7d43bf4060dbe58d468b71cf629776bcd60e007c))
* **big:** k-th complex roots, BigComplex.root(n, k, mc) ([0719072](https://github.com/gciatto/kt-math/commit/0719072a2d028c3cc86365133779c1c3270489a1))
* **js:** an ES module exporting every declaration by name, with static companion members ([929b23f](https://github.com/gciatto/kt-math/commit/929b23fe4b2c2d1c0b2e8cc07242561df5253c21))

### Bug Fixes

* **big:** complex sqrt of 0 and of negative reals, hence asin and acos of many reals ([6386755](https://github.com/gciatto/kt-math/commit/6386755d79d3db2627b0f1848cd8d516674a1f1b))
* BigDecimal.div with a MathContext and round are non-null ([15a5e1e](https://github.com/gciatto/kt-math/commit/15a5e1e14ee7003cbbebc119a7b5b7807359a550))
* **deps:** kotlin-reflect is no longer an API dependency ([8619284](https://github.com/gciatto/kt-math/commit/86192846c34557a337ba84b19060e62f9c9a9c92))
* lock, DEBUG and JavaBigDecimalAdapter are internal ([7979f76](https://github.com/gciatto/kt-math/commit/7979f76e8cdd35bf960389e74d9714cac43a135b))
* no unnecessary nulls in the API: MathContext arguments default to UNLIMITED, plus and times take non-null operands ([1864ba6](https://github.com/gciatto/kt-math/commit/1864ba6f19bfe27f0753d03854737ad7590a8c7c))
* pow, absoluteValue, unaryMinus and unaryPlus with a MathContext are non-null too ([88e7b98](https://github.com/gciatto/kt-math/commit/88e7b98cdc15ccf0ee0cb003c809925851586761))

### Documentation

* agent notes on BigComplex, BigDecimalStream and the big-math test classes ([a2a6d23](https://github.com/gciatto/kt-math/commit/a2a6d234749e289d755c400fa4890d68fa709056))
* KDoc for every public declaration, enforced by detekt; extension functions documented as such ([65c005d](https://github.com/gciatto/kt-math/commit/65c005da5533e638f9701b7912b446128927c0fc))

### Performance improvements

* **native:** C kernels on 64-bit limbs for the BigInteger routines HotSpot intrinsifies ([cc73153](https://github.com/gciatto/kt-math/commit/cc73153d8a1a8d499594e799e19187f20788bfca))

### Tests

* **big:** BigComplex and decimal ranges against big-math, one test class per big-math class ([23d6e72](https://github.com/gciatto/kt-math/commit/23d6e72a34898e6356cc91196a5700982c985e47))
* **big:** complex roots of non-positive reals, k-th roots, isIntValue and isLongValue limits ([57c3913](https://github.com/gciatto/kt-math/commit/57c391319eeda17938a92bb718fe78fa8e1468aa))
* differential cases for Montgomery multiplication and long schoolbook products ([f0717da](https://github.com/gciatto/kt-math/commit/f0717da1f44adfde67fa78a5f05092bb1111ba3d))
* differential tests of BigInteger.getIntBits and getLongBits ([8e47175](https://github.com/gciatto/kt-math/commit/8e47175e86325a9eda74a94c391e8b38846dd629))
* differential tests of the byte-array BigInteger factories ([41ddca2](https://github.com/gciatto/kt-math/commit/41ddca242eeaa0aebb8491a9b4396cbdf165913e))
* div with a MathContext is non-null in the TypeScript usage test ([0e256b0](https://github.com/gciatto/kt-math/commit/0e256b00a5c668e11510c189583306c0553113bc))
* getIntBits and getLongBits in the Java, JS and C usage tests ([c4e8086](https://github.com/gciatto/kt-math/commit/c4e8086d13849969926c2180828e06ebeb93d664))
* Java and C usage tests covering the whole API, checked by reflection and on the C header ([58ccb35](https://github.com/gciatto/kt-math/commit/58ccb35973dd4af9dd4ba94aa4a0e4ecb6c4adee))
* MathContext.UNLIMITED instead of null in the Java and C usage tests ([4f6cc75](https://github.com/gciatto/kt-math/commit/4f6cc758f31886ede70f01f0f4c1275bdb257871))
* one base class for the differential tests against java.math and big-math ([d0d8942](https://github.com/gciatto/kt-math/commit/d0d8942e358f901efdb3e1eabe289ace5993ea17))
* the byte-array factories in the Java, JS and C usage tests and in TestBigFactory ([87d8061](https://github.com/gciatto/kt-math/commit/87d8061df948d8efca599425109a8360773c53aa))

### Build and continuous integration

* benchmarks in a dedicated source set, run by the benchmark task ([c24b602](https://github.com/gciatto/kt-math/commit/c24b6028edc2005677093dc345d3711c1147b636))
* build and release on macOS, where cinterop can build the Apple klibs ([c7ed35c](https://github.com/gciatto/kt-math/commit/c7ed35c06419d7882f445a168e1fd81ce7411ae3))

### Refactoring

* **big:** BigDecimalMath functions are top-level and extension functions, exported to JS ([e441289](https://github.com/gciatto/kt-math/commit/e44128979f109a0656e49ef1ba619dc2a6c0b264))
* **big:** isIntValue and isLongValue without exceptions; answer big-math's TODOs on pow and toDouble ([4ba3a5d](https://github.com/gciatto/kt-math/commit/4ba3a5d4d4b29692d5310ff7e2b88c188b3ad4e7))
* one lock for every cache, CacheLock (lock is gone) ([ea9eace](https://github.com/gciatto/kt-math/commit/ea9eace4a77f3366fd21a7e6b262bfeef96df91b))

## [1.0.0](https://github.com/gciatto/kt-math/compare/0.10.2...1.0.0) (2026-11-03)

### ⚠ BREAKING CHANGES

* **js:** remainder and ofSignMagnitudeSlice JS names; BigDecimal.of with a precision is internal
* on JS, BigInteger % with negative operands now follows the
dividend's sign, and a negative modulus no longer throws.

### Features

* add BigDecimal.div with scale and/or rounding mode ([b307c19](https://github.com/gciatto/kt-math/commit/b307c19e0f2ca7cc6f6b305c4ee93cce5414235a))
* BigFactory, conversions among BigInteger, BigDecimal and BigRational ([6656786](https://github.com/gciatto/kt-math/commit/6656786b025a4bab6c912934be1a434c6026296f))
* **js:** TypeScript definitions in the npm package ([d05ec27](https://github.com/gciatto/kt-math/commit/d05ec277f342f1c15d44e7c123b1affee934f13e))
* Kotlin/Native targets ([d9b128e](https://github.com/gciatto/kt-math/commit/d9b128ed90bed39e7f17478f2c768ce577e9a6aa))
* kt-math-big-math, a multiplatform port of big-math's BigDecimalMath ([5cf52db](https://github.com/gciatto/kt-math/commit/5cf52dbc080b82ccc9eef602adc0573f7bab67f0))

### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.146 ([0b6cebd](https://github.com/gciatto/kt-math/commit/0b6cebde5c32eb19ca3ac648f1c29da939a2cf6c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.147 ([dd76216](https://github.com/gciatto/kt-math/commit/dd76216c6cf37383ecac7356f7530aa088f3d4b4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.148 ([62a95f4](https://github.com/gciatto/kt-math/commit/62a95f49f2d2016318d81512bc67fdf60769f26c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.149 ([eebbf7b](https://github.com/gciatto/kt-math/commit/eebbf7b0ae4900e90f38fc71c70ebee3541d4b83))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.150 ([e4a94de](https://github.com/gciatto/kt-math/commit/e4a94defdb7a2c6b66609998359ea7fa6041c380))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.152 ([99f51df](https://github.com/gciatto/kt-math/commit/99f51df5b52c92334a8f2e15621eb00eb50febc0))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.153 ([9b109e6](https://github.com/gciatto/kt-math/commit/9b109e624a188805ba22b1201cc08065ac35d4cc))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.154 ([a681e6e](https://github.com/gciatto/kt-math/commit/a681e6e086634a310d9bd4d0e425796f2e82915b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.155 ([cc10882](https://github.com/gciatto/kt-math/commit/cc10882bb2e9bcd5d630ae127214ae1457ce990b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.156 ([649fc55](https://github.com/gciatto/kt-math/commit/649fc558e3d2cc7e41d3e355fc6efc60b80b074a))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.157 ([93f31b0](https://github.com/gciatto/kt-math/commit/93f31b0a11045596195c818fd68e5bdbfeaed5c7))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.158 ([#521](https://github.com/gciatto/kt-math/issues/521)) ([b867f2f](https://github.com/gciatto/kt-math/commit/b867f2ff071b74f79c04175d4c247503f2f1658e))
* **deps:** update gradle to v9.1.0 ([dc4fe78](https://github.com/gciatto/kt-math/commit/dc4fe7860587f65e4f2abd4ce4b5138aa0390e53))
* **deps:** update gradle to v9.2.0 ([fb6c60f](https://github.com/gciatto/kt-math/commit/fb6c60f2218593e0c2e6e2c00fd896dbf0b700c4))
* **deps:** update gradle to v9.2.1 ([a669ae8](https://github.com/gciatto/kt-math/commit/a669ae89d37ea39adc25c3c31cf2bffd869a82d4))
* **deps:** update gradle to v9.3.0 ([641ed88](https://github.com/gciatto/kt-math/commit/641ed88317fcbe1bbf9b758cc1e961d6099dfa5d))
* **deps:** update gradle to v9.3.1 ([8e6af3f](https://github.com/gciatto/kt-math/commit/8e6af3f89c69d4573cdde2e3e86b10dba5f65934))
* **deps:** update gradle to v9.4.0 ([524db68](https://github.com/gciatto/kt-math/commit/524db68ae3e9655ef050f269c44a0ca8786bbf4d))
* **deps:** update gradle to v9.4.1 ([948c2b5](https://github.com/gciatto/kt-math/commit/948c2b5325c00968fd5d92090be6184933eca193))
* **deps:** update gradle to v9.5.0 ([3a55353](https://github.com/gciatto/kt-math/commit/3a55353736f01638e108af2faf9e9a79ada37115))
* **deps:** update gradle to v9.5.1 ([c923eb1](https://github.com/gciatto/kt-math/commit/c923eb1a0452e7c0cb40e005b5e06b7211d6e9cf))
* **deps:** update gradle to v9.6.0 ([2286b83](https://github.com/gciatto/kt-math/commit/2286b8383cca16b6102dfab710243e4cf5789b92))
* **deps:** update gradle to v9.6.1 ([5d2dfe0](https://github.com/gciatto/kt-math/commit/5d2dfe0c0e6a8db43246818f607a4a7762b17550))
* **deps:** update gradle to v9.7.0 ([c931bae](https://github.com/gciatto/kt-math/commit/c931baee30cafed16154524f4529c22ba73e5434))
* **deps:** update gradle to v9.7.1 ([dff28d5](https://github.com/gciatto/kt-math/commit/dff28d5db1475588f0559595437052f0dd3c991e))
* **deps:** update gradle to v9.8.0 ([6ade562](https://github.com/gciatto/kt-math/commit/6ade562b2986775cbdc6f91069133de7af3a0ab3))
* **deps:** update gradle to v9.8.1 ([a6bf1f3](https://github.com/gciatto/kt-math/commit/a6bf1f33b03bc55452076794116cede25ae50dcd))
* **deps:** update gradle to v9.8.2 ([65eb985](https://github.com/gciatto/kt-math/commit/65eb985c84ac38dd383674f5b5967cf19a129d1b))
* **deps:** update ktmpp to v5.1.5 ([15bbde9](https://github.com/gciatto/kt-math/commit/15bbde9472c90790deefbedd4c01eec434eb724b))
* **deps:** update node.js to 22.20 ([2221c64](https://github.com/gciatto/kt-math/commit/2221c640605a4c19da23c0fbfa9b4ced1c1ef656))
* **deps:** update node.js to 22.21 ([c606275](https://github.com/gciatto/kt-math/commit/c60627586d89398dccf0017773535011aa9b80b2))
* **deps:** update node.js to 24.12 ([23ec5d5](https://github.com/gciatto/kt-math/commit/23ec5d5000cbdd8120a86678bc75f1414ffdf176))
* **deps:** update node.js to 24.13 ([e670e35](https://github.com/gciatto/kt-math/commit/e670e35679bba15dd5d5097e71d143048d2185d0))
* **deps:** update node.js to 24.14 ([f7f95f5](https://github.com/gciatto/kt-math/commit/f7f95f508d56c1e76949b7061942715658b78dd8))
* **deps:** update node.js to 24.15 ([6d2209e](https://github.com/gciatto/kt-math/commit/6d2209ea4e11aaa93c75a38cb4b715ca1e214776))
* **deps:** update node.js to 24.16 ([8514a36](https://github.com/gciatto/kt-math/commit/8514a363ce2c58e3c4cd8fa8766abdbd050a55d2))
* **deps:** update node.js to 24.17 ([59bc22f](https://github.com/gciatto/kt-math/commit/59bc22f5f121fa1cc4fcdbcddc275a21b8457b68))
* **deps:** update node.js to 24.18 ([34f825c](https://github.com/gciatto/kt-math/commit/34f825ce0f9abc698d6d54690a14837e9d35f413))
* **deps:** update node.js to 24.19 ([e56661f](https://github.com/gciatto/kt-math/commit/e56661f53cf1bcd89ba4ec76ce2a3ebb207a9a05))
* **deps:** update node.js to 24.20 ([3746ef8](https://github.com/gciatto/kt-math/commit/3746ef873139ad79e6f126193e45225e2926cbf9))
* **deps:** update node.js to 24.21 ([93ad140](https://github.com/gciatto/kt-math/commit/93ad140a9fd14e90978194d15a8e686452c7b5f4))
* **deps:** update node.js to 24.22 ([2aa4331](https://github.com/gciatto/kt-math/commit/2aa4331ca0ab3770567ac5294aa839e24b356ac1))
* **deps:** update node.js to v24 ([f4ae90c](https://github.com/gciatto/kt-math/commit/f4ae90c5a57f6d1bd7b4ca002788772cd662781b))
* **deps:** update node.js to v26 ([#503](https://github.com/gciatto/kt-math/issues/503)) ([1f943cb](https://github.com/gciatto/kt-math/commit/1f943cba9b5ef4ae1977c1d4e3ad680ead8d492c))
* **deps:** update plugin com.gradle.develocity to v4.2.1 ([a53d830](https://github.com/gciatto/kt-math/commit/a53d8302f09784ce5212ac1d3717e0e822eca6ae))
* **deps:** update plugin com.gradle.develocity to v4.2.2 ([3844c3b](https://github.com/gciatto/kt-math/commit/3844c3b036f45c20f49e244e154d3b9119068335))
* **deps:** update plugin com.gradle.develocity to v4.3 ([4904579](https://github.com/gciatto/kt-math/commit/49045798a9355035a2eab4016344e6ad1b281f05))
* **deps:** update plugin com.gradle.develocity to v4.3.1 ([889d663](https://github.com/gciatto/kt-math/commit/889d663b6a8a6f72222fb7e441b2fce6fd05c09c))
* **deps:** update plugin com.gradle.develocity to v4.3.2 ([595db7e](https://github.com/gciatto/kt-math/commit/595db7e1ebda87c532c8df4ab77c313b632959c2))
* **deps:** update plugin com.gradle.develocity to v4.4.0 ([cc26343](https://github.com/gciatto/kt-math/commit/cc2634339f1548f5b58d4cd29862ec151c65b537))
* **deps:** update plugin com.gradle.develocity to v4.4.1 ([5adf094](https://github.com/gciatto/kt-math/commit/5adf0947efac04f3ca602c0b4756828b03830842))
* **deps:** update plugin com.gradle.develocity to v4.4.2 ([7d50345](https://github.com/gciatto/kt-math/commit/7d503450d7643b1ebdbb5980f7f7e2fa9cb91235))
* **deps:** update plugin com.gradle.develocity to v4.4.3 ([76ae5b4](https://github.com/gciatto/kt-math/commit/76ae5b40730cf998634e18f8a05f5101c9e124c3))
* **deps:** update plugin com.gradle.develocity to v4.5.0 ([2b870d5](https://github.com/gciatto/kt-math/commit/2b870d5bb13d5c40afb00c0d9535db20216fe8ef))
* **deps:** update plugin com.gradle.develocity to v4.5.1 ([2393af6](https://github.com/gciatto/kt-math/commit/2393af6a37ce916c2d9c00411559aa341c7b1d64))
* **deps:** update plugin com.gradle.develocity to v4.6.0 ([c7058bd](https://github.com/gciatto/kt-math/commit/c7058bd8dccbd9f3710c9b5006178a95113e3b77))
* **deps:** update plugin com.gradle.develocity to v4.6.1 ([c4ff0f1](https://github.com/gciatto/kt-math/commit/c4ff0f12a28b807d71e7fdd25d5a9f7276041bdc))
* **deps:** update plugin gitsemver to v7.0.10 ([b9ac30b](https://github.com/gciatto/kt-math/commit/b9ac30ba9b37052c36e0936f370db0d28d88b70d))
* **deps:** update plugin gitsemver to v7.0.11 ([496f731](https://github.com/gciatto/kt-math/commit/496f731753410276d992a3d0c850d6821fbfd850))
* **deps:** update plugin gitsemver to v7.0.12 ([d7743d6](https://github.com/gciatto/kt-math/commit/d7743d66b60f7f78523803ad6758b59c61e896f4))
* **deps:** update plugin gitsemver to v7.0.13 ([440f2b1](https://github.com/gciatto/kt-math/commit/440f2b176dc1aab668772843c9de8a0791bab8bb))
* **deps:** update plugin gitsemver to v7.0.15 ([fe38d82](https://github.com/gciatto/kt-math/commit/fe38d82f0ca222f1f783598d390ede13200de0bf))
* **deps:** update plugin gitsemver to v7.0.16 ([#424](https://github.com/gciatto/kt-math/issues/424)) ([11eb940](https://github.com/gciatto/kt-math/commit/11eb94091c9176224e333c8ca1b5752a3ee81692))
* **deps:** update plugin gitsemver to v7.0.17 ([64a24cd](https://github.com/gciatto/kt-math/commit/64a24cd6758733b49a7ef790cadc1a79f4cf76f2))
* **deps:** update plugin gitsemver to v7.0.18 ([da285e3](https://github.com/gciatto/kt-math/commit/da285e338d4038a901db37e610c937912cef0ba6))
* **deps:** update plugin gitsemver to v7.0.19 ([4aa33df](https://github.com/gciatto/kt-math/commit/4aa33df43ba165e3ddb4d01a044844685aaeed3c))
* **deps:** update plugin gitsemver to v7.0.20 ([b913db7](https://github.com/gciatto/kt-math/commit/b913db70c26d73e9697056f3c66fb844927fbf1d))
* **deps:** update plugin gitsemver to v7.0.22 ([e1302bd](https://github.com/gciatto/kt-math/commit/e1302bd981208b9b4aabec01a84320a9d7bbff1d))
* **deps:** update plugin gitsemver to v7.0.23 ([1ac9955](https://github.com/gciatto/kt-math/commit/1ac995501e3573882c33978232802d2a429b8682))
* **deps:** update plugin gitsemver to v7.0.24 ([a151d2a](https://github.com/gciatto/kt-math/commit/a151d2abbd760b2b096f1a21d28dbc2a7b982ddb))
* **deps:** update plugin gitsemver to v7.0.25 ([3b66790](https://github.com/gciatto/kt-math/commit/3b6679002e1d9bc4e621f912cdf7f8fcf147c858))
* **deps:** update plugin gitsemver to v7.0.4 ([df929fe](https://github.com/gciatto/kt-math/commit/df929fe1150f633beb147f877e83ae264d455a2e))
* **deps:** update plugin gitsemver to v7.0.5 ([acafbe2](https://github.com/gciatto/kt-math/commit/acafbe21aacc2eaa3350243c1dc4bc4bae4fa1d4))
* **deps:** update plugin gitsemver to v7.0.6 ([30c48ae](https://github.com/gciatto/kt-math/commit/30c48ae7663fa7664223dc2f24ce64e814d41ee8))
* **deps:** update plugin gitsemver to v7.0.7 ([50e4b04](https://github.com/gciatto/kt-math/commit/50e4b04f64f9887f08fb88a2bcf149ba0fccaf25))
* **deps:** update plugin gitsemver to v7.0.8 ([294adb9](https://github.com/gciatto/kt-math/commit/294adb9111abc0cde86e96677dc7db8c1db00166))
* **deps:** update plugin gitsemver to v7.0.9 ([057a0c9](https://github.com/gciatto/kt-math/commit/057a0c9eb15a9bd1f0fa19eea96366c5da31be67))
* **deps:** update plugin gitsemver to v7.2.0 ([a1e87f7](https://github.com/gciatto/kt-math/commit/a1e87f74d1f7c6c1f92b28a433fa18a73e3d60c6))
* **deps:** update plugin gitsemver to v7.3.0 ([e50efbc](https://github.com/gciatto/kt-math/commit/e50efbcce6c706d77c903216a068f0bc0e9fb72d))
* **deps:** update plugin gitsemver to v7.3.3 ([#522](https://github.com/gciatto/kt-math/issues/522)) ([12f07e6](https://github.com/gciatto/kt-math/commit/12f07e60049326a9e2515a6a11af227ab7eb8aab))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.10 ([6698ee5](https://github.com/gciatto/kt-math/commit/6698ee55f33a5e888b4b4db178252531cb716ad5))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.11 ([c94f773](https://github.com/gciatto/kt-math/commit/c94f7736b69b5d83c3a685f83dc44f702c246549))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.12 ([acaa08d](https://github.com/gciatto/kt-math/commit/acaa08d581c5087270fde65df1c2d08699e85a74))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.13 ([e6aabaa](https://github.com/gciatto/kt-math/commit/e6aabaadcfb6b41183ccdab4c043b121d6af9de1))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.14 ([ed6e35a](https://github.com/gciatto/kt-math/commit/ed6e35a7e00627c4625f47607cfa20d03999203e))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.15 ([e76b7e9](https://github.com/gciatto/kt-math/commit/e76b7e9e0834557bf0b5922b7fd554c14c7bfd34))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.16 ([5ab03a6](https://github.com/gciatto/kt-math/commit/5ab03a6b5e5918250e435cbca2c8330bbdee123a))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.17 ([76151eb](https://github.com/gciatto/kt-math/commit/76151eb3c4399a45f1559af80a819bdb1c09238a))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.18 ([6ddc82e](https://github.com/gciatto/kt-math/commit/6ddc82e90047379bd2f75a70442208dafb53bd31))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.19 ([fbfdbff](https://github.com/gciatto/kt-math/commit/fbfdbffb9f73beb04dec3b235eb972a912a53100))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.2 ([298dac8](https://github.com/gciatto/kt-math/commit/298dac867536ae443d2b66fd003a8ec28d6d9e4f))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.20 ([66fb625](https://github.com/gciatto/kt-math/commit/66fb62596eb49dcfb68b81df9fa8f686c3adb271))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.21 ([9dfa486](https://github.com/gciatto/kt-math/commit/9dfa4864eb7df3e3edcd6d19da6ed1b7fb9e11b0))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.22 ([a1ed80d](https://github.com/gciatto/kt-math/commit/a1ed80ddfae87f6027c41090824604e78778194f))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.23 ([5c527b5](https://github.com/gciatto/kt-math/commit/5c527b5764851f0b81d9fb9be774bfb387424845))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.24 ([302109d](https://github.com/gciatto/kt-math/commit/302109d6861157ac663fe458e8eb54e1ca4692b2))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.25 ([ecef43e](https://github.com/gciatto/kt-math/commit/ecef43e80363efba7b781999ad750624ac35fa2b))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.26 ([a443836](https://github.com/gciatto/kt-math/commit/a443836ab4b44c3c6c1572dc29855533de0f4fef))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.27 ([61d96d6](https://github.com/gciatto/kt-math/commit/61d96d641dd988877e53a50e51348ab616fa136e))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.28 ([#523](https://github.com/gciatto/kt-math/issues/523)) ([39b7c79](https://github.com/gciatto/kt-math/commit/39b7c79e63efcb2f1ff6a3ff390ffc30f540da72))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.3 ([91e9ff7](https://github.com/gciatto/kt-math/commit/91e9ff71577ada99aaae9807f6e36ebcc7010885))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.4 ([5cafe0a](https://github.com/gciatto/kt-math/commit/5cafe0a4074c91f1eac3ef0ce277b1b70e30998b))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.5 ([2103b87](https://github.com/gciatto/kt-math/commit/2103b87f6792f904af605df07a5da7da940893e8))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.6 ([8154aa1](https://github.com/gciatto/kt-math/commit/8154aa1c131da0897934016619b5596a05e2ae68))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.7 ([5fef888](https://github.com/gciatto/kt-math/commit/5fef888227a4a33926150849bcc9eebc5512f377))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.8 ([cf522e8](https://github.com/gciatto/kt-math/commit/cf522e8e7838d96115210f2a3f39df2cee57704f))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.1.9 ([0ad7817](https://github.com/gciatto/kt-math/commit/0ad7817596fd58579700cf1892d1e8dcff3588bb))

### Bug Fixes

* BigDecimal sqrt and string-parsing divergences from the JDK ([43002f8](https://github.com/gciatto/kt-math/commit/43002f88aac6792055c04a348aca1453af09e4b0))
* division-by-zero and algorithm-choice divergences from the JDK ([f697c48](https://github.com/gciatto/kt-math/commit/f697c483a5a7e0753b4eb1a353551dbea4428c52))
* Java and JS usability of the API ([6223a1e](https://github.com/gciatto/kt-math/commit/6223a1e64e3f5dfd9633c86d62d39aae1468894c))
* **js:** bigInteger() of non-integer, NaN and infinite numbers ([ee9a620](https://github.com/gciatto/kt-math/commit/ee9a620278b9c767ef3e6749fb682602e01f71ea))
* **js:** export BigFactory.default under its own name ([8a2392c](https://github.com/gciatto/kt-math/commit/8a2392c40a6baa766e36f4b5b3e709898b927b82))
* **js:** remainder and ofSignMagnitudeSlice JS names; BigDecimal.of with a precision is internal ([4a93079](https://github.com/gciatto/kt-math/commit/4a93079b499a59657829a53368d26e5ac5109673))
* make BigInteger rem (%) the remainder on every platform ([7268639](https://github.com/gciatto/kt-math/commit/72686399460f83e4249a22b15f32e457909f3309))
* port bugs in the common BigInteger/BigDecimal implementation ([6065061](https://github.com/gciatto/kt-math/commit/606506194a1e7c8d8ee4b87f01405934db1571a5))
* radix prefixes only at the start of BigInteger strings, and other API corrections ([6da0333](https://github.com/gciatto/kt-math/commit/6da0333b3900275ac163f5ed622d15b1dd5a2c51))
* run on Java 8 again ([7efbe35](https://github.com/gciatto/kt-math/commit/7efbe356251c37e4cfa808e83e8f1dc48f682100))

### Documentation

* document Java 8 compatibility in AGENTS.md ([7bf1599](https://github.com/gciatto/kt-math/commit/7bf15996a64ed6db6dc424134af02c49a5ff99c5))
* document native targets in AGENTS.md ([1f899e8](https://github.com/gciatto/kt-math/commit/1f899e8fc1003ee1544656576e0dabc8cdb70492))
* document the big-math module in AGENTS.md ([f8668be](https://github.com/gciatto/kt-math/commit/f8668befc84c0622afdc3752bdc57e61ae3e4319))
* document the JS differential tests and JS pitfalls in AGENTS.md ([60f5b34](https://github.com/gciatto/kt-math/commit/60f5b34d355f6718610612b9ac8b69476c506f29))
* document the testing approach and port bug patterns in AGENTS.md ([9d171f6](https://github.com/gciatto/kt-math/commit/9d171f676d94b2ed369f5937f4e0a09885614f05))
* KDoc cleanup, and a documentation website on GitHub Pages ([87901c1](https://github.com/gciatto/kt-math/commit/87901c1b04340c3ff0a717f06cdfae59e9ea834d))
* note JDK-version-dependent cases in the differential tests ([673e62e](https://github.com/gciatto/kt-math/commit/673e62e1b6f91f44493b965b667d0cc2d89dd9cb))
* README with badges, purpose, features, platforms and benchmarks ([cbefaa4](https://github.com/gciatto/kt-math/commit/cbefaa41f17775939fb08cbbb24817472589688b))

### Performance improvements

* BigDecimal on top of the BigInteger interface, native BigInts on JS ([9c23db6](https://github.com/gciatto/kt-math/commit/9c23db68d324c2021f1b5b598f4ac19d7b726429))
* BigInteger on JS backed by native BigInts ([bf422f5](https://github.com/gciatto/kt-math/commit/bf422f57e03888743e48746462c057d3cea620b8))

### Tests

* BigFactory and the conversions, on every platform ([7ad73e9](https://github.com/gciatto/kt-math/commit/7ad73e911f34242c104a40d6580e2a52c9fd6df0))
* build the Kotlin BigDecimals of the JVM tests from BigInteger.of ([512cd44](https://github.com/gciatto/kt-math/commit/512cd441772c45728ab4a9ff146dcf2745dd5e42))
* differential tests of BigDecimal.div with scale and rounding mode ([1ab01be](https://github.com/gciatto/kt-math/commit/1ab01befb24842d273884c384dec87d19c3799e6))
* differential tests of kt-math-big-math against big-math, known values on every platform ([5eab116](https://github.com/gciatto/kt-math/commit/5eab11636710c889eb7b6072ffa567fdd45a76d0))
* differential tests of the common implementation against java.math ([8e9f99d](https://github.com/gciatto/kt-math/commit/8e9f99d1237b9f51c3517c78918126aa27e59cb3))
* extend the differential tests against java.math ([21fb495](https://github.com/gciatto/kt-math/commit/21fb495cf27fd69ad61d19ece91213da3155bed6))
* **js:** check that JS BigIntegers are native BigInts, document JsBigInteger ([c4f46b5](https://github.com/gciatto/kt-math/commit/c4f46b5f9940a4e38df9d9c312f608c5c0fe343e))
* **js:** cover the bigInteger and bigDecimal JS entry points ([2754c69](https://github.com/gciatto/kt-math/commit/2754c69884157d71b1849c93bb57c2259fb3630a))
* pass the differential cases path to the Apple simulator tests ([6addaa4](https://github.com/gciatto/kt-math/commit/6addaa43db12609f762e9ccd8363bddb039c025b))
* radix prefixes, empty int arrays, invalid rounding mode codes ([02ab985](https://github.com/gciatto/kt-math/commit/02ab9856bbbb879bd5f7217d777c23afd6a85c37))
* run the differential cases against java.math on JS too ([82a17f8](https://github.com/gciatto/kt-math/commit/82a17f826d4dbf4eebbdd513a34dad9bdedbece4))
* run the JVM tests on Java 8 in CI ([6adc43b](https://github.com/gciatto/kt-math/commit/6adc43b5639676b72977e464f6ce8fb654b93879))
* run the tests on native targets, differential cases included ([f814fba](https://github.com/gciatto/kt-math/commit/f814fba3f04a8268ddfa5cddd71f88b55b5e1e47))
* usage tests in Java, plain JavaScript and C ([4e2fae2](https://github.com/gciatto/kt-math/commit/4e2fae2b1a18d2d005b8990b22cddd7e2603b176))

### Build and continuous integration

* align CI, Renovate, kt-mpp and semantic-release with 2p-kt; enforce detekt; add Kover ([#504](https://github.com/gciatto/kt-math/issues/504)) ([de530a4](https://github.com/gciatto/kt-math/commit/de530a47e27fa924a5bbb4f847352ba71ea351d7)), closes [DanySK/publish-on-central#1804](https://github.com/DanySK/publish-on-central/issues/1804)
* build every klib with assemble instead of an unsigned local publish ([e7eb971](https://github.com/gciatto/kt-math/commit/e7eb971ddb3afc77d269f762c378d75882b03be2))
* **deps:** update actions/checkout action to v5.0.1 ([3b2fa4a](https://github.com/gciatto/kt-math/commit/3b2fa4a57447ec060afbb2a1d2c102d0920e2e17))
* **deps:** update actions/checkout action to v6 ([5a3082e](https://github.com/gciatto/kt-math/commit/5a3082e7a1393ecb3626320347a9f8176a3ebe48))
* **deps:** update actions/checkout action to v6.0.1 ([96708f5](https://github.com/gciatto/kt-math/commit/96708f55003e8f5ba37e6dc65dae83347192bd75))
* **deps:** update actions/checkout action to v6.0.2 ([9b25a85](https://github.com/gciatto/kt-math/commit/9b25a85fd47a46e982df2b1de3fb4d1af9d04648))
* **deps:** update actions/checkout action to v6.0.3 ([2eedc40](https://github.com/gciatto/kt-math/commit/2eedc409cee33755bb33e33462417d0d66b5ba12))
* **deps:** update actions/checkout action to v7 ([f5926d0](https://github.com/gciatto/kt-math/commit/f5926d0759c0b1492efac9b2792103484c71435b))
* **deps:** update actions/checkout action to v7.0.1 ([cce6daa](https://github.com/gciatto/kt-math/commit/cce6daaa924b2947a05f45a1a41f1069b1e02634))
* **deps:** update actions/setup-node action to v6 ([659a725](https://github.com/gciatto/kt-math/commit/659a7251ce3095ca094a6ea0023bae8cccd4eaa6))
* **deps:** update actions/setup-node action to v6.1.0 ([96e63de](https://github.com/gciatto/kt-math/commit/96e63dec7ae123e9075be4129d3f06fb17271a67))
* **deps:** update actions/setup-node action to v6.2.0 ([bb4a557](https://github.com/gciatto/kt-math/commit/bb4a557a6d5e11e1bd34eab14e4948e3978abeef))
* **deps:** update actions/setup-node action to v6.3.0 ([0389372](https://github.com/gciatto/kt-math/commit/038937268db0f033f27e4572e57ff4b8847c2825))
* **deps:** update actions/setup-node action to v6.4.0 ([5e8e40e](https://github.com/gciatto/kt-math/commit/5e8e40e23abb6e1e23813c7b16ae38d80f2e8f81))
* **deps:** update actions/setup-node action to v6.5.0 ([9c91dfa](https://github.com/gciatto/kt-math/commit/9c91dfa171c097fd05ed0d8ca7673aca6c8c9cd7))
* **deps:** update actions/setup-node action to v7 ([d360e24](https://github.com/gciatto/kt-math/commit/d360e24099be12abe7d75e7cbf7c2f0dd6d6aee7))
* **deps:** update actions/setup-node action to v7.1.0 ([6df520b](https://github.com/gciatto/kt-math/commit/6df520bd5bcbde99e88d7e11b200a0d7fd968d85))
* **deps:** update danysk/action-checkout action to v0.2.26 ([3fe768d](https://github.com/gciatto/kt-math/commit/3fe768dacc574abfba0ba0aeff9c6b69dda82743))
* **deps:** update danysk/action-checkout action to v0.2.27 ([557bd4f](https://github.com/gciatto/kt-math/commit/557bd4f7bb23bf2b618e807a5dca583795afc2cd))
* **deps:** update danysk/action-checkout action to v0.2.28 ([5c5dd93](https://github.com/gciatto/kt-math/commit/5c5dd933c090e64545a97a289f6f521086d57ee0))
* **deps:** update danysk/action-checkout action to v0.2.29 ([a50b740](https://github.com/gciatto/kt-math/commit/a50b740724a0f6d2145927071265a953f29f4279))
* **deps:** update danysk/action-checkout action to v0.2.30 ([66a34ae](https://github.com/gciatto/kt-math/commit/66a34ae72088e8ec8210584bf852af65a8977671))
* **deps:** update danysk/action-checkout action to v0.2.31 ([f4b36f1](https://github.com/gciatto/kt-math/commit/f4b36f15885b18979a0d6b490c0609b9a6494321))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.10 ([e5fa8d8](https://github.com/gciatto/kt-math/commit/e5fa8d85f55baa65ff1e1a57628869efbb02b90c))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.11 ([56dd1e4](https://github.com/gciatto/kt-math/commit/56dd1e451e7eca92ccf231f485661277ebac873a))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.12 ([7ab216c](https://github.com/gciatto/kt-math/commit/7ab216c04409ae1429ef0e24d9d94c3621ac1fcb))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.13 ([c2e5a27](https://github.com/gciatto/kt-math/commit/c2e5a275c81f5fb4158c8da6b969dbd78a7f4b94))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.14 ([bdb3e01](https://github.com/gciatto/kt-math/commit/bdb3e01781ad78d9357cc140486ec5cc628b8a2d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.15 ([13d4cad](https://github.com/gciatto/kt-math/commit/13d4cad4989437bede80492e63011a777b134b77))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.17 ([5d2fc05](https://github.com/gciatto/kt-math/commit/5d2fc05e5d7e5548f6e87dfae0dd6da540fe70d6))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.18 ([7918916](https://github.com/gciatto/kt-math/commit/7918916f70d3dbc499020cf966ab36c7f27025ce))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.19 ([5f16d4c](https://github.com/gciatto/kt-math/commit/5f16d4c48072bdd01456a296ea0e276d8223d63d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.20 ([c71f36b](https://github.com/gciatto/kt-math/commit/c71f36b3017b4a9d1fe941eebdbadcd76b625aef))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.21 ([9c1c15d](https://github.com/gciatto/kt-math/commit/9c1c15d0976d8b9b51eed94dce48762b16eaba3d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.22 ([3f2d621](https://github.com/gciatto/kt-math/commit/3f2d621877293cbc2878fe81b6116cb517b7aee8))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.23 ([288ccea](https://github.com/gciatto/kt-math/commit/288cceafeef941c8c01a511ac8db7249e9f0e80d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.24 ([e88404b](https://github.com/gciatto/kt-math/commit/e88404b5346f52cc4afadc476f762b55ad63c692))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.25 ([c792b4b](https://github.com/gciatto/kt-math/commit/c792b4b397b819eb4a4e86df87edb88524a79ab1))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.26 ([d402c37](https://github.com/gciatto/kt-math/commit/d402c37e06407cc97d0ab0d9d76a570d8c66a729))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.27 ([7d22c45](https://github.com/gciatto/kt-math/commit/7d22c45b14e57c2a4bd80772da43b1038154c2f4))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.28 ([c65eec5](https://github.com/gciatto/kt-math/commit/c65eec5ee4135b02cc32506eb28bfc16c0cfce09))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.29 ([b5ddd02](https://github.com/gciatto/kt-math/commit/b5ddd02cc45968b83057ca8a0a201a0a58eb94f5))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.30 ([ac228ea](https://github.com/gciatto/kt-math/commit/ac228ea7abbf216998bc7f3c059c328cc9889e86))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.31 ([542f41e](https://github.com/gciatto/kt-math/commit/542f41e3d436c976dcac825ff8e15a3cbc3984da))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.32 ([960df5a](https://github.com/gciatto/kt-math/commit/960df5a7518303b9516e133e912d2845e0feee0e))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.33 ([5126776](https://github.com/gciatto/kt-math/commit/5126776db2b7b18307d32a25315381eb1fa90929))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.34 ([1abafc4](https://github.com/gciatto/kt-math/commit/1abafc41d8c07243c0822d9c12811db0372419bd))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.35 ([1810dd1](https://github.com/gciatto/kt-math/commit/1810dd11b4a2f0a110595ee47ab6bb6086f0906f))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.36 ([c08358e](https://github.com/gciatto/kt-math/commit/c08358e687bec9978b093c68e28cff94ad6df33c))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.37 ([0726d43](https://github.com/gciatto/kt-math/commit/0726d436e0bed0305df6a28c4ac3587d3198336e))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.38 ([16416a0](https://github.com/gciatto/kt-math/commit/16416a0db3b7fbc3786b351f52d277cfd5cc0c1f))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.39 ([8cb97b3](https://github.com/gciatto/kt-math/commit/8cb97b3a9c84d4b186bdd487b59a82d034788544))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.40 ([1c3d46f](https://github.com/gciatto/kt-math/commit/1c3d46ffcc6f2b5f4c1a08b17879d8a109d80341))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.41 ([2767045](https://github.com/gciatto/kt-math/commit/2767045f43f871c2c547e3eeeccaf185574f42e9))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.42 ([87fabd4](https://github.com/gciatto/kt-math/commit/87fabd41cdf14041f1e17f68e6030797531e0597))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.43 ([bc5136b](https://github.com/gciatto/kt-math/commit/bc5136bcc9e3be8fbed6dfe58f3d7e2de55047ae))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.8 ([7f1f4d8](https://github.com/gciatto/kt-math/commit/7f1f4d8edd29c18fba5bc2130ff457689b4df050))
* **deps:** update danysk/build-check-deploy-gradle-action action to v4.0.9 ([e5dd430](https://github.com/gciatto/kt-math/commit/e5dd43027e53a55945b448e4c17b8199a4fea786))
* **deps:** update dependency ubuntu github actions runner to v26 ([9395186](https://github.com/gciatto/kt-math/commit/9395186dfcfada8a027b5128944248ee97405b30))
* keep the nonJvm source-set group flat ([2b156c3](https://github.com/gciatto/kt-math/commit/2b156c31cefc9be9671c97015a590026b277e78d))

### Refactoring

* merge the big-math subproject into the root project ([5d9954d](https://github.com/gciatto/kt-math/commit/5d9954d5e062736f6f5e8599afcf8416291c148b))
* one top-level type per file ([877e9cb](https://github.com/gciatto/kt-math/commit/877e9cb7d19afd842e5bbfc33fe69a0ec550698c))

## [0.10.2](https://github.com/gciatto/kt-math/compare/0.10.1...0.10.2) (2025-09-17)

### Bug Fixes

* **ci:** npm parameter ([e6c8a3f](https://github.com/gciatto/kt-math/commit/e6c8a3f0100a5994251b4f5053b083a029aee5de))

## [0.10.1](https://github.com/gciatto/kt-math/compare/0.10.0...0.10.1) (2025-09-17)

### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlin:kotlin-reflect to v1.9.23 ([51e3b3d](https://github.com/gciatto/kt-math/commit/51e3b3d711444a620899f9dc62a46dc6b434ce26))
* **core-deps:** update dependency org.jetbrains.kotlin:kotlin-reflect to v1.9.24 ([1cd5dee](https://github.com/gciatto/kt-math/commit/1cd5dee0c1e2fadf490fcd5a504362ca0255f997))
* **deps:** general upgrade ([51397d1](https://github.com/gciatto/kt-math/commit/51397d111099fe5c69f291a5ea68db3b7596eb3a))
* **deps:** update dependency gradle to v8.6 ([83546b0](https://github.com/gciatto/kt-math/commit/83546b000e1f17786768945c4e67e79e4c649699))
* **deps:** update dependency gradle to v8.7 ([53f9d18](https://github.com/gciatto/kt-math/commit/53f9d18d25656fdede524e06902493bcf48e38c3))
* **deps:** update dependency gradle to v8.8 ([d45ff58](https://github.com/gciatto/kt-math/commit/d45ff588d6c241c08d544c26b7d1028c044a35c9))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.145 ([6097217](https://github.com/gciatto/kt-math/commit/60972179fefe1599ca2cfda7fceca8fa235da9d6))
* **deps:** update ktmpp to v4.1.1 ([53cca2f](https://github.com/gciatto/kt-math/commit/53cca2f53eb7877b25a744b3c82c49ec7f47b3de))
* **deps:** update ktmpp to v4.1.2 ([81cb4bf](https://github.com/gciatto/kt-math/commit/81cb4bf32efb10ee3201b143bf75a8727e045ecc))
* **deps:** update ktmpp to v4.1.3 ([adcd7c8](https://github.com/gciatto/kt-math/commit/adcd7c8c54238339c5cc63bb5e9217e527e175f3))
* **deps:** update ktmpp to v4.1.4 ([ec77e39](https://github.com/gciatto/kt-math/commit/ec77e39e48462ea80d7b351941573e51b173e773))
* **deps:** update ktmpp to v5.1.4 ([7e0d897](https://github.com/gciatto/kt-math/commit/7e0d897d6a446d2902e92cb98ad486d5cd9e9d8c))
* **deps:** update node.js to 20.11 ([e7f2b61](https://github.com/gciatto/kt-math/commit/e7f2b61c025c6302fe8ce455b64389d706cb0db2))
* **deps:** update node.js to 20.12 ([aabfb28](https://github.com/gciatto/kt-math/commit/aabfb2899d64c6449d4749e242eb4fbe8e22e534))
* **deps:** update node.js to 20.13 ([188ee2e](https://github.com/gciatto/kt-math/commit/188ee2e5afa89ec0c06ad8fcc3f484883601de23))
* **deps:** update node.js to 20.14 ([4b12039](https://github.com/gciatto/kt-math/commit/4b120391ac191f4c00fcbfb9b357775c880b5ca3))
* **deps:** update plugin com.gradle.enterprise to v3.16.2 ([a8cd085](https://github.com/gciatto/kt-math/commit/a8cd085ce6d30fd24bdeaa455c9abf856ce7e2b1))
* **deps:** update plugin com.gradle.enterprise to v3.17 ([3203740](https://github.com/gciatto/kt-math/commit/3203740288c87b7839dded6fb1513f73093661d4))
* **deps:** update plugin com.gradle.enterprise to v3.17.1 ([6b506fd](https://github.com/gciatto/kt-math/commit/6b506fd6d9a41722f610b28af36bfc419b3b0a78))
* **deps:** update plugin com.gradle.enterprise to v3.17.2 ([14e82c0](https://github.com/gciatto/kt-math/commit/14e82c0e567cafbca67f92dfa81b401e27ed94db))
* **deps:** update plugin com.gradle.enterprise to v3.17.3 ([aa8e6c0](https://github.com/gciatto/kt-math/commit/aa8e6c0fac8ba04904f1e2222fcdaf2c92fadb20))
* **deps:** update plugin com.gradle.enterprise to v3.17.4 ([b0f5879](https://github.com/gciatto/kt-math/commit/b0f58794dc66caa1f89d802271c8c46015b82d74))
* **deps:** update plugin com.gradle.enterprise to v3.17.5 ([7c2cf3b](https://github.com/gciatto/kt-math/commit/7c2cf3bedec81578330ffd6238367f2aa560fa89))
* **deps:** update plugin gitsemver to v3 ([c6ad141](https://github.com/gciatto/kt-math/commit/c6ad1417b0c646554c36b95f853737ebf993d744))
* **deps:** update plugin gitsemver to v3.1.0 ([0a49f49](https://github.com/gciatto/kt-math/commit/0a49f4964adca5c06c5ba90760a78841a0c0249e))
* **deps:** update plugin gitsemver to v3.1.1 ([e2c660b](https://github.com/gciatto/kt-math/commit/e2c660b1e1359db0661417712c1c662aa009f86c))
* **deps:** update plugin gitsemver to v3.1.2 ([a94ebc0](https://github.com/gciatto/kt-math/commit/a94ebc0a5468474e69491f89c9d3f4d91ff132b6))
* **deps:** update plugin gitsemver to v3.1.3 ([3e3c184](https://github.com/gciatto/kt-math/commit/3e3c184fb8382beb3c2e88933ff0d540ab60df63))
* **deps:** update plugin gitsemver to v3.1.4 ([3831957](https://github.com/gciatto/kt-math/commit/3831957c5cb904f856ac98ba035286d205fc5b89))
* **deps:** update plugin gitsemver to v3.1.5 ([63358b7](https://github.com/gciatto/kt-math/commit/63358b73617b6715bfbc3de1843dbcfc2264f9e7))
* **deps:** update plugin gitsemver to v3.1.6 ([ce168c7](https://github.com/gciatto/kt-math/commit/ce168c741ebbca1d1b9f7a25627d0937d3bf37f7))
* **deps:** update plugin gitsemver to v3.1.7 ([468b3a1](https://github.com/gciatto/kt-math/commit/468b3a1d24df1bd9cfdd93f9d33ceff17fb4e684))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.18 ([050dff6](https://github.com/gciatto/kt-math/commit/050dff6e43e7070c0e55036c5afe09352b6e6562))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2 ([a26ceeb](https://github.com/gciatto/kt-math/commit/a26ceebfba2c0af350f1160cb12fcf0f987fb879))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.1 ([4ed02eb](https://github.com/gciatto/kt-math/commit/4ed02ebc292a696c74ccb0dd7411a514a598f79d))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.2 ([eb0f316](https://github.com/gciatto/kt-math/commit/eb0f3162b0c21e7b1847b203de800c4fba8ec462))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.3 ([32bc6b6](https://github.com/gciatto/kt-math/commit/32bc6b6117520e670d21f90e8a8389b4c146687f))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.4 ([fd20e9b](https://github.com/gciatto/kt-math/commit/fd20e9b9eacf959baeae66ce7168a2510c80c3f8))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.5 ([155ddd8](https://github.com/gciatto/kt-math/commit/155ddd87bfe4a590e14d8b6448f3cf4741e8e6d0))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.6 ([0b361ba](https://github.com/gciatto/kt-math/commit/0b361babddd9bd7a6515f147f9ce27666b2c2234))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.7 ([7e34e3d](https://github.com/gciatto/kt-math/commit/7e34e3d497b0eca4bd04bd10329505bc4fd8a03c))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.8.0 ([117dbd9](https://github.com/gciatto/kt-math/commit/117dbd9f87148086f9ba1018ddd8b25d912db3aa))

### Bug Fixes

* **ci:** runners versions ([556811f](https://github.com/gciatto/kt-math/commit/556811f538991607d962533958f555b6fb94c5da))

### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.2 ([903e70f](https://github.com/gciatto/kt-math/commit/903e70f0183c4a0dd5cd69ac25630d53593471a6))
* **deps:** update actions/checkout action to v4.1.3 ([72f7783](https://github.com/gciatto/kt-math/commit/72f7783a4e8883b8d4368fe2fddbd54178e6aa35))
* **deps:** update actions/checkout action to v4.1.4 ([558f9a7](https://github.com/gciatto/kt-math/commit/558f9a7cca38910e64a35d6eb7ab0fbac657371b))
* **deps:** update actions/checkout action to v4.1.5 ([ba79ffa](https://github.com/gciatto/kt-math/commit/ba79ffa24bc2da45ba9f9653b9f9141d212d4494))
* **deps:** update actions/checkout action to v4.1.6 ([4532e22](https://github.com/gciatto/kt-math/commit/4532e220c962056d75cb2228489f4b7bbb72a329))
* **deps:** update actions/checkout action to v4.1.7 ([0ec034e](https://github.com/gciatto/kt-math/commit/0ec034e44f4a41f76359f44ca2b74135f77861f5))
* **deps:** update actions/setup-node action to v4.0.2 ([dbb52e7](https://github.com/gciatto/kt-math/commit/dbb52e76ed1460671738996ed20337e6dcbaf5dc))
* **deps:** update danysk/action-checkout action to v0.2.15 ([277f626](https://github.com/gciatto/kt-math/commit/277f626f4bd3951515bbaf0651fdb7f3f14b86de))
* **deps:** update danysk/action-checkout action to v0.2.16 ([cca46cc](https://github.com/gciatto/kt-math/commit/cca46cc3f5dc6c138b7a73e780cadebc0d8a4320))
* **deps:** update danysk/action-checkout action to v0.2.17 ([d65f0ed](https://github.com/gciatto/kt-math/commit/d65f0ed5628c8ee16352cc32706835a453ed724f))
* **deps:** update danysk/action-checkout action to v0.2.18 ([bd992d0](https://github.com/gciatto/kt-math/commit/bd992d016062a314d896fe0d71e847b503860a52))
* **deps:** update danysk/action-checkout action to v0.2.19 ([8337b48](https://github.com/gciatto/kt-math/commit/8337b48f940d950ad5317c0b0dede3334a293bd8))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.13 ([523214f](https://github.com/gciatto/kt-math/commit/523214f2eae4db9f16969ef08bc6085286793beb))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.14 ([675ac5e](https://github.com/gciatto/kt-math/commit/675ac5ec4b102aad999929b699beda09fa696aaa))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.15 ([ead5365](https://github.com/gciatto/kt-math/commit/ead53656c710423b965a63ab90d013956970f0df))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.17 ([7db15c7](https://github.com/gciatto/kt-math/commit/7db15c7983b19f1d9f9434735eaa9c5c5d6223af))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.18 ([46230c2](https://github.com/gciatto/kt-math/commit/46230c2451987b3cad07f4ec8c810e9a1319bdcb))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.19 ([19a126d](https://github.com/gciatto/kt-math/commit/19a126d270be27202578bf5f95523b449eed3899))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.20 ([43b87d0](https://github.com/gciatto/kt-math/commit/43b87d0654fe9b74a566a5a878e07d9fe080cab4))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.0 ([93f4da6](https://github.com/gciatto/kt-math/commit/93f4da6422b2b9d46301d725e1a5f8620f2e4801))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.1 ([f7cea6b](https://github.com/gciatto/kt-math/commit/f7cea6b18d099d63a951e7ce3ad06dace72c2ac8))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.10 ([b456ab5](https://github.com/gciatto/kt-math/commit/b456ab5cb446fe35ea8e1dba5372edcf489faea0))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.11 ([b745ddb](https://github.com/gciatto/kt-math/commit/b745ddb39585d81ada80f0659fae2e3212e9f813))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.12 ([2b62e53](https://github.com/gciatto/kt-math/commit/2b62e53259250af5409b7dfd6a232158c41f0f35))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.13 ([b1925d4](https://github.com/gciatto/kt-math/commit/b1925d49e06ce3e5cc29d0273ea0cb9728e1c5ef))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.14 ([631e559](https://github.com/gciatto/kt-math/commit/631e559dfc8d4ffb341a2338468b2f3da5efc395))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.15 ([8904301](https://github.com/gciatto/kt-math/commit/890430194cae788688f17ec3f58368c98bd8a6de))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.16 ([486bacd](https://github.com/gciatto/kt-math/commit/486bacd09513ce55c60c2a8c3753d19fa9dd3ae0))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.17 ([efa94c8](https://github.com/gciatto/kt-math/commit/efa94c809db6bc9210c53dfc25d362810bc5d373))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.18 ([a6119f5](https://github.com/gciatto/kt-math/commit/a6119f5bae337f98ad12e336fa6bd7c9a2f47cb5))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.19 ([52ed9d1](https://github.com/gciatto/kt-math/commit/52ed9d15f74ada409c6e68dbe7e280b5cd6a1246))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.2 ([7dc3d0c](https://github.com/gciatto/kt-math/commit/7dc3d0cc01997badfff44d1f755c8b56ff389bf4))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.20 ([03400ca](https://github.com/gciatto/kt-math/commit/03400caeba37d087853fcd4766717d80f13ab322))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.21 ([95e3c89](https://github.com/gciatto/kt-math/commit/95e3c89931eb9cd8280516036676f7e88e04c178))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.22 ([da2f545](https://github.com/gciatto/kt-math/commit/da2f545f2abb9f9b604faa9d891363add733d661))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.3 ([54d429f](https://github.com/gciatto/kt-math/commit/54d429f45b721ba7a7785a83dd8e4d9b57a47ca9))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.4 ([9c86c5d](https://github.com/gciatto/kt-math/commit/9c86c5d5c1d9ad82c2d27d25e2ad2803bd320ed5))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.5 ([eea1289](https://github.com/gciatto/kt-math/commit/eea128920f94d896bb161837bdeba9154d42995f))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.6 ([e1a8717](https://github.com/gciatto/kt-math/commit/e1a87170e16c7c851e9a324537507601ce118520))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.7 ([d59108d](https://github.com/gciatto/kt-math/commit/d59108dbe48e8a1c6bc697cc1c1a1fa9870ed663))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.8 ([09d2243](https://github.com/gciatto/kt-math/commit/09d2243531cf9183d42bbff49ebb3deaa407b38e))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.9 ([243666e](https://github.com/gciatto/kt-math/commit/243666e03b692b51890b7019754ebf7e766bcb5d))
* **deps:** update dependency ubuntu github actions runner to v24 ([015be9c](https://github.com/gciatto/kt-math/commit/015be9c063c02294362f71af352286900db38fe2))

### General maintenance

* removed useless config ([7170e4d](https://github.com/gciatto/kt-math/commit/7170e4d45d19ec31bc67c1bc315ab422d0a31e44))

## [0.10.0](https://github.com/gciatto/kt-math/compare/0.9.0...0.10.0) (2024-01-02)


### Features

* user kt-mpp 4.0.3, kt 1.9.22 ([8aa438a](https://github.com/gciatto/kt-math/commit/8aa438a6609ce312770791a6a300cda33d2ed113))


### Dependency updates

* **deps:** update dependency gradle to v8.2.1 ([94a4621](https://github.com/gciatto/kt-math/commit/94a46215e7afe052e4951e411013dedc836f78d9))
* **deps:** update dependency gradle to v8.3 ([4b67697](https://github.com/gciatto/kt-math/commit/4b6769720d9af0b479d0732bbcd1097baaa9f3d5))
* **deps:** update dependency gradle to v8.4 ([4e07937](https://github.com/gciatto/kt-math/commit/4e0793738f8133db13a60c3786deb9429edbcd29))
* **deps:** update dependency gradle to v8.5 ([464ab67](https://github.com/gciatto/kt-math/commit/464ab67ff510b0cf7f1c330918647b37d1cbeaf6))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.39 ([ec7ec00](https://github.com/gciatto/kt-math/commit/ec7ec00eb6e5d231d1f24d5109cef65d9edb98b9))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.40 ([4d114b8](https://github.com/gciatto/kt-math/commit/4d114b8c6f71cb696950ba26f0361c1e2a5c328b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.41 ([728c48c](https://github.com/gciatto/kt-math/commit/728c48c203663df19d3fb9f27117ee2105950869))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.42 ([72497e9](https://github.com/gciatto/kt-math/commit/72497e936b25f25391c8760c38865128499dd11b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.43 ([ec49942](https://github.com/gciatto/kt-math/commit/ec499422874406a1bd4e7dbf63aa10e1d1a9d9f1))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.44 ([6fd8c37](https://github.com/gciatto/kt-math/commit/6fd8c373f3dfc9810c398a2315246ad2186235c6))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.46 ([6c0f0dc](https://github.com/gciatto/kt-math/commit/6c0f0dcd7629b4fc2b5d43c7939a9d206709eaaa))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.47 ([7404c70](https://github.com/gciatto/kt-math/commit/7404c703b55618730849f35ac84d979bbad6c4c2))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.49 ([528b536](https://github.com/gciatto/kt-math/commit/528b536e3a3eb856ee215fca83298e5281a257d9))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.50 ([58f87da](https://github.com/gciatto/kt-math/commit/58f87da390f512aa989e75a8d5e495d7f3959945))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.53 ([fa61c4c](https://github.com/gciatto/kt-math/commit/fa61c4c34b0124d3d941c4ed728606cd83212213))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.54 ([e8bd806](https://github.com/gciatto/kt-math/commit/e8bd8063801aa83c55c6baa4f55969c3fe069700))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.55 ([f59b393](https://github.com/gciatto/kt-math/commit/f59b393fea42e4d22331bf233eeda741f49e7b67))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.56 ([d7f99fe](https://github.com/gciatto/kt-math/commit/d7f99fe23bc166bf9ddab1276573642f7b9d4e9a))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.60 ([3083ce9](https://github.com/gciatto/kt-math/commit/3083ce94934a55f46279d348b7882e24c4d0f0b2))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.61 ([84aa222](https://github.com/gciatto/kt-math/commit/84aa222747ec2239fc14159c05ecf5337c65adc5))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.62 ([a9143d9](https://github.com/gciatto/kt-math/commit/a9143d9930cb34e4be571ddb68ac5ebd9fd4c733))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.63 ([3d2f51e](https://github.com/gciatto/kt-math/commit/3d2f51e0c6da08fd26801e395c9e9bebc41cb85d))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.66 ([2616c78](https://github.com/gciatto/kt-math/commit/2616c782dc7c95105a5f3a93b3a2fb69b1fe7172))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.67 ([ebd83b4](https://github.com/gciatto/kt-math/commit/ebd83b427dc7064ba932ef283f058d146a719967))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.68 ([d988c4f](https://github.com/gciatto/kt-math/commit/d988c4fc415db67c9726451e7811c162097cc48b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.69 ([63b482a](https://github.com/gciatto/kt-math/commit/63b482ad377eb8591ff92e1693f65715b34834f5))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.70 ([3a15ae1](https://github.com/gciatto/kt-math/commit/3a15ae1f6303a59f603e4c095bb846347eafa3a2))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.71 ([1a9ec31](https://github.com/gciatto/kt-math/commit/1a9ec31d1196c494f01e62e76ff0d1194a54a418))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.72 ([9c520f5](https://github.com/gciatto/kt-math/commit/9c520f56ac24b6861ac988f2d120a07ba3c375c1))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.73 ([4b7fbd2](https://github.com/gciatto/kt-math/commit/4b7fbd2bbd1022b3b63ed1baf3fdf295869c6edc))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.75 ([b04b9b0](https://github.com/gciatto/kt-math/commit/b04b9b0aa2dbeee51e6c520cf63d47b7ea274a73))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.76 ([0f0e3d1](https://github.com/gciatto/kt-math/commit/0f0e3d1d2e0b8f4e2a86091d07fbd2fb6710b6b3))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.77 ([d351b1d](https://github.com/gciatto/kt-math/commit/d351b1d9ae0e95a8ab6d4a49a578ce777febff35))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.78 ([cbc58ea](https://github.com/gciatto/kt-math/commit/cbc58ea0608a29813b18e18591042b91757ea911))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.79 ([6d3de07](https://github.com/gciatto/kt-math/commit/6d3de074856d86521a10a22fdf79eb700bdb17f0))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.80 ([a5d53bd](https://github.com/gciatto/kt-math/commit/a5d53bdc6428437c988aac248dd998f662eec76e))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.82 ([c6525b6](https://github.com/gciatto/kt-math/commit/c6525b62c0947f4cfd8e5c9896bb69e72fc4cc28))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.83 ([6a05774](https://github.com/gciatto/kt-math/commit/6a05774473fad0ffb2e0533c319aa66192a21017))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.84 ([13d9c7a](https://github.com/gciatto/kt-math/commit/13d9c7ab247ae7895a9c99119f110eead5b176b0))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.85 ([5b89adc](https://github.com/gciatto/kt-math/commit/5b89adc47bdd75f3f494bb6701ef25c2d802add0))
* **deps:** update ktmpp to v2.1.3 ([609ab5a](https://github.com/gciatto/kt-math/commit/609ab5a7ed6a5748712dbec3d03ae4c9702ad766))
* **deps:** update ktmpp to v2.2.0 ([43307cf](https://github.com/gciatto/kt-math/commit/43307cf6d810c135ab3a609a18363acdd9a9e4a7))
* **deps:** update ktmpp to v2.2.1 ([8819be7](https://github.com/gciatto/kt-math/commit/8819be7417121d8265ad9e6861154b1a2d4a1bf0))
* **deps:** update node.js to 18.17 ([e579b75](https://github.com/gciatto/kt-math/commit/e579b75214c707d20fac91278dee45b3eba7c0aa))
* **deps:** update node.js to 18.18 ([92a6092](https://github.com/gciatto/kt-math/commit/92a609205e60b4e4866d6cfbb62715863ea05ac7))
* **deps:** update node.js to 20.10 ([29d1a06](https://github.com/gciatto/kt-math/commit/29d1a069a656dcc15592fa8a2b6637f5f654a9c8))
* **deps:** update node.js to 20.9 ([b57a683](https://github.com/gciatto/kt-math/commit/b57a683a71043d624248f444b9fcc8a7816dc38d))
* **deps:** update node.js to v20 ([d79ec42](https://github.com/gciatto/kt-math/commit/d79ec4268b71c9368fb4ee9a7facca848c0ddc8f))
* **deps:** update plugin com.gradle.enterprise to v3.14 ([9a47b49](https://github.com/gciatto/kt-math/commit/9a47b4918c2c3bccc72b042704263ee67145470c))
* **deps:** update plugin com.gradle.enterprise to v3.14.1 ([d069a21](https://github.com/gciatto/kt-math/commit/d069a2100d8749e313eaf11e10c983bd75e6b619))
* **deps:** update plugin com.gradle.enterprise to v3.15 ([2900c19](https://github.com/gciatto/kt-math/commit/2900c19e142d365050b9eba62f1595ca340aa360))
* **deps:** update plugin com.gradle.enterprise to v3.15.1 ([3ece083](https://github.com/gciatto/kt-math/commit/3ece0833fd61b8498b9f4e0fe98b0e9ede7bf003))
* **deps:** update plugin com.gradle.enterprise to v3.16 ([41aa58a](https://github.com/gciatto/kt-math/commit/41aa58a321157a846cadad247413add74370fb89))
* **deps:** update plugin com.gradle.enterprise to v3.16.1 ([37ea34d](https://github.com/gciatto/kt-math/commit/37ea34d1f4bfbd1ec59480d874275791e3870dfc))
* **deps:** update plugin gitsemver to v1.1.11 ([2654ec2](https://github.com/gciatto/kt-math/commit/2654ec24bbdd32ffe4251a4cd79a3e6d5d8696f9))
* **deps:** update plugin gitsemver to v1.1.13 ([4355b1c](https://github.com/gciatto/kt-math/commit/4355b1c9bfb513c46491346f7e7ad44d096b65c0))
* **deps:** update plugin gitsemver to v1.1.14 ([dd91655](https://github.com/gciatto/kt-math/commit/dd916556baf26af8c18deb0ac18b2f31c60bae4d))
* **deps:** update plugin gitsemver to v1.1.15 ([4581efb](https://github.com/gciatto/kt-math/commit/4581efbbd506fe06c10c4c7b11eede678960a9d2))
* **deps:** update plugin gitsemver to v2 ([68c014d](https://github.com/gciatto/kt-math/commit/68c014d3e62af24ff73af66491c9b49db8dd179a))
* **deps:** update plugin gitsemver to v2.0.1 ([18cd1b4](https://github.com/gciatto/kt-math/commit/18cd1b46c4ba6812b4d8411edf33654e95d7cbed))
* **deps:** update plugin gitsemver to v2.0.2 ([3f5f6bc](https://github.com/gciatto/kt-math/commit/3f5f6bc4837c4beb43000ce13196ce587a17c01d))
* **deps:** update plugin gitsemver to v2.0.3 ([79bcb49](https://github.com/gciatto/kt-math/commit/79bcb49dfec246d70e724fee07a96ddaccc40b58))
* **deps:** update plugin gitsemver to v2.0.4 ([8c8b2a0](https://github.com/gciatto/kt-math/commit/8c8b2a09a9f5d21a31bc7b81da3d8fc636a14001))
* **deps:** update plugin gitsemver to v2.0.5 ([9b687b5](https://github.com/gciatto/kt-math/commit/9b687b5dd465173397ceb712d67e835daf07aab4))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.10 ([0dc82cb](https://github.com/gciatto/kt-math/commit/0dc82cb6f2d6eaf3487003e84247cfe7f7a8cc54))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.11 ([f90d3f0](https://github.com/gciatto/kt-math/commit/f90d3f00fc69e0c682f8800ca06ff57b86138ac4))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.13 ([bf6978a](https://github.com/gciatto/kt-math/commit/bf6978ad36874fd3c33deea1a9806ec4c418b918))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.14 ([3a8aa91](https://github.com/gciatto/kt-math/commit/3a8aa916d64ea37eafb1d5b145babd7c7f9f9537))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.15 ([de0b266](https://github.com/gciatto/kt-math/commit/de0b266cc10ffbbf00bdc874bd6b3dc12e347422))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.16 ([b1f78a1](https://github.com/gciatto/kt-math/commit/b1f78a1c6c5c2ca114dfc21d2eb8d637d2e29ce7))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.17 ([4430a4b](https://github.com/gciatto/kt-math/commit/4430a4b9ebf8bede4af3b644c57fcea6e9eab64a))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.6.0 ([c260cd9](https://github.com/gciatto/kt-math/commit/c260cd97098b77ed834b7b44c012de5f9d27b91a))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.7.0 ([75dcc15](https://github.com/gciatto/kt-math/commit/75dcc15c4200747b57dd75c1db9291e143c44351))


### Bug Fixes

* code style, according to new version of kt-lint ([8121086](https://github.com/gciatto/kt-math/commit/8121086032af26ae3d0fd232fe6e36c4444b561c))


### Build and continuous integration

* **deps:** update actions/checkout action to v3.6.0 ([e6b55db](https://github.com/gciatto/kt-math/commit/e6b55db8093cbcf51e81a2365b457d07963a3e2d))
* **deps:** update actions/checkout action to v4 ([99d5f7d](https://github.com/gciatto/kt-math/commit/99d5f7d0486cc632af289e4a3665ae1390ddb3a7))
* **deps:** update actions/checkout action to v4.1.0 ([a949bdc](https://github.com/gciatto/kt-math/commit/a949bdc3d59f1b6d00fabe39fc6ff4319a1d1146))
* **deps:** update actions/checkout action to v4.1.1 ([4d84261](https://github.com/gciatto/kt-math/commit/4d84261d09fb5f38cc7b36600b83fcf4325ff73c))
* **deps:** update actions/setup-node action to v3.7.0 ([ab66eb7](https://github.com/gciatto/kt-math/commit/ab66eb72ec7534d522658b454ee58a8afb74b908))
* **deps:** update actions/setup-node action to v3.8.0 ([c9703cd](https://github.com/gciatto/kt-math/commit/c9703cdee5d0be5e577649faf54288b6110fd140))
* **deps:** update actions/setup-node action to v3.8.1 ([f874c1f](https://github.com/gciatto/kt-math/commit/f874c1f76c4da211cf67671f6f7b5a6a38db66d7))
* **deps:** update actions/setup-node action to v4 ([ed8f238](https://github.com/gciatto/kt-math/commit/ed8f238be8e9c9f21fb0dc95420079bcc5d3a464))
* **deps:** update actions/setup-node action to v4.0.1 ([3616fb9](https://github.com/gciatto/kt-math/commit/3616fb91fb42791e6f68dc6a1ad4096fc5950fa9))
* **deps:** update danysk/action-checkout action to v0.2.11 ([bcbde9b](https://github.com/gciatto/kt-math/commit/bcbde9bdd4fe277fdc8fe5fe6bddfb5b0ff996b1))
* **deps:** update danysk/action-checkout action to v0.2.12 ([446d897](https://github.com/gciatto/kt-math/commit/446d8975c3b94b3fcc5678eb5345d0fa2bf90d9e))
* **deps:** update danysk/action-checkout action to v0.2.13 ([dc9b335](https://github.com/gciatto/kt-math/commit/dc9b335f29b9701f46d69633329a73cf8e37a07b))
* **deps:** update danysk/action-checkout action to v0.2.14 ([c790ebf](https://github.com/gciatto/kt-math/commit/c790ebf0eda574492fa5ec174a5de1a640d4bef2))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.10 ([0ef7d64](https://github.com/gciatto/kt-math/commit/0ef7d648991598a6654657384ce41d9689c674b3))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.11 ([b55f044](https://github.com/gciatto/kt-math/commit/b55f04409316d6e3a9e123c937d11afe15617a08))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.12 ([3e6584f](https://github.com/gciatto/kt-math/commit/3e6584f7ebc768cbbb56adf2184e1c21a4078eda))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.7 ([ede6c35](https://github.com/gciatto/kt-math/commit/ede6c359270e0720d9e7ed673a317ffc4abd0ad4))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.8 ([858ed0b](https://github.com/gciatto/kt-math/commit/858ed0b3aa1bd96e08a391874020eb82dce5db55))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.9 ([1f9facb](https://github.com/gciatto/kt-math/commit/1f9facb63c1120af254960182f1f3e1cdfd9fa9e))


### General maintenance

* **build:** disable MCR url property ([4a9ea45](https://github.com/gciatto/kt-math/commit/4a9ea45bb36f21867ef3f75b0d55df7ace72b0ef))
* **build:** use kt-mpp 4, and kt 1.9.22 ([4e27ce4](https://github.com/gciatto/kt-math/commit/4e27ce488078255777bd44043b8157e24be96f7e))
* **ci:** avoid run extensive tests in ci, if author is renovate ([0c50d9c](https://github.com/gciatto/kt-math/commit/0c50d9c8c40b10e285bcadc9223f9f9673160189))
* **ci:** close staging repository even when extensive tests are skipped ([c824ea9](https://github.com/gciatto/kt-math/commit/c824ea9cce04f735303bc5fb219c6d4273181393))
* **ci:** test on jvm and js latest lts, and current ([9a756dc](https://github.com/gciatto/kt-math/commit/9a756dc764c1669d077c4596734a0098745f0613))

## [0.9.0](https://github.com/gciatto/kt-math/compare/0.8.5...0.9.0) (2023-07-02)


### Features

* remove dependency from kotlin-bom ([05ad715](https://github.com/gciatto/kt-math/commit/05ad715e9b8e47146db26bf241a33af48d81b418))


### Build and continuous integration

* **deps:** update actions/checkout action to v3.5.3 ([023e888](https://github.com/gciatto/kt-math/commit/023e888cea576f3dd81f5483d34bd98ed22f85d8))
* **deps:** update danysk/action-checkout action to v0.2.10 ([c03edb3](https://github.com/gciatto/kt-math/commit/c03edb36e482420dbd67e0e45ebc64d0079b2a81))


### Dependency updates

* **deps:** update dependency gradle to v8.2 ([83431cf](https://github.com/gciatto/kt-math/commit/83431cf72b77c4c189d77c0ef1aa9b252fb0fc3f))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.32 ([51c0e7d](https://github.com/gciatto/kt-math/commit/51c0e7d5cc9b65ee6f9c6779ba825d72db8f6b39))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.33 ([d340dc2](https://github.com/gciatto/kt-math/commit/d340dc2f704f169f0f7ed19916bf101a25785802))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.35 ([9b4f071](https://github.com/gciatto/kt-math/commit/9b4f071c3455517e82662beca4a22a1da71c1482))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.36 ([7f422f7](https://github.com/gciatto/kt-math/commit/7f422f75f4e7e368589350fbe3b84ecb718a2b24))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.37 ([6bbf16d](https://github.com/gciatto/kt-math/commit/6bbf16d9380bfcdaf662ef3b234c72d2032c5465))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.38 ([818b7e7](https://github.com/gciatto/kt-math/commit/818b7e768307fdb394066d4df8a6eff13319b93c))
* **deps:** update ktmpp to v2.1.1 ([9439cc4](https://github.com/gciatto/kt-math/commit/9439cc436d2c27ef3ef58d6e001a9708e145885a))
* **deps:** update ktmpp to v2.1.2 ([52b8f9a](https://github.com/gciatto/kt-math/commit/52b8f9a26db87fd74b7e41827dd83dc7025cd5c5))
* **deps:** update plugin com.gradle.enterprise to v3.13.4 ([9205d79](https://github.com/gciatto/kt-math/commit/9205d7956917a5c55129efb497ae162aafeabf8c))

## [0.8.5](https://github.com/gciatto/kt-math/compare/0.8.4...0.8.5) (2023-06-08)


### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlin:kotlin-reflect to v1.8.22 ([187a314](https://github.com/gciatto/kt-math/commit/187a314649cd2688bef456676026a7a53e13902d))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.27 ([45dccaa](https://github.com/gciatto/kt-math/commit/45dccaa3ef87f6c26980a4ac921696a50f9b2aee))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.30 ([1f1e087](https://github.com/gciatto/kt-math/commit/1f1e0879eca649dab9f4f91f996587f6f900559f))
* **deps:** update ktmpp to v2.0.5 ([1e01511](https://github.com/gciatto/kt-math/commit/1e01511e8a185c732a1548febd17c9289c83d778))
* **deps:** update plugin gitsemver to v1.1.10 ([aee9cf4](https://github.com/gciatto/kt-math/commit/aee9cf4c3ab97622310d9309b88994501b11d239))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.8 ([a26a723](https://github.com/gciatto/kt-math/commit/a26a72394881e5ca3ac703fbc11c930bc957b2e7))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.9 ([c00e9ab](https://github.com/gciatto/kt-math/commit/c00e9abe8939e3bda214fb9dcce90501cd09f9ae))

## [0.8.4](https://github.com/gciatto/kt-math/compare/0.8.3...0.8.4) (2023-05-25)


### Bug Fixes

* **ci:** show gradle scans in ci ([2300d87](https://github.com/gciatto/kt-math/commit/2300d87677e0a36cbaa4aef541295cfba0fadd12))


### Dependency updates

* **deps:** update plugin com.gradle.enterprise to v3.13.3 ([0bbee2c](https://github.com/gciatto/kt-math/commit/0bbee2cda5930bc79673a701ee7e7e8ee0144af6))


### General maintenance

* **ci:** use matrix to do platform specific tests ([2fcf70f](https://github.com/gciatto/kt-math/commit/2fcf70f8b9dd883a7106e2978d697caea42ca68b))
* remove .test-npm ([054e867](https://github.com/gciatto/kt-math/commit/054e867abd613b864af4742528d4fa18aae9198f))
* **style:** format settings.gradle.kts ([79dbe97](https://github.com/gciatto/kt-math/commit/79dbe9796f88bafd2ec61ebf55e3403c6bdb3d77))
* support node version overriding via property ([cb9f1a0](https://github.com/gciatto/kt-math/commit/cb9f1a0ce3e0b777c07116315bafe8ae008217ed))

## [0.8.3](https://github.com/gciatto/kt-math/compare/0.8.2...0.8.3) (2023-05-25)


### Bug Fixes

* **release:** version numbering on NPM release ([31420f4](https://github.com/gciatto/kt-math/commit/31420f402882e93a5dbe19717549f59dd7a934a4))

## [0.8.2](https://github.com/gciatto/kt-math/compare/0.8.1...0.8.2) (2023-05-25)


### Bug Fixes

* **release:** remove tuProlog org from NPM release ([bb40962](https://github.com/gciatto/kt-math/commit/bb409621e0bda3e38d668c3de5fd2a904f5cfa28))


### Dependency updates

* **deps:** update ktmpp to v2.0.4 ([e3a372c](https://github.com/gciatto/kt-math/commit/e3a372cf4d2430b6f313fabc1c2196a3a7e5a476))

## [0.8.1](https://github.com/gciatto/kt-math/compare/0.8.0...0.8.1) (2023-05-24)


### Bug Fixes

* **ci:** release script w.r.t. NPM version ([5a12d60](https://github.com/gciatto/kt-math/commit/5a12d601a83a5403cf2bfa4bd97f45fa791e86e1))

## [0.8.0](https://github.com/gciatto/kt-math/compare/0.7.4...0.8.0) (2023-05-24)


### Features

* **build,style,test:** use kt-mpp, improve style, and tests ([c6ec972](https://github.com/gciatto/kt-math/commit/c6ec9727709526c4ff5e897d1f01b89180c818c1))
* **build:** use io.github.gciatto.kt-mpp ([9f4ad34](https://github.com/gciatto/kt-math/commit/9f4ad34882728578cc61f353d39632741bc4cb7a))


### Bug Fixes

* broken plugin variant selection on windows ([5cd6cf3](https://github.com/gciatto/kt-math/commit/5cd6cf394b364884fb445c3cde27799cc9398b5d))
* **build:** enforce java 11 for buildSrc ([dc94bc8](https://github.com/gciatto/kt-math/commit/dc94bc8d71fc86e32f494586b091b9daecde069d))
* **ci:** test jvm 11, 17, 18 and js 16, 18, 19 ([eaf8b00](https://github.com/gciatto/kt-math/commit/eaf8b002ab4d6ab9d5b1c178745c1006b220bbb5))
* doc artifact in maven release ([06c32ad](https://github.com/gciatto/kt-math/commit/06c32adab61abb55dad2915ede3903185c602330))


### Dependency updates

* **deps:** update dependency gradle to v8.0.2 ([0e3b707](https://github.com/gciatto/kt-math/commit/0e3b707887db7afb17357a249c6793b72a75640d))
* **deps:** update dependency gradle to v8.1 ([63efb8c](https://github.com/gciatto/kt-math/commit/63efb8c096cb0ec895561ac697f5dcc26cda4777))
* **deps:** update dependency gradle to v8.1.1 ([fa7c731](https://github.com/gciatto/kt-math/commit/fa7c731773e348f1279ad8a2f3b256af8e942abe))
* **deps:** update ktmpp to v1.2.1 ([76c5f7d](https://github.com/gciatto/kt-math/commit/76c5f7d9e47da9295b4c61dd385352bac154c34c))
* **deps:** update plugin com.gradle.enterprise to v3.12.5 ([8acdb09](https://github.com/gciatto/kt-math/commit/8acdb0957266a8de153cbf1ea0e83d1a7be830a3))
* **deps:** update plugin com.gradle.enterprise to v3.12.6 ([e581f55](https://github.com/gciatto/kt-math/commit/e581f550e886c2c1936d5c472281e10bb06969ff))
* **deps:** update plugin com.gradle.enterprise to v3.13 ([03f0be2](https://github.com/gciatto/kt-math/commit/03f0be2efd975a63decd53eb51bdd4998a3f1340))
* **deps:** update plugin com.gradle.enterprise to v3.13.2 ([a5d469b](https://github.com/gciatto/kt-math/commit/a5d469b2c0d3a1695d8cdd4e8f61acdebd98342d))
* **deps:** update plugin gitsemver to v1.1.5 ([2f4b9a9](https://github.com/gciatto/kt-math/commit/2f4b9a97535e53eaab42f5965710bfcfca3a740f))
* **deps:** update plugin gitsemver to v1.1.6 ([7cdd0c5](https://github.com/gciatto/kt-math/commit/7cdd0c54cc1f1ebfd1fef162f3898bb9beeef4a0))
* **deps:** update plugin gitsemver to v1.1.7 ([fa6d344](https://github.com/gciatto/kt-math/commit/fa6d34415c728da72991322539d9cc7d96494c53))
* **deps:** update plugin gitsemver to v1.1.9 ([82952d5](https://github.com/gciatto/kt-math/commit/82952d57f80eb11ee42cac0f04ce58dcc9602650))
