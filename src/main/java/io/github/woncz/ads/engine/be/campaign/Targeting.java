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

package io.github.woncz.ads.engine.be.campaign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定向条件
 *
 * @author woncz
 * @date 2019/5/9.
 */
public class Targeting {

    /**
     * 定向条件
     */
    private Map<String, List<Object>> conditions = new HashMap<>();

    public Map<String, List<Object>> getConditions() {
        return conditions;
    }

    public Targeting setConditions(Map<String, List<Object>> conditions) {
        this.conditions = conditions;
        return this;
    }
}
