package com.example;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Main {
    static Date currentDate = new Date();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMDD.HHmmss");
    static String formattedDate = dateFormat.format(currentDate);

    // CGEB algorithms
    public static MutableList<C> CGEBFucntion(UD UD, int minsup, double minpro) {
        System.out.println("Start CGEBFucntion");
        MutableList<MutableSet<String>> F = Lists.mutable.empty();
        MutableSet<String> elements = Sets.mutable.empty();
        MutableMap<String, Double> elementsProbability = UD.getProbability();

        // Separate the prob out of UD
        MutableList<MutableSet<String>> D = UD.removeProbFromUD();
        System.out.println(D);

        // Get unique data
        D.each(transaction -> elements.addAll(transaction));

        // Init the first set from the unique data (F)
        // elements.each(e -> F.add(Sets.mutable.with(e)));
        for (String e : elements) {
            MutableSet<String> itemSet = Sets.mutable.with(e);
            F.add(itemSet);
        }

        MutableList<C> result = Lists.mutable.empty();
        MutableSet<String> varList = Sets.mutable.empty();

        // Start checking (F) if it contains in the database
        while (true) {
            MutableList<MutableSet<String>> L = Lists.mutable.empty();
            F.each(f -> {
                int count = 0;
                double E = 0;
                double var = 0;
                double prob = 1;
                // f.each(ff -> prob *= elementsProbability.get(ff));
                for (Iterator<String> iterator = f.iterator(); iterator.hasNext();) {
                    String ff = iterator.next();
                    prob *= elementsProbability.get(ff);
                }

                for (MutableSet<String> transaction : D) {
                    if (transaction.containsAll(f)) {
                        count++;
                        E += prob;
                        var += prob * (1 - prob);
                    }

                    // If meet requirements, add this set to result (minsup and lb(E(f)))
                    if (count >= minsup && E >= lb(minsup, minpro)) {
                        result.add(new C(f, E, var, count, prob));
                        varList.add(String.format("%.5f", var));
                        L.add(f);
                        break;
                    }
                }
            });

            int maxlengthL = 0;

            for (MutableSet<String> l : L) {
                if (maxlengthL < l.size()) {
                    maxlengthL = l.size();
                }
            }

            // If L not empty, start union each other between L and unique set
            if (L.isEmpty() || maxlengthL == elementsProbability.size()) {
                System.out.println("End CGEBFucntion");
                return result;
            } else {
                F = generateSet(L, elements);
            }
        }
    }

    private static double lb(double minsup, double minpro) {
        return (2 * minsup - Math.log(minpro)
                - Math.sqrt(Math.pow(Math.log(minpro), 2) - 8 * minpro * Math.log(minpro))) / 2;
    }

    private static double ub(double minsup, double minpro) {
        return (minsup - Math.log(1 - minpro)
                + Math.sqrt(Math.pow(Math.log(1 - minpro), 2) - 2 * minpro * Math.log(1 - minpro)));
    }

    // APFI-MAX
    private static MutableList<C> APFI_MAX(UD UD, int minsup, double minpro) {
        System.out.println("Lowerbound: " + Double.toString(lb(minsup, minpro)));
        System.out.println("Upperbound: " + Double.toString(ub(minsup, minpro)));

        MutableList<C> C = CGEBFucntion(UD, minsup, minpro);
        System.out.println("Start APFI_MAX");

        MutableList<C> res = Lists.mutable.empty();
        MutableList<MutableSet<String>> Fre_Cur = Lists.mutable.empty();
        MutableList<MutableSet<String>> Fre_Pre = Lists.mutable.empty();

        for (int i = C.size() - 1; i >= 0; i--) {
            MutableSet<String> X = C.get(i).getSet();
            for (String string : X) {
                if (X.contains(string) && Fre_Pre.containsAll(X)) {
                    Fre_Cur.add(X);
                }
                if (FM(minsup, minpro, C.get(i).getE())) {
                    if (!res.contains(C.get(i))) {
                        res.add(C.get(i));
                    }
                    Fre_Cur.add(X);
                }
            }
            Fre_Pre = Fre_Cur;
            Fre_Cur = Lists.mutable.empty();
        }
        
        System.out.println("End APFI_MAX");
        return res;
    }

    // FM
    private static boolean FM(int minsup, double minpro, double E) {
        return E >= ub(minsup, minpro) || lb(minsup, minpro) < minsup;
    }

    // Generation the union set from 2 sets
    private static MutableList<MutableSet<String>> generateSet(MutableList<MutableSet<String>> A,
            MutableSet<String> B) {
        MutableList<MutableSet<String>> result = Lists.mutable.empty();
        MutableList<String> bList = B.toList();

        A.each(a -> {
            MutableList<String> aList = a.toList();
            int index = bList.indexOf(aList.getLast());
            for (int j = index + 1; j < bList.size(); j++) {
                MutableSet<String> newSet = Sets.mutable.withAll(a);
                newSet.add(bList.get(j));
                result.add(newSet);
            }
        });
        return result;
    }

    public static void main(String[] args) {
        try {
            // Set the file input name
            String fileNameInput = "T10I4D100K.txt";

            // Set the file output name with the current date and time
            String fileName = "output_" + fileNameInput.replace(".txt", "") + "_" + formattedDate + ".txt";

            // Redirect the output to the file with the current date and time
            PrintStream fileOut = new PrintStream(new FileOutputStream(fileName));
            System.setOut(fileOut);

            long start = System.currentTimeMillis();
            long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // Read data
            UD UD = new UD(fileNameInput);

            System.out.println(UD.getProbability());
            // Call CGEBFucntion and store the result
            MutableList<C> apfiMaxResults = APFI_MAX(UD, 10000, 0.3);
            System.out.println("APFI_MAX Results:");
            apfiMaxResults.each(result -> System.out.println(result));

            long end = System.currentTimeMillis();
            long duration = end - start;
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = endMemory - startMemory;
            System.out.println("Code run time: " + duration + "ms");
            System.out.println("Memory used: " + Math.round(memoryUsed / 1000.0) + " kb");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
