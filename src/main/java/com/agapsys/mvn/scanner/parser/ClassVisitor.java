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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedList;
import java.util.List;

/**
 * Source file parser
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
class ClassVisitor extends VoidVisitorAdapter {
	// CLASS SCOPE =============================================================
	private static String getClassName(String identifier, List<String> imports) {
		for (String importStr : imports) {
			if (importStr.endsWith(identifier))
				return importStr;
		}

		return identifier;
	}

	private static String getReflectionClassName(String packageName, String containerClass, String simpleName) {

		if (!containerClass.isEmpty())
			return containerClass + "$" + simpleName;

		if (packageName.isEmpty()) {
			return simpleName;
		} else {
			return packageName + "." + simpleName;
		}
	}

	private static String getClassName(String packageName, String containerClass, String simpleName) {

		if (!containerClass.isEmpty())
			return containerClass + "." + simpleName;

		if (packageName.isEmpty()) {
			return simpleName;
		} else {
			return packageName + "." + simpleName;
		}
	}

	private static ClassInfo getClassInfo(ClassInfo containerClass, String packageName, List<String> imports, ClassOrInterfaceDeclaration cid) {
		ClassInfo classInfo = new ClassInfo();

		String containerClassName = containerClass != null ? containerClass.className : "";
		String containerClassReflectionName = containerClass != null ? containerClass.reflectionClassName : "";

		classInfo.className = getClassName(packageName, containerClassName, cid.getName().toString());
		classInfo.reflectionClassName = getReflectionClassName(packageName, containerClassReflectionName, cid.getName().toString());
		classInfo.visibility = Visibility.fromModifiers(cid.getModifiers());
		classInfo.isAbstract = ModifierSet.isAbstract(cid.getModifiers());
		classInfo.isInterface = cid.isInterface();
		classInfo.superClassName = getClassName(cid.getExtends().get(0).toString(), imports);

		for (ClassOrInterfaceType id : cid.getImplements()) {
			classInfo.implementedInterfaces.add(getClassName(id.toString(), imports));
		}

		for (AnnotationExpr ae : cid.getAnnotations()) {
			AnnotationInfo annotation = getAnnotationInfo(packageName, imports, ae);
			classInfo.annotations.add(annotation);
		}

		for (BodyDeclaration member : cid.getMembers()) {
			if (member instanceof MethodDeclaration) {
				classInfo.methods.add(getMethodInfo(packageName, imports, (MethodDeclaration) member));
			}
		}

		return classInfo;
	}

	private static AnnotationInfo getAnnotationInfo(String packgeName, List<String> imports, AnnotationExpr ae) {
		AnnotationInfo annotation = new AnnotationInfo();
		annotation.className = getClassName(ae.getName().toString(), imports);
		if (ae.getChildrenNodes().size() > 1) {
			for (int i = 1; i < ae.getChildrenNodes().size(); i++) {
				MemberValuePair pair = (MemberValuePair) ae.getChildrenNodes().get(i);
				String name = pair.getName();

				String value = getAnnotationParamValue(pair.getValue(), imports);

				annotation.properties.put(name, value);
			}
		}

		return annotation;
	}

	private static MethodInfo getMethodInfo(String packageName, List<String> imports, MethodDeclaration md) {
		MethodInfo info = new MethodInfo();

		for (AnnotationExpr ae : md.getAnnotations()) {
			AnnotationInfo annotation = getAnnotationInfo(packageName, imports, ae);
			info.annotations.add(annotation);
		}

		info.name = md.getName();
		info.isStatic = ModifierSet.isStatic(md.getModifiers());
		info.visibility = Visibility.fromModifiers(md.getModifiers());
		info.signature = md.getDeclarationAsString();

		return info;
	}

	private static final String getAnnotationParamValue(Object expr, List<String> imports) {
		String str = expr.toString();

		if (expr instanceof FieldAccessExpr) {
			FieldAccessExpr fae = (FieldAccessExpr) expr;
			String className = getClassName(fae.getScope().toString(), imports);
			return String.format("%s.%s", className, fae.getField());
		} else if (expr instanceof IntegerLiteralExpr) {
			return str;
		} else if (expr instanceof LongLiteralExpr) {
			return str;
		} else if (expr instanceof DoubleLiteralExpr) {
			return str;
		} else if (expr instanceof StringLiteralExpr) {
			return str;
		}

		return null;
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	private final SourceFileInfo sourceFileInfo;

	public ClassVisitor(SourceFileInfo sourceFileInfo) {
		this.sourceFileInfo = sourceFileInfo;
	}

	private String currentPackage;
	private ClassInfo currentClass;
	private List<String> imports;

	@Override
	public void visit(CompilationUnit n, Object arg) {
		currentPackage = n.getPackage() != null ? n.getPackage().getName().toString() : "";

		imports = new LinkedList<String>();
		for (ImportDeclaration importDec : n.getImports()) {
			imports.add(importDec.getName().toString());
		}
		super.visit(n, arg);
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		currentClass = getClassInfo(currentClass, currentPackage, imports, n);
		sourceFileInfo.classes.add(currentClass);
		super.visit(n, arg);
	}
	// =========================================================================


}
