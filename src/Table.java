import java.util.*;

public class Table {
    private String name;
    private Map<String, List<String>> schema;

    private List<Row> rows;

    public Table() {
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<String>> getSchema() {
        return this.schema;
    }

    public void setSchema(Map<String, List<String>> schema) {
        this.schema = schema;
    }

    public List<Row> getRows() {
        return this.rows;
    }

    public void setRow(Row row) {
        this.rows.add(row);
    }

    public int getSize() {
        List<Row> rows = this.rows;
        return rows.size();
    }

    public boolean isEmpty() {
        int tableSize = getSize();
        if (tableSize == 0) {
            return true;
        }

        return false;
    }
}
