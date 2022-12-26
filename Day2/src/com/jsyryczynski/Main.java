package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            long totalCount = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);

                String[] splited = line.split("\\s+");
                String opponent = splited[0];
                String result = splited[1];

                String me = calculateMe(opponent, result);
                long chooseCount = calculateChooseCount(me);
                long winCount = calculateWinCount(result);

                totalCount += winCount;
                totalCount += chooseCount;
            }
            System.out.println("totalCount " + totalCount);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    /*
    A - rock
    B - paper
    C - scissors

    X - rock
    Y - paper
    Z - scissiors

    X - lose
    Y - draw
    Z - win
     */
    private static String calculateMe(String opponent, String result) {
       if (opponent.equals("A")){
           if (result.equals("X")){
                return "Z";
           }
           else if (result.equals("Y")){
               return "X";
           }
           else {
               return "Y";
           }
       }
       else if (opponent.equals("B")){
           if (result.equals("X")){
               return "X";
           }
           else if (result.equals("Y")){
               return "Y";
           }
           else {
               return "Z";
           }
       }
       else { // C
           if (result.equals("X")){
               return "Y";
           }
           else if (result.equals("Y")){
               return "Z";
           }
           else {
               return "X";
           }
       }
    }

    private static long calculateChooseCount(String me) {
        if (me.equals("X")) {
            return 1L;
        }
        else if (me.equals("Y")) {
            return 2L;
        }
        else {
            return 3L;
        }
    }

    private static long calculateWinCount(String result) {
        if (result.equals("X")) {
            return 0L;
        }
        else if (result.equals("Y")) {
            return 3L;
        }
        else {
            return 6L;
        }
    }
}
