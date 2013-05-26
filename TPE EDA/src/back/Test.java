package back;

import back.Hipergraph.Hiperedge;
import back.Hipergraph.Node;

public class Test
{
	public static void main(String[] args)
	{
	    Node start = new Node("A");
	    Node end = new Node("K");
	    Hipergraph hg = new Hipergraph(start, end);
	    
	    Node e = new Node("E");
	    Node f = new Node("F");
	    Node g = new Node("G");
	    Node h = new Node("H");
	    
	   Hiperedge b = new Hiperedge("B", 1, 4.0);
	   Hiperedge c = new Hiperedge("C", 1, 2.0);
	   Hiperedge d = new Hiperedge("D", 1, 3.0);
	   Hiperedge i = new Hiperedge("I", 3, 1.0);
	   Hiperedge j = new Hiperedge("J", 2, 8.0);
	   
	   b.tails.add(e);
	   b.tails.add(f);
	   c.tails.add(f);
	   c.tails.add(g);
	   d.tails.add(g);
	   d.tails.add(h);
	   i.tails.add(end);
	   j.tails.add(end);
	   
	   b.heads.add(start);
	   c.heads.add(start);
	   d.heads.add(start);
	   i.heads.add(e);
	   i.heads.add(f);
	   i.heads.add(g);
	   j.heads.add(g);
	   j.heads.add(h);
	   
	   
	   
	   
	   start.destinationEdges.add(b);
	   start.destinationEdges.add(c);
	   start.destinationEdges.add(d);
	   e.destinationEdges.add(i);
	   f.destinationEdges.add(i);
	   g.destinationEdges.add(i);
	   g.destinationEdges.add(j);
	   h.destinationEdges.add(j);
	   
	   
	   System.out.println(hg.exactAlgorithm());
	   System.out.println(j.distance);


	   
	    
	}
}
