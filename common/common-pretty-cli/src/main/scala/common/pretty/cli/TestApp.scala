package common.pretty.cli

object TestApp extends App {

  import ConsoleColorise.*

  println("This is bold red line!".red.bold)
  println("This is green line :)".green)
  println("This is yellow line with blue background".yellow.blueBg)
}
