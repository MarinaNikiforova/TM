
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 *
 * @author Marina
 */
public class TM {

    private static String logFile = "timelog.txt";

    public static void main(String[] args) {
        
        // there must be a command
        if (args.length < 1) {
            System.exit(0);
        }
        
        // initialize the log file
        Log log = new Log(logFile);
        
        // get comamand
        String command = args[0];
        if (command.equals("summary") && args.length == 1) {
            log.summary();
        } else if (args.length > 1) {
            
            // get task
            String task = args[1];
            if (command.equals("start")) {
                log.start(task);
            } else if (command.equals("stop")) {
                log.stop(task);
            } else if (command.equals("summary")) {
                log.summary(task);
            } else if (command.equals("size") && args.length == 3) {
                log.size(task, args[2]);
            } else if (command.equals("describe") 
                    && args.length > 2) {
                
                // get description
                String description = "";
                for (int i = 2; i < args.length; i++) {
                    description += args[i] + " ";
                }
                log.describe(task, description.trim());
            }
        }
    }
}

class Log {

    ArrayList<Record> records;
    String logFile;
    
    /**
     * constructor 
     * @param logFile 
     */
    public Log(String logFile) {
        records = new ArrayList<>();
        this.logFile = logFile;
        readFile();
    }

    
    public void size(String task, String size_){
        for (Record record : records) {
            if (record.name.equals(task)) {
                if (record.setSize(size_) == false) {
                    System.out.println("The entered size " + size_ + " is invalid");
                    System.out.println("It's should be one of these: " +
                            Arrays.toString(Record.sizes));
                } 
                writeFile();
                return;
            }
        }
    }
    
    /*
     * Provide a summary of
     * ALL tasks and total time spent working on ALL tasks
     */
    public void summary() {
        double runTime = 0;
        for (Record record : records) {
            record.summary();
            runTime += record.getRunTime();
        }
        System.out.println("Total running time: " + runTime + " seconds");
    }

    /**
     * Provides a report of the activity and total time spent working 
     * on certain task
     */
    public void summary(String task) {
        for (Record record : records) {
            if (record.name.equals(task)) {
                record.summary();
                return;
            }
        }
        System.out.println(task + " hasn't run");
    }

    /**
     * Logs the description of the task with name 
     * of the task and its description
     */
    public void describe(String task, String description) {
        for (Record record : records) {
            if (record.name.equals(task)) {
                record.description = description;
                break;
            }
        }
        writeFile();
    }

    /*
     * 	Logs the stop time of a task with name of the task
     */
    public boolean stop(String task) {
        for (Record record : records) {
            if (record.name.equals(task)
                    && record.stop.equals(Record.EMPTY)) {
                record.stop = LocalTime.now().toString();
                writeFile();
                return true;
            }
        }
        System.out.println("Error: " + task + " isn't running");
        return false;
    }

    /**
     * Logs the start time of a task with the name of the task
     */
    public boolean start(String task) {
        for (Record record : records) {
            if (record.name.equals(task)) {
                System.out.println("Error: " + task + " has been in the log");
                return false;
            }
        }
        Record record = new Record(task);
        records.add(record);
        writeFile();
        return true;
    }

    /*
     * Writes updated data to file
     */
    private void writeFile() {
        try {
            BufferedWriter writer
                    = new BufferedWriter(new FileWriter(logFile));

            for (Record record : records) {
                writer.write(record.toString());
            }

            writer.close();
        } catch (IOException ex) {

        }
    }

    /**
     * Reads data from file
     */
    private void readFile() {
        try {
            Scanner input = new Scanner(new File(logFile));

            while (input.hasNext()) {
                Record record = new Record();
                if (record.read(input)) {
                    records.add(record);
                } else {
                    break;
                }
            }
            input.close();
        } catch (FileNotFoundException ex) {
        }
    }

}

class Record {

    String name;
    String start;
    String stop;
    String description;
    // this value has to be checked --> set its mode private
    private String size;

    static final String EMPTY = "-";
    // this static array let us check for the correctness of the input size !!!!
    static final String [] sizes = {"XS", "S", "M", "L", "XL"};

    /**
     * default constructor 
     */
    public Record() {
        this.name = EMPTY;
        this.start = EMPTY;
        this.stop = EMPTY;
        this.description = EMPTY;
        this.size = EMPTY;
    }

    /**
     * constructor
     */
    public Record(String name) {
        this.name = name;
        this.start = LocalTime.now().toString();
        this.stop = EMPTY;
        this.description = EMPTY;
        size = EMPTY;
    }

    /**
     * string to be written to log file
     */
    @Override
    public String toString() {
        return name + System.lineSeparator()
                + start + System.lineSeparator()
                + stop + System.lineSeparator()
                + description + System.lineSeparator()
                + size + System.lineSeparator();
    }

    /**
     * Reads data from inputing stream input
     */
    public boolean read(Scanner input) {
        try {
            name = input.nextLine();
            start = input.nextLine();
            stop = input.nextLine();
            description = input.nextLine();
            size = input.nextLine();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * print summary of this record 
     */
    public void summary() {
        System.out.println("Task: " + name);
        System.out.println("\tStart time: " + start);
        if (stop.equals(EMPTY)) {
            System.out.println("\tStop time: " + "Still running");
        } else {
            System.out.println("\tStop time: " + stop);
            System.out.println("\tRunning time: " + getRunTime() + " seconds");
        }
        System.out.println("\tDescription: " + description);
        System.out.println("\tSize: " + size);
    }

    public boolean setSize(String size) {
        for (String sz : sizes) {
            
            // the size has to match a sample size
            if (sz.equals(size)) { 
                this.size = size;
                return true;
            }
        }        
        // it doesn't match
        return false;
    }

    
    
    /**
     * get run time
     * @return 
     */
    public double getRunTime() {
        if (stop.equals(EMPTY)) {
            // the task is still running 
            return Double.MAX_VALUE;
        }
        
        // turn string to local time 
        LocalTime st = LocalTime.parse(start);
        LocalTime sp = LocalTime.parse(stop);
        int tmp = 0;
        double second, minute, hour;
        
        // get the right run time        
        if (sp.getSecond() < st.getSecond()) {
            second = sp.getSecond() + 60 - st.getSecond();
            tmp = 1;
        } else {
            second = sp.getSecond() - st.getSecond();
        }
        
        sp.minusMinutes(tmp);
        if (sp.getMinute() < st.getMinute()) {
            minute = sp.getMinute() + 60 - st.getMinute();
            tmp = 1;
        } else {
            minute = sp.getMinute() - st.getMinute();
            tmp = 0;
        }
        
        sp.minusHours(tmp);
        hour = sp.getHour() - st.getHour();
        
        return second + 60 * (minute + 60 * hour);
    }
}
