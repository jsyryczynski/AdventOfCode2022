package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.ToString;

public class Main {
    public static void main(String[] args) throws IOException {

        ArrayList<ArrayList<String>> map = new ArrayList<>();
        LinkedList<Move> movesList = new LinkedList<>();

        boolean readingMap = true;
        int maxRowLength = 1000;
        int maxFoundRowLength = 0;
        int startingPos = -1;
        int rowIdx = 0;
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    readingMap = false;
                    continue;
                }

                if (readingMap) {
                    int currentLineLength = line.length();
                    if (currentLineLength > maxFoundRowLength) {
                        maxFoundRowLength = currentLineLength;
                    }
                    ArrayList<String> row = new ArrayList<>(maxRowLength);
                    map.add(row);

                    for (int idx = 0; idx < line.length(); ++idx) {
                        String c = line.substring(idx, idx + 1);
                        if (rowIdx == 0 && startingPos == -1 && (c.equals("."))) {
                            startingPos = idx;
                        }
                        row.add(c);
                    }
                    rowIdx++;
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    for (int idx = 0; idx < line.length(); ++idx) {
                        String c = line.substring(idx, idx + 1);

                        if (isNumeric(c)) {
                            sb.append(c);
                        }
                        else {
                            if (!sb.isEmpty()) {
                                Integer value = Integer.parseInt(sb.toString());
                                sb.setLength(0);
                                movesList.add(new Move(value));
                            }
                            if (c.equals("L")) {
                                movesList.add(new Move(OrientationChange.LEFT));
                            }
                            else {
                                movesList.add(new Move(OrientationChange.RIGHT));
                            }
                        }
                    }
                    if (!sb.isEmpty()) {
                        Integer value = Integer.parseInt(sb.toString());
                        sb.setLength(0);
                        movesList.add(new Move(value));
                    }
                }

            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        Point mapSizeUnwrapped = new Point(maxFoundRowLength, rowIdx);
        Position currentPos = new Position(new Point(startingPos, 0), Orientation.RIGHT);

        // resize rows that are too short
        for (var row : map) {
            while (row.size() < mapSizeUnwrapped.x) {
                row.add(" ");
            }
        }

        for (Move move : movesList) {
            //currentPos.printBoard(map);
            System.out.println("----------------------------");
            System.out.println("Position before move " + currentPos);
            System.out.print("Move " + move + "\n");

            if (!move.isDirectionChange) {

                int moveIdx = 0;
                while (moveIdx < move.count) {
                    ++moveIdx;
                    Point vector = currentPos.getOrientation().getVector();
                    Position triedNexPos = currentPos.makeDeepCopy();
                    triedNexPos = triedNexPos.add(vector);
                    String mapElement = map.get(triedNexPos.getPoint().y).get(triedNexPos.getPoint().x);

                    if (mapElement.equals(".")) {
                        currentPos = triedNexPos;
                    }
                    else if (mapElement.equals("#")) {
                        break;
                    }
                    //currentPos.printBoard(map);
                }
            }
            else {
                currentPos.setOrientation(currentPos.getOrientation().add(move));
            }
            System.out.println("Position after move " + currentPos);
        }

