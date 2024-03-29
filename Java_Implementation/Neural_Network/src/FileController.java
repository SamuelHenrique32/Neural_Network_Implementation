import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import serial.Deserializer;
import serial.Serializer;

// Read and write data in the files
public class FileController {
	
	private String directoryName;
	
	// Weights matrix serializer
	private Serializer serializer;
	
	// Weights matrix deserializer
	private Deserializer deserializer;
		
	private int quantityOfLinesTrainingDataset;
	private int quantityOfLinesTestDataset;
	
	// Store all lines of training dataset
	private String[] trainingDataset;
	
	// Store all lines of test dataset
	private String[] testDataset;
	
	// Constructor
	public FileController() throws IOException {
		
		// Full filename
		this.directoryName = Params.getFilesDirectory();
		
		// Quantity of lines in training dataset file
		this.quantityOfLinesTrainingDataset = getTrainingDatasetLinesQuantity();
		
		// Quantity of lines in test dataset file
		this.quantityOfLinesTestDataset = getTestDatasetLinesQuantity();
		
		// Store all lines of training dataset
		this.trainingDataset = new String[this.quantityOfLinesTrainingDataset];
		
		// Store all lines of test dataset
		this.testDataset = new String[this.quantityOfLinesTestDataset];
		
		serializer = new Serializer();
		
		deserializer = new Deserializer();
	}
	
	public int getQuantityOfLinesTrainingDataset() {
		return quantityOfLinesTrainingDataset;
	}
	
	public int getQuantityOfLinesTestDataset() {
		return quantityOfLinesTestDataset;
	}
	
	// File type 1 = training dataset
	// File type 2 = test dataset 
	public void readDataset(String fileName, int fileType) throws IOException{
		
		// Current line
		int nroCurrentLine = 0;
		
		// Try to read file
		try {
			
			// Open file to read
			BufferedReader buffRead = new BufferedReader(new FileReader(directoryName.concat(fileName)));
			
			// For each line
			while(buffRead.ready()) {
				
				// Read one line
				String currentLine = buffRead.readLine();
				
				//System.out.println("Li: " + currentLine);
				
				// Training dataset
				if(fileType == 1) {
					// Store the line
					this.trainingDataset[nroCurrentLine] = currentLine;
				// Test dataset
				} else if(fileType == 2) {
					this.testDataset[nroCurrentLine] = currentLine;
				} else {
					System.out.println("Tipo de arquivo invalido");
				}
				
				nroCurrentLine++;				
			}
			
			buffRead.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	// Get the quantity of lines in the training dataset file
	private int getTrainingDatasetLinesQuantity() throws IOException {
		
		int linesQuantity = 0;
		
		BufferedReader buffRead = new BufferedReader(new FileReader(directoryName.concat(Params.getTrainingFile())));
		
		try {
			while(buffRead.readLine() != null)
				linesQuantity++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buffRead.close();
		
		return linesQuantity;
	}
	
	// Get the quantity of lines in the test dataset file
	public int getTestDatasetLinesQuantity() throws IOException {
			
			int linesQuantity = 0;
			
			BufferedReader buffRead = new BufferedReader(new FileReader(directoryName.concat(Params.getTestFile())));
			
			try {
				while(buffRead.readLine() != null)
					linesQuantity++;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			buffRead.close();
			
			return linesQuantity;
	}
	
	// Returns a specific line of training dataset
	public String getTrainingDatasetLine(int lineNumber) {
		
		if(lineNumber >=0 && lineNumber<=quantityOfLinesTrainingDataset)
			return this.trainingDataset[lineNumber];
		else
			return null;
	}
	
	// Returns a specific line of test dataset
	public String getTestDatasetLine(int lineNumber) {
			
		if(lineNumber >=0 && lineNumber<=quantityOfLinesTestDataset) {
			
			String[] cutLine = this.testDataset[lineNumber].split(",");
			
			return cutLine[0];
		}
			
		else
			return null;
	}
	
	// Returns a specific expected value of test dataset
	public String getTestDatasetExpectedValue(int lineNumber) {
				
		if(lineNumber >=0 && lineNumber<=quantityOfLinesTestDataset) {
				
			String[] cutLine = this.testDataset[lineNumber].split(",");
			
			if(cutLine.length>=2) {
				return cutLine[1];	
			} else {
				return null;
			}			
		} else {
			return null;			
		}
	}
	
	public boolean serialize(int type, Double[][] matrix){
		
		try {
			
			// WeightsMatrixInputHidden
			if(type == 1) {
			
				serializer.serialize(directoryName + "/weightsMatrixInputHidden.ser", matrix);	
				
			// WeightsMatrixHiddenOutput	
			} else if(type == 2) {
				
				serializer.serialize(directoryName + "/weightsMatrixHiddenOutput.ser", matrix);
				
			} else {
				
				System.out.println("Invalid file type\n");
			}			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return false;
		}
		
		return true;		
	}
	
	public Double[][] deserialize(int type){
		
		try {
			
			// WeightsMatrixInputHidden
			if(type == 1) {
			
				Double[][] weightsMatrix = new Double[Params.getInputNeuronsQuantity() + 1][Params.getHiddenNeuronsQuantity()];
				
				weightsMatrix = (Double[][]) deserializer.deserializar(directoryName + "/weightsMatrixInputHidden.ser");
				
				return weightsMatrix;
				
			// WeightsMatrixHiddenOutput	
			} else if(type == 2) {
				
				Double[][] weightsMatrix = new Double[Params.getHiddenNeuronsQuantity() + 1][Params.getOutputNeuronsQuantity()];
				
				weightsMatrix = (Double[][]) deserializer.deserializar(directoryName + "/weightsMatrixHiddenOutput.ser");
				
				return weightsMatrix;
				
			} else {
				
				System.out.println("Invalid file type\n");
			}			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return null;		
	}
}