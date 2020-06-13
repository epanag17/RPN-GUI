package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import rpnsim.application.model.Arrow;
import rpnsim.application.model.Bond;
import rpnsim.application.model.LabelItem;
import rpnsim.application.model.Place;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Token;
import rpnsim.application.model.Transition;

public class ForwardExecution extends Execution{
	
	public ForwardExecution(RPN rpn) {
		super(rpn);
	}
	

	
	private boolean areMarked(ArrayList<Token> tokenList, boolean isPositiveList, Place place) {
		for(Token token:tokenList) {
			if(isPositiveList) {
				if(!place.containsToken(token))
					return false;
			}
			else {
				if(place.containsToken(token))
					return false;
			}
		}
		
		return true;
	}
	
	private boolean areMarked(Place place,ArrayList<Pair<Token,Token>> bondList, boolean isPositiveList) {
		for(Pair<Token,Token> bond:bondList) {
			//Token[] tokens = place.getTokens();
			Token tokenA = bond.getKey();
			Token tokenB = bond.getValue();
			if(place.containsToken(tokenA) && place.containsToken(tokenB)) {
				
				Bond[] existedBonds = rpn.getBonds();
				if(existedBonds.length==0 && isPositiveList)
					return false;
				
				if(isPositiveList) {
					if(!hasBond(tokenA, tokenB, existedBonds))
						return false;
				}
				else {
					if(hasBond(tokenA, tokenB, existedBonds))
						return false;
				}
				
			}
			else {
				if(isPositiveList)
					return false;
			}
		}
		
		return true;
	}
	
	private ArrayList<Arrow> getOutgoingArrows(Transition transition) {
		ArrayList<Arrow> outgoing_arrows = new ArrayList<>();
		Arrow[] arrows = transition.getArrows();
		for(Arrow arrow:arrows) {
			if(arrow.getSource() == transition) {
				outgoing_arrows.add(arrow);
			}
		}
		return outgoing_arrows;
	}

	
	public Set<Transition> getEnabledTransitions(){
		enabledTransitions.clear();
		
		for(Transition transition:rpn.getTransitions()) {
			boolean isEnabled = true;
			Arrow[] arrows = transition.getArrows();
			for(Arrow arrow: arrows) {
				if(arrow.getDestination() == transition) {
					//incoming arrow
					
					//start definition 3.1
					Place place = (Place) arrow.getSource();
					ArrayList<Token> positiveTokens = arrow.getTokens(false);
					//an estw ena apo ta positive tokens tou arrow den periexetai sto incoming place tote to
					//transition den einai forward enabled
					if(!areMarked(positiveTokens, true, place)) {
						System.out.println("Failed Definition 3.1");
						isEnabled = false;
						break;
					}
					ArrayList<Token> negativeTokens = arrow.getTokens(true);
					//an estw ena apo ta negative tokens tou arrow periexetai sto incoming place tote to
					//transition den einai forward enabled
					if(!areMarked(negativeTokens, false, place)) {
						System.out.println("Failed Definition 3.1");
						isEnabled = false;
						break;
					}
					// end definition 3.1
					
					//start definition 3.2
					ArrayList<Pair<Token,Token>> positiveBonds = arrow.getBonds(false);
					//an estw apo ta positive bonds tou arrow den periexontai sto incoming place tote to
					//transition den einai forward enabled
					if(!areMarked(place,positiveBonds, true)) {
						System.out.println("Failed Definition 3.2");
						isEnabled = false;
						break;
					}
					ArrayList<Pair<Token,Token>> negativeBonds = arrow.getBonds(true);
					//an estw ena apo ta negative bonds periexetai sto incoming place tote to transition
					//den einai forward enabled
					if(!areMarked(place,negativeBonds, false)) {
						System.out.println("Failed Definition 3.2");
						isEnabled = false;
						break;
					}
					//end definition 3.2
					
					// Start definition 3.3
					//an yparxei desmo sto incoming place, elegxw kata poso ta tokens tou desmou pane
					//se diaforetiko outgoing place kai an pane tote simainei oti to transition den einai
					//forward enabled
					RPNUtils utils = new RPNUtils(rpn);
					ArrayList<Arrow> outgoingArrows = getOutgoingArrows(transition);
					Token[] tokensIntoPlace = place.getTokens();
					Bond[] existedBonds = rpn.getBonds();
					for(Bond bond:existedBonds) {
						Token tokenA = bond.getSource();
						Token tokenB = bond.getDestination();
						
						if(place.containsToken(tokenA)) {
							//simainei oti yparxei desmos mesa sto place
							for(int i=0; i<outgoingArrows.size(); i++) {
								Set<LabelItem> label1 = outgoingArrows.get(i).getSet();
								Set<Token> tokensLabel1 = utils.getTokensFrom(label1);
								for(int j=i+1; j<outgoingArrows.size(); j++){
									Set<LabelItem> label2 = outgoingArrows.get(j).getSet();
									Set<Token> tokensLabel2 = utils.getTokensFrom(label2);
									
									if(tokensLabel1.contains(tokenA)) {
										if(tokensLabel2.contains(tokenB)) {
											System.out.println("Failed Definition 3.3");
											isEnabled = false;
											break;
										}
									}
									
									if(tokensLabel1.contains(tokenB)) {
										if(tokensLabel2.contains(tokenA)) {
											System.out.println("Failed Definition 3.3");
											isEnabled = false;
											break;
										}
									}
								}
							}
							
						}
					}
					
					// end definition 3.3
					
					// start definition 3.4
					//an sta outgoing arrows yparxei estw ena desmos pou yparxei se ena apo ta incoming places
					//tou transition tote koitazw sto antistoixo incoming arrow an yparxei o desmos kai an den
					//yparxei tote simainei oti to transition den einai forward enabled.
					for(Arrow outgoingArrow:outgoingArrows) {
						Set<LabelItem> outgoingLabel = outgoingArrow.getSet();
						Set<LabelItem> outgoingBondsLabel = utils.getBondsFrom(outgoingLabel);
						for(LabelItem outgoingbondLabel:outgoingBondsLabel) {
							Pair<Token,Token> bond = outgoingbondLabel.getBond();
							Token tokenA = bond.getKey();
							Token tokenB = bond.getValue();
							Boolean isExistBond = false;
							if(place.containsToken(tokenA) && place.containsToken(tokenB)) {
								isExistBond = hasBond(tokenA, tokenB, existedBonds);
								
							}
							if(isExistBond) {
								//check if the incoming arrow include as label item the bond
								Set<LabelItem> incomingLabel = arrow.getSet();
								Set<LabelItem> incomingBonds = utils.getBondsFrom(incomingLabel);
								/*if(!incomingBonds.contains(bondLabel)) {
									isEnabled = false;
									break;
								}*/
								boolean contain = false;
								for(LabelItem incomingBond: incomingBonds) {
									if(incomingBond.equals(outgoingbondLabel)) {
										contain = true;
										break;
									}
									
								}
								if(!contain) {
									System.out.println("Failed Definition 3.4");
									isEnabled = false;
									break;
								}
							}
						}
					}
						
					
					// end definition 3.4
					
					
				}
			}
			if(isEnabled)
				enabledTransitions.add(transition);
		}
		
		return enabledTransitions;
	}
	
