package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class Main {

    private static boolean moved = true;

    public static void main(String[] args) throws IOException {

        ArrayList<ArrayList<String>> map = new ArrayList<>();
        Point startPoint = null;
        Point endPoint = null;
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            int yIdx = 0;
            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }

                var row = new ArrayList<String>();

                for (int xIdx = 0; xIdx < line.length(); ++xIdx) {

                    String s = line.substring(xIdx, xIdx+1);
                    if (yIdx == 0 && s.equals(".")){
                        startPoint = new Point(xIdx, yIdx);
                    }
                    else if (s.equals(".")) {
                        endPoint = new Point(xIdx, yIdx);
                    }
                    row.add(s);
                }

                map.add(row);
                yIdx++;
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        Map mapFull = new Map(map);

        int turnIdx = 0;

        turnIdx = findDestination(startPoint, endPoint, mapFull, turnIdx);
        turnIdx = findDestination(endPoint, startPoint, mapFull, turnIdx);
        turnIdx = findDestination(startPoint, endPoint, mapFull, turnIdx);

    }

    private static int findDestination(Point startPoint, Point endPoint, Map mapFull, int turnIdx) {
        HashSet<Point> possiblePositions = new HashSet<>();
        possiblePositions.add(startPoint);
        boolean foundExit = false;

        while (!foundExit) {
            HashSet<Point> nextPossiblePositions = new HashSet<>();
            for (Point position : possiblePositions) {
                if (position.equals(endPoint)) {
                    foundExit = true;
                    System.out.println("Reached exit in turn " + turnIdx);
                    break;
                }

                if (position.y < 0) {
                    // outside the map
                    continue;
                }

                if (position.y >= mapFull.height) {
                    // outside the map
                    continue;
                }

                // check if not occupied
                if (!mapFull.isEmpty(turnIdx, position)) {
                    continue;
                }

                nextPossiblePositions.add(position);
                nextPossiblePositions.add(position.add(Direction.BOTTOM.toVector()));
                nextPossiblePositions.add(position.add(Direction.TOP.toVector()));
                nextPossiblePositions.add(position.add(Direction.RIGHT.toVector()));
                nextPossiblePositions.add(position.add(Direction.LEFT.toVector()));
            }
            possiblePositions = nextPossiblePositions;
            turnIdx++;
        }
        return turnIdx;
    }
}

@AllArgsConstructor
class State {
    Point position;
    int turn;
}

enum Direction {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT;

    Point toVector() {
        if (this.equals(Direction.TOP)) {
            return new Point(0,-1);
        }
        else if (this.equals(Direction.RIGHT)) {
            return new Point(1,0);
        }
        else if (this.equals(Direction.BOTTOM)) {
            return new Point(0,1);
        }
        else {
            return new Point(-1, 0);
        }
    }
}

class MapField {
    boolean isWall;
    List<Direction> blizzards;
    public MapField() {
        isWall = false;
        blizzards = new ArrayList<>();
    }

    public MapField(String s) {
        blizzards = new ArrayList<>();
        if (s.equals(".")) {
            isWall = false;
        }
        else if (s.equals("#")) {
            isWall = true;
        }
        else if (s.equals("^")) {
            isWall = false;
            blizzards.add(Direction.TOP);
        }
        else if (s.equals(">")) {
            isWall = false;
            blizzards.add(Direction.RIGHT);
        }
        else if (s.equals("v")) {
            isWall = false;
            blizzards.add(Direction.BOTTOM);
        }
        else if (s.equals("<")) {
            isWall = false;
            blizzards.add(Direction.LEFT);
        }
    }
}

class Map {
    ArrayList<ArrayList<ArrayList<MapField>>> mapsPerTurn = new ArrayList<>();
    int width;  // including edges
    int height; // including edges
    int mod;

    public Map(ArrayList<ArrayList<String>> input) {
        mapsPerTurn.add(mapToMap(input));

        height = input.size();
        width = input.get(0).size();

        mod = (width - 2) * (height - 2);
    }

