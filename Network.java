import java.util.Random;
import java.util.ArrayList;

class Network {
    private ArrayList<ArrayList<Neuron>> net = new ArrayList<ArrayList<Neuron>>();
    private float fitnessValue;
    public static final int RANGE = 1;

    public Network (int[] layers) {
        for (int index = 0; index < layers.length; index++) {
            ArrayList<Neuron> layer = new ArrayList<Neuron>();
            net.add(layer);
        }
        fitnessValue = 0;
        generateConnections(layers);
    }

    private void generateConnections (int[] layers) {
        ArrayList<Neuron> prev = null;
        for (int i = 0; i < net.size(); i++) {
            ArrayList<Neuron> layer = net.get(i);
            for (int j = 0; j < layers[i]; j++) {
                Random random = new Random();
                float bias = random.nextFloat() * RANGE;
                bias = -(RANGE/2.0f) + bias;
                Neuron neuron = new Neuron(bias); //set random bias
                if (prev != null) {
                    for(Neuron prevNeuron : prev) {
                        float weight = random.nextFloat() * RANGE;
                        weight = -(RANGE/2.0f) + weight;
                        neuron.addConnection(prevNeuron, weight); //set random weight
                    }
                }
                layer.add(neuron);
            }
            //this layer is done populating. move to the next layer
            prev = layer;
        }
    }

    //send some values to the nodes, which will propogate through the network and end up at the outputs
    private ArrayList<Float> calculateOutput (ArrayList<Float> inputValues) {
        if (net.get(0).size() != inputValues.size()) {
            //invalid number of inputs. they should be the same amount as inputs.length
            return null; 
        }
        //set values of all first layer neurons
        //set the value of each neuron to each input
        for (int index = 0; index < net.get(0).size(); index++) {
            Neuron neuron = net.get(0).get(index);
            neuron.setValue(inputValues.get(index));
            //don't compute sigmoid value for first layer
        }

        //do for all other layers 
        for (int index = 1; index < net.size(); index++) {
            for (Neuron neuron : net.get(index)) {
                neuron.computeSigmoidValue();
            }
        }

        //get the results!
        ArrayList<Float> outputResults = new ArrayList<Float>();
        for (Neuron neuron : net.get(net.size() - 1)) {
            float finalValue = neuron.getValue();
            outputResults.add(finalValue);
        }

        return outputResults;
    }

    public ArrayList<ArrayList<Neuron>> getNet() {
        return net;
    }

    public float getFitness () {
        return fitnessValue;
    }

    public void setFitness (float fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public void addFitness (float addValue) {
        this.fitnessValue += addValue;
    }

}