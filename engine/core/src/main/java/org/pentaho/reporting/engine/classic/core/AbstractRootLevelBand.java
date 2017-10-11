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

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.RootLevelBandDefaultStyleSheet;

import java.util.ArrayList;

/**
 * The root-level band is the container that is processed by a report-state. The root-level band processing is atomic -
 * so either the full band is processed or not processed at all.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractRootLevelBand extends Band implements RootLevelBand {
  /**
   * A empty array. (For performance reasons.)
   */
  private static final SubReport[] EMPTY_SUBREPORTS = new SubReport[0];
  /**
   * The list of follow-up root-level sub-reports.
   */
  private ArrayList<SubReport> subReports;

  /**
   * Constructs a new band (initially empty).
   */
  protected AbstractRootLevelBand() {
  }

  /**
   * Constructs a new band with the given pagebreak attributes. Pagebreak attributes have no effect on subbands.
   *
   * @param pagebreakAfter
   *          defines, whether a pagebreak should be done after that band was printed.
   * @param pagebreakBefore
   *          defines, whether a pagebreak should be done before that band gets printed.
   */
  protected AbstractRootLevelBand( final boolean pagebreakBefore, final boolean pagebreakAfter ) {
    super( pagebreakBefore, pagebreakAfter );
  }

  /**
   * Returns the number of subreports attached to this root level band.
   *
   * @return the number of subreports.
   */
  public int getSubReportCount() {
    if ( subReports == null ) {
      return 0;
    }
    return subReports.size();
  }

  /**
   * Clones this band and all elements contained in this band. After the cloning the band is no longer connected to a
   * report definition.
   *
   * @return the clone of this band.
   */
  public AbstractRootLevelBand clone() {
    final AbstractRootLevelBand rootLevelBand = (AbstractRootLevelBand) super.clone();
    if ( rootLevelBand.subReports != null ) {
      rootLevelBand.subReports = (ArrayList<SubReport>) rootLevelBand.subReports.clone();
      rootLevelBand.subReports.clear();
      for ( int i = 0; i < subReports.size(); i++ ) {
        final SubReport report = subReports.get( i );
        final SubReport clone = (SubReport) report.clone();
        clone.setParent( rootLevelBand );
        rootLevelBand.subReports.add( clone );
      }
    }
    return rootLevelBand;
  }

  /**
   * Creates a deep copy of this element and regenerates all instance-ids.
   *
   * @return the copy of the element.
   */
  public AbstractRootLevelBand derive( final boolean preserveElementInstanceIds ) {
    final AbstractRootLevelBand rootLevelBand = (AbstractRootLevelBand) super.derive( preserveElementInstanceIds );
    if ( rootLevelBand.subReports != null ) {
      rootLevelBand.subReports = (ArrayList<SubReport>) rootLevelBand.subReports.clone();
      rootLevelBand.subReports.clear();
      for ( int i = 0; i < subReports.size(); i++ ) {
        final SubReport report = subReports.get( i );
        final SubReport clone = (SubReport) report.derive( preserveElementInstanceIds );
        clone.setParent( rootLevelBand );
        rootLevelBand.subReports.add( clone );
      }
    }
    return rootLevelBand;
  }

  /**
   * Returns the subreport at the given index-position.
   *
   * @param index
   *          the index
   * @return the subreport stored at the given index.
   * @throws IndexOutOfBoundsException
   *           if there is no such subreport.
   */
  public SubReport getSubReport( final int index ) {
    if ( subReports == null ) {
      throw new IndexOutOfBoundsException();
    }
    return subReports.get( index );
  }

  /**
   * Attaches a new subreport at the end of the list.
   *
   * @param index
   * @param element
   *          the subreport, never null.
   */
  public void addSubReport( final int index, final SubReport element ) {
    if ( element == null ) {
      throw new NullPointerException( "Parameter 'report' must not be null" );
    }

    validateLooping( element );
    if ( unregisterParent( element ) ) {
      return;
    }

    // add the element, update the childs Parent and the childs stylesheet.
    if ( subReports == null ) {
      subReports = new ArrayList<SubReport>();
    }
    subReports.add( index, element );
    registerAsChild( element );
    notifyNodeChildAdded( element );
  }

  /**
   * Attaches a new subreport at the end of the list.
   *
   * @param element
   *          the subreport, never null.
   */
  public void addSubReport( final SubReport element ) {
    if ( element == null ) {
      throw new NullPointerException( "Parameter 'report' must not be null" );
    }

    validateLooping( element );
    if ( unregisterParent( element ) ) {
      return;
    }

    // add the element, update the childs Parent and the childs stylesheet.
    if ( subReports == null ) {
      subReports = new ArrayList<SubReport>();
    }
    subReports.add( element );
    registerAsChild( element );
    notifyNodeChildAdded( element );
  }

  /**
   * Removes the given subreport from the list of attached sub-reports.
   *
   * @param e
   *          the subreport to be removed.
   */
  public void removeSubreport( final SubReport e ) {
    if ( e == null ) {
      throw new NullPointerException( "Parameter 'report' must not be null" );
    }
    if ( subReports == null ) {
      return;
    }
    if ( e.getParentSection() != this ) {
      // this is none of my childs, ignore the request ...
      return;
    }
    if ( subReports.contains( e ) == false ) {
      return;
    }

    e.setParent( null );
    subReports.remove( e );
    notifyNodeChildRemoved( e );
  }

  /**
   * Returns all sub-reports as array.
   *
   * @return the sub-reports as array.
   */
  public SubReport[] getSubReports() {
    if ( subReports == null ) {
      return AbstractRootLevelBand.EMPTY_SUBREPORTS;
    }
    return subReports.toArray( new SubReport[subReports.size()] );
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return RootLevelBandDefaultStyleSheet.getRootLevelBandDefaultStyle();
  }
}
