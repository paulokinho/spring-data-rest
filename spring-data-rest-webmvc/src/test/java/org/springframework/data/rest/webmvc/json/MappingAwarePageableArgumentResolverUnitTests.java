/*
 * Copyright 2016-2017 original author or authors.
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
package org.springframework.data.rest.webmvc.json;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Unit tests for {@link MappingAwarePageableArgumentResolver}.
 * 
 * @author Mark Paluch
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingAwarePageableArgumentResolverUnitTests {

	@Mock JacksonMappingAwareSortTranslator translator;
	@Mock PageableHandlerMethodArgumentResolver delegate;
	@Mock MethodParameter parameter;
	@Mock NativeWebRequest webRequest;
	@Mock ModelAndViewContainer modelAndViewContainer;
	@Mock WebDataBinderFactory binderFactory;

	MappingAwarePageableArgumentResolver resolver;

	@Before
	public void setUp() {
		resolver = new MappingAwarePageableArgumentResolver(translator, delegate);
	}

	@Test // DATAREST-906
	public void resolveArgumentShouldReturnTranslatedPageable() throws Exception {

		Sort translated = new Sort("world");
		Pageable pageable = new PageRequest(0, 1, Direction.ASC, "hello");

		when(delegate.resolveArgument(parameter, modelAndViewContainer, webRequest, binderFactory)).thenReturn(pageable);
		when(translator.translateSort(pageable.getSort(), parameter, webRequest)).thenReturn(translated);

		Pageable result = resolver.resolveArgument(parameter, modelAndViewContainer, webRequest, binderFactory);

		assertThat(result.getPageSize(), is(1));
		assertThat(result.getPageNumber(), is(0));
		assertThat(result.getSort(), is(equalTo(translated)));
	}

	@Test // DATAREST-906
	public void resolveArgumentShouldReturnPageableWithoutSort() throws Exception {

		Pageable pageable = new PageRequest(0, 1);

		when(delegate.resolveArgument(parameter, modelAndViewContainer, webRequest, binderFactory)).thenReturn(pageable);

		Pageable result = resolver.resolveArgument(parameter, modelAndViewContainer, webRequest, binderFactory);

		assertThat(result.getPageSize(), is(1));
		assertThat(result.getPageNumber(), is(0));
		assertThat(result.getSort(), is(nullValue()));
	}

	@Test // DATAREST-906
	public void resolveArgumentShouldReturnNoPageable() throws Exception {

		Pageable result = resolver.resolveArgument(parameter, modelAndViewContainer, webRequest, binderFactory);

		assertThat(result, is(nullValue()));
	}
}
