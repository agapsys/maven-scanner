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

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a class information
 * @author Leandro Oliveira (leandro@agapsys.com)
 */
public class ClassInfo {
	public Visibility visibility;
	public boolean    isAbstract;
	public boolean    isInterface;
	public boolean    isEnum;
	public String     className;
	public String     reflectionClassName;
	public String     superClassName;
	public ClassInfo  containerClass;
	public boolean    isStaticNested;
	public final List<AnnotationInfo> annotations = new LinkedList<AnnotationInfo>();
	public final List<String>         implementedInterfaces = new LinkedList<String>();
	public final List<MethodInfo>     methods = new LinkedList<MethodInfo>();

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 19 * hash + (this.className != null ? this.className.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ClassInfo other = (ClassInfo) obj;
		if ((this.className == null) ? (other.className != null) : !this.className.equals(other.className)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return className;
	}
}
