package main.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.naming.TimeLimitExceededException;

import main.HyperGraph;
import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;
import main.InvalidTimeException;

/**
 * La clase estatica <code>MinimumPathApproxAlgorithm</code> contiene todas las funciones necesarias para poder
 * buscar un camino minimo aproximado en un hipergrafo, en una cierta cantidad de tiempo definida.
 */
public class MinimumPathApproxAlgorithm
{
	private static HyperGraph graph;

	private static double minWeight;

	private static HashSet<HyperEdge> minPath;

	private static long startingTime;

	private static long maxTime;

	/**
	 * 
	 * El metodo <code>execute</code> es el unico metodo publico de la clase, que es usado por el front-end 
	 * para realizar un calculo de camino minimo sobre un grafo.
	 * 
	 * <p>El metodo <code>execute</code> llama a <code>bestFirstSearch</code> para obtener un camino aproximado inicial,
	 * y luego a <code>getBetterMinPath</code> para mejorarlo.
	 * 
	 * @param hyperGraph - Hipergrafo donde se busacara un camino minimo.
	 * @param maxTimeSeg - Tiempo maximo en segundos en la que el algoritmo puede correr.
	 * @return Peso del camino calculado.  Los ejes y nodos del camino quedan con <code>visited = true</code>.
	 * @throws InvalidTimeException si el tiempo especificado es invalido.
	 */
	public static double execute(HyperGraph hyperGraph, int maxTimeSeg)
			throws InvalidTimeException
	{
		if (maxTimeSeg < 0)
		{
			throw new InvalidTimeException("El tiempo no puede ser negativo");
		}

		graph = hyperGraph;
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge firstResult = bestFirstSearch();

		minWeight = firstResult.edgePath.totalWeight;
		minPath = firstResult.edgePath.path;

		if (firstResult == null)
		{
			throw new InvalidTimeException("El intervalo de tiempo es demasiado chico");
		}
		
		getBetterMinPath();
		graph.end.visited = true;
		markPath(minPath);

		return minWeight;
	}

	/**
	 * 
	 * 
	 */
	private static void getBetterMinPath()
	{

		HyperEdge result = null;
		HyperEdge edge;

		HashSet<HyperEdge> recentTaboos = new HashSet<HyperEdge>();
		HashSet<HyperEdge> current;

		ArrayList<HyperEdge> taboos;

		int numberOfTaboos = 1;
		int i = 0;
		int pathCount = 0;
		int maxPaths = maxNumberOfPaths();

		current = minPath;

		Iterator<HyperEdge> it = minPath.iterator();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{
			taboos = new ArrayList<HyperEdge>();
			
			if (!it.hasNext())
			{
				if (numberOfTaboos >= current.size())
				{
					current = pickNeighbour(current);
					numberOfTaboos = 1;

				} else
				{
					numberOfTaboos++;
				}

				it = current.iterator();
			}

			i = 0;
			while (it.hasNext() && i < numberOfTaboos)
			{
				edge = it.next();
				edge.isTaboo = true;
				recentTaboos.add(edge);
				taboos.add(edge);
				i++;
			}

			resetGraph();
			result = bestFirstSearch();

			if (result != null && (result.edgePath.totalWeight < minWeight))
			{
				minWeight = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				it = minPath.iterator();
				current = minPath;
				numberOfTaboos = 1;

			} else
			{
				removeTaboo(taboos);
			}

			if (pathCount >= maxPaths)
			{
				removeTaboo(recentTaboos);
				recentTaboos = new HashSet<HyperEdge>();
				pathCount = 0;
			}
			pathCount++;
		}
	}

	private static void removeTaboo(Collection<HyperEdge> c)
	{
		for (HyperEdge taboo : c)
		{
			taboo.isTaboo = false;
		}
	}

	/**
	 * El metodo <code>bestFirstSearch</code> es el centro del algoritmo aproximado.  
	 * 
	 * <p>Comienza creando un PriorityQueue de 
	 * hiperejes, para aplicar un algorimto similar al algoritmo de Dijkstra.  Se remueve un eje de la cola (el de menor peso), 
	 * se marca como visitado y se marca tambien como visitados todos los nodos hijos.  A la vez, se accede a todos los
	 * ejes hijos de dichos nodos mencionados, y se agrega una referencia <code>parent</code> al eje removido.  Cuando
	 * un eje tiene todos sus nodos padre visitados, se aplica al eje una heuristica que permite eliminar padres innecesarios, y se 
	 * agrega a la lista de prioridades, utilizando la clase <code>EdgePath</code> para calcular el peso total del camino que
	 * lleva a ese eje.
	 * 
	 * @return El ultimo eje del camino calculado (contiene referencias a los demas ejes del camino).
	 */
	private static HyperEdge bestFirstSearch()
	{

		HyperEdge edge = null;

		//Lista de prioridades.  Almacena HyperEdge, y los compara utilizando el valor weight
		//de sus respectivas variables path.  La variable path representa el camino construido para 
		//llegar a cierto eje.
		
		PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>(
				graph.hyperEdges.size(), new Comparator<HyperEdge>() {
					@Override
					public int compare(HyperEdge edge1, HyperEdge edge2)
					{
						return Double.compare(edge1.edgePath.totalWeight,
								edge2.edgePath.totalWeight);
					}
				});
		
		//Se agregan todos los ejes iniciales (hijos del nodo origen)
		
		for (HyperEdge topEdge : graph.start.head)
		{
			topEdge.calculatePathDistance();
			
			//Se ignoran ejes taboo
			if (!topEdge.isTaboo)
			{
				hq.offer(topEdge);
			}
		}

		while (!hq.isEmpty())
		{
			//Si no hay tiempo, se devuelve null
			if ((System.currentTimeMillis() - startingTime) > maxTime)
			{
				return null;
			}

			edge = hq.poll();

			if (edge.isBottom)
			{
				//Se llego al destino
				return edge;
			}
			
			processEdge(edge, hq);
		}

		return null;
	}

