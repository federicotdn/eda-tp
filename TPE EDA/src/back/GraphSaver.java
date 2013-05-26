package back;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class GraphSaver {

    public static void toDOT(HyperGraph hGraph) throws IOException {

	FileWriter fileOutput = new FileWriter(hGraph.name + ".dot");
	BufferedWriter writer = new BufferedWriter(fileOutput);

	writer.write("digraph  {");

	for (Node node : hGraph.nodes.values()) {
	    writer.write(node.name + " [label=\"" + node.name + "\"]");
	    writer.newLine();

	    for (HyperEdge hEdge : node.destinationEdges) {
		writer.write(hEdge.name + " [shape=box, height=0.18, fontsize=12, label=\"" + hEdge.name + " ( " + hEdge.weight + " )" + "\"]");
		writer.newLine();

		writer.write(node.name + "->" + hEdge.name);
		writer.newLine();

	    }

	}

	for (HyperEdge edge : hGraph.hEdges.values()) {

	    for (Node node : edge.tails) {
		writer.write(edge.name + "->" + node.name);
		writer.newLine();
	    }

	}

	writer.write("}");
	writer.close();
    }

    public static void toDOT(HyperGraph hGraph, HyperGraph subgraph) throws IOException {

	FileWriter fileOutput = new FileWriter(hGraph.name + ".min.dot");
	BufferedWriter writer = new BufferedWriter(fileOutput);

	writer.write("digraph  {");

	for (Node node : hGraph.nodes.values()) {
	    if (subgraph.nodes.containsKey(node.name)) {
		writer.write(node.name + " [color = red,label=\"" + node.name + "\"]");
	    } else {
		writer.write(node.name + " [ label=\"" + node.name + "\"]");
	    }
	    writer.newLine();

	    for (HyperEdge hEdge : node.destinationEdges) {
		if (subgraph.hEdges.containsKey(hEdge.name)) {
		    writer.write(hEdge.name + " [color= red, shape=box, height=0.18, fontsize=12, label=\"" + hEdge.name + " ( " + hEdge.weight
			    + " )" + "\"]");
		} else {
		    writer.write(hEdge.name + " [shape=box, height=0.18, fontsize=12, label=\"" + hEdge.name + " ( " + hEdge.weight + " )" + "\"]");
		}

		if (subgraph.hEdges.containsKey(hEdge.name)) {
		    writer.write(node.name + "->" + hEdge.name + "[style=bold, color=red]");
		} else {
		    writer.write(node.name + "->" + hEdge.name);
		}
		writer.newLine();

	    }

	}

	for (HyperEdge edge : hGraph.hEdges.values()) {

	    for (Node node : edge.tails) {
		if (subgraph.nodes.containsKey(node.name) && subgraph.hEdges.containsKey(edge.name)) {
		    writer.write(edge.name + "->" + node.name + "[style=bold, color=red]");
		} else {
		    writer.write(edge.name + "->" + node.name);
		}
		writer.newLine();
	    }

	}

	writer.write("}");
	writer.close();
    }
}
