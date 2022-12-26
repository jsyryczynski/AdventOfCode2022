package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        long stacksCount = 0;
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<LinkedList<String>> stacks = new ArrayList<LinkedList<String>>();

        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            for (int idx = 0; idx < 100; ++idx) {
                stacks.add(new LinkedList<String>());
            }
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                lines.add(line);
                System.out.println(line);
                if (!line.isEmpty() && stacksCount != 0) {
                    String [] splitString = line.split("\s");
                    int moveCount = Integer.parseInt(splitString[1]);
                    int from = Integer.parseInt(splitString[3]) - 1;
                    int to = Integer.parseInt(splitString[5]) - 1;

                    for (int idx = 0; idx < stacksCount; ++idx) {
                        System.out.println("Debug before " + stacks.get(idx).toString());
                    }

                    LinkedList<String> tmp = new LinkedList<>();
                    for (int idx = 0; idx < moveCount; ++idx) {
                        tmp.add(stacks.get(from).removeLast());

                    }
                    for (int idx = 0; idx < moveCount; ++idx) {
                        stacks.get(to).add(tmp.removeLast());
                    }

                    for (int idx = 0; idx < stacksCount; ++idx) {
                        System.out.println("Debug after " + stacks.get(idx).toString());
                    }
                }
                if (!line.isEmpty() && line.substring(0,2).equals("\s1")) {
                    // get stack count
                    String [] splitString = line.split("\s");
                    stacksCount = Long.parseLong(splitString[splitString.length-1]);
                }
                if (!line.isEmpty() && stacksCount == 0) {
                    for (int idx = 0; idx < line.length(); ++idx) {
                        if ((idx-1)%4==0) {
                            String data = line.substring(idx, idx +1);

                            int stackNum = (idx - 1) /4;
                            if (!data.isEmpty() && !data.equals(" ")) {
                                //System.out.println("adding " + idx + " char " + data);
                                stacks.get(stackNum).push(data);
                                for (int stackIdx = 0; stackIdx < 3; ++stackIdx) {
                                    //System.out.println("After adding" + stacks.get(stackIdx).toString());
                                }
                            }
                        }
                    }
                }

            }
            String str1 = "";
            StringBuilder sb = new StringBuilder(str1);
            for (int idx = 0; idx < stacksCount; ++idx) {
                sb.append(stacks.get(idx).getLast());
            }
            System.out.println(sb);

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static void reverseOrder(LinkedList<String> strings) {
        LinkedList<String> tmp = new LinkedList<>();
        for(int idx = strings.size() - 1; idx >= 0 ; --idx) {
            tmp.add(strings.get(idx));
        }
        strings.clear();
        for(int idx = 0; idx < tmp.size(); ++idx) {
            strings.add(tmp.get(idx));
        }
    }
}
