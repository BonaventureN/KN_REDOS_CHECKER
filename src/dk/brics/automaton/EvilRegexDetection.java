package dk.brics.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
/**
 * Class: EvilRegexDetection
 * Description: Cette classe implémente les méthodes de détection des patterns evil dans les regex
 * @author Arnaud KENNE ET Bonaventure NDSIYO
 * Date: 2022-12-17
 * Version: 1.0
 */

public class EvilRegexDetection {
	
	private RegExp r;
	
	public EvilRegexDetection(String regex) {
		r = new RegExp(regex);
	}
	
	public RegExp getRegex() {
		return r;
	}
	
	/**Rétourne les groupes de  captures () dans la regex objet de la classe Regexp
	 *  @param objet classe RegExp
	 *  @return Map contenenant les positions de début et de fin de chaque groupe de capture
	 */
	public Map<Integer, Integer> parse(RegExp re) {
		Map<Integer, Integer> hm = new HashMap<Integer, Integer> ();
		
		Stack<Integer> pile= new Stack<>();
		int i=0, debutPos = 0, finPos = 0;
		String  ch = re.toString();
		
		//parcourir la regex
		while( i<ch.length()) {
			if(ch.charAt(i)=='(') {
				pile.push(i);	
			}else if(ch.charAt(i)==')') {
				debutPos = pile.pop();
				finPos = i;
				//Mettre la position de debut et de fin dans le map
				hm.put(debutPos, finPos);
			}
			i++;
		}
		return hm;
	}
	
	/**Rétourne les groupes de  captures () dans la  chaîne de carctères
	 *  @param objet classe RegExp
	 *  @return Map contenenant les positions de début et de fin de chaque groupe de capture
	 */
	public Map<Integer, Integer> parse(String str) {
		Map<Integer, Integer> hm = new HashMap<Integer, Integer> ();
		
		Stack<Integer> pile= new Stack<>();
		int i=0, debutPos = 0, finPos = 0;
	
		
		//parcourir la regex
		while( i<str.length()) {
			if(str.charAt(i)=='(') {
				pile.push(i);	
			}else if(str.charAt(i)==')') {
				debutPos = pile.pop();
				finPos = i;
				//Mettre la position de debut et de fin dans le map
				hm.put(debutPos, finPos);
			}
			i++;
		}
		return hm;
	}
	/**
	 * 
	 * @param s : état
	 * @param tr: liste des transitions sortantes de l'état s
	 * @return: la transition qui est une boule ou null
	 */
	
	
	
	
	public Transition getLoop(State s, Collection<Transition > tr) {
		for(Transition t: tr) {
			if(t.getDest().equals(s)) return t;
		}
		return null;
	}
	
	/**
	 * 
	 * @param s : état
	 * @param tr : liste des transitions sortantes de l'état s
	 * @param c: caractères sur lequel on veut transiter 
	 * @return true si s a une boucle sur le carcartère c
	 */
	public boolean hasLoop(State s, Collection<Transition > tr ,char c) {
		for(Transition t: tr) {
			for(char car = t.getMin(); car<=t.getMax(); car++) {
			if(t.getDest().equals(s) && car==c) return true;
			}
		}
		return false;
	}
	
