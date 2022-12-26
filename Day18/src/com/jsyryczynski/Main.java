package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class Main {
    public static void main(String[] args) throws IOException {

        int inputCount = 0;
        int maxValue = 0;
        HashSet<Point> points = new HashSet<>();
        List<Point> neighbours = List.of(
                new Point(-1,0,0),
                new Point(1,0,0),
                new Point(0,-1,0),
                new Point(0,1,0),
                new Point(0,0,-1),
                new Point(0,0,1)
        );

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }

                System.out.println(line);
                String[] split = line.split(",");
                System.out.println(Arrays.toString(split));
                int[] values = new int[3];
                for (int idx = 0; idx < split.length; ++idx) {
                    var v = split[idx];
                    int currentValue = Integer.parseInt(v) + 1; // add 1 for air to have one row around
                    values[idx] = currentValue;
                    if (currentValue > maxValue) {
                        maxValue = currentValue;
                    }
                }
                Point p = new Point(values[0], values[1], values[2]);
                points.add(p);

                inputCount++;
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        HashSet<Point> outsideAir = new HashSet<>();
        Queue<Point> airQueue = new LinkedList<>();
        airQueue.add(new Point(0,0,0));

        while (!airQueue.isEmpty()) {
            Point currentPoint = airQueue.poll();

            if (points.contains(currentPoint)) {
                // rock
                continue;
            }

            if (currentPoint.x < 0 || currentPoint.y < 0 || currentPoint.z < 0 || currentPoint.x > (maxValue + 1)
                    || currentPoint.y > (maxValue + 1) || currentPoint.z > (maxValue + 1)) {
                // outside the board
                continue;
            }

            if (outsideAir.contains(currentPoint)) {
                // already visited
                continue;
            }

            outsideAir.add(currentPoint);

            for (Point currentNeighbour : neighbours) {
                Point testedNeighbour = currentNeighbour.add(currentPoint);
                airQueue.add(testedNeighbour);
            }
        }

        long result = 0;
        for(Point currentPoint : points) {
            for (Point currentNeighbour : neighbours) {
                Point testedNeighbour = currentNeighbour.add(currentPoint);
                if (!points.contains(testedNeighbour)) {
                    if (outsideAir.contains(testedNeighbour)) {
                        result++;
                    }
                }
            }
        }


        System.out.println("inputCount " + inputCount);
        System.out.println("maxValue " + maxValue);
        System.out.println("result " + result);
    }

}

@AllArgsConstructor
@EqualsAndHashCode
class Point {
    int x;
    int y;
    int z;

    Point add(Point o) {
        return new Point(o.x + x, o.y + y, o.z + z);
    }
}
