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

package ci.xlj.libs.dataparser.plugin;

import ci.xlj.libs.dataparser.Parsable;

/**
 * Parse report(like PMD/FindBugs/Cobertura for statistics purposes) data <br/>
 * from "customized plugins", which means u should isolate the parsing algorithms<br/>
 * from related report plugins, such as PMD/FindBugs etc., and make it implement <br/>
 * the "parse" method here.
 * 
 * @author kfzx-xulj
 * 
 */
public abstract class PluginParser implements Parsable<String> {

	@Override
	public abstract String parse(String reportFilePath);

}
