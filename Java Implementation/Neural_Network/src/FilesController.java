import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

// Read and write data in the files
public class FilesController {
	
	private String fullFileName;
	private int quantityOfLinesDataset;
	
	// Constructor
	public FilesController() throws IOException {
		
		// Full filename
		fullFileName = Params.getParamFilesDirectory();
		
		// Quantity of lines in dataset file
		this.quantityOfLinesDataset = getDatasetLinesQuantity();
	}

	public void readFile(NeuralNetwork NeuralNetwork, String fileName, boolean isTest) throws IOException{
		
		// Try to read file
		try {
			
			// Open file to read
			BufferedReader buffRead = new BufferedReader(new FileReader(fullFileName.concat(fileName)));
			
			// Read each line
			while(buffRead.ready()) {
				
				// Read one line
				String currentLine = buffRead.readLine();
				
				// TODO Send to neural network (verify if it's the best way)
				NeuralNetwork.handleInput(currentLine, isTest);
				
			}
			
			buffRead.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		//System.out.println(this.quantityOfLinesDataset);
	}
	
	private int getDatasetLinesQuantity() throws IOException {
		
		int linesQuantity = 0;
		
		BufferedReader buffRead = new BufferedReader(new FileReader(fullFileName.concat(Params.getParamTrainingFile())));
		
		try {
			while(buffRead.readLine() != null)
				linesQuantity++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buffRead.close();
		
		return linesQuantity;
	}
}
