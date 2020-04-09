package org.jqurantree.analysis.stemmer;

import java.util.ArrayList;
import java.util.regex.*;
public class ISRI{

    // length three prefixes
    private String[] p3 = {"\u0643\u0627\u0644", "\u0628\u0627\u0644",
            "\u0648\u0644\u0644", "\u0648\u0627\u0644"};

    // length two prefixes
    private String[] p2 = {"\u0627\u0644", "\u0644\u0644"};

    // length one prefixes
    private String[] p1 = {"\u0644", "\u0628", "\u0641", "\u0633", "\u0648",
            "\u064a", "\u062a", "\u0646", "\u0627"};

    // length three suffixes
    private String[] s3 = {"\u062a\u0645\u0644", "\u0647\u0645\u0644",
            "\u062a\u0627\u0646", "\u062a\u064a\u0646",
            "\u0643\u0645\u0644"};

    // length two suffixes
    private String[]  s2 = {"\u0648\u0646", "\u0627\u062a", "\u0627\u0646",
            "\u064a\u0646", "\u062a\u0646", "\u0643\u0645",
            "\u0647\u0646", "\u0646\u0627", "\u064a\u0627",
            "\u0647\u0627", "\u062a\u0645", "\u0643\u0646",
            "\u0646\u064a", "\u0648\u0627", "\u0645\u0627",
            "\u0647\u0645"};

    // length one suffixes
    private String[] s1 = {"\u0629", "\u0647", "\u064a", "\u0643", "\u062a",
            "\u0627", "\u0646"};

    // groups of length four patterns
    private String[][]  pr4 = { {"\u0645"},  {"\u0627"},
            {"\u0627", "\u0648", "\u064A"},  {"\u0629"}};

    // Groups of length five patterns and length three roots
    private String[][] pr53 = { {"\u0627", "\u062a"},
            {"\u0627", "\u064a", "\u0648"},
            {"\u0627", "\u062a", "\u0645"},
            {"\u0645", "\u064a", "\u062a"},
            {"\u0645", "\u062a"},
            {"\u0627", "\u0648"},
            {"\u0627", "\u0645"}};

    private Pattern re_short_vowels = Pattern.compile("[\u064B-\u0652]",Pattern.UNICODE_CHARACTER_CLASS);
    //the following is not used in the nltk implementation, but exists anyway ..
    //  private Pattern re_hamza = Pattern.compile("[\u0621\u0624\u0626]",Pattern.UNICODE_CHARACTER_CLASS);
    private Pattern re_initial_hamza = Pattern.compile("^[\u0622\u0623\u0625]",Pattern.UNICODE_CHARACTER_CLASS);

    private String[] stop_words = {"\u064a\u0643\u0648\u0646",
            "\u0648\u0644\u064a\u0633",
            "\u0648\u0643\u0627\u0646",
            "\u0643\u0630\u0644\u0643",
            "\u0627\u0644\u062a\u064a",
            "\u0648\u0628\u064a\u0646",
            "\u0639\u0644\u064a\u0647\u0627",
            "\u0645\u0633\u0627\u0621",
            "\u0627\u0644\u0630\u064a",
            "\u0648\u0643\u0627\u0646\u062a",
            "\u0648\u0644\u0643\u0646",
            "\u0648\u0627\u0644\u062a\u064a",
            "\u062a\u0643\u0648\u0646",
            "\u0627\u0644\u064a\u0648\u0645",
            "\u0627\u0644\u0644\u0630\u064a\u0646",
            "\u0639\u0644\u064a\u0647",
            "\u0643\u0627\u0646\u062a",
            "\u0644\u0630\u0644\u0643",
            "\u0623\u0645\u0627\u0645",
            "\u0647\u0646\u0627\u0643",
            "\u0645\u0646\u0647\u0627",
            "\u0645\u0627\u0632\u0627\u0644",
            "\u0644\u0627\u0632\u0627\u0644",
            "\u0644\u0627\u064a\u0632\u0627\u0644",
            "\u0645\u0627\u064a\u0632\u0627\u0644",
            "\u0627\u0635\u0628\u062d",
            "\u0623\u0635\u0628\u062d",
            "\u0623\u0645\u0633\u0649",
            "\u0627\u0645\u0633\u0649",
            "\u0623\u0636\u062d\u0649",
            "\u0627\u0636\u062d\u0649",
            "\u0645\u0627\u0628\u0631\u062d",
            "\u0645\u0627\u0641\u062a\u0626",
            "\u0645\u0627\u0627\u0646\u0641\u0643",
            "\u0644\u0627\u0633\u064a\u0645\u0627",
            "\u0648\u0644\u0627\u064a\u0632\u0627\u0644",
            "\u0627\u0644\u062d\u0627\u0644\u064a",
            "\u0627\u0644\u064a\u0647\u0627",
            "\u0627\u0644\u0630\u064a\u0646",
            "\u0641\u0627\u0646\u0647",
            "\u0648\u0627\u0644\u0630\u064a",
            "\u0648\u0647\u0630\u0627",
            "\u0644\u0647\u0630\u0627",
            "\u0641\u0643\u0627\u0646",
            "\u0633\u062a\u0643\u0648\u0646",
            "\u0627\u0644\u064a\u0647",
            "\u064a\u0645\u0643\u0646",
            "\u0628\u0647\u0630\u0627",
            "\u0627\u0644\u0630\u0649"};

