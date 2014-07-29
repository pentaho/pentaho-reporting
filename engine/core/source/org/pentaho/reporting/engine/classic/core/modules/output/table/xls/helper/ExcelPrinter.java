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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ImageUtils;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawableRotatedComponent;
import org.pentaho.reporting.engine.classic.core.util.RotationUtils;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public class ExcelPrinter
{
  private static final Log logger = LogFactory.getLog(ExcelPrinter.class);

  private InputStream templateInputStream;
  private OutputStream outputStream;
  private Workbook workbook;
  private HashMap<String,Integer> sheetNamesCount;
  private double scaleFactor;
  private Configuration config;
  private OutputProcessorMetaData metaData;
  private ResourceManager resourceManager;
  private Sheet sheet;
  private Drawing patriarch;
  private HSSFCellStyleProducer cellStyleProducer;
  private CellBackgroundProducer cellBackgroundProducer;
  private ExcelTextExtractor textExtractor;
  private ExcelColorProducer colorProducer;
  private ExcelColorProducer fontColorProducer;
  private boolean useXlsxFormat;
  private int sheetFreezeTop;
  private int sheetFreezeLeft;

  public ExcelPrinter(final OutputStream outputStream, final ResourceManager resourceManager)
  {
    if (outputStream == null)
    {
      throw new NullPointerException();
    }
    if (resourceManager == null)
    {
      throw new NullPointerException();
    }
    this.outputStream = outputStream;
    this.resourceManager = resourceManager;
    this.sheetNamesCount = new HashMap<String,Integer>();
  }

  public boolean isUseXlsxFormat()
  {
    return useXlsxFormat;
  }

  public void setUseXlsxFormat(final boolean useXlsxFormat)
  {
    this.useXlsxFormat = useXlsxFormat;
  }

  public boolean isInitialized()
  {
    return metaData != null;
  }

  public void init(final OutputProcessorMetaData metaData)
  {
    if (metaData == null)
    {
      throw new NullPointerException();
    }

    this.metaData = metaData;
    this.config = metaData.getConfiguration();
    this.cellBackgroundProducer = new CellBackgroundProducer
        (metaData.isFeatureSupported(AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE),
            metaData.isFeatureSupported(OutputProcessorFeature.UNALIGNED_PAGEBANDS));

    try
    {
      final String scaleFactorText = config.getConfigProperty
          ("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.CellWidthScaleFactor");
      if (scaleFactorText == null)
      {
        scaleFactor = 50;
      }
      else
      {
        scaleFactor = Double.parseDouble(scaleFactorText);
      }
    }
    catch (Exception e)
    {
      this.scaleFactor = 50;
    }
  }

  public InputStream getTemplateInputStream()
  {
    return templateInputStream;
  }

  public void setTemplateInputStream(final InputStream templateInputStream)
  {
    this.templateInputStream = templateInputStream;
  }

  private String makeUnique(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }

    final Integer count = sheetNamesCount.get(name);
    if (count == null)
    {
      sheetNamesCount.put(name, IntegerCache.getInteger(1));
      return name;
    }

    final int value = count.intValue() + 1;
    sheetNamesCount.put(name, IntegerCache.getInteger(value));
    return makeUnique(name + ' ' + value);
  }

  private boolean isValidSheetName(final String sheetname)
  {
    if ((sheetname.indexOf('/') > -1)
        || (sheetname.indexOf('\\') > -1)
        || (sheetname.indexOf('?') > -1)
        || (sheetname.indexOf('*') > -1)
        || (sheetname.indexOf(']') > -1)
        || (sheetname.indexOf('[') > -1)
        || (sheetname.indexOf(':') > -1))
    {
      return false;
    }

    return true;
  }

  private Cell getCellAt(final int x, final int y)
  {
    final Row row = getRowAt(y);
    final Cell cell = row.getCell(x);
    if (cell != null)
    {
      return cell;
    }
    return row.createCell(x);
  }

  private Row getRowAt(final int y)
  {
    final Row row = sheet.getRow(y);
    if (row != null)
    {
      return row;
    }
    return sheet.createRow(y);
  }

  private boolean isHeaderFooterValid(final String left, final String center, final String right)
  {
    int length = 0;
    if (left != null)
    {
      length += left.length();
    }
    if (center != null)
    {
      length += center.length();
    }
    if (right != null)
    {
      length += right.length();
    }
    return length < 255;
  }

  public void print(final LogicalPageKey logicalPageKey,
                    final LogicalPageBox logicalPage,
                    final TableContentProducer contentProducer,
                    final boolean incremental)
  {
    if (workbook == null)
    {
      workbook = createWorkbook();

      if (workbook instanceof HSSFWorkbook)
      {
        final boolean dynamicColors = "true".equals
            (config.getConfigProperty(
                "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.DynamicColors"));
        if (dynamicColors)
        {
          final HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
          colorProducer = new DynamicExcelColorProducer(hssfWorkbook);
        }
        else
        {
          colorProducer = new StaticExcelColorSupport();
        }
        fontColorProducer = colorProducer;
      }
      else
      {
        colorProducer = new XSSFExcelColorProducer();
        fontColorProducer = new StaticExcelColorSupport();
      }

      this.textExtractor = new ExcelTextExtractor(metaData, colorProducer);

      final boolean hardLimit = "true".equals(config.getConfigProperty
          ("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.HardStyleCountLimit"));
      cellStyleProducer = new HSSFCellStyleProducer(workbook, hardLimit, colorProducer, fontColorProducer);
    }

    final int startRow = contentProducer.getFinishedRows();
    final int finishRow = contentProducer.getFilledRows();
    if (incremental && startRow == finishRow)
    {
      return;
    }

    if (sheet == null)
    {
      sheet = openSheet(contentProducer.getSheetName());
      final ExcelTableContentProducer excelTableContentProducer = (ExcelTableContentProducer) contentProducer;
      final String pageHeaderCenter = excelTableContentProducer.getPageHeaderCenter();
      final String pageFooterCenter = excelTableContentProducer.getPageFooterCenter();
      final String pageHeaderLeft = excelTableContentProducer.getPageHeaderLeft();
      final String pageFooterLeft = excelTableContentProducer.getPageFooterLeft();
      final String pageHeaderRight = excelTableContentProducer.getPageHeaderRight();
      final String pageFooterRight = excelTableContentProducer.getPageFooterRight();
      this.sheetFreezeTop = excelTableContentProducer.getFreezeTop();
      this.sheetFreezeLeft = excelTableContentProducer.getFreezeLeft();

      if (isHeaderFooterValid(pageHeaderLeft, pageHeaderCenter, pageHeaderRight))
      {
        if (pageHeaderLeft != null)
        {
          sheet.getHeader().setLeft(pageHeaderLeft);
        }
        if (pageHeaderCenter != null)
        {
          sheet.getHeader().setCenter(pageHeaderCenter);
        }
        if (pageHeaderRight != null)
        {
          sheet.getHeader().setRight(pageHeaderRight);
        }
      }
      else
      {
        ExcelPrinter.logger.warn(
            "Page-Header exceeds the maximum length of 255 characters. No page-header will be added to the sheet.");
      }
      if (isHeaderFooterValid(pageFooterLeft, pageFooterCenter, pageFooterRight))
      {
        if (pageFooterCenter != null)
        {
          sheet.getFooter().setCenter(pageFooterCenter);
        }
        if (pageFooterLeft != null)
        {
          sheet.getFooter().setLeft(pageFooterLeft);
        }
        if (pageFooterRight != null)
        {
          sheet.getFooter().setRight(pageFooterRight);
        }
      }
      else
      {
        ExcelPrinter.logger.warn(
            "Page-Footer exceeds the maximum length of 255 characters. No page-footer will be added to the sheet.");
      }

      // Start a new page.
      final PhysicalPageBox page = logicalPage.getPageGrid().getPage(0, 0);
      configureSheet(page);

      // Set column widths ..
      final SheetLayout sheetLayout = contentProducer.getSheetLayout();
      final int columnCount = contentProducer.getColumnCount();
      for (int col = 0; col < columnCount; col++)
      {
        final double cellWidth = StrictGeomUtility.toExternalValue(sheetLayout.getCellWidth(col, col + 1));
        final double poiCellWidth = (cellWidth * scaleFactor);
        sheet.setColumnWidth(col, Math.min(255 * 256, (int) poiCellWidth));
      }
    }

    // and finally the content ..
    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    final int colCount = sheetLayout.getColumnCount();

    for (int row = startRow; row < finishRow; row++)
    {
      final Row hssfRow = getRowAt(row);
      final double lastRowHeight = StrictGeomUtility.toExternalValue(sheetLayout.getRowHeight(row));
      hssfRow.setHeightInPoints((float) (lastRowHeight));

      for (int col = 0; col < colCount; col++)
      {
        final CellMarker.SectionType sectionType = contentProducer.getSectionType(row, col);
        final RenderBox content = contentProducer.getContent(row, col);

        if (content == null)
        {
          final RenderBox backgroundBox = contentProducer.getBackground(row, col);
          final CellBackground background;
          if (backgroundBox != null)
          {
            background = cellBackgroundProducer.getBackgroundForBox
                (logicalPage, sheetLayout, col, row, 1, 1, true, sectionType, backgroundBox);
          }
          else
          {
            background = cellBackgroundProducer.getBackgroundAt(logicalPage, sheetLayout, col, row, true, sectionType);
          }
          if (background == null)
          {
            if (row == 0 && col == 0)
            {
              // create a single cell, so that we dont run into nullpointer inside POI..
              getCellAt(col, row);
            }
            // An empty cell .. ignore
            continue;
          }

          // A empty cell with a defined background ..
          final Cell cell = getCellAt(col, row);
          final CellStyle style = cellStyleProducer.createCellStyle(null, background);
          if (style != null)
          {
            cell.setCellStyle(style);
          }
          continue;
        }

        if (content.isCommited() == false)
        {
          throw new InvalidReportStateException("Uncommited content encountered");
        }

        final long contentOffset = contentProducer.getContentOffset(row, col);
        final TableRectangle rectangle = sheetLayout.getTableBounds
            (content.getX(), content.getY() + contentOffset,
                content.getWidth(), content.getHeight(), null);
        if (rectangle.isOrigin(col, row) == false)
        {
          // A spanned cell ..
          continue;
        }

        final CellBackground fastBackground = cellBackgroundProducer.getBackgroundForBox
            (logicalPage, sheetLayout, rectangle.getX1(), rectangle.getY1(),
                rectangle.getColumnSpan(), rectangle.getRowSpan(), false, sectionType, content);
        // export the cell and all content ..

        final Cell cell = getCellAt(col, row);
        final CellStyle style = cellStyleProducer.createCellStyle(content, fastBackground);
        if (style != null)
        {
          cell.setCellStyle(style);
        }

        if (applyCellValue(metaData, content, cell, sheetLayout, rectangle, contentOffset))
        {
          // TODO: better rotation integration with code
          mergeCellRegion(rectangle, row, col, sheetLayout, logicalPage, content, contentProducer,
              cell.getCellStyle().getRotation());
        }

        content.setFinishedTable(true);
      }

    }

    if (incremental == false)
    {
      // cleanup ..
      patriarch = null;
      sheet = null;
    }
  }

  private void mergeCellRegion(final TableRectangle rectangle,
                               final int row,
                               final int col,
                               final SheetLayout sheetLayout,
                               final LogicalPageBox logicalPage,
                               final RenderBox content,
                               final TableContentProducer contentProducer, final short rotation)
  {
    if (content == null)
    {
      throw new NullPointerException();
    }

    final int rowSpan = rectangle.getRowSpan();
    final int columnSpan = rectangle.getColumnSpan();
    if (rowSpan <= 1 && columnSpan <= 1)
    {
      return;
    }

    sheet.addMergedRegion(new CellRangeAddress(row, (row + rowSpan - 1), col, (col + columnSpan - 1)));
    final int rectX = rectangle.getX1();
    final int rectY = rectangle.getY1();

    for (int spannedRow = 0; spannedRow < rowSpan; spannedRow += 1)
    {
      for (int spannedCol = 0; spannedCol < columnSpan; spannedCol += 1)
      {
        final CellMarker.SectionType sectionType = contentProducer.getSectionType(row, col);
        final CellBackground bg = cellBackgroundProducer.getBackgroundForBox
            (logicalPage, sheetLayout, rectX + spannedCol, rectY + spannedRow, 1, 1, false, sectionType, content);
        final Cell regionCell = getCellAt((col + spannedCol), row + spannedRow);
        final CellStyle spannedStyle = cellStyleProducer.createCellStyle(content, bg);
        if (spannedStyle != null)
        {
          //TODO: integrate rotation better with stylesheet keys and HSSFCellStyleProducer
          spannedStyle.setRotation( rotation );
          regionCell.setCellStyle(spannedStyle);
        }
      }
    }
  }

  /**
   * Applies the cell value and determines whether the cell should be merged. Merging will only take place if the cell
   * has a row or colspan greater than one. Images will never be merged, as image content is rendered into an anchored
   * frame on top of the cells.
   *
   * @param content
   * @param cell
   * @param sheetLayout
   * @param rectangle
   * @return true, if the cell may to be put into a merged region, false otherwise.
   */
  private boolean applyCellValue(final OutputProcessorMetaData metaData,
                                 final RenderBox content,
                                 final Cell cell,
                                 final SheetLayout sheetLayout,
                                 final TableRectangle rectangle,
                                 final long contentOffset)
  {
    final Object value = textExtractor.compute(content, cellStyleProducer.getFontFactory(), workbook.getCreationHelper());

    if (value instanceof Image)
    {
      try
      {
        final ImageContainer imageContainer = new DefaultImageReference((Image) value);
        final RenderNode rawSource = textExtractor.getRawSource();
        final StrictBounds contentBounds =
            new StrictBounds(content.getX(), content.getY() + contentOffset, content.getWidth(), content.getHeight());
        createImageCell(rawSource, imageContainer, sheetLayout, rectangle, contentBounds);
      }
      catch (IOException ioe)
      {
        // Should not happen.
        ExcelPrinter.logger.warn("Failed to process AWT-Image in Excel-Export", ioe);
      }
      return false;
    }
    else if (value instanceof ImageContainer)
    {
      final ImageContainer imageContainer = (ImageContainer) value;
      // todo: this is wrong ..
      final RenderNode rawSource = textExtractor.getRawSource();
      final StrictBounds contentBounds =
          new StrictBounds(content.getX(), content.getY() + contentOffset, content.getWidth(), content.getHeight());
      createImageCell(rawSource, imageContainer, sheetLayout, rectangle, contentBounds);
      return false;
    }
    else if (value instanceof DrawableWrapper)
    {
      final DrawableWrapper drawable = (DrawableWrapper) value;
      final RenderNode rawSource = textExtractor.getRawSource();
      final StrictBounds contentBounds = new StrictBounds
          (rawSource.getX(), rawSource.getY() + contentOffset, rawSource.getWidth(), rawSource.getHeight());
      final ImageContainer imageFromDrawable =
          RenderUtility.createImageFromDrawable(drawable, contentBounds, content, metaData);
      createImageCell(rawSource, imageFromDrawable, sheetLayout, rectangle, contentBounds);
      return false;
    }
    else if (value instanceof Shape)
    {
      // We *could* do this as well ... but for now we dont.
      return false;
    }


    final String linkTarget = (String) content.getStyleSheet().getStyleProperty(ElementStyleKeys.HREF_TARGET);
    if (linkTarget != null)
    {
      // this may be wrong if we have quotes inside. We should escape them ..
      final String formula = "HYPERLINK(" + excelFormulaSplitAndQuote(linkTarget) +
          "," + excelFormulaSplitAndQuote(textExtractor.getText()) + ")";
      if (formula.length() < 1024)
      {
        cell.setCellFormula(formula);
        return true;
      }

      ExcelPrinter.logger.warn(
          "Excel-Cells cannot contain formulas longer than 1023 characters. Converting hyperlink into plain text");
    }

    final Object attr1 = content.getAttributes().getAttribute(AttributeNames.Excel.NAMESPACE,
        AttributeNames.Excel.FIELD_FORMULA);
    if (attr1 != null)
    {
      final String formula = String.valueOf(attr1);
      if (formula.length() < 1024)
      {
        cell.setCellFormula(formula);
        return true;
      }

      ExcelPrinter.logger.warn(
          "Excel-Cells cannot contain formulas longer than 1023 characters. Converting excel formula into plain text");
    }

    if( RotationUtils.hasRotation( content ) ){
      ReportDrawableRotatedComponent.drawExcel( cell, new Float( RotationUtils.getRotation( content ) ).intValue() );
    }
    if (value instanceof RichTextString)
    {
      cell.setCellValue((RichTextString) value);
    }
    else if (value instanceof Date)
    {
      cell.setCellValue((Date) value);
    }
    else if (value instanceof Number)
    {
      final Number number = (Number) value;
      cell.setCellValue(number.doubleValue());
    }
    else if (value instanceof Boolean)
    {
      cell.setCellValue(Boolean.TRUE.equals(value));
    }
    else // Something we can't handle.
    {
      if (value == null)
      {
        cell.setCellType(Cell.CELL_TYPE_BLANK);
      }
      else
      {
        cell.setCellValue(String.valueOf(value));
      }
    }
    return true;
  }

  private String excelFormulaSplitAndQuote(final String s)
  {
    final StringBuilder b = new StringBuilder();
    b.append('"');
    final char[] chars = s.toCharArray();
    int count = 0;
    for (int i = 0; i < chars.length; i++)
    {
      final char c = chars[i];
      if (c == '"')
      {
        b.append('"');
        b.append('"');
        count += 2;
      }
      else
      {
        b.append(c);
        count += 1;
      }
      if (count > 252)
      {
        count = 0;
        b.append("\" & \"");
      }
    }
    b.append('"');
    return b.toString();
  }

  private void configureSheet(final PhysicalPageBox page)
  {
    // make sure a new patriarch is created if needed.
    patriarch = null;

    final String paper = config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.Paper");
    final String orientation = config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PaperOrientation");
    final short scale = (short) ParserUtil.parseInt
        (config.getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintScaleFactor"), 100);
    final short hres = (short) ParserUtil.parseInt
        (config.getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintHorizontalResolution"), -1);
    final short vres = (short) ParserUtil.parseInt
        (config.getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintVerticalResolution"), -1);
    final boolean noColors = "true".equals
        (config.getConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintNoColors"));
    final boolean notes = "true".equals
        (config.getConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintNotes"));
    final boolean usePage = "true".equals
        (config.getConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintUsePage"));
    final boolean draft = "true".equals
        (config.getConfigProperty("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.PrintDraft"));

    final PrintSetup printSetup = sheet.getPrintSetup();
    ExcelPrintSetupFactory.performPageSetup(printSetup, page, paper, orientation);
    printSetup.setScale(scale);
    printSetup.setNoColor(noColors);
    printSetup.setNotes(notes);
    printSetup.setUsePage(usePage);
    if (hres > 0)
    {
      printSetup.setHResolution(hres);
    }
    if (vres > 0)
    {
      printSetup.setVResolution(vres);
    }
    printSetup.setDraft(draft);

    final boolean displayGridLines = "true".equals(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.GridLinesDisplayed"));
    final boolean printGridLines = "true".equals(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.xls.GridLinesPrinted"));
    sheet.setDisplayGridlines(displayGridLines);
    sheet.setPrintGridlines(printGridLines);

    if (sheetFreezeTop > 0 || sheetFreezeLeft > 0)
    {
      sheet.createFreezePane(sheetFreezeLeft, sheetFreezeTop);
    }
  }

  public void close()
  {
    final long start = System.currentTimeMillis();
    logger.info ("Closing workbook and writing content to disk.");
    if (workbook != null)
    {
      try
      {
        workbook.write(outputStream);
        // cleanup..
        patriarch = null;
        sheet = null;
        outputStream.flush();
      }
      catch (IOException e)
      {
        ExcelPrinter.logger.warn("could not write xls data. Message:", e);
      }
      finally
      {
        workbook = null;
      }
    }
    final long end = System.currentTimeMillis();
    logger.info ("Closing workbook finished in " + ((end - start) / 1000f) + "s");

  }

  protected Workbook createWorkbook()
  {
    // Not opened yet. Lets do this now.
    if (templateInputStream != null)
    {
      // do some preprocessing ..
      try
      {
        final Workbook workbook = WorkbookFactory.create(templateInputStream);

        // OK, we have a workbook, but we can't stop here..
        final int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++)
        {
          final String sheetName = workbook.getSheetName(i);
          // make sure that that name is marked as used ..
          makeUnique(sheetName);
        }

        return workbook;
      }
      catch (IOException e)
      {
        ExcelPrinter.logger.warn("Unable to read predefined xls-data.", e);
      }
      catch (InvalidFormatException e)
      {
        ExcelPrinter.logger.warn("Unable to read predefined xls-data.", e);
      }
    }
    if (isUseXlsxFormat())
    {
      return new XSSFWorkbook();
    }
    else
    {
      return new HSSFWorkbook();
    }
  }

  private Sheet openSheet(final String sheetName)
  {
    if (sheetName == null)
    {
      return workbook.createSheet();
    }
    else
    {
      final String uniqueSheetname = makeUnique(sheetName);
      if (uniqueSheetname.length() == 0 || uniqueSheetname.length() > 31)
      {
        ExcelPrinter.logger.warn("A sheet name must not be empty and greater than 31 characters");
        return workbook.createSheet();
      }
      else if (isValidSheetName(uniqueSheetname) == false)
      {
        ExcelPrinter.logger.warn("A sheet name must not contain any of ':/\\*?[]'");
        // OpenOffice is even more restrictive and only allows Letters,
        // Digits, Spaces and the Underscore
        return workbook.createSheet();
      }
      else
      {
        return workbook.createSheet(uniqueSheetname);
      }
    }
  }


  /**
   * Produces the content for image or drawable cells. Excel does not support image-content in cells. Images are
   * rendered to an embedded OLE canvas instead, which is then positioned over the cell that would contain the image.
   *
   * @param contentNode   the render node that contains the image.
   * @param image         the image object
   * @param currentLayout the current sheet layout containing all row and column breaks
   * @param rectangle     the current cell in grid-coordinates
   * @param cellBounds    the bounds of the cell.
   */
  private void createImageCell(final RenderNode contentNode,
                               final ImageContainer image,
                               final SheetLayout currentLayout,
                               TableRectangle rectangle,
                               final StrictBounds cellBounds)
  {
    try
    {
      if (rectangle == null)
      {
        // there was an error while computing the grid-position for this
        // element. Evil me...
        ExcelPrinter.logger.debug("Invalid reference: I was not able to compute " +
            "the rectangle for the content.");
        return;
      }

      final StyleSheet layoutContext = contentNode.getStyleSheet();
      final boolean shouldScale = layoutContext.getBooleanStyleProperty(ElementStyleKeys.SCALE);

      final int imageWidth = image.getImageWidth();
      final int imageHeight = image.getImageHeight();
      if (imageWidth < 1 || imageHeight < 1)
      {
        return;
      }

      final double scaleFactor;
      final double devResolution = metaData.getNumericFeatureValue(OutputProcessorFeature.DEVICE_RESOLUTION);
      if (metaData.isFeatureSupported(OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING))
      {
        if (devResolution != 72.0 && devResolution > 0)
        {
          // Need to scale the device to its native resolution before attempting to draw the image..
          scaleFactor = (72.0 / devResolution);

        }
        else
        {
          scaleFactor = 1;
        }
      }
      else
      {
        scaleFactor = 1;
      }

      final ElementAlignment horizontalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty(ElementStyleKeys.ALIGNMENT);
      final ElementAlignment verticalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty(ElementStyleKeys.VALIGNMENT);

      final long internalImageWidth = StrictGeomUtility.toInternalValue(scaleFactor * imageWidth);
      final long internalImageHeight = StrictGeomUtility.toInternalValue(scaleFactor * imageHeight);

      final long cellWidth = cellBounds.getWidth();
      final long cellHeight = cellBounds.getHeight();

      final StrictBounds cb;
      final int pictureId;
      try
      {
        if (shouldScale)
        {
          final double scaleX;
          final double scaleY;

          final boolean keepAspectRatio = layoutContext.getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO);
          if (keepAspectRatio)
          {
            final double imgScaleFactor = Math.min(cellWidth / (double) internalImageWidth,
                cellHeight / (double) internalImageHeight);
            scaleX = imgScaleFactor;
            scaleY = imgScaleFactor;
          }
          else
          {
            scaleX = cellWidth / (double) internalImageWidth;
            scaleY = cellHeight / (double) internalImageHeight;
          }

          final long clipWidth = (long) (scaleX * internalImageWidth);
          final long clipHeight = (long) (scaleY * internalImageHeight);

          final long alignmentX = RenderUtility.computeHorizontalAlignment(horizontalAlignment, cellWidth, clipWidth);
          final long alignmentY = RenderUtility.computeVerticalAlignment(verticalAlignment, cellHeight, clipHeight);

          cb = new StrictBounds(cellBounds.getX() + alignmentX,
              cellBounds.getY() + alignmentY,
              Math.min(clipWidth, cellWidth),
              Math.min(clipHeight, cellHeight));

          // Recompute the cells that this image will cover (now that it has been resized)
          rectangle = currentLayout.getTableBounds(cb, rectangle);

          pictureId = loadImage(workbook, image);
          if (isUseXlsxFormat())
          {
            if (pictureId < 0)
            {
              return;
            }
          }
          else if (pictureId <= 0)
          {
            return;
          }
        }
        else
        {
          // unscaled ..
          if (internalImageWidth <= cellWidth &&
              internalImageHeight <= cellHeight)
          {
            // No clipping needed.
            final long alignmentX = RenderUtility.computeHorizontalAlignment
                (horizontalAlignment, cellBounds.getWidth(), internalImageWidth);
            final long alignmentY = RenderUtility.computeVerticalAlignment
                (verticalAlignment, cellBounds.getHeight(), internalImageHeight);

            cb = new StrictBounds(cellBounds.getX() + alignmentX,
                cellBounds.getY() + alignmentY,
                internalImageWidth,
                internalImageHeight);

            // Recompute the cells that this image will cover (now that it has been resized)
            rectangle = currentLayout.getTableBounds(cb, rectangle);

            pictureId = loadImage(workbook, image);
            if (isUseXlsxFormat())
            {
              if (pictureId < 0)
              {
                return;
              }
            }
            else if (pictureId <= 0)
            {
              return;
            }
          }
          else
          {
            // at least somewhere there is clipping needed.
            final long clipWidth = Math.min(cellWidth, internalImageWidth);
            final long clipHeight = Math.min(cellHeight, internalImageHeight);
            final long alignmentX = RenderUtility.computeHorizontalAlignment
                (horizontalAlignment, cellBounds.getWidth(), clipWidth);
            final long alignmentY = RenderUtility.computeVerticalAlignment
                (verticalAlignment, cellBounds.getHeight(), clipHeight);
            cb = new StrictBounds(cellBounds.getX() + alignmentX,
                cellBounds.getY() + alignmentY,
                clipWidth,
                clipHeight);

            // Recompute the cells that this image will cover (now that it has been resized)
            rectangle = currentLayout.getTableBounds(cb, rectangle);


            pictureId = loadImageWithClipping(workbook, image, clipWidth, clipHeight, scaleFactor);
            if (isUseXlsxFormat())
            {
              if (pictureId < 0)
              {
                return;
              }
            }
            else if (pictureId <= 0)
            {
              return;
            }
          }
        }
      }
      catch (UnsupportedEncoderException uee)
      {
        // should not happen, as PNG is always supported.
        logger.warn("Assertation-Failure: PNG encoding failed.", uee);
        return;
      }

      final int cell1x = rectangle.getX1();
      final int cell1y = rectangle.getY1();
      final int cell2x = Math.max(cell1x, rectangle.getX2());
      final int cell2y = Math.max(cell1y, rectangle.getY2());

      final long cell1width = currentLayout.getCellWidth(cell1x);
      final long cell1height = currentLayout.getRowHeight(cell1y);
      final long cell2width = currentLayout.getCellWidth(cell2x);
      final long cell2height = currentLayout.getRowHeight(cell2y);

      final long cell1xPos = currentLayout.getXPosition(cell1x);
      final long cell1yPos = currentLayout.getYPosition(cell1y);
      final long cell2xPos = currentLayout.getXPosition(cell2x);
      final long cell2yPos = currentLayout.getYPosition(cell2y);

      final int dx1 = (int)(1023 * ((cb.getX() - cell1xPos) / (double)cell1width));
      final int dy1 = (int)(255 * ((cb.getY() - cell1yPos) / (double)cell1height));
      final int dx2 = (int)(1023 * ((cb.getX() + cb.getWidth() - cell2xPos) / (double)cell2width));
      final int dy2 = (int)(255 * ((cb.getY() + cb.getHeight() - cell2yPos) / (double)cell2height));

      final ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
      anchor.setDx1(dx1);
      anchor.setDy1(dy1);
      anchor.setDx2(dx2);
      anchor.setDy2(dy2);
      anchor.setCol1(cell1x);
      anchor.setRow1(cell1y);
      anchor.setCol2(cell2x);
      anchor.setRow2(cell2y);
      anchor.setAnchorType(ClientAnchor.MOVE_DONT_RESIZE); // Move, but don't size

      if (patriarch == null)
      {
        patriarch = sheet.createDrawingPatriarch();
      }

      final Picture picture = patriarch.createPicture(anchor, pictureId);
      if(picture instanceof XSSFPicture)
      {
    	picture.resize();
      }
      ExcelPrinter.logger.info("Created image: " + pictureId + " => " + picture);
    }
    catch (IOException e)
    {
      ExcelPrinter.logger.warn("Failed to add image. Ignoring.", e);
    }
  }

  private int getImageFormat(final ResourceKey key)
  {
    final URL url = resourceManager.toURL(key);
    if (url == null)
    {
      return -1;
    }

    final String file = url.getFile();
    if (StringUtils.endsWithIgnoreCase(file, ".png")) // NON-NLS
    {
      return Workbook.PICTURE_TYPE_PNG;
    }
    if (StringUtils.endsWithIgnoreCase(file, ".jpg") || // NON-NLS
        StringUtils.endsWithIgnoreCase(file, ".jpeg")) // NON-NLS
    {
      return Workbook.PICTURE_TYPE_JPEG;
    }
    if (StringUtils.endsWithIgnoreCase(file, ".bmp") || // NON-NLS
        StringUtils.endsWithIgnoreCase(file, ".ico")) // NON-NLS
    {
      return Workbook.PICTURE_TYPE_DIB;
    }
    return -1;
  }

  private int loadImageWithClipping(final Workbook workbook,
                                    final ImageContainer reference,
                                    final long clipWidth,
                                    final long clipHeight,
                                    final double deviceScaleFactor)
      throws IOException, UnsupportedEncoderException
  {

    Image image = null;
    // The image has an assigned URL ...
    if (reference instanceof URLImageContainer)
    {
      final URLImageContainer urlImage = (URLImageContainer) reference;
      final ResourceKey url = urlImage.getResourceKey();
      // if we have an source to load the image data from ..
      if (url != null && urlImage.isLoadable())
      {
        if (reference instanceof LocalImageContainer)
        {
          final LocalImageContainer li = (LocalImageContainer) reference;
          image = li.getImage();
        }
        if (image == null)
        {
          try
          {
            final Resource resource = resourceManager.create(url, null, Image.class);
            image = (Image) resource.getResource();
          }
          catch (ResourceException e)
          {
            // ignore.
          }
        }
      }
    }

    if (reference instanceof LocalImageContainer)
    {
      // Check, whether the imagereference contains an AWT image.
      // if so, then we can use that image instance for the recoding
      final LocalImageContainer li = (LocalImageContainer) reference;
      if (image == null)
      {
        image = li.getImage();
      }
    }

    if (image != null)
    {
      // now encode the image. We don't need to create digest data
      // for the image contents, as the image is perfectly identifyable
      // by its URL
      return clipAndEncodeImage(workbook, image, clipWidth, clipHeight, deviceScaleFactor);
    }
    return -1;
  }

  private int clipAndEncodeImage(final Workbook workbook,
                                 final Image image,
                                 final long width,
                                 final long height,
                                 final double deviceScaleFactor) throws UnsupportedEncoderException, IOException
  {
    final int imageWidth = (int) StrictGeomUtility.toExternalValue(width);
    final int imageHeight = (int) StrictGeomUtility.toExternalValue(height);
    // first clip.
    final BufferedImage bi = ImageUtils.createTransparentImage(imageWidth, imageHeight);
    final Graphics2D graphics = (Graphics2D) bi.getGraphics();
    graphics.scale(deviceScaleFactor, deviceScaleFactor);

    if (image instanceof BufferedImage)
    {
      if (graphics.drawImage(image, null, null) == false)
      {
        ExcelPrinter.logger.debug("Failed to render the image. This should not happen for BufferedImages");
      }
    }
    else
    {
      final WaitingImageObserver obs = new WaitingImageObserver(image);
      obs.waitImageLoaded();

      while (graphics.drawImage(image, null, obs) == false)
      {
        obs.waitImageLoaded();
        if (obs.isError())
        {
          ExcelPrinter.logger.warn("Error while loading the image during the rendering.");
          break;
        }
      }
    }

    graphics.dispose();
    final byte[] data = RenderUtility.encodeImage(bi);
    return workbook.addPicture(data, Workbook.PICTURE_TYPE_PNG);
  }

  private int loadImage(final Workbook workbook, final ImageContainer reference)
      throws IOException, UnsupportedEncoderException
  {
    Image image = null;
    // The image has an assigned URL ...
    if (reference instanceof URLImageContainer)
    {
      final URLImageContainer urlImage = (URLImageContainer) reference;
      final ResourceKey url = urlImage.getResourceKey();
      // if we have an source to load the image data from ..
      if (url != null && urlImage.isLoadable())
      {
        // and the image is one of the supported image formats ...
        // we we can embedd it directly ...
        final int format = getImageFormat(url);
        if (format == -1)
        {
          // This is a unsupported image format.
          if (reference instanceof LocalImageContainer)
          {
            final LocalImageContainer li = (LocalImageContainer) reference;
            image = li.getImage();
          }
          if (image == null)
          {
            try
            {
              final Resource resource = resourceManager.create(url, null, Image.class);
              image = (Image) resource.getResource();
            }
            catch (ResourceException re)
            {
              ExcelPrinter.logger.info("Failed to load image from URL " + url, re);
            }
          }
        }
        else
        {
          try
          {
            final ResourceData data = resourceManager.load(url);
            // create the image
            return workbook.addPicture(data.getResource(resourceManager), format);
          }
          catch (ResourceException re)
          {
            ExcelPrinter.logger.info("Failed to load image from URL " + url, re);
          }

        }
      }
    }

    if (reference instanceof LocalImageContainer)
    {
      // Check, whether the imagereference contains an AWT image.
      // if so, then we can use that image instance for the recoding
      final LocalImageContainer li = (LocalImageContainer) reference;
      if (image == null)
      {
        image = li.getImage();
      }
    }

    if (image != null)
    {
      // now encode the image. We don't need to create digest data
      // for the image contents, as the image is perfectly identifyable
      // by its URL
      final byte[] data = RenderUtility.encodeImage(image);
      return workbook.addPicture(data, Workbook.PICTURE_TYPE_PNG);
    }
    return -1;
  }

}
