/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.common.metric.batch;



import com.dtstack.engine.common.metric.Filter;
import com.dtstack.engine.common.metric.QueryInfo;
import com.dtstack.engine.common.metric.prometheus.func.CommonFunc;

import java.util.ArrayList;
import java.util.List;

/**
 * Reason:
 * Date: 2019/4/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SyncJobMetricWithSum extends BaseMetric {

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();

        Filter filter = new Filter();
        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);

        queryInfo.setFilters(filterList);

        CommonFunc func = new CommonFunc("sum");
        queryInfo.addAggregator(func);
        return queryInfo;
    }
}
