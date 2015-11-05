package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;

public class HtmlRowBackgroundStruct {
  private Color color;
  private BorderEdge topEdge;
  private BorderEdge bottomEdge;
  private boolean failed;

  public HtmlRowBackgroundStruct() {
  }

  public void setColor( final Color color ) {
    this.color = color;
  }

  public void setTopEdge( final BorderEdge topEdge ) {
    this.topEdge = topEdge;
  }

  public void setBottomEdge( final BorderEdge bottomEdge ) {
    this.bottomEdge = bottomEdge;
  }

  public void setFailed( final boolean failed ) {
    this.failed = failed;
  }

  public Color getColor() {
    return color;
  }

  public BorderEdge getTopEdge() {
    return topEdge;
  }

  public BorderEdge getBottomEdge() {
    return bottomEdge;
  }

  public boolean isFailed() {
    return failed;
  }

  public void set( Color color, BorderEdge topEdge, BorderEdge bottomEdge ) {
    this.color = color;
    this.topEdge = topEdge;
    this.bottomEdge = bottomEdge;
    this.failed = false;
  }

  public void fail() {
    this.color = null;
    this.topEdge = BorderEdge.EMPTY;
    this.bottomEdge = BorderEdge.EMPTY;
    this.failed = true;
  }
}
