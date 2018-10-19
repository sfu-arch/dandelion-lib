package dataflow

import FPU._
import accel._
import arbiters._
import chisel3._
import chisel3.util._
import chisel3.Module._
import chisel3.testers._
import chisel3.iotesters._
import config._
import control._
import interfaces._
import junctions._
import loop._
import memory._
import muxes._
import node._
import org.scalatest._
import regfile._
import stack._
import util._


  /* ================================================================== *
   *                   PRINTING PORTS DEFINITION                        *
   * ================================================================== */

abstract class cilk_for_test01DFIO(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32, 32))))
    val call_9_out = Decoupled(new Call(List(32, 32, 32)))
    val call_9_in = Flipped(Decoupled(new Call(List())))
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
    val out = Decoupled(new Call(List(32)))
  })
}

class cilk_for_test01DF(implicit p: Parameters) extends cilk_for_test01DFIO()(p) {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */

  val MemCtrl = Module(new UnifiedController(ID=0, Size=32, NReads=2, NWrites=2)
		 (WControl=new WriteMemoryController(NumOps=2, BaseSize=2, NumEntries=2))
		 (RControl=new ReadMemoryController(NumOps=2, BaseSize=2, NumEntries=2))
		 (RWArbiter=new ReadWriteArbiter()))

  io.MemReq <> MemCtrl.io.MemReq
  MemCtrl.io.MemResp <> io.MemResp

  val InputSplitter = Module(new SplitCallNew(List(1,1)))
  InputSplitter.io.In <> io.in



  /* ================================================================== *
   *                   PRINTING LOOP HEADERS                            *
   * ================================================================== */

  val Loop_0 = Module(new LoopBlock(NumIns=List(1,1), NumOuts = 0, NumExits=1, ID = 0))



  /* ================================================================== *
   *                   PRINTING BASICBLOCK NODES                        *
   * ================================================================== */

