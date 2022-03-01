package com.passulo

import picocli.CommandLine
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.{usage, Command}

import java.util.concurrent.Callable

object Main {
  def main(args: Array[String]): Unit = {
    new CommandLine(new TopCommand()).execute(args*)
    ()
  }
}

object StdOutText {
  def error(text: String): String   = Ansi.AUTO.string(s"@|bold,red $text|@")
  def success(text: String): String = Ansi.AUTO.string(s"@|bold,green $text|@")
}

@Command(
  name = "",
  subcommands = Array(
    classOf[KeypairCommand],
    classOf[SignCommand]
  ),
  mixinStandardHelpOptions = true,
  version = Array("@|bold,blue PassCreator v1|@", "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})", "OS: ${os.name} ${os.version} ${os.arch}"),
  description = Array("Command Line Interface for using ed25519 EdEC Crypto")
)
class TopCommand extends Callable[Int] {
  def call(): Int = {
    usage(this, System.out)
    0
  }
}