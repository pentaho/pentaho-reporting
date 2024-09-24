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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class FunctionsReadHandler extends AbstractPropertyXmlReadHandler {
  private static Log logger = LogFactory.getLog( FunctionsReadHandler.class );
  private AbstractReportDefinition report;
  private ArrayList<ExpressionReadHandler> expressionHandlers;
  private ArrayList<PropertyReferenceReadHandler> propertyRefs;

  public FunctionsReadHandler( final AbstractReportDefinition report ) {
    this.report = report;
    this.expressionHandlers = new ArrayList<ExpressionReadHandler>();
    this.propertyRefs = new ArrayList<PropertyReferenceReadHandler>();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param attrs
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes attrs )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "expression".equals( tagName ) || "function".equals( tagName ) ) {
      final ExpressionReadHandler readHandler = new ExpressionReadHandler();
      expressionHandlers.add( readHandler );
      return readHandler;

    } else if ( "property-ref".equals( tagName ) ) {
      final PropertyReferenceReadHandler readHandler = new PropertyReferenceReadHandler();
      propertyRefs.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < expressionHandlers.size(); i++ ) {
      final ExpressionReadHandler readHandler = (ExpressionReadHandler) expressionHandlers.get( i );
      if ( readHandler.getObject() != null ) {
        report.addExpression( (Expression) readHandler.getObject() );
      }
    }

    final MasterReport master;
    if ( report instanceof MasterReport ) {
      master = (MasterReport) report;
    } else {
      master = null;
    }

    for ( int i = 0; i < propertyRefs.size(); i++ ) {
      final PropertyReferenceReadHandler readHandler = propertyRefs.get( i );
      final Object object = readHandler.getObject();
      if ( object != null ) {
        if ( object instanceof String ) {
          final String text = (String) object;
          if ( text.length() == 0 ) {
            continue;
          }
        }

        if ( master != null ) {
          final ParameterDefinitionEntry[] parameterDefinitions =
              master.getParameterDefinition().getParameterDefinitions();
          boolean foundParameter = false;
          for ( int j = 0; j < parameterDefinitions.length; j++ ) {
            final ParameterDefinitionEntry definition = parameterDefinitions[j];
            if ( readHandler.getPropertyName().equals( definition.getName() ) ) {
              foundParameter = true;
              break;
            }
          }
          if ( foundParameter == false ) {
            if ( master.getParameterDefinition() instanceof ModifiableReportParameterDefinition ) {
              final ModifiableReportParameterDefinition parameterDefinition =
                  (ModifiableReportParameterDefinition) master.getParameterDefinition();
              parameterDefinition.addParameterDefinition( new PlainParameter( readHandler.getPropertyName() ) );
            }
          }
          master.getParameterValues().put( readHandler.getPropertyName(), object );
        } else {
          logger.warn( "Subreports are not supposed to have "
              + "parameter nor report properties. Ignoring definition for '" + readHandler.getPropertyName() + "." );
        }
      }
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
