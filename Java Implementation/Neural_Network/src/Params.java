// Application parameters
public class Params {
	
	//--------------------------------------------------------------------------------------------------------------------
	// Files parameters

	// Directory of files used
	private static String paramFilesDirectory = new String("/home/samuel/Desktop/Neural_Network/Java Implementation/Neural_Network/files/");
	
	// File used to training
	private static String paramTrainingFile = new String("datasets.txt");
	
	// File used to tests
    private static String paramTestFile = new String("test.txt");
    
    // File used to output
    private static String paramOutputFile = new String("output.txt");

    // Directory of files used
	public static String getParamFilesDirectory() {
		return paramFilesDirectory;
	}

	// File used to training
	public static String getParamTrainingFile() {
		return paramTrainingFile;
	}

	// File used to tests
	public static String getParamTestFile() {
		return paramTestFile;
	}

	// File used to output
	public static String getParamOutputFile() {
		return paramOutputFile;
	}
        
    //--------------------------------------------------------------------------------------------------------------------
    // Neural network parameters
	
	private static int paramsInputNeuronsQuantity = 48;
	
	private static int paramsHiddenNeuronsQuantity = 30;
	
	private static int paramsOutputNeuronsQuantity = 20;
	
	private static double paramsLearningRate = 0.5;
	
	private static int paramsMaxIterations = 2000;

	public static int getParamsInputNeuronsQuantity() {
		return paramsInputNeuronsQuantity;
	}

	public static int getParamsHiddenNeuronsQuantity() {
		return paramsHiddenNeuronsQuantity;
	}

	public static int getParamsOutputNeuronsQuantity() {
		return paramsOutputNeuronsQuantity;
	}

	public static double getParamsLearningRate() {
		return paramsLearningRate;
	}

	public static int getParamsMaxIterations() {
		return paramsMaxIterations;
	}	
}
