## LAMVS

### Linux kernel Architecture Modelling and Verification System(LAMVS)

This system can verify whether the Linux kernel design meets the requirements, mainly verifying the relationship between its types, and finding errors in the early design stage to reduce costs.

## Code

### Tech

Language: Java

JEE Framework: SpringBoot

Data Base: mysql

### Directory Introduction
**-code:**  
Java source code of LAMVS.

**-examples:**  【删掉？】

C code of two examples(VFS and QiHang Kernel), tools extract design from here. 

**-data:**  

+ -source  
  + the source code of Linux Kernel and QiHang Kernel, IDLT work in this directory, it generate several .txt files for each .c file.  
+ -demand  
  + Demands data extracted automatically by cflow.  
+ -db  
  + All the data of lamvs in mysql database.    

**-tools:**  

+ -IDLT
  + Import Design from Linux Code Tool(IDLT), finish by our team, use regular expressions and other methods to extract designing data from the Linux Kernel source code.    
+ -cflow
  + A slightly changed version of GNU cflow, analyzes Linux Kernel files and prints function calls and generate demands finally.


## Run

download the code, run in the IntelliJ IDEA. Enter the following link in the browser, auto verify will start:
```
http://localhost:8080/autoVerify
```

Actually, the same effect can be achieve by enter the following link in sequence:
```
1 import design from linux code
	http://localhost:8080/importDesign?pro=7
	
2 finish type and list table
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

the 1st property you should change is the absolute path of the lamvs root :

root=D:\java\

put the source code in this directory, we have been put the guest_small for example:

lamvs/data/source



several days needed to finish the total source code of guest, in order to finish the example in 5 minutes, we take out part of the guest, it could show the same effect. 

