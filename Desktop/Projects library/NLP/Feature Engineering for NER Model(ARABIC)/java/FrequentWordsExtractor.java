import java.io.*;
import java.util.*;

public class FrequentWordsExtractor {
    public static void main(String[] args) {
        String inputFile = "C:/Users/asus/Downloads/hw2 (1)/data/train.txt";   // Your labeled dataset
        String outputFile = "frequent_names.txt"; // Output file for frequent names

        Map<String, Integer> nameCounts = new HashMap<>();

        // Read the training dataset
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println("Processing line: " + line); // Debugging

                String[] parts = line.split("\\s+"); // Space/tab-separated
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    String label = parts[1].trim().toUpperCase(); // Normalize label

                    if (label.contains("PER")) { // Matches "PERSON", "B-PERSON", etc.
                        nameCounts.put(word, nameCounts.getOrDefault(word, 0) + 1);
                        //System.out.println("Detected PERSON: " + word); // Debugging
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        // Debug: Print extracted names before sorting
       // System.out.println("Extracted Names: " + nameCounts);

        // Sort by frequency
        List<Map.Entry<String, Integer>> sortedNames = new ArrayList<>(nameCounts.entrySet());
        sortedNames.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        if (sortedNames.isEmpty()) {
            System.out.println("No names detected. Check dataset format or label case.");
            return;
        }

        // Save the most frequent names
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : sortedNames) {
                writer.write(entry.getKey());
                writer.newLine();
            }
            System.out.println("Frequent person names saved to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
