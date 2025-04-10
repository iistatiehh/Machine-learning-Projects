import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class posStanfordLibImport {
    private static MaxentTagger tagger;

    // Load the Arabic POS model
    static {
        // Use the correct Arabic POS model
        tagger = new MaxentTagger("C:/Users/asus/Downloads/stanford-tagger-4.2.0/stanford-postagger-full-2020-11-17/models/arabic.tagger");

    }

    public static String getPOSTag(String word) {
        String taggedWord = tagger.tagString(word);
        //System.out.println("Tagged word: " + taggedWord);

        // Split by the slash (/) instead of underscore (_)
        String[] parts = taggedWord.split("/");

        if (parts.length > 1) {
            return parts[1];  // Returns POS tag
        } else {
            // Handle edge cases (return "UNKNOWN" or any other default value)
            return "UNKNOWN";
        }
    }


}

