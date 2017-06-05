**PROJECT TITLE: JS2JAVA: A compiler to translate a subset of JavaScript to Java
**GROUP: G16
NAME1: Bruno Santos, NR1: 201402962, GRADE1: <0 to 20 values>, CONTRIBUTION1: 33%
NAME2: Frederico Rocha, NR2: 201408030,  GRADE2: <0 to 20 values>, CONTRIBUTION2: 33%
NAME3: José Costa, NR3: 201402717, GRADE3: <0 to 20 values>, CONTRIBUTION1: 33%
 
**SUMMARY:
This project generates Java code from a subset of JavaScript. 
The input is the AST of the JavaScript code and the output is its corresponding Java code. 
The Javascript code consists of one or more functions and global variables. 
The framework infers the types of the variables used based on the provided code.
It uses an input JSON file describing the types of the variables used in the Javascript code. 
Besides the code generated, the framework outputs the type considered for each variable in the Javascript code.

**EXECUTE:
After compiling the source code run JS2Java with the arguments <ast_file_name> <types_file_name>.
Both the js ast file and the types file must be anywhere inside the files/ directory.
For the js file provide only the file name. For the types file also provide the path after files/.
The second argument can be omitted if you don't want to use a types file.
If the program is run without arguments follow the instructions given by the interface.

**DEALING WITH SYNTACTIC ERRORS:
Our framework assumes a valid Javascript syntax tree.
During development we used the parser provided by http://esprima.org/demo/parse.html,
which already does some semantic and syntactic verifications on its own.
We did, however, make sure that that identifiers used for variables and functions in the Javascript code are not
reserved words of Java, outputting an error which specifies them.
Since we based our project on the parser above we recommend that you also use it to parse your Javascript code.

**SEMANTIC ANALYSIS: (Refer the possible semantic rules implemented by your tool.)

**INTERMEDIATE REPRESENTATIONS (IRs):
We use an AST(Abstract Sintax Tree) of the JavaScript code, generated into a JSON file,
and proceed to analysing it and generating the corresponding Java code from it.

**CODE GENERATION: (when applicable, describe how the code generation of your tool works and identify the possible problems your tool has regarding code generation.)


**OVERVIEW: 
Our project reads an AST by running it through a TypeInferrer, and then proceeds to generates the Java code by rereading the AST, now with more information available.
To make this process easier and more organized we use the following external libraries:
	-GSON: a java library that can be used to convert Java Objects into their JSON representation;
	-JUnit: a simple framework to write repeatable tests;
	-hamcrest: a framework for writing matcher objects allowing 'match' rules to be defined declaratively;
	-javatuples: a java library whose aim is to provide a set of java classes that allow you to work with tuples.

**TESTSUITE AND TEST INFRASTRUCTURE:
The automated tests are done in the Test.java file, where we run several json files through the program. 
Each type of json file represents a different aspect of the conversion, for example: loops, declarations, expressions, etc. 
In total there are 6 different types of json files, each with a different number of files of the same type.

**TASK DISTRIBUTION: (Identify the set of tasks done by each member of the project.)
	-Frederico Rocha: contributed in both of the main tasks, and some minor ones.

**PROS: 
One of the biggest pros of the project is the fact that it's prepared to deal with a large variety of JavaScript code, from all the possible node types in the ASTs most are treated correctly which means we can convert a significant amount of JavaScript code.

**CONS: 
The main con of the project is the inability to determine data types. At the moment the user must also provide, besides the file to be converted, an additional JSON file which specifies all the types for the variables present in the JavaScript code.
