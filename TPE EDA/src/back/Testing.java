package back;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("enunciado.hg");
		
		
		
		System.out.println(g.exactAlgorithm());
		System.out.println("Visitados: " + g.visited.size());
		
		//GraphSaver.toDOT(g);
	}
}
