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

package org.pentaho.reporting.designer.core.model;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelEvent;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelListener;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ModelUtility {
  private static final String CACHED_LAYOUT_DATA = "CachedLayoutData";
  private static final String NUMBER_FORMAT_CONFIG_PREFIX = "NumberFormat.String.";
  private static final String DATE_FORMAT_CONFIG_PREFIX = "DateFormat.String.";

  private ModelUtility() {
  }

  private static class LinealUpdateHandler implements LinealModelListener {
    private Element element;
    private LinealModel model;
    private String attribute;

    private LinealUpdateHandler( final Element element,
                                 final LinealModel model,
                                 final String attribute ) {
      this.element = element;
      this.model = model;
      this.attribute = attribute;
    }

    public void modelChanged( final LinealModelEvent event ) {
      element.setAttribute( ReportDesignerParserModule.NAMESPACE, attribute, model.externalize(), false );
    }
  }

  public static boolean isHideInLayoutGui( final RenderNode node ) {
    final Object attribute = node.getAttributes().getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE );
    return attribute != null && Boolean.TRUE.equals( attribute );
  }

  public static boolean isHideInLayoutGui( final ReportElement element ) {
    final Object attribute = element.getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE );
    return attribute != null && Boolean.TRUE.equals( attribute );
  }

  public static LinealModel getVerticalLinealModel( final Element rootBand ) {
    final Object maybeLinealModel = rootBand.getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerBoot.DESIGNER_LINEAL_MODEL_OBJECT );
    if ( maybeLinealModel instanceof LinealModel ) {
      return (LinealModel) maybeLinealModel;
    }

    final LinealModel verticalLinealModel = new LinealModel();
    final Object attribute = rootBand.getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerParserModule.VERTICAL_GUIDE_LINES_ATTRIBUTE );
    if ( attribute instanceof String ) {
      verticalLinealModel.parse( attribute.toString() );
    }
    verticalLinealModel.addLinealModelListener( new LinealUpdateHandler
      ( rootBand, verticalLinealModel, ReportDesignerParserModule.VERTICAL_GUIDE_LINES_ATTRIBUTE ) );
    rootBand.setAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerBoot.DESIGNER_LINEAL_MODEL_OBJECT, verticalLinealModel, false );
    return verticalLinealModel;
  }

  public static LinealModel getHorizontalLinealModel( final AbstractReportDefinition rootBand ) {
    final Object maybeLinealModel = rootBand.getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerBoot.DESIGNER_LINEAL_MODEL_OBJECT );
    if ( maybeLinealModel instanceof LinealModel ) {
      return (LinealModel) maybeLinealModel;
    }

    final LinealModel linealModel = new LinealModel();
    final Object attribute = rootBand.getAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerParserModule.HORIZONTAL_GUIDE_LINES_ATTRIBUTE );
    if ( attribute instanceof String ) {
      linealModel.parse( attribute.toString() );
    }
    linealModel.addLinealModelListener( new LinealUpdateHandler
      ( rootBand, linealModel, ReportDesignerParserModule.HORIZONTAL_GUIDE_LINES_ATTRIBUTE ) );
    rootBand.setAttribute( ReportDesignerParserModule.NAMESPACE,
      ReportDesignerBoot.DESIGNER_LINEAL_MODEL_OBJECT, linealModel, false );
    return linealModel;
  }

  public static CachedLayoutData getCachedLayoutData( final Element element ) {
    final Object attribute = element.getAttribute( ReportDesignerParserModule.NAMESPACE, CACHED_LAYOUT_DATA );
    if ( attribute instanceof CachedLayoutData ) {
      return (CachedLayoutData) attribute;
    }

    final CachedLayoutData retval = new CachedLayoutData();
    element.setAttribute( ReportDesignerParserModule.NAMESPACE, CACHED_LAYOUT_DATA, retval, false );
    return retval;
  }

  public static boolean isDescendant( final Section definition, final ReportElement element ) {
    ReportElement band = element;
    while ( band != null ) {
      if ( band == definition ) {
        return true;
      }
      band = band.getParentSection();
    }

    return false;
  }

  public static List<Element> filterParents( final List<Element> elements ) {
    if ( elements.size() == 1 ) {
      return elements;
    }

    final ArrayList<Element> retval = new ArrayList<Element>( elements.size() );
    for ( Element element : elements ) {
      if ( element instanceof Section ) {
        final Section s = (Section) element;
        if ( isParentSection( s, elements ) ) {
          continue;
        }
      }
      retval.add( element );
    }
    return retval;
  }

  private static boolean isParentSection( final Section s, final List<Element> elements ) {
    for ( Element potentialChild : elements ) {
      if ( potentialChild != s ) {
        if ( ModelUtility.isDescendant( s, potentialChild ) ) {
          return true;
        }
      }
    }

    return false;
  }


  public static SubReport[] findSubReports( final Section section ) {
    final ArrayList<SubReport> result = new ArrayList<SubReport>();
    findSubReportsInteral( section, result );
    return result.toArray( new SubReport[ result.size() ] );
  }

  private static void findSubReportsInteral( final Section section, final ArrayList<SubReport> result ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      if ( element instanceof Section ) {
        findSubReportsInteral( (Section) element, result );
      }
    }

    if ( section instanceof SubReport ) {
      result.add( (SubReport) section );
    }
  }

  public static ReportElement findElementById( final AbstractReportDefinition definition, final InstanceID id ) {
    return FunctionUtilities.findElementByInstanceId( (ReportDefinition) definition, id );
  }

  public static int findIndexOf( final Section parent, final Element visualElement ) {
    final int count = parent.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = parent.getElement( i );
      if ( element == visualElement ) {
        return i;
      }
    }
    return -1;
  }

  public static int findSubreportIndexOf( final RootLevelBand parent, final SubReport visualElement ) {
    final int count = parent.getSubReportCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = parent.getSubReport( i );
      if ( element == visualElement ) {
        return i;
      }
    }
    return -1;
  }

  public static String[] getGroups( final AbstractReportDefinition reportDefinition ) {
    final LinkedHashSet<String> groups = new LinkedHashSet<String>();
    final int count = reportDefinition.getGroupCount();
    for ( int i = 0; i < count; i++ ) {
      final Group g = reportDefinition.getGroup( i );
      final String groupName = g.getName();
      if ( groupName != null ) {
        groups.add( groupName );
      }
    }

    for ( int i = 0; i < count; i++ ) {
      final Group g = reportDefinition.getGroup( i );
      final String groupName = g.getGeneratedName();
      if ( groupName != null ) {
        groups.add( groupName );
      }
    }
    return groups.toArray( new String[ groups.size() ] );
  }

  public static String[] getNumberFormats() {
    return getMessageSeries( NUMBER_FORMAT_CONFIG_PREFIX );
  }

  public static String[] getDateFormats() {
    return getMessageSeries( DATE_FORMAT_CONFIG_PREFIX );
  }

  private static String[] getMessageSeries( final String seriesPrefix ) {
    final ArrayList<String> stringSeries = new ArrayList<String>();
    int index = 0;

    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    String message = configuration.getConfigProperty( seriesPrefix + index );
    while ( message != null ) {
      stringSeries.add( message );
      index++;
      message = configuration.getConfigProperty( seriesPrefix + index );
    }

    return stringSeries.toArray( new String[ stringSeries.size() ] );
  }

  public static DataFactory findDataFactoryForQuery( AbstractReportDefinition definition,
                                                     final String query ) {
    while ( definition != null ) {
      final DataFactory dataFactory = definition.getDataFactory();
      if ( dataFactory instanceof CompoundDataFactory ) {
        final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
        final DataFactory factoryForQuery = cdf.getDataFactoryForQuery( query );
        if ( factoryForQuery != null ) {
          return factoryForQuery;
        }
      }

      final Section parentSection = definition.getParentSection();
      if ( parentSection == null ) {
        definition = null;
      } else {
        definition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }
    return null;
  }

}
