import scala.math.pow
import java.io.PrintWriter
import collection.mutable.{ArrayBuffer => ArrayBf}
import collection.mutable.{Map => mMap}

object DiagonalAlignment{
  def ComputeZ(i :Int, m :Int, n :Int, alpha :Double) :Double = {
    val split :Double = i.toDouble * n / m
    val floor :Int = split.toInt
    val ceil :Int = floor + 1
    val ratio :Double = math.exp(-alpha / n)
    val num_top :Int = n - floor
    var ezt :Double = 0
    var ezb :Double = 0
    if(num_top != 0)
      ezt = UnnormalizedProb(i, ceil, m, n, alpha) * (1.0 - math.pow(ratio, num_top)) / (1.0 - ratio)
    if(floor != 0)
      ezb = UnnormalizedProb(i, floor, m, n, alpha) * (1.0 - math.pow(ratio, floor)) / (1.0 - ratio)
    return ezb + ezt
  }

  def ComputeDLogZ(i :Int, m :Int, n :Int, alpha :Double) :Double = {
    val z = ComputeZ(i, n, m, alpha)
    val split = i.toDouble * n / m
    val floor = split.toInt
    val ceil = floor + 1
    val ratio = math.exp(-alpha/n)
    val d = -1.0/n
    val num_top = n -floor
    var pct = 0.0
    var pcb = 0.0
    if (num_top != 0){
      pct = arithmetico_geometric_series(Feature(i, ceil, m, n), UnnormalizedProb(i, ceil, m, n, alpha), ratio, d, num_top)
    }
    if (floor != 0){
      pcb = arithmetico_geometric_series(Feature(i, floor, m, n), UnnormalizedProb(i, floor, m, n, alpha), ratio, d, floor)
    }
    return (pct + pcb) / z
  }

  def arithmetico_geometric_series(a_1 :Double, g_1 :Double, r :Double, d :Double, n :Int) :Double = {
    val g_np1 = g_1 * math.pow(r, n)
    val a_n = d * (n - 1) + a_1
    val x_1 = a_1 * g_1
    val g_2 = g_1 * r
    val rm1 = r - 1
    return (a_n * g_np1 - x_1) / rm1 - d * (g_np1 - g_2) / (rm1 * rm1)
  }

  def UnnormalizedProb(i :Int, j :Int, m :Int, n :Int, alpha :Double) :Double = {
    return math.exp(Feature(i, j, m, n) * alpha)
  }
  def Feature(i :Int, j :Int, m :Int, n :Int) :Double = {
    return -(j.toDouble / n - i.toDouble / m).abs
  }
}
