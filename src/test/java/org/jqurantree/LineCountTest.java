/* Copyright (C) Kais Dukes, 2009.
 * 
 * This file is part of JQuranTree.
 * 
 * JQuranTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JQuranTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JQuranTree. If not, see <http://www.gnu.org/licenses/>.
 */

package org.jqurantree;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.io.FileUtils;
//import org.jqurantree.analysis.stemmer.ArabicStemmer;
//import org.jqurantree.analysis.stemmer.ISRI;
import org.jqurantree.analysis.stemmer.StemmerType;
import org.jqurantree.arabic.ArabicCharacter;
import org.jqurantree.arabic.ArabicText;
import org.jqurantree.core.error.JQuranTreeException;
import org.jqurantree.orthography.Document;
import org.jqurantree.orthography.Token;
import org.junit.Ignore;
import org.junit.Test;

import static org.jqurantree.tools.Tools.*;

public class LineCountTest {

	public static final int MAX_COL = 18;

	@Test
	@Ignore
	public void extractRootsAndWordsAlKhalil1_1() throws Exception {
		csvWriter("stemming/roots_alkhalil1.1_auto.csv",getAllDistinctWordsAndRoots(StemmerType.AlKhalil1_1,true, false), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsNoDiacriticsAlKhalil1_1() throws Exception {
		csvWriter("stemming/roots_alkhalil1.1_no_haraka_auto.csv",getAllDistinctWordsAndRoots(StemmerType.AlKhalil1_1,true, true), false);
	}
	@Test
//	@Ignore
	public void extractRootsAndWordsAlKhalil2_1() throws Exception {
		csvWriter("stemming/roots_alkhalil2.1_auto.csv",getAllDistinctWordsAndRoots(StemmerType.AlKhalil1_1,true, false), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsNoDiacriticsAlKhalil2_1() throws Exception {
		csvWriter("stemming/roots_alkhalil2.1_no_haraka_auto.csv",getAllDistinctWordsAndRoots(StemmerType.AlKhalil1_1,true, true), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsISRI() throws Exception {
		csvWriter("stemming/roots_isri_auto.csv",getAllDistinctWordsAndRoots(StemmerType.ISRI,true, false), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsNoDiacriticsISRI() throws Exception {
		csvWriter("stemming/roots_isri_no_haraka_auto.csv",getAllDistinctWordsAndRoots(StemmerType.ISRI,true,  true), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsKhodja() throws Exception {
		csvWriter("stemming/roots_khodja_auto.csv",getAllDistinctWordsAndRoots(StemmerType.KODJA,true, false), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsNoDiacriticsKhodja() throws Exception {
		csvWriter("stemming/roots_khodja_no_haraka_auto.csv",getAllDistinctWordsAndRoots(StemmerType.KODJA,true, true), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsLucene() throws Exception {
		csvWriter("stemming/roots_lucene_auto.csv",getAllDistinctWordsAndRoots(StemmerType.Lucene,true, false), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsNoDiacriticsLucene() throws Exception {
		csvWriter("stemming/roots_lucene_no_haraka_auto.csv",getAllDistinctWordsAndRoots(StemmerType.Lucene,true,true), false);
	}
	@Test
	@Ignore
	public void extractRootsAndWordsQuranicCorpus() throws Exception {
		csvWriter("stemming/roots_quraniccorpus_auto.csv",getAllDistinctWordsAndRoots(StemmerType.QuranicCorpus,true,false), false);
	}
	@Test
	@Ignore
	public void extractRootsAndLetters() throws Exception {
		Map<String, Integer> lettersAndNumbers = getCharacterIntegerMap();
		csvWriter("letter_and_numbers.csv", Collections.unmodifiableMap(lettersAndNumbers));
		Map<ArabicText, String> wordsAndRoots= getAllDistinctWordsAndRoots(StemmerType.QuranicCorpus,false,false);
		List<String[]> ret = new ArrayList<>();
		String[] n = new String[MAX_COL];
		for (int i=0;i<MAX_COL-3;i++){
			n[i] = "l" + i;
		}
		n[MAX_COL-3] = "r1";
		n[MAX_COL-2] = "r2";
		n[MAX_COL-1] = "r3";
		ret.add(n);
		for (Map.Entry<ArabicText, String> v: wordsAndRoots.entrySet()) {
			n = new String[MAX_COL];
			String key = v.getKey().removeDiacritics().removeNonLetters().toUnicode();
			String value = v.getValue();
			if(value.length()>2) {
				for (int iW = 0; iW < MAX_COL - 3 ; iW++) {
					if(iW < key.length()) {
						n[iW] = String.valueOf(lettersAndNumbers.get(String.valueOf(key.charAt(iW))));
					}else{
						n[iW] = "0";
					}
				}
				ArabicText t = ArabicText.fromUnicode(value);
				n[MAX_COL - 3] = "h"+ lettersAndNumbers.get(t.getCharacter(0).toUnicode());
				n[MAX_COL - 2] = "h"+ lettersAndNumbers.get(t.getCharacter(1).toUnicode());
				n[MAX_COL - 1] = "h"+ lettersAndNumbers.get(t.getCharacter(2).toUnicode());
				ret.add(n);
			}
		}
		csvWriter("roots_and_letters.csv",ret);
	}

	private Map<String, Integer> getCharacterIntegerMap() throws IOException {
		String[] letters = getAllDistinctLetters(true);
		Map<String, Integer> lettersAndNumbers = new LinkedHashMap<>();
		int currentNumber=1;
		for (String l:letters) {
			if(StringUtils.isNotBlank(l)) {
				lettersAndNumbers.put(l, currentNumber++);
			}
		}
		return lettersAndNumbers;
	}

	@Test
	@Ignore
	public void extractWords() throws IOException {
		Map<ArabicText, String> wordsAndRoots= new LinkedHashMap<ArabicText, String>();
		for (Token v: Document.getTokens()) {
			if(!wordsAndRoots.containsKey(v)) {
				wordsAndRoots.put(v, v.removeDiacritics().toUnicode());
			}
		}
		csvWriter("words.csv",wordsAndRoots, true);
	}
	@Test
	@Ignore
	public void extractLetters() throws IOException {
		csvWriter("letters.csv",getAllDistinctLetters(true));
	}
	@Test
	@Ignore
	public void testLineCount() {

		// Count lines.
		int mainCount = countDirectory(new File("src/main"));
		int testCount = countDirectory(new File("src/test"));

		// Display counts.
		System.out.println("Line count (main): " + mainCount);
		System.out.println("Line count (test): " + testCount);
		System.out.println("Total: " + (mainCount + testCount));
	}

	private int countDirectory(File file) {

		// Count files.
		int count = 0;
		if (file.getName().endsWith(".java")) {
			count += countFile(file);
		}

		// Recurse through subdirectories.
		String[] directories = file.list();
		if (directories != null) {
			for (String directory : directories) {
				count += countDirectory(new File(file, directory));
			}
		}

		// Return count.
		return count;
	}

	private int countFile(File file) {
		int count = 0;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			while (reader.readLine() != null) {
				count++;
			}
			reader.close();
		} catch (IOException exception) {
			throw new JQuranTreeException("Failed to read: "
					+ file.getAbsolutePath(), exception);
		}
		return count;
	}
}
