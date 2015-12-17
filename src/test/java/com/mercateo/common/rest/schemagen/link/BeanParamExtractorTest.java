/*
 * Created on 01.06.2015
 *
 * author joerg_adler
 */
package com.mercateo.common.rest.schemagen.link;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BeanParamExtractorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private BeanParamExtractor beanParamExtractor;

    @Before
    public void setUp() {
        beanParamExtractor = new BeanParamExtractor();
    }

    @Test
    public void shouldNotFailBecauseOfDoubleQueryParam() {
        IllegalChildBeanParam2 bean = new IllegalChildBeanParam2();
        bean.path2 = "1";
        bean.setQuery1("1");

        final Map<String, Object[]> queryParameters = beanParamExtractor.getQueryParameters(bean);

        assertThat(queryParameters).hasSize(1).containsEntry("q1", new String[]{"1", "1"});
    }

    @Test
    public void should_not_fail_because_of_double_path_param() {
        final IllegalChildBeanParam1 bean = new IllegalChildBeanParam1();
        bean.setPath1("PA1");
        bean.setPath2("PA2");

        final Map<String, Object> pathParameterValues = beanParamExtractor.getPathParameters(bean);
        assertThat(pathParameterValues).hasSize(2);
    }

    @Test
    public void testGetQueryParam() {
        ChildBeanParam bean = new ChildBeanParam();
        bean.setPath1("PA1");
        bean.setQuery1("Q1");
        bean.setQuery2("Q2");

        Map<String, Object[]> pars = beanParamExtractor.getQueryParameters(bean);
        assertThat(pars).hasSize(2).containsEntry("q1", new String[]{"Q1"}).containsEntry("q2", new String[]{"Q2"});
    }

    @Test
    public void testGetPathParam() {
        ChildBeanParam bean = new ChildBeanParam();
        bean.setPath1("PA1");
        bean.setQuery1("Q1");
        bean.setQuery2("Q2");

        assertThat(beanParamExtractor.getPathParameters(bean)).containsEntry("p1", "PA1");
    }

    @Test
    public void testNullValuesNotInMap() {
        ChildBeanParam bean = new ChildBeanParam();
        assertThat(beanParamExtractor.getQueryParameters(bean)).doesNotContainKey("q1");
    }

    @Test
    public void testDoubleDefinitionOfSamePathParam() {
        final InvalidPathBeanParam beanParam = new InvalidPathBeanParam();

        assertThatThrownBy(() -> beanParamExtractor.getPathParameters(beanParam))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No single occurence of a PathParam annotation for name path");
    }

    @Test
    public void testUseOfTemplates() {
        final PathBeanParam beanParam = new PathBeanParam();

        final Map<String, Object> pathParameters = beanParamExtractor.getPathParameters(beanParam);

        assertThat(pathParameters).hasSize(1).containsEntry("pathParam", "{pathParam}");
    }

    @Test
    public void testCollectionQueryParams() {
        final CollectionQueryBeanParam queryParam = new CollectionQueryBeanParam();
        queryParam.setValues(Arrays.asList("foo", "bar"));

        final Map<String, Object[]> queryParameters = beanParamExtractor.getQueryParameters(queryParam);

        assertThat(queryParameters).hasSize(1).containsEntry("names", new Object[]{"foo", "bar"});
    }

    public static class CollectionQueryBeanParam {
        @QueryParam("names")
        private List<String> values;

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    public static class PathBeanParam {
        @PathParam("pathParam")
        private String path;

        @QueryParam("queryParam")
        private String query;
    }

    public static class ParentBeanParam<T> {
        @QueryParam("q1")
        private T query1;

        public T getQuery1() {
            return query1;
        }

        public void setQuery1(T query1) {
            this.query1 = query1;
        }
    }

    public static class ChildBeanParam extends ParentBeanParam<String> {
        @QueryParam("q2")
        private String query2;

        @PathParam("p1")
        private String path1;

        public String getQuery2() {
            return query2;
        }

        public void setQuery2(String query2) {
            this.query2 = query2;
        }

        public String getPath1() {
            return path1;
        }

        public void setPath1(String path1) {
            this.path1 = path1;
        }
    }

    public static class IllegalChildBeanParam1 extends ChildBeanParam {
        @PathParam("p2")
        private String path2;

        public String getPath2() {
            return path2;
        }

        public void setPath2(String path2) {
            this.path2 = path2;
        }

    }

    public static class IllegalChildBeanParam2 extends ChildBeanParam {
        @QueryParam("q1")
        private String path2;

        public String getPath2() {
            return path2;
        }

        public void setDoubleQ1(String path2) {
            this.path2 = path2;
        }
    }

    public static class InvalidPathBeanParam {
        @PathParam("path")
        private String path1;

        @PathParam("path")
        private String path2;
    }

}
