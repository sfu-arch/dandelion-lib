package dataflow

import chisel3._
import chisel3.util._
import chisel3.Module
import chisel3.testers._
import chisel3.iotesters._
import org.scalatest.{FlatSpec, Matchers}
import muxes._
import config._
import control._
import util._
import interfaces._
import regfile._
import memory._
import stack._
import arbiters._
import loop._
import accel._
import node._

//class test08CacheWrapper()(implicit p: Parameters) extends test08DF()(p)
  //with CacheParams {

  //// Instantiate the AXI Cache
  //val cache = Module(new Cache)
  //cache.io.cpu.req <> CacheMem.io.MemReq
  //CacheMem.io.MemResp <> cache.io.cpu.resp
  //cache.io.cpu.abort := false.B
  //// Instantiate a memory model with AXI slave interface for cache
  //val memModel = Module(new NastiMemSlave)
  //memModel.io.nasti <> cache.io.nasti

//}

class test08MainIO(implicit val p: Parameters)  extends Module with CoreParams with CacheParams {
  val io = IO( new CoreBundle {
    val in = Flipped(Decoupled(new Call(List(32,32,32))))
    val addr = Input(UInt(nastiXAddrBits.W))
    val din  = Input(UInt(nastiXDataBits.W))
    val write = Input(Bool())
    val dout = Output(UInt(nastiXDataBits.W))
    val out = Decoupled(new Call(List(32)))
  })
}

class test08Main(implicit p: Parameters) extends test08MainIO {

  val cache = Module(new Cache)            // Simple Nasti Cache
  val memModel = Module(new NastiMemSlave) // Model of DRAM to connect to Cache
  val memCopy = Mem(1024, UInt(32.W))      // Local memory just to keep track of writes to cache for validation

  // Store a copy of all data written to the cache.  This is done since the cache isn't
  // 'write through' to the memory model and we have no easy way of reading the
  // cache contents from the testbench.
  when(cache.io.cpu.req.valid && cache.io.cpu.req.bits.iswrite) {
    memCopy.write((cache.io.cpu.req.bits.addr>>2).asUInt(), cache.io.cpu.req.bits.data)
  }
  io.dout := memCopy.read((io.addr>>2).asUInt())

  // Connect the wrapper I/O to the memory model initialization interface so the
  // test bench can write contents at start.
  memModel.io.nasti <> cache.io.nasti
  memModel.io.init.bits.addr := io.addr
  memModel.io.init.bits.data := io.din
  memModel.io.init.valid := io.write
  cache.io.cpu.abort := false.B

  // Wire up the cache and modules under test.
  val test08 = Module(new test08DF())

  cache.io.cpu.req <> test08.io.MemReq
  test08.io.MemResp <> cache.io.cpu.resp
  test08.io.in <> io.in
  io.out <> test08.io.out

}



//class test08Test01(c: test08CacheWrapper) extends PeekPokeTester(c) {
class test08Test01[T <: test08MainIO](c: T) extends PeekPokeTester(c) {


  /**
  *  test08DF interface:
  *
  *    data_0 = Flipped(Decoupled(new DataBundle))
   *    val pred = Decoupled(new Bool())
   *    val start = Input(new Bool())
   *    val result = Decoupled(new DataBundle)
   */


  // Initializing the signals

  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.enable.control, false.B)
  poke(c.io.in.bits.data("field0").data, 0.U)
  poke(c.io.in.bits.data("field0").predicate, false.B)
  poke(c.io.out.ready, false.B)

  step(1)
  poke(c.io.in.valid, true.B)
  poke(c.io.in.bits.enable.control, true.B)
  poke(c.io.in.bits.data("field0").data, 100.U)
  poke(c.io.in.bits.data("field0").predicate, true.B)
  poke(c.io.out.ready, true.B)
  step(1)
  poke(c.io.in.valid, false.B)
  poke(c.io.in.bits.enable.control, false.B)
  poke(c.io.in.bits.data("field0").data, 0.U)
  poke(c.io.in.bits.data("field0").predicate, false.B)
  step(1)
  var time = 1  //Cycle counter
  var result = false
  while (time < 200) {
    time += 1
    step(1)
    //println(s"Cycle: $time")
    if (peek(c.io.out.valid) == 1 &&
      peek(c.io.out.bits.data("field0").predicate) == 1){
      result = true
      val data = peek(c.io.out.bits.data("field0").data)
      if (data != 105) {
        println(Console.RED + s"*** Incorrect result received. Got $data. Hoping for 105")
        fail
      } else {
        println(Console.BLUE + "*** Correct result received.")
      }
    }
  }

  if(!result) {
    println("*** Timeout.")
    fail
  }

}

class test08Tester extends FlatSpec with Matchers {
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  it should "Check that test08 works correctly." in {
    // iotester flags:
    // -ll  = log level <Error|Warn|Info|Debug|Trace>
    // -tbn = backend <firrtl|verilator|vcs>
    // -td  = target directory
    // -tts = seed for RNG
    chisel3.iotesters.Driver.execute(
     Array(
       // "-ll", "Info",
       "-tbn", "verilator",
       "-td", "test_run_dir",
       "-tts", "0001"),
     () => new test08Main()) {
     c => new test08Test01(c)
    } should be(true)
  }
}

