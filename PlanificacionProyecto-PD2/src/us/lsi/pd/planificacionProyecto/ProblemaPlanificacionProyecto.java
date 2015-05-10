package us.lsi.pd.planificacionProyecto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import us.lsi.stream.Stream2;

public class ProblemaPlanificacionProyecto {
	
	private static List<Componente> componentes;
	private static Comparator<Componente> ordenComponentes;
	
	public static void leeComponentes(String fichero) {
		ordenComponentes = Comparator.reverseOrder();
		componentes = Stream2.fromFile(fichero)
				.map((String s) -> Componente.create(s))
				.sorted(ordenComponentes)
				.collect(Collectors.toList());	
	}

	
	

}
