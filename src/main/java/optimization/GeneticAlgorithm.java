package optimization;

import algorithm.Fitness;
import graph.Graph;
import model.Scenario;
import model.SinkCandidate;
import model.SinkConfiguration;
import model.SinkNode;
import report.Report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {

    // dna length is equal to number of sink candidates
    // each genome in dna can be 0 to nOption (0 means not selected) (1~nOption selects the chosen sink type)
    static class GAState {
        public int[] dna;

        public GAState(int dna_size) {
            this.dna = new int[dna_size];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("| ");
            for(int value : dna) {
                sb.append(value);
                sb.append(" | ");
            }
            return sb.toString();
        }
    }

    static class GAScore {
        public GAState state;
        public double score;

        public GAScore(GAState state,double score){
            this.state = state;
            this.score = score;
        }

        public double getScore() { return score; }
    }

    Scenario scenario;
    Graph root;
    int nSink;
    int nOption;

    private final int population_size;
    private final double mutation_rate;
    private final double crossover_rate;
    private final int generations;

    private final int maxCost;

    private Fitness fitnessUtil;

    public GeneticAlgorithm(Scenario scenario,int population_size,double mutation_rate,double crossover_rate,int generations) {
        this.scenario = scenario;
        root = scenario.getRootGraph();
        nSink = scenario.getSinkCandidates().size();
        nOption = scenario.getSinkTypes().size();

        this.population_size = population_size;
        this.mutation_rate = mutation_rate;
        this.crossover_rate = crossover_rate;

        this.generations = generations;

        maxCost = scenario.getSinkCandidates().size() *
                  scenario.getSinkTypes().stream().max(Comparator.comparingInt(SinkConfiguration::getCost)).get().getCost();

        fitnessUtil = new Fitness(root, maxCost);
    }

    public Report solve() {
        long startTime = System.currentTimeMillis();

        // initialization
        ArrayList<GAState> population = initialPopulation();

        // advance generations
        for (int i = 0; i < generations; i++) {

            System.out.println("[GA] running generation "+ i + "...");

            // calc each specie's fitness
            double sumFit = 0;
            double bestFit = Double.MAX_VALUE;
            double worstFit = Double.MIN_VALUE;
            ArrayList<GAScore> scores = new ArrayList<>();
            for (GAState s: population) {
                double fitness = fitness(s);
                // use (1 - fitness) so worst case would have 0 score
                scores.add(new GAScore(s, 1.0 - fitness));
                sumFit += 1.0 - fitness;
                if(fitness > worstFit) worstFit = fitness;
                if(fitness < bestFit) bestFit = fitness;
            }
            double avgFit = sumFit / population.size();
            System.out.println("[GA] fitness best: " + bestFit + " avg: " + avgFit + " worst: " + worstFit);

            // shift scores so they all in positive range
            // in new fitness algorithm we dont have negative fitness
//            if(worstFit < 0) {
//                for(GAScore gs: scores) {
//                    gs.score += Math.abs(worstFit);
//                }
//                sumFit += Math.abs(worstFit)*scores.size();
//            }
//            System.out.println("Population Size: " + population.size() + " Fitness Sum: " + sumFit);
//            double test_s = 0.0;
//            for(GAScore s: scores) {
//                System.out.println(s.score / sumFit);
//                test_s += s.score / sumFit;
//            }
//            System.out.println("Total : " + test_s);

            // calculate cumulative probability for RWS
            double[] probability = new double[population.size()];
            double probability_offset = 0.0;
            for (int j = 0; j < scores.size(); j++) {
                double p = scores.get(j).score / sumFit;
                probability[j] = probability_offset + p;
                probability_offset += p;
            }
//            System.out.println(Arrays.toString(probability));

            // perform RWS for natural selection
            ArrayList<GAState> selectedPopulation = new ArrayList<>();
            Random rnd = new Random();
            while (selectedPopulation.size() < population_size) {
                double r = rnd.nextDouble();
                for (int j = 0; j < population_size; j++) {
                    if (r < probability[j]) {
                        //select jth element
                        selectedPopulation.add(scores.get(j).state);
                        break;
                    }
                }
            }
//          ArrayList<GAState> selectedPopulation = new ArrayList<>();
//          scores.stream().sorted(Comparator.comparingInt(GAScore::getScore)).

            // parents which go to next generation
            ArrayList<GAState> nextPopulation = new ArrayList<>(selectedPopulation);

            // select for crossover
            ArrayList<GAState> selectedForCrossover = new ArrayList<>();
            for (GAState gaState : selectedPopulation) {
                if (rnd.nextDouble() < crossover_rate) {
                    selectedForCrossover.add(gaState);
                }
            }
            System.out.println("[GA] " + selectedForCrossover.size() + " individuals selected for crossover.");

            // crossover
            if (selectedForCrossover.size() >= 2) {
                for (int j=0; j < selectedForCrossover.size(); j++) {
                    for (int k=j+1; k < selectedForCrossover.size(); k++) {
                        nextPopulation.add(crossover(selectedForCrossover.get(j), selectedForCrossover.get(k)));
                    }
                }
            }

            // mutation
            int number_of_mutations = (int)Math.floor(mutation_rate * population_size);
            for (int j=0; j < number_of_mutations; j++) {
                int mutIndex = rnd.nextInt(selectedPopulation.size());
                nextPopulation.add(mutate(selectedPopulation.get(mutIndex)));
            }
            System.out.println("[GA] " + number_of_mutations + " individuals mutated.");

            population = nextPopulation;
            System.out.println("[GA] advancing generation with " + nextPopulation.size() + " members.");
        }

        // after generation iterations return answer
        double bestFitness = 0;
        GAState bestState = null;

        for(GAState s : population){
            if(bestState == null || fitness(s) < bestFitness){
                bestState = s;
                bestFitness = fitness(s);
            }
        }

        System.out.println("[GA] completed!");
        long time = (System.currentTimeMillis() - startTime);
        Graph gg = null;
        double ff = 1.0;
        if(bestState != null) {
            gg = dnaToGraph(bestState.dna);
            ff = fitnessUtil.calc(gg);
        }
        System.out.println("[GA] time: " + time + "ms");
        return new Report(gg,ff,time);
    }

    private Graph dnaToGraph(int[] dna) {
        Graph pGraph = root.clone();
        // create graph based on dna
        for (int i = 0; i < dna.length; i++) {
            int dnaValue = dna[i];
            // if dna value is 0 we don't need to place anything
            if(dnaValue != 0) {
                // the candidate we are placing now
                SinkCandidate candidate = scenario.getSinkCandidates().get(i);
                // the sink type we're placing
                SinkConfiguration config = scenario.getSinkTypes().get(dnaValue-1);
                // place sink
                int vi = candidate.getPlacmentVertexIndex();
                pGraph.getVertices().get(vi).setNode(new SinkNode("S" + (i + 1), config));
            }
        }
        return pGraph;
    }

    private ArrayList<GAState> initialPopulation() {
        ArrayList<GAState> result = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < population_size; i++) {
            GAState s = new GAState(nSink);
            for (int j = 0; j < nSink; j++) {
                s.dna[j] = rnd.nextInt(nOption+1);
            }
            result.add(s);
        }
        return result;
    }

    private double fitness(GAState s) {
        // evaluate the graph
        return fitnessUtil.calc(dnaToGraph(s.dna));
    }

    private GAState crossover(GAState a, GAState b) {
        Random rnd = new Random();
        int dnaLen = a.dna.length;
        GAState baby = new GAState(dnaLen);
        int cindex = rnd.nextInt(dnaLen - 1) + 1;

        System.arraycopy(a.dna, 0, baby.dna, 0, cindex);
        System.arraycopy(b.dna, cindex, baby.dna, cindex, dnaLen - cindex);

        return baby;
    }

    private GAState mutate(GAState a) {
        Random rnd = new Random();
        int dnaLen = a.dna.length;
        GAState mutated = new GAState(dnaLen);
        int mindex = rnd.nextInt(dnaLen);

        System.arraycopy(a.dna, 0, mutated.dna, 0, dnaLen);
        mutated.dna[mindex] = rnd.nextInt(nOption+1);

        return mutated;
    }
}
