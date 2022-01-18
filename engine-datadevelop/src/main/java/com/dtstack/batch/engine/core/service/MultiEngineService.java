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

package com.dtstack.batch.engine.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.domain.TenantEngine;
import com.dtstack.batch.engine.core.domain.MultiEngineFactory;
import com.dtstack.batch.engine.rdbms.hive.util.SparkThriftConnectionUtils;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.datasource.impl.DatasourceService;
import com.dtstack.batch.service.datasource.impl.IMultiEngineService;
import com.dtstack.batch.service.impl.TenantEngineService;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.engine.JdbcInfo;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.EScriptType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.master.service.ComponentService;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 和console交互获取多集群的配置信息
 * Date: 2019/4/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class MultiEngineService implements IMultiEngineService {

    private static final Logger LOG = LoggerFactory.getLogger(MultiEngineService.class);

    @Autowired
    private TenantEngineService tenantEngineService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    public ComponentService componentService;

    // 需要拼接jdbcUrl的引擎类型
    private final static Set<Integer> buildUrlEngineType = Sets.newHashSet(MultiEngineType.HADOOP.getType(), MultiEngineType.LIBRA.getType(),
            MultiEngineType.ANALYTICDB_FOR_PG.getType());

    // 需要拼接schema的引擎类型
    private final static Set<Integer> buildUrlWithSchemaEngineType = Sets.newHashSet(MultiEngineType.LIBRA.getType(), MultiEngineType.ANALYTICDB_FOR_PG.getType());

    @Override
    public List<Integer> getTenantSupportMultiEngine(Long dtuicTenantId) {
        return null;
    }

    /**
     * 从console获取Hadoop的meta数据源
     * @param tenantId
     * @return
     */
    @Override
    public DataSourceType getTenantSupportHadoopMetaDataSource(Long tenantId) {
        Integer metaComponent = Engine2DTOService.getMetaComponent(tenantId);
        if (EComponentType.HIVE_SERVER.getTypeCode().equals(metaComponent)){
            JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, null, EJobType.HIVE_SQL);
            SparkThriftConnectionUtils.HiveVersion hiveVersion = SparkThriftConnectionUtils.HiveVersion.getByVersion(jdbcInfo.getVersion());
            if (SparkThriftConnectionUtils.HiveVersion.HIVE_1x.equals(hiveVersion)){
                return DataSourceType.HIVE1X;
            }else if (SparkThriftConnectionUtils.HiveVersion.HIVE_3x.equals(hiveVersion)){
                return DataSourceType.HIVE3X;
            }else {
                return DataSourceType.HIVE;
            }
        }
        if (EComponentType.SPARK_THRIFT.getTypeCode().equals(metaComponent)){
            return DataSourceType.SparkThrift2_1;
        }
        throw new RdosDefineException("not find 'Hadoop' meta DataSource!");
    }

    /**
     * @param tenantId
     * @return
     */
    @Override
    public List<EJobType> getTenantSupportJobType(Long tenantId) {
        List<Component> engineSupportVOS = componentService.listComponents(tenantId);
        if(CollectionUtils.isEmpty(engineSupportVOS)){
            throw new DtCenterDefException("该租户 console 未配置任何 集群");
        }
        List<Integer> tenantSupportMultiEngine = engineSupportVOS.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(tenantSupportMultiEngine)) {
            List<Integer> usedEngineTypeList = tenantEngineService.getUsedEngineTypeList(tenantId);

            List<EComponentType> componentTypeByEngineType = MultiEngineFactory.getComponentTypeByEngineType(usedEngineTypeList);
            List<Integer> userEcomponentList = componentTypeByEngineType.stream().map(EComponentType::getTypeCode).collect(Collectors.toList());
            //项目配置  和 租户 支持 引擎交集
            Sets.SetView<Integer> intersection = Sets.intersection(new HashSet<>(tenantSupportMultiEngine), new HashSet<>(userEcomponentList));
            // 任务类型有 顺序
            if (CollectionUtils.isNotEmpty(intersection)) {
                //console 配置组件
                List<EJobType> supportJobTypes = this.convertComponentTypeToJobType(new ArrayList<>(intersection));
                //排序
                supportJobTypes.sort(Comparator.comparing(EJobType::getSort));
                return supportJobTypes;
            }
        }

        return new ArrayList<>();
    }

    /**
     * 根据console端配置配置组件 转换为对应支持job类型
     * @return
     */
    private List<EJobType> convertComponentTypeToJobType(List<Integer> component) {
        List<EJobType> supportType = new ArrayList<>();
        supportType.add(EJobType.WORK_FLOW);
        supportType.add(EJobType.VIRTUAL);
        if(!CollectionUtils.isEmpty(component)){
            if(component.contains(EComponentType.HDFS.getTypeCode()) || component.contains(EComponentType.YARN.getTypeCode())){
                //hdfs 对应hadoopMR
                supportType.add(EJobType.HADOOP_MR);
            }
            if(component.contains(EComponentType.FLINK.getTypeCode())){
                supportType.add(EJobType.SYNC);
            }

            if(component.contains(EComponentType.SPARK.getTypeCode())){
                supportType.add(EJobType.SPARK_SQL);
                supportType.add(EJobType.SPARK);
                supportType.add(EJobType.SPARK_PYTHON);
            }
            if(component.contains(EComponentType.DT_SCRIPT.getTypeCode())){
                supportType.add(EJobType.PYTHON);
                supportType.add(EJobType.SHELL);
            }
            if(component.contains(EComponentType.CARBON_DATA.getTypeCode())){
                supportType.add(EJobType.CARBON_SQL);
            }
            if(component.contains(EComponentType.LIBRA_SQL.getTypeCode())){
                supportType.add(EJobType.GaussDB_SQL);
            }
            if(component.contains(EComponentType.TIDB_SQL.getTypeCode())){
                supportType.add(EJobType.TIDB_SQL);
            }
            if(component.contains(EComponentType.ORACLE_SQL.getTypeCode())){
                supportType.add(EJobType.ORACLE_SQL);
            }
            if(component.contains(EComponentType.HIVE_SERVER.getTypeCode())){
                supportType.add(EJobType.HIVE_SQL);
            }
            if (component.contains(EComponentType.IMPALA_SQL.getTypeCode())) {
                supportType.add(EJobType.IMPALA_SQL);
            }
            if (component.contains(EComponentType.GREENPLUM_SQL.getTypeCode())) {
                supportType.add(EJobType.GREENPLUM_SQL);
            }
            if (component.contains(EComponentType.INCEPTOR_SQL.getTypeCode())) {
                supportType.add(EJobType.INCEPTOR_SQL);
            }
            if (component.contains(EComponentType.DTSCRIPT_AGENT.getTypeCode())) {
                supportType.add(EJobType.SHELL_ON_AGENT);
            }
            if (component.contains(EComponentType.ANALYTICDB_FOR_PG.getTypeCode())) {
                supportType.add(EJobType.ANALYTICDB_FOR_PG);
            }
        }
        return supportType;
    }


    @Override
    public List<EScriptType> getTenantSupportScriptType(Long tenantId) {
        return null;
    }


    @Override
    public EngineInfo getEnginePluginInfo(Long tenantId, Integer engineType) {
        String jsonStr = Engine2DTOService.getEnginePluginInfo(tenantId, engineType);
        if (StringUtils.isEmpty(jsonStr)) {
            throw new RdosDefineException(String.format("该租户 console 集群类型 %d 未配置任何插件.", engineType));
        }

        Map<String, String> pluginMap;
        try {
            pluginMap = JSONObject.parseObject(jsonStr, HashMap.class);
        } catch (Exception e) {
            LOG.error("get engine plugin with tenantId:{}, type:{} , consoleResultJson:{}", tenantId, engineType, jsonStr);
            throw new RdosDefineException(String.format("解析 console 返回json 失败。原因是：%s", e.getMessage()));
        }

        if (MapUtils.isEmpty(pluginMap)) {
            throw new RdosDefineException("解析 console 返回json 为空! consoleResultJson:" + jsonStr);
        }

        EngineInfo engineInfo = MultiEngineFactory.createEngineInfo(engineType);
        //如果是Hadoop、Libra、ADB For PG 引擎，则需要替换jdbc信息中的%s为数据库名称
        if (buildUrlEngineType.contains(engineType)){
            int eComponentType;
            //处理Libra引擎
            if(MultiEngineType.LIBRA.getType() == engineType){
                eComponentType = EComponentType.LIBRA_SQL.getTypeCode();
            } else if(MultiEngineType.ANALYTICDB_FOR_PG.getType() == engineType){
                eComponentType = EComponentType.ANALYTICDB_FOR_PG.getTypeCode();
            } else {
                //处理Hadoop引擎，Hadoop引擎则获取mete数据源
                DataSourceType dataSourceType = this.getTenantSupportHadoopMetaDataSource(tenantId);
                eComponentType = datasourceService.getEComponentTypeByDataSourceType(dataSourceType.getVal());
                pluginMap.put("metePluginInfo", pluginMap.get(eComponentType + ""));
            }

            TenantEngine projectEngine = tenantEngineService.getByTenantAndEngineType(tenantId, engineType);
            String dbName = projectEngine == null? null : projectEngine.getEngineIdentity();
            String pluginInfo = pluginMap.get(eComponentType + "");
            if (StringUtils.isNotBlank(dbName) && StringUtils.isNotBlank(pluginInfo)) {
                JSONObject pluginInfoJsonObj = JSONObject.parseObject(pluginInfo);
                if(pluginInfoJsonObj != null  && pluginInfoJsonObj.getString("jdbcUrl") != null){
                    //用dbName替换console返回的jdbcUrl中的%s
                    pluginInfoJsonObj.put("jdbcUrl", buildUrl(pluginInfoJsonObj.getString("jdbcUrl"), dbName, engineType));
                    pluginMap.put(eComponentType + "", pluginInfoJsonObj.toJSONString());
                    //在下面的init方法中不知道Hadoop的组件类型，所以Hadoop使用单独的字段存放jdbc连接信息
                    if (engineType.equals(MultiEngineType.HADOOP.getType())){
                        pluginMap.put("metePluginInfo", pluginInfoJsonObj.toJSONString());
                    }
                }
            }
        }

        if (Objects.nonNull(engineInfo)) {
            engineInfo.init(pluginMap);
        }
        return engineInfo;
    }

    /**
     * 拼接jdbcUrl中db或者schema信息
     *
     * @param jdbcUrl
     * @param dbName
     * @param engineType
     * @return
     */
    private String buildUrl(String jdbcUrl, String dbName, Integer engineType) {
        if (buildUrlWithSchemaEngineType.contains(engineType)) {
            Map<String, String> params = new HashMap<>();
            params.put("currentSchema", dbName);
            return Engine2DTOService.buildJdbcURLWithParam(jdbcUrl, params);
        }
        return Engine2DTOService.buildUrlWithDb(jdbcUrl, dbName);
    }

}
