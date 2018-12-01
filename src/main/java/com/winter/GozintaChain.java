package com.winter;

import java.util.*;

public class GozintaChain {
    private Map<List<Long>, Long> cache;
    private List<Integer> firstPrimes;

    public GozintaChain() {
        cache = new HashMap<>();
        firstPrimes = new ArrayList<>(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43));
        fillCache(10001);
    }

    /**
     * Find all numbers till N for which gozinta chain length equal given number
     * e.g g(48)=48, so 48 will be in result list
     * @param n upper boundarym, till it method will check numbers
     * @return list of all numbers, for which gozinta chain length equal this number
     */
    public List<Long> findAllNumbersWithEqualChainLengthTill(long n) {
        List<Long> answers = new ArrayList<>();
        List<Long> exponents = new ArrayList<>(Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L));
        int currentIncrementIndex = 0;
        int depth = 50;
        long currentChainLength = 0;

        while (true) {

            int exponentsIndex = exponents.contains(0) ? exponents.indexOf(0) : exponents.size();
            if (exponentsIndex == 0) exponentsIndex = 1;

            List<Long> divisors = findDivisors(firstPrimes.subList(0, exponentsIndex), exponents, 0, 1L);
            boolean drop = check(n, firstPrimes.subList(0, exponentsIndex), exponents, divisors);

            if (drop) {
                cache.put(exponents.subList(0, exponentsIndex), Long.MAX_VALUE);
            }

            if (!drop) {
                currentChainLength = getGozintaChainLengthByDividers(divisors);
                if (currentChainLength > 0 && currentChainLength == getGozintaChainLength(currentChainLength)) {
                    System.out.println(currentChainLength);
                    answers.add(currentChainLength);
                }
            }

            if (exponents.get(currentIncrementIndex) > depth || currentChainLength > n) {
                currentIncrementIndex++;
                if (currentIncrementIndex == exponents.size()) break;
                exponents.set(currentIncrementIndex, exponents.get(currentIncrementIndex) + 1);
                for (int i = 0; i < currentIncrementIndex; i++) {
                    exponents.set(i, exponents.get(currentIncrementIndex));
                }
            } else {
                currentIncrementIndex = 0;
                exponents.set(currentIncrementIndex, exponents.get(currentIncrementIndex) + 1);
            }

        }

        return answers;
    }

    /**
     * fills cache with exponents of first n numbers
     * @param depth first N numbers for which exponents we fill cache
     */
    private void fillCache(int depth) {
        for (int i = 0; i < depth; i++) {
            List<Long> primes = primeFactors(i);
            List<Long> exponents = getExponents(primes);
            Long chainLength = getGozintaChainLengthNaive(i);
            cache.put(exponents, chainLength);
        }
    }

    /**
     * Naively calculates chain length for a small numbers
     * used to fill cache
     * @param n number
     * @return chain length
     */
    private Long getGozintaChainLengthNaive(long n) {
        if (n == 1) return 1L;
        long count = 0;
        for (long i = 1; i < n / 2 + 1; i++) {
            if (n % i == 0) {
                count += getGozintaChainLengthNaive(i);
            }
        }
        return count;
    }

    /**
     * checks if we can skip number and dont calculate chain length for it, e.g.
     * it chain length would not be equal to given number
     * @param upperBoundary upper boundary of current calculations
     * @param primes primes of prime factorization of a number
     * @param exponents exponents of prime factorization of a number
     * @param divisors list of all divisors of a number with this prime factorization
     * @return if we should skip this number
     */
    private boolean check(long upperBoundary, List<Integer> primes, List<Long> exponents, List<Long> divisors) {
        long curVal = 0L;

        if (cache.containsKey(exponents) && cache.get(exponents).equals(Long.MAX_VALUE)) return true;

        for (int i = 0; i < primes.size(); i++) {
            curVal += Math.pow(primes.get(i), exponents.get(i));
            if (curVal > upperBoundary) {
                return true;
            }
        }

        if (divisors.size() > curVal * 2) {
            return true;
        }
        return false;
    }

    /**
     * Get gozinta chain length for a number, use prime factorization
     * and divisors to get it fast
     * @param n number
     * @return length of gozinta chain for this number
     */
    private Long getGozintaChainLength(long n) {
        List<Long> primes = primeFactors(n);
        List<Long> exponents = getExponents(primes);
        if (cache.containsKey(exponents)) return cache.get(exponents);
        List<Long> divisors = findDivisors(firstPrimes.subList(0, exponents.size()), exponents, 0, 1L);
        Long result = getGozintaChainLengthByDividers(divisors);
        cache.put(exponents, result);
        return result;
    }


    /**
     * Create prime factorization for a number
     * each prime enters in result list its exponent value times
     * @param n number for which create factorization
     * @return list, containing prime factorization of this number
     */
    private List<Long> primeFactors(long n) {
        List<Long> factors = new ArrayList<Long>();
        while (n % 2 == 0 && n > 0) {
            factors.add(2L);
            n /= 2;
        }

        for (long i = 3; i * i <= n; i += 2) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }
        if (n > 1)
            factors.add(n);

        return factors;
    }

    /**
     * Create complete list of all number divisors by its prime factorization
     * @param primeDivisors prime factorization, each prime occure once, exponents stored in another list
     * @param exponents list of exponents in this prime factorization
     * @param currentDivisorIndex index of prime, which is checked in this method
     * @param currentResult  current divisor value
     * @return list of all divisors
     */
    private List<Long> findDivisors(List<Integer> primeDivisors, List<Long> exponents, int currentDivisorIndex, Long currentResult) {
        List<Long> divisors = new ArrayList<>();
        if (currentDivisorIndex == primeDivisors.size()) {
            divisors.add(currentResult);
            return divisors;
        }
        for (int i = 0; i <=exponents.get(currentDivisorIndex); ++i) {
            divisors.addAll(findDivisors(primeDivisors, exponents, currentDivisorIndex + 1, currentResult));
            currentResult *= primeDivisors.get(currentDivisorIndex);
        }
        return divisors;
    }

    /**
     * Return length of gozinta chain for a number, by its divisors
     * @param divisors list of all dividers for a number
     * @return length of gozinta chain for this number
     */
    private Long getGozintaChainLengthByDividers(List<Long> divisors) {
        Long currentResult = 0L;
        for (int i = 0; i < divisors.size() - 1; i++) {
            currentResult += getGozintaChainLength(divisors.get(i));
        }
        return currentResult;
    }

    /**
     * Returns list of exponents for given prime factorization
     * @param primes contains prime factorization for a number
     * @return list with exponents for each prime in factorization
     * */
    private List<Long> getExponents(List<Long> primes) {
        List<Long> exponents = new ArrayList<>();
        Long currentPrime = null;
        Long currentExponent = 0L;
        for (Long prime : primes) {
            if (currentPrime == null) {
                currentPrime = prime;
                currentExponent = 1L;
            } else {
                if (!prime.equals(currentPrime)) {
                    exponents.add(currentExponent);
                    currentPrime = prime;
                    currentExponent = 1L;
                } else {
                    currentExponent++;
                }
            }
        }
        exponents.add(currentExponent);

        exponents.sort(Collections.reverseOrder());

        return exponents;
    }
}
