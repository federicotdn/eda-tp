package main;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		//Metodo main de paquete Main - keep it clean
		
		HyperGraph g = GraphLoader.loadGraph("H.hg");
		
		//for (int i = 0; i < 4; i++)
			g.minimumPathApprox();
		//g.minimumPathExact();
		
//		HyperGraph A = GraphLoader.loadGraph("A.hg");
//		HyperGraph B = GraphLoader.loadGraph("B.hg");
//		HyperGraph en = GraphLoader.loadGraph("enunciado.hg");
	}
}
