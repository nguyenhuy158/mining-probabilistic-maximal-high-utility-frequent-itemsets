import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


//This class is used for storing set and prob
public class Item {
    private Set<String> item;

    //Init and get data to object
    public Item(String[] item){
        Set<String> temp = new HashSet<>();
        List<Double> probTemp = new ArrayList<>();
        for (int i=0;i<item.length;i++){
            temp.add(String.valueOf(item[i]));
            probTemp.add(probRandom());
        }

        this.item = temp;
    }

    public Set<String> getItem(){
        return this.item;
    }


    public String toString(){
        return String.valueOf(this.item);
    }

    private double probRandom(){
        Random random = new Random();
        return random.nextDouble();
    }
}
