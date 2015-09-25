import java.util.*;
import java.util.Random;

class NaturalSelection {
    Network[] networks;
    public static final float MUTATION_CHANCE = 0.05f;
    public static final int MUTATION_FORCE = 20;

    NaturalSelection (Network[] networks) {
        this.networks = networks;
    }

    //performs natural selection by only allowing the 50% most fit to continue
    //to the next generation. the original population is restored through
    //breeding. Genes will be split and possibly mutated to create the new children
    public Network[] newGeneration () {
        //get the top 50%, rounded up
        Network[] mostFit = getMostFit();
        //next, create a new generation through crossing genes and mutations
        Network[] nextGen = createNewNetworks(mostFit, networks.length);

        return nextGen;
    }

    private Network[] getMostFit () {
        //sort the network array by the greatest fitness value
        sort();

        //now only take the top 50%. round up if the number is odd
        int finalLength = (int) Math.ceil(networks.length / 2.0f);
        Network[] newNetworks = new Network[finalLength];
        for (int index = 0; index < finalLength; index++) {
            newNetworks[index] = networks[index];
        }

        return newNetworks;
    }

    //makes more networks based on two parents and mutations. networkCount is how many
    //should exist by the end of this function
    private Network[] createNewNetworks (Network[] mostFit, int networkTotal) {
        //keep making new networks until networkTotal is reached
        Network[] newGen = new Network[0];

        while (newGen.length < networkTotal) {
            //use probability based on their fitness scores to choose random parents
            float fitnessTotal = 0;
            float[] probabilities = new float[mostFit.length];
            for (int i = 0; i < mostFit.length; i++) {
                probabilities[i] = mostFit[i].getFitness();
                fitnessTotal += mostFit[i].getFitness();
            }
            //if fitnessTotal is 0, everyone failed. give everyone equal probability to reproduce
            if (fitnessTotal == 0) {
                for (int i = 0; i < mostFit.length; i++) {
                    mostFit[i].setFitness(1);
                    probabilities[i] = 1;
                    fitnessTotal += 1;
                }
            }
            //normalize all the values so they are in percentages
            for (int i = 0; i < mostFit.length; i++) {
                probabilities[i] /= fitnessTotal;
            }
            //pick two parents based on this probability distribution given to each network
            //get two random parents together
            int firstParentIndex = pickIndex(probabilities);
            int secondParentIndex = pickIndex(probabilities);

            //let parents allow to be the same in the case where one parent gets all the points and has 
            //100% chance to be picked; that will always be picked anyway

            Gene firstParentGene = new Gene(mostFit[firstParentIndex]);
            Gene secondParentGene = new Gene(mostFit[secondParentIndex]);
            Gene[] children = firstParentGene.breed(secondParentGene);

            Network offspring1 = children[0].toNetwork();
            Network offspring2 = children[1].toNetwork();

            int finalLength = newGen.length + 2;
            if (finalLength > networkTotal) {
                finalLength = networkTotal;
            }

            Network[] newGenAddTwo = new Network[finalLength];
            for (int index = 0; index < newGen.length; index++) {
                newGenAddTwo[index] = newGen[index];
            }
            //add the two new offspring
            if (newGen.length < networkTotal)
                newGenAddTwo[newGen.length] = offspring1;
            if (newGen.length + 1 < networkTotal)
                newGenAddTwo[newGen.length + 1] = offspring2;
            newGen = newGenAddTwo; //newGen has two more offspring on it
        }
        return newGen;
    }

    public int pickIndex (float[] probabilities) {
        float prob = (float) Math.random();
        float cumulativeProbability = 0.0f;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (prob <= cumulativeProbability) {
                return i;
            }
        }
        return probabilities.length - 1; //if the for loop went all the way through
        //(theoretically impossible) then just return the last index
    }

    //sort the network array, largest to smallest
    private void sort () {
        Arrays.sort(networks, new Comparator<Network>() {
            public int compare(Network n1, Network n2) {
                return Float.compare(n2.getFitness(), n1.getFitness());
            }
        });
    }

}