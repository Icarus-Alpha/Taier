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

package com.dtstack.batch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.JavaPolicyUtils;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/07/08
 */
@SpringBootApplication(exclude = {
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
@EnableScheduling
@MapperScan("com.dtstack.engine.mapper")
public class EngineApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(EngineApplication.class);

    public static void main(String[] args) {
        try {
            SystemPropertyUtil.setSystemUserDir();
            JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
            SpringApplication application = new SpringApplication(EngineApplication.class);
            application.run(args);
            System.setSecurityManager(new NoExitSecurityManager());


            ShutdownHookUtil.addShutdownHook(EngineApplication::shutdown, EngineApplication.class.getSimpleName(), LOGGER);
            JavaPolicyUtils.checkJavaPolicy();
        } catch (Throwable t) {
            LOGGER.error("start error:", t);
            System.exit(-1);
        } finally {
            LOGGER.info("engine-master start end...");
        }
    }

    private static void shutdown() {
        LOGGER.info("EngineMain is shutdown...");
    }

}