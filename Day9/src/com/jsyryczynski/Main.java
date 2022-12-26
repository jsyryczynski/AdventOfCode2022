package com.jsyryczynski;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import lombok.EqualsAndHashCode;

public class Main {

    public static final int ROPE_LENGTH = 10;

    public static void main(String[] args) {

        HashSet<Postion> uniqueTailPostions = new HashSet<>();

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            Postion ropePos[] = new Postion[ROPE_LENGTH];
            for (int ropeIdx = 0; ropeIdx < ROPE_LENGTH; ++ropeIdx) {
                ropePos[ropeIdx] = new Postion(0,0);
            }
            uniqueTailPostions.add(new Postion(ropePos[ROPE_LENGTH - 1]));

            int lineIdx = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);

                String[] stripLine = line.split("\s");

                String command = stripLine[0];
                Long step = Long.parseLong(stripLine[1]);

                for (int subStepIdx = 0; subStepIdx < step; ++subStepIdx) {
                    if (lineIdx == 4) {
                        System.out.println("error");
                    }
                    ropePos[0].move(command, 1);
                    for (int ropeIdx = 1; ropeIdx < ROPE_LENGTH; ++ropeIdx) {
                        ropePos[ropeIdx].moveAfter(ropePos[ropeIdx - 1]);
                    }
                    uniqueTailPostions.add(new Postion(ropePos[ROPE_LENGTH - 1]));
                    lineIdx++;
                }
            }
            System.out.println("unique pos " +  uniqueTailPostions.size());
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static void printBoard(Postion currentHeadPos, Postion currentTailPos) {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------------------------BEGIN MAP------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

        long minXsize = -ROPE_LENGTH;
        long maxXsize = ROPE_LENGTH;
        long minYsize = -ROPE_LENGTH;
        long maxYsize = ROPE_LENGTH;

        if (currentHeadPos.x < minXsize) {
            minXsize = currentHeadPos.x;
        }
        if (currentTailPos.x < minXsize) {
            minXsize = currentTailPos.x;
        }

        if (currentHeadPos.x > maxXsize) {
            maxXsize = currentHeadPos.x;
        }
        if (currentTailPos.x > maxXsize) {
            maxXsize = currentTailPos.x;
        }

        if (currentHeadPos.y < minYsize) {
            minYsize = currentHeadPos.y;
        }
        if (currentTailPos.y < minYsize) {
            minYsize = currentTailPos.y;
        }

        if (currentHeadPos.y > maxYsize) {
            maxYsize = currentHeadPos.y;
        }
        if (currentTailPos.y > maxYsize) {
            maxYsize = currentTailPos.y;
        }


        for (long rowIdx = minYsize; rowIdx <= maxYsize; rowIdx++) {
            for (long colIdx = minXsize; colIdx <= maxXsize; colIdx++) {

                if (rowIdx == currentHeadPos.y && colIdx == currentHeadPos.x) {
                    System.out.print("H");
                }
                else if (rowIdx == currentTailPos.y && colIdx == currentTailPos.x) {
                    System.out.print("T");
                }
                else if (rowIdx == 0 && colIdx == 0) {
                    System.out.print("s");
                }
                else {
                    System.out.print(".");
                }
            }
            System.out.println("\s");
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------------------------END MAP--------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

    }

}

@EqualsAndHashCode
class Postion{
    long x;
    long y;

    Postion(long x, long y) {
        this.x = x;
        this.y = y;
    }

    Postion(Postion other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void move(String command, int i) {
        if (command.equals("U")) {
            y--;
        }
        else  if (command.equals("D")) {
            y++;
        }
        else if (command.equals("L")) {
            x--;
        }
        else {
            x++;
        }
    }

    public String toString() {
        return "x: " + x + " y: " + y;
    }

    public void moveAfter(Postion currentHeadPos) {
        long xdiff = currentHeadPos.x - this.x;
        long ydiff = currentHeadPos.y - this.y;

        if (xdiff == 0 && Math.abs(ydiff) >= 2) {
            y += ydiff > 0 ? 1 : -1;
        }
        else if (ydiff == 0 && Math.abs(xdiff) >= 2) {
            x += xdiff > 0 ? 1 : -1;
        }
        else if ((xdiff >= 2 && ydiff >= 1) || (xdiff >= 1 && ydiff >= 2)) {
            x+=1;
            y+=1;
        }
        else if ((xdiff <= -2 && ydiff <= -1) || (xdiff <= -1 && ydiff <= -2)) {
            x-=1;
            y-=1;
        }
        else if ((xdiff <= -2 && ydiff >= 1) || (xdiff <= -1 && ydiff >= 2)) {
            x-=1;
            y+=1;
        }
        else if ((xdiff >= 2 && ydiff <= -1) || (xdiff >= 1 && ydiff <= -2)) {
            x+=1;
            y-=1;
        }
    }
}
