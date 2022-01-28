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

package com.dtstack.taiga.scheduler.utils;

import com.dtstack.taiga.common.exception.RdosDefineException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-25
 */
public class FileUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 解析文件 每一行带换行符
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static String getContentFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()) {
            StringBuilder content = new StringBuilder();
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error read file content.", e);
            }
            return content.toString();
        }
        throw new FileNotFoundException("File " + filePath + " not exists.");
    }

    public static List<PrincipalName> getPrincipal(File file) {
        if (file == null || !file.exists()) {
            throw new RdosDefineException("The current keytab file does not exist");
        }
        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(file);
        } catch (IOException e) {
            LOGGER.error("Keytab loadKeytab error ", e);
            throw new RdosDefineException("Failed to parse keytab file");
        }
        if (keytab == null || CollectionUtils.isEmpty(keytab.getPrincipals())) {
            throw new RdosDefineException("The current keytab file does not contain principal information");
        }
        return keytab.getPrincipals();
    }
}