package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiFunction;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

public class Main {
    public static void main(String[] args) throws IOException {

        HashMap<String, Long> monkeysWithKnownValues = new HashMap<>();
        HashMap<String, OperationMonkey> monkeysWithUnknownValues = new HashMap<>();

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                String[] split = line.split("\s");
                String currentMonkeyName = split[0].substring(0, split[0].length() - 1);

                if (split.length > 2) {
                    //operation monkey
                    String monkey1 = split[1];
                    String operationString = split[2];
                    String monkey2 = split[3];

                    BiFunction<Long, Long, Long> operation = null;
                    if (operationString.equals("+")) {
                        operation = (a,b) -> Math.addExact(a,b);
                    }
                    else if (operationString.equals("*")) {
                        operation = (a,b) -> Math.multiplyExact(a,b);
                    }
                    else if (operationString.equals("-")) {
                        operation = (a,b) -> Math.subtractExact(a,b);
                    }
                    else {
                        operation = (a,b) -> a/b;
                    }


                    OperationMonkey om = new OperationMonkey(currentMonkeyName, monkey1, monkey2, operation);
                    // part 2

                    if (currentMonkeyName.equals("root")) {
                        om = new OperationMonkey(currentMonkeyName, monkey1, monkey2, (a,b) -> a-b);
                    }

                    monkeysWithUnknownValues.put(monkey1, om);
                    monkeysWithUnknownValues.put(monkey2, om);
                }
                else  {
                    // value monkey
                    Long value = Long.parseLong(split[1]);
                    monkeysWithKnownValues.put(currentMonkeyName, value);
                }
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }


        long bound1arg  = Long.MIN_VALUE / 10000;  // just start with biggest & lowest that do not crash
        long bound2arg = Long.MAX_VALUE / 10000;

        long result;
        long humnArg = 0L;
        long target = 0L;

        long bound1value = calcForHumn(monkeysWithKnownValues, monkeysWithUnknownValues, bound2arg);
        long bound2value = calcForHumn(monkeysWithKnownValues, monkeysWithUnknownValues, bound1arg);


        // bound1value must be > bound2value
        if (bound1value > bound2value) {
            long tmp = bound1arg;
            bound1arg = bound2arg;
            bound2arg = tmp;
        }

