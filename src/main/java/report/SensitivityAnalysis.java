package report;

import model.Scenario;
import model.ScenarioLoader;
import optimization.FastHillClimbing;
import optimization.GeneticAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensitivityAnalysis {

    public static void main(String[] args) {
        analyseGA_PS("wsn_100");
    }

    public static void analyseFHC_Q(String scenarioName) {
        try {
            File directory = new File("report/");
            if(!directory.exists()) directory.mkdir();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDateTime now = LocalDateTime.now();
            File reportFile = new File("report/report_fhc_" + dtf.format(now) + "_" + scenarioName + ".csv");
            if(reportFile.exists()) {
                reportFile.delete();
            }
            reportFile.createNewFile();
            BufferedWriter wrt = new BufferedWriter(new FileWriter(reportFile));

            //start running
            Scenario s = ScenarioLoader.loadFromFile("data/" + scenarioName + "/");

            //write headers
            wrt.write("Q,Fitness,Runtime\n");

            for (int q = 4; q < 8; q++) {
                System.out.println("Running with Q="+q);
                FastHillClimbing fhc = new FastHillClimbing(s,q);
                Report r = fhc.solve();
                wrt.write(q+","+r.getFitness()+","+r.getTime());
                wrt.newLine();
            }
            wrt.flush();
            wrt.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyseGA_PS(String scenarioName) {
        try {
            File directory = new File("report/");
            if(!directory.exists()) directory.mkdir();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDateTime now = LocalDateTime.now();
            File reportFile = new File("report/report_ga_ps_" + dtf.format(now) + "_" + scenarioName + ".csv");
            if(reportFile.exists()) {
                reportFile.delete();
            }
            reportFile.createNewFile();
            BufferedWriter wrt = new BufferedWriter(new FileWriter(reportFile));

            //start running
            Scenario s = ScenarioLoader.loadFromFile("data/" + scenarioName + "/");

            //write headers
            wrt.write("PS,Fitness,Runtime\n");

            for (int ps = 50; ps < 300; ps+=50) {
                System.out.println("Running with PS="+ps);
                GeneticAlgorithm ga = new GeneticAlgorithm(s,ps,0.05,0.1,10);
                Report r = ga.solve();
                wrt.write(ps+","+r.getFitness()+","+r.getTime());
                wrt.newLine();
            }
            wrt.flush();
            wrt.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyseGA_MR(String scenarioName) {
        try {
            File directory = new File("report/");
            if(!directory.exists()) directory.mkdir();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDateTime now = LocalDateTime.now();
            File reportFile = new File("report/report_ga_mr_" + dtf.format(now) + "_" + scenarioName + ".csv");
            if(reportFile.exists()) {
                reportFile.delete();
            }
            reportFile.createNewFile();
            BufferedWriter wrt = new BufferedWriter(new FileWriter(reportFile));

            //start running
            Scenario s = ScenarioLoader.loadFromFile("data/" + scenarioName + "/");

            //write headers
            wrt.write("MR,Fitness,Runtime\n");

            for (double mr = 0.02; mr <= 0.1; mr+=0.02) {
                System.out.println("Running with MR="+mr);
                GeneticAlgorithm ga = new GeneticAlgorithm(s,100,mr,0.1,10);
                Report r = ga.solve();
                wrt.write(mr+","+r.getFitness()+","+r.getTime());
                wrt.newLine();
            }
            wrt.flush();
            wrt.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyseGA_CR(String scenarioName) {
        try {
            File directory = new File("report/");
            if(!directory.exists()) directory.mkdir();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDateTime now = LocalDateTime.now();
            File reportFile = new File("report/report_ga_cr_" + dtf.format(now) + "_" + scenarioName + ".csv");
            if(reportFile.exists()) {
                reportFile.delete();
            }
            reportFile.createNewFile();
            BufferedWriter wrt = new BufferedWriter(new FileWriter(reportFile));

            //start running
            Scenario s = ScenarioLoader.loadFromFile("data/" + scenarioName + "/");

            //write headers
            wrt.write("CR,Fitness,Runtime\n");

            for (double cr = 0.1; cr <= 0.5; cr+=0.1) {
                System.out.println("Running with CR="+cr);
                GeneticAlgorithm ga = new GeneticAlgorithm(s,100,0.05,cr,10);
                Report r = ga.solve();
                wrt.write(cr+","+r.getFitness()+","+r.getTime());
                wrt.newLine();
            }
            wrt.flush();
            wrt.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyseGA_Gen(String scenarioName) {
        try {
            File directory = new File("report/");
            if(!directory.exists()) directory.mkdir();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDateTime now = LocalDateTime.now();
            File reportFile = new File("report/report_ga_gen_" + dtf.format(now) + "_" + scenarioName + ".csv");
            if(reportFile.exists()) {
                reportFile.delete();
            }
            reportFile.createNewFile();
            BufferedWriter wrt = new BufferedWriter(new FileWriter(reportFile));

            //start running
            Scenario s = ScenarioLoader.loadFromFile("data/" + scenarioName + "/");

            //write headers
            wrt.write("Gen,Fitness,Runtime\n");

            for (int gen = 1; gen <= 20; gen++) {
                System.out.println("Running with Gen="+gen);
                GeneticAlgorithm ga = new GeneticAlgorithm(s,100,0.05,0.1,gen);
                Report r = ga.solve();
                wrt.write(gen+","+r.getFitness()+","+r.getTime());
                wrt.newLine();
            }
            wrt.flush();
            wrt.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
