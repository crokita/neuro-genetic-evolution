import java.util.Random;
import java.util.ArrayList;

class Neuron {
	//a list of connections one level shallower in the network
	private ArrayList<Connection> connectionsBack = new ArrayList<Connection>();
	private float bias;
	private float value;

	public Neuron (float bias) {
		//ex: if biasRange is 10 then the possible bias values are -5 to 5
		this.bias = bias;
		value = 0;
	}

    public void addConnection (Neuron neuron, float weight) {
        Connection conn = new Connection(neuron, weight);
        connectionsBack.add(conn);
    }

	public void computeSigmoidValue () {
        for(Connection connection : connectionsBack) {
            Neuron target = connection.getNeuron();
            value += target.getValue() * connection.getWeight();
        }
        //all the values are added up. now do sigmoid function
        value -= bias;
        value = 1 / (1 + (float) Math.exp(-value));
	}

    public float getValue () {
        return value;
    }

    public void setValue (float value) {
        this.value = value;
    }

    public float getBias () {
        return bias;
    }

    public ArrayList<Connection> getConnections () {
        return connectionsBack;
    }

	public class Connection {
	    private Neuron connectedNeuron;
	    private float weight;

	    public Connection (Neuron connectedNeuron, float weight) {
	        this.connectedNeuron = connectedNeuron;
	        this.weight = weight;
	    }

	    public Neuron getNeuron () {
	        return connectedNeuron;
	    }

	    public float getWeight () {
	        return weight;
	    }

	    public void setWeight (float weight) {
	        this.weight = weight;
	    }
	}
}