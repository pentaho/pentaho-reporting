/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.sorting;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.metadata.ResourceReference;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SortingIT {
  private static class ValidateDataFactory extends TableDataFactory {
    private List<SortConstraint> expectedConstraints;

    public ValidateDataFactory( final String name, final TableModel tableModel,
        final List<SortConstraint> expectedConstraints ) {
      super( name, tableModel );
      this.expectedConstraints = expectedConstraints;
    }

    public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
      Assert.assertEquals( parameters.get( DataFactory.QUERY_SORT ), expectedConstraints );

      return super.queryData( query, parameters );
    }
  }

  private static class ValidateDataFactoryMetaData implements DataFactoryMetaData {
    public Image getIcon( final Locale locale, final int iconKind ) {
      return null;
    }

    public MaturityLevel getFeatureMaturityLevel() {
      return MaturityLevel.Limited;
    }

    public String[] getReferencedFields( final DataFactory element, final String queryName, final DataRow parameter ) {
      return new String[0];
    }

    public ResourceReference[] getReferencedResources( final DataFactory element,
        final ResourceManager resourceManager, final String queryName, final DataRow parameter ) {
      return new ResourceReference[0];
    }

    public boolean isEditable() {
      return false;
    }

    public boolean isEditorAvailable() {
      return false;
    }

    public boolean isFreeFormQuery() {
      return false;
    }

    public boolean isFormattingMetaDataSource() {
      return false;
    }

    public DataSourcePlugin createEditor() {
      return null;
    }

    public String getDisplayConnectionName( final DataFactory dataFactory ) {
      return null;
    }

    public Object getQueryHash( final DataFactory element, final String queryName, final DataRow parameter ) {
      return null;
    }

    public String getName() {
      return ValidateDataFactory.class.getName();
    }

    public String getDisplayName( final Locale locale ) {
      return null;
    }

    public String getMetaAttribute( final String attributeName, final Locale locale ) {
      return null;
    }

    public String getGrouping( final Locale locale ) {
      return null;
    }

    public int getGroupingOrdinal( final Locale locale ) {
      return 0;
    }

    public int getItemOrdinal( final Locale locale ) {
      return 0;
    }

    public String getDeprecationMessage( final Locale locale ) {
      return null;
    }

    public String getDescription( final Locale locale ) {
      return null;
    }

    public boolean isDeprecated() {
      return false;
    }

    public boolean isExpert() {
      return false;
    }

    public boolean isPreferred() {
      return false;
    }

    public boolean isHidden() {
      return false;
    }

    public boolean isExperimental() {
      return false;
    }

    public int getCompatibilityLevel() {
      return 0;
    }

    public String getKeyPrefix() {
      return null;
    }

    public String getBundleLocation() {
      return null;
    }
  }

  private ValidateDataFactoryMetaData metaData;

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    metaData = new ValidateDataFactoryMetaData();
    DataFactoryRegistry.getInstance().register( metaData );
  }

  @After
  public void tearDown() throws Exception {
    DataFactoryRegistry.getInstance().unregister( metaData );
  }

  @Test
  public void testRunSorting() {
    List<SortConstraint> sc = new ArrayList<SortConstraint>();
    sc.add( new SortConstraint( "A", true ) );
    sc.add( new SortConstraint( "B", true ) );

    MasterReport report = new MasterReport();
    report.setQuery( "default" );
    report.setDataFactory( new ValidateDataFactory( "default", new DefaultTableModel( 10, 10 ), sc ) );
    report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.AUTOSORT, true );
    RelationalGroup rootGroup = (RelationalGroup) report.getRootGroup();
    rootGroup.setFields( Arrays.asList( "A", "B" ) );

    int i = DebugReportRunner.execGraphics2D( report );
  }

  @Test
  public void testCrosstabSorting() {
    List<SortConstraint> sc = new ArrayList<SortConstraint>();
    sc.add( new SortConstraint( "A", true ) );
    sc.add( new SortConstraint( "B", true ) );

    MasterReport report = new MasterReport();
    report.setQuery( "default" );
    report.setDataFactory( new ValidateDataFactory( "default", new DefaultTableModel( 10, 10 ), sc ) );
    report.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.AUTOSORT, true );
    RelationalGroup rootGroup = (RelationalGroup) report.getRootGroup();
    rootGroup.setFields( Arrays.asList( "A", "B" ) );

    int i = DebugReportRunner.execGraphics2D( report );
  }

}
