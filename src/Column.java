import java.util.ArrayList;
import java.util.List;

public class Column {
    private String name;
    private List<Object> values;

    public Column() {
        this.values = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getValues() {
        return this.values;
    }

    public void setValue(Object value) {
        this.values.add(value);
    }
}