    private ArrayList<ArrayList<MapField>> mapToMap(ArrayList<ArrayList<String>> input) {
        ArrayList<ArrayList<MapField>> resultingMap = new ArrayList<>();
        for (int yIdx = 0; yIdx < input.size(); ++yIdx) {
            var oldRow = input.get(yIdx);

            ArrayList<MapField> newRow = new ArrayList<>();
            resultingMap.add(newRow);
            for (int xIdx = 0; xIdx < oldRow.size(); ++xIdx) {

                var elem = oldRow.get(xIdx);
                newRow.add(new MapField(elem));
            }
        }
        return resultingMap;
    }

    boolean isEmpty(int turn, Point p) {
        int turnMod = turn % mod;
        while (mapsPerTurn.size() <= turnMod) {
            mapsPerTurn.add(calcNextTurn());
        }

        MapField mapField = mapsPerTurn.get(turnMod).get(p.y).get(p.x);
        return !mapField.isWall && mapField.blizzards.isEmpty();
    }

    private ArrayList<ArrayList<MapField>> calcNextTurn() {
        var oldMap = mapsPerTurn.get(mapsPerTurn.size() - 1);

        ArrayList<ArrayList<MapField>> resultingMap = new ArrayList<>();
        for (int yIdx = 0; yIdx < oldMap.size(); ++yIdx) {
            var oldRow = oldMap.get(yIdx);

            if (yIdx == 0 || yIdx == (oldMap.size() - 1)) {
                // first and last row remain the same always
                resultingMap.add(oldRow);
                continue;
            }

            ArrayList<MapField> newRow = new ArrayList<>();
            resultingMap.add(newRow);
            for (int xIdx = 0; xIdx < oldRow.size(); ++xIdx) {

                var elem = oldRow.get(xIdx);
                if (elem.isWall) {
                    newRow.add(elem);
                }
                else {
                    Point currentPoint = new Point(xIdx, yIdx);
                    var addedElement = new MapField();

                    if(getLastPoint(currentPoint.add(Direction.TOP.toVector())).blizzards.contains(Direction.BOTTOM)) {
                        addedElement.blizzards.add(Direction.BOTTOM);
                    };

                    if(getLastPoint(currentPoint.add(Direction.BOTTOM.toVector())).blizzards.contains(Direction.TOP)) {
                        addedElement.blizzards.add(Direction.TOP);
                    };

                    if(getLastPoint(currentPoint.add(Direction.LEFT.toVector())).blizzards.contains(Direction.RIGHT)) {
                        addedElement.blizzards.add(Direction.RIGHT);
                    };

                    if(getLastPoint(currentPoint.add(Direction.RIGHT.toVector())).blizzards.contains(Direction.LEFT)) {
                        addedElement.blizzards.add(Direction.LEFT);
                    };
                    newRow.add(addedElement);
                }
            }
        }
        return resultingMap;
    }

    private MapField getLastPoint(Point point) {
        var lastTurnMap = mapsPerTurn.get(mapsPerTurn.size() - 1);
        Point wrapPoint = new Point(Math.floorMod(point.x - 1, width - 2) + 1, Math.floorMod(point.y - 1, height - 2) + 1 );
        return lastTurnMap.get(wrapPoint.y).get(wrapPoint.x);
    }


    public void printForTurn(int turnIdx) {
        int turnMod = turnIdx % mod;
        while (mapsPerTurn.size() <= turnMod) {
            mapsPerTurn.add(calcNextTurn());
        }

        var map = mapsPerTurn.get(turnMod);
        for (int yIdx = 0; yIdx < map.size(); ++yIdx) {
            var oldRow = map.get(yIdx);
            for (int xIdx = 0; xIdx < oldRow.size(); ++xIdx) {
                var elem = oldRow.get(xIdx);

                if (elem.isWall) {
                    System.out.print("#");
                }
                else if (elem.blizzards.size() == 1) {

                    if (elem.blizzards.contains(Direction.TOP)) {
                        System.out.print("^");
                    }
                    else if (elem.blizzards.contains(Direction.BOTTOM)) {
                        System.out.print("v");
                    }
                    else if (elem.blizzards.contains(Direction.RIGHT)) {
                        System.out.print(">");
                    }
                    else if (elem.blizzards.contains(Direction.LEFT)) {
                        System.out.print("<");
                    }
                }
                else if (elem.blizzards.size() > 1) {
                    System.out.print(elem.blizzards.size());
                }
                else {
                    System.out.print(".");
                }
            }
            System.out.println("");
        }
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
