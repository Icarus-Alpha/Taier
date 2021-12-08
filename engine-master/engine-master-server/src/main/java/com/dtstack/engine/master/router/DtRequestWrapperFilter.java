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

package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Component
public class DtRequestWrapperFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(DtRequestWrapperFilter.class);

    public final static String DT_REQUEST_BODY = "DT_REQUEST_BODY";

    private static String[] excludeTargets = {"/node/download/component/downloadFile", "/node/upload/component/config", "/node/upload/component/addOrUpdateComponent",
            "/upload/batch/batchResource/addResource","/upload/batch/batchResource/replaceResource",
            "/node/alert/edit","/node/alert/testAlert","/node/upload/component/parseKerberos", "/node/upload/component/uploadKerberos","/node/login/submit","/node/login/logout"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest(request);

        boolean isExclude = false;
        for (String exc: excludeTargets) {
            if (uri.endsWith(exc)) {
                isExclude = true;
                break;
            }
        }

        LOGGER.info("Uri: " + uri + ", Params: " + getParameterString(requestWrapper));
        JSONObject reqBody;
        if (isExclude) {
            reqBody = new JSONObject();
        } else {
            reqBody = getRequestBodyJson(requestWrapper);
            LOGGER.info("Uri: " + uri + ", Params: " + reqBody);
        }


        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                reqBody.putIfAbsent(cookie.getName(), cookie.getValue());
            }
        }
        request.setAttribute(DT_REQUEST_BODY, reqBody);
        filterChain.doFilter(requestWrapper, response);
    }

    private JSONObject getRequestBodyJson(MultiReadHttpServletRequest requestWrapper) throws IOException {
        try (BufferedReader reader = requestWrapper.getReader()) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            String reqBody = builder.toString();
            if (StringUtils.isNotBlank(reqBody)) {
                JSONObject bodyJson = JSONObject.parseObject(reqBody);
                return bodyJson;
            }
        }
        return new JSONObject();
    }

    private String getParameterString(MultiReadHttpServletRequest requestWrapper) {
        StringBuilder infoBuilder = new StringBuilder();
        Map<String, String[]> map = requestWrapper.getParameterMap();
        for (String key: map.keySet()) {
            String[] params = map.get(key);
            if (params.length == 0) {
                infoBuilder.append(key + ":" + "null ");
            } else if (params.length == 1){
                infoBuilder.append(key + ":" + params[0] + " ");
            } else {
                infoBuilder.append(key + ":" + Arrays.toString(params) + " ");
            }
        }
        return infoBuilder.toString();
    }
}