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

package com.agapsys.mvn.scanner;

import com.agapsys.mvn.scanner.parser.ClassInfo;
import com.agapsys.mvn.scanner.parser.ParsingException;
import com.agapsys.mvn.scanner.parser.SourceFileInfo;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class SourceDirectoryScanner {
	/**
	 * Parse a source directory
	 * @param srcDir source directory to be analyzed.
	 * @return List containing filtered classes
	 * @throws ParsingException if an error happened during parsing.
	 */
	public final List<ClassInfo> getFilteredClasses(File srcDir) throws ParsingException {
		return parseDirectory(srcDir, null);
	}

	/**
	 * Returns a boolean indicating if given class information shall be included in processed scan information.
	 * @param classInfo class information to be evaluated
	 * @return boolean indicating if given class information shall be included in processed scan information.
	 */
	protected abstract boolean isValid(ClassInfo classInfo);

	/**
	 * Parses a directory
	 * @param srcDir directory to be analyzed
	 * @param classInfoList list which will contain returned results. On non-recursive calls, pass null.
	 * @throws ParsingException if an error happened during parsing.
	 */
	private List<ClassInfo> parseDirectory(File srcDir, List<ClassInfo> classInfoList) throws ParsingException {
		if (classInfoList == null)
			classInfoList = new LinkedList<ClassInfo>();

		for (File file : srcDir.listFiles()) {
			if (file.isDirectory()) {
				parseDirectory(file, classInfoList);
			} else {
				parseFile(file, classInfoList);
			}
		}

		return classInfoList;
	}

	/**
	 * Parses a source file
	 * @param srcFile source file to be analyzed.
	 * @param classInfoList list which will contain returned results.
	 * @throws ParsingException if an error happened during parsing.
	 */
	private void parseFile(File srcFile, List<ClassInfo> classInfoList) throws ParsingException {
		SourceFileInfo srcFileInfo = SourceFileInfo.getInfo(srcFile);

		for (ClassInfo classInfo : srcFileInfo.classes) {
			if (isValid(classInfo))
				classInfoList.add(classInfo);
		}
	}
}
