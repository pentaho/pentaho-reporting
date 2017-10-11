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

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The group list is used to store groups in a ordered way. The less specific groups are guaranteed to be listed before
 * the more specific subgroups.
 * <p/>
 * Groups are ordered by comparing the declared fieldnames for the groups. A subgroup of an group must contain all
 * fields from its parent plus at least one new field.
 * <p/>
 * This implementation is not synchronized.
 * <p/>
 * The group list cannot be empty. JFreeReport needs at least one group instance to work as expected. By default, this
 * default instance does not define any fields (and therefore contains the complete report) and has no Bands defined
 * (rendering it invisible). You cannot remove that group. Every attempt to remove the last group will recreates a new
 * default group.
 * <p/>
 * As of version 0.8.10, this list only exists for the support for the legacy parsing.
 *
 * @author Thomas Morgner
 * @deprecated The group-list is a legacy class and should not be used outside the legacy handling.
 */
public class GroupList implements Cloneable, Serializable {
  /**
   * A unique identifier for long term persistance.
   */
  private static final long serialVersionUID = 2193162824440886046L;

  /**
   * Cache.
   */
  private transient RelationalGroup[] cache;

  /**
   * The backend to store the groups.
   */
  private ArrayList backend;

  /**
   * The name of the automaticly created default group.
   */
  public static final String DEFAULT_GROUP_NAME = "default";

  /**
   * Constructs a new group list, with only a default group inside.
   */
  public GroupList() {
    backend = new ArrayList();
    createDefaultGroup();
  }

  /**
   * Creates a default group. The default group has no fields defined and spans all fields of the report.
   */
  private void createDefaultGroup() {
    final RelationalGroup defaultGroup = new RelationalGroup();
    add( defaultGroup );
  }

  /**
   * Creates a new group list and copies the contents of the given grouplist. If the given group list was assigned with
   * a report definition, then the new group list will share that registration.
   *
   * @param list
   *          groups to add to the list.
   */
  protected GroupList( final GroupList list ) {
    backend = new ArrayList();
    backend.addAll( list.backend );
  }

  /**
   * Returns the group at a given position in the list.
   *
   * @param i
   *          the position index (zero-based).
   * @return the report group.
   */
  public Group get( final int i ) {
    if ( cache == null ) {
      cache = (RelationalGroup[]) backend.toArray( new RelationalGroup[backend.size()] );
    }
    return cache[i];
  }

  /**
   * Removes an group from the list.
   *
   * @param o
   *          the group that should be removed.
   * @return a boolean indicating whether or not the object was removed.
   * @throws NullPointerException
   *           if the given group object is null.
   */
  public boolean remove( final RelationalGroup o ) {
    if ( o == null ) {
      throw new NullPointerException();
    }
    cache = null;
    final int idxOf = backend.indexOf( o );
    if ( idxOf == -1 ) {
      // the object was not in the list ...
      return false;
    }

    // it might as well be a group that looks like the one we have in the list
    // so be sure that you modify the one, that was removed, and not the one given
    // to us.
    backend.remove( idxOf );

    if ( backend.isEmpty() ) {
      createDefaultGroup();
    }
    return true;
  }

  /**
   * Clears the list.
   */
  public void clear() {
    backend.clear();
    createDefaultGroup();
    cache = null;
  }

  /**
   * Adds a group to the list.
   *
   * @param o
   *          the group object.
   */
  public void add( final RelationalGroup o ) {
    if ( o == null ) {
      throw new NullPointerException( "Try to add null" );
    }
    cache = null;
    final int idxOf = backend.indexOf( o );
    if ( idxOf != -1 ) {
      // it might as well be a group that looks like the one we have in the list
      // so be sure that you modify the one, that was removed, and not the one given
      // to us.
      backend.remove( idxOf );
    }

    // this is a linear search to find the correct insertation point ..
    for ( int i = 0; i < backend.size(); i++ ) {
      final RelationalGroup compareGroup = (RelationalGroup) backend.get( i );
      // if the current group at index i is greater than the new group
      if ( compareGroups( compareGroup, o ) > 0 ) {
        // then insert the new one before the current group ..
        backend.add( i, o );
        return;
      }
    }
    // finally, if this group is the smallest group ...
    backend.add( o );
  }

  /**
   * Adds all groups of the collection to this group list. This method will result in a ClassCastException if the
   * collection does not contain Group objects.
   *
   * @param c
   *          the collection that contains the groups.
   * @throws NullPointerException
   *           if the given collection is null.
   * @throws ClassCastException
   *           if the collection does not contain groups.
   */
  public void addAll( final Collection c ) {
    final Iterator it = c.iterator();
    while ( it.hasNext() ) {
      add( (RelationalGroup) it.next() );
    }
  }

  /**
   * Clones the group list and all contained groups.
   *
   * @return a clone of this list.
   * @throws CloneNotSupportedException
   *           if cloning the element failed.
   * @see Cloneable
   */
  public Object clone() throws CloneNotSupportedException {
    final GroupList l = (GroupList) super.clone();
    final Group[] groups = getGroupCache();

    l.backend = (ArrayList) backend.clone();
    l.backend.clear();
    final int length = groups.length;
    l.cache = new RelationalGroup[length];
    for ( int i = 0; i < length; i++ ) {
      final RelationalGroup group = (RelationalGroup) groups[i].clone();
      l.backend.add( group );
      l.cache[i] = group;
    }
    return l;
  }

