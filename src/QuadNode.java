import java.util.ArrayList;
import java.util.List;

public class QuadNode {
    Location origin;
    Double width;
    Double height;
    List<Node> nodes = new ArrayList<>();

    public QuadNode(Location origin, Double width, Double height) {
        this.origin = origin;
        this.width = width;
        this.height = height;
    }


}
