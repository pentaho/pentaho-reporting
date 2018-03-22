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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import org.pentaho.reporting.engine.classic.core.designtime.Change;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsHeaderType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ReportDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchemaDefinition;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The AbstractReportDefinition serves as base-implementation for both the SubReport and the global JFreeReport
 * instance. There's no point to subclass this class any further.
 * <p/>
 * ReportDefinitions define the query string to "default" by default, change this to reflect the accepted queries in
 * your data-source.
 *
 * @author Thomas Morgner
 * @noinspection UnusedDeclaration
 */
public abstract class AbstractReportDefinition extends Section implements ReportDefinition {
  /**
   * Storage for the expressions in the report.
   */
  private ExpressionCollection expressions;
  /**
   * An hierarchy of report groups (each group defines its own header and footer).
   */
  private Group rootGroup;
  /**
   * The report header band (printed once at the start of the report).
   */
  private ReportHeader reportHeader;
  /**
   * The report footer band (printed once at the end of the report).
   */
  private ReportFooter reportFooter;
  /**
   * The page header band (printed at the start of every page).
   */
  private PageHeader pageHeader;
  /**
   * The page footer band (printed at the end of every page).
   */
  private PageFooter pageFooter;
  /**
   * The watermark band.
   */
  private Watermark watermark;

  private transient EventListenerList eventListeners;
  private long nonVisualsChangeTracker;
  private long datasourceChangeTracker;

  private DataSchemaDefinition dataSchemaDefinition;

  protected AbstractReportDefinition( final InstanceID id ) {
    super( id );
    init();
  }

  /**
   * Creates a new instance. This initializes all properties to their defaults - especially for subreports you have to
   * set sensible values before you can use them later.
   */
  protected AbstractReportDefinition() {
    init();
  }

  private void init() {
    this.dataSchemaDefinition = new DefaultDataSchemaDefinition();
    this.rootGroup = new RelationalGroup();
    this.reportHeader = new ReportHeader();
    this.reportFooter = new ReportFooter();
    this.pageHeader = new PageHeader();
    this.pageFooter = new PageFooter();
    this.watermark = new Watermark();

    this.expressions = new ExpressionCollection();
    registerAsChild( rootGroup );
    registerAsChild( reportHeader );
    registerAsChild( reportFooter );
    registerAsChild( pageHeader );
    registerAsChild( pageFooter );
    registerAsChild( watermark );
  }

  /**
   * Returns the resource bundle factory for this report definition. The {@link ResourceBundleFactory} is used in
   * internationalized reports to create the resourcebundles holding the localized resources.
   *
   * @return the assigned resource bundle factory.
   */
  @Deprecated
  public ResourceBundleFactory getResourceBundleFactory() {
    return DesignTimeUtil.getResourceBundleFactory( this );
  }

  /**
   * Redefines the resource bundle factory for the report.
   *
   * @param resourceBundleFactory
   *          the new resource bundle factory, never null.
   * @throws NullPointerException
   *           if the given ResourceBundleFactory is null.
   */
  @Deprecated
  public void setResourceBundleFactory( final ResourceBundleFactory resourceBundleFactory ) {
  }

