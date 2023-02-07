import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Column> columns;

    public Row() {
        this.columns = new ArrayList<>();
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setColumns(List<String> columnNames, List<Object> valuesToInsert) {
        for (int i = 0; i < valuesToInsert.size(); i++) {
            Object valueToInsert = valuesToInsert.get(i);

            Column column = new Column();
            column.setName(columnNames.get(i));
            column.setValue(valueToInsert);
            this.columns.add(column);
        }
    }
}