        while (true) {
            result = calcForHumn(monkeysWithKnownValues, monkeysWithUnknownValues, humnArg);
            System.out.println("" + humnArg + " " + result);
            long tmpValue = humnArg;
            if (result > target) {
                humnArg = (humnArg + bound2arg)/2;
                bound1arg = tmpValue;
            }
            else if (result < target) {
                humnArg = (humnArg + bound1arg)/2;
                bound2arg = tmpValue;
            } else {
                System.out.println("result " + tmpValue);
                break;
            }
        }
    }

    private static long calcForHumn(HashMap<String, Long> monkeysWithKnownValues,
            HashMap<String, OperationMonkey> monkeysWithUnknownValues, long value) {
        long result;

        HashMap<String, Long> monkeysWithKnownValuesTmp = (HashMap<String, Long>) monkeysWithKnownValues.clone();
        monkeysWithKnownValuesTmp.put("humn", value);

        HashMap<String, OperationMonkey> monkeysWithUnknownValuesTmp = makeDeepCopy(monkeysWithUnknownValues);

        result = calcROOT(monkeysWithKnownValuesTmp, monkeysWithUnknownValuesTmp);
        return result;
    }

    private static HashMap<String, OperationMonkey> makeDeepCopy(HashMap<String, OperationMonkey> input) {
        HashMap<String, OperationMonkey> result = new HashMap<>();

        for (Map.Entry<String, OperationMonkey> knownMonkeyEntry : input.entrySet()) {
            result.put(knownMonkeyEntry.getKey(), (OperationMonkey) knownMonkeyEntry.getValue().clone());
        }

        return result;
    }

    private static HashMap<String, OperationMonkey> makeShallowCopy(HashMap<String, OperationMonkey> input) {
        return new HashMap<>(input);
    }

    private static long calcROOT(HashMap<String, Long> monkeysWithKnownValues,
            HashMap<String, OperationMonkey> monkeysWithUnknownValues) {
        while (!monkeysWithUnknownValues.isEmpty()) {
            HashMap<String, Long> resolvedInThisIteration = new HashMap<>();

            for (Map.Entry<String, Long> knownMonkeyEntry : monkeysWithKnownValues.entrySet()) {
                String knownMonkeyName = knownMonkeyEntry.getKey();
                if (monkeysWithUnknownValues.containsKey(knownMonkeyName)) {

                    OperationMonkey unknownMonkey = monkeysWithUnknownValues.get(knownMonkeyName);

                    String monkey1Name = unknownMonkey.monkey1name;
                    String monkey2Name = unknownMonkey.monkey2name;
                    Long monkey1Value = unknownMonkey.monkey1Value;
                    Long monkey2Value = unknownMonkey.monkey2Value;

                    if (monkey1Value == null && knownMonkeyName.equals(monkey1Name)) {
                        monkey1Value = knownMonkeyEntry.getValue();
                    }
                    if (monkey2Value == null && knownMonkeyName.equals(monkey2Name)) {
                        monkey2Value = knownMonkeyEntry.getValue();
                    }

                    if (monkey1Value != null && monkey2Value != null) {
                        // stop at root
                        Long result = unknownMonkey.operation.apply(monkey1Value, monkey2Value);

                        if (unknownMonkey.name.equals("root")) {
                            return result;
                        }

                        resolvedInThisIteration.put(unknownMonkey.name, result);

                        // both known, add to resolvedInThisIteration
                        monkeysWithUnknownValues.remove(unknownMonkey.monkey1name);
                        monkeysWithUnknownValues.remove(unknownMonkey.monkey2name);
                    }
                    else {
                        unknownMonkey.monkey1Value = monkey1Value;
                        unknownMonkey.monkey2Value = monkey2Value;
                        if (monkey1Value != null) {
                            monkeysWithUnknownValues.remove(unknownMonkey.monkey1name);
                            // while making deepcopy we replaced one instance with two, so now we need to overwrite the second instance
                            monkeysWithUnknownValues.put(unknownMonkey.monkey2name, unknownMonkey);
                        }
                        else if (monkey2Value != null) {
                            monkeysWithUnknownValues.remove(unknownMonkey.monkey2name);
                            // while making deepcopy we replaced one instance with two, so now we need to overwrite the second instance
                            monkeysWithUnknownValues.put(unknownMonkey.monkey1name, unknownMonkey);
                        }
                    }
                }
            }

            monkeysWithKnownValues.putAll(resolvedInThisIteration);
            resolvedInThisIteration.clear();
        }
        return 0;
    }
}


@EqualsAndHashCode
@RequiredArgsConstructor
class OperationMonkey implements Cloneable{
    final String name;
    final String monkey1name;
    final String monkey2name;
    final BiFunction<Long, Long, Long> operation;

    Long monkey1Value = null;
    Long monkey2Value = null;

    @Override
    public Object clone() {
        OperationMonkey result = new OperationMonkey(name, monkey1name, monkey2name, operation);
        result.monkey1Value = monkey1Value;
        result.monkey2Value = monkey2Value;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OperationMonkey{");
        sb.append("addr='").append(System.identityHashCode(this)).append('\'');
        sb.append("name='").append(name).append('\'');
        sb.append(", monkey1name='").append(monkey1name).append('\'');
        sb.append(", monkey2name='").append(monkey2name).append('\'');
        sb.append(", monkey1Value=").append(monkey1Value);
        sb.append(", monkey2Value=").append(monkey2Value);
        sb.append('}');
        return sb.toString();
    }
}
