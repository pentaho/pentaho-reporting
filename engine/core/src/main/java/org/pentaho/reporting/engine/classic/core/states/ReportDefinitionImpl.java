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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.MasterReportType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.Map;
import java.util.Set;

/**
 * A report definition. This the working copy of the JFreeReport object. This object is not serializable, as it is used
 * internally. This implementation is not intended to be known outside. Whatever you planned to do with it - dont do it!
 * <p/>
 * Its only pupose is to be used and manipulated in the report states, there is no reason to do it outside.
 *
 * @author Thomas Morgner.
 */
public class ReportDefinitionImpl extends Section implements ReportDefinition {
  /**
   * The report header band (if not null, printed once at the start of the report).
   */
  private ReportHeader reportHeader;

  /**
   * The report footer band (if not null, printed once at the end of the report).
   */
  private ReportFooter reportFooter;

  /**
   * The page header band (if not null, printed at the start of every page).
   */
  private PageHeader pageHeader;

  /**
   * The page footer band (if not null, printed at the end of every page).
   */
  private PageFooter pageFooter;

  /**
   * The watermark acts a global page background.
   */
  private Watermark watermark;

  /**
   * The page definition defines the output area.
   */
  private PageDefinition pageDefinition;

  private String query;
  private Group rootGroup;
  private int queryLimit;
  private int queryTimeout;

  /**
   * Creates a report definition from a report object.
   *
   * @param report
   *          the report.
   * @param pageDefinition
   *          the current page definition.
   * @throws ReportProcessingException
   *           if there is a problem cloning.
   */
  public ReportDefinitionImpl( final MasterReport report, final PageDefinition pageDefinition )
    throws ReportProcessingException {
    super( report.getObjectID() );
    copyReport( report, pageDefinition );
  }

  public ReportDefinitionImpl( final SubReport report, final PageDefinition pageDefinition, final Section parentSection )
    throws ReportProcessingException {
    super( report.getObjectID() );
    copyReport( report, pageDefinition );
    setParent( parentSection );
  }

  private void copyReport( final ReportDefinition report, final PageDefinition pageDefinition ) {
    if ( pageDefinition == null ) {
      throw new NullPointerException();
    }

    this.rootGroup = (Group) report.getRootGroup().derive( true );
    this.reportFooter = (ReportFooter) report.getReportFooter().derive( true );
    this.reportHeader = (ReportHeader) report.getReportHeader().derive( true );
    this.pageFooter = (PageFooter) report.getPageFooter().derive( true );
    this.pageHeader = (PageHeader) report.getPageHeader().derive( true );
    this.watermark = (Watermark) report.getWatermark().derive( true );
    this.pageDefinition = pageDefinition;
    this.query = report.getQuery();

    copyAttributes( report.getAttributes() );

    final String[] attrExprNamespaces = report.getAttributeExpressionNamespaces();
    for ( int i = 0; i < attrExprNamespaces.length; i++ ) {
      final String namespace = attrExprNamespaces[i];
      final String[] attributeNames = report.getAttributeExpressionNames( namespace );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String name = attributeNames[j];
        setAttributeExpression( namespace, name, report.getAttributeExpression( namespace, name ) );
      }
    }

    getStyle().copyFrom( report.getStyle() );

    final Set<Map.Entry<StyleKey, Expression>> styleExpressionEntries = report.getStyleExpressions().entrySet();
    for ( final Map.Entry<StyleKey, Expression> entry : styleExpressionEntries ) {
      setStyleExpression( entry.getKey(), entry.getValue() );
    }

    registerAsChild( rootGroup );
    registerAsChild( reportHeader );
    registerAsChild( reportFooter );
    registerAsChild( pageHeader );
    registerAsChild( pageFooter );
    registerAsChild( watermark );

    this.queryLimit = report.getQueryLimit();
    this.queryTimeout = report.getQueryTimeout();

