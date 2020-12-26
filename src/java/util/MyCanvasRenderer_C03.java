/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.CanvasRenderer;
import com.itextpdf.layout.renderer.IRenderer;

public class MyCanvasRenderer_C03 extends CanvasRenderer
{
 
    protected boolean full = false;

    public MyCanvasRenderer_C03(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void addChild(IRenderer renderer) {
        super.addChild(renderer);
        full = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FULL));
    }

    public boolean isFull() {
        return full;
    }
}
