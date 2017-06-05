**PROJECT TITLE: JS2JAVA: A compiler to translate a subset of JavaScript to Java
**GROUP: G16
NAME1: Bruno Santos, NR1: 201402962, GRADE1: <0 to 20 value>, CONTRIBUTION1: 33%
NAME2: Frederico Rocha, NR2: 201408030,  GRADE2: <0 to 20 value>, CONTRIBUTION2: 33%
NAME3: José Costa, NR3: 201402717, GRADE3: <0 to 20 value>, CONTRIBUTION1: 33%

**SUMMARY:
This project generates Java code from a subset of JavaScript. 
The input is the AST of the JavaScript code and the output is Java. 
The Javascript code consists of one or more functions and global variables. 
The framework infers the types of the variables used based on the provided code.
It can also use an input JSON file describing the types of the variables used in the Javascript
code. 
Besides the code generated, the framework outputs the type considered for each variable in the
Javascript code.

**EXECUTE:
After compiling the source code run JS2Java with the arguments <ast_file_name> <types_file_name>.
Both the Javascript AST file and the types file must be anywhere inside the files/ directory and
be of type .json.
For the Javascript file provide only the file name. For the types file also provide the path
after files/.
The second argument can be omitted if you don't want to use a types file.
If the program is run without arguments, follow the instructions given by the interface.
If a .txt file exists in the files/ directory with the same name as the AST file, its contents
will be outputted on the end result as a comment.
This feature allows for easy comparison of the original Javascript code and the Java code
generated, for example.

**DEALING WITH SYNTACTIC ERRORS:
Our framework assumes a valid Javascript syntax tree.
During development we used the parser provided by http://esprima.org/demo/parse.html,
which already does some syntactic and semantic verifications on its own.
We did, however, make sure that that the identifiers used for variables and functions in the
Javascript code are not reserved words of Java, outputting an error which specifies them.
Since we based our project on the parser above we recommend that you also use it to parse your
Javascript code.

**SEMANTIC ANALYSIS:
As referenced above, the use of the parser provided by http://esprima.org/demo/parse.html, which
means that the semantic analysis on the Javascript code is already mostly done.

**INTERMEDIATE REPRESENTATIONS (IRs):
To analyse and generate code we transverse the provided AST directly, without the necessity of another representation.

**CODE GENERATION:
We begin by getting every identifier used to represent parameters and the declared and used
variables of each function.
By the order and location of variable declarations and uses are then infer the scope of each
variable present throughout the code.
We then procede to infer the types of these variables based on the value of the literals present
in the code, operations the variables are in and other infered variables.
If every variable has a known type we then generate the Java code.
In this stage we transverse the AST and output the conversion as we go, making the necessary
adjustments to ensure the result is valid Java code.
To do this we make use of the variable types infered previously, convert arrays to ArrayLists,
convert Javascript specific actions (access and adding array elements, print functions) to
Javass, and output checks when there are type mismatches (arithmetic operations with booleans,
boolean expressions with ints...).

**OVERVIEW:
Our project basically reads an AST and, by running iterating through its nodes, infers the type
of the used variables and generates Java code.
To make this process easier we used GSON, a java library that can be used to convert JSON
representations to Java Objects.
In the end the program creates a Java file in the output folder of the project which can be
compiled and run.

**TESTSUITE AND TEST INFRASTRUCTURE:
We have several testing ASTs that highlight the different aspects of our program.
We also have a Test class the runs all of the test cases, with the verification of each result
having to be done manually.
 
**TASK DISTRIBUTION:
Every member of the group partook in the development of the two main modules of the project, the
TypeInferer and the Output.
 
**PROS:
The biggest pro of our project is that it is able to infer the data types of most variables,
provided that the AST used represents a valid
Javascript program, without undefined variables or variables that have multiple types along
their lifetime.
It is also able to parse most statements with arithmetic and logical expressions, operations for
adding and accessing array elements, as well as loops, conditions, function blocks.

**CONS:
The main con of the project is that it isn't able to parse some Javascript specific
funcionalities like named indexes on arrays and variables with different types along their
lifetimes.