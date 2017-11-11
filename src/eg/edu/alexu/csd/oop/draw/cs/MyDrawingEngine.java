package eg.edu.alexu.csd.oop.draw.cs;

import eg.edu.alexu.csd.oop.draw.DrawingEngine;
import eg.edu.alexu.csd.oop.draw.Shape;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MyDrawingEngine implements DrawingEngine {

    private static MyDrawingEngine drawingEngine = null;

    private static LinkedList<Shape> shapes = new LinkedList<>();
    private final Canvas canvas = new Canvas();
    private MyDrawingEngine() {};
    private final Stack<Command> done = new Stack<>();
    private final Stack<Command> undone = new Stack<>();

    public static synchronized MyDrawingEngine getInstance()
    {
        if(drawingEngine == null)
        {
            drawingEngine = new MyDrawingEngine();
        }
        return drawingEngine;
    }

    public void setShapes(final LinkedList<Shape> newShapes) {
        this.shapes = newShapes;
    }

    @Override
    public void refresh(final Graphics canvas) {
        for (final Shape shape : shapes) {
            shape.draw(canvas);
        }
    }

    public void refresh(final GraphicsContext graphicsContext) {
        for (final Shape shape : shapes) {
            ((TheShape)shape).draw(graphicsContext);
        }
    }

    @Override
    public void addShape(final Shape shape) {
        undone.empty();
        final Command addShape = new AddShape(shape);
        addShape.execute();
        done.push(addShape);
    }

    @Override
    public void removeShape(final Shape shape) {
        undone.empty();
        final Command removeShape = new RemoveShape(shape);
        removeShape.execute();
        done.push(removeShape);
    }

    @Override
    public void updateShape(final Shape oldShape, final Shape newShape) {
        undone.empty();
        final Command updateShape = new UpdateShape(oldShape,newShape);
        updateShape.execute();
        done.push(updateShape);
    }

    public void permatlyAddShape(final Shape shape) {
        undone.empty();
        final Command addShape = new AddShape(shape);
        addShape.execute();
    }

    public void permatlyRemoveShape(final Shape shape) {
        undone.empty();
        final Command removeShape = new RemoveShape(shape);
        removeShape.execute();
    }

    public void permatlyUpdateShape(final Shape oldShape, final Shape newShape) {
        undone.empty();
        final Command updateShape = new UpdateShape(oldShape,newShape);
        updateShape.execute();
    }
    @Override
    public Shape[] getShapes() {
        return shapes.toArray(new Shape[shapes.size()]);
    }

    @Override
    public List<Class<? extends Shape>> getSupportedShapes() {
        List<Class<? extends Shape>> supportedShapes = new LinkedList<>();
        Class<?>[] classes = Shape.class.getClasses();
        for (Class aClass :classes) {
            if(aClass.getSuperclass() == TheShape.class) {
                supportedShapes.add(aClass);
            }
        }
        return supportedShapes;
    }

    @Override
    public void undo() {
        if(done.size() > 0)
        {
            final Command command = done.pop();
            command.reverse();
            undone.push(command);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @Override
    public void redo() {
        if(undone.size() > 0)
        {
            final Command command = undone.pop();
            command.execute();
            done.push(command);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    @Override
    public void save(final String path) {
        Saver.save(path,shapes);
    }

    @Override
    public void load(final String path) {
        shapes = Saver.load(path);
    }
}