package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class ResizableCanvas extends Canvas {
    public interface PaintListener {
        void paint(GraphicsContext context);
    }

    private PaintListener paintListener;

    public PaintListener getPaintListener() {
        return this.paintListener;
    }

    public void setPaintListener(PaintListener paintListener) {
        this.paintListener = paintListener;
    }

    @Override
    public double minHeight(double width) {
        return 10;
    }

    @Override
    public double maxHeight(double width) {
        return 10000;
    }

    @Override
    public double prefHeight(double width) {
        //return getHeight();
        return minHeight(width);
    }

    @Override
    public double minWidth(double height) {
        return 10;
    }

    //
    @Override
    public double maxWidth(double height) {
        return 10000;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        super.setWidth(width);
        super.setHeight(height);
        paint();
    }

    public void paint() {
        if (paintListener != null)
            paintListener.paint(getGraphicsContext2D());
    }

}
