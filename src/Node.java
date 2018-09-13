import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    private int id;
    private Location loc;
    private List<Segment> outgoingEdges = new ArrayList<>();
    private List<Segment> incomingEdges = new ArrayList<>();
    private Set<Road> roads = new HashSet<>();

    private int count;
    private int reachBack;
    private Node parent;
    private List<Node> children = new ArrayList<>();

    public Node(int id, Location loc) {
        this.id = id;
        this.loc = Location.newFromLatLon(loc.y, loc.x);
        this.count = Integer.MAX_VALUE;
    }

    public void addOutgoingEdge(Segment s) {
        outgoingEdges.add(s);
        roads.add(s.getRoad());
    }

    public void addIncomingEdge(Segment s) {
        incomingEdges.add(s);
        roads.add(s.getRoad());
    }

    public List<Segment> getEdges() {

        return outgoingEdges;
    }

    public Segment getSegmentFromNeighbour(Node n){
        for(Segment s: outgoingEdges){
            if(s.getNodeFrom().equals(n)||s.getNodeTo().equals(n)) return s;
        }
        return null;
    }
    public void draw(Graphics g, Location origin, int scale, Color color) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(color);
        int radius = 2;
        if(color!= Color.BLACK) {
            g2d.setStroke(new BasicStroke(10));
            radius = 3;
        }
        Point p = this.loc.asPoint(origin, scale);
        g2d.fillOval((int) p.getX() - radius, (int) p.getY() - radius, 2*radius, 2*radius);
        g2d.setColor(Color.BLACK);
        if(color!= Color.BLACK) g2d.setStroke(new BasicStroke(1));
    }

    public HashSet<Node> getNeigbouringNodes(){
        HashSet<Node> neighbours = new HashSet<>();
        for(Segment s:outgoingEdges){
            neighbours.add(s.getNodeTo());
        }
        for(Segment s:incomingEdges){
            neighbours.add(s.getNodeFrom());
        }
        return neighbours;
    }

    public Location getLoc() {
        return loc;
    }

    //=======================================================================
    //
    // Articulation point methods
    //
    //=======================================================================
    public int getCount() {
        return count;
    }

    public int getReachBack() {
        return reachBack;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setReachBack(int reachBack) {
        this.reachBack = reachBack;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setChildren(){
        for(Segment s:outgoingEdges){
            if(s.getNodeTo()!= parent) children.add(s.getNodeTo());
        }
        for(Segment s:incomingEdges){
            if(s.getNodeFrom()!= parent) children.add(s.getNodeFrom());
        }
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getAndRemoveChild(){
        Node c = children.get(0);
        children.remove(c);
        return c;
    }


    public String toString() {
        String result = "node ID: " + id + "\n";
        for (Road r : roads) result += " " + r.toString();

        return result;
    }
}


