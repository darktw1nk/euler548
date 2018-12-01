package com.winter;

import java.util.List;

public class Main {

    public static void main(String[] args){
        Long N = 10_000_000_000_000_000L;
        GozintaChain gozintaChain = new GozintaChain();

        List<Long> result = gozintaChain.findAllNumbersWithEqualChainLengthTill(N);

        Long sum = result.stream().mapToLong(Long::longValue).sum();
        System.out.println("Sum: "+sum);
    }

}
