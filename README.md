# Dandelion: Library of hardware components for designing parallel dataflow accelerators

Dandelion is a library of hardware components to implement parallel dataflow accelerators.

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
