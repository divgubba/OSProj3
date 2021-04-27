import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.lang.Process;

public class Project3 {

    static ArrayList<String> arrJobs = new ArrayList<String>();
    static ArrayList<Integer> arrStartTime = new ArrayList<Integer>();
    static ArrayList<Integer> arrDuration = new ArrayList<Integer>();

    // static Scheduler process;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing input file name");
            System.exit(1);
        }
        // filename is set
        String fileInput = args[0];

        try {
            Scanner file = new Scanner(new File(fileInput));

            while (file.hasNext()) {
                String line = file.nextLine();
                String[] arr = line.split("\\t+");

                arrJobs.add(arr[0]);
                arrStartTime.add(Integer.parseInt(arr[1]));
                arrDuration.add(Integer.parseInt(arr[2]));
            }

            fcfs();

        } catch (FileNotFoundException e) {
            System.out.println("Error reading filename");
            System.exit(1);
        }

    }

    // process waits for other process to finish in fcfs
    public static void fcfs() {
        int finishTime = 0;
        int startTime = 0;
        for (int i = 0; i < arrJobs.size(); i++) {
            if (i == 0) {
                startTime = arrStartTime.get(i);
            }
            finishTime = startTime + arrDuration.get(i);
            printProcess(arrJobs.get(i), startTime, finishTime);
            startTime = finishTime;

        }
    }

    public static void spn() {

    }

    public static void hrrn() {

    }

    public static void printProcess(String jobString, int arrival, int finish) {
        // System.out.println("Print job: " + jobString + " arrival: " + arrival + "
        // finish: " + finish);
        System.out.print(jobString);
        String line = " ";
        String space = "   ";
        String xChar = " X ";
        for (int i = 0; i < arrival; i++) {
            line = line.concat(space);
        }
        for (int i = arrival; i < finish; i++) {
            line = line.concat(xChar);
        }
        System.out.println(line);
    }
}

/*
 * Your program should read in a list of jobs from a tab-delimited text file
 * named jobs.txt. The format of the text file should have one line for each
 * job, where each line has a job name, a start time, and a duration.
 * 
 * The job name must be a letter from A-Z. The first job should be named A, and
 * the remaining jobs should be named sequentially following the alphabet, so if
 * there are five jobs, they are named A-E. The arrival times of these jobs
 * should be in order.
 * 
 * The jobs should be scheduled first using the FCFS scheduler, then scheduled
 * again using the SPN scheduler, and once more using the HRRN scheduler.
 */

// arrival time