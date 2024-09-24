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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.CrosstabElementType;
import org.pentaho.reporting.engine.classic.core.filter.types.DateFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.EllipseType;
import org.pentaho.reporting.engine.classic.core.filter.types.ExternalElementType;
import org.pentaho.reporting.engine.classic.core.filter.types.HorizontalLineType;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.RectangleType;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceLabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceMessageType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.VerticalLineType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.BandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabSummaryHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabTitleHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubGroupBodyType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.WatermarkType;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.CrosstabXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.MasterReportXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.DataDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.DataDefinitionXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.SubReportDataDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.SubReportDataDefinitionXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.BandStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.BorderStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.CommonStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ContentStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementStyleDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementStyleDefinitionXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.LayoutDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.PageBandStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.RotationStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SpatialStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.StyleDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SubReportReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.TextStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.BandReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.CrosstabElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.LegacyElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.settings.BundleSettingsXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.settings.BundleSettingsXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleMetaFileWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandlerRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.ContentFileWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.DataDefinitionFileWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.DataSchemaWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.LayoutFileWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.ResourceWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.SettingsFileWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.StyleFileWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabColumnGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabOtherGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabRowGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.DataGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.GroupFooterElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.GroupHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ItembandElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.LegacyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.NoDataBandElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.RelationalGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ReportFooterElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ReportHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.SubGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.SubreportElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BandStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BorderStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.CommonStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.ContentStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.PageBandStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.RotationStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.SpatialStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.TextStyleSetWriteHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class BundleXmlModule extends AbstractModule {
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.";

  public BundleXmlModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    ElementStyleDefinitionXmlResourceFactory.register( ElementStyleDefinitionXmlFactoryModule.class );
    BundleSettingsXmlResourceFactory.register( BundleSettingsXmlFactoryModule.class );
    DataDefinitionXmlResourceFactory.register( DataDefinitionXmlFactoryModule.class );
    SubReportDataDefinitionXmlResourceFactory.register( SubReportDataDefinitionXmlFactoryModule.class );

    SubReportXmlResourceFactory.register( StyleDefinitionXmlFactoryModule.class );
    SubReportXmlResourceFactory.register( LayoutDefinitionXmlFactoryModule.class );
    SubReportXmlResourceFactory.register( BundleSubReportXmlFactoryModule.class );
    MasterReportXmlResourceFactory.register( StyleDefinitionXmlFactoryModule.class );
    MasterReportXmlResourceFactory.register( LayoutDefinitionXmlFactoryModule.class );
    MasterReportXmlResourceFactory.register( BundleReportXmlFactoryModule.class );

    BundleStyleRegistry.getInstance().register( BandStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( BorderStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( CommonStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( ContentStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( PageBandStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( SpatialStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( TextStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( RotationStyleSetWriteHandler.class );

    BundleStyleRegistry.getInstance().register( BundleNamespaces.STYLE, "band-styles", BandStyleReadHandler.class );
    BundleStyleRegistry.getInstance().register( BundleNamespaces.STYLE, "text-styles", TextStyleReadHandler.class );
    BundleStyleRegistry.getInstance().register( BundleNamespaces.STYLE, "rotation-styles", RotationStyleReadHandler.class );
    BundleStyleRegistry.getInstance()
        .register( BundleNamespaces.STYLE, "spatial-styles", SpatialStyleReadHandler.class );
    BundleStyleRegistry.getInstance().register( BundleNamespaces.STYLE, "common-styles", CommonStyleReadHandler.class );
    BundleStyleRegistry.getInstance()
        .register( BundleNamespaces.STYLE, "content-styles", ContentStyleReadHandler.class );
    BundleStyleRegistry.getInstance().register( BundleNamespaces.STYLE, "page-band-styles",
        PageBandStyleReadHandler.class );
    BundleStyleRegistry.getInstance().register( BundleNamespaces.STYLE, "border-styles", BorderStyleReadHandler.class );

    BundleElementRegistry.getInstance().registerGenericElement( ContentFieldType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( ContentType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( DateFieldType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( EllipseType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( ExternalElementType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( HorizontalLineType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( LabelType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( MessageType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( NumberFieldType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( RectangleType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( ResourceFieldType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( ResourceLabelType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( ResourceMessageType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( TextFieldType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( VerticalLineType.INSTANCE );

    BundleElementRegistry.getInstance().registerGenericWriter( BandType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( PageFooterType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( PageHeaderType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( WatermarkType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( DetailsHeaderType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( DetailsFooterType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabOtherGroupBodyType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabRowGroupBodyType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabColumnGroupBodyType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabTitleHeaderType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabSummaryHeaderType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabHeaderType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabCellType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabCellBodyType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericWriter( CrosstabGroupType.INSTANCE );

    BundleElementRegistry.getInstance().register( GroupDataBodyType.INSTANCE, DataGroupBodyElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( SubGroupBodyType.INSTANCE, SubGroupBodyElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( GroupFooterType.INSTANCE, GroupFooterElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( GroupHeaderType.INSTANCE, GroupHeaderElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( ItemBandType.INSTANCE, ItembandElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( LegacyType.INSTANCE, LegacyElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( NoDataBandType.INSTANCE, NoDataBandElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( RelationalGroupType.INSTANCE,
        RelationalGroupElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( ReportFooterType.INSTANCE, ReportFooterElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( ReportHeaderType.INSTANCE, ReportHeaderElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( RelationalGroupType.INSTANCE,
        RelationalGroupElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( CrosstabOtherGroupType.INSTANCE,
        CrosstabOtherGroupElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( CrosstabRowGroupType.INSTANCE,
        CrosstabRowGroupElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( CrosstabColumnGroupType.INSTANCE,
        CrosstabColumnGroupElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( SubReportType.INSTANCE, SubreportElementWriteHandler.class );
    BundleElementRegistry.getInstance().register( CrosstabElementType.INSTANCE, CrosstabElementWriteHandler.class );

    BundleElementRegistry.getInstance().registerReader( BandType.INSTANCE, BandReadHandler.class );
    BundleElementRegistry.getInstance().registerReader( LegacyType.INSTANCE, LegacyElementReadHandler.class );
    BundleElementRegistry.getInstance().registerReader( SubReportType.INSTANCE, SubReportReadHandler.class );
    BundleElementRegistry.getInstance().registerReader( CrosstabElementType.INSTANCE, CrosstabElementReadHandler.class );

    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( ContentFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( BundleMetaFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( DataSchemaWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( DataDefinitionFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( SettingsFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( StyleFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( LayoutFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler( ResourceWriter.class );

    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler( DataDefinitionFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler( ContentFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler( StyleFileWriter.class );
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler( LayoutFileWriter.class );

    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.CONTENT, false );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.DATADEFINITION, false );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.DATASCHEMA, false );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.LEGACY, false );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.LAYOUT, false );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.SETTINGS, true );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( BundleNamespaces.STYLE, false );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( AttributeNames.Core.VALUE, "value", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.DATADEFINITION, "attribute", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.DATADEFINITION, "property", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LEGACY, "basic-key", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LEGACY, "basic-object", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LEGACY, "field", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LEGACY, "property", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LEGACY, "property-ref", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LAYOUT, "attribute", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LAYOUT, "field", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.LAYOUT, "property", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.SETTINGS, "property", true );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.SETTINGS, "settings", false );
    BundleWriterHandlerRegistry.getInstance().setElementHasCData( BundleNamespaces.STYLE, "selector", true );

    CrosstabXmlResourceFactory.register( BundleCrosstabXmlFactoryModule.class );
    CrosstabXmlResourceFactory.register( LayoutDefinitionXmlFactoryModule.class );
    CrosstabXmlResourceFactory.register( StyleDefinitionXmlFactoryModule.class );

  }
}
