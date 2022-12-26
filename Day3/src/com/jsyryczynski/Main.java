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

            long totalCount = 0;
            int idx = 0;
            ArrayList<ArrayList<Integer>> threeArrays = new ArrayList<ArrayList<Integer>>();

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                threeArrays.add(makeUniqueArray(line));
                idx++;

                if (idx >= 3) {
                    int uniqueValue = findUnique(threeArrays);
                    totalCount += uniqueValue;
                    threeArrays.clear();
                    idx = 0;
                }
            }
            System.out.println("totalCount " + totalCount);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static int findUnique(ArrayList<ArrayList<Integer>> threeArrays) {
        var result = new ArrayList<Integer>(Collections.nCopies(100, 0));

        for (int idx = 0; idx < 100; ++idx) {
            int sum = 0;
            for (int arrIdx = 0; arrIdx < threeArrays.size(); ++arrIdx) {
                var currArr = threeArrays.get(arrIdx);
                sum += currArr.get(idx);
            }
            result.set(idx, sum);
        }

        for (int idx = 0; idx < 100; ++idx) {
            if (result.get(idx) == 3) return idx;
        }
        return 0;
    }

    private static ArrayList<Integer> makeUniqueArray(String line) {
        var result = new ArrayList<Integer>(Collections.nCopies(100, 0));
        line.chars().forEach(intValRaw -> {
            int intVal =  calcPriority((char) intValRaw);
            var prvVal = result.get(intVal);
            if (prvVal == 0) {
                result.set(intVal, 1);
            }
        });
        return result;
    }

    private static int calcPriority(char c) {
        int intVal = (int) c;
        if (intVal <= 95 ) {
            return intVal - 65 + 27;
        }
        else {
            return intVal - 97 + 1;
        }
    }
}
