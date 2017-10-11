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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.GroupFieldsReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class GroupReadHandler extends AbstractPropertyXmlReadHandler {
  /**
   * Literal text for an XML report element.
   */
  public static final String GROUP_HEADER_TAG = "group-header";

  /**
   * Literal text for an XML report element.
   */
  public static final String GROUP_FOOTER_TAG = "group-footer";

  /**
   * Literal text for an XML report element.
   */
  public static final String FIELDS_TAG = "fields";

  /**
   * Literal text for an XML report element.
   */
  public static final String FIELD_TAG = "field";

  private static final String NAME_ATT = "name";

  private GroupList groupList;
  private RelationalGroup group;

  public GroupReadHandler( final GroupList groupList ) {
    this.groupList = groupList;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final String groupName = attrs.getValue( getUri(), GroupReadHandler.NAME_ATT );
    if ( groupName != null ) {
      final AbstractReportDefinition report =
          (AbstractReportDefinition) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
      final Group maybeDefaultGroup = report.getGroupByName( groupName );
      if ( maybeDefaultGroup instanceof RelationalGroup ) {
        group = (RelationalGroup) maybeDefaultGroup;
      } else {
        group = new RelationalGroup();
        group.setName( groupName );
        group.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, getRootHandler().getSource() );
      }
    } else {
      group = new RelationalGroup();
      group.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, getRootHandler().getSource() );
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( tagName.equals( GroupReadHandler.GROUP_HEADER_TAG ) ) {
      return new RootBandReadHandler( group.getHeader() );
    } else if ( tagName.equals( GroupReadHandler.GROUP_FOOTER_TAG ) ) {
      return new RootBandReadHandler( group.getFooter() );
    } else if ( tagName.equals( GroupReadHandler.FIELDS_TAG ) ) {
      return new GroupFieldsReadHandler( group );
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
    groupList.add( group );
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return group;
  }
}
