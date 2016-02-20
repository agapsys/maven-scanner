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
import java.io.File;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Base LIST mojo.
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public abstract class AbstractListMojo extends AbstractMojo {
	// STATIC SCOPE ============================================================
	static void setScanInfo(
		MavenProject mavenProject,
		SourceDirectoryScanner srcDirectoryScanner,
		ScanInfo scanInfoInstance,
		String embeddedScanInfoFilePath,
		String embeddedScanInfoFileEncoding,
		boolean includeDependencies,
		boolean includeTest
	) throws ParsingException {

		// Scan sources...
		List<String> srcDirList = new LinkedList<String>();
		srcDirList.add(mavenProject.getBuild().getSourceDirectory());

		if (includeTest)
			srcDirList.add(mavenProject.getBuild().getTestSourceDirectory());

		for (String srcDir : srcDirList) {
			List<ClassInfo> filteredClasses = srcDirectoryScanner.getFilteredClasses(new File(srcDir));

			for (ClassInfo classInfo : filteredClasses) {
				scanInfoInstance.addClassInfo(classInfo);
			}
		}

		// Scan dependencies...
		if (includeDependencies) {
			Set<Artifact> dependencies = new LinkedHashSet<Artifact>();
			dependencies.addAll(mavenProject.getArtifacts());

			if (includeTest)
				dependencies.addAll(mavenProject.getTestArtifacts());

			for (Artifact artifact : dependencies) {
				scanInfoInstance.addJar(artifact.getFile(), embeddedScanInfoFilePath, embeddedScanInfoFileEncoding);
			}
		}
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	protected abstract MavenProject getMavenProject();

	protected boolean includeDependencies() {
		return true;
	}

	protected boolean includeTests() {
		return false;
	}

	protected abstract String getFilterPropertyName();

	protected abstract String getExposedEntry(String scanInfoEntry);

	protected abstract ScannerDefs getScannerDefs();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

			ScannerDefs defs = getScannerDefs();

			MavenProject mavenProject = getMavenProject();
			ScanInfo scanInfo = defs.getScanInfoInstance();

			setScanInfo(mavenProject,
				defs.getSourceDirectoryScanner(),
				scanInfo,
				defs.getEmbeddedScanInfoFilePath(),
				defs.getEmbeddedScanInfoFileEncoding(),
				includeDependencies(),
				includeTests()
			);

			StringBuilder sb = new StringBuilder();
			for (String scanInfoEntry : scanInfo.getEntries()) {
				sb.append(getExposedEntry(scanInfoEntry));
			}

			mavenProject.getProperties().setProperty(getFilterPropertyName(), sb.toString());
		} catch (ParsingException ex) {
			throw new MojoExecutionException(ex.getMessage());
		}
	}
	// =========================================================================
}
