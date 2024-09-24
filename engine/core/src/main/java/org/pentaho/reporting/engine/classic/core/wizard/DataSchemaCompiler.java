/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.wizard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironmentMapping;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.core.states.datarow.ProcessingDataSchemaCompiler;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Map;

public class DataSchemaCompiler {
  private static final Log logger = LogFactory.getLog( ProcessingDataSchemaCompiler.class );

  protected static class GenericDataAttributes implements DataAttributes {
    private static final String[] CORE_NAMES = new String[] { MetaAttributeNames.Core.NAME,
      MetaAttributeNames.Core.SOURCE, MetaAttributeNames.Core.TYPE };
    private static final String[] FORMATTING_NAMES = new String[] { MetaAttributeNames.Formatting.LABEL };
    private static final String[] NAMESPACES = new String[] { MetaAttributeNames.Core.NAMESPACE,
      MetaAttributeNames.Formatting.NAMESPACE };

    private String parameterName;
    private Class parameterType;
    private String source;
    private String label;
    private DataAttributes globalAttributes;

    protected GenericDataAttributes() {
      this.globalAttributes = new DefaultDataAttributes();
    }

    public void setup( final String parameterName, final Class parameterType, final String source, final String label,
        final DataAttributes globalAttributes ) {
      if ( globalAttributes == null ) {
        throw new NullPointerException();
      }
      if ( parameterName == null ) {
        throw new NullPointerException();
      }
      if ( parameterType == null ) {
        throw new NullPointerException();
      }
      if ( source == null ) {
        throw new NullPointerException();
      }
      this.parameterName = parameterName;
      this.parameterType = parameterType;
      this.source = source;
      this.label = label;
      this.globalAttributes = globalAttributes;
    }

    public String[] getMetaAttributeDomains() {
      return StringUtils.merge( globalAttributes.getMetaAttributeDomains(), GenericDataAttributes.NAMESPACES );
    }

