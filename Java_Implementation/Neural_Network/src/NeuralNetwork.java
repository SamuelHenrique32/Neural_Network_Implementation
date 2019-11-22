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

    private Double[][] deltaWeightInputHidden;
    private Double[][] deltaWeightHiddenOutput;

    private Double[][] storedInputLayerTraining;
    private Double[][] expectedOutput;

    private Integer currentIteration;
    
    // File controller
 	private FileController fileController;
 	
 	private boolean isTesting;

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
     	this.storedInputLayerTraining = new Double[fileController.getQuantityOfLinesDataset()][Params.getInputNeuronsQuantity()];
     	// Expected values
     	this.expectedOutput = new Double[fileController.getQuantityOfLinesDataset()][Params.getOutputNeuronsQuantity()];

        this.weightsMatrixInputHidden = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()]; 
        this.weightsMatrixHiddenOutput = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
        this.deltaWeightInputHidden = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()];
        this.deltaWeightHiddenOutput = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];

        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.weightsMatrixInputHidden[i][j] = this.generateRandomWeight();
            }
        }
        for (int i = 0; i < Params.getHiddenNeuronsQuantity() + 1; i++) { 
            for (int j = 0; j < Params.getOutputNeuronsQuantity(); j++) {
                this.weightsMatrixHiddenOutput[i][j] = this.generateRandomWeight();
            }
        }
        
        // Reads dataset
        fileController.readDataset(Params.getTrainingFile());
        
        // Initialize the data structure with the dataset files
     	this.initializeTrainingDataset();
    }

    public Double train(int times) {
        Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
        Double erro = 0.0;
        if (this.storedInputLayerTraining != null && this.expectedOutput != null) {
            System.out.println("Treinando, por favor aguarde");
            Double err = 0.0;
            while (times > 0) {
                this.currentIteration++;
                for (int i = 0; i < this.storedInputLayerTraining.length; i++) {
                    System.arraycopy(this.storedInputLayerTraining[i], 0, inputLayer, 0, this.storedInputLayerTraining[i].length);
                    System.arraycopy(this.expectedOutput[i], 0, eO, 0, this.expectedOutput[i].length);

                    this.feedForward();
                    this.backPropagation(eO);
                }
                err = this.caclERR();
                System.out.println("Taxa de erro na iteracao " + this.currentIteration + ": " + err);
                times--;
                erro = err;
            } 
        } else {
            System.out.println("Sem dados para treinar");
        }
        
        System.out.println("\nA rede treinou " + Params.getMaxIterations() + " vezes!\n\n");
        
        return erro;        
    }

    private Double caclERR() {
        Double[] eO = new Double[Params.getOutputNeuronsQuantity()];
        Double err = 0.0;
        Double errTotal = 0.0;

        for (int i = 0; i < this.storedInputLayerTraining.length; i++) {
            System.arraycopy(this.storedInputLayerTraining[i], 0, inputLayer, 0, this.storedInputLayerTraining[i].length);
            System.arraycopy(this.expectedOutput[i], 0, eO, 0, this.expectedOutput[i].length);
            this.feedForward();
            for (int a = 0; a < Params.getOutputNeuronsQuantity(); a++) {
                err += Math.pow((eO[a] - this.outputLayer[a]), 2);
            }
            err /= Params.getOutputNeuronsQuantity();
            errTotal += err;
        }
        errTotal /= this.storedInputLayerTraining.length;
        return errTotal;
    }

    public void test(Double[] input) {
    	
    	this.isTesting = true;
    	
        System.arraycopy(input, 0, this.inputLayer, 0, Params.getInputNeuronsQuantity());
        this.feedForward();
    }

    private void feedForward() {
        this.setOutputY();
        this.setOutputZ();
    }

    private void setOutputY() {
    	
        for (int a = 0; a < Params.getHiddenNeuronsQuantity(); a++) {
            this.sigmaForHiddenLayer[a] = 0.0;
        }
        
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
            for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
                try {
                    this.sigmaForHiddenLayer[j] = this.sigmaForHiddenLayer[j] + this.inputLayer[i] * this.weightsMatrixInputHidden[i][j];
                } catch (Exception e) {
                    System.out.println("erro" + e);
                }

            }
        }
        
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
        	this.hiddenLayer[j] = this.sigmoid(this.sigmaForHiddenLayer[j]);
        }
    }

    private void setOutputZ() {
        for (int a = 0; a < Params.getOutputNeuronsQuantity(); a++) {
            this.sigmaForOutputLayer[a] = 0.0;
        }
        
        for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {
                this.sigmaForOutputLayer[k] = this.sigmaForOutputLayer[k] + this.hiddenLayer[j] * this.weightsMatrixHiddenOutput[j][k];
            }
        }
        
        for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
        	this.outputLayer[k] = this.sigmoid(this.sigmaForOutputLayer[k]);
        }
        
        if(this.isTesting) {
        	
        	for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
            	System.out.println(this.outputLayer[k] + "\n");
            }
        	
        	this.isTesting = false;
        }
    }    

    private void backPropagation(Double[] expectedOutput) {
    	
        Double[] fO = new Double[Params.getOutputNeuronsQuantity()];

        for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
            
        	fO[k] = (expectedOutput[k] - this.outputLayer[k]) * this.sigmoidDerivative(this.sigmaForOutputLayer[k]);
        }
        
        for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {//+bias weight
            for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
                this.deltaWeightHiddenOutput[j][k] = Params.getLearningRate() * fO[k] * this.hiddenLayer[j];
            }
        }
        Double[] fHNet = new Double[Params.getHiddenNeuronsQuantity()];
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
            fHNet[j] = 0.0;
            for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
                fHNet[j] = fHNet[j] + (fO[k] * this.weightsMatrixHiddenOutput[j][k]);
            }
        }
        Double[] fH = new Double[Params.getHiddenNeuronsQuantity()];
        for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
           
        	fH[j] = fHNet[j] * this.sigmoidDerivative(this.sigmaForHiddenLayer[j]);
        }
        
        
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.deltaWeightInputHidden[i][j] = Params.getLearningRate() * fH[j] * this.inputLayer[i];
            }
        }
        this.changeWeight();
    }

    private void changeWeight() {
        for (int j = 0; j < Params.getHiddenNeuronsQuantity() + 1; j++) {
            for (int k = 0; k < Params.getOutputNeuronsQuantity(); k++) {
                this.weightsMatrixHiddenOutput[j][k] = this.weightsMatrixHiddenOutput[j][k] + this.deltaWeightHiddenOutput[j][k];
            }
        }
        for (int i = 0; i < Params.getInputNeuronsQuantity() + 1; i++) {
            for (int j = 0; j < Params.getHiddenNeuronsQuantity(); j++) {
                this.weightsMatrixInputHidden[i][j] = this.weightsMatrixInputHidden[i][j] + this.deltaWeightInputHidden[i][j];
            }
        }
    }

    private Double sigmoid(Double value) {
        return 1 / (1 + (double) Math.exp(-value));
    }

    private Double sigmoidDerivative(Double value) {
        return this.sigmoid(value) * (1 - this.sigmoid(value));
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
}
