package io.github.woncz.ads.engine.be;

import io.github.woncz.ads.engine.Indexer;
import io.github.woncz.ads.engine.cache.Cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class BEIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger(BEIndexer.class);

    /**
     * 广告索引(内存)
     */
    private ConjunctionIndex index;

    public BEIndexer() {
        index = new ConjunctionIndex();
    }

    /**
     * 增量索引
     *
     * @param document
     */
    @Override
    public void append(Document document) {
        this.append(document, this.index);
    }

    private void append(Document document, ConjunctionIndex index) {
        DNF dnf = document.getTargeting();
        List<Conjunction> conjunctions = dnf.getConjunctions();
        if (conjunctions == null || conjunctions.size() != 1) {
            throw new IllegalArgumentException("bad format document entered the index engine");
        }
        Conjunction conjunction = conjunctions.get(0);
        int size = conjunction.size();
        index.append(size, document);
        if (size > index.getMaxConjunctionSize()) {
            index.setMaxConjunctionSize(size);
        }
    }

    /**
     * 重建索引
     */
    @Override
    public synchronized void rebuild(List<Document> documents) {
        documents.sort(Comparator.comparingLong(Document::getDocId));
        ConjunctionIndex idx = new ConjunctionIndex();
        documents.forEach(document -> this.append(document, idx));
        idx.padding();
        logger.debug("index is " + '\n' + idx.toString());
        // replace the old index
        this.index = idx;
    }

    /**
     * 根据正排索引过滤（实时特征）
     *
     * @param result
     * @return
     */
    @Override
    public Set<Long> filter(Set<Long> result) {
        return result;
    }

    /**
     * 广告排序（业务逻辑）
     *
     * @param result
     * @return
     */
    @Override
    public List<Long> ranking(String position, int page, Set<Long> result, int topK) {
        // 使用LRU算法，以及根据曝光量动态平衡
        Set<Long> recent = Cache.recentAds(position);
        logger.info("recent ads=" + recent);
        recent.retainAll(result);
        result.removeAll(recent);
        int left = result.size();
        if (left >= topK) {
            return this.rank(result, topK);
        } else {
            List l = recent.stream().collect(Collectors.toList());
            Collections.shuffle(l);
            result.addAll(l.subList(0, Math.min(topK - left, recent.size())));
            return this.rank(result, topK);
        }
    }

    /**
     * 排序
     */
    private List<Long> rank(Set<Long> result, int topK) {

        logger.info("before rank=" + result);

        if (result == null || result.isEmpty()) {
            return Collections.emptyList();
        }
        if (result.size() == 1) {
            return result.stream().collect(Collectors.toList());
        }
        return result.stream().limit(topK).collect(Collectors.toList());
    }
    /**
     * 广告检索
     *
     * The Conjunction Algorithm
     */
    @Override
    public Set<Long> retrieve(Query query) {

        logger.info("query : " + query);

        Set<Long> result = new HashSet<>();

        int k = Math.min(index.getMaxConjunctionSize(), query.size());

        int counter = 1;

        for (; k >= 0; k--) {
            List<PostingList> pLists = index.getPostingLists(query, k);
            initCurrentEntries(pLists);

            // Processing K = 0 and K = 1 are identical
            int currentSize = k;
            if (currentSize == 0) {
                currentSize = 1;
            }
            // Too few posting lists for any conjunction to be satisfied
            if (pLists.size() < k) {
                continue;
            }

            sortByCurrentEntries(pLists);

            while (pLists.get(currentSize - 1).getCurrentPos() < pLists.get(currentSize - 1).size()) {

                logger.info("counter:" + counter++);

                Entry currentEntry0 = pLists.get(0).getCurrentEntry();
                Entry currentEntryK = pLists.get(currentSize - 1).getCurrentEntry();

                long nextConjunctionId = currentEntryK.getConjunctionId();

                // Check if the first K posting lists have the same conjunction ID in their current entries
                if (currentEntry0.getConjunctionId() == currentEntryK.getConjunctionId()) {
                    // Reject conjunction if a ∉ predicate is violated
                    if (!currentEntry0.getBelong()) {
                        long rejectId = currentEntry0.getConjunctionId();
                        for (int l = currentSize; l < pLists.size(); l++) {
                            // Skip to smallest ID where ID > RejectID
                            Entry currentEntryL = pLists.get(l).getCurrentEntry();
                            if (currentEntryL.getConjunctionId() == rejectId) {
                                pLists.get(l).skipTo(rejectId + 1);
                            } else {
                                // out of for loop
                                break;
                            }
                        }
                    } else {
                        // conjunction is fully satisfied
                        result.add(currentEntryK.getConjunctionId());
                    }
                    nextConjunctionId = currentEntryK.getConjunctionId() + 1;
                }

                for (int l = 0; l < currentSize; l++) {
                    pLists.get(l).skipTo(nextConjunctionId);
                }

                sortByCurrentEntries(pLists);
            }
        }
        return result;
    }

    /**
     * 初始化下标位置
     *
     * @param pLists
     */
    private void initCurrentEntries(List<PostingList> pLists) {
        pLists.forEach(i -> i.reset());
    }

    /**
     * 排序
     *
     * @param pLists
     */
    private void sortByCurrentEntries(List<PostingList> pLists) {
        pLists.sort((o1, o2) -> {
            Entry e1 = o1.getCurrentEntry();
            Entry e2 = o2.getCurrentEntry();

            if (e1 != null && e2 != null) {
                if (e1.getConjunctionId() == e2.getConjunctionId()) {
                    if (e1.getBelong() == e2.getBelong()){
                        return 0;
                    } else {
                        return e1.getBelong() ? 1 : -1;
                    }
                } else {
                    return e1.getConjunctionId() > e2.getConjunctionId() ? 1 : -1;
                }
            } else {
                if (e1 == null && e2 == null) {
                    return 0;
                } else {
                    return e1 == null ? 1 : -1;
                }
            }

        });
    }

    @Override
    public String toString() {
        return index.toString();
    }

    public static void main(String[] args) {
        BEIndexer indexer = new BEIndexer();


        Entry e1 = new Entry(40000008, true);
        Entry e2 = new Entry(43, true);
        List<Entry> l = new ArrayList<>();
        l.add(e1);
        l.add(e2);
        PostingList pl = new PostingList(l);
        indexer.sortByCurrentEntries(Arrays.asList(pl));

        System.out.println(pl);


        List<Entry> list0_0 = new ArrayList<>();
        list0_0.add(new Entry(6, false));
        List<Entry> list0_1 = new ArrayList<>();
        list0_1.add(new Entry(6, true));
        Map<Key, PostingList> postingListMap0 = new HashMap<>(2);
        postingListMap0.put(new Key("state", "CA"),  new PostingList(list0_0));
        postingListMap0.put(Key.ZKey, new PostingList(list0_1));
        ConjunctionPartition postingListMapSize0 = new ConjunctionPartition(0, postingListMap0);

        List<Entry> list1_0 = new ArrayList<>();
        list1_0.add(new Entry(-1, true));
        list1_0.add(new Entry(5, true));
        Map<Key, PostingList> postingListMap1 = new HashMap<>(1);

        List<Entry> list1_1 = new ArrayList<>();
        list1_1.add(new Entry(-4, false));
        list1_1.add(new Entry(-3, true));
        list1_1.add(new Entry(-2, true));
        postingListMap1.put(new Key("state", "CA"), new PostingList(list1_1));

        postingListMap1.put(new Key("age", "3"), new PostingList(list1_0));
        ConjunctionPartition postingListMapSize1 = new ConjunctionPartition(1, postingListMap1);

        List<Entry> list2_0 = new ArrayList<>();
        list2_0.add(new Entry(1, true));
        list2_0.add(new Entry(2, true));
        list2_0.add(new Entry(3, true));
        List<Entry> list2_1 = new ArrayList<>();
        list2_1.add(new Entry(3, false));
        list2_1.add(new Entry(4, true));
        List<Entry> list2_2 = new ArrayList<>();
        list2_2.add(new Entry(3, true));
        list2_2.add(new Entry(4, true));
        Map<Key, PostingList> postingListMap2 = new HashMap<>(3);
        postingListMap2.put(new Key("age", "3"),  new PostingList(list2_0));
        postingListMap2.put(new Key("state", "CA"), new PostingList(list2_1));
        postingListMap2.put(new Key("gender", "M"), new PostingList(list2_2));
        ConjunctionPartition postingListMapSize2 = new ConjunctionPartition(2, postingListMap2);

        indexer.index.setMaxConjunctionSize(2);
        Map<Integer, ConjunctionPartition> invertedIndex = indexer.index.getInvertedIndex();
        invertedIndex.put(0, postingListMapSize0);
        invertedIndex.put(1, postingListMapSize1);
        invertedIndex.put(2, postingListMapSize2);

        List<Assignment> assignments = new ArrayList<>(3);
        assignments.add(new Assignment("age", true, "3"));
        assignments.add(new Assignment("state", true, "CA"));
        assignments.add(new Assignment("gender", true, "M"));

        Query query = new Query(assignments);
        Set<Long> result = indexer.retrieve(query);

        // 结果输出： [4, 5]
        System.out.println(result.toString());
    }
}
