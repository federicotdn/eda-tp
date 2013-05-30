package back;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HyperGraph
{
	
	public String name;
	
	private Node start;
	private Node end;
	
	private double minDistance;
	
	
	public HyperGraph(Node start, Node end){
		this.start = start;
		this.end = end;
	}
	
	protected static class Node
	{

		public String name;

		public List<HyperEdge> head;
		public List<HyperEdge> tail;

		public boolean visited;


		public Node(String name)
		{

			this.name = name;
			tail = new ArrayList<HyperEdge>();
			head = new ArrayList<HyperEdge>();

		}

		@Override
		public String toString()
		{

			// TODO Auto-generated method stub
			return name;
		}
	}
	
	
	protected static class HyperEdge 
	{

		public String name;

		public List<Node> tail = new ArrayList<Node>();
		public List<Node> head = new ArrayList<Node>();
		public Set<HyperEdge> parents = new HashSet<HyperEdge>();

		public boolean visited;

		public final double weight;

		public double distance;
		
		public HyperEdge(String name, double weight){
			this.name = name;
			this.weight = weight;
		}
	}
	
	
	
	
	
}
