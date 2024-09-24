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
* Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.Dataset;
import org.jfree.ui.RectangleEdge;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.DynamicExpression;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.LegacyDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Stroke;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @noinspection UnusedDeclaration, JavaDoc
 */
public abstract class AbstractChartExpression extends AbstractExpression implements ChartExpression, DynamicExpression {
  public static final String LINE_STYLE_SOLID_STR = "solid"; //$NON-NLS-1$
  public static final String LINE_STYLE_DASH_STR = "dash"; //$NON-NLS-1$
  public static final String LINE_STYLE_DOT_STR = "dot"; //$NON-NLS-1$
  public static final String LINE_STYLE_DASHDOT_STR = "dashdot"; //$NON-NLS-1$
  public static final String LINE_STYLE_DASHDOTDOT_STR = "dashdotdot"; //$NON-NLS-1$

  protected static final Stroke EMPTY_STROKE = new BasicStroke( 0.0f );

  private static final Log logger = LogFactory.getLog( AbstractChartExpression.class );

  private static final Map<String, RectangleEdge> LEGEND_LOCATIONS;

  static {
    final Map<String, RectangleEdge> locations = new HashMap<String, RectangleEdge>();
    locations.put( "left", RectangleEdge.LEFT ); //$NON-NLS-1$
    locations.put( "west", RectangleEdge.LEFT ); //$NON-NLS-1$
    locations.put( "right", RectangleEdge.RIGHT ); //$NON-NLS-1$
    locations.put( "east", RectangleEdge.RIGHT ); //$NON-NLS-1$
    locations.put( "top", RectangleEdge.TOP ); //$NON-NLS-1$
    locations.put( "north", RectangleEdge.TOP ); //$NON-NLS-1$
    locations.put( "bottom", RectangleEdge.BOTTOM ); //$NON-NLS-1$
    locations.put( "south", RectangleEdge.BOTTOM ); //$NON-NLS-1$
    LEGEND_LOCATIONS = Collections.unmodifiableMap( locations );
  }

  private String dataSource;
  private String titleText;
  private String titleField;
  private String noDataMessage;
  private boolean antiAlias;
  private String legendLocation;
  private String titleFont;
  private String labelFont;
  private String legendFont;
  private Font itemLabelFont;

  private boolean showBorder;
  private String borderColor;
  private String backgroundColor;
  private Color plotBackgroundColor;
  private Color legendBackgroundColor;
  private float plotForegroundAlpha;
  private float plotBackgroundAlpha;
  private boolean drawLegendBorder;
  private Boolean itemsLabelVisible;
  private boolean showLegend;
  private boolean threeD;
  private boolean chartSectionOutline;
  private String backgroundImage;
  private HashMap<Object, JFreeChart> chartCache;
  private ArrayList<String> seriesColors;

  // cache the images, since we roll through here multiple times...
  private transient Image plotImageCache;
  private Color legendTextColor;

  private String postProcessingLanguage;
  private String postProcessingScript;

  private String tooltipFormula;
  private String urlFormula;
  private LinkedHashMap<String, Expression> expressionMap;

