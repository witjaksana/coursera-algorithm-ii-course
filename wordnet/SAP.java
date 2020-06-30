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
 *  Name: SAP.java
 *  Date: 30/06/2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph G;

    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Null argument!");

        this.G = new Digraph(G);
    }

    private int[] shortest(int v, int w) {
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V())
            throw new IllegalArgumentException("Bad argument");

        int[] result = new int[2];
        DeluxeBFS vDeluxeBFS = new DeluxeBFS(G, v);
        DeluxeBFS wDeluxeBFS = new DeluxeBFS(G, w);
        boolean[] vmarked = vDeluxeBFS.getMarked();
        boolean[] wmarked = wDeluxeBFS.getMarked();

        int shortestLength = Integer.MAX_VALUE;
        int tempLength = Integer.MAX_VALUE;
        int shortestAncestor = Integer.MAX_VALUE;
        for (int i = 0; i < vmarked.length; i++) {
            if (vmarked[i] && wmarked[i]) {
                tempLength = vDeluxeBFS.distTo(i) + wDeluxeBFS.distTo(i);
                if (tempLength < shortestLength) {
                    shortestLength = tempLength;
                    shortestAncestor = i;
                }
            }
        }

        if (shortestLength == Integer.MAX_VALUE) {
            result[0] = -1;
            result[1] = -1;
            return result;
        }
        result[0] = shortestLength;
        result[1] = shortestAncestor;
        return result;
    }

    private int[] shortest(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Bad argument");

        int[] result = new int[2];
        int shortestLength = Integer.MAX_VALUE;
        int shortestAncestor = Integer.MAX_VALUE;

        for (int vNode : v) {
            for (int wNode : w) {
                int[] tempResult = shortest(vNode, wNode);
                if (tempResult[0] != -1 && tempResult[0] < shortestLength) {
                    shortestLength = tempResult[0];
                    shortestAncestor = tempResult[1];
                }
            }
        }

        if (shortestLength == Integer.MAX_VALUE) {
            result[0] = -1;
            result[1] = -1;
            return result;
        }

        result[0] = shortestLength;
        result[1] = shortestAncestor;
        return result;

    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V())
            throw new IllegalArgumentException("Bad argument");
        int[] res = shortest(v, w);
        return res[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V())
            throw new IllegalArgumentException("Bad argument");
        int[] res = shortest(v, w);
        return res[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Null argument");
        int[] res = shortest(v, w);
        return res[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Null argument");
        int[] res = shortest(v, w);
        return res[1];
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
