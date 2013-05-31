package back;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import back.HyperGraph.*;

public class GraphCreator //no anda bien
{
	public static void main(String[] args) throws IOException
	{
		HyperGraph g = createRandom("testing", 10);
		GraphSaver.toDOT(g);
	}
	
	private static final String startName = "start";
	private static final String endName = "end";
	
	private static final double widthMultiplier = 1.0f; //uno a uno
	private static final double weightMultiplier = 20.0f;
	
	private static final int parentMultiplier = 5;
	private static final int childrenMultiplier = 5;
	
	public static HyperGraph createRandom(String gName, int levels)
	{
		if (levels < 1)
			throw new UnsupportedOperationException();
		
		int width = (int) (levels * widthMultiplier);
		int nodeLevels = levels + 1;
		Random rand = new Random(System.currentTimeMillis());
		
		Node start = new Node(startName);
		Node end = new Node(endName);
		
		HyperGraph g = new HyperGraph(start, end);
		
		g.name = gName;
		
		ArrayList<ArrayList<HyperEdge>> edgeList = new ArrayList<ArrayList<HyperEdge>>();
		ArrayList<ArrayList<Node>> nodeList = new ArrayList<ArrayList<Node>>();
		
		char prefix = 'a';
		int postfix = 0;
		int levelStep = 0;
		
		for (int i = 0; i < nodeLevels; i++)
		{
			ArrayList<Node> temp = new ArrayList<Node>();
			nodeList.add(temp);
			
			levelStep = (i > nodeLevels / 2) ? levelStep - 1 : levelStep + 1;
			
			System.out.println("Levelstep de nodes:" + levelStep);
			
			if (i == 0)
			{
				temp.add(start);
				continue;
			}
			else if (i == nodeLevels - 1)
			{
				temp.add(end);
				continue;
			}
			
			int levelWidth = (int) (((double)levelStep / (nodeLevels / 2)) * (double)width);
			
			for (int j = 0; j < levelWidth; j++)
			{
				String name = prefix + "_" + postfix;
				Node aux = new Node(name);
				temp.add(aux);
				g.nodes.add(aux);
				postfix++;
			}
			
			prefix++;
		}
		
		prefix = 'A';
		postfix = 0;
		levelStep = 0;
		
		HashSet<Integer> usedBottom = new HashSet<Integer>();
		HashSet<Integer> usedTop = new HashSet<Integer>();
		
		for (int i = 0; i < levels; i++)
		{
			ArrayList<HyperEdge> temp = new ArrayList<HyperEdge>();
			edgeList.add(temp);
			
			levelStep = (i > levels / 2) ? levelStep - 1 : levelStep + 1;
			System.out.println("Levelstep de edge:" + levelStep);
			int levelWidth = (int) (((double)levelStep / (nodeLevels / 2)) * (double)width);
			
			for (int j = 0; j < levelWidth; j++)
			{
				usedBottom.clear();
				usedTop.clear();
				
				String name = prefix + "--" + postfix;
				postfix++;
				HyperEdge edge = new HyperEdge(name, (int)(rand.nextDouble() * weightMultiplier));
				temp.add(edge);
				
				ArrayList<Node> topNodes = nodeList.get(i);
				ArrayList<Node> bottomNodes = nodeList.get(i + 1);
				
				int firstNodeIndex = (int) ((bottomNodes.size() - 1) * rand.nextDouble());
				usedBottom.add(firstNodeIndex);
				
				edge.head.add(bottomNodes.get(firstNodeIndex));
				
				firstNodeIndex = (int) ((topNodes.size() - 1) * rand.nextDouble());
				usedTop.add(firstNodeIndex);
				
				edge.tail.add(topNodes.get(firstNodeIndex));
				
				//por ahora agregue uno arriba y uno abajo
				
				for (Node node : edge.head)
					node.tail.add(edge);
				
				g.hEdges.add(edge);
				
			}
			
			prefix++;
		}
		
		return g;
	}
}
