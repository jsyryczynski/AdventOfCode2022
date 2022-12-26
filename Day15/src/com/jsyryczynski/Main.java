package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import javax.swing.text.Position;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class Main {

    static ArrayList<Point> sensorList = new ArrayList<>();
    static ArrayList<Point> beaconList = new ArrayList<>();
    static long searchArea = 20;

    public static void main(String[] args) {

        long minX = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE;
        long minY = Long.MAX_VALUE;
        long maxY = Long.MIN_VALUE;
        long maxDistance = Long.MIN_VALUE;

        HashSet<Point> uniqueBeaconList= new HashSet<>();

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNext()) {
                String line1 = scanner.nextLine();
                if (line1.isEmpty()) {
                    continue;
                }

                System.out.println(line1);
                String[] split = line1.split("[\s=:,]");
                System.out.println(Arrays.toString(split));

                long sensorX = Long.parseLong(split[3]);
                if (sensorX < minX) {
                    minX = sensorX;
                }
                if (sensorX > maxX) {
                    maxX = sensorX;
                }
                long sensorY = Long.parseLong(split[6]);
                if (sensorY < minY) {
                    minY = sensorY;
                }
                if (sensorY > maxY) {
                    maxY = sensorY;
                }
                Point sensorPos = new Point(sensorX, sensorY);
                sensorList.add(sensorPos);

                long beaconX = Long.parseLong(split[13]);
                if (beaconX < minX) {
                    minX = beaconX;
                }
                if (beaconX > maxX) {
                    maxX = beaconX;
                }
                long beaconY = Long.parseLong(split[16]);
                if (beaconY < minY) {
                    minY = beaconY;
                }
                if (beaconY > maxY) {
                    maxY = beaconY;
                }
                Point beaconPos = new Point(beaconX, beaconY);
                long currentDistance = beaconPos.distance(sensorPos);
                if (currentDistance > maxDistance)    {
                    maxDistance = currentDistance;
                }
                beaconList.add(beaconPos);
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }


        if (beaconList.size() > 20) {
            searchArea = 4000000;
        }
        long frequency = 4000000;

        ArrayList<Point> possiblePoints = new ArrayList<>();


        for (int sensorIdx = 0; sensorIdx < sensorList.size(); ++sensorIdx) {
            Point sensorPos = sensorList.get(sensorIdx);
            Point beaconPos = beaconList.get(sensorIdx);
            long distance = sensorPos.distance(beaconPos);

            checkAround(sensorPos, distance);
        }


/*


        for (int beaconIdx = 0; beaconIdx < beaconList.size(); ++beaconIdx) {
            Point beaconPos = beaconList.get(beaconIdx);
            uniqueBeaconList.add(beaconPos);
        }

        long count = 0;
        long yPos = 10;
        if (beaconList.size() > 20) {
            yPos = 2000000;
        }
        for (long xPos = minX - maxDistance - 1; xPos <= maxX + maxDistance + 1; ++xPos) {
            System.out.println("xPos " + xPos);
            for (int sensorIdx = 0; sensorIdx < sensorList.size(); ++sensorIdx) {
                Point sensorPos = sensorList.get(sensorIdx);
                Point beaconPos = beaconList.get(sensorIdx);
                Point currentPos = new Point(xPos, yPos);
                long distance = sensorPos.distance(beaconPos);
                if (currentPos.distance(sensorPos) <= distance) {
                    boolean isAlreadyPresent = false;
                    for (Point testBeaconPos : uniqueBeaconList) {
                        if (testBeaconPos.equals(currentPos)) {
                            isAlreadyPresent = true;
                            break;
                        }
                    }
                    if (!isAlreadyPresent) {
                        System.out.println("increasing count");
                        ++count;
                        break;
                    }
                }
            }
        }
        System.out.println("count " + count);

 */
    }



    // Theory being that the right point is on the edge of one of the sensor ranges
    // so we just walk around the damn thing
    public static void checkAround(Point s, long ring)
    {
        long count = ring+1;
        LinkedList<Point> dirs = new LinkedList<>();
        dirs.add(new Point(1,1)); // down right
        dirs.add(new Point(-1,1)); // down left
        dirs.add(new Point(-1,-1)); // up left
        dirs.add(new Point(1, -1)); //up right

        Point cur = s.add(new Point(0, -ring-1));

        for(Point dir : dirs)
            for(long step =0; step<count; step++)
            {
                cur = cur.add(dir);
                if (!pointImpossible(cur))
                {
                    System.out.println(cur);
                    long n = cur.x * 4000000 + cur.y;
                    System.out.println("Part 2: " + n);
                }
            }
    }

    public static boolean pointImpossible(Point p)
    {
        if (p.x <= 0) return true;
        if (p.y <= 0) return true;
        if (p.x > searchArea) return true;
        if (p.y > searchArea) return true;


        //if (points.contains(p)) return true;

        for (int sensorIdx = 0; sensorIdx < sensorList.size(); ++sensorIdx) {
            Point s = sensorList.get(sensorIdx);
            Point b = beaconList.get(sensorIdx);
            long dist = s.distance(b);
            long d = p.distance(s);
            if (d <= dist)
            {
                return true;
            }

        }
        return false;
    }

}

@AllArgsConstructor
@EqualsAndHashCode
@ToString
class Point {
    long x;
    long y;

    public long distance(Point beaconPos) {
        return Math.abs(x - beaconPos.x) + Math.abs(y - beaconPos.y);
    }


    public Point add(Point dir) {
        return new Point(x + dir.x, y + dir.y);
    }
}
