import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class Graph extends GUI {
    private Map<Integer, Node> nodeMap = new HashMap<>();
    private Map<Integer, Road> roadMap = new HashMap<>();
    private Set<Segment> segmentSet = new HashSet<>();
    private Set<polygon> polygonSet = new HashSet<>();
    private Map<Segment,Set<Segment>> restrictionsMap = new HashMap<>();


    private TrieNode roadNames;
    private QuadTree intersecPointLoc;

    private double minLong = Double.MAX_VALUE;
    private double maxLat = -Double.MAX_VALUE;
    private double maxLong = -Double.MAX_VALUE;
    private double minLat = Double.MAX_VALUE;


    private final double MAX_SPEED_LIMIT = 110*1.06;//max speed * quality road bonus
    private boolean distanceMode = true;
    private boolean pathFIndingMode = false;


    private Location origin;
    private double zoomFactor = 1;
    private double vertPan = 0;
    private double horzPan = 0;
    private int scale;

    private Node selectedNode = null;
    private Node prevSelectedNode = null;
    private List<String> selectedRoads = new ArrayList<>();
    private HashSet<Segment> selectedSegments = new HashSet<>();
    HashSet<Node> artPts = new HashSet<>();

    public Graph(){


    }
    @Override
    protected void redraw(Graphics g) {
        //origin is dependant on the level of zoom and horizontal/vertical panning
        Double lat = (maxLat+minLat)/2+(maxLat-minLat)/(2*zoomFactor)+vertPan;
        Double lon = (maxLong+minLong)/2-(maxLong-minLong)/(2*zoomFactor)+horzPan;
        origin = Location.newFromLatLon(lat,lon);
        //the scale is calculated to allow every node to be seen when the zoom is set to default

        scale = Math.min((int)(getDrawingAreaDimension().getHeight()/((maxLat-minLat)*111)*zoomFactor),(int)(getDrawingAreaDimension().getWidth()/((maxLong-minLong)*88.649)*zoomFactor));
        for(polygon p:polygonSet){
            p.draw(g,origin,scale);
        }


        for(Segment s:segmentSet){//draw unselected segments first
            if(!((selectedRoads!= null && selectedRoads.contains(s.getRoad().getLabel()))||selectedSegments.contains(s)))
            s.draw(g,origin,scale,Color.BLACK);
        }

        for(Node n:nodeMap.values()){//then unselected nodes
            if(artPts.contains(n)){
                n.draw(g, origin, scale, Color.RED);
            }
            else if(!(n.equals(selectedNode)|| n.equals(prevSelectedNode))){
                n.draw(g, origin, scale, Color.BLACK);
            }
        }

        for(Segment s:restrictionsMap.keySet()){//then selected segments
            s.draw(g,origin,scale,Color.CYAN);
            for(Segment s1: restrictionsMap.get(s)){
                s1.draw(g,origin,scale,Color.BLUE);
            }
        }

        for(Segment s:segmentSet){//then selected segments
            if(((selectedRoads!= null && selectedRoads.contains(s.getRoad().getLabel()))))
                s.draw(g,origin,scale,Color.RED);
            else if(selectedSegments.contains(s)){
                s.draw(g,origin,scale,Color.MAGENTA);
            }
        }



        for(Node n:nodeMap.values()) {
            if (n.equals(selectedNode) || n.equals(prevSelectedNode)) {
                n.draw(g, origin, scale, Color.MAGENTA);
            }
        }

        //the line below can be uncommented out to display the quadtree boundaries
        //if(intersecPointLoc!=null)intersecPointLoc.draw(g,origin,scale);

    }

    //prints the details of the intersection closest to the where the mouse was clicked providing it was within reasonable
    //distance from a node
    @Override
    protected void onClick(MouseEvent e) {
        selectedSegments.clear();
        Location mouseClick = Location.newFromPoint(new Point(e.getX(),e.getY()),origin,scale);
        Node closestNode = intersecPointLoc.getClosestNode(mouseClick);

        if(closestNode!= null){
            getTextOutputArea().setText(null);
            getTextOutputArea().append(closestNode.toString()+"\n");
            prevSelectedNode = selectedNode;
            selectedNode = closestNode;
        }
        if(prevSelectedNode!=null && pathFIndingMode){
            ASearchNode goalNode = findShortestPath(prevSelectedNode,selectedNode);
            getTextOutputArea().setText(null);
            LinkedHashMap<String,Double> pathDescription= new LinkedHashMap<>();
            String result= "";
            Double totLength = 0.0;
            if(distanceMode) {
                while (goalNode.getSegmentToPrev() != null) {
                    Segment s = goalNode.getSegmentToPrev();
                    selectedSegments.add(s);
                    if (!pathDescription.containsKey(s.toString())) {
                        pathDescription.put(s.toString(), s.getLength());
                    } else {
                        pathDescription.replace(s.toString(), pathDescription.get(s.toString()) + s.getLength());//sums the length of the segments with matching road names.
                    }
                    totLength += s.getLength();
                    goalNode = goalNode.getPrev();
                }
                for (String s : pathDescription.keySet()) {
                    result = s + String.format(": %.2fkm\n", pathDescription.get(s)) + result;
                }
                getTextOutputArea().append(result);
                getTextOutputArea().append("\n" + String.format("Total distance: %.2fkm", totLength));
            }
            else{
                while (goalNode.getSegmentToPrev() != null) {
                    Segment s = goalNode.getSegmentToPrev();
                    selectedSegments.add(s);
                    if (!pathDescription.containsKey(s.toString())) {
                        pathDescription.put(s.toString(), s.getLength()/s.getRoad().getSpeed());
                    } else {
                        pathDescription.replace(s.toString(), pathDescription.get(s.toString()) + s.getLength()/s.getRoad().getSpeed());//sums the length of the segments with matching road names.
                    }
                    totLength += s.getLength()/s.getRoad().getSpeed();
                    goalNode = goalNode.getPrev();
                }
                for (String s : pathDescription.keySet()) {
                    result = s + String.format(": %.2fmin\n", pathDescription.get(s)*60) + result;
                }
                getTextOutputArea().append(result);
                getTextOutputArea().append("\n" + String.format("Total distance: %.2fmin", totLength*60));
            }
        }





    }

    @Override
    protected void onSearch() {
        selectedRoads.clear();
        getComboBox().removeAllItems();
        JTextField search= getSearchBox();
        String label = search.getText();

        List<Road> matches = roadNames.get(label.toCharArray());
        if(matches==null||matches.isEmpty()) {
            matches = roadNames.getAll(label.toCharArray());
        }


        int items = 0;
        if(matches!=null) {
            for (Road r : matches) {
                selectedRoads.add(r.getLabel());
                if(items < 12) {
                    getComboBox().removeItem(r.getLabel());
                    getComboBox().addItem(r.getLabel());
                    items++;
                }
            }
        }






    }

    private ASearchNode findShortestPath(Node start, Node goal){
        HashSet<Node> visitedNodes= new HashSet<>();
        PriorityQueue<ASearchNode> path = new PriorityQueue<>();
        ASearchNode aSearchNode;

        if(distanceMode) {
            path.add(new ASearchNode(start,null,null,0,start.getLoc().distance(goal.getLoc())));
            aSearchNode = path.peek();
            while (!path.isEmpty()) {
                aSearchNode = path.poll();
                Node node = aSearchNode.getNode();
                if (!visitedNodes.contains(node)) {
                    visitedNodes.add(node);
                    if (node.equals(goal)) break;
                    for (Segment s : node.getEdges()) {
                        if(restrictionsMap.get(aSearchNode.getSegmentToPrev())!=null&&restrictionsMap.get(aSearchNode.getSegmentToPrev()).contains(s)) continue;
                        if (!visitedNodes.contains(s.getNodeFrom()) || !visitedNodes.contains(s.getNodeTo())) {//check both the from/to nodes as we don't know which one will be the neighbour node
                            Node neigh = (s.getNodeFrom().equals(node) ? s.getNodeTo() : s.getNodeFrom());
                            Double distFromStart = aSearchNode.getDistFromStart() + s.getLength();
                            Double distFromGoal = distFromStart + neigh.getLoc().distance(goal.getLoc());
                            path.add(new ASearchNode(neigh, aSearchNode, s, distFromStart, distFromGoal));
                        }

                    }
                }
            }
        }
        else{
            path.add(new ASearchNode(start,null,null,0,start.getLoc().distance(goal.getLoc())/MAX_SPEED_LIMIT));
            aSearchNode = path.peek();
            while (!path.isEmpty()) {
                aSearchNode = path.poll();
                Node node = aSearchNode.getNode();
                if (!visitedNodes.contains(node)) {
                    visitedNodes.add(node);
                    if (node.equals(goal)) break;
                    for (Segment s : node.getEdges()) {
                        if(restrictionsMap.get(aSearchNode.getSegmentToPrev())!=null&&restrictionsMap.get(aSearchNode.getSegmentToPrev()).contains(s)) continue;
                        if (!visitedNodes.contains(s.getNodeFrom()) || !visitedNodes.contains(s.getNodeTo())) {//check both the from/to nodes as we don't know which one will be the neighbour node
                            Node neigh = (s.getNodeFrom().equals(node) ? s.getNodeTo() : s.getNodeFrom());
                            Double distFromStart = aSearchNode.getDistFromStart() + s.getLength()/s.getRoad().getRefinedSpeed();
                            Double distFromGoal = distFromStart + neigh.getLoc().distance(goal.getLoc())/MAX_SPEED_LIMIT;
                            path.add(new ASearchNode(neigh, aSearchNode, s, distFromStart, distFromGoal));
                        }
                    }
                }
            }

        }

        return aSearchNode;
    }

    @Override
    protected void onMove(Move m) {
        if(m.equals(Move.ZOOM_IN)) zoomFactor*=2;
        if(m.equals(Move.ZOOM_OUT) && zoomFactor > 0.5) zoomFactor/=2;
        if(m.equals(Move.EAST)) horzPan+=(maxLong-minLong)/(4*zoomFactor);
        if(m.equals(Move.WEST)) horzPan-=(maxLong-minLong)/(4*zoomFactor);
        if(m.equals(Move.NORTH)) vertPan+=(maxLat-minLat)/(4*zoomFactor);
        if(m.equals(Move.SOUTH)) vertPan-=(maxLat-minLat)/(4*zoomFactor);
    }

    private void findArticulationPoints(){
        int numSubTrees = 0;

        for(Node root: nodeMap.values()) {
            if (root.getCount() == Integer.MAX_VALUE) {
                root.setCount(0);
                for (Node n : root.getNeigbouringNodes()) {
                    if (n.getCount() == Integer.MAX_VALUE) {
                        iterArtPts(n, root);
                        numSubTrees++;
                    }
                }
                if (numSubTrees > 1) artPts.add(root);
                numSubTrees = 0;
            }
        }
    }

    private void iterArtPts(Node firstNode,Node root){
        firstNode.setParent(root);
        firstNode.setCount(1);
        firstNode.setReachBack(1);
        firstNode.setChildren();
        Stack<Node> s = new Stack<>();
        s.push(firstNode);
        while(!s.isEmpty()){
            Node n = s.peek();
            if(!n.getChildren().isEmpty()){
                Node child = n.getAndRemoveChild();
                if (child.getCount() < Integer.MAX_VALUE){
                    n.setReachBack(Math.min(child.getCount(),n.getReachBack()));
                }
                else {
                    child.setParent(n);
                    child.setCount(n.getCount()+1);
                    child.setReachBack(n.getCount()+1);
                    child.setChildren();
                    s.push(child);
                }
            }
            else{
                if(!n.equals(firstNode)){
                    n.getParent().setReachBack(Math.min(n.getReachBack(),n.getParent().getReachBack()));
                    if(n.getReachBack()>=n.getParent().getCount()) artPts.add(n.getParent());
                }
                s.pop();
            }
        }

    }

    protected void setDistanceMode(Boolean distanceMode) {
        this.distanceMode = distanceMode;
    }

    protected void togglePathFindingMode(){
        pathFIndingMode = !pathFIndingMode;

    }

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions) {
        segmentSet.clear();
        roadMap.clear();
        nodeMap.clear();
        polygonSet.clear();
        roadNames = new TrieNode();



        BufferedReader nodeReader = null;
        BufferedReader roadReader = null;
        BufferedReader segmentsReader = null;
        BufferedReader polygonReader = null;
        BufferedReader restReader = null;
        try{
            //reads the nodes data into the node map
            nodeReader = new BufferedReader(new FileReader(nodes));
            String nodeLine = nodeReader.readLine();
            while(nodeLine!=null){//loads node data into node map and finds the max/min latitude and longitude
                String[] nodeData = nodeLine.split("\t");
                nodeMap.put(Integer.parseInt(nodeData[0]),new Node(Integer.parseInt(nodeData[0]),
                        new Location(Double.parseDouble(nodeData[2]),Double.parseDouble(nodeData[1]))));
                if(Double.parseDouble(nodeData[1])>maxLat)maxLat = Double.parseDouble(nodeData[1]);
                if(Double.parseDouble(nodeData[2])<minLong)minLong = Double.parseDouble(nodeData[2]);
                if(Double.parseDouble(nodeData[1])<minLat)minLat = Double.parseDouble(nodeData[1]);
                if(Double.parseDouble(nodeData[2])>maxLong)maxLong = Double.parseDouble(nodeData[2]);
                nodeLine = nodeReader.readLine();
            }

            Location topLeft = Location.newFromLatLon(maxLat,minLong);
            Location botRight = Location.newFromLatLon(minLat,maxLong);
            intersecPointLoc = new QuadTree(topLeft,botRight.x-topLeft.x,topLeft.y-botRight.y,null);
            for(Node n:nodeMap.values()) intersecPointLoc.addNode(n);

            //reads the road data into the road map
            roadReader = new BufferedReader(new FileReader(roads));
            roadReader.readLine();
            String roadLine = roadReader.readLine();
            while(roadLine!=null){//loads the road data into the road map
                String[] roadData = roadLine.split("\t");
                Road road = new Road(Integer.parseInt(roadData[0]),Integer.parseInt(roadData[1]),roadData[2],roadData[3],
                        Integer.parseInt(roadData[4]),Integer.parseInt(roadData[5]),Integer.parseInt(roadData[6])
                        ,Integer.parseInt(roadData[7]),Integer.parseInt(roadData[8]),Integer.parseInt(roadData[9]));
                roadMap.put(Integer.parseInt(roadData[0]),road);
                roadLine = roadReader.readLine();
            }

            //adds roads to trie for fast search results
            for(Road r:roadMap.values()) roadNames.add(r.getLabel().toCharArray(),r);

            //reads the segment data into the segment set
            segmentsReader = new BufferedReader(new FileReader(segments));
            segmentsReader.readLine();
            String segmentLine = segmentsReader.readLine();
            while(segmentLine!=null){
                String[] segmentData = segmentLine.split("\t");
                int roadId = Integer.parseInt(segmentData[0]);
                Road road = roadMap.get(roadId);
                Double length = Double.parseDouble(segmentData[1]);
                Node nodeFrom = nodeMap.get(Integer.parseInt(segmentData[2]));
                Node nodeTo = nodeMap.get(Integer.parseInt(segmentData[3]));
                List<Location> coordinates = new ArrayList<>();
                for(int i = 4; i < segmentData.length;i=i+2){
                    coordinates.add(Location.newFromLatLon(Double.parseDouble(segmentData[i]),Double.parseDouble(segmentData[i+1])));
                }
                Segment seg = new Segment(roadId,road,length,nodeFrom,nodeTo,coordinates);
                segmentSet.add(seg);
                //add segments to the intersection edge list
                nodeFrom.addOutgoingEdge(seg);
                nodeTo.addIncomingEdge(seg);
                if(!road.isOneWay()){
                    nodeFrom.addIncomingEdge(seg);
                    nodeTo.addOutgoingEdge(seg);
                }

                segmentLine = segmentsReader.readLine();
            }
            if(polygons!=null) {
                polygonReader = new BufferedReader(new FileReader(polygons));
                String polygonLine = polygonReader.readLine();
                while (polygonLine != null) {
                    String type = polygonReader.readLine().split("=")[1];
                    polygonLine = polygonReader.readLine();
                    if(polygonLine.startsWith("L")){
                        polygonLine = polygonReader.readLine();
                    }
                    if(polygonLine.startsWith("E")){
                        polygonLine = polygonReader.readLine();
                    }
                    if(polygonLine.startsWith("C")){
                        polygonLine = polygonReader.readLine();
                    }
                    StringBuffer result = new StringBuffer();
                    while (!polygonLine.equals("[END]")) {
                        result.append(polygonLine);
                        polygonLine = polygonReader.readLine();
                        if(polygonLine.startsWith("D")){
                            String parts[] = result.toString().split("[=,()]");
                            List<Location> vertices = new ArrayList<>();
                            for (int i = 2; i < parts.length; i++) {
                                vertices.add(Location.newFromLatLon(Double.parseDouble(parts[i]), Double.parseDouble(parts[i + 1])));
                                i=i+3;
                            }
                            polygonSet.add(new polygon(type, vertices));
                            result = new StringBuffer();
                        }
                    }
                    String parts[] = result.toString().split("[=,()]");
                    List<Location> vertices = new ArrayList<>();
                    for (int i = 2; i < parts.length; i++) {
                        vertices.add(Location.newFromLatLon(Double.parseDouble(parts[i]), Double.parseDouble(parts[i + 1])));
                        i=i+3;
                    }
                    polygonSet.add(new polygon(type, vertices));
                    polygonReader.readLine();
                    polygonLine = polygonReader.readLine();

                }
            }
            if(restrictions!=null) {
                restReader = new BufferedReader(new FileReader(restrictions));
                restReader.readLine();
                String restLine =restReader.readLine();
                while (restLine != null){
                    String[] restData = restLine.split("\t");
                    Segment seg1 = nodeMap.get(Integer.parseInt(restData[0])).getSegmentFromNeighbour(nodeMap.get(Integer.parseInt(restData[2])));
                    Segment seg2 = nodeMap.get(Integer.parseInt(restData[2])).getSegmentFromNeighbour(nodeMap.get(Integer.parseInt(restData[4])));
                    if(restrictionsMap.get(seg1)==null){
                        HashSet<Segment> restSegs = new HashSet<>();
                        restSegs.add(seg2);
                        restrictionsMap.put(seg1,restSegs);
                    }
                    else{
                        restrictionsMap.get(seg1).add(seg2);
                    }
                    restLine = restReader.readLine();

                    }

                }

            findArticulationPoints();
            selectedNode = null;
            prevSelectedNode = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if (nodeReader != null)
                    nodeReader.close();
                if (roadReader != null)
                    roadReader.close();
                if(segmentsReader !=null)
                    segmentsReader.close();
                if(polygonReader !=null)
                    polygonReader.close();

            }
            catch (IOException e)
            {
                System.out.println("Error in closing the BufferedReader");
            }
        }

    }

    public static void main(String[] args){
        new Graph();
    }
}
