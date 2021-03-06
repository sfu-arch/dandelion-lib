package dataflow

import accel._
import arbiters._
import chisel3._
import chisel3.util._
import chisel3.Module._
import chisel3.testers._
import chisel3.iotesters._
import config._
import control._
import dataflow.vector_scaleMain.p
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

abstract class cilk_saxpyDFIO(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Call(List(32, 32, 32, 32))))
    val call_9_out = Decoupled(new Call(List(32, 32, 32, 32)))
    val call_9_in = Flipped(Decoupled(new Call(List(32))))
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
    val out = Decoupled(new Call(List(32)))
  })
}

class cilk_saxpyDF(implicit p: Parameters) extends cilk_saxpyDFIO()(p) {


  /* ================================================================== *
   *                   PRINTING MEMORY MODULES                          *
   * ================================================================== */

  val MemCtrl = Module(new UnifiedController(ID = 0, Size = 32, NReads = 2, NWrites = 2)
  (WControl = new WriteMemoryController(NumOps = 2, BaseSize = 2, NumEntries = 2))
  (RControl = new ReadMemoryController(NumOps = 2, BaseSize = 2, NumEntries = 2))
  (RWArbiter = new ReadWriteArbiter()))

  io.MemReq <> MemCtrl.io.MemReq
  MemCtrl.io.MemResp <> io.MemResp

  val InputSplitter = Module(new SplitCallNew(List(1, 1, 1, 1)))
  InputSplitter.io.In <> io.in



  /* ================================================================== *
   *                   PRINTING LOOP HEADERS                            *
   * ================================================================== */

  val Loop_0 = Module(new LoopBlock(NumIns = List(1, 1, 1, 1), NumOuts = 0, NumExits = 1, ID = 0))


  /* ================================================================== *
   *                   PRINTING BASICBLOCK NODES                        *
   * ================================================================== */

  val bb_entry0 = Module(new BasicBlockNoMaskFastNode3(NumOuts = 1, BID = 0))

  val bb_pfor_cond1 = Module(new LoopHead(NumOuts = 4, NumPhi = 1, BID = 1))

  //  val bb_pfor_detach2 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 6, BID = 2))
  //  val bb_pfor_detach2 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 6, BID = 2))
  val bb_pfor_detach2 = Module(new BasicBlockNoMaskFastNode3(NumOuts = 6, BID = 2))

  //  val bb_pfor_inc3 = Module(new BasicBlockNoMaskNode(NumInputs = 1, NumOuts = 4, BID = 3))

  //  val bb_pfor_end4 = Module(new BasicBlockNoMaskFastNode2()(NumInputs = 1, NumOuts = 1, BID = 4))
  val bb_pfor_end4 = Module(new BasicBlockNoMaskFastNode3(NumOuts = 1, BID = 4))

  val bb_pfor_end_continue5 = Module(new BasicBlockNoMaskFastNode3(NumOuts = 2, BID = 5))

  val bb_offload_pfor_body6 = Module(new BasicBlockNoMaskFastNode3(NumOuts = 1, BID = 6))


  /* ================================================================== *
   *                   PRINTING INSTRUCTION NODES                       *
   * ================================================================== */

  //  br label %pfor.cond, !UID !2, !BB_UID !3
  val br_0 = Module(new UBranchFastNode(ID = 0))

  //  %i.0 = phi i32 [ 0, %entry ], [ %inc, %pfor.inc ], !UID !4
  val phi_i_01 = Module(new PhiFastNode(NumInputs = 2, NumOutputs = 3, ID = 1))

  //  %cmp = icmp slt i32 %i.0, %n, !UID !5
//    val icmp_cmp2 = Module(new IcmpNode(NumOuts = 1, ID = 2, opCode = "ult")(sign = false))
  val icmp_cmp2 = Module(new IcmpFastNode(NumOuts = 1, ID = 2, opCode = "ult")(sign = false))

  //  br i1 %cmp, label %pfor.detach, label %pfor.end, !UID !6, !BB_UID !7
  //  val br_3 = Module(new CBranchNode(ID = 3))
  val br_3 = Module(new CBranchFastNode(ID = 3))

  //  detach label %offload.pfor.body, label %pfor.inc, !UID !8, !BB_UID !9
  //  val detach_4 = Module(new Detach(ID = 4))

  //  %inc = add nsw i32 %i.0, 1, !UID !10
  val binaryOp_inc5 = Module(new ComputeFastNode(NumOuts = 1, ID = 5, opCode = "add")(sign = false))
  //  val binaryOp_inc5 = Module(new LoopCounterCompute(ID = 5, step = 1))

  //  br label %pfor.cond, !llvm.loop !11, !UID !13, !BB_UID !14
  //  val br_6 = Module(new UBranchNode(NumOuts=2, ID = 6))

  //  sync label %pfor.end.continue, !UID !15, !BB_UID !16
  val sync_7 = Module(new SyncTC(ID = 7, NumInc = 1, NumDec = 1, NumOuts = 1))