    setName( report.getName() );
    setChangeTracker( report.getChangeTracker() );
  }

  public int getQueryLimit() {
    return queryLimit;
  }

  public int getQueryTimeout() {
    return queryTimeout;
  }

  public String getQuery() {
    return query;
  }

  /**
   * Returns the report header.
   *
   * @return The report header.
   */
  public ReportHeader getReportHeader() {
    return reportHeader;
  }

  /**
   * Returns the report footer.
   *
   * @return The report footer.
   */
  public ReportFooter getReportFooter() {
    return reportFooter;
  }

  /**
   * Returns the page header.
   *
   * @return The page header.
   */
  public PageHeader getPageHeader() {
    return pageHeader;
  }

  /**
   * Returns the page footer.
   *
   * @return The page footer.
   */
  public PageFooter getPageFooter() {
    return pageFooter;
  }

  /**
   * Returns the item band.
   *
   * @return The item band.
   */
  public ItemBand getItemBand() {
    final Group group = getInnerMostGroup();
    final GroupBody body = group.getBody();
    if ( body instanceof GroupDataBody ) {
      final GroupDataBody dataBody = (GroupDataBody) body;
      return dataBody.getItemBand();
    }
    return null;
  }

  public CrosstabCellBody getCrosstabCellBody() {
    final Group group = getInnerMostGroup();
    final GroupBody body = group.getBody();
    if ( body instanceof CrosstabCellBody ) {
      return (CrosstabCellBody) body;
    }
    return null;
  }

  /**
   * Returns the details header band.
   *
   * @return The details header band.
   */
  public DetailsHeader getDetailsHeader() {
    final Group group = getInnerMostGroup();
    final GroupBody body = group.getBody();
    if ( body instanceof GroupDataBody ) {
      final GroupDataBody dataBody = (GroupDataBody) body;
      return dataBody.getDetailsHeader();
    }
    return null;
  }

  /**
   * Returns the details header band.
   *
   * @return The details header band.
   */
  public DetailsFooter getDetailsFooter() {
    final Group group = getInnerMostGroup();
    final GroupBody body = group.getBody();
    if ( body instanceof GroupDataBody ) {
      final GroupDataBody dataBody = (GroupDataBody) body;
      return dataBody.getDetailsFooter();
    }
    return null;
  }

  public Group getRootGroup() {
    return rootGroup;
  }

  private Group getInnerMostGroup() {
    Group existingGroup = rootGroup;
    GroupBody gb = existingGroup.getBody();
    while ( gb != null ) {
      final int count = gb.getElementCount();
      GroupBody locatedBody = null;
      for ( int i = 0; i < count; i++ ) {
        final ReportElement element = gb.getElement( i );
        if ( element instanceof Group ) {
          existingGroup = (Group) element;
          locatedBody = existingGroup.getBody();
          break;
        }
      }
      if ( locatedBody == null ) {
        gb = null;
      } else {
        gb = locatedBody;
      }
    }
    return existingGroup;
  }

  /**
   * Returns the "no-data" band, which is displayed if there is no data available.
   *
   * @return The no-data band.
   */
  public NoDataBand getNoDataBand() {
    final Group group = getInnerMostGroup();
    final GroupBody body = group.getBody();
    if ( body instanceof GroupDataBody ) {
      final GroupDataBody dataBody = (GroupDataBody) body;
      return dataBody.getNoDataBand();
    }
    return null;
  }

  /**
   * Returns the number of groups in this report.
   * <P>
   * Every report has at least one group defined.
   *
   * @return the group count.
   */
  public int getGroupCount() {
    int result = 1; // we always have at least a default-group.

    Group existingGroup = rootGroup;
    GroupBody gb = existingGroup.getBody();
    while ( gb != null ) {
      final int count = gb.getElementCount();
      boolean found = false;
      for ( int i = 0; i < count; i++ ) {
        final ReportElement element = gb.getElement( i );
        if ( element instanceof Group ) {
          existingGroup = (Group) element;
          result += 1;
          gb = existingGroup.getBody();
          found = true;
          break;
        }
      }
      if ( found == false ) {
        gb = null;
      }
    }

    return result;
  }

  /**
   * Returns the group at the specified index or null, if there is no such group.
   *
   * @param groupIndex
   *          the group index.
   * @return the requested group.
   * @throws IllegalArgumentException
   *           if the count is negative.
   * @throws IndexOutOfBoundsException
   *           if the count is greater than the number of defined groups.
   */
  public Group getGroup( final int groupIndex ) {
    if ( groupIndex < 0 ) {
      throw new IllegalArgumentException( "GroupCount must not be negative" );
    }
    if ( groupIndex == 0 ) {
      return rootGroup;
    }

    int result = 0; // we always have at least a default-group.

    Group existingGroup = rootGroup;
    GroupBody gb = existingGroup.getBody();
    while ( gb != null ) {
      final int count = gb.getElementCount();
      boolean found = false;
      for ( int i = 0; i < count; i++ ) {
        final ReportElement element = gb.getElement( i );
        if ( element instanceof Group ) {
          existingGroup = (Group) element;
          result += 1;
          if ( result == groupIndex ) {
            return existingGroup;
          }
          gb = existingGroup.getBody();
          found = true;
          break;
        }
      }
      if ( found == false ) {
        gb = null;
      }
    }
    throw new IndexOutOfBoundsException( "No group defined at the given index " + groupIndex + " . Max-index=" + result );
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException
   *           if the object's class does not support the <code>Cloneable</code> interface. Subclasses that override the
   *           <code>clone</code> method can also throw this exception to indicate that an instance cannot be cloned.
   * @see java.lang.Cloneable
   */
  public ReportDefinitionImpl clone() {
    final ReportDefinitionImpl report = (ReportDefinitionImpl) super.clone();
    report.rootGroup = (Group) rootGroup.clone();
    report.pageFooter = (PageFooter) pageFooter.clone();
    report.pageHeader = (PageHeader) pageHeader.clone();
    report.reportFooter = (ReportFooter) reportFooter.clone();
    report.reportHeader = (ReportHeader) reportHeader.clone();
    report.watermark = (Watermark) watermark.clone();
    // pagedefinition is not! cloned ...
    report.pageDefinition = pageDefinition;
    report.setParent( getParentSection() );

    report.registerAsChild( report.rootGroup );
    report.registerAsChild( report.reportHeader );
    report.registerAsChild( report.reportFooter );
    report.registerAsChild( report.pageHeader );
    report.registerAsChild( report.pageFooter );
    report.registerAsChild( report.watermark );
    return report;
  }

  public ReportDefinitionImpl derive( final boolean preserveElementInstanceIds ) {
    final ReportDefinitionImpl report = (ReportDefinitionImpl) super.derive( preserveElementInstanceIds );
    report.rootGroup = (Group) rootGroup.derive( preserveElementInstanceIds );
    report.pageFooter = (PageFooter) pageFooter.derive( preserveElementInstanceIds );
    report.pageHeader = (PageHeader) pageHeader.derive( preserveElementInstanceIds );
    report.reportFooter = (ReportFooter) reportFooter.derive( preserveElementInstanceIds );
    report.reportHeader = (ReportHeader) reportHeader.derive( preserveElementInstanceIds );
    report.watermark = (Watermark) watermark.derive( preserveElementInstanceIds );
    // pagedefinition is not! cloned ...
    report.pageDefinition = pageDefinition;
    report.setParent( getParentSection() );

    report.registerAsChild( report.rootGroup );
    report.registerAsChild( report.reportHeader );
    report.registerAsChild( report.reportFooter );
    report.registerAsChild( report.pageHeader );
    report.registerAsChild( report.pageFooter );
    report.registerAsChild( report.watermark );
    return report;
  }

  public Watermark getWatermark() {
    return watermark;
  }

  public PageDefinition getPageDefinition() {
    return pageDefinition;
  }

  /**
   * Returns the currently assigned report definition.
   *
   * @return the report definition or null, if no report has been assigned.
   */
  public ReportDefinition getReportDefinition() {
    return this;
  }

  public ReportDefinition getMasterReport() {
    if ( getElementType() instanceof MasterReportType ) {
      return this;
    }

    return super.getMasterReport();
  }

  protected void removeElement( final Element element ) {
    throw new UnsupportedOperationException(
        "Method 'removeElement' is not supported in the read-only report-definition." );
  }

  public void setElementAt( final int position, final Element element ) {
    throw new UnsupportedOperationException(
        "Method 'removeElement' is not supported in the read-only report-definition." );
  }

  public int getElementCount() {
    return 6;
  }

  public Element getElement( final int index ) {
    switch ( index ) {
      case 0:
        return pageHeader;
      case 1:
        return reportHeader;
      case 2:
        return rootGroup;
      case 3:
        return reportFooter;
      case 4:
        return pageFooter;
      case 5:
        return watermark;
      default:
        throw new IndexOutOfBoundsException();

    }
  }
}
