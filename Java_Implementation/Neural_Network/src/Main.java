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
	}
	
	public static void handleOption(int option) throws IOException {
		
		// TODO Do it in a better way
		while(option!=1 & option!=2) {
			
			System.out.println("Informe uma opcao valida!\n");
			
		}
		
		switch(option){
		
			case 1:
				// Instanciate the neural network
				neuralNetwork = new NeuralNetwork();	
				testAvailable = true;
			break;
	
			case 2:
				if(testAvailable) {
					neuralNetwork.test();	
				}
				else {
					System.out.println("\nA rede precisa treinar primeiro!\n\n");
				}
				
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
		
		// Main loop
		while(true) {
			menuHandler();
		}			
	}
}