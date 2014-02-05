// import scala.math.pow
// import java.io.PrintWriter
// import collection.mutable.{ArrayBuffer => ArrayBf}
// import collection.mutable.{Map => mMap}

// object Md {
//   def digamma(x: Double) :Double = {
//     var result :Double = 0
//     var xx :Double = 0
//     var xx2 :Double = 0
//     var xx4 :Double = 0
//     for (i <- Range(x.toInt, 7)) result -= 1/i
//     var x2 = x + 7 - 1.0/2.0
//     xx = 1.0/x2
//     xx2 = xx * xx
//     xx4 = xx2 * xx2
//     result += math.log(x2) + (1.0 / 24.0) * xx2 - (7.0 / 960.0) * xx4 + (31.0 / 8064.0) * xx4 * xx2 - (127.0 * 30720.0) * xx4 * xx4;
//     return result
//   }
// }

// class Dict(){
//   var d_ = mMap.empty[String, Int]
//   var words_ = ArrayBf.empty[String]
//   var b0_ :String = ""

//   //def max() :Int = { words_.size }

//   // def is_ws(s: Char) :Boolean = {
//   //   x == ' ' || x == '\t'
//   // }

// //  def ConvertWhitespaceDelimitedLine(line :String, out :ArrayBf) :Unit = {
// //この関数いらないです
// //
// //    var cur :Int = 0
// //    var last :Int = 0
// //    var state :Int = 0
// //    out.clear
// //    while (cur < line.size){
// //      if (is_ws(line[cur++])){
// //        if (state != 0){
// //          out += Convert(line.substring(last, cur - last -1))
// //          state = 0
// //        }
// //      } else {
// //        if (state != 1){
// //          last = cur - 1
// //          state = 1
// //        }
// //      }
// //    }
// //    if (staet == 1) out += Convert(line.substring(last, cur - last))
// //  }
// //

//   def Convert(word :String, frozen :Boolean = false) :Int = {
//     val i = d_.get(word)
//     i match {
//       case Some(value) => value
//       case None        => if(frozen) {
//         0
//       } else {
//         words_ += word
//         d_(word) = words_.size
//         return words_.size
//       }
//     }
//   }
//   def Convert(id :Int) :String = {
//     if (id == 0) { return b0_ }
//     words_(id - 1)
//   }
// }

// class TTable{
//   var ttable = ArrayBf.empty[mMap[Int ,Double]]
//   var counts = ArrayBf.empty[mMap[Int ,Double]]
//   def prob(e :Int, f :Int) :Double = {
//     if(e < ttable.size){
//       val cpd = ttable(e)
//       val it = cpd.get(f)
//       it match{
//         case Some(value) => value
//         case None        => 1e-9
//       }
//     }
//     else{
//       1e-9
//     }
//   }
//   def Increment(e :Int, f :Int): Unit = {
//     counts(e)(f) += 1.0
//   }
//   def Increment(e :Int, f :Int, x :Double): Unit = {
//     counts(e)(f) += x
//   }
//   def NormalizeVB(alpha :Double): Unit = {
//     //swapいらないかな
//     for (i <- 1 to ttable.size){
//       var tot :Double = 0
//       var cpd = ttable(i)
//       cpd.foreach {pair =>
//         tot += pair._2 + alpha
//       }
//       if (tot != 0) tot = 1
//       cpd.foreach {pair =>
//         //あやしい
//         cpd(pair._1) = math.exp(Md.digamma(pair._2 + alpha) - Md.digamma(tot))
//       }
//     }
//   }
//   def Normalize(){
//     //swapいらない
//     for(i <- 0 to ttable.size){
//       var tot :Double = 0
//       var cpd = ttable(i)
//       cpd.foreach {pair =>
//         tot += pair._2
//       }
//       if (tot != 0) tot = 1
//       cpd.foreach {pair =>
//         //あやしい
//         cpd(pair._1) /= tot
//       }
//     }
//   }

//   def +=(rhs :TTable) : TTable = {
//     for (i <- 0 to rhs.counts.size){
//       var cpd = rhs.counts(i)
//       var tgt = counts(i)
//       cpd.foreach {pair =>
//         tgt(pair._1) = pair._2
//       }
//     }
//     return this
//   }

//   def ExportToFile(filename :String, d :Dict){
//     val out = new PrintWriter(filename)
//     for(i <- 0 to ttable.size){
//       val a :String = d.Convert(i)
//       var cpd = ttable(i)
//       cpd.foreach{pair=>
//         val b :String = d.Convert(pair._1)
//         val c :Double = math.log(pair._2)
//         out.println(a + "\t" + b + "\t" + c)
//       }
//     }
//   }
// }
// object DiagonalAlignment{
//   def ComputeZ(i :Int, m :Int, n :Int, alpha :Double) :Double = {
//     val split :Double = i.toDouble * n / m
//     val floor :Int = split.toInt
//     val ceil :Int = floor + 1
//     val ratio :Double = math.exp(-alpha / n)
//     val num_top :Int = n - floor
//     var ezt :Double = 0
//     var ezb :Double = 0
//     if(num_top != 0)
//       ezt = UnnormalizedProb(i, ceil, m, n, alpha) * (1.0 - math.pow(ratio, num_top)) / (1.0 - ratio)
//     if(floor != 0)
//       ezb = UnnormalizedProb(i, floor, m, n, alpha) * (1.0 - math.pow(ratio, floor)) / (1.0 - ratio)
//     return ezb + ezt
//   }
//   def UnnormalizedProb(i :Int, j :Int, m :Int, n :Int, alpha :Double) :Double = {
//     return math.exp(Feature(i, j, m, n) * alpha)
//   }
//   def Feature(i :Int, j :Int, m :Int, n :Int) :Double = {
//     return -(j.toDouble / n - i.toDouble / m).abs
//   }
// }
