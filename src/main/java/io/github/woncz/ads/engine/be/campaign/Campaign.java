package io.github.woncz.ads.engine.be.campaign;

import java.sql.Timestamp;

/**
 * 推广计划
 *
 * @author woncz
 * @date 2019/5/9.
 */
public class Campaign {

    private long id;

    private String name;

    private Timestamp effDatetime;

    private Timestamp expDatetime;

    private String advPosition; // 广告位

    private String creative; // 创意

    private String status;

    private Targeting targeting;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
    }
}
