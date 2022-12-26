package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            long count = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String [] indexesString = line.split("[-,]");
                long[] indexesLong = new long[4];
                System.out.println(Arrays.toString(indexesString));

                for (int idx = 0; idx < 4; ++idx) {
                    indexesLong[idx] = Long.parseLong(indexesString[idx]);
                }

                System.out.println("converted: " + Arrays.toString(indexesLong));
                if (indexesLong[1] < indexesLong[2] || indexesLong[3] < indexesLong[0]){
                    // not overlapping
                }
                else{
                    count++;
                }
            }
            System.out.println(count);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
