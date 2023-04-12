package sujet;

import sujet.core.PageManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;



public class WebGrep {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-cT --threads=100 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		// TODO Just do it!
		System.err.println("You must search recursively!");
		System.err.println("You must parallelize the application between " + Tools.numberThreads() + " threads!");



		//Initialisation du writer pour ecrire les resultats dans un fichier
		PageManager.bufferedWriter = new BufferedWriter(new FileWriter("assets/urlvisit.txt"));
		PageManager.bufferedWriter.write("");

		// Lancement de la fonction recursive
		PageManager.startPageThread(Tools.startingURL());

		//Tant que les 100 threads ne sont pas terminés et qu'il y a des pages à parser, on laisse finir, sinon on termine
		while (!PageManager.executorService.isTerminated()) {
			if (PageManager.executorService.getActiveCount() == 0 && PageManager.executorService.getQueue().size() == 0) {
				PageManager.executorService.shutdown();
				break;
			} else {
				System.out.println("Nombre de threads en cours:  " + PageManager.executorService.getActiveCount()
						+ " / Nombre d'url en attente de parsing: " +
						PageManager.executorService.getQueue().size());
				Thread.sleep(100);
			}
		}
		System.out.println("Ecriture dans le fichier");
		PageManager.good.forEach(e -> {
			System.out.println(e);
			try {
				PageManager.bufferedWriter.append(e + "\n");
				Thread.sleep(100);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		PageManager.bufferedWriter.flush();
		PageManager.bufferedWriter.close();
	}

	
}