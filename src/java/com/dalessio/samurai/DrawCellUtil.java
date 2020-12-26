/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

/**
 *
 * @author Franco D'Alessio
 * 
 */
public class DrawCellUtil extends CellRenderer 
{
    CellBorderDrawer[] borders;

    public DrawCellUtil(Cell modelElement, CellBorderDrawer[] borders) {
        super(modelElement);
        this.borders = new CellBorderDrawer[borders.length];
        for (int i = 0; i < this.borders.length; i++) {
            this.borders[i] = borders[i];
        }
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);
        PdfCanvas canvas = drawContext.getCanvas();
        Rectangle position = getOccupiedAreaBBox();
        canvas.saveState();
        //top
        if (null != borders[0]) {
            canvas.saveState();
            borders[0].applyLineDash(canvas);
            canvas.moveTo(position.getRight(), position.getTop());
            canvas.lineTo(position.getLeft(), position.getTop());
            canvas.stroke();
            canvas.restoreState();
        }
        //right
        if (null != borders[1]) {
            canvas.saveState();
            borders[1].applyLineDash(canvas);
            canvas.moveTo(position.getRight(), position.getTop());
            canvas.lineTo(position.getRight(), position.getBottom());
            canvas.stroke();
            canvas.restoreState();
        }
        //bottom
        if (null != borders[2]) {
            canvas.saveState();
            borders[2].applyLineDash(canvas);
            canvas.moveTo(position.getRight(), position.getBottom());
            canvas.lineTo(position.getLeft(), position.getBottom());
            canvas.stroke();
            canvas.restoreState();
        }
        //left
        if (null != borders[3]) {
            canvas.saveState();
            borders[3].applyLineDash(canvas);
            canvas.moveTo(position.getLeft(), position.getTop());
            canvas.lineTo(position.getLeft(), position.getBottom());
            canvas.stroke();
            canvas.restoreState();
        }
        canvas.stroke();
        canvas.restoreState();
    }
}