	private static HyperEdge pickRandomEdge(HashSet<HyperEdge> set)
	{
		int rand = (int) (Math.random() * set.size());
		Iterator<HyperEdge> it = set.iterator();
		HyperEdge edge = null;

		for (int i = 0; i <= rand; i++)
		{
			edge = it.next();
		}

		return edge;
	}

	private static void resetGraph()
	{
		graph.clearNodeMarks();
		graph.clearEdges();
	}

	/**
	 * El metodo <code>processEdge</code> se encarga de procesar ejes individuales seleccionados por el 
	 * metodo <code>bestFirstSearch()</code>.
	 * 
	 * <p>Primero, por cada nodo hijo no visitado de hEdge, se marca como <code>visited</code>.  Por cada nodo visitado,
	 * se agrega a cada eje hijo EDGE de dicho nodo una referencia a hEdge.  De esta forma, se "visita" una vez a EDGE.  Si
	 * la cantidad de "visitas" que tiene EDGE concuerda con su cantidad de nodos cola, entonces se procesa EDGE, y se lo
	 * agrega a la cola de prioridades.
	 * 
	 * @param hEdge - El eje al que se analizara los nodos hijos, y respectivos ejes hijos.
	 * @param hq - Cola de prioridades utilizada por <code>bestFirstSearch()</code>.
	 */
	private static void processEdge(HyperEdge hEdge, PriorityQueue<HyperEdge> hq)
	{
		for (Node node : hEdge.head)
		{
			if (!node.visited)
			{
				node.visited = true;

				for (HyperEdge edge : node.head)
				{
					//Se ignoran ejes taboo
					if (!edge.isTaboo)
					{
						//Se aumenta por uno la variable currentEntriesCount de edge
						edge.addVisitor();
						
						edge.parents.add(hEdge);
						
						//Si el eje esta completamente visitado:
						if (edge.currentEntriesCount == edge.tail.size())
						{
							removeUnnecesaryParents(edge);
							edge.calculatePathDistance();
							hq.offer(edge);
						}
					}
				}

			}
		}

	}

	/**
	 * El metodo <code>removeUnnecesaryParents</code> se encarga de que un eje listo para agregar a la cola de prioridades
	 * no tenga ejes <code>parent</code> innecesarios.  Se remueven todos las referencias a ejes <code>parent</code> posibles,
	 * asegurandose de que cada nodo padre de hEdge esté siendo habilitado por algun eje padre.
	 * 
	 * @param hEdge - Eje a procesar para remover padres innecesarios.
	 */
	private static void removeUnnecesaryParents(HyperEdge hEdge)
	{

		for (Node node : hEdge.tail)
		{
			node.tempParentCount = 0;
		}

		for (HyperEdge edge : hEdge.parents)
		{
			addParentToChildren(edge);
		}

		Iterator<HyperEdge> it = hEdge.parents.iterator();

		while (it.hasNext())
		{

			HyperEdge current = it.next();
			substractParentToChildren(current);

			if (!hasMissingParents(current))
			{
				it.remove();

			} else
			{

				addParentToChildren(current);
			}
		}

		it = hEdge.parents.iterator();

	}

	private static HashSet<HyperEdge> pickNeighbour(HashSet<HyperEdge> current)
	{

		HashSet<HyperEdge> neighbour = current;
		HyperEdge result = null;
		HyperEdge edge;

		resetGraph();

		while (result == null
				&& (System.currentTimeMillis() - startingTime) < maxTime)
		{
			edge = pickRandomEdge(current);
			edge.isTaboo = true;
			result = bestFirstSearch();
			edge.isTaboo = false;

			if (result != null)
			{
				neighbour = result.edgePath.path;
			}

			resetGraph();

		}
		return neighbour;
	}

	/**
	 * Recorre un conjunto de HyperEdge, seteando la variable <code>visited</code> de cada eje como true, asi tambien
	 * como todos los nodos cola.  Esto efectivamente marca todos los elementos de un camino de ejes y nodos.
	 * 
	 * @param set
	 */
	private static void markPath(HashSet<HyperEdge> set)
	{

		for (HyperEdge edge : set)
		{
			edge.visited = true;
			for (Node node : edge.tail)
			{
				node.visited = true;
			}
		}

	}	

	/**
	 * Los metodos addParentToChildren, substractParentToChildren y hasMissingParents componen el sistema
	 * que permite eliminar a un eje padres innecesarios.  Se toma a un eje hEdge, y por cada eje <code>parent</code>
	 * se envia el mensaje substractParentToChildren.  Si la variable tempParentCount de uno de esos nodos llega a 0, quiere
	 * decir que dicho eje es necesario para poder habilitar hEdge.
	 * 
	 * @param edge - Uno de los ejes <code>parent</code> del eje que se esta analizando.
	 */
	public static void addParentToChildren(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			node.tempParentCount++;
		}
	}

	/**
	 * Ver <code>addParentToChildren.</code>
	 *
	 */
	public static void substractParentToChildren(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			node.tempParentCount--;
		}
	}

	/**
	 * Ver <code>addParentToChildren.</code>
	 */
	public static boolean hasMissingParents(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			if (node.tempParentCount == 0)
			{
				return true;
			}
		}

		return false;
	}

	private static int maxNumberOfPaths()
	{
		int maxPaths = 0;
		for (HyperEdge e : graph.hyperEdges)
		{
			maxPaths += e.head.size();
		}
		
		for (Node node : graph.nodes)
		{
			maxPaths += node.head.size();
		}
		
		return maxPaths;
	}

}
