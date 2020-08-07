package optimization;

import algorithm.Evaluator;
import graph.Graph;
import model.Scenario;
import model.SinkCandidate;
import model.SinkConfiguration;
import model.SinkNode;
import visual.ShowGraph;

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
        public int score;

        public GAScore(GAState state,int score){
            this.state = state;
            this.score = score;
        }

        public int getScore() { return score; }
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
    }

    public void solve() {

        // initialization
        ArrayList<GAState> population = initialPopulation();

        // advance generations
        for (int i = 0; i < generations; i++) {

            System.out.println("[GA] running generation "+ i + "...");

            // calc each specie's fitness
            int sumFit = 0;
            int bestFit = Integer.MIN_VALUE;
            int worstFit = Integer.MAX_VALUE;
            ArrayList<GAScore> scores = new ArrayList<>();
            for (GAState s: population) {
                int fitness = fitness(s);
                scores.add(new GAScore(s, fitness));
                sumFit += fitness;
                if(fitness < worstFit) worstFit = fitness;
                if(fitness > bestFit) bestFit = fitness;
            }
            double avgFit = (double)sumFit / population.size();
            System.out.println("[GA] fitness best: " + bestFit + " avg: " + avgFit + " worst: " + worstFit);

            // calculate cumulative probability for RWS
            double[] cumulativeP = new double[population.size() + 1];
            cumulativeP[0] = 0;
            double cumulativeS = 0;
            for (int j = 1; j <= scores.size(); j++) {
                double p = (double)scores.get(j - 1).score / sumFit;
                cumulativeS += p;
                cumulativeP[j] = cumulativeS;
            }

            // perform RWS for natural selection
            ArrayList<GAState> selectedPopulation = new ArrayList<>();
            Random rnd = new Random();
            while (selectedPopulation.size() < population_size) {
                double r = rnd.nextDouble();
                for (int j = 0; j < population_size; j++) {
                    if (r < cumulativeP[j]) {
                        //select i-1 nth element
                        selectedPopulation.add(scores.get(j).state);
                        break;
                    }
                }
            }
//            ArrayList<GAState> selectedPopulation = new ArrayList<>();
//            scores.stream().sorted(Comparator.comparingInt(GAScore::getScore)).

            // parents which go to next generation
            ArrayList<GAState> nextPopulation = new ArrayList<>(selectedPopulation);

            // select for crossover
            ArrayList<GAState> selectedForCrossover = new ArrayList<>();
            for (int j = 0; j < population_size; j++) {
                if (rnd.nextDouble() < crossover_rate) {
                    selectedForCrossover.add(selectedPopulation.get(j));
                }
            }

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
                int mutIndex = rnd.nextInt(population_size);
                nextPopulation.add(mutate(selectedPopulation.get(mutIndex)));
            }

            population = nextPopulation;
            System.out.println("[GA] advancing generation with " + nextPopulation.size() + " members.");
        }

        // after generation iterations return answer
        int bestFitness = 0;
        GAState bestState = null;

        for(GAState s : population){
            if(bestState == null || fitness(s) > bestFitness){
                bestState = s;
                bestFitness = fitness(s);
            }
        }

        System.out.println("[GA] completed!");
        Graph gg = dnaToGraph(bestState.dna);
        ShowGraph.showGraph("cost: " + Evaluator.evaluate(gg),gg);
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

    private int fitness(GAState s) {
        // evaluate the graph
        int eval = Evaluator.evaluate(dnaToGraph(s.dna));
        if(eval == Integer.MAX_VALUE) return 1;
        else return 1 + (maxCost - eval);
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
