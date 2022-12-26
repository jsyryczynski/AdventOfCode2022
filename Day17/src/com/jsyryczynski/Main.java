package com.jsyryczynski;

import static com.jsyryczynski.Main.elementsList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import lombok.ToString;

public class Main {

    static final long REAL_DATA_MAX = 1000000000000L;
    static final long TEST_DATA_MAX = 2022L;
    static final long MAX_TURN_NUM = TEST_DATA_MAX;

    static List<List<Integer>> elementsList = List.of(
            // line
            List.of(
                    0b001111000
            ),
            // cross
            List.of(
                    0b000010000,
                    0b000111000,
                    0b000010000
            ),
            //L
            List.of(
                    0b000111000,
                    0b000100000,
                    0b000100000
            ),
            // vertical line
            List.of(
                    0b000001000,
                    0b000001000,
                    0b000001000,
                    0b000001000
            ),
            // square
            List.of(
                    0b000011000,
                    0b000011000
            )
    );

    static long startPosY = 4;
    static List<Direction>  movesList = new LinkedList<>();
    static long moveIdx = 0;
    static long elemIdx = 0;

    public static void main(String[] args) throws IOException {

        long currentMaxHeight = 0;
        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNext()) {
                String line1 = scanner.nextLine();
                if (line1.isEmpty()) {
                    continue;
                }

                System.out.println(line1);

                movesList = line1.chars().mapToObj(ch -> {
                    if (ch == '<') {
                        return Direction.LEFT;
                    }
                    else {
                        return Direction.RIGHT;
                    }
                }).collect(Collectors.toList());
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        System.out.println("Moves size " + movesList.size());

        BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", false));
        BufferedWriter hashWriter = new BufferedWriter(new FileWriter("hash.txt", false));

        int rollingHashWidth = movesList.size() * elementsList.size() * 100;
        Board board = new Board(rollingHashWidth);

        writer.append("movesList.size() " + movesList.size() + "\n");
        writer.append("elementsList.size() " + elementsList.size() + "\n");
        writer.append("rollingHashWidth " + rollingHashWidth + "\n");

        ArrayList<Long> resultArray = new ArrayList<>();

        //for (long turnNum = 1; turnNum <= MAX_TURN_NUM + 1; ++turnNum) {
        for (long turnNum = 1; turnNum <= 3 * rollingHashWidth; ++turnNum) {

            currentMaxHeight = board.getMaxHeight();
            long apperancePosition = currentMaxHeight + startPosY;
            while (board.ySize() < apperancePosition + 4) {
                board.addEmptyRow();
            }

            Element element = new Element(elemIdx, apperancePosition);
            elemIdx++;

            settleElement(board, element);
            //board.print(element);
            System.out.println("turn " + turnNum + " currentMaxHeight " + currentMaxHeight + " hash " + board.hashCode());
            writer.append("turn " + turnNum + " currentMaxHeight " + currentMaxHeight + " hash " + board.hashCode() + "\n");

            hashWriter.append("hash " + board.hashCode() + "\n" );
            resultArray.add(currentMaxHeight);
        }
        writer.close();
        hashWriter.close();

        BufferedWriter boardWriter = new BufferedWriter(new FileWriter("board.txt", false));
        board.save(boardWriter);
        boardWriter.close();

        long turnCount = REAL_DATA_MAX;

        // fill this data based on output files
        long cycleMinTurn = 4000000;
        long cycleWidth = 1710;
        long cycleGain = 2572;

        long cycleCount = 0;
        while (turnCount > cycleMinTurn) {
            turnCount -= cycleWidth;
            cycleCount++;
        }
        long result = resultArray.get((int)turnCount);
        result += cycleCount  * cycleGain;

        System.out.println("result is " + result);
    }

    private static void settleElement(Board board, Element element) {

        while (true) {
            var move = movesList.get((int)(moveIdx % movesList.size()));
            board.moveHorizontally(element, move);

            moveIdx++;
            if (!board.moveDown(element)) {   // cannot move any more, resting
                board.addElement(element);
                break;
            };
        }
    }
}

class Element {
    public Element(long elemIdx, long yPos) {
        this.yPos = yPos;
        shape = new ArrayList<>(elementsList.get((int)(elemIdx % elementsList.size())));
    }
    public ArrayList<Integer> shape;

    public long yPos;

    public void move(Direction move) {
        for (int yIdx = 0; yIdx< shape.size(); ++yIdx) {
            Integer row = shape.get(yIdx);
            if (move.equals(Direction.LEFT)) {
                shape.set(yIdx, row >> 1);
            }
            else {
                shape.set(yIdx, row << 1);
            }

        }
    }

    public void moveY(int i) {
        yPos += i;
    }
}

class Board {

    // false - empty
    // true - used
    private ArrayList<Integer> board = new ArrayList<>();
    private ArrayList<Integer> pows = new ArrayList<>();

    int maxHeight = 0;
    int hashCode;
    int rollingHashWidth;
    int hashPrime = 31;
    int moduloPrime = 1572869;

