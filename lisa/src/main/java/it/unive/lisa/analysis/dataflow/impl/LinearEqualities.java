package it.unive.lisa.analysis.dataflow.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.PossibleForwardDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.*;

// An element of the reaching definitions dataflow domain.
public class LinearEqualities
	implements DataflowElement< // instances of this class represent a single element of the dataflow domain
		PossibleForwardDataflowDomain<LinearEqualities>, // the dataflow domain to be used with this element is
															// a PossibleForwardDataflowDomain containing
															// instances of ReachingDefinitions
		LinearEqualities // the concrete type of dataflow elements, that must be the same of this class
	> {

	// the variable defined
	private final Identifier variable;
	// the program point where the variable has been defined
	private final ProgramPoint programPoint;

	private ValueExpression expression = null;

	private int line;

	private static boolean find = false;

	HashMap<String, String > value = new HashMap<String, String>();
	private ProgramPoint programPoint1;


	// this constructor will be used when creating the abstract state
	public LinearEqualities() {
		this(null, null);
	}
	
	// this constructor is what we actually use to create dataflow elements 
	private LinearEqualities(Identifier variable, ProgramPoint pp) {
		this.programPoint = pp;
		this.variable = variable;
	}


	private LinearEqualities(Identifier variable, ProgramPoint pp, ValueExpression exp) {
		this.programPoint = pp;
		this.variable = variable;
		this.expression = exp;

		value.put( variable.toString() , "1" );

		String[] appo = expression.toString().split(" ");


		int segno = 1;

		for (int i=0; i<appo.length; i++){
			if(appo[i].compareTo("+") == 0 )
				segno = 1;

			else if(appo[i].compareTo("-")==0)
				segno = -1;

			else
				try {
					Integer num = segno * Integer.parseInt(appo[i]);
					if(!(value.containsKey("R")))
						value.put("R", "0");

					value.put("R", String.valueOf(Integer.parseInt(value.get("R")) + num) );
				}catch (Exception e) {
					if(!(value.containsKey(appo[i])))
						value.put(appo[i], "0");

					value.put(appo[i], String.valueOf(Integer.parseInt(value.get(appo[i])) - (1*segno)));
				}

		}

	}


	// instances of this class will end up in collections, so it is a good practice to 
	// implement equals and hashCode methods
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((programPoint == null) ? 0 : programPoint.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinearEqualities other = (LinearEqualities) obj;
		if (programPoint == null) {
			if (other.programPoint != null)
				return false;
		} else if (!programPoint.equals(other.programPoint))
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}
	
	// we want to pretty print information inside the .dot files, so we redefine the 
	// toString method
	
	@Override
	public String toString() {
			return "(" + variable + "," + programPoint + ")";
	}


	// the variable we are referring to
	@Override
	public Identifier getIdentifier() {
		return this.variable;
	}

	// the gen function: which elements are we generating when we are performing an assignment
	@Override
	public Collection<LinearEqualities> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
											PossibleForwardDataflowDomain<LinearEqualities> domain) {


		LinearEqualities generated = new LinearEqualities(id, pp, expression);
		Collection<LinearEqualities> result = new HashSet<>();
		result.add(generated);

		generated.line = domain.getDataflowElements().size();

		/////////////////////////////////
		//While detector
		////////////////////////////////
		Iterator<LinearEqualities> it1 = domain.getDataflowElements().iterator();
		Boolean trovato = false;

		while (it1.hasNext()) {
			LinearEqualities uno = it1.next();

			Iterator<LinearEqualities> it2 = domain.getDataflowElements().iterator();
			while (it2.hasNext()) {
				LinearEqualities due = it2.next();

				if (due.variable.toString().compareTo(uno.variable.toString()) == 0) {
					if (due.programPoint.toString().compareTo(uno.programPoint.toString()) != 0) {
						trovato = true;
						break;
					}
				}
			}
			if (trovato) break;
		}



		if (trovato && !find) {


			/////////////////////////////////
			//Line detector
			////////////////////////////////

			int line_find = 0;
			Object[] appo = domain.getDataflowElements().toArray();

			for (int i = 0; i < appo.length; i++) {
				LinearEqualities prova = (LinearEqualities) appo[i];
				if (prova.expression.toString().compareTo(expression.toString()) == 0)
					if (prova.line >= line_find)
						line_find = prova.line;
			}


			/////////////////////////////////
			//Create block, before while and into while
			////////////////////////////////
			HashMap<String, HashMap<String, String>> hash_succ = new HashMap<String, HashMap<String, String>>();

			it1 = domain.getDataflowElements().iterator();

			while (it1.hasNext()) {
				LinearEqualities uno = it1.next();
				if (uno.line >= line_find)
					hash_succ.put(uno.variable.toString(), uno.value);

			}

			HashMap<String, HashMap<String, String>> hash_prec = new HashMap<String, HashMap<String, String>>();

			it1 = domain.getDataflowElements().iterator();


			while (it1.hasNext()) {
				LinearEqualities uno = it1.next();
				if (uno.line < line_find && hash_succ.containsKey(uno.variable.toString()))
					hash_prec.put(uno.variable.toString(), uno.value);

			}


			//////////////////////////////////
			//Replace
			/////////////////////////////////
			HashMap<String, HashMap<String, String>> hash_result = new HashMap<String, HashMap<String, String>>();


			hash_succ.entrySet().forEach(entry -> {
				String key = entry.getKey();

				Integer r = Integer.parseInt(entry.getValue().get("R"));
				HashMap<String, String> appo2 = (HashMap<String, String>) hash_prec.get(key).clone();
				appo2.put("R", Integer.toString(Integer.parseInt(appo2.get("R")) + r));
				hash_result.put(key, appo2);
			});

			//////////////////////////////////
			//Cont to multiply by second line
			/////////////////////////////////

			AtomicReference<Float> cont= new AtomicReference<>((float) 1);

			hash_prec.entrySet().forEach(entry -> {
				float R = Integer.parseInt(	entry.getValue().get("R"));
				R -= Integer.parseInt(hash_result.get(entry.getKey()).get("R"));
				float finalR = R;
				cont.updateAndGet(v -> new Float((float) (v / finalR)));
			});


			/////////////////////////////////////
			//order hashmap
			////////////////////////////////////7
			HashMap<Integer, HashMap<String, String>> order_prec = new HashMap<>();
			HashMap<Integer, HashMap<String, String>> order_result = new HashMap<>();
			AtomicReference<Integer> cont1 = new AtomicReference<>(1);


			hash_prec.entrySet().forEach(entry -> {
				order_prec.put(cont1.get(), entry.getValue() );

				order_result.put(cont1.get(), hash_result.get(entry.getKey()));

				cont1.set(cont1.get()+1);
			});


			///////////////////////////////////////////////////
			//second line update
			////////////////////////////////////////////////
			order_prec.get(2).entrySet().forEach(entry -> {
				float res = Integer.parseInt(entry.getValue()) * cont.get() ;

				entry.setValue(String.valueOf(res));
			});

			order_result.get(2).entrySet().forEach(entry -> {
				float res = Integer.parseInt(entry.getValue()) * cont.get() ;

				entry.setValue(String.valueOf(res));
			});


			///////////////////////////////////////////////////
			//subtraction
			////////////////////////////////////////////////

			order_prec.get(1).entrySet().forEach(entry -> {

				float res = 0 ;


				if(order_prec.get(2).containsKey(entry.getKey()))
					res = Float.parseFloat(order_prec.get(2).get(entry.getKey()));


				float res2 = Float.parseFloat(entry.getValue()) - res ;

				entry.setValue(String.valueOf(res2));
			});


			order_result.get(1).entrySet().forEach(entry -> {
				float res = 0 ;
				if(order_result.get(2).containsKey(entry.getKey()))
					res = Float.parseFloat(order_result.get(2).get(entry.getKey()));

				float res2 = Float.parseFloat(entry.getValue()) - res ;

				entry.setValue(String.valueOf(res2));
			});



			///////////////////////////////////////////////////
			//addition
			////////////////////////////////////////////////


			order_prec.get(2).entrySet().forEach(entry -> {
					if(!order_prec.get(1).containsKey(entry.getKey()))
						order_prec.get(1).put(entry.getKey(), String.valueOf(Float.parseFloat(entry.getValue()) * -1));

			});


			order_result.get(2).entrySet().forEach(entry -> {
					if(!order_result.get(1).containsKey(entry.getKey()))
						order_result.get(1).put(entry.getKey(), String.valueOf(Float.parseFloat(entry.getValue()) * -1));
			});

			find = true;


			System.out.println("\n");
			System.out.println("Linear Equalities Analysis");
			System.out.println("_____________________________________________________");
			System.out.println("First Matrix: " + order_prec.get(1).toString());
			System.out.println("Second Matrix: " + order_result.get(1).toString());

			if(order_prec.get(1).equals(order_result.get(1)))
				System.out.println("Matrix Match!");
			else
				System.out.println("Matrix DON'T Match!");
			System.out.println("_____________________________________________________");
			System.out.println("\n");
		}


		return result;

	}

	// the kill function: which variables are we killing when we perform an assignment
	@Override
	public Collection<Identifier> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			PossibleForwardDataflowDomain<LinearEqualities> domain) {
		Collection<Identifier> result = new HashSet<>();
		result.add(id);

		/////////////////////////////////////////////////
		// PRINT
		/////////////////////////////////////////////////
		/*System.out.println("ID: " + id);
		System.out.println("EXPRESSION: " + expression);
		System.out.println("PP: " + pp);
		System.out.println("DOMAIN: " + domain);
		System.out.println("####################################");*/

		return result;
	}
}
