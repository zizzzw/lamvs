## LAMVS

**Linux kernel Architecture Modelling and Verification System(LAMVS)**

This system can verify whether the Linux kernel design meets the requirements, mainly verifying the relationship between its types, and finding errors in the early design stage to reduce costs.

## Directory Introduction

**-source：**
Java source code of LAMVS, automatically verify the designing of Linux Kernel.

**-data:** 
Demands and designing data of QiHang Kernel extracted automatically by tools.

**-tools：**

(1)Import Design from Linux Code Tool(IDLT): Write by our team, use regular expressions and other methods to extract designing data from the Linux Kernel source code.

(2)cflow: A slightly changed version of GNU cflow, analyzes Linux Kernel files and prints function calls and generate demands finally.



