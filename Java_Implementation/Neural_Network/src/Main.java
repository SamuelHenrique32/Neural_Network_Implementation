import java.io.IOException;
import java.util.Scanner;

public class Main {
	
	private static boolean testAvailable = false;
	
	private static NeuralNetwork neuralNetwork;
	
	public static void showMenuOptions() {
		
		System.out.println("################### Rede Neural Artificial Para Reconhecimento De Caracteres ASCII ###################");
		System.out.println("Alunos: Samuel H. Dalmas e Maria Carolina\n\n");
		System.out.println("Opcoes:\n");
		System.out.println("1- Treinar Rede Neural");
		System.out.println("2- Testar Rede Neural");
		System.out.println("3- Carregar Pesos da Rede");
		System.out.print("Opcao escolhida: ");
	}
	
	public static void handleOption(int option) throws IOException {
		
		// TODO Do it in a better way
		while(option!=1 && option!=2 && option!=3) {
			
			System.out.println("Informe uma opcao valida!\n");
			
		}
		
		switch(option){
		
			case 1:				
				neuralNetwork.train(Params.getMaxIterations());	
				testAvailable = true;
			break;
	
			case 2:
				if(testAvailable) {
					String[] testInput = {"0","1","1","1","1","0","1","0","0","0","0","1","1","0","0","0","0","1","1","0","0","0","0","1","1","1","1","1","1","1","1","0","0","0","0","1","1","0","0","0","0","1","1","0","0","0","0","1"};
					Double[] testInputDouble = new Double[Params.getInputNeuronsQuantity()];
					for(int i=0 ; i<testInput.length ; i++) {
						testInputDouble[i] = Double.parseDouble(testInput[i]);
					}
					neuralNetwork.test(testInputDouble);	
				}
				else {
					System.out.println("\nA rede precisa treinar primeiro!\n\n");
				}
				
			break;
			
			case 3:
				
				neuralNetwork.loadWeights();
				
				System.out.println("Pesos carregados!\n");
				
				testAvailable = true;
				
			break;
			
			default:
				System.out.println("Opcao invalida!\n");
			break;
		}		
	}
	
	public static void menuHandler() throws IOException {
		
		showMenuOptions();
		
		Scanner sc  = new Scanner(System.in);
		int option = sc.nextInt();
		
		handleOption(option);
		
	}

	public static void main(String[] args) throws IOException {
		
		// Instantiate the neural network
		neuralNetwork = new NeuralNetwork();
		
		// Main loop
		while(true) {
			menuHandler();
		}			
	}
}