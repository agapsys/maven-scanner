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

import a.b.c.SuperClass1;
import com.example.Type;
import f.g.h.Annotation1;
import f.g.h.Annotation2;

@Annotation1(a=1)
@Annotation4
public class TestClass extends SuperClass1 implements SuperInterface1, aa.bb.cc.dd.SuperInterface1 {
	@Annotation2
	protected static class InnerClass extends aa.bb.cc.dd.SuperInterface3 implements aa.bb.cc.dd.SuperInterface2 {
		
		private static class DeepClass {}
		
		public InnerClass() {}
		
		public int inner_publicMethod() {}
		
		public double inner_publicMethod1() {}
		
		public static String inner_publicMethod1(int arg) {}
		
		@Annotation1
		private Type inner_privateMethod() {}
		
		@aa.bb.cc.dd.Annotation1
		protected void inner_protectedMethod() {}
		
		void inner_defaultMethod() {}
		
		a.c.b method2() {}
	}
	
	enum InnerEnum {
		A, B, C;
	}
	
	public TestClass() {}
		
	public int publicMethod() {}
	
	public double publicMethod1() {}
	
	public static String publicMethod1(int arg) {}
	
	private Type privateMethod() {}
	
	@aa.bb.cc.dd.Annotation1
	protected void protectedMethod() {}
	
	void defaultMethod() {}
	
	a.b.c method3() {}
}