	/**
	 * Détermine s'il y a un cycle ou non entre deux états s1 et s2
	 * @param s1
	 * @param S2
	 * @return true ou false
	 */
	
	
	public boolean cycleBetween(State s1, State S2) {
		for(Transition t:s1.getSortedTransitions(false)){
			if(t.to.equals(S2)) {
				char c = t.getMin();
				for(Transition tr : S2.getSortedTransitions(false)) {
					if(tr.to.equals(s1) && tr.getMin() == c) {
						return true;
					}
				}
				
			}
		}
		
		return false;
	}
	/**
	 * 
	 * @param re : Objet de la classe Regexp
	 * @return true si la regex est vulnérable au cas 1: Nested Quantifiers sinon false
	 */
	public boolean  NQ_checker(RegExp re) {
        Automaton a = re.toAutomaton(false);
	
		System.out.println(a.toString());
		a.expandSingleton();
		Set<State> visited;
		if (a.isDebug())
			visited = new LinkedHashSet<State>();
		else
			visited = new HashSet<State>();
		LinkedList<State> worklist = new LinkedList<State>();
		worklist.add(a.initial);
		visited.add(a.initial);
		
		
		while (worklist.size() > 0) {
			State s = worklist.removeFirst();
			Collection<Transition> tr;
		    
			if (a.isDebug())
				tr = s.getSortedTransitions(false);
			else
				tr = s.transitions;
			
			
			Transition t = getLoop(s, tr);  //t est une boucle
			
			
		if(t!=null) {
			
			for (char c = t.getMin(); c <= t.getMax(); c++) {
				for(Transition trans: tr) {
					for (char car = t.getMin(); car <= t.getMax(); car++) {
					   if(!(trans.equals(t)) && car==c   ) {
						State dest = trans.getDest();
						for(Transition tRetour: dest.getSortedTransitions(false)) {
							for (char carac = tRetour.getMin(); carac <= tRetour.getMax(); carac++) {
						
							if(carac == c && tRetour.getDest().equals(s)) {
							
								return true;
							}
							}
						}
					}
					
					}
				}
				
				
			}

			
			
		}
			
			//avancer
			for (Transition t1 : tr) {
				
				if (!visited.contains(t1.to)) {
					
					visited.add(t1.to);
					worklist.add(t1.to);
					
				}
				
			}
			
	}
		//si * ou ? in
		
		Map<Integer, Integer> blocs = parse(re);
		//Analyser tous les blocs
		String ch =re.toString();
		for (Map.Entry<Integer, Integer> entry : blocs.entrySet()) {
			int debutPos = entry.getKey();
			int finPos = entry.getValue();
			String bloc = ch.substring(debutPos, finPos+1);		
	     
			int i = 0;
			
			//Verifier si * dans le bloc  ou ?
			
			//Dans le bloc tous les sous blocs ou lettre sans () doivent avoir *, ? ou {1,}

			boolean starIn = bloc.contains("?")||bloc.contains("*")||bloc.contains("{1,}"); 
	if(starIn) {
		boolean all =true;
		int  longueur =0;
		
		//recuperer les sous blocs du bloc
		 Map<Integer, Integer> sousBlocs = parse(bloc);
		//si tous les sous blocs du bloc ont *  ou   {1, } ou ?
		for (Map.Entry<Integer, Integer> entr : sousBlocs.entrySet()) {
			String sousbloc = bloc.substring(entr.getKey(),entr.getValue()+1);
			int finPosi = entr.getValue();
			//voir si tous les sous blocs ont *, ? ou {1, }: methode: voir si la somme des longueurs des sous blocs y compris
			//le caractere ?, * ou {1, } donne la longueur du bloc-2(les 2 parentheses de debut et de fin)
			
			if(finPosi +2<bloc.length() && (bloc.charAt(finPosi +1)=='*' ||bloc.charAt(finPosi +1)=='?')){
				
				longueur += sousbloc.length()+1;
				if(finPosi +2<bloc.length() && bloc.charAt(finPosi +2)=='.')
					longueur += 1;
				
			}else if(finPosi +1<bloc.length() && (bloc.charAt(finPosi +1)=='{')){
				longueur += sousbloc.length()+4;
				if(finPosi +5<bloc.length() && (bloc.charAt(finPosi +5)=='.'))
						longueur += 1;
			
			}
            
			if(finPosi +1<bloc.length() && bloc.charAt(finPosi +1)!='*' && bloc.charAt(finPosi +1)!='?' && bloc.charAt(finPosi +1)!='{') all =false;
			if( all && (longueur == bloc.length()-2)) {
				
				return true;
			}
			
			
		}
	}
 
	}
		return false;
	}
			
	/**
	 * 
	 * @param re : Objet de la classe Regexp
	 * @return true si la regex est vulnérable au cas 2: Quantified Overlapping Disjunction sinon false
	 */		
			

