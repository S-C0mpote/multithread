package sujet.core;

import sujet.Tools;

import java.io.BufferedWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PageManager {

    //url visitées
    private static final Set<String> visiteds = new HashSet<>();
    public static BufferedWriter bufferedWriter = null;
    public static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(Tools.numberThreads());

    //Urls qui contiennent Nantes
    public static final Set<String> good = new HashSet<>();

    //On filtre les liens inutiles qui peuvent etre des redirection de page,
    // redirection vers une meme page sous une autre url...
    static Set<String> filter = new HashSet<>(Arrays.asList("\\.php", "\\.jpeg", "\\.jpg", "\\.png", "\\.gif", "\\.svg", "\\?"));

    //Regex
    static String regexPattern = String.join("|", filter);

    //Matching pattern
    static Pattern pattern = Pattern.compile(regexPattern);
    public static void startPageThread(List<String> links) {
        //pour chaque url
        for (String link : links) {
            //On accede à visiteds, qui est une zone critique
            synchronized (visiteds) {
                //si elle n'est pas visité on la parse
                if (!visiteds.contains(link)) {
                    try {
                        //ajout dans la liste
                        visiteds.add(link);
                        //On soumet un runnable qui parse, et si ca match, on fait la meme chose en recursion
                        //et on ajoute à la liste des urls valides
                        executorService.submit(() -> {
                            try {
                                Tools.ParsedPage p = Tools.parsePage(link);
                                if (!p.matches().isEmpty()) {
                                    synchronized (good){
                                        good.add(link);
                                    }
                                    //Je supprime tout ce qu'il y a après un # pour les redirections
                                    //et je filtre les urls à visiter
                                    startPageThread(p.hrefs().stream().map(e -> {
                                                int indexAnchor = e.indexOf("#");
                                                if(indexAnchor != -1){
                                                    e = e.substring(0,indexAnchor);
                                                }
                                                return e;
                                            }).filter(e -> !pattern.matcher(e).find())
                                            .collect(Collectors.toList()));
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
