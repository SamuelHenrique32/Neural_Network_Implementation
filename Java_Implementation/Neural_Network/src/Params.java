import java.util.ArrayList;

// Application parameters
public class Params {
	
	//--------------------------------------------------------------------------------------------------------------------
	// Files parameters

	// Directory of files used
	private static String filesDirectory = new String("/home/samuel/Desktop/test/Neural_Network_Implementation/Java_Implementation/Neural_Network/files/");
	//private static String filesDirectory = new String("C:\\Users\\Samuel\\Desktop\\Neural_Network_Implementation\\Java_Implementation\\Neural_Network\\files\\");
	
	// File used to training
	private static String trainingFile = new String("training_dataset.txt");
	
	// File used to tests
    private static String testFile = new String("test_dataset.txt");
    
    // File used to output
    private static String outputFile = new String("output.txt");

    // Directory of files used
	public static String getFilesDirectory() {
		return filesDirectory;
	}

	// File used to training
	public static String getTrainingFile() {
		return trainingFile;
	}

	// File used to tests
	public static String getTestFile() {
		return testFile;
	}

	// File used to output
	public static String getOutputFile() {
		return outputFile;
	}
        
    //--------------------------------------------------------------------------------------------------------------------
    // Neural network parameters
	
	private static int inputNeuronsQuantity = 48;
	
	private static int hiddenNeuronsQuantity = 20;
	
	private static int outputNeuronsQuantity = 36;
	
	private static double learningRate = 0.6;
	
	private static int maxIterations = 2000;
	
	private static int expectedOutputSize = 36;

	public static int getInputNeuronsQuantity() {
		return inputNeuronsQuantity;
	}

	public static int getHiddenNeuronsQuantity() {
		return hiddenNeuronsQuantity;
	}

	public static int getOutputNeuronsQuantity() {
		return outputNeuronsQuantity;
	}

	public static double getLearningRate() {
		return learningRate;
	}

	public static int getMaxIterations() {
		return maxIterations;
	}

	public static int getExpectedOutputSize() {
		return expectedOutputSize;
	}
	
	//--------------------------------------------------------------------------------------------------------------------
    // Menu parameters
	
	// Menu options
	public static int[] menuOptions = {1,2};
	//public static ArrayList<Integer> menuOptions = new ArrayList<Integer>();
	
}
