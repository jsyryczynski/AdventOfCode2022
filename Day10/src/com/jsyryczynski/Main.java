package com.jsyryczynski;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            Integer registerX = 1;
            IntHolder cycle = new IntHolder(0);
            Integer count = 0;

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] lineSplit = line.split("\s");
                String command = lineSplit[0];

                //System.out.println(line);

                if (command.equals("noop")) {
                    count += increaseCycle(cycle, 1, registerX);
                } else if (command.equals("addx")) {
                    String value = lineSplit[1];

                    count += increaseCycle(cycle, 2, registerX);
                    registerX += Integer.parseInt(value);
                }
                //System.out.println("  Cycle " + cycle);
                //System.out.println("  registerX " + registerX);
                //System.out.println("  total count " + count);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static int increaseCycle(IntHolder cycle, Integer duration, Integer registerX) {
        int result = 0;
        for (int idx = 0; idx < duration; ++idx) {
            if (cycle.value % 40 == 0) {
                System.out.println("");
            }
            if (registerX >= (cycle.value % 40) - 1 && registerX <= (cycle.value % 40) + 1) {
                System.out.print("#");
            }
            else  {
                System.out.print(".");
            }


            cycle.add(1);
            if ( (cycle.value - 20) % 40 == 0) {
                //System.out.println("  cycle " + cycle.value );
                //System.out.println("  registerX " + registerX);
                result += cycle.value * registerX;
                //System.out.println("  adding " + result);
            }


        }
        return result;
    }
}

class IntHolder {
    public int value;
    public IntHolder(int i) {
        value = i;
    }
    public IntHolder add(int i) {
        value += i;
        return this;
    }
    public String toString() {
        return String.valueOf(value);
    }
}