    Board(int rollingHashWidth) {
        this.rollingHashWidth = rollingHashWidth;

        // add bottom and sides
        board.add(0b111111111);

        Integer lastPow = 1;
        pows.add(lastPow);
        for (int idx = 1; idx <= rollingHashWidth; ++idx) {
            Integer nextPow = lastPow * hashPrime % moduloPrime;
            pows.add(nextPow);
            lastPow = nextPow;
        }
    }

    ArrayList<Integer> getBoard() {
        return board;
    }

    int ySize() {
        return board.size();
    }

    public void addEmptyRow() {
        board.add(0b100000001);
    }

    public void moveHorizontally(Element element, Direction move) {
        element.move(move);
        if (!elementFits(element)) {
            element.move(move.negative());
        }
    }

    private boolean elementFits(Element element) {
        for (int yIdx = 0; yIdx < element.shape.size(); ++yIdx) {
            var boardRow = board.get((int)element.yPos + yIdx);
            var elementRow = element.shape.get(yIdx);

            if ((boardRow & elementRow) > 0) {
                return false;
            }
        }
        return true;
    }

    /*
     false = cannot move
     */
    public boolean moveDown(Element element) {
        element.moveY(-1);
        if (!elementFits(element)) {
            element.moveY(1);
            return false;
        }
        return true;
    }

    public long getMaxHeight() {
        return maxHeight;
    }

    public void addElement(Element element) {
        for (int elementYIdx = 0; elementYIdx < element.shape.size(); ++elementYIdx) {

            int boardReplacedRowIdx = (int) element.yPos + elementYIdx;
            var boardReplacedRow = board.get(boardReplacedRowIdx);
            var elementRow = element.shape.get(elementYIdx);

            var boardNewRow = boardReplacedRow | elementRow;

            board.set(boardReplacedRowIdx, boardNewRow);

            if (boardReplacedRow == 0b100000001) {
                var rowFallingOutOfWindowIdx = boardReplacedRowIdx - rollingHashWidth;
                var rowFallingOutOfWindow = 0;
                if (rowFallingOutOfWindowIdx > 0) {
                    rowFallingOutOfWindow = board.get(rowFallingOutOfWindowIdx);
                }

                int newHash = (int) ((((long) hashCode
                        - (long) (rowFallingOutOfWindow * pows.get(rollingHashWidth - 1)) % moduloPrime) % moduloPrime)
                        * pows.get(1) + boardNewRow) % moduloPrime;
                while (newHash < 0) {
                    newHash += moduloPrime;
                }
                while (newHash >= moduloPrime) {
                    newHash -= moduloPrime;
                }

                hashCode = newHash;
                // row was previously empty
                ++maxHeight;
            }
            else {
                // recalc hash for this row
                int fallingOutRowIdx = maxHeight - boardReplacedRowIdx;
                if (fallingOutRowIdx < 0 || fallingOutRowIdx > rollingHashWidth) {
                    // outside window size
                    continue;
                }
                int pow = pows.get(fallingOutRowIdx);
                int newHash = (hashCode + (pow * (boardNewRow - boardReplacedRow) % moduloPrime )) % moduloPrime;
                while (newHash < 0) {
                    newHash += moduloPrime;
                }
                while (newHash >= moduloPrime) {
                    newHash -= moduloPrime;
                }

                hashCode = newHash;
            }
        }
    }


    public void print(Element element) {
        var board = getBoard();

        for (long yIdx = board.size() - 1; yIdx  >= 0; --yIdx) {
            long elementYIdx = yIdx - element.yPos;

            var boardRow = board.get((int)yIdx);
            var elementRow = 0b0;
            if (elementYIdx >= 0 && elementYIdx < element.shape.size()) {
                elementRow = element.shape.get((int) elementYIdx);
            }

            for (int xIdx = 0; xIdx < 9; ++xIdx) {

                var resultingRow = boardRow | elementRow;

                if ((elementRow& (1 << xIdx)) > 0) {
                    System.out.print("O");
                }
                else if ((resultingRow & (1 << xIdx)) > 0) {
                    System.out.print("#");
                }
                else {
                    System.out.print(".");
                }
            }
            System.out.println(" " + yIdx + " " + boardRow);
        }
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public void save(BufferedWriter boardWriter) throws IOException {
        var board = getBoard();
        for (long yIdx = board.size() - 1; yIdx  >= 0; --yIdx) {
            var boardRow = board.get((int)yIdx);
            for (int xIdx = 0; xIdx < 9; ++xIdx) {
                if ((boardRow & (1 << xIdx)) > 0) {
                    boardWriter.write("#");
                }
                else {
                    boardWriter.write(".");
                }
            }
            boardWriter.write( " " + boardRow + "\n");
        }
    }
}

@ToString
enum Direction
{
    LEFT, RIGHT;

    public Direction negative() {
        switch(this) {
            case LEFT:
                return Direction.RIGHT;
            default:
                return Direction.LEFT;
        }
    }
}
