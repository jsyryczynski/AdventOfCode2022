package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class Main {

    static HashMap<Pair<String>, Integer> distanceMap;

    public static void main(String[] args) {
        int endTurnIdx = 26;
        HashMap<String, Valve> fullValveList = new HashMap<String, Valve>();

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNext()) {
                String line1 = scanner.nextLine();
                if (line1.isEmpty()) {
                    continue;
                }

                System.out.println(line1);
                String[] split = line1.split("[\s;,=]");
                System.out.println(Arrays.toString(split));
                String name = split[1];
                int flowRate = Integer.parseInt(split[5]);
                var neighbours = Arrays.stream(split).filter(str -> !str.isEmpty()).skip(10).collect(Collectors.toSet());

                Valve valve = new Valve(name, flowRate, neighbours);
                fullValveList.put(name, valve);

            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        calculateDistances(fullValveList);

        Queue<State> movesQueue = new PriorityQueue<>();

        HashMap<String, Valve> reducedValveList = new HashMap<String, Valve>();
        for (var key : fullValveList.keySet() ) {
            if (fullValveList.get(key).flowRate > 0) {
                reducedValveList.put(key, fullValveList.get(key));
            }
        }

        movesQueue.add(new State("AA", "AA", 0, 0, 0, 0, reducedValveList.keySet()));

        long heighestScore = 0;
        while (!movesQueue.isEmpty()) {

            State currentState = movesQueue.poll();
            long currentScore = currentState.score;

            System.out.println("Checking " + currentState);
            System.out.println("Queue size " + movesQueue.size()  + " heighestScore " + heighestScore);


            if (currentState.step >= endTurnIdx) {
                if (currentScore > heighestScore) {
                    heighestScore = currentScore;
                }
                continue;
            }

            boolean noValvesToOpen = true;

            boolean firstFree = currentState.stepAtDest1 == currentState.step;
            if (firstFree) {
                // move to each unopened valve
                for (var valve : currentState.unopenedValves) {

                    if (fullValveList.get(valve).flowRate == 0) {
                        // ignore empty flow;
                        continue;
                    }
                    noValvesToOpen = false;

                    // go and open valve
                    int stepCost = getDistance(valve, currentState.dest1) + 1;

                    HashSet<String> unopenedValves = new HashSet<>(currentState.unopenedValves);
                    unopenedValves.remove(valve);

                    int nextStep1 = currentState.step + stepCost;
                    long nextScore = currentScore;
                    if (endTurnIdx < nextStep1) {
                        //nextStep1 = endTurnIdx;
                        continue; // if this valve is too far to matter, ignore it
                    }
                    else {
                        nextScore += (endTurnIdx - nextStep1) * fullValveList.get(valve).flowRate;
                    }
                    int nextStep2 = currentState.stepAtDest2;
                    int nextStep = Math.min(nextStep1, nextStep2);
                    State nextState = new State(valve, currentState.dest2, nextStep, nextStep1, nextStep2, nextScore, unopenedValves);

                    movesQueue.add(nextState);
                }
            }

            boolean secondFree = currentState.stepAtDest2 == currentState.step;
            if (secondFree) {
                // move to each unopened valve
                for (var valve : currentState.unopenedValves) {

                    if (fullValveList.get(valve).flowRate == 0) {
                        // ignore empty flow;
                        continue;
                    }
                    noValvesToOpen = false;

                    // go and open valve
                    int stepCost = getDistance(valve, currentState.dest2) + 1;

                    HashSet<String> unopenedValves = new HashSet<>(currentState.unopenedValves);
                    unopenedValves.remove(valve);

                    int nextStep2 = currentState.step + stepCost;

                    int nextStep1 = currentState.stepAtDest1;
                    long nextScore = currentScore;
                    if (endTurnIdx < nextStep2) {
                        //nextStep2 = endTurnIdx;
                        continue; // if this valve is too far to matter, ignore it
                    }
                    else {
                        nextScore += (endTurnIdx - nextStep2) * fullValveList.get(valve).flowRate;
                    }
                    int nextStep = Math.min(nextStep1, nextStep2);
                    State nextState = new State(currentState.dest1, valve, nextStep, nextStep1, nextStep2, nextScore,
                            unopenedValves);

                    movesQueue.add(nextState);
                }
            }


            if (noValvesToOpen) {
                if (currentScore > heighestScore) {
                    heighestScore = currentScore;
                }
            }
        }

        System.out.println("Heighest score " + heighestScore);
    }

    private static void calculateDistances(HashMap<String, Valve> valveList) {
        distanceMap = new HashMap<>();

        for (String startingValve : valveList.keySet()) {

            Queue<DistanceState> queue = new LinkedList<>();
            queue.add(new DistanceState(startingValve, 0));
            while (!queue.isEmpty()) {
                DistanceState cs = queue.poll();

                if (distanceMap.containsKey(new Pair<>(cs.currentValve, startingValve))) {
                    continue;
                }

                distanceMap.put(new Pair<>(cs.currentValve, startingValve), cs.distance);

                Valve currentValve = valveList.get(cs.currentValve);
                for (String neighbour : currentValve.neighbours) {
                    queue.add(new DistanceState(neighbour, cs.distance + 1));
                }
            }
        }
    }

    private static int getDistance(String nodeA, String nodeB) {
        return distanceMap.get(new Pair<>(nodeA, nodeB));
    }


}

@AllArgsConstructor
class DistanceState {
    String currentValve;
    int distance;
}

@AllArgsConstructor
@EqualsAndHashCode
@ToString
class Pair<T> {
    T first;
    T second;
}

@AllArgsConstructor
class State implements Comparable<State> {
    String dest1;
    String dest2;
    int step;
    int stepAtDest1;  // at which step will dest be reached
    int stepAtDest2;

    long score;
    Set<String> unopenedValves;


    // Overriding compareTo() method
    @Override
    public int compareTo(State o) {
        return Long.compare(o.score,score);
    }

    @Override
    public String toString() {
        return "State{" +
                "pos1='" + dest1 + '\'' +
                ", pos2='" + dest2 + '\'' +
                ", step=" + step +
                ", stepAtDest1=" + stepAtDest1 +
                ", stepAtDest2=" + stepAtDest2 +
                ", score=" + score +
                //", unopenedValves=" + unopenedValves.toString() +
                '}';
    }
}

@AllArgsConstructor
class Valve {
    String name;
    int flowRate;
    Set<String> neighbours;
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
