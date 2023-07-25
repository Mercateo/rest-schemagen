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
package com.mercateo.common.rest.schemagen.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObjectContextTest {

    private ObjectContext<TestClass> uut;

    @BeforeEach
    public void setuUp() {
        uut = ObjectContext.buildFor(TestClass.class).build();
    }

    @Test
    public void testSizeConstraint() throws NoSuchFieldException, SecurityException {
        Field f1 = TestClass.class.getDeclaredField("sizeString");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertThat(s1.getSizeConstraints().getMin().get()).isEqualTo(1);
        assertThat(s1.getSizeConstraints().getMax().get()).isEqualTo(10);
    }

    @Test
    public void testCustomSizeConstraints() throws NoSuchFieldException, SecurityException {
        Field f1 = TestClass.class.getDeclaredField("notEmptyString");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertThat(s1.getSizeConstraints().getMin().get()).isEqualTo(1);

        Field f2 = TestClass.class.getDeclaredField("notEmptyStringWithSize");
        ObjectContext<Object> s2 = uut.forField(f2);
        assertThat(s2.getSizeConstraints().getMin().get()).isEqualTo(10);
    }

    @Test
    public void testCustomValueConstraints() throws NoSuchFieldException {
        Field f1 = TestClass.class.getDeclaredField("intWithValueConstraints");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertThat(s1.getValueConstraints().getMin().get()).isEqualTo(-4);
        assertThat(s1.getValueConstraints().getMax().get()).isEqualTo(2704);
    }

    @Test
    public void testNotNullConstraint() throws NoSuchFieldException, SecurityException {
        Field f1 = TestClass.class.getDeclaredField("notNullString");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertTrue(s1.isRequired());
    }

    @Test
    public void testCustomNotNullConstraint() throws NoSuchFieldException, SecurityException {
        Field f1 = TestClass.class.getDeclaredField("notEmptyString");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertTrue(s1.isRequired());
    }

    @Test
    public void testNotNullConstraint_not_there() throws NoSuchFieldException, SecurityException {
        Field f1 = TestClass.class.getDeclaredField("sizeString");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertFalse(s1.isRequired());
    }

    @Test
    public void testSizeConstraint_not_there() throws NoSuchFieldException, SecurityException {
        Field f1 = TestClass.class.getDeclaredField("notNullString");
        ObjectContext<Object> s1 = uut.forField(f1);
        assertFalse(s1.getSizeConstraints().getMin().isPresent());
        assertFalse(s1.getSizeConstraints().getMax().isPresent());
    }

    @Test
    public void testAllowedValues() throws NoSuchFieldException {
        final TestClass testClass = new TestClass();
        testClass.setNotEmptyString("allowed");
        uut = ObjectContext.buildFor(TestClass.class).addAllowedValues(testClass).build();
        Field f1 = TestClass.class.getDeclaredField("notEmptyString");

        final ObjectContext<Object> objectObjectContext = uut.forField(f1);
        assertThat(objectObjectContext.getAllowedValues()).containsOnly("allowed");

    }
}
