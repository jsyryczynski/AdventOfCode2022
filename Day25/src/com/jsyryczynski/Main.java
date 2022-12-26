package com.jsyryczynski;

import static java.util.Map.entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class Main {

    private static boolean moved = true;
    static ArrayList<Long> pows = new ArrayList<>();
    static Map<Long, String> possibleDigits = Map.ofEntries(
        entry(2L, "2"),
        entry(1L, "1"),
        entry(0L, "0"),
        entry(-1L, "-"),
        entry(-2L, "=")
    );

    public static void main(String[] args) throws IOException {

        long sum = 0;
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }

                long number = 0;
                for (int xIdx = line.length() - 1; xIdx >= 0 ; --xIdx) {

                    String s = line.substring(xIdx, xIdx+1);
                    long digit;
                    if (s.equals("2")) {
                        digit = 2;
                    }
                    else if (s.equals("1")) {
                        digit = 1;
                    }
                    else if (s.equals("0")) {
                        digit = 0;
                    }
                    else if (s.equals("-")) {
                        digit = -1;
                    }
                    else{
                        digit = -2;
                    }
                    number += getPow(line.length() - (xIdx + 1) ) * digit;
                }

                //System.out.println("" + line + "  " + number);
                sum += number;
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        System.out.println(sum);

        System.out.println(convertToElf(sum));

    }

    private static String convertToElf(long sum) {
        StringBuilder sb = new StringBuilder();

        while (sum != 0) {
            long result = sum / 5;
            long remainder = sum - result * 5;

            System.out.println("result " + result);
            System.out.println("remainder " + remainder);

            if (remainder > 2) {
                remainder -= 5;
            }
            else if (remainder < -2) {
                remainder += 5;
            }

            sb.append(possibleDigits.get(remainder));
            // sum = result // would be answer for normal base 5
            sum = (sum - remainder) / 5;
        }

        return sb.reverse().toString();
    }

    private static long getPow(int xIdx) {
        if (pows.isEmpty()) {
            long value = 1;
            for (int idx = 0; idx < 50; ++idx) {
                pows.add(value);
                value = value * 5;
            }
        }
        return pows.get(xIdx);
    }
}


@EqualsAndHashCode
@AllArgsConstructor
class Point{
    int x;
    int y;

    public Point add(Point point) {
        return new Point(x + point.x, y + point.y);
    }
}
