package org.pentaho.reporting.engine.classic.core;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class ReportEnvironmentDataRow implements DataRow
{
  private static final String ENV_MAPPING_KEY_PREFIX = "org.pentaho.reporting.engine.classic.core.env-mapping.";
  private ReportEnvironment environment;
  private LinkedHashMap<String,String> columnMap;
  private String[] columnNames;

  public ReportEnvironmentDataRow(final ReportEnvironment environment)
  {
    final LinkedMap envMapping = createEnvironmentMapping();
    this.columnMap = new LinkedHashMap<String,String>();
    final Object[] keys = envMapping.keys();
    for (int i = 0; i < keys.length; i++)
    {
      final String key = (String) keys[i];
      final String value = (String) envMapping.get(key);
      if (StringUtils.isEmpty(key) == false && StringUtils.isEmpty(value) == false)
      {
        this.columnMap.put(value, key);
      }
    }
    this.columnNames = columnMap.keySet().toArray(new String[columnMap.size()]);
    this.environment = environment;
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col the item index.
   * @return the value.
   */
  public Object get(final String col)
  {
    final String envName = columnMap.get(col);
    if (envName == null)
    {
      return null;
    }
    if (envName.endsWith("-array")) // NON-NLS
    {
      final String name = envName.substring(0, envName.length() - 6);
      final Object s = environment.getEnvironmentProperty(name);
      if (s == null)
      {
        return new String[0];
      }

      final CSVTokenizer csvTokenizer = new CSVTokenizer(String.valueOf(s), ",", "\"", false);
      final int length = csvTokenizer.countTokens();
      final String[] rolesArray = new String[length];
      for (int i = 0; i < length; i += 1)
      {
        rolesArray[i] = csvTokenizer.nextToken();
      }
      return rolesArray;
    }
    return environment.getEnvironmentProperty(envName);
  }

  /**
   * Returns the known column names, this data-row understands. The column names may change over time but do not
   * change while a event is processed by a function. The array returned is a copy of the internal data-storage
   * and can be safely modified.
   *
   * @return the column names as array.
   */
  public String[] getColumnNames()
  {
    return columnNames.clone();
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged(final String name)
  {
    return false;
  }

  public boolean isArray(final String columnName)
  {
    final String envName = columnMap.get(columnName);
    if (envName == null)
    {
      return false;
    }
    return (envName.endsWith("-array")); // NON-NLS
  }

  /**
   * Creates a ordered map that contains the environment names as keys and the data-row column names as values.
   *
   * @return the mapping from environment names to data-row column names.
   */
  public static LinkedMap createEnvironmentMapping()
  {
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator propertyKeys = configuration.findPropertyKeys(ENV_MAPPING_KEY_PREFIX);
    final LinkedMap names = new LinkedMap();
    while (propertyKeys.hasNext())
    {
      final String key = (String) propertyKeys.next();
      final String value = configuration.getConfigProperty(key);
      final String shortKey = key.substring(ENV_MAPPING_KEY_PREFIX.length());
      names.put(shortKey, value);
    }
    return names;
  }

  public ReportEnvironment getEnvironment()
  {
    return environment;
  }


  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("ReportEnvironmentDataRow");
    sb.append("{columnMap=").append(columnMap);
    sb.append('}');
    return sb.toString();
  }
}
