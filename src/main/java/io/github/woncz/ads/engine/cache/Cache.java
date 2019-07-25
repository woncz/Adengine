/*
 * Copyright (C) 2014-2020 Nuhtech Technology(Beijing) Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of Nuhtech Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Nuhtech inc.
 *
 */

package io.github.woncz.ads.engine.cache;

import java.util.HashSet;
import java.util.Set;

/**
 * @author woncz
 * @date 4/22/2019.
 */
public class Cache {

    private static Set<Long> recent = new HashSet<>();

    public static Set<Long> recentAds(String position) {
        return recent;
    }
}
