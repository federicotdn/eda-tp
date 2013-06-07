package main.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import main.HyperGraph;
import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

public class MinimumPathApproxAlgorithm
{
	private static HyperGraph graph;
	
	private static double minDistance;

	private static HashSet<HyperEdge> minPath;

	private static long startingTime;

	private static long maxTime;
	
	public static double execute(HyperGraph hyperGraph, int maxTimeSeg)
	{
		graph = hyperGraph;
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge firstResult = bestFirstSearch();
		
		minDistance = firstResult.edgePath.totalWeight;
		minPath = firstResult.edgePath.path;
		
		
		return minDistance;
	}
	
	private static void getBetterMinPath(HyperEdge firstResult){
		
		HyperEdge result = null;
		HyperEdge edge;

		HashSet<HyperEdge> recentTaboos = new HashSet<HyperEdge>();
		HashSet<HyperEdge> current;

		ArrayList<HyperEdge> taboos;
		
		int numberOfTaboos = 1;
		int i = 0;
		int pathCount = 0;
		int maxPaths = 0;

		
		current = minPath;

		for (HyperEdge e : graph.hEdges)
		{
			 maxPaths += e.head.size();
		}
		for(Node node: graph.nodes){
			maxPaths += node.head.size();
		}

		Iterator<HyperEdge> it = minPath.iterator();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{
			taboos = new ArrayList<HyperEdge>();
			if (!it.hasNext())
			{
				if (numberOfTaboos >= current.size() )
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

			if (result != null && (result.edgePath.totalWeight < minDistance))
			{
				minDistance = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				it = minPath.iterator();
				current = minPath;
				numberOfTaboos = 1;

			} else
			{
				removeTaboo(taboos);
			}

			if (pathCount >= maxPaths )
			{
				removeTaboo(recentTaboos);
				recentTaboos = new HashSet<HyperEdge>();
				pathCount = 0;
			}
			pathCount++;

		}

		resetGraph();
		graph.end.visited = true;
		markPath(minPath);
//		System.out.println(minDistance);
//		System.out.println((double) (System.currentTimeMillis() - startingTime)
//				/ 1000 + " segundos ");
		
		
	}
	
	
	public static void removeTaboo(Collection<HyperEdge> c){
		for (HyperEdge taboo : c)
		{
			taboo.isTaboo = false;
		}
	}
	

	
	private static HyperEdge bestFirstSearch()
	{

		HyperEdge hEdge = null;
		boolean hasEnded = false;

		PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>(
				graph.hEdges.size(), new Comparator<HyperEdge>() {
					@Override
					public int compare(HyperEdge edge1, HyperEdge edge2)
					{
						return Double.compare(edge1.edgePath.totalWeight,
								edge2.edgePath.totalWeight);
					}
				});

		for (HyperEdge edge: graph.start.head)
		{
			edge.calculatePathDistance();
			if (!edge.isTaboo)
			{
				hq.offer(edge);
			}
		}

		while (!hq.isEmpty() && !hasEnded)
		{
			if ((System.currentTimeMillis() - startingTime) > maxTime)
			{
				return null;
			}

			hEdge = hq.poll();

			for (Node node : hEdge.head)
			{
				if (node == graph.end)
				{
					hasEnded = true;
					break;
				}
			}
			procesHEdge(hEdge, hq);

		}

		if (!hasEnded && hq.isEmpty())
		{
			return null;
		}

		// me queda hEdge con el camino para arriba
		// marcar ejes del camino

		return hEdge;
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

	private static void procesHEdge(HyperEdge hEdge, PriorityQueue<HyperEdge> hq)
	{
		for (Node node : hEdge.head)
		{
			if (!node.visited)
			{
				node.visited = true;

				for (HyperEdge edge : node.head)
				{
					if (!edge.isTaboo)
					{
						edge.addVisitor();
						edge.parents.add(hEdge);

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
		int i = 0;
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

	private static void markPath(HashSet<HyperEdge> set)
	{
		
		for (HyperEdge edge : set)
		{
			edge.visited = true;
			for(Node node: edge.tail){
				node.visited = true;
			}
		}

	}


	
	
	//NO VA
	
	public static double minimumPathApproxAlt2(int maxTimeSeg)
	{
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge edge = bestFirstSearch();
		minDistance = edge.edgePath.totalWeight;
		minPath = edge.edgePath.path;

		improvePath(edge);

		resetGraph();
		
		return minDistance;

	}
	
	public static double minimumPathApproxAlt(int maxTimeSeg)
	{
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge edge = bestFirstSearch();
		HyperEdge result = null;

		HashSet<HyperEdge> recentTaboos = new HashSet<HyperEdge>();
		HashSet<HyperEdge> current;

		int count = 0;

		minDistance = edge.edgePath.totalWeight;
		minPath = edge.edgePath.path;
		current = minPath;

		// resetGraph();

		Iterator<HyperEdge> it = minPath.iterator();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{
			if (!it.hasNext())
			{
				for (HyperEdge taboo : recentTaboos)
				{
					taboo.isTaboo = false;
				}
				recentTaboos = new HashSet<HyperEdge>();
				current = pickNeighbour(current);
				it = current.iterator();

			}

			edge = it.next();
			edge.isTaboo = true;
			recentTaboos.add(edge);
			resetGraph();
			result = bestFirstSearch();

			if (result != null && (result.edgePath.totalWeight < minDistance))
			{
				minDistance = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				it = minPath.iterator();
				current = minPath;

			} else
			{
				edge.isTaboo = false;
			}

			long thisTime = (System.currentTimeMillis() - startingTime) / 1000;

			
			if (count >= current.size())
			{
				for (HyperEdge taboo : recentTaboos)
				{
					taboo.isTaboo = false;
				}
				recentTaboos = new HashSet<HyperEdge>();
				count = 0;

			}

		}

		
		resetGraph();
		
		return minDistance;
	}
	
	private static void improvePath(HyperEdge last)
	{
		HyperEdge edge;
		HyperEdge result = null;
		HashSet<HyperEdge> current = last.edgePath.path;
		HashSet<HyperEdge> previous = null;
		HashSet<HyperEdge> taboo = new HashSet<HyperEdge>();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{

			if (current != null)
			{
				previous = current;
				edge = pickRandomEdge(current);

			} else
			{
				edge = pickRandomEdge(previous);

			}

			edge.isTaboo = true;
			resetGraph();

			result = bestFirstSearch();
			if (result != null && (result.edgePath.totalWeight < minDistance))
			{
				minDistance = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				current = minPath;
			}

			edge.isTaboo = false;

			if (result == null)
			{
				current = null;
			} else
			{
				current = result.edgePath.path;
			}

		}

		
	}
	
	public static void addParentToChildren(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			node.tempParentCount++;
		}
	}

	public static void substractParentToChildren(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			node.tempParentCount--;
		}
	}

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

}
