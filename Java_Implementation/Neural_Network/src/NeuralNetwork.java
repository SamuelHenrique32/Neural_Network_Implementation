import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NeuralNetwork {

	// Weights matrix input -> hidden
	private double[][] weightsMatrixInputHidden;
	
	// Weights matrix hidden -> output
	private double[][] weightsMatrixHiddenOutput;
	
	// Stored input layer
	private double[][] storedInputLayer;
	
	// Input layer
	private double[] inputLayer;
	
	// Hidden layer
	private double[] hiddenLayer;
	
	// Output layer
	private Double[] outputLayer;
	
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
	
	// File controller
	private FileController fileController;
	
	// TODO why?
	private Double[] sigmaForY;
	private Double[] sigmaForZ;
	private double[][] deltaw1;
    private double[][] deltaw2;
	
	
	public NeuralNetwork() throws IOException {
		
		// File controller
		this.fileController = new FileController();
		
		// Read the dataset
        fileController.readDataset(Params.getTrainingFile());
		
		// Weights matrix input hidden
		//this.weightsMatrixInputHidden = new double[Params.getParamsInputNeuronsQuantity()][Params.getParamsHiddenNeuronsQuantity()];
		
		// Weights matrix hidden output
		//this.weightsMatrixHiddenOutput = new double[Params.getParamsHiddenNeuronsQuantity()][Params.getParamsOutputNeuronsQuantity()];
		
		// Stored input layer
		this.storedInputLayer = new double[fileController.getQuantityOfLinesDataset()][Params.getInputNeuronsQuantity()];
		
		// Current input layer
		this.inputLayer = new double[Params.getInputNeuronsQuantity()+1];
		
		// Hidden layer
		this.hiddenLayer = new double[Params.getHiddenNeuronsQuantity()+1];
		
		// Output of output layer
		this.outputLayer = new Double[Params.getOutputNeuronsQuantity()];
		
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
        this.sigmaForY = new Double[Params.getHiddenNeuronsQuantity() + 1];
        this.sigmaForZ = new Double[Params.getOutputNeuronsQuantity()];
        this.deltaw1 = new double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()];
        this.deltaw2 = new double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];        
		
		this.init();
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
		
		// Expected values
		this.expectedOutput = new Double[fileController.getQuantityOfLinesDataset()][Params.getOutputNeuronsQuantity()];
		
		// Initialize the data structure with the dataset files
		this.initializeTrainingDataset();
		
		// Train the specified times
		this.train(Params.getMaxIterations());
	}
	
	// Trains for the specified amount of times according to parameter paramsMaxIterations
	// Return error
	// Receive the number of times to train
	private Double train(int times) throws IOException {
		
		// Current expected output
		Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
        Double error = 0.0;

		System.out.println("\nTreinando...\n");
		
		Double err = 0.0;
		
		int currentIteration = 0;
		
		//System.out.println(fileController.getQuantityOfLinesDataset());
		
		// Trains the specified times
		while(currentIteration < Params.getMaxIterations()) {

			for (int i = 0; i < fileController.getQuantityOfLinesDataset()-1; i++) {

				//this.copyLineReadToLayers(fileController.getDatasetLine(i), i);
				
				// Calls method of feedForward to the given input
				this.feedForward();
				
				// Calls method with expected output
				//this.backPropagation(eO);
				this.backPropagation(this.expectedOutput[i]);
			}
			err = this.calculateError();
			System.out.println("Taxa de erro da iteracao " + currentIteration + ": " + err);
			
			currentIteration++;
		}

		return error;
	}
	
	// feedForward calculates the output of hidden layer and output layer
	private void feedForward() {
		// Hidden layer
        this.setOutputHiddenLayer();
        
        // Output layer
        this.setOutputFinalLayer();
    }
	
	// Calculates the output of hidden layer
	private void setOutputHiddenLayer() {
		
		// Set zero values is array
		for (int i = 0; i < Params.getHiddenNeuronsQuantity(); i++) {
	            this.sigmaForY[i] = 0.0;
	    }
		
		// Generate the output value
		for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
            for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            	// Try to multiply the values
                try {
                	// Multiply the values
                    this.sigmaForY[j] = this.sigmaForY[j] + this.inputLayer[i] * this.weightsMatrixInputHidden[i][j];
                } catch (Exception e) {
                	// If an error has ocurred
                    System.out.println("erro" + e);
                }
            }
        }		
		
		// Apply the activation function
		for (int i = 0; i < Params.getHiddenNeuronsQuantity(); i++) {
			
			this.hiddenLayer[i] = this.sigmoidal(this.sigmaForY[i]);
			
			//System.out.println(this.hiddenLayer[i]);
		}
	}
	
	// Calculates the values of output layer
	private void setOutputFinalLayer() {
		// Set zero values is array
		for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
			this.sigmaForZ[i] = 0.0;
		}
		
		// Generate the output value
		for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {
                this.sigmaForZ[i] = this.sigmaForZ[i] + this.hiddenLayer[j] * this.weightsMatrixHiddenOutput[j][i];
            }
        }
		
		// Apply the activation function
		for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
			this.outputLayer[i] = this.sigmoidal(this.sigmaForZ[i]);
        }
	}
	
	// Calculates backPropagation
	private void backPropagation(Double[] expectedOutputParam) {
		
		Double[] values = new Double[Params.getOutputNeuronsQuantity()];
		
		// Calculate the values
		for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
			
			//System.out.println(expectedOutput[i]);
			
			values[i] = (expectedOutputParam[i] - this.outputLayer[i]) * this.sigmoidalDerivate(this.sigmaForZ[i]);			
			
		}
		
		for (int i = 0; i < Params.getHiddenNeuronsQuantity() + 1; i++) { 
            for (int j = 0; j < Params.getOutputNeuronsQuantity(); j++) {                  
                this.deltaw2[i][j] = Params.getLearningRate() * values[j] * this.hiddenLayer[i];
            }
        }
		
		Double[] fHNet = new Double[Params.getHiddenNeuronsQuantity()];
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
            fHNet[j] = 0.0;
            for (int k = 0; k < Params.getExpectedOutputSize(); k++) {
                fHNet[j] = fHNet[j] + (values[k] * this.weightsMatrixHiddenOutput[j][k]);
            }
        }
        
        Double[] fH = new Double[Params.getHiddenNeuronsQuantity()];
        for (int i = 0; i < Params.getHiddenNeuronsQuantity() ; i++) {
        	
        	fH[i] = fHNet[i] * this.sigmoidalDerivate(this.sigmaForY[i]);
        }
        
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.deltaw1[i][j] = Params.getLearningRate() * fH[j] * this.inputLayer[i];
            }
        }
        
        // Recalculate weights
        this.changeWeights();
	}
	
	private Double calculateError() {
		
		Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
		Double err = 0.0;
		Double totalError = 0.0;
		
		for(int i= 0 ; i < fileController.getQuantityOfLinesDataset() ; i++) {
			
			//this.copyLineReadToLayers(fileController.getDatasetLine(i), i);
			
			this.feedForward();
			
			for (int j=0; j < Params.getOutputNeuronsQuantity(); j++) {
				//err += Math.pow((eO[j] - this.outputLayer[j]), 2);
				err += Math.pow((this.expectedOutput[i][j] - this.outputLayer[j]), 2);
	        }
			
			err /= Params.getOutputNeuronsQuantity();
			totalError += err;
		}
		
		totalError /= fileController.getQuantityOfLinesDataset();
		
		return totalError;
	}
	
	// Recalculate weights
	private void changeWeights() {
        for (int i = 0; i < Params.getHiddenNeuronsQuantity() + 1; i++) { 
            for (int j = 0; j < Params.getExpectedOutputSize(); j++) {    
                this.weightsMatrixHiddenOutput[i][j] = this.weightsMatrixHiddenOutput[i][j] + this.deltaw2[i][j];
            }
        }
        
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.weightsMatrixInputHidden[i][j] = this.weightsMatrixInputHidden[i][j] + this.deltaw1[i][j];
            }
        }
    }
	
	public void test() {
		
	}
	
	// Get the training dataset
	private void initializeTrainingDataset() {
		
		boolean foudComma = false;
		boolean isLastNumber = true;
		int expectedOutputIndex = 0;
		String[] cutLines;
		String expectedOutputString = new String();
		String currentLine;
		
		//System.out.println("Linhas no arquivo: " + fileController.getQuantityOfLinesDataset());
		
		// For each line in the file
		for(int i=0 ; i<this.fileController.getQuantityOfLinesDataset() ; i++) {
			foudComma = false;
			isLastNumber = true;
			// Get current line
			currentLine = this.fileController.getDatasetLine(i);
			
			// Handle each character of current line
			for(int j=0 ; j < currentLine.length() -1; j++) {
				
				int posVerifyComma = j+1;
				
				//System.out.println("POS: " + posVerifyComma);
				
				if(currentLine.charAt(posVerifyComma) == ',') {
					
					//System.out.println("Encontrei a virgula na pos " + i + "\n");
					foudComma = true;
					
					cutLines = currentLine.split(",");
					
					//System.out.println(cutLines[0]);
					//System.out.println(cutLines[1]);
					
					expectedOutputString = cutLines[1];
					
					//System.out.println(expectedOutputString);
					
					// Reset pos
					expectedOutputIndex = 0;
					
				}
				
				if(!foudComma) {
					// Copy to stored input layer
					this.storedInputLayer[i][j] = Character.getNumericValue(currentLine.charAt(j));
					
					//System.out.println("i:" + i);
					//System.out.println("j:" + j);
					//System.out.println("if=" + currentLine.charAt(j));					
				}
				else {
					
					if(isLastNumber) {
						isLastNumber= false;
						// Copy to stored input layer
						this.storedInputLayer[i][j] = Character.getNumericValue(currentLine.charAt(j));
						
						//System.out.println("i:" + i);
						//System.out.println("j:" + j);
						//System.out.println("else=" + currentLine.charAt(j));	
					}
					else {
						
						// Copy to expected output
						this.expectedOutput[i][expectedOutputIndex] = (double) Character.getNumericValue(expectedOutputString.charAt(expectedOutputIndex));
						
						//System.out.println(expectedOutputString);
						
						//System.out.println(this.expectedOutput[i][expectedOutputIndex]);
						//System.out.println("Index: " + expectedOutputIndex);
						
						expectedOutputIndex++;						
					}								
				}			
			}			
		}
		
		/*for (int i = 0; i < fileController.getQuantityOfLinesDataset(); i++) {
			for(int j=0 ; j< Params.getInputNeuronsQuantity() ; j++) {
				System.out.println("Stored input layer pos[" + i + "] [" + j + "] = " + this.storedInputLayer[i][j]);
				
			}
			
			System.out.println("Mudou de linha\n\n");
		}
		
	
		for(int i=0 ; i< fileController.getQuantityOfLinesDataset(); i++) {
			for(int j=0 ; j< Params.getOutputNeuronsQuantity() ; j++) {
				System.out.println("Expected input layer pos[" + i + "] [" + j + "] = " + this.expectedOutput[i][j]);
			}
			
			System.out.println("Mudou de linha\n\n");
		}
		
		System.out.println("Terminou\n\n");*/
		
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
	
	// Calculates the derivate of sigmoidal activation function 
	private double sigmoidalDerivate(Double value) {
		
		return this.sigmoidal(value) * (1 - this.sigmoidal(value));
	}
}
