/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.Stack;

public class WordNet {
    private final String[] synsets;
    private final SAP sap;
    private final Digraph g;
    private final SeparateChainingHashST<String, Stack<Integer>> forIndex;
    private int numOfEntries;
    private final boolean[] onStack;
    private final boolean[] markedForSort;
    private boolean isAsyclic;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        forIndex = new SeparateChainingHashST<String, Stack<Integer>>();
        int rooted = 0;
        isAsyclic = true;
        In file = new In(synsets);
        int numOfEntries = 0;
        String read;
        while ((read = file.readLine()) != null) {
            numOfEntries = Integer.parseInt(read.split(",")[0]);
        }
        file.close();
        this.numOfEntries = numOfEntries + 1;
        onStack = new boolean[numOfEntries + 1];
        markedForSort = new boolean[numOfEntries + 1];
        g = new Digraph(this.numOfEntries);
        this.synsets = new String[numOfEntries + 1];
        file = new In(synsets);
        while ((read = file.readLine()) != null) {
            String[] helper = read.split(",");
            Stack<Integer> ids;
            numOfEntries = Integer.parseInt(helper[0]);
            this.synsets[numOfEntries] = helper[1];
            for (String s : helper[1].split("\\s+")) {
                if (forIndex.contains(s)) {
                    ids = forIndex.get(s);
                }
                else {
                    ids = new Stack<Integer>();
                }
                ids.push(numOfEntries);
                forIndex.put(s, ids);
            }
        }
        file.close();
        file = new In(hypernyms);
        while ((read = file.readLine()) != null) {
            String[] helper = read.split(",");
            if (helper.length < 2) {
                rooted++;
            }
            numOfEntries = Integer.parseInt(helper[0]);
            for (int i = 1, k = helper.length; i < k; i++) {
                g.addEdge(numOfEntries, Integer.parseInt(helper[i]));
            }
        }
        file.close();
        if (rooted != 1) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < this.numOfEntries; i++) {
            if (!markedForSort[i]) {
                dfsForSort(i);
            }
        }
        if (!isAsyclic) {
            throw new IllegalArgumentException();
        }
        sap = new SAP(g);
    }


    private void dfsForSort(int i) {
        markedForSort[i] = true;
        onStack[i] = true;
        for (int j : g.adj(i)) {
            if (!markedForSort[j]) {
                dfsForSort(j);
            }
            else if (onStack[j]) {
                isAsyclic = false;
            }
        }
        onStack[i] = false;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Stack<String> helper = new Stack<String>();
        for (String noun : synsets) {
            helper.push(noun);
        }
        return helper;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return forIndex.contains(word);
    }


    // private int indexOfNoun(String noun, int start, int end) {
    //     if (start > end) {
    //         return -1;
    //     }
    //     int mid = (start + end) / 2;
    //     int compare;
    //
    //     if (synsets[mid].equals(noun)) {
    //         return mid;
    //     }
    //
    //     if (noun.compareTo(synsets[mid]) < 0) {
    //         return indexOfNoun(noun, start, mid - 1);
    //     }
    //     else {
    //         return indexOfNoun(noun, mid + 1, end);
    //     }
    // }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        Stack<Integer> indexA = forIndex.get(nounA);
        Stack<Integer> indexB = forIndex.get(nounB);
        return sap.length(indexA, indexB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        Stack<Integer> indexA = forIndex.get(nounA);
        Stack<Integer> indexB = forIndex.get(nounB);
        return synsets[sap.ancestor(indexA, indexB)];
    }


    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(
                "/Users/mitul/Desktop/algorithms/Algorithms - 2/Assignment/wordnet/synsets100-subgraph.txt",
                "/Users/mitul/Desktop/algorithms/Algorithms - 2/Assignment/wordnet/hypernyms100-subgraph.txt");
        // for (int i = 0; i < wn.synsets.length; i++) {
        //     System.out.println(i);
        //     for (String s : wn.synsets[i]) {
        //         System.out.println(s);
        //     }
        // }
        // for (int i = 0; i < wn.hypernyms.length; i++) {
        //     System.out.println(i);
        //     for (int j : wn.hypernyms[i]) {
        //         System.out.println(j);
        //     }
        // }
        // System.out.println(wn.indexOfNoun("jimdandy jimhickey crackerjack"));
        System.out.println(wn.isNoun("factor_IV"));
        // System.out.println(wn.indexOfNoun("antihemophilic_globulin"));
        // System.out.println(wn.indexOfNoun("CRP"));
        // System.out.println(wn.indexOfNoun("C-reactive_protein"));
        // System.out.println(wn.indexOfNoun("zymase"));
        // System.out.println(wn.indexOfNoun("factor_IX"));
        // System.out.println(wn.indexOfNoun("corn_gluten"));
        // System.out.println(wn.indexOfNoun("factor_XIII"));
    }
}
