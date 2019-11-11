import java.io.IOException;
import java.util.Random;

public class NeuralNetwork {

	// Weights matrix input hidden
	private double[][] weightsMatrixInputHidden;
	
	// Weights matrix hidden output
	private double[][] weightsMatrixHiddenOutput;
	
	// Output of hidden layer
	private double[] hiddenLayerValues;
	
	// Output of output layer
	private double[] outputLayerValues;
	
	// Confusion matrix
	private int[][] confusionMatrix;
	
	// Processed lines of file
	private int processedLines;
	
	public NeuralNetwork() throws IOException {
		
		// Weights matrix input hidden
		this.weightsMatrixInputHidden = new double[Params.getParamsInputNeuronsQuantity()][Params.getParamsHiddenNeuronsQuantity()];
		
		// Weights matrix hidden output
		this.weightsMatrixHiddenOutput = new double[Params.getParamsHiddenNeuronsQuantity()][Params.getParamsOutputNeuronsQuantity()];
		
		// Output of hidden layer
		this.hiddenLayerValues = new double[Params.getParamsHiddenNeuronsQuantity()];
		
		// Output of output layer
		this.outputLayerValues = new double[Params.getParamsOutputNeuronsQuantity()];
		
		// Confusion matrix
		this.confusionMatrix = new int[Params.getParamsOutputNeuronsQuantity()][Params.getParamsOutputNeuronsQuantity()];
		
		// Processed lines of file
		this.processedLines = 0;
		
		this.init();
		this.train();
	}
	
	private void init() {
		
		// Init weights Input -> Hidden
		for(int i=0 ; i<Params.getParamsInputNeuronsQuantity() ; i++) {			
			for(int j=0 ; j<Params.getParamsHiddenNeuronsQuantity() ; i++) {
				
				this.weightsMatrixInputHidden[i][j] = generateRandomWeight();
			}
		}
		
		// Init weights Hidden -> Output
		for(int i=0 ; i<Params.getParamsHiddenNeuronsQuantity() ; i++) {
			for(int j=0 ; j<Params.getParamsOutputNeuronsQuantity() ; i++) {
				
				this.weightsMatrixHiddenOutput[i][j] = generateRandomWeight();
			}
		}
	}
	
	// Trains for the specified amount of times according to parameter paramsMaxIterations
	private void train() throws IOException {
		
		int currentIteration = 0;
		
		// Trains the specified times
		while(currentIteration < Params.getParamsMaxIterations()) {
		
			FilesController filesController = new FilesController();
			
			// Send this because readFiles call NeuralNetwork method handleInput
			// TODO Verify if it's the best method, i don't think so!
			filesController.readFile(this, Params.getParamTrainingFile(), false);
			
			currentIteration++;
		}		
	}
	
	public void test() {
		
	}
	
	public void handleInput(String currentLine, boolean isTesting) {
		
	}
	
	private double generateRandomWeight() {
		
		Random rand =  new Random();
		double number = rand.nextDouble();
		
		if(number == 0) {
			number = 0.01;
		}
		
		return number;
	}
}