	public boolean  QOD_checker(RegExp re) {
		if(!re.toString().contains("|")) return false;
		
		Automaton a = re.toAutomaton(false);
	
		a.expandSingleton();
		Set<State> visited;
		if (a.isDebug())
			visited = new LinkedHashSet<State>();
		else
			visited = new HashSet<State>();
		LinkedList<State> worklist = new LinkedList<State>();
		worklist.add(a.initial);
		visited.add(a.initial);
		
		
		while (worklist.size() > 0) {
			State s = worklist.removeFirst();
			Collection<Transition> tr;
		    
			if (a.isDebug())
				tr = s.getSortedTransitions(false);
			else
				tr = s.transitions;

			for(Transition trans: tr) {
				  if(cycleBetween(s, trans.to) ) {
					  //parcourir la regex pour voir si meme bloc entre le |
					  Map<Integer, Integer> blocs = parse(r);
						//Analyser tous les blocs
						String ch =r.toString();
						for (Map.Entry<Integer, Integer> entry : blocs.entrySet()) {
							int debutPos = entry.getKey();
							int finPos = entry.getValue();
							//
							String bloc = ch.substring(debutPos, finPos+1);
							
							int i = 0;
							
							
							int indexDisj =bloc.indexOf("|");
							if(indexDisj>0) {
								int  indexDebutAvant = indexDisj-1 , indexFinApres = indexDisj+1;
								
								while(indexDebutAvant>=0 && bloc.charAt(indexDebutAvant)!='(') {
									indexDebutAvant--;
								}
								while(indexFinApres< bloc.length() && bloc.charAt(indexFinApres)!=')') {
									indexFinApres++;
								}
							
								String partieAvantDisj = "", partieApresDisj="";
							//recuperer la chaine avant la | et celle apres
							if(indexDebutAvant+2<bloc.length() &&  indexDisj-1<=bloc.length() && indexFinApres-1<=bloc.length() && indexDisj+2<bloc.length()) {
								if(bloc.charAt(indexDebutAvant+1)=='\"')
								partieAvantDisj= bloc.substring(indexDebutAvant+2, indexDisj-1);
								else
									partieAvantDisj= bloc.substring(indexDebutAvant+1, indexDisj);
								if(bloc.charAt(indexDisj+1)=='\"')
									partieApresDisj= bloc.substring(indexDisj+2, indexFinApres-1);
								else
									partieApresDisj= bloc.substring(indexDisj+1, indexFinApres);
							}
						
							boolean same = true;
							String str =partieAvantDisj+partieApresDisj;
							
							for(int l =0; l<str.length(); l++) {
								if(str.charAt(l)!=str.charAt(0)) same = false;
							}
					  if(partieAvantDisj.equals(partieApresDisj  )|| same) {
						
						  return true;
					  }
						
					  
				  }
			}
		 }
	}	
			/*
			 * On recheche le pattern vulnérable 
			 */
			Transition t = getLoop(s, tr);                  //transition qui est une boucle
			
			
		if(t!=null) {
			
			for (char c = t.getMin(); c <= t.getMax(); c++) {
				for(Transition trans: tr) {
					if(!(trans.equals(t)) && trans.getMin()==c   ) {
						State dest = trans.getDest();
						
						for(Transition tRetour: dest.getSortedTransitions(false)) {
							
							if(tRetour.getMin() == c) {
								State EtatIntermediare = tRetour.to;
								for(Transition tInter: EtatIntermediare.getSortedTransitions(false)) {
									if(tInter.getMin() == c && tInter.to.equals(s) ) {
										 
										 return true;
									   }
								   }
							     }    

							 }
						 }
					 }
					
					
				}
				
				
			}
			

			
			//avancer sur l'automate
			for (Transition t1 : tr) {
				
				if (!visited.contains(t1.to)) {

					visited.add(t1.to);
					worklist.add(t1.to);
					
				}
				
			}
		}
			return false;	
	
}
		
	/**
	 * 
	 * @param re : Objet de la classe Regexp
	 * @return true si la regex est vulnérable au cas 3: Quantified Overlapping Adjacency sinon false
	 */		
				
	
	public boolean  QOA_checker(RegExp re) {
		
		Automaton a = re.toAutomaton(false);
		
		a.expandSingleton();
		Set<State> visited;
		if (a.isDebug())
			visited = new LinkedHashSet<State>();
		else
			visited = new HashSet<State>();
		LinkedList<State> worklist = new LinkedList<State>();
		worklist.add(a.initial);
		visited.add(a.initial);
		
		
		while (worklist.size() > 0) {
			State s = worklist.removeFirst();
			Collection<Transition> tr;
		    
			if (a.isDebug())
				tr = s.getSortedTransitions(false);
			else
				tr = s.transitions;
			/*
			 * Nous recherchons le pattern evil dans l'automate
			 */
		//if(s.isAccept()) {	
			State s1 =null, s2 =null;
			char c =' ';   //initialisation
			for(Transition t: tr) {
				//Trouver une transition t vers un noeud S1 avec boucle avec le même caractère
				for(char car= t.getMin(); car<=t.getMax(); car++) {
				  if(!t.to.equals(s) && hasLoop(t.to, t.to.getSortedTransitions(false), car) ) {
					c =car;
					s1 =t.to;
					
					for(Transition t1: tr) {
						//Trouver une autre transition t1 vers un noeud avec boucle avec le même caractère c
						if( !t1.equals(t) && ! t1.to.equals(s) && hasLoop(t1.to, t1.to.getSortedTransitions(false), c) ) {
							s2 = t1.to;
						}
					}
			    }
	         }
			}
			if(s1!=null && s2!=null) {
				for(Transition t2: s1.getSortedTransitions(false)) {
					for(char car= t2.getMin(); car<=t2.getMax(); car++) {
					if(t2.to.equals(s1) && car== c) {
					
						return true;
					}
					}
				}
				for(Transition t3: s2.getSortedTransitions(false)) {
					for(char car= t3.getMin(); car<=t3.getMax(); car++) {
					if(t3.to.equals(s2) && car == c) {
					
						return true;
					}
					}
				}
			}

			//avancer si l'on a pas trouvé
			for (Transition t1 : tr) {
				
				if (!visited.contains(t1.to)) {

					visited.add(t1.to);
					worklist.add(t1.to);
					
				}
				
			}
		
		}
		

			return false;
	}
	
	
}
