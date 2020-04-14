package org.jqurantree.analysis.stemmer.arabiccorpus;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CorpusItem {
    private  int surah;
    private  int ayah;
    private int word;
    private int letter;
    private String text;
    private String Tag;
    private Map<String,String> features;

    public CorpusItem(int surah, int ayah, int word, int letter, String text, String tag, Map<String, String> features) {
        this.surah = surah;
        this.ayah = ayah;
        this.word = word;
        this.letter = letter;
        this.text = text;
        Tag = tag;
        this.features = features;
    }

    public static CorpusItem parse(String[] items) {
        String[] location = StringUtils.split(items[0], ":");
        String[] featuresArray = StringUtils.split(items[3], "|");
        Map<String, String> features  = new LinkedHashMap<>();
        for (String item:featuresArray) {
            String[] ts = StringUtils.split(item, ":");
            features.put(ts[0],ts.length>1?ts[1]:"");
        }
        return new CorpusItem(Integer.parseInt(StringUtils.strip(location[0], "()")),
                Integer.parseInt( StringUtils.strip(location[1], "()")),
                Integer.parseInt(StringUtils.strip(location[2], "()")),
                Integer.parseInt(StringUtils.strip(location[3], "()")),
                items[1],
                items[2],
                features);
    }

    public int getSurah() {
        return surah;
    }

    public int getAyah() {
        return ayah;
    }

    public int getWord() {
        return word;
    }

    public int getLetter() {
        return letter;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return Tag;
    }

    public Map<String, String> getFeatures() {
        return features;
    }
}
