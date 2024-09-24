/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements.BandTopLevelElementReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ReportGroupReadHandler extends AbstractXmlReadHandler {
  private ReportGroupReadHandler groupReadHandler;
  private BandTopLevelElementReadHandler headerReadHandler;
  private BandTopLevelElementReadHandler footerReadHandler;
  private Group group;
  private PropertyReadHandler nameReadHandler;
  private PropertyArrayReadHandler groupFieldsReadHandler;

  public ReportGroupReadHandler() {
  }

  public Group getGroup() {
    return group;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "padding".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }
    if ( "property".equals( tagName ) ) {

      final String type = atts.getValue( uri, "name" );
      if ( "name".equals( type ) ) {
        nameReadHandler = new PropertyReadHandler();
        return nameReadHandler;
      }
      if ( "groupFields".equals( type ) ) {
        groupFieldsReadHandler = new PropertyArrayReadHandler( String.class );
        return groupFieldsReadHandler;
      }
      return new IgnoreAnyChildReadHandler();
    }
    if ( "child".equals( tagName ) ) {
      final String type = atts.getValue( uri, "type" );
      if ( "org.pentaho.reportdesigner.crm.report.model.ReportGroup".equals( type ) ) {
        groupReadHandler = new ReportGroupReadHandler();
        return groupReadHandler;
      }
      if ( "org.pentaho.reportdesigner.crm.report.model.BandToplevelGroupReportElement".equals( type ) ) {
        final String bandtype = atts.getValue( uri, "bandToplevelType" );
        if ( "GROUP_FOOTER".equals( bandtype ) ) {
          footerReadHandler = new BandTopLevelElementReadHandler( new GroupFooter(), bandtype );
          return footerReadHandler;
        }
        if ( "GROUP_HEADER".equals( bandtype ) ) {
          headerReadHandler = new BandTopLevelElementReadHandler( new GroupHeader(), bandtype );
          return headerReadHandler;
        }
      }
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final RelationalGroup group = new RelationalGroup();
    if ( nameReadHandler != null ) {
      group.setName( nameReadHandler.getResult() );
    }
    if ( groupFieldsReadHandler != null ) {
      final Object[] data = (Object[]) groupFieldsReadHandler.getObject();
      final String[] realValue = new String[ data.length ];
      System.arraycopy( data, 0, realValue, 0, data.length );
      group.setFieldsArray( realValue );
    }
    if ( headerReadHandler != null ) {
      group.setHeader( (GroupHeader) headerReadHandler.getBand() );
    }
    if ( footerReadHandler != null ) {
      group.setFooter( (GroupFooter) footerReadHandler.getBand() );
    }
    if ( groupReadHandler != null ) {
      final SubGroupBody subGroupBody = new SubGroupBody();
      subGroupBody.setGroup( groupReadHandler.getGroup() );
      group.setBody( subGroupBody );
    } else {
      group.setBody( new GroupDataBody() );
    }

    this.group = group;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return group;
  }
}