        int result = 1000 * (currentPos.getPoint().y + 1) + 4 * (currentPos.getPoint().x + 1) + currentPos.getOrientation().value;
        System.out.println("result " + result);
    }

    @ToString
    private static class Move {
        boolean isDirectionChange;
        int count;
        OrientationChange rotation;
        public Move(int count) {
            this.count = count;
            isDirectionChange = false;
        }
        public Move(OrientationChange rotation) {
            this.rotation = rotation;
            isDirectionChange = true;
        }
    }

    // TODO check ascii
    public static boolean isNumeric(String digit) {
        if (digit == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(digit);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    enum OrientationChange {
        LEFT(-1),
        RIGHT(1);

        private int value;
        OrientationChange(int i) {
            value = i;
        }
    }

    @ToString
    enum Orientation {
        RIGHT(0),
        BOTTOM(1),
        LEFT(2),
        TOP(3);

        private final int value;
        private static Map<Integer, Orientation> map = new HashMap<Integer, Orientation>();

        Orientation(int i) {
            value = i;
        }

        public Orientation add(Move move) {
            return valueOf(Math.floorMod(value + move.rotation.value, 4));
        }

        static {
            for (Orientation value : Orientation.values()) {
                map.put(value.value, value);
            }
        }

        public static Orientation valueOf(int value) {
            return map.get(value);
        }

        public Point getVector() {
            if (value == RIGHT.value) {
                return new Point(1,0);
            }
            else if (value == BOTTOM.value) {
                return new Point(0, 1);
            }
            else if (value == LEFT.value) {
                return new Point(-1, 0);
            }
            else {
                return new Point(0, -1);
            }
        }
    }

    @AllArgsConstructor
    @ToString
    private static class Point {
        final int x;
        final int y;

        public Point add(Point vector) {
            return new Point(x + vector.x, y + vector.y);
        }
    }

    @ToString
    @AllArgsConstructor
    private static class Position {

        Point point;
        Orientation orientation;

        public Orientation getOrientation() {
            return orientation;
        }

        public Point getPoint() {
            return point;
        }

        public Position add(Point vector) {
            Point resultingPoint = point.add(vector);
            Orientation resultingOrientation = orientation;

            if ( resultingPoint.x < 100 && resultingPoint.y == -1) {
                resultingPoint = new Point(0, 100 + resultingPoint.x);
                resultingOrientation = Orientation.RIGHT;
            }
            else if ( resultingPoint.x >= 100 && resultingPoint.y == -1) {
                resultingPoint = new Point(resultingPoint.x - 100, 199);
                resultingOrientation = Orientation.TOP;
            }
            else if ( resultingPoint.x >= 150) {
                resultingPoint = new Point(99, 149 - resultingPoint.y);
                resultingOrientation = Orientation.LEFT;
            }
            else if ( resultingPoint.x >= 100 && resultingPoint.y == 50) {
                resultingPoint = new Point(99, resultingPoint.x - 50);
                resultingOrientation = Orientation.LEFT;
            }
            else if ( resultingPoint.x == 100 && resultingPoint.y >= 50 && resultingPoint.y < 100) {
                resultingPoint = new Point(resultingPoint.y + 50, 49);
                resultingOrientation = Orientation.TOP;
            }
            else if ( resultingPoint.x == 100 && resultingPoint.y >= 100) {
                resultingPoint = new Point(149,  149 - resultingPoint.y);
                resultingOrientation = Orientation.LEFT;
            }
            else if ( resultingPoint.x >= 50 && resultingPoint.y == 150) {
                resultingPoint = new Point(49, resultingPoint.x + 100);
                resultingOrientation = Orientation.LEFT;
            }
            else if ( resultingPoint.x == 50 && resultingPoint.y >= 150) {
                resultingPoint = new Point(resultingPoint.y - 100, 149);
                resultingOrientation = Orientation.TOP;
            }
            else if (resultingPoint.y == 200) {
                resultingPoint = new Point(resultingPoint.x + 100, 0 );
                resultingOrientation = Orientation.BOTTOM;
            }
            else if (resultingPoint.x == -1 && resultingPoint.y >= 150) {
                resultingPoint = new Point(resultingPoint.y - 100, 0 );
                resultingOrientation = Orientation.BOTTOM;
            }
            else if (resultingPoint.x == -1 && resultingPoint.y < 150) {
                resultingPoint = new Point(50, 149 - resultingPoint.y);
                resultingOrientation = Orientation.RIGHT;
            }
            else if (resultingPoint.x <= 49 && resultingPoint.y == 99) {
                resultingPoint = new Point(50, 50 + resultingPoint.x);
                resultingOrientation = Orientation.RIGHT;
            }
            else if (resultingPoint.x == 49 && resultingPoint.y <= 99 && resultingPoint.y >= 50) {
                resultingPoint = new Point(resultingPoint.y - 50, 100);
                resultingOrientation = Orientation.BOTTOM;
            }
            else if (resultingPoint.x == 49 && resultingPoint.y <= 49) {
                resultingPoint = new Point(0, 149 - resultingPoint.y);
                resultingOrientation = Orientation.RIGHT;
            }

            return new Position(resultingPoint, resultingOrientation);
        }

        public Position makeDeepCopy() {
            Position result = new Position(new Point(point.x, point.y), orientation);
            return result;
        }

        public void setOrientation(Orientation newOrientation) {
            orientation = newOrientation;
        }

        public void printBoard(ArrayList<ArrayList<String>> board) {
            for (int yIdx = 0; yIdx < board.size(); ++yIdx) {
           //for (int yIdx = 0; yIdx < 3; ++yIdx) {
                var row = board.get(yIdx);
                for (int xIdx = 0; xIdx < row.size(); ++xIdx) {
                    if (point.y == yIdx && point.x == xIdx) {
                        if (orientation == Orientation.TOP) {
                            System.out.print("^");
                        }
                        else if (orientation == Orientation.BOTTOM) {
                            System.out.print("V");
                        }
                        else if (orientation == Orientation.LEFT) {
                            System.out.print("<");
                        }
                        else {
                            System.out.print(">");
                        }
                    }
                    else {

                        String s = row.get(xIdx);
                        if (s.equals("#")) {
                            System.out.print("_");
                        }
                        else
                            System.out.print(s);
                    }
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }
}
