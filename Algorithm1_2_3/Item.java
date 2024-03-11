import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


/**
 * This class is used for storing set and prob
 */
public class Item {
    private Set<String> item;

    /**
     * Constructs with full parameters.
     *
     * @param Item   list of element in item.
     */
    public Item(String[] item){
        Set<String> temp = new HashSet<>();
        List<Double> probTemp = new ArrayList<>();
        for (int i=0;i<item.length;i++){
            temp.add(String.valueOf(item[i]));
            probTemp.add(probRandom());
        }

        this.item = temp;
    }

    // Getter and Setter
    public Set<String> getItem(){
        return this.item;
    }

    public void setItem(Set<String> item) {
        this.item = item;
    }

    // Override toString()
    @Override
    public String toString(){
        return String.valueOf(this.item);
    }

    /**
     * generates a random double value
     * @return the range of values returned is [0.0, 1.0).
     */
    private double probRandom(){
        Random random = new Random();
        return random.nextDouble();
    }
}
