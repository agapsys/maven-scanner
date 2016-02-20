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
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
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
import java.util.regex.Pattern;

/**
 * Source file parser
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
class ClassVisitor extends VoidVisitorAdapter {
	// CLASS SCOPE =============================================================
	private static String getClassName(String packageName, String identifier, List<String> imports) {
		int genericBegin = identifier.indexOf("<");
		int genericEnd = identifier.lastIndexOf(">");
		
		if (genericBegin != -1 && genericEnd != -1) {
			String genericSubStr = identifier.substring(genericBegin, genericEnd + 1);
			identifier = identifier.replaceAll(Pattern.quote(genericSubStr), "");
		}
			
		for (String importStr : imports) {
			if (importStr.endsWith("." + identifier))
				return importStr;
		}

		if (!identifier.contains("."))		
			return packageName + "." + identifier;
		
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

	private static ClassInfo getClassInfo(SourceFileInfo sourceFileInfo, String packageName, List<String> imports, TypeDeclaration td) {
		ClassInfo classInfo = new ClassInfo();
		classInfo.sourceFileInfo = sourceFileInfo;

		ClassInfo containerClass;
		if (td.getParentNode() instanceof TypeDeclaration) {
			TypeDeclaration containerTd = (TypeDeclaration) td.getParentNode();
			String parentClassSimpleName = containerTd.getName().toString();
			containerClass = sourceFileInfo.getClassInfoBySimpleName(parentClassSimpleName);
		} else {
			containerClass = null;
		}

		classInfo.containerClass = containerClass;
		classInfo.className = getClassName(packageName, containerClass == null ? "" : containerClass.className, td.getName().toString());
		classInfo.isStaticNested = containerClass != null && ModifierSet.isStatic(td.getModifiers());
		classInfo.reflectionClassName = getReflectionClassName(packageName, containerClass == null ? "" : containerClass.reflectionClassName, td.getName().toString());
		classInfo.visibility = Visibility.fromModifiers(td.getModifiers());
		classInfo.isAbstract = ModifierSet.isAbstract(td.getModifiers());
		
		if (td instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) td;
			classInfo.isInterface = cid.isInterface();
		
			List extendsList = cid.getExtends();		
			classInfo.superClassName = extendsList == null || extendsList.isEmpty() ? null : getClassName(packageName, extendsList.get(0).toString(), imports);

			List<ClassOrInterfaceType> implementList = cid.getImplements();
		
			if (implementList == null)
				implementList = new LinkedList<ClassOrInterfaceType>();

			for (ClassOrInterfaceType id : implementList) {
				classInfo.implementedInterfaces.add(getClassName(packageName, id.toString(), imports));
			}
			
			classInfo.isEnum = false;
		} else if (td instanceof EnumDeclaration) {
			classInfo.isEnum = true;
		}

		List<AnnotationExpr> annotationList = td.getAnnotations();
		
		if (annotationList == null)
			annotationList = new LinkedList<AnnotationExpr>();
		
		for (AnnotationExpr ae : annotationList) {
			AnnotationInfo annotation = getAnnotationInfo(packageName, imports, ae);
			classInfo.annotations.add(annotation);
		}

		List<BodyDeclaration> bodyDeclarationList = td.getMembers();
		if (bodyDeclarationList == null)
			bodyDeclarationList = new LinkedList<BodyDeclaration>();
		
		for (BodyDeclaration member : bodyDeclarationList) {
			if (member instanceof MethodDeclaration) {
				classInfo.methods.add(getMethodInfo(packageName, imports, (MethodDeclaration) member));
			}
		}

		return classInfo;
	}

	private static AnnotationInfo getAnnotationInfo(String packgeName, List<String> imports, AnnotationExpr ae) {
		AnnotationInfo annotation = new AnnotationInfo();
		annotation.className = getClassName(packgeName, ae.getName().toString(), imports);
		if (ae.getChildrenNodes().size() > 1) {
			for (int i = 1; i < ae.getChildrenNodes().size(); i++) {
				MemberValuePair pair = (MemberValuePair) ae.getChildrenNodes().get(i);
				String name = pair.getName();

				String value = getAnnotationParamValue(packgeName, pair.getValue(), imports);

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

	private static final String getAnnotationParamValue(String packageName, Object expr, List<String> imports) {
		String str = expr.toString();

		if (expr instanceof FieldAccessExpr) {
			FieldAccessExpr fae = (FieldAccessExpr) expr;
			String className = getClassName(packageName, fae.getScope().toString(), imports);
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

	private void visit(TypeDeclaration td) {
		ClassInfo classInfo = getClassInfo(sourceFileInfo, currentPackage, imports, td);
		sourceFileInfo.classes.add(classInfo);
	}
	
	@Override
	public void visit(EnumDeclaration n, Object arg) {
		visit(n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		visit(n);
		super.visit(n, arg);
	}
	// =========================================================================


}
