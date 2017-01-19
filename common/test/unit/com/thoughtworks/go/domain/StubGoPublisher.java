/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.domain;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.go.server.service.AgentRuntimeInfo;
import com.thoughtworks.go.work.DefaultGoPublisher;

import static org.junit.Assert.fail;

public class StubGoPublisher extends DefaultGoPublisher {
    private String message = "";
    private Map<File, String> uploadedFiles = new HashMap<>();
    private boolean shouldFail;

    public StubGoPublisher() {
        this(false);
    }

    public StubGoPublisher(boolean shouldFail) {
        super(null, null, null, AgentRuntimeInfo.initialState(NullAgent.createNullAgent()));
        this.shouldFail = shouldFail;
    }

    protected void init() {
    }

    public void consumeLineWithPrefix(String message) {
        this.message += message;
    }

    public void consumeLine(String message) {
        this.message += message;
    }

    public String getMessage() {
        return message;
    }

    public void upload(File fileToUpload, String destPath) {
        if (shouldFail) {
            throw new RuntimeException("failed on purpose");
        }
        uploadedFiles.put(fileToUpload, destPath);
    }

    public Map<File, String> publishedFiles() {
        return uploadedFiles;
    }

    public void assertPublished(String endOfFileName, String destination) {

        for (File file : uploadedFiles.keySet()) {
            if (file.getAbsolutePath().endsWith(endOfFileName)) {
                org.junit.Assert.assertThat(uploadedFiles.get(file), org.hamcrest.core.Is.is(destination));
                return;
            }
        }
        fail(endOfFileName + " was not published to " + destination + ".\n" + uploadedFiles);
    }
}
