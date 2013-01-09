package org.pentaho.reporting.engine.classic.core.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DataCacheKey
{
  public static final String QUERY_CACHE = "QueryCache";

  private Map<String, Object> parameter;
  private Map<String, Object> attributes;

  public DataCacheKey()
  {
    parameter = new HashMap<String, Object>();
    attributes = new HashMap<String, Object>();
  }

  public void addParameter(final String key, final Object value)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }
    parameter.put(key, value);
  }

  public void addAttribute(final String key, final Object value)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }
    parameter.put(key, value);
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final DataCacheKey that = (DataCacheKey) o;

    if (!equalsMap(attributes, that.attributes))
    {
      return false;
    }
    if (!equalsMap(parameter, that.parameter))
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result = hashCodeMap(parameter);
    result = 31 * result + hashCodeMap(attributes);
    return result;
  }

  public void makeReadOnly()
  {
    // Make the following maps immutable.  This method should be called after
    // the parameters & attributes have been seeded.
    parameter = Collections.unmodifiableMap(parameter);
    attributes = Collections.unmodifiableMap(attributes);
  }

  private boolean equalsMap(final Map<String,Object> values, final Map<String,Object> otherValues)
  {
    if (otherValues.size() != values.size())
    {
      return false;
    }

    for (final String key: values.keySet())
    {
      final Object value = values.get(key);
      final Object otherValue = otherValues.get(key);

      if (value == null && otherValue == null)
      {
        continue;
      }

      if (value instanceof Object[] && otherValue instanceof Object[])
      {
        if (ObjectUtilities.equalArray((Object[]) value, (Object[]) otherValue) == false)
        {
          return false;
        }
      }
      else if (ObjectUtilities.equal(value, otherValue) == false)
      {
        return false;
      }
    }
    return true;
  }

  private int hashCodeMap(final Map<String,Object> values)
  {
    int hashCode = values.size();

    for (final String key: values.keySet())
    {
      final Object value = values.get(key);

      hashCode = 31 * hashCode + (key != null ? key.hashCode() : 0);
      if (value == null)
      {
        hashCode = 31 * hashCode;
      }
      else if (value instanceof Object[])
      {
        hashCode = 31 * hashCode + ObjectUtilities.hashCode((Object[]) value);
      }
      else
      {
        hashCode = 31 * hashCode + value.hashCode();
      }
    }
    return hashCode;
  }
}
