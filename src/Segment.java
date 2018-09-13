import java.awt.*;
import java.util.List;

public class Segment{
    private int id;
    private Road road;
    private double length;
    private Node nodeFrom;
    private Node nodeTo;
    private List<Location> coordinates;


    public Segment(int id,Road road,double length,Node nodeFrom,Node nodeTo,List<Location> coordinates){
        this.road = road;
        this.id = id;
        this.length = length;
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.coordinates = coordinates;
    }

    public void draw(Graphics g,Location origin,int scale,Color color){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        if(color!=Color.BLACK) g2d.setStroke(new BasicStroke(2));

        for(int i = 0; i < coordinates.size()-1;i++){
            int x1 = (int)coordinates.get(i).asPoint(origin,scale).getX();
            int y1 = (int)coordinates.get(i).asPoint(origin,scale).getY();
            int x2 = (int)coordinates.get(i+1).asPoint(origin,scale).getX();
            int y2 = (int)coordinates.get(i+1).asPoint(origin,scale).getY();
            g2d.drawLine(x1,y1,x2,y2);
        }
        g2d.setColor(Color.BLACK);


    }

    public double getLength() {
        return length;
    }

    public Node getNodeFrom() {
        return nodeFrom;
    }

    public Node getNodeTo() {
        return nodeTo;
    }

    public Road getRoad() {
        return road;
    }

    public String toString(){
        return road.getLabel();
    }
}


