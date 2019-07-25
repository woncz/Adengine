package io.github.woncz.ads.engine.be;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class ConjunctionPartition {

    private int conjunctionSize = -1;

    private Map<Key, PostingList> postingListMap;

    public ConjunctionPartition(int conjunctionSize, Map<Key, PostingList> postingListMap) {
        this.conjunctionSize = conjunctionSize;
        this.postingListMap = postingListMap;

        if (conjunctionSize == 0 && !postingListMap.containsKey(Key.ZKey)) {
            postingListMap.put(Key.ZKey, new PostingList(new ArrayList<>()));
        }
    }

    public PostingList get(Key key) {
        return postingListMap.get(key);
    }

    public List<PostingList> get(Key key, boolean soft) {
        if (!soft) {
            PostingList pl = this.get(key);
            return pl == null ? null : Arrays.asList(pl);
        }
        List<PostingList> postingLists = new ArrayList<>();
        Set<Map.Entry<Key, PostingList>> entries = postingListMap.entrySet();
        for(Map.Entry<Key, PostingList> e : entries) {
            Key k = e.getKey();
            if (k.equals(key, true)) {
                postingLists.add(e.getValue());
            }
        }
        return postingLists;
    }

    public void append(Key key, PostingList postingList) {
        if (postingListMap.containsKey(key)) {
            throw new IllegalStateException("duplicate key in ConjunctionPartition");
        }
        postingListMap.put(key, postingList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (postingListMap != null) {
            Set<Map.Entry<Key, PostingList>> entries = postingListMap.entrySet();
            entries.forEach(i -> sb.append(i.getKey() + "|" + i.getValue()).append("\n"));
        }
        return sb.toString();
    }
}