  protected AbstractChartExpression() {
    seriesColors = new ArrayList<String>();
    seriesColors.add( "#ff6600" );
    seriesColors.add( "#fcd202" );
    seriesColors.add( "#b0de09" );
    seriesColors.add( "#0d8ecf" );
    seriesColors.add( "#2a0cd0" );
    seriesColors.add( "#cd0d74" );
    seriesColors.add( "#cc0000" );
    seriesColors.add( "#00cc00" );
    seriesColors.add( "#0000cc" );
    seriesColors.add( "#3a3a3a" );
    seriesColors.add( "#ffaa72" );
    seriesColors.add( "#fde673" );
    seriesColors.add( "#d3ed77" );
    seriesColors.add( "#79c1e4" );
    seriesColors.add( "#8979e5" );
    seriesColors.add( "#e379b2" );
    seriesColors.add( "#e37272" );
    seriesColors.add( "#72e372" );
    seriesColors.add( "#7272e3" );
    seriesColors.add( "#929292" );
    seriesColors.add( "#a64100" );
    seriesColors.add( "#a48901" );
    seriesColors.add( "#739106" );
    seriesColors.add( "#085c87" );
    seriesColors.add( "#1b0887" );
    seriesColors.add( "#85084c" );
    seriesColors.add( "#850000" );
    seriesColors.add( "#008500" );
    seriesColors.add( "#000085" );
    seriesColors.add( "#000000" );
    backgroundColor = "#ffffff";
    drawLegendBorder = true;
    legendFont = "SansSerif--8";
    labelFont = "SansSerif--8";
    titleFont = "SansSerif-BOLD-14";
    legendLocation = "bottom";
    antiAlias = true;
    noDataMessage = "CHART.USER_NO_DATA_AVAILABLE";
    showLegend = true;
    chartCache = new HashMap<Object, JFreeChart>();
    plotBackgroundAlpha = 1;
    plotForegroundAlpha = 1;
    expressionMap = new LinkedHashMap<>();
  }

  public Font getItemLabelFont() {
    return itemLabelFont;
  }

  public void setItemLabelFont( final Font itemLabelFont ) {
    this.itemLabelFont = itemLabelFont;
  }

  public String getTooltipFormula() {
    return tooltipFormula;
  }

  public void setTooltipFormula( final String tooltipFormula ) {
    this.tooltipFormula = tooltipFormula;
  }

  public String getUrlFormula() {
    return urlFormula;
  }

  public void setUrlFormula( final String urlFormula ) {
    this.urlFormula = urlFormula;
  }

  public String getPostProcessingLanguage() {
    return postProcessingLanguage;
  }

  public void setPostProcessingLanguage( final String postProcessingLanguage ) {
    this.postProcessingLanguage = postProcessingLanguage;
  }

  public String getPostProcessingScript() {
    return postProcessingScript;
  }

  public void setPostProcessingScript( final String postProcessingScript ) {
    this.postProcessingScript = postProcessingScript;
  }

  public Color getLegendBackgroundColor() {
    return legendBackgroundColor;
  }

  public void setLegendBackgroundColor( final Color legendBackgroundColor ) {
    this.legendBackgroundColor = legendBackgroundColor;
  }

  public Color getLegendTextColor() {
    return legendTextColor;
  }

  public void setLegendTextColor( final Color legendTextColor ) {
    this.legendTextColor = legendTextColor;
  }

  public float getPlotForegroundAlpha() {
    return plotForegroundAlpha;
  }

  public void setPlotForegroundAlpha( final float plotForegroundAlpha ) {
    this.plotForegroundAlpha = plotForegroundAlpha;
  }

  public float getPlotBackgroundAlpha() {
    return plotBackgroundAlpha;
  }

  public void setPlotBackgroundAlpha( final float plotBackgroundAlpha ) {
    this.plotBackgroundAlpha = plotBackgroundAlpha;
  }

  public Color getPlotBackgroundColor() {
    return plotBackgroundColor;
  }

  public void setPlotBackgroundColor( final Color plotBackgroundColor ) {
    this.plotBackgroundColor = plotBackgroundColor;
  }

  public String getTitleFont() {
    return titleFont;
  }

  public void setTitleFont( final String value ) {
    this.titleFont = value;
  }

  public String getLegendFont() {
    return legendFont;
  }

  public void setLegendFont( final String value ) {
    legendFont = value;
  }

  public String getLabelFont() {
    return labelFont;
  }

