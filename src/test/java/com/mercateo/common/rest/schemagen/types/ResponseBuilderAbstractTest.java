package com.mercateo.common.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Link;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResponseBuilderAbstractTest {

    private final int value = 1234;
    private ResponseBuilder responseBuilder;
    @Mock
    private Link link1, link2, link3;

    @Before
    public void setUp() {
        responseBuilder = new ResponseBuilder();
        responseBuilder.withValue(value);

        responseBuilder.withElementMapper(integer ->
                ObjectWithSchema.create(integer.longValue(), JsonHyperSchema.from(link3)));
    }

    @Test
    public void elementMapperShouldMapValueAndAddLink() {
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.object.object).isEqualTo(value);
        assertThat(result.object.schema.getLinks()).containsExactly(link3);
    }

    @Test
    public void shouldCollectListOfOptionalContainerLinks() {
        responseBuilder.withContainerLinks(
                Arrays.asList(Optional.of(link1), Optional.of(link2), Optional.empty())
        );
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.schema.getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldCollectOptionalContainerLinks() {
        responseBuilder.withContainerLinks(
                Optional.of(link1), Optional.of(link2), Optional.empty()
        );
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.schema.getLinks()).containsExactly(link1, link2);
    }

    @Test
    public void shouldCollectConcreteContainerLinks() {
        responseBuilder.withContainerLinks(link1, link2);
        ObjectWithSchema<ObjectWithSchema<Long>> result = responseBuilder.build();

        assertThat(result.schema.getLinks()).containsExactly(link1, link2);
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