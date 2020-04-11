# JQuranTree

JQuranTree is a set of Java APIs for accessing and analyzing the Quran, in its authentic Arabic form. The Java library is released as an open source project. The Uthmani distribution of the Tanzil project is used (http://tanzil.info) and is left unmodified.

# The Library:
The library contains;
* a Java API which wraps the XML Uthmani Script of the Tanzil project
* an object model for the Quran’s orthography
* encoders and decoders for converting Arabic text
* Java classes for searching the text of the Quran
* The library includes a user guide and API documentation, with examples on using the library to perform basic analysis of Quranic text.


# Command line

```!bash
java -cp jqurantree-1.0.0.jar  org.jqurantree.tools.Tools qamar

usage: jqurantree [input-text]
 -if,--input-format <arg>    input text format 1=Buckwalter 2-Arabic
                             [Default =1]
 -in,--input-file <arg>      input file path
 -o,--operation <arg>        operation 1=Diacritics Free Search 2-Exact
                             Token Search 3-Root Search [Default=1]
 -of,--output-format <arg>   output text format 1=Buckwalter 2-Arabic
                             [Default =1]
 -out,--output-file <arg>    output file



Sample;

java -cp jqurantree-1.0.0.jar  org.jqurantree.tools.Tools -o 2 -in <path>\roots.csv -out <path>\roots_bw.csv
```