  public void setLabelFont( final String value ) {
    this.labelFont = value;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource( final String dataSource ) {
    this.dataSource = dataSource;
  }

  @Override
  public String[] getHyperlinkFormulas() {
    if ( !StringUtils.isEmpty( this.urlFormula ) ) {
      return new String[]{this.urlFormula};
    }
    return new String[]{};
  }

  public String getTitleField() {
    return titleField;
  }

  public void setTitleField( final String titleField ) {
    this.titleField = titleField;
  }

  public String getTitleText() {
    return titleText;
  }

  public void setTitleText( final String titleText ) {
    this.titleText = titleText;
  }

  public boolean isAntiAlias() {
    return antiAlias;
  }

  public void setAntiAlias( final boolean value ) {
    antiAlias = value;
  }

  public String getBorderColor() {
    return borderColor;
  }

  public void setBorderColor( final String value ) {
    borderColor = value;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor( final String value ) {
    backgroundColor = value;
  }

  public boolean isShowBorder() {
    return showBorder;
  }

  public void setShowBorder( final boolean value ) {
    showBorder = value;
  }

  public String getLegendLocation() {
    return legendLocation;
  }

  public void setLegendLocation( final String value ) {
    legendLocation = value;
  }

  public boolean isDrawLegendBorder() {
    return drawLegendBorder;
  }

  public void setDrawLegendBorder( final boolean value ) {
    drawLegendBorder = value;
  }

  public boolean isShowLegend() {
    return showLegend;
  }

  public void setShowLegend( final boolean value ) {
    showLegend = value;
  }

  public boolean isThreeD() {
    return threeD;
  }

  public void setThreeD( final boolean value ) {
    threeD = value;
  }

  public boolean isChartSectionOutline() {
    return chartSectionOutline;
  }

  public void setChartSectionOutline( final boolean value ) {
    chartSectionOutline = value;
  }

  public String getNoDataMessage() {
    return noDataMessage;
  }

  public void setNoDataMessage( final String noDataMessage ) {
    this.noDataMessage = noDataMessage;
  }

  public void setBackgroundImage( final String value ) {
    this.backgroundImage = value;
    this.plotImageCache = null;
  }

  public String getBackgroundImage() {
    return this.backgroundImage;
  }

  public Boolean getItemsLabelVisible() {
    return itemsLabelVisible;
  }

  public void setItemsLabelVisible( final Boolean itemsLabelVisible ) {
    this.itemsLabelVisible = itemsLabelVisible;
  }

  public void setSeriesColor( final int index, final String field ) {
    if ( seriesColors.size() == index ) {
      seriesColors.add( field );
    } else {
      seriesColors.set( index, field );
    }
  }

  public String getSeriesColor( final int index ) {
    return seriesColors.get( index );
  }

  public int getSeriesColorCount() {
    return seriesColors.size();
  }

  public String[] getSeriesColor() {
    return seriesColors.toArray( new String[seriesColors.size()] );
  }

  public void setSeriesColor( final String[] fields ) {
    this.seriesColors.clear();
    this.seriesColors.addAll( Arrays.asList( fields ) );
  }

  @Override
  public Map<String, Expression> getExpressionMap() {
    return new LinkedHashMap<>( expressionMap );
  }

  @Override
  public void setExpressionMap( Map<String, Expression> values ) {
    expressionMap.clear();
    expressionMap.putAll( values );
  }

  public void addExpression( String property, Expression e ) {
    expressionMap.put( property, e );
  }

  public void removeExpression( String property ) {
    expressionMap.remove( property );
  }

  protected void storeChartInCache( final Object key, final JFreeChart chart ) {
    if ( key == null ) {
      return;
    }
    if ( chart == null ) {
      throw new NullPointerException();
    }
    chartCache.put( key, chart );
  }

  protected JFreeChart loadChartFromCache( final Object key ) {
    if ( key == null ) {
      return null;
    }

    final JFreeChart o = chartCache.get( key );
    if ( o != null ) {
      return o;
    }
    return null;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final AbstractChartExpression instance = (AbstractChartExpression) super.getInstance();
    instance.chartCache = (HashMap<Object, JFreeChart>) instance.chartCache.clone();
    instance.chartCache.clear();
    instance.seriesColors = (ArrayList<String>) seriesColors.clone();
    instance.plotImageCache = null;
    instance.expressionMap = (LinkedHashMap<String, Expression>) expressionMap.clone();
    for ( Map.Entry<String, Expression> entry : expressionMap.entrySet() ) {
      entry.setValue( entry.getValue().getInstance() );
    }
    return instance;
  }

  public Object getValue() {
    try {

      Iterator it = expressionMap.entrySet().iterator();
      while ( it.hasNext() ) {
        Map.Entry pair = (Map.Entry) it.next();
        FormulaExpression formulaExpression = (FormulaExpression) pair.getValue();
        formulaExpression.setRuntime( getRuntime() );
        final Object o = formulaExpression.getValue();

        BeanUtility beanUtility = new BeanUtility( this );
        Class propertyType = beanUtility.getPropertyType( (String) pair.getKey() );
        beanUtility.setPropertyAsString( (String) pair.getKey(), propertyType, String.valueOf( o ) );
      }

      final Object maybeCollector = getDataRow().get( getDataSource() );
      final Dataset dataset;
      final Object cacheKey;
      if ( maybeCollector instanceof ICollectorFunction ) {
        final ICollectorFunction collector = (ICollectorFunction) maybeCollector;
        dataset = (Dataset) collector.getDatasourceValue();
        cacheKey = collector.getCacheKey();
      } else if ( maybeCollector instanceof CollectorFunctionResult ) {
        final CollectorFunctionResult collector = (CollectorFunctionResult) maybeCollector;
        dataset = collector.getDataSet();
        cacheKey = collector.getCacheKey();
      } else {
        logger.debug( "CATEGORICALCHARTEXPRESSION.USER_NOT_A_DATASET" ); //$NON-NLS-1$
        return null;
      }

      if ( dataset == null ) {
        return null;
      }

      final Object key;
      if ( cacheKey != null ) {
        key = cacheKey;
      } else {
        key = getName();
      }

      final JFreeChart chartFromCache = loadChartFromCache( key );
      final JFreeChart chart;
      if ( chartFromCache != null ) {
        chart = chartFromCache;
      } else {
        chart = computeChart( dataset );
        if ( chart == null ) {
          return null;
        }

        storeChartInCache( key, chart );
      }
      // we have to call the deprecated method to let that method call the real method to catch all
      // subclasses that exist out there.
      configureChart( chart );
      postProcessChart( chart );
      return new JFreeChartReportDrawable( chart,
          StringUtils.isEmpty( getUrlFormula() ) == false || StringUtils.isEmpty( getTooltipFormula() ) == false );
    } catch ( Exception e ) {
      logger.error( "Failed to configure chart", e );
      return null;
    }
  }

  protected JFreeChart postProcessChart( final JFreeChart originalChart ) {
    if ( postProcessingLanguage == null || postProcessingScript == null ) {
      return originalChart;
    }

    boolean allowScriptEval = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "false" )
      .equalsIgnoreCase( "true" );

    if ( !allowScriptEval ) {
      logger.error( "Scripts are prevented from running by default in order to avoid"
        + " potential remote code execution.  The system administrator must enable this capability." );
    }

    final LegacyDataRowWrapper legacyDataRowWrapper = new LegacyDataRowWrapper();
    final WrapperExpressionRuntime runtimeWrapper = new WrapperExpressionRuntime();
    runtimeWrapper.update( null, getRuntime() );
    legacyDataRowWrapper.setParent( getDataRow() );
    try {
      final BSFManager interpreter = new BSFManager();
      interpreter.declareBean( "chartExpression", this, getClass() ); //$NON-NLS-1$
      interpreter.declareBean( "chart", originalChart, JFreeChart.class ); //$NON-NLS-1$
      interpreter.declareBean( "runtime", runtimeWrapper, ExpressionRuntime.class ); //$NON-NLS-1$
      interpreter.declareBean( "dataRow", legacyDataRowWrapper, DataRow.class ); //$NON-NLS-1$
      final Object o = interpreter.eval( postProcessingLanguage, "expression", 1, 1, postProcessingScript ); //$NON-NLS-1$
      if ( o instanceof JFreeChart ) {
        return (JFreeChart) o;
      }
      return originalChart;
    } catch ( BSFException e ) {
      // this is not nice
      AbstractChartExpression.logger.warn( "Failed to evaluate post processing script", e );
    } finally {
      legacyDataRowWrapper.setParent( null );
      runtimeWrapper.update( null, null );
    }
    return originalChart;
  }

  protected JFreeChart computeChart( final Dataset dataset ) {
    return null;
  }

  protected void configureChart( final JFreeChart chart ) {
    // Misc Properties
    final TextTitle chartTitle = chart.getTitle();
    if ( chartTitle != null ) {
      final Font titleFont = Font.decode( getTitleFont() );
      chartTitle.setFont( titleFont );
    }

    if ( isAntiAlias() == false ) {
      chart.setAntiAlias( false );
    }

    chart.setBorderVisible( isShowBorder() );

    final Color backgroundColor = parseColorFromString( getBackgroundColor() );
    if ( backgroundColor != null ) {
      chart.setBackgroundPaint( backgroundColor );
    }

    if ( plotBackgroundColor != null ) {
      chart.getPlot().setBackgroundPaint( plotBackgroundColor );
    }
    chart.getPlot().setBackgroundAlpha( plotBackgroundAlpha );
    chart.getPlot().setForegroundAlpha( plotForegroundAlpha );
    final Color borderCol = parseColorFromString( getBorderColor() );
    if ( borderCol != null ) {
      chart.setBorderPaint( borderCol );
    }

    //remove legend if showLegend = false
    if ( !isShowLegend() ) {
      chart.removeLegend();
    } else { //if true format legend
      final LegendTitle chLegend = chart.getLegend();
      if ( chLegend != null ) {
        final RectangleEdge loc = translateEdge( legendLocation.toLowerCase() );
        if ( loc != null ) {
          chLegend.setPosition( loc );
        }
        if ( getLegendFont() != null ) {
          chLegend.setItemFont( Font.decode( getLegendFont() ) );
        }
        if ( !isDrawLegendBorder() ) {
          chLegend.setBorder( BlockBorder.NONE );
        }
        if ( legendBackgroundColor != null ) {
          chLegend.setBackgroundPaint( legendBackgroundColor );
        }
        if ( legendTextColor != null ) {
          chLegend.setItemPaint( legendTextColor );
        }
      }

    }

    final Plot plot = chart.getPlot();
    plot.setNoDataMessageFont( Font.decode( getLabelFont() ) );

    final String message = getNoDataMessage();
    if ( message != null ) {
      plot.setNoDataMessage( message );
    }

    plot.setOutlineVisible( isChartSectionOutline() );

    if ( backgroundImage != null ) {
      if ( plotImageCache != null ) {
        plot.setBackgroundImage( plotImageCache );
      } else {
        final ExpressionRuntime expressionRuntime = getRuntime();
        final ProcessingContext context = expressionRuntime.getProcessingContext();
        final ResourceKey contentBase = context.getContentBase();
        final ResourceManager manager = context.getResourceManager();
        try {
          final ResourceKey key = createKeyFromString( manager, contentBase, backgroundImage );
          final Resource resource = manager.create( key, null, Image.class );
          final Image image = (Image) resource.getResource();
          plot.setBackgroundImage( image );
          plotImageCache = image;
        } catch ( Exception e ) {
          logger.error( "ABSTRACTCHARTEXPRESSION.ERROR_0007_ERROR_RETRIEVING_PLOT_IMAGE", e ); //$NON-NLS-1$
          throw new IllegalStateException( "Failed to process chart" );
        }
      }
    }
  }

  private ResourceKey createKeyFromString( final ResourceManager resourceManager,
                                           final ResourceKey contextKey,
                                           final String file ) {

    try {
      if ( contextKey != null ) {
        return resourceManager.deriveKey( contextKey, file );
      }
    } catch ( ResourceException re ) {
      // failed to load from context
      logger.debug( "Failed to load background-image as derived path: " + re );
    }

    try {
      return resourceManager.createKey( new URL( file ) );
    } catch ( ResourceException re ) {
      logger.debug( "Failed to load background-image as URL: " + re );
    } catch ( MalformedURLException e ) {
      //
    }

    try {
      return resourceManager.createKey( new File( file ) );
    } catch ( ResourceException re ) {
      // failed to load from context
      logger.debug( "Failed to load background-image as file: " + re );
    }

    return null;
  }

  protected Color parseColorFromString( final String colStr ) {
    if ( colStr == null ) {
      return null;
    }

    try {
      return Color.decode( colStr );
    } catch ( NumberFormatException ex ) {
      // Ignored - try other parser...
    }
    return ColorHelper.lookupColor( colStr );
  }

  protected String computeTitle() {
    if ( titleField != null ) {
      final Object computedTitle = getDataRow().get( titleField );
      if ( computedTitle != null ) {
        return String.valueOf( computedTitle );
      }
    }
    if ( titleText != null ) {
      // this is a deprecated behaviour, later we will not interpret the title anymore.
      final Object computedTitle = getDataRow().get( titleText );
      if ( computedTitle != null ) {
        return String.valueOf( computedTitle );
      }
    }
    return titleText;
  }

  protected RectangleEdge translateEdge( final String edge ) {
    final RectangleEdge translatedEdge = LEGEND_LOCATIONS.get( edge );
    if ( translatedEdge != null ) {
      return translatedEdge;
    }
    return RectangleEdge.LEFT;
  }

  /**
   * Overrides the dependency level to only execute this function on the pagination and content-generation level.
   *
   * @return LayoutProcess.LEVEL_PAGINATE.
   */
  public int getDependencyLevel() {
    return LayoutProcess.LEVEL_PAGINATE;
  }

  public void setDependencyLevel( final int ignored ) {
    // do nothing
  }

  /**
   * @return java.awt.Stroke for JFreeChart renderer to draw lines
   */
  public static Stroke translateLineStyle( float lineWidth, final String lineStyle ) {
    // Negative linewidths not allowed, reset to default.
    if ( lineWidth < 0 ) {
      logger.error( ( "LineChartExpression.ERROR_0001_INVALID_LINE_WIDTH" ) ); //$NON-NLS-1$
      lineWidth = 1.0f;
    }

    final int strokeType;
    if ( LINE_STYLE_DASH_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DASHED;
    } else if ( LINE_STYLE_DOT_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DOTTED;
    } else if ( LINE_STYLE_DASHDOT_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DOT_DASH;
    } else if ( LINE_STYLE_DASHDOTDOT_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DOT_DOT_DASH;
    } else {
      if ( lineWidth == 0 ) {
        strokeType = StrokeUtility.STROKE_NONE;
      } else {
        strokeType = StrokeUtility.STROKE_SOLID;
      }
    }

    return StrokeUtility.createStroke( strokeType, lineWidth );
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setChartWidth( final int value ) {
  }

  /**
   * @return always zero.
   * @deprecated This property is no longer used.
   */
  public int getChartWidth() {
    return 0;
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setChartHeight( final int value ) {
  }

  /**
   * @return always zero.
   * @deprecated This property is no longer used.
   */
  public int getChartHeight() {
    return 0;
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setChartDirectory( final String value ) {
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setChartFile( final String value ) {
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setChartUrlMask( final String value ) {
  }

  /**
   * @return always null.
   * @deprecated This property is no longer used.
   */
  public String getChartDirectory() {
    return null;
  }

  /**
   * @return always null.
   * @deprecated This property is no longer used.
   */
  public String getChartFile() {
    return null;
  }

  /**
   * @return always null.
   * @deprecated This property is no longer used.
   */
  public String getChartUrlMask() {
    return null;
  }

  /**
   * @return always false.
   * @deprecated This property is no longer used.
   */
  public boolean isReturnFileNameOnly() {
    return false;
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setReturnFileNameOnly( final boolean value ) {
  }

  /**
   * @return always false.
   * @deprecated This property is no longer used.
   */
  public boolean isReturnImageReference() {
    return false;
  }

  /**
   * @param value ignored.
   * @deprecated This property is no longer used.
   */
  public void setReturnImageReference( final boolean value ) {
  }


  /**
   * @param key
   * @deprecated This method should not be public and should check for null-values. Use #storeChartInCache now.
   */
  public JFreeChart getChartFromCache( final Object key ) {
    if ( key == null ) {
      return null;
    }
    return loadChartFromCache( key );
  }

  /**
   * @param chart
   * @param key
   * @deprecated This method should not be public and should check for null-values. Use #storeChartInCache now.
   */
  public void putChartInCache( final JFreeChart chart, final Object key ) {
    if ( chart == null || key == null ) {
      return;
    }
    storeChartInCache( key, chart );
  }

  /**
   * @return
   * @deprecated This property should no longer be defined manually. The only sensible value it can take is "true" now.
   */
  public boolean isUseDrawable() {
    return true;
  }

  /**
   * @param value
   * @deprecated This property should no longer be defined manually. The only sensible value it can take is "true" now.
   */
  public void setUseDrawable( final boolean value ) {
  }

  /**
   * @param colStr
   * @return
   * @deprecated This is no getter and therefor should not call itself a getter.
   */
  protected Color getColorFromString( final String colStr ) {
    return parseColorFromString( colStr );
  }

  /**
   * @param lookupValue
   * @return
   * @deprecated This method should not be public and is part of a messed up title-semantic. Will be removed later.
   */
  public String getPossibleExpressionStringValue( final String lookupValue ) {
    if ( lookupValue == null ) {
      return null;
    }

    Object maybeExpression = null;
    try {
      maybeExpression = getDataRow().get( lookupValue );
    } catch ( Exception ignored ) {
      // ignore the expression
    }
    if ( maybeExpression != null ) {
      return maybeExpression.toString();
    } else {
      return lookupValue;
    }
  }

  /**
   * Reduces standard tick unit array to meet  formatting  precision and avoid duplicated values (PRD-5821)
   *
   * @return
   */
  protected void standardTickUnitsApplyFormat( NumberAxis numberAxis, NumberFormat format ) {
    final TickUnits standardTickUnits = (TickUnits) numberAxis.getStandardTickUnits();
    TickUnits cutTickUnits = new TickUnits();
    double formatterMinSize = 1 / Math.pow( 10, format.getMaximumFractionDigits() );
    for ( int i = 0; i < standardTickUnits.size(); i++ ) {
      if ( Double.compare( standardTickUnits.get( i ).getSize(), formatterMinSize ) >= 0 ) {
        cutTickUnits.add( new NumberTickUnit( standardTickUnits.get( i ).getSize() ) );
      }
    }
    numberAxis.setStandardTickUnits( cutTickUnits );
  }

  /**
   * @return
   * @deprecated The semantics of this property is all messed up and thus we can only deprecate it.
   */
  public String getTitle() {
    return getPossibleExpressionStringValue( getTitleText() );
  }

  /**
   * @deprecated The semantics of this property is all messed up and thus we can only deprecate it.
   */
  public void setTitle( final String title ) {
    this.titleText = title;
  }

  /**
   * @deprecated use bean accessors instead: getSeriesColor(), getSeriesColor(int index)
   */
  public List getSeriesColors() {
    return seriesColors;
  }

}
