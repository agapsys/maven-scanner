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

public abstract class ScannerDefs {

	public abstract SourceDirectoryScanner getSourceDirectoryScanner();

	public abstract ScanInfo getScanInfoInstance();

	public String getEmbeddedScanInfoFileEncoding() {
		return "UTF-8";
	}

	public abstract String getEmbeddedScanInfoFilename();

	public String getEmbeddedScanInfoFileDirectory() {
		return "META-INF";
	}

	public final String getEmbeddedScanInfoFilePath() {
		String scanInfoFilePath = String.format(
			"%s/%s",
			getEmbeddedScanInfoFileDirectory(), getEmbeddedScanInfoFilename()
		);

		return scanInfoFilePath;
	}
}
