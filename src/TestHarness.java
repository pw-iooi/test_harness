/**
 * @author Patricia Wong
 * TDD to create ART Harness (2021)
 * Note: Place .jar files in the same folder as ARTHarness ./src/PUT.jar or ./src/Oracle.jar
 */

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestHarness for Program under test (PUT) and Oracle
 * ART algorithms: RT and DART
 */
public class TestHarness {
    /**
     * Program main method. Create TestHarness object.
     * @param args string of flags and arguments for parsing
     * @throws IOException invalid file path
     */
    public static void main(String[] args) throws IOException {
        // Variables declaration
        int i = 0, j, k, n; // i is for args; rest for counter in loop
        char flag;
        String arg;
        String program = "";
        String oracle = "";
        String ART_algo = "";
        String algo = "";
        int seed = 0;
        int num_testCases = 0;
        int dim = 0;
        int candidateSize = 0;
        ArrayList<String> args_ranges = new ArrayList<String>();

        TestHarness t = new TestHarness();

        if (args.length < 2) {
            throw new IllegalArgumentException("insufficient arguments");
        }
        // Parse the command line
        flagLoop:
        while (i < args.length) {
            arg = args[i++];
            if (arg.startsWith("-")) {
                flag = arg.charAt(1);
                switch (flag) {
                    case 'm': // <RT|DART>
                        List<String> listAlgo = new ArrayList<String>();
                        listAlgo.add("rt");
                        listAlgo.add("dart");
                        ART_algo = args[i++];
                        algo = ART_algo.toLowerCase();
                        if (!listAlgo.contains(algo)) {
                            System.err.println("Invalid ART algorithm: " + ART_algo + "\n" + "enter RT or DART");
                            System.exit(0);
                        }
                        break;
                    case 'k': // <Candidate Set Size, as an integer: DART>
                        try {
                            candidateSize = Integer.parseInt(args[i++]);
                        } catch (NumberFormatException ex) {
                            System.out.println("Try again. \nIncorrect input for candidate size: an integer is required");
                            System.exit(0);
                        } // end try/catch
                        break;
                    case 'p': // <program under test>
                        program = args[i++];
                        break;
                    case 'o': // <oracle>
                        oracle = args[i++];
                        break;
                    case 's': // <seed>
                        try {
                            seed = Integer.parseInt(args[i++]);
                        } catch (NumberFormatException ex) {
                            System.out.println("Try again. \nIncorrect input for Seed: an integer is required");
                            System.exit(0);
                        } // end try/catch
                        break;
                    case 'n': // <number of test cases to generate>
                        try {
                            num_testCases = Integer.parseInt(args[i++]);
                        } catch (NumberFormatException ex) {
                            System.out.println("Try again. \nIncorrect input for number of test cases: an integer is required");
                            System.exit(0);
                        } // end try/catch
                        break;
                    case 'a': // <num of arguments/parameters the PUT takes>
                        try {
                            dim = Integer.parseInt(args[i++]);
                        } catch (NumberFormatException ex) {
                            System.out.println("Try again. \nIncorrect input for number of arguments: an integer is required");
                            System.exit(0);
                        } // end try/catch
                        break;
                    case 'r': // series of lower and upper bounds for the arguments/parameters [lwr, upr) range
                        int ind = i;
                        int argLen = dim * 2;
                        // throw exception when args is not equal to the dimension
                        for (j = 0; j < argLen; j++) {
                            args_ranges.add(args[ind]);
                            ind++;
                        }
                        break flagLoop;
                    default: // illegal flag
                        System.err.println("Try again. \nIllegal flag " + flag);
                        System.exit(0);
                } // end switch
            } // end if
        } // end while
        // --------------------------------------------------------------------------------------------------------------------//

        String[] arrOneTestCase;
        String[] firstTestCase;
        String[] subTestCase;
        String[] nextTestCase;
        int countFailTests = 0;
        boolean isEqual;
        Random rand = new Random(seed);

        LocalDateTime currDateTime = LocalDateTime.now();
        DateTimeFormatter currDateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDateTime = currDateTime.format(currDateTimeFormat);
        String displayDetails = ("Date/Time: " + formattedDateTime + "\n" + "PUT filename: " + program + ".jar" + "\t" + "Oracle filename: " + oracle + ".jar" + "\n"
                + "ART Algo: " + ART_algo + "\t" + "SEED: " + seed + "\n");
        String displayHeader = String.format("%5s %1s %-30s %-5s %5s", "TestCase#", "|", "Input", "|", "Status");
        String ruler = String.format("%s", "-------------------------------------------------------");

        // create TestResults.dat in the current directory
        String file = new String("./TestResults.dat");
        File myFile = new File(file);

        try {
            if (myFile.createNewFile()) {
                FileWriter myWriter = new FileWriter(file);
                myWriter.write(displayDetails);
                myWriter.write(displayHeader);
                myWriter.write("\n"); myWriter.write(ruler); myWriter.write("\n");
                myWriter.close();
                System.out.println("File created.");
            } else {
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(displayDetails);
                bw.write(displayHeader);
                bw.newLine(); bw.write(ruler); bw.newLine();
                bw.close();
                System.out.println("File already exist. Append to existing file.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } // end try/catch

        // -------------------- command line ART algorithm is "RT" ------------------- //
        if (algo.equals("rt")) {
            long start = System.currentTimeMillis();
            for (k = 1; k <= num_testCases; k++) {
                arrOneTestCase = t.createTestCase(dim, args_ranges, rand);
                isEqual = t.runPUTOracle(arrOneTestCase, program, oracle);

                if (!isEqual) {
                    // failure found
                    t.appendTestCase(k, arrOneTestCase, file, "Fail"); countFailTests ++;
                } // end if
            } // end for
            long end = System.currentTimeMillis();
            t.appendSummaryToFileRT(num_testCases, countFailTests, file, (end-start));
            System.out.println("Testing completed, please check " + file);
        } // end if

        // -------------------- command line ART algorithm is "DART" -------------------- //
        if (algo.equals("dart")) {
            // hashmap to track test case number
            boolean evidenceOfFailure = false;
            HashMap<String[], Integer> testcaseTracker = new HashMap<String[], Integer>();
            int index = 1;
            int numExecuted = 0;
            ArrayList<String []> candidateSet = new ArrayList<>();
            ArrayList<String []> executedSet = new ArrayList<>();

            long start = System.currentTimeMillis();
            // first test case is generated randomly
            firstTestCase = t.createTestCase(dim, args_ranges, rand);
            testcaseTracker.put(firstTestCase, index);

            isEqual = t.runPUTOracle(firstTestCase, program, oracle);
            if (isEqual) {
                executedSet.add(firstTestCase);
                t.appendTestCase(index, firstTestCase, file, "Pass");
                index++;
            } else {
                // failure found
                t.appendTestCase(index, firstTestCase, file, "Fail");
                evidenceOfFailure = true;
            }

            // subsequent test cases
            while (executedSet.size() != num_testCases) {
                // Generate New Candidate Set
                for (n = 1; n <= candidateSize; n++) {
                    subTestCase = t.createTestCase(dim, args_ranges, rand);
                    testcaseTracker.put(subTestCase, index);
                    index++;
                    candidateSet.add(subTestCase);
                }
                // Execute the next test case in the candidate set with maximum minimum distance
                nextTestCase = t.computeEuclideanDistForEachCandidate(candidateSet, executedSet);
                isEqual = t.runPUTOracle(nextTestCase, program, oracle);

                if (isEqual) {
                    executedSet.add(nextTestCase);
                    t.appendTestCase(testcaseTracker.get(nextTestCase), nextTestCase, file, "Pass");
                } else {
                    // failure found
                    t.appendTestCase(testcaseTracker.get(nextTestCase), nextTestCase, file, "Fail");
                    evidenceOfFailure = true;
                    break;
                }
                candidateSet.clear(); // empty the candidate set
            } // end while

            if (evidenceOfFailure) {
                numExecuted = executedSet.size()+1; // total test cases run = number of test case in executed set + 1 failed test that was not in executed set
            } else {
                numExecuted = executedSet.size();
            }
            long end = System.currentTimeMillis();

            t.appendSummaryToFileDART(candidateSize, index-1, numExecuted, file, (end-start), evidenceOfFailure);
            System.out.println("Testing completed, please check " + file);
        } // end if
    } // end main

    /**
     * Append failed test case to TestResults.dat
     * Contain method(s): -
     * @param caseNo test case number
     * @param testcase test case array
     * @param filename .dat filename
     * @throws IOException invalid file path
     */
    public void appendTestCase(int caseNo, String[] testcase, String filename, String status) throws IOException {
        FileWriter fw = new FileWriter(filename, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.format("%5s %5s %-30s %-5s %-5s", caseNo, "|", Arrays.toString(testcase), "|", status));
        bw.newLine();
        bw.close();
    }

    /**
     * Append RT summary to TestResults.dat
     * Contain method(s): -
     * @param totalTestCasesRun total number of test cases run
     * @param FailedTestCases total number of failed test
     * @param filename .dat filename
     * @param milliseconds total execution time
     * @throws IOException invalid file path
     */
    public void appendSummaryToFileRT(int totalTestCasesRun, int FailedTestCases, String filename, long milliseconds) throws IOException {
        FileWriter fw = new FileWriter(filename, true);
        BufferedWriter bw = new BufferedWriter(fw);
        double seconds = milliseconds/1000.0;
        bw.write("Total Test Cases Generated and Executed: " + totalTestCasesRun + "\n" + "Total Failed Test Cases: " + FailedTestCases + "\n" + "Execution time in seconds: " + String.format("%.3f", seconds));
        bw.newLine();bw.newLine();
        bw.close();
    }

    /**
     * Append DART summary to TestResults.dat
     * Contain method(s): -
     * @param candidateSize candidate size
     * @param totalTestCasesGenerated total number test cases generated throughout the program execution
     * @param totalTestCasesRun total number of test cases run
     * @param filename .dat filename
     * @param milliseconds total execution time
     * @throws IOException invalid file path
     */
    public void appendSummaryToFileDART(int candidateSize, int totalTestCasesGenerated, int totalTestCasesRun, String filename, long milliseconds, boolean evidenceOfFailure) throws IOException {
        FileWriter fw = new FileWriter(filename, true);
        BufferedWriter bw = new BufferedWriter(fw);
        double seconds = milliseconds/1000.0;
        if (!evidenceOfFailure) {
            bw.write("No evidence of failure found \n");
        } else {
            bw.write("Evidence of failure found \n");
        }
        bw.write("Candidate Size: " + candidateSize + "\n" + "Total Test Cases Generated: " + totalTestCasesGenerated + "\n" + "Total Test Cases Executed: " + totalTestCasesRun +
                "\n" + "Execution time in seconds: " + String.format("%.3f", seconds));
        bw.newLine();bw.newLine();
        bw.close();
    }

    /**
     * Run test case on both PUT and Oracle, compare the output
     * Contain method(s): executeTestCase(), compareFloatNum()
     * @param arrOneTestCase test case array
     * @param program PUT filename
     * @param oracle Oracle filename
     * @return True if output from PUT and Oracle are the same, False otherwise
     */
    public Boolean runPUTOracle(String[] arrOneTestCase, String program, String oracle) {
        float PUT_Output = 0;
        float Oracle_Output = 0;
        boolean isEqual;
        try {
            PUT_Output = executeTestCase(arrOneTestCase, program);
            Oracle_Output = executeTestCase(arrOneTestCase, oracle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isEqual = compareFloatNum(PUT_Output, Oracle_Output);
        return isEqual;
    }

    /**
     * Create a test case
     * Contain method(s): -
     * @param dim the input domain dimension
     * @param args_ranges the input domain arguments
     * @param rand Random wth seed
     * @return an array of scaled random number (size of the dim)
     */
    public String[] createTestCase(int dim, ArrayList<String> args_ranges, Random rand) {
        String[] arrOneTestCase = new String[dim];
        int m = 0;      // pointer for lwr
        int n = m + 1;  // pointer for upr
        float point = 0;
        float upr, lwr;
        for (int i = 0; i < dim; i++) {
            String arg1 = args_ranges.get(m);
            String arg2 = args_ranges.get(n);
            lwr = Float.parseFloat(arg1);
            upr = Float.parseFloat(arg2);
            point = lwr + (rand.nextFloat() * (upr - lwr));
            arrOneTestCase[i] = Float.toString(point);
            m += 2;
            n = m + 1;
        } // end for
        return arrOneTestCase;
    }

    /**
     * Execute test case, pass the filename "PUT" or Oracle
     * Contain method(s): -
     * @param testcase test case to be executed
     * @param filename file name of PUT or Oracle
     * @return a single numerical output
     * @throws IOException invalid file path
     */
    public float executeTestCase(String[] testcase, String filename) throws IOException {
        float output_PUT = 0;
        StringBuilder inputArguments = new StringBuilder();
        inputArguments.append("java -jar " + filename + ".jar ");
        for (int i = 0; i < testcase.length; i++) {
            inputArguments.append(" ");
            inputArguments.append(testcase[i]);
        } // end for

        String out = inputArguments.toString();
        Process p = Runtime.getRuntime().exec(out);
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String s;
        while ((s = in.readLine()) != null) {
            output_PUT = Float.parseFloat(s);
        } // end while
        return output_PUT;
    }

    /**
     * Compare the float output from PUT and Oracle, tolerance set at 0.01f
     * Contain method(s): -
     * @param PUT output generated from PUT
     * @param Oracle output generated from Oracle
     * @return boolean - True if equivalent, False if not equivalent
     */
    public boolean compareFloatNum(float PUT, float Oracle) {
        float THRESHOLD = 0.01f;
        boolean result;
        if(Math.abs(PUT-Oracle) < THRESHOLD) {
            result = true; // PUT and Oracle are equal
        } else {
            result = false;// PUT and Oracle are NOT equal
        }
        return result;
    }

    /**
     * Calculate the two distance between two test cases
     * Contain method(s): -
     * @param cTestCase candidate test case
     * @param eTestCase executed test case
     * @return the Euclidean distance between two test cases
     */

    public float getEuclideanDist(String[] cTestCase, String[] eTestCase) {
        float sum = 0.0f;
        float result;
        double[] cTC = Arrays.stream(cTestCase).mapToDouble(Double::parseDouble).toArray();
        double[] eTC = Arrays.stream(eTestCase).mapToDouble(Double::parseDouble).toArray();
        for (int i = 0; i < cTC.length; i++) {
            sum += (cTC[i] - eTC[i]) * (cTC[i] - eTC[i]);
        } // end for
        result = (float) Math.sqrt(sum);
        return result;
    }

    /**
     * Compute the Euclidean dist for each candidate test case with all executed test case
     * Store the minimum distance
     * Repeat for all test case in candidate set
     * Select the maximum distance in stored minimum distance
     * Contain method(s): getEuclideanDist()
     * @param candidateSet Array to store candidate test cases generated
     * @param executedSet Array to store executed test cases
     * @return test case with maximum distance
     */
    public String[] computeEuclideanDistForEachCandidate(ArrayList<String []> candidateSet, ArrayList<String []> executedSet) {
        float min;
        float max;
        ArrayList<Float> Max = new ArrayList<>();
        HashMap<Float, String[]> testcaseTracker = new HashMap<Float, String[]>();

        for (String[] c: candidateSet) {
            ArrayList<Float> Min = new ArrayList<>();
            for (String[] e: executedSet) {
                min = getEuclideanDist(c, e);
                testcaseTracker.put(min, c);
                Min.add(min);
            } // end inner for
            Max.add(Collections.min(Min));
        } // end outer for

        max = Collections.max(Max);
        return testcaseTracker.get(max);
    }
} // end class
