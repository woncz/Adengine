package io.github.woncz.ads.engine.be;

import java.util.List;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class PostingList {

    private ThreadLocal<Integer> currentPos = ThreadLocal.withInitial(() -> 0);

    private List<Entry> entries;

    public PostingList(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public int getCurrentPos() {
        return this.currentPos.get();
    }

    public Entry getCurrentEntry() {
        return this.get(currentPos.get());
    }

    public int size() {
        return entries.size();
    }

    public Entry get(int index) {
        return index >= entries.size() ? null : entries.get(index);
    }

    public void skipTo(long conjunctionId) {
        for (; currentPos.get() < entries.size(); currentPos.set(currentPos.get() + 1)) {
            if (entries.get(currentPos.get()).getConjunctionId() >= conjunctionId) {
                break;
            }
        }
    }

    public void reset() {
        currentPos.set(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.entries.forEach(e -> sb.append("(").append(e.getConjunctionId()).append(",").append(e.getBelong() ? "∈" : "∉").append(")"));
        sb.append("(currentPos:" + currentPos.get() +")");
        return sb.toString();
    }
}