    private boolean search (String s , String []Arr)
    {
        for (String str:Arr){
            if (s.equals(str))
                return true;
        }
        return false;
    }

    private String norm(String  word,int num){

        // normalization:
        //num=1  normalize diacritics
        //num=2  normalize initial hamza
        //num=3  both 1&2

        switch (num) {
            case 3:
                word=word.replaceAll(re_short_vowels.pattern(), "");

            case 2:
                word=word.replaceAll(re_initial_hamza.pattern(),"\u0627");
                break;

            case 1:
                word=word.replaceAll(re_short_vowels.pattern(), "");
                break;
        }

        return word;
    }

    private String pre32(String word){
        // remove length three and length two prefixes in this order
        if (word.length()>=6)
            for (String s:p3)
                if (word.startsWith(s))
                    return word.substring(3);
        if (word.length() >= 5)
            for (String s:p2)
                if (word.startsWith(s))
                    return word.substring(2);
        return word;
    }

    private String suf32(String word){
        //remove length three and length two suffixes in this order
        if (word.length()>=6)
            for (String s:s3)
                if (word.endsWith(s))
                    return word.substring(0, word.length()-3);
        if (word.length()>=5)
            for (String s:s2)
                if (word.endsWith(s))
                    return word.substring(0,word.length()-2);
        return word;
    }

    private String waw(String word){
        //remove connective ‘و’ if it precedes a word beginning with ‘و’
        if (word.length()>=4 && word.startsWith("\u0648\u0648"))
            word = word.substring(1);
        return word;
    }

    private String pro_w4(String word){
        //process length four patterns and extract length three roots

        if (word.startsWith(pr4[0][0])){ 	// مفعل
            word = word.substring(1);
        }
        else if (word.startsWith(pr4[1][0],1)){ // فاعل
            word = word.substring(0,1) + word.substring(2);
        }
        else if (word.startsWith(pr4[2][0],2)
                || word.startsWith(pr4[2][1],2)
                || word.startsWith(pr4[2][2],2)){	// فعال - فعول - فعيل
            word = word.substring(0,2) + word.charAt(3);
        }
        else if (word.endsWith(pr4[3][0])){ 	// فعلة
            word = word.substring(0, word.length()-1);
        }

        else{
            word = suf1(word);      // do - normalize short sufix
            if (word.length()==4)
                word = pre1(word);  // do - normalize short prefix
        }

        return word;
    }

    private String pro_w53(String word){
        //process length five patterns and extract length three roots
        if ( search(Character.toString(word.charAt(2)),pr53[0]) && word.charAt(0) == '\u0627')    // افتعل - افاعل
            word = word.charAt(1)+word.substring(3);
        else if ( search(Character.toString(word.charAt(3)),pr53[1]) && word.charAt(0) == '\u0645') // مفعول - مفعال - مفعيل
            word = word.substring(1,3) + word.charAt(4);
        else if ( search(Character.toString(word.charAt(0)),pr53[2]) && word.charAt(4) == '\u0629') // مفعلة - تفعلة - افعلة
            word = word.substring(1,4);
        else if ( search(Character.toString(word.charAt(0)),pr53[3]) && word.charAt(2) == '\u062a') // مفتعل - يفتعل - تفتعل
            word = word.charAt(1) + word.substring(3);
        else if ( search(Character.toString(word.charAt(0)),pr53[4]) && word.charAt(2) == '\u0627') // مفاعل - تفاعل
            word = word.charAt(1) + word.substring(3);
        else if ( search(Character.toString(word.charAt(2)),pr53[5]) && word.charAt(4) == '\u0629') // فعولة - فعالة
            word = word.substring(0,2) + word.charAt(3);
        else if ( search(Character.toString(word.charAt(0)),pr53[6]) && word.charAt(1) == '\u0646') // انفعل - منفعل
            word = word.substring(2);
        else if ( word.charAt(3) == '\u0627' && word.charAt(0) == '\u0627')     // افعال
            word = word.substring(1,3) + word.charAt(4);
        else if ( word.charAt(4) == '\u0646' && word.charAt(3) == '\u0627')     // فعلان
            word = word.substring(0, 3);
        else if ( word.charAt(3) == '\u064a' && word.charAt(0) == '\u062a')     // تفعيل
            word = word.substring(1, 3) + word.charAt(4);
        else if ( word.charAt(3) == '\u0648' && word.charAt(1) == '\u0627')     // فاعول
            word = word.substring(0,1) + word.charAt(2) + word.charAt(4);
        else if ( word.charAt(2) == '\u0627' && word.charAt(1) == '\u0648')     // فواعل
            word = word.charAt(0) + word.substring(3);
        else if ( word.charAt(3) == '\u0626' && word.charAt(2) == '\u0627')     // فعائل
            word = word.substring(0,2) + word.charAt(4);
        else if ( word.charAt(4) == '\u0629' && word.charAt(1) == '\u0627')     // فاعلة
            word = word.charAt(0) + word.substring(2,4);
        else if ( word.charAt(4) == '\u064a' && word.charAt(2) == '\u0627')     // فعالي
            word = word.substring(0,2) + word.charAt(3);
        else{
            word =  suf1(word);      // do - normalize short sufix
            if (word.length() == 5)
                word =  pre1(word);  // do - normalize short prefix
        }

        return word;
    }