  //  ret i32 1, !UID !17, !BB_UID !18
  val ret_8 = Module(new RetNode2(retTypes = List(32), ID = 8))

  //  call void @cilk_saxpy_detach1(i32* %x, i32 %i.0, i32 %a, i32* %y)
  val call_9_out = Module(new CallOutNode(ID = 9, NumSuccOps = 0, argTypes = List(32, 32, 32, 32)))

  val call_9_in = Module(new CallInNode(ID = 9, argTypes = List()))

  //  reattach label %pfor.inc
  val reattach_10 = Module(new Reattach(NumPredOps = 1, ID = 10))


  /* ================================================================== *
   *                   PRINTING CONSTANTS NODES                         *
   * ================================================================== */

  //i32 0
  //  val const0 = Module(new ConstNode(value = 0, NumOuts = 1, ID = 0))
  val const0 = Module(new ConstFastNode(value = 0, ID = 0))

  //i32 1
  //  val const1 = Module(new ConstNode(value = 1, NumOuts = 1, ID = 1))
  val const1 = Module(new ConstFastNode(value = 1, ID = 1))

  //i32 1
  //  val const2 = Module(new ConstNode(value = 1, NumOuts = 1, ID = 2))
  val const2 = Module(new ConstFastNode(value = 1, ID = 2))


  /* ================================================================== *
   *                   BASICBLOCK -> PREDICATE INSTRUCTION              *
   * ================================================================== */

  bb_entry0.io.predicateIn <> InputSplitter.io.Out.enable

  bb_pfor_cond1.io.activate <> Loop_0.io.activate

  //  bb_pfor_cond1.io.loopBack <> br_6.io.Out(0)
  bb_pfor_cond1.io.loopBack <> bb_pfor_detach2.io.Out(2)

  bb_pfor_detach2.io.predicateIn <> br_3.io.Out(0)

  //  bb_pfor_inc3.io.predicateIn <> detach_4.io.Out(0)
  //  bb_pfor_inc3.io.predicateIn <> bb_pfor_detach2.io.Out(0)

  bb_pfor_end4.io.predicateIn <> Loop_0.io.endEnable

  bb_pfor_end_continue5.io.predicateIn <> sync_7.io.Out(0)

  //  bb_offload_pfor_body6.io.predicateIn <> detach_4.io.Out(1)
  bb_offload_pfor_body6.io.predicateIn <> bb_pfor_detach2.io.Out(0)


  /* ================================================================== *
   *                   PRINTING PARALLEL CONNECTIONS                    *
   * ================================================================== */

  sync_7.io.incIn(0) <> bb_pfor_detach2.io.Out(1)
  //  sync_7.io.incIn(0) <> detach_4.io.Out(2)

  sync_7.io.decIn(0) <> reattach_10.io.Out(0)



  /* ================================================================== *
   *                   LOOP -> PREDICATE INSTRUCTION                    *
   * ================================================================== */

  Loop_0.io.enable <> br_0.io.Out(0)

  //  Loop_0.io.latchEnable <> br_6.io.Out(1)
  Loop_0.io.latchEnable <> bb_pfor_detach2.io.Out(4)

  Loop_0.io.loopExit(0) <> br_3.io.Out(1)



  /* ================================================================== *
   *                   ENDING INSTRUCTIONS                              *
   * ================================================================== */



  /* ================================================================== *
   *                   LOOP INPUT DATA DEPENDENCIES                     *
   * ================================================================== */

  Loop_0.io.In(0) <> InputSplitter.io.Out.data("field0")(0)

  Loop_0.io.In(1) <> InputSplitter.io.Out.data("field2")(0)

  Loop_0.io.In(2) <> InputSplitter.io.Out.data("field1")(0)

  Loop_0.io.In(3) <> InputSplitter.io.Out.data("field3")(0)



  /* ================================================================== *
   *                   LOOP DATA LIVE-IN DEPENDENCIES                   *
   * ================================================================== */

  icmp_cmp2.io.RightIO <> Loop_0.io.liveIn.data("field0")(0)

  call_9_out.io.In("field0") <> Loop_0.io.liveIn.data("field1")(0)

  call_9_out.io.In("field2") <> Loop_0.io.liveIn.data("field2")(0)

  call_9_out.io.In("field3") <> Loop_0.io.liveIn.data("field3")(0)



  /* ================================================================== *
   *                   LOOP DATA LIVE-OUT DEPENDENCIES                  *
   * ================================================================== */



  /* ================================================================== *
   *                   BASICBLOCK -> ENABLE INSTRUCTION                 *
   * ================================================================== */

  br_0.io.enable <> bb_entry0.io.Out(0)


  const0.io.enable <> bb_pfor_cond1.io.Out(0)

  phi_i_01.io.enable <> bb_pfor_cond1.io.Out(1)

  icmp_cmp2.io.enable <> bb_pfor_cond1.io.Out(2)

