package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            // 0 - empty
            // 1 - wall
            // 2 - sand
            ArrayList<ArrayList<Integer>> board = new ArrayList<>();
            for(int rowIdx = 0; rowIdx < 1000; ++rowIdx) {
                ArrayList<Integer> row = new ArrayList<>();
                board.add(row);
                for(int colIdx = 0; colIdx < 1000; ++colIdx) {
                    row.add(0);
                }
            }

            int highestY = 0;
            while (scanner.hasNext()) {
                String line1 = scanner.nextLine();
                if (line1.isEmpty()) {
                    continue;
                }


                System.out.println(line1);
                String[] split = line1.split("[\\W]");
                System.out.println(split.length + " " + Arrays.toString(split));

                int pointNum = (split.length - 2) / 5 + 1;
                System.out.println(pointNum);

                int prvPointX = -1;
                int prvPointY = -1;
                for (int pointIdx = 0; pointIdx < pointNum; ++pointIdx) {
                    int pointX = Integer.parseInt(split[pointIdx * 5]);
                    int pointY = Integer.parseInt(split[pointIdx * 5 + 1]);

                    if (pointY > highestY) {
                        highestY = pointY;
                    }
                    if (pointIdx > 0) {
                        // draw a line on map
                        int diffXMax = prvPointX - pointX;
                        int diffYMax = prvPointY - pointY;
                        if (diffXMax != 0) {
                            for (int diffIdxX = 0; Math.abs(diffIdxX) <= Math.abs(diffXMax);
                                    diffIdxX += diffXMax / Math.abs(diffXMax)) {
                                int currentX = prvPointX - diffIdxX;
                                int currentY = prvPointY;
                                board.get(currentY).set(currentX, 1);
                            }
                        }
                        if (diffYMax != 0) {
                            for (int diffIdxY = 0; Math.abs(diffIdxY) <= Math.abs(diffYMax);
                                    diffIdxY += diffYMax / Math.abs(diffYMax)) {
                                int currentX = prvPointX;
                                int currentY = prvPointY - diffIdxY;
                                board.get(currentY).set(currentX, 1);
                            }
                        }
                    }

                    prvPointX = pointX;
                    prvPointY = pointY;
                }
            }

            highestY += 2;
            for (int xIdx = 0; xIdx < 1000; xIdx++) {
                board.get(highestY).set(xIdx, 1);
            }

            int sandCount = 0;
            int sandStartX = 500;
            int sandStartY = 0;
            while (true) {
                if (board.get(sandStartY).get(sandStartX) == 2) {
                    // sand up to top
                    break;
                }
                board.get(sandStartY).set(sandStartX, 2);
                Point sandPos = new Point(sandStartX, sandStartY);
                Point prvSandPos = new Point(sandStartX, sandStartY);
                boolean end = false;
                while(sandPos.y < 999) {
                    sandPos = moveSand(board, sandPos);

                    if (prvSandPos.equals(sandPos)) {
                        break;
                    }
                    if (sandPos.y == 999) {
                        end = true;
                        break;
                    }
                    board.get(prvSandPos.y).set(prvSandPos.x, 0);
                    board.get(sandPos.y).set(sandPos.x, 2);
                    prvSandPos = sandPos;
                    //drawBoard(board);
                }
                //drawBoard(board);
                if (end) {
                    break;
                }
                ++sandCount;
            }

            //drawBoard(board);

            System.out.println("result " + sandCount);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

    }

    private static void drawBoard(ArrayList<ArrayList<Integer>> board) {
        System.out.println("-------------------------------------------");
        for(int rowIdx = 0; rowIdx < 10; ++rowIdx) {
            ArrayList<Integer> row =  board.get(rowIdx);
            for(int colIdx = 450; colIdx < 550; ++colIdx) {
                int val = row.get(colIdx);
                if (val == 0) {
                    System.out.print(".");
                }
                else if (val == 1) {
                    System.out.print("#");
                }
                else if (val == 2) {
                    System.out.print("o");
                }
            }
            System.out.println("");
        }
    }

    /*
    0 - next ok
    1 - falling out of board
    2 - no move
     */
    private static Point moveSand(ArrayList<ArrayList<Integer>> board, Point sandPos) {
        if (board.get(sandPos.y + 1).get(sandPos.x) == 0) {
            return new Point(sandPos.x, sandPos.y + 1);
        }
        else if (board.get(sandPos.y + 1).get(sandPos.x - 1) == 0) {
            return new Point(sandPos.x - 1, sandPos.y + 1);
        }
        else if (board.get(sandPos.y + 1).get(sandPos.x + 1) == 0) {
            return new Point(sandPos.x + 1, sandPos.y + 1);
        }
        else return sandPos;
    }
}

@AllArgsConstructor
@EqualsAndHashCode
class Point {
    int x;
    int y;
}
