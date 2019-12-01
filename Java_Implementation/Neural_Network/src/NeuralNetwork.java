import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NeuralNetwork {

    private Double[] inputLayer;
    private Double[] hiddenLayer;
    private Double[] outputLayer;

    // Weights matrix input -> hidden
    private Double[][] weightsMatrixInputHidden;
    
    // Weights matrix hidden -> output
    private Double[][] weightsMatrixHiddenOutput;

    private Double[] sigmaForHiddenLayer;
    private Double[] sigmaForOutputLayer;

    private Double[][] difWeightsInputHidden;
    private Double[][] difWeightsHiddenOutput;

    // Stored data
    private Double[][] storedInputLayerTraining;
    private Double[][] storedExpectedOutputTraining;

    private Integer currentIteration;
    
    // File controller
 	private FileController fileController;
 	
 	private boolean isTesting;
 	
 	// Store the index of the neuron with the biggest output value
 	private int biggestNeuronValueIndex;

    // Constructor method    
    public NeuralNetwork() throws IOException {
		super();
		this.init();
	}

	private void init() throws IOException {

        this.currentIteration = 0;
        
        // File controller
     	this.fileController = new FileController();
     	
     	this.isTesting = false;

        this.inputLayer = new Double[Params.getInputNeuronsQuantity() + 1];
        this.hiddenLayer = new Double[Params.getHiddenNeuronsQuantity() + 1];
        this.outputLayer = new Double[Params.getOutputNeuronsQuantity()];
        // Set bias
        this.inputLayer[Params.getInputNeuronsQuantity()] = 1.0;
        this.hiddenLayer[Params.getHiddenNeuronsQuantity()] = 1.0;

        this.sigmaForHiddenLayer = new Double[Params.getHiddenNeuronsQuantity() + 1];
        this.sigmaForOutputLayer = new Double[Params.getOutputNeuronsQuantity()];
        
        // Stored input layer training
     	this.storedInputLayerTraining = new Double[fileController.getQuantityOfLinesTrainingDataset()][Params.getInputNeuronsQuantity()];
     	// Stored expected values
     	this.storedExpectedOutputTraining = new Double[fileController.getQuantityOfLinesTrainingDataset()][Params.getOutputNeuronsQuantity()];

        this.weightsMatrixInputHidden = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()]; 
        this.weightsMatrixHiddenOutput = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        
        this.difWeightsInputHidden = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()];
        this.difWeightsHiddenOutput = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        
        this.biggestNeuronValueIndex = 0;

        // Initialize weights matrix input -> hidden
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.weightsMatrixInputHidden[i][j] = this.generateRandomWeight();
            }
        }
        
        // Initialize weights matrix hidden -> output
        for (int i = 0; i < Params.getHiddenNeuronsQuantity() + 1; i++) { 
            for (int j = 0; j < Params.getOutputNeuronsQuantity(); j++) {
                this.weightsMatrixHiddenOutput[i][j] = this.generateRandomWeight();
            }
        }
        
        // Reads training dataset
        fileController.readDataset(Params.getTrainingFile(), 1);
        
        // Reads test dataset
        fileController.readDataset(Params.getTestFile(), 2);
        
        // Initialize the data structure with the dataset files
     	this.initializeTrainingDataset();
    }
	
	public int getBiggestNeuronValueIndex() {

		return this.biggestNeuronValueIndex;
	}

    public Double train(int times) {
    	
        Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
        Double error = 0.0;
        
        System.out.println("Treinando...");
        
        while (this.currentIteration < Params.getMaxIterations()) {
        	// For each line in training file
            for (int i = 0; i < this.storedInputLayerTraining.length; i++) {
            	// Copy data
                System.arraycopy(this.storedInputLayerTraining[i], 0, inputLayer, 0, this.storedInputLayerTraining[i].length);
                System.arraycopy(this.storedExpectedOutputTraining[i], 0, eO, 0, this.storedExpectedOutputTraining[i].length);

                this.feedForward();
                this.backPropagation(eO);
            }
            error = this.calculateError();
            System.out.println("Taxa de erro na iteracao " + this.currentIteration + ": " + error);
            this.currentIteration++;
        } 
        System.out.println("\nA rede treinou " + this.currentIteration + " vezes!\n\n");
        
        // Serialize
        fileController.serialize(1, this.weightsMatrixInputHidden);
        fileController.serialize(2, this.weightsMatrixHiddenOutput);
        
        return error;        
    }

    private Double calculateError() {
    	
        Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
        Double err = 0.0;
        Double finalError = 0.0;

        for (int i = 0; i < this.storedInputLayerTraining.length; i++) {
        	
            System.arraycopy(this.storedInputLayerTraining[i], 0, inputLayer, 0, this.storedInputLayerTraining[i].length);
            System.arraycopy(this.storedExpectedOutputTraining[i], 0, eO, 0, this.storedExpectedOutputTraining[i].length);
            
            this.feedForward();
            
            for (int j = 0; j < Params.getOutputNeuronsQuantity(); j++) {
                err += Math.pow((eO[j] - this.outputLayer[j]), 2);
            }
            err /= Params.getOutputNeuronsQuantity();
            finalError += err;
        }
        finalError /= this.storedInputLayerTraining.length;
        return finalError;
    }

    public void test(Double[] input) {
    	
    	this.isTesting = true;
    	
        System.arraycopy(input, 0, this.inputLayer, 0, Params.getInputNeuronsQuantity());
        
        this.feedForward();
    }

    private void feedForward() {
    	
        this.setOutputHiddenLayer();
        this.setOutputFinalLayer();
    }

    private void setOutputHiddenLayer() {
    	
        for (int i = 0; i < Params.getHiddenNeuronsQuantity(); i++) {
            this.sigmaForHiddenLayer[i] = 0.0;
        }
        
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
            for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
                try {
                    this.sigmaForHiddenLayer[j] = this.sigmaForHiddenLayer[j] + this.inputLayer[i] * this.weightsMatrixInputHidden[i][j];
                } catch (Exception e) {
                    System.out.println("Um erro aconteceu: " + e);
                }
            }
        }
        
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
        	this.hiddenLayer[j] = this.sigmoidal(this.sigmaForHiddenLayer[j]);
        }
    }

    private void setOutputFinalLayer() {
    	
    	Double biggestNeuronValue = 0.0;
    	
        for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
            this.sigmaForOutputLayer[i] = 0.0;
        }
        
        for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {
                this.sigmaForOutputLayer[k] = this.sigmaForOutputLayer[k] + this.hiddenLayer[j] * this.weightsMatrixHiddenOutput[j][k];
            }
        }
        
        for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
        	this.outputLayer[i] = this.sigmoidal(this.sigmaForOutputLayer[i]);
        }
        
        if(this.isTesting) {
        	
        	System.out.println("\nValores neuronios de saida:\n");
        	
        	for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
        		// Verify the output value
        		if(this.outputLayer[i] > biggestNeuronValue) {
        			
        			// Store value
        			biggestNeuronValue = this.outputLayer[i];
        			
        			// Store index
        			this.biggestNeuronValueIndex = i;
        		}
            	System.out.println("Neuronio " + i + ": " + this.outputLayer[i] + "\n");
            }
        	
        	this.isTesting = false;
        }
    }    

    private void backPropagation(Double[] expectedOutput) {
    	
        Double[] fO = new Double[Params.getOutputNeuronsQuantity()];

        for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
            
        	fO[i] = (expectedOutput[i] - this.outputLayer[i]) * this.derivatedSigmoidal(this.sigmaForOutputLayer[i]);
        }
        
        for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {//+bias weight
            for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
                this.difWeightsHiddenOutput[j][k] = Params.getLearningRate() * fO[k] * this.hiddenLayer[j];
            }
        }
        
        Double[] valuesHiddenOutput = new Double[Params.getHiddenNeuronsQuantity()];
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
            valuesHiddenOutput[j] = 0.0;
            for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
                valuesHiddenOutput[j] = valuesHiddenOutput[j] + (fO[k] * this.weightsMatrixHiddenOutput[j][k]);
            }
        }
        
        Double[] valuesHidden = new Double[Params.getHiddenNeuronsQuantity()];
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
           
        	valuesHidden[j] = valuesHiddenOutput[j] * this.derivatedSigmoidal(this.sigmaForHiddenLayer[j]);
        }
        
        
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.difWeightsInputHidden[i][j] = Params.getLearningRate() * valuesHidden[j] * this.inputLayer[i];
            }
        }
        
        this.recalculateWeights();
    }

    private void recalculateWeights() {
    	
        for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {
            for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
                this.weightsMatrixHiddenOutput[j][k] = this.weightsMatrixHiddenOutput[j][k] + this.difWeightsHiddenOutput[j][k];
            }
        }
        
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.weightsMatrixInputHidden[i][j] = this.weightsMatrixInputHidden[i][j] + this.difWeightsInputHidden[i][j];
            }
        }
    }

    private Double sigmoidal(Double value) {
        return 1 / (1 + (double) Math.exp(-value));
    }

    private Double derivatedSigmoidal(Double value) {
        return this.sigmoidal(value) * (1 - this.sigmoidal(value));
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
 		for(int i=0 ; i<this.fileController.getQuantityOfLinesTrainingDataset() ; i++) {
 			foudComma = false;
 			isLastNumber = true;
 			// Get current line
 			currentLine = this.fileController.getTrainingDatasetLine(i);
 			
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
 					this.storedInputLayerTraining[i][j] = (double) Character.getNumericValue(currentLine.charAt(j));
 					
 					//System.out.println("i:" + i);
 					//System.out.println("j:" + j);
 					//System.out.println("if=" + currentLine.charAt(j));					
 				}
 				else {
 					
 					if(isLastNumber) {
 						isLastNumber= false;
 						// Copy to stored input layer
 						this.storedInputLayerTraining[i][j] = (double) Character.getNumericValue(currentLine.charAt(j));
 						
 						//System.out.println("i:" + i);
 						//System.out.println("j:" + j);
 						//System.out.println("else=" + currentLine.charAt(j));	
 					}
 					else {
 						
 						// Copy to expected output
 						this.storedExpectedOutputTraining[i][expectedOutputIndex] = (double) Character.getNumericValue(expectedOutputString.charAt(expectedOutputIndex));
 						
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
 	
 	public void loadWeights() {
 		
 		this.weightsMatrixInputHidden = fileController.deserialize(1);
 		
 		this.weightsMatrixHiddenOutput = fileController.deserialize(2);
 	}
 }
