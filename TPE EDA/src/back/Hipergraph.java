package back;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Hipergraph {

    private Node start;
    private Node end;
    private PriorityQueue<Hiperedge> hq = new PriorityQueue<Hiperedge>();

    public Hipergraph(Node start, Node end) {

	this.start = start;
	this.end = end;
    }

    protected static class Node {

	public String name;

	public List<Hiperedge> destinationEdges;

	public boolean visited;

	public int tempoParentCount;

	public Node(String name) {

	    this.name = name;
	    destinationEdges = new ArrayList<Hiperedge>();

	}
	
	@Override
	public String toString() {
	
	    // TODO Auto-generated method stub
	    return name;
	}
    }

    protected static class Hiperedge implements Comparable<Hiperedge> {

	public String name;

	public List<Node> heads = new ArrayList<Node>();
	public List<Node> tails = new ArrayList<Node>();
	public Set<Hiperedge> parents = new HashSet<Hiperedge>();

	public boolean visited;

	public final int numberOfEntries;
	public int currentEntriesCount;
	public final Double weight;
	public Double distance;

	public Hiperedge(String name, int numberOfEntries, Double weight) {

	    this.name = name;
	    this.numberOfEntries = numberOfEntries;
	    this.weight = weight;
	    this.distance = weight;
	}

	public int compareTo(Hiperedge o) {

	    return this.distance.compareTo(o.distance);
	}

	private void addVisitor() {

	    currentEntriesCount++;
	}

	private void addParentToChildren() {

	    for (Node node : this.tails) {
		node.tempoParentCount++;
	    }
	}

	private void substractParentToChildren() {

	    for (Node node : this.tails) {
		node.tempoParentCount--;
	    }
	}

	private boolean hasMissingParents() {

	    for (Node node : this.tails) {
		if (node.tempoParentCount == 0) {
		    return true;
		}
	    }

	    return false;
	}

	private void calculateDitance() {

	    for (Hiperedge hEdge : this.parents) {
		this.distance += hEdge.distance;
	    }
	}

	@Override
	public String toString() {

	    // TODO Auto-generated method stub
	    return name + " (" + weight + ")";
	}

    }

    public List<Hiperedge> exactAlgorithm() {

	Hiperedge hEdge = null;
	boolean hasEnded = false;

	for (Hiperedge edges : start.destinationEdges) {
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

	List<Hiperedge> list = new LinkedList<Hiperedge>();

	generateResult(list, hEdge);
	return list;

    }

    private void generateResult(List<Hiperedge> list, Hiperedge hEdge) {

	for (Hiperedge edge : hEdge.parents) {
	    generateResult(list, edge);
	}
	list.add(hEdge);
    }

    private void procesHEdge(Hiperedge hEdge) {

	hEdge.visited = true;

	for (Node node : hEdge.tails) {

	    if (!node.visited) {
		node.visited = true;

		for (Hiperedge edge : node.destinationEdges) {
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

    private void removeUnnecesaryParents(Hiperedge hEdge) {

	for (Node node : hEdge.heads) {
	    node.tempoParentCount = 0;
	}

	for (Hiperedge edge : hEdge.parents) {
	    edge.addParentToChildren();
	}

	Iterator<Hiperedge> it = hEdge.parents.iterator();

	while (it.hasNext()) {

	    Hiperedge current = it.next();
	    current.substractParentToChildren();

	    if (!current.hasMissingParents()) {
		it.remove();

	    } else {

		current.addParentToChildren();
	    }
	}
    }

}
