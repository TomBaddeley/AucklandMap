import java.awt.*;
import java.util.List;

public class polygon {
    private String type;
    private List<Location> vertices;

    public polygon(String type, List<Location> vertices) {
        this.type = type;
        this.vertices = vertices;
    }

    public void draw(Graphics g, Location origin, int scale){
        if(type.equals("0x1a")) g.setColor(Color.BLUE);
        else if(type.equals("0x13")) g.setColor(Color.gray);
        else if(type.equals("0x17")) g.setColor(Color.green);
        else if(type.equals("0x18")) g.setColor(Color.green);
        else if(type.equals("0x19")) g.setColor(Color.green);
        else if(type.equals("0x40")) g.setColor(Color.blue);
        else if(type.equals("0x28")) g.setColor(Color.blue);//ocean
        else if(type.equals("0x2")) g.setColor(Color.green);
        else if(type.equals("0xa")) g.setColor(Color.green);
        else if(type.equals("0x7")) g.setColor(Color.green);
        else if(type.equals("0x3c")) g.setColor(Color.blue);
        else if(type.equals("0x46")) g.setColor(Color.blue);
        else if(type.equals("0x3e")) g.setColor(Color.blue);
        else if(type.equals("0x48")) g.setColor(Color.blue);
        else if(type.equals("0x1a")) g.setColor(Color.gray);
        else if(type.equals("0xb")) g.setColor(Color.white);
        else if(type.equals("0xe")) g.setColor(Color.gray);
        else if(type.equals("0x8")) g.setColor(Color.gray);
        else if(type.equals("0x41")) g.setColor(Color.blue);
        else if(type.equals("0x5")) g.setColor(Color.gray);
        else if(type.equals("0x1a")) g.setColor(Color.green);//national park
        else if(type.equals("0x45")) g.setColor(Color.blue);
        else if(type.equals("0x50")) g.setColor(Color.green);//woods
        else if(type.equals("0x1e")) g.setColor(Color.gray);//park
        else  g.setColor(Color.gray);



        int[] xPoints = new int[vertices.size()];
        int[] yPoints = new int[vertices.size()];
        for(int i = 0;i < vertices.size(); i++){
            xPoints[i] = (int)vertices.get(i).asPoint(origin,scale).getX();
            yPoints[i] = (int)vertices.get(i).asPoint(origin,scale).getY();
        }
        g.fillPolygon(xPoints,yPoints,vertices.size());
        g.setColor(Color.black);

    }
}
