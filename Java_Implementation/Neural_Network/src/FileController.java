import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

// Read and write data in the files
public class FileController {
	
	private String fullFileName;
	private int quantityOfLinesDataset;
	// Store all lines of dataset
	private String[] dataset;
	
	// Constructor
	public FileController() throws IOException {
		
		// Full filename
		this.fullFileName = Params.getFilesDirectory();
		
		// Quantity of lines in dataset file
		this.quantityOfLinesDataset = getDatasetLinesQuantity();
		
		// Store all lines of dataset
		this.dataset = new String[this.quantityOfLinesDataset];
	}
	
	public int getQuantityOfLinesDataset() {
		return quantityOfLinesDataset;
	}
	
	public void readDataset(String fileName) throws IOException{
		
		// Current line
		int nroCurrentLine = 0;
		
		// Try to read file
		try {
			
			// Open file to read
			BufferedReader buffRead = new BufferedReader(new FileReader(fullFileName.concat(fileName)));
			
			// For each line
			while(buffRead.ready()) {
				
				// Read one line
				String currentLine = buffRead.readLine();
				
				//System.out.println("Li: " + currentLine);
				
				// Store the line
				this.dataset[nroCurrentLine] = currentLine;
				
				nroCurrentLine++;
				
			}
			
			buffRead.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		//System.out.println(this.quantityOfLinesDataset);
	}
	
	// Get the quantity of lines in the dataset file
	private int getDatasetLinesQuantity() throws IOException {
		
		int linesQuantity = 0;
		
		BufferedReader buffRead = new BufferedReader(new FileReader(fullFileName.concat(Params.getTrainingFile())));
		
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
	
	// Returns a specific line of dataset
	public String getDatasetLine(int lineNumber) {
		
		if(lineNumber >=0 && lineNumber<=quantityOfLinesDataset)
			return this.dataset[lineNumber];
		else
			return null;
	}
}
