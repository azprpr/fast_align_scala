import scala.math.pow
import java.io.PrintWriter
import collection.mutable.{ArrayBuffer => ArrayBf}
import collection.mutable.{Map => mMap}

class Dict(){
  var d_ = mMap.empty[String, Int]
  var words_ = ArrayBf.empty[String]
  var b0_ :String = ""

  def Convert(word :String, frozen :Boolean = false) :Int = {
    val i = d_.get(word)
    i match {
      case Some(value) => value
      case None        => if(frozen) {
        0
      } else {
        words_ += word
        d_(word) = words_.size
        return words_.size
      }
    }
  }
  def Convert(id :Int) :String = {
    if (id == 0) { return b0_ }
    words_(id - 1)
  }
}
