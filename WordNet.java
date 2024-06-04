/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class WordNet {
    private final Stack<String>[] synsets;
    private final Stack<Integer>[] hypernyms;
    private int numOfEntries;
    private final boolean[] onStack;
    private final boolean[] markedForSort;
    private final boolean[] marked;
    private final int[] id;
    private int count;
    private boolean isAsyclic;
    private final Stack<Integer> topologicalSort;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        int rooted = 0;
        isAsyclic = true;
        count = 0;
        In file = new In(synsets);
        int numOfEntries = 0;
        String read;
        while ((read = file.readLine()) != null) {
            numOfEntries = Integer.parseInt(read.split(",")[0]);
        }
        file.close();
        this.numOfEntries = numOfEntries + 1;
        topologicalSort = new Stack<Integer>();
        onStack = new boolean[numOfEntries + 1];
        markedForSort = new boolean[numOfEntries + 1];
        marked = new boolean[numOfEntries + 1];
        id = new int[this.numOfEntries];
        this.synsets = (Stack<String>[]) new Stack[numOfEntries + 1];
        this.hypernyms = (Stack<Integer>[]) new Stack[numOfEntries + 1];
        for (int i = 0; i < numOfEntries + 1; i++) {
            markedForSort[i] = false;
            marked[i] = false;
            this.synsets[i] = new Stack<String>();
            this.hypernyms[i] = new Stack<Integer>();
        }
        file = new In(synsets);
        while ((read = file.readLine()) != null) {
            String[] helper = read.split(",");
            numOfEntries = Integer.parseInt(helper[0]);
            for (String s : helper[1].split("\\s+")) {
                this.synsets[numOfEntries].push(s);
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
                this.hypernyms[numOfEntries].push(Integer.parseInt(helper[i]));
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

        for (int i : topologicalSort) {
            if (!marked[i]) {
                dfs(i);
                count++;
            }
        }

    }

    private void dfs(int i) {
        id[i] = count;
        for (int j : hypernyms[i]) {
            if (!marked[j]) {
                dfs(j);
            }
        }
    }

    private void dfsForSort(int i) {
        markedForSort[i] = true;
        onStack[i] = true;
        for (int j : hypernyms[i]) {
            if (!markedForSort[j]) {
                dfsForSort(j);
            }
            else if (onStack[j]) {
                isAsyclic = false;
            }
        }
        onStack[i] = false;
        topologicalSort.push(i);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Stack<String> helper = new Stack<String>();
        for (Stack<String> s : synsets) {
            for (String noun : s) {
                helper.push(noun);
            }
        }
        return helper;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return isNoun(word, 0, this.numOfEntries - 1);
    }

    private boolean isNoun(String word, int start, int end) {
        if (start < end) {
            return false;
        }
        int mid = (start + end) / 2;
        for (String s : synsets[mid]) {
            if (s.equals(word)) {
                return true;
            }
        }
        if (word.compareTo(synsets[mid].peek()) < 0) {
            return isNoun(word, start, mid - 1);
        }
        else {
            return isNoun(word, mid + 1, end);
        }
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        boolean flagA = false;
        int indexA = -1;
        int indexB = -1;
        boolean flagB = false;
        boolean flag = false;
        int minDistance = Integer.MAX_VALUE;
        for (int i : topologicalSort) {
            if (flagA && flagB) {
                if ((id[i] == id[indexA]) && (id[i] == id[indexB])) {
                    int helper = bfs(indexA, i) + bfs(indexB, i);
                    if (helper < minDistance) {
                        minDistance = helper;
                        flag = true;
                    }

                }
            }
            else {
                for (String s : synsets[i]) {
                    if (s.equals(nounA)) {
                        flagA = true;
                        indexA = i;
                    }
                    if (s.equals(nounB)) {
                        flagB = true;
                        indexB = i;
                    }
                }
            }

        }
        if (indexA == indexB) {
            return 0;
        }
        if (flag) {
            return minDistance;
        }
        return -1;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        boolean flagA = false;
        int indexA = -1;
        int indexB = -1;
        boolean flagB = false;
        String answer = null;
        int minDistance = Integer.MAX_VALUE;
        for (int i : topologicalSort) {
            if (flagA && flagB) {
                if ((id[i] == id[indexA]) && (id[i] == id[indexB])) {
                    int helper = bfs(indexA, i) + bfs(indexB, i);
                    if (helper < minDistance) {
                        minDistance = helper;
                        StringBuilder sb = new StringBuilder();
                        for (String s : synsets[i]) {
                            sb.append(s);
                        }
                        answer = sb.reverse().toString();
                    }

                }
            }
            else {
                for (String s : synsets[i]) {
                    if (s.equals(nounA)) {
                        flagA = true;
                        indexA = i;
                    }
                    if (s.equals(nounB)) {
                        flagB = true;
                        indexB = i;
                    }
                }
            }

        }
        if (indexA == indexB) {
            return nounA;
        }
        return answer;
    }

    private int bfs(int i, int j) {
        boolean[] markedForQueue = new boolean[numOfEntries];
        int[] distance = new int[numOfEntries];
        Queue<Integer> helper = new Queue<Integer>();
        helper.enqueue(i);
        while (!helper.isEmpty()) {
            int reference = helper.dequeue();
            if (reference == j) {
                return distance[j];
            }
            markedForQueue[reference] = true;
            for (int k : hypernyms[reference]) {
                if (!markedForQueue[k]) {
                    helper.enqueue(k);
                    distance[k] = distance[reference] + 1;
                }
            }
        }
        return -1;
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
