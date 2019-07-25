package io.github.woncz.ads.engine.be.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author woncz
 * @date 2019/5/24.
 */
public class Mapper {

    private static final Map<String, String> mapper = new HashMap<>();

    static {

    }

    public static String get(String name) {
        return mapper.get(name);
    }

}
