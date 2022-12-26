package com.jsyryczynski;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class Main {

    public static void main(String[] args) {

        ArrayList<ArrayList<Integer>> heightMap = new ArrayList<>();
        Position startPosition = null;
        Position endPosition = null;
        int boardX;
        int boardY;

        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            int yPos = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                System.out.println(line);

                ArrayList<Integer> currentRow = new ArrayList<>();
                heightMap.add(currentRow);

                for (int xPos = 0; xPos< line.length(); ++xPos) {
                    String currentField = line.substring(xPos, xPos + 1);
                    Integer currentHeight;
                    if (currentField.equals("S")) {
                        currentHeight = 0;
                        startPosition = new Position(xPos, yPos);
                    }
                    else if (currentField.equals("E")) {
                        currentHeight = 'z' - 'a';
                        endPosition = new Position(xPos, yPos);
                    }
                    else {
                        currentHeight = currentField.toCharArray()[0] - 'a';
                    }
                    currentRow.add(currentHeight);
                }
                yPos++;
            }
            boardY = heightMap.size();
            boardX = heightMap.get(0).size();

            ArrayList<ArrayList<Boolean>> visited = new ArrayList<>();
            for (int yIdx = 0; yIdx < boardY; ++yIdx) {
                ArrayList<Boolean> tmp = new ArrayList<>();
                for (int xIdx = 0; xIdx < boardX; ++xIdx) {
                    tmp.add(false);
                }
                visited.add(tmp);
            }

            Queue<Position> nodeQueue = new LinkedList<>();
            nodeQueue.add(endPosition);
            while (!nodeQueue.isEmpty()) {
                Position currentPos = nodeQueue.poll();
                int currentX = currentPos.x;
                int currentY = currentPos.y;
                int currentSteps = currentPos.steps;
                int currentHeight =  heightMap.get(currentY).get(currentX);

                if (visited.get(currentY).get(currentX).equals(true)) {
                    continue;
                }

                visited.get(currentY).set(currentX, true);

                if (currentHeight == 0) {
                    System.out.println("result is " + currentPos.steps);
                    break;
                }

                if (currentX > 0) {
                    int neighbourX = currentX - 1;
                    int neighbourY = currentY;

                    int candidateHeight = heightMap.get(neighbourY).get(neighbourX);
                    int diff = candidateHeight - currentHeight;
                    if (diff >= -1) {
                        nodeQueue.add(new Position(neighbourX, currentY, currentSteps + 1));
                    }
                }
                if (currentX < boardX - 1) {
                    int neighbourX = currentX + 1;
                    int neighbourY = currentY;

                    int candidateHeight = heightMap.get(neighbourY).get(neighbourX);
                    int diff = candidateHeight - currentHeight;
                    if (diff >= -1) {
                        nodeQueue.add(new Position(neighbourX, neighbourY, currentSteps + 1));
                    }
                }
                if (currentY> 0) {
                    int neighbourX = currentX;
                    int neighbourY = currentY - 1;

                    int candidateHeight = heightMap.get(neighbourY).get(neighbourX);
                    int diff = candidateHeight - currentHeight;
                    if (diff >= -1) {
                        nodeQueue.add(new Position(neighbourX, neighbourY, currentSteps + 1));
                    }
                }
                if (currentY < boardY - 1) {
                    int neighbourX = currentX;
                    int neighbourY = currentY + 1;

                    int candidateHeight = heightMap.get(neighbourY).get(neighbourX);
                    int diff = candidateHeight - currentHeight;
                    if (diff >= -1) {
                        nodeQueue.add(new Position(neighbourX, neighbourY, currentSteps + 1));
                    }
                }
            }
            /*
            for (int yIdx = 0; yIdx < boardY; ++yIdx) {
                for (int xIdx = 0; xIdx < boardX; ++xIdx) {
                    if (visited.get(yIdx).get(xIdx)) {
                        System.out.print("V");
                    }
                    else {
                        System.out.print(".");
                    }

                }
                System.out.println("");
            }
            System.out.println("--------------------------");
            for (int yIdx = 0; yIdx < boardY; ++yIdx) {
                for (int xIdx = 0; xIdx < boardX; ++xIdx) {
                    if (visited.get(yIdx).get(xIdx)) {
                        int height = heightMap.get(yIdx).get(xIdx) % 10;
                        System.out.print(height);
                    }
                    else {
                        System.out.print(".");
                    }

                }
                System.out.println("");
            }

            for (char c = 'a' ; c <= 'z' ; ++c) {
                System.out.println("" + c + " -> " + (c - 'a') % 10);
            }

             */

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

    }
}

@RequiredArgsConstructor
@AllArgsConstructor
class Position {
    final int x;
    final int y;

    @Setter
    @Getter
    int steps = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
