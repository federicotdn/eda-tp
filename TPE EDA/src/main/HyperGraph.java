package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * La clase <code>HyperGraph</code> es la clase central del trabajo practico.
 * 
 * <p><code>HyperGraph</code> consiste en una representacion simple de un hipergrafo: contiene una lista de 
 * nodos, y una lista de hiperejes.  Tambien cuenta con dos referencias a dos nodos especiales: start (origen) y end (destino).
 *
 *<p> La clase tambien contiene dos clases internas: <code>HyperEdge</code>, que representa a un hipereje, y <code>Node</code>,
 *que representa a un nodo del hipergrafo.
 */
public class HyperGraph
{
	public String name;

	public Node start;
	public Node end;

	public List<HyperEdge> hyperEdges;
	public List<Node> nodes;

	/**
	 * 
	 * Constructor unico de <code>HyperGraph</code>. Construye un hipergrafo vacio, excepto por un nodo inicio y uno final.
	 * 
	 * @param name - Nombre del hipergrafo.
	 * @param start - Nodo inicio.
	 * @param end - Nodo final.
	 */
	public HyperGraph(String name, Node start, Node end)
	{
		this.name = name;
		this.start = start;
		this.end = end;
		hyperEdges = new LinkedList<HyperEdge>();
		nodes = new LinkedList<Node>();
	}

	/**
	 * Metodo de utilidad que recorre recorre todos los nodos del hipergrafo, desvisitandolos.
	 */
	public void clearNodeMarks()
	{
		for (Node node : nodes)
			node.visited = false;
	}
	
	/**
	 * Metodo de utilidad que revierte a todos los hiperejes del hipergrafo a su estado original, luego de haber sido creados.
	 */
	public  void clearEdges()
	{
		for (HyperEdge edge : hyperEdges)
		{
			edge.edgePath = null;
			edge.parents = new ArrayList<HyperEdge>();
			edge.currentEntriesCount = 0;
		}
	}

	

	/**
	 * La clase <code>Node</code> representa a un nodo en el hipergrafo.
	 * 
	 * <p>La clase posee dos listas: head y tail.  Head contiene referencias a hiperejes cuyo el nodo entra, y tail contiene
	 * referencias a ejes a hiperejes cuyo el nodo sale.
	 *
	 */
	public static class Node
	{
		public List<HyperEdge> head; // Las dos listas son necesarias?
		public List<HyperEdge> tail;

		public String name;

		public int tempParentCount;

		public boolean visited;


		public String getName()
		{
			return name;
		}

		public Node(String name)
		{
			head = new LinkedList<HyperEdge>();
			tail = new LinkedList<HyperEdge>();
			this.name = name;
			visited = false;
		}

		@Override
		public String toString()
		{
			return "(" + name + ")";
		}
	}

	public static class HyperEdge
	{
		public List<Node> head;
		public List<Node> tail;
		public List<HyperEdge> parents;

		public String name;
		public final double weight;

		public EdgePath edgePath;
		public boolean visited;
		public int currentEntriesCount;
		public boolean isTaboo;

		public boolean isTop;
		public boolean isBottom;

		public HyperEdge(String name, double weight)
		{
			head = new LinkedList<Node>();
			tail = new LinkedList<Node>();
			parents = new LinkedList<HyperEdge>();

			this.name = name;
			this.weight = weight;
			visited = false;
			isTop = false;
			isBottom = false;

			isTaboo = false;

			currentEntriesCount = 0;
		}


		public void calculatePathDistance()
		{
			if (this.edgePath == null)
			{
				this.edgePath = new EdgePath(this);
			}

			for (HyperEdge parent : this.parents)
			{
				this.edgePath.mergeWith(parent.edgePath);
			}
		}

		public void addVisitor()
		{
			currentEntriesCount++;
		}

		public void setChildrenVisited(boolean value)
		{
			for (Node node : head)
				node.visited = value;
		}

		@Override
		public int hashCode()
		{
			return super.hashCode();
		}

		@Override
		public String toString()
		{
			return "[" + name + ", " + weight + "]";
		}
	}
}
