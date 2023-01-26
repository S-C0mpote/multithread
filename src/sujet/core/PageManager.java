package sujet.core;

import sujet.Tools;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PageManager {
    private static Set<String> visiteds = new HashSet<>();
    private static ConcurrentLinkedQueue<List<String>> queue = new ConcurrentLinkedQueue<>();
    public static PrintWriter printWriter = null;
    public static ExecutorService executorService = Executors.newFixedThreadPool(Tools.numberThreads());

    public static void startPageThread(List<String> links) {

        for (String link : links) {
            if(!visiteds.contains(link)) {
                try {
                    visiteds.add(link);
                    printWriter.println(link);
                    executorService.submit(() -> {
                        try {
                            Tools.ParsedPage p = Tools.parsePage(link);
                            if(!p.matches().isEmpty())
                                startPageThread(p.hrefs().stream().filter(e -> e.contains(Tools.getRegularExpression())).collect(Collectors.toList()));
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
