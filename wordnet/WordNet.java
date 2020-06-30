/*
 * MIT License
 *
 * Copyright (c) 2020 Arief Wicaksana (arief.wicaksana@outlook.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/* *****************************************************************************
 *  Name: WordNet.java
 *  Date: 21/06/2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WordNet {

    private final HashMap<Integer, String> synset;
    private final HashMap<String, List<Integer>> nouns;

    private Digraph G;
    private SAP sap;

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Null argument!");

        synset = new HashMap<>();
        nouns = new HashMap<>();
        Set<String> nounSet = new TreeSet<>();

        int countSynset = 0;
        In synsetFile = new In(synsets);
        while (synsetFile.hasNextLine()) {
            String[] line = synsetFile.readLine().split(",");
            Integer idx = Integer.parseInt(line[0]);
            synset.put(idx, line[1]);

            String[] words = line[1].split(" ");
            for (String word : words) {
                boolean isNewAdd = nounSet.add(word);
                if (isNewAdd) {
                    List<Integer> list = new ArrayList<>();
                    list.add(idx);
                    nouns.put(word, list);
                }
                else {
                    nouns.get(word).add(idx);
                }
            }
        }
        synsetFile.close();

        G = new Digraph(synset.size());
        In hypernymsFile = new In(hypernyms);
        while (hypernymsFile.hasNextLine()) {
            String[] line = hypernymsFile.readLine().split(",");
            Integer idx = Integer.parseInt(line[0]);
            int n = line.length;
            for (int i = 1; i < n; i++)
                G.addEdge(idx, Integer.parseInt(line[i]));
        }
        hypernymsFile.close();
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle())
            throw new IllegalArgumentException();

        sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Null argument!");

        List<Integer> a = nouns.get(nounA);
        List<Integer> b = nouns.get(nounB);
        return sap.length(a, b);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Null argument!");

        List<Integer> a = nouns.get(nounA);
        List<Integer> b = nouns.get(nounB);
        int ancestor = sap.ancestor(a, b);
        return synset.get(ancestor);
    }

    public static void main(String[] args) {

        WordNet wd = new WordNet(args[0], args[1]);
        StdOut.print(wd.isNoun("AIDS"));
    }
}
