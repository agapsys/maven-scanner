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

import com.agapsys.mvn.scanner.parser.ParsingException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Base CREATE Mojo.
 */
public abstract class AbstractCreateMojo extends AbstractMojo {

    protected abstract MavenProject getMavenProject();

    protected boolean includeDependencies() {
        return false;
    }

    protected boolean includeTests() {
        return false;
    }

    protected abstract ScannerDefs getScannerDefs();

    @Override
    public void execute() throws MojoExecutionException {
        try {

            ScannerDefs defs = getScannerDefs();
            MavenProject mavenProject = getMavenProject();
            ScanInfo scanInfo = defs.getScanInfoInstance();
            boolean includeTests = includeTests();

            AbstractListMojo.setScanInfo(
                mavenProject,
                defs.getSourceDirectoryScanner(),
                scanInfo,
                defs.getEmbeddedScanInfoFilePath(),
                defs.getEmbeddedScanInfoFileEncoding(),
                includeDependencies(),
                includeTests
            );

            String outputDirectoryPath = String.format(
                "%s%s%s",
                includeTests ? mavenProject.getBuild().getTestOutputDirectory() :  mavenProject.getBuild().getOutputDirectory(),
                FileUtils.FOLDER_DELIMITER,
                defs.getEmbeddedScanInfoFileDirectory()
            );

            File outputDirectory = FileUtils.getOrCreateDirectory(outputDirectoryPath);
            File outputFile = new File(outputDirectory, defs.getEmbeddedScanInfoFilename());

            String ioErrMsg = "Error generating file: " + outputFile.getAbsolutePath();

            try {
                PrintWriter writer = new PrintWriter(outputFile);

                for (String entry : scanInfo.getEntries()) {
                    writer.println(entry);
                }

                writer.close();
            } catch (IOException ex) {
                throw new MojoExecutionException(ioErrMsg);
            }

        } catch (ParsingException ex) {
            throw new MojoExecutionException(ex.getMessage());
        }
    }
}