  val bb_0 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 1, BID = 0))

  val bb_1 = Module(new LoopHead(NumOuts = 5, NumPhi=1, BID = 1))

  val bb_2 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 1, BID = 2))

  val bb_3 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 3, BID = 3))

  val bb_4 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 1, BID = 4))

  val bb_5 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 2, BID = 5))

  val bb_offload_6 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 1, BID = 6))



  /* ================================================================== *
   *                   PRINTING INSTRUCTION NODES                       *
   * ================================================================== */

  //  br label %3, !UID !3, !BB_UID !4
  val br_0 = Module(new UBranchNode(ID = 0))

  //  %.0 = phi i32 [ 0, %2 ], [ %7, %6 ], !UID !5
  val phi_01 = Module(new PhiFastNode2(NumInputs = 2, NumOutputs = 3, ID = 1))

  //  %4 = icmp slt i32 %.0, 5, !UID !6
  val icmp_2 = Module(new IcmpNode(NumOuts = 1, ID = 2, opCode = "ult")(sign=false))

  //  br i1 %4, label %5, label %8, !UID !7, !BB_UID !8
  val br_3 = Module(new CBranchNodeVariable(NumTrue = 1, NumFalse = 1, ID = 3))

  //  detach label %offload., label %6, !UID !9, !BB_UID !10
  val detach_4 = Module(new Detach(ID = 4))

  //  %7 = add nsw i32 %.0, 1, !UID !11
  val binaryOp_5 = Module(new ComputeNode(NumOuts = 1, ID = 5, opCode = "add")(sign=false))

  //  br label %3, !llvm.loop !12, !UID !14, !BB_UID !15
  val br_6 = Module(new UBranchNode(NumOuts=2, ID = 6))

  //  sync label %9, !UID !16, !BB_UID !17
  val sync_7 = Module(new SyncTC(ID = 7, NumInc=1, NumDec=1, NumOuts=1))

  //  ret i32 1, !UID !18, !BB_UID !19
  val ret_8 = Module(new RetNode2(retTypes=List(32), ID = 8))

  //  call void @cilk_for_test01_detach1(i32* %0, i32 %.0, i32* %1)
  val call_9_out = Module(new CallOutNode(ID = 9, NumSuccOps = 0, argTypes = List(32,32,32)))

  val call_9_in = Module(new CallInNode(ID = 9, argTypes = List()))

  //  reattach label %6
  val reattach_10 = Module(new Reattach(NumPredOps= 1, ID = 10))



  /* ================================================================== *
   *                   PRINTING CONSTANTS NODES                         *
   * ================================================================== */

  //i32 0
  val const0 = Module(new ConstNode(value = 0, ID = 0))

  //i32 5
  val const1 = Module(new ConstNode(value = 5, ID = 1))

  //i32 1
  val const2 = Module(new ConstNode(value = 1, ID = 2))

  //i32 1
  val const3 = Module(new ConstNode(value = 1, ID = 3))



  /* ================================================================== *
   *                   BASICBLOCK -> PREDICATE INSTRUCTION              *
   * ================================================================== */

  bb_0.io.predicateIn <> InputSplitter.io.Out.enable

  bb_1.io.activate <> Loop_0.io.activate

  bb_1.io.loopBack <> br_6.io.Out(0)

  bb_2.io.predicateIn <> br_3.io.TrueOutput(0)

  bb_3.io.predicateIn <> detach_4.io.Out(0)

  bb_4.io.predicateIn <> Loop_0.io.endEnable

  bb_5.io.predicateIn <> sync_7.io.Out(0)

  bb_offload_6.io.predicateIn <> detach_4.io.Out(1)



  /* ================================================================== *
   *                   PRINTING PARALLEL CONNECTIONS                    *
   * ================================================================== */

  sync_7.io.incIn(0) <> detach_4.io.Out(2)

  sync_7.io.decIn(0) <> reattach_10.io.Out(0)



  /* ================================================================== *
   *                   LOOP -> PREDICATE INSTRUCTION                    *
   * ================================================================== */

  Loop_0.io.enable <> br_0.io.Out(0)

  Loop_0.io.latchEnable <> br_6.io.Out(1)

  Loop_0.io.loopExit(0) <> br_3.io.FalseOutput(0)



  /* ================================================================== *
   *                   ENDING INSTRUCTIONS                              *
   * ================================================================== */



  /* ================================================================== *
   *                   LOOP INPUT DATA DEPENDENCIES                     *
   * ================================================================== */

  Loop_0.io.In(0) <> InputSplitter.io.Out.data("field0")(0)

  Loop_0.io.In(1) <> InputSplitter.io.Out.data("field1")(0)



  /* ================================================================== *
   *                   LOOP DATA LIVE-IN DEPENDENCIES                   *
   * ================================================================== */

  call_9_out.io.In("field0") <> Loop_0.io.liveIn.data("field0")(0)

  call_9_out.io.In("field2") <> Loop_0.io.liveIn.data("field1")(0)



  /* ================================================================== *
   *                   LOOP DATA LIVE-OUT DEPENDENCIES                  *
   * ================================================================== */



  /* ================================================================== *
   *                   BASICBLOCK -> ENABLE INSTRUCTION                 *
   * ================================================================== */

  br_0.io.enable <> bb_0.io.Out(0)


  const0.io.enable <> bb_1.io.Out(0)

  const1.io.enable <> bb_1.io.Out(1)

  phi_01.io.enable <> bb_1.io.Out(2)

  icmp_2.io.enable <> bb_1.io.Out(3)

  br_3.io.enable <> bb_1.io.Out(4)


  detach_4.io.enable <> bb_2.io.Out(0)


  const2.io.enable <> bb_3.io.Out(0)

  binaryOp_5.io.enable <> bb_3.io.Out(1)

  br_6.io.enable <> bb_3.io.Out(2)


  sync_7.io.enable <> bb_4.io.Out(0)


  const3.io.enable <> bb_5.io.Out(0)

  ret_8.io.In.enable <> bb_5.io.Out(1)


  call_9_in.io.enable.enq(ControlBundle.active())

  call_9_out.io.enable <> bb_offload_6.io.Out(0)




  /* ================================================================== *
   *                   CONNECTING PHI NODES                             *
   * ================================================================== */

  phi_01.io.Mask <> bb_1.io.MaskBB(0)



  /* ================================================================== *
   *                   PRINT ALLOCA OFFSET                              *
   * ================================================================== */



  /* ================================================================== *
   *                   CONNECTING MEMORY CONNECTIONS                    *
   * ================================================================== */



  /* ================================================================== *
   *                   PRINT SHARED CONNECTIONS                         *
   * ================================================================== */



  /* ================================================================== *
   *                   CONNECTING DATA DEPENDENCIES                     *
   * ================================================================== */

  phi_01.io.InData(0) <> const0.io.Out(0)

  icmp_2.io.RightIO <> const1.io.Out(0)

  binaryOp_5.io.RightIO <> const2.io.Out(0)

  ret_8.io.In.data("field0") <> const3.io.Out(0)

  icmp_2.io.LeftIO <> phi_01.io.Out(0)

  binaryOp_5.io.LeftIO <> phi_01.io.Out(1)

  call_9_out.io.In("field1") <> phi_01.io.Out(2)

  br_3.io.CmpIO <> icmp_2.io.Out(0)

  phi_01.io.InData(1) <> binaryOp_5.io.Out(0)

  reattach_10.io.predicateIn(0).enq(DataBundle.active(1.U))



  /* ================================================================== *
   *                   PRINTING CALLIN AND CALLOUT INTERFACE            *
   * ================================================================== */

  call_9_in.io.In <> io.call_9_in

  io.call_9_out <> call_9_out.io.Out(0)

  reattach_10.io.enable <> call_9_in.io.Out.enable



  /* ================================================================== *
   *                   PRINTING OUTPUT INTERFACE                        *
   * ================================================================== */

  io.out <> ret_8.io.Out

}

import java.io.{File, FileWriter}
object cilk_for_test01Main extends App {
  val dir = new File("RTL/cilk_for_test01") ; dir.mkdirs
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new cilk_for_test01DF()))

  val verilogFile = new File(dir, s"${chirrtl.main}.v")
  val verilogWriter = new FileWriter(verilogFile)
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilogWriter.write(compiledStuff.value)
  verilogWriter.close()
}
