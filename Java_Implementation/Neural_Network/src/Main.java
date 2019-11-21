import java.io.IOException;
import java.util.Scanner;

public class Main {
	
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
		
		switch (option) {
		
			case 1:
				// Instanciate the neural network
				neuralNetwork = new NeuralNetwork();		
			break;
	
			case 2:
				neuralNetwork.test();
			break;
			
			default:
		
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
		
		menuHandler();
	}
}