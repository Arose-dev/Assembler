import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Parser {

    // COMMANDS
    public static final char NO_COMMAND = 'N';
    public static final char A_COMMAND = 'A';
    public static final char C_COMMAND = 'C';
    public static final char L_COMMAND = 'L';

    //PRIVATE VARIABLES
    private Scanner input;

    private int lineNumber;
    private String rawLine;
    private String cleanLine;

    private char commandType;
    private String symbol;
    private String destMnemonic;
    private String compMnemonic;
    private String jumpMnemonic;


    //DESCRIPTION: opens input file/stream and prepares to parse
    //PRECONDITION: provided file is ASM file
    //POSTCONDITION: if file can’t be opened, ends program w/ error message
    public Parser(String inFileName) {
        System.out.println("FILENAME: " + inFileName);
        try {
            input = new Scanner(new FileInputStream(inFileName));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR WITH GETTING FILE: " + e);
            System.exit(0);
        }
        lineNumber = 0;
    }


    //DESCRIPTION: returns boolean if more commands left, closes stream if not
    //PRECONDITION: file stream is open
    //POSTCONDITION: returns true if more commands, else closes stream
    public boolean hasMoreCommands(){
        if(input.hasNextLine()){
            return true;
        }
        else{
            input.close();
            return false;
        }
    }


    //DESCRIPTION: reads next line from file and parses it into instance vars
    //PRECONDITION: file stream is open, called only if hasMoreCommands()
    //POSTCONDITION: current instruction parts put into instance vars
    public void advance(){
        if(hasMoreCommands())
        {
            this.rawLine = input.nextLine();
            cleanLine();
            parseCommandType();
            parse();
        }

        if(commandType != NO_COMMAND && commandType != L_COMMAND  )
        {
            lineNumber++;
        }
    }

    //ALL PRIVITE METHODS (PARSER HELPERS)


    //DESCRIPTION: cleans raw instruction by removing non-essential parts
    //PRECONDITION: String parameter given (not null)
    //POSTCONDITION: returned without comments and whitespace
    private void cleanLine(){
        this.cleanLine = getRawLine().replaceAll(" ","");
        this.cleanLine = getCleanLine().replaceAll("\t", "");
        int note = getCleanLine().indexOf("//");

        if(note != -1)
        {
            this.cleanLine = getCleanLine().substring(0, note);
        }
    }


    //DESCRIPTION: determines command type from parameter
    //PRECONDITION: String parameter is clean instruction
    //POSTCONDITION: returns A_COMMAND (A-instruction),
    // C_COMMAND (C-instruction), L_COMMAND (Label) or
    // NO_COMMAND (no command)
    private void parseCommandType(){
        if(this.cleanLine == null || getCleanLine().isEmpty())
        {
            this.commandType = NO_COMMAND;
        }
        else if(getCleanLine().contains("@"))
        {
            this.commandType = A_COMMAND;
        }
        else if(getCleanLine().contains("(") || getCleanLine().contains(")"))
        {
            this.commandType = L_COMMAND;
        }
        else
        {
            this.commandType = C_COMMAND;
        }
    }


    //DESCRIPTION: helper method parses line depending on instruction type
    //PRECONDITION: advance() called so cleanLine has value
    //POSTCONDITION: appropriate parts (instance vars) of instruction filled
    private void parse(){
        if(this.commandType == NO_COMMAND){

        }
        else if(this.commandType == A_COMMAND || this.commandType == L_COMMAND){
            this.parseSymbol();
        }
        else if(this.commandType == C_COMMAND){
            this.parseDest();
            this.parseComp();
            this.parseJump();
        }
    }



    //DESCRIPTION: parses symbol for A- or L-commands
    //PRECONDITION: advance() called so cleanLine has value,
    // call for A- and L-commands only
    //POSTCONDITION: symbol has appropriate value from instruction assigned
    private void parseSymbol(){
        if(this.commandType==A_COMMAND){
            this.symbol = getCleanLine().substring(1);
        }
        else if(this.commandType == L_COMMAND){
            this.symbol = getCleanLine().substring(1, cleanLine.length() - 1);
            System.out.println(this.symbol + ": THE STYMBOL FOR L");
        }
    }



    //DESCRIPTION: helper method parses line to get dest part
    //PRECONDITION: advance() called so cleanLine has value,
    // call for C-instructions only
    //POSTCONDITION: destMnemonic set to appropriate value from instruction
    private void parseDest(){
        if(this.getCleanLine().contains("="))
        {
            this.destMnemonic = getCleanLine().substring(0, getCleanLine().indexOf('='));
        }
        else
        {
            this.destMnemonic = "NULL";
        }
        System.out.println("DEST: " + destMnemonic);
    }



    //DESCRIPTION: helper method parses line to get comp part
    //PRECONDITION: advance() called so cleanLine has value,
    // call for C-instructions only
    //POSTCONDITION: compMnemonic set to appropriate value from instruction
    private void parseComp() {
        String line = getCleanLine();

        if(this.commandType == C_COMMAND)
        {
            if(line.contains("="))
            {
                this.compMnemonic = line.substring((line.indexOf('=')) + 1);
            }
            else if(line.contains(";"))
            {
                this.compMnemonic = line.substring(0, line.indexOf(';'));
            }
        }
        System.out.println("COMP: " + compMnemonic);
    }


    //DESCRIPTION: helper method parses line to get jump part
    //PRECONDITION: advance() called so cleanLine has value,
    // call for C-instructions only
    //POSTCONDITION: jumpMnemonic set to appropriate value from instruction
    private void parseJump(){
        if(this.commandType == C_COMMAND)
        {
            if(this.getCleanLine().contains(";"))
            {
                System.out.println("There is a semicolon");
                this.jumpMnemonic = getCleanLine().substring((getCleanLine().indexOf(";")) + 1);
            }
            else
            {
                this.jumpMnemonic = "NULL";
            }
        }
        System.out.println("JUMP: " + jumpMnemonic);
    }



    //GETTER METHODS
    //DESCRIPTION: getter for command type
    //PRECONDITION: cleanLine has been parsed (advance was called)
    //POSTCONDITION: returns Command for type (N/A/C/L)
    public char getCommandType(){
        return commandType;
    }

    //DESCRIPTION: getter for symbol name
    // PRECONDITION: cleanLine has been parsed (advance was called),
    // call for labels only (use getCommandType())
    //POSTCONDITION: returns string for symbol name
    public String getSymbol(){
        return symbol;
    }

    //DESCRIPTION: getter for dest part of C-instruction
    //PRECONDITION: cleanLine has been parsed (advance was called),
    // call for C-instructions only (use getCommandType())
    //POSTCONDITION: returns mnemonic (ASM symbol) for dest part
    public String getDest(){
        return destMnemonic;
    }

    //DESCRIPTION: getter for comp part of C-instruction
    //PRECONDITION: cleanLine has been parsed (advance was called),
    // call for C-instructions only (use getCommandType())
    //POSTCONDITION: returns mnemonic (ASM symbol) for comp part
    public String getComp(){
        return compMnemonic;
    }

    //DESCRIPTION: getter for jump part of C-instruction
    //PRECONDITION: cleanLine has been parsed (advance was called),
    // call for C-instructions only (use getCommandType())
    //POSTCONDITION: returns mnemonic (ASM symbol) for jump part
    public String getJump(){
        return jumpMnemonic;
    }

    //DESCRIPTION: getter for rawLine from file (debugging)
    //PRECONDITION: advance() was called to put value from file in here
    //POSTCONDITION: returns string of current original line from file
    public String getRawLine(){
        return rawLine;
    }

    //DESCRIPTION: getter for cleanLine from file (debugging)
    //PRECONDITION: advance() and cleanLine() were called
    //POSTCONDITION: returns string of current clean instruction from file
    public String getCleanLine(){
        return cleanLine;
    }

    //DESCRIPTION: getter for lineNumber (debugging)
    //PRECONDITION: n/a
    //POSTCONDITION: returns line number currently being processed from file
    public int getLineNumber(){
        return lineNumber;
    }




}
