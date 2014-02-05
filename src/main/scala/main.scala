import InitCommandLine._
import java.io.File
import scala.io.Source
import test._

//class Dict{
//  var i = 1
//  def Convert(word :String) :Int = {
//    i += 1
//    return i
//  }
//  def Convert(id :Int) :String = {
//    return "word"
//  }
//}
//
//class TTable{
//  def prob(e :Int, f :Int) :Double = {
//    return 1e-9
//  }
//  def Increment(e :Int, f :Int){
//  }
//  def Increment(e :Int, f :Int, x :Double){
//  }
//  def NormalizeVB(slpha :Double){
//  }
//  def Normalize(){
//  }
//  def ExportToFile(filename :String, d :Dict){
//  }
//}
//
//object DiagonalAlignment{
//  def ComputeZ(i :Int, m :Int, n :Int, alpha :Double) :Double = {
//    return 0.001
//  }
//  def UnnormalizedProb(i :Int, j :Int, m :Int, n :Int, alpha :Double) :Double = {
//    return 0.001
//  }
//  def Feature(i :Int, j :Int, m :Int, n :Int) :Double = {
//    return 0.001
//  }
//}


object Main{

  var d = new Dict()

  def main(args: Array[String]){

    val Args= parseArgs(args.toList)
    val use_null = !Args("no_null_word").toBoolean 	
    if (Args("variational_bayes").toBoolean && Args("alpha").toDouble <= 0.0){
      println("\n\nerror\n--alpha must be > 0\n")
      sys.exit()
    }
    var diagonal_tension = Args("diagonal_tension").toDouble
    var prob_align_not_null :Double = 1.0 - Args("prob_align_null").toDouble
    val kDIV = d.Convert("|||")
    val kNULL :Int = d.Convert("<eps>")
    var s2t = new TTable() 
    val size_counts = scala.collection.mutable.Map[String, Int]()
    var tot_len_ratio :Double = 0.0
    var mean_srclen_multiplier :Double = 0.0

    for (iter <- 0 to Args("ITERATIONS").toInt - 1){
      var final_iteration = (iter == (Args("ITERATIONS").toInt - 1))
      println("ITERATION : " + (iter + 1).toString + "   (Final : " + Args("ITERATIONS") + ")")

      var likelihood :Double = 0.0
      var denom :Double = 0.0
      var lc :Int = 0
      var c0 :Double = 0.0
      var emp_feat :Double = 0.0
      var toks :Double = 0.0

      for (line <- Source.fromFile(Args("input")).getLines){

        lc += 1
        if(lc % 50000 == 0) {
          println(lc.toString + "\n")
        }

        val tmp = line.split('|')
        if(tmp.size != 4) {
          println("Error line\n")
          sys.exit()
        }
        
        var src = for (word <- tmp(0).split(" ") if (word != "")) yield {
          d.Convert(word)
        }
        var trg = for (word <- tmp(3).split(" ") if (word != "")) yield {
          d.Convert(word)
        }

        if(Args("is_reverse").toBoolean) {
          var tmp = src
          src = trg
          trg = tmp
        }

        var probs = scala.collection.mutable.ListBuffer.fill(src.size + 1)(0.0)

        if(src.size == 0 || trg.size == 0) {
          println("Error in line\n")
          sys.exit()
        }

        if(iter == 0){
          size_counts.getOrElseUpdate(trg.size.toString + "," + src.size.toString, 0)
          size_counts(trg.size.toString + "," + src.size.toString) += 1
        }

        toks += trg.size

        for (j <- 0 to trg.length - 1) {
          val f_j = trg(j)
          var sum = 0.0
          var prob_a_i = 1.0 / (src.size + (if(use_null) 1 else 0))

          if(use_null) {
            if(Args("favor_diagonal").toBoolean) {
              prob_a_i = Args("prob_align_null").toDouble
            }
            probs(0) = (s2t.prob(kNULL, f_j) + prob_a_i)
            sum += probs(0)
          }

          var az = 0.0
          if(Args("favor_diagonal").toBoolean) {
            az = (DiagonalAlignment.ComputeZ(j + 1, trg.size, src.size, diagonal_tension) / prob_align_not_null)
          }

          for (i <- 1 to src.length) {
            if(Args("favor_diagonal").toBoolean) {
              prob_a_i = DiagonalAlignment.UnnormalizedProb(j + 1, i, trg.size, src.size, diagonal_tension) / az
            }
            probs(i) = s2t.prob(src(i -1), f_j) * prob_a_i
            sum += probs(i)
          }

          if(final_iteration) {
            var max_p = -1.0
            var max_index = -1
            if(use_null) {
              max_index = 0
              max_p = probs(0)
            }
            for (i <- 1 to src.length) {
              if(probs(i) > max_p) {
                max_index = i
                max_p = probs(i)
              }
            }
          }
          else{
            if(use_null) {
              var count = probs(0) / sum
              c0 += count
              s2t.Increment(kNULL, f_j, count)
            }
            for (i <- 1 to src.length) {
              val p = probs(i) / sum
              s2t.Increment(src(i - 1), f_j, p)
              emp_feat += DiagonalAlignment.Feature(j, i, trg.size, (src.size * p).toInt)
            }
          }
          likelihood += scala.math.log(sum)
        }
      }



      var base2_likelihood = likelihood / scala.math.log(2)


      emp_feat /= toks

      if(!final_iteration) {
        if(Args("favor_diagonal").toBoolean && Args("optimize_tension").toBoolean && iter > 0) {
          for (ii <- 0 to 7) {
            var mod_feat = 0.0
            for ((key, value) <- size_counts) {
              val p = key.split(',')
              for (j <- 1 to p(0).toInt) {
                mod_feat += value * DiagonalAlignment.ComputeZ(j, p(0)toInt, p(1).toInt, diagonal_tension)
              }
            }
            mod_feat /= toks
            diagonal_tension += (emp_feat - mod_feat) * 20.0
            if(diagonal_tension <= 0.1) {
              diagonal_tension = 0.1
            }
            if(diagonal_tension > 14) {
              diagonal_tension = 14
            }
          }
        }
        if(Args("variational_bayes").toBoolean) {
          s2t.NormalizeVB(Args("alpha").toDouble)
        }
        else{
          s2t.Normalize()
        }
        prob_align_not_null = 1.0 - Args("prob_align_null").toDouble
      }
    }
    s2t.ExportToFile(Args("conditional_probability_filename"), d)
    println("終わり")
  }
}





