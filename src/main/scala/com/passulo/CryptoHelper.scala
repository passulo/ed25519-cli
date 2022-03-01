package com.passulo

import java.security.*
import java.security.cert.{CertificateFactory, X509Certificate}
import java.security.interfaces.EdECPublicKey
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.util.Base64
import scala.util.{Success, Try}

/** Collection of helpful abstractions to load and decode certificates and keys, using java.Security and bouncy castle.
  *
  * helpful: https://www.baeldung.com/java-read-pem-file-keys helpful: https://www.tbray.org/ongoing/When/202x/2021/04/19/PKI-Detective
  */
object CryptoHelper {

  def generateKeyPair(overwrite: Boolean, privateFile: String = "private.pem", publicFile: String = "public.pem"): Either[String, Success.type] = {
    val generator  = KeyPairGenerator.getInstance("ed25519")
    val keypair    = generator.generateKeyPair()
    val publicKey  = encodeAsPEM(keypair.getPublic)
    val privateKey = encodeAsPEM(keypair.getPrivate)

    for {
      _ <- FileOperations.writeFile(privateFile, privateKey, overwrite)
      _ <- FileOperations.writeFile(publicFile, publicKey, overwrite)
    } yield Success
  }

  def encodeAsPEM(key: Key): String = {
    val identifier = key match {
      case _: PrivateKey => "PRIVATE"
      case _: PublicKey  => "PUBLIC"
      case key           => throw new UnknownError(s"Unknown key type: ${key.getFormat}")
    }
    val encoded = Base64.getEncoder.encodeToString(key.getEncoded)
    s"""-----BEGIN $identifier KEY-----
       |$encoded
       |-----END $identifier KEY-----""".stripMargin
  }

  /** Reads an Ed25519 public key stored in X.509 Encoding (base64) in a PEM file (-----BEGIN … END … KEY-----) */
  def loadX509EncodedPEM(filename: String): EdECPublicKey = {
    val keyBytes   = decodePEM(filename)
    val spec       = new X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("ed25519")
    keyFactory.generatePublic(spec) match {
      case key: EdECPublicKey => key
    }
  }

  /** Reads an Ed25519 private key stored in PKCS #8 Encoding (base64) in a PEM file (-----BEGIN … END … KEY-----) */
  def loadPKCS8EncodedPEM(filename: String): PrivateKey = {
    val keyBytes   = decodePEM(filename)
    val spec       = new PKCS8EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("ed25519")
    keyFactory.generatePrivate(spec)
  }

  /** Reads a file in PEM format, returns the payload (between the lines). */
  def decodePEM(filename: String): Array[Byte] = {
    val file             = FileOperations.loadFile(filename)
    val encodedKeyString = file.getLines().filterNot(_.startsWith("----")).mkString("")
    Base64.getDecoder.decode(encodedKeyString)
  }

  /** Reads an X.509 Certificate from a file */
  def certificateFromFile(certPath: String): Option[X509Certificate] = {
    val inputStream = Thread.currentThread().getContextClassLoader.getResourceAsStream(certPath)
    CertificateFactory.getInstance("X.509").generateCertificate(inputStream) match {
      case cert: X509Certificate if Try(cert.checkValidity()).isSuccess => Some(cert)
      case _                                                            => None
    }
  }

  def sign(bytes: Array[Byte], privateKey: PrivateKey): Array[Byte] = {
    val signature: Signature = Signature.getInstance("Ed25519")
    signature.initSign(privateKey)
    signature.update(bytes)
    signature.sign
  }
}
