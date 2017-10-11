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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;

/**
 * A collection of utility methods relating to functions.
 *
 * @author Thomas Morgner.
 */
public final class FunctionUtilities {

  /**
   * Default Constructor.
   */
  private FunctionUtilities() {
  }

  /**
   * Try to find the first element with the given name in the last active root-band.
   *
   * @param band
   *          the band that is suspected to contain the element.
   * @param element
   *          the element name.
   * @return the found element or null, if no element could be found.
   */
  public static Element findElement( final Band band, final String element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element name must not be null" );
    }

    if ( band == null ) {
      throw new NullPointerException( "Band must not be null" );
    }

    if ( band.getName().equals( element ) ) {
      return band;
    }

    final Element[] elements = band.getElementArray();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element e = elements[i];
      if ( element.equals( e.getName() ) ) {
        return e;
      }
      if ( e instanceof Band ) {
        final Element retval = findElement( (Band) e, element );
        if ( retval != null ) {
          return retval;
        }
      }
    }
    return null;
  }

  public static ReportElement findElementById( final ReportDefinition reportDefinition, final String id ) {
    if ( reportDefinition == null ) {
      throw new NullPointerException( "Element name must not be null" );
    }
    if ( id == null ) {
      return null;
    }
    return findElementById( (Section) reportDefinition, id );
  }

  public static ReportElement findElementByInstanceId( final ReportDefinition reportDefinition, final InstanceID id ) {
    if ( reportDefinition == null ) {
      throw new NullPointerException( "Element name must not be null" );
    }
    if ( id == null ) {
      return null;
    }
    return findElementByInstanceId( (Section) reportDefinition, id );
  }

  /**
   * Try to find the defined element in the last active root-band.
   *
   * @param band
   *          the band that is suspected to contain the element.
   * @param id
   *          the element's unique id.
   * @return the found element or null, if no element could be found.
   */
  public static ReportElement findElementById( final Section band, final String id ) {
    return findElementByAttribute( band, AttributeNames.Xml.NAMESPACE, AttributeNames.Xml.ID, id );
  }

  /**
   * Try to find the defined element in the last active root-band.
   *
   * @param band
   *          the band that is suspected to contain the element.
   * @param id
   *          the element's unique id.
   * @return the found element or null, if no element could be found.
   */
  public static ReportElement findElementByInstanceId( final Section band, final InstanceID id ) {
    if ( band == null ) {
      throw new NullPointerException( "Element name must not be null" );
    }
    if ( id == null ) {
      return null;
    }

    if ( band.getObjectID() == id ) {
      return band;
    }

    for ( int i = 0; i < band.getElementCount(); i++ ) {
      final ReportElement e = band.getElement( i );
      if ( id == e.getObjectID() ) {
        return e;
      }
      if ( e instanceof Section ) {
        final ReportElement retval = findElementByInstanceId( (Section) e, id );
        if ( retval != null ) {
          return retval;
        }
      }
    }

    if ( band instanceof RootLevelBand ) {
      final RootLevelBand rootLevelBand = (RootLevelBand) band;
      final SubReport[] reports = rootLevelBand.getSubReports();
      for ( int i = 0; i < reports.length; i++ ) {
        final SubReport report = reports[i];
        if ( report.getObjectID() == id ) {
          return report;
        }
      }
    }
    return null;
  }

  public static ReportElement findElementByName( final Section band, final String name ) {
    return findElementByAttribute( band, AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, name );
  }

  /**
   * Try to find the defined element in the last active root-band.
   *
   * @param band
   *          the band that is suspected to contain the element.
   * @param attributeNamespace
   *          the namespace of the attribute, never null.
   * @param attributeName
   *          the attribute name, never null.
   * @param attributeValue
   *          the value, never null.
   * @return the found element or null, if no element could be found.
   */
  public static ReportElement findElementByAttribute( final Section band, final String attributeNamespace,
      final String attributeName, final String attributeValue ) {
    if ( band == null ) {
      throw new NullPointerException( "Element must not be null" );
    }
    if ( attributeNamespace == null ) {
      throw new NullPointerException( "Attribute name must not be null" );
    }
    if ( attributeName == null ) {
      throw new NullPointerException( "Attribute namespace must not be null" );
    }
    if ( attributeValue == null ) {
      throw new NullPointerException( "Attribute value must not be null" );
    }

    if ( attributeValue.equals( band.getAttribute( attributeNamespace, attributeName ) ) ) {
      return band;
    }

    for ( int i = 0; i < band.getElementCount(); i++ ) {
      final ReportElement e = band.getElement( i );
      if ( attributeValue.equals( e.getAttribute( attributeNamespace, attributeName ) ) ) {
        return e;
      }
      if ( e instanceof Section ) {
        final ReportElement retval =
            findElementByAttribute( (Section) e, attributeNamespace, attributeName, attributeValue );
        if ( retval != null ) {
          return retval;
        }
      }
    }
    return null;
  }

  /**
   * Try to find all element with the given name in the last active root-band.
   *
   * @param band
   *          the band that is suspected to contain the element.
   * @param element
   *          the element name.
   * @return the found element or null, if no element could be found.
   */
  public static Element[] findAllElements( final Band band, final String element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element name must not be null" );
    }
    if ( band == null ) {
      throw new NullPointerException( "Band must not be null" );
    }

    final ArrayList<Element> collector = new ArrayList<Element>();
    if ( band.getName().equals( element ) ) {
      collector.add( band );
    }
    performFindElement( band, element, collector );
    return collector.toArray( new Element[collector.size()] );
  }

  /**
   * Try to find all element with the given name in the last active root-band.
   *
   * @param band
   *          the band that is suspected to contain the element.
   * @param element
   *          the element name.
   * @return the found element or null, if no element could be found.
   */
  public static Element[] findAllElements( final CrosstabCellBody band, final String element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element name must not be null" );
    }
    if ( band == null ) {
      throw new NullPointerException( "Band must not be null" );
    }

    final ArrayList<Element> collector = new ArrayList<Element>();
    if ( band.getName().equals( element ) ) {
      collector.add( band );
    }
    for ( int i = 1; i < band.getElementCount(); i += 1 ) {
      final CrosstabCell b = (CrosstabCell) band.getElement( i );
      if ( b.getName().equals( element ) ) {
        collector.add( band );
      }
      performFindElement( b, element, collector );
    }
    return collector.toArray( new Element[collector.size()] );
  }

  /**
   * Internal function that collects all elements of a given band with a given name.
   *
   * @param band
   *          the band from which elements should be collected.
   * @param element
   *          the name of the element to collect.
   * @param collector
   *          the list of results.
   */
  private static void performFindElement( final Band band, final String element, final ArrayList<Element> collector ) {
    final int count = band.getElementCount();
    final Element[] buffer = band.getElementArray();
    for ( int i = 0; i < count; i++ ) {
      final Element e = buffer[i];
      if ( e.getName().equals( element ) ) {
        collector.add( e );
      }
      if ( e instanceof Band ) {
        performFindElement( (Band) e, element, collector );
      }
    }
  }

  /**
   * Returns true if the events current groupname is equal to the group name.
   *
   * @param groupName
   *          the group name.
   * @param event
   *          the report event.
   * @return A boolean.
   */
  public static boolean isDefinedGroup( final String groupName, final ReportEvent event ) {
    if ( groupName == null ) {
      return false;
    }

    final int groupIndex = event.getState().getCurrentGroupIndex();
    final Group group = event.getReport().getGroup( groupIndex );
    if ( groupName.equals( group.getName() ) ) {
      return true;
    }
    if ( groupName.equals( group.getGeneratedName() ) ) {
      return true;
    }
    return false;
  }

  /**
   * Returns true, if the current run level is defined for the given function and this is a prepare run. The prepare run
   * is used to compute the function values.
   *
   * @param f
   *          the function.
   * @param event
   *          the event.
   * @return A boolean.
   */
  public static boolean isDefinedPrepareRunLevel( final Function f, final ReportEvent event ) {
    if ( f == null ) {
      throw new NullPointerException( "Function is null" );
    }

    if ( event == null ) {
      throw new NullPointerException( "ReportEvent is null" );
    }

    final ReportState state = event.getState();
    if ( state.isPrepareRun() == false ) {
      return false;
    }
    return ( state.getLevel() == f.getDependencyLevel() );
  }

  /**
   * Returns true or false.
   *
   * @param event
   *          the report event.
   * @return A boolean.
   */
  public static boolean isLayoutLevel( final ReportEvent event ) {
    if ( event == null ) {
      throw new NullPointerException( "ReportEvent is null" );
    }
    return ( event.getState().getLevel() == LayoutProcess.LEVEL_PAGINATE );
  }

  /**
   * Returns the current group instance, based on the given report event.
   *
   * @param event
   *          the event which is base for the action.
   * @return the current group of the event, never null.
   */
  public static Group getCurrentGroup( final ReportEvent event ) {
    if ( event == null ) {
      throw new NullPointerException( "ReportEvent is null" );
    }

    final int index = event.getState().getCurrentGroupIndex();
    if ( index == -1 ) {
      throw new IllegalStateException();
    }

    return event.getReport().getGroup( index );
  }

  /**
   * Returns the current group instance, based on the given report event.
   *
   * @param event
   *          the event which is base for the action.
   * @return the current group of the event, or null if the event is a deep traversing event.
   */
  public static Group getCurrentDeepTraverseGroup( final ReportEvent event ) {
    if ( event == null ) {
      throw new NullPointerException( "ReportEvent is null" );
    }

    if ( event.isDeepTraversing() ) {
      final int index = event.getOriginatingState().getCurrentGroupIndex();
      return event.getOriginatingState().getReport().getGroup( index );
    } else {
      return getCurrentGroup( event );
    }
  }

  public static boolean isCrosstabFilterValid( ReportDefinition def, String filterName ) {
    if ( filterName == null ) {
      return true;
    }

    for ( int i = 0; i < def.getGroupCount(); i++ ) {
      final Group group = def.getGroup( i );
      if ( group instanceof CrosstabColumnGroup ) {
        final CrosstabColumnGroup columnGroup = (CrosstabColumnGroup) group;
        if ( ObjectUtilities.equal( columnGroup.getName(), filterName ) ) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isCrosstabDefined( final ReportEvent event ) {
    return event.getReport().getCrosstabCellBody() != null;
  }

  public static String computeElementLocation( ReportElement e ) {
    final StringBuilder b = new StringBuilder();
    computeElementLocationInternal( e, b );
    return b.toString();
  }

  private static void computeElementLocationInternal( final ReportElement e, final StringBuilder b ) {
    final Section parentSection = e.getParentSection();
    if ( parentSection != null ) {
      computeElementLocationInternal( parentSection, b );
      b.append( "->" );
    }

    final String typeName = e.getElementType().getMetaData().getName();
    b.append( typeName );

    if ( parentSection != null ) {
      final int elementCount = parentSection.getElementCount();
      for ( int i = 0; i < elementCount; i += 1 ) {
        if ( parentSection.getElement( i ) == e ) {
          b.append( "[" );
          b.append( i );
          b.append( "]" );
          break;
        }
      }
    }
  }
}
