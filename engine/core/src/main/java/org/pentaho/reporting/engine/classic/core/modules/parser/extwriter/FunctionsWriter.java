/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FormulaFunction;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * An XML definition writer that outputs the functions.
 *
 * @author Thomas Morgner.
 */
public class FunctionsWriter extends AbstractXMLDefinitionWriter {
  private static final Log logger = LogFactory.getLog( FunctionsWriter.class );

  /**
   * The name of the function tag.
   */
  public static final String FUNCTION_TAG = "function";

  /**
   * The name of the expression tag.
   */
  public static final String EXPRESSION_TAG = "expression";
  public static final String STYLE_EXPRESSION_TAG = "style-expression";

  /**
   * The name of the 'property-ref' tag.
   */
  public static final String PROPERTY_REF_TAG = "property-ref";

  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the report writer.
   * @param indentLevel
   *          the current indention level.
   */
  public FunctionsWriter( final ReportWriterContext reportWriter, final XmlWriter indentLevel ) {
    super( reportWriter, indentLevel );
  }

  /**
   * Writes the functions to XML.
   *
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if the report function definition could not be written.
   */
  public void write() throws IOException, ReportWriterException {
    if ( shouldWriteFunctions() ) {
      final XmlWriter writer = getXmlWriter();
      writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.FUNCTIONS_TAG, XmlWriterSupport.OPEN );

      writeExpressions( getReport().getExpressions() );

      writer.writeCloseTag();
    }
  }

  /**
   * Tests, whether to start a functions section.
   *
   * @return true, if there are functions, marked properties or expressions defined, false otherwise.
   */
  private boolean shouldWriteFunctions() {
    final AbstractReportDefinition report = getReport();
    return report.getExpressions().size() != 0;
  }

  /**
   * Writes a collection of functions/expressions to XML.
   *
   * @param exp
   *          the collection.
   * @throws java.io.IOException
   *           if there is an I/O problem.
   */
  public void writeExpressions( final ExpressionCollection exp ) throws IOException {
    for ( int i = 0; i < exp.size(); i++ ) {
      final Expression expression = exp.getExpression( i );
      writeExpression( expression );
    }
  }

  private void writeExpression( final Expression expression ) throws IOException {
    final XmlWriter writer = getXmlWriter();
    if ( expression instanceof FormulaExpression ) {
      final FormulaExpression fe = (FormulaExpression) expression;
      final AttributeList properties = new AttributeList();
      if ( expression.getName() != null ) {
        properties.setAttribute( ExtParserModule.NAMESPACE, "name", expression.getName() );
      }
      properties.setAttribute( ExtParserModule.NAMESPACE, "formula", fe.getFormula() );
      if ( expression.getDependencyLevel() > 0 ) {
        properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String
            .valueOf( expression.getDependencyLevel() ) );
      }
      writer.writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.EXPRESSION_TAG, properties, XmlWriterSupport.CLOSE );
      return;
    }

    if ( expression instanceof FormulaFunction ) {
      final FormulaFunction fe = (FormulaFunction) expression;
      final AttributeList properties = new AttributeList();
      if ( expression.getName() != null ) {
        properties.setAttribute( ExtParserModule.NAMESPACE, "name", expression.getName() );
      }
      properties.setAttribute( ExtParserModule.NAMESPACE, "formula", fe.getFormula() );
      properties.setAttribute( ExtParserModule.NAMESPACE, "initial", fe.getInitial() );
      if ( expression.getDependencyLevel() > 0 ) {
        properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String
            .valueOf( expression.getDependencyLevel() ) );
      }
      writer.writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.EXPRESSION_TAG, properties, XmlWriterSupport.CLOSE );
      return;
    }

    try {
      final BeanUtility bu = new BeanUtility( expression );
      final String[] propertyNames = bu.getProperties();
      if ( propertyNames.length == 0 ) {
        final AttributeList properties = new AttributeList();
        if ( expression.getName() != null ) {
          properties.setAttribute( ExtParserModule.NAMESPACE, "name", expression.getName() );
        }
        properties.setAttribute( ExtParserModule.NAMESPACE, "class", expression.getClass().getName() );
        if ( expression.getDependencyLevel() > 0 ) {
          properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String.valueOf( expression
              .getDependencyLevel() ) );
        }
        writer.writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.EXPRESSION_TAG, properties, XmlWriterSupport.CLOSE );
      } else {
        final AttributeList properties = new AttributeList();
        if ( expression.getName() != null ) {
          properties.setAttribute( ExtParserModule.NAMESPACE, "name", expression.getName() );
        }
        properties.setAttribute( ExtParserModule.NAMESPACE, "class", expression.getClass().getName() );
        if ( expression.getDependencyLevel() > 0 ) {
          properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String.valueOf( expression
              .getDependencyLevel() ) );
        }
        writer.writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.EXPRESSION_TAG, properties, XmlWriterSupport.OPEN );

        writeExpressionParameters( propertyNames, bu );

        writer.writeCloseTag();
      }

    } catch ( Exception e ) {
      FunctionsWriter.logger.error( "Failed to write the expression", e );
      throw new IOException( "Unable to extract or write properties." );
    }
  }

  public void writeStyleExpression( final Expression expression, final StyleKey styleKey ) throws IOException {
    if ( expression instanceof FormulaExpression ) {
      final FormulaExpression fe = (FormulaExpression) expression;
      final AttributeList properties = new AttributeList();
      properties.setAttribute( ExtParserModule.NAMESPACE, "style-key", styleKey.getName() );
      properties.setAttribute( ExtParserModule.NAMESPACE, "formula", fe.getFormula() );
      if ( expression.getDependencyLevel() > 0 ) {
        properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String
            .valueOf( expression.getDependencyLevel() ) );
      }
      getXmlWriter().writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.STYLE_EXPRESSION_TAG, properties,
          XmlWriterSupport.CLOSE );
      return;
    }

    if ( expression instanceof FormulaFunction ) {
      final FormulaFunction fe = (FormulaFunction) expression;
      final AttributeList properties = new AttributeList();
      properties.setAttribute( ExtParserModule.NAMESPACE, "style-key", styleKey.getName() );
      properties.setAttribute( ExtParserModule.NAMESPACE, "formula", fe.getFormula() );
      properties.setAttribute( ExtParserModule.NAMESPACE, "initial", fe.getInitial() );
      if ( expression.getDependencyLevel() > 0 ) {
        properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String
            .valueOf( expression.getDependencyLevel() ) );
      }
      getXmlWriter().writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.STYLE_EXPRESSION_TAG, properties,
          XmlWriterSupport.CLOSE );
      return;
    }

    try {
      final BeanUtility bu = new BeanUtility( expression );
      final String[] propertyNames = bu.getProperties();
      if ( propertyNames.length == 0 ) {
        final AttributeList properties = new AttributeList();
        properties.setAttribute( ExtParserModule.NAMESPACE, "style-key", styleKey.getName() );
        properties.setAttribute( ExtParserModule.NAMESPACE, "class", expression.getClass().getName() );
        if ( expression.getDependencyLevel() > 0 ) {
          properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String.valueOf( expression
              .getDependencyLevel() ) );
        }
        getXmlWriter().writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.STYLE_EXPRESSION_TAG, properties,
            XmlWriterSupport.CLOSE );
      } else {
        final AttributeList properties = new AttributeList();
        properties.setAttribute( ExtParserModule.NAMESPACE, "style-key", styleKey.getName() );
        properties.setAttribute( ExtParserModule.NAMESPACE, "class", expression.getClass().getName() );
        if ( expression.getDependencyLevel() > 0 ) {
          properties.setAttribute( ExtParserModule.NAMESPACE, "deplevel", String.valueOf( expression
              .getDependencyLevel() ) );
        }
        getXmlWriter().writeTag( ExtParserModule.NAMESPACE, FunctionsWriter.STYLE_EXPRESSION_TAG, properties,
            XmlWriterSupport.OPEN );

        writeExpressionParameters( propertyNames, bu );

        getXmlWriter().writeCloseTag();
      }

    } catch ( Exception e ) {
      throw new IOException( "Unable to extract or write properties." );
    }
  }

  /**
   * Writes the parameters for an expression or function.
   *
   * @param propertyNames
   *          the names of the properties.
   * @param beanUtility
   *          the bean utility containing the expression bean.
   * @throws IOException
   *           if an IO error occurs.
   * @throws BeanException
   *           if a bean error occured.
   */
  private void writeExpressionParameters( final String[] propertyNames, final BeanUtility beanUtility )
    throws IOException, BeanException {
    final XmlWriter writer = getXmlWriter();
    writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.PROPERTIES_TAG, XmlWriterSupport.OPEN );

    for ( int i = 0; i < propertyNames.length; i++ ) {
      final String key = propertyNames[i];
      // filter some of the standard properties. These are system-properties
      // and are set elsewhere
      if ( "name".equals( key ) ) {
        continue;
      }
      if ( "dependencyLevel".equals( key ) ) {
        continue;
      }
      if ( "runtime".equals( key ) ) {
        continue;
      }
      if ( "active".equals( key ) ) {
        continue;
      }
      if ( "preserve".equals( key ) ) {
        continue;
      }

      final Object property = beanUtility.getProperty( key );
      final Class propertyType = beanUtility.getPropertyType( key );
      final String value = beanUtility.getPropertyAsString( key );
      if ( value != null && property != null ) {
        final AttributeList attList = new AttributeList();
        attList.setAttribute( ExtParserModule.NAMESPACE, "name", key );
        if ( BeanUtility.isSameType( propertyType, property.getClass() ) == false ) {
          attList.setAttribute( ExtParserModule.NAMESPACE, "class", property.getClass().getName() );
        }
        writer.writeTag( ExtParserModule.NAMESPACE, "property", attList, XmlWriterSupport.OPEN );
        writer.writeTextNormalized( value, false );
        writer.writeCloseTag();
      }
    }

    writer.writeCloseTag();
  }

}
