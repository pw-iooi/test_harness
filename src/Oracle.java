/**
 * @author Patricia Wong 
 * Oracle
 * Simple adder program that sum all the input arguments
 */
import java.util.Arrays;

public class Oracle {
    public static void main(String[] args) {
        double[] array = Arrays.stream(args).mapToDouble(Double::parseDouble).toArray();
        float sum = 0;
        for (double value: array) {
            sum += (float)value;
        }
        System.out.println(sum);
    } // end main
} // end class
