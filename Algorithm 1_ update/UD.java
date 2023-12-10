import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//This class is used for storing an uncerntain database
public class UD {
    private List<Item> UD = new ArrayList<>();

    public UD(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                String[] tempItem = line.split(" ");

                this.UD.add(new Item(tempItem[0], tempItem[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get prob from specific line
    public double getProb(int i) {
        int temp = 0;
        for (Item u : UD) {
            if (temp == i) {
                return u.getProbability();
            } else {
                temp++;
            }
        }
        return -1;
    }

    // Seperate the prob from the UD
    public List<Set<String>> removeProbFromUD() {
        List<Set<String>> res = new ArrayList<>();

        for (Item u : this.UD) {
            res.add(u.getItem());
        }

        return res;
    }

    public List<Item> getUD() {
        return this.UD;
    }

    public String toString() {
        for (Item t : UD) {
            System.out.println(t.toString());
        }
        return "";
    }
}
