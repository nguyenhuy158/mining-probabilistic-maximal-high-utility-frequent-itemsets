# Mining probabilistic maximal high-utility frequent itemsets

## User Manual

### Using Java Object

Requirement: Java version 17

- Step 1: Compile the Java code. Run the command: `javac Main.java`

- Step 2: Run the program with parameters.
Run the command: `java Main`
```
 The options are:
  -f or --file <filename> 
     Required. Specify the input database filename 
  -ms or --minsup <value>  
     Specify the minimum support threshold. An integer is required.
  -mp or --minpro <value>
     Specify the minimum probability threshold. A double is required.
  -k or --top-k <value>   
     Specify the number of top patterns to return. An integer is required.
  -h or --help
     Display help information.
  -v or --version   
     Display version information.
```
Example: `java Main -f transactions.txt -ms 1000 -mp 0.6 -k 20`

- Step 3: The program will validate all the options. If any option is invalid or missing a required value, an error will be printed.

- Step 4: If all options are valid, the results will also be saved to a file named `result_filename_timestamp.txt` in the same directory as the java code.  


### 2. Using Java Eclipse Primitive

Requirement: Java version 17, Maven 3.2.3


- Step 1: Navigate to the project directory containing the `pom.xml` file

- Step 2: Build the project by running `mvn clean install`
     will compile the code

- Step 3: Run the application passing in the required arguments
    Run `mvn compile exec:java -Dexec.mainClass="mining.frequentitemsets.Main" -Dexec.args="-f file_name.txt -ms 1000 -mp 0.6 -k 100"` This will: Compile the code, Run the Main class, Pass in the database file name, min support, min prob, and top k as arguments
            
- Step 4: Check the console output or results file
    The top k patterns will be output to the console
    Results will also be saved to `result_file_name_timestamp.txt`

