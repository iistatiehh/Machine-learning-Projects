import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import edu.stanford.nlp.process.Morphology;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import static edu.stanford.nlp.tagger.maxent.MaxentTagger.lemmatize;

public class FeatureFactory {
	/*
	public boolean isSimilar(String word1, String word2, int maxDistance) {
		return getLevenshteinDistance(word1, word2) <= maxDistance;
	}

	public int getLevenshteinDistance(String word1, String word2) {
		int lenWord1 = word1.length();
		int lenWord2 = word2.length();
		int[][] dp = new int[lenWord1 + 1][lenWord2 + 1];

		for (int i = 0; i <= lenWord1; i++) {
			for (int j = 0; j <= lenWord2; j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = Math.min(dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1),
							Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
				}
			}
		}
		return dp[lenWord1][lenWord2];
	}
	*/
	public FeatureFactory() {
	}

	public List<String> computeFeatures(List<String> words, String previousLabel, int position) {
		List<String> features = new ArrayList<>();
		String currentWord = words.get(position);

		// Baseline Features
		features.add("word=" + currentWord);
		features.add("prevLabel=" + previousLabel);
		features.add("word=" + currentWord + ", prevLabel=" + previousLabel);

		//all pos related features been removed because there were no real effect on imporving the model accuracy
		// while sending words sepretly to the stanford api
		//when i tried to send sentences the computaion was so high
// TODO: Add your features here

		lemmatizer lemmaTool = new lemmatizer();
		String lemmatizedWord = lemmaTool.lemmatize(currentWord);


		if (position < words.size() - 1) {
			String nextWord = words.get(position + 1);
			features.add("nextWord" + nextWord);
			if (position < words.size() - 2) {
				String nextNextWord = words.get(position + 2);
				features.add("wordbigram" + nextWord + "_" + nextNextWord);
			}
		}

		if (position < words.size() - 1 && (words.get(position + 1).equals("بن") || words.get(position + 1).equals("بنت"))) {
			features.add("nameWithIbnPattern");
		}

		if (currentWord.length() > 3) {
			features.add("longWord=true");
		}
		if (position < words.size() - 1 && words.get(position + 1).equals(":")) {
			features.add("followedByColon");
		}

		if (position == 0) {
			features.add("isFirstInSentence");
		}
		if (position == words.size() - 1) {
			features.add("isLastInSentence");
		}

		List<String> charTrigrams = Chargrams(currentWord, 3);
		for (String trigram : charTrigrams) {
			features.add("wordTrigram=" + trigram);
		}

		List<String> charUnigrams = Chargrams(currentWord, 1);
		for (String unigram : charUnigrams) {
			features.add("wordUnigram=" + unigram);
		}

		if (position > 0) {
			String prevWord = words.get(position - 1);
			if (prevWord.matches("(في|على|من|إلى|عن|مع|لـ|بـ|كـ)")) {
				features.add("precededByPreposition");
			}
		}
		if (arabicnameslexicon.isKnownName(currentWord)) {
			features.add("knownPersonName");
		}


		if (position < words.size() - 1 && (words.get(position + 1).equals("بن") || words.get(position + 1).equals("بنت"))) {
			features.add("nameWithIbnPattern");
		}

		if (lemmatizedWord.startsWith("ال")) {
			features.add("startsWithAl");
		}

		if (lemmatizedWord.endsWith("ي") || lemmatizedWord.endsWith("ان") || lemmatizedWord.endsWith("ون") || lemmatizedWord.endsWith("ية")) {
			features.add("endsWithCommonSuffix");
		}

		for (String prefix : Arrays.asList("حا", "عب", "بج", "نا", "عم", "بل", "بو", "نج", "جو", "فا", "أل", "شا", "مح", "حم", "را", "أن")) {
			if (lemmatizedWord.startsWith(prefix)) {
				features.add("startsWithCommonPrefix=" + prefix);
				break;
			}
		}

		for (String suffix : Arrays.asList("وش", "يق", "يز", "بو", "اك", "سف", "وظ", "دن", "سن", "عد", "ني", "بي", "ون", "ان", "في", "مر", "ول", "نا",
				"تو", "لح", "ري", "ود", "ين", "كي")) {
			if (lemmatizedWord.endsWith(suffix)) {
				features.add("endsWithCommonSuffix=" + suffix);
				break;
			}
		}

		return features;
}

