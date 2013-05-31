package back;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("B.hg");
		
		System.out.println("Camino minimo pesa: " + g.exactAlgorithm());
		System.out.println("Visitados: " + g.visited.size());
		
		GraphSaver.toDOT(g);
	}
}
