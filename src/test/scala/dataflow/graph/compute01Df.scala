package dataflow.graph

/**
  * Created by vnaveen0 on 26/6/17.
  */

import chisel3._
import chisel3.util._
import chisel3.iotesters._
import org.scalatest.{FlatSpec, FreeSpec, Matchers}
import node._
import dataflow._
import muxes._
import config._
import util._
import interfaces._
import dataflow._
import firrtl_interpreter.InterpreterOptions




// Tester.
class compute01Tester(df: Compute01DF)
                  (implicit p: config.Parameters) extends PeekPokeTester(df)  {

  poke(df.io.data0.bits.data, 4.U)
  poke(df.io.data0.valid, false.B)
  poke(df.io.data0.bits.predicate, true.B)

  poke(df.io.data1.bits.data, 5.U)
  poke(df.io.data1.valid, false.B)
  poke(df.io.data1.bits.predicate, true.B)

  poke(df.io.data2.bits.data, 6.U)
  poke(df.io.data2.valid, false.B)
  poke(df.io.data2.bits.predicate, true.B)

  poke(df.io.data3.bits.data, 7.U)
  poke(df.io.data3.valid, false.B)
  poke(df.io.data3.bits.predicate, true.B)

  poke(df.io.enable.bits.control, false.B)
  poke(df.io.enable.valid, false.B)

  poke(df.io.dataOut.ready, true.B)
  println(s"Output: ${peek(df.io.dataOut)}\n")

  step(1)

  poke(df.io.data0.valid, true.B)
  poke(df.io.data1.valid, true.B)
  poke(df.io.data2.valid, true.B)
  poke(df.io.data3.valid, true.B)
  poke(df.io.enable.bits.control, true.B)
  poke(df.io.enable.valid, true.B)

  println(s"Output: ${peek(df.io.dataOut)}\n")

  for(i <- 0 until 20){
    println(s"Output: ${peek(df.io.dataOut)}")

    println(s"t: ${i}\n ------------------------")
    step(1)
  }
}

class Compute01Tests extends  FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  it should "Not fuse tester" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator", "--target-dir", "test_run_dir"),
      () => new Compute01DF()) {
      c => new compute01Tester(c)
    } should be(true)
  }

}


/**
  * @note different example of test cases
  */
//class VerilogTests extends  FlatSpec with Matchers {
//   implicit val p = config.Parameters.root((new MiniConfig).toInstance)
//   it should "Dataflow tester" in {
//      chisel3.iotesters.Driver.execute(Array("--backend-name", "firrtl", "--target-dir", "test_run_dir"), () => new Compute01DF()){
//       c => new compute01Tester(c)
//     } should be(true)
//   }

//   it should "run firrtl via direct option configuration" in {
//     val manager = new TesterOptionsManager {
//       testerOptions = TesterOptions(backendName = "firrtl")
//       interpreterOptions = InterpreterOptions(writeVCD = true, setVerbose = true)
//     }
//
//     val args = Array("--backend-name", "verilator", "--fint-write-vcd")
//     chisel3.iotesters.Driver.execute(args, () => new Compute01DF()) {
//       c => new compute01Tester(c)
//     }
//   }
// }

//object Compute01DFVerilog extends App {
  //implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  //chisel3.iotesters.Driver.execute(args, () => new Compute01DF()(p))
  //{ c => new compute01Tester(c)  }
//}
