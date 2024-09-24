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

package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GroupDefinitionReadHandler extends AbstractXmlReadHandler {
  private GroupDefinition groupDefinition;

  public GroupDefinitionReadHandler() {
    groupDefinition = new DefaultGroupDefinition();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {

    final String thAlightAttr = attrs.getValue( getUri(), "totals-align" );
    groupDefinition.setTotalsHorizontalAlignment
      ( ReportParserUtil.parseHorizontalElementAlignment( thAlightAttr, getLocator() ) );
    groupDefinition.setNullString( attrs.getValue( getUri(), "null-string" ) );
    groupDefinition.setDisplayName( attrs.getValue( getUri(), "display-name" ) );
    groupDefinition.setField( attrs.getValue( getUri(), "field" ) );
    groupDefinition.setDataFormat( attrs.getValue( getUri(), "data-format" ) );

    final String aggFunctionName = attrs.getValue( getUri(), "aggregation-function" );
    if ( aggFunctionName != null ) {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader( DetailsFieldDefinitionReadHandler.class );
      try {
        final Class c = Class.forName( aggFunctionName, false, classLoader );
        if ( Expression.class.isAssignableFrom( c ) == false ) {
          throw new ParseException( "Failed to parse attribute 'aggregation-function': invalid class name" );
        }
        groupDefinition.setAggregationFunction( c );
      } catch ( final ClassNotFoundException e ) {
        throw new ParseException( "Failed to parse attribute 'aggregation-function': invalid class name", e );
      }
    }

    groupDefinition.setGroupName( attrs.getValue( getUri(), "group-name" ) );
    groupDefinition.setGroupTotalsLabel( attrs.getValue( getUri(), "group-totals-label" ) );
    groupDefinition.setGroupType( parseGroupType( attrs.getValue( getUri(), "group-type" ) ) );
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }


    if ( ( "footer".equals( tagName ) ) || ( "group-footer".equals( tagName ) ) ) {
      return new RootBandDefinitionReadHandler( groupDefinition.getFooter() );
    }
    if ( ( "header".equals( tagName ) || ( "group-header".equals( tagName ) ) ) ) {
      return new RootBandDefinitionReadHandler( groupDefinition.getHeader() );
    }
    return null;
  }

  private GroupType parseGroupType( final String s ) {
    final GroupType[] types = GroupType.values();
    for ( int i = 0; i < types.length; i++ ) {
      final GroupType type = types[ i ];
      if ( type.getType().equals( s ) ) {
        return type;
      }
    }
    return GroupType.RELATIONAL;
  }

  public Object getObject() throws SAXException {
    return groupDefinition;
  }
}
