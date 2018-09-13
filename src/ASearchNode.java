public class ASearchNode implements Comparable<ASearchNode>{
    private Node node;
    private ASearchNode prev;
    private Segment segmentToPrev;
    private double distFromStart;
    private double distFromGoal;

    public ASearchNode(Node node, ASearchNode prev,Segment prevSeg, double distFromStart, double distFromGoal) {
        this.node = node;
        this.prev = prev;
        this.segmentToPrev = prevSeg;
        this.distFromStart = distFromStart;
        this.distFromGoal = distFromGoal;
    }


    public Node getNode() {
        return node;
    }

    public ASearchNode getPrev() {
        return prev;
    }

    public Segment getSegmentToPrev() {
        return segmentToPrev;
    }

    public double getDistFromStart() {
        return distFromStart;
    }

    public double getDistFromGoal() {
        return distFromGoal;
    }


    public int compareTo(ASearchNode e) {
        return distFromGoal < e.getDistFromGoal() ? -1 : 1;

    }
}
