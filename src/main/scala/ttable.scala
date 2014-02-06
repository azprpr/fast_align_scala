import scala.math.pow
import java.io.PrintWriter
import collection.mutable.{ArrayBuffer => ArrayBf}
import collection.mutable.{Map => mMap}


object Md {
  def digamma(x: Double) :Double = {
    var result :Double = 0
    var xx :Double = 0
    var xx2 :Double = 0
    var xx4 :Double = 0
    var x2 = x
    while (x2 <  7) {
      result -= 1/x2
      x2 += 1
    }
    x2 -= 1.0/2.0
    xx = 1.0/x2
    xx2 = xx * xx
    xx4 = xx2 * xx2
    result += math.log(x2) + (1.0 / 24.0) * xx2 - (7.0 / 960.0) * xx4 + (31.0 / 8064.0) * xx4 * xx2 - (127.0 / 30720.0) * xx4 * xx4;
    return result
  }
}

class TTable{
  var ttable = ArrayBf.empty[mMap[Int ,Double]]
  var counts = ArrayBf.empty[mMap[Int ,Double]]
  def prob(e :Int, f :Int) :Double = {
    if(e < ttable.size){
      ttable(e).getOrElse(f, 1e-9)
    }
    else{
      1e-9
    }
  }
  def Increment(e :Int, f :Int, x :Double): Unit = {
    for (i <-counts.size to e){
      counts += mMap.empty
    }
    counts(e).getOrElseUpdate(f, 0.0)
    counts(e)(f) += x
  }
  def NormalizeVB(alpha :Double): Unit = {
    val temp = counts
    counts = ttable
    ttable = temp
    for (i <- 0 to ttable.size - 1){
      var tot :Double = 0
      var cpd = ttable(i)
      cpd.foreach {pair =>
      tot += pair._2 + alpha
    }
    if (tot == 0) tot = 1
      cpd.foreach {pair =>
      ttable(i)(pair._1) = math.exp(Md.digamma(pair._2 + alpha) - Md.digamma(tot))
      }
    }
    counts =  ArrayBf.empty[mMap[Int ,Double]]
  }
  def Normalize(){
    val temp = counts
    counts = ttable
    ttable = temp
    for(i <- 0 to ttable.size - 1){
      var tot :Double = 0
      var cpd = ttable(i)
      cpd.foreach {pair =>
      tot += pair._2
    }
    if (tot == 0) tot = 1
      cpd.foreach {pair =>
      ttable(i)(pair._1) /= tot
      }
    }
    counts =  ArrayBf.empty[mMap[Int ,Double]]
  }

  def +=(rhs :TTable) : TTable = {
    for (i <- 0 to rhs.counts.size - 1){
      var cpd = rhs.counts(i)
      var tgt = counts(i)
      cpd.foreach {pair =>
      tgt(pair._1) = pair._2
      }
    }
    return this
  }

  def ExportToFile(filename :String, d :Dict){
    val out = new PrintWriter(filename)
    for(i <- 0 to ttable.size - 1){
      val a :String = d.Convert(i)
      var cpd = ttable(i)
      cpd.foreach{pair=>
      val b :String = d.Convert(pair._1)
      val c :Double = math.log(pair._2)
      out.println(a + "\t" + b + "\t" + c)
      }
    }
    out.close
  }
}
