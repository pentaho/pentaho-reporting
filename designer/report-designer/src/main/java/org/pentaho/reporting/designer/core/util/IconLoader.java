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

package org.pentaho.reporting.designer.core.util;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class IconLoader {

  private static final IconLoader instance = new IconLoader();

  private ImageIcon fowardArrowIcon;
  private ImageIcon moveUpIcon;
  private ImageIcon moveDownIcon;


  private ImageIcon reportFrameIcon;

  private ImageIcon functionsIcon;

  private ImageIcon undoIconSmall;

  private ImageIcon redoIconSmall;


  private ImageIcon deleteIconSmall;


  private ImageIcon dataSetsIcon;

  private ImageIcon propertiesDataSetIcon;


  private ImageIcon infoIcon;

  private ImageIcon warningIcon;

  private ImageIcon errorIcon;

  private ImageIcon noErrorIcon;


  private ImageIcon drawSelectionTypeClampIcon;

  private ImageIcon drawSelectionTypeOutlineIcon;


  private ImageIcon paletteIcon;

  private ImageIcon messagesIcon;

  private ImageIcon propertyTableIcon;

  private ImageIcon reportTreeIcon;


  private ImageIcon reportWizardIcon;


  private ImageIcon pageFirstIcon;

  private ImageIcon pageUpIcon;

  private ImageIcon pageDownIcon;

  private ImageIcon pageLastIcon;


  private ImageIcon aboutIcon;


  private ImageIcon aboutDialogPicture;


  private ImageIcon generalSettingsIcon32;

  private ImageIcon browserIcon32;

  private ImageIcon networkIcon32;

  private ImageIcon externalToolsIcon32;


  private ImageIcon cutIcon;

  private ImageIcon copyIcon;

  private ImageIcon pasteIcon;

  private ImageIcon visitOnlineForumIcon;


  private ImageIcon selectionEdge;


  private ImageIcon dontShowInLayoutGUISelectionEdge;


  private ImageIcon newIcon;

  private ImageIcon openIcon;

  private ImageIcon saveIcon;


  private ImageIcon mergeIcon;

  private ImageIcon settingsIcon;


  private ImageIcon selectAllIcon;

  private ImageIcon deselectAllIcon;

  private ImageIcon layerUpIcon;

  private ImageIcon layerDownIcon;


  private ImageIcon zoomIcon;


  private ImageIcon zoomOverlay50Icon;

  private ImageIcon zoomOverlay100Icon;

  private ImageIcon zoomOverlay200Icon;

  private ImageIcon zoomOverlay400Icon;


  private ImageIcon layoutBandsIcon;

  private ImageIcon exportXMLIcon;


  private ImageIcon createReportIcon;


  private ImageIcon previewPDFIcon;

  private ImageIcon previewHTMLIcon;

  private ImageIcon previewRTFIcon;

  private ImageIcon previewXLSIcon;

  private ImageIcon previewCSVIcon;

  private ImageIcon previewXMLIcon;


  private ImageIcon alignLeftIcon;

  private ImageIcon alignCenterIcon;

  private ImageIcon alignRightIcon;

  private ImageIcon alignTopIcon;

  private ImageIcon alignMiddleIcon;

  private ImageIcon alignBottomIcon;


  private ImageIcon distributeLeftIcon;

  private ImageIcon distributeCenterIcon;

  private ImageIcon distributeRightIcon;

  private ImageIcon distributeGapsHorizontalIcon;

  private ImageIcon distributeTopIcon;

  private ImageIcon distributeMiddleIcon;

  private ImageIcon distributeBottomIcon;

  private ImageIcon distributeGapsVerticalIcon;

  private ImageIcon groupIcon;

  private ImageIcon sortAscendingIcon;

  private ImageIcon sortDescendingIcon;

  private ImageIcon openFolderIcon;

  private ImageIcon snapToElementsIcon;


  private ImageIcon boldCommand;

  private ImageIcon italicCommand;

  private ImageIcon underlineCommand;

  private ImageIcon textAlignLeftCommand;

  private ImageIcon textAlignRightCommand;

  private ImageIcon textAlignCenterCommand;

  private ImageIcon textAlignJustifyCommand;

  private ImageIcon colorChooserCommand;


  private ImageIcon samplesFolderClosed;

  private ImageIcon samplesFolderOpened;

  private ImageIcon genericSquare;

  private ImageIcon genericSquareDisabled;

  private ImageIcon addIcon;

  private ImageIcon editIcon;

  private ImageIcon removeIcon;

  private ImageIcon previewIcon;

  private ImageIcon parameterIcon;

  private ImageIcon functionIcon;

  private ImageIcon rubberbandSelectionIcon;

  private ImageIcon templateColoredSample;

  private ImageIcon blankDocumentIcon;

  private ImageIcon templateDocumentIcon;

  private ImageIcon wizardDocumentIcon;

  private ImageIcon chevronRight;

  private ImageIcon chevronDown;
  private ImageIcon productIcon;
  private ImageIcon refreshIcon;
  private ImageIcon hyperlinkIcon;
  private ImageIcon emptyIcon;

  private ImageIcon crosstabBandSelectionIcon;

  public static IconLoader getInstance() {
    return instance;
  }

  private IconLoader() {
    hyperlinkIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/hyperlink.png" ) ); // NON-NLS
    refreshIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/refresh.png" ) ); // NON-NLS
    fowardArrowIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/e_forward.gif" ) ); // NON-NLS
    moveUpIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/move_up.gif" ) ); // NON-NLS
    moveDownIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/move_down.gif" ) ); // NON-NLS

    reportFrameIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ReportFrameIcon.png" ) ); // NON-NLS

    functionsIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/reportelements/Functions.png" ) ); // NON-NLS
    undoIconSmall = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/UndoIcon.png" ) ); // NON-NLS
    redoIconSmall = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/RedoIcon.png" ) ); // NON-NLS

    deleteIconSmall = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DeleteIcon.png" ) ); // NON-NLS

    dataSetsIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/reportelements/DataSets.png" ) ); // NON-NLS
    propertiesDataSetIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/reportelements/PropertiesDataSetIcon.png" ) ); // NON-NLS

    infoIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/InfoIcon.png" ) ); // NON-NLS
    warningIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/WarningIcon.png" ) ); // NON-NLS
    errorIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/ErrorIcon.png" ) ); // NON-NLS
    noErrorIcon = new ImageIcon( new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB ) );

    drawSelectionTypeOutlineIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DrawSelectionTypeOutlineIcon.png" ) ); // NON-NLS
    drawSelectionTypeClampIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DrawSelectionTypeClampIcon.png" ) ); // NON-NLS

    paletteIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PaletteIcon.png" ) ); // NON-NLS
    messagesIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/MessagesIcon.png" ) ); // NON-NLS
    propertyTableIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PropertyTableIcon.png" ) ); // NON-NLS
    reportTreeIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ReportTreeIcon.png" ) ); // NON-NLS

    reportWizardIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ReportWizardIcon.png" ) ); // NON-NLS

    pageFirstIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PageFirstIcon.png" ) ); // NON-NLS
    pageUpIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PageUpIcon.png" ) ); // NON-NLS
    pageDownIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PageDownIcon.png" ) ); // NON-NLS
    pageLastIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PageLastIcon.png" ) ); // NON-NLS

    aboutIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/AboutIcon.png" ) ); // NON-NLS
    aboutDialogPicture = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/splash/SplashScreen.png" ) ); // NON-NLS

    generalSettingsIcon32 = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/GeneralSettingsIcon32.png" ) ); // NON-NLS
    browserIcon32 = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/BrowserIcon32.png" ) ); // NON-NLS
    networkIcon32 = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/NetworkIcon32.png" ) ); // NON-NLS
    externalToolsIcon32 = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ExternalToolsIcon32.png" ) ); // NON-NLS

    cutIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/CutIcon.png" ) ); // NON-NLS
    copyIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/CopyIcon.png" ) ); // NON-NLS
    pasteIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/PasteIcon.png" ) ); // NON-NLS
    visitOnlineForumIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/OnlineForumIcon.png" ) ); // NON-NLS

    selectionEdge = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SelectionEdge1.png" ) ); // NON-NLS
    dontShowInLayoutGUISelectionEdge = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SelectionEdge2.png" ) ); // NON-NLS

    newIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/NewIcon.png" ) ); // NON-NLS
    openIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/OpenIcon.png" ) ); // NON-NLS
    saveIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/SaveIcon.png" ) ); // NON-NLS

    mergeIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/MergeIcon.png" ) ); // NON-NLS

    settingsIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AdvancedIcon.png" ) ); // NON-NLS

    selectAllIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SelectAllIcon.png" ) ); // NON-NLS
    deselectAllIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DeselectAllIcon.png" ) ); // NON-NLS

    layerUpIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/LayerUpIcon.png" ) ); // NON-NLS
    layerDownIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/LayerDownIcon.png" ) ); // NON-NLS

    zoomIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/ZoomIcon.png" ) ); // NON-NLS
    zoomOverlay50Icon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ZoomOverlay50Icon.png" ) ); // NON-NLS
    zoomOverlay100Icon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ZoomOverlay100Icon.png" ) ); // NON-NLS
    zoomOverlay200Icon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ZoomOverlay200Icon.png" ) ); // NON-NLS
    zoomOverlay400Icon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ZoomOverlay400Icon.png" ) ); // NON-NLS

    layoutBandsIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/LayoutBandsIcon.png" ) ); // NON-NLS

    exportXMLIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ExportXMLIcon.png" ) ); // NON-NLS

    createReportIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/CreateReportIcon.png" ) ); // NON-NLS

    previewPDFIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PreviewPDFIcon.png" ) ); // NON-NLS
    previewHTMLIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PreviewHTMLIcon.png" ) ); // NON-NLS
    previewRTFIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PreviewRTFIcon.png" ) ); // NON-NLS
    previewXLSIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PreviewXLSIcon.png" ) ); // NON-NLS
    previewCSVIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PreviewCSVIcon.png" ) ); // NON-NLS
    previewXMLIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/PreviewXMLIcon.png" ) ); // NON-NLS

    alignLeftIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AlignLeftIcon.png" ) ); // NON-NLS
    alignCenterIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AlignCenterIcon.png" ) ); // NON-NLS
    alignRightIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AlignRightIcon.png" ) ); // NON-NLS
    alignTopIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AlignTopIcon.png" ) ); // NON-NLS
    alignMiddleIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AlignMiddleIcon.png" ) ); // NON-NLS
    alignBottomIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/AlignBottomIcon.png" ) ); // NON-NLS

    distributeLeftIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeLeftIcon.png" ) ); // NON-NLS
    distributeCenterIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeCenterIcon.png" ) ); // NON-NLS
    distributeRightIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeRightIcon.png" ) ); // NON-NLS
    distributeGapsHorizontalIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeGapsHorizontalIcon.png" ) ); // NON-NLS
    distributeTopIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeTopIcon.png" ) ); // NON-NLS
    distributeMiddleIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeMiddleIcon.png" ) ); // NON-NLS
    distributeBottomIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeBottomIcon.png" ) ); // NON-NLS
    distributeGapsVerticalIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/DistributeGapsVerticalIcon.png" ) ); // NON-NLS


    groupIcon = new ImageIcon( IconLoader.class.getResource
      ( "/org/pentaho/reporting/designer/core/icons/GroupIcon.png" ) ); // NON-NLS
    sortAscendingIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SortAscendingIcon.png" ) ); // NON-NLS
    sortDescendingIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SortDescendingIcon.png" ) ); // NON-NLS
    snapToElementsIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SnapToElementsIcon.png" ) ); // NON-NLS
    openFolderIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/OpenFolder.png" ) ); // NON-NLS

    boldCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/BoldCommand.png" ) ); // NON-NLS
    italicCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ItalicCommand.png" ) ); // NON-NLS
    underlineCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/UnderlineCommand.png" ) ); // NON-NLS

    textAlignCenterCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/TextAlignCenter.png" ) ); // NON-NLS
    textAlignLeftCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/TextAlignLeft.png" ) ); // NON-NLS
    textAlignRightCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/TextAlignRight.png" ) ); // NON-NLS
    textAlignJustifyCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/TextAlignJustify.png" ) ); // NON-NLS
    colorChooserCommand = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ColorChooserCommand.png" ) ); // NON-NLS

    samplesFolderClosed = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SampleFolderClosed.png" ) ); // NON-NLS
    samplesFolderOpened = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SampleFolderOpened.png" ) ); // NON-NLS

    genericSquare = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/generic_square_16.png" ) ); // NON-NLS
    genericSquareDisabled = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/generic_square_16_disabled.png" ) ); // NON-NLS

    addIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/Add.png" ) ); // NON-NLS

    editIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/Edit.png" ) ); // NON-NLS

    removeIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/Remove.png" ) ); // NON-NLS

    previewIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/preview.png" ) ); // NON-NLS

    parameterIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/param.png" ) ); // NON-NLS

    functionIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/function.gif" ) );  // NON-NLS

    templateColoredSample = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/TemplateColoredSampleIcon.png" ) ); // NON-NLS

    chevronRight = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ChevronRight.png" ) ); // NON-NLS

    chevronDown = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/ChevronDown.png" ) ); // NON-NLS

    blankDocumentIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/BlankDocumentIcon.png" ) ); // NON-NLS

    templateDocumentIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/TemplateDocumentIcon.png" ) ); // NON-NLS

    wizardDocumentIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/WizardDocumentIcon.png" ) ); // NON-NLS

    rubberbandSelectionIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/select.png" ) ); // NON-NLS

    productIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/pentaho_reporting.png" ) ); // NON-NLS

    emptyIcon = new ImageIcon( createTransparentImage( 16, 16 ) );

    crosstabBandSelectionIcon = new ImageIcon( IconLoader.class.getResource(
      "/org/pentaho/reporting/designer/core/icons/SelectAllIcon.png" ) ); // NON-NLS
  }


  /**
   * Creates a transparent image.  These can be used for aligning menu items.
   *
   * @param width  the width.
   * @param height the height.
   * @return the created transparent image.
   */
  private BufferedImage createTransparentImage( final int width,
                                                final int height ) {
    final BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
    final int[] data = img.getRGB( 0, 0, width, height, null, 0, width );
    img.setRGB( 0, 0, width, height, data, 0, width );
    return img;
  }

  public ImageIcon getEmptyIcon() {
    return emptyIcon;
  }

  public ImageIcon getHyperlinkIcon() {
    return hyperlinkIcon;
  }

  public ImageIcon getProductIcon() {
    return productIcon;
  }

  public ImageIcon getOpenFolderIcon() {
    return openFolderIcon;
  }


  public ImageIcon getFowardArrowIcon() {
    return fowardArrowIcon;
  }

  public ImageIcon getMoveUpIcon() {
    return moveUpIcon;
  }

  public ImageIcon getMoveDownIcon() {
    return moveDownIcon;
  }

  public ImageIcon getDistributeBottomIcon() {
    return distributeBottomIcon;
  }


  public ImageIcon getDistributeGapsVerticalIcon() {
    return distributeGapsVerticalIcon;
  }


  public ImageIcon getDistributeMiddleIcon() {
    return distributeMiddleIcon;
  }


  public ImageIcon getDistributeTopIcon() {
    return distributeTopIcon;
  }


  public ImageIcon getDistributeCenterIcon() {
    return distributeCenterIcon;
  }


  public ImageIcon getDistributeGapsHorizontalIcon() {
    return distributeGapsHorizontalIcon;
  }


  public ImageIcon getDistributeLeftIcon() {
    return distributeLeftIcon;
  }


  public ImageIcon getDistributeRightIcon() {
    return distributeRightIcon;
  }


  public ImageIcon getAlignCenterIcon() {
    return alignCenterIcon;
  }


  public ImageIcon getAlignLeftIcon() {
    return alignLeftIcon;
  }


  public ImageIcon getAlignRightIcon() {
    return alignRightIcon;
  }


  public ImageIcon getAlignTopIcon() {
    return alignTopIcon;
  }


  public ImageIcon getAlignMiddleIcon() {
    return alignMiddleIcon;
  }


  public ImageIcon getAlignBottomIcon() {
    return alignBottomIcon;
  }


  public ImageIcon getPreviewPDFIcon() {
    return previewPDFIcon;
  }


  public ImageIcon getPreviewRTFIcon() {
    return previewRTFIcon;
  }


  public ImageIcon getPreviewXLSIcon() {
    return previewXLSIcon;
  }


  public ImageIcon getPreviewCSVIcon() {
    return previewCSVIcon;
  }


  public ImageIcon getPreviewXMLIcon() {
    return previewXMLIcon;
  }


  public ImageIcon getPreviewHTMLIcon() {
    return previewHTMLIcon;
  }


  public ImageIcon getExternalToolsIcon32() {
    return externalToolsIcon32;
  }


  public ImageIcon getCreateReportIcon() {
    return createReportIcon;
  }


  public ImageIcon getExportXMLIcon() {
    return exportXMLIcon;
  }


  public ImageIcon getLayoutBandsIcon() {
    return layoutBandsIcon;
  }


  public ImageIcon getZoomOverlay50Icon() {
    return zoomOverlay50Icon;
  }


  public ImageIcon getZoomOverlay100Icon() {
    return zoomOverlay100Icon;
  }


  public ImageIcon getZoomOverlay200Icon() {
    return zoomOverlay200Icon;
  }


  public ImageIcon getZoomOverlay400Icon() {
    return zoomOverlay400Icon;
  }


  public ImageIcon getLayerUpIcon() {
    return layerUpIcon;
  }


  public ImageIcon getLayerDownIcon() {
    return layerDownIcon;
  }


  public ImageIcon getSelectAllIcon() {
    return selectAllIcon;
  }


  public ImageIcon getDeselectAllIcon() {
    return deselectAllIcon;
  }


  public ImageIcon getMergeIcon() {
    return mergeIcon;
  }


  public ImageIcon getSettingsIcon() {
    return settingsIcon;
  }


  public ImageIcon getNewIcon() {
    return newIcon;
  }


  public ImageIcon getOpenIcon() {
    return openIcon;
  }


  public ImageIcon getSaveIcon() {
    return saveIcon;
  }


  public ImageIcon getPropertiesDataSetIcon() {
    return propertiesDataSetIcon;
  }


  public ImageIcon getSelectionEdge() {
    return selectionEdge;
  }


  public ImageIcon getDontShowInLayoutGUISelectionEdge() {
    return dontShowInLayoutGUISelectionEdge;
  }


  public ImageIcon getCutIcon() {
    return cutIcon;
  }


  public ImageIcon getCopyIcon() {
    return copyIcon;
  }


  public ImageIcon getPasteIcon() {
    return pasteIcon;
  }

  public ImageIcon getPageFirstIcon() {
    return pageFirstIcon;
  }


  public ImageIcon getPageUpIcon() {
    return pageUpIcon;
  }


  public ImageIcon getPageDownIcon() {
    return pageDownIcon;
  }


  public ImageIcon getPageLastIcon() {
    return pageLastIcon;
  }


  public ImageIcon getReportFrameIcon() {
    return reportFrameIcon;
  }

  public ImageIcon getPaletteIcon() {
    return paletteIcon;
  }


  public ImageIcon getMessagesIcon() {
    return messagesIcon;
  }


  public ImageIcon getPropertyTableIcon() {
    return propertyTableIcon;
  }


  public ImageIcon getReportTreeIcon() {
    return reportTreeIcon;
  }


  public ImageIcon getDrawSelectionTypeClampIcon() {
    return drawSelectionTypeClampIcon;
  }


  public ImageIcon getDrawSelectionTypeOutlineIcon() {
    return drawSelectionTypeOutlineIcon;
  }


  public ImageIcon getInfoIcon() {
    return infoIcon;
  }


  public ImageIcon getWarningIcon() {
    return warningIcon;
  }


  public ImageIcon getErrorIcon() {
    return errorIcon;
  }

  public ImageIcon getFunctionsIcon() {
    return functionsIcon;
  }

  public ImageIcon getUndoIconSmall() {
    return undoIconSmall;
  }


  public ImageIcon getRedoIconSmall() {
    return redoIconSmall;
  }


  public ImageIcon getDeleteIconSmall() {
    return deleteIconSmall;
  }


  public ImageIcon getDataSetsIcon() {
    return dataSetsIcon;
  }


  public ImageIcon getZoomIcon() {
    return zoomIcon;
  }


  public ImageIcon getReportWizardIcon() {
    return reportWizardIcon;
  }


  public ImageIcon getAboutIcon() {
    return aboutIcon;
  }


  public ImageIcon getAboutDialogPicture() {
    return aboutDialogPicture;
  }


  public ImageIcon getNoErrorIcon() {
    return noErrorIcon;
  }


  public ImageIcon getGeneralSettingsIcon32() {
    return generalSettingsIcon32;
  }


  public ImageIcon getBrowserIcon32() {
    return browserIcon32;
  }


  public ImageIcon getNetworkIcon32() {
    return networkIcon32;
  }


  public ImageIcon getRefreshIcon() {
    return refreshIcon;
  }


  public ImageIcon getVisitOnlineForumIcon() {
    return visitOnlineForumIcon;
  }


  public ImageIcon getShowLogIcon() {
    return newIcon;
  }

  public ImageIcon getGroupIcon() {
    return groupIcon;
  }


  public ImageIcon getSortAscendingIcon() {
    return sortAscendingIcon;
  }


  public ImageIcon getSortDescendingIcon() {
    return sortDescendingIcon;
  }

  public ImageIcon getSnapToElementsIcon() {
    return snapToElementsIcon;
  }

  public ImageIcon getBoldCommand() {
    return boldCommand;
  }

  public ImageIcon getSelectCrosstabBandCommand() {
    return crosstabBandSelectionIcon;
  }

  public ImageIcon getItalicCommand() {
    return italicCommand;
  }


  public ImageIcon getUnderlineCommand() {
    return underlineCommand;
  }


  public ImageIcon getTextAlignLeftCommand() {
    return textAlignLeftCommand;
  }


  public ImageIcon getTextAlignRightCommand() {
    return textAlignRightCommand;
  }


  public ImageIcon getTextAlignCenterCommand() {
    return textAlignCenterCommand;
  }

  public ImageIcon getTextAlignJustifyCommand() {
    return textAlignJustifyCommand;
  }


  public ImageIcon getColorChooserCommand() {
    return colorChooserCommand;
  }


  public ImageIcon getSamplesFolderClosed() {
    return samplesFolderClosed;
  }


  public ImageIcon getSamplesFolderOpened() {
    return samplesFolderOpened;
  }

  public ImageIcon getGenericSquare() {
    return genericSquare;
  }

  public ImageIcon getGenericSquareDisabled() {
    return genericSquareDisabled;
  }

  public ImageIcon getAddIcon() {
    return addIcon;
  }

  public ImageIcon getEditIcon() {
    return editIcon;
  }

  public ImageIcon getRemoveIcon() {
    return removeIcon;
  }

  public ImageIcon getPreviewIcon() {
    return previewIcon;
  }

  public ImageIcon getParameterIcon() {
    return parameterIcon;
  }

  public ImageIcon getFunctionIcon() {
    return functionIcon;
  }


  public ImageIcon getTemplateColoredSample() {
    return templateColoredSample;
  }


  public ImageIcon getChevronRight() {
    return chevronRight;
  }


  public ImageIcon getChevronDown() {
    return chevronDown;
  }

  public ImageIcon getBlankDocumentIcon() {
    return blankDocumentIcon;
  }

  public ImageIcon getTemplateDocumentIcon() {
    return templateDocumentIcon;
  }

  public ImageIcon getWizardDocumentIcon() {
    return wizardDocumentIcon;
  }

  public ImageIcon getRubberbandSelectionIcon() {
    return rubberbandSelectionIcon;
  }

}
