
package db61b;


import java.util.HashMap;

/** A collection of Tables, indexed by name.
 *  @author Francisco Perez */
class Database {
    /** An empty database. */
    public Database() {
        database = new HashMap<>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        if (database.isEmpty()) {
            return null;
        }
        return database.get(name);
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        } else if (database.isEmpty()) {
            database.put(name, table);
        } else if (database.containsKey(name)) {
            database.replace(name, table);
        } else {
            database.put(name, table);
        }
    }
    /**HashMap DATABASE which stores the tables loaded.  */
    private HashMap<String, Table> database;
}
