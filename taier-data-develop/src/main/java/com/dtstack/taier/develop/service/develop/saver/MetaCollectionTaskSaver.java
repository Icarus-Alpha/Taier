package com.dtstack.taier.develop.service.develop.saver;

import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_GUIDE;
import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_TEMPLATE;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.common.template.Setting;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.service.develop.impl.TaskDirtyDataManageService;
import com.dtstack.taier.develop.service.template.DefaultSetting;
import com.dtstack.taier.develop.service.template.FlinkxJobTemplate;
import com.dtstack.taier.develop.service.template.Restoration;
import com.dtstack.taier.develop.service.template.SyncBuilderFactory;
import com.dtstack.taier.develop.service.template.bulider.nameMapping.NameMappingBuilder;
import com.dtstack.taier.develop.service.template.bulider.nameMapping.NameMappingBuilderFactory;
import com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder;
import com.dtstack.taier.develop.service.template.bulider.writer.DaWriterBuilder;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Classname MetaCollectionTaskSaver
 * @Description TODO 元数据采集任务保存
 * @Date 2024/5/20 上午 11:57
 * @Created by Icarus
 */
@Component
public class MetaCollectionTaskSaver extends AbstractTaskSaver {

    public static Logger LOGGER = LoggerFactory.getLogger(MetaCollectionTaskSaver.class);


    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.METADATA_COLLECTION);
    }


    @Autowired
    private DefaultTaskSaver defaultTaskSaver;



    @Override
    public TaskResourceParam beforeProcessing(TaskResourceParam taskResourceParam) {
        if (taskResourceParam.getUpdateSource()) {
            taskResourceParam.setSourceStr(taskResourceParam.getSourceMap() == null ? ""
                : JSON.toJSONString(taskResourceParam.getSourceMap()));
            taskResourceParam.setTargetStr(taskResourceParam.getTargetMap() == null ? ""
                : JSON.toJSONString(taskResourceParam.getTargetMap()));
        } else {
            Task task = developTaskService.getOne(taskResourceParam.getId());
            taskResourceParam.setSourceStr(task.getSourceStr());
            taskResourceParam.setTargetStr(task.getTargetStr());

        }

        defaultTaskSaver.beforeProcessing(taskResourceParam);
        return taskResourceParam;
    }


    public Map<String, Object> getTargetMap(Map<String, Object> targetMap) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("metadataServiceAddr", targetMap.get("metadataServiceAddr"));
        map.put("groupId", targetMap.get("groupId"));
        map.put("metaModelId", targetMap.get("metaModelId"));
        map.put("collectTaskId", targetMap.get("collectTaskId"));
        map.put("fromSystem", targetMap.get("fromSystem"));
        return map;
    }


}
