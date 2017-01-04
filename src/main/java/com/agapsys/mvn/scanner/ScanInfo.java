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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.TreeSet;

/**
 * Scan information
 */
public abstract class ScanInfo {
    private final Set<String> entryList = new TreeSet<String>();

    public Set<String> getEntries() {
        return entryList;
    }

    public void addJar(File jarFile, String embeddedFile, String encoding) throws ParsingException {
        InputStream is = null;
        try {
            URL[] urls = {jarFile.toURI().toURL()};
            ClassLoader cl = new URLClassLoader(urls);

            is = cl.getResourceAsStream(embeddedFile);

            if (is == null)
                return;

            BufferedReader in = new BufferedReader(new InputStreamReader(is, encoding));

            String readLine;

            while ((readLine = in.readLine()) != null) {
                readLine = readLine.trim();

                if (readLine.isEmpty() || readLine.startsWith("#"))
                    continue;

                getEntries().add(readLine);
            }

            in.close();

        } catch (MalformedURLException ex) {
            throw new ParsingException(ex);
        } catch (IOException ex) {
            throw new ParsingException(ex);
        } finally {
            if (is != null)
                try {
                    is.close();
            } catch (IOException ex) {
                throw new ParsingException(ex);
            }
        }
    }

    protected void addEntry(String entry) {
        getEntries().add(entry);
    }

    public void addClassInfo(ClassInfo classInfo) {
        addEntry(getEntryString(classInfo));
    }

    protected abstract String getEntryString(ClassInfo classInfo);
}
