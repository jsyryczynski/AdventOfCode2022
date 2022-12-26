package com.jsyryczynski;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.ToString;

public class Main {

    //public static final int MAX_TURN = 24;
    public static final int MAX_TURN = 32;
    public static ArrayList<Integer> triangleValues = new ArrayList();


    public static void main(String[] args) throws IOException {
        fillTriangle();

        List<Blueprint> blueprints = new LinkedList<>();
        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            int blueprintIdx = 1;
            while (scanner.hasNext()) {

                Blueprint currentBlueprint = new Blueprint(blueprintIdx);
                blueprintIdx++;
                String line = scanner.nextLine(); // Blueprint:
                if (line.isEmpty()) {
                    continue;
                }


                int firstValueIdx = 6;
                int secondValueIdx = 9;

                // ore robot
                line = scanner.nextLine();
                String[] split = line.split("\s");
                System.out.println(Arrays.toString(split));

                Array4 oreRobotCost = new Array4(Integer.parseInt(split[firstValueIdx]),0,0,0);
                currentBlueprint.resourceCost[Resource.ORE.value] = oreRobotCost;

                // clay robot
                line = scanner.nextLine();
                split = line.split("\s");
                System.out.println(Arrays.toString(split));

                Array4 clayRobotCost = new Array4(Integer.parseInt(split[firstValueIdx]),0,0,0);
                currentBlueprint.resourceCost[Resource.CLAY.value] = clayRobotCost;

                // obsidian robot
                line = scanner.nextLine();
                split = line.split("\s");
                System.out.println(Arrays.toString(split));

                Array4 obsidianRobotCost = new Array4(Integer.parseInt(split[firstValueIdx]),Integer.parseInt(split[secondValueIdx]), 0,0);
                currentBlueprint.resourceCost[Resource.OBSIDIAN.value] = obsidianRobotCost;

                // geode robot
                line = scanner.nextLine();
                split = line.split("\s");
                System.out.println(Arrays.toString(split));

                Array4 geodeRobotCost = new Array4(Integer.parseInt(split[firstValueIdx]), 0, Integer.parseInt(split[secondValueIdx]), 0);
                currentBlueprint.resourceCost[Resource.OBSIDIAN.value] = obsidianRobotCost;
                currentBlueprint.resourceCost[Resource.GEODE.value] = geodeRobotCost;

                blueprints.add(currentBlueprint);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }


        int result = 0;
        for (var currentBlueprint : blueprints) {
            Array4 resourcesToBuildEveryRobotType = currentBlueprint.resourceCost[0].max(currentBlueprint.resourceCost[1])
                    .max(currentBlueprint.resourceCost[2]).max(currentBlueprint.resourceCost[3]);

            LinkedList<State> stateQueue = new LinkedList<>();
            stateQueue.add(new State(0, new Array4(0,0,0,0), new Array4(1,0,0,0)));

            long maxGeodes = 0;
            while (!stateQueue.isEmpty()) {
                State currentState = stateQueue.removeLast();

                //System.out.println(currentState);

                if (currentState.minute >= MAX_TURN) {
                    if (currentState.resources.get(Resource.GEODE.value) > maxGeodes) {
                        maxGeodes = currentState.resources.get(Resource.GEODE.value);
                    }
                    continue;
                }


                // prune if estimated lower then current best
                int turnRemaining = MAX_TURN - currentState.minute;
                int estimatedGeodes = currentState.resources.get(Resource.GEODE.value);
                int estimatedRobots = currentState.robots.get(Resource.GEODE.value);
                var estimatedGeodes2 = estimatedGeodes + estimatedRobots * turnRemaining + triangleNumber(turnRemaining - 1);
                if (estimatedGeodes2 <= maxGeodes) {
                    continue;
                }

                // do not build unnecessary robots
                for (int idx = 0; idx < 3; ++idx) {
                    if (currentState.robots.get(idx) > resourcesToBuildEveryRobotType.get(idx)) {
                        continue;
                    }
                }

                // do not hoard more then can be used up
                for (int idx = 0; idx < 3; ++idx) {
                    int maxUsage = resourcesToBuildEveryRobotType.get(idx) * turnRemaining;
                    if (currentState.resources.get(idx) > maxUsage) {
                        continue;
                    }
                }


                {
                    // no new robots, just gather resources
                    State tmp = new State(currentState.minute + 1, currentState.resources.add(currentState.robots),
                            currentState.robots);
                    stateQueue.add(tmp);
                }
                // try to add ore robot
                if (currentState.resources.isBiggerThen(currentBlueprint.resourceCost[Resource.ORE.value])) {
                    Array4 buildRobots = new Array4(1, 0, 0, 0);
                    Array4 nextStateRobots = currentState.robots.add(buildRobots);
                    Array4 nextStateRes = currentState.resources.add(currentState.robots)
                            .sub(buildRobots.multiplyRobotCost(currentBlueprint.resourceCost));

                    State tmp = new State(currentState.minute + 1, nextStateRes,
                            nextStateRobots);
                    stateQueue.add(tmp);
                }

                // try to add clay robot
                if (currentState.resources.isBiggerThen(currentBlueprint.resourceCost[Resource.CLAY.value])) {
                    Array4 buildRobots = new Array4(0, 1, 0, 0);
                    Array4 nextStateRobots = currentState.robots.add(buildRobots);
                    Array4 nextStateRes = currentState.resources.add(currentState.robots)
                            .sub(buildRobots.multiplyRobotCost(currentBlueprint.resourceCost));


                    State tmp = new State(currentState.minute + 1, nextStateRes,
                            nextStateRobots);
                    stateQueue.add(tmp);
                }

                // try to add Obsidian robot
                if (currentState.resources.isBiggerThen(currentBlueprint.resourceCost[Resource.OBSIDIAN.value])) {
                    Array4 buildRobots = new Array4(0, 0, 1, 0);
                    Array4 nextStateRobots = currentState.robots.add(buildRobots);
                    Array4 nextStateRes = currentState.resources.add(currentState.robots)
                            .sub(buildRobots.multiplyRobotCost(currentBlueprint.resourceCost));

                    State tmp = new State(currentState.minute + 1, nextStateRes,
                            nextStateRobots);
                    stateQueue.add(tmp);
                }

                // try to add geode robot
                if (currentState.resources.isBiggerThen(currentBlueprint.resourceCost[Resource.GEODE.value])) {
                    Array4 buildRobots = new Array4(0, 0, 0, 1);
                    Array4 nextStateRobots = currentState.robots.add(buildRobots);
                    Array4 cost = buildRobots.multiplyRobotCost(currentBlueprint.resourceCost);
                    Array4 withoutCost = currentState.resources.add(currentState.robots);
                    Array4 nextStateRes = currentState.resources.add(currentState.robots)
                            .sub(cost);

                    State tmp = new State(currentState.minute + 1, nextStateRes,
                            nextStateRobots);
                    stateQueue.add(tmp);
                }
            }
            System.out.println("currentBlueprint.index " + currentBlueprint.index + " maxGeodes " + maxGeodes);
            result += maxGeodes * currentBlueprint.index;
        }
        System.out.println("result " + result);

    }

