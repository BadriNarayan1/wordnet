/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Outcast {
    private WordNet wordNet;

    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }         // constructor takes a WordNet object

    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException();
        }
        int disMax = Integer.MIN_VALUE;
        String outcast = null;
        for (String s : nouns) {
            int helper = 0;
            for (String w : nouns) {
                helper += wordNet.distance(s, w);
            }
            if (helper > disMax) {
                disMax = helper;
                outcast = s;
            }
        }
        return outcast;
    }   // given an array of WordNet nouns, return an outcast

    public static void main(String[] args) {

    }  // see test client below
}
