package back;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("A.hg");
		
		GraphSaver.toDOT(g);
		
		System.out.println("Camino minimo pesa: " + g.exactAlgorithm() + " (en teoria)");
		System.out.println("Visitados: " + g.visited.size());
		
		g.name = g.name + "Solved";
		GraphSaver.toDOT(g);
	}
}