	private boolean hasBond(Token tokenA, Token tokenB, Bond[] existedBonds) {
		boolean isExistBond = false;
		for(Bond existedBond:existedBonds) {
			Token source = existedBond.getSource();
			Token destination = existedBond.getDestination();
			if(tokenA == source || tokenA == destination) {
				if(tokenB == source || tokenB == destination) {
					isExistBond = true;
					break;
				}
			}
		}
		
		return isExistBond;
	}
	
	/**Gia kathe outgoing arrow tou transition metakinw ta antistoixa tokens kai bonds pou yparxoun
	 * sto outgoing arrow sto antistoixo outgoing place. Akomi e3etazw kata poso ypirxe proigoumenws o
	 * desmos pou anagrafetai sto outgoing arrow sta antistoixa tokens kai an den ypirxe tote prosthetw 
	 * bond sta tokens. Telos, enimerwnw tin history list tou transition pou pirwdwtithike.
	 * 
	 * @param t: to transition pou tha ektelestei
	 * @param count: o arithmos pou tha prosthethei stin history list tou transition pou deixnei
	 * 	me poia seira ektelestike to transition
	 */
	public void fireTransition(Transition t, int count) {
		ArrayList<Arrow> outgoingArrows = getOutgoingArrows(t);
		for(Arrow outgoingArrow: outgoingArrows) {
			Set<LabelItem> outgoingLabel = outgoingArrow.getSet();
			Place outgoingPlace = (Place) outgoingArrow.getDestination();
			
			for(LabelItem item: outgoingLabel) {
				if(item.isToken()) {
					Token token = item.getToken();
					outgoingPlace.moveToken(token);
				}
				
				if(item.isBond()) {
					Pair<Token,Token> bond = item.getBond();
					Token tokenA = bond.getKey();
					Token tokenB = bond.getValue();
					outgoingPlace.moveToken(tokenA);
					outgoingPlace.moveToken(tokenB);
					
					Boolean isExistBond = false;
					for(Bond existedBond:rpn.getBonds()) {
						
						if(existedBond.getSource()==tokenA && existedBond.getDestination()==tokenB) {
							isExistBond = true;
							break;
						}
						if(existedBond.getSource()==tokenB && existedBond.getDestination()==tokenA) {
							isExistBond = true;
							break;
						}
						
						
					}
					
					if(!isExistBond) {
						tokenA.createBond(tokenB);
					}
				}
			}
		}
		
		t.getHistoryList().add(count);
		//t.updateHistory();
		
	}

}
