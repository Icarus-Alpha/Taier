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

package com.dtstack.taier.datasource.api.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提供代理使用的 thread factory, 每个 classloader 维护一份, 用于设置线程上下文 classloader
 *
 * @author ：wangchuan
 * date：Created in 14:49 2022/9/23
 * company: www.dtstack.com
 */
public class ProxyThreadFactory implements ThreadFactory {

    private final static AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final static AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final ClassLoader classLoader;
    private final ThreadGroup group;
    private final String namePrefix;

    public ProxyThreadFactory(ClassLoader classLoader, String pluginName) {
        this.classLoader = classLoader;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = pluginName + "-proxy-pool-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + THREAD_NUMBER.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        if (classLoader != null) {
            t.setContextClassLoader(classLoader);
        }
        return t;
    }
}