package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class Main {

    private static boolean moved = true;

    public static void main(String[] args) throws IOException {

        HashSet<Point> map = new HashSet<>();
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            int yIdx = 0;
            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }

                for (int xIdx = 0; xIdx < line.length(); ++xIdx) {
                    String s = line.substring(xIdx, xIdx + 1);
                    if (s.equals("#")) {
                        map.add(new Point(xIdx, yIdx));
                    }
                }

                yIdx++;
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        System.out.println("Initial state");
        printMapWithSize(map);

        int roundIdx = 0;
        while (true) {
            System.out.println("Round " + roundIdx);
            HashMap<Point, LinkedList<Point>> chosenSource = new HashMap<>();
            HashSet<Point> newPostions = new HashSet<>();

            // choose destination
            for (var elf : map) {
                if (elf.equals(new Point(7,2))) {
                    System.out.println("DEBUG");
                }
                Point chosenPoint = choosePoint(elf, map, roundIdx);
                if (!chosenSource.containsKey(chosenPoint)) {
                    chosenSource.put(chosenPoint, new LinkedList<>());
                }

                var list = chosenSource.get(chosenPoint);
                list.push(elf);
            }

            // move each elf
            for (var entry : chosenSource.entrySet()) {
                if (entry.getValue().size() == 1) {
                    newPostions.add(entry.getKey());

                    if (!entry.getKey().equals(entry.getValue().get(0))){
                        moved = true;
                    }
                }
                else {
                    for (var elf : entry.getValue()) {
                        newPostions.add(elf);
                    }
                }
            }


            if (map.equals(newPostions)) {
                System.out.println("RESULT " + (roundIdx + 1));
                break;
            }

            map = newPostions;

            System.out.println("Map after round " + (roundIdx + 1));
            //printMapWithSize(map);

            roundIdx++;
        }

        //printMapWithSize(map);
    }

    private static void printMapWithSize(HashSet<Point> map) {
        // calc final map size
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (var elf : map) {
            if (elf.x < minX) {
                minX = elf.x;
            }
            if (elf.x > maxX) {
                maxX = elf.x;
            }

            if (elf.y < minY) {
                minY = elf.y;
            }
            if (elf.y > maxY) {
                maxY = elf.y;
            }
        }
        printMap(map, minX, maxX, minY, maxY);

        System.out.println("Map size is " + (maxX - minX) + " x " + (maxY - minY));
        System.out.println("Elves count " + map.size());
        int area = (maxX+ 1- minX) * (maxY + 1 - minY);
        System.out.println("Empty fields: " + (area - map.size()) );
    }

    private static void printMap(HashSet<Point> map, int minX, int maxX, int minY, int maxY) {
        System.out.printf("      ");
        for (int xIdx = minX; xIdx <= maxX; ++xIdx) {
            System.out.print(xIdx%10);
        }
        System.out.println("");
        for (int yIdx = minY; yIdx <= maxY; ++yIdx) {
            System.out.printf("%5d ", yIdx);
            for (int xIdx = minX; xIdx <= maxX; ++xIdx) {
                Point p = new Point(xIdx, yIdx);
                if (map.contains(p)) {
                    System.out.print("#");
                }
                else {
                    System.out.print(".");
                }

            }
            System.out.println("");
        }
    }

    private static Point choosePoint(Point elf, HashSet<Point> map, int round) {

        LinkedList<Point> allPoints = getAllNeighbourPoint(elf);
        if(allEmpty(allPoints, map)) {
            return elf;
        };

        for (int tryIdx = round; tryIdx < round + 4; ++tryIdx) {
            Point vector = getVec(tryIdx);
            List<Point> checkePoints = pointToCheck(elf, vector);
            if (allEmpty(checkePoints, map)) {
                return elf.add(vector);
            }
        }

        return elf;
    }

    private static List<Point> pointToCheck(Point elf, Point vector) {
        if (vector.x == 1) {
            return List.of(elf.add(new Point(1, -1)), elf.add(new Point(1, 0)), elf.add(new Point(1, 1)));
        }
        else if (vector.x == -1) {
            return List.of(elf.add(new Point(-1, -1)), elf.add(new Point(-1, 0)), elf.add(new Point(-1, 1)));
        }
        else if (vector.y == -1) {
            return List.of(elf.add(new Point(-1, -1)), elf.add(new Point(0, -1)),elf.add(new Point(1, -1)));
        }
        else {
            return List.of(elf.add(new Point(-1, 1)), elf.add(new Point(0, 1)), elf.add(new Point(1, 1)));
        }
    }

    private static Point getVec(int tryIdx) {
        int mod = tryIdx % 4;
        if (mod == 0) {
            return new Point(0, -1);
        }
        else if (mod == 1) {
            return new Point(0, 1);
        }
        else if (mod == 2) {
            return new Point(-1, 0);
        }
        else {
            return new Point(1, 0);
        }
    }

    private static LinkedList<Point> getAllNeighbourPoint(Point elf) {
        LinkedList<Point> result = new LinkedList<>();

        result.add(elf.add(new Point(-1,1)));
        result.add(elf.add(new Point(0,1)));
        result.add(elf.add(new Point(1,1)));
        result.add(elf.add(new Point(1,0)));
        result.add(elf.add(new Point(1,-1)));
        result.add(elf.add(new Point(0,-1)));
        result.add(elf.add(new Point(-1,-1)));
        result.add(elf.add(new Point(-1,0)));
        return result;
    }

    private static boolean allEmpty(List<Point> allPoints, HashSet<Point> map) {
        for (var elf : allPoints) {
            if (map.contains(elf)) {
                return false;
            }
        }
        return true;
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
