import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrieNode {
    private List<Road> roads;
    private HashMap<Character, TrieNode> children;

    public TrieNode(){
        roads = new ArrayList<>();
        children = new HashMap<>();
    }

    public void add(char[] word, Road road) {
        TrieNode node = this;
        for (char c : word) {
            if (!node.children.containsKey(c)) {
                node.children.put(c, new TrieNode());
            }
            node = node.children.get(c);
        }
        node.roads.add(road);
    }

    public List<Road> get(char[] word) {
        TrieNode node = this;
        for (char c : word) {
            if (!node.children.containsKey(c))
                return null;
            node = node.children.get(c);
        }
        return node.roads;
    }

    public List<Road> getAll(char[] prefix) {
        List<Road> results = new ArrayList<Road>();
        TrieNode node = this;
        for (char c : prefix) {
            if (!node.children.containsKey(c))
                return null;
            node = node.children.get(c);
        }
        getAllFrom(node, results);
        return results;
    }

    private void getAllFrom(TrieNode node, List<Road> results) {
        results.addAll(node.roads);

        for (TrieNode child: node.children.values()) {
            getAllFrom(child, results);
        }
    }
}
