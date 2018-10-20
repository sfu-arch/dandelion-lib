# Dandelion

Dandelion is a library of hardware components to implement **parallel dataflow accelerators.**

## Getting Started on a Local Ubuntu Machine

This will walk you through installing Chisel and its dependencies:

* **[sbt:](https://www.scala-sbt.org/)** which is the preferred Scala build system and what Chisel uses.

* **[Verilator:](https://www.veripool.org/wiki/verilator)**, which compiles Verilog down to C++ for simulation. The included unit testing infrastructure uses this.

## (Ubuntu-like) Linux

Install Java

```
sudo apt-get install default-jdk
```

Install sbt, which isn't available by default in the system package manager:

```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-get update
sudo apt-get install sbt
```

Install Verilator. We currently recommend Verilator version 3.922. Follow these instructions to compile it from source.

Install prerequisites (if not installed already):

```
sudo apt-get install git make autoconf g++ flex bison
```

Clone the Verilator repository:

```
git clone http://git.veripool.org/git/verilator
```
In the Verilator repository directory, check out a known good version:

```
git pull
git checkout verilator_3_922
```

In the Verilator repository directory, build and install:

unset VERILATOR_ROOT # For bash, unsetenv for csh
```
autoconf # Create ./configure script
./configure
make
sudo make install
```


## Dandelion's dependencies
Dandelion depends on _Berkeley Hardware Floating-Point Units_ for floating nodes implementation. We have forked Berkely's implementation and applied minor changes to make the library compatible with Dandelion's nodes.

`dandelion-hardflot` needs to be published locally. For publishing the code locally follow the following commands:

```
$ sbt -DchiselVersion="latest.release" "publishLocal"
```

## Compiling Dandelion Accelerator
For compiling dandelion is the first step to build the library

```
sbt "compile"
```


Dandelion has a set of test cases for testing different dataflow designs. All the dataflow tests are located at:
```
src/main/scala/generator
```

Each of these test cases has a specific test bench and they have been located at following location:
```
src/test/main/scala/generator
```
User can run each of these test cases to simulate the design. For running a test case the command which is needed to enter is:
```
sbt "testOnly <PackageName>.<TesterName>"
```

* **PackageName:** Is the name of the scala package which the dataflow is been developed within.
* **TesterName:** Is the specification of test bench in chisel.

To read more about the overall design of test bench please read **Wiki** section **Test Bench**.

## Getting verilog design
For each test example, there is a main function which generates verilog file of the design.

```
sbt runMain <PackageName>.<MainObjectName>
```

Running following command generates a verilog file in the `RTL` folder.

In order to map the generated verilog on a SoC device please read **SoC FPGA Interface** from our wiki page.

## Contributors
 - Amirali Sharifian (Simon Fraser University)
 - Steven Margem (Simon Fraser University)
 - Navid Rahimi (Simon Fraser University)
 - Apala Guha (Simon Fraser University)
