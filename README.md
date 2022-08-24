## LAMVS

### Linux kernel Architecture Modelling and Verification System(LAMVS)

This system can verify whether the Linux kernel design meets the requirements, mainly verifying the relationship between its types, and finding errors in the early design stage to reduce costs.

## Code

### Tech

Language: Java

JEE Framework: SpringBoot

Data Base: mysql

### Directory Introduction
**-source：**
Java source code of LAMVS.

**-data:** 
Demands and designing data of QiHang Kernel extracted automatically by tools.

**-tools：**

(1)Import Design from Linux Code Tool(IDLT): Write by our team, use regular expressions and other methods to extract designing data from the Linux Kernel source code.

(2)cflow: A slightly changed version of GNU cflow, analyzes Linux Kernel files and prints function calls and generate demands finally.


## Run

download the code, run in the IntelliJ IDEA. Enter the following link in the browser, auto verify will start:
```
http://localhost:8080/autoVerify
```

Actually, the same effect can be achieve by enter the following link in sequence:
```
1 import design from code
	http://localhost:8080/importDesign?pro=7
	
2 fihish type and list table
	http://localhost:8080/finishType?pro=7

3 import demand(.txt)
	http://localhost:8080/importDemand?pro=7
	
4 finish demand table
	http://localhost:8080/finishDemand

5 relation generate
	http://localhost:8080/relationGenerate?relationClassify=-1

6 include check 
	http://localhost:8080/selfCheckInclude?pro=7

7 global verify and show the report
	http://localhost:8080/globalVerify
```


## Example1: VFS



## Example2: QiHang Kernel 






