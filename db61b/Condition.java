
package db61b;
import static db61b.Utils.*;
import java.util.List;

/** Represents a single 'where' condition in a 'select' command.
 *  @author Francisco Perez*/
class Condition {

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        int i = 0;
        String[] comparisonSigns = {"<=", ">=", "<", ">", "=", "!="};
        if (col1 == null) {
            firstColumn = false;
            throw error("columns cannot be empty.");
        }
        _col1 = col1;
        firstColumn = true;
        if (col2 != null) {
            secondColumn = true;
            _col2 = col2;
        } else {
            secondColumn = false;
        }
        String sign = comparisonSigns[i];
        while (relation.compareTo(sign) != 0) {
            i += 1;
            sign = comparisonSigns[i];
        }
        compare = i;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }


    /** Assuming that ROWS are row indices in the respective tables
     *  from which my columns are selected, returns the result of
     *  performing the test I denote. */
    boolean test(Integer... rows) {
        String comp1 = _col1.getFrom(rows);
        String comp2;
        if (secondColumn) {
            comp2 = _col2.getFrom(rows);
        } else {
            comp2 = _val2;
        }
        int result = comp1.compareTo(comp2);
        if (result < 0) {
            return compare == 0 || compare == 2 || compare == 5;
        } else if (result > 0) {
            return compare == 1 || compare == 3 || compare == 5;
        } else {
            return compare <= 1 || compare == 4;
        }
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Integer... rows) {
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }

    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;
    /** Returns true if _COL2 is not NULL.  */
    private boolean secondColumn, firstColumn;
    /** Int COMPARE that compares the integer string tests. */
    private int compare;
}
