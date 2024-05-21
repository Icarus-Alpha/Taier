package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskParamService;
import com.dtstack.taier.develop.service.develop.impl.TaskDirtyDataManageService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Classname MetaCollectionTaskRunner
 * @Description TODO
 * @Date 2024/5/21 下午 2:54
 * @Created by Icarus
 */
public class MetaCollectionTaskRunner  implements ITaskRunner {
    @Autowired
    protected DevelopTaskParamService developTaskParamService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private TaskDirtyDataManageService taskDirtyDataManageService;

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.METADATA_COLLECTION);
    }

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariables) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        return null;
    }

    @Override
    public ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        return null;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        return null;
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema, Long datasourceId) {
        return null;
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long userId, Task task) {
        return null;
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        Map<String, Object> actionParam = Maps.newHashMap();
        try {
            List<DevelopTaskParam> taskParamsToReplace = developTaskParamService.getTaskParam(task.getId());
            addConfPropAndParseJob(actionParam, tenantId, task, taskParamsToReplace);
            String name = "run_meta_collection_task_" + task.getName() + "_" + System.currentTimeMillis();
            actionParam.put("taskSourceId", task.getDatasourceId());
            actionParam.put("taskType", EScheduleJobType.METADATA_COLLECTION.getVal());
            actionParam.put("name", name);
            actionParam.put("computeType", task.getComputeType());
            actionParam.put("sqlText", "");
            actionParam.put("tenantId", tenantId);
            actionParam.put("isFailRetry", false);
            actionParam.put("maxRetryNum", 0);
            //临时运行不做重试
            actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));
        } catch (Exception e) {
            throw new TaierDefineException(String.format("创建元数据同步 job 失败：%s", e.getMessage()), e);
        }

        return actionParam;
    }






    public void addConfPropAndParseJob(Map<String, Object> actionParam, Long tenantId, Task task, List<DevelopTaskParam> taskParamsToReplace) throws Exception {
        String sql = task.getSqlText() == null ? "" : task.getSqlText();
        String taskParams = task.getTaskParams();
        JSONObject syncJob = JSON.parseObject(task.getSqlText());

        String job = syncJob.getString("job");
        // 向导模式根据 job 中的 sourceId 填充数据源信息，保证每次运行取到最新的连接信息
        job = datasourceService.setJobDataSourceInfo(job, tenantId, syncJob.getIntValue("createModel"));

        developTaskParamService.checkParams(developTaskParamService.checkSyncJobParams(job), taskParamsToReplace);

        JSONObject confProp = new JSONObject();

        actionParam.put("job", job);
        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
        actionParam.put("confProp", confProp.toJSONString());
    }
}
