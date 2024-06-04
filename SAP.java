/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class SAP {
    private final Digraph G;
    private final Stack<Integer> topologicalSort;
    private final boolean[] markedForSort;
    private final boolean[] marked;
    private final int[] id;
    private int count;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        count = 0;
        this.G = new Digraph(G);
        markedForSort = new boolean[this.G.V()];
        marked = new boolean[this.G.V()];
        id = new int[this.G.V()];
        topologicalSort = new Stack<Integer>();
        for (int i = 0, l = G.V(); i < l; i++) {
            if (!markedForSort[i]) {
                dfsForSort(this.G, i);
            }
        }
        for (int i : topologicalSort) {
            if (!marked[i]) {
                dfs(this.G, i);
                count++;
            }
        }

    }

    private void dfsForSort(Digraph G, int v) {
        markedForSort[v] = true;
        for (int i : G.adj(v)) {
            if (!markedForSort[i]) {
                dfsForSort(G, i);
            }
        }
        topologicalSort.push(v);
    }

    private void dfs(Digraph G, int v) {
        marked[v] = true;
        id[v] = count;
        for (int i : G.adj(v)) {
            if (!marked[i]) {
                dfs(G, i);
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!checkRange(v) || !checkRange(w)) {
            throw new IllegalArgumentException();
        }
        if (v == w) {
            return 0;
        }
        boolean flagA = false;
        boolean flagB = false;
        boolean flag = false;
        int minimum = Integer.MAX_VALUE;
        for (int i : topologicalSort) {
            if (flagA && flagB) {
                if (id[i] == id[v] && id[i] == id[w]) {
                    int helper = bfs(i, v) + bfs(i, w);
                    if (helper < minimum) {
                        minimum = helper;
                        flag = true;
                    }
                }
            }
            else {
                if (i == v) {
                    flagA = true;
                }
                else if (i == w) {
                    flagB = true;
                }
            }
        }
        if (flag) {
            return minimum;
        }
        return -1;
    }

    private int bfs(int i, int j) {
        boolean[] markedForQueue = new boolean[G.V()];
        int[] distance = new int[G.V()];
        Queue<Integer> helper = new Queue<Integer>();
        helper.enqueue(i);
        while (!helper.isEmpty()) {
            int reference = helper.dequeue();
            if (reference == j) {
                return distance[j];
            }
            markedForQueue[reference] = true;
            for (int k : G.adj(reference)) {
                if (!markedForQueue[k]) {
                    helper.enqueue(k);
                    distance[k] = distance[reference] + 1;
                }
            }
        }
        return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!checkRange(v) || !checkRange(w)) {
            throw new IllegalArgumentException();
        }
        if (v == w) {
            return v;
        }
        boolean flagA = false;
        boolean flagB = false;
        int minimum = Integer.MAX_VALUE;
        int comAncestor = -1;
        for (int i : topologicalSort) {
            if (flagA && flagB) {
                if (id[i] == id[v] && id[i] == id[w]) {
                    int helper = bfs(i, v) + bfs(i, w);
                    if (helper < minimum) {
                        minimum = helper;
                        comAncestor = i;
                    }
                }
            }
            else {
                if (i == v) {
                    flagA = true;
                }
                else if (i == w) {
                    flagB = true;
                }
            }
        }
        return comAncestor;
    }

    private boolean checkRange(int i) {
        return (i >= 0) && (i < G.V());
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        int minimum = Integer.MAX_VALUE;
        boolean success = false;
        for (int i : v) {
            for (int j : w) {
                int k = length(i, j);
                if (k != -1 && k < minimum) {
                    success = true;
                    minimum = k;
                }
            }
        }
        if (success) {
            return minimum;
        }
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        int comAncestor = -1;
        int minimum = Integer.MAX_VALUE;
        for (int i : v) {
            for (int j : w) {
                int k = length(i, j);
                if (k != -1 && k < minimum) {
                    minimum = k;
                    comAncestor = ancestor(i, j);
                }
            }
        }
        return comAncestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
