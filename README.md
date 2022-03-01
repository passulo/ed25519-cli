# ed25519 CLI

A simple & fast wrapper around the Java 15+ implementation of Ed25519.

### Installation

```shell
$ brew install passulo/tap/ed25519-cli
```

or download [the latest release](https://github.com/passulo/ed25519-cli/releases).

## Commands

### Generate a keypair (matching public and private key)

Stores them in [X509 format](https://en.wikipedia.org/wiki/X.509#Certificate_filename_extensions) (i.e. base64 encoded DER with `-----BEGIN…END-----`)

```shell
$ ed25519 keypair
```

### Inspect contents of a key

```shell
$ ed25519 inspect --private-key private.pem
Private Key

-----BEGIN PRIVATE KEY-----
MC4CAQAwBQYDK2VwBCIEIHK/QKaurjJYKFW+1zuYawEJr+uQs2RNrfAR/30hCqfu
-----END PRIVATE KEY-----

Algorithm:   EdDSA
Format:      PKCS#8
Hex:         04:22:04:20:72:bf:40:a6:ae:ae:32:58:28:55:be:d7:3b:98:6b:01:09:af:eb:90:b3:64:4d:ad:f0:11:ff:7d:21:0a:a7:ee

$ ed25519 inspect --public-key public.pem
Public Key

-----BEGIN PUBLIC KEY-----
MCowBQYDK2VwAyEAOo6bJgwQK1mw9c8hXxn7MkKNyAMAf1BblEKgWtlMhY8=
-----END PUBLIC KEY-----

Algorithm:   EdDSA
Format:      X.509
Point-Y:     7020213780939337217595117931293078227692515130139046136149940614356509888058
Point-X-odd: true
Hex:         3a:8e:9b:26:0c:10:2b:59:b0:f5:cf:21:5f:19:fb:32:42:8d:c8:03:00:7f:50:5b:94:42:a0:5a:d9:4c:85:8f
```

### Sign a text

Uses the `private.pem` key (or a private key at the specified path) to sign the UTF8 representation of the input and outputs a base64-encoded signature.

```shell
$ ed25519 sign --private-key="~/.keys/private.pem" --url-encoded "My Secret Message"
IrlmZ21-9WGEnEFWjtYtXyyzuzxAGJ5EaA11g5ESNOOzBWIaD9hGh2QFsauqCflFOJvyexY9ZJFkzXmlmVe0Bg==
```

# Dependencies

* The underlying crypto is provided by JDK 17 (the `java.security` package of OpenJDK)
* The CLI is built with [Picocli](https://picocli.info) and [Picocli Code Generation](https://github.com/remkop/picocli/tree/main/picocli-codegen)
* The executable is built with [GraalVM Native Image](https://www.graalvm.org/22.0/reference-manual/native-image/)
