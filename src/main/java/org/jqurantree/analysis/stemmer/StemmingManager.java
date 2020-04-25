package org.jqurantree.analysis.stemmer;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.jqurantree.analysis.stemmer.arabiccorpus.QuranicCorpusStemmer;
import org.jqurantree.arabic.ArabicText;
import org.mortbay.util.StringUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StemmingManager {
    private static ISRI isriStemmer = new ISRI();
    private static ArabicStemmer luceneStemmer = new ArabicStemmer();
    private static KhodjaStemmer khodjaStemmer = new KhodjaStemmer();
    public static Map<StemmerType, String> stemmerFiles = new LinkedHashMap<>();
    public static Map<StemmerType, Map<ArabicText, String>> stemmerCahce = new LinkedHashMap<>();
    static {
        stemmerFiles.put(StemmerType.AlKhalil1_1, "stemming/roots_alkhalil1.1.csv");
        stemmerFiles.put(StemmerType.ISRI, "stemming/roots_isri.csv");
        stemmerFiles.put(StemmerType.KODJA, "stemming/roots_khodja.csv");
        stemmerFiles.put(StemmerType.Lucene, "stemming/roots_lucene.csv");
        stemmerFiles.put(StemmerType.QuranicCorpus, "stemming/roots_quraniccorpus.csv");
        for (Map.Entry<StemmerType, String> kv:stemmerFiles.entrySet()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(kv.getValue()));
                StemmerType key = kv.getKey();
                stemmerCahce.put(key, new LinkedHashMap<ArabicText, String>());
                Map<ArabicText, String> cache = stemmerCahce.get(key);
                for (String line:
                     lines) {
                    String[] words = StringUtils.split(line, ", ");
                    ArabicText nonRoot = ArabicText.fromUnicode(words[0]);
                    cache.putIfAbsent(nonRoot,words[1]);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static String findFromCache(StemmerType stemmerType, ArabicText text){
        text = text;
        return stemmerCahce.get(stemmerType).get(text);
    }
    public static String stem(StemmerType stemmerType, ArabicText text) throws Exception {
        text = text;
        String fromCache = findFromCache(stemmerType, text);
        if(StringUtils.isBlank(fromCache)) {
            switch (stemmerType) {
                case ISRI:
                    return isriStemmer.stem(text.toUnicode());
                case Lucene:
                    char[] toStem = text.toUnicode().toCharArray();
                    luceneStemmer.stem(toStem, toStem.length);
                    return new String(toStem);
                case AlKhalil1_1:
                    return AlkhalilStemmer.stem(text.toUnicode());
                case KODJA:
                    return khodjaStemmer.stemWord(text.toUnicode());
                case QuranicCorpus:
                    return QuranicCorpusStemmer.stem(text);
            }
            return text.toUnicode();
        }else{
            return fromCache;
        }
    }
}
