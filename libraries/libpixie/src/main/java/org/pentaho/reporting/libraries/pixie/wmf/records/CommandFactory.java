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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pixie.wmf.records;

import java.util.HashMap;

/**
 * Manages the available WmfCommands and allows a generic command instantiation.
 */
public class CommandFactory {
  private static CommandFactory commandFactory;

  public static synchronized CommandFactory getInstance() {
    if ( commandFactory == null ) {
      commandFactory = new CommandFactory();
    }
    return commandFactory;
  }

  public CommandFactory() {
  }

  /**
   * A global collection of all known record types.
   */
  private HashMap<Integer, MfCmd> recordTypes;

  /**
   * Registers all known command types to the standard factory.
   */
  public synchronized void registerAllKnownTypes() {
    if ( recordTypes != null ) {
      return;
    }

    recordTypes = new HashMap<Integer, MfCmd>();

    registerCommand( new MfCmdAnimatePalette() );
    registerCommand( new MfCmdArc() );
    registerCommand( new MfCmdDibBitBlt() );
    registerCommand( new MfCmdChord() );
    registerCommand( new MfCmdCreateBrush() );
    registerCommand( new MfCmdCreateDibPatternBrush() );
    registerCommand( new MfCmdCreateFont() );
    registerCommand( new MfCmdCreatePen() );
    registerCommand( new MfCmdCreatePalette() );
    registerCommand( new MfCmdCreatePatternBrush() );
    registerCommand( new MfCmdCreateRegion() );
    registerCommand( new MfCmdDeleteObject() );
    registerCommand( new MfCmdEllipse() );
    registerCommand( new MfCmdEscape() );
    registerCommand( new MfCmdExcludeClipRect() );
    registerCommand( new MfCmdExtFloodFill() );
    registerCommand( new MfCmdExtTextOut() );
    registerCommand( new MfCmdFillRegion() );
    registerCommand( new MfCmdFrameRegion() );
    registerCommand( new MfCmdFloodFill() );
    registerCommand( new MfCmdInvertRegion() );
    registerCommand( new MfCmdIntersectClipRect() );
    registerCommand( new MfCmdLineTo() );
    registerCommand( new MfCmdMoveTo() );
    registerCommand( new MfCmdOffsetClipRgn() );
    registerCommand( new MfCmdOffsetViewportOrg() );
    registerCommand( new MfCmdOffsetWindowOrg() );
    registerCommand( new MfCmdBitBlt() );
    registerCommand( new MfCmdStretchBlt() );
    registerCommand( new MfCmdPatBlt() );
    registerCommand( new MfCmdPaintRgn() );
    registerCommand( new MfCmdPie() );
    registerCommand( new MfCmdPolyPolygon() );
    registerCommand( new MfCmdPolygon() );
    registerCommand( new MfCmdPolyline() );
    registerCommand( new MfCmdRealisePalette() );
    registerCommand( new MfCmdRectangle() );
    registerCommand( new MfCmdRestoreDc() );
    registerCommand( new MfCmdResizePalette() );
    registerCommand( new MfCmdRoundRect() );
    registerCommand( new MfCmdSaveDc() );
    registerCommand( new MfCmdScaleWindowExt() );
    registerCommand( new MfCmdScaleViewportExt() );
    registerCommand( new MfCmdSelectClipRegion() );
    registerCommand( new MfCmdSelectObject() );
    registerCommand( new MfCmdSelectPalette() );
    registerCommand( new MfCmdSetBkMode() );
    registerCommand( new MfCmdSetBkColor() );
    registerCommand( new MfCmdSetDibitsToDevice() );
    registerCommand( new MfCmdSetMapperFlags() );
    registerCommand( new MfCmdSetMapMode() );
    registerCommand( new MfCmdSetPaletteEntries() );
    registerCommand( new MfCmdSetPolyFillMode() );
    registerCommand( new MfCmdSetPixel() );
    registerCommand( new MfCmdSetRop2() );
    registerCommand( new MfCmdSetStretchBltMode() );
    registerCommand( new MfCmdSetTextCharExtra() );
    registerCommand( new MfCmdSetTextAlign() );
    registerCommand( new MfCmdSetTextColor() );
    registerCommand( new MfCmdSetTextJustification() );
    registerCommand( new MfCmdSetViewPortExt() );
    registerCommand( new MfCmdSetViewPortOrg() );
    registerCommand( new MfCmdSetWindowExt() );
    registerCommand( new MfCmdSetWindowOrg() );
    registerCommand( new MfCmdDibStretchBlt() );
    registerCommand( new MfCmdStretchDibits() );
    registerCommand( new MfCmdTextOut() );
  }

  private void registerCommand( final MfCmd command ) {
    MfCmd cmd = recordTypes.get( new Integer( command.getFunction() ) );
    if ( cmd != null ) {
      throw new IllegalArgumentException( "Already registered command " + command + " -> was: " + cmd );
    }

    recordTypes.put( command.getFunction(), command );
  }

  public MfCmd getCommand( final int function ) {
    if ( recordTypes == null ) {
      registerAllKnownTypes();
    }

    final MfCmd cmd = recordTypes.get( new Integer( function ) );
    if ( cmd == null ) {
      final MfCmdUnknownCommand ucmd = new MfCmdUnknownCommand();
      ucmd.setFunction( function );
      return ucmd;
    }
    return cmd.getInstance();
  }
}
