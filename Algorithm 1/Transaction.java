import java.util.List;

public class Transaction {
    String id;
    List<Attribute> attributes;

    public Transaction(String id, List<Attribute> attributes) {
        this.id = id;
        this.attributes = attributes;
    }
}