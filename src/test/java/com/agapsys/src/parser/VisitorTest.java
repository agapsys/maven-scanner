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

package com.agapsys.src.parser;

import com.agapsys.mvn.scanner.parser.AnnotationInfo;
import com.agapsys.mvn.scanner.parser.ClassInfo;
import com.agapsys.mvn.scanner.parser.ParsingException;
import com.agapsys.mvn.scanner.parser.SourceFileInfo;
import java.io.File;
import junit.framework.Assert;
import org.junit.Test;

public class VisitorTest {

	@Test
	public void test() throws ParsingException {
		ClassInfo classInfo;
		SourceFileInfo srcFileInfo = SourceFileInfo.getInfo(new File("TestClass.java"));
		Assert.assertEquals(2, srcFileInfo.classes.size());

		classInfo = srcFileInfo.classes.get(0);

		Assert.assertEquals(2, classInfo.annotations.size());

		// Annotations...
		AnnotationInfo annotationInfo = classInfo.annotations.get(0);
		Assert.assertEquals("f.g.h.Annotation1", annotationInfo.className);
		Assert.assertEquals(7, annotationInfo.properties.size());
		Assert.assertEquals("1", annotationInfo.properties.get("a"));
		Assert.assertEquals("2", annotationInfo.properties.get("b"));
		Assert.assertEquals("\"abc\"", annotationInfo.properties.get("c"));
		Assert.assertEquals("com.example1.Type.VALUE", annotationInfo.properties.get("d"));
		Assert.assertEquals("com.example.Type.VALUE", annotationInfo.properties.get("e"));
		Assert.assertEquals("1.0", annotationInfo.properties.get("f"));
		Assert.assertEquals("1.0f", annotationInfo.properties.get("g"));

		annotationInfo = classInfo.annotations.get(1);
		Assert.assertEquals("Annotation4", annotationInfo.className);
		Assert.assertEquals(0, annotationInfo.properties.size());

		// Class info...
		Assert.assertEquals("com.agapsys.src.parser.TestClass", classInfo.className);
		Assert.assertEquals("a.b.c.SuperClass1", classInfo.superClassName);
		Assert.assertEquals("a.b.c.SuperClass1", classInfo.superClassName);

		Assert.assertEquals(2, classInfo.implementedInterfaces.size());
		Assert.assertEquals("SuperInterface1", classInfo.implementedInterfaces.get(0));
		Assert.assertEquals("aa.bb.cc.dd.SuperInterface1", classInfo.implementedInterfaces.get(1));

		// Methods...
		Assert.assertEquals(7, classInfo.methods.size());
		Assert.assertEquals(1, classInfo.methods.get(4).annotations.size());
		Assert.assertEquals("aa.bb.cc.dd.Annotation1", classInfo.methods.get(4).annotations.get(0).className);

		// Inner class
		classInfo = srcFileInfo.classes.get(1);
		Assert.assertEquals("com.agapsys.src.parser.TestClass.InnerClass", classInfo.className);
		Assert.assertEquals("com.agapsys.src.parser.TestClass$InnerClass", classInfo.reflectionClassName);
	}
}
