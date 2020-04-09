package org.jqurantree.tools;

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.apache.maven.shared.utils.io.FileUtils;
import org.jqurantree.analysis.stemmer.ISRI;
import org.jqurantree.arabic.ArabicCharacter;
import org.jqurantree.arabic.ArabicText;
import org.jqurantree.orthography.Document;
import org.jqurantree.orthography.Token;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tools {
    public static void main(String[] args) throws IOException {

        Options options = new Options();

        Option inputFile = new Option("in", "input-file", true, "input file path");
        inputFile.setRequired(true);
        options.addOption(inputFile);

        Option outputFile = new Option("out", "output-file", true, "output file");
        outputFile.setRequired(false);
        options.addOption(outputFile);

        Option output = new Option("o", "operation", true, "operation 1=Buckwater to Arabic 2-Arabic to Buckwater [Default=1]");
        output.setRequired(false);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("jqurantree [input-text]", options);

            System.exit(1);
        }

        String inputFilePath = cmd.getOptionValue("in");
        String outputFilePath = cmd.getOptionValue("out");
        String operation = cmd.getOptionValue("operation");
        int operationNum = 1;
        if(StringUtils.isNotBlank(operation)){
            operationNum = Integer.parseInt(operation);
        }
        String inputText = null;
        String[] remaininArgs = cmd.getArgs();
        if(remaininArgs!=null && remaininArgs.length>0){
            inputText = remaininArgs[0];
        }else{
            if(StringUtils.isNotBlank(inputFilePath)){
                inputText = readFile(inputFilePath);
//                inputText = StringUtils.replace(inputText, "\r\n", System.lineSeparator());
//                inputText = StringUtils.replace(inputText, "\r", System.lineSeparator());
//                inputText = StringUtils.replace(inputText, "\n", System.lineSeparator());
            }
        }
        String outPut = null;
        if(StringUtils.isNotBlank(inputText)){
                switch (operationNum){
                    case 1:
                        outPut = ArabicText.fromBuckwalter(inputText).toUnicode();
                        break;
                    case 2:
                        outPut = ArabicText.fromUnicode(inputText).toBuckwalter();
                        break;
                    default:
                        break;
                }
        }
        if(StringUtils.isNotBlank(outputFilePath)){
            writeFile(outputFilePath, outPut);
        }else {
            System.out.println(outPut);
        }
//        System.out.println(inputText);
//        System.out.println(outPut);
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
    public static Map<String, String> getAllDistinctWordsAndRoots() throws IOException {
        Map<String, String> wordsAndRoots = new LinkedHashMap<String, String>();
        ISRI stem = new ISRI();

        for (Token w : Document.getTokens()) {
            String key = w.removeDiacritics().removeNonLetters().toUnicode();
            if (!wordsAndRoots.containsKey(key)) {
                wordsAndRoots.put(key, stem.stem(key));
            }
        }

        return wordsAndRoots;
    }
}
