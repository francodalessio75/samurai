/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;


/**
 *
 * @author Franco
 */
public class CellBorderType
{
    public static final CellBorderDrawer SOLID = new SolidLine();
    public static final CellBorderDrawer DOTTED = new DottedLine();
    public static final CellBorderDrawer DASHED = new DashedLine();
    public static final CellBorderDrawer THIN_SOLID_DARK_GRAY = new thinSolidDarkGray();
    
    public CellBorderType()
    {
        
    }
    
    public static class SolidLine implements CellBorderDrawer
    {
        @Override
        public void applyLineDash(PdfCanvas canvas)
        {
        }
    }
    
    public static class DottedLine implements CellBorderDrawer {
        @Override
        public void applyLineDash(PdfCanvas canvas) {
            canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);
            canvas.setLineDash(0, 4, 2);
        }
    }
 
 
    public static class DashedLine implements CellBorderDrawer {
        @Override
        public void applyLineDash(PdfCanvas canvas) {
            canvas.setLineDash(3, 3);
        }
    }
    
    public static class thinSolidDarkGray implements CellBorderDrawer {
        @Override
        public void applyLineDash(PdfCanvas canvas) {
            canvas
                .setLineWidth(0.5F)
                .setStrokeColor(Color.DARK_GRAY);
        }
    }
    
}
