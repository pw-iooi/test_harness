/**
 * @author Patricia Wong 
 * Program under test (PUT)
 * Simple adder program that sum all the input arguments
 * Failure region created where sum is between 0.5 and 0.6
 */
import java.util.Arrays;

public class PUT {
    public static void main(String[] args) {
        double[] array = Arrays.stream(args).mapToDouble(Double::parseDouble).toArray();
        float sum = 0;
        for (double value: array) {
            sum += (float)value;
        }
        // create a failure region
        if (sum >= 0.5 && sum <= 0.6) {
            System.out.println(0.0);
        } else {
            System.out.println(sum);
        }

    } // end main
} // end class
