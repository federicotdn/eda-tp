package main;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		//Metodo main de paquete Main - keep it clean
		
		HyperGraph g = GraphLoader.loadGraph("aproximado1.hg");
		
		//for (int i = 0; i < 4; i++)
			g.minimumPathApproxAlt2(30);
		//g.minimumPathExact();
			g.resetGraph();
			//g.minimumPathApproxAlt(10);
		
//		HyperGraph A = GraphLoader.loadGraph("A.hg");
//		HyperGraph B = GraphLoader.loadGraph("B.hg");
//		HyperGraph en = GraphLoader.loadGraph("enunciado.hg");
	}
}
