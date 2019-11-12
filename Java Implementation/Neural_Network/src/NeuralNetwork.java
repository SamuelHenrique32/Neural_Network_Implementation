import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
		
		System.out.println("\nInicializando pesos...\n");
		
		// Init weights Input -> Hidden
		for(int i=0 ; i<Params.getParamsInputNeuronsQuantity()-1; i++) {			
			for(int j=0 ; j<Params.getParamsHiddenNeuronsQuantity()-1; j++) {
				
				this.weightsMatrixInputHidden[i][j] = generateRandomWeight();
			}
		}
		
		// Init weights Hidden -> Output
		for(int i=0 ; i<Params.getParamsHiddenNeuronsQuantity()-1 ; i++) {
			for(int j=0 ; j<Params.getParamsOutputNeuronsQuantity()-1 ; j++) {
				
				this.weightsMatrixHiddenOutput[i][j] = generateRandomWeight();
			}
		}
		
		System.out.println("\nPesos inicializados\n");
	}
	
	// Trains for the specified amount of times according to parameter paramsMaxIterations
	private void train() throws IOException {
		
		System.out.println("\nTreinando...\n");
		
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
		
		//System.out.println("Recebi: " + currentLine);
		
		int[] inputLayerValues = new int[currentLine.length()];
		
		// Put each character of the received string at inputLayerValues[index]
		for(int i=0 ; i < currentLine.length() ; i++) {
			//System.out.println("String quebrada pos[ " + i + "] = " + currentLine.charAt(i));
			inputLayerValues[i] = Character.getNumericValue(currentLine.charAt(i));
		}
		
//		for(int i=0 ; i< currentLine.length() ; i++) {
//			System.out.println("Input layer pos[ " + i + "] = " + inputLayerValues[i]);
//		}

		int sum = 0;
		
		// Sum of entries with weight (it will be hidden layer input)
		for (int i = 0 ; i < Params.getParamsHiddenNeuronsQuantity()-1 ; i++) {
			for (int j = 0; j < Params.getParamsInputNeuronsQuantity()-1 ; j++) {
				
				sum += (inputLayerValues[j] * weightsMatrixInputHidden[j][i]);
				//System.out.println("Sum [" + i + "] = " + sum);
				//System.out.println("inputLayerValues [" + j + "] = " + inputLayerValues[j]);
			}
			
			// Apply activation function
			hiddenLayerValues[i] = sigmoidal(sum);
			//System.out.println("Hidden layer value [" + i + "] = " + hiddenLayerValues[i]);
			
		}
		
		sum = 0;
		
		// The output of hidden layer will be the entry of output layer
		for(int i=0 ; i < Params.getParamsOutputNeuronsQuantity()-1 ; i++) {
			for(int j = 0 ; j < Params.getParamsHiddenNeuronsQuantity()-1 ; j++) {
				
				sum += (hiddenLayerValues[j] * weightsMatrixHiddenOutput[j][i]);
			}
			
			outputLayerValues[i] = sigmoidal(sum);
		}
		
		for(int i=0 ; i < Params.getParamsOutputNeuronsQuantity() ; i++) {
			System.out.println(outputLayerValues[i]);
		}
		
		System.out.println("------------------");
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
