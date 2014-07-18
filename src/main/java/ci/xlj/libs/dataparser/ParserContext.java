/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//      Contributors:      Xu Lijia 

package ci.xlj.libs.dataparser;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.log4j.Logger;

import ci.xlj.libs.jenkinsvisitor.JenkinsVisitor;
import ci.xlj.libs.dataparser.restapi.APIParser;

/**
 * Parse report data from Jenkins Server through API or customized plugins.<br/>
 * Strategy Pattern is applied here.
 * 
 * @author kfzx-xulj
 *
 */
public class ParserContext {

	private static Logger logger = Logger.getLogger(ParserContext.class);

	private static ParserContext instance;

	private ParserContext() {
	}

	public static ParserContext getInstance() {
		if (instance == null) {
			instance = new ParserContext();
		}

		return instance;
	}

	/**
	 * Parse data by api
	 */
	public String parseData(JenkinsVisitor visitor, String jobName,
			String pluginUrlFragment, List<String> dataItems) {
		if (dataItems.size() > 0) {
			APIParser apiParser = new APIParser(visitor, jobName,
					pluginUrlFragment);
			return apiParser.parse(dataItems);
		} else {
			logger.error("No data items provided for " + jobName);
			return "No data to be analyzed.";
		}
	}

	/**
	 * Parse data by plugin
	 */
	public String parseData(String pluginPath, String pluginClassName,
			String reportFilePath) throws Exception {

		File plugin = new File(pluginPath);

		URLClassLoader ClassLoader = new URLClassLoader(new URL[] { plugin
				.toURI().toURL() });

		Class<?> pluginClass = ClassLoader.loadClass(pluginClassName);

		Object pluginInstance = pluginClass.newInstance();

		Method method = pluginClass.getMethod("parse");

		return method.invoke(pluginInstance, reportFilePath).toString();
	}
}
