package dataflow

import chisel3._
import chisel3.util._

import node._
import config._
import interfaces._
import arbiters._
import memory._

class TypeLoadDataFlow(implicit val p: Parameters) extends Module with CoreParams{

	val io = IO(new Bundle{val dummy = Input(UInt{32.W})})

	val StackFile = Module(new TypeStackFile(ID=0,Size=32,NReads=1,NWrites=1)
		            (WControl=new WriteTypMemoryController(NumOps=1,BaseSize=2,NumEntries=1))
		            (RControl=new ReadTypMemoryController(NumOps=1,BaseSize=2,NumEntries=1)))
	val Store     = Module(new TypStore(NumPredOps=0,NumSuccOps=1,NumOuts=1,ID=0,RouteID=0))
	val Load      = Module(new TypLoad(NumPredOps=1,NumSuccOps=0,NumOuts=1,ID=0,RouteID=0))


StackFile.io.ReadIn(0) <> Load.io.memReq
Load.io.memResp  <> StackFile.io.ReadOut(0)

StackFile.io.WriteIn(0) <> Store.io.memReq
Store.io.memResp  <> StackFile.io.WriteOut(0)


Store.io.GepAddr.bits.data      := 8.U
Store.io.GepAddr.bits.predicate := true.B
Store.io.GepAddr.valid          := true.B

Store.io.inData.bits.data       := 0x1eadbeefbeefbeefL.U
Store.io.inData.bits.predicate  := true.B
Store.io.inData.valid           := true.B

Store.io.enable.bits.control  := true.B
Store.io.enable.valid := true.B
Store.io.Out(0).ready := true.B


Load.io.GepAddr.bits.data      := 8.U
Load.io.GepAddr.bits.predicate := true.B
Load.io.GepAddr.valid          := true.B

Load.io.enable.bits.control  := true.B
Load.io.enable.valid := true.B
Load.io.Out(0).ready := true.B

Load.io.PredOp(0) <> Store.io.SuccOp(0)

}

