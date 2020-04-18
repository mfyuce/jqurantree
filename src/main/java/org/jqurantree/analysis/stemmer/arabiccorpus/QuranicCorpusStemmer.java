package org.jqurantree.analysis.stemmer.arabiccorpus;

import org.apache.maven.shared.utils.StringUtils;
import org.jqurantree.arabic.ArabicText;
import org.jqurantree.orthography.Document;
import org.jqurantree.orthography.Location;
import org.jqurantree.orthography.Token;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.jqurantree.analysis.stemmer.arabiccorpus.CorpusItem.parse;

public class QuranicCorpusStemmer {
    public static final String ROOT = "ROOT";
    public static List<CorpusItem> corpus = new ArrayList<>();
    public static Map<String,String> roots = new LinkedHashMap<>();
    static {
        try {
            loadCorpusFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadCorpusFile() throws IOException {
        String inBetween = null;
        String lastRoot = null;
        Location lastLocation = null;
        for (String line:Files.readAllLines(Paths.get("stemming/quranic-corpus-morphology-0.4.txt"), Charset.forName("UTF8"))) {
            String trim = line.trim();
            if(StringUtils.isNotBlank(trim) && !trim.startsWith("#") && !trim.startsWith("LOCATION")) {
                String[] items = line.split("\t",-1);
                CorpusItem parsed = parse(items);
                Location currentLocation = parsed.getLocation();
                if(lastLocation !=null && !lastLocation.equals(currentLocation)){
                    // unrootable word
                    addNewRoot(inBetween, ArabicText.fromUnicode(inBetween).toBuckwalter());

                    inBetween=null;
                    lastRoot = null;
                    lastLocation = null;
                }
                Token token = Document.getToken(currentLocation);
                Map<String, String> features = parsed.getFeatures();
                boolean canCheckForRoot = true;
                String text = ArabicText.fromBuckwalter(parsed.getText()).
                        removeDiacritics().removeNonLetters().toUnicode();
                boolean lastRootExists = StringUtils.isNotBlank(lastRoot);
                if (features.containsKey(ROOT) || lastRootExists) {
                    String root = lastRootExists ? lastRoot : features.get(ROOT);
                    String currentToken = token.removeDiacritics().removeNonLetters().toUnicode();
                    if (!text.equals(currentToken)) {
                        if (StringUtils.isNotBlank(inBetween)) {
                            inBetween = inBetween + text;
                            if (!currentToken.equals(inBetween)) {
                                canCheckForRoot = false;
                                lastRoot = root;
                                lastLocation = currentLocation;
                            } else {
                                text = inBetween;
                                inBetween = null;
                                lastRoot = null;
                                lastLocation=null;
                            }
                        } else {
                            inBetween = text;
                            lastRoot = root;
                            canCheckForRoot = false;
                            lastLocation=currentLocation;
                        }
                    } else {
                        inBetween = null;
                        lastRoot = null;
                        lastLocation=null;
                    }
                    if (canCheckForRoot) {
                        addNewRoot(text, root);
                    }
                } else {
                    inBetween = String.format("%s%s", StringUtils.isNotBlank(inBetween) ? inBetween : "", text);
                    lastLocation=currentLocation;
                }
                corpus.add(parsed);
            }
        }
    }

    private static void addNewRoot(String text, String root) {
        if (!roots.containsKey(text)) {
            roots.put(text, ArabicText.fromBuckwalter(root).removeDiacritics().removeNonLetters().toUnicode());
        }
    }

    public static String stem(String text){
        if(roots.containsKey(text)){
            return roots.get(text);
        }
        return text;
    }
}