    private String pro_w54(String word){
        //process length five patterns and extract length four roots
        if (search(Character.toString(word.charAt(0)),pr53[2]))  // تفعلل - افعلل - مفعلل
            word = word.substring(1);
        else if (word.charAt(4)=='\u0629')    // فعللة
            word = word.substring(0,4);
        else if (word.charAt(2) == '\u0627')    // فعالل
            word = word.substring(0,2) + word.substring(3);
        return word;
    }

    private String end_w5(String word){
        // ending step (word of length five)
        if (word.length()==4)
            word = pro_w4(word);
        else if(word.length()==5)
            word = pro_w54(word);
        return word;
    }

    private String pro_w6(String word){
        //process length six patterns and extract length three roots
        if (word.startsWith("\u0627\u0633\u062a") || word.startsWith("\u0645\u0633\u062a"))  // مستفعل - استفعل
            word = word.substring(3);
        else if(word.charAt(0) == '\u0645' && word.charAt(3) == '\u0627' && word.charAt(5) == '\u0629') // مفعالة
            word = word.substring(1,3) + word.charAt(4);
        else if (word.charAt(0) == '\u0627' && word.charAt(2) == '\u062a' && word.charAt(4) == '\u0627') // افتعال
            word = word.substring(1,2) + word.charAt(3) + word.charAt(5);
        else if (word.charAt(0) == '\u0627' && word.charAt(3) == '\u0648' && word.charAt(2) == word.charAt(4)) // افعوعل
            word = word.charAt(1) + word.substring(4);
        else if (word.charAt(0) == '\u062a' && word.charAt(2) == '\u0627' && word.charAt(4) == '\u064a') // تفاعيل   new pattern
            word = word.substring(1,2) + word.charAt(3) + word.charAt(5);
        else
        {
            word = suf1(word);      // do - normalize short sufix
            if (word.length()==6)
                word = pre1(word);  // do - normalize short prefix
        }

        return word;
    }

    private String pro_w64(String word){
        //process length six patterns && extract length four roots
        if (word.charAt(0) == '\u0627' && word.charAt(4) == '\u0627')  // افعلال
            word = word.substring(1,4) + word.charAt(5);
        else if (word.startsWith("\u0645\u062a"))            // متفعلل
            word = word.substring(2);
        return word;
    }

    private String end_w6(String word){
        //ending step (word of length six)
        if (word.length()==5){
            word =  pro_w53(word);
            word =  end_w5(word);
        }
        else if (word.length()==6)
            word =  pro_w64(word);
        return word;
    }

    private String suf1(String word){
        //normalize short sufix
        for (String s:s1)
            if (word.endsWith(s))
                return word.substring(0,word.length()-1);
        return word ;
    }

    private String pre1(String word){
        //normalize short prefix
        for (String s :  p1)
            if (word.startsWith(s))
                return word.substring(1);
        return word;

    }

    public String stem(String token){
        //         Stemming a word token using the ISRI stemmer.

        token =  norm(token, 1);   // remove diacritics which representing Arabic short vowels
        for(String s:stop_words)
            if (s.equals(token))
                return token;              // exclude stop words from being processed

        token =  pre32(token);     // remove length three and length two prefixes in this order
        token =  suf32(token);     // remove length three and length two suffixes in this order
        token =  waw(token);       // remove connective ‘و’ if it precedes a word beginning with ‘و’
        token =  norm(token, 2);   // normalize initial hamza to bare alif

        // if 4 <= word length <= 7, then stem; otherwise, no stemming

        if (token.length()==4)           // length 4 word
            token =  pro_w4(token);
        else if (token.length()==5){	// length 5 word
            token =  pro_w53(token);
            token =  end_w5(token);
        }

        else if (token.length()==6){	// length 6 word
            token =  pro_w6(token);
            token =  end_w6(token);
        }

        else if (token.length()==7){	// length 7 word
            token =  suf1(token);
            if (token.length()==7)
                token =  pre1(token);
            if (token.length()==6){
                token =  pro_w6(token);
                token =  end_w6(token);
            }
        }
        return token;
    }

    public ArrayList<String> stem( ArrayList<String> Strings){
        ArrayList<String> list=new ArrayList<String>();

        for(String s:Strings){
            list.add(stem(s));
        }
        return list;

    }

}