package com.passulo

import picocli.CommandLine.{Command, Option}

import java.util.concurrent.Callable
import scala.annotation.nowarn

@Command(
  name = "keypair",
  mixinStandardHelpOptions = true,
  description = Array("Creates a key-pair based on the Ed25519 algorithm and writes the keys to 'private.pem' and 'public.pem'.")
)
class KeypairCommand extends Callable[Int] {

  // noinspection VarCouldBeVal
  @nowarn("msg=consider using immutable val")
  @Option(names = Array("-f", "--overwrite"), description = Array("Overwrite existing files"))
  private var overwrite = false

  def call(): Int = {
    // TODO: Load filenames from input
    CryptoHelper.generateKeyPair(overwrite) match {
      case Left(errorMessage) => println(StdOutText.error(errorMessage))
      case Right(_)           => println(StdOutText.success("Written successfully"))
    }
    0
  }
}
