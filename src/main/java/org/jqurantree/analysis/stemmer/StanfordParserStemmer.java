package org.jqurantree.analysis.stemmer;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

public class StanfordParserStemmer {
    static LexicalizedParser lp =null;
    static {
        String parserModel = "edu/stanford/nlp/models/lexparser/arabicFactored.ser.gz";

        lp = LexicalizedParser.loadModel(parserModel);
    }
    public static String stem(String text) throws Exception {


                return text;
    }
//
//    /**
//     * demoDP demonstrates turning a file into tokens and then parse
//     * trees.  Note that the trees are printed by calling pennPrint on
//     * the Tree object.  It is also possible to pass a PrintWriter to
//     * pennPrint if you want to capture the output.
//     * This code will work with any supported language.
//     */
//    public static void demoDP(LexicalizedParser lp, String filename) {
//        // This option shows loading, sentence-segmenting and tokenizing
//        // a file using DocumentPreprocessor.
//        TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a PennTreebankLanguagePack for English
//        GrammaticalStructureFactory gsf = null;
//        if (tlp.supportsGrammaticalStructures()) {
//            gsf = tlp.grammaticalStructureFactory();
//        }
//        // You could also create a tokenizer here (as below) and pass it
//        // to DocumentPreprocessor
//        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
//            Tree parse = lp.apply(sentence);
//            parse.pennPrint();
//            System.out.println();
//
//            if (gsf != null) {
//                GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
//                Collection tdl = gs.typedDependenciesCCprocessed();
//                System.out.println(tdl);
//                System.out.println();
//            }
//        }
//    }
//
//    /**
//     * demoAPI demonstrates other ways of calling the parser with
//     * already tokenized text, or in some cases, raw text that needs to
//     * be tokenized as a single sentence.  Output is handled with a
//     * TreePrint object.  Note that the options used when creating the
//     * TreePrint can determine what results to print out.  Once again,
//     * one can capture the output by passing a PrintWriter to
//     * TreePrint.printTree. This code is for English.
//     */
//    public static void demoAPI(LexicalizedParser lp, String word) {
//        // This option shows parsing a list of correctly tokenized words
//        String[] sent = { word };
//        List<CoreLabel> rawWords = SentenceUtils.toCoreLabelList(sent);
//        Tree parse = lp.apply(rawWords);
//        parse.pennPrint();
//        System.out.println();
//
//        // This option shows loading and using an explicit tokenizer
//        String sent2 = word;
//        TokenizerFactory<CoreLabel> tokenizerFactory =
//                PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
//        Tokenizer<CoreLabel> tok =
//                tokenizerFactory.getTokenizer(new StringReader(sent2));
//        List<CoreLabel> rawWords2 = tok.tokenize();
//        parse = lp.apply(rawWords2);
//        parse.pennPrint();
//    }
//
//    public static void main(String[] args) throws Exception {
////        stem("العقد");
////        demoAPI(lp,"العقد");
//        demoDP(lp,"a.txt");
//    }
}
