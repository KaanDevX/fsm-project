import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class FSM implements Serializable {
    private Set<String> symbols;
    private Set<String> states;
    private Set<String> finalStates;
    private Set<Transitions> transitions;
    private File fileToBeLogged = null;
    private boolean initialStateDeclared = false;
    private boolean isLoggingEnabled = false;
    private String initialState = null; //starting state
    private String currentState = null;


    public FSM() {
        this.symbols = new HashSet<>();
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashSet<>();
    }

    public boolean getLoggingEnabled() {
        return isLoggingEnabled;
    }
    public void setLoggingEnabled(boolean isLoggingEnabled) {
        this.isLoggingEnabled = isLoggingEnabled;
    }
    public String getFileToBeLoggedName() {
        return fileToBeLogged.getName();
    }
    public void setFileToBeLogged(File fileToBeLogged) {
        this.fileToBeLogged = fileToBeLogged;
    }

    public String addSymbols(List<String> commandArguments) {
        List<String> invalidSymbols = new ArrayList<>();
        List<String> duplicateSymbols = new ArrayList<>();
        StringBuilder systemMessage = new StringBuilder();
        for(String argument : commandArguments) {
            if(!argument.matches("[A-Za-z0-9]")) {
                invalidSymbols.add(argument);
            }
            else if(symbols.contains(argument.toUpperCase()) || symbols.contains(argument.toLowerCase())) {
                duplicateSymbols.add(argument);
            }
            else {
                symbols.add(argument);
            }
        }
        if(!invalidSymbols.isEmpty()) {
            if(invalidSymbols.size() == 1) {
                systemMessage.append("\n").append("Warning: invalid symbol ")
                        .append(invalidSymbols.getFirst()).append("\n");
            }
            else {
                systemMessage.append("\n").append("Warning: invalid symbols ");
                for(int i = 0;i<invalidSymbols.size();i++) {
                    systemMessage.append(invalidSymbols.get(i));
                    if(i<invalidSymbols.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append("\n");
            }
        }
        if(!duplicateSymbols.isEmpty()) {
            if(duplicateSymbols.size() == 1) {
                systemMessage.append("\n").append("Warning: ").append(duplicateSymbols.getFirst())
                        .append(" was already declared as a symbol");
            }
            else {
                systemMessage.append("\n").append("Warning: ");
                for(int i = 0;i<duplicateSymbols.size();i++) {
                    systemMessage.append(duplicateSymbols.get(i));
                    if(i<duplicateSymbols.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append(" were already declared as symbols");
            }
        }
        return systemMessage.toString();
    }

    public String printSymbols() {
        List<String> allSymbolsArray = new ArrayList<>(symbols);
        StringBuilder systemMessage = new StringBuilder("\n");
        systemMessage.append("All Symbols: ");
        for(int i = 0;i<allSymbolsArray.size();i++) {
            systemMessage.append(allSymbolsArray.get(i));
            if(i<allSymbolsArray.size()-1) {
                systemMessage.append(", ");
            }
        }
        return systemMessage.toString();
    }

    public String addStates(List<String> commandArguments) {
        List<String> invalidStates = new ArrayList<>();
        List<String> duplicateStates = new ArrayList<>();
        StringBuilder systemMessage = new StringBuilder();
        for(String argument : commandArguments) {
            if(!argument.matches("[A-Za-z][0-9]")) {
                invalidStates.add(argument);
            }
            else {
                boolean isDuplicate = false;
                for(String state : states) {
                    if(argument.equalsIgnoreCase(state)) {
                        isDuplicate = true;
                        duplicateStates.add(argument);
                        break;
                    }
                }
                if(!isDuplicate) {
                    if(!initialStateDeclared && initialState == null) {
                        initialStateDeclared = true;
                        this.initialState = argument;
                        states.add(argument);
                    }
                    states.add(argument);
                }

            }
        }
        if(!invalidStates.isEmpty()) {
            if(invalidStates.size() == 1) {
                systemMessage.append("\n").append("Warning: invalid state ")
                        .append(invalidStates.getFirst()).append("\n");
            }
            else {
                systemMessage.append("\n").append("Warning: invalid states ");
                for(int i = 0;i<invalidStates.size();i++) {
                    systemMessage.append(invalidStates.get(i));
                    if(i< invalidStates.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append("\n");
            }
        }
        if(!duplicateStates.isEmpty()) {
            if(duplicateStates.size() == 1) {
                systemMessage.append("\n").append("Warning: ")
                        .append(duplicateStates.getFirst()).append(" was already declared as a state");
            }
            else {
                systemMessage.append("\n").append("Warning: ");
                for(int i = 0;i<duplicateStates.size();i++) {
                    systemMessage.append(duplicateStates.get(i));
                    if(i<duplicateStates.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append(" were already declared as states");
            }
        }
        return systemMessage.toString();
    }

    public String setInitialState(List<String> commandArguments) { //executes only if commandArguments.size() = 1
        StringBuilder systemMessage = new StringBuilder();
        if (!commandArguments.getFirst().matches("[A-Za-z][0-9]")) { // commandArguments list only has one argument and has the index 0
            systemMessage.append("\n").append("Warning: invalid state ").append(commandArguments.getFirst());
        }
        else {
            if(states.contains(commandArguments.getFirst().toUpperCase()) ||
                    states.contains(commandArguments.getFirst().toLowerCase())) {
                this.initialState = commandArguments.getFirst();
            }
            else {
                this.initialState = commandArguments.getFirst();
                states.add(commandArguments.getFirst());
                systemMessage.append("\n").append("Warning: ").append(commandArguments.getFirst())
                        .append(" was not previously declared as a state");
            }
        }
        return systemMessage.toString();
    }

    public String setFinalStates(List<String> commandArguments) {
        List<String> addedFinalStates = new ArrayList<>();
        List<String> invalidFinalStates = new ArrayList<>();
        List<String> duplicateFinalStates = new ArrayList<>();
        StringBuilder systemMessage = new StringBuilder();
        for(String argument : commandArguments) {
            if(!argument.matches("[A-Za-z][0-9]")) {
                invalidFinalStates.add(argument);
            }
            else {
                boolean isDuplicate = false;
                for(String finalState : finalStates) {
                    if(argument.equalsIgnoreCase(finalState)) {
                        isDuplicate = true;
                        duplicateFinalStates.add(argument);
                        break;
                    }
                }
                if(!isDuplicate) {
                    if(states.contains(argument.toLowerCase()) || states.contains(argument.toUpperCase())) {
                        finalStates.add(argument);
                    }
                    else {
                        if(!initialStateDeclared && initialState == null) {
                            this.initialState = argument;
                            initialStateDeclared = true;
                        }
                        finalStates.add(argument);
                        states.add(argument);
                        addedFinalStates.add(argument);
                    }
                }
            }
        }
        if(!addedFinalStates.isEmpty()) {
            if(addedFinalStates.size() == 1) {
                systemMessage.append("\n").append("Warning: ").append(addedFinalStates.getFirst())
                        .append(" was not previously declared as a state").append("\n");
            }
            else {
                systemMessage.append("\n").append("Warning: ");
                for(int i = 0;i<addedFinalStates.size();i++) {
                    systemMessage.append(addedFinalStates.get(i));
                    if(i<addedFinalStates.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append(" were not previously declared as states").append("\n");
            }
        }
        if(!invalidFinalStates.isEmpty()) {
            if(invalidFinalStates .size() == 1) {
                systemMessage.append("\n").append("Warning: invalid state ")
                        .append(invalidFinalStates.getFirst()).append("\n");
            }
            else {
                systemMessage.append("\n").append("Warning: invalid states ");
                for(int i = 0;i<invalidFinalStates.size();i++) {
                    systemMessage.append(invalidFinalStates.get(i));
                    if(i<invalidFinalStates.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append("\n");
            }
        }
        if(!duplicateFinalStates.isEmpty()) {
            if(duplicateFinalStates.size() == 1) {
                systemMessage.append("\n").append("Warning: ").append(duplicateFinalStates.getFirst())
                        .append(" was already declared as a final state");
            }
            else {
                systemMessage.append("\n").append("Warning: ");
                for(int i = 0;i<duplicateFinalStates.size();i++) {
                    systemMessage.append(duplicateFinalStates.get(i));
                    if(i<duplicateFinalStates.size()-1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append(" were already declared as final states");
            }
        }
        return systemMessage.toString();
    }

    public String printStates() {
        List<String> allStatesArray = new ArrayList<>(states);
        StringBuilder systemMessage = new StringBuilder();
        StringBuilder allStates = new StringBuilder();
        for(int i = 0;i<allStatesArray.size();i++) {
            allStates.append(allStatesArray.get(i));
            if(i<allStatesArray.size()-1) {
                allStates.append(", ");
            }
        }
        List<String> finalStatesArray = new ArrayList<>(finalStates);
        StringBuilder finalStates = new StringBuilder();
        for(int i = 0;i<finalStatesArray.size();i++) {
            finalStates.append(finalStatesArray.get(i));
            if(i<finalStatesArray.size()-1) {
                finalStates.append(", ");
            }
        }
        systemMessage.append("\n");
        systemMessage.append("All states: ").append(allStates).append("\n");
        systemMessage.append("Initial state: ").append(Objects.requireNonNullElse(initialState, " ")).append("\n");
        systemMessage.append("Final states: ").append(finalStates).append("\n");
        systemMessage.append("Current state: ").append(Objects.requireNonNullElse(currentState, " "));
        return systemMessage.toString();
    }

    public String addTransitions(List<String> commandArgumentsForTransitions) {
        List<String> invalidTransitions = new ArrayList<>();
        List<String> duplicateTransitions = new ArrayList<>();
        List<String> transitionsWithUndeclaredParts = new ArrayList<>();
        List<String> overriddenTransitions = new ArrayList<>();
        StringBuilder systemMessage = new StringBuilder();
        for (String argument : commandArgumentsForTransitions) {
            argument = argument.trim();
            if (!argument.matches("[A-Za-z0-9]\\s+[A-Za-z][0-9]\\s+[A-Za-z][0-9]")) { // does not match the transitions format
                invalidTransitions.add(argument);
            } else { // argument of the transition format
                boolean isDuplicate = false;
                for (Transitions transition : transitions) {
                    if (transition.getFullTransition().equalsIgnoreCase(argument)) {
                        duplicateTransitions.add(argument);
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    String[] argumentElements = argument.split("\\s+");
                    boolean isValidTransition = (symbols.contains(argumentElements[0].toLowerCase()) || //String of transition type, valid symbols and states
                            symbols.contains(argumentElements[0].toUpperCase())) &&
                            (states.contains(argumentElements[1].toLowerCase()) ||
                                    states.contains(argumentElements[1].toUpperCase())) &&
                            (states.contains(argumentElements[2].toLowerCase()) ||
                                    states.contains(argumentElements[2].toUpperCase()));
                    if (!isValidTransition) {
                        transitionsWithUndeclaredParts.add(argument);
                    } else { //if it is a valid transition type
                        boolean overridden = false;
                        for (Transitions transition : transitions) {
                            if (transition.getSymbolAndCurrentState().equalsIgnoreCase(argumentElements[0] + " " + argumentElements[1])) {
                                overridden = true;
                                transitions.remove(transition);
                                transitions.add(new Transitions(argumentElements[0], argumentElements[1], argumentElements[2]));
                                overriddenTransitions.add(transition.getFullTransition());
                                break;
                            }
                        }
                        if (!overridden) {
                            transitions.add(new Transitions(argumentElements[0], argumentElements[1], argumentElements[2]));
                        }
                    }
                }
            }
        }
        if (!invalidTransitions.isEmpty()) {
            if (invalidTransitions.size() == 1) {
                systemMessage.append("\n").append("Warning: invalid transition declaration ")
                        .append(invalidTransitions.getFirst()).append("\n");
            } else {
                systemMessage.append("\n").append("Warning: invalid transition declarations ");
                for (int i = 0; i < invalidTransitions.size(); i++) {
                    systemMessage.append(invalidTransitions.get(i));
                    if (i < invalidTransitions.size() - 1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append("\n");
            }
        }
        if (!duplicateTransitions.isEmpty()) {
            if (duplicateTransitions.size() == 1) {
                systemMessage.append("\n").append("Warning: transition was already declared ")
                        .append(duplicateTransitions.getFirst()).append("\n");
            } else {
                systemMessage.append("\n").append("Warning: transitions were already declared ");
                for (int i = 0; i < duplicateTransitions.size(); i++) {
                    systemMessage.append(duplicateTransitions.get(i));
                    if (i < duplicateTransitions.size() - 1) {
                        systemMessage.append(", ");
                    }
                }
                systemMessage.append("\n");
            }
        }
        if(!transitionsWithUndeclaredParts.isEmpty()) {
            StringBuilder transitionsWithUndeclaredPartsMessage = new StringBuilder();
            for (String transitionWithUndeclaredParts : transitionsWithUndeclaredParts) {
                String[] undeclaredPartsTransitionElements = transitionWithUndeclaredParts.split("\\s+");
                if (!symbols.contains(undeclaredPartsTransitionElements[0].toLowerCase()) &&
                        !symbols.contains(undeclaredPartsTransitionElements[0].toUpperCase())) {
                    transitionsWithUndeclaredPartsMessage.append("Undeclared symbol: ")
                            .append(undeclaredPartsTransitionElements[0]).append(",").append(" ");
                }
                if (!states.contains(undeclaredPartsTransitionElements[1].toLowerCase()) &&
                        !states.contains(undeclaredPartsTransitionElements[1].toUpperCase())) {
                    transitionsWithUndeclaredPartsMessage.append("Undeclared current state: ")
                            .append(undeclaredPartsTransitionElements[1]).append(",").append(" ");
                }
                if (!states.contains(undeclaredPartsTransitionElements[2].toLowerCase()) &&
                        !states.contains(undeclaredPartsTransitionElements[2].toUpperCase())) {
                    transitionsWithUndeclaredPartsMessage.append("Undeclared resulting state: ")
                            .append(undeclaredPartsTransitionElements[2]).append(",").append(" ");
                }
                transitionsWithUndeclaredPartsMessage.append(" for the transition ")
                        .append(transitionWithUndeclaredParts).append(" ").append("\n");
            }
            systemMessage.append(transitionsWithUndeclaredPartsMessage);
        }
        if (!overriddenTransitions.isEmpty()) {
            if (overriddenTransitions.size() == 1) {
                systemMessage.append("\n").append("Warning: transition was overridden ")
                        .append(overriddenTransitions.getFirst());
            } else {
                systemMessage.append("\n").append("Warning: transitions were overridden ");
                for (int i = 0; i < overriddenTransitions.size(); i++) {
                    systemMessage.append(overriddenTransitions.get(i));
                    if (i < overriddenTransitions.size() - 1) {
                        systemMessage.append(", ");
                    }
                }
            }
        }
        return systemMessage.toString();
    }

    public String setFile(List<String> commandArguments) { //FOR THE LOG COMMAND
        StringBuilder systemMessage = new StringBuilder();
        if(!commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.txt") &&
                !commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.log")) { //should be a .txt file or a .log file
            systemMessage.append("\n").append("Warning: invalid LOG argument ")
                    .append(commandArguments.getFirst()).append(" (Argument should be of the format fileName.txt or fileName.log )");
        }
        else { //if it matches
            String fileName = commandArguments.getFirst().trim();
            if(fileToBeLogged != null) { //fileToBeLogged is not null
                if(fileToBeLogged.getName().equals(fileName)) { //if the same fileName is written after LOG
                    systemMessage.append("\n").append("Warning: stopped LOGGING into the File ").append(fileName);
                }
                else {
                    systemMessage.append("\n").append("Warning: LOGGING must be stopped on the File ")
                            .append(fileToBeLogged.getName()).append(" to proceed");
                }
            }
            else { //fileToBeLogged is null
                fileToBeLogged = new File(fileName);
                if(!fileToBeLogged.exists()) {
                    try {
                        boolean fileCreated = fileToBeLogged.createNewFile();
                        if(fileCreated) {
                            systemMessage.append("\n").append("Warning: File ").append(fileToBeLogged.getName())
                                    .append(" has been created ").append("\n");
                            isLoggingEnabled = true;
                        }
                        else {
                            systemMessage.append("\n").append("Warning: Error creating the file ").append(fileName);
                            isLoggingEnabled = false;
                            fileToBeLogged = null;
                        }
                    }
                    catch(IOException e) {
                        systemMessage.append("\n").append("Warning: error creating the File ")
                                .append(fileName).append(" to log into ").append(" due to (").append(e.getMessage())
                                .append(")");
                        isLoggingEnabled = false;
                        fileToBeLogged = null;
                    }
                }
                else {
                    systemMessage.append("\n").append("Warning: File ").append(fileName)
                            .append(" already exists").append("\n");
                    isLoggingEnabled = true;
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(fileToBeLogged, false));
                        bw.write("");
                        bw.flush();
                    }
                    catch(FileNotFoundException e) {
                        String message = e.getMessage().toLowerCase();
                        if(message.contains("engellendi") || message.contains("denied")) { //no permission
                            systemMessage.append("\n").append("Warning: no permission to log into the File ")
                                    .append(fileName);
                        }
                        isLoggingEnabled = false;
                        fileToBeLogged = null;
                    }
                    catch(IOException e) {
                        systemMessage.append("\n").append("Warning: error logging into File ")
                                .append(fileName).append(" due to (").append(e.getMessage())
                                .append(")");
                        isLoggingEnabled = false;
                        fileToBeLogged = null;
                    }
                }

            }
        }
        return systemMessage.toString();
    }

    public void logIntoFile(String userInputOrSystemMessage) { //LOG COMMAND
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileToBeLogged, true));
            bw.write(userInputOrSystemMessage);
            bw.flush();
            bw.close();
        }
        catch(IOException e) {
            System.out.println("Warning: error logging into the file " + fileToBeLogged.getName() + " due to ("
            + e.getMessage() + ")");
            isLoggingEnabled = false;
            fileToBeLogged = null;
        }
    }

    public String printAllToConsole() { //PRINT COMMAND
        StringBuilder systemMessage = new StringBuilder();
        List<String> allSymbolsArray = new ArrayList<>(symbols);
        StringBuilder allSymbols = new StringBuilder("{");
        List<String> allStatesArray = new ArrayList<>(states);
        StringBuilder allStates = new StringBuilder("{");
        List<String> allFinalStatesArray = new ArrayList<>(finalStates);
        StringBuilder allFinalStates = new StringBuilder("{");
        List<Transitions> allTransitionsArray = new ArrayList<>(transitions);
        StringBuilder allTransitions = new StringBuilder("{");

        for(int i = 0;i<allSymbolsArray.size();i++) { //symbols
            allSymbols.append(allSymbolsArray.get(i));
            if(i<allSymbolsArray.size()-1) {
                allSymbols.append(",");
            }
        }
        allSymbols.append("}");
        systemMessage.append("SYMBOLS ").append(allSymbols).append("\n");

        for(int i = 0;i<allStatesArray.size();i++) { //all states
            allStates.append(allStatesArray.get(i));
            if(i<allStatesArray.size()-1) {
                allStates.append(",");
            }
        }
        allStates.append("}");
        systemMessage.append("STATES ").append(allStates).append("\n");

        if(initialState == null) { //INITIAL STATE
            systemMessage.append("INITIAL STATE {}").append("\n");
        }
        else {
            systemMessage.append("INITIAL STATE {").append(initialState).append("}").append("\n");
        }

        for(int i = 0;i<allFinalStatesArray.size();i++) { // finalStates
            allFinalStates.append(allFinalStatesArray.get(i));
            if(i<allFinalStatesArray.size()-1) {
                allFinalStates.append(",");
            }
        }
        allFinalStates.append("}");
        systemMessage.append("FINAL STATES ").append(allFinalStates).append("\n");

        if(currentState == null) { //current state
            systemMessage.append("CURRENT STATE {}").append("\n");
        }
        else {
            systemMessage.append("CURRENT STATE {").append(currentState).append("}").append("\n");
        }

        for(int i = 0;i<allTransitionsArray.size();i++) { // transitions
            allTransitions.append(allTransitionsArray.get(i).getFullTransition());
            if(i<allTransitionsArray.size()-1) {
                allTransitions.append(",");
            }
        }
        allTransitions.append("}");
        systemMessage.append("TRANSITIONS ").append(allTransitions);
        return systemMessage.toString();
    }

    public String printAllToFile(List<String> commandArguments) { //PRINT COMMAND
        StringBuilder systemMessage = new StringBuilder();
        if(!commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.txt") &&
                !commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.log")) { // it is not a text file
            systemMessage.append("\n").append("Warning: invalid PRINT argument ")
                    .append(commandArguments.getFirst()).append(" (Argument should be of the format fileName.txt or fileName.log)");
        }
        else {
            String fileName = commandArguments.getFirst().trim();
            File fileToPrintTo = new File(fileName);
            if(fileToBeLogged != null && fileToPrintTo.getName().equals(fileToBeLogged.getName()) && isLoggingEnabled) {
                systemMessage.append("\n").append("Warning: LOGGING is already in progress into the file ")
                        .append(fileName).append(", PRINT ").append(fileName).append("; command redundant");
            }
            else {
                if (fileToPrintTo.exists()) { //Already existing file
                    systemMessage.append("\n").append("Warning: file ").append(fileToPrintTo.getName())
                            .append(" already exists").append("\n");
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(fileToPrintTo, false)); //Overriding
                        String allElements = printAllToConsole();
                        bw.write(allElements);
                        bw.flush();
                        bw.close();
                    }
                    catch(FileNotFoundException e) {
                        String message = e.getMessage().toLowerCase();
                        if(message.contains("engellendi") || message.contains("denied")) { //no permission
                            systemMessage.append("\n").append("Warning: no permission to print to the File ")
                                    .append(fileName);
                        }
                    }
                    catch (IOException e) {
                        systemMessage.append("\n").append("Warning: error printing to File ")
                                .append(fileToPrintTo).append(" due to (").append(e.getMessage())
                                .append(")");
                    }
                }
                else {
                    try {
                        boolean isCreated = fileToPrintTo.createNewFile();
                        if (isCreated) {
                            systemMessage.append("\n").append("Warning: File ").append(fileToPrintTo.getName())
                                    .append(" has been created, PRINTING in process");
                            try {
                                BufferedWriter bw = new BufferedWriter(new FileWriter(fileToPrintTo, false)); //Overriding
                                String allElements = printAllToConsole();
                                bw.write(allElements);
                                bw.flush();
                                bw.close();
                            }
                            catch (IOException e) {
                                systemMessage.append("\n").append("Warning: error printing to File ")
                                        .append(fileToPrintTo).append(" due to (").append(e.getMessage())
                                        .append(")");
                            }
                        } else {
                            systemMessage.append("\n").append("Warning: error creating the file ")
                                    .append(fileToPrintTo.getName());
                        }
                    } catch (IOException e) {
                        systemMessage.append("\n").append("Warning: error printing to File ")
                                .append(fileToPrintTo).append(" due to (").append(e.getMessage())
                                .append(")");
                    }
                }
            }
        }
        return systemMessage.toString();
    }

    public String clearAll() { //CLEAR COMMAND
        StringBuilder systemMessage = new StringBuilder();
        symbols.clear();
        states.clear();
        finalStates.clear();
        transitions.clear();
        this.initialState = null;
        this.initialStateDeclared = false;
        this.currentState = null;
        systemMessage.append("\n").append("Clear successful");
        return systemMessage.toString();
    }

    public String serializeFSM(List<String> commandArguments) {
        StringBuilder systemMessage = new StringBuilder();
        if(!commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.ser") &&
                !commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.bin")) {
            systemMessage.append("\n").append("Warning: invalid COMPILE argument ")
                    .append(commandArguments.getFirst())
                    .append(" (Argument should be of the format fileName.ser or fileName.bin)");
        }
        else {
            String fileToSaveFSMName = commandArguments.getFirst().trim();
            File fileToSaveFSM = new File(fileToSaveFSMName);
            if(fileToSaveFSM.exists()) { //fileName does exist
                systemMessage.append("\n").append("Warning: file ").append(fileToSaveFSMName)
                        .append(" already exists ").append("\n");
                try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSaveFSM, false))) {
                    oos.writeObject(this);
                    systemMessage.append("\n").append("Compile successful");
                }
                catch(FileNotFoundException e) {
                    String message = e.getMessage().toLowerCase();
                    if(message.contains("engellendi") || message.contains("denied")) { //no permission
                        systemMessage.append("\n").append("Warning: no permission to save to the File ")
                                .append(fileToSaveFSMName);
                    }
                }
                catch(IOException e) {
                    systemMessage.append("\n").append("Warning: error saving to the File ")
                            .append(fileToSaveFSMName).append(" due to (").append(e.getMessage())
                            .append(")");
                }
            }
            else { //fileName does not exist
                try {
                    boolean isCreated = fileToSaveFSM.createNewFile();
                    if(isCreated) {
                        systemMessage.append("\n").append("Warning: File ").append(fileToSaveFSMName)
                                .append(" has been created ").append("\n");
                        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSaveFSM,false))) {
                            oos.writeObject(this);
                            systemMessage.append("\n").append("Compile successful");
                        }
                        catch (IOException e) {
                            systemMessage.append("\n").append("Warning: error saving to the File ")
                                    .append(fileToSaveFSMName).append(" due to (").append(e.getMessage())
                                    .append(")");
                        }
                    }
                }
                catch (IOException e) {
                    systemMessage.append("\n").append("Warning: error saving to the File ")
                            .append(fileToSaveFSMName).append(" due to (").append(e.getMessage())
                            .append(")");
                }
            }
        }
        return systemMessage.toString();
    }

    public String deserializeFSM(File loadFile) {
        StringBuilder systemMessage = new StringBuilder();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(loadFile))) {
            FSM deserializedFSM = (FSM) ois.readObject();
            this.symbols = deserializedFSM.symbols;
            this.states = deserializedFSM.states;
            this.finalStates = deserializedFSM.finalStates;
            this.transitions = deserializedFSM.transitions;
            this.fileToBeLogged = deserializedFSM.fileToBeLogged;
            this.initialStateDeclared = deserializedFSM.initialStateDeclared;
            this.isLoggingEnabled = deserializedFSM.isLoggingEnabled;
            this.initialState = deserializedFSM.initialState;
            this.currentState = deserializedFSM.currentState;
            systemMessage.append("\n").append("Loading successful").append("\n");
        }
        catch (ClassNotFoundException e) {
            systemMessage.append("\n").append("Warning: loading failed due to  missing class definition (")
                    .append(e.getMessage()).append(")");
        }
        catch(FileNotFoundException e) {
            String message = e.getMessage().toLowerCase();
            if(message.contains("engellendi") || message.contains("denied")) { //no permission
                systemMessage.append("\n").append("Warning: no permission to load from the File ")
                        .append(loadFile.getName());
            }
        }
        catch(IOException e) {
            systemMessage.append("\n").append("Warning: loading failed due to (").append(e.getMessage())
                    .append(")");
        }
        return systemMessage.toString();
    }

    public String processSymbols(String compressedString) { //we have to check for valid declared symbols, the transitions
        StringBuilder systemMessage = new StringBuilder();
        if(initialState == null) { //no state has ever been declared yet
            systemMessage.append("\n").append("Warning: no states exist for the command EXECUTE");
        }
        else { //at least one state has been declared;
            if (currentState == null) {
                currentState = initialState;
            }
            List<String> invalidSymbols = new ArrayList<>();
            List<String> nonDeclaredSymbols = new ArrayList<>();
            systemMessage.append("\n").append(currentState).append(" ");
            for (char symbolChar : compressedString.toCharArray()) {
                String symbolToBeProcessed = String.valueOf(symbolChar).trim();
                if (!symbolToBeProcessed.matches("[A-Za-z0-9]")) { //invalid symbol
                    invalidSymbols.add(symbolToBeProcessed);
                } else { //valid symbol
                    if (!symbols.contains(symbolToBeProcessed.toLowerCase()) &&
                            !symbols.contains(symbolToBeProcessed.toUpperCase())) { //has not yet been declared
                        nonDeclaredSymbols.add(symbolToBeProcessed);
                    } else { //valid declared symbol
                        for (Transitions transition : transitions) {
                            if (transition.getSymbolAndCurrentState()
                                    .equalsIgnoreCase(symbolToBeProcessed + " " + currentState)) {
                                currentState = transition.getResultingState();
                                systemMessage.append(transition.getResultingState()).append(" ");
                                break;
                            }
                        }
                    }
                }
            }
            String[] helper = systemMessage.toString().split("\\s+");
            ArrayList<String> printedStates = new ArrayList<>(Arrays.asList(helper));
            if (finalStates.contains(printedStates.getLast().toLowerCase()) ||
                    finalStates.contains(printedStates.getLast().toUpperCase())) {
                systemMessage.append("YES").append("\n");
            } else {
                systemMessage.append("NO").append("\n");
            }

            if (!invalidSymbols.isEmpty()) {
                if (invalidSymbols.size() == 1) {
                    systemMessage.append("\n").append("Warning: invalid symbol ")
                            .append(invalidSymbols.getFirst()).append("\n");
                } else {
                    systemMessage.append("\n").append("Warning: invalid symbols ");
                    for (int i = 0; i < invalidSymbols.size(); i++) {
                        systemMessage.append(invalidSymbols.get(i));
                        if (i < invalidSymbols.size() - 1) {
                            systemMessage.append(", ");
                        }
                    }
                    systemMessage.append("\n");
                }
            }

            if (!nonDeclaredSymbols.isEmpty()) {
                if (nonDeclaredSymbols.size() == 1) {
                    systemMessage.append("\n").append("Warning: undeclared symbol ")
                            .append(nonDeclaredSymbols.getFirst()).append("\n");
                } else {
                    systemMessage.append("\n").append("Warning: undeclared symbols ");
                    for (int i = 0; i < nonDeclaredSymbols.size(); i++) {
                        systemMessage.append(nonDeclaredSymbols.get(i));
                        if (i < nonDeclaredSymbols.size() - 1) {
                            systemMessage.append(", ");
                        }
                    }
                    systemMessage.append("\n");
                }
            }
        }
        return systemMessage.toString();
    }
}

class ExecuteFSM {

    public static void main(String[] args) {
        FSM fsm = new FSM();
        Scanner scanner = new Scanner(System.in);
        CommandProcessor cp = new CommandProcessor(fsm);
        if(args.length == 0) { //no file as an argument
            cp.runFSM(scanner);
        }
        else if(args.length == 1) {
            String message = checkArgument(args[0]);
            if(message.equals("Valid")) {
                File readFile = new File(args[0]);
                String systemMessage = cp.runFSMWithFileCommands(readFile);
                System.out.println(systemMessage);
            }
            else if(message.equals("File does not exist")) {
                System.out.println("Warning: failed running FSM, File provided does not exist");
            }
            else {
                System.out.println("Warning: failed running FSM, invalid File Name " +
                        "(Argument should be of the format fileName.txt or fileName.log)");
            }
        }
        else {
            System.out.println("Warning: failed running FSM (Only one argument is expected)");
        }
    }

    public static String checkArgument(String argument) {
        if(argument.matches("[^\\\\/:*?\"<>|]+\\.txt") ||
                argument.matches("[^\\\\/:*?\"<>|]+\\.log")) {
            File readFile = new File(argument);
            if (readFile.exists()) {
                return "Valid";
            } else {
                return "File does not exist";
            }
        }
        else {
            return "File name of the wrong format";
        }
    }
}

class CommandProcessor {
    private final FSM fsm;
    File loadFile = null;
    private boolean loadCommandLoadingEnabled = false; //.ser or .bin file
    File readFile = null;
    private boolean loadCommandReadingEnabled = false; //.txt or .log file

    public CommandProcessor(FSM fsm) {
        this.fsm = fsm;
    }

    public String getRawUserInput(Scanner scanner) { // will return the whole user input
        StringBuilder inputString = new StringBuilder(); //no modifications
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(!line.contains(";")) {
                inputString.append(line).append("\n");
            }
            else {
                inputString.append(line);
                break;
            }
        }
        return inputString.toString().trim();
    }

    public String getModifiedRawUserInput(String rawUserInput) { //Execution does not start until a ";" is seen, There might be multiple lines
        String[] helperArray = rawUserInput.split("\n"); //new lines and the ";" is not preserved
        StringBuilder modifiedRawUserInput = new StringBuilder(); //returns the rawUserInput without ; and newlines
        for(String s : helperArray) {
            modifiedRawUserInput.append(s);
        }
        String temporary = modifiedRawUserInput.toString();
        return temporary.substring(0,temporary.indexOf(";"));
    }

    public String getCommandKeyword(String modifiedRawUserInput) { //return the command keyword of the modifiedRawUserInput
        String[] modifiedRawUserInputParts = modifiedRawUserInput.split("\\s+");
        return modifiedRawUserInputParts[0].trim();
    }

    public List<String> getCommandArguments(String modifiedRawUserInput) { //would not work with the TRANSITIONS command since we need to split by "," there
        String[] modifiedRawUserInputParts = modifiedRawUserInput.split("\\s+");
        String[] commandArgumentsArray = Arrays.copyOfRange(modifiedRawUserInputParts, 1, modifiedRawUserInputParts.length);
        return Arrays.asList(commandArgumentsArray);
    }

    public List<String> getCommandArgumentsForTransitions(String modifiedRawUserInput) {
        modifiedRawUserInput = modifiedRawUserInput.replaceAll("\\s+", " ");
        modifiedRawUserInput = modifiedRawUserInput.substring("TRANSITIONS".length()).trim();
        String[] modifiedRawUserInputParts = modifiedRawUserInput.split(",");
        for(String modifiedRawUserInputPart : modifiedRawUserInputParts) {
            modifiedRawUserInputPart = modifiedRawUserInputPart.trim();
        }
        return Arrays.asList(modifiedRawUserInputParts);
    }

    public String getCompressedStringForExecution(List<String> commandArguments) { //for the EXECUTE
        StringBuilder compressedString = new StringBuilder();
        for(String argument : commandArguments) {
            compressedString.append(argument);
        }
        return compressedString.toString();
    }

    public String checkArgumentForLoadCommand(List<String> commandArguments) {
        StringBuilder systemMessage = new StringBuilder();
        if (commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.ser") ||
                commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.bin")) {
            String fileToLoadFromString = commandArguments.getFirst();
            File fileToLoadFrom = new File(fileToLoadFromString);
            if (fileToLoadFrom.exists()) {
                loadFile = fileToLoadFrom;
                loadCommandLoadingEnabled = true;
                systemMessage.append("\n").append("Warning: File ").append(fileToLoadFrom.getName())
                        .append(" exists");
            } else {
                systemMessage.append("\n").append("Warning: File ").append(fileToLoadFrom.getName())
                        .append(" does not exist");
            }
        } else if (commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.txt") ||
                commandArguments.getFirst().matches("[^\\\\/:*?\"<>|]+\\.log")) {
            String fileToReadFromString = commandArguments.getFirst();
            File fileToReadFrom = new File(fileToReadFromString);
            if (fileToReadFrom.exists()) {
                readFile = fileToReadFrom;
                loadCommandReadingEnabled = true;
                systemMessage.append("\n").append("Warning: File ").append(fileToReadFrom.getName())
                        .append(" exists");
            } else {
                systemMessage.append("\n").append("Warning: File ").append(fileToReadFrom.getName())
                        .append(" does not exist");
            }
        }
        else {
            systemMessage.append("\n").append("Warning: invalid LOAD argument ").append(commandArguments.getFirst())
                    .append(" (Argument should be of the format fileName.ser, fileName.bin or " +
                            "fileName.txt, fileName.log)");
        }
        return systemMessage.toString();
    }


    public String getRawFileContents(String fileName) { //Get the file content without any modifications
        StringBuilder rawFileContentsString = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while((line = br.readLine()) != null) {
                rawFileContentsString.append(line).append("\n");
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return rawFileContentsString.toString();
    }

    public String[] processRawFileContents(String rawFileContents) {
        rawFileContents = rawFileContents.replace("\r", "").replace("\n", " ");
        rawFileContents = rawFileContents.replaceAll("\\s+", " ");
        return rawFileContents.split(";");
    }

    public static List<String> getValidCommandsFromTextFile(String[] helper) {
        List<String> validArguments = new ArrayList<>();
        for(String helperElement : helper) {
            if(helperElement.matches(".*\\bEXIT\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("EXIT"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bSYMBOLS\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("SYMBOLS"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bFINAL-STATES\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("FINAL-STATES"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bINITIAL-STATE\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("INITIAL-STATE"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bSTATES\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("STATES"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bTRANSITIONS\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("TRANSITIONS"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bPRINT\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("PRINT"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bCOMPILE\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("COMPILE"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bCLEAR\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("CLEAR"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bEXECUTE\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("EXECUTE"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bLOAD\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("LOAD"));
                validArguments.add(helperElement);
            }
            else if(helperElement.matches(".*\\bLOG\\b.*")) {
                helperElement = helperElement.substring(helperElement.indexOf("LOG"));
                validArguments.add(helperElement);
            }
        }
        return validArguments;
    }

    public void runFSM(Scanner scanner) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = currentTime.format(dateTimeFormatter);
        System.out.println("FSM DESIGNER <64cc2f4> " + currentTimeString);

        while(true) {
            System.out.println("?");
            String rawUserInput = getRawUserInput(scanner);
            String modifiedRawUserInput = getModifiedRawUserInput(rawUserInput);
            List<String> commandArguments = getCommandArguments(modifiedRawUserInput);
            if(fsm.getLoggingEnabled()) {
                fsm.logIntoFile("\n");
                fsm.logIntoFile("\n");
                fsm.logIntoFile(rawUserInput);
                fsm.logIntoFile("\n");
            }
            if(getCommandKeyword(modifiedRawUserInput).equals("EXIT")) { // EXIT command keyword
                if(commandArguments.isEmpty()) {
                    System.out.println("TERMINATED BY USER");
                    scanner.close();
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("TERMINATED BY USER");
                    }
                    return;
                }
                else {
                    System.out.println("Warning: invalid command for EXIT (No arguments expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for EXIT (No arguments expected)");
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("SYMBOLS")) { //SYMBOLS command keyword
                String systemMessage;
                if(commandArguments.isEmpty()) { //print symbols
                    systemMessage = fsm.printSymbols();
                }
                else { //add symbols
                    systemMessage = fsm.addSymbols(commandArguments);
                }
                System.out.println(systemMessage);
                if(fsm.getLoggingEnabled()) {
                    fsm.logIntoFile(systemMessage);
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("STATES")) { //STATES command keyword
                String systemMessage;
                if(commandArguments.isEmpty()) {
                    systemMessage = fsm.printStates();
                }
                else { //add symbols
                    systemMessage = fsm.addStates(commandArguments);
                }
                System.out.println(systemMessage);
                if(fsm.getLoggingEnabled()) {
                    fsm.logIntoFile(systemMessage);
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("INITIAL-STATE")) { //INITIAL-STATE command keyword
                if(commandArguments.size() == 1) {
                    String systemMessage = fsm.setInitialState(commandArguments);
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
                else {
                    System.out.println("Warning: invalid command for INITIAL-STATE (Only one argument is expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for INITIAL-STATE (Only one argument is expected)");
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("FINAL-STATES")) { //FINAL-STATES command keyword
                if(commandArguments.isEmpty()) {
                    System.out.println("Warning: invalid command for FINAL-STATES (Arguments are expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for FINAL-STATES (Arguments are expected)");
                    }
                }
                else {
                    String systemMessage = fsm.setFinalStates(commandArguments);
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("TRANSITIONS")) { //TRANSITIONS command keyword
                List<String> commandArgumentsForTransitions = getCommandArgumentsForTransitions(modifiedRawUserInput);
                if(commandArguments.isEmpty()) {
                    System.out.println("Warning: invalid command for TRANSITIONS (Arguments are expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for TRANSITIONS (Arguments are expected)");
                    }
                }
                else {
                    String systemMessage = fsm.addTransitions(commandArgumentsForTransitions);
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("CLEAR")) { //CLEAR command keyword
                if(commandArguments.isEmpty()) {
                    String systemMessage = fsm.clearAll();
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
                else {
                    System.out.println("Warning invalid command for CLEAR (No arguments expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning invalid command for CLEAR (No arguments expected)");
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("LOG")) { //LOG command keyword
                if(commandArguments.isEmpty()) { //we have to set isLoggingEnabled to false LOG;
                    if(fsm.getLoggingEnabled()) {
                        System.out.println("Warning: STOPPED LOGGING INTO THE FILE " + fsm.getFileToBeLoggedName());
                        fsm.logIntoFile("Warning: STOPPED LOGGING INTO THE FILE " + fsm.getFileToBeLoggedName());
                        fsm.setLoggingEnabled(false);
                        fsm.setFileToBeLogged(null);
                    }
                    else {
                        System.out.println("Warning: LOGGING was not enabled");
                    }
                }
                else if(commandArguments.size() == 1) { // we have to check the fileName LOG <fileName>; or LOG b;
                    String systemMessage = fsm.setFile(commandArguments);
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                    if(systemMessage.contains("Warning: stopped LOGGING into the File ")) {
                        fsm.setLoggingEnabled(false);
                        fsm.setFileToBeLogged(null);
                    }
                }
                else { //LOG A B;
                    System.out.println("Warning: invalid command for LOG (One or no arguments are expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for LOG (One or no arguments are expected)");
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("PRINT")) { //PRINT command keyword
                if(commandArguments.isEmpty()) {
                    String systemMessage = fsm.printAllToConsole();
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
                else if(commandArguments.size() == 1) { //Only one argument like a fileName or an invalid one like a
                    String systemMessage = fsm.printAllToFile(commandArguments);
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
                else {
                    System.out.println("Warning: invalid command for PRINT (One or no arguments are expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for PRINT (One or no arguments are expected)");
                    }
                }
            }
            else if (getCommandKeyword(modifiedRawUserInput).equals("COMPILE")) { //COMPILE command keyword
                if(commandArguments.isEmpty()) {
                    System.out.println("Warning: invalid command for COMPILE (One argument is expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for COMPILE (One argument is expected)");
                    }
                }
                else if (commandArguments.size() == 1) {
                    String systemMessage = fsm.serializeFSM(commandArguments);
                    System.out.println(systemMessage);
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
                else {
                    System.out.println("Warning: invalid command for COMPILE (Only one argument is expected)");
                    if (fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning: invalid command for COMPILE (Only one argument is expected)");
                    }
                }

            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("EXECUTE")) { //EXECUTE command keyword
                if(commandArguments.isEmpty()) {
                    System.out.println("Warning invalid command for EXECUTE (Arguments are expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning invalid command for EXECUTE (Arguments are expected)");
                    }
                }
                else {
                    String compressedString = getCompressedStringForExecution(commandArguments);
                    String systemMessage = fsm.processSymbols(compressedString);
                    System.out.println(systemMessage);
                    if (fsm.getLoggingEnabled()) {
                        fsm.logIntoFile(systemMessage);
                    }
                }
            }
            else if(getCommandKeyword(modifiedRawUserInput).equals("LOAD")) { //LOAD command keyword
                if(commandArguments.isEmpty()) {
                    System.out.println("Warning invalid command for LOAD (An argument is expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning invalid command for LOAD (An argument is expected)");
                    }
                }
                else if(commandArguments.size() == 1) {
                    String systemMessage1 = checkArgumentForLoadCommand(commandArguments);
                    System.out.println(systemMessage1);
                    if(loadCommandReadingEnabled) {
                        String systemMessage = runFSMWithFileCommands(readFile);
                        System.out.println(systemMessage);
                        if(systemMessage.contains("TERMINATED BY USER")) {
                            return;
                        }
                    }
                    if(loadCommandLoadingEnabled) {
                        String systemMessage = fsm.deserializeFSM(loadFile);
                        loadFile = null;
                        loadCommandLoadingEnabled = false;
                        System.out.println(systemMessage);
                        if(fsm.getLoggingEnabled()) {
                            fsm.logIntoFile(systemMessage);
                        }
                    }
                }
                else {
                    System.out.println("Warning invalid command for LOAD (Only one argument is expected)");
                    if(fsm.getLoggingEnabled()) {
                        fsm.logIntoFile("Warning invalid command for LOAD (Only one argument is expected)");
                    }
                }
            }
            else {
                System.out.println("Warning: invalid command " + getCommandKeyword(modifiedRawUserInput));
                if(fsm.getLoggingEnabled()) {
                    fsm.logIntoFile(("Warning: invalid command " + getCommandKeyword(modifiedRawUserInput)));
                    fsm.logIntoFile("\n");
                }
            }
        }
    }

    public String runFSMWithFileCommands(File fileName) {
        StringBuilder systemMessage = new StringBuilder();
        String rawFileContents = getRawFileContents(fileName.getName());
        if(rawFileContents.isEmpty()) {
            systemMessage.append("\n").append("Warning: ").append(fileName).append(" is empty");
        }
        else {
            List<String> validCommandsArray = getValidCommandsFromTextFile(processRawFileContents(rawFileContents));
            for(String validCommands : validCommandsArray) {
                String commandKeyword = getCommandKeyword(validCommands);
                List<String> commandArguments = getCommandArguments(validCommands);

                switch (commandKeyword) {
                    case "EXIT" -> {
                        if (commandArguments.isEmpty()) {
                            systemMessage.append("\n").append("TERMINATED BY USER");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("TERMINATED BY USER");
                            }
                            String systemMessageString = systemMessage.toString().replaceAll("\n+","\n");
                            String[] helper = systemMessageString.split("\n");
                            StringBuilder sb = new StringBuilder();
                            for(String line : helper) {
                                sb.append(line).append("\n").append("\n");
                            }
                            return sb.toString();
                        } else {
                            systemMessage.append("\n").append("Warning: invalid command for EXIT (No arguments expected)");

                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for EXIT (No arguments expected)");
                            }
                        }
                    }
                    case "SYMBOLS" -> { //SYMBOLS command keyword
                        String message;
                        if (commandArguments.isEmpty()) { //print symbols
                            message = fsm.printSymbols();
                        } else { //add symbols
                            message = fsm.addSymbols(commandArguments);
                        }
                        systemMessage.append("\n").append(message);
                        if (fsm.getLoggingEnabled()) {
                            fsm.logIntoFile("\n");
                            fsm.logIntoFile(message);
                        }
                    }
                    case "STATES" -> { //STATES command keyword
                        String message;
                        if (commandArguments.isEmpty()) { //print symbols
                            message = fsm.printStates();
                        } else { //add symbols
                            message = fsm.addStates(commandArguments);
                        }
                        systemMessage.append("\n").append(message);
                        if (fsm.getLoggingEnabled()) {
                            fsm.logIntoFile("\n");
                            fsm.logIntoFile(message);
                        }
                    }
                    case "INITIAL-STATE" -> {
                        if (commandArguments.size() == 1) {
                            String message = fsm.setInitialState(commandArguments);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        } else {
                            systemMessage.append("\n").append("Warning: invalid command for INITIAL-STATE (Only one argument is expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for INITIAL-STATE (Only one argument is expected)");
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "FINAL-STATES" -> {
                        if (commandArguments.isEmpty()) {
                            systemMessage.append("\n").append("Warning: invalid command for FINAL-STATES (Arguments are expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for FINAL-STATES (Arguments are expected)");
                                fsm.logIntoFile("\n");
                            }
                        } else {
                            String message = fsm.setFinalStates(commandArguments);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "TRANSITIONS" -> {
                        List<String> commandArgumentsForTransitions = getCommandArgumentsForTransitions(validCommands);
                        if (commandArguments.isEmpty()) {
                            systemMessage.append("\n").append("Warning: invalid command for TRANSITIONS (Arguments are expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for TRANSITIONS (Arguments are expected)");
                                fsm.logIntoFile("\n");
                            }
                        } else {
                            String message = fsm.addTransitions(commandArgumentsForTransitions);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "CLEAR" -> {
                        if (commandArguments.isEmpty()) {
                            String message = fsm.clearAll();
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        } else {
                            systemMessage.append("\n").append("Warning invalid command for CLEAR (No arguments expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning invalid command for CLEAR (No arguments expected)");
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "LOG" -> {
                        if (commandArguments.isEmpty()) { //we have to set isLoggingEnabled to false LOG;
                            if (fsm.getLoggingEnabled()) {
                                String fileToBeLoggedName = fsm.getFileToBeLoggedName();
                                systemMessage.append("Warning: STOPPED LOGGING INTO THE FILE ").append(fileToBeLoggedName);
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: STOPPED LOGGING INTO THE FILE " + fsm.getFileToBeLoggedName());
                                fsm.logIntoFile("\n");
                                fsm.setLoggingEnabled(false);
                                fsm.setFileToBeLogged(null);
                            } else {
                                systemMessage.append("Warning: LOGGING was not enabled");
                            }
                        } else if (commandArguments.size() == 1) { // we have to check the fileName LOG <fileName>; or LOG b;
                            String message = fsm.setFile(commandArguments);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                            if (message.contains("Warning: stopped LOGGING into the File ")) {
                                fsm.setLoggingEnabled(false);
                                fsm.setFileToBeLogged(null);
                            }
                        } else { //LOG A B;
                            systemMessage.append("\n").append("Warning: invalid command for LOG (One or no arguments are expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for LOG (One or no arguments are expected)");
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "PRINT" -> {
                        if (commandArguments.isEmpty()) {
                            String message = fsm.printAllToConsole();
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        } else if (commandArguments.size() == 1) { //Only one argument like a fileName or an invalid one like a
                            String message = fsm.printAllToFile(commandArguments);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }

                        } else {
                            systemMessage.append("\n").append("Warning: invalid command for PRINT (One or no arguments are expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for PRINT (One or no arguments are expected)");
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "COMPILE" -> {
                        if (commandArguments.isEmpty()) {
                            systemMessage.append("\n").append("Warning: invalid command for COMPILE (One argument is expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for COMPILE (One argument is expected)");
                                fsm.logIntoFile("\n");
                            }
                        } else if (commandArguments.size() == 1) {
                            String message = fsm.serializeFSM(commandArguments);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        } else {
                            systemMessage.append("\n").append("Warning: invalid command for COMPILE (Only one argument is expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning: invalid command for COMPILE (Only one argument is expected)");
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "EXECUTE" -> {
                        if (commandArguments.isEmpty()) {
                            systemMessage.append("\n").append("Warning invalid command for EXECUTE (Arguments are expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning invalid command for EXECUTE (Arguments are expected)");
                                fsm.logIntoFile("\n");
                            }
                        } else {
                            String compressedString = getCompressedStringForExecution(commandArguments);
                            String message = fsm.processSymbols(compressedString);
                            systemMessage.append("\n").append(message).append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile(message);
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    case "LOAD" -> {
                        if (commandArguments.isEmpty()) {
                            systemMessage.append("\n").append("Warning invalid command for LOAD (An argument is expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning invalid command for LOAD (An argument is expected)");
                                fsm.logIntoFile("\n");
                            }
                        } else if (commandArguments.size() == 1) {
                            String systemMessage1 = checkArgumentForLoadCommand(commandArguments);
                            systemMessage.append("\n").append(systemMessage1).append("\n");
                            if (loadCommandReadingEnabled) {
                                systemMessage.append("\n").append("Warning: already reading from a file, reading not allowed for new ones")
                                        .append("\n");
                            }
                            if (loadCommandLoadingEnabled) {
                                String message = fsm.deserializeFSM(loadFile);
                                loadFile = null;
                                loadCommandLoadingEnabled = false;
                                systemMessage.append("\n").append(message);
                                if(fsm.getLoggingEnabled()) {
                                    fsm.logIntoFile(message);
                                }
                            }

                        } else {
                            systemMessage.append("\n").append("Warning invalid command for LOAD (Only one argument is expected)")
                                    .append("\n");
                            if (fsm.getLoggingEnabled()) {
                                fsm.logIntoFile("\n");
                                fsm.logIntoFile("Warning invalid command for LOAD (Only one argument is expected)");
                                fsm.logIntoFile("\n");
                            }
                        }
                    }
                    default -> {
                        systemMessage.append("Warning: invalid command ").append(commandKeyword);
                        if (fsm.getLoggingEnabled()) {
                            fsm.logIntoFile("\n");
                            fsm.logIntoFile(("Warning: invalid command " + commandKeyword));
                            fsm.logIntoFile("\n");
                        }
                    }
                }
            }
        }
        loadCommandReadingEnabled = false;
        readFile = null;
        String systemMessageString = systemMessage.toString().replaceAll("\n+","\n");
        String[] helper = systemMessageString.split("\n");
        StringBuilder sb = new StringBuilder();
        for(String line : helper) {
            sb.append(line).append("\n").append("\n");
        }
        return sb.toString();
    }
}