	private List<String> Chargrams(String word, int n) {
		List<String> ngrams = new ArrayList<>();
		if (word.length() < n) {
			ngrams.add(word);
			return ngrams;
		}

		for (int i = 0; i <= word.length() - n; i++) {
			ngrams.add(word.substring(i, i + n));
		}
		return ngrams;
	}
/*
if (position > 0) {
			String prevWord = words.get(position - 1);
			//features.add("prevWord=" + prevWord);
			if (position > 2) {
				String ppPrevWord = words.get(position - 3);
				//features.add("prevtrigram=" + ppPrevWord + "_" + prevWord);
			}
		}
if (prevWord.matches("(والد|والدة|أم|أب|ابن|ابنة|زوج|زوجة|أخ|أخت|خال|خالة|عم|عمة|جد|جدة)")) {
				features.add("precededByFamilyRelation");
			}
if (prevWord.matches("(قال|ذكرت|كتب|أعلن|روى|أفاد|أوضح|أجاب|تابع|صرّح)")) {
				features.add("precededByReportingVerb");
			}
		String posTag = posStanfordLibImport.getPOSTag(lemmatizedWord);
		features.add("POS=" + posTag);
		features.add("wordShape=" + lemmatizedWord.replaceAll("[أ-ي]", "X").replaceAll("[0-9]", "D"));

		if (posTag.equals("NNP") || posTag.equals("NNPS")) {
			features.add("strongNameCandidate");
		}

		if (position < words.size() - 1) {
			String nextWord = words.get(position + 1);
			String nextPOS = posStanfordLibImport.getPOSTag(nextWord);
			if (nextPOS.equals("NNP") || nextPOS.equals("NNPS")) {
				features.add("nextIsProperNoun");
			}
			if (nextPOS.startsWith("V")) { // Verb starts with "V" in POS taggers
				features.add("nameFollowedByVerb");
			}
		}

		if (position > 0) {
			String prevPOS = posStanfordLibImport.getPOSTag(words.get(position - 1));
			if (prevPOS.equals("NNP") || prevPOS.equals("NNPS")) {
				features.add("prevIsProperNoun");
			}
			if ((posTag.equals("NNP") || posTag.equals("NNPS")) && (prevPOS.equals("NNP") || prevPOS.equals("NNPS"))) {
				features.add("partOfAcronym");
			}
		}

		if (position > 0) {
			String prevPOS = posStanfordLibImport.getPOSTag(words.get(position - 1));
			if ((posTag.equals("NNP") || posTag.equals("NNPS")) && (lemmatizedWord.equals("بن") || lemmatizedWord.equals("بنت"))) {
				features.add("nameBeforeIbnBint");
			}
		}
*/




		/*
currentWord
		if (position < words.size() - 1 && words.get(position + 1).matches("(رحمه الله|رضي الله عنه|عليه السلام|سلام الله عليه)")) {
			features.add("followedByHonorific");


			if (currentWord.endsWith("ي") || currentWord.endsWith("ان") || currentWord.endsWith("ون") || currentWord.endsWith("ية")) {
		features.add("endsWithCommonSuffix");
	}


		}
*/




/*

		if (arabicnameslexicon.isKnownName(currentWord)) {
			features.add("knownPersonName");
		}

		if (position < words.size() - 1 && (words.get(position + 1).equals("بن") || words.get(position + 1).equals("بنت"))) {
			features.add("nameWithIbnPattern");
		}

		if (currentWord.startsWith("ال")) {
			features.add("startsWithAl");
		}
/*
		if (currentWord.endsWith("ي") || currentWord.endsWith("ان") || currentWord.endsWith("ون") || currentWord.endsWith("ية")) {
			features.add("endsWithCommonSuffix");
		}

		for (String prefix : Arrays.asList("حا", "عب", "بج", "نا", "عم", "بل", "بو", "نج", "جو", "فا", "أل", "شا", "مح", "حم", "را", "أن")) {
		if (currentWord.startsWith(prefix)) {
			features.add("startsWithCommonPrefix=" + prefix);
			break;
		}
	}

		for (String suffix : Arrays.asList("وش", "يق", "يز", "بو", "اك", "سف", "وظ", "دن", "سن", "عد", "ني", "بي", "ون", "ان", "في", "مر", "ول", "نا",
			"تو", "لح", "ري", "ود", "ين", "كي")) {
		if (currentWord.endsWith(suffix)) {
			features.add("endsWithCommonSuffix=" + suffix);
			break;
		}
	}


/*

 */
/** Do not modify this method **/
    public List<Datum> readData(String filename) throws IOException {

	List<Datum> data = new ArrayList<Datum>();
	BufferedReader in = new BufferedReader(new FileReader(filename));

	for (String line = in.readLine(); line != null; line = in.readLine()) {
	    if (line.trim().length() == 0) {
		continue;
	    }
	    String[] bits = line.split("\\s+");
	    String word = bits[0];
	    String label = bits[1];

	    Datum datum = new Datum(word, label);
	    data.add(datum);
	}

	return data;
    }

