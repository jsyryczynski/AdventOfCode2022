package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class Main {

    public static final long DECRYPTION_KEY = 811589153L;
    // public static final long DECRYPTION_KEY = 1L;

    public static void main(String[] args) throws IOException {

        int count = 0;

        Node previousNode = null;
        Node startingNode = null;
        Node lastNode = null;
        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                Long input = Long.parseLong(line);

                ++count;


                lastNode = new Node(input * DECRYPTION_KEY);

                if (startingNode == null) {
                    startingNode = lastNode;
                }
                if (previousNode != null) {
                    previousNode.nextInOriginalOrder = lastNode;
                    previousNode.nextInOutputOrder = lastNode;
                    lastNode.previousInOutputOrder = previousNode;
                }
                previousNode = lastNode;
            }

            lastNode.nextInOutputOrder = startingNode;
            lastNode.nextInOriginalOrder = startingNode;
            startingNode.previousInOutputOrder = lastNode;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        System.out.println("count " + count);

        printList(count, startingNode);

        Node currentNode = startingNode;

        for (int mixingIdx = 0; mixingIdx < 10; ++mixingIdx) {
            int numberIdx = 0;
            do {
                System.out.println("mixingIdx " + mixingIdx +  " number idx " + numberIdx + " / " + count);
                ++numberIdx;
                long changeIdx = 0;
                long changeNum = currentNode.number;
                if (changeNum % (count - 1) == 0) {
                    currentNode = currentNode.nextInOriginalOrder;
                    continue;
                }

                changeNum = Math.floorMod(changeNum, count - 1);

                long increment = changeNum / Math.abs(changeNum);
                Node replacementNode = currentNode;
                while (changeIdx != changeNum) {

                    if (increment > 0) {
                        replacementNode = replacementNode.nextInOutputOrder;
                    }
                    else {
                        replacementNode = replacementNode.previousInOutputOrder;
                    }
                    changeIdx += increment;
                }

                // change previous
                currentNode.previousInOutputOrder.nextInOutputOrder = currentNode.nextInOutputOrder;
                currentNode.nextInOutputOrder.previousInOutputOrder = currentNode.previousInOutputOrder;

                // change replacement
                currentNode.nextInOutputOrder = replacementNode.nextInOutputOrder;
                currentNode.previousInOutputOrder = replacementNode;

                replacementNode.nextInOutputOrder.previousInOutputOrder = currentNode;
                replacementNode.nextInOutputOrder = currentNode;

                currentNode = currentNode.nextInOriginalOrder;
                //printList(count, startingNode);
            }  while(currentNode != startingNode);

        }


        currentNode = startingNode;
        long turnAfterZero = -1;
        long sum = 0;
        while(true) {
            if (turnAfterZero >= 0) {
                turnAfterZero++;
            }

            if (turnAfterZero < 0 && currentNode.number == 0) {
                turnAfterZero = 0;
            }

            if (turnAfterZero == Math.floorMod(1000, count - 1)) {
                sum += currentNode.number;
            }
            if (turnAfterZero == Math.floorMod(2000, count - 1)){
                sum += currentNode.number;
            }
            if (turnAfterZero == Math.floorMod(3000, count - 1)){
                sum += currentNode.number;
                break;
            }
            currentNode = currentNode.nextInOutputOrder;
        }

        System.out.println("result is " + sum );
    }

    private static void printList(int count, Node node) {
        System.out.println("-------------");
        Node currentNode = node;
        do {
            System.out.print(" " + node.number);
            node = node.nextInOutputOrder;
        } while (currentNode != node);
        System.out.println("\n-------------");
    }


    @ToString
    @EqualsAndHashCode
    private static class Node {
        long number;
        Node nextInOriginalOrder;
        Node nextInOutputOrder;
        Node previousInOutputOrder;

        Node(long number) {
            this.number = number;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("  " + this.number + " " + System.identityHashCode(this) + "\n");
            sb.append("    nextInOriginalOrder " + System.identityHashCode(nextInOriginalOrder) + "\n");
            sb.append("    nextInOutputOrder " + System.identityHashCode(nextInOutputOrder) + "\n");
            sb.append("    previousInOutputOrder " + System.identityHashCode(previousInOutputOrder) + "\n");
            return sb.toString();
        }
    }
}
