
package db61b;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _titles = new String[_rowSize];
        for (int i = 0; i < _rowSize; i++) {
            _titles[i] = columnTitles[i];
        }
        _columns = new ValueList[columnTitles.length];
        for (int i = 0; i < columnTitles.length; i++) {
            _columns[i] = new ValueList();
        }
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _titles.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        if (k > columns()) {
            throw error("%s column does not exist.", k);
        }
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < columns(); i++) {
            if (title.equals(_titles[i])) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        return _columns[0].size();
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            ValueList X = _columns[col];
            return X.get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal  `
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        for (int row = 0; row < size(); row++) {
            int inputSame = 0;
            for (int c = 0; c < _columns.length; c++) {
                if (_columns[c].get(row).equals(values[c])) {
                    inputSame += 1;
                }
            }
            if (inputSame == values.length) {
                return false;
            }
        }
        for (int col = 0; col < _columns.length; col++) {
            _columns[col].add(values[col]);
        }
        return true;
    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        for (int row = 0; row < size(); row++) {
            int inputSame = 0;
            for (int c = 0; c < columns.size(); c++) {
                Column X = columns.get(c);
                if (X.getFrom(rows).equals(_columns[c].get(row))) {
                    inputSame += 1;
                }
            }
            if (inputSame == _columns.length) {
                return false;
            }
        }
        for (int col = 0; col < columns.size(); col++) {
            _columns[col].add(columns.get(col).getFrom(rows));
        }
        return true;
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String line = input.readLine();
            while (line != null) {
                String[] row = line.split(",");
                table.add(row);
                line = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = ",";
            output = new PrintStream(name + ".db");
            String row;
            String[] values = new String[columns()];
            String[] header = new String[columns()];
            StringBuilder build = new StringBuilder();
            for (String string: header) {
                if (build.length() > 0) {
                    build.append(sep);
                }
                build.append(string);
            }
            String b = build.toString();
            output.println(b);
            for (int r = 0; r < size(); r++) {
                for (int c = 0; c < columns(); c++) {
                    values[c] = _columns[c].get(r);
                }
                StringBuilder build2 = new StringBuilder();
                for (String string: values) {
                    if (build2.length() > 0) {
                        build2.append(sep);
                    }
                    build2.append(string);
                }
                String b2 = build2.toString();
                output.println(b2);
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (int rowI = 0; rowI < size(); rowI++) {
            _index.add(rowI);
        }
        for (int r = 0; r < size(); r++) {
            int x = r;
            while (x < size()) {
                int temp;
                if (compareRows(r, x) > 0) {
                    int indexX = _index.indexOf(x);
                    temp = _index.get(indexX);
                    _index.remove(indexX);
                    int indexR = _index.indexOf(r);
                    _index.add(indexR, temp);
                }
                x += 1;
            }
        }
        String indention = "  ";
        String sep = " ";
        String[] values = new String[columns()];

        for (int row = 0; row < size(); row++) {
            int orderedIndex = _index.get(row);
            for (int col = 0; col < columns(); col++) {
                values[col] = get(orderedIndex, col);
            }
            StringBuilder build = new StringBuilder();
            for (String string: values) {
                if (build.length() > 0) {
                    build.append(sep);
                    build.append(string);
                } else {
                    build.append(indention);
                    build.append(string);
                }
            }
            String b = build.toString();
            System.out.println(b);
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        int[] columnIndex = new int[result.columns()];
        int k;
        int i = 0;
        List<Column> col = new ArrayList<>();
        for (String colNames: columnNames) {
            k = findColumn(colNames);
            columnIndex[i] = k;
            i++;
        }
        for (int r = 0; r < size(); r++) {
            boolean works = true;
            String[] valsADD = new String[columnIndex.length];
            for (int c = 0; c < conditions.size(); c++) {
                Condition cond = conditions.get(c);
                if (!cond.test(r)) {
                    works = false;
                }
            }
            for (int c = 0; c < valsADD.length; c++) {
                valsADD[c] = get(r, columnIndex[c]);
            }
            if (works) {
                result.add(valsADD);
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> colInCommon1 = new ArrayList<>();
        List<Column> colInCommon2 = new ArrayList<>();
        for (int i = 0; i < columns(); i++) {
            int comp;
            String colName1 = getTitle(i);
            for (int c = 0; c < table2.columns(); c++) {
                String colName2 = table2.getTitle(c);
                comp = colName1.compareTo(colName2);
                if (comp == 0) {
                    colInCommon1.add(new Column(colName1, this));
                    colInCommon2.add(new Column(colName2, table2));
                }
            }
        }
        List<Integer> t1 = new ArrayList<>();
        List<Integer> t2 = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < table2.size(); j++) {
                if (equijoin(colInCommon1, colInCommon2, i, j)) {
                    t1.add(i);
                    t2.add(j);
                }
            }
        }
        List<Column> colList = new ArrayList<>();
        for (String cName: columnNames) {
            colList.add(new Column(cName, this, table2));
        }
        for (int i = 0; i < t1.size(); i++) {
            boolean works = true;
            for (Condition c: conditions) {
                if (!c.test(t1.get(i), t2.get(i))) {
                    works = false;
                }
            }
            if (works) {
                result.add(colList, t1.get(i), t2.get(i));
            }
        }
        return result;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        Column col1, col2;
        for (int i = 0; i < common1.size(); i++) {
            col1 = common1.get(i);
            col2 = common2.get(i);
            String x1 = col1.getFrom(row1);
            String x2 = col2.getFrom(row2);
            int comparable = x1.compareTo(x2);
            if (comparable != 0) {
                return false;
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
