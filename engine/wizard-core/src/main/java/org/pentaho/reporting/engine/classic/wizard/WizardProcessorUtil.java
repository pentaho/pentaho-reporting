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

package org.pentaho.reporting.engine.classic.wizard;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.function.ProcessingDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.states.StateUtilities;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class WizardProcessorUtil {
  private WizardProcessorUtil() {
  }

  public static boolean isCacheEnabled( ReportDefinition reportDefinition ) {
    while ( reportDefinition != null ) {
      final Object dataCacheEnabledRaw =
        reportDefinition.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE );
      if ( Boolean.FALSE.equals( dataCacheEnabledRaw ) ) {
        return false;
      }
      final Section parentSection = reportDefinition.getParentSection();
      if ( parentSection == null ) {
        break;
      }
      reportDefinition = parentSection.getReportDefinition();
    }
    return true;
  }

  public static SubReport materialize( final SubReport report,
                                       final WizardProcessor processor ) throws ReportProcessingException {
    final PerformanceMonitorContext performanceMonitorContext =
      ClassicEngineBoot.getInstance().getObjectFactory().get( PerformanceMonitorContext.class );
    try {
      final DefaultProcessingContext processingContext;
      final MasterReport masterReport = DesignTimeUtil.getMasterReport( report );
      if ( masterReport != null ) {
        processingContext = new DefaultProcessingContext( masterReport );
      } else {
        processingContext = new DefaultProcessingContext();
      }

      final DataSchemaDefinition definition = report.getDataSchemaDefinition();
      final DefaultFlowController flowController =
        new DefaultFlowController( processingContext, definition,
          StateUtilities.computeParameterValueSet( report ), performanceMonitorContext );
      final CachingDataFactory dataFactory =
        new CachingDataFactory( report.getDataFactory(), isCacheEnabled( report ) );
      dataFactory.initialize( new ProcessingDataFactoryContext( processingContext, dataFactory ) );

      try {
        final DefaultFlowController postQueryFlowController = flowController.performDesignTimeQuery
          ( dataFactory, report.getQuery(), report.getQueryLimit(),
            report.getQueryTimeout(), flowController.getMasterRow().getResourceBundleFactory() );

        final Object originalEnable =
          report.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE );
        report.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE, Boolean.TRUE );
        final SubReport subReport = processor.performPreProcessing( report, postQueryFlowController );
        subReport.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE, originalEnable );
        return subReport;
      } finally {
        dataFactory.close();
      }
    } finally {
      performanceMonitorContext.close();
    }
  }


  public static MasterReport materialize( final MasterReport report,
                                          final WizardProcessor processor ) throws ReportProcessingException {
    final PerformanceMonitorContext performanceMonitorContext =
      ClassicEngineBoot.getInstance().getObjectFactory().get( PerformanceMonitorContext.class );
    try {
      final DefaultProcessingContext processingContext = new DefaultProcessingContext( report );
      final DataSchemaDefinition definition = report.getDataSchemaDefinition();
      final DefaultFlowController flowController = new DefaultFlowController( processingContext,
        definition, StateUtilities.computeParameterValueSet( report ), performanceMonitorContext );
      final CachingDataFactory dataFactory =
        new CachingDataFactory( report.getDataFactory(), isCacheEnabled( report ) );
      dataFactory.initialize( new ProcessingDataFactoryContext( processingContext, dataFactory ) );

      try {
        final DefaultFlowController postQueryFlowController = flowController.performDesignTimeQuery
          ( dataFactory, report.getQuery(), report.getQueryLimit(),
            report.getQueryTimeout(), flowController.getMasterRow().getResourceBundleFactory() );

        final Object originalEnable =
          report.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE );
        report.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE, Boolean.TRUE );
        final MasterReport masterReport = processor.performPreProcessing( report, postQueryFlowController );
        masterReport.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE, originalEnable );

        masterReport.setName( null );
        DesignTimeUtil.resetDocumentMetaData( masterReport );
        return masterReport;
      } finally {
        dataFactory.close();
      }
    } finally {
      performanceMonitorContext.close();
    }
  }

  /**
   * @param masterReport
   * @deprecated Use DesignTimeUtil.resetTemplate(..) to reset the template properties.
   */
  public static void resetDocumentMetaData( final MasterReport masterReport ) {
    DesignTimeUtil.resetDocumentMetaData( masterReport );
  }

  public static void ensureWizardProcessorIsAdded( final AbstractReportDefinition element,
                                                   final WizardProcessor processor ) {
    final ReportPreProcessor[] processors = element.getPreProcessors();
    boolean hasWizardProcessor = false;
    for ( int i = 0; i < processors.length; i++ ) {
      final ReportPreProcessor preProcessor = processors[ i ];
      if ( preProcessor instanceof WizardProcessor ) {
        hasWizardProcessor = true;
      }
    }
    if ( hasWizardProcessor == false ) {
      if ( processor == null ) {
        element.addPreProcessor( new WizardProcessor() );
      } else {
        element.addPreProcessor( processor );
      }
    }
  }

  public static void applyWizardSpec( final AbstractReportDefinition definition,
                                      final WizardSpecification wizardSpecification ) {
    definition.setAttribute( AttributeNames.Wizard.NAMESPACE, "wizard-spec", wizardSpecification );
  }

  public static WizardSpecification loadWizardSpecification( final AbstractReportDefinition definition,
                                                             final ResourceManager resourceManager )
    throws ReportProcessingException {
    final Object maybeWizardSpec = definition.getAttribute( AttributeNames.Wizard.NAMESPACE, "wizard-spec" );
    if ( maybeWizardSpec instanceof WizardSpecification ) {
      return (WizardSpecification) maybeWizardSpec;
    }

    final Object attribute = definition.getAttribute( AttributeNames.Wizard.NAMESPACE, "source" );
    if ( attribute != null ) {
      try {
        final ResourceKey contentBase = definition.getContentBase();
        final ResourceKey resourceKey = resourceManager.deriveKey( contentBase, String.valueOf( attribute ) );
        final Resource resource = resourceManager.create( resourceKey, contentBase, WizardSpecification.class );
        return (WizardSpecification) resource.getResource();
      } catch ( ResourceKeyCreationException e ) {
        throw new ReportProcessingException( "Failed to load the wizard-specification", e );
      } catch ( ResourceException e ) {
        throw new ReportProcessingException( "Failed to load the wizard-specification", e );
      }
    }

    try {
      final ResourceKey contentBase = definition.getContentBase();
      final ResourceKey resourceKey = resourceManager.deriveKey( contentBase, "wizard-specification.xml" );
      final Resource resource = resourceManager.create( resourceKey, contentBase, WizardSpecification.class );
      return (WizardSpecification) resource.getResource();
    } catch ( final ResourceKeyCreationException e ) {
      // not a error.
    } catch ( final ResourceLoadingException e ) {
      // not a error
    } catch ( ResourceException e ) {
      throw new ReportProcessingException( "Failed to load the wizard-specification", e );
    }
    return null;
  }

  public static boolean isGroupMatchesType( final Group group, final GroupType type ) {
    if ( GroupType.RELATIONAL.equals( type ) ) {
      if ( group instanceof RelationalGroup ) {
        return true;
      }
    } else if ( GroupType.CT_COLUMN.equals( type ) ) {
      if ( group instanceof CrosstabColumnGroup ) {
        return true;
      }
    } else if ( GroupType.CT_ROW.equals( type ) ) {
      if ( group instanceof CrosstabRowGroup ) {
        return true;
      }
    }

    return false;
  }
}
