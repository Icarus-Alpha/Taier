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

package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("汇总返回信息")
public class BatchUsageResultVO {

    @ApiModelProperty(value = "新表数量", example = "1")
    private Integer todayNewTable;

    @ApiModelProperty(value = "新字段数量", example = "1")
    private Integer todayNewColumn;

    @ApiModelProperty(value = "今日不规范模型总数", example = "1")
    private Integer todayBadTable;

    @ApiModelProperty(value = "今日不规范字段总数", example = "1")
    private Integer todayBadColumn;

    @ApiModelProperty(value = "不规范模型总数", example = "1")
    private Integer sumBadTable;

    @ApiModelProperty(value = "不规范字段总数", example = "1")
    private Integer sumBadColumn;
}
