package back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class HyperGraph {

    protected Map<String, HyperEdge> hEdges = new HashMap<String, HyperEdge>();
    protected Map<String, Node> nodes = new HashMap<String, Node>();

    protected String name;

    private Node start;
    private Node end;

    private PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>();

    public HyperGraph(Node start, Node end) {

	this.start = start;
	this.end = end;
	nodes.put(start.name, start);
	nodes.put(end.name, end);
    }

    protected static class Node  {

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



	@Override
	public boolean equals(Object obj) {

	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Node other = (Node) obj;
	    if (name == null) {
		if (other.name != null)
		    return false;
	    } else if (!name.equals(other.name))
		return false;
	    return true;
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
	public int tag = 1;

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

	public void prepareParentNodes() {

	    for (Node node : this.heads) {
		node.destinationEdges.add(this);
	    }
	}

	@Override
	public String toString() {

	    // TODO Auto-generated method stub
	    return name + " (" + weight + ")";
	}


	@Override
	public boolean equals(Object obj) {

	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    HyperEdge other = (HyperEdge) obj;
	    if (name == null) {
		if (other.name != null)
		    return false;
	    } else if (!name.equals(other.name))
		return false;
	    return true;
	}
	
	

    }

    public HyperGraph exactAlgorithm() {

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

	Node newEnd = new Node(end.name);

	HyperGraph subgraph = new HyperGraph(new Node(start.name), newEnd );
	
	HyperEdge aux = new HyperEdge(hEdge.name, hEdge.numberOfEntries, hEdge.weight);
	subgraph.hEdges.put(aux.name, aux);
	generateResult(subgraph, hEdge, aux);
	aux.tails.add(newEnd);
	
	
	subgraph.name = this.name;

	return subgraph;

    }

    private void generateResult(HyperGraph subgraph, HyperEdge current, HyperEdge newCurrent) {

	

	for (HyperEdge parentEdge : current.parents) {
	    
	    HyperEdge newParentEdge = new HyperEdge(parentEdge.name, parentEdge.numberOfEntries, parentEdge.weight);
	    subgraph.hEdges.put(newParentEdge.name, newParentEdge);
	    subgraph.addCommonChilds(current, parentEdge, newCurrent, newParentEdge);
	    generateResult(subgraph, parentEdge, newParentEdge);
	    
	}
	if(current.parents.isEmpty()){
	    newCurrent.heads.add(subgraph.start);
	    subgraph.start.destinationEdges.add(newCurrent);
	}
	
	
    }

    private void addCommonChilds(HyperEdge hEdge, HyperEdge parent, HyperEdge newHEdge, HyperEdge newParent) {


	for (Node node : hEdge.heads) {
	    for (Node parentNode : parent.tails) {
		if (node == parentNode) {
		    Node aux = new Node(node.name);
		    if (!newParent.tails.contains(aux)) {
			newParent.tails.add(aux);
			aux.destinationEdges.add(newHEdge);
			newHEdge.heads.add(aux);
			
			if(!nodes.containsKey(aux.name)){
			    nodes.put(aux.name, aux);
			    
			    
			}
		    }
		}
	    }
	}
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

	it = hEdge.parents.iterator();
	hEdge.tag = it.next().tag + 1;

    }
    
    public Node getStart(){
	return start;
    }

}
