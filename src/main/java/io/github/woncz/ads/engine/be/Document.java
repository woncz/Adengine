package io.github.woncz.ads.engine.be;

/**
 * @author woncz
 * @date 2019/5/9.
 */
public class Document {

    private long docId;

    private DNF targeting;

    public Document(long docId, DNF targeting) {
        this.docId = docId;
        this.targeting = targeting;
    }

    public long getDocId() {
        return docId;
    }

    public DNF getTargeting() {
        return targeting;
    }

    public boolean validate() {
        return targeting != null;
    }
}
