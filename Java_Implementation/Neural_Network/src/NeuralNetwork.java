import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NeuralNetwork {

    private Double[] inputLayer;
    private Double[] hiddenLayer;
    private Double[] outputLayer;
    private Double[] outputLayerNormalized;

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
    private char[] storedExpectedOutputTrainingChar;

    private Integer currentIteration;
    
    // File controller
 	private FileController fileController;
 	
 	private boolean isTesting;
 	
 	// Store the index of the neuron with the biggest output value
 	private int biggestNeuronValueIndex;
 	
 	private int[][] confusionMatrix;
 	
 	// Verdadeiro positivo, falso positivo, verdadeiro negativo, falso negativo
 	private int [][] analysesValues;
 	
 	// Acuracia, erro, sensitividade, precisao, especificidade, ROC: TPR, FPR
 	private double [][] calculatedValues;

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
        this.outputLayerNormalized = new Double[Params.getOutputNeuronsQuantity()];
        // Set bias
        this.inputLayer[Params.getInputNeuronsQuantity()] = 1.0;
        this.hiddenLayer[Params.getHiddenNeuronsQuantity()] = 1.0;

        this.sigmaForHiddenLayer = new Double[Params.getHiddenNeuronsQuantity() + 1];
        this.sigmaForOutputLayer = new Double[Params.getOutputNeuronsQuantity()];
        
        // Stored input layer training
     	this.storedInputLayerTraining = new Double[fileController.getQuantityOfLinesTrainingDataset()][Params.getInputNeuronsQuantity()];
     	// Stored expected values
     	this.storedExpectedOutputTraining = new Double[fileController.getQuantityOfLinesTrainingDataset()][Params.getOutputNeuronsQuantity()];
     	// Stored expected values char
     	storedExpectedOutputTrainingChar = new char[fileController.getQuantityOfLinesTrainingDataset()];

        this.weightsMatrixInputHidden = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()]; 
        this.weightsMatrixHiddenOutput = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        
        this.difWeightsInputHidden = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()];
        this.difWeightsHiddenOutput = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        
        this.confusionMatrix = new int[Params.getOutputNeuronsQuantity()][Params.getOutputNeuronsQuantity()];
        
        this.analysesValues = new int[Params.getOutputNeuronsQuantity()][4];
        
        // Acuracia, erro, sensitividade, precisao, especificidade, ROC: TPR, FPR
     	this.calculatedValues = new double[Params.getOutputNeuronsQuantity()][7];
        
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
     	this.initializeTrainingDatasetChar();
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

    public void test() throws IOException {
    	
    	this.isTesting = true;
    	
    	Double[] testInputDouble = new Double[Params.getInputNeuronsQuantity()];
    	
    	//System.out.println(fileController.getTestDatasetLinesQuantity());
    	
    	// For each line in test dataset    	
    	for(int i=0 ; i<fileController.getTestDatasetLinesQuantity() ; i++) {
    		
    		this.isTesting = true;
    	
    		String testInput = fileController.getTestDatasetLine(i);
    		//System.out.println(testInput);
    		
    		// For each input neuron
    		for(int j=0 ; j<Params.getInputNeuronsQuantity() ; j++) {
    			
    			//System.out.println(testInput);    			
    			testInputDouble[j] = (double) (testInput.charAt(j)-48);
    			//System.out.print(testInputDouble[j]);    			
    		}
    		
    		System.arraycopy(testInputDouble, 0, this.inputLayer, 0, Params.getInputNeuronsQuantity());
    		
    		this.feedForward();
            
            int recognizedCharacterPos = verifyRecognizedCharacter();
        	// Verify if it's a valid character
        	if(recognizedCharacterPos != -1) {
        		
        		// Recognized
        		System.out.println("\nLinha de teste " + i + " - Reconheci o caracter " + this.storedExpectedOutputTrainingChar[recognizedCharacterPos]);
        		if(fileController.getTestDatasetExpectedValue(i) != null) {
        			System.out.println("Caractere esperado: " + fileController.getTestDatasetExpectedValue(i) + "\n");
        		} else {
        			System.out.println("Caractere esperado: o caractere nao pertence ao dataset \n");
        		}
        		
        		// Confusion matrix
        		if(fileController.getTestDatasetExpectedValue(i) != null) {
        			// What was recognized == What really is 
            		if(this.storedExpectedOutputTrainingChar[recognizedCharacterPos] == fileController.getTestDatasetExpectedValue(i).charAt(0)) {
            			int line = verifyIndexConfusionMatrix(this.storedExpectedOutputTrainingChar[recognizedCharacterPos]);
            			//System.out.println(verifyIndexConfusionMatrix(this.storedExpectedOutputTrainingChar[recognizedCharacterPos]));
            			// Verdadeiro positivo
            			
            			// Increment the value
            			confusionMatrix[line][line] += 1;        			
            		} else {
            			
            			// What was recognized
            			int line = verifyIndexConfusionMatrix(this.storedExpectedOutputTrainingChar[recognizedCharacterPos]);
            			
            			// What really is        			
            			int col = verifyIndexConfusionMatrix(fileController.getTestDatasetExpectedValue(i).charAt(0));
            			
            			this.confusionMatrix[line][col] += 1;
            		}	
        		}
        	} else {
    			
        		System.out.println("\nLinha de teste " + i + " - Nao reconhecido\n");
    		}
        	
        	//this.showConfusionMatrix();
    	}      
    	
    	this.showConfusionMatrix();
    	
    	this.analyseFinalValues();
    	
    	this.showAnalyseValues();
    	
    	this.calculateMetrics();
    	
    	this.showMetrics();
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
        	
        	//System.out.println("\nValores neuronios de saida:\n");
        	
        	for (int i = 0; i < Params.getOutputNeuronsQuantity(); i++) {
        		// Verify the output value
        		if(this.outputLayer[i] > biggestNeuronValue) {
        			
        			// Store value
        			biggestNeuronValue = this.outputLayer[i];
        			
        			// Store index
        			this.biggestNeuronValueIndex = i;
        		}
            	//System.out.println("Neuronio " + i + ": " + this.outputLayer[i] + "\n");
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
 			// Don't analize the last character
 			for(int j=0 ; j < currentLine.length() - 3; j++) {
 				
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
 				else if(isLastNumber) {
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
 	
 	// TODO Not the best way, merge with initializeTrainingDataset method
 	private void initializeTrainingDatasetChar() {
 		
 		String currentLine;
 		
 		// For each line
 		for(int i=0 ; i<this.fileController.getQuantityOfLinesTrainingDataset() ; i++) {
 			// Get current line
 			currentLine = this.fileController.getTrainingDatasetLine(i);
 			
 			storedExpectedOutputTrainingChar[i] = currentLine.charAt(currentLine.length()-1);
 		}
 			
 	}
 	
 	private int verifyRecognizedCharacter() {
 		
 		int pos = -1, charactersCounter = 0;;
 		
 		// Normalize
 		for(int i=0 ; i < Params.getOutputNeuronsQuantity() ; i++) {
 			this.outputLayerNormalized[i] = (double) 0;
 		}
 		
 		this.outputLayerNormalized[this.biggestNeuronValueIndex] = 1.0;
 		
 		/*System.out.println("Normalized:");
 		for(int i=0 ; i < Params.getOutputNeuronsQuantity() ; i++) {
 			System.out.println(this.outputLayerNormalized[i]);
 		}*/
 		
 		/*System.out.println("Expected:");
 		for(int i=0 ; i < Params.getOutputNeuronsQuantity() ; i++) {
 			System.out.println(this.storedExpectedOutputTraining[0][i]);
 		}*/
 		
 		 		
 		// For each line of training dataset
 		for(int i=0 ; i < fileController.getQuantityOfLinesTrainingDataset() ; i++) {
 			charactersCounter = 0;
 			for(int j=0 ; j<Params.getOutputNeuronsQuantity() ; j++) {
 				//System.out.println(this.outputLayerNormalized[j] + "--" + this.storedExpectedOutputTraining[i][j]);
 				if((double)this.outputLayerNormalized[j] == (double)this.storedExpectedOutputTraining[i][j]) {
 					charactersCounter++;
 				}
 				else {
 					break;
 				}
 			}
 			//System.out.println("--" + charactersCounter);
 			if(charactersCounter == Params.getOutputNeuronsQuantity()) {
 				pos = i;
 				break;
 			}
 		}
 		
 		this.biggestNeuronValueIndex = -1;
 		
 		// If reached here, the character was recognizes
 		return pos;
 	}
 	
 	void showConfusionMatrix() {
 		
 		System.out.println("\nMatriz de Confusao:\n");
 		
 		for(int i=0 ; i<Params.getOutputNeuronsQuantity(); i++) {
 			for(int j=0 ; j<Params.getOutputNeuronsQuantity(); j++) {
 	 			
 				System.out.print(this.confusionMatrix[i][j] + " ");
 	 		}
 			System.out.println();
 		} 		
 	}
 	
 	int verifyIndexConfusionMatrix(char character) {
 		
 		for(int i=0 ; i<fileController.getQuantityOfLinesTestDataset() ; i++) {
 			if(character == this.storedExpectedOutputTrainingChar[i]) {
 				// Return the pos correspondent in confusion matrix
 				for(int j=0 ; j<Params.getOutputNeuronsQuantity() ; j++) {
 					if(this.storedExpectedOutputTraining[i][j] == 1) {
 						// Pos to insert the value in confusion matrix
 						return j;
 					}
 				} 				
 			}
 		}
 		
 		return -1;
 	}
 	
 	void calculateVP(char character, int lineNumber) {
 		
 		int index = verifyIndexConfusionMatrix(character);
 		
 		this.analysesValues[lineNumber][0] = this.confusionMatrix[index][index];
 	}
 	
 	void calculateFP(char character, int lineNumber) {
 		
 		int col = verifyIndexConfusionMatrix(character);
 		
 		// Sum all the values of the col less the principal diagonal
 		for(int i=0 ; i<Params.getOutputNeuronsQuantity() ; i++) {
 			
 			// Not to the principal diagonal
 			if(i!=col) {
 				this.analysesValues[lineNumber][1] += this.confusionMatrix[i][col]; 				
 			}
 		}
 		//System.out.println(this.analysesValues[lineNumber][1]);
 	}
 	
 	void calculateVN(char character, int lineNumber) {
 		
 		// Sum all the VP less the current VP
 		for(int i=0 ; i<Params.getOutputNeuronsQuantity() ; i++) {
 			
 			if(i!=lineNumber) {
 				this.analysesValues[lineNumber][2] += this.analysesValues[i][0];
 				//System.out.println("Somar " + this.analysesValues[i][0]);
 			} 			
 		}
 	}
 	
 	void calculateFN(char character, int lineNumber) {
 		
 		int line = verifyIndexConfusionMatrix(character);
 		
 		// Sum all the values of the line less the principal diagonal
 	 	for(int i=0 ; i<Params.getOutputNeuronsQuantity() ; i++) {
 	 		
 	 		// Not to the principal diagonal
 			if(i!=line) {

 				this.analysesValues[lineNumber][3] += this.confusionMatrix[line][i];
 			}
 	 	} 	 	
 	 	//System.out.println(this.analysesValues[lineNumber][3]);
 	}
 	
 	void analyseFinalValues() {
 		
 		// For each character
 		for(int i=0 ; i<fileController.getQuantityOfLinesTrainingDataset() ; i++) {
 			
 			this.calculateVP(this.storedExpectedOutputTrainingChar[i], i);
 			
 			this.calculateFP(this.storedExpectedOutputTrainingChar[i], i);
 			
 			//this.calculateVN(this.storedExpectedOutputTrainingChar[i], i);
 			
 			this.calculateFN(this.storedExpectedOutputTrainingChar[i], i);
 			
 			//this.showAnalyseValues();
 		}
 		
 		// It's necessary to calculate the other values first
 	 	for(int i=0 ; i<fileController.getQuantityOfLinesTrainingDataset() ; i++) {
 	 		
 	 		this.calculateVN(this.storedExpectedOutputTrainingChar[i], i);
 	 	}
 	}
 	
 	void showAnalyseValues() {
 		
 		System.out.println("\nAnalyse Values:\n");
 		
 		for(int i=0 ; i<fileController.getQuantityOfLinesTrainingDataset(); i++) {
 			for(int j=0 ; j<4; j++) {
 	 			
 				System.out.print(this.analysesValues[i][j] + " ");
 	 		}
 			System.out.println();
 		} 	
 	}
 	
 	private void calculateMetrics() {
 		
 		for(int i=0 ; i<fileController.getQuantityOfLinesTrainingDataset() ; i++) {
 			
 			this.calculatedValues[i][0] = this.calculateAcuracy(this.analysesValues[i][0], this.analysesValues[i][2], this.analysesValues[i][1], this.analysesValues[i][3]);
 			
 			this.calculatedValues[i][1] = this.calculateErrorAcuracy(this.analysesValues[i][0], this.analysesValues[i][2], this.analysesValues[i][1], this.analysesValues[i][3]);
 			
 			this.calculatedValues[i][2] = this.calculateSensitivity(this.analysesValues[i][0], this.analysesValues[i][3]);
 			
 			this.calculatedValues[i][3] = this.calculatePrecision(this.analysesValues[i][0], this.analysesValues[i][1]);
 			
 			this.calculatedValues[i][4] = this.calculateSpecificity(this.analysesValues[i][2], this.analysesValues[i][1]);
 			
 			this.calculatedValues[i][5] = this.calculateRocTPR(this.analysesValues[i][0], this.analysesValues[i][3]);
 			
 			this.calculatedValues[i][6] = this.calculateRocFPR(this.analysesValues[i][2], this.analysesValues[i][1]);
 		}
 	}
 	
 	private void showMetrics() {
 		
 		System.out.println("\nMetricas:\n");
 		
 		System.out.println("Colunas: Acuracia, Erro, Sensitividade, Precisao, Especificidade, Roc TPR, Roc FPR");
 		System.out.println("Linhas: Caracteres\n");
 		
 		for(int i=0 ; i<Params.getOutputNeuronsQuantity(); i++) {
 			for(int j=0 ; j<6 ; j++) {
 	 			
 				System.out.print(this.calculatedValues[i][j] + " ");
 	 		}
 			System.out.println();
 		} 		
 		
 	}
 	
 	private double calculateAcuracy(double vp, double vn, double fp, double fn) {
 		
 		return(vp+vn)/(vp+fp+vn+fn);
 	}
 	
 	private double calculateErrorAcuracy(double vp, double vn, double fp, double fn) {
 		
 		return 1-(calculateAcuracy(vp, vn, fp, fn));
 	}
 	
 	private double calculateSensitivity(double vp, double fn) {
 		
 		double denominator = vp+fn;
 		
 		if(denominator>0) {
 			return (vp)/(denominator);
 		} else {
 			return 0;
 		}
 	}
 	
 	private double calculatePrecision(double vp, double fp) {
 		
 		double denominator = vp+fp;
 		
 		if(denominator>0) {
 			return (vp)/(denominator);	
 		} else {
 			return 0;
 		} 		
 	}
 	
 	private double calculateSpecificity(double vn, double fp) {
 		
 		return (vn)/(vn+fp);
 	}
 	
 	private double calculateRocTPR(double vp, double fn) {
 		
 		double denominator = vp+fn;
 		
 		if(denominator>0) {
 			return (vp)/(denominator);	
 		} else {
 			return 0;
 		} 		
 	}
 	
 	private double calculateRocFPR(double vn, double fp) {
 		
 		return (fp)/(vn+fp);
 	}
 	
 	public void loadWeights() {
 		
 		this.weightsMatrixInputHidden = fileController.deserialize(1);
 		
 		this.weightsMatrixHiddenOutput = fileController.deserialize(2);
 	}
 }
