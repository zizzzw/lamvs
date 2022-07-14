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

download the code, run in the idea. Enter the following link in the browser:

1 从代码导入design表
	http://localhost:8080/importDesign?pro=7
	
2 完善Type和List表
	http://localhost:8080/finishType?pro=7

3 导入demand的txt
	http://localhost:8080/importDemand?pro=7
	
4 完善Demand表
	http://localhost:8080/finishDemand

5 关系生成
	http://localhost:8080/relationGenerate?relationClassify=-1

6 include文件自检+完善【调用验证需要】
	http://localhost:8080/selfCheckInclude?pro=7

7 一键验证，报告
	http://localhost:8080/globalVerify
	
	显示报告页面：【前提是把repId在js中设置好】
	http://localhost:8080/veri_reportShow
	


## Example1: VFS



## Example2: QiHang Kernel 






