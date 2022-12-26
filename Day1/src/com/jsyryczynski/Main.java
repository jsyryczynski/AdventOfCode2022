package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            var pq = new PriorityQueue<Long>(100, (a, b) -> Long.compare(b,a));

            long currentCount = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);

                if (line.isEmpty()) {
                    System.out.println("currentCount " + currentCount);

                    pq.add(currentCount);
                    currentCount = 0;
                }
                else {
                    currentCount += Long.parseLong(line);
                    System.out.println("currentCount " + currentCount);
                }
            }

            pq.add(currentCount);
            System.out.println("currentCount " + currentCount);
            System.out.println("maxCount " + pq.peek());

            long totalCount = 0;
            for (int idx = 0; idx < 3; ++idx) {
                totalCount += pq.poll();
            }
            System.out.println("max 3 Count " + totalCount);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