  /**
   * Returns an iterator for the groups of the list.
   *
   * @return An iterator over all groups of the list.
   */
  public Iterator iterator() {
    return Collections.unmodifiableList( backend ).iterator();
  }

  /**
   * Returns the number of groups in the list.
   *
   * @return The number of groups in the list.
   */
  public int size() {
    return backend.size();
  }

  /**
   * Returns a string representation of the list (useful for debugging).
   *
   * @return A string.
   */
  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "GroupList={backend='" );
    b.append( backend );
    b.append( "'} " );
    return b.toString();
  }

  /**
   * Returns a direct reference to the group cache.
   *
   * @return the groups of this list as array.
   */
  protected RelationalGroup[] getGroupCache() {
    if ( cache == null ) {
      cache = (RelationalGroup[]) backend.toArray( new RelationalGroup[backend.size()] );
    }
    return cache;
  }

  /**
   * Searches a group by its defined name. This method returns null, if the group was not found.
   *
   * @param name
   *          the name of the group.
   * @return the group or null if not found.
   */
  public Group getGroupByName( final String name ) {
    if ( name == null ) {
      // Groups cannot have a null-name
      return null;
    }

    final Group[] cache = getGroupCache();
    final int length = cache.length;
    for ( int i = 0; i < length; i++ ) {
      if ( name.equals( cache[i].getName() ) ) {
        return cache[i];
      }
    }
    return null;
  }

  /**
   * Creates a hierarchical group structure and moves the data group body to the inner most group. This method is only
   * guaranteed to work correctly if there is exactly one data-group.
   *
   * @return the constructed group.
   */
  public Group constructRootGroup() {
    final RelationalGroup[] cache = getGroupCache();
    if ( cache.length == 0 ) {
      return new RelationalGroup();
    }

    GroupDataBody dataBody = null;

    final Group rootGroup = cache[0];
    Group currentGroup = rootGroup;
    for ( int i = 1; i < cache.length; i++ ) {
      final Group g = cache[i];

      final GroupBody body = currentGroup.getBody();
      if ( body instanceof SubGroupBody ) {
        final SubGroupBody sbody = (SubGroupBody) body;
        sbody.setGroup( g );
      } else {
        dataBody = (GroupDataBody) currentGroup.getBody();
        currentGroup.setBody( new SubGroupBody( g ) );
      }

      currentGroup = g;
    }

    if ( dataBody != null ) {
      currentGroup.setBody( dataBody );
    }

    return rootGroup;
  }

  /**
   * Compares two objects (required to be instances of the Group class). The group's field lists are compared, order of
   * the fields does not matter.
   * <p/>
   * This method only exists for legacy reasons.
   *
   * @param g1
   *          the first group.
   * @param g2
   *          the second group.
   * @return an integer indicating the relative ordering of the two groups.
   */
  private int compareGroups( final RelationalGroup g1, final RelationalGroup g2 ) {
    final List fieldsGroup1 = g1.getFields();
    final List fieldsGroup2 = g2.getFields();
    /** Remove all element, which are in both lists, they are equal */
    if ( fieldsGroup1.size() == fieldsGroup2.size() ) {
      // both lists contain the same elements.
      if ( fieldsGroup1.containsAll( fieldsGroup2 ) ) {
        return 0;
      } else {
        // groups with the same number of -, but different fields, are not compareable.
        throw new IllegalArgumentException(
            "These groups are not comparable, as they don't have any subgroup relation. "
                + " Groups of the same GroupList must have a subgroup relation. The designated "
                + " child group must contain all fields of the direct parent plus at least one " + " new field." );
      }
    }

    if ( fieldsGroup1.containsAll( fieldsGroup2 ) ) {
      // c2 contains all elements of c1, so c1 is subgroup of c2
      return 1;
    }
    if ( fieldsGroup2.containsAll( fieldsGroup1 ) ) {
      // c1 contains all elements of c2, so c2 is subgroup of c1
      return -1;
    }
    // not compareable, invalid groups
    // return 0;
    throw new IllegalArgumentException( "These groups are not comparable, as they don't have any subgroup relation. "
        + " Groups of the same GroupList must have a subgroup relation. The designated "
        + " child group must contain all fields of the direct parent plus at least one " + " new field." );
  }

  public void installIntoReport( final AbstractReportDefinition report ) throws ParseException {
    final GroupDataBody originalGroupDataBody =
        (GroupDataBody) report.getChildElementByType( GroupDataBodyType.INSTANCE );
    if ( originalGroupDataBody == null ) {
      throw new ParseException( "The report is not a relational report, cannot install relational detail sections here" );
    }

    final ItemBand ib = originalGroupDataBody.getItemBand();
    final NoDataBand nd = originalGroupDataBody.getNoDataBand();
    final DetailsHeader detailsHeader = originalGroupDataBody.getDetailsHeader();
    final DetailsFooter detailsFooter = originalGroupDataBody.getDetailsFooter();

    final Group newRootGroup = constructRootGroup();
    if ( report.getRootGroup() == newRootGroup ) {
      return;
    }

    report.setRootGroup( newRootGroup );
    final GroupDataBody groupDataBody = (GroupDataBody) newRootGroup.getChildElementByType( GroupDataBodyType.INSTANCE );
    if ( groupDataBody == null ) {
      return;
    }

    groupDataBody.setDetailsFooter( detailsFooter );
    groupDataBody.setDetailsHeader( detailsHeader );
    groupDataBody.setItemBand( ib );
    groupDataBody.setNoDataBand( nd );
  }
}