    private static void fillTriangle() {
        for (int idx = 0; idx < MAX_TURN + 1; ++idx) {
            triangleValues.add(idx * (idx + 1) / 2);
        }
    }

    private static int triangleNumber(int turnRemaining) {

        return triangleValues.get(turnRemaining);
    }

}

class Array4 {
    int array[];
    Array4(int input1, int input2, int input3, int input4) {
        array = new int[]{input1, input2, input3, input4};
    }
    Array4(Array4 other) {
        array = Arrays.copyOf(other.array, other.array.length);
    }

    public Array4 sub(Array4 input) {
        Array4 res = new Array4(this);
        for (int idx = 0; idx < 4; ++idx) {
            res.array[idx] -= input.array[idx];
        }
        return res;
    }

    public Array4 add(Array4 input) {
        Array4 res = new Array4(this);
        for (int idx = 0; idx < 4; ++idx) {
            res.array[idx] += input.array[idx];
        }
        return res;
    }

    public Array4 multiplyRobotCost(Array4[] input) {
        int array[] = new int[4];
        for (int resourceType = 0; resourceType < 4; ++resourceType) {
            for (int robotType = 0; robotType < 4; ++robotType) {
                array[resourceType] += this.array[robotType] * input[robotType].array[resourceType];
            }
        }
        Array4 res = new Array4(array[0], array[1], array[2], array[3]);
        return res;
    }

    public int get(int value) {
        return array[value];
    }

    @Override
    public String toString() {
        return "Array4{" +
                "array=" + Arrays.toString(array) +
                '}';
    }

    boolean isBiggerThen(Array4 other) {
        for (int idx = 0; idx < 3; ++idx) { // dont check geode
            if (array[idx] < other.array[idx]) {
                return false;
            }
        }
        return true;
    }

    public Array4 max(Array4 input) {
        return new Array4(Math.max(array[0], input.array[0]), Math.max(array[1], input.array[1]),
                Math.max(array[2], input.array[2]), Math.max(array[3], input.array[3]));
    }
}

enum Resource
{
    ORE(0),
    CLAY(1),
    OBSIDIAN(2),
    GEODE(3);

    public final int value;

    Resource(int value) {
        this.value = value;
    }

}


@AllArgsConstructor
@ToString
class State {
    public int minute;
    public Array4 resources;
    public Array4 robots;
}

class Blueprint {
    int index;
    Array4[] resourceCost;

    public Blueprint(int index) {
        this.index = index;
        resourceCost = new Array4[Resource.values().length];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Array4 cost : resourceCost) {
            sb.append(cost.toString());
        }
        return "Blueprint{" +
                "index=" + index +
                ", resourceCost=" + sb.toString() +
                '}';
    }
}
