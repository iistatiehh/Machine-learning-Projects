import java.io.*;
import java.util.*;

public class arabicnameslexicon {
    private static Set<String> nameSet = new HashSet<>();

    static {
        loadNamesFromFile(//"arabic_names.txt"
                 "frequent_names.txt");
        //loadFrequentPersonWords("frequent_names.txt", 1000); // Load only top 500
    }

    private static void loadNamesFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                nameSet.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error loading name lexicon from " + filename + ": " + e.getMessage());
        }
    }

    private static void loadFrequentPersonWords(String filename, int maxWords) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < maxWords) {
                nameSet.add(line.trim());
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error loading frequent person words from " + filename + ": " + e.getMessage());
        }
    }

    public static boolean isKnownName(String word) {
        return nameSet.contains(word);
    }
}
