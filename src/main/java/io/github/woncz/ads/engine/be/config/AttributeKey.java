package io.github.woncz.ads.engine.be.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 属性名称, 用于用户画像及定向条件
 *
 * @author woncz
 * @date 2019/5/10.
 */
public enum AttributeKey {

    // TODO just for test
    AGE                ("age"                                     ),
    STATE              ("state"                                   ),
    SUBSCRIPTION       ("subscription"                            ),
    PROVINCE           ("provinceCode"                            ),
    USER_ID            ("userId"                                  ),

    /*
     * context
     */
    // 版位
    AD_POSITION        ("ad_position",  AD_POSITION_VALUES.range()),
    // 广告
    AD                 ("ad"                                      ),

    /*
     * targeting
     */
    LOCATION           ("location"                                ),
    GENDER             ("gender"                                  ),;

    // 属性名称
    private String name;

    // 取值范围
    private Set<String> values;

    AttributeKey(String name) {
        this.name = name;
    }

    AttributeKey(String name, Set<String> values) {
        this.name = name;
        this.values = values;
    }
    public String getName() {
        return this.name;
    }

    public static AttributeKey get(String name) {
        for (AttributeKey key : AttributeKey.values()) {
            if (key.name.equals(name)) {
                return key;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        boolean flag = false;
        for (AttributeKey key : AttributeKey.values()) {
            if (key.name.equals(name)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 取值范围
     *
     * @return
     */
    public Set<String> range() {
        return this.values;
    }

    private enum AD_POSITION_VALUES {
        ARTICLE_FLOW, ARTICLE_DETAIL, ARTICLE_SELECT, EDU_FLOW, EDU_SHAKE;

        private static final Set<String> values = new HashSet<>();

        static {
            for (AD_POSITION_VALUES v : values()) {
                values.add(v.name());
            }
        }

        public static Set<String> range() {
            return values;
        }
    }

    private enum PAGE_VALUES {
        HOME("1");

        PAGE_VALUES(String page) {
            this.page = page;
        }

        private static final Set<String> values = new HashSet<>();

        private String page;

        static {
            for (PAGE_VALUES v : values()) {
                values.add(v.page);
            }
        }

        public static Set<String> range() {
            return values;
        }

    }

    private enum DEFAULT {
        ;
        private static final Set<String> values = Collections.singleton("");

        public static Set<String> range() {
            return values;
        }
    }

    public static void main(String[] args) {
        String ts = "Hello";
        System.out.println(AttributeKey.contains(ts));

        ts = "location";
        System.out.println(AttributeKey.contains(ts));

        System.out.println(AGE.name());

        System.out.println(AD_POSITION.range());

    }

}
