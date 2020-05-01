package org.jqurantree.analysis.stemmer;


import AlKhalil2.morphology.result.model.Result;

public class Alkhalil2Stemmer {
    public static String stem(String text) throws Exception {
        AlKhalil2.util.constants.Static
                .analyzer = new AlKhalil2.morphology.analyzer.AnalyzerTokens();

        AlKhalil2.util.constants.Static.allResults = new java.util.HashMap();

        AlKhalil2.text.tokenization.Tokenization tokens = new AlKhalil2.text.tokenization.Tokenization();
        tokens.setTokenizationString(text);

        java.util.List l = new java.util.ArrayList();
        l.addAll(tokens.getTokens());
        java.util.Collections.sort(l);
        java.util.Iterator<String> it_normalized = l.iterator();
        while( it_normalized.hasNext() ) {
            String normalizedWord = it_normalized.next();
            java.util.List lst =  AlKhalil2.util.constants.Static.analyzer.analyzerToken(normalizedWord);
            if(lst!=null && lst.size()>0) {
                String wordroot = ((Result) lst.get(0)).getRoot();
                if(!wordroot.trim().equals("#")) {
                    return wordroot;
                }
            }
        }
        return text;
    }
    public static void main(String[] args) throws Exception {
        stem("العقد");
    }
}
