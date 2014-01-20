import scala.collection.immutable
import InitCommandLine._
import java.io.File
import scala.io.Source

object InitCommandLine{

  val helpMessage = """
  Usage:

  --help display this help and exit

  Standard options ([USE] = strongly recommended)
  -i: [REQ] Input parallel corpus
  -v: [USE] Use Dirichlet prior on lexical translation distributions
  -d: [USE] Favor alignment points close to the monotonic diagonoal
  -o: [USE] Optimize how close to the diagonal alignment points should be
  -r: Run alignment in reverse (condition on target and predict source)
  -c: Output conditional probability table

  Advanced options:
  -I: number of iterations in EM training (default = 5)
  -p: p_null parameter (default = 0.08)
  -N: No null word
  -a: alpha parameter for optional Dirichlet prior (default = 0.01)
  -T: starting lambda for diagonal distance parameter (default = 4)
  """

  def parseArgs(args: List[String]): Map[String, String] = {
    // default value
    var input = ""
    var is_reverse = "false"
    var ITERATIONS = "5"
    var favor_diagonal = "false"
    var prob_align_null = "0.08"
    var diagonal_tension = "4.0"
    var optimize_tension = "false"
    var variational_bayes = "false"
    var alpha = "0.01"
    var no_null_word = "false"
    var conditional_probability_filename = ""
    var help = "false"

    @scala.annotation.tailrec
    def parseArgsHelper(args: List[String]){
      args match {
        case "-r" :: rest => {
          is_reverse = "true"
          parseArgsHelper(rest)
        }
        case "-o" :: rest => {
          optimize_tension = "true"
          parseArgsHelper(rest)
        }
        case "-v" :: rest => {
          variational_bayes = "true"
          parseArgsHelper(rest)
        }
        case "-N" :: rest => {
          no_null_word = "true"
          parseArgsHelper(rest)
        }
        case "-d" :: rest => {
          favor_diagonal = "true"
          parseArgsHelper(rest)
        }
        case "-i" :: i :: rest => {
          input = i
          parseArgsHelper(rest)
        }
        case "-I" :: ii :: rest => {
          try{ii.toInt}catch{case e :Exception =>{ println("wrong argment\n"); sys.exit}}
          ITERATIONS = ii
          parseArgsHelper(rest)
        }
        case "-P" :: p :: rest => {
          try{p.toDouble}catch{case e :Exception =>{ println("wrong argment\n"); sys.exit}}
          prob_align_null = p
          parseArgsHelper(rest)
        }
        case "-T" :: t :: rest => {
          try{t.toDouble}catch{case e :Exception =>{ println("wrong argment\n"); sys.exit}}
          diagonal_tension = t
          parseArgsHelper(rest)
        }
        case "-a" :: a :: rest => {
          try{a.toDouble}catch{case e :Exception =>{ println("wrong argment\n"); sys.exit}}
          alpha = a
          parseArgsHelper(rest)
        }
        case "-c" :: c :: rest => {
          conditional_probability_filename = c
          parseArgsHelper(rest)
        }
        case "-I" :: rest => {
          println("wrong argment\n")
          sys.exit
        }
        case "-P" :: rest => {
          println("wrong argment\n")
          sys.exit
        }
        case "-T" :: rest => {
          println("wrong argment\n")
          sys.exit
        }
        case "-a" :: rest => {
          println("wrong argment\n")
          sys.exit
        }
        case "--help" :: rest => help = "true"
        case _ =>
      }
    }

    parseArgsHelper(args)

    val Args = Map("input" -> input, "is_reverse" -> is_reverse, "ITERATIONS" -> ITERATIONS, "favor_diagonal" -> favor_diagonal, "prob_align_null" -> prob_align_null, "diagonal_tension" -> diagonal_tension, "optimize_tension" -> optimize_tension, "variational_bayes" -> variational_bayes, "alpha" -> alpha, "no_null_word" -> no_null_word, "conditional_probability_filename" -> conditional_probability_filename, "help" -> help)

    if(Args("help").toBoolean) {
      println(helpMessage)
      sys.exit()
    }
    val inputFile = new File(Args("input"))
    val outputFile = new File(Args("conditional_probability_filename"))
    if(!inputFile.exists || !inputFile.isFile()){
      println("input file not found")
      sys.exit()
    }
    if(!outputFile.exists){
      try{
        outputFile.createNewFile()
      }catch{
        case e: Exception => println("output file not found")
      }
    }
    if(!outputFile.isFile()){
      println("output file not found")
      sys.exit()
    }
    Args
  }	
}
