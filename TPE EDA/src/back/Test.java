package back;

import java.io.FileNotFoundException;
import java.io.IOException;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class Test
{

	public static void main(String[] args) throws FileNotFoundException,
			IOException
	{

		HyperGraph hg = GraphLoader.loadGraph("A.hg");
		GraphSaver.toDOT(hg);

		System.out.println(hg.exactAlgorithm());

		/*
		 * Node start = new Node("A"); Node end = new Node("K"); HyperGraph hg =
		 * new HyperGraph(start, end); hg.name = "test";
		 * 
		 * Node e = new Node("E"); Node f = new Node("F"); Node g = new
		 * Node("G"); Node h = new Node("H");
		 * 
		 * hg.nodes.put(e.name, e); hg.nodes.put(f.name, f);
		 * hg.nodes.put(g.name, g); hg.nodes.put(h.name, h);
		 * 
		 * HyperEdge b = new HyperEdge("B", 1, 4.0); HyperEdge c = new
		 * HyperEdge("C", 1, 2.0); HyperEdge d = new HyperEdge("D", 1, 3.0);
		 * HyperEdge i = new HyperEdge("I", 3, 1.0); HyperEdge j = new
		 * HyperEdge("J", 2, 8.0);
		 * 
		 * hg.hEdges.put(b.name, b); hg.hEdges.put(c.name, c);
		 * hg.hEdges.put(d.name, d); hg.hEdges.put(i.name, i);
		 * hg.hEdges.put(j.name, j);
		 * 
		 * b.tails.add(e); b.tails.add(f); c.tails.add(f); c.tails.add(g);
		 * d.tails.add(g); d.tails.add(h); i.tails.add(end); j.tails.add(end);
		 * 
		 * b.heads.add(start); c.heads.add(start); d.heads.add(start);
		 * i.heads.add(e); i.heads.add(f); i.heads.add(g); j.heads.add(g);
		 * j.heads.add(h);
		 * 
		 * start.destinationEdges.add(b); start.destinationEdges.add(c);
		 * start.destinationEdges.add(d); e.destinationEdges.add(i);
		 * f.destinationEdges.add(i); g.destinationEdges.add(i);
		 * g.destinationEdges.add(j); h.destinationEdges.add(j);
		 * 
		 * 
		 * 
		 * 
		 * 
		 * HyperGraph hGraph = null;
		 * 
		 * hGraph = GraphLoader.loadGraph("pinchador.hg");
		 * 
		 * System.out.println(hg.exactAlgorithm());
		 * System.out.println(hGraph.exactAlgorithm());
		 */

	}

}
