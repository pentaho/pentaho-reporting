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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A group that accepts fields.
 *
 * @author Thomas Morgner
 */
public class RelationalGroup extends Group {
  private static final String[] EMPTY_FIELDS = new String[0];

  private GroupHeader header;
  private GroupFooter footer;

  public RelationalGroup() {
    setElementType( new RelationalGroupType() );
    this.footer = new GroupFooter();
    this.header = new GroupHeader();

    registerAsChild( footer );
    registerAsChild( header );
  }

  /**
   * Returns the group header.
   * <P>
   * The group header is a report band that contains elements that should be printed at the start of a group.
   *
   * @return the group header.
   */
  public GroupHeader getHeader() {
    return header;
  }

  /**
   * Sets the header for the group.
   *
   * @param header
   *          the header (null not permitted).
   * @throws NullPointerException
   *           if the given header is null
   */
  public void setHeader( final GroupHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "Header must not be null" );
    }
    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }

    final Element element = this.header;
    this.header.setParent( null );
    this.header = header;
    this.header.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.header );
  }

  /**
   * Returns the group footer.
   *
   * @return the footer.
   */
  public GroupFooter getFooter() {
    return footer;
  }

  /**
   * Sets the footer for the group.
   *
   * @param footer
   *          the footer (null not permitted).
   * @throws NullPointerException
   *           if the given footer is null.
   */
  public void setFooter( final GroupFooter footer ) {
    if ( footer == null ) {
      throw new NullPointerException( "The footer must not be null" );
    }
    validateLooping( footer );
    if ( unregisterParent( footer ) ) {
      return;
    }

    final Element element = this.footer;
    this.footer.setParent( null );
    this.footer = footer;
    this.footer.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.footer );
  }

  /**
   * Sets the fields for this group. The given list must contain Strings defining the needed fields from the DataRow.
   * Don't reference Function-Fields here, functions are not supported in th groupfield definition.
   *
   * @param c
   *          the list containing strings.
   * @throws NullPointerException
   *           if the given list is null or the list contains null-values.
   */
  public void setFields( final List<String> c ) {
    if ( c == null ) {
      throw new NullPointerException();
    }
    final String[] fields = c.toArray( new String[c.size()] );
    setFieldsArray( fields );
  }

  public void clearFields() {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, EMPTY_FIELDS );
  }

  protected GroupBody createDefaultBody() {
    return new GroupDataBody();
  }

  public int getElementCount() {
    return 3;
  }

  /**
   * Adds a field to the group. The field names must correspond to the column names in the report's TableModel.
   *
   * @param name
   *          the field name (null not permitted).
   * @throws NullPointerException
   *           if the name is null
   */
  public void addField( final String name ) {
    if ( name == null ) {
      throw new NullPointerException( "Group.addField(...): name is null." );
    }
    final ArrayList<String> fieldsList = new ArrayList<String>( getFields() );
    fieldsList.add( name );
    Collections.sort( fieldsList );
    setFieldsArray( fieldsList.toArray( new String[fieldsList.size()] ) );
  }

  /**
   * Returns the list of fields for this group.
   *
   * @return a list (unmodifiable) of fields for the group.
   */
  public List<String> getFields() {
    return Collections.unmodifiableList( Arrays.asList( getFieldsArray() ) );
  }

  public void setFieldsArray( final String[] fields ) {
    if ( fields == null ) {
      throw new NullPointerException();
    }
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, fields.clone() );
  }

  /**
   * Returns the group fields as array.
   *
   * @return the fields as string array.
   */
  public String[] getFieldsArray() {
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS );
    if ( o instanceof String[] ) {
      final String[] fields = (String[]) o;
      return fields.clone();
    }
    return EMPTY_FIELDS;
  }

  /**
   * Returns a string representation of the group (useful for debugging).
   *
   * @return A string.
   */
  public String toString() {
    final StringBuilder b = new StringBuilder( 120 );
    b.append( "org.pentaho.reporting.engine.classic.core.RelationalGroup={Name='" );
    b.append( getName() );
    b.append( "', GeneratedName=" );
    b.append( getGeneratedName() );
    b.append( "', fields=" );
    b.append( getFields() );
    b.append( "} " );
    return b.toString();
  }

  public void setBody( final GroupBody body ) {
    if ( body instanceof GroupDataBody == false && body instanceof SubGroupBody == false ) {
      throw new IllegalArgumentException();
    }

    super.setBody( body );
  }

  /**
   * Checks whether the group is equal. A group is considered equal to another group, if it defines the same fields as
   * the other group.
   *
   * @param obj
   *          the object to be checked
   * @return true, if the object is a group instance with the same fields, false otherwise.
   */
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( !( obj instanceof RelationalGroup ) ) {
      return false;
    }

    final RelationalGroup group = (RelationalGroup) obj;
    final String[] otherFields = group.getFieldsArray();
    final String[] myFields = getFieldsArray();
    if ( ObjectUtilities.equalArray( otherFields, myFields ) == false ) {
      return false;
    }
    return true;
  }

  /**
   * Computes a hashcode for this group.
   *
   * @return the hashcode.
   */
  public int hashCode() {
    final String[] fields = getFieldsArray();

    int hashCode = 0;
    final int length = fields.length;
    for ( int i = 0; i < length; i++ ) {
      final String field = fields[i];
      if ( field == null ) {
        hashCode = 29 * hashCode;
      } else {
        hashCode = 29 * hashCode + field.hashCode();
      }
    }
    return hashCode;
  }

  public boolean isGroupChange( final DataRow dataRow ) {
    // compare item and item+1, if any field differs, then item==last in group
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS );
    if ( o instanceof String[] ) {
      final String[] fields = (String[]) o;
      for ( int i = 0; i < fields.length; i++ ) {
        final String field = fields[i];
        if ( field != null && dataRow.isChanged( field ) ) {
          return true;
        }
      }
    }

    return false;
  }

  @Deprecated
  public GroupDataBody findGroupDataBody() {
    final GroupBody body = getBody();
    if ( body instanceof GroupDataBody ) {
      return (GroupDataBody) body;
    }
    if ( body instanceof SubGroupBody ) {
      final SubGroupBody groupBody = (SubGroupBody) body;
      final Group group = groupBody.getGroup();
      if ( group instanceof RelationalGroup ) {
        final RelationalGroup rg = (RelationalGroup) group;
        return rg.findGroupDataBody();
      }
    }

    return null;
  }

  /**
   * Clones this Element.
   *
   * @return a clone of this element.
   */
  public RelationalGroup clone() {
    final RelationalGroup g = (RelationalGroup) super.clone();
    g.footer = (GroupFooter) footer.clone();
    g.header = (GroupHeader) header.clone();

    g.registerAsChild( g.footer );
    g.registerAsChild( g.header );
    return g;
  }

  public RelationalGroup derive( final boolean preserveElementInstanceIds ) {
    final RelationalGroup g = (RelationalGroup) super.derive( preserveElementInstanceIds );
    g.footer = (GroupFooter) footer.derive( preserveElementInstanceIds );
    g.header = (GroupHeader) header.derive( preserveElementInstanceIds );

    g.registerAsChild( g.footer );
    g.registerAsChild( g.header );
    return g;
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( footer == element ) {
      this.footer.setParent( null );
      this.footer = new GroupFooter();
      this.footer.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.footer );

    } else if ( header == element ) {
      this.header.setParent( null );
      this.header = new GroupHeader();
      this.header.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.header );
    } else {
      super.removeElement( element );
    }
    // Else: Ignore the request, none of my childs.
  }

  public Element getElement( final int index ) {
    switch ( index ) {
      case 0:
        return header;
      case 1:
        return getBody();
      case 2:
        return footer;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setElementAt( final int index, final Element element ) {
    switch ( index ) {
      case 0:
        setHeader( (GroupHeader) element );
        break;
      case 1:
        setBody( (GroupBody) element );
        break;
      case 2:
        setFooter( (GroupFooter) element );
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public List<SortConstraint> getSortingConstraint() {
    return mapFields( getFields() );
  }
}
