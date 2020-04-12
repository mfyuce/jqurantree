package org.jqurantree.tools;

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.apache.maven.shared.utils.io.FileUtils;
import org.jqurantree.analysis.AnalysisTable;
import org.jqurantree.analysis.stemmer.AlkhalilStemmer;
import org.jqurantree.analysis.stemmer.ISRI;
import org.jqurantree.analysis.stemmer.StemmerType;
import org.jqurantree.analysis.stemmer.StemmingManager;
import org.jqurantree.arabic.ArabicCharacter;
import org.jqurantree.arabic.ArabicText;
import org.jqurantree.arabic.encoding.EncodingType;
import org.jqurantree.orthography.Document;
import org.jqurantree.orthography.Token;
import org.jqurantree.search.SearchOptions;
import org.jqurantree.search.TokenSearch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Tools {
    public static void main(String[] args) throws IOException {

        Options options = new Options();

        Option inputFile = new Option("in", "input-file", true, "input file path");
        inputFile.setRequired(false);
        options.addOption(inputFile);

        Option inputFormat = new Option("if", "input-format", true, "input text format 1=Buckwalter 2-Arabic [Default =1] ");
        inputFile.setRequired(false);
        options.addOption(inputFormat);

        Option outputFile = new Option("out", "output-file", true, "output file");
        outputFile.setRequired(false);
        options.addOption(outputFile);

        Option outputFormat = new Option("of", "output-format", true, "output text format 1=Buckwalter 2-Arabic [Default =1] ");
        inputFile.setRequired(false);
        options.addOption(outputFormat);

        Option output = new Option("o", "operation", true, "operation 1=Diacritics Free Search 2-Exact Token Search 3-Root Search [Default=1]");
        output.setRequired(false);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        if(args.length==0){
            formatter.printHelp("jqurantree [input-text]", options);

            System.exit(1);
            return;
        }
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("jqurantree [input-text]", options);

            System.exit(1);
        }

        String inputFilePath = cmd.getOptionValue("in");
        String outputFilePath = cmd.getOptionValue("out");
        String inputFormatText = cmd.getOptionValue("if");
        String outputFormatText = cmd.getOptionValue("of");
        String operation = cmd.getOptionValue("operation");
        int operationNum = 1;
        int inputFormatNum = 1;
        int outputFormatNum = 1;
        if(StringUtils.isNotBlank(operation)){
            operationNum = Integer.parseInt(operation);
        }
        if(StringUtils.isNotBlank(inputFormatText)){
            inputFormatNum = Integer.parseInt(inputFormatText);
        }
        if(StringUtils.isNotBlank(outputFormatText)){
            outputFormatNum = Integer.parseInt(outputFormatText);
        }
        String inputTextIn = null;
        String[] remaininArgs = cmd.getArgs();
        if(remaininArgs!=null && remaininArgs.length>0){
            inputTextIn = remaininArgs[0];
        }else{
            if(StringUtils.isNotBlank(inputFilePath)){
                inputTextIn = readFile(inputFilePath);
//                inputText = StringUtils.replace(inputText, "\r\n", System.lineSeparator());
//                inputText = StringUtils.replace(inputText, "\r", System.lineSeparator());
//                inputText = StringUtils.replace(inputText, "\n", System.lineSeparator());
            }
        }
        ArabicText inputText = null;
        switch (inputFormatNum){
            case 1: //Buckwalter
            default:
                inputText = ArabicText.fromBuckwalter(inputTextIn);
                break;
            case 2: //Arabic
                inputText = ArabicText.fromUnicode(inputTextIn);
                break;
        }
        EncodingType outputEncodingType = EncodingType.Buckwalter;
        switch (outputFormatNum) {
            case 1: //Buckwalter
            default:
                outputEncodingType = EncodingType.Buckwalter;
                break;
            case 2: //Arabic
                outputEncodingType = EncodingType.Unicode;
                break;
        }
        String outPutText = null;
        boolean general_out = true;
        switch (operationNum){
            case 1:
            default:
                handleSearch(outputFilePath, inputText, SearchOptions.RemoveDiacritics,false,outputEncodingType);
                general_out = false;
                break;
            case 2:
                handleSearch(outputFilePath, inputText, null,false,outputEncodingType);
                general_out = false;
                break;
            case 3:
                handleSearch(outputFilePath, inputText, null,true,outputEncodingType);
                general_out = false;
                break;
        }

        if(general_out) {
            switch (outputFormatNum) {
                case 1: //Buckwalter
                default:
                    outPutText = inputText.toBuckwalter();
                    break;
                case 2: //Arabic
                    outPutText = inputText.toUnicode();
                    break;
            }
            if (StringUtils.isNotBlank(outputFilePath)) {
                writeFile(outputFilePath, outPutText);
            } else {
                System.out.println(outPutText);
            }
        }
//        System.out.println(inputText);
//        System.out.println(outPut);
    }

    private static void handleSearch(String outputFilePath,
                                     ArabicText inputText,
                                     SearchOptions options,
                                     boolean searchRoot,
                                     EncodingType outputEncodingType) throws UnsupportedEncodingException {
        AnalysisTable table = null;
        if(options != null && options.equals(SearchOptions.RemoveDiacritics)) {
            table = listAllReferences(StringUtils.split(inputText.removeDiacritics().toString(outputEncodingType), "\r\n\t, "),searchRoot,outputEncodingType);
        }else{
            table = listAllReferences(StringUtils.split(inputText.toString(outputEncodingType), "\r\n\t, "),searchRoot,outputEncodingType);
        }
        if(StringUtils.isNotBlank(outputFilePath)){
            table.writeFile(outputFilePath);
        }else{
            System.out.println(table);
        }
        System.out.println("Matches: " + table.getRowCount());
    }

    /**
     * @throws IOException
     */
    public static void csvWriter(String fileName, Map<String, String> map, boolean onlyKeys) throws IOException {
        File file = new File(fileName);
        if(file.exists()) {
            FileUtils.delete(file);
        }
        // Create a File and append if it already exists.
//        Writer writer = new FileWriter(file, true);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        if(!onlyKeys){
            writer.write("word,root" + System.lineSeparator());
        }
        for (Map.Entry<String, String> kv:map.entrySet()) {
            if(onlyKeys) {
                writer.write(kv.getKey() + System.lineSeparator());
            }else{
                writer.write(kv.getKey() + "," + kv.getValue() + System.lineSeparator());
            }
        }
        writer.flush();
    }
    public static void csvWriter(String fileName, Set<String> map) throws IOException {
        File file = new File(fileName);
        if(file.exists()) {
            FileUtils.delete(file);
        }
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        for (String kv:map) {
            writer.write(kv + System.lineSeparator());
        }
        writer.flush();
    }
    public static void csvWriter(String fileName, List<String[]> map) throws IOException {
        File file = new File(fileName);
        if(file.exists()) {
            FileUtils.delete(file);
        }
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        for (String[] kv:map) {
            writer.write(StringUtils.join(kv,",") + System.lineSeparator());
        }
        writer.flush();
    }
    public static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        if(!file.exists()) {
           return null;
        }
        return FileUtils.fileRead(file,"UTF8");
    }
    public static void  writeFile(String fileName, String value) throws IOException {
        File file = new File(fileName);
        if(file.exists()) {
            FileUtils.delete(file);
        }
        FileUtils.fileWrite(file,"UTF8", value);
    }

    public static Set<String> getAllDistinctLetters() throws IOException {
        Map<String, String> wordsAndRoots= new LinkedHashMap<String, String>();
        for (Token w: Document.getTokens()) {
            for (ArabicCharacter l: w.removeDiacritics().removeNonLetters()) {
                String key = l.toUnicode();
                if (!wordsAndRoots.containsKey(key)) {
                    wordsAndRoots.put(key, key);
                }
            }
        }

        return wordsAndRoots.keySet();
    }
    public static Map<String, String> getAllDistinctWordsAndRootsLLuceneStemmer() throws IOException {
        Map<String, String> wordsAndRoots = new LinkedHashMap<String, String>();
        ArabicStemmer stem = new ArabicStemmer();

        for (Token w : Document.getTokens()) {
            String key = w.removeDiacritics().removeNonLetters().toUnicode();
            if (!wordsAndRoots.containsKey(key)) {
                char[] toStem = key.toCharArray();
                stem.stem(toStem,toStem.length);
                wordsAndRoots.put(key, new String(toStem));
            }
        }

        return wordsAndRoots;
    }
    public static Map<String, String> getAllDistinctWordsAndRoots(StemmerType stemmerType) throws Exception {
        Map<String, String> wordsAndRoots = new LinkedHashMap<String, String>();
        for (Token w : Document.getTokens()) {
            String key = w.removeDiacritics().removeNonLetters().toUnicode();
            if (!wordsAndRoots.containsKey(key)) {
                wordsAndRoots.put(key, StemmingManager.stem(stemmerType, key));
            }
        }

        return wordsAndRoots;
    }
    public static AnalysisTable listAllReferences(String[] lst,
                                                  boolean searchRoot,
                                                  EncodingType outputEncodingType){
        TokenSearch search = new TokenSearch(outputEncodingType,
                SearchOptions.RemoveDiacritics);

        for (String item:lst) {
            if(searchRoot){
                search.findRoot(item,SearchOptions.RemoveDiacritics);
            }else {
                search.findSubstring(item);
            }
        }

        return search.getResults();
    }
}
