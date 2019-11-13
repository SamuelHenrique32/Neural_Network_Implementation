import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NeuralNetwork {

	// Weights matrix input -> hidden
	private double[][] weightsMatrixInputHidden;
	
	// Weights matrix hidden -> output
	private double[][] weightsMatrixHiddenOutput;
	
	// Input layer
	private double[] inputLayer;
	
	// Hidden layer
	private double[] hiddenLayer;
	
	// Output layer
	private double[] outputLayer;
	
	// TODO initialize
	// Input values
	private Double[][] inputTraining;
	
	// TODO initialize
	// Expected values
	private Double[][] expectedOutput;
	
	// Current iteration
	private int currentIteration;
	
	// Confusion matrix
	private int[][] confusionMatrix;
	
	// Processed lines of file
	private int processedLines;
	
	// TODO why?
	private Double[] sigmaForY;
	private Double[] sigmaForZ;
	private double[][] deltaw1;
    private double[][] deltaw2;
	
	
	public NeuralNetwork() throws IOException {
		
		// Weights matrix input hidden
		//this.weightsMatrixInputHidden = new double[Params.getParamsInputNeuronsQuantity()][Params.getParamsHiddenNeuronsQuantity()];
		
		// Weights matrix hidden output
		//this.weightsMatrixHiddenOutput = new double[Params.getParamsHiddenNeuronsQuantity()][Params.getParamsOutputNeuronsQuantity()];
		
		//Input layer
		this.inputLayer = new double[Params.getInputNeuronsQuantity()+1];
		
		// Hidden layer
		this.hiddenLayer = new double[Params.getHiddenNeuronsQuantity()+1];
		
		// Output of output layer
		this.outputLayer = new double[Params.getOutputNeuronsQuantity()];
		
		// Current iteration
		this.currentIteration = 0;
		
		// Confusion matrix
		this.confusionMatrix = new int[Params.getOutputNeuronsQuantity()][Params.getOutputNeuronsQuantity()];
		
		// Processed lines of file
		this.processedLines = 0;
		
		//TODO
		// Why?
		this.inputLayer[Params.getInputNeuronsQuantity()] = 1.0;
		this.hiddenLayer[Params.getHiddenNeuronsQuantity()] = 1.0;
		this.weightsMatrixInputHidden = new double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()]; 
        this.weightsMatrixHiddenOutput = new double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        this.deltaw1 = new double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()];
        this.deltaw2 = new double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        this.sigmaForY = new Double[Params.getHiddenNeuronsQuantity() + 1];
        this.sigmaForZ = new Double[Params.getOutputNeuronsQuantity()];
		
		this.init();
		//this.train();
	}
	
	private void init() throws IOException {
		
		System.out.println("\nInicializando pesos...\n");
		
		// Init weights Input -> Hidden
		for(int i=0 ; i<Params.getInputNeuronsQuantity()+1; i++) {			
			for(int j=0 ; j<Params.getHiddenNeuronsQuantity(); j++) {
				
				this.weightsMatrixInputHidden[i][j] = generateRandomWeight();
			}
		}
		
		// Init weights Hidden -> Output
		for(int i=0 ; i<Params.getHiddenNeuronsQuantity()+1 ; i++) {
			for(int j=0 ; j<Params.getOutputNeuronsQuantity() ; j++) {
				
				this.weightsMatrixHiddenOutput[i][j] = generateRandomWeight();
			}
		}
		
		System.out.println("\nPesos inicializados\n");
		
		this.train(Params.getMaxIterations());
	}
	
	// Trains for the specified amount of times according to parameter paramsMaxIterations
	// Return error
	// Receive the number of times to train
	private Double train(int times) throws IOException {
		
		Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
        Double error = 0.0;

        FileController fileController = new FileController();

        // Read the dataset
        fileController.readDataset(Params.getTrainingFile());

		System.out.println("\nTreinando...\n");
		
		Double err = 0.0;
		
		int currentIteration = 0;
		
		System.out.println(fileController.getQuantityOfLinesDataset());
		
		// Trains the specified times
		while(currentIteration < Params.getMaxIterations()) {

			for (int i = 0; i < fileController.getQuantityOfLinesDataset()-1; i++) {

				this.copyLineReadToLayers(fileController.getDatasetLine(i), eO);
			}
			
			currentIteration++;
		}

		return error;
	}
	
	// Copy the line read to input layer and to expected output data structures
	private void copyLineReadToLayers(String datasetLine, Double[] expectedOutput) {
		
		boolean foudComma = false;
		int expectedOutputIndex = 0;
		
		// Put each character of the received string at inputLayerValues[index]
		for(int i=0 ; i < datasetLine.length() ; i++) {
			//System.out.println("String quebrada pos[ " + i + "] = " + datasetLine.charAt(i) + "\n");
			
			if(datasetLine.charAt(i) == ',') {
				
				System.out.println("Encontrei a virgula na pos " + i + "\n");
				foudComma = true;
				continue;
			}
			
			if(!foudComma) {
				// Copy to input layer
				this.inputLayer[i] = Character.getNumericValue(datasetLine.charAt(i));
			} else {
				// Copy to expected output
				expectedOutput[expectedOutputIndex] = (double) Character.getNumericValue(datasetLine.charAt(i));
				expectedOutputIndex++;
			}			
		}
		
		for(int i=0 ; i< Params.getInputNeuronsQuantity() ; i++) {
			System.out.println("Input layer pos[ " + i + "] = " + inputLayer[i]);
		}
		
		for(int i=0 ; i< Params.getExpectedOutputSize() ; i++) {
			System.out.println("Expected output pos[ " + i + "] = " + expectedOutput[i]);
		}
	}

	public void test() {
		
	}
	
	public void handleInput(String currentLine, boolean isTesting) {
		
			
		
		
		


		
	}
	
	private double generateRandomWeight() {
		
		Random rand =  new Random();
		double number = rand.nextDouble();
		
		// Correct value if it' zero
		if(number == 0) {
			number = 0.01;
		}
		
		return number;
	}
	
	// Activation function
	private double sigmoidal(double sum) {
		return (1 / (1 + Math.exp(-sum)));
	}
}