    /** Do not modify this method **/
    public List<Datum> readTestData(String ch_aux) throws IOException {

	List<Datum> data = new ArrayList<Datum>();

	for (String line : ch_aux.split("\n")) {
	    if (line.trim().length() == 0) {
		continue;
	    }
	    String[] bits = line.split("\\s+");
	    String word = bits[0];
	    String label = bits[1];

	    Datum datum = new Datum(word, label);
	    data.add(datum);
	}

	return data;
    }

    /** Do not modify this method **/
    public List<Datum> setFeaturesTrain(List<Datum> data) {
	// this is so that the feature factory code doesn't accidentally use the
	// true label info
	List<Datum> newData = new ArrayList<Datum>();
	List<String> words = new ArrayList<String>();

	for (Datum datum : data) {
	    words.add(datum.word);
	}

	String previousLabel = "O";
	for (int i = 0; i < data.size(); i++) {
	    Datum datum = data.get(i);

	    Datum newDatum = new Datum(datum.word, datum.label);
	    newDatum.features = computeFeatures(words, previousLabel, i);
	    newDatum.previousLabel = previousLabel;
	    newData.add(newDatum);

	    previousLabel = datum.label;
	}

	return newData;
    }

    /** Do not modify this method **/
    public List<Datum> setFeaturesTest(List<Datum> data) {
	// this is so that the feature factory code doesn't accidentally use the
	// true label info
	List<Datum> newData = new ArrayList<Datum>();
	List<String> words = new ArrayList<String>();
	List<String> labels = new ArrayList<String>();
	Map<String, Integer> labelIndex = new HashMap<String, Integer>();

	for (Datum datum : data) {
	    words.add(datum.word);
	    if (labelIndex.containsKey(datum.label) == false) {
		labelIndex.put(datum.label, labels.size());
		labels.add(datum.label);
	    }
	}

	// compute features for all possible previous labels in advance for
	// Viterbi algorithm
	for (int i = 0; i < data.size(); i++) {
	    Datum datum = data.get(i);

	    if (i == 0) {
		String previousLabel = "O";
		datum.features = computeFeatures(words, previousLabel, i);

		Datum newDatum = new Datum(datum.word, datum.label);
		newDatum.features = computeFeatures(words, previousLabel, i);
		newDatum.previousLabel = previousLabel;
		newData.add(newDatum);

	    } else {
		for (String previousLabel : labels) {
		    datum.features = computeFeatures(words, previousLabel, i);

		    Datum newDatum = new Datum(datum.word, datum.label);
		    newDatum.features = computeFeatures(words, previousLabel, i);
		    newDatum.previousLabel = previousLabel;
		    newData.add(newDatum);
		}
	    }

	}

	return newData;
    }

    /** Do not modify this method **/
    public void writeData(List<Datum> data, String filename)
	throws IOException {


	FileWriter file = new FileWriter(filename + ".json", false);

	       
	for (int i = 0; i < data.size(); i++) {
	    try {
		JSONObject obj = new JSONObject();
		Datum datum = data.get(i);
		obj.put("_label", datum.label);
		obj.put("_word", base64encode(datum.word));
		obj.put("_prevLabel", datum.previousLabel);

		JSONObject featureObj = new JSONObject();

		List<String> features = datum.features;
		for (int j = 0; j < features.size(); j++) {
		    String feature = features.get(j).toString();
		    featureObj.put("_" + feature, feature);
		}
		obj.put("_features", featureObj);
		obj.write(file);
		file.append("\n");
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	file.close();
    }

    /** Do not modify this method **/
    private String base64encode(String str) {
	Base64 base = new Base64();
	byte[] strBytes = str.getBytes();
	byte[] encBytes = base.encode(strBytes);
	String encoded = new String(encBytes);
	return encoded;
    }

}
