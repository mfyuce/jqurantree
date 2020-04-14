package org.jqurantree.analysis.stemmer.arabiccorpus;

import org.apache.maven.shared.utils.StringUtils;
import org.jqurantree.arabic.ArabicText;

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
        for (String line:
                Files.readAllLines(Paths.get("stemming/quranic-corpus-morphology-0.4.txt"), Charset.forName("UTF8"))) {
            String trim = line.trim();
            if(StringUtils.isNotBlank(trim) && !trim.startsWith("#") && !trim.startsWith("LOCATION")) {
                String[] items = line.split("\t",-1);
                CorpusItem parse = parse(items);
                Map<String, String> features = parse.getFeatures();
                if(features.containsKey(ROOT)){
                    String text = ArabicText.fromBuckwalter(parse.getText()).removeNonLetters().removeDiacritics().toUnicode();
                    if(!roots.containsKey(text)){
                        roots.put(text,ArabicText.fromBuckwalter(features.get(ROOT)).removeNonLetters().removeDiacritics().toUnicode());
                    }
                }
                corpus.add(parse);
            }
        }
    }
    public static String stem(String text){
        if(roots.containsKey(text)){
            return roots.get(text);
        }
        return text;
    }
}
