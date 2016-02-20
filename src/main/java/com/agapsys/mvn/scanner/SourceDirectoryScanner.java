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
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SourceDirectoryScanner {
	/**
	 * Parse a source directory
	 * @param srcDirOrFile source directory to be analyzed.
	 * @return List containing filtered classes
	 * @throws ParsingException if an error happened during parsing.
	 */
	public final Set<ClassInfo> getFilteredClasses(File srcDirOrFile) throws ParsingException {
		return parseDirOrFile(srcDirOrFile, null);
	}

	/**
	 * Returns a boolean indicating if given class information shall be included in processed scan information.
	 * @param classInfo class information to be evaluated
	 * @return boolean indicating if given class information shall be included in processed scan information.
	 */
	protected abstract boolean isValid(ClassInfo classInfo);

	/**
	 * Parses a directory
	 * @param srcDirOrFile directory or source file to be analyzed
	 * @param classInfoSet set which will contain returned results. On non-recursive calls, pass null.
	 * @throws ParsingException if an error happened during parsing.
	 */
	private Set<ClassInfo> parseDirOrFile(File srcDirOrFile, Set<ClassInfo> classInfoSet) throws ParsingException {
		if (classInfoSet == null)
			classInfoSet = new LinkedHashSet<ClassInfo>();

		if (srcDirOrFile.isFile()) {
			parseFile(srcDirOrFile, classInfoSet);
		} else {
			for (File file : srcDirOrFile.listFiles()) {
				if (file.isDirectory()) {
					parseDirOrFile(file, classInfoSet);
				} else {
					parseFile(file, classInfoSet);
				}
			}
		}

		return classInfoSet;
	}

	/**
	 * Parses a source file
	 * @param srcFile source file to be analyzed.
	 * @param classInfoSet list which will contain returned results.
	 * @throws ParsingException if an error happened during parsing.
	 */
	private void parseFile(File srcFile, Set<ClassInfo> classInfoSet) throws ParsingException {
		SourceFileInfo srcFileInfo = SourceFileInfo.getInfo(srcFile);

		for (ClassInfo classInfo : srcFileInfo.classes) {
			if (isValid(classInfo))
				classInfoSet.add(classInfo);
		}
	}
}
