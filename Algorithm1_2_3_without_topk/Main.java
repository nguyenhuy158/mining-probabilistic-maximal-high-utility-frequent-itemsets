import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Main {
    static Date currentDate = new Date();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HH-mm-ss");
    static String formattedDate = dateFormat.format(currentDate);

    // CGEB algorithms

    public static List<C> CGEBFucntion(UD UD, int minsup, double minpro) {
        System.out.println("Start CGEBFucntion");
        List<Set<String>> F = new ArrayList<>();
        Set<String> elements = new HashSet<>();
        Map<String, Double> elementsProbability = UD.getProbability();

        // Seperate the prob out of UD
        List<Set<String>> D = UD.removeProbFromUD();
        System.out.println(D);

        // Get unique data
        for (Set<String> transaction : D) {
            for (String item : transaction) {
                elements.add(item);
            }
        }

        // Init the first set from the unique data (F)
        for (String e : elements) {
            Set<String> itemSet = new HashSet<>();
            itemSet.add(e);
            F.add(itemSet);
        }

        List<C> result = new ArrayList<>();
        Set<String> varList = new HashSet<>();

        // Start checking (F) if it contains in database
        while (true) {
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
                    // If meet requirements, add this set to result (minsup and lb(E(f)))
                    if (count >= minsup && E >= lb(minsup, minpro)) {
                        result.add(new C(f, Double.parseDouble(String.format("%.2f", E)),
                                Double.parseDouble(String.format("%.2f", var)), count,
                                Double.parseDouble(String.format("%.2f", prob))));
                        varList.add(String.format("%.5f", var));
                        L.add(f);
                        break;
                    }
                }

            }

            int maxlengthL = 0;

            for (Set<String> l : L) {
                if (maxlengthL < l.size()) {
                    maxlengthL = l.size();
                }
            }

            // If L not empty, start union each other between L and unique set
            if (L.isEmpty() || maxlengthL == elementsProbability.size()) {
                System.out.println("End CGEBFucntion");
                break;
            } else {
                F = generateSet(L, elements);
            }
        }
        return result;
    }
    // result.add(varList);
    // CGEB algorithms
    // public static List<C> CGEBFucntion(UD UD, int minsup, double minpro, int k) {
    // System.out.println("Start CGEBFucntion");
    // List<Set<String>> F = new ArrayList<>();
    // Set<String> elements = new HashSet<>();
    // Map<String, Double> elementsProbability = UD.getProbability();

    // // Seperate the prob out of UD
    // List<Set<String>> D = UD.removeProbFromUD();
    // System.out.println(D);

    // // Get unique data
    // for (Set<String> transaction : D) {
    // for (String item : transaction) {
    // elements.add(item);
    // }
    // }

    // // Init the first set from the unique data (F)
    // for (String e : elements) {
    // Set<String> itemSet = new HashSet<>();
    // itemSet.add(e);
    // F.add(itemSet);
    // }

    // PriorityQueue<C> topKPatterns = new
    // PriorityQueue<>(Comparator.comparing(C::getE).reversed()); // Create a
    // // priority queue
    // // for top-k
    // // patterns

    // // Start checking (F) if it contains in database
    // while (true) {
    // List<Set<String>> L = new ArrayList<>();
    // for (Set<String> f : F) {
    // int count = 0;
    // double E = 0;
    // double var = 0;
    // double prob = 1;
    // for (String ff : f) {
    // prob *= elementsProbability.get(ff);
    // }
    // for (Set<String> transaction : D) {
    // if (transaction.containsAll(f)) {
    // E += prob;
    // var += prob * (1 - prob);
    // count++;
    // }
    // // If meet requirements, add this set to result (minsup and lb(E(f)))
    // if (count >= minsup && E >= lb(minsup, minpro)) {
    // C newPattern = new C(f, Double.parseDouble(String.format("%.2f", E)),
    // Double.parseDouble(String.format("%.2f", var)), count,
    // Double.parseDouble(String.format("%.2f", prob)));

    // topKPatterns.offer(newPattern); // Add new pattern to the priority queue
    // if (topKPatterns.size() > k) {
    // topKPatterns.poll(); // Ensure the size of the priority queue does not exceed
    // k
    // }

    // L.add(f);
    // break;
    // }
    // }
    // }

    // // Determine the maximum length of L
    // int maxlengthL = L.stream().mapToInt(Set::size).max().orElse(0);

    // // If L not empty, start union each other between L and unique set
    // if (L.isEmpty() || maxlengthL == elementsProbability.size()) {
    // System.out.println("End CGEBFucntion");
    // break; // Exit the loop when no more candidates can be generated or all
    // elements have
    // // been considered
    // } else {
    // F = generateSet(L, elements);
    // }
    // }

    // // Convert the priority queue to a list to return
    // List<C> result = new ArrayList<>(topKPatterns);
    // return result;
    // }

    private static double lb(double minsup, double minpro) {
        return (2 * minsup - Math.log(minpro)
                - Math.sqrt(Math.pow(Math.log(minpro), 2) - 8 * minpro * Math.log(minpro))) / 2;
    }

    private static double ub(double minsup, double minpro) {
        return (minsup - Math.log(1 - minpro)
                + Math.sqrt(Math.pow(Math.log(1 - minpro), 2) - 2 * minpro * Math.log(1 - minpro)));
    }

    // APFI-MAX
    private static List<C> APFI_MAX(UD UD, int minsup, double minpro) {
        System.out.println("Lowerbound: " + Double.toString(lb(minsup, minpro)));
        System.out.println("Upperbound: " + Double.toString(ub(minsup, minpro)));

        List<C> C = CGEBFucntion(UD, minsup, minpro);
        System.out.println("Start APFI_MAX");

        List<C> res = new ArrayList<>();
        List<Set<String>> Fre_Cur = new ArrayList<>();
        List<Set<String>> Fre_Pre = new ArrayList<>();

        for (int i = C.size() - 1; i >= 0; i--) {
            Set<String> X = C.get(i).getSet();
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
            Fre_Cur = new ArrayList<>();
        }
        System.out.println("End APFI_MAX");
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

    public static void main(String[] args) {
        try {
            String fileNameInput = "T10I4D100K.txt";
            int minsup = 6000;
            double minpro = 0.6;

            // Set the file output name with the current date and time
            String fileName = "result_" + fileNameInput.replace(".txt", "") + "_" + formattedDate + ".txt";

            // Redirect the output to the file with the current date and time
            PrintStream fileOut = new PrintStream(new FileOutputStream(fileName));
            System.setOut(fileOut);

            long start = System.currentTimeMillis();
            long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // Read data
            UD UD = new UD(fileNameInput);

            System.out.println(UD.getProbability());
            // Call CGEBFucntion and store the result
            List<C> apfiMaxResults = APFI_MAX(UD, minsup, minpro);
            System.out.println("APFI_MAX Results:");
            for (C result : apfiMaxResults) {
                System.out.println(result);
            }

            System.out.println("number of transaction: " + UD.getSize());
            System.out.println("minsup: " + minsup);
            System.out.println("minpro: " + minpro);

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
