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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a source file structure
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class SourceFileInfo {
	// STATIC SCOPE ============================================================
	public static SourceFileInfo getInfo(File file) throws ParsingException {
		SourceFileInfo info = new SourceFileInfo(file);

		FileInputStream fis = null;
		CompilationUnit cu = null;

		try {
			fis = new FileInputStream(file);
			cu = JavaParser.parse(fis);
		} catch (ParseException ex) {
			throw new ParsingException(ex);
		} catch (IOException ex) {
			throw new ParsingException(ex);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				throw new ParsingException(ex);
			}
		}

		ClassVisitor cv = new ClassVisitor(info);
		cv.visit(cu, null);

		return info;
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private SourceFileInfo(File srcFile) {
		this.sourceFile = srcFile;
	}
	
	public final File sourceFile;
	
	public final Set<ClassInfo> classes = new LinkedHashSet<ClassInfo>();
	
	public ClassInfo getClassInfoByClassName(String className) {
		for (ClassInfo classInfo : classes) {
			if (classInfo.className.equals(className))
				return classInfo;
		}
		
		return null;
	}
	
	public ClassInfo getClassInfoBySimpleName(String simpleName) {
		// According to JLS, inner classes must have unique name inside enclosing classes:
		// http://stackoverflow.com/a/24214835
		for (ClassInfo classInfo : classes) {
			if (classInfo.getSimpleName().equals(simpleName))
				return classInfo;
		}
		
		return null;
		
	}
	
	
	@Override
	public String toString() {
		return sourceFile.toString();
	}
	// =========================================================================

}
