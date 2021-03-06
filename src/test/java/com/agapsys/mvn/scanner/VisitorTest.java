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

import com.agapsys.mvn.scanner.parser.AnnotationInfo;
import com.agapsys.mvn.scanner.parser.ClassInfo;
import com.agapsys.mvn.scanner.parser.ParsingException;
import com.agapsys.mvn.scanner.parser.SourceFileInfo;
import com.agapsys.mvn.scanner.parser.Visibility;
import java.io.File;
import java.util.Iterator;
import junit.framework.Assert;
import org.junit.Test;

public class VisitorTest {

    @Test
    public void test() throws ParsingException {
        ClassInfo classInfo;
        
        File sourceFile = new File("TestClass.java");
        
        SourceFileInfo sourceFileInfo = SourceFileInfo.getInfo(sourceFile);
        Assert.assertEquals(sourceFile, sourceFileInfo.sourceFile);
        
        Assert.assertEquals(4, sourceFileInfo.classes.size());

        Iterator<ClassInfo> iterator = sourceFileInfo.classes.iterator();
        
        classInfo = iterator.next();
        Assert.assertEquals(sourceFile, classInfo.sourceFileInfo.sourceFile);
        Assert.assertEquals(2, classInfo.annotations.size());

        // Annotations...
        AnnotationInfo annotationInfo = classInfo.annotations.get(0);
        Assert.assertEquals("f.g.h.Annotation1", annotationInfo.className);

        annotationInfo = classInfo.annotations.get(1);
        Assert.assertEquals("com.agapsys.src.parser.Annotation4", annotationInfo.className);

        // Class info...
        Assert.assertEquals("com.agapsys.src.parser.TestClass", classInfo.className);
        Assert.assertEquals("a.b.c.SuperClass1", classInfo.superclassName);
        Assert.assertEquals("a.b.c.SuperClass1", classInfo.superclassName);
        Assert.assertNull(classInfo.containerClass);

        Assert.assertEquals(2, classInfo.implementedInterfaces.size());
        Assert.assertEquals("com.agapsys.src.parser.SuperInterface1", classInfo.implementedInterfaces.get(0));
        Assert.assertEquals("aa.bb.cc.dd.SuperInterface1", classInfo.implementedInterfaces.get(1));

        // Methods...
        Assert.assertEquals(7, classInfo.methods.size());
        Assert.assertEquals(1, classInfo.methods.get(4).annotations.size());
        Assert.assertEquals("aa.bb.cc.dd.Annotation1", classInfo.methods.get(4).annotations.get(0).className);

        // Inner class
        classInfo = iterator.next();
        Assert.assertEquals(sourceFile, classInfo.sourceFileInfo.sourceFile);
        Assert.assertEquals("com.agapsys.src.parser.TestClass.InnerClass", classInfo.className);
        Assert.assertEquals("com.agapsys.src.parser.TestClass$InnerClass", classInfo.reflectionClassName);
        Assert.assertEquals("com.agapsys.src.parser.TestClass", classInfo.containerClass.className);
        Assert.assertTrue(classInfo.isStaticNested);
        
        // Deep class
        classInfo = iterator.next();
        Assert.assertEquals(sourceFile, classInfo.sourceFileInfo.sourceFile);
        Assert.assertEquals("com.agapsys.src.parser.TestClass.InnerClass.DeepClass", classInfo.className);
        Assert.assertEquals("com.agapsys.src.parser.TestClass$InnerClass$DeepClass", classInfo.reflectionClassName);
        Assert.assertEquals("com.agapsys.src.parser.TestClass.InnerClass", classInfo.containerClass.className);
        Assert.assertTrue(classInfo.isStaticNested);

        // Inner enum
        classInfo = iterator.next();
        Assert.assertEquals(sourceFile, classInfo.sourceFileInfo.sourceFile);
        Assert.assertTrue(classInfo.isEnum);
        Assert.assertEquals(Visibility.DEFAULT, classInfo.visibility);
        Assert.assertEquals("com.agapsys.src.parser.TestClass.InnerEnum", classInfo.className);
        Assert.assertEquals("com.agapsys.src.parser.TestClass$InnerEnum", classInfo.reflectionClassName);
        Assert.assertEquals("com.agapsys.src.parser.TestClass", classInfo.containerClass.className);
        Assert.assertFalse(classInfo.isStaticNested);

    }
}
