package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static final int FULL_DISK_SIZE = 70000000;

    public static void main(String[] args) {


        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            ArrayList<ArrayList<Integer>> board = new ArrayList<>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);
                var currLine = new ArrayList<Integer>();
                board.add(currLine);
                for (int rowIdx = 0; rowIdx < line.length(); ++rowIdx) {
                    String c = line.substring(rowIdx, rowIdx+1);
                    int height = Integer.parseInt(c);
                    currLine.add(height);
                }
            }

            ArrayList<ArrayList<Node>> heights = calculateMaxHeight(board);

            System.out.println("result "  + findHighestScenicScore(board, heights));

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static long findHighestScenicScore(ArrayList<ArrayList<Integer>> board, ArrayList<ArrayList<Node>> heights) {
        long result = 0;
        for (int rowIdx = 0; rowIdx < board.size(); ++rowIdx) {
            for (int colIdx = 0; colIdx < board.get(rowIdx).size(); ++colIdx) {
                int currentHeight = board.get(rowIdx).get(colIdx);

                // left
                int visibleLeft = 0;
                int neighbourIdx = colIdx - 1;
                while (neighbourIdx >= 0) {
                    ++visibleLeft;
                    int neighbourHeigh = board.get(rowIdx).get(neighbourIdx);
                    if (neighbourHeigh >= currentHeight) {
                        break;
                    }
                    --neighbourIdx;
                }

                // right
                int visibleRight = 0;
                neighbourIdx = colIdx + 1;
                while (neighbourIdx <  board.get(rowIdx).size()) {
                    ++visibleRight;
                    int neighbourHeigh = board.get(rowIdx).get(neighbourIdx);
                    if (neighbourHeigh >= currentHeight) {
                        break;
                    }
                    ++neighbourIdx;
                }

                // top
                int visibleTop = 0;
                neighbourIdx = rowIdx - 1;
                while (neighbourIdx >=0 ) {
                    ++visibleTop;
                    int neighbourHeigh = board.get(neighbourIdx).get(colIdx);
                    if (neighbourHeigh >= currentHeight) {
                        break;
                    }
                    --neighbourIdx;
                }

                // bottom
                int visibleBottom = 0;
                neighbourIdx = rowIdx + 1;
                while (neighbourIdx < board.size()) {
                    ++visibleBottom;
                    int neighbourHeigh = board.get(neighbourIdx).get(colIdx);
                    if (neighbourHeigh >= currentHeight) {
                        break;
                    }
                    ++neighbourIdx;
                }

                long tmpResult = visibleBottom * visibleTop * visibleLeft * visibleRight;
                if (tmpResult > result) {
                    result = tmpResult;
                }
            }
        }

        return result;
    }

    private static ArrayList<ArrayList<Node>> calculateMaxHeight(ArrayList<ArrayList<Integer>> board) {
        ArrayList<ArrayList<Node>> heights = new ArrayList<>();

        // init
        for (int rowIdx = 0; rowIdx < board.size(); ++rowIdx) {
            ArrayList<Node> row = new ArrayList<>();
            for (int colIdx = 0; colIdx < board.get(rowIdx).size(); ++colIdx) {
                row.add(new Node());
            }
            heights.add(row);
        }

        // calculate top
        for (int rowIdx = 0; rowIdx < board.size(); ++rowIdx) {
            for (int colIdx = 0; colIdx < board.get(rowIdx).size(); ++colIdx) {
                Node node = heights.get(rowIdx).get(colIdx);
                node.currentHeight = board.get(rowIdx).get(colIdx);

               if (rowIdx == 0) {
                   node.maxHeightTop = -1;
               }
               else {
                   Node topNode = heights.get(rowIdx - 1).get(colIdx);
                   node.maxHeightTop = topNode.maxHeightTop;
                   if (node.maxHeightTop < topNode.currentHeight) {
                       node.maxHeightTop = topNode.currentHeight;
                   }
               }
            }
        }

        // calculate bottom
        for (int rowIdx = board.size() - 1; rowIdx >= 0; --rowIdx) {
            for (int colIdx = 0; colIdx < board.get(rowIdx).size(); ++colIdx) {

                if (rowIdx == 1 && colIdx == 3) {
                    System.out.println("debug");
                }

                Node node = heights.get(rowIdx).get(colIdx);
                node.currentHeight = board.get(rowIdx).get(colIdx);

                if (rowIdx == board.size() - 1) {
                    node.maxHeightBottom = -1;
                }
                else {
                    Node bottomNode = heights.get(rowIdx + 1).get(colIdx);
                    node.maxHeightBottom = bottomNode.currentHeight;
                    if (node.maxHeightBottom < bottomNode.maxHeightBottom) {
                        node.maxHeightBottom = bottomNode.maxHeightBottom;
                    }
                }
            }
        }

        // calculate left
        for (int colIdx = 0; colIdx < board.get(0).size(); ++colIdx) {
            for (int rowIdx = 0; rowIdx < board.size(); ++rowIdx) {

                Node node = heights.get(rowIdx).get(colIdx);
                node.currentHeight = board.get(rowIdx).get(colIdx);

                if (colIdx == 0) {
                    node.maxHeightLeft = -1;
                }
                else {
                    Node leftNode = heights.get(rowIdx).get(colIdx - 1);
                    node.maxHeightLeft = leftNode.currentHeight;
                    if (node.maxHeightLeft < leftNode.maxHeightLeft) {
                        node.maxHeightLeft = leftNode.maxHeightLeft;
                    }
                }
            }
        }

        // calculate right
        for (int colIdx = board.get(0).size() - 1; colIdx >= 0; --colIdx) {
            for (int rowIdx = 0; rowIdx < board.size(); ++rowIdx) {

                Node node = heights.get(rowIdx).get(colIdx);
                node.currentHeight = board.get(rowIdx).get(colIdx);

                if (colIdx == board.get(0).size() - 1) {
                    node.maxHeightRight = -1;
                }
                else {
                    Node rightNode = heights.get(rowIdx).get(colIdx + 1);
                    node.maxHeightRight = rightNode.currentHeight;
                    if (node.maxHeightRight < rightNode.maxHeightRight) {
                        node.maxHeightRight = rightNode.maxHeightRight;
                    }
                }
            }
        }

        return  heights;
    }

}

class Node {
    int currentHeight;
    int maxHeightTop;
    int maxHeightBottom;
    int maxHeightLeft;
    int maxHeightRight;
}
