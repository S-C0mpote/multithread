package sujet;
import sujet.core.PageManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


public class WebGrep {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-cT --threads=5 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		// TODO Just do it!
		System.err.println("You must search recursively!");
		System.err.println("You must parallelize the application between " + Tools.numberThreads() + " threads!");

		// Get the starting URL given in argument
		PageManager.printWriter = new PrintWriter("assets/urlvisited.txt", StandardCharsets.UTF_8);
		PageManager.startPageThread(Tools.startingURL());
	}
	
}