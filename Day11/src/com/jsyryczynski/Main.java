package com.jsyryczynski;


import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ArrayList<Monkey> monkeyList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            Monkey currentMonkey = null;

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                String[] lineSplit = line.split("[\s,]");
                System.out.println(line);
                System.out.println(Arrays.toString(lineSplit));
                if (lineSplit[0].equals("Monkey")) {
                    int monkeyIdx = Integer.parseInt(lineSplit[1].substring(0, lineSplit[1].length()-1));
                    currentMonkey = new Monkey(monkeyIdx);
                    monkeyList.add(currentMonkey);
                }
                else if (lineSplit[2].equals("Starting")) {
                    int idx = 4;
                    while(idx < lineSplit.length ) {
                        currentMonkey.items.add(BigInteger.valueOf(Long.parseLong(lineSplit[idx])));
                        idx += 2;
                    }
                }
                else if (lineSplit[2].equals("Operation:")) {
                    String operator = line.substring(23, 24);
                    String value = line.substring(25, line.length());

                    if (line.equals("  Operation: new = old * old")) {
                        currentMonkey.setOperation(new SquareOperation());
                    }
                    else if (operator.equals("*")){
                        Long longValue = Long.parseLong(value);
                        currentMonkey.setOperation(new MulitplyOperation(BigInteger.valueOf(longValue)));
                    }
                    else {
                        Long longValue = Long.parseLong(value);
                        currentMonkey.setOperation(new AddOperation(BigInteger.valueOf(longValue)));
                    }
                }
                else if (lineSplit[2].equals("Test:")) {
                    String value = line.substring(21, line.length());
                    System.out.println(value);
                    currentMonkey.divisible = BigInteger.valueOf(Integer.parseInt(value));
                }
                else if (lineSplit[5].equals("true:")) {
                    String value = line.substring(29, line.length());
                    System.out.println(value);
                    currentMonkey.destTrue = Integer.parseInt(value);
                }
                else if (lineSplit[5].equals("false:")) {
                    String value = line.substring(30, line.length());
                    System.out.println(value);
                    currentMonkey.destFalse = Integer.parseInt(value);
                }
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        BigInteger lcd = BigInteger.valueOf(1L);
        for (int monkeyIdx = 0; monkeyIdx < monkeyList.size(); ++monkeyIdx) {
            Monkey currentMonkey = monkeyList.get(monkeyIdx);
            lcd = lcd.multiply(currentMonkey.divisible);
        }


        for (int roundIdx = 0; roundIdx < 10000; ++roundIdx) {
            for (int monkeyIdx = 0; monkeyIdx < monkeyList.size(); ++monkeyIdx) {
                Monkey currentMonkey = monkeyList.get(monkeyIdx);
                while (!currentMonkey.items.isEmpty()) {
                    BigInteger item = currentMonkey.items.remove();
                    currentMonkey.inspectionCount++;
                    item = currentMonkey.getOperation().operation(item);
                    //item = item.divide(BigInteger.valueOf(3L));
                    item = item.mod(lcd);
                    int destMonkey;
                    if (item.mod(currentMonkey.divisible).equals(BigInteger.valueOf(0L))) {
                        destMonkey = currentMonkey.destTrue;
                    }
                    else {
                        destMonkey = currentMonkey.destFalse;
                    }
                    monkeyList.get(destMonkey).items.add(item);
                }
            }


            if (roundIdx % 1000 == 0) {
                System.out.println("After round " + roundIdx);
                for (int monkeyIdx = 0; monkeyIdx < monkeyList.size(); ++monkeyIdx) {

                    Monkey currentMonkey = monkeyList.get(monkeyIdx);
                    System.out.println("Monkey " + monkeyIdx + " " + currentMonkey.inspectionCount);
                }
            }
        }

        PriorityQueue<Long> pq = new PriorityQueue<>(Collections.reverseOrder());

        for (int monkeyIdx = 0; monkeyIdx < monkeyList.size(); ++monkeyIdx) {
            Monkey currentMonkey = monkeyList.get(monkeyIdx);
            pq.add(currentMonkey.inspectionCount);
        }
        Long result = pq.poll() * pq.poll();
        System.out.println("result " + result);
    }
}

interface Operation {
    BigInteger operation(BigInteger input) ;
}

class SquareOperation implements Operation{
    @Override
    public BigInteger operation(BigInteger input) {
        return input.multiply(input);
    }
}

class MulitplyOperation implements Operation{
    BigInteger value;
    public MulitplyOperation(BigInteger value) {
        this.value = value;
    }

    @Override
    public BigInteger operation(BigInteger input) {
        return input.multiply(value);
    }
}

class AddOperation implements Operation{
    BigInteger value;
    public AddOperation(BigInteger value) {
        this.value = value;
    }

    @Override
    public BigInteger operation(BigInteger input) {
        return input.add(value);
    }
}