    public String[] getMetaAttributeNames( final String domainName ) {
      final String[] metaNamess = globalAttributes.getMetaAttributeNames( domainName );
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domainName ) ) {
        return StringUtils.merge( GenericDataAttributes.CORE_NAMES, metaNamess );
      }
      if ( MetaAttributeNames.Formatting.NAMESPACE.equals( domainName ) ) {
        return StringUtils.merge( GenericDataAttributes.FORMATTING_NAMES, metaNamess );
      }
      return metaNamess;
    }

    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context ) {
      return getMetaAttribute( domain, name, type, context, null );
    }

    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context, final Object defaultValue ) {
      if ( domain == null ) {
        throw new NullPointerException();
      }
      if ( name == null ) {
        throw new NullPointerException();
      }
      if ( context == null ) {
        throw new NullPointerException();
      }

      if ( MetaAttributeNames.Core.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Core.NAME.equals( name ) ) {
          return parameterName;
        }
        if ( MetaAttributeNames.Core.SOURCE.equals( name ) ) {
          return source;
        }
        if ( MetaAttributeNames.Core.TYPE.equals( name ) ) {
          return parameterType;
        }
      }

      final Object result = globalAttributes.getMetaAttribute( domain, name, type, context, defaultValue );
      if ( result != null ) {
        return result;
      }
      if ( MetaAttributeNames.Formatting.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Formatting.LABEL.equals( name ) ) {
          return label;
        }
      }
      return null;
    }

    public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException(
          "This is an internal class and should not have been leaked to the outside world" );
    }

    public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
      return DefaultConceptQueryMapper.INSTANCE;
    }
  }

  protected static class ParameterDataAttributes implements DataAttributes {
    private static final String[] NAMES = new String[] { MetaAttributeNames.Core.NAME, MetaAttributeNames.Core.SOURCE,
      MetaAttributeNames.Core.TYPE };
    private static final String[] NAMESPACES = new String[] { MetaAttributeNames.Core.NAMESPACE };

    private DataAttributes globalAttributes;
    private ParameterDefinitionEntry entry;
    private ParameterContext parameterContext;

    protected ParameterDataAttributes() {
      this.globalAttributes = new DefaultDataAttributes();
    }

    public void setup( final ParameterDefinitionEntry parameter, final DataAttributes globalAttributes,
        final ReportEnvironment reportEnvironment, final ResourceManager resourceManager )
      throws ReportDataFactoryException {
      if ( globalAttributes == null ) {
        throw new NullPointerException();
      }
      if ( parameter == null ) {
        throw new NullPointerException();
      }
      this.globalAttributes = globalAttributes;
      this.entry = parameter;

      this.parameterContext =
          new DefaultParameterContext( new CompoundDataFactory(), new StaticDataRow(), ClassicEngineBoot.getInstance()
              .getGlobalConfig(), new DefaultResourceBundleFactory(), resourceManager, null, reportEnvironment );
    }

    public String[] getMetaAttributeDomains() {
      return StringUtils.merge( globalAttributes.getMetaAttributeDomains(), StringUtils.merge(
          ParameterDataAttributes.NAMESPACES, entry.getParameterAttributeNamespaces() ) );
    }

    public String[] getMetaAttributeNames( final String domainName ) {
      final String[] metaNamess = globalAttributes.getMetaAttributeNames( domainName );
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domainName ) ) {
        return StringUtils.merge( ParameterDataAttributes.NAMES, metaNamess );
      }
      return StringUtils.merge( metaNamess, entry.getParameterAttributeNames( domainName ) );
    }

    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context ) {
      return getMetaAttribute( domain, name, type, context, null );
    }

    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context, final Object defaultValue ) {
      if ( domain == null ) {
        throw new NullPointerException();
      }
      if ( name == null ) {
        throw new NullPointerException();
      }
      if ( context == null ) {
        throw new NullPointerException();
      }

      if ( MetaAttributeNames.Core.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Core.NAME.equals( name ) ) {
          return entry.getName();
        }
        if ( MetaAttributeNames.Core.SOURCE.equals( name ) ) {
          return MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER;
        }
        if ( MetaAttributeNames.Core.TYPE.equals( name ) ) {
          return entry.getValueType();
        }
      }
      final Object override = entry.getParameterAttribute( domain, name, parameterContext );
      if ( override != null ) {
        return override;
      }
      return globalAttributes.getMetaAttribute( domain, name, type, context, defaultValue );
    }

    public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException(
          "This is an internal class and should not have been leaked to the outside world" );
    }

    public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
      return DefaultConceptQueryMapper.INSTANCE;
    }
  }

  protected static class ExpressionsDataAttributes implements DataAttributes {
    private static final String[] CORENAMES = new String[] { MetaAttributeNames.Core.NAME,
      MetaAttributeNames.Core.SOURCE, MetaAttributeNames.Core.TYPE };
    private static final String[] NAMESPACES = new String[] { MetaAttributeNames.Core.NAMESPACE,
      MetaAttributeNames.Expressions.NAMESPACE };
    private static final String[] EXPRESSIONNAMES = new String[] { MetaAttributeNames.Expressions.CLASS };

    private ExpressionMetaData expressionMetaData;
    private Class expressionType;
    private Class resultType;
    private String expressionName;
    private BeanUtility beanUtility;
    private String[] expressionProperties;
    private static final String[] EMPTY_STRINGS = new String[0];

    public ExpressionsDataAttributes( final Expression expression ) {
      if ( expression == null ) {
        throw new NullPointerException();
      }
      if ( expression.getName() == null ) {
        throw new IllegalStateException();
      }

      this.expressionType = expression.getClass();
      if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionType.getName() ) ) {
        this.expressionMetaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionType.getName() );
        this.resultType = expressionMetaData.getResultType();
        this.expressionProperties =
            StringUtils.merge( expressionMetaData.getPropertyNames(), ExpressionsDataAttributes.EXPRESSIONNAMES );
      } else {
        this.expressionMetaData = null;
        this.resultType = Object.class;
        this.expressionProperties = ExpressionsDataAttributes.EXPRESSIONNAMES;
      }

      this.expressionName = expression.getName();
      try {
        this.beanUtility = new BeanUtility( expression );
      } catch ( IntrospectionException e ) {
        // should not happen, but if it does, we simply ignore the bean-metadata.
      }

    }

    public String[] getMetaAttributeDomains() {
      return ExpressionsDataAttributes.NAMESPACES.clone();
    }

    public String[] getMetaAttributeNames( final String domainName ) {
      if ( MetaAttributeNames.Core.NAMESPACE.equals( domainName ) ) {
        return ExpressionsDataAttributes.CORENAMES.clone();
      }
      if ( MetaAttributeNames.Expressions.NAMESPACE.equals( domainName ) ) {
        return expressionProperties.clone();
      }
      return ExpressionsDataAttributes.EMPTY_STRINGS;
    }

    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context ) {
      return getMetaAttribute( domain, name, type, context, null );
    }

    public Object getMetaAttribute( final String domain, final String name, final Class type,
        final DataAttributeContext context, final Object defaultValue ) {
      if ( domain == null ) {
        throw new NullPointerException();
      }
      if ( name == null ) {
        throw new NullPointerException();
      }
      if ( context == null ) {
        throw new NullPointerException();
      }

      if ( MetaAttributeNames.Core.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Core.NAME.equals( name ) ) {
          return expressionName;
        }
        if ( MetaAttributeNames.Core.SOURCE.equals( name ) ) {
          return MetaAttributeNames.Core.SOURCE_VALUE_EXPRESSION;
        }
        if ( MetaAttributeNames.Core.TYPE.equals( name ) ) {
          return resultType;
        }
      }
      if ( MetaAttributeNames.Expressions.NAMESPACE.equals( domain ) ) {
        if ( MetaAttributeNames.Expressions.CLASS.equals( name ) ) {
          return expressionType;
        }
        if ( beanUtility != null && expressionMetaData != null ) {
          final ExpressionPropertyMetaData propertyMetaData = expressionMetaData.getPropertyDescription( name );
          if ( propertyMetaData != null ) {
            try {
              return beanUtility.getProperty( name );
            } catch ( BeanException e ) {
              // ignore once more .. fall back to the default meta-data or return null..
            }
          }
        }
      }
      return defaultValue;
    }

    public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
      return DefaultConceptQueryMapper.INSTANCE;
    }

    public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException(
          "This is an internal class and should not have been leaked to the outside world" );
    }
  }

  private GenericDataAttributes environmentDataAttributes;
  private GenericDataAttributes tableDataAttributes;
  private ParameterDataAttributes parameterDataAttributes;
  private DefaultDataAttributes globalAttributes;
  private DefaultDataAttributeReferences globalReferences;

  private MetaSelectorRule[] indirectRules;
  private DirectFieldSelectorRule[] directRules;
  private DataAttributeContext context;
  private DataSchemaDefinition reportSchemaDefinition;
  private ResourceManager resourceManager;
  private boolean initialized;

  public DataSchemaCompiler( final DataSchemaDefinition reportSchemaDefinition, final DataAttributeContext context ) {
    this( reportSchemaDefinition, context, createDefaultResourceManager() );
  }

  private static ResourceManager createDefaultResourceManager() {
    return new ResourceManager();
  }

  public DataSchemaCompiler( final DataSchemaDefinition reportSchemaDefinition, final DataAttributeContext context,
      final ResourceManager resourceManager ) {
    if ( reportSchemaDefinition == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.context = context;
    this.reportSchemaDefinition = reportSchemaDefinition;
    this.resourceManager = resourceManager;
  }

  protected void init() {
    final DefaultDataSchemaDefinition schemaDefinition = new DefaultDataSchemaDefinition();
    schemaDefinition.merge( parseGlobalDefaults( resourceManager ) );
    schemaDefinition.merge( reportSchemaDefinition );

    this.tableDataAttributes = new GenericDataAttributes();
    this.environmentDataAttributes = new GenericDataAttributes();
    this.parameterDataAttributes = new ParameterDataAttributes();
    this.globalAttributes = new DefaultDataAttributes();
    this.globalReferences = new DefaultDataAttributeReferences();
    final DataSchemaRule[] globalRules = schemaDefinition.getGlobalRules();
    for ( int i = 0; i < globalRules.length; i++ ) {
      final DataSchemaRule rule = globalRules[i];
      final DataAttributes attributes = rule.getStaticAttributes();
      globalAttributes.merge( attributes, context );
      final DataAttributeReferences mappedAttributes = rule.getMappedAttributes();
      globalReferences.merge( mappedAttributes );
    }

    indirectRules = schemaDefinition.getIndirectRules();
    directRules = schemaDefinition.getDirectRules();
    initialized = true;
  }

  protected DataSchemaDefinition parseGlobalDefaults( final ResourceManager resourceManager ) {
    return DataSchemaUtility.parseDefaults( resourceManager );
  }

  public DataSchema compile( final TableModel data ) throws ReportDataFactoryException {
    return compile( data, null, null, null, null );
  }

  public boolean isInitialized() {
    return initialized;
  }

  public DataSchema compile( final TableModel data, final Expression[] expressions, final ParameterDataRow parameters,
      final ParameterDefinitionEntry[] parameterDefinitions, final ReportEnvironment reportEnvironment )
    throws ReportDataFactoryException {
    if ( initialized == false ) {
      init();
    }

    if ( data == null ) {
      throw new NullPointerException();
    }

    final DefaultDataSchema defaultDataSchema = new DefaultDataSchema();

    if ( reportEnvironment != null ) {
      processReportEnvironment( globalAttributes, indirectRules, directRules, defaultDataSchema );
    }

    if ( parameters != null ) {
      processParameters( parameters, parameterDefinitions, reportEnvironment, globalAttributes, indirectRules,
          directRules, defaultDataSchema );
    }

    // expressions
    if ( expressions != null ) {
      for ( int i = 0; i < expressions.length; i++ ) {
        final Expression expression = expressions[i];
        final String name = expression.getName();
        if ( name == null ) {
          continue;
        }
        final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
        computedParameterDataAttributes.merge( globalAttributes, context );
        computedParameterDataAttributes.merge( new ExpressionsDataAttributes( expression ), context );

        applyRules( indirectRules, directRules, computedParameterDataAttributes );
        defaultDataSchema.setAttributes( name, computedParameterDataAttributes );
      }
    }

    if ( data instanceof MetaTableModel == false ) {
      final int count = data.getColumnCount();
      for ( int i = 0; i < count; i++ ) {
        final String colName = data.getColumnName( i );
        if ( colName == null ) {
          continue;
        }

        tableDataAttributes.setup( colName, data.getColumnClass( i ), MetaAttributeNames.Core.SOURCE_VALUE_TABLE,
            colName, globalAttributes );

        final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
        computedParameterDataAttributes.merge( this.tableDataAttributes, context );
        applyRules( indirectRules, directRules, computedParameterDataAttributes );
        defaultDataSchema.setAttributes( colName, computedParameterDataAttributes );
      }
    } else {
      final MetaTableModel mt = (MetaTableModel) data;

      final DefaultDataAttributes tableGlobalAttributes = new DefaultDataAttributes();
      tableGlobalAttributes.merge( globalAttributes, context );
      tableGlobalAttributes.merge( mt.getTableAttributes(), context );

      try {
        defaultDataSchema.setTableAttributes( tableGlobalAttributes );
      } catch ( CloneNotSupportedException e ) {
        logger.warn( "Unable to copy global data-attributes", e );
      }

      final int count = data.getColumnCount();
      for ( int i = 0; i < count; i++ ) {
        final String colName = data.getColumnName( i );
        if ( colName == null ) {
          continue;
        }

        final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
        computedParameterDataAttributes.merge( tableGlobalAttributes, context );
        computedParameterDataAttributes.merge( mt.getColumnAttributes( i ), context );

        tableDataAttributes.setup( colName, data.getColumnClass( i ), MetaAttributeNames.Core.SOURCE_VALUE_TABLE, null,
            EmptyDataAttributes.INSTANCE );
        computedParameterDataAttributes.merge( tableDataAttributes, context );

        applyRules( indirectRules, directRules, computedParameterDataAttributes );
        defaultDataSchema.setAttributes( colName, computedParameterDataAttributes );
      }
    }

    return defaultDataSchema;
  }

  protected void applyRules( final DataSchemaRule[] indirectRules, final DataSchemaRule[] directRules,
      final DefaultDataAttributes computedParameterDataAttributes ) {
    for ( int j = 0; j < indirectRules.length; j++ ) {
      final DataSchemaRule rule = indirectRules[j];
      if ( rule.isMatch( computedParameterDataAttributes, context ) ) {
        computedParameterDataAttributes.merge( rule.getStaticAttributes(), context );
      }
    }

    for ( int j = 0; j < directRules.length; j++ ) {
      final DataSchemaRule rule = directRules[j];
      if ( rule.isMatch( computedParameterDataAttributes, context ) ) {
        computedParameterDataAttributes.merge( rule.getStaticAttributes(), context );
      }
    }

    computedParameterDataAttributes.mergeReferences( globalReferences, context );

    for ( int j = 0; j < indirectRules.length; j++ ) {
      final DataSchemaRule rule = indirectRules[j];
      if ( rule.isMatch( computedParameterDataAttributes, context ) ) {
        final DataAttributeReferences mappedAttributes = rule.getMappedAttributes();
        computedParameterDataAttributes.mergeReferences( mappedAttributes, context );
      }
    }

    for ( int j = 0; j < directRules.length; j++ ) {
      final DataSchemaRule rule = directRules[j];
      if ( rule.isMatch( computedParameterDataAttributes, context ) ) {
        final DataAttributeReferences mappedAttributes = rule.getMappedAttributes();
        computedParameterDataAttributes.mergeReferences( mappedAttributes, context );
      }
    }

  }

  protected void processReportEnvironment( final DefaultDataAttributes globalAttributes,
      final DataSchemaRule[] indirectRules, final DataSchemaRule[] directRules, final DefaultDataSchema schema ) {
    final Map<String, String> names = DefaultReportEnvironmentMapping.INSTANCE.createEnvironmentMapping();
    final String[] parameterNames = names.keySet().toArray( new String[names.size()] );
    for ( int i = 0; i < parameterNames.length; i++ ) {
      final String envName = parameterNames[i];
      final String name = names.get( envName );
      if ( envName.endsWith( "-array" ) ) {
        environmentDataAttributes.setup( name, String[].class, MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT, name,
            globalAttributes );
      } else {
        environmentDataAttributes.setup( name, String.class, MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT, name,
            globalAttributes );
      }

      final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
      computedParameterDataAttributes.merge( this.environmentDataAttributes, context );
      applyRules( indirectRules, directRules, computedParameterDataAttributes );

      schema.setAttributes( name, computedParameterDataAttributes );
    }
  }

  protected void processParameters( final ParameterDataRow parameters,
      final ParameterDefinitionEntry[] parameterDefinitionEntries, final ReportEnvironment reportEnvironment,
      final DefaultDataAttributes globalAttributes, final DataSchemaRule[] indirectRules,
      final DataSchemaRule[] directRules, final DefaultDataSchema schema ) throws ReportDataFactoryException {
    final Map<String, ParameterDefinitionEntry> map =
        normalizeParameterDefinitions( parameters, parameterDefinitionEntries );
    for ( final Map.Entry<String, ParameterDefinitionEntry> entry : map.entrySet() ) {
      final ParameterDefinitionEntry parameter = entry.getValue();
      parameterDataAttributes.setup( parameter, globalAttributes, reportEnvironment, resourceManager );

      final DefaultDataAttributes computedParameterDataAttributes = new DefaultDataAttributes();
      computedParameterDataAttributes.merge( this.parameterDataAttributes, context );
      applyRules( indirectRules, directRules, computedParameterDataAttributes );

      schema.setAttributes( parameter.getName(), computedParameterDataAttributes );
    }
  }

  private Map<String, ParameterDefinitionEntry> normalizeParameterDefinitions( final ParameterDataRow parameters,
      final ParameterDefinitionEntry[] parameterDefinitionEntries ) {
    final String[] parameterNames = parameters.getColumnNames();
    final HashMap<String, ParameterDefinitionEntry> map = new HashMap<String, ParameterDefinitionEntry>();
    if ( parameterDefinitionEntries != null ) {
      for ( int i = 0; i < parameterDefinitionEntries.length; i++ ) {
        final ParameterDefinitionEntry entry = parameterDefinitionEntries[i];
        map.put( entry.getName(), entry );
      }
    }
    for ( int i = 0; i < parameterNames.length; i++ ) {
      final String name = parameterNames[i];
      if ( map.containsKey( name ) ) {
        continue;
      }
      final Object value = parameters.get( name );
      if ( value != null ) {
        map.put( name, new PlainParameter( name, value.getClass() ) );
      } else {
        map.put( name, new PlainParameter( name, Object.class ) );
      }
    }
    return map;
  }

  protected GenericDataAttributes getTableDataAttributes() {
    return tableDataAttributes;
  }

  protected ParameterDataAttributes getParameterDataAttributes() {
    return parameterDataAttributes;
  }

  protected DefaultDataAttributes getGlobalAttributes() {
    return globalAttributes;
  }

  protected DefaultDataAttributeReferences getGlobalReferences() {
    return globalReferences;
  }

  protected MetaSelectorRule[] getIndirectRules() {
    return indirectRules;
  }

  protected DirectFieldSelectorRule[] getDirectRules() {
    return directRules;
  }

  protected DataAttributeContext getContext() {
    return context;
  }
}
