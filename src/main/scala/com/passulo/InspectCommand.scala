package com.passulo

import picocli.CommandLine.{Command, Option}

import java.security.PublicKey
import java.security.interfaces.EdECPublicKey
import java.util.HexFormat
import java.util.concurrent.Callable

@Command(
  name = "inspect",
  mixinStandardHelpOptions = true,
  description = Array("Uses the private key to sign text.")
)
class InspectCommand extends Callable[Int] {

  @Option(names = Array("--private-key"), description = Array("The private key to inspect."), converter = Array(classOf[OptionalParameter]))
  var privateKeyFile: scala.Option[String] = None

  @Option(names = Array("--public-key"), description = Array("The public key to inspect."), converter = Array(classOf[OptionalParameter]))
  var publicKeyFile: scala.Option[String] = None

  def call(): Int = {
    privateKeyFile.foreach { filename =>
      val privateKey = CryptoHelper.loadPKCS8EncodedPEM(filename)
      println(StdOutText.headline("Private Key"))
      println()
      println(FileOperations.loadFile(filename).getLines().map(StdOutText.code).mkString("\n"))
      println()
      println(s"Algorithm:   ${privateKey.getAlgorithm}")
      println(s"Format:      ${privateKey.getFormat}")
      println(s"Hex:         ${HexFormat.of().withDelimiter(":").formatHex(privateKey.getEncoded.drop(16))}")

//      Computing the Public Key from seed is possible, but hidden in the module sun.security.ec.SunEC
//      val spec      = NamedParameterSpec.ED25519
//      val params    = EdDSAParameters.get(new InvalidAlgorithmParameterException(_), NamedParameterSpec.ED25519)
//      val point: EdECPoint = new sun.security.ec.ed.EdDSAOperations(params).computePublic(privateKey.getEncoded)
//      val publicKey = new EdDSAPublicKeyImpl(params, point)
//      printPublicKeyInfo(publicKey, CryptoHelper.encodeAsPEM(publicKey))
    }

    publicKeyFile.foreach { filename =>
      val raw       = FileOperations.loadFile(filename).getLines().map(StdOutText.code).mkString("\n")
      val publicKey = CryptoHelper.loadX509EncodedPEM(filename)
      printPublicKeyInfo(publicKey, raw)
    }

    0
  }

  def printPublicKeyInfo(publicKey: PublicKey, pemString: String): Unit = {
    println(StdOutText.headline("Public Key"))
    println()
    println(pemString)
    println()
    println(s"Algorithm:   ${publicKey.getAlgorithm}")
    println(s"Format:      ${publicKey.getFormat}")
    publicKey match {
      case edECPublicKey: EdECPublicKey =>
        println(s"Parameters:  ${edECPublicKey.getParams.getName}")
        println(s"Point-Y:     ${edECPublicKey.getPoint.getY}")
        println(s"Point-X-odd: ${edECPublicKey.getPoint.isXOdd}")
    }
    println(s"Hex:         ${HexFormat.of().withDelimiter(":").formatHex(publicKey.getEncoded.drop(12))}")
  }
}
