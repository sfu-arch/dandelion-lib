package config


import chisel3._
import chisel3.util._
import config._
import util._
//import examples._
import regfile._

case object XLEN       extends Field[Int]
case object TLEN       extends Field[Int]
case object GLEN       extends Field[Int]
case object RDMSHRLEN  extends Field[Int]
case object WRMSHRLEN  extends Field[Int]
case object TYPSZ      extends Field[Int]
case object Trace      extends Field[Boolean]
case object BuildRFile extends Field[Parameters => AbstractRFile]


abstract trait CoreParams {
  implicit val p: Parameters
  val xlen    = p(XLEN)
  val tlen    = p(TLEN)
  val glen    = p(GLEN)
  val Typ_SZ  = p(TYPSZ)
  val Beats   = Typ_SZ/xlen
  val rdmshrlen = p(RDMSHRLEN)
  val wrmshrlen = p(WRMSHRLEN)
}

abstract class CoreBundle(implicit val p: Parameters) extends ParameterizedBundle()(p) with CoreParams


class MiniConfig extends Config((site, here, up) => {
    // Core
    case XLEN => 64
    case TLEN => 32
    case GLEN => 16
    // Size of read MSHR table bits 
    case RDMSHRLEN => 0
    // Size of write MSHR table bits 
    case WRMSHRLEN => 0
    case TYPSZ  => 64
    // Max size of type memory system may see
    case Trace => true
    case BuildRFile    => (p: Parameters) => Module(new RFile(32)(p))

  }
)
