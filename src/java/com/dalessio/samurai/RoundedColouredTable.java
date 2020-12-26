/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dalessio.samurai;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.TableRenderer;

/**
 *
 * @author Franco
 */
public class RoundedColouredTable extends TableRenderer
{
    //constructor that takes a table and in the body assigns to the table the renderer
    public RoundedColouredTable(Table modelElement) {
        super(modelElement);
    }

    @Override
    public void drawBackground(DrawContext drawContext)
    {
        Background background = this.<Background>getProperty(Property.BACKGROUND);
        if (background != null) {
            Rectangle bBox = getOccupiedAreaBBox();
            boolean isTagged =
                drawContext.isTaggingEnabled()
                && getModelElement() instanceof IAccessibleElement;
            if (isTagged) {
                drawContext.getCanvas().openTag(new CanvasArtifact());
            }
            Rectangle bgArea = applyMargins(bBox, false);
            if (bgArea.getWidth() <= 0 || bgArea.getHeight() <= 0) {
                return;
            }
            drawContext.getCanvas().saveState()
                .setFillColor(background.getColor())
                .roundRectangle(
                    (double)bgArea.getX() - background.getExtraLeft(),
                    (double)bgArea.getY() - background.getExtraBottom(),
                    (double)bgArea.getWidth()+background.getExtraLeft() + background.getExtraRight(),
                    (double)bgArea.getHeight()+ background.getExtraTop() + background.getExtraBottom(),
                    5
                )
                .fill().restoreState();
            if (isTagged) {
                drawContext.getCanvas().closeTag();
            }
        }
    }
}
    

