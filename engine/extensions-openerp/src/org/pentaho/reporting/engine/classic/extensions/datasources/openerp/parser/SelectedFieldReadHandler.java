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
* Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.openerp.parser;

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
 * Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
 */


import com.debortoliwines.openerp.api.Field.FieldType;
import com.debortoliwines.openerp.reporting.di.OpenERPFieldInfo;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Class to read search filter parameters
 *
 * @author Pieter van der Merwe
 */
public class SelectedFieldReadHandler extends AbstractXmlReadHandler {
  private final ArrayList<OpenERPFieldInfo> allFields;
  private SelectedFieldReadHandler parentReadHandler;

  private int instanceNum;
  private String modelName;
  private String fieldName;
  private String renamedFieldName;
  private FieldType fieldType;
  private String relatedChildModelName;
  private int sortIndex;
  private int sortDirection;

  public SelectedFieldReadHandler( ArrayList<OpenERPFieldInfo> allFields ) {
    this.allFields = allFields;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    // Store the values.  Can't store attrs (input) for later use since it is a reference that gets reused for every
    // element.
    // No attrs.clone() available
    try {
      this.instanceNum = Integer.parseInt( attrs.getValue( getUri(), "instanceNum" ) );
    } catch ( Exception e ) {
      this.instanceNum = 1;
    }

    try {
      this.sortIndex = Integer.parseInt( attrs.getValue( getUri(), "sortIndex" ) );
    } catch ( Exception e ) {
      this.sortIndex = 0;
    }

    try {
      this.sortDirection = Integer.parseInt( attrs.getValue( getUri(), "sortDirection" ) );
    } catch ( Exception e ) {
      this.sortDirection = 0;
    }

    this.modelName = attrs.getValue( getUri(), "modelName" );
    this.fieldName = attrs.getValue( getUri(), "fieldName" );
    this.renamedFieldName = attrs.getValue( getUri(), "renamedFieldName" );
    this.fieldType = FieldType.valueOf( attrs.getValue( getUri(), "fieldType" ) );
    this.relatedChildModelName = attrs.getValue( getUri(), "relatedChildModelName" );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return null;
  }

  public OpenERPFieldInfo getField() {

    // Do the parent first because we need the parent for the modelPath to be accurate (And the unique lookup to work)
    OpenERPFieldInfo parentItem = null;
    if ( parentReadHandler != null ) {
      parentItem = parentReadHandler.getField();
    }

    OpenERPFieldInfo field = new OpenERPFieldInfo(
      this.modelName,
      this.instanceNum,
      this.fieldName,
      this.renamedFieldName,
      parentItem,
      this.fieldType,
      this.relatedChildModelName,
      this.sortIndex,
      this.sortDirection );

    // When the objects were written, all parents were for each child even if some parents had multiple children
    // Fix it up so only one parent is referenced and every child doesn't have a separate parent object
    int fieldIndex = allFields.indexOf( field );
    if ( fieldIndex >= 0 ) {
      field = allFields.get( fieldIndex );
    } else {
      allFields.add( field );
    }

    return field;
  }

  @Override
  protected XmlReadHandler getHandlerForChild( String uri, String tagName,
                                               Attributes atts ) throws SAXException {

    // Even though it is technically a child object, the parent field was stored as the only child of this element
    if ( "selectedField".equals( tagName ) ) {
      parentReadHandler = new SelectedFieldReadHandler( this.allFields );
      return parentReadHandler;
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

}
