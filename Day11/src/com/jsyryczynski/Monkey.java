package com.jsyryczynski;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
public class Monkey {
    final int idx;
    final Queue<BigInteger> items;
    BigInteger divisible;
    int destTrue;
    int destFalse;
    long inspectionCount;

    @Getter
    @Setter
    private Operation operation;

    Monkey(int idx){
        this.idx = idx;
        items = new LinkedList<BigInteger>();
        inspectionCount = 0;
    }

}
