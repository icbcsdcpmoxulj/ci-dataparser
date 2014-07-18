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

package ci.xlj.libs.dataparser.restapi;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import ci.xlj.libs.jenkinsvisitor.JenkinsVisitor;
import ci.xlj.libs.utils.StringUtils;
import ci.xlj.libs.dataparser.Parsable;

/**
 * Parse report data from Jenkins Server through API.<br/>
 * API can be got access to by URI like ".../api/xml" or ".../api/json"
 * 
 * @author kfzx-xulj
 *
 */
public class APIParser implements Parsable<List<String>> {

	private static Logger logger = Logger.getLogger(APIParser.class);

	private JSONObject json;

	private StringBuilder data;

	private String jobName;
	private String pluginUrlFragment;

	public APIParser(JenkinsVisitor visitor, String jobName,
			String pluginUrlFragment) {
		this.jobName = jobName;
		this.pluginUrlFragment = pluginUrlFragment;

		try {
			json = (JSONObject) visitor.getJobReportData(this.jobName,
					"lastSuccessfulBuild", pluginUrlFragment);
		} catch (Exception e) {
			logger.error("Error in visiting last successful build for job \""
					+ jobName + "\". Details:\n"+StringUtils.getStrackTrace(e));
		}

		logger.info("Retrieving data from the last successful build of \""
				+ this.jobName + "\".");
	}

	@Override
	public String parse(List<String> dataItems) {
		if (dataItems.size() > 0) {
			data = new StringBuilder();

			if (json != null) {

				for (int i = 0; i < dataItems.size() - 1; i++) {
					String dataItem = dataItems.get(i);

					if (dataItem.indexOf(".") == -1) {
						data.append(dataItem + ":")
								.append(json == null ? "No such data" : json
										.getString(dataItems.get(i)))
								.append(",");

					} else {
						String result = getEmbeddedJSONValue(dataItem);
						logger.info("Value of \"" + dataItem + "\" is:"
								+ result);

						data.append(dataItem + ":" + result + ",");
					}

				}

				String dataItem = dataItems.get(dataItems.size() - 1);

				if (dataItem.indexOf(".") == -1) {

					data.append(dataItems.get(dataItems.size() - 1) + ":")
							.append(json == null ? "No such data"
									: json.getString(dataItems.get(dataItems
											.size() - 1)));

				} else {
					String result = getEmbeddedJSONValue(dataItem);
					logger.info("Value of \"" + dataItem + "\" is:" + result);

					data.append(dataItem + ":" + result);
				}

			} else {
				logger.error("All builds for job \"" + jobName
						+ "\" have failed.");
			}

			return data.toString();

		} else {
			logger.error("No data items to parse for \""
					+ this.pluginUrlFragment + "\" of \"" + this.jobName + "\"");

			return "No data items to parse.";
		}
	}

	private String getEmbeddedJSONValue(String dataItem) {
		String[] items = dataItem.split("\\.");

		JSONObject jsonObject = null;
		String result = null;

		if (items.length > 0) {

			jsonObject = json.getJSONObject(items[0]);

			for (int i = 1; i < items.length - 1; i++) {

				if (jsonObject == null) {
					result = dataItem + ":No such data,";
					break;
				} else {
					jsonObject = jsonObject.getJSONObject(items[i]);
				}
			}

			result = jsonObject.getString(items[items.length - 1]);

		}

		return result;
	}
}
