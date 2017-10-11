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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.wizard.AutoGeneratorUtility;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.RelationalAutoGeneratorPreProcessor;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.FieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.engine.classic.wizard.model.RootBandDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.WatermarkDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Thomas Morgner
 * @noinspection UnnecessaryBoxing, UnnecessaryUnboxing, UnusedDeclaration
 */
public class WizardProcessor extends AbstractReportPreProcessor {
  private static final Log logger = LogFactory.getLog( WizardProcessor.class );

  protected DefaultDataAttributeContext attributeContext;
  protected AbstractReportDefinition definition;
  protected DefaultFlowController flowController;
  protected WizardSpecification wizardSpecification;

  public WizardProcessor() {
  }

  public MasterReport performPreProcessing( final MasterReport definition,
                                            final DefaultFlowController flowController )
    throws ReportProcessingException {
    return (MasterReport) performCommonPreProcessing( definition, flowController, definition.getResourceManager() );
  }

  public SubReport performPreProcessing( final SubReport definition,
                                         final DefaultFlowController flowController )
    throws ReportProcessingException {
    return (SubReport) performCommonPreProcessing( definition, flowController, definition.getResourceManager() );
  }

  protected AbstractReportDefinition performCommonPreProcessing( final AbstractReportDefinition definition,
                                                                 final DefaultFlowController flowController,
                                                                 final ResourceManager resourceManager )
    throws ReportProcessingException {
    try {
      this.wizardSpecification = WizardProcessorUtil.loadWizardSpecification( definition, resourceManager );
      if ( wizardSpecification == null ) {
        return definition;
      }

      final StructureFunction[] functions = definition.getStructureFunctions();
      boolean hasOverrideFunction = false;
      for ( int i = 0; i < functions.length; i++ ) {
        final StructureFunction function = functions[ i ];
        if ( function instanceof WizardOverrideFormattingFunction ) {
          hasOverrideFunction = true;
          break;
        }
      }
      if ( hasOverrideFunction == false ) {
        definition.addStructureFunction( new WizardOverrideFormattingFunction() );
      }

      final ProcessingContext reportContext = flowController.getReportContext();
      this.definition = definition;
      this.flowController = flowController;
      this.attributeContext = new DefaultDataAttributeContext( reportContext.getOutputProcessorMetaData(),
        reportContext.getResourceBundleFactory().getLocale() );

      final Object o = definition.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE );
      if ( Boolean.TRUE.equals( o ) == false ) {
        return performRefreshPreProcessing();
      } else {
        return performGenerationPreProcessing();
      }
    } finally {
      this.wizardSpecification = null;
      this.definition = null;
      this.flowController = null;
      this.attributeContext = null;
    }

  }

  private AbstractReportDefinition performRefreshPreProcessing()
    throws ReportProcessingException {
    final GroupDefinition[] groupDefinitions = wizardSpecification.getGroupDefinitions();
    final int wizardCount = groupDefinitions.length;   // 1  5   5
    final int groupCount = definition.getGroupCount(); // 5  1   5
    final int count = Math.min( groupCount, wizardCount );
    for ( int i = 0; i < count; i++ ) {
      final int groupIdx = groupCount - i - 1;
      final int wizGroupIdx = wizardCount - i - 1;
      final GroupDefinition gd = groupDefinitions[ wizGroupIdx ];
      final RelationalGroup g = definition.getRelationalGroup( groupIdx );
      if ( g == null ) {
        break;
      }

      final Band groupHeader = g.getHeader();
      final Band groupFooter = g.getFooter();

      iterateSection( groupHeader, new UpdateHeaderTask( gd ) );
      iterateSection( groupFooter, new UpdateFooterTask( gd ) );
    }

    final Band itemband = definition.getItemBand();
    final Band detailsHeader = definition.getDetailsHeader();
    final Band detailsFooter = definition.getDetailsFooter();

    final DetailFieldDefinition[] detailFieldDefinitions = wizardSpecification.getDetailFieldDefinitions();
    for ( int i = 0; i < detailFieldDefinitions.length; i++ ) {
      final DetailFieldDefinition fieldDefinition = detailFieldDefinitions[ i ];
      final UpdateDetailsTask updateTask = new UpdateDetailsTask( fieldDefinition );
      iterateSection( itemband, updateTask );
      iterateSection( detailsHeader, updateTask );
      iterateSection( detailsFooter, updateTask );
    }

    return definition;
  }


  protected static interface UpdateTask {
    public void processElement( ReportElement e );
  }

  private static class UpdateHeaderTask implements UpdateTask {
    private GroupDefinition groupDefinition;

    private UpdateHeaderTask( final GroupDefinition groupDefinition ) {
      this.groupDefinition = groupDefinition;
    }

    public void processElement( final ReportElement e ) {
      if ( Boolean.TRUE.equals
        ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING ) ) ) {
        e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
          groupDefinition.getHeader() );
      }
      if ( Boolean.TRUE.equals
        ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
        e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA,
          groupDefinition );
      }
    }
  }

  private static class UpdateFooterTask implements UpdateTask {
    private GroupDefinition groupDefinition;

    private UpdateFooterTask( final GroupDefinition groupDefinition ) {
      this.groupDefinition = groupDefinition;
    }

    public void processElement( final ReportElement e ) {
      if ( Boolean.TRUE.equals
        ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING ) ) ) {
        e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
          groupDefinition.getFooter() );
      }
      if ( Boolean.TRUE.equals
        ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
        e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA,
          groupDefinition );
      }
    }
  }

  private static class UpdateDetailsTask implements UpdateTask {
    private FieldDefinition fieldDefinition;

    private UpdateDetailsTask( final FieldDefinition groupDefinition ) {
      this.fieldDefinition = groupDefinition;
    }

    public void processElement( final ReportElement e ) {
      final String field = fieldDefinition.getField();
      final Object labelFor = e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR );
      if ( labelFor != null ) {
        if ( ObjectUtilities.equal( field, labelFor ) == false ) {
          return;
        }
      } else {
        final Object fieldName = e.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
        if ( ObjectUtilities.equal( field, fieldName ) == false ) {
          return;
        }
      }

      if ( Boolean.TRUE.equals
        ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING ) ) ) {
        e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
          fieldDefinition );

        if ( Boolean.TRUE
          .equals( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.PRESERVE_USER_STYLING ) ) ) {
          final ElementStyleSheet styleSheet = e.getStyle();
          if ( styleSheet.isLocalKey( TextStyleKeys.BOLD ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_BOLD ) != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_BOLD, Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( TextStyleKeys.ITALIC ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_ITALICS )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_ITALICS,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( TextStyleKeys.UNDERLINED ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_UNDERLINE )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_UNDERLINE,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( TextStyleKeys.STRIKETHROUGH ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_STRIKETHROUGH )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_STRIKETHROUGH,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( TextStyleKeys.FONT ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_FONTFAMILY )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_FONTFAMILY,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( TextStyleKeys.FONTSIZE ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_FONTSIZE )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_FONTSIZE,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( ElementStyleKeys.PAINT ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_COLOR ) != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_COLOR,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( ElementStyleKeys.BACKGROUND_COLOR ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_BACKGROUND_COLOR )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_BACKGROUND_COLOR,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( ElementStyleKeys.VALIGNMENT ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_VALIGNMENT )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_VALIGNMENT,
                Boolean.FALSE );
            }
          }
          if ( styleSheet.isLocalKey( ElementStyleKeys.ALIGNMENT ) ) {
            if ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_ALIGNMENT )
              != null ) {
              e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ENABLE_STYLE_ALIGNMENT,
                Boolean.FALSE );
            }
          }
        }
      }
      if ( Boolean.TRUE.equals
        ( e.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
        e.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA,
          fieldDefinition );
      }
    }
  }

  protected void iterateSection( final Section s, final UpdateTask task ) {
    final int count = s.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = s.getElement( i );
      task.processElement( element );
      if ( element instanceof SubReport ) {
        continue;
      }
      if ( element instanceof Section ) {
        iterateSection( (Section) element, task );
      }
    }
  }

  protected AbstractReportDefinition performGenerationPreProcessing()
    throws ReportProcessingException {
    setupWatermark();
    setupRelationalGroups();
    /** remove check on data source to display to use cross-tab
     if (isCrosstab())
     {
     setupCrosstab();
     }
     else
     {
     clearCrosstab();

     }
     */
    setupDetails();
    return definition;
  }

  private void clearCrosstab() {
    final CrosstabGroup crosstabGroup = lookupCrosstab();
    if ( crosstabGroup != null ) {
      if ( definition.getRootGroup() == crosstabGroup ) {
        definition.setRootGroup( new RelationalGroup() );
      } else {
        final Section parentSection = crosstabGroup.getParentSection();
        if ( parentSection instanceof SubGroupBody ) {
          final SubGroupBody sgb = (SubGroupBody) parentSection;
          sgb.setGroup( new RelationalGroup() );
        }
      }
    }

  }

  /**
   * Creating a crosstab report deletes all sub-definitions and the itemband.
   *
   * @throws ReportProcessingException if an error occurs.
   */
  private void setupCrosstab() throws ReportProcessingException {
    CrosstabGroup crosstab = lookupCrosstab();
    if ( crosstab == null ) {
      crosstab = new CrosstabGroup();
      insertCrosstab( crosstab );
    }

    Group insertGroup = crosstab;
    final GroupDefinition[] groupDefinitions = wizardSpecification.getGroupDefinitions();
    for ( int i = 0; i < groupDefinitions.length; i++ ) {
      final GroupDefinition groupDefinition = groupDefinitions[ i ];
      if ( GroupType.CT_OTHER.equals( groupDefinition.getGroupType() ) == false ) {
        continue;
      }

      // create a new group and insert it at the end
      final CrosstabOtherGroup relationalGroup = new CrosstabOtherGroup();
      if ( groupDefinition.getGroupName() != null ) {
        relationalGroup.setName( groupDefinition.getGroupName() );
      }
      configureCrosstabOtherGroup( relationalGroup, groupDefinition );
      insertGroup.setBody( new CrosstabOtherGroupBody( relationalGroup ) );
      insertGroup = relationalGroup;
    }

    for ( int i = 0; i < groupDefinitions.length; i++ ) {
      final GroupDefinition groupDefinition = groupDefinitions[ i ];
      if ( GroupType.CT_ROW.equals( groupDefinition.getGroupType() ) == false ) {
        continue;
      }

      // create a new group and insert it at the end
      final CrosstabRowGroup relationalGroup = new CrosstabRowGroup();
      if ( groupDefinition.getGroupName() != null ) {
        relationalGroup.setName( groupDefinition.getGroupName() );
      }
      configureCrosstabRowGroup( relationalGroup, groupDefinition );
      insertGroup.setBody( new CrosstabRowGroupBody( relationalGroup ) );
      insertGroup = relationalGroup;
    }

    for ( int i = 0; i < groupDefinitions.length; i++ ) {
      final GroupDefinition groupDefinition = groupDefinitions[ i ];
      if ( GroupType.CT_COLUMN.equals( groupDefinition.getGroupType() ) == false ) {
        continue;
      }

      // create a new group and insert it at the end
      final CrosstabColumnGroup relationalGroup = new CrosstabColumnGroup();
      if ( groupDefinition.getGroupName() != null ) {
        relationalGroup.setName( groupDefinition.getGroupName() );
      }
      configureCrosstabColumnGroup( relationalGroup, groupDefinition );
      insertGroup.setBody( new CrosstabColumnGroupBody( relationalGroup ) );
      insertGroup = relationalGroup;
    }

    final Band itemBand = AutoGeneratorUtility.findGeneratedContent( definition.getItemBand() );
    if ( itemBand == null ) {
      return;
    }
    final DetailFieldDefinition[] detailFieldDefinitions = wizardSpecification.getDetailFieldDefinitions();
    if ( detailFieldDefinitions.length == 0 ) {
      return;
    }

    final Float[] widthSpecs = new Float[ detailFieldDefinitions.length ];
    for ( int i = 0; i < detailFieldDefinitions.length; i++ ) {
      final DetailFieldDefinition fieldDefinition = detailFieldDefinitions[ i ];
      final Length length = fieldDefinition.getWidth();
      if ( length == null ) {
        continue;
      }
      widthSpecs[ i ] = length.getNormalizedValue();
    }
    final float[] computedWidth =
      AutoGeneratorUtility.computeFieldWidths( widthSpecs, definition.getPageDefinition().getWidth() );


    itemBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    for ( int i = 0; i < detailFieldDefinitions.length; i++ ) {
      final DetailFieldDefinition detailFieldDefinition = detailFieldDefinitions[ i ];
      setupField( null, null, itemBand, detailFieldDefinition, computedWidth[ i ], i );
    }

  }

  protected void setupDetails() throws ReportProcessingException {
    final DetailFieldDefinition[] detailFieldDefinitions = wizardSpecification.getDetailFieldDefinitions();
    if ( detailFieldDefinitions.length == 0 ) {
      if ( wizardSpecification.isAutoGenerateDetails() ) {
        final RelationalAutoGeneratorPreProcessor generatorPreProcessor = new RelationalAutoGeneratorPreProcessor();
        if ( definition instanceof MasterReport ) {
          generatorPreProcessor.performPreProcessing( (MasterReport) definition, flowController );
        } else if ( definition instanceof SubReport ) {
          generatorPreProcessor.performPreProcessing( (SubReport) definition, flowController );
        }
      }
      return;
    }

    definition.getDetailsHeader().setRepeat( true );
    definition.getDetailsFooter().setRepeat( true );

    final Band detailsHeader = AutoGeneratorUtility.findGeneratedContent( definition.getDetailsHeader() );
    final Band detailsFooter = AutoGeneratorUtility.findGeneratedContent( definition.getDetailsFooter() );
    final Band itemBand = AutoGeneratorUtility.findGeneratedContent( definition.getItemBand() );

    if ( itemBand == null ) {
      return;
    }

    final Float[] widthSpecs = new Float[ detailFieldDefinitions.length ];
    for ( int i = 0; i < detailFieldDefinitions.length; i++ ) {
      final DetailFieldDefinition fieldDefinition = detailFieldDefinitions[ i ];
      final Length length = fieldDefinition.getWidth();
      if ( length == null ) {
        continue;
      }
      widthSpecs[ i ] = length.getNormalizedValue();
    }
    final float[] computedWidth =
      AutoGeneratorUtility.computeFieldWidths( widthSpecs, definition.getPageDefinition().getWidth() );

    itemBand.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    if ( detailsHeader != null ) {
      detailsHeader.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    }
    if ( detailsFooter != null ) {
      detailsFooter.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    }

    for ( int i = 0; i < detailFieldDefinitions.length; i++ ) {
      final DetailFieldDefinition detailFieldDefinition = detailFieldDefinitions[ i ];
      setupField( detailsHeader, detailsFooter, itemBand, detailFieldDefinition, computedWidth[ i ], i );
    }

    if ( detailsFooter != null ) {
      final Element[] elements = detailsFooter.getElementArray();
      boolean footerEmpty = true;
      for ( int i = 0; i < elements.length; i++ ) {
        final Element element = elements[ i ];
        if ( LabelType.class.isInstance( element.getElementType() ) == false ) {
          footerEmpty = false;
          break;
        }
      }
      if ( footerEmpty ) {
        detailsFooter.clear();
      }
    }
  }

  protected void setupDefaultPadding( final Band band, final Element detailElement ) {
    final Object maybePaddingTop =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.PADDING_TOP );
    final Object maybePaddingLeft =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.PADDING_LEFT );
    final Object maybePaddingBottom =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.PADDING_BOTTOM );
    final Object maybePaddingRight =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.PADDING_RIGHT );

    if ( maybePaddingTop instanceof Number == false ||
      maybePaddingLeft instanceof Number == false ||
      maybePaddingBottom instanceof Number == false ||
      maybePaddingRight instanceof Number == false ) {
      return;
    }

    final Number paddingTop = (Number) maybePaddingTop;
    final Number paddingLeft = (Number) maybePaddingLeft;
    final Number paddingBottom = (Number) maybePaddingBottom;
    final Number paddingRight = (Number) maybePaddingRight;

    final ElementStyleSheet styleSheet = detailElement.getStyle();
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( paddingTop.floatValue() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( paddingLeft.floatValue() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( paddingBottom.floatValue() ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( paddingRight.floatValue() ) );
  }

  protected void setupDefaultGrid( final Band band, final Element detailElement ) {
    setupDefaultPadding( band, detailElement );
    final ElementStyleSheet styleSheet = detailElement.getStyle();
    // Always make the height of the detailElement dynamic to the band
    // According to thomas negative numbers equate to percentages
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( -100 ) );

    final Object maybeBorderStyle =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.GRID_STYLE );
    final Object maybeBorderWidth =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.GRID_WIDTH );
    final Object maybeBorderColor =
      band.getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.GRID_COLOR );

    if ( maybeBorderColor instanceof Color == false ||
      maybeBorderStyle instanceof BorderStyle == false ||
      maybeBorderWidth instanceof Number == false ) {
      return;
    }

    final BorderStyle style = (BorderStyle) maybeBorderStyle;
    final Color color = (Color) maybeBorderColor;
    final Number number = (Number) maybeBorderWidth;
    final Float width = new Float( number.floatValue() );

    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, width );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, color );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, style );

    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, width );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, color );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, style );

    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, width );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, color );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, style );

    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, width );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, color );
    styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, style );
  }

  protected void setupField( final Band detailsHeader,
                             final Band detailsFooter,
                             final Band itemBand,
                             final DetailFieldDefinition field,
                             final float width,
                             final int fieldIdx ) throws ReportProcessingException {
    if ( StringUtils.isEmpty( field.getField() ) ) {
      return;
    }

    final Element detailElement =
      AutoGeneratorUtility.generateDetailsElement( field.getField(), computeElementType( field ) );
    setupDefaultGrid( itemBand, detailElement );

    final String id = "wizard::details-" + field.getField();
    detailElement.setName( id );
    detailElement.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( width ) );
    if ( Boolean.TRUE.equals
      ( detailElement
        .getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING ) ) ) {
      detailElement
        .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA, field );
    }
    if ( Boolean.TRUE.equals
      ( detailElement
        .getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
      detailElement
        .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA, field );
    }
    itemBand.addElement( detailElement );

    if ( Boolean.TRUE.equals( field.getOnlyShowChangingValues() ) ) {
      detailElement.setAttribute
        ( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ONLY_SHOW_CHANGING_VALUES, Boolean.TRUE );
    }

    if ( detailsHeader != null ) {
      final Element headerElement = AutoGeneratorUtility.generateHeaderElement( field.getField() );
      setupDefaultGrid( detailsHeader, headerElement );
      headerElement.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( width ) );
      if ( Boolean.TRUE.equals
        ( headerElement
          .getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING ) ) ) {
        headerElement
          .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA, field );
      }
      if ( Boolean.TRUE.equals
        ( headerElement
          .getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
        headerElement
          .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA, field );
      }
      headerElement.setAttribute( AttributeNames.Wizard.NAMESPACE,
        MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT, field.getHorizontalAlignment() );

      detailsHeader.addElement( headerElement );
    }

    if ( detailsFooter != null ) {
      final Class aggFunctionClass = field.getAggregationFunction();
      final Element footerElement = AutoGeneratorUtility.generateFooterElement
        ( aggFunctionClass, computeElementType( field ), null, field.getField() );

      setupDefaultGrid( detailsFooter, footerElement );

      footerElement.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( width ) );
      if ( Boolean.TRUE.equals
        ( footerElement
          .getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING ) ) ) {
        footerElement
          .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA, field );
      }
      if ( Boolean.TRUE.equals
        ( footerElement
          .getAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES ) ) ) {
        footerElement
          .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA, field );
      }

      detailsFooter.addElement( footerElement );
    }
  }


  /*
   * Removes the unusedTemplateGroups based on the assumption that if a group doesn't have any
   * fields assigned to it that it is empty.
   */
  private void removedUnusedTemplateGroups( final int groupsDefined ) {
    final RelationalGroup[] templateRelationalGroups = getTemplateRelationalGroups();
    final int templateRelationalGroupCount = templateRelationalGroups.length;
    for ( int i = groupsDefined; i < templateRelationalGroupCount; i++ ) {
      final RelationalGroup templateRelationalGroup = templateRelationalGroups[ i ];
      definition.removeGroup( templateRelationalGroup );
    }
  }

  /**
   * @return the relational groups in the templates in a flattened array.
   */
  private RelationalGroup[] getTemplateRelationalGroups() {
    final ArrayList<RelationalGroup> relationalGroups = new ArrayList<RelationalGroup>();
    Group group = definition.getRootGroup();
    while ( group != null && group instanceof RelationalGroup ) {
      relationalGroups.add( (RelationalGroup) group );
      final GroupBody body = group.getBody();
      if ( body instanceof SubGroupBody ) {
        final SubGroupBody sgBody = (SubGroupBody) body;
        if ( sgBody.getGroup() instanceof RelationalGroup ) {
          group = sgBody.getGroup();
        } else {
          group = null;
        }
      } else {
        group = null;
      }
    }

    return relationalGroups.toArray( new RelationalGroup[ relationalGroups.size() ] );
  }

  private void setupRelationalGroups() throws ReportProcessingException {
    final Group rootgroup = definition.getRootGroup();
    RelationalGroup group;
    if ( rootgroup instanceof RelationalGroup == false ) {
      group = null;
    } else {
      group = (RelationalGroup) rootgroup;
    }

    final RelationalGroup template = findInnermostRelationalGroup( definition );

    final GroupDefinition[] groupDefinitions = wizardSpecification.getGroupDefinitions();
    for ( int i = 0; i < groupDefinitions.length; i++ ) {
      final GroupDefinition groupDefinition = groupDefinitions[ i ];
      final GroupType type = groupDefinition.getGroupType();
      if ( type != null && GroupType.RELATIONAL.equals( type ) == false ) {
        continue;
      }

      if ( group == null ) {
        // create a new group and insert it at the end
        final RelationalGroup relationalGroup;
        if ( template != null ) {
          relationalGroup = (RelationalGroup) template.derive();
        } else {
          relationalGroup = new RelationalGroup();
        }

        if ( groupDefinition.getGroupName() != null ) {
          relationalGroup.setName( groupDefinition.getGroupName() );
        }
        configureRelationalGroup( relationalGroup, groupDefinition, i );
        insertGroup( relationalGroup );
      } else {
        // modify the existing group
        configureRelationalGroup( group, groupDefinition, i );

        final GroupBody body = group.getBody();
        if ( body instanceof SubGroupBody ) {
          final SubGroupBody sgBody = (SubGroupBody) body;
          if ( sgBody.getGroup() instanceof RelationalGroup ) {
            group = (RelationalGroup) sgBody.getGroup();
          } else {
            group = null;
          }
        } else {
          group = null;
        }
      }
    }
    // Remove any group bands are not being used ie. groups with no fields
    removedUnusedTemplateGroups( groupDefinitions.length );
  }

  private RelationalGroup findInnermostRelationalGroup( final AbstractReportDefinition definition ) {
    RelationalGroup retval = null;
    Group existingGroup = definition.getRootGroup();
    while ( existingGroup instanceof RelationalGroup ) {
      retval = (RelationalGroup) existingGroup;
      final GroupBody body = existingGroup.getBody();
      if ( body instanceof SubGroupBody == false ) {
        return retval;
      }
      final SubGroupBody sgb = (SubGroupBody) body;
      existingGroup = sgb.getGroup();
    }

    return retval;
  }

  /**
   * Inserts the crosstab into the report as innermost group. This method will fail if there is already a crosstab
   * active.
   *
   * @param crosstabGroup
   */
  private void insertCrosstab( final CrosstabGroup crosstabGroup ) {
    Group existingGroup = definition.getRootGroup();
    GroupBody gb = existingGroup.getBody();
    while ( gb instanceof SubGroupBody ) {
      final SubGroupBody sgb = (SubGroupBody) gb;
      existingGroup = sgb.getGroup();
      gb = existingGroup.getBody();
    }
    existingGroup.setBody( new SubGroupBody( crosstabGroup ) );
  }

  private CrosstabGroup lookupCrosstab() {
    Group existingGroup = definition.getRootGroup();
    if ( existingGroup instanceof CrosstabGroup ) {
      return (CrosstabGroup) existingGroup;
    }

    GroupBody gb = existingGroup.getBody();
    while ( gb instanceof SubGroupBody ) {
      final SubGroupBody sgb = (SubGroupBody) gb;
      existingGroup = sgb.getGroup();
      if ( existingGroup instanceof CrosstabGroup ) {
        return (CrosstabGroup) existingGroup;
      }
      gb = existingGroup.getBody();
    }

    return null;
  }

  private void insertGroup( final RelationalGroup group ) {
    Group lastGroup = null;
    Group insertGroup = definition.getRootGroup();
    while ( true ) {
      if ( insertGroup instanceof RelationalGroup == false ) {
        if ( lastGroup == null ) {
          definition.setRootGroup( group );
          group.setBody( new SubGroupBody( insertGroup ) );
          return;
        }

        final GroupBody body = lastGroup.getBody();
        final SubGroupBody sgb = new SubGroupBody( group );
        lastGroup.setBody( sgb );
        group.setBody( body );
        return;
      }

      final GroupBody body = insertGroup.getBody();
      if ( body instanceof SubGroupBody == false ) {
        final SubGroupBody sgb = new SubGroupBody( group );
        insertGroup.setBody( sgb );
        group.setBody( body );
        return;
      }

      lastGroup = insertGroup;
      final SubGroupBody sgb = (SubGroupBody) body;
      insertGroup = sgb.getGroup();
    }
  }

  private void configureCrosstabOtherGroup( final CrosstabOtherGroup group,
                                            final GroupDefinition groupDefinition ) throws ReportProcessingException {
    final String groupField = groupDefinition.getField();
    group.setField( groupField );

    configureCrosstabGroupHeader( group, groupDefinition );
    configureCrosstabGroupFooter( group, groupDefinition );
  }

  private void configureCrosstabRowGroup( final CrosstabRowGroup group,
                                          final GroupDefinition groupDefinition ) throws ReportProcessingException {
    final String groupField = groupDefinition.getField();
    group.setField( groupField );

    configureCrosstabGroupHeader( group, groupDefinition );
    configureCrosstabGroupFooter( group, groupDefinition );
  }

  private void configureCrosstabColumnGroup( final CrosstabColumnGroup group,
                                             final GroupDefinition groupDefinition ) throws ReportProcessingException {
    final String groupField = groupDefinition.getField();
    group.setField( groupField );

    configureCrosstabGroupHeader( group, groupDefinition );
    configureCrosstabGroupFooter( group, groupDefinition );
  }


  protected void configureRelationalGroup( final RelationalGroup group,
                                           final GroupDefinition groupDefinition,
                                           final int index ) throws ReportProcessingException {
    final String groupField = groupDefinition.getField();
    if ( groupField != null ) {
      group.setFieldsArray( new String[] { groupField } );
    }

    configureRelationalGroupFooter( group, groupDefinition, index );
    configureRelationalGroupHeader( group, groupDefinition, index );
  }

  private void configureCrosstabGroupHeader( final Group group,
                                             final GroupDefinition groupDefinition ) {
    /*
    final RootBandDefinition headerDefinition = groupDefinition.getHeader();
    if (headerDefinition.isVisible())
    {
      final GroupHeader header = group.getHeader();
      final Boolean repeat = headerDefinition.getRepeat();
      if (repeat != null)
      {
        header.setRepeat(repeat.booleanValue());
      }

      final Band content = AutoGeneratorUtility.findGeneratedContent(header);
      if (content == null)
      {
        return;
      }

      final Element headerElement = AutoGeneratorUtility.generateDetailsElement
          (groupDefinition.getField(), computeElementType(groupDefinition));
      final Length length = groupDefinition.getWidth();
      if (length != null)
      {
        headerElement.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, length.getNormalizedValue());
      }
      headerElement.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
      headerDefinition);
      headerElement.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA,
      groupDefinition);

      content.clear();
      content.addElement(headerElement);
    }
    */
  }

  protected void configureRelationalGroupHeader( final RelationalGroup group,
                                                 final GroupDefinition groupDefinition,
                                                 final int index ) {
    final RootBandDefinition headerDefinition = groupDefinition.getHeader();
    if ( headerDefinition.isVisible() ) {
      final GroupHeader header = group.getHeader();
      final Boolean repeat = headerDefinition.getRepeat();
      if ( repeat != null ) {
        header.setRepeat( repeat.booleanValue() );
      }

      final Band content = AutoGeneratorUtility.findGeneratedContent( header );
      if ( content == null ) {
        return;
      }

      final Element headerLabelElement = new Element();
      headerLabelElement.setElementType( new LabelType() );
      if ( groupDefinition.getDisplayName() != null ) {
        headerLabelElement.setAttribute
          ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, groupDefinition.getDisplayName() );
      } else {
        headerLabelElement.setAttribute
          ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, groupDefinition.getField() );
        headerLabelElement.setAttribute
          ( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, groupDefinition.getField() );
        headerLabelElement.setAttribute( AttributeNames.Wizard.NAMESPACE,
          AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, Boolean.TRUE );
      }

      final Element headerValueElement =
        AutoGeneratorUtility
          .generateDetailsElement( groupDefinition.getField(), computeElementType( groupDefinition ) );
      headerValueElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, "-" );

      final Band headerElement = new Band();
      headerElement.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );
      headerElement.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( -100 ) );
      headerElement.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
      headerElement.setAttribute( AttributeNames.Wizard.NAMESPACE,
        AttributeNames.Wizard.ALLOW_METADATA_STYLING, Boolean.TRUE );
      headerElement.setAttribute
        ( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, groupDefinition.getField() );
      headerElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
        headerDefinition );
      headerElement.addElement( headerLabelElement );
      headerElement.addElement( headerValueElement );
      content.clear();
      content.addElement( headerElement );
    }
  }

  protected void configureRelationalGroupFooter( final RelationalGroup group, final GroupDefinition groupDefinition,
                                                 final int index )
    throws ReportProcessingException {
    final RootBandDefinition footerDefinition = groupDefinition.getFooter();
    if ( footerDefinition.isVisible() == false ) {
      return;
    }

    if ( groupDefinition.getAggregationFunction() == null && ( groupDefinition.getGroupTotalsLabel() == null
      || groupDefinition.getGroupTotalsLabel().length() == 0 ) ) {
      return;
    }

    final GroupFooter footer = group.getFooter();
    final Boolean repeat = footerDefinition.getRepeat();
    if ( repeat != null ) {
      footer.setRepeat( repeat.booleanValue() );
    }

    final Band content = AutoGeneratorUtility.findGeneratedContent( footer );
    if ( content == null ) {
      return;
    }

    final Class aggFunctionClass = groupDefinition.getAggregationFunction();
    final Element footerValueElement = AutoGeneratorUtility.generateFooterElement
      ( aggFunctionClass, computeElementType( groupDefinition ),
        groupDefinition.getGroupName(), groupDefinition.getField() );

    final Element footerLabelElement = new Element();
    footerLabelElement.setElementType( new LabelType() );
    if ( groupDefinition.getGroupTotalsLabel() != null ) {
      footerLabelElement.setAttribute
        ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, groupDefinition.getGroupTotalsLabel() );
    } else {
      footerLabelElement.setAttribute
        ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, groupDefinition.getField() );
      footerLabelElement.setAttribute
        ( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, groupDefinition.getField() );
      footerLabelElement.setAttribute( AttributeNames.Wizard.NAMESPACE,
        AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, Boolean.TRUE );
    }

    final Band footerElement = new Band();
    footerElement.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );
    footerElement.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( -100 ) );
    footerElement.getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
    footerElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING,
      Boolean.TRUE );
    footerElement.setAttribute
      ( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, groupDefinition.getField() );
    footerElement.setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
      footerDefinition );
    footerElement
      .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA, groupDefinition );
    footerElement.addElement( footerLabelElement );
    footerElement.addElement( footerValueElement );

    content.clear();
    content.addElement( footerElement );
  }

  private void configureCrosstabGroupFooter( final Group group, final GroupDefinition groupDefinition )
    throws ReportProcessingException {
    /*
    final RootBandDefinition footerDefinition = groupDefinition.getFooter();
    if (footerDefinition.isVisible() == false)
    {
      return;
    }

    if (groupDefinition.getAggregationFunction() == null)
    {
      return;
    }

    final GroupFooter footer = group.getFooter();
    final Boolean repeat = footerDefinition.getRepeat();
    if (repeat != null)
    {
      footer.setRepeat(repeat.booleanValue());
    }

    final Band content = AutoGeneratorUtility.findGeneratedContent(footer);
    if (content == null)
    {
      return;
    }

    final Class aggFunctionClass = groupDefinition.getAggregationFunction();
    final Element footerValueElement = AutoGeneratorUtility.generateFooterElement
        (aggFunctionClass, computeElementType(groupDefinition),
            groupDefinition.getGroupName(), groupDefinition.getField());
    footerValueElement.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FORMAT_DATA,
     footer);
    footerValueElement.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.CACHED_WIZARD_FIELD_DATA,
    groupDefinition);

    content.clear();
    content.addElement(footerValueElement);
*/
  }

  protected ElementType computeElementType( final FieldDefinition fieldDefinition ) {
    final String field = fieldDefinition.getField();
    final DataAttributes attributes = flowController.getDataSchema().getAttributes( field );
    if ( attributes == null ) {
      logger.warn( "Field '" + field + "' is declared in the wizard-specification, " +
        "but not present in the data. Assuming defaults." );
      return new TextFieldType();
    }
    final Class fieldType = (Class) attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class, attributeContext );
    if ( fieldType == null ) {
      return new TextFieldType();
    }

    return AutoGeneratorUtility.createFieldType( fieldType );
  }

  private boolean isCrosstab() {
    final GroupDefinition[] groupDefinitions = wizardSpecification.getGroupDefinitions();
    for ( int i = 0; i < groupDefinitions.length; i++ ) {
      final GroupDefinition groupDefinition = groupDefinitions[ i ];
      final GroupType groupType = groupDefinition.getGroupType();
      if ( GroupType.CT_COLUMN.equals( groupType ) ||
        GroupType.CT_ROW.equals( groupType ) ) {
        return true;
      }
    }

    final DataAttributes tableAttributes = flowController.getDataSchema().getTableAttributes();
    final Object crosstabMode = tableAttributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.CROSSTAB_MODE, String.class, attributeContext );
    if ( MetaAttributeNames.Core.CROSSTAB_VALUE_NORMALIZED.equals( crosstabMode ) ) {
      // the data-source indicates a high probability that a crosstab is requested. 
      return true;
    }
    return false;
  }

  private void setupWatermark() {
    final WatermarkDefinition watermarkDefinition = wizardSpecification.getWatermarkDefinition();
    if ( watermarkDefinition.isVisible() == false ) {
      return;
    }

    if ( watermarkDefinition.getSource() == null ) {
      return;
    }

    final Watermark watermark = definition.getWatermark();
    final Band content = AutoGeneratorUtility.findGeneratedContent( watermark );
    if ( content == null ) {
      // there is already some content, and we are not allowed to override it.
      return;
    }

    content.clear();

    final Element watermarkImage = new Element();
    watermarkImage.setElementType( new ContentType() );
    watermarkImage.setAttribute
      ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, watermarkDefinition.getSource() );
    final ElementStyleSheet watermarkStyle = watermarkImage.getStyle();
    watermarkStyle.setStyleProperty( ElementStyleKeys.POS_X, convertLength( watermarkDefinition.getX() ) );
    watermarkStyle.setStyleProperty( ElementStyleKeys.POS_Y, convertLength( watermarkDefinition.getY() ) );
    watermarkStyle.setStyleProperty( ElementStyleKeys.MIN_WIDTH, convertLength( watermarkDefinition.getWidth() ) );
    watermarkStyle.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, convertLength( watermarkDefinition.getHeight() ) );
    watermarkStyle.setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, watermarkDefinition.getKeepAspectRatio() );
    watermarkStyle.setStyleProperty( ElementStyleKeys.SCALE, watermarkDefinition.getScale() );

    content.addElement( watermarkImage );
  }

  private Float convertLength( final Length length ) {
    if ( length == null ) {
      return null;
    }
    return length.getNormalizedValue();
  }

}
