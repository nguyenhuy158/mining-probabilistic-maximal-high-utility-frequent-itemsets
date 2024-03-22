package mining.frequentitemsets;

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
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {
    static Date currentDate = new Date();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HH-mm-ss");
    static String formattedDate = dateFormat.format(currentDate);

    /**
     * CGEB algorithms
     *
     * @param UD     uncerntain database
     * @param minsup min support
     * @param minpro min probabilistic
     * @return
     */
    public static MutableList<C> CGEBTopKFunction(UD UD, int minsup, double minpro, int k) {
        System.out.println("Start CGEBTopKFunction");
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

        PriorityQueue<C> topKPatterns = new PriorityQueue<>(Comparator.comparing(C::getE)); // Min-heap based on

        // Start checking (F) if it contains in the database
        while (!F.isEmpty()) {
            MutableList<MutableSet<String>> L = Lists.mutable.empty();
            F.each(f -> {
                int count = 0;
                double E = 0;
                double var = 0;
                double prob = 1;

                for (String ff : f) {
                    prob *= elementsProbability.get(ff);
                }

                for (MutableSet<String> transaction : D) {
                    if (transaction.containsAll(f)) {
                        E += prob;
                        var += prob * (1 - prob);
                        count++;
                    }

                    // If meets requirements, add to result
                    if (count >= minsup && E >= lb(minsup, minpro)) {
                        C newPattern = new C(f, Double.parseDouble(String.format("%.2f", E)),
                                Double.parseDouble(String.format("%.2f", var)), count,
                                Double.parseDouble(String.format("%.2f", prob)));

                        topKPatterns.offer(newPattern);

                        if (topKPatterns.size() > k) {
                            topKPatterns.poll();
                        }

                        L.add(f);
                        break;
                    }
                }
            });

            // Generate next candidates
            F = generateSet(L, elements);

            if (L.isEmpty()) {
                System.out.println("End CGEBTopKFunction");
                break;
            }
        }

        // Convert to list
        MutableList<C> result = Lists.mutable.withAll(topKPatterns);

        result.sortThis(Comparator.comparing(C::getE).reversed());

        System.out.println("End CGEBTopKFunction");
        return result;
//        // Start checking (F) if it contains in the database
//        while (true) {
//            MutableList<MutableSet<String>> L = Lists.mutable.empty();
//            F.each(f -> {
//                int count = 0;
//                double E = 0;
//                double var = 0;
//                double prob = 1;
//                // f.each(ff -> prob *= elementsProbability.get(ff));
//                for (Iterator<String> iterator = f.iterator(); iterator.hasNext(); ) {
//                    String ff = iterator.next();
//                    prob *= elementsProbability.get(ff);
//                }
//
//                for (MutableSet<String> transaction : D) {
//                    if (transaction.containsAll(f)) {
//                        count++;
//                        E += prob;
//                        var += prob * (1 - prob);
//                    }
//
//                    // If meet requirements, add this set to result (minsup and lb(E(f)))
//                    if (count >= minsup && E >= lb(minsup, minpro)) {
//                        result.add(new C(f, E, var, count, prob));
//                        varList.add(String.format("%.5f", var));
//                        L.add(f);
//                        break;
//                    }
//                }
//            });
//
//            int maxlengthL = 0;
//
//            for (MutableSet<String> l : L) {
//                if (maxlengthL < l.size()) {
//                    maxlengthL = l.size();
//                }
//            }
//
//            // If L not empty, start union each other between L and unique set
//            if (L.isEmpty() || maxlengthL == elementsProbability.size()) {
//                System.out.println("End CGEBTopKFunction");
//                return result;
//            } else {
//                F = generateSet(L, elements);
//            }
//        }
    }

    /**
     * Compute lower bound
     *
     * @param minsup min support
     * @param minpro min probabilistic
     * @return
     */
    private static double lb(double minsup, double minpro) {
        return (2 * minsup - Math.log(minpro) - Math.sqrt(Math.pow(Math.log(minpro), 2) - 8 * minpro * Math.log(minpro))) / 2;
    }

    /**
     * Compute upper bound
     *
     * @param minsup min support
     * @param minpro min probabilistic
     * @return
     */
    private static double ub(double minsup, double minpro) {
        return (minsup - Math.log(1 - minpro) + Math.sqrt(Math.pow(Math.log(1 - minpro), 2) - 2 * minpro * Math.log(1 - minpro)));
    }

    /**
     * APFI-MAX algorithms
     *
     * @param UD     uncerntain database
     * @param minsup min support
     * @param minpro min probabilistic
     * @return
     */
    private static MutableList<C> APFI_MAX_TopK(UD UD, int minsup, double minpro, int k) {
        System.out.println("Lowerbound: " + Double.toString(lb(minsup, minpro)));
        System.out.println("Upperbound: " + Double.toString(ub(minsup, minpro)));

        MutableList<C> allPatterns = CGEBTopKFunction(UD, minsup, minpro, k);

        System.out.println("Start APFI_MAX_TopK");

        MutableList<MutableSet<String>> Fre_Cur = Lists.mutable.empty();
        MutableList<MutableSet<String>> Fre_Pre = Lists.mutable.empty();
        MutableList<C> res = Lists.mutable.empty();

        for (C pattern : allPatterns) {

            MutableSet<String> X = pattern.getSet();

            // check if superset
            boolean isSuperset = Fre_Pre.anySatisfy(pre -> X.containsAll(pre));

            if(isSuperset) {
                Fre_Cur.add(X);
            }

            // apply FM
            if(FM(minsup, minpro, pattern.getE())) {
                res.add(pattern);

                if(res.size() > k) {
//                    res.sortThis(C::getE);
                    res.sortThis((c1, c2) -> {
                        return Double.compare(c1.getE(), c2.getE());
                    });
                    res.remove(res.size() - 1);
                }

                Fre_Cur.add(X);
            }

            // prepare for next iteration
            Fre_Pre = Lists.mutable.withAll(Fre_Cur);
            Fre_Cur.clear();
        }

// sort and limit to top k
//        res.sortThis(C::getE);
        res.sortThis((c1, c2) -> {
            return Double.compare(c1.getE(), c2.getE());
        });
        if(res.size() > k) {
            res = res.subList(0, k);
        }

        System.out.println("End APFI_MAX_TopK");
        return res;
//        MutableList<C> C = CGEBTopKFunction(UD, minsup, minpro);
//        System.out.println("Start APFI_MAX_TopK");
//
//        MutableList<C> res = Lists.mutable.empty();
//        MutableList<MutableSet<String>> Fre_Cur = Lists.mutable.empty();
//        MutableList<MutableSet<String>> Fre_Pre = Lists.mutable.empty();
//
//        for (int i = C.size() - 1; i >= 0; i--) {
//            MutableSet<String> X = C.get(i).getSet();
//            for (String string : X) {
//                if (X.contains(string) && Fre_Pre.containsAll(X)) {
//                    Fre_Cur.add(X);
//                }
//                if (FM(minsup, minpro, C.get(i).getE())) {
//                    if (!res.contains(C.get(i))) {
//                        res.add(C.get(i));
//                    }
//                    Fre_Cur.add(X);
//                }
//            }
//            Fre_Pre = Fre_Cur;
//            Fre_Cur = Lists.mutable.empty();
//        }
//
//        System.out.println("End APFI_MAX_TopK");
//        return res;
    }

    /**
     * Compute frequency measurement
     *
     * @param minsup min support
     * @param minpro min probabilistic
     * @param E      expectation
     * @return
     */
    private static boolean FM(int minsup, double minpro, double E) {
        return E >= ub(minsup, minpro) || lb(minsup, minpro) < minsup;
    }

    /**
     * Generation the union set from 2 set
     *
     * @param A the first set
     * @param B the second set
     * @return
     */
    private static MutableList<MutableSet<String>> generateSet(MutableList<MutableSet<String>> A, MutableSet<String> B) {
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

    private static void printOutput(String fileName, int minSup, double minPro, int topK) {
        System.out.printf("Input file name: %s%n", fileName);
        System.out.printf("Minimum support: %d%n", minSup);
        System.out.printf("Minimum probabilistically: %.2f%n", minPro);
        System.out.printf("Top K: %d%n", topK);
    }

    private static void printHelp() {
        System.out.println("Help:");
        System.out.println("[-f, --file] <filename> Specify input file name (required)");
        System.out.println("-ms, --minsup <value>  Specify minimum support");
        System.out.println("-mp, --minpro <value>  Specify minimum probabilistically");
        System.out.println("-k, --top-k <value>    Specify top K");
        System.out.println("-h, --help             Show help information");
        System.out.println("-v, --version          Show version information");
    }

    private static void printVersion() {
        System.out.println("Version: 1.0");
    }

    public static void main(String[] args) {
        try {
            String fileName = null;
            int minSup = 10000;
            double minPro = 0.6;
            int topK = 100;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "-f":
                    case "--file":
                        if (i + 1 < args.length) {
                            fileName = args[i + 1];
                            i++;
                        } else {
                            System.err.println("Missing value for --file option.");
                            return;
                        }
                        break;
                    case "-ms":
                    case "--minsup":
                        if (i + 1 < args.length) {
                            try {
                                minSup = Integer.parseInt(args[i + 1]);
                                i++;
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid value for --minsup option. Must be an integer.");
                                return;
                            }
                        } else {
                            System.err.println("Missing value for --minsup option.");
                            return;
                        }
                        break;
                    case "-mp":
                    case "--minpro":
                        if (i + 1 < args.length) {
                            try {
                                minPro = Double.parseDouble(args[i + 1]);
                                i++;
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid value for --minpro option. Must be a double.");
                                return;
                            }
                        } else {
                            System.err.println("Missing value for --minpro option.");
                            return;
                        }
                        break;
                    case "-k":
                    case "--top-k":
                        if (i + 1 < args.length) {
                            try {
                                topK = Integer.parseInt(args[i + 1]);
                                i++;
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid value for --top-k option. Must be an integer.");
                                return;
                            }
                        } else {
                            System.err.println("Missing value for --top-k option.");
                            return;
                        }
                        break;
                    case "-h":
                    case "--help":
                        printHelp();
                        return;
                    case "-v":
                    case "--version":
                        printVersion();
                        return;
                    default:
                        System.err.printf("Unknown option: %s%n", arg);
                        return;
                }
            }

            if (fileName == null) {
                System.err.println("Missing value for -f or --file option.");
                return;
            }

            printOutput(fileName, minSup, minPro, topK);

            // Read data
            UD UD = new UD(fileName);

            // Set the file output name with the current date and time
            fileName = "result_%s_%s.txt".formatted(
                    fileName.substring(fileName.lastIndexOf("/") + 1).replace(".txt", ""),
                    formattedDate);

            // Redirect the output to the file with the current date and time
            PrintStream fileOut = new PrintStream(new FileOutputStream(fileName));
            System.setOut(fileOut);

            long start = System.currentTimeMillis();
            long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            System.out.println(UD.getProbability());
            // Call CGEBTopKFunction and store the result
            MutableList<C> apfiMaxResults = APFI_MAX_TopK(UD, minSup, minPro, topK);
            System.out.println("APFI_MAX_TopK Results:");
            apfiMaxResults.each(result -> System.out.println(result));

            System.out.println("number of transaction: " + UD.getSize());
            System.out.println("minsup: " + minSup);
            System.out.println("minpro: " + minPro);


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
