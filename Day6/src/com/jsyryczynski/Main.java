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
                System.out.println(line);

                LinkedList<String> lastLetters = new LinkedList<>();
                for (int charidx = 0; charidx < line.length(); ++charidx) {
                    String currentLetter = line.substring(charidx, charidx + 1);
                    lastLetters.addLast(currentLetter);
                    if (lastLetters.size() > 14) {
                        lastLetters.removeFirst();
                        if (checkIsUnique(lastLetters)) {
                            System.out.println("Unique idx " + (charidx + 1));
                            break;
                        }
                    }

                }
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static boolean checkIsUnique(LinkedList<String> lastLetters) {
        for (int lidx = 0; lidx < lastLetters.size(); ++lidx) {
            for (int ridx = lidx + 1; ridx < lastLetters.size(); ++ridx) {
                if (lastLetters.get(lidx).equals(lastLetters.get(ridx))) {
                    return false;
                }
            }
        }
        return  true;
    }

}
