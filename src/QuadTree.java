import javafx.scene.transform.Scale;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private Location origin;
    private double width;
    private double height;
    private List<Node> nodes = new ArrayList<>();
    private QuadTree root;

    private QuadTree NW;
    private QuadTree NE;
    private QuadTree SW;
    private QuadTree SE;

    public QuadTree(Location origin, double width, double height, QuadTree root) {
        this.origin = origin;
        this.width = width;
        this.height = height;
        if(root == null) this.root = this;
        else this.root = root;
    }

    public void addNode(Node n){
        if(findQuadrant(n.getLoc().x,n.getLoc().y)==null && (width > 0.2 && height > 0.2
        )){//this line will determine how small the quadrants can get
            QuadTree t = addQuadrant(n.getLoc().x,n.getLoc().y);//create a quadrant and add the node to it
            for(int i =0;i <nodes.size();i++) {//readds nodes to this tree as some may need to move down into the new quadrant
                Node temp = nodes.get(i);
                nodes.remove(temp);
                addNode(temp);
            }
            t.nodes.add(n);
        }
        else if(findQuadrant(n.getLoc().x,n.getLoc().y)!=null)findQuadrant(n.getLoc().x,n.getLoc().y).addNode(n);
        else nodes.add(n); //when there are no more quadrants left to explore add the node to the list
    }

    public Node getClosestNode(Location loc) {
        if (findQuadrant(loc.x, loc.y) != null) return findQuadrant(loc.x, loc.y).getClosestNode(loc);
        else {
            double distance = 1000;
            Node closestNode = null;
            for (Node n : nodes) {
                if (n.getLoc().distance(loc) < distance) {
                    distance = n.getLoc().distance(loc);
                    closestNode = n;
                }
            }
            if(SE !=null || SW != null || NW != null || NE != null){//if selected quadrant is empty, check children before check adjacent quadrants
                List<Node> childNodes = getChiildNodes();
                for (Node n : childNodes) {
                    if (n.getLoc().distance(loc) < distance) {
                        distance = n.getLoc().distance(loc);
                        closestNode = n;
                    }
                }

            }
            if (loc.x - origin.x < distance) {//if closer to left edge the node check quadrant to the left
                QuadTree adjacentQuadrant = root.findQuadrant(origin.x - 0.0001, loc.y);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(origin.x - 0.0001, loc.y) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(origin.x - 0.0001, loc.y);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }

            if (origin.x + width - loc.x < distance) {//if closer to right edge the node check quadrant to the right
                QuadTree adjacentQuadrant = root.findQuadrant(origin.x + width + 0.0001, loc.y);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(origin.x + width + 0.0001, loc.y) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(origin.x + width + 0.0001, loc.y);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }

                }
            }
            if (origin.y - loc.y < distance) {//if closer to top edge the node check quadrant to the top
                QuadTree adjacentQuadrant = root.findQuadrant(loc.x, origin.y + 0.0001);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(loc.x, origin.y + 0.0001) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(loc.x, this.origin.y + 0.0001);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }
            if (loc.y - (origin.y - height) < distance) {//if closer to bottom edge than the node check quadrant to the bottom
                QuadTree adjacentQuadrant = root.findQuadrant(loc.x, origin.y - height - 0.0001);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(loc.x, origin.y - height - 0.0001) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(loc.x, origin.y - height - 0.0001);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }
            if (loc.distance(origin) < distance) {//if closer to top left corner than the node check quadrant to the top left
                QuadTree adjacentQuadrant = root.findQuadrant(origin.x-0.0001, origin.y + 0.001);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(origin.x-0.0001, origin.y + 0.001) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(origin.x-0.0001, origin.y + 0.001);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }
            if (loc.distance(new Location(origin.x,origin.y-height)) < distance) {//if closer to bottom left corner than the node check quadrant to the bot left
                QuadTree adjacentQuadrant = root.findQuadrant(origin.x-0.0001, origin.y -height - 0.001);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(origin.x-0.0001, origin.y -height - 0.001) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(origin.x-0.0001, origin.y -height - 0.001);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }

            if (loc.distance(new Location(origin.x+width,origin.y)) < distance) {//if closer to top right corner than the node check quadrant to the top right
                QuadTree adjacentQuadrant = root.findQuadrant(origin.x+width+0.0001, origin.y + 0.001);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(origin.x+width+0.0001, origin.y + 0.001) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(origin.x+width+0.0001, origin.y + 0.001);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }
            if (loc.distance(new Location(origin.x+width,origin.y-height)) < distance) {//if closer to bot right corner than the node check quadrant to the bot right
                QuadTree adjacentQuadrant = root.findQuadrant(origin.x+width+0.0001, origin.y -height - 0.001);
                if (adjacentQuadrant != null) {
                    while (adjacentQuadrant.findQuadrant(origin.x+width+0.0001, origin.y -height - 0.001) != null) {
                        adjacentQuadrant = adjacentQuadrant.findQuadrant(origin.x+width+0.0001, origin.y -height - 0.001);
                    }
                    for (Node n : adjacentQuadrant.nodes) {
                        if (n.getLoc().distance(loc) < distance) {
                            distance = n.getLoc().distance(loc);
                            closestNode = n;
                        }
                    }
                }
            }
            return closestNode;
        }
    }



    private QuadTree findQuadrant(double x,double y){
        if(x <= origin.x+width/2 && y >= origin.y-height/2) return NW;
        else if(x <= origin.x+width/2 && y < origin.y-height/2) return SW;
        else if(x > origin.x+width/2 && y >= origin.y-height/2) return NE;
        else return SE;

    }

    private QuadTree addQuadrant(double x,double y){
        if(x <= origin.x+width/2 && y >= origin.y-height/2){
            if(NW==null) NW = new QuadTree(origin,width/2,height/2,root);
            return NW;
        }
        else if(x <= origin.x+width/2 && y < origin.y-height/2) {
            if(SW==null) SW = new QuadTree(new Location(origin.x,origin.y-height/2),width/2,height/2,root);
            return SW;
        }
        else if(x > origin.x+width/2 && y >= origin.y-height/2) {
            if(NE==null) NE = new QuadTree(new Location(origin.x+width/2,origin.y),width/2,height/2,root);
            return NE;
        }
        else{
            if(SE == null)SE = new QuadTree(new Location(origin.x+width/2,origin.y-height/2),width/2,height/2,root);
            return SE;
        }


    }

    private List<Node> getChiildNodes(){
        List<Node> childNodes = new ArrayList<>(nodes);
        if(SE!=null) childNodes.addAll(SE.getChiildNodes());
        if(NE!=null) childNodes.addAll(NE.getChiildNodes());
        if(SW!=null) childNodes.addAll(SW.getChiildNodes());
        if(NW!=null) childNodes.addAll(NW.getChiildNodes());
        return childNodes;

    }

    public void draw(Graphics g,Location Origin,int scale){
        int x = origin.asPoint(Origin,scale).x;
        int y = origin.asPoint(Origin,scale).y;
        g.drawRect(x,y,(int)(width*scale),(int)(height* scale));
        if(SE!=null) SE.draw(g,Origin,scale);
        if(NE!=null)NE.draw(g,Origin,scale);
        if(NW!=null)NW.draw(g,Origin,scale);
        if(SW!=null)SW.draw(g,Origin,scale);
    }



}
