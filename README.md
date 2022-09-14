# test_harness
“Copyright 2021 Patricia Wong”

### Specification 
macOS, IntelliJ IDEA, JUnit 4
Assumptions/restrictions: the harness will only work in a macOS environment with .jar PUT and Oracle files 
No external libraries used. 
Algorithms implemented: RT and DART 

### TestHarness Project
The TestHarness was built using TDD (refer to TestHarnessTest.java for the tests). These tests will form the regression test. A TestHarness object will be created for each execution. Java Documentation included for each function; comments are added appropriately. The  PUT and Oracle are adder function that adds all the arguments. A failure region between 0.5 and 0.6 was included in PUT.jar

### File Directory
All the files are kept in folder TestHarness/src
TestResults.dat will be created in the same directory as the harness.

### Data Type
Float. The harness compares two floating point numbers directly (e.g. OracleOutput and PUTOutput) in compareFloatNum() function. A tolerance of 0.01 is established. 

### Data Validation 
The arguments entered by the user is validated. Exception handling is used to terminate the program if any argument is of incorrect datatype. The TestHarness will only accept RT or DART as the input algorithm. 

### Pseudorandom Number Generator (PRNG)
Class Random was used to generate a stream of pseudorandom numbers. The same SEED should generate the same sequence of random numbers. A different seed will generate a different sequence. The sequence is consistent for each seed. nextFloat() will return a pseudorandom, uniformly distributed float value [0.0, 1.0). The output will be scaled to the range as input by the user as shown in createTestCase() function. For RT, the test cases generated are numbered and executed in sequence. For DART, the test cases generated are numbered but not executed in sequence (DART uses a combination of max-min distance to select the next candidate test case for execution).

### RT (Random Testing)
There are one stopping criteria, when the algorithm has executed the number of generated test cases introduced by flag -n. 

### DART (Distance-based ART)
DART uses a FSCS. Size of candidate set, introduced by flag-k, is required for this algorithm. FSCS uses two sets of test cases, the candidate set (C) and executed set (E), initially empty. The first test case is generated randomly from the input domain createTestCase() and this test case will be passed into the PUT and Oracle executeTestCase(), and if no evidence of failure is found runPUTOracle(), this test case will be added to the executed set. In each iteration, k numbers of test cases will be generated from the input domain, the Euclidean distance getEuclideanDist() between each candidate test case and each previously executed test cases in E is calculated. The minimum value for each candidate is recorded. Finally, the candidate with the maximum value is selected  computeEuclideanDistForEachCandidate() to be the next test case to be executed. There are two stopping criteria, one is when an evidence of failure is found and the other is when the total number of test cases generated and executed introduced by flag -n is met.

### TestResults.dat
At the end of each execution, the results from the algorithm will be shown. Each run, the date, time, ART algorithm, SEED, a list of test cases (numbered) and a summary will be added.
