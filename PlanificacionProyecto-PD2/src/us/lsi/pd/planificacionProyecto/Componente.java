package us.lsi.pd.planificacionProyecto;

import java.util.List;

import com.google.common.collect.Lists;

public class Componente implements Comparable<Componente>{
	
	private String componente;
	private List<Double> probabilidades;

	public static Componente create(String s) {
		return new Componente(s);
	}
		
	Componente(String s) {
		String[] v = s.split(",");
		Integer ne = v.length;
		
		componente = new String(v[0]);
		probabilidades = Lists.newArrayList();
		
		for(int i=1; i<ne; i++){
			probabilidades.add(new Double(v[i]));
		}

	}//

	

	
}
