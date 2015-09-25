import java.util.Random;
import java.util.ArrayList;

//convert from and to other network instances
class Gene {
	ArrayList<ArrayList<Float>> info;

	Gene (Network network) {
        ArrayList<ArrayList<Neuron>> net = network.getNet();
        info = new ArrayList<ArrayList<Float>>();
        setInfo(net);
	}

	//format for a Neuron:
	// <bias number> <number of connections> <connection weights>
	private void setInfo(ArrayList<ArrayList<Neuron>> net) {
		//first layer just has biases
		for (ArrayList<Neuron> layer : net) {
			ArrayList<Float> geneLayer = new ArrayList<Float>();
			for (Neuron neuron : layer) {
				geneLayer.add(neuron.getBias());
				ArrayList<Neuron.Connection> connections = neuron.getConnections();
				geneLayer.add((float) connections.size());
				for (int i = 0; i < connections.size(); i++) {
					geneLayer.add(connections.get(i).getWeight());
				}
			}
			info.add(geneLayer);
		}
	}

	//picks a random point to slice through and swaps the information of the two genes after that point
	//brings back the two genes
	public Gene[] breed (Gene gene) {
		int totalInfo = getTotalInfo();
		int randomSplitPoint = 1 + new Random().nextInt(totalInfo - 2);

		//swap genes at split point
		for (int i = 0; i < info.size(); i++) {
			ArrayList<Float> layer = info.get(i);
			for (int j = 0; j < layer.size(); j++){ 
				float value = layer.get(j);
				if (randomSplitPoint < 0) { //swap
					float otherGeneValue = gene.getInfo().get(i).get(j);
					info.get(i).set(j, otherGeneValue);
					gene.getInfo().get(i).set(j, value);
				}
				randomSplitPoint -= 1;
			}
		}

        //have a small change for a mutation, for both new children
        if (new Random().nextFloat() < NaturalSelection.MUTATION_CHANCE) {
            int mutationsLeft = NaturalSelection.MUTATION_FORCE;
            while (mutationsLeft > 0) {
            	int randomMutateLayer = new Random().nextInt(info.size());
            	int randomMutatePos = new Random().nextInt(info.get(randomMutateLayer).size());
		    	float weight = new Random().nextFloat() * Network.RANGE;
				weight = -(Network.RANGE/2.0f) + weight;
                info.get(randomMutateLayer).set(randomMutatePos, weight);
                mutationsLeft--;
            }
        }
        if (new Random().nextFloat() < NaturalSelection.MUTATION_CHANCE) {
            int mutationsLeft = NaturalSelection.MUTATION_FORCE;
            while (mutationsLeft > 0) {
            	int randomMutateLayer = new Random().nextInt(gene.getInfo().size());
            	int randomMutatePos = new Random().nextInt(gene.getInfo().get(randomMutateLayer).size());
		    	float weight = new Random().nextFloat() * Network.RANGE;
				weight = -(Network.RANGE/2.0f) + weight;
                gene.getInfo().get(randomMutateLayer).set(randomMutatePos, weight);
                mutationsLeft--;
            }
        }

        Gene[] genes = {this, gene};
        return genes;
	}

	//convert this class into a network and return it
	public Network toNetwork () {
		int[] layers = new int[info.size()];
		for (int i = 0; i < info.size(); i++) {
			layers[i] = info.get(i).size();
		}
		Network network = new Network(layers);
		ArrayList<ArrayList<Neuron>> net = network.getNet();

		ArrayList<Neuron> prevLayer = null;
		System.out.println("NEW NETWORK");
		for (ArrayList<Float> layer : info) {
			ArrayList<Neuron> geneLayer = new ArrayList<Neuron>();

			int index = 0;
			System.out.println("NEW LAYER");
			for(float m : layer) {
				System.out.println();
				System.out.print(m);
			}
			while (index < layer.size()) {
				//new loop means a new neuron to extract from the data
				//format for a Neuron:
				// <bias number> <number of connections> <connection weights>
				Neuron neuron = new Neuron(layer.get(index)); 
				int connectionNumber = Math.round(layer.get(index + 1));
				index += 2;
				for (int i = 0; i < connectionNumber; i++) {
					neuron.addConnection(prevLayer.get(i), layer.get(index));
					index ++;
				}
				geneLayer.add(neuron);
			}

			net.add(geneLayer);
			prevLayer = geneLayer;
		}
		return network;
	}

	private int getTotalInfo () {
		int totalInfo = 0; //how much information, including connection number, that is stored in a gene
		//connection number should be the same for both genes, as well as structure of network
		for (ArrayList<Float> layer : info) {
			totalInfo += layer.size();
		}
		return totalInfo;
	}

	public ArrayList<ArrayList<Float>> getInfo () {
		return info;
	}

}