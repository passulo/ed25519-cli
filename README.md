# ed25519 CLI

A simple & fast wrapper around the Java 15+ implementation of Ed25519.

## Commands

### Generate a keypair (matching public and private key)

Stores them in [X509 format](https://en.wikipedia.org/wiki/X.509#Certificate_filename_extensions) (i.e. base64 encoded DER with `-----BEGIN…END-----`)

```shell
$ ed25519 keypair
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
