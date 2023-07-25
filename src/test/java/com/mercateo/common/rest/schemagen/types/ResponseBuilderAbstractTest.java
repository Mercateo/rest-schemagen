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
package com.mercateo.common.rest.schemagen.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

import jakarta.ws.rs.core.Link;

@ExtendWith(MockitoExtension.class)
public class ResponseBuilderAbstractTest {

    private final int value = 1234;
    private ResponseBuilder responseBuilder;
    @Mock
    private Link link1, link2, link3;

    @BeforeEach
    public void setUp() {
        responseBuilder = new ResponseBuilder();
        responseBuilder.withValue(value);

        responseBuilder.withElementMapper(integer ->
                ObjectWithSchema.create(integer.longValue(), JsonHyperSchema.from(link3)));
    }

    @Test
    public void elementMapperShouldMapValueAndAddLink() {
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.getObject().getObject()).isEqualTo(value);
        assertThat(result.getObject().getSchema().getLinks()).containsExactly(link3);
    }

    @Test
    public void shouldCollectListOfOptionalContainerLinks() {
        responseBuilder.withContainerLinks(
                Arrays.asList(Optional.of(link1), Optional.of(link2), Optional.empty())
        );
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.getSchema().getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldCollectOptionalContainerLinks() {
        responseBuilder.withContainerLinks(
                Optional.of(link1), Optional.of(link2), Optional.empty()
        );
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.getSchema().getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldCollectConcreteContainerLinks() {
        responseBuilder.withContainerLinks(link1, link2);
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.getSchema().getLinks()).containsExactly(link1, link2);
    }

    static class ResponseBuilder extends ResponseBuilderAbstract<ResponseBuilder, Integer, Long, ObjectWithSchema<ObjectWithSchema<Long>>> {

        private Integer value;

        public ResponseBuilder withValue(Integer value) {
            this.value = value;
            return this;
        }

        @Override
        public ObjectWithSchema<ObjectWithSchema<Long>> build() {
            ObjectWithSchema<Long> intermediateResult = elementMapper.apply(this.value);
            return ObjectWithSchema.create(intermediateResult, JsonHyperSchema.from(containerLinks));
        }
    }


}
