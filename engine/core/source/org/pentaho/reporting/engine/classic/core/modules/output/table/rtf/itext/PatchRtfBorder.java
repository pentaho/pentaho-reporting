/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.DocWriter;
import com.lowagie.text.rtf.RtfElement;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.style.RtfColor;


/**
 * The PatchRtfBorder handle one row or cell border.
 * INTERNAL USE ONLY
 * 
 * @version $Id: PatchRtfBorder.java 3580 2008-08-06 15:52:00Z howard_s $
 * @author Mark Hall (Mark.Hall@mail.room3b.eu)
 * @author Thomas Bickel (tmb99@inode.at)
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PatchRtfBorder extends RtfElement {

    /**
     * Constant for the left row border
     */
    protected static final byte[] ROW_BORDER_LEFT = DocWriter.getISOBytes("\\trbrdrl");
    /**
     * Constant for the top row border
     */
    protected static final byte[] ROW_BORDER_TOP = DocWriter.getISOBytes("\\trbrdrt");
    /**
     * Constant for the right row border
     */
    protected static final byte[] ROW_BORDER_RIGHT = DocWriter.getISOBytes("\\trbrdrr");
    /**
     * Constant for the bottom row border
     */
    protected static final byte[] ROW_BORDER_BOTTOM = DocWriter.getISOBytes("\\trbrdrb");
    /**
     * Constant for the horizontal line
     */
    protected static final byte[] ROW_BORDER_HORIZONTAL = DocWriter.getISOBytes("\\trbrdrh");
    /**
     * Constant for the vertical line
     */
    protected static final byte[] ROW_BORDER_VERTICAL = DocWriter.getISOBytes("\\trbrdrv");
    /**
     * Constant for the left cell border
     */
    protected static final byte[] CELL_BORDER_LEFT = DocWriter.getISOBytes("\\clbrdrl");
    /**
     * Constant for the top cell border
     */
    protected static final byte[] CELL_BORDER_TOP = DocWriter.getISOBytes("\\clbrdrt");
    /**
     * Constant for the right cell border
     */
    protected static final byte[] CELL_BORDER_RIGHT = DocWriter.getISOBytes("\\clbrdrr");
    /**
     * Constant for the bottom cell border
     */
    protected static final byte[] CELL_BORDER_BOTTOM = DocWriter.getISOBytes("\\clbrdrb");
    /**
     * Constant for the border width
     */
    protected static final byte[] BORDER_WIDTH = DocWriter.getISOBytes("\\brdrw");
    /**
     * Constant for the border color number
     */
    protected static final byte[] BORDER_COLOR_NUMBER = DocWriter.getISOBytes("\\brdrcf");
    /**
     * Constant for the single border style
     */
    protected static final byte[] BORDER_STYLE_SINGLE = DocWriter.getISOBytes("\\brdrs");
    /**
     * Constant for the double thick border style
     */
    protected static final byte[] BORDER_STYLE_DOUBLE_THICK	= DocWriter.getISOBytes("\\brdrth");
    /**
     * Constant for the shadowed border style
     */
    protected static final byte[] BORDER_STYLE_SHADOWED = DocWriter.getISOBytes("\\brdrsh");
    /**
     * Constant for the dotted border style
     */
    protected static final byte[] BORDER_STYLE_DOTTED = DocWriter.getISOBytes("\\brdrdot");
    /**
     * Constant for the dashed border style
     */
    protected static final byte[] BORDER_STYLE_DASHED = DocWriter.getISOBytes("\\brdrdash");
    /**
     * Constant for the hairline border style
     */
    protected static final byte[] BORDER_STYLE_HAIRLINE = DocWriter.getISOBytes("\\brdrhair");
    /**
     * Constant for the double border style
     */
    protected static final byte[] BORDER_STYLE_DOUBLE = DocWriter.getISOBytes("\\brdrdb");
    /**
     * Constant for the dot dash border style
     */
    protected static final byte[] BORDER_STYLE_DOT_DASH = DocWriter.getISOBytes("\\brdrdashd");
    /**
     * Constant for the dot dot dash border style
     */
    protected static final byte[] BORDER_STYLE_DOT_DOT_DASH	= DocWriter.getISOBytes("\\brdrdashdd");
    /**
     * Constant for the triple border style
     */
    protected static final byte[] BORDER_STYLE_TRIPLE = DocWriter.getISOBytes("\\brdrtriple");
    /**
     * Constant for the thick thin border style
     */
    protected static final byte[] BORDER_STYLE_THICK_THIN = DocWriter.getISOBytes("\\brdrtnthsg");
    /**
     * Constant for the thin thick border style
     */
    protected static final byte[] BORDER_STYLE_THIN_THICK = DocWriter.getISOBytes("\\brdrthtnsg");
    /**
     * Constant for the thin thick thin border style
     */
    protected static final byte[] BORDER_STYLE_THIN_THICK_THIN = DocWriter.getISOBytes("\\brdrtnthtnsg");
    /**
     * Constant for the thick thin medium border style
     */
    protected static final byte[] BORDER_STYLE_THICK_THIN_MED = DocWriter.getISOBytes("\\brdrtnthmg");
    /**
     * Constant for the thin thick medium border style
     */
    protected static final byte[] BORDER_STYLE_THIN_THICK_MED = DocWriter.getISOBytes("\\brdrthtnmg");
    /**
     * Constant for the thin thick thin medium border style
     */
    protected static final byte[] BORDER_STYLE_THIN_THICK_THIN_MED = DocWriter.getISOBytes("\\brdrtnthtnmg");
    /**
     * Constant for the thick thin large border style
     */
    protected static final byte[] BORDER_STYLE_THICK_THIN_LARGE = DocWriter.getISOBytes("\\brdrtnthlg");
    /**
     * Constant for the thin thick large border style
     */
    protected static final byte[] BORDER_STYLE_THIN_THICK_LARGE	= DocWriter.getISOBytes("\\brdrthtnlg");
    /**
     * Constant for the thin thick thin large border style
     */
    protected static final byte[] BORDER_STYLE_THIN_THICK_THIN_LARGE = DocWriter.getISOBytes("\\brdrtnthtnlg");
    /**
     * Constant for the wavy border style
     */
    protected static final byte[] BORDER_STYLE_WAVY = DocWriter.getISOBytes("\\brdrwavy");
    /**
     * Constant for the double wavy border style
     */
    protected static final byte[] BORDER_STYLE_DOUBLE_WAVY = DocWriter.getISOBytes("\\brdrwavydb");
    /**
     * Constant for the striped border style
     */
    protected static final byte[] BORDER_STYLE_STRIPED = DocWriter.getISOBytes("\\brdrdashdotstr");
    /**
     * Constant for the embossed border style
     */
    protected static final byte[] BORDER_STYLE_EMBOSS = DocWriter.getISOBytes("\\brdremboss");
    /**
     * Constant for the engraved border style
     */
    protected static final byte[] BORDER_STYLE_ENGRAVE = DocWriter.getISOBytes("\\brdrengrave");

    /**
     * Constant for a row border
     */
    protected static final int ROW_BORDER = 1;
    /**
     * Constant for a cell border
     */
    protected static final int CELL_BORDER = 2;
    
    /**
     * This border is no border :-)
     */
    protected static final int NO_BORDER = 0;
    /**
     * Constant for a left border
     */
    protected static final int LEFT_BORDER = 1;
    /**
     * Constant for a top border
     */
    protected static final int TOP_BORDER = 2;
    /**
     * Constant for a right border
     */
    protected static final int RIGHT_BORDER = 4;
    /**
     * Constant for a bottom border
     */
    protected static final int BOTTOM_BORDER = 8;
    /**
     * Constant for a box (left, top, right, bottom) border 
     */
    protected static final int BOX_BORDER = 15;
    /**
     * Constant for a vertical line
     */
    protected static final int VERTICAL_BORDER = 16;
    /**
     * Constant for a horizontal line
     */
    protected static final int HORIZONTAL_BORDER = 32;
    
    /**
     * Constant for a border with no border
     */
    public static final int BORDER_NONE = 0;
    /**
     * Constant for a single border
     */
    public static final int BORDER_SINGLE = 1;
    /**
     * Constant for a double thick border
     */
    public static final int BORDER_DOUBLE_THICK = 2;
    /**
     * Constant for a shadowed border
     */
    public static final int BORDER_SHADOWED = 3;
    /**
     * Constant for a dotted border
     */
    public static final int BORDER_DOTTED = 4;
    /**
     * Constant for a dashed border
     */
    public static final int BORDER_DASHED = 5;
    /**
     * Constant for a hairline border
     */
    public static final int BORDER_HAIRLINE = 6;
    /**
     * Constant for a double border
     */
    public static final int BORDER_DOUBLE = 7;
    /**
     * Constant for a dot dash border
     */
    public static final int BORDER_DOT_DASH = 8;
    /**
     * Constant for a dot dot dash border
     */
    public static final int BORDER_DOT_DOT_DASH = 9;
    /**
     * Constant for a triple border
     */
    public static final int BORDER_TRIPLE = 10;
    /**
     * Constant for a thick thin border
     */
    public static final int BORDER_THICK_THIN = 11;
    /**
     * Constant for a thin thick border
     */
    public static final int BORDER_THIN_THICK = 12;
    /**
     * Constant for a thin thick thin border
     */
    public static final int BORDER_THIN_THICK_THIN = 13;
    /**
     * Constant for a thick thin medium border
     */
    public static final int BORDER_THICK_THIN_MED = 14;
    /**
     * Constant for a thin thick medium border
     */
    public static final int BORDER_THIN_THICK_MED = 15;
    /**
     * Constant for a thin thick thin medium border
     */
    public static final int BORDER_THIN_THICK_THIN_MED = 16;
    /**
     * Constant for a thick thin large border
     */
    public static final int BORDER_THICK_THIN_LARGE = 17;
    /**
     * Constant for a thin thick large border
     */
    public static final int BORDER_THIN_THICK_LARGE = 18;
    /**
     * Constant for a thin thick thin large border
     */
    public static final int BORDER_THIN_THICK_THIN_LARGE = 19;
    /**
     * Constant for a wavy border
     */
    public static final int BORDER_WAVY = 20;
    /**
     * Constant for a double wavy border
     */
    public static final int BORDER_DOUBLE_WAVY = 21;
    /**
     * Constant for a striped border
     */
    public static final int BORDER_STRIPED = 22;
    /**
     * Constant for an embossed border
     */
    public static final int BORDER_EMBOSS = 23;
    /**
     * Constant for an engraved border
     */
    public static final int BORDER_ENGRAVE = 24;
    
    /**
     * The type of this PatchRtfBorder
     */
    private int borderType = ROW_BORDER;
    /**
     * The position of this PatchRtfBorder
     */
    private int borderPosition = NO_BORDER;
    /**
     * The style of this PatchRtfBorder
     */
    private int borderStyle = BORDER_NONE;
    /**
     * The width of this PatchRtfBorder
     */
    private int borderWidth = 20;
    /**
     * The color of this PatchRtfBorder
     */
    private RtfColor borderColor = null;
    
    /**
     * Makes a copy of the given PatchRtfBorder
     * 
     * @param doc The RtfDocument this PatchRtfBorder belongs to
     * @param borderType The border type of this PatchRtfBorder
     * @param border The PatchRtfBorder to copy
     */
    protected PatchRtfBorder(RtfDocument doc, int borderType, PatchRtfBorder border) {
        super(doc);
        this.borderType = borderType;
        this.borderPosition = border.getBorderPosition();
        this.borderStyle = border.getBorderStyle();
        this.borderWidth = border.getBorderWidth();
        this.borderColor = new RtfColor(this.document, border.getBorderColor());
    }
    
    /**
     * Constructs a PatchRtfBorder
     * 
     * @param doc The RtfDocument this PatchRtfBorder belongs to
     * @param borderType The type of border this PatchRtfBorder is
     * @param borderPosition The position of this PatchRtfBorder
     * @param borderStyle The style of this PatchRtfBorder
     * @param borderWidth The width of this PatchRtfBorder
     * @param borderColor The color of this PatchRtfBorder
     */
    protected PatchRtfBorder(RtfDocument doc,
                             int borderType,
                             int borderPosition,
                             int borderStyle,
                             float borderWidth,
                             Color borderColor) {
        super(doc);
        this.borderType = borderType;
        this.borderPosition = borderPosition;
        this.borderStyle = borderStyle;
        this.borderWidth = (int) Math.min((borderWidth * TWIPS_FACTOR), 75);
        if(this.borderWidth == 0) {
            this.borderStyle = BORDER_NONE;
        }
        if(borderColor == null) {
            this.borderColor = new RtfColor(this.document, new Color(0, 0, 0));
        } else {
            this.borderColor = new RtfColor(this.document, borderColor);
        }
    }
    
    /**
     * Writes the PatchRtfBorder settings
     */
    public void writeContent(final OutputStream result) throws IOException
    {
        if(this.borderStyle == BORDER_NONE || this.borderPosition == NO_BORDER || this.borderWidth == 0) {
            return;
        }

    	if(this.borderType == ROW_BORDER) {
            switch(this.borderPosition) {
                case LEFT_BORDER:
                    result.write(ROW_BORDER_LEFT);
                	break;
                case TOP_BORDER:
                    result.write(ROW_BORDER_TOP);
                    break;
                case RIGHT_BORDER:
                    result.write(ROW_BORDER_RIGHT);
                    break;
                case BOTTOM_BORDER:
                    result.write(ROW_BORDER_BOTTOM);
                    break;
                case HORIZONTAL_BORDER:
                    result.write(ROW_BORDER_HORIZONTAL);
                    break;
                case VERTICAL_BORDER:
                    result.write(ROW_BORDER_VERTICAL);
                    break;
                default:
                    return;
            }
            result.write(writeBorderStyle());
            result.write(BORDER_WIDTH);
            result.write(intToByteArray(this.borderWidth));
            result.write(BORDER_COLOR_NUMBER);
            result.write(intToByteArray(this.borderColor.getColorNumber()));
            this.document.outputDebugLinebreak(result);
        } else if(this.borderType == CELL_BORDER) {
            switch(this.borderPosition) {
                case LEFT_BORDER:
                    result.write(CELL_BORDER_LEFT);
                	break;
                case TOP_BORDER:
                    result.write(CELL_BORDER_TOP);
                    break;
                case RIGHT_BORDER:
                    result.write(CELL_BORDER_RIGHT);
                    break;
                case BOTTOM_BORDER:
                    result.write(CELL_BORDER_BOTTOM);
                    break;
                default:
                    return;
            }
            result.write(writeBorderStyle());
            result.write(BORDER_WIDTH);
            result.write(intToByteArray(this.borderWidth));
            result.write(BORDER_COLOR_NUMBER);
            result.write(intToByteArray(this.borderColor.getColorNumber()));
            this.document.outputDebugLinebreak(result);
        }    	
    }
     
    /**
     * Writes the style of this PatchRtfBorder
     * 
     * @return A byte array containing the style of this PatchRtfBorder
     */
    private byte[] writeBorderStyle() {
        switch(this.borderStyle) {
            case BORDER_NONE                    : return new byte[0];
            case BORDER_SINGLE 					: return BORDER_STYLE_SINGLE;
            case BORDER_DOUBLE_THICK	 		: return BORDER_STYLE_DOUBLE_THICK;
            case BORDER_SHADOWED 				: return BORDER_STYLE_SHADOWED;
            case BORDER_DOTTED   				: return BORDER_STYLE_DOTTED;
            case BORDER_DASHED   				: return BORDER_STYLE_DASHED;
            case BORDER_HAIRLINE   				: return BORDER_STYLE_HAIRLINE;
            case BORDER_DOUBLE 		  			: return BORDER_STYLE_DOUBLE;
            case BORDER_DOT_DASH   				: return BORDER_STYLE_DOT_DASH;
            case BORDER_DOT_DOT_DASH			: return BORDER_STYLE_DOT_DOT_DASH;
            case BORDER_TRIPLE					: return BORDER_STYLE_TRIPLE;
            case BORDER_THICK_THIN				: return BORDER_STYLE_THICK_THIN;
            case BORDER_THIN_THICK				: return BORDER_STYLE_THIN_THICK;
            case BORDER_THIN_THICK_THIN			: return BORDER_STYLE_THIN_THICK_THIN;
            case BORDER_THICK_THIN_MED			: return BORDER_STYLE_THICK_THIN_MED;
            case BORDER_THIN_THICK_MED			: return BORDER_STYLE_THIN_THICK_MED;
            case BORDER_THIN_THICK_THIN_MED		: return BORDER_STYLE_THIN_THICK_THIN_MED;
            case BORDER_THICK_THIN_LARGE		: return BORDER_STYLE_THICK_THIN_LARGE;
            case BORDER_THIN_THICK_LARGE		: return BORDER_STYLE_THIN_THICK_LARGE;
            case BORDER_THIN_THICK_THIN_LARGE	: return BORDER_STYLE_THIN_THICK_THIN_LARGE;
            case BORDER_WAVY					: return BORDER_STYLE_WAVY;
            case BORDER_DOUBLE_WAVY				: return BORDER_STYLE_DOUBLE_WAVY;
            case BORDER_STRIPED					: return BORDER_STYLE_STRIPED;
            case BORDER_EMBOSS					: return BORDER_STYLE_EMBOSS;
            case BORDER_ENGRAVE					: return BORDER_STYLE_ENGRAVE;
            default                             : return BORDER_STYLE_SINGLE;
        }
    }
    
    /**
     * Gets the color of this PatchRtfBorder
     * 
     * @return Returns RtfColor of this PatchRtfBorder
     */
    protected RtfColor getBorderColor() {
        return borderColor;
    }

    /**
     * Gets the position of this PatchRtfBorder
     * @return Returns the position of this PatchRtfBorder
     */
    protected int getBorderPosition() {
        return borderPosition;
    }

    /**
     * Gets the style of this PatchRtfBorder
     * 
     * @return Returns the style of this PatchRtfBorder
     */
    protected int getBorderStyle() {
        return borderStyle;
    }

    /**
     * Gets the type of this PatchRtfBorder
     * 
     * @return Returns the type of this PatchRtfBorder
     */
    protected int getBorderType() {
        return borderType;
    }

    /**
     * Gets the width of this PatchRtfBorder
     * 
     * @return Returns the width of this PatchRtfBorder
     */
    protected int getBorderWidth() {
        return borderWidth;
    }
}
