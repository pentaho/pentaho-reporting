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

import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A report group. Reports can contain any number of (nested) groups. The order of the fields is not important. If the
 * group does not contain any fields, the group spans the whole report from the first to the last row (such a group is
 * called the default group).
 * <p/>
 * The group's field list should not be modified after the group was added to the group list, or the results are
 * undefined.
 * <p/>
 * Groups of the same GroupList must have a subgroup relation. The designated child group must contain all fields of the
 * direct parent plus at least one new field. There is no requirement, that the referenced field actually exists, if it
 * doesn't, null is assumed as field value.
 * <p/>
 * It is recommended that the name of the group is unique within the report. The name will not be used internally to
 * identify the group, but most functions depend on a recognizable group name to identify the group to be processed.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public abstract class Group extends Section {
  private GroupBody body;
  private transient String generatedName;

  /**
   * Constructs a group with no fields, and an empty header and footer.
   */
  protected Group() {
    this.body = createDefaultBody();

    registerAsChild( body );
  }

  protected Group( final GroupBody body ) {
    if ( body == null ) {
      throw new NullPointerException();
    }

    this.body = body;

    registerAsChild( body );
  }

  public GroupBody getBody() {
    return body;
  }

  public void setBody( final GroupBody body ) {
    if ( body == null ) {
      throw new NullPointerException( "The body must not be null" );
    }
    validateLooping( body );
    if ( unregisterParent( body ) ) {
      return;
    }

    final Element element = this.body;
    this.body.setParent( null );
    this.body = body;
    this.body.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.body );
  }

  /**
   * Clones this Element.
   *
   * @return a clone of this element.
   */
  public Group clone() {
    final Group g = (Group) super.clone();
    g.body = (GroupBody) body.clone();

    g.registerAsChild( g.body );
    return g;
  }

  public Group derive( final boolean preserveElementInstanceIds ) {
    final Group g = (Group) super.derive( preserveElementInstanceIds );
    g.body = (GroupBody) body.derive( preserveElementInstanceIds );

    g.registerAsChild( g.body );
    return g;
  }

  public abstract boolean isGroupChange( final DataRow dataRow );

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( body == element ) {
      this.body.setParent( null );
      this.body = createDefaultBody();
      this.body.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.body );
    }
    // Else: Ignore the request, none of my childs.
  }

  protected abstract GroupBody createDefaultBody();

  protected void notifyElement() {
    this.generatedName = null;
  }

  public String getGeneratedName() {
    final String generatedName = this.generatedName;
    if ( generatedName != null ) {
      return generatedName;
    }

    final String name = generatedName();
    this.generatedName = name;
    return name;
  }

  public String getName() {
    final String name = super.getName();
    if ( StringUtils.isEmpty( name ) ) {
      return getGeneratedName();
    } else {
      return name;
    }
  }

  public boolean matches( final String name ) {
    if ( ObjectUtilities.equal( name, getName() ) ) {
      return true;
    }

    return ObjectUtilities.equal( name, getGeneratedName() );
  }

  private String generatedName() {
    int parentGroupCounter = 0;
    Section parent = getParentSection();
    while ( parent != null && parent instanceof ReportDefinition == false ) {
      if ( parent instanceof Group ) {
        parentGroupCounter += 1;
      }
      parent = parent.getParentSection();
    }

    return "::group-" + parentGroupCounter;
  }

  public abstract List<SortConstraint> getSortingConstraint();

  protected List<SortConstraint> mapFields( List<String> fields ) {
    boolean ascending = isAscendingSortOrder();
    final ArrayList<SortConstraint> c = new ArrayList<SortConstraint>( fields.size() );
    for ( final String field : fields ) {
      if ( !StringUtils.isEmpty( field ) ) {
        c.add( new SortConstraint( field, ascending ) );
      }
    }
    return c;
  }

  public boolean isAscendingSortOrder() {
    Object attribute = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SORT_ORDER );
    if ( Boolean.FALSE.equals( attribute ) ) {
      return false;
    }
    return true;
  }

  public void setAscendingSortOrder( final Boolean order ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SORT_ORDER, order );
  }
}
