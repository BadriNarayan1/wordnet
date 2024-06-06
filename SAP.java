/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class SAP {
    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        this.G = new Digraph(G);

    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!checkRange(v) || !checkRange(w)) {
            throw new IllegalArgumentException();
        }
        if (v == w) {
            return 0;
        }
        int[] forV = bfs(v);
        int[] forW = bfs(w);
        boolean flag = false;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if ((forV[i] != 0 && forW[i] != 0) || (i == v && forW[i] != 0) || (i == w
                    && forV[i] != 0)) {
                int helper = forV[i] + forW[i];
                if (helper < minDistance) {
                    minDistance = helper;
                    flag = true;
                }
            }
        }
        if (flag) {
            return minDistance;
        }
        return -1;
    }

    private int[] bfs(int i) {
        boolean[] markedForQueue = new boolean[G.V()];
        int[] distance = new int[G.V()];
        Queue<Integer> helper = new Queue<Integer>();
        helper.enqueue(i);
        markedForQueue[i] = true;
        while (!helper.isEmpty()) {
            int reference = helper.dequeue();
            for (int k : G.adj(reference)) {
                if (!markedForQueue[k]) {
                    helper.enqueue(k);
                    distance[k] = distance[reference] + 1;
                    markedForQueue[k] = true;
                }
            }
        }
        return distance;
    }

    private int[] bfs(Iterable<Integer> i) {
        boolean[] markedForQueue = new boolean[G.V()];
        int[] distance = new int[G.V()];
        Queue<Integer> helper = new Queue<Integer>();
        for (int j : i) {
            helper.enqueue(j);
            markedForQueue[j] = true;
        }
        while (!helper.isEmpty()) {
            int reference = helper.dequeue();
            for (int k : G.adj(reference)) {
                if (!markedForQueue[k]) {
                    helper.enqueue(k);
                    distance[k] = distance[reference] + 1;
                    markedForQueue[k] = true;
                }
            }
        }
        return distance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!checkRange(v) || !checkRange(w)) {
            throw new IllegalArgumentException();
        }
        if (v == w) {
            return v;
        }
        int[] forV = bfs(v);
        int[] forW = bfs(w);
        int comAncestor = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if ((forV[i] != 0 && forW[i] != 0) || (i == v && forW[i] != 0) || (i == w
                    && forV[i] != 0)) {
                int helper = forV[i] + forW[i];
                if (helper < minDistance) {
                    minDistance = helper;
                    comAncestor = i;
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
        boolean[] markedV = new boolean[G.V()];
        boolean[] markedW = new boolean[G.V()];
        for (Integer i : v) {
            if (i == null || !checkRange(i)) {
                throw new IllegalArgumentException();
            }
            markedV[i] = true;
        }
        for (Integer i : w) {
            if (i == null || !checkRange(i)) {
                throw new IllegalArgumentException();
            }
            markedW[i] = true;
        }
        int[] forV = bfs(v);
        int[] forW = bfs(w);
        boolean flag = false;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (markedW[i] && markedV[i]) {
                return 0;
            }
            else if ((forV[i] != 0 && forW[i] != 0) || (markedV[i] && forW[i] != 0) || (markedW[i]
                    && forV[i] != 0)) {
                int helper = forV[i] + forW[i];
                if (helper < minDistance) {
                    minDistance = helper;
                    flag = true;
                }
            }
        }
        if (flag) {
            return minDistance;
        }
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        boolean[] markedV = new boolean[G.V()];
        boolean[] markedW = new boolean[G.V()];
        for (Integer i : v) {
            if (i == null || !checkRange(i)) {
                throw new IllegalArgumentException();
            }
            markedV[i] = true;
        }
        for (Integer i : w) {
            if (i == null || !checkRange(i)) {
                throw new IllegalArgumentException();
            }
            markedW[i] = true;
        }
        int[] forV = bfs(v);
        int[] forW = bfs(w);
        int comAncestor = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (markedW[i] && markedV[i]) {
                return i;
            }
            else if ((forV[i] != 0 && forW[i] != 0) || (markedV[i] && forW[i] != 0) || (markedW[i]
                    && forV[i] != 0)) {
                int helper = forV[i] + forW[i];
                if (helper < minDistance) {
                    minDistance = helper;
                    comAncestor = i;
                }
            }
        }
        return comAncestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        Digraph g = new Digraph(4);
        // g.addEdge(7, 3);
        // g.addEdge(8, 3);
        // g.addEdge(3, 1);
        // g.addEdge(4, 1);
        // g.addEdge(5, 1);
        // g.addEdge(9, 5);
        // g.addEdge(10, 5);
        // g.addEdge(11, 10);
        // g.addEdge(12, 10);
        // g.addEdge(1, 0);
        // g.addEdge(2, 0);
        // SAP s = new SAP(g);
        g.addEdge(0, 1);
        g.addEdge(1, 0);
        g.addEdge(1, 2);
        g.addEdge(2, 1);
        g.addEdge(2, 3);
        g.addEdge(3, 2);
        g.addEdge(3, 0);
        g.addEdge(0, 3);
        g.addEdge(1, 3);
        g.addEdge(3, 1);
        g.addEdge(0, 2);
        g.addEdge(2, 0);

        SAP s = new SAP(g);
        System.out.println(s.length(0, 3));
        System.out.println(s.ancestor(0, 3));
    }
}
