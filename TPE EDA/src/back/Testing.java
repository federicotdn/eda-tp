package back;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("F.hg");
		HyperGraph g2 = GraphLoader.loadGraph("F.hg");
		
		AltGraph a = new AltGraph(g);
		AltCombGraph b = new AltCombGraph(g2);
	
		a.exactAlgorithm();
		b.exactAlgorithm();
		
		//GraphSaver.toDOT(g);
		
		g.name = g.name + "Solved";
		g2.name = g2.name + "CombSolved";
		GraphSaver.toDOTwithPath(g);
		GraphSaver.toDOTwithPath(g2);
	}
}
