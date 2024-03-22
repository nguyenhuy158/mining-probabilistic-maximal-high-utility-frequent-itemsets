import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.lang.management.MemoryMXBean;
import java.lang.management.ManagementFactory;

public class Main {
    static Date currentDate = new Date();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HH-mm-ss");
    static String formattedDate = dateFormat.format(currentDate);

    // CGEB algorithms
    public static List<C> CGEBTopKFunction(UD UD, int minsup, double minpro, int k) {
        System.out.println("Start CGEBTopKFunction");
        List<Set<String>> F = new ArrayList<>();
        Set<String> elements = new HashSet<>();
        Map<String, Double> elementsProbability = UD.getProbability();

        // Separate the probability out of UD
        List<Set<String>> D = UD.removeProbFromUD();
        int _count = 0;
        for (Set<String> set : D) {
            System.out.println(++_count + "\t" + set);
        }

        // Get unique data
        for (Set<String> transaction : D) {
            for (String item : transaction) {
                elements.add(item);
            }
        }

        // Initialize the first set from the unique data (F)
        for (String e : elements) {
            Set<String> itemSet = new HashSet<>();
            itemSet.add(e);
            F.add(itemSet);
        }

        PriorityQueue<C> topKPatterns = new PriorityQueue<>(Comparator.comparing(C::getE)); // Min-heap based on
        // Exception order

        // Start checking (F) if it contains in the database
        while (!F.isEmpty()) {
            List<Set<String>> L = new ArrayList<>();
            for (Set<String> f : F) {
                int count = 0;
                double E = 0;
                double var = 0;
                double prob = 1;
                for (String ff : f) {
                    prob *= elementsProbability.get(ff);
                }
                for (Set<String> transaction : D) {
                    if (transaction.containsAll(f)) {
                        E += prob;
                        var += prob * (1 - prob);
                        count++;
                    }
                    // If meets requirements, add this set to result (minsup and lb(E(f)))
                    if (count >= minsup && E >= lb(minsup, minpro)) {
                        C newPattern = new C(f, Double.parseDouble(String.format("%.2f", E)),
                                Double.parseDouble(String.format("%.2f", var)), count,
                                Double.parseDouble(String.format("%.2f", prob)));

                        topKPatterns.offer(newPattern); // Add new pattern to the priority queue
                        if (topKPatterns.size() > k) {
                            topKPatterns.poll(); // Remove the pattern with the lowest E to maintain size k
                        }

                        L.add(f);
                        break;
                    }
                }
            }

            // Generate next level of candidate sets (F) from L
            F = generateSet(L, elements);

            // Check if further processing is possible
            if (L.isEmpty()) {
                System.out.println("End CGEBTopKFunction");
                break; // Exit the loop when no more candidates can be generated
            }
        }

        // Convert the priority queue to a list to return, already sorted in increasing
        // order of E
        List<C> result = new ArrayList<>(topKPatterns);
        Collections.sort(result, Comparator.comparing(C::getE).reversed()); // Sort in descending order of Exception

        System.out.println("End CGEBTopKFunction");
        return result;
    }

    // result.add(varList);

    private static double lb(double minsup, double minpro) {
        return (2 * minsup - Math.log(minpro)
                - Math.sqrt(Math.pow(Math.log(minpro), 2) - 8 * minpro * Math.log(minpro))) / 2;
    }

    private static double ub(double minsup, double minpro) {
        return (minsup - Math.log(1 - minpro)
                + Math.sqrt(Math.pow(Math.log(1 - minpro), 2) - 2 * minpro * Math.log(1 - minpro)));
    }

    private static List<C> APFI_MAX_TopK(UD UD, int minsup, double minpro, int k) {
        System.out.println("Lowerbound: " + Double.toString(lb(minsup, minpro)));
        System.out.println("Upperbound: " + Double.toString(ub(minsup, minpro)));

        List<C> allPatterns = CGEBTopKFunction(UD, minsup, minpro, k);
        System.out.println("Start APFI_MAX_TopK ");

        List<Set<String>> Fre_Cur = new ArrayList<>();
        List<Set<String>> Fre_Pre = new ArrayList<>();
        List<C> res = new ArrayList<>(); // This will store the final results

        for (C pattern : allPatterns) {
            Set<String> X = pattern.getSet();

            // Check if X is a superset of any set in Fre_Pre
            boolean isSuperset = Fre_Pre.stream().anyMatch(pre -> X.containsAll(pre));
            if (isSuperset) {
                Fre_Cur.add(X);
            }

            // Apply your FM function to filter patterns
            if (FM(minsup, minpro, pattern.getE())) {
                res.add(pattern);
                if (res.size() > k) {
                    Collections.sort(res, Comparator.comparing(C::getE).reversed());
                    res.remove(res.size() - 1);
                }
                Fre_Cur.add(X);
            }

            // Prepare for the next iteration
            Fre_Pre = new ArrayList<>(Fre_Cur); // Set Fre_Pre for the next round
            Fre_Cur.clear(); // Clear Fre_Cur for the next round
        }

        // After filtering with FM and Fre_Pre, sort the remaining patterns by
        // 'Exception' and limit to top k
        res.sort(Comparator.comparing(C::getE));
        if (res.size() > k) {
            res = res.subList(0, k); // Keep only the top k elements
        }

        System.out.println("End APFI_MAX_TopK ");
        return res;
    }

    // FM
    private static boolean FM(int minsup, double minpro, double E) {
        if (E >= ub(minsup, minpro)) {
            return true;
        } else if (lb(minsup, minpro) < minsup) {
            return true;
        } else {
            return false;
        }
    }

    // Generation the union set from 2 set
    private static List<Set<String>> generateSet(List<Set<String>> A, Set<String> B) {
        List<Set<String>> result = new ArrayList<>();
        List<String> bList = new ArrayList<>(B);

        for (Set<String> a : A) {
            List<String> aList = new ArrayList<>(a);
            int index = bList.indexOf(aList.get(aList.size() - 1));
            for (int j = index + 1; j < bList.size(); j++) {
                Set<String> newSet = new HashSet<>(a);
                newSet.add(bList.get(j));
                result.add(newSet);
            }
        }
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
            // Get the memory MXBean
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            // Get the heap memory usage before running the code
            long startHeapMemoryUsage = memoryMXBean.getHeapMemoryUsage().getUsed();

            // System.out.println(UD.getProbability());
            for (Map.Entry<String, Double> entry : UD.getProbability().entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

            // Call CGEBFucntion and store the result
            List<C> apfiMaxResults = APFI_MAX_TopK(UD, minSup, minPro, topK);
            System.out.println("APFI_MAX_TopK Results:");
            for (C result : apfiMaxResults) {
                System.out.println(result);
            }
            System.out.printf("number of transaction: %d%n", UD.getSize());
            System.out.printf("minSup: %d%n", minSup);
            System.out.printf("minPro: %s%n", minPro);
            System.out.printf("topK: %s%n", topK);

            long end = System.currentTimeMillis();
            long duration = end - start;
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = endMemory - startMemory;
            System.out.printf("Code run time: %dms%n", duration);
            System.out.printf("Memory used: %d kb%n", Math.round(memoryUsed / 1000.0));

            long endHeapMemoryUsage = memoryMXBean.getHeapMemoryUsage().getUsed();
            // Calculate the memory used
            long memoryUsed1 = endHeapMemoryUsage - startHeapMemoryUsage;
            // Print the memory used
            System.out.println("Memory used: " + memoryUsed1 / 1000.0);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
