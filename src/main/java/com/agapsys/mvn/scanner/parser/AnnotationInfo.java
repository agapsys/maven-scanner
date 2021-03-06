/*
 * Copyright 2016 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agapsys.mvn.scanner.parser;

/**
 * Represents an annotation information
 */
public class AnnotationInfo {
    public String className;
	public String memberValue;

    @Override
    public String toString() {
		if (className == null)
			return null;

		String memberValueStr = memberValue == null ? "" : "(\"\")";

		return String.format("@%s%s", className, memberValueStr);
    }
}
