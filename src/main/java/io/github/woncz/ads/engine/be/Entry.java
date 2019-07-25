package io.github.woncz.ads.engine.be;

/**
 * @author woncz
 * @date 2019/5/5.
 */
public class Entry {

    private long conjunctionId;

    private boolean belong;

    public Entry(long conjunctionId, boolean belong) {
        this.conjunctionId = conjunctionId;
        this.belong = belong;
    }

    public long getConjunctionId() {
        return conjunctionId;
    }

    public boolean getBelong() {
        return belong;
    }

    @Override
    public String toString() {
        return "(" + conjunctionId + "," + (belong ? "∈" : "∉") + ")";
    }
}
