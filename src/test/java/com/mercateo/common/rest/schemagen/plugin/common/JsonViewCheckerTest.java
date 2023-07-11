package com.mercateo.common.rest.schemagen.plugin.common;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Sets;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