  public int getPreProcessorCount() {
    final Object maybeArray = getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS );
    if ( maybeArray instanceof ReportPreProcessor[] ) {
      final ReportPreProcessor[] preprocessors = (ReportPreProcessor[]) maybeArray;
      return preprocessors.length;
    }
    return 0;
  }

  public ReportPreProcessor[] getPreProcessors() {
    final Object maybeArray = getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS );
    if ( maybeArray instanceof ReportPreProcessor[] ) {
      final ReportPreProcessor[] preprocessors = (ReportPreProcessor[]) maybeArray;
      return preprocessors.clone();
    }
    return new ReportPreProcessor[0];
  }

  public ReportPreProcessor getPreProcessor( final int index ) {
    final Object maybeArray = getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS );
    if ( maybeArray instanceof ReportPreProcessor[] ) {
      final ReportPreProcessor[] preprocessors = (ReportPreProcessor[]) maybeArray;
      return preprocessors[index];
    }
    throw new IndexOutOfBoundsException();
  }

  public void addPreProcessor( final ReportPreProcessor preProcessor ) {
    if ( preProcessor == null ) {
      throw new NullPointerException();
    }

    final ReportPreProcessor[] preprocessors = getPreProcessors();
    final ArrayList<ReportPreProcessor> newProcessors =
        new ArrayList<ReportPreProcessor>( Math.max( 10, preprocessors.length ) );
    for ( int i = 0; i < preprocessors.length; i++ ) {
      final ReportPreProcessor preprocessor = preprocessors[i];
      newProcessors.add( preprocessor );
    }
    newProcessors.add( preProcessor );

    final ReportPreProcessor[] newArray = newProcessors.toArray( new ReportPreProcessor[newProcessors.size()] );
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS, newArray );
  }

  public void removePreProcessor( final ReportPreProcessor preProcessor ) {
    if ( preProcessor == null ) {
      throw new NullPointerException();
    }

    final ReportPreProcessor[] preprocessors = getPreProcessors();
    final ArrayList<ReportPreProcessor> newProcessors =
        new ArrayList<ReportPreProcessor>( Math.max( 10, preprocessors.length ) );
    boolean found = false;
    for ( int i = 0; i < preprocessors.length; i++ ) {
      final ReportPreProcessor preprocessor = preprocessors[i];
      if ( found || preprocessor != preProcessor ) {
        newProcessors.add( preprocessor );
        found = true;
      }
    }
    if ( found ) {
      final ReportPreProcessor[] newArray = newProcessors.toArray( new ReportPreProcessor[newProcessors.size()] );
      setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS, newArray );
    }
  }

  public Group getRootGroup() {
    return rootGroup;
  }

  public void setRootGroup( final Group rootGroup ) {
    if ( rootGroup == null ) {
      throw new NullPointerException();
    }
    if ( rootGroup instanceof CrosstabGroup == false && rootGroup instanceof RelationalGroup == false ) {
      throw new IllegalArgumentException( "Only Crosstabs or relational-groups are permitted at the root" );
    }
    validateLooping( rootGroup );
    if ( unregisterParent( rootGroup ) ) {
      return;
    }

    final Element oldElement = this.rootGroup;
    this.rootGroup.setParent( null );
    this.rootGroup = rootGroup;
    this.rootGroup.setParent( this );
    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( rootGroup );
  }

  /**
   * Sets the report header.
   *
   * @param header
   *          the report header (<code>null</code> not permitted).
   */
  public void setReportHeader( final ReportHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "AbstractReportDefinition.setReportHeader(...) : null not permitted." );
    }
    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }

    final Element oldElement = this.reportHeader;
    this.reportHeader.setParent( null );
    this.reportHeader = header;
    this.reportHeader.setParent( this );
    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( header );
  }

  /**
   * Returns the report header.
   *
   * @return the report header (never <code>null</code>).
   */
  public ReportHeader getReportHeader() {
    return reportHeader;
  }

  /**
   * Sets the report footer.
   *
   * @param footer
   *          the report footer (<code>null</code> not permitted).
   */
  public void setReportFooter( final ReportFooter footer ) {
    if ( footer == null ) {
      throw new NullPointerException( "AbstractReportDefinition.setReportFooter(...) : null not permitted." );
    }
    validateLooping( footer );
    if ( unregisterParent( footer ) ) {
      return;
    }
    final Element oldElement = this.reportFooter;
    this.reportFooter.setParent( null );
    this.reportFooter = footer;
    this.reportFooter.setParent( this );
    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( footer );
  }

  /**
   * Returns the page footer.
   *
   * @return the report footer (never <code>null</code>).
   */
  public ReportFooter getReportFooter() {
    return reportFooter;
  }

  /**
   * Sets the page header.
   *
   * @param header
   *          the page header (<code>null</code> not permitted).
   */
  public void setPageHeader( final PageHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "AbstractReportDefinition.setPageHeader(...) : null not permitted." );
    }

    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }
    final Element oldElement = this.pageHeader;
    this.pageHeader.setParent( null );
    this.pageHeader = header;
    this.pageHeader.setParent( this );
    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( header );
  }

  /**
   * Returns the page header.
   *
   * @return the page header (never <code>null</code>).
   */
  public PageHeader getPageHeader() {
    return pageHeader;
  }

  /**
   * Sets the page footer.
   *
   * @param footer
   *          the page footer (<code>null</code> not permitted).
   */
  public void setPageFooter( final PageFooter footer ) {
    if ( footer == null ) {
      throw new NullPointerException( "AbstractReportDefinition.setPageFooter(...) : null not permitted." );
    }
    validateLooping( footer );
    if ( unregisterParent( footer ) ) {
      return;
    }
    final Element oldElement = this.pageFooter;
    this.pageFooter.setParent( null );
    this.pageFooter = footer;
    this.pageFooter.setParent( this );
    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( footer );
  }

  /**
   * Returns the page footer.
   *
   * @return the page footer (never <code>null</code>).
   */
  public PageFooter getPageFooter() {
    return pageFooter;
  }

  /**
   * Sets the watermark band for the report.
   *
   * @param band
   *          the new watermark band (<code>null</code> not permitted).
   */
  public void setWatermark( final Watermark band ) {
    if ( band == null ) {
      throw new NullPointerException( "AbstractReportDefinition.setWatermark(...) : null not permitted." );
    }

    validateLooping( band );
    if ( unregisterParent( band ) ) {
      return;
    }

    final Element oldElement = this.watermark;
    this.watermark.setParent( null );
    this.watermark = band;
    this.watermark.setParent( this );
    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( band );
  }

  /**
   * Returns the report's watermark band.
   *
   * @return the watermark band (never <code>null</code>).
   */
  public Watermark getWatermark() {
    return this.watermark;
  }

  /**
   * Returns the report's no-data band.
   *
   * @return the no-data band (never <code>null</code>).
   */
  public NoDataBand getNoDataBand() {
    Group innerMostRelationalGroup = getInnerMostRelationalGroup();
    if ( innerMostRelationalGroup instanceof CrosstabGroup ) {
      CrosstabGroup cg = (CrosstabGroup) innerMostRelationalGroup;
      return cg.getNoDataBand();
    }
    GroupBody body = innerMostRelationalGroup.getBody();
    if ( body instanceof GroupDataBody ) {
      GroupDataBody gd = (GroupDataBody) body;
      return gd.getNoDataBand();
    }
    return null;
  }

  /**
   * Returns the report's item band.
   *
   * @return the item band (never <code>null</code>).
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

  /**
   * Returns the details header band.
   *
   * @return The details header band.
   */
  public DetailsHeader getDetailsHeader() {
    return (DetailsHeader) getInnerMostGroup().getChildElementByType( DetailsHeaderType.INSTANCE );
  }

  /**
   * Returns the details header band.
   *
   * @return The details header band.
   */
  public DetailsFooter getDetailsFooter() {
    return (DetailsFooter) getInnerMostGroup().getChildElementByType( DetailsFooterType.INSTANCE );
  }

  private Group getInnerMostGroup() {
    Group existingGroup = rootGroup;
    while ( existingGroup != null ) {
      Group next = existingGroup.getBody().getGroup();
      if ( next == null ) {
        return existingGroup;
      }
      existingGroup = next;
    }
    throw new IllegalStateException( "We shall never reach this point." );
  }

  private Group getInnerMostRelationalGroup() {
    Group existingGroup = rootGroup;
    GroupBody gb = existingGroup.getBody();
    while ( gb != null ) {
      final int count = gb.getElementCount();
      boolean found = false;
      for ( int i = 0; i < count; i++ ) {
        final ReportElement element = gb.getElement( i );
        if ( element instanceof RelationalGroup ) {
          existingGroup = (Group) element;
          gb = existingGroup.getBody();
          found = true;
          break;
        }
      }
      if ( found == false ) {
        gb = null;
      }
    }
    return existingGroup;
  }

  /**
   * Adds a group to the report. This replaces the group body on the group with a new data-group-body composed of the
   * existing itemband and no-databand.
   *
   * @param group
   *          the group.
   */
  public void addGroup( final RelationalGroup group ) {
    if ( group == null ) {
      throw new NullPointerException( "AbstractReporDefinition.addGroup(..) : Null not permitted" );
    }

    final Group existingGroup = getInnerMostRelationalGroup();
    final GroupBody gb = existingGroup.getBody();
    existingGroup.setBody( new SubGroupBody( group ) );
    group.setBody( gb );
  }

  /**
   * Adds a crosstab group. This replaces any existing crosstabs and all the details sections.
   *
   * @param group
   */
  public void addGroup( final CrosstabGroup group ) {
    if ( group == null ) {
      throw new NullPointerException( "AbstractReporDefinition.addGroup(..) : Null not permitted" );
    }

    final Group existingGroup = getInnerMostRelationalGroup();
    existingGroup.setBody( new SubGroupBody( group ) );
  }

  public void removeGroup( final CrosstabGroup group ) {
    if ( group == null ) {
      throw new NullPointerException( "AbstractReporDefinition.addGroup(..) : Null not permitted" );
    }

    if ( rootGroup == group ) {
      removeRootGroup();
      return;
    }

    final Group existingGroup = getInnerMostRelationalGroup();
    final GroupBody gb = existingGroup.getBody();
    if ( gb instanceof SubGroupBody ) {
      final SubGroupBody sgb = (SubGroupBody) gb;
      if ( sgb.getGroup() == group ) {
        existingGroup.setBody( new GroupDataBody() );
      }
    }
  }

  public void removeGroup( final RelationalGroup deleteGroup ) {
    // Checks if we have a group to remove if not then throw an exception
    if ( deleteGroup == null ) {
      throw new NullPointerException( "AbstractReporDefinition.addGroup(..) : Null not permitted" );
    }

    // Special case check to see if we're removing the root group.
    if ( rootGroup == deleteGroup ) { // If we're at root then
      removeRootGroup(); // Remove it an exit
      return;
    }

    // Walk through the groups and find the one that we need to remove
    Group currentGroup = rootGroup;
    Group parentGroup = null;
    GroupBody currentGroupBody = currentGroup.getBody();
    while ( currentGroupBody instanceof SubGroupBody && currentGroup != deleteGroup ) {
      parentGroup = currentGroup;
      final SubGroupBody sgb = (SubGroupBody) currentGroupBody;
      currentGroup = sgb.getGroup();
      currentGroupBody = currentGroup.getBody();
    }

    if ( currentGroup == deleteGroup ) { // if this is true then we found the group we need to remove
      parentGroup.setBody( currentGroupBody );
      final SubGroupBody subGroupBody = (SubGroupBody) currentGroup.getParentSection();
      subGroupBody.setParent( parentGroup );
    }
  }

  private void removeRootGroup() {
    final Group group = rootGroup;
    final GroupBody rootBody = rootGroup.getBody();
    if ( group instanceof CrosstabGroup ) {
      rootGroup = new CrosstabGroup();
      rootGroup.setBody( rootBody );
    } else {
      if ( rootBody instanceof SubGroupBody ) {
        final SubGroupBody newRootGroup = (SubGroupBody) rootBody;
        rootGroup.removeElement( rootBody );
        rootGroup = newRootGroup.getGroup();
        registerAsChild( rootGroup );
      } else {
        rootGroup = new RelationalGroup();
        rootGroup.setBody( rootBody );
      }
    }
    unregisterAsChild( group );
    registerAsChild( rootGroup );
    notifyNodeChildRemoved( group );
    notifyNodeChildAdded( rootGroup );
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

  public RelationalGroup getRelationalGroup( final int groupIndex ) {
    final Group g = getGroup( groupIndex );
    if ( g instanceof RelationalGroup ) {
      return (RelationalGroup) g;
    }
    return null;
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
    throw new IndexOutOfBoundsException( "No group defined at the given index. Max-index=" + result );
  }

  /**
   * Searches a group by its defined name. This method returns null, if the group was not found.
   *
   * @param name
   *          the name of the group.
   * @return the group or null if not found.
   */
  public RelationalGroup getGroupByName( final String name ) {
    if ( name == null ) {
      throw new NullPointerException( "AbstractReporDefinition.getGroupByName(..) : Null not permitted" );
    }

    if ( rootGroup instanceof RelationalGroup == false ) {
      return null;
    }

    if ( rootGroup.matches( name ) ) {
      return (RelationalGroup) rootGroup;
    }
    GroupBody gb = rootGroup.getBody();
    while ( gb instanceof SubGroupBody ) {
      final SubGroupBody sgb = (SubGroupBody) gb;
      final Group group = sgb.getGroup();
      if ( group instanceof RelationalGroup == false ) {
        return null;
      }
      if ( group.matches( name ) ) {
        return (RelationalGroup) group;
      }
      gb = group.getBody();
    }
    return null;
  }

  /**
   * Adds a function to the report's collection of expressions.
   *
   * @param function
   *          the function.
   */
  public void addExpression( final Expression function ) {
    if ( function == null ) {
      throw new NullPointerException( "AbstractReporDefinition.addExpression(..) : Null not permitted" );
    }

    expressions.add( function );
    notifyNodeChildAdded( function );
  }

  public int getQueryTimeout() {
    final Object queryTimeoutText =
        getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_TIMEOUT );
    if ( queryTimeoutText instanceof Number ) {
      final Number n = (Number) queryTimeoutText;
      return n.intValue();
    }
    return 0;
  }

  public void setQueryTimeout( final int queryTimeout ) {
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_TIMEOUT, IntegerCache
        .getInteger( queryTimeout ) );
  }

  public int getQueryLimit() {
    final Object queryLimitText = getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT );
    if ( queryLimitText instanceof Number ) {
      final Number n = (Number) queryLimitText;
      return n.intValue();
    }
    return -1;
  }

  public void setQueryLimit( final int queryLimit ) {
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT, IntegerCache
        .getInteger( queryLimit ) );
  }

  public void setQueryLimitInheritance( final boolean queryLimitInheritance ) {
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT_INHERITANCE, queryLimitInheritance );
  }

  public boolean isQueryLimitInherited() {
    final Object queryLimitInheritanceText =  getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT_INHERITANCE );
    if ( queryLimitInheritanceText instanceof Boolean ) {
      return (Boolean) queryLimitInheritanceText;
    } else {
      final Object masterReportQueryLimitInheritanceText = getMasterReport()
        .getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT_INHERITANCE );
      if ( masterReportQueryLimitInheritanceText instanceof Boolean ) {
        return (Boolean) masterReportQueryLimitInheritanceText;
      }
    }
    return false;
  }

  public int getUserQueryLimit() {
    final Object queryLimitText =
        getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT_USER );
    if ( queryLimitText instanceof Number ) {
      final Number n = (Number) queryLimitText;
      return n.intValue();
    }
    return -1;
  }

  public void setUserQueryLimit( final int queryLimit ) {
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY_LIMIT_USER, IntegerCache
        .getInteger( queryLimit ) );
  }

  /**
   * Returns a new query or query-name that is used when retrieving the data from the data-factory.
   *
   * @return the query-string.
   */
  public String getQuery() {
    return (String) getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY );
  }

  /**
   * Defines a new query or query-name that is used when retrieving the data from the data-factory.
   *
   * @param query
   *          the query-string.
   * @see DataFactory#queryData(String, DataRow)
   */
  public void setQuery( final String query ) {
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, query );
  }

  /**
   * Returns the expressions for the report. When adding or removing expressions on the list, make sure to call
   * "notifyStructureChanged" afterwards when in design-mode.
   *
   * @return the expressions.
   */
  public ExpressionCollection getExpressions() {
    return expressions;
  }

  /**
   * Sets the expressions for the report.
   *
   * @param expressions
   *          the expressions (<code>null</code> not permitted).
   */
  public void setExpressions( final ExpressionCollection expressions ) {
    if ( expressions == null ) {
      throw new NullPointerException( "AbstractReportDefinition.setExpressions(...) : null not permitted." );
    }
    this.expressions = expressions;
    notifyNodeStructureChanged();
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public AbstractReportDefinition clone() {
    try {
      final AbstractReportDefinition report = (AbstractReportDefinition) super.clone();
      report.eventListeners = null;
      report.rootGroup = rootGroup.clone();
      report.watermark = (Watermark) watermark.clone();
      report.pageFooter = (PageFooter) pageFooter.clone();
      report.pageHeader = (PageHeader) pageHeader.clone();
      report.reportFooter = (ReportFooter) reportFooter.clone();
      report.reportHeader = (ReportHeader) reportHeader.clone();
      report.expressions = expressions.clone();
      report.dataSchemaDefinition = (DataSchemaDefinition) dataSchemaDefinition.clone();
      report.rootGroup.setParent( report );
      report.reportHeader.setParent( report );
      report.reportFooter.setParent( report );
      report.pageHeader.setParent( report );
      report.pageFooter.setParent( report );
      report.watermark.setParent( report );

      final ReportPreProcessor[] reportPreProcessors = report.getPreProcessors();
      for ( int i = 0; i < reportPreProcessors.length; i++ ) {
        reportPreProcessors[i] = reportPreProcessors[i].clone();
      }
      report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS,
          reportPreProcessors );

      final StructureFunction[] structureFunctions = report.getStructureFunctions();
      for ( int i = 0; i < structureFunctions.length; i++ ) {
        structureFunctions[i] = (StructureFunction) structureFunctions[i].clone();
      }
      report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS,
          structureFunctions );
      return report;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public AbstractReportDefinition derive( final boolean preserveElementInstanceIds ) {
    final AbstractReportDefinition report = (AbstractReportDefinition) super.derive( preserveElementInstanceIds );
    report.eventListeners = null;
    report.rootGroup = rootGroup.derive( preserveElementInstanceIds );
    report.watermark = (Watermark) watermark.derive( preserveElementInstanceIds );
    report.pageFooter = (PageFooter) pageFooter.derive( preserveElementInstanceIds );
    report.pageHeader = (PageHeader) pageHeader.derive( preserveElementInstanceIds );
    report.reportFooter = (ReportFooter) reportFooter.derive( preserveElementInstanceIds );
    report.reportHeader = (ReportHeader) reportHeader.derive( preserveElementInstanceIds );
    report.expressions = expressions.clone();
    report.dataSchemaDefinition = (DataSchemaDefinition) dataSchemaDefinition.clone();
    report.rootGroup.setParent( report );
    report.reportHeader.setParent( report );
    report.reportFooter.setParent( report );
    report.pageHeader.setParent( report );
    report.pageFooter.setParent( report );
    report.watermark.setParent( report );

    final ReportPreProcessor[] reportPreProcessors = report.getPreProcessors();
    for ( int i = 0; i < reportPreProcessors.length; i++ ) {
      reportPreProcessors[i] = reportPreProcessors[i].clone();
    }
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.PREPROCESSORS, reportPreProcessors );

    final StructureFunction[] structureFunctions = report.getStructureFunctions();
    for ( int i = 0; i < structureFunctions.length; i++ ) {
      structureFunctions[i] = (StructureFunction) structureFunctions[i].getInstance();
    }
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS,
        structureFunctions );
    return report;
  }

  public void setElementAt( final int position, final Element element ) {
    switch ( position ) {
      case 0:
        setPageHeader( (PageHeader) element );
        break;
      case 1:
        setReportHeader( (ReportHeader) element );
        break;
      case 2:
        setRootGroup( (Group) element );
        break;
      case 3:
        setReportFooter( (ReportFooter) element );
        break;
      case 4:
        setPageFooter( (PageFooter) element );
        break;
      case 5:
        setWatermark( (Watermark) element );
        break;
      default:
        throw new IndexOutOfBoundsException();

    }
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException( "AbstractReporDefinition.removeElement(..) : Null not permitted" );
    }

    if ( pageHeader == element ) {
      this.pageHeader.setParent( null );
      this.pageHeader = new PageHeader();
      this.pageHeader.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.pageHeader );
    } else if ( watermark == element ) {
      this.watermark.setParent( null );
      this.watermark = new Watermark();
      this.watermark.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.watermark );
    } else if ( reportHeader == element ) {
      this.reportHeader.setParent( null );
      this.reportHeader = new ReportHeader();
      this.reportHeader.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.reportHeader );
    } else if ( rootGroup == element ) {
      removeRootGroup();
    } else if ( reportFooter == element ) {
      this.reportFooter.setParent( null );
      this.reportFooter = new ReportFooter();
      this.reportFooter.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.reportFooter );
    } else if ( pageFooter == element ) {
      this.pageFooter.setParent( null );
      this.pageFooter = new PageFooter();
      this.pageFooter.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.pageFooter );
    }
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

  /**
   * Defines the content base for the report. The content base will be used to resolve relative URLs during the report
   * generation and resource loading. If there is no content base defined, it will be impossible to resolve relative
   * paths.
   *
   * @param key
   *          the content base or null.
   */
  public void setContentBase( final ResourceKey key ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE, key );
  }

  /**
   * Returns the content base of this report. The content base is used to resolve relative URLs during the report
   * generation and resource loading. If there is no content base defined, it will be impossible to resolve relative
   * paths.
   *
   * @return the content base or null, if no content base is defined.
   */
  public ResourceKey getContentBase() {
    final Object attribute = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE );
    if ( attribute instanceof ResourceKey ) {
      return (ResourceKey) attribute;
    }
    return getDefinitionSource();
  }

  public void setDefinitionSource( final ResourceKey key ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, key );
  }

  public ResourceKey getDefinitionSource() {
    final Object attribute = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE );
    if ( attribute instanceof ResourceKey ) {
      return (ResourceKey) attribute;
    }
    return null;
  }

  /**
   * Returns the currently assigned report definition.
   *
   * @return the report definition or null, if no report has been assigned.
   */
  public ReportDefinition getReportDefinition() {
    return this;
  }

  /**
   * Returns the data factory that has been assigned to this report. The data factory will never be null.
   *
   * @return the data factory.
   */
  public abstract DataFactory getDataFactory();

  /**
   * Sets the data factory for the report.
   *
   * @param dataFactory
   *          the data factory for the report, never null.
   */
  public abstract void setDataFactory( final DataFactory dataFactory );

  public void addReportModelListener( final ReportModelListener listener ) {
    if ( eventListeners == null ) {
      eventListeners = new EventListenerList();
    }
    this.eventListeners.add( ReportModelListener.class, listener );
  }

  public void removeReportModelListener( final ReportModelListener listener ) {
    if ( eventListeners == null ) {
      return;
    }
    eventListeners.remove( ReportModelListener.class, listener );
  }

  public void fireModelLayoutChanged( final ReportElement node, final int type, final Object parameter ) {
    if ( node == this ) {
      if ( parameter instanceof Change == false ) {
        nonVisualsChangeTracker += 1;
      }
      if ( parameter instanceof DataFactory ) {
        datasourceChangeTracker += 1;
      }
    }

    updateInternalChangeFlag();

    if ( eventListeners != null ) {
      final ReportModelEvent event = new ReportModelEvent( this, node, type, parameter );
      final ReportModelListener[] listeners = eventListeners.getListeners( ReportModelListener.class );
      for ( int i = 0; i < listeners.length; i++ ) {
        final ReportModelListener listener = listeners[i];
        listener.nodeChanged( event );
      }
    }
  }

  public long getDatasourceChangeTracker() {
    return datasourceChangeTracker;
  }

  public long getNonVisualsChangeTracker() {
    return nonVisualsChangeTracker;
  }

  public void removeExpression( final Expression expression ) {
    expressions.removeExpression( expression );
    notifyNodeChildRemoved( expression );
  }

  public DataSchemaDefinition getDataSchemaDefinition() {
    return dataSchemaDefinition;
  }

  public void setDataSchemaDefinition( final DataSchemaDefinition dataSchemaDefinition ) {
    if ( dataSchemaDefinition == null ) {
      throw new NullPointerException();
    }
    this.dataSchemaDefinition = dataSchemaDefinition;
    notifyNodePropertiesChanged();
  }

  /**
   * This method has only meaning for master-reports. This handle will be removed in the next majore release.
   *
   * @return
   */
  @Deprecated
  public abstract ResourceManager getResourceManager();

  /**
   * Adds a structural function to the report. Structural functions perform content preparation and maintainance
   * operations before elements are layouted or printed.
   * <p/>
   * Structural function can live on their own processing level and are evaluated after the user expressions but before
   * the layout expressions have been evaluated.
   *
   * @param function
   *          the structure function.
   */
  public void addStructureFunction( final StructureFunction function ) {
    if ( function == null ) {
      throw new NullPointerException();
    }

    final StructureFunction[] structureFunctions = getStructureFunctions();
    final ArrayList<StructureFunction> newProcessors =
        new ArrayList<StructureFunction>( Math.max( 10, structureFunctions.length ) );
    for ( int i = 0; i < structureFunctions.length; i++ ) {
      final StructureFunction structureFunction = structureFunctions[i];
      newProcessors.add( structureFunction );
    }
    newProcessors.add( function );

    final StructureFunction[] newArray = newProcessors.toArray( new StructureFunction[newProcessors.size()] );
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS, newArray );
  }

  /**
   * Returns the number of structural functions added to the report.
   *
   * @return the function count.
   */
  public int getStructureFunctionCount() {
    final Object maybeArray =
        getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS );
    if ( maybeArray instanceof StructureFunction[] ) {
      final StructureFunction[] structureFunctions = (StructureFunction[]) maybeArray;
      return structureFunctions.length;
    }
    return 0;
  }

  /**
   * Returns the structure function at the given position.
   *
   * @param index
   *          the position of the function in the list.
   * @return the function, never null.
   * @throws IndexOutOfBoundsException
   *           if the index is invalid.
   */
  public StructureFunction getStructureFunction( final int index ) {
    final Object maybeArray =
        getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS );
    if ( maybeArray instanceof StructureFunction[] ) {
      final StructureFunction[] structureFunctions = (StructureFunction[]) maybeArray;
      return structureFunctions[index];
    }
    throw new IndexOutOfBoundsException();
  }

  /**
   * Removes the given function from the collection of structure functions. This removes only the first occurence of the
   * function, in case a function has been added twice.
   *
   * @param f
   *          the function to be removed.
   */
  public void removeStructureFunction( final StructureFunction f ) {
    if ( f == null ) {
      throw new NullPointerException();
    }

    final StructureFunction[] structureFunctions = getStructureFunctions();
    final ArrayList<StructureFunction> newProcessors =
        new ArrayList<StructureFunction>( Math.max( 10, structureFunctions.length ) );
    boolean found = false;
    for ( int i = 0; i < structureFunctions.length; i++ ) {
      final StructureFunction structureFunction = structureFunctions[i];
      if ( found || structureFunction != f ) {
        newProcessors.add( structureFunction );
        found = true;
      }
    }
    if ( found ) {
      final StructureFunction[] newArray = newProcessors.toArray( new StructureFunction[newProcessors.size()] );
      setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS, newArray );
    }
  }

  /**
   * Returns a copy of all structure functions contained in the report. Modifying the function instances will not alter
   * the functions contained in the report.
   *
   * @return the functions.
   */
  public StructureFunction[] getStructureFunctions() {
    final Object maybeArray =
        getAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.STRUCTURE_FUNCTIONS );
    if ( maybeArray instanceof StructureFunction[] ) {
      final StructureFunction[] structureFunctions = (StructureFunction[]) maybeArray;
      return structureFunctions.clone();
    }
    return new StructureFunction[0];
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return ReportDefaultStyleSheet.getSectionDefault();
  }

  public CrosstabCellBody getCrosstabCellBody() {
    final GroupBody body = getInnerMostGroup().getBody();
    if ( body instanceof CrosstabCellBody ) {
      return (CrosstabCellBody) body;
    }
    return null;
  }

  public void setAutoSort( Boolean sort ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.AUTOSORT, sort );
  }

  public Boolean getAutoSort() {
    Object attribute = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.AUTOSORT );
    if ( attribute instanceof Boolean ) {
      return (Boolean) attribute;
    }
    return null;
  }
}