  br_3.io.enable <> bb_pfor_cond1.io.Out(3)


  //    detach_4.io.enable <> bb_pfor_detach2.io.Out(0)
  //  detach_4.io.enable <> br_3.io.Out(0)


  //  const1.io.enable <> bb_pfor_inc3.io.Out(0)
  const1.io.enable <> bb_pfor_detach2.io.Out(5)

  //  binaryOp_inc5.io.enable <> bb_pfor_inc3.io.Out(1)
  binaryOp_inc5.io.enable <> bb_pfor_detach2.io.Out(3)

  //  br_6.io.enable <> bb_pfor_inc3.io.Out(2)


  sync_7.io.enable <> bb_pfor_end4.io.Out(0)


  const2.io.enable <> bb_pfor_end_continue5.io.Out(0)

  ret_8.io.In.enable <> bb_pfor_end_continue5.io.Out(1)


  call_9_in.io.enable.enq(ControlBundle.active())

  call_9_out.io.enable <> bb_offload_pfor_body6.io.Out(0)




  /* ================================================================== *
   *                   CONNECTING PHI NODES                             *
   * ================================================================== */

  phi_i_01.io.Mask <> bb_pfor_cond1.io.MaskBB(0)



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

  phi_i_01.io.InData(0) <> const0.io.Out

  binaryOp_inc5.io.RightIO <> const1.io.Out

  ret_8.io.In.data("field0") <> const2.io.Out

  icmp_cmp2.io.LeftIO <> phi_i_01.io.Out(0)

  binaryOp_inc5.io.LeftIO <> phi_i_01.io.Out(1)

  call_9_out.io.In("field1") <> phi_i_01.io.Out(2)

  br_3.io.CmpIO <> icmp_cmp2.io.Out(0)

  phi_i_01.io.InData(1) <> binaryOp_inc5.io.Out(0)

  //reattach_10.io.predicateIn(0) <> call_9_in.io.Out.data("field0")
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

class cilk_saxpyTopIO(implicit val p: Parameters) extends Module with CoreParams with CacheParams {
  val io = IO(new CoreBundle {
    val in = Flipped(Decoupled(new Call(List(32, 32, 32, 32))))
    val MemResp = Flipped(Valid(new MemResp))
    val MemReq = Decoupled(new MemReq)
    val out = Decoupled(new Call(List(32)))
  })
}

class cilk_saxpyTop(children: Int)(implicit p: Parameters) extends cilk_saxpyTopIO {


  // Wire up the cache, TM, and modules under test.

  val TaskControllerModule = Module(new TaskController(List(32, 32, 32, 32), List(), 1, children))
  val saxpy = Module(new cilk_saxpyDF())

  val saxpy_detach = for (i <- 0 until children) yield {
    val detach1 = Module(new cilk_saxpy_detach1DF())
    detach1
  }

  // Ugly hack to merge requests from two children.  "ReadWriteArbiter" merges two
  // requests ports of any type.  Read or write is irrelevant.
  val CacheArbiter = Module(new MemArbiter(children))
  for (i <- 0 until children) {
    CacheArbiter.io.cpu.MemReq(i) <> saxpy_detach(i).io.MemReq
    saxpy_detach(i).io.MemResp <> CacheArbiter.io.cpu.MemResp(i)
  }
  io.MemReq <> CacheArbiter.io.cache.MemReq
  CacheArbiter.io.cache.MemResp <> io.MemResp


  // tester to saxpy
  saxpy.io.in <> io.in

  // saxpy to task controller
  TaskControllerModule.io.parentIn(0) <> saxpy.io.call_9_out

  // task controller to sub-task saxpy_detach
  for (i <- 0 until children) {
    saxpy_detach(i).io.in <> TaskControllerModule.io.childOut(i)
    TaskControllerModule.io.childIn(i) <> saxpy_detach(i).io.out
  }

  // Task controller to saxpy
  saxpy.io.call_9_in <> TaskControllerModule.io.parentOut(0)

  // saxpy to tester
  io.out <> saxpy.io.out

}

import java.io.{File, FileWriter}

object cilk_saxpyMain extends App {
  val dir = new File("RTL/cilk_saxpyTop");
  dir.mkdirs
  implicit val p = config.Parameters.root((new MiniConfig).toInstance)
  val testParams = p.alterPartial({
    case TLEN => 6
    case TRACE => false
  })
  val chirrtl = firrtl.Parser.parse(chisel3.Driver.emit(() => new cilk_saxpyTop(3)(testParams)))

  val verilogFile = new File(dir, s"${chirrtl.main}.v")
  val verilogWriter = new FileWriter(verilogFile)
  val compileResult = (new firrtl.VerilogCompiler).compileAndEmit(firrtl.CircuitState(chirrtl, firrtl.ChirrtlForm))
  val compiledStuff = compileResult.getEmittedCircuit
  verilogWriter.write(compiledStuff.value)
  verilogWriter.close()
}
