import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//This class is used for storing an uncerntain database
public class UD {
    private List<Item> UD = new ArrayList<>();
    private Map<String, Double> uniqueProbability = new HashMap<>();

    public UD(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            ArrayList<Double> probArr = new ArrayList<>();

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                String[] tempItem = line.split(" ");
                if (isProbability(tempItem[tempItem.length - 1])) {
                    this.UD.add(new Item(Arrays.copyOf(tempItem, tempItem.length - 1)));
                    probArr.add(Double.parseDouble(tempItem[tempItem.length - 1]));
                } else {
                    this.UD.add(new Item(tempItem));
                    probArr.add(0.0);
                }
            }

            List<Set<String>> tempDataset = removeProbFromUD();
            Set<String> tempUnique = new HashSet<>();
            for (Set<String> transaction : tempDataset) {
                for (String item : transaction) {
                    tempUnique.add(item);
                }
            }

            // Create seperate item
            for (String t : tempUnique) {
                this.uniqueProbability.put(t, 0.0);
            }

            // Read the prob in transaction
            int i = 0;
            for (Set<String> transaction : tempDataset) {
                for (String t : transaction) {
                    if (probArr.get(i) != 0.0) {
                        double prob = this.uniqueProbability.get(t);
                        if (prob == 0) {
                            prob = 1;
                        }
                        this.uniqueProbability.put(t, prob * probArr.get(i));
                    }
                }
                i++;
            }

            // Generate prob with some item
            for (Map.Entry<String, Double> entry : this.uniqueProbability.entrySet()) {
                String key = entry.getKey();
                double value = entry.getValue();
                if (value == 0.0) {
                    this.uniqueProbability.put(key, probRandom());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double probRandom() {
        Random random = new Random();
        return Double.parseDouble(String.format("%.2f", random.nextDouble()));
    }

    private boolean isProbability(String s) {
        try {
            double prob = Double.parseDouble(s);
            if (prob > 0 && prob < 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Seperate the prob from the UD
    public List<Set<String>> removeProbFromUD() {
        List<Set<String>> res = new ArrayList<>();

        for (Item u : this.UD) {
            res.add(u.getItem());
        }

        return res;
    }

    public Map<String, Double> getProbability() {
        return this.uniqueProbability;
    }

    public List<Item> getUD() {
        return this.UD;
    }

    public String toString() {
        for (Item t : UD) {
            System.out.println(t.toString());
        }
        System.out.println("Probability: " + this.uniqueProbability);
        return "";
    }
}
