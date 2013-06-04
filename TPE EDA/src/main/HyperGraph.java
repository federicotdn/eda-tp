package main;

import java.util.ArrayList;
import java.util.List;

//Clase HyperGraph limpia, con implementacion de algoritmo exacto:
//Usa la version con HashMap<Integer, EdgeSet>, llega recursivamente hasta arriba
//primero y despues va bajando, sumando pesos

public class HyperGraph
{
	private String name;
	
	private Node start;
	private Node end;
	
	private List<HyperEdge> hEdges;
	private List<Node> nodes;
	
	public HyperGraph(String name, Node start, Node end)
	{
		this.name = name;
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}
	
	public Node start()
	{
		return start;
	}

	public Node end()
	{
		return end;
	}
	
	public List<HyperEdge> edges()
	{
		return hEdges;
	}
	
	public List<Node> nodes()
	{
		return nodes;
	}
	
	//----------------Algoritmo Exacto---------------------------------------
	
	public void minimumPathExact()
	{
		System.out.println("Comenzando búsqueda de camino minimo (exacto)...");
		long lastTime = System.currentTimeMillis();
		
		EdgeSet min = null;
		
		for (HyperEdge edge : end.tail)
		{
			EdgeSet aux = new EdgeSet(edge);
			
			minimumPathExact(aux);
			
			if (min == null || aux.getTotalWeight() < min.getTotalWeight())
				min = aux;
		}
		
		System.out.println("Camino minimo pesa: " + min.getTotalWeight());
		
		System.out.println("Tardó: " + ((double)System.currentTimeMillis() - lastTime) + " segundos.");
	}
	
	private void minimumPathExact(EdgeSet current)
	{
		
	}
	
	
	//----------------Algoritmo Exacto End-----------------------------------
	
	protected static class Node
	{
		private ArrayList<HyperEdge> head; //Las dos listas son necesarias?
		private ArrayList<HyperEdge> tail;
		
		private String name;
		
		private boolean visited;
		
		public ArrayList<HyperEdge> tail()
		{
			return tail;
		}
		
		public ArrayList<HyperEdge> head()
		{
			return head;
		}
		
		public String getName()
		{
			return name;
		}
		
		public Node(String name)
		{
			this.name = name;
			visited = false;
		}
		
		@Override
		public String toString()
		{
			return "(" + name + ")";
		}
	}
	
	protected static class HyperEdge
	{
		private ArrayList<Node> head; //Las dos listas son necesarias?
		private ArrayList<Node> tail; 
		
		private String name;
		
		private boolean visited;
		private boolean isTop;
		
		private final double weight;
		
		public HyperEdge(String name, double weight)
		{
			this.name = name;
			this.weight = weight;
			visited = false;
			isTop = false;
		}
		
		public ArrayList<Node> tail()
		{
			return tail;
		}
		
		public ArrayList<Node> head()
		{
			return head;
		}
		
		public double weight()
		{
			return weight;
		}
		
		public void setAsTop()
		{
			isTop = true;
		}
		
		@Override
		public int hashCode()
		{
			return super.hashCode(); //Dejamos el hashcode de Object, por ahora
									//Quizas darle un ID unico a cada eje y usar eso
		}
		
		@Override
		public String toString()
		{
			return "[" + name + ", " + weight + "]";
		}
	}
}
