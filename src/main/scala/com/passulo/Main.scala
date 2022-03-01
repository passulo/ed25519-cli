package com.passulo

import picocli.AutoComplete.GenerateCompletion
import picocli.CommandLine
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.{Command, ITypeConverter}

@Command(
  name = "Ed25519cli",
  subcommands = Array(
    classOf[KeypairCommand],
    classOf[InspectCommand],
    classOf[SignCommand],
    classOf[GenerateCompletion]
  ),
  mixinStandardHelpOptions = true,
  version = Array(
    "@|bold,blue Ed25519 CLI v1|@",
    "@|underline https://github.com/passulo/ed25519-cli|@",
    "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
    "OS: ${os.name} ${os.version} ${os.arch}"
  ),
  description = Array("Command Line Interface for using ed25519 EdEC Crypto")
)
class Ed25519cli {}

object Ed25519cli {
  def main(args: Array[String]): Unit = {
    new CommandLine(new Ed25519cli()).execute(args*)
    ()
  }
}

object StdOutText {
  def error(text: String): String    = Ansi.AUTO.string(s"@|bold,red $text|@")
  def success(text: String): String  = Ansi.AUTO.string(s"@|bold,green $text|@")
  def headline(text: String): String = Ansi.AUTO.string(s"@|bold,underline $text|@")
  def code(text: String): String     = Ansi.AUTO.string(s"@|cyan $text|@")
}

class OptionalParameter extends ITypeConverter[Option[String]] {
  override def convert(value: String): Option[String] = Option(value)
}
