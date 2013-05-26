package back;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class HyperGraph {

    protected List<HyperEdge> hEdges = new LinkedList<HyperEdge>();
    protected List<Node>  nodes = new LinkedList<Node>();
    
    protected String name;
    
    private Node start;
    private Node end;
    
    private PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>();
    
    

    public HyperGraph(Node start, Node end) {

	this.start = start;
	this.end = end;
    }

    protected static class Node {

	public String name;

	public List<HyperEdge> destinationEdges;

	public boolean visited;

	public int tempoParentCount;

	public Node(String name) {

	    this.name = name;
	    destinationEdges = new ArrayList<HyperEdge>();

	}
	
	@Override
	public String toString() {
	
	    // TODO Auto-generated method stub
	    return name;
	}
    }

    protected static class HyperEdge implements Comparable<HyperEdge> {

	public String name;

	public List<Node> heads = new ArrayList<Node>();
	public List<Node> tails = new ArrayList<Node>();
	public Set<HyperEdge> parents = new HashSet<HyperEdge>();

	public boolean visited;

	public final int numberOfEntries;
	public int currentEntriesCount;
	public final Double weight;
	public Double distance;

	public HyperEdge(String name, int numberOfEntries, Double weight) {

	    this.name = name;
	    this.numberOfEntries = numberOfEntries;
	    this.weight = weight;
	    this.distance = weight;
	}

	public int compareTo(HyperEdge o) {

	    return this.distance.compareTo(o.distance);
	}

	public void addVisitor() {

	    currentEntriesCount++;
	}

	public void addParentToChildren() {

	    for (Node node : this.tails) {
		node.tempoParentCount++;
	    }
	}

	public void substractParentToChildren() {

	    for (Node node : this.tails) {
		node.tempoParentCount--;
	    }
	}

	public boolean hasMissingParents() {

	    for (Node node : this.tails) {
		if (node.tempoParentCount == 0) {
		    return true;
		}
	    }

	    return false;
	}

	public void calculateDitance() {

	    for (HyperEdge hEdge : this.parents) {
		this.distance += hEdge.distance;
	    }
	}
	
	public void prepareParentNodes(){
	    for(Node node: this.heads){
		node.destinationEdges.add(this);
	    }
	}

	@Override
	public String toString() {

	    // TODO Auto-generated method stub
	    return name + " (" + weight + ")";
	}

    }

    public List<HyperEdge> exactAlgorithm() {

	HyperEdge hEdge = null;
	boolean hasEnded = false;

	for (HyperEdge edges : start.destinationEdges) {
	    hq.offer(edges);
	}

	while (!hq.isEmpty() && !hasEnded) {
	    hEdge = hq.poll();

	    for (Node node : hEdge.tails) {
		if (node == end) {
		    hasEnded = true;
		    break;
		}
	    }
	    procesHEdge(hEdge);

	}

	List<HyperEdge> list = new LinkedList<HyperEdge>();

	generateResult(list, hEdge);
	return list;

    }

    private void generateResult(List<HyperEdge> list, HyperEdge hEdge) {

	for (HyperEdge edge : hEdge.parents) {
	    generateResult(list, edge);
	}
	list.add(hEdge);
    }

    private void procesHEdge(HyperEdge hEdge) {

	hEdge.visited = true;

	for (Node node : hEdge.tails) {

	    if (!node.visited) {
		node.visited = true;

		for (HyperEdge edge : node.destinationEdges) {
		    edge.addVisitor();
		    edge.parents.add(hEdge);

		    if (edge.currentEntriesCount == edge.numberOfEntries) {
			removeUnnecesaryParents(edge);
			edge.calculateDitance();
			hq.offer(edge);

		    }
		}

	    }
	}

    }

    private void removeUnnecesaryParents(HyperEdge hEdge) {

	for (Node node : hEdge.heads) {
	    node.tempoParentCount = 0;
	}

	for (HyperEdge edge : hEdge.parents) {
	    edge.addParentToChildren();
	}

	Iterator<HyperEdge> it = hEdge.parents.iterator();

	while (it.hasNext()) {

	    HyperEdge current = it.next();
	    current.substractParentToChildren();

	    if (!current.hasMissingParents()) {
		it.remove();

	    } else {

		current.addParentToChildren();
	    }
	}
    }
    

}
