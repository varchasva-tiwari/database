import java.util.*;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    private static final Class JAVA_DATA_TYPE_INTEGER_CLASS;
    private static final Class JAVA_DATA_TYPE_FLOAT_CLASS;
    private static final Class JAVA_DATA_TYPE_DOUBLE_CLASS;
    private static final Class JAVA_DATA_TYPE_STRING_CLASS;

    private static final Map<String, Class> SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS;
    private static final Map<String, Table> tables;

    static {
        tables = new HashMap<>();

        SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS = new LinkedHashMap<>();
        try {
            JAVA_DATA_TYPE_INTEGER_CLASS = Class.forName("java.lang.Integer");
            JAVA_DATA_TYPE_FLOAT_CLASS = Class.forName("java.lang.Float");
            JAVA_DATA_TYPE_DOUBLE_CLASS = Class.forName("java.lang.Double");
            JAVA_DATA_TYPE_STRING_CLASS = Class.forName("java.lang.String");

            SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.put("int", JAVA_DATA_TYPE_INTEGER_CLASS);
            SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.put("float", JAVA_DATA_TYPE_FLOAT_CLASS);
            SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.put("double", JAVA_DATA_TYPE_DOUBLE_CLASS);
            SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.put("char", JAVA_DATA_TYPE_STRING_CLASS);
            SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.put("varchar", JAVA_DATA_TYPE_STRING_CLASS);
            SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.put("text", JAVA_DATA_TYPE_STRING_CLASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String CREATE_QUERY_REGEX = "(?i)create table [a-zA-Z0-9]+ \\(([a-zA-Z0-9]+, )*[a-zA-Z0-9]+\\)";
    private static final String INSERT_QUERY_REGEX = "(?i)insert into [a-zA-Z0-9]+ values \\((([a-zA-Z0-9]+)|([0-9]*\\.[0-9]*), )*[a-zA-Z0-9]+\\)";
    private static final String INSERT_QUERY_TABLE_NAME_REGEX = "(?i)insert into | values \\(.*";

    private static void createSchema(String query, Map<String, Table> tables, Table table) {
        /* Check if the SQL query is even valid or not
        boolean isValidQuery = query.matches(CREATE_QUERY_REGEX);
        if (!isValidQuery) {
            System.out.println("\nInvalid SQL Query\n");
            return;
        }*/

        // Create table schema, basis which before insertion, we would be able to decide in which column to enter the
        // values

        String queriedTableName = query.replaceAll("((?i)create table )| (\\(.*)","");

        String allColumnsAttributesString = query.replaceAll("(?i)create table [a-zA-Z]+ \\(|\\);","");
        List<String> allColumnsAttributes = List.of(allColumnsAttributesString.split(","));

        // Store schema in the form of columnName -> dataType, isUnique, isNullAllowed, isPrimaryKey
        Map<String, List<String>> schema = new LinkedHashMap<>();

        for (String perColumnAttributes : allColumnsAttributes) {
            List<String> attributes = List.of(perColumnAttributes.trim().split(" "));

            String columnName = attributes.get(0);
            String columnDataType = attributes.get(1);

            List<String> schemaAttributes = new ArrayList<>();
            schemaAttributes.add(columnDataType);
            schema.put(columnName, schemaAttributes);
        }

        table.setName(queriedTableName);
        table.setSchema(schema);

        System.out.println("\nTable created successfully!\n");
    }

    private static void createTable(String sqlQuery) {
        Table table = new Table();
        createSchema(sqlQuery, tables, table);
        tables.put(table.getName(), table);
    }

    private static Map<String, Object> checkSchemaValidityAndGetValuesInInsertableFormat(Map<String, String> valuesToInsertInRow, Map<String, List<String>> storedTableSchema) {
        Boolean isConversionSuccessful;

        List<Object> convertedValues = new ArrayList<>();

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("schemaValid", true);

        for (Map.Entry<String, List<String>> storedTableColumnAttributes : storedTableSchema.entrySet()) {
            String storedTableColumnName = storedTableColumnAttributes.getKey();
            String storedTableColumnSQLDataType = storedTableColumnAttributes.getValue().get(0);
            Class storedTableColumnJavaDataTypeClass = SQL_DATA_TYPE_TO_JAVA_DATA_TYPE_CLASS.get(storedTableColumnSQLDataType);

            String valueToInsertInRow = valuesToInsertInRow.get(storedTableColumnName);

            if (!storedTableColumnJavaDataTypeClass.equals(JAVA_DATA_TYPE_STRING_CLASS)) {
                Map<String, Object> conversionResult = Util.convertStringToWrapper(valueToInsertInRow, storedTableColumnJavaDataTypeClass);

                isConversionSuccessful = (Boolean) conversionResult.get("conversionSuccess");
                if (isConversionSuccessful == null || !isConversionSuccessful) {
                    finalResult.put("schemaValid", false);
                    finalResult.put("values", null);
                    break;
                }

                Object convertedValue = conversionResult.get("convertedValue");
                convertedValues.add(convertedValue);
            } else {
                convertedValues.add(valueToInsertInRow);
                finalResult.put("schemaValid", true);
            }
            finalResult.put("values", convertedValues);
        }

        return finalResult;
    }

    private static void insertInAllColumns(String query) {
        // Check if the SQL query is even valid or not
        boolean isValidQuery = query.matches(INSERT_QUERY_REGEX);
        if (!isValidQuery) {
            System.out.println("here");
            System.out.println("\nInvalid SQL Query\n");
            return;
        }

        if (tables == null || tables.isEmpty()) {
            System.out.println("\nNo tables exist in the DB currently! Please create a table first.\n");
            return;
        }

        // Check if the queried table even exists or not in the database
        String queriedTableName = query.replaceAll(INSERT_QUERY_TABLE_NAME_REGEX,"");

        boolean queriedTableExists = tables.containsKey(queriedTableName);
        if (!queriedTableExists) {
            System.out.println("Queried table does not exist");
            return;
        }

        // Check if the schema in SQL query matches the stored table's schema
        Map<String, List<String>> storedTableSchema = tables.get(queriedTableName).getSchema();

        String valuesToInsertString = query.replaceAll("(?i)insert into [a-zA-Z0-9]+ values \\(|\\)", "");
        List<String> valuesToInsert = Arrays.asList(valuesToInsertString.split(",\\s*"));

        int noOfColumnsInTable = storedTableSchema.size();
        int noOfColumnsInQuery = valuesToInsert.size();

        if (noOfColumnsInQuery != noOfColumnsInTable) {
            System.out.println("\nInvalid SQL Query: No. of columns in query does not match no. of columns in table\n");
            return;
        }

        List<String> storedTableColumnNames = new ArrayList<>(storedTableSchema.keySet());

        Map<String, String> queryRowSchema = new LinkedHashMap<>();

        for (int i = 0; i < storedTableColumnNames.size(); i++) {
            String columnName = storedTableColumnNames.get(i);
            String valueToInsert = valuesToInsert.get(i);

            queryRowSchema.put(columnName, valueToInsert);
        }

        Map<String, Object> schemaValidityTestResult = checkSchemaValidityAndGetValuesInInsertableFormat(queryRowSchema, storedTableSchema);

        boolean isQuerySchemaValid = (boolean) schemaValidityTestResult.get("schemaValid");
        if (!isQuerySchemaValid) {
            System.out.println("\nInvalid SQL Query Schema\n");
        }

        List<Object> valuesToInsertInInsertableFormat = (List<Object>) schemaValidityTestResult.get("values");

        Row row = new Row();
        row.setColumns(storedTableColumnNames, valuesToInsertInInsertableFormat);

        Table table = tables.get(queriedTableName);
        table.setRow(row);
    }

    private static void selectAll(String query) {
        if (tables == null || tables.isEmpty()) {
            System.out.println("\nNo tables exist in the DB currently! Please create a table first.\n");
            return;
        }

        String queriedTableName = query.replaceAll("(?i)select \\* from |;","");

        boolean queriedTableExists = tables.containsKey(queriedTableName);
        if (!queriedTableExists) {
            System.out.println("Queried table does not exist");
            return;
        }

        Table table = tables.get(queriedTableName);
        if (table == null || table.isEmpty()) {
            System.out.println("\nQueried table is empty! Add rows into it first.\n");
            return;
        }

        List<Row> rows = table.getRows();

        System.out.println("Table Name: " + queriedTableName + "\n");

        for (Row row : rows) {
            List<Column> columns = row.getColumns();
            for (Column column : columns) {
                System.out.print(column.getName() + " ");
            }
        }
        System.out.println();

        for (Row row : rows) {
            List<Column> columns = row.getColumns();
            for (Column column : columns) {
                List<Object> values = column.getValues();
                for (Object value : values) {
                    System.out.print(value + " ");
                }
            }
        }
    }

    private static void update() {
    }

    private static void delete() {
    }

    private static void executeQuery(String sqlQuery) {
        if (sqlQuery.matches("create .*")) {
            createTable(sqlQuery);
        } else if (sqlQuery.matches("insert .*")) {
            insertInAllColumns(sqlQuery);
        } else if (sqlQuery.matches("select .*")) {
            selectAll(sqlQuery);
        } else {
            System.out.println("\nInvalid Query\n");
        }
    }

    public static void main(String[]args) {
        while(true) {
            System.out.println("Enter choice: \n\n1. Execute SQL Query\n2. Exit Database Application\n");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.println("\nEnter SQL Query: \n");
                    String sqlQuery = sc.nextLine();

                    executeQuery(sqlQuery);
                    break;
                case 2:
                    System.out.println("\nExiting Database Application. Bye!");
                    System.exit(0);
            }
        }
    }
}