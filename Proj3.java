import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Process;
import java.util.Collections;
import java.util.Map;

public class Proj3 {

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
            spn();
            hrrn();

        } catch (FileNotFoundException e) {
            System.out.println("Error reading filename");
            System.exit(1);
        }

    }

    // process waits for other process to finish in fcfs
    public static void fcfs() {

        int finishTime = 0;
        int startTime = 0;
        System.out.println("\nFCFS :\n");
        // loop through jobs
        for (int i = 0; i < arrJobs.size(); i++) {
            // set start time of first job
            if (i == 0) {
                startTime = arrStartTime.get(i);
            }
            // set finish time to duration plus start time
            finishTime = startTime + arrDuration.get(i);
            printProcess(arrJobs.get(i), startTime, finishTime);
            // need to wait til job has finished to print next process
            // set start time for new process as finish time of previous process
            startTime = finishTime;

        }
    }

    public static void spn() {
        HashMap<String, Integer> hmDuration = new HashMap<String, Integer>(); // Hash map for storing duration in form
                                                                              // of {Job : duration}
        HashMap<String, Integer> hmStartTime = new HashMap<String, Integer>(); // Hash map for storing start time in
                                                                               // form of {Job : start time}
        HashMap<String, Integer> hmPickedJobs = new HashMap<String, Integer>(); // Hash map for storing duration in form
                                                                                // of {Job picked : duration}

        int startTime = 0;
        int finishTime = 0;

        int indexJob = 0;
        boolean jobsSelected = false;
        String minJob = null;

        // store job, duration in hashmap for easy retrieval at the end
        for (int i = 0; i < arrJobs.size(); i++) {
            hmDuration.put(arrJobs.get(i), arrDuration.get(i));
        }

        // Iterate through jobs
        while (arrJobs.size() > indexJob) {
            // while jobs are selected go into loop else set jobsSelected to false
            while (jobsSelected) {
                // get minimum duration job if start time of previous job < next job star time
                if (startTime <= arrStartTime.get(indexJob)) {
                    // get smallest job duration and assign to string
                    int minValueInMap = (Collections.min(hmPickedJobs.values()));
                    for (Map.Entry<String, Integer> entry : hmPickedJobs.entrySet()) { // Itrate through hashmap
                        if (entry.getValue() == minValueInMap) {
                            minJob = entry.getKey();
                        }
                    }
                    // put job and start time of min job in the hashmap
                    hmStartTime.put(minJob, startTime);
                    // set finish time to start time + picked job's duration value
                    finishTime = startTime + hmPickedJobs.get(minJob);
                    // set start time of now new job to finish time of last job
                    startTime = finishTime;
                    // remove the min duration job from picked jobs
                    hmPickedJobs.remove(minJob);
                } else {
                    jobsSelected = false;
                }
            }
            // if start time > = arrival time add the job
            if (startTime >= arrStartTime.get(indexJob)) {
                hmPickedJobs.put(arrJobs.get(indexJob), arrDuration.get(indexJob));
                jobsSelected = true;
                indexJob++;
            }
        }

        while (hmPickedJobs.size() > 0) {
            // get smallest job duration and assign to string
            int minValueInMap = (Collections.min(hmPickedJobs.values()));
            for (Map.Entry<String, Integer> entry : hmPickedJobs.entrySet()) {
                if (entry.getValue() == minValueInMap) {
                    minJob = entry.getKey();
                }
            }
            // put job and start time of min job in the hashmap
            hmStartTime.put(minJob, startTime);
            // set finish time to start time + picked job's duration value
            finishTime = startTime + hmPickedJobs.get(minJob);
            // set start time of now new job to finish time of last job
            startTime = finishTime;
            // remove the min duration job from picked jobs
            hmPickedJobs.remove(minJob);
        }
        // Print out SPN
        System.out.println("\nSPN :\n");
        for (int i = 0; i < arrJobs.size(); i++) {
            printProcess(arrJobs.get(i), hmStartTime.get(arrJobs.get(i)),
                    hmStartTime.get(arrJobs.get(i)) + hmDuration.get(arrJobs.get(i)));
        }

    }

    public static void hrrn() {
        HashMap<String, Integer> hmDuration = new HashMap<String, Integer>();
        HashMap<String, Integer> hmStartTime = new HashMap<String, Integer>(); // Hash map for storing start time in
        // form of {Job : start time}
        HashMap<String, Integer> hmPickedJobs = new HashMap<String, Integer>(); // Hash map for storing duration in form
        // of {Job picked : duration}
        HashMap<String, Double> hmResponseRatio = new HashMap<String, Double>(); // Hash map to store job , response
                                                                                 // ratio
        HashMap<String, Integer> hmWaitTimes = new HashMap<String, Integer>(); // Hash map to store job, wait time

        int startTime = 0;
        int finishTime = 0;
        String job = "";
        int startForJob = 0;

        int indexJob = 0;
        boolean jobsSelected = false;

        // store job, start time in hashmap and job, duration time for easy retrieval at
        // the end
        for (int i = 0; i < arrJobs.size(); i++) {
            hmDuration.put(arrJobs.get(i), arrDuration.get(i));
            hmStartTime.put(arrJobs.get(i), arrStartTime.get(i));
        }

        String maxRatioJob = null;

        // Iterate through jobs
        while (arrJobs.size() > indexJob) {
            // while jobs are selected go into loop else set jobsSelected to false
            while (jobsSelected) {
                if (startTime <= arrStartTime.get(indexJob)) {
                    // From picked jobs, calculate wait time to put in wait time hashmap with job
                    for (Map.Entry<String, Integer> entry : hmPickedJobs.entrySet()) {
                        job = entry.getKey();
                        startForJob = hmStartTime.get(job);
                        hmWaitTimes.put(job, startTime - startForJob);
                    }

                    // loop through picked jobs and calculate then insert ratio into response ratio
                    // map
                    for (Map.Entry<String, Integer> jobEntry : hmPickedJobs.entrySet()) {
                        String jobForRR = jobEntry.getKey();
                        int wait = hmWaitTimes.get(jobForRR);
                        int duration = hmPickedJobs.get(jobForRR);
                        // calculate response ratio
                        double respRatio = (double) (wait + duration) / duration;
                        // add job and response ratio to map
                        hmResponseRatio.put(jobForRR, respRatio);
                    }

                    // get max job response ratio value and assign to string to find job key
                    Double maxValueInMap = (Collections.max(hmResponseRatio.values()));
                    for (Map.Entry<String, Double> entryMax : hmResponseRatio.entrySet()) {
                        // maxRatioJob holds max job response ratio key
                        if ((entryMax.getValue()) == maxValueInMap) {
                            maxRatioJob = entryMax.getKey();
                        }
                    }

                    // remove max ratio response job from hashmaps
                    hmWaitTimes.remove(maxRatioJob);
                    hmResponseRatio.remove(maxRatioJob);
                    hmStartTime.put(maxRatioJob, startTime);

                    // set finish time to start time plus duration of picked jobs ( stored in
                    // hmPicked Jobs )
                    finishTime = startTime + hmPickedJobs.get(maxRatioJob);

                    // set start time of now new job to finish time of last job
                    startTime = finishTime;
                    hmPickedJobs.remove(maxRatioJob);

                } else {
                    jobsSelected = false;
                }
            }
            // if start time > = arrival time add the job
            if (startTime >= arrStartTime.get(indexJob)) {
                hmPickedJobs.put(arrJobs.get(indexJob), arrDuration.get(indexJob));
                jobsSelected = true;
                indexJob++;
            }
        }

        while (hmPickedJobs.size() > 0) {
            for (Map.Entry<String, Integer> entry : hmPickedJobs.entrySet()) {
                job = entry.getKey();
                startForJob = hmStartTime.get(job);
                hmWaitTimes.put(job, startTime - startForJob);
            }
            for (Map.Entry<String, Integer> jobEntry2 : hmPickedJobs.entrySet()) {
                String jobForRR = jobEntry2.getKey();
                int wait = hmWaitTimes.get(jobForRR);
                int duration = hmPickedJobs.get(jobForRR);
                double respRatio = (double) (wait + duration) / duration;
                hmResponseRatio.put(jobForRR, respRatio);
            }
            Double maxValueInMap = (Collections.max(hmResponseRatio.values()));
            for (Map.Entry<String, Double> entryMax : hmResponseRatio.entrySet()) {
                if ((entryMax.getValue()) == maxValueInMap) {
                    maxRatioJob = entryMax.getKey();
                }
            }

            hmWaitTimes.remove(maxRatioJob);
            hmResponseRatio.remove(maxRatioJob);
            hmStartTime.put(maxRatioJob, startTime);
            finishTime = startTime + hmPickedJobs.get(maxRatioJob);
            // set start time of now new job to finish time of last job
            startTime = finishTime;
            hmPickedJobs.remove(maxRatioJob);

        }
        // Print out HRRN
        System.out.println("\nHRRN :\n");
        for (int i = 0; i < arrJobs.size(); i++) {
            printProcess(arrJobs.get(i), hmStartTime.get(arrJobs.get(i)),
                    hmStartTime.get(arrJobs.get(i)) + hmDuration.get(arrJobs.get(i)));
        }
    }

    // Print Process function
    public static void printProcess(String jobString, int arrival, int finish) {

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
