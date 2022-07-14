# lamvs

### Linux kernel Architecture Modelling and Verification System(LAMVS).


## Directory Introduction

source：

Java source code of LAMVS, automatically verify the designing of Linux Kernel.

-data: Demands and designing data of QiHang Kernel extracted automatically by tools.

-tools：
 +(1)Import Design from Linux Code Tool(IDLT): Write by our team, use regular expressions and other methods to extract designing data from the Linux Kernel source code.
 +(2)cflow: A slightly changed version of GNU cflow, analyzes Linux Kernel files and prints function calls and generate demands finally.


本系统可验证Linux内核设计是否满足需求，主要验证其类型间关系，在较早的设计阶段发现错误，降低成本。
