package femr.data.models.research;

import femr.common.models.IPatientResearch;
import femr.data.models.PatientResearch;
import femr.util.DataStructure.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the SQL WHERE BODY from the given input
 *
 */
public class ResearchSQLBuilder {

    //private Map<String,String> tableLookup;
   // private Map<String,String> logicLookup;
    private String inputSt;
    private IPatientResearch patientResearch;


    public ResearchSQLBuilder(String input, IPatientResearch patientResearch) {
    //    InitializeTableLookup();
    //    InitializeLogicLookup();
        inputSt = input;

        this.patientResearch =  patientResearch;

    }

    /**
     * Given a preparedStatement and a list of assocciated values for the paramaters
     * this creates the sql statements with the users data and sends it back to the service for it to be executed
     * @param ps the preparedStatement created by the service with the blank values listed
     * @param paramValues the values the users provided that go with the blank parameters
     * @return the modified preparedStatement
     */
    public PreparedStatement BuildQuery(PreparedStatement ps, List<String> paramValues) {

        int count = 1; // we start at 1 not 0
        for(String value : paramValues)
        {
            try {
                ps.setString(count,value);
                count++;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        return ps;
    }


    /**
     * This function creates the initial SQL string that is put in the prepare statement with the
     * blank parameters
     * @param input The SQL WHERE clause generated by the users request
     * @return A pair data type containing the formatted where clause and the list of values to go with the blank
     *         parameters
     */
    public Pair<String,List<String>> CreatePreparedStatement(String input) {

        String[] splitStr = inputSt.split("\\s+");
        int step = 1;
        String tempSQL = " WHERE ";
        String temp = "";
        List<String> wordList = new ArrayList<>();

        for (String word: splitStr)
        {
            // if this is the value step, use an ?
            // instead of the real value as a place holder
            if(step == 3)
            {
                tempSQL += "? ";
                step ++;
                wordList.add(word);
                continue;
            }
            temp = handleInput(step,word);

            // if we get back null then there is a problem and we want to
            // about this so return null
            if(temp == null)
            {
                return null;
            }
            tempSQL += temp + " ";

            step = (step == 4)? 1: step+1;
        }

        Pair<String,List<String>> tempReturn = new Pair<>(tempSQL,wordList);
        return tempReturn;

    }


    /**
     * Takes the current step and the current word or symbol being processed
     * and returns the correct value for the sql query
     *
     * @param step An integer from 1 to 4 indicating witch part of the input we have:
     *             1 is property, 2 is the comparison symbol, 3 the user input value,
     *             4 logical value to combine another group of statements
     * @param input the value to be added
     * @return The correct mapped value
     */
    private String handleInput(int step, String input) {

        switch(step)
        {
            case 1:
                return this.patientResearch.getPatientPropertiesLookup().get(input);
            case 2:
                return this.patientResearch.getConditionLookup().get(input);
            case 3:
                return "'" + input + "'";
            case 4:
                return this.patientResearch.getLogicLookup().get(input);
        }

        return null;
    }



    /**
     * populates the table lookup map.  Add new tables to this function to they will be in the map
     */
//    private void InitializeTableLookup() {
//
//        tableLookup = new HashMap<>();
//
//        tableLookup.put("ID","p.id");
//        tableLookup.put("Age","p.age");
//        tableLookup.put("City","p.city");
//        tableLookup.put("Sex","p.sex");
//        tableLookup.put("Date Taken","p.date_taken");
//        tableLookup.put("Medication","pp.medication_name");
//        tableLookup.put("Treatment","petf.treatment");
//
//    }
//
//    /**
//     * Populates the LocgicLookup map.
//     */
//    private void InitializeLogicLookup() {
//
//        logicLookup = new HashMap<>();
//
//        logicLookup.put("AND","AND");
//        logicLookup.put("OR","OR");
//        logicLookup.put("NOT","NOT");
//        logicLookup.put("XOR","XOR");
//        logicLookup.put("=","=");
//        logicLookup.put("!=","!=");
//        logicLookup.put("<","<");
//        logicLookup.put("<=","<=");
//        logicLookup.put(">",">");
//        logicLookup.put(">=",">=");
//
//    }


}