package db61b;

import static org.junit.Assert.*;

import org.junit.Test;

public class TableTest {

    @Test
    /*Tests the first ADD method in Table.java. */
    public void testFirstADD() {
        String[] list = {"Name", "Grade", "SID"};
        Table exampleONE = new Table(list);
        String[] row1 = {"Francisco", "Freshman", "1"};
        String[] row2 = {"Hannah", "Sophomore", "2"};
        String[] row3 = {"Javi", "Sophomore", "3"};
        boolean firstRow = exampleONE.add(row1);
        boolean secondRow = exampleONE.add(row2);
        boolean thirdRow = exampleONE.add(row3);
        assertTrue(firstRow);
        assertTrue(secondRow);
        assertTrue(thirdRow);
    }

    @Test
    /*Tests the GET method in Table.java. */
    public void testGet() {
        String[] list = {"Name", "Grade", "SID"};
        Table exampleONE = new Table(list);
        String[] row1 = {"Francisco", "Freshman", "1"};
        String[] row2 = {"Hannah", "Sophomore", "2"};
        String[] row3 = {"Javi", "Sophomore", "3"};
        exampleONE.add(row1);
        exampleONE.add(row2);
        exampleONE.add(row3);
        String firstAnswer = exampleONE.get(0, 1);
        assertEquals("Freshman", firstAnswer);
    }

    @Test
    /*Tests the second ADD method in Table.java. */
    public void testSecondADD() {
        String[] list = {"Name", "Grade", "SID", "Gender"};
        Table exampleONE = new Table(list);
        String[] row1 = {"Francisco", "Freshman", "1", "M"};
        String[] row2 = {"Hannah", "Sophomore", "2", "F"};
        String[] row3 = {"Javi", "Sophomore", "3", "M"};
        exampleONE.add(row1);
        exampleONE.add(row2);
        exampleONE.add(row3);

        String[] list2 = {"LastName", "SID", "Sport", "Gender"};
        Table exampleTWO = new Table(list2);
        String[] R1 = {"Perez", "10", "Soccer", "M"};
        String[] R2 = {"Salcedo", "20", "Soccer", "M"};
        String[] R3 = {"Deza", "30", "Softball", "F"};
        exampleTWO.add(R1);
        exampleTWO.add(R2);
        exampleTWO.add(R3);

        Column X = new Column("SID", exampleONE, exampleTWO);
        Column Y = new Column("Sport", exampleONE, exampleTWO);
        Column Z = new Column("Gender", exampleONE, exampleTWO);


        String[] example3 = {"SID", "Gender"};
        Table newTable = new Table(example3);
        System.out.println(exampleONE.get(2, 1));
    }


}
