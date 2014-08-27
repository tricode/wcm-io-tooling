/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.config.spi;

import io.wcm.config.api.Parameter;
import io.wcm.config.api.Visibility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

/**
 * Fluent API for building configuration parameter definitions.
 * @param <T> Parameter value type
 */
public final class ParameterBuilder<T> {

  private final String name;
  private final Class<T> type;
  private final Map<String, Object> properties = new HashMap<>();
  private String applicationId;
  private Visibility visibility = Visibility.NONE;
  private String defaultOsgiConfigProperty;
  private T defaultValue;

  private ParameterBuilder(String name, Class<T> type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Create a new parameter builder.
   * @param name Parameter name
   * @param type Parameter value type
   * @return Parameter builder
   */
  public static <T> ParameterBuilder<T> create(String name, Class<T> type) {
    return new ParameterBuilder<T>(name, type);
  }

  /**
   * @param value Application Id
   * @return this
   */
  public ParameterBuilder<T> applicationId(String value) {
    this.applicationId = value;
    return this;
  }

  /**
   * @param value Parameter visibility
   * @return this
   */
  public ParameterBuilder<T> visibility(Visibility value) {
    if (value == null) {
      throw new IllegalArgumentException("Visibility argument must not be null.");
    }
    this.visibility = value;
    return this;
  }

  /**
   * References OSGi configuration property which is checked for default value if this parameter is not set
   * in any configuration.
   * @param value OSGi configuration parameter name with syntax {persitentIdentity}:{propertyName}
   * @return this
   */
  public ParameterBuilder<T> defaultOsgiConfigProperty(String value) {
    this.defaultOsgiConfigProperty = value;
    return this;
  }

  /**
   * @param value Default value if parameter is not set for configuration
   *          and no default value is defined in OSGi configuration
   * @return this
   */
  public ParameterBuilder<T> defaultValue(T value) {
    this.defaultValue = value;
    return this;
  }

  /**
   * Further properties for documentation and configuration of behavior in configuration editor.
   * @param map Property map. Is merged with properties already set in builder.
   * @return this
   */
  public ParameterBuilder<T> properties(Map<String, Object> map) {
    if (map == null) {
      throw new IllegalArgumentException("Map argument must not be null.");
    }
    this.properties.putAll(map);
    return this;
  }

  /**
   * Further property for documentation and configuration of behavior in configuration editor.
   * @param key Property key
   * @param value Property value
   * @return this
   */
  public ParameterBuilder<T> property(String key, Object value) {
    if (key == null) {
      throw new IllegalArgumentException("Key argument must not be null.");
    }
    this.properties.put(key, value);
    return this;
  }

  /**
   * Builds the parameter definition.
   * @return Parameter definition
   */
  public Parameter<T> build() {
    return new ParameterImpl<T>(
        this.name,
        this.type,
        new ValueMapDecorator(Collections.unmodifiableMap(this.properties)),
        this.applicationId,
        this.visibility,
        this.defaultOsgiConfigProperty,
        this.defaultValue);
  }

  /**
   * Immutable {@link Parameter} implementation.
   * @param <T> Parameter value type
   */
  private static final class ParameterImpl<T> implements Parameter<T> {

    private final String name;
    private final Class<T> type;
    private final ValueMap properties;
    private final String applicationId;
    private final Visibility visibility;
    private final String defaultOsgiConfigProperty;
    private final T defaultValue;

    private ParameterImpl(String name, Class<T> type, ValueMap properties, String applicationId,
        Visibility visibility, String defaultOsgiConfigProperty, T defaultValue) {
      this.name = name;
      this.type = type;
      this.properties = properties;
      this.applicationId = applicationId;
      this.visibility = visibility;
      this.defaultOsgiConfigProperty = defaultOsgiConfigProperty;
      this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
      return this.name;
    }

    @Override
    public Class<T> getType() {
      return this.type;
    }

    @Override
    public ValueMap getProperties() {
      return this.properties;
    }

    @Override
    public String getApplicationId() {
      return this.applicationId;
    }

    @Override
    public Visibility getVisibility() {
      return this.visibility;
    }

    @Override
    public String getDefaultOsgiConfigProperty() {
      return this.defaultOsgiConfigProperty;
    }

    @Override
    public T getDefaultValue() {
      return this.defaultValue;
    }

  }

}
