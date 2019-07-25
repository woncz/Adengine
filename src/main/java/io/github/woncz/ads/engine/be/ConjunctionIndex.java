package io.github.woncz.ads.engine.be;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class ConjunctionIndex {

    private static final String VERSION = "idx_version";

    private int maxConjunctionSize;

    private long maxConjunctionId;

    private Map<Integer, ConjunctionPartition> invertedIndex;

    public ConjunctionIndex() {
        this.invertedIndex = new HashMap<>(32);
    }

    public int getMaxConjunctionSize() {
        return maxConjunctionSize;
    }

    public void setMaxConjunctionSize(int maxConjunctionSize) {
        this.maxConjunctionSize = maxConjunctionSize;
    }

    public long getMaxConjunctionId() {
        return maxConjunctionId;
    }

    public void setMaxConjunctionId(long maxConjunctionId) {
        this.maxConjunctionId = maxConjunctionId;
    }

    @Deprecated
    public Map<Integer, ConjunctionPartition> getInvertedIndex() {
        return invertedIndex;
    }

    /**
     * continuity for size [0,1,2... -> maxConjunctionSize]
     */
    public void padding() {
        for (int i = 0; i <= maxConjunctionSize; i++) {
            if (invertedIndex.get(i) == null)  {
                invertedIndex.put(i, new ConjunctionPartition(i, new HashMap<>(1)));
            }
            if (i == 0) {
                Key version = new Key(VERSION, new Timestamp(System.currentTimeMillis()));
                ConjunctionPartition partition = invertedIndex.get(0);
                partition.append(version, new PostingList(new ArrayList<>()));
            }
        }
    }

    public void append(int size, Document document) {
        ConjunctionPartition partition = invertedIndex.get(size);
        if (partition == null) {
            partition = new ConjunctionPartition(size, new HashMap<>(8));
            invertedIndex.put(size, partition);
        }
        DocumentTransformer.transfer(document, partition);
    }

    /**
     * List of posting lists matching A for conjunction size K
     *
     * soft equal not strict equal: [a,b,c] equals [a,d,e] is true
     *
     * @param conjunction
     * @param k
     * @return
     */
    public List<PostingList> getPostingLists(Conjunction conjunction, int k) {
        Set<Key> keySet = new HashSet<>();
        keySet.addAll(conjunction.keys());

        final ConjunctionPartition partition = invertedIndex.get(k);

        List<PostingList> postingLists = new ArrayList<>();

        if (k > 0 && partition == null) {
            return postingLists;
        }

        keySet.forEach(key -> {
            List<PostingList> multi = partition.get(key, true);
            if (multi != null && multi.size() > 0) {
                postingLists.addAll(multi);
            }
        });
        if (k == 0) {
            postingLists.add(partition.get(Key.ZKey));
        }
        return postingLists;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // header
        sb.append("|K" + '\t' + "|Key" + '\t' + '\t' + '\t' + "| Posting List |" + '\n' + "|");
        IntStream stream = IntStream.range(0, sb.length());
        stream.forEach(li -> sb.append("-"));
        stream.close();
        sb.append("|\n");
        // body
        stream = IntStream.rangeClosed(0, maxConjunctionSize);
        stream.forEach(i -> {
            String[] lines = invertedIndex.get(i).toString().split("\\n");
            Arrays.sort(lines);
            int length = 0;
            for (String line : lines) {
                if (length < line.length()) {
                    length = line.length();
                }
                sb.append("|" + i + '\t' + "|" + line + '|' + '\n');
            }
            sb.append("|");
            IntStream.range(0, Math.min(length + 4, 80)).forEach(li -> sb.append("-"));
            sb.append("|\n");
        });
        stream.close();
        return sb.toString();
    }
}
