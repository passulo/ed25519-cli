package com.passulo

import java.security.*
import java.security.cert.{CertificateFactory, X509Certificate}
import java.security.interfaces.{EdECPrivateKey, EdECPublicKey}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.util.Base64
import scala.io.Source
import scala.util.{Success, Try}

/** Collection of helpful abstractions to load and decode certificates and keys, using java.Security and bouncy castle.
  *
  * helpful: https://www.baeldung.com/java-read-pem-file-keys helpful: https://www.tbray.org/ongoing/When/202x/2021/04/19/PKI-Detective
  */
object CryptoHelper {

  def generateKeyPair(overwrite: Boolean, privateFile: String = "private.pem", publicFile: String = "public.pem"): Either[String, Success.type] = {
    val generator = KeyPairGenerator.getInstance("ed25519")
    val keypair   = generator.generateKeyPair()
    val publicKey = keypair.getPublic match {
      case publicKey: EdECPublicKey =>
        val encoded = Base64.getEncoder.encodeToString(publicKey.getEncoded)
        s"""-----BEGIN PUBLIC KEY-----
           |$encoded
           |-----END PUBLIC KEY-----""".stripMargin
    }
    val privateKey = keypair.getPrivate match {
      case privateKey: EdECPrivateKey =>
        val encoded = Base64.getEncoder.encodeToString(privateKey.getEncoded)
        s"""-----BEGIN PRIVATE KEY-----
           |$encoded
           |-----END PRIVATE KEY-----""".stripMargin
    }

    for {
      _ <- FileOperations.writeFile(privateFile, privateKey, overwrite)
      _ <- FileOperations.writeFile(publicFile, publicKey, overwrite)
    } yield Success
  }

  /** Reads an Ed25519 public key stored in X.509 Encoding (base64) in a PEM file (-----BEGIN … END … KEY-----) */
  def publicKeyFromFile(path: String): Option[EdECPublicKey] = {
    val file             = Source.fromResource(path)
    val encodedKeyString = file.getLines().filterNot(_.startsWith("----")).mkString("")
    val keyBytes         = Base64.getDecoder.decode(encodedKeyString)
    val spec             = new X509EncodedKeySpec(keyBytes)
    val keyFactory       = KeyFactory.getInstance("ed25519")
    keyFactory.generatePublic(spec) match {
      case key: EdECPublicKey => Some(key)
    }
  }

  /** Reads an Ed25519 private key stored in PKCS #8 Encoding (base64) in a PEM file (-----BEGIN … END … KEY-----) */
  def privateKeyFromFile(path: String): PrivateKey = {
    val file             = FileOperations.loadFile(path)
    val encodedKeyString = file.getLines().filterNot(_.startsWith("----")).mkString("")
    val keyBytes         = Base64.getDecoder.decode(encodedKeyString)
    val spec             = new PKCS8EncodedKeySpec(keyBytes)
    val keyFactory       = KeyFactory.getInstance("ed25519")
    keyFactory.generatePrivate(spec)
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
