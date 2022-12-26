package com.jsyryczynski;


import static com.jsyryczynski.Main.compareLists;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import lombok.Getter;
import lombok.Setter;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(new File("input.txt"))) {

            PriorityQueue<MyList> pq = new PriorityQueue<>();

            MyList tmpList = createMyList("[[2]]");
            tmpList.setSpecialIdx(1);
            pq.add(tmpList);


            tmpList = createMyList("[[6]]");
            tmpList.setSpecialIdx(1);
            pq.add(tmpList);

            while (scanner.hasNext()) {
                String line1 = scanner.nextLine();
                if (line1.isEmpty()) {
                    continue;
                }

                System.out.println(line1);

                MyList list1 = createMyList(line1);
                pq.add(list1);
            }
            System.out.println("RESULTSSSS");
            int idx = 1;
            int result = 1;
            while (!pq.isEmpty()) {
                MyList tmp = pq.poll();
                System.out.println("" + idx + " " + tmp.getSpecialIdx());
                if (tmp.getSpecialIdx() != null) {
                    result = result * idx;
                }
                ++idx;
            }
            System.out.println("result " + result);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

    }

    public static int compareLists(MyList list1, MyList list2) {
        // left smaller = 1
        // equal = 0
        // left bigger = -1

        MyList currentList1 = list1;
        MyList currentList2 = list2;

        if (currentList1.isList && currentList2.isList) {
            int maxCompareIdx = currentList1.elements.size();
            if (maxCompareIdx < currentList2.elements.size()) {
                maxCompareIdx = currentList2.elements.size();
            }
            for (int compareIdx = 0; compareIdx < maxCompareIdx; compareIdx++) {
                if (compareIdx >= currentList2.elements.size()) {
                    return -1;
                }
                if (compareIdx >= currentList1.elements.size()) {
                    return 1;
                }
                int value = compareLists(currentList1.elements.get(compareIdx), currentList2.elements.get(compareIdx));
                if (value != 0) {
                    return value;
                }
            }
        }
        else if (!currentList1.isList && !currentList2.isList) {
            if (currentList1.value < currentList2.value) {
                return 1;
            }
            else if (currentList1.value== currentList2.value){
                return 0;
            }
            else {
                return -1;
            }
        }
        else {
            // list vs non-list
            if (!currentList1.isList) {
                MyList tmp = new MyList();
                tmp.elements.add(new MyList(currentList1.value));
                int result = compareLists(tmp, currentList2);
                return result;
            }
            else{
                MyList tmp = new MyList();
                tmp.elements.add(new MyList(currentList2.value));
                int result = compareLists(currentList1, tmp);
                return result;
            }
        }
        return 0;
    }

    private static MyList createMyList(String line) {
        MyList result = new MyList();
        MyList currentList = result;
        Queue<MyList> stack = new LinkedList<>();
        StringBuilder sb = new StringBuilder();

        for (int idx = 1; idx < line.length(); ++idx) {
            String ch = line.substring(idx, idx + 1);
            if (ch.equals("[")) {
                sb.setLength(0);
                stack.add(currentList);
                MyList tmp = new MyList();
                currentList.elements.add(tmp);
                currentList = tmp;
            }
            else if (ch.equals("]")) {
                if (sb.length() > 0) {
                    Integer value = Integer.parseInt(sb.toString());
                    currentList.elements.add(new MyList(value));
                }
                sb.setLength(0);

                currentList = stack.poll();
            }
            else if (ch.equals(",")) {
                // end of current value
                if (sb.length() > 0) {
                    Integer value = Integer.parseInt(sb.toString());
                    currentList.elements.add(new MyList(value));
                }
                sb.setLength(0);
            }
            else {
                // integer
                sb.append(ch);
            }
        }

        return result;
    }
}

class MyList implements Comparable<MyList>{
    ArrayList<MyList> elements;
    Integer value;
    boolean isList;

    @Getter
    @Setter
    Integer specialIdx;

    public MyList(Integer value) {
        this.value = value;
        isList = false;
    }

    public MyList() {
        elements = new ArrayList<>();
        isList = true;
    }


    @Override
    public String toString() {
        return "MyList{" +
                "elements=" + elements +
                ", value=" + value +
                ", isList=" + isList +
                '}';
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(other == null || (this.getClass() != other.getClass())){
            return false;
        }

        MyList guest = (MyList) other;
        return compareLists(this, guest) == 0;
    }

    @Override
    public int compareTo(MyList o) {
        return -1 * compareLists(this, o);
    }
}
