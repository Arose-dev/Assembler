import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Assembler {
    // DESCRIPTION: Initializes and runs the first and second pass of the assembler
    // PRECONDITION: An .asm file is present and is readable
    // POSTCONDITION: Generates a corresponding .hack file with machine code
    public static void main(String[] args) {
        String inputFileName = "Rect.asm";
        String outputFileName = inputFileName.replace(".asm", ".hack");
        PrintWriter outputFile = null;
        SymbolTable symbolTable;

        try {
            outputFile = new PrintWriter(new FileOutputStream(outputFileName));
        } catch (FileNotFoundException ex) {
            System.err.println("Could not open output file " + outputFileName);
            System.exit(0);
        }

        symbolTable = new SymbolTable();

        firstPass(inputFileName, symbolTable);

        secondPass(inputFileName, symbolTable, outputFile);

        outputFile.close();
    }

    //DESCRIPTION: converts integer from decimal notation to binary notation
    //PRECONDITION: number is valid size for architecture, non-negative
    //POSTCONDITION: returns 16-bit string of binary digits (first char is MSB)
    private static String decimalToBinary(int decimal)
    {
        StringBuilder binaryString = new StringBuilder();
        binaryString.append(Integer.toBinaryString(decimal));

        for(int i = binaryString.length(); i < 16; i++)
        {
            binaryString.insert(0, "0");
        }

        return binaryString.toString();
    }


    // DESCRIPTION: Parses the input file to locate label symbols (L commands) and stores them in the symbol table with their line addresses
    // PRECONDITION: The input file exists and is readable; the symbol table is empty or prepared for new entries
    // POSTCONDITION: All label symbols are recorded in the symbol table with their addresses
    private static void firstPass(String inputFileName, SymbolTable symbolTable) {
        Parser parse = new Parser(inputFileName);
        String symbol = "";
        int address;

        while(parse.hasMoreCommands())
        {
            parse.advance();
            address = parse.getLineNumber();

            if(parse.getCommandType() == 'L')
            {
                symbol = parse.getSymbol();
                symbolTable.addEntry(symbol, address);
                System.out.println(address  + ": ADDRESS OF L");
            }
        }
    }


    // DESCRIPTION: Parses the input file to translate A and C commands into binary code, handling symbols as needed
    // PRECONDITION: The input file exists and is readable; the symbol table contains labels from the first pass
    // POSTCONDITION: The binary code for each command is written to the output file
    private static void secondPass(String inputFileName, SymbolTable symbolTable, PrintWriter outputFile) {
        Parser parse = new Parser(inputFileName);
        CInstructionMapper codeTable = new CInstructionMapper();
        String output ="";
        int address = 16;

        while(parse.hasMoreCommands()){
            parse.advance();

            if(parse.getCommandType() == Parser.C_COMMAND){
                if(codeTable.comp(parse.getComp()) == null || codeTable.dest(parse.getDest()) == null || codeTable.jump(parse.getJump()) == null){
                    System.out.println("FINAL DEST: " + codeTable.comp(parse.getComp()));
                    System.out.println("FINAL COMP: " + codeTable.dest(parse.getDest()));
                    System.out.println("FINAL JUMP: " + codeTable.jump(parse.getJump()));
                    System.exit(0);
                }
                else{
                    System.out.println("JUMP Code: " + parse.getJump());
                    output = "111" + codeTable.comp(parse.getComp()) + codeTable.dest(parse.getDest()) + codeTable.jump(parse.getJump());
                    System.out.println(output);
                    outputFile.println(output);
                }
            }
            else if(parse.getCommandType() == Parser.A_COMMAND) {

                int num = 0;
                String symbol = parse.getSymbol();

                if (isNumeric(symbol)) {
                    try {
                        num = Integer.parseInt(symbol);
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }

                    String outputCode = decimalToBinary(num);
                    outputFile.println(outputCode);
                } else {
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, address);
                        address++;
                    }

                    num = symbolTable.getAddress(symbol);
                    String outputCode = decimalToBinary(num);
                    outputFile.println(outputCode);
                }
            }
        }
    }


    // DESCRIPTION: Determines if the first character of a string is a numeric digit
    // PRECONDITION: The string is non-empty
    // POSTCONDITION: Returns true if the first character is numeric; otherwise, false
    //**********************************
    private static boolean isNumeric(String str)
    {
        String numbers = "0123456789";
        return numbers.indexOf(str.charAt(0)) != -1;
    }



}
