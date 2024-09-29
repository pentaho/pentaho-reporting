/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.wizard.ui.xul;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModelFactory;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessorUtil;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.util.SourceFieldDefinition;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.ui.xul.XulEventSourceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A thin wrapper around the report-spec to allow the model to have a state without a file or definition being active.
 *
 * @author Thomas Morgner
 */
public class WizardEditorModel extends XulEventSourceAdapter {
  private static final String RELATIONAL_MODEL_PROPERTY_NAME = "relationalModel"; //$NON-NLS-1$

  private AbstractReportDefinition reportDefinition;

  private AbstractReportDefinition emptyTemplate;

  private boolean relationalModel;

  private DataSchemaModel dataSchemaModel;

  private DataAttributeContext attributeContext;

  private WizardSpecification specification;

  private boolean materialize;

  private boolean editing = false;

  private DataFactory dataFactory;

  public WizardEditorModel( final AbstractReportDefinition emptyTemplate ) {
    if ( emptyTemplate == null ) {
      throw new NullPointerException();
    }
    this.materialize = true;
    this.relationalModel = true;
    this.emptyTemplate = emptyTemplate;
    this.attributeContext = new DefaultDataAttributeContext();
    this.reportDefinition = (AbstractReportDefinition) emptyTemplate.derive();
  }

  public WizardEditorModel() {
    this( createDefaultReport() );
  }

  private static MasterReport createDefaultReport() {
    final MasterReport report = new MasterReport();
    report.setAutoSort( Boolean.TRUE );
    report.setDataFactory( new CompoundDataFactory() );
    report.setQuery( null );
    return report;
  }

  public AbstractReportDefinition getReportDefinition() {
    return reportDefinition;
  }

  public void setReportDefinition( final AbstractReportDefinition reportDefinition, final boolean isEditing ) {
    if ( reportDefinition == null ) {
      throw new NullPointerException();
    }
    final AbstractReportDefinition oldDefinition = this.reportDefinition;
    this.reportDefinition = reportDefinition;
    if ( oldDefinition != reportDefinition ) {
      dataSchemaModel = null;
      specification = getReportSpec();  // now get the new one if it exists
      if ( dataFactory == null ) {
        final DataFactory theDataFactory = reportDefinition.getDataFactory();
        if ( theDataFactory.getQueryNames().length > 0 ) {
          dataFactory = reportDefinition.getDataFactory();
        }
      } else {
        reportDefinition.setQuery( oldDefinition.getQuery() );
        reportDefinition.setDataFactory( dataFactory );
      }
      this.firePropertyChange( "reportDefinition", oldDefinition, reportDefinition );
    }
    editing = isEditing;
  }

  public void setReportDefinition( final AbstractReportDefinition reportDefinition ) {
    setReportDefinition( reportDefinition, false );
  }

  public AbstractReportDefinition getEmptyTemplate() {
    return (AbstractReportDefinition) emptyTemplate.derive();
  }

  public WizardSpecification getReportSpec() {
    if ( specification == null ) {
      try {

        specification = WizardProcessorUtil.loadWizardSpecification
          ( reportDefinition, DesignTimeUtil.getResourceManager( reportDefinition ) );
        if ( specification != null ) {
          return specification;
        }
      } catch ( ReportProcessingException e ) {
        // ignore, create a new one
      }

      specification = new DefaultWizardSpecification();
      WizardProcessorUtil.applyWizardSpec( reportDefinition, specification );
    }

    return specification;
  }

  public ResourceKey getDefinitionSource() {
    return reportDefinition.getDefinitionSource();
  }

  public boolean isRelationalModel() {
    return relationalModel;
  }

  public void setRelationalModel( final boolean relationalModel ) {
    final boolean oldRelational = this.relationalModel;
    this.relationalModel = relationalModel;
    if ( oldRelational != relationalModel ) {
      this.firePropertyChange( RELATIONAL_MODEL_PROPERTY_NAME, oldRelational, relationalModel );
    }
  }

  public List<SourceFieldDefinition> getSelectableFieldsArray() {
    final List<SourceFieldDefinition> sourceFields = new ArrayList<SourceFieldDefinition>();
    final DataSchemaModel localSchemaModel = getDataSchema();
    final DataSchema dataSchema = localSchemaModel.getDataSchema();
    final String[] names = dataSchema.getNames();
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[ i ];
      final SourceFieldDefinition fieldDefinition =
        new SourceFieldDefinition( name, getDataSchema().getDataSchema() );
      sourceFields.add( fieldDefinition );
    }
    return sourceFields;
  }

  public void updateQuery( final DataFactory factory, final String queryName ) {
    getReportDefinition().setQuery( queryName );
    getReportDefinition().setDataFactory( factory );
  }

  public static DataSchemaModel compileDataSchemaModel( final AbstractReportDefinition reportDefinition ) {
    final ContextAwareDataSchemaModelFactory factory =
      ClassicEngineBoot.getInstance().getObjectFactory().get( ContextAwareDataSchemaModelFactory.class );
    return factory.create( reportDefinition );
  }

  public DataSchemaModel getDataSchema() {
    if ( dataSchemaModel == null ) {
      dataSchemaModel = compileDataSchemaModel( reportDefinition );
    }
    return dataSchemaModel;
  }

  public DataAttributeContext getAttributeContext() {
    return attributeContext;
  }

  public boolean isMaterialize() {
    return materialize;
  }

  public void setMaterialize( final boolean materialize ) {
    this.materialize = materialize;
  }

  public boolean isEditing() {
    return editing;
  }
}
