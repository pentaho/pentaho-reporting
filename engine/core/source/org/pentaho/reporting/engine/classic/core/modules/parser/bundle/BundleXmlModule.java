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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
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
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SpatialStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.StyleDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.SubReportReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.TextStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.BandReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ContentFieldReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ContentReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.DateFieldReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.EllipseReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ExternalElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.HorizontalLineReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.LabelReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.LegacyElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.MessageReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.NumberFieldReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.RectangleReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ResourceFieldReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ResourceLabelReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.ResourceMessageReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.TextFieldReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.VerticalLineReadHandler;
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
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.BandElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ContentElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ContentFieldElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabCellBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabCellElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabColumnGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabColumnGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabOtherGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabOtherGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabRowGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabRowGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabSummaryHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.CrosstabTitleHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.DataGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.DateFieldElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.DetailsFooterElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.DetailsHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.EllipseElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ExternalElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.GroupFooterElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.GroupHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.HorizontalLineElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ItembandElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.LabelElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.LegacyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.MessageElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.NoDataBandElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.NumberFieldElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.PageFooterElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.PageHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.RectangleElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.RelationalGroupElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ReportFooterElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ReportHeaderElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ResourceFieldElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ResourceLabelElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.ResourceMessageElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.SubGroupBodyElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.SubreportElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.TextFieldElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.VerticalLineElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements.WatermarkElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BandStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BorderStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.CommonStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.ContentStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.PageBandStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.SpatialStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.TextStyleSetWriteHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class BundleXmlModule extends AbstractModule
{
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.";

  public BundleXmlModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error ocurred while initializing the module.
   */
  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    ElementStyleDefinitionXmlResourceFactory.register(ElementStyleDefinitionXmlFactoryModule.class);
    BundleSettingsXmlResourceFactory.register(BundleSettingsXmlFactoryModule.class);
    DataDefinitionXmlResourceFactory.register(DataDefinitionXmlFactoryModule.class);
    SubReportDataDefinitionXmlResourceFactory.register(SubReportDataDefinitionXmlFactoryModule.class);

    SubReportXmlResourceFactory.register(StyleDefinitionXmlFactoryModule.class);
    SubReportXmlResourceFactory.register(LayoutDefinitionXmlFactoryModule.class);
    SubReportXmlResourceFactory.register(BundleSubReportXmlFactoryModule.class);
    MasterReportXmlResourceFactory.register(StyleDefinitionXmlFactoryModule.class);
    MasterReportXmlResourceFactory.register(LayoutDefinitionXmlFactoryModule.class);
    MasterReportXmlResourceFactory.register(BundleReportXmlFactoryModule.class);

    BundleStyleRegistry.getInstance().register(BandStyleSetWriteHandler.class);
    BundleStyleRegistry.getInstance().register(BorderStyleSetWriteHandler.class);
    BundleStyleRegistry.getInstance().register(CommonStyleSetWriteHandler.class);
    BundleStyleRegistry.getInstance().register(ContentStyleSetWriteHandler.class);
    BundleStyleRegistry.getInstance().register(PageBandStyleSetWriteHandler.class);
    BundleStyleRegistry.getInstance().register(SpatialStyleSetWriteHandler.class);
    BundleStyleRegistry.getInstance().register(TextStyleSetWriteHandler.class);

    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "band-styles", BandStyleReadHandler.class);
    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "text-styles", TextStyleReadHandler.class);
    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "spatial-styles", SpatialStyleReadHandler.class);
    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "common-styles", CommonStyleReadHandler.class);
    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "content-styles", ContentStyleReadHandler.class);
    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "page-band-styles", PageBandStyleReadHandler.class);
    BundleStyleRegistry.getInstance().register(BundleNamespaces.STYLE, "border-styles", BorderStyleReadHandler.class);

    BundleElementRegistry.getInstance().register("band", BandElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("content-field", ContentFieldElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("content", ContentElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("group-data-body", DataGroupBodyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("date-field", DateFieldElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("ellipse", EllipseElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("external-element-field", ExternalElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("group-footer", GroupFooterElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("group-header", GroupHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("horizontal-line", HorizontalLineElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("itemband", ItembandElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("label", LabelElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("legacy-element", LegacyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("message", MessageElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("no-data-band", NoDataBandElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("number-field", NumberFieldElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("page-footer", PageFooterElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("page-header", PageHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("rectangle", RectangleElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("group", RelationalGroupElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("report-footer", ReportFooterElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("report-header", ReportHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("resource-field", ResourceFieldElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("resource-label", ResourceLabelElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("resource-message", ResourceMessageElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("sub-group-body", SubGroupBodyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("text-field", TextFieldElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("vertical-line", VerticalLineElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("watermark", WatermarkElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("relational-group", RelationalGroupElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("details-header", DetailsHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("details-footer", DetailsFooterElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab", CrosstabGroupElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-other-group", CrosstabOtherGroupElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-other-group-body", CrosstabOtherGroupBodyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-row-group", CrosstabRowGroupElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-row-group-body", CrosstabRowGroupBodyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-column-group", CrosstabColumnGroupElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-column-group-body", CrosstabColumnGroupBodyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-title-header", CrosstabTitleHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-summary-header", CrosstabSummaryHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-header", CrosstabHeaderElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-cell", CrosstabCellElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("crosstab-cell-body", CrosstabCellBodyElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("sub-report", SubreportElementWriteHandler.class);

    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "band", BandReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "content-field", ContentFieldReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "content", ContentReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "date-field", DateFieldReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "ellipse", EllipseReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "horizontal-line", HorizontalLineReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "label", LabelReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "external-element-field", ExternalElementReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "message", MessageReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "number-field", NumberFieldReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "rectangle", RectangleReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "resource-field", ResourceFieldReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "resource-label", ResourceLabelReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "resource-message", ResourceMessageReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "text-field", TextFieldReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "vertical-line", VerticalLineReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "legacy", LegacyElementReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "sub-report", SubReportReadHandler.class);
    
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(ContentFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(BundleMetaFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(DataSchemaWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(DataDefinitionFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(SettingsFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(StyleFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(LayoutFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerMasterReportHandler(ResourceWriter.class);

    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler(DataDefinitionFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler(ContentFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler(StyleFileWriter.class);
    BundleWriterHandlerRegistry.getInstance().registerSubReportHandler(LayoutFileWriter.class);
    
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.CONTENT, false);
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.DATADEFINITION, false);
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.DATASCHEMA, false);
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.LEGACY, false);
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.LAYOUT, false);
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.SETTINGS, true);
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData(BundleNamespaces.STYLE, false);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(AttributeNames.Core.VALUE, "value", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.DATADEFINITION, "attribute", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.DATADEFINITION, "property", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LEGACY, "basic-key", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LEGACY, "basic-object", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LEGACY, "field", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LEGACY, "property", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LEGACY, "property-ref", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LAYOUT, "attribute", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LAYOUT, "field", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.LAYOUT, "property", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.SETTINGS, "property", true);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.SETTINGS, "settings", false);
    BundleWriterHandlerRegistry.getInstance().setElementHasCData(BundleNamespaces.STYLE, "selector", true);
  }
}
