/*
 * Copyright © 2015 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.common.rest.schemagen.plugin.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Sets;
import com.mercateo.common.rest.schemagen.parameter.CallContext;

public class JsonViewCheckerTest {

	public static class TestBean{
		@JsonView({Class.class, TestBean.class})
		private String viewField;

		public String getViewField() {
			return viewField;
		}

		public void setViewField(String viewField) {
			this.viewField = viewField;
		}
	}

	
	@Test
	public void test_true_without_views_in_context() throws NoSuchFieldException, SecurityException{
		CallContext callContext=Mockito.mock(CallContext.class);
		Mockito.when(callContext.getAdditionalObjectsFor(Class.class)).thenReturn(Optional.empty());
		JsonViewChecker uut=new JsonViewChecker();
		assertTrue(uut.test(TestBean.class.getDeclaredField("viewField"), callContext));
	}
	
	
	@Test
	public void test_true_with_views_in_context() throws NoSuchFieldException, SecurityException{
		CallContext callContext=Mockito.mock(CallContext.class);
		Mockito.when(callContext.getAdditionalObjectsFor(Class.class)).thenReturn(Optional.of(Sets.newHashSet(Class.class, this.getClass())));
		JsonViewChecker uut=new JsonViewChecker();
		assertTrue(uut.test(TestBean.class.getDeclaredField("viewField"), callContext));
	}
	
	@Test
	public void test_false_with_views_in_context() throws NoSuchFieldException, SecurityException{
		CallContext callContext=Mockito.mock(CallContext.class);
		Mockito.when(callContext.getAdditionalObjectsFor(Class.class)).thenReturn(Optional.of(Sets.newHashSet(this.getClass())));
		JsonViewChecker uut=new JsonViewChecker();
		assertFalse(uut.test(TestBean.class.getDeclaredField("viewField"), callContext));
	}
}
