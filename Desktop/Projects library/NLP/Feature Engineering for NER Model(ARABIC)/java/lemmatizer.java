import edu.stanford.nlp.pipeline.*;

import java.util.Properties;

public class lemmatizer {

    private  StanfordCoreNLP pipeline;

    public lemmatizer() {
        // Initialize StanfordCoreNLP with properties for lemmatization
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public String lemmatize(String word) {
        // Create an empty annotation with the word to be lemmatized
        CoreDocument document = new CoreDocument(word);

        // Annotate the document (this will run the pipeline on the word)
        pipeline.annotate(document);

        // Return the lemmatized version of the word (the first token's lemma)
        return document.tokens().get(0).lemma();
    }

    public static void main(String[] args) {
        lemmatizer lemmatizer = new lemmatizer();
        String lemmatizedWord = lemmatizer.lemmatize("running");
        //System.out.println("Lemmatized word: " + lemmatizedWord);  // Output: run
    }
}
