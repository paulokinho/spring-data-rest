/*
 * Copyright 2013-2017 the original author or authors.
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
package org.springframework.data.rest.core.mapping;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.annotation.Reference;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import org.springframework.data.keyvalue.core.mapping.context.KeyValueMappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Unit tests for {@link PersistentPropertyResourceMapping}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class PersistentPropertyResourceMappingUnitTests {

	KeyValueMappingContext mappingContext = new KeyValueMappingContext();

	@Test // DATAREST-175
	public void usesPropertyNameAsDefaultResourceMappingRelAndPath() {

		ResourceMapping mapping = getPropertyMappingFor(Entity.class, "first");

		assertThat(mapping, is(notNullValue()));
		assertThat(mapping.getPath(), is(new Path("first")));
		assertThat(mapping.getRel(), is("first"));
		assertThat(mapping.isExported(), is(false));
	}

	@Test // DATAREST-175
	public void considersMappingAnnotationOnDomainClassProperty() {

		ResourceMapping mapping = getPropertyMappingFor(Entity.class, "second");

		assertThat(mapping, is(notNullValue()));
		assertThat(mapping.getPath(), is(new Path("secPath")));
		assertThat(mapping.getRel(), is("secRel"));
		assertThat(mapping.isExported(), is(false));
	}

	@Test // DATAREST-175
	public void considersMappingAnnotationOnDomainClassPropertyMethod() {

		ResourceMapping mapping = getPropertyMappingFor(Entity.class, "third");

		assertThat(mapping, is(notNullValue()));
		assertThat(mapping.getPath(), is(new Path("thirdPath")));
		assertThat(mapping.getRel(), is("thirdRel"));
		assertThat(mapping.isExported(), is(false));
	}

	@Test // DATAREST-233
	public void returnsDefaultDescriptionKey() {

		ResourceMapping mapping = getPropertyMappingFor(Entity.class, "second");

		ResourceDescription description = mapping.getDescription();

		assertThat(description.isDefault(), is(true));
		assertThat(description.getMessage(), is("rest.description.entity.second"));
	}

	@Test // DATAREST-233
	public void considersAtDescription() {

		ResourceMapping mapping = getPropertyMappingFor(Entity.class, "fourth");

		ResourceDescription description = mapping.getDescription();
		assertThat(description.isDefault(), is(false));
		assertThat(description.getMessage(), is("Some description"));
	}

	private ResourceMapping getPropertyMappingFor(Class<?> entity, String propertyName) {

		KeyValuePersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entity);
		KeyValuePersistentProperty property = persistentEntity.getPersistentProperty(propertyName);

		ResourceMappings resourceMappings = new PersistentEntitiesResourceMappings(
				new PersistentEntities(Arrays.asList(mappingContext)));

		return new PersistentPropertyResourceMapping(property, resourceMappings);
	}

	public static class Entity {

		Related first;
		@Reference Related third;

		@Reference //
		@RestResource(path = "secPath", rel = "secRel", exported = false) //
		List<Related> second;

		@Description("Some description") String fourth;

		@RestResource(path = "thirdPath", rel = "thirdRel", exported = false)
		public Related getThird() {
			return third;
		}
	}

	public static class Related {

	}
}